package no.kristiania.http;

import no.kristiania.database.Member;
import no.kristiania.database.Project;
import no.kristiania.database.ProjectDao;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ChangeProjectStatusController implements HttpController {
    private static ProjectDao projectDao;

    public ChangeProjectStatusController(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        HttpMessage response = handle(request);
        response.write(clientSocket);
    }

    public HttpMessage handle(HttpMessage request) throws SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        changeStatus(requestParameter);

        HttpMessage redirect = new HttpMessage();
        redirect.setStartLine("HTTP/1.1 302 Redirect");
        redirect.getHeaders().put("Location", "http://localhost:8080/index.html");
        return redirect;

    }

    public static void changeStatus(QueryString requestParameter) throws SQLException {
        Integer projectId = Integer.valueOf(requestParameter.getParameter("projectId"));
        String projectStatus = String.valueOf(requestParameter.getParameter("status"));
        Project project = projectDao.retrieve(projectId);
        project.setId(projectId);
        project.setStatus(projectStatus);

        projectDao.update(project);
    }
}
