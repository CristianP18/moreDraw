package repository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class Dynamo {
    public static Region region = Region.US_EAST_1;
    public static final String DB_PREFIX = System.getenv("ENV");

    private static DynamoDbClient INSTANCE_DYNAMO;
    private static DynamoDbEnhancedClient INSTANCE_MAPPER;

    private static final DynamoDbClient mapper = DynamoDbClient
            .builder()
            .region(region)
            .build();

    public static DynamoDbClient getInstanceMapper() {
        if(INSTANCE_DYNAMO == null)
            INSTANCE_DYNAMO = mapper;

        return INSTANCE_DYNAMO;
    }

    public static DynamoDbEnhancedClient getInstanceDynamo() {
        if(INSTANCE_MAPPER == null)
            INSTANCE_MAPPER = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(mapper)
                    .build();

        return INSTANCE_MAPPER;
    }
}
