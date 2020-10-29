package no.kristiania.http;

import no.kristiania.database.Member;
import no.kristiania.database.MemberDao;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class UpdateMemberController implements HttpController {

    private final MemberDao memberDao;

    public UpdateMemberController(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        Integer memberId = Integer.valueOf(requestParameter.getParameter("memberId"));
        Integer projectId = Integer.valueOf(requestParameter.getParameter("projectId"));
        Member member = memberDao.retrieve(memberId);
        member.setProjectId(projectId);

        memberDao.update(member);
    }
}
