package httpserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    private static File documentRoot;

    public HttpServer(int port) throws IOException {

        ServerSocket serverSocket = new ServerSocket(port);

        new Thread(() -> {
         try {
             Socket clientSocket = serverSocket.accept();
             handleRequest(clientSocket);
         } catch (IOException e) {
             e.printStackTrace();
         }
        }).start();

    }

    public static void main(String[] args) throws IOException {

        HttpServer server = new HttpServer(8080);
        server.setDocumentRoot(new File("src/main/resources"));

    }

    private static void handleRequest(Socket clientSocket) throws IOException {
        String statusCode = "200";
        String body = null;
        String requestLine = HttpClient.readLine(clientSocket);
        System.out.println(requestLine);

        String requestTarget = requestLine.split(" ")[1];
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
            String responseHeaders = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: " + targetFile.length() + "\r\n" +
                    "Content-Type: text/html\r\n" +
                    "\r\n";;

            clientSocket.getOutputStream().write(responseHeaders.getBytes());
            try(FileInputStream inputStream = new FileInputStream(targetFile)){
                inputStream.transferTo(clientSocket.getOutputStream());
            }



        }

        if(body == null) body = "Hello <strong>World</strong>";


        writeResponse(clientSocket, statusCode, body);
    }

    private static void writeResponse(Socket clientSocket, String statusCode, String body) throws IOException {
        String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Content-Length: " + body.length() +"\r\n" +
                "Content-Type: text/html; charset=utf-8\r\n" +
                "\r\n" +
                body;

        clientSocket.getOutputStream().write(response.getBytes());
    }

    public void setDocumentRoot(File documentRoot) {
        this.documentRoot = documentRoot;
    }
}
