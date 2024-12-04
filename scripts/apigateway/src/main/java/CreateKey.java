import com.amazonaws.services.apigateway.AmazonApiGateway;
import com.amazonaws.services.apigateway.AmazonApiGatewayClientBuilder;
import com.amazonaws.services.apigateway.model.*;

public class CreateKey {
    public static final AmazonApiGateway apigateway = AmazonApiGatewayClientBuilder.standard().build();
    public static void main(String[] args) {
        CreateApiKeyResult res = apigateway.createApiKey(
                new CreateApiKeyRequest()
                        .withName("Nome da chave")
                        .withDescription("API moreDraw")
                        .withEnabled(true));

        System.out.println(res.getValue());
    }
}
