package no.kristiania.database;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;


public class MemberDaoTest {

    private MemberDao memberDao;
    private Random random = new Random();

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();

        memberDao = new MemberDao(dataSource);
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
    void shouldRetrieveAllProductProperties() throws SQLException {
        memberDao.insert(exampleMember());
        memberDao.insert(exampleMember());
        Member member = exampleMember();
        memberDao.insert(member);
        assertThat(member).hasNoNullFieldsOrProperties();
        assertThat(memberDao.retrieve(member.getId())).usingRecursiveComparison().isEqualTo(member);
    }


    private Member exampleMember() {
        Member member = new Member();
        member.setFirstName(exampleFirstName());
        member.setLastName(exampleLastName());
        member.setEmail(exampleEmail());
        return member;
    }

    private String exampleFirstName() {
        String[] options = {"Alex", "Ivar", "Peder", "Kent", "Arne"};
        return  options[random.nextInt(options.length)];
    }

    private String exampleLastName() {
        String[] options = {"Andreassen", "Karlsen", "Hansen", "Lodden", "Lyngholm"};
        Random random = new Random();
        return  options[random.nextInt(options.length)];
    }

    private String exampleEmail() {
        String[] options = {"alex@gmail.com", "ivar@hotmail.com", "peder@yahoo.no", "kent@outlook.com", "arne@egms.student.no"};
        Random random = new Random();
        return  options[random.nextInt(options.length)];
    }

}
