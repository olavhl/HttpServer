package no.kristiania.members;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;


public class MemberDaoTest {

    @Test
    void shouldListInsertedMember() throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();

        MemberDao memberDao = new MemberDao(dataSource);
        Members members = exampleMember();
        memberDao.insertFirstName(members);
        assertThat(memberDao.list()).extracting(Members::getFirstName).contains(members.getFirstName());
    }

    private Members exampleMember() {
        Members members = new Members();
        members.setFirstName(exampleFirstName());
        return members;
    }

    private String exampleFirstName() {
        String[] options = {"Alex", "Ivar", "Peder", "Kent", "Arne"};
        Random random = new Random();
        return  options[random.nextInt(options.length)];
    }
}
