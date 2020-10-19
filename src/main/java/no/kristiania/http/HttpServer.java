package no.kristiania.http;

import no.kristiania.database.Member;
import no.kristiania.database.MemberDao;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private File contentRoot;
    //private static final List<Member> members = new ArrayList<>();
    private MemberDao memberDao;

    public HttpServer(int port, DataSource dataSource) throws IOException {
        memberDao = new MemberDao(dataSource);

        ServerSocket serverSocket = new ServerSocket(port);

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
        dataSource.setUser(properties.getProperty("dataSource.user"));
        dataSource.setPassword(properties.getProperty("dataSource.password"));

        logger.info("Using database {}", dataSource.getUrl());
        Flyway.configure().dataSource(dataSource).load().migrate();


        HttpServer server = new HttpServer(8080, dataSource);
        logger.info("Started on http://localhost:{}/index.html", 8080);
        //server.setContentRoot(new File("src/main/resources/"));

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
           QueryString requestParameter = new QueryString(request.getBody());

           Member member = new Member();
           member.setFirstName(requestParameter.getParameter("first_name"));
           member.setLastName(requestParameter.getParameter("last_name"));
           member.setEmail(requestParameter.getParameter("email"));

           memberDao.insert(member);

           String body = "Okay";
           String response = "HTTP/1.1 200 OK\r\n" +
                   "Content-Length: " + body.length() + "\r\n" +
                   "Connection: close\r\n" +
                   "\r\n" +
                   body;

           clientSocket.getOutputStream().write(response.getBytes());
       } else {
           if (requestPath.equals("/echo")) {
               handleEchoRequest(clientSocket, requestTarget, questionPos);
           } else if (requestPath.equals("/api/members")) {
               handleGetMembers(clientSocket);
           } else {
               File file = new File(contentRoot, requestPath);

               if (!file.exists()) {
                   String body = file + " does not exist";
                   String response = "HTTP/1.1 404 Not Found\r\n" +
                           "Content-Length: " + body.length() + "\r\n" +
                           "Connection: close\r\n" +
                           "\r\n" +
                           body;
                   clientSocket.getOutputStream().write(response.getBytes());
                   return;
               }
               String statusCode = "200";
               String contentType = "text/plain";
               if (file.getName().endsWith(".html")) {
                   contentType = "text/html";
               } else if (file.getName().endsWith(".css")) {
                   contentType = "text/css";
               }

               String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                       "Content-Length: " + file.length() + "\r\n" +
                       "Connection: close\r\n" +
                       "Content-Type: " + contentType + "\r\n" +
                       "\r\n";
               clientSocket.getOutputStream().write(response.getBytes());

               new FileInputStream(file).transferTo(clientSocket.getOutputStream());
           }
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
            body += "<li>" + member.getFirstName() + " " + member.getLastName() +  " (" + member.getEmail() + ")" + "</li>";
        }
        body += "</ul>";

        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                body;

        clientSocket.getOutputStream().write(response.getBytes());
    }



    public void setContentRoot(File documentRoot) {
        contentRoot = documentRoot;
    }

}
