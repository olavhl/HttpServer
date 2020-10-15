package no.kristiania.members;

import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberDao {
    public static void main(String[] args) throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/projectmember");
        dataSource.setUser("projectmember");
        dataSource.setPassword("pojewojrwjrep");

        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("select * from members");
        ResultSet rs = statement.executeQuery();

        while(rs.next()) {
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            String email = rs.getString("email");

            System.out.println(firstName + " " + lastName);
            System.out.println(email + "\n");
        }

    }
}
