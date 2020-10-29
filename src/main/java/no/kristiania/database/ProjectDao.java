package no.kristiania.database;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDao extends AbstractDao<Project> {

    public ProjectDao(DataSource dataSource){
        super(dataSource);
    }
    public List<Project> list() throws SQLException {

        try (Connection connection = dataSource.getConnection()){

            try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM projects")){
                try (ResultSet rs = statement.executeQuery()){
                    List<Project> members = new ArrayList<>();
                    while (rs.next()){
                        members.add(mapRow(rs));
                    }
                    return members;
                }
            }

        }

    }

    @Override
    protected Project mapRow(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setId(rs.getInt("id"));
        project.setName(rs.getString("name"));
        project.setStatus(rs.getString("status"));
        return project;
    }


    public void insert(Project project) throws SQLException {

        try (Connection connection = dataSource.getConnection()){
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO projects (name, status) values (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )){

                statement.setString(1,project.getName());
                statement.setString(2, project.getStatus());
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();
                    project.setId(generatedKeys.getInt("id"));
                }
            }
        }
    }


    public Project retrieve(Integer id) throws SQLException {
        return retrieve(id, "SELECT * FROM projects WHERE id = ?");
    }
}
