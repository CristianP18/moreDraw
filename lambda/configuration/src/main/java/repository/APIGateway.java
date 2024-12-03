package repository;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;

public final class APIGateway {
    public static Region region = Region.US_EAST_1;
    private static ApiGatewayClient INSTANCE;

    public static ApiGatewayClient getInstance() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                "...",
                "...");

        if(INSTANCE == null)
            INSTANCE = ApiGatewayClient
                    .builder()
                    .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                    .region(region)
                    .build();
        return INSTANCE;
    }
}
