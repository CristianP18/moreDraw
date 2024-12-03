package repository;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

public class SES {
    public static Region region = Region.US_EAST_1;
    private static SesClient INSTANCE;

    public static SesClient getInstance() {
        if(INSTANCE == null)
            INSTANCE = SesClient
                    .builder()
                    .region(region)
                    .build();
        return INSTANCE;
    }

}
