package no.kristiania.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class HttpServer {

    private static File documentRoot;
    private final List<String> memberNames = new ArrayList<>();

    public HttpServer(int port) throws IOException {

        ServerSocket serverSocket = new ServerSocket(port);

        new Thread(() -> {
         try {
             while (true) {
                 Socket clientSocket = serverSocket.accept();
                 handleRequest(clientSocket);
             }
         } catch (IOException e) {
             e.printStackTrace();
         }
        }).start();

    }

    public static void main(String[] args) throws IOException {

        HttpServer server = new HttpServer(8080);
        server.setDocumentRoot(new File("src/main/resources/"));

    }

    private static void handleRequest(Socket clientSocket) throws IOException {

        String requestLine = HttpMessage.readLine(clientSocket);
        System.out.println(requestLine);

        String requestTarget = requestLine.split(" ")[1];
        String statusCode = null;
        String body = null;

        int questionPos = requestTarget.indexOf("?");
        if(questionPos != -1) {

            QueryString queryString = new QueryString(requestTarget.substring(questionPos + 1));
            statusCode = queryString.getParameter("status");

            if(statusCode == null) statusCode = "200";
            body = queryString.getParameter("body");

        }else if(!requestTarget.equals("/echo")){
            File targetFile = new File(documentRoot, requestTarget);

            if (!targetFile.exists()){
                writeResponse(clientSocket, "404", requestTarget + " not found");

                return;
            }

            HttpMessage responseMessage = new HttpMessage("HTTP/1.1 200 OK");
            responseMessage.setHeader("Content-Length", String.valueOf(targetFile.length()));
            responseMessage.setHeader("Content-type", "text/html");

            if(targetFile.getName().endsWith(".txt")) {
                responseMessage.setHeader("Content-Type", "text/plain");
            }

            responseMessage.write(clientSocket);

            try(FileInputStream inputStream = new FileInputStream(targetFile)){
                inputStream.transferTo(clientSocket.getOutputStream());
            }

        }

        if(body == null) body = "Hello <strong>World</strong>";


        writeResponse(clientSocket, statusCode, body);
    }

    private static void writeResponse(Socket clientSocket, String statusCode, String body) throws IOException {

        HttpMessage responseMessage = new HttpMessage("HTTP/1.1 " + statusCode + " OK");
        responseMessage.setHeader("Content-Length", String.valueOf(body.length()));
        responseMessage.setHeader("Content-Type", "text/plain");
        responseMessage.write(clientSocket);
        clientSocket.getOutputStream().write(body.getBytes());
    }

    public void setDocumentRoot(File documentRoot) {
        HttpServer.documentRoot = documentRoot;
    }

    public List<String> getMemberNames() {
        return memberNames;
    }
}
