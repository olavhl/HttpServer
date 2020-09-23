package httpserver;

import java.io.IOException;
import java.net.Socket;

public class HttpClient {
    public HttpClient(String hostname, int port, String requestTarget) throws IOException {

        Socket socket = new Socket(hostname, port);

        String request = "GET " + requestTarget + " HTTP/1.1\r\n" +
                "Host: " + hostname + "\r\n\r\n";

        socket.getOutputStream().write(request.getBytes());

        int c;
        while ((c = socket.getInputStream().read()) != -1) {
            System.out.print((char)c);
        }
        
    }

    public static void main(String[] args) throws IOException {
        String hostname = "urlecho.appspot.com";
        int port = 80;
        String requestTarget = "/echo?status&body=Hello%20World!";

        HttpClient httpClient = new HttpClient(hostname, port, requestTarget);
    }

    public int getResponseCode() {

        return 200;
    }
}
