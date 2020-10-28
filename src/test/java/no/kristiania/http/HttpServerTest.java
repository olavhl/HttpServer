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

    @BeforeEach
    void setUp() {
        dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();
    }

    // Trying to fix
    @Test
    void shouldReturnSuccessfulErrorCode() throws IOException {
        new HttpServer(10001, dataSource);
        HttpClient client = new HttpClient("localhost", 10001, "/echo");
        assertEquals(200, client.getStatusCode());
    }

    @Test
    void shouldReturnUnsuccessfulErrorCode() throws IOException {
        new HttpServer(10002, dataSource);
        HttpClient client = new HttpClient("localhost", 10002, "/echo?status=404");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldReturnHttpHeaders() throws IOException {
        new HttpServer(10003, dataSource);
        HttpClient client = new HttpClient("localhost", 10003, "/echo?body=HelloWorld");
        assertEquals("10", client.getResponseHeader("Content-Length"));
    }

    @Test
    void shouldReturnResponseBody() throws IOException {
        new HttpServer(10004, dataSource);
        HttpClient client = new HttpClient("localhost", 10004, "/echo?body=HelloWorld");
        assertEquals("HelloWorld", client.getResponseBody());
    }

    @Test
    void shouldReturnFileContent() throws IOException {
        HttpServer server = new HttpServer(10005, dataSource);
        File documentRoot = new File("target/test-classes");

        String fileContent = "Hello " + new Date();
        Files.writeString(new File(documentRoot, "test.txt").toPath(), fileContent);

        HttpClient client = new HttpClient("localhost", 10005, "/test.txt");
        assertEquals(fileContent, client.getResponseBody());
    }

    @Test
    void shouldReturn404onMissingFile() throws IOException {
        HttpServer server = new HttpServer(10006, dataSource);
        HttpClient client = new HttpClient("localhost", 10006, "/missingFile");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldReturnCorrectContentType() throws IOException {
        HttpServer server = new HttpServer(10007, dataSource);
        File documentRoot = new File("target");
        Files.writeString(new File(documentRoot, "plain.txt").toPath(), "Plain text");
        HttpClient client = new HttpClient("localhost", 10007, "/plain.txt");
        assertEquals("text/plain", client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldPostMember() throws IOException, SQLException {
        HttpServer server = new HttpServer(10009, dataSource);
        HttpClient client = new HttpClient("localhost", 10009, "/api/members", "POST",
                "first_name=OlaNormann&email=ola@nordmann.no");
        assertThat(server.getMemberNames()).extracting(Member::getFirstName).contains("OlaNormann");
    }

   @Test
    void shouldDisplayExistingMembers() throws IOException, SQLException {
        HttpServer server = new HttpServer(10010, dataSource);
        // Clearing list in HttpServer to test responseBody
        Member member = new Member();
        member.setFirstName("Kristian");
        member.setLastName("Pedersen");
        member.setEmail("ok@gmail.com");
        MemberDao memberDao = new MemberDao(dataSource);
        memberDao.insert(member);
        HttpClient client = new HttpClient("localhost", 10010, "/api/members");
        assertThat(client.getResponseBody()).contains("<li>Kristian Pedersen (ok@gmail.com)</li>");
    }

    @Test
    void shouldPostNewProject() throws IOException {
        HttpServer server = new HttpServer(10011, dataSource);
        String requestBody = "projectName=angular&status=Good";
        HttpClient postClient = new HttpClient("localhost", 10011, "/api/newProject", "POST", requestBody);
        assertEquals(200, postClient.getStatusCode());

        HttpClient getClient = new HttpClient("localhost", 10011, "/api/project");
        assertThat(getClient.getResponseBody()).contains("<li>angular</li>");
    }


}