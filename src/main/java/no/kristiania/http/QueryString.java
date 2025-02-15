package no.kristiania.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class QueryString {
    private final Map<String, String> parameters = new LinkedHashMap<>();

    public QueryString(String queryString) {
        if (queryString.isEmpty()) return;
        for(String parameter : queryString.split("&")){
            int equalsPos = parameter.indexOf("=");
            String key = parameter.substring(0, equalsPos);
            String value = parameter.substring(equalsPos + 1);
            key = decodeValue(key);
            value = decodeValue(value);
            this.parameters.put(key, value);
        }
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public void addParameter(String key, String value) {
        parameters.put(key, value);
    }

    public String getQueryString() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(parameter.getKey()).append("=").append(parameter.getValue());
        }
        return result.toString();
    }

    public static String decodeValue(String str) {
        try {
            return URLDecoder.decode(str, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

}
