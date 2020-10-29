package no.kristiania.http;

import no.kristiania.database.Member;
import no.kristiania.database.MemberDao;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpServerTest {

    private JdbcDataSource dataSource;
    private HttpServer server;

    @BeforeEach
    void setUp() throws IOException {
        dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();
        server = new HttpServer(0, dataSource);
    }

    // Trying to fix
    @Test
    void shouldReturnSuccessfulErrorCode() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo");
        assertEquals(200, client.getStatusCode());
    }

    @Test
    void shouldReturnUnsuccessfulErrorCode() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo?status=404");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldReturnHttpHeaders() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo?body=HelloWorld");
        assertEquals("10", client.getResponseHeader("Content-Length"));
    }

    @Test
    void shouldReturnResponseBody() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo?body=HelloWorld");
        assertEquals("HelloWorld", client.getResponseBody());
    }

    @Test
    void shouldReturnFileContent() throws IOException {
        File documentRoot = new File("target/test-classes");

        String fileContent = "Hello " + new Date();
        Files.writeString(new File(documentRoot, "test.txt").toPath(), fileContent);

        HttpClient client = new HttpClient("localhost", server.getPort(), "/test.txt");
        assertEquals(fileContent, client.getResponseBody());
    }

    @Test
    void shouldReturn404onMissingFile() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/missingFile");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldReturnCorrectContentType() throws IOException {
        File documentRoot = new File("target");
        Files.writeString(new File(documentRoot, "plain.txt").toPath(), "Plain text");
        HttpClient client = new HttpClient("localhost", server.getPort(), "/plain.txt");
        assertEquals("text/plain", client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldPostMember() throws IOException, SQLException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/member", "POST",
                "first_name=OlaNormann&email=ola@nordmann.no");
        assertThat(server.getMemberNames()).extracting(Member::getFirstName).contains("OlaNormann");
    }

   @Test
    void shouldDisplayExistingMembers() throws IOException, SQLException {
        // Clearing list in HttpServer to test responseBody
        Member member = new Member();
        member.setFirstName("Kristian");
        member.setLastName("Pedersen");
        member.setEmail("ok@gmail.com");
        MemberDao memberDao = new MemberDao(dataSource);
        memberDao.insert(member);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/member");
        assertThat(client.getResponseBody()).contains("<li>Kristian Pedersen (ok@gmail.com)</li>");
    }

    @Test
    void shouldPostNewProject() throws IOException {
        String requestBody = "projectName=angular&status=Good";
        HttpClient postClient = new HttpClient("localhost", server.getPort(), "/api/newProject", "POST", requestBody);
        // Should return 302 because it is redirecting to showProjects.html
        assertEquals(302, postClient.getStatusCode());

        HttpClient getClient = new HttpClient("localhost", server.getPort(), "/api/project");
        assertThat(getClient.getResponseBody()).contains("angular");
    }


}