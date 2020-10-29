package no.kristiania.http;

import no.kristiania.database.Member;
import no.kristiania.database.MemberDao;
import no.kristiania.database.Project;
import no.kristiania.database.ProjectDao;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProjectGetController implements HttpController {

    private final ProjectDao projectDao;
    private final MemberDao memberDao;

    public ProjectGetController(ProjectDao projectDao, MemberDao memberDao) {
        this.memberDao = memberDao;
        this.projectDao = projectDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        String body = "<ul>";
        for (Project project : projectDao.list()) {
            body += "<br><li>" + project.getName() + " (status: " + project.getStatus() + ")" + "</li>";

            for (Member member : memberDao.list()) {
                if (project.getId().equals(member.getProjectId())) {
                    body += "<li style=padding-left:1em>" + member.getFirstName() + " " + member.getLastName() + "</li>";
                }
            }
        }
        body += "</ul>";

        String response = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                body;

        clientSocket.getOutputStream().write(response.getBytes());
    }
}
