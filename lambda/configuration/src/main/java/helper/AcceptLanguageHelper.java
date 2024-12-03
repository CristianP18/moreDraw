package helper;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import java.util.Map;

public final class AcceptLanguageHelper {
    private static AcceptLanguageHelper INSTANCE;

    public static AcceptLanguageHelper getInstance() {
        if(INSTANCE == null)
            INSTANCE = new AcceptLanguageHelper();
        return INSTANCE;
    }

    public String getAcceptLanguage(APIGatewayProxyRequestEvent request){
        if(request != null && request.getHeaders() != null){
            Map<String, String> headers = request.getHeaders();
            String keyUpper = "Accept-Language";
            String keyDown = "accept-language";

            if(headers.containsKey(keyUpper))
                return headers.get(keyUpper);

            else if(headers.containsKey(keyDown))
                return headers.get(keyDown);
        }
        return "pt_BR";
    }
}
