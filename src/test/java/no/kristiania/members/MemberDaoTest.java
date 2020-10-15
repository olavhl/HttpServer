package no.kristiania.members;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;


public class MemberDaoTest {

    //@Test
    /*void shouldListInsertedMember() throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement("create table members (first_name varchar)").executeUpdate();
        }

        MemberDao memberDao = new MemberDao(dataSource);
        String memberFirstName = exampleMember();
        memberDao.insertFirstName(memberFirstName);
        assertThat(memberDao.list().contains(memberFirstName));
    }*/

    private String exampleMember() {
        String[] options = {"Alex", "Ivar", "Peder", "Kent", "Arne"};
        Random random = new Random();
        return  options[random.nextInt(options.length)];
    }
}
