package no.kristiania.members;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MemberDao {

    private final DataSource dataSource;

    public MemberDao(DataSource dataSource) {
        this.dataSource = dataSource;
        
    }

    public static void main(String[] args) throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/projectmember");
        dataSource.setUser("projectmember");
        dataSource.setPassword("pojewojrwjrep");

        MemberDao memberDao = new MemberDao(dataSource);

        System.out.println("Please enter first name:");
        Scanner scanner = new Scanner(System.in);
        String firstName = scanner.nextLine();

        Members members = new Members();
        members.setFirstName(firstName);
        memberDao.insertFirstName(members);

        for (Members member : memberDao.list()) {
            System.out.println(member);
        }
    }

    public void insertFirstName(Members members) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO members (first_name) VALUES (?)")) {
                statement.setString(1, members.getFirstName());
                statement.executeUpdate();
            }
        }
    }

    public List<Members> list() throws SQLException {
        List<Members> members = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from members")) {
                try (ResultSet rs = statement.executeQuery()) {
                    while(rs.next()) {
                        Members member = new Members();
                        member.setFirstName(rs.getString("first_name"));
                        members.add(member);
                    }
                }
            }
        }
        return members;
    }

}
