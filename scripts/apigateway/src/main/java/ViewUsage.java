import com.amazonaws.services.apigateway.AmazonApiGateway;
import com.amazonaws.services.apigateway.AmazonApiGatewayClientBuilder;
import com.amazonaws.services.apigateway.model.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewUsage {
    public static final AmazonApiGateway apigateway = AmazonApiGatewayClientBuilder.standard().build();
    public static final String BASIC_PLAN = "dq97wt";

    public static void main(String[] args) {
        GetUsagePlanKeysResult getUsagePlanKeys = apigateway
                .getUsagePlanKeys(new GetUsagePlanKeysRequest()
                        .withUsagePlanId(BASIC_PLAN));

        ArrayList<String> apiKeyIdArray = new ArrayList<>();
        for(UsagePlanKey upk : getUsagePlanKeys.getItems())
            apiKeyIdArray.add(upk.getId());

        GetUsageResult getUsage = apigateway
                .getUsage(new GetUsageRequest()
                        .withUsagePlanId(BASIC_PLAN)
                        .withStartDate("2021-03-01")
                        .withEndDate("2021-03-31"));

        double sum = 0.0;
        String name = "";
        HashMap<String, Double> map = new HashMap<>();
        for(String id : apiKeyIdArray){
            if(getUsage.getItems().get(id) != null){
                for(int i = 0; i < getUsage.getItems().get(id).size(); i++){
                    sum += getUsage.getItems().get(id).get(i).get(0);

                    GetApiKeyResult getApiKey = apigateway.getApiKey(new GetApiKeyRequest()
                            .withApiKey(id));

                    name = getApiKey.getName();
                }

                map.put(name, sum);
                sum = 0;
            }
        }

        for(String key : map.keySet())
            System.out.println("Empresa: " + key + ", quantidade de requisições: " + map.get(key));
    }
}
