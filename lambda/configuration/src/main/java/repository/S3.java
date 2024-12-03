package repository;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public final class S3 {
    private static AmazonS3 INSTANCE;

    public static AmazonS3 getInstance() {
        if(INSTANCE == null)
            INSTANCE = AmazonS3ClientBuilder
                    .standard()
                    .withRegion(Regions.US_EAST_1)
                    .build();
        return INSTANCE;
    }

    /**
     * Cria link do documento no S3.
     */
    public static String link(String bucket, String key){
        return "https://s3.amazonaws.com/"
                .concat(bucket)
                .concat("/")
                .concat(key);
    }
}
