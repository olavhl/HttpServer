package no.kristiania.database;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectDaoTest {

    private ProjectDao projectDao;
    private Random random = new Random();

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();

        projectDao = new ProjectDao(dataSource);
    }


    @Test
    void shouldListAllProjects() throws SQLException {

        Project project1 = exampleProject();
        Project project2 = exampleProject();

        projectDao.insert(project1);
        projectDao.insert(project2);

        assertThat(projectDao.list()).extracting(Project::getName).contains(project1.getName(), project2.getName());

    }

    @Test

    void shouldRetrieveAllProjectProperties() throws SQLException {
        projectDao.insert(exampleProject());
        projectDao.insert(exampleProject());
        Project project = exampleProject();
        projectDao.insert(project);
        assertThat(project).hasNoNullFieldsOrProperties();
        assertThat(projectDao.retrieve(project.getId()))
                .usingRecursiveComparison()
                .isEqualTo(project);
    }

    private Project exampleProject() {
        Project project = new Project();
        project.setName(exampleProjectName());
        return project;
    }

    private String exampleProjectName() {
        String[] options = {"Java", "Javascript", "HTML/CSS", "React", "Angular"};
        return options[random.nextInt(options.length)];
    }


}
