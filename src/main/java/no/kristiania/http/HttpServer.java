package no.kristiania.http;

import no.kristiania.database.Member;
import no.kristiania.database.MemberDao;
import no.kristiania.database.ProjectDao;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private final Map<String, HttpController> controllers;
    private final MemberDao memberDao;
    private final ServerSocket serverSocket;


    public HttpServer(int port, DataSource dataSource) throws IOException {
        memberDao = new MemberDao(dataSource);
        ProjectDao projectDao = new ProjectDao(dataSource);
        controllers = Map.of(
                "/api/newProject", new ProjectPostController(projectDao),
                "/api/project", new ProjectGetController(projectDao, memberDao),
                "/api/projectOption", new ProjectOptionController(projectDao),
                "/api/memberOption", new MemberOptionController(memberDao),
                "/api/updateProject", new UpdateMemberController(memberDao),
                "/api/changeProjectStatus", new ChangeProjectStatusController(projectDao)
        );

        serverSocket = new ServerSocket(port);

        new Thread(() -> {
             while (true) {
                try (Socket clientSocket = serverSocket.accept();) {
                      handleRequest(clientSocket);
                } catch (IOException | SQLException e) {
                        e.printStackTrace();
                }
             }
        }).start();

    }

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        try (FileReader fileReader = new FileReader("pgr203.properties")) {
            properties.load(fileReader);
        }

        PGSimpleDataSource dataSource = new PGSimpleDataSource();

        dataSource.setUrl(properties.getProperty("dataSource.url"));
        dataSource.setUser(properties.getProperty("dataSource.username"));
        dataSource.setPassword(properties.getProperty("dataSource.password"));

        logger.info("Using database {}", dataSource.getUrl());
        Flyway.configure().dataSource(dataSource).load().migrate();


        HttpServer server = new HttpServer(8080, dataSource);
        logger.info("Started on http://localhost:{}/index.html", 8080);

    }

    private void handleRequest(Socket clientSocket) throws IOException, SQLException {

       HttpMessage request = new HttpMessage(clientSocket);
       String requestLine = request.getStartLine();
       System.out.println(requestLine);

       String requestMethod = requestLine.split(" ")[0];
       String requestTarget = requestLine.split(" ")[1];

       int questionPos = requestTarget.indexOf("?");
       String requestPath = questionPos != -1 ? requestTarget.substring(0, questionPos) : requestTarget;

       if (requestMethod.equals("POST")){
           if (requestPath.equals("/api/member")) {
               handlePostProject(clientSocket, request);
           } else {
               getController(requestPath).handle(request, clientSocket);
           }
       } else {
           if (requestPath.equals("/echo")) {
               handleEchoRequest(clientSocket, requestTarget, questionPos);
           } else if (requestPath.equals("/api/member")) {
               handleGetMembers(clientSocket);
           } else {
               HttpController controller = controllers.get(requestPath);
               if (controller != null) {
                   controller.handle(request, clientSocket);
               } else {
                   handleFileRequest(clientSocket, requestPath);
               }

               logger.info("See members at http://localhost:{}/showMembers.html", 8080);
           }
       }
    }

    private void handlePostProject(Socket clientSocket, HttpMessage request) throws SQLException, IOException {
        QueryString requestParameter = new QueryString(request.getBody());

        Member member = new Member();
        member.setFirstName(requestParameter.getParameter("first_name"));
        member.setLastName(requestParameter.getParameter("last_name"));
        member.setEmail(requestParameter.getParameter("email"));

        memberDao.insert(member);

        String response = "HTTP/1.1 302 Redirect\r\n" +
                "Location: http://localhost:8080/showMembers.html \r\n" +
                "\r\n";

        clientSocket.getOutputStream().write(response.getBytes());
    }

    private HttpController getController(String requestPath) {
        return controllers.get(requestPath);
    }

    private void handleFileRequest(Socket clientSocket, String requestPath) throws IOException {
        try (InputStream inputStream =  getClass().getResourceAsStream(requestPath)) {
            if (inputStream == null) {
                String body = requestPath + " does not exist";
                String response = "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: " + body.length() + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n" +
                        body;

                clientSocket.getOutputStream().write(response.getBytes());
                return;
            }

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            inputStream.transferTo(buffer);

            String contentType = "text/plain";
            if (requestPath.endsWith(".html")) {
                contentType = "text/html";
            } else if (requestPath.endsWith(".css")) {
                contentType = "text/css";
            }

            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: " + buffer.toByteArray().length + "\r\n" +
                    "Connection: close\r\n" +
                    "Content-Type: " + contentType +
                    "\r\n" +
                    "\r\n";

        clientSocket.getOutputStream().write(response.getBytes());
        clientSocket.getOutputStream().write(buffer.toByteArray());
        }


    }

    private void handleEchoRequest(Socket clientSocket, String requestTarget, int questionPos) throws IOException {
        String statusCode = "200";
        String body = "Hello <strong>World!</strong>";
        if (questionPos != -1) {
            QueryString queryString = new QueryString(requestTarget.substring(questionPos+1));

            if (queryString.getParameter("status") != null) {
                statusCode = queryString.getParameter("status");
            }
            if (queryString.getParameter("body") != null) {
                body = queryString.getParameter("body");
            }
        }
        String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                body;

        clientSocket.getOutputStream().write(response.getBytes());
    }

    private void handleGetMembers(Socket clientSocket) throws IOException, SQLException {
        String body = "<ul>";
        for (Member member : memberDao.list()) {
            body += "<li>" + member.getFirstName() + " " + member.getLastName()
                    +  " (" + member.getEmail() + ")" + "</li>";
        }
        body += "</ul>";

        body = decodeValue(body);
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                body;

        clientSocket.getOutputStream().write(response.getBytes());
    }

    public List<Member> getMemberNames() throws SQLException {
        return memberDao.list();
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public static String decodeValue(String str) {
        try {
            return URLDecoder.decode(str, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }
}
