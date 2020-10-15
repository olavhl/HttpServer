package no.kristiania.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryStringTest {

    @Test

    void shouldRetrieveQueryParameter() {
        QueryString queryString = new QueryString("status=200");
        assertEquals("200", queryString.getParameter("status"));
    }

    @Test
    void shouldRetrieveMultipleParameters() {
        QueryString queryString = new QueryString("body=Hello&status=200");
        assertEquals("200", queryString.getParameter("status"));
        assertEquals("Hello", queryString.getParameter("body"));
    }

    @Test
    void shouldAddMultipleParameters() {
        QueryString queryString = new QueryString("body=Hello");
        queryString.addParameter("status", "200");
        queryString.addParameter("halla", "jup");
        assertEquals("200", queryString.getParameter("status"));
        assertEquals("body=Hello&status=200&halla=jup", queryString.getQueryString());
    }


}
