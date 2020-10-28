package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProjectPostController implements HttpController{
    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        String body = "Okay";
        String response = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                body;

        clientSocket.getOutputStream().write(response.getBytes());
    }
}
