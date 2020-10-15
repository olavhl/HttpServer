package no.kristiania.members;
/*
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


        memberDao.insertFirstName(firstName);
        for (String memberName : memberDao.list()) {
            System.out.println(memberName);

        }
    }

    public void insertFirstName(String firstName) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO members (first_name) VALUES (?)")) {
                statement.setString(1, firstName);
                statement.executeUpdate();
            }
        }
    }
/*
    public void insertLastName(String lastName) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO members (first_name) VALUES (?)")) {
                statement.setString(1, lastName);
                statement.executeUpdate();
            }
        }
    }

    public List<String> list() throws SQLException {
        List<String> members = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from members")) {
                try (ResultSet rs = statement.executeQuery()) {
                    while(rs.next()) {
                       members.add(rs.getString("first_name"));
                       members.add(rs.getString("last_name"));
                       //members.add(rs.getString("email"));
                    }
                }
            }
        }
        return members;
    }

}*/
