package repository;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

public final class SNS {
    public static Region region = Region.US_EAST_1;
    private static SnsClient INSTANCE;

    public static SnsClient getInstance() {
        if(INSTANCE == null)
            INSTANCE = SnsClient
                    .builder()
                    .region(region)
                    .build();
        return INSTANCE;
    }
}
