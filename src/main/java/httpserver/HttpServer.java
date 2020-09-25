package httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {


    public HttpServer(int port) throws IOException {

        ServerSocket serverSocket = new ServerSocket(port);

        new Thread(() -> {
         try {
             Socket socket = serverSocket.accept();
             handleRequest(socket);
         } catch (IOException e) {
             e.printStackTrace();
         }
        }).start();

    }

    public static void main(String[] args) throws IOException {

        new HttpServer(8080);

    }

    private static void handleRequest(Socket socket) throws IOException {
        String statusCode = "200";
        String body = null;
        String requestLine = HttpClient.readLine(socket);
        System.out.println(requestLine);

        String requestTarget = requestLine.split(" ")[1];
        int questionPos = requestTarget.indexOf("?");
        if(questionPos != -1) {

            QueryString queryString = new QueryString(requestTarget.substring(questionPos + 1));

            statusCode = queryString.getParameter("status");
            if(statusCode == null) statusCode = "200";
            body = queryString.getParameter("body");
        }

        if(body == null) body = "Hello <strong>World</strong>";



        String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Content-Type: text/html; charset=utf-8\r\n" +
                "Content-Length: 10\r\n" +
                "\r\n" +
                "Kristiania";

        socket.getOutputStream().write(response.getBytes());
    }
}
