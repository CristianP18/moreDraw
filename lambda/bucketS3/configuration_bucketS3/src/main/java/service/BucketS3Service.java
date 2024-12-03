package service;

import bundles.Bundle;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import exceptions.InvalidRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.MultipartStream;
import repository.BucketS3Repository;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class BucketS3Service extends Service {

    private static BucketS3Service INSTANCE;

    public static BucketS3Service getInstance() {
        if(INSTANCE == null)
            INSTANCE = new BucketS3Service();
        return INSTANCE;
    }


    public void uploadS3(String bucket, String key, APIGatewayProxyRequestEvent event, Context context, String locale)
            throws IOException, InvalidRequest {
        LambdaLogger logger = context.getLogger();
        String contentType = "";

        logger.log("event headers: " + event.getHeaders());
        byte[] body = Base64.decodeBase64(event.getBody().getBytes());

        Map<String, String> headers = event.getHeaders();
        if (headers != null){
            String lower = headers.get("content-type");
            String upper = headers.get("Content-Type");
            if (upper == null)
                contentType = lower;
            else if (lower == null)
                contentType = upper;
            else
                throw new InvalidRequest(Bundle.getInstance().getString("InvalidDoc", locale));
        }

        String[] boundaryArray = contentType.split("=");
        logger.log("boundaryArray: " + Arrays.toString(boundaryArray));
        byte[] boundaryBytes = boundaryArray[1].getBytes();
        logger.log("boundaryBytes: " + Arrays.toString(boundaryBytes));

        logger.log(new String(body, StandardCharsets.UTF_8) + "\n");
        ByteArrayInputStream content = new ByteArrayInputStream(body);
        MultipartStream multipartStream =
                new MultipartStream(content, boundaryBytes, body.length, null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean nextPart = multipartStream.skipPreamble();

        while (nextPart) {
            String header = multipartStream.readHeaders();
            logger.log("Headers: " + header);

            multipartStream.readBodyData(out);
            nextPart = multipartStream.readBoundary();
        }
        InputStream fis = new ByteArrayInputStream(out.toByteArray());

        BucketS3Repository.getInstance().postDocumentFile(bucket, key, fis, out);
        logger.log("Put object in S3");
    }

    public void removeLastAvatarFromS3(String bucket, String key){
        if(!key.equals("user_orange.jpg") && !key.equals("user_gray.png"))
            BucketS3Repository.getInstance().removeFileFromS3(bucket, key);
    }

}