package no.kristiania.http;

import java.util.HashMap;
import java.util.Map;

public class QueryString {

    private final Map<String, String> parameters = new HashMap<>();

    public QueryString(String queryString) {
        if (queryString.isEmpty()) return;
        for(String parameter : queryString.split("&")){
            int equalsPos = parameter.indexOf("=");
            String key = parameter.substring(0, equalsPos);
            String value = parameter.substring(equalsPos + 1);
            parameters.put(key, value);
        }


    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public void addParameter(String name, String value) {
        parameters.put(name, value);
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
}
