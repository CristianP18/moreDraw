package repository;

import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.InputStream;
import static configuration.GenericRequestHandler.S3Service;

public final class BucketS3Repository {
    private static BucketS3Repository INSTANCE;

    public static BucketS3Repository getInstance() {
        if (INSTANCE == null)
            INSTANCE = new BucketS3Repository();
        return INSTANCE;
    }

    public void postDocumentFile(String bucket, String key, InputStream fis, long contentLength, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        metadata.setContentType(contentType);
        metadata.setCacheControl("public, max-age=2592000");

        S3Service.putObject(bucket, key, fis, metadata);
    }

    /**
     * Remove arquivo pela chave no S3.
     */
    public void removeFileFromS3(String bucket, String key) {
        S3Service.deleteObject(bucket, key);
    }
}
