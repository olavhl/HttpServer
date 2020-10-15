package no.kristiania.members;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberDao {

    private ArrayList<String> members = new ArrayList<>();
    private DataSource dataSource;
    private String memberFirstName;

    public MemberDao(DataSource dataSource) {
        this.dataSource = dataSource;
        
    }

    public static void main(String[] args) throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/projectmember");
        dataSource.setUser("projectmember");
        dataSource.setPassword("pojewojrwjrep");



        try ( Connection connection = dataSource.getConnection() ) {
            try (PreparedStatement statement = connection.prepareStatement("select * from members")) {
                try (ResultSet rs = statement.executeQuery()) {
                    while(rs.next()) {
                        String firstName = rs.getString("first_name");
                        String lastName = rs.getString("last_name");
                        String email = rs.getString("email");
                        
                        System.out.println(firstName + " " + lastName);
                        System.out.println(email + "\n");
                    }
                }
            }
        }




    }

    public void insertFirstName(String member) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO members (first_name) VALUES (?)")) {
                statement.setString(1, memberFirstName);
                statement.executeUpdate();
            }
        }
        
        members.add(member);
    }

    public List<String> list() {
        return members;
    }
}
