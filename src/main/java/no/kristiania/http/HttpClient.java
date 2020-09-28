package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;

public class HttpClient {

    private String responseBody;
    private final HttpMessage responseMessage;


    public HttpClient(String hostname, int port, String requestTarget) throws IOException {

        Socket socket = new Socket(hostname, port);

        HttpMessage requestMessage = new HttpMessage("GET " + requestTarget + " HTTP/1.1");
        requestMessage.setHeader("Host", hostname);
        requestMessage.write(socket);

        responseMessage = HttpMessage.read(socket);



        int contentLength = Integer.parseInt(getResponseHeader("Content-Length"));
        StringBuilder body = new StringBuilder();
        for (int i = 0; i < contentLength; i++) {
            body.append((char) socket.getInputStream().read());
        }
        responseBody = body.toString();

    }

    public HttpClient(String hostname, int port, String requestTarget, String method, QueryString form) throws IOException {

        Socket socket = new Socket(hostname, port);

        HttpMessage requestMessage = new HttpMessage("GET " + requestTarget + " HTTP/1.1");
        requestMessage.setHeader("Host", hostname);
        requestMessage.write(socket);

        responseMessage = HttpMessage.read(socket);
    }


    public static void main(String[] args) throws IOException {
        String hostname = "urlecho.appspot.com";
        int port = 80;
        String requestTarget = "/echo?status&body=Hello%20World!";

        new HttpClient(hostname, port, requestTarget);
    }


    public int getStatusCode() {
        String[] responseLineParts = responseMessage.getStartLine().split(" ");
        int statusCode = Integer.parseInt(responseLineParts[1]);
        return statusCode;
    }

    public String getResponseHeader(String headerName) {
        return responseMessage.getHeader(headerName);
    }


    public String getResponseBody() {
        return responseBody;
    }
}

