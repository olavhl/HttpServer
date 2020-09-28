package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;

public class HttpClient {

    private final String responseBody;
    private final HttpMessage responseMessage;


    public HttpClient(String hostname, int port, String requestTarget) throws IOException {

        Socket socket = new Socket(hostname, port);

        HttpMessage requestMessage = new HttpMessage("GET " + requestTarget + " HTTP/1.1");
        requestMessage.setHeader("Host", hostname);
        requestMessage.write(socket);

        responseMessage = HttpMessage.read(socket);
        responseBody = responseMessage.readBody(socket);

    }

    public HttpClient(String hostname, int port, String requestTarget, String method, QueryString form) throws IOException {

        Socket socket = new Socket(hostname, port);

        String requestBody = form.getQueryString();

        HttpMessage requestMessage = new HttpMessage(method + " " + requestTarget + " HTTP/1.1");
        requestMessage.setHeader("Host", hostname);
        requestMessage.write(socket);
        socket.getOutputStream().write(requestBody.getBytes());

        responseMessage = HttpMessage.read(socket);
        responseBody = responseMessage.readBody(socket);
    }

    public static void main(String[] args) throws IOException {
        String hostname = "urlecho.appspot.com";
        int port = 80;
        String requestTarget = "/echo?status&body=Hello%20World!";

        new HttpClient(hostname, port, requestTarget);
    }


    public int getStatusCode() {
        String[] responseLineParts = responseMessage.getStartLine().split(" ");
        return Integer.parseInt(responseLineParts[1]);
    }

    public String getResponseHeader(String headerName) {
        return responseMessage.getHeader(headerName);
    }


    public String getResponseBody() {
        return responseBody;
    }
}

