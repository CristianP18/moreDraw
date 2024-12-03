package repository;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

public class SQS {
    public static Region region = Region.US_EAST_1;
    private static SqsClient INSTANCE;

    public static SqsClient getInstance() {
        if(INSTANCE == null)
            INSTANCE = SqsClient
                    .builder()
                    .region(region)
                    .build();
        return INSTANCE;
    }
}
