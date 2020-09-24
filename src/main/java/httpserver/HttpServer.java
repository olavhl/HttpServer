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


    }

    private static void handleRequest(Socket socket) throws IOException {
        String responseLine = HttpClient.readLine(socket);
        System.out.println(responseLine);

        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html; charset=utf-8\r\n" +
                "Content-Length: 10\r\n" +
                "\r\n" +
                "Kristiania";

        socket.getOutputStream().write(response.getBytes());
    }
}
