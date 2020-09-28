package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpMessage {

    private String startLine = null;
    private Map<String, String> headers = new HashMap<>();

    public HttpMessage(String startLine) {
        this.startLine = startLine;
    }

    public void setHeader(String name, String value){
         headers.put(name, value);
    }

    public void write(Socket socket) throws IOException {
        socket.getOutputStream().write((startLine + "\r\n").getBytes());
        for (Map.Entry<String, String> header: headers.entrySet()){
            socket.getOutputStream().write((header.getKey() + ": " + header.getValue() + "\r\n").getBytes());
        }
        socket.getOutputStream().write("\r\n".getBytes());
    }
}
