package no.kristiania.database;

import no.kristiania.http.HttpMessage;
import no.kristiania.http.MemberOptionController;
import no.kristiania.http.UpdateMemberController;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;


public class MemberDaoTest {

    private MemberDao memberDao;
    private ProjectDao projectDao;
    private static Random random = new Random();

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();

        memberDao = new MemberDao(dataSource);
        projectDao = new ProjectDao(dataSource);
    }


    @Test
    void shouldListInsertedMember() throws SQLException {
        Member member1 = exampleMember();
        Member member2 = exampleMember();
        memberDao.insert(member1);
        memberDao.insert(member2);
        assertThat(memberDao.list()).extracting(Member::getFirstName).contains(member1.getFirstName(), member2.getFirstName());
    }

    @Test
    void shouldRetrieveAllMemberProperties() throws SQLException {
        memberDao.insert(exampleMember());
        memberDao.insert(exampleMember());
        Member member = exampleMember();
        memberDao.insert(member);
        assertThat(member).hasNoNullFieldsOrPropertiesExcept("projectId");
        assertThat(memberDao.retrieve(member.getId())).usingRecursiveComparison().isEqualTo(member);
    }

    @Test
    void shouldReturnMembersAsOptions() throws SQLException {
        MemberOptionController controller = new MemberOptionController(memberDao);
        Member member = MemberDaoTest.exampleMember();
        memberDao.insert(member);

        assertThat(controller.getBody()).contains("<option value=" +
                member.getId() + ">" + member.getFirstName() + " " + member.getLastName() + "</option>");
    }


    public static Member exampleMember() {
        Member member = new Member();
        member.setFirstName(exampleFirstName());
        member.setLastName(exampleLastName());
        member.setEmail(exampleEmail());
        return member;
    }

    @Test
    void shouldUpdateExistingMemberWithNewProject() throws SQLException, IOException {
        UpdateMemberController controller = new UpdateMemberController(memberDao);

        Member member = exampleMember();
        memberDao.insert(member);

        Project project = ProjectDaoTest.exampleProject();
        projectDao.insert(project);

        String body = "memberId=" + member.getId() + "&projectId=" + project.getId();

        HttpMessage response = controller.handle(new HttpMessage(body));

        assertThat(memberDao.retrieve(member.getId()).getProjectId())
                .isEqualTo(project.getId());

        assertThat(response.getStartLine())
                .isEqualTo("HTTP/1.1 302 Redirect");
        assertThat(response.getHeaders().get("Location"))
                .isEqualTo("http://localhost:8080/index.html");
    }

    private static String exampleFirstName() {
        String[] options = {"Alex", "Ivar", "Peder", "Kent", "Arne"};
        return  options[random.nextInt(options.length)];
    }

    private static String exampleLastName() {
        String[] options = {"Andreassen", "Karlsen", "Hansen", "Lodden", "Lyngholm"};
        Random random = new Random();
        return  options[random.nextInt(options.length)];
    }

    private static String exampleEmail() {
        String[] options = {"alex@gmail.com", "ivar@hotmail.com", "peder@yahoo.no", "kent@outlook.com", "arne@egms.student.no"};
        Random random = new Random();
        return  options[random.nextInt(options.length)];
    }

}
