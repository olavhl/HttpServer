package no.kristiania.http;

import no.kristiania.database.Project;
import no.kristiania.database.ProjectDao;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProjectPostController implements HttpController{
    private final ProjectDao projectDao;

    public ProjectPostController(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        Project project = new Project();
        project.setName(requestParameter.getParameter("projectName"));
        project.setStatus(requestParameter.getParameter("status"));
        projectDao.insert(project);

        String response = "HTTP/1.1 302 Redirect \r\n" +
                "Location: http://localhost:8080/showProjects.html\r\n" +
                "\r\n";

        clientSocket.getOutputStream().write(response.getBytes());
    }
}
