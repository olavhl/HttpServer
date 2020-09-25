package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {

    private final int statusCode;
    private final String responseBody;
    private Map<String, String> responseHeaders = new HashMap<>();


    public HttpClient(String hostname, int port, String requestTarget) throws IOException {

        Socket socket = new Socket(hostname, port);

        String request = "GET " + requestTarget + " HTTP/1.1\r\n" +
                "Host: " + hostname + "\r\n\r\n";
        socket.getOutputStream().write(request.getBytes());

        String line = readLine(socket);

        String[] responseLineParts = line.split(" ");

        statusCode = Integer.parseInt(responseLineParts[1]);

        String headerLine;
        while (!(headerLine = readLine(socket)).isEmpty()) {

            int colonPos = headerLine.indexOf(":");
            String name = headerLine.substring(0, colonPos);
            String value = headerLine.substring(colonPos + 1).trim();
            responseHeaders.put(name, value);


        }

        int contentLength = Integer.parseInt(getResponseHeader("Content-Length"));
        StringBuilder body = new StringBuilder();
        for (int i = 0; i < contentLength; i++) {
            body.append((char) socket.getInputStream().read());
        }
        responseBody = body.toString();

    }


    public static String readLine(Socket socket) throws IOException {

        // Creating StringBuilder line to save the response
        StringBuilder line = new StringBuilder();
        int c;
        while ((c = socket.getInputStream().read()) != -1) {

            if (c == '\r') {
                socket.getInputStream().read();
                break;
            }


            // Adding char into line when it's not '\n'
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


    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseHeader(String headerName) {
        return responseHeaders.get(headerName);
    }


    public String getResponseBody() {
        return responseBody;
    }
}

