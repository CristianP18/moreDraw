package configuration;

import java.util.HashMap;
import java.util.Map;

public class Headers {
    public static final String DELETE = "OPTIONS,DELETE",
            GET = "OPTIONS,GET",
            PATCH = "OPTIONS,PATCH",
            POST = "OPTIONS,POST",
            PUT = "OPTIONS,PUT",
            WILDCARD = "OPTIONS, GET, POST, DELETE, PATCH, PUT";

    public static Map<String, String> headers(String method){
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("Access-Control-Allow-Origin", "*");
        header.put("Access-Control-Allow-Headers", "*");
        header.put("Access-Control-Allow-Methods", getMethod(method));
        return header;
    }

    private static String getMethod(String method){
        if(method == null)
            return WILDCARD;
        if(method.equals("GET"))
            return GET;
        if(method.equals("DELETE"))
            return DELETE;
        if(method.equals("POST"))
            return POST;
        if(method.equals("PATCH"))
            return PATCH;
        if(method.equals("PUT"))
            return PUT;
        else
            return WILDCARD;
    }
}
