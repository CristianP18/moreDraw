package helper;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import configuration.ResponseType;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;

import java.util.Arrays;

public class LogHelper {
    public LambdaLogger logger;
    private static LogHelper INSTANCE;

    public LogHelper(Context context) {
        logger = context.getLogger();
    }

    public static LogHelper getInstance(Context context) {
        if(INSTANCE == null)
            INSTANCE = new LogHelper(context);
        return INSTANCE;
    }

    public void content(APIGatewayProxyRequestEvent request){
        logger.log("params:" + request.getQueryStringParameters());
        logger.log("header:" + request.getHeaders());
        logger.log("path:" + request.getPath());
        logger.log("path parameters:" + request.getPathParameters());
        logger.log("body:" + request.getBody());
    }

    public void warning(Exception e){
        logger.log("type:" + ResponseType.WARNING);
        messageAndStacktrace(e);
    }

    public void error(Exception e){
        logger.log("type:" + ResponseType.ERROR);
        messageAndStacktrace(e);
    }

    public void messageAndStacktrace(Exception e){
        String data = " message:" + e.getMessage() + " stacktrace:" + Arrays.toString(e.getStackTrace()) +
                "\n========================================";
//        PutRecordRequest putRecordRequest = PutRecordRequest.builder()
//                .streamName(System.getenv("ENV") + "_log")
//                .partitionKey(request)
//                .data(SdkBytes.fromByteBuffer(ByteBuffer.wrap(data.getBytes())))
//                .build();
//        Kinesis.getInstance().putRecord(putRecordRequest);

        logger.log("message:" + e.getMessage());
        logger.log("stacktrace:" + Arrays.toString(e.getStackTrace()));
    }
}