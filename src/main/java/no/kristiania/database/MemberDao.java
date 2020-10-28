package no.kristiania.database;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MemberDao extends AbstractDao<Member>{
    public MemberDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Member mapRow(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getInt("id"));
        member.setFirstName(rs.getString("first_name"));
        member.setLastName(rs.getString("last_name"));
        member.setEmail(rs.getString("email"));
        return member;
    }

    public static void main(String[] args) throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/projectmember");
        dataSource.setUser("projectmember");
        dataSource.setPassword("pojewojrwjrep");

        MemberDao memberDao = new MemberDao(dataSource);

        System.out.println("Please enter first name:");
        Scanner scannerFirstName = new Scanner(System.in);
        String firstName = scannerFirstName.nextLine();

        Scanner scannerLastName = new Scanner(System.in);
        String lastName = scannerLastName.nextLine();

        Scanner scannerEmail = new Scanner(System.in);
        String email = scannerEmail.nextLine();

        Member members = new Member();
        members.setFirstName(firstName);
        members.setLastName(lastName);
        members.setEmail(email);
        memberDao.insert(members);

        for (Member member : memberDao.list()) {
            System.out.println(member);
        }
    }


    public void insert(Member member) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO members (first_name, last_name, email)" +
                    " values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, member.getFirstName());
                statement.setString(2, member.getLastName());
                statement.setString(3, member.getEmail());
                statement.executeUpdate();
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();
                    member.setId(generatedKeys.getInt("id"));
                }
            }
        }
    }

    public Member retrieve(int id) throws SQLException {
        return  retrieve(id, "SELECT * FROM members WHERE id = ?");
    }

    public List<Member> list() throws SQLException {
        List<Member> members = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from members")) {
                try (ResultSet rs = statement.executeQuery()) {
                    while(rs.next()) {
                        members.add(mapRow(rs));
                    }
                    return members;
                }
            }
        }
    }
}
