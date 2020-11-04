package no.kristiania.database;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDao {

    private DataSource dataSource;

    public ProjectDao(DataSource dataSource){

        this.dataSource = dataSource;

    }
    public List<Project> list() throws SQLException {

        try (Connection connection = dataSource.getConnection()){

            try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM projects")){
                try (ResultSet rs = statement.executeQuery()){
                    List<Project> members = new ArrayList<>();
                    while (rs.next()){
                        members.add(mapRowToProject(rs));
                    }
                    return members;
                }
            }

        }

    }

    private Project mapRowToProject(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setId(rs.getInt("id"));
        project.setName(rs.getString("name"));
        return project;
    }


    public void insert(Project project) throws SQLException {

        try (Connection connection = dataSource.getConnection()){
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO projects (name) values (?)",
                    Statement.RETURN_GENERATED_KEYS
            )){

                statement.setString(1,project.getName());
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()){
                    generatedKeys.next();
                    project.setId(generatedKeys.getInt("id"));
                }



            }
        }
    }


    public Project retrieve(Integer id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM projects WHERE id = ?")) {
                statement.setInt(1, id);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return mapRowToProject(rs);
                    } else {
                        return null;
                    }
                }
            }
        }
    }
}
