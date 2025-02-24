package service;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import repository.BucketS3Repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class BucketS3Service extends Service {

    private static BucketS3Service INSTANCE;

    public static BucketS3Service getInstance() {
        if (INSTANCE == null)
            INSTANCE = new BucketS3Service();
        return INSTANCE;
    }

    public void uploadS3(String bucket, String key, byte[] fileBytes, String contentType, Context context)
            throws IOException {

        LambdaLogger logger = context.getLogger();
        logger.log("Uploading file to S3 with key: " + key + " and contentType: " + contentType);

        InputStream inputStream = new ByteArrayInputStream(fileBytes);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(fileBytes);

        BucketS3Repository.getInstance().postDocumentFile(bucket, key, inputStream, outputStream.size(), contentType);

        logger.log("File successfully uploaded to S3: " + key);
    }

    public void removeLastAvatarFromS3(String bucket, String key) {
        if (!key.equals("user_orange.jpg") && !key.equals("user_gray.png"))
            BucketS3Repository.getInstance().removeFileFromS3(bucket, key);
    }
}
