package no.kristiania.http;

import no.kristiania.database.Member;
import no.kristiania.database.MemberDao;
import no.kristiania.database.Project;
import no.kristiania.database.ProjectDao;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ShowMemberAndStatusProjectController implements HttpController {
    private final MemberDao memberDao;
    private final ProjectDao projectDao;

    public ShowMemberAndStatusProjectController(MemberDao memberDao, ProjectDao projectDao) {
        this.memberDao = memberDao;
        this.projectDao = projectDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        Integer memberId = Integer.valueOf(requestParameter.getParameter("memberId"));
        String status = String.valueOf(requestParameter.getParameter("status"));
        Member member = memberDao.retrieve(memberId);

        String body = "<ul>";

        for (Project project : projectDao.list()) {
            if (project.getId().equals(member.getId()) && project.getStatus().equals(status)) {
                body += "<li>" + project.getName() + " (" + project.getStatus() + ")</li>";
            }
        }

        if (body.length() < 5) {
            body += "<li>No projects with this member and status</li>";
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
