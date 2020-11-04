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

    public void update(Member member) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE members SET project_id = ? WHERE id = ?"
            )) {
                statement.setInt(1, member.getProjectId());
                statement.setInt(2, member.getId());
                statement.executeUpdate();
            }
        }
    }

    @Override
    protected Member mapRow(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getInt("id"));
        member.setProjectId((Integer) rs.getObject("project_id"));
        member.setFirstName(rs.getString("first_name"));
        member.setLastName(rs.getString("last_name"));
        member.setEmail(rs.getString("email"));
        return member;
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

    public Member retrieve(Integer id) throws SQLException {
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
