package no.kristiania.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpClientTest {

    @Test
    void shouldReadSuccessStatusCode() throws IOException {
        HttpClient httpClient = makeEchoRequest("/echo?status=200");
        assertEquals(200, httpClient.getStatusCode());
    }

    private HttpClient makeEchoRequest(String requestTarget) throws IOException {
        return new HttpClient("urlecho.appspot.com", 80, requestTarget);
    }

    @Test
    void shouldReadHeaders() throws IOException {
        HttpClient httpClient = makeEchoRequest("/echo?body=Kristiania");
        assertEquals("10", httpClient.getResponseHeader("Content-Length"));
    }

    @Test
    void shouldReadBody() throws IOException {
        HttpClient httpClient = makeEchoRequest("/echo?body=HelloWorld");
        assertEquals("HelloWorld", httpClient.getResponseBody());
    }

}
