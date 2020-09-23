package httpserver;

import java.io.IOException;
import java.net.Socket;

public class HttpClient {
    private final int responseCode;

    public HttpClient(String hostname, int port, String requestTarget) throws IOException {

        Socket socket = new Socket(hostname, port);

        String request = "GET " + requestTarget + " HTTP/1.1\r\n" +
                "Host: " + hostname + "\r\n\r\n";
        socket.getOutputStream().write(request.getBytes());

        String line = readLine(socket);

        System.out.println(line);
        String[] responseLineParts = line.split(" ");
        responseCode = Integer.parseInt(responseLineParts[1]);
        
    }

    private String readLine(Socket socket) throws IOException {
        // Creating StringBuilder line to save the response
        StringBuilder line = new StringBuilder();
        int c;
        while ((c = socket.getInputStream().read()) != -1) {

            if (c == '\r') {
                break;
            }

            // Adding char into line when its not '\n'
            line.append((char) c);
        }
        return line.toString();
    }

    public static void main(String[] args) throws IOException {
        String hostname = "urlecho.appspot.com";
        int port = 80;
        String requestTarget = "/echo?status&body=Hello%20World!";

        new HttpClient(hostname, port, requestTarget);
    }

    public int getResponseCode() {

        return responseCode;
    }
}
