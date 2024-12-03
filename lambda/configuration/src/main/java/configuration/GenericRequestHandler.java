package configuration;

import bundles.Bundle;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.s3.AmazonS3;
import exceptions.*;
import helper.AcceptLanguageHelper;
import helper.GsonHelper;
import helper.LogHelper;
import repository.Dynamo;
import repository.S3;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import java.io.IOException;

public abstract class GenericRequestHandler implements
        RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    public static DynamoDbEnhancedClient DynamoService = Dynamo.getInstanceDynamo();
    public static AmazonS3 S3Service = S3.getInstance();


    public abstract Response<?> execute(APIGatewayProxyRequestEvent request, Context context, String locale)
            throws Exception;

    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent request, Context context) {
        String locale = AcceptLanguageHelper.getInstance().getAcceptLanguage(request);

        String method = null;
        if(request.getHttpMethod() != null)
            method = request.getHttpMethod();

        LogHelper logHelper = LogHelper.getInstance(context);

        try {
//            Kinesis.log("handle");
//            Kinesis.log(request, context);
            logHelper.content(request);
            return onSuccess(request, context, method, locale);
        } catch (InvalidRequest | InvalidOneTimePassword |
                 InvalidAddress | InvalidRelationalToken | IOException e) {
            logHelper.warning(e);
            return onInvalidRequest(e, method);
        } catch (Unauthorized e) {
            logHelper.warning(e);
            return onUnauthorized(e, method);
        } catch (Forbidden e) {
            logHelper.warning(e);
            return onForbidden(e, method);
        } catch (NotFound e) {
            logHelper.warning(e);
            return onNotFound(e, method);
        } catch (Conflict e) {
            logHelper.warning(e);
            return onConflict(e, method);
        } catch (NullPointerException e) {
            logHelper.warning(e);
            return onNullPointer(method, locale);
        } catch (Exception e) {
            logHelper.error(e);
            return onException(method, locale);
        }
    }

    protected APIGatewayProxyResponseEvent onSuccess(APIGatewayProxyRequestEvent request,
                                                     Context context, String method, String locale)
            throws Exception {
//        Kinesis.log("onsuccess");
//        Kinesis.log(request, context);
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(ResponseStatusCode.OK)
                .withHeaders(Headers.headers(method))
                .withBody(GsonHelper.getInstance().toJson(execute(request, context, locale)));
    }

    protected APIGatewayProxyResponseEvent onInvalidRequest(Exception e, String method) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(ResponseStatusCode.BAD_REQUEST)
                .withHeaders(Headers.headers(method))
                .withBody(catchResponse(
                        e.getMessage(),
                        ResponseType.WARNING,
                        ResponseStatusCode.BAD_REQUEST));
    }

    protected APIGatewayProxyResponseEvent onUnauthorized(Unauthorized e, String method) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(ResponseStatusCode.UNAUTHORIZED)
                .withHeaders(Headers.headers(method))
                .withBody(catchResponse(
                        e.getMessage(),
                        ResponseType.WARNING,
                        ResponseStatusCode.UNAUTHORIZED));
    }

    protected APIGatewayProxyResponseEvent onForbidden(Forbidden e, String method) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(ResponseStatusCode.FORBIDDEN)
                .withHeaders(Headers.headers(method))
                .withBody(catchResponse(
                        e.getMessage(),
                        ResponseType.WARNING,
                        ResponseStatusCode.FORBIDDEN));
    }

    protected APIGatewayProxyResponseEvent onNotFound(NotFound e, String method) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(ResponseStatusCode.NOT_FOUND)
                .withHeaders(Headers.headers(method))
                .withBody(catchResponse(
                        e.getMessage(),
                        ResponseType.WARNING,
                        ResponseStatusCode.NOT_FOUND));
    }

    protected APIGatewayProxyResponseEvent onConflict(Conflict e, String method) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(ResponseStatusCode.CONFLICT)
                .withHeaders(Headers.headers(method))
                .withBody(catchResponse(
                        e.getMessage(),
                        ResponseType.WARNING,
                        ResponseStatusCode.CONFLICT));
    }

    protected APIGatewayProxyResponseEvent onNullPointer(String method, String locale) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(ResponseStatusCode.BAD_REQUEST)
                .withHeaders(Headers.headers(method))
                .withBody(catchResponse(
                        Bundle.getInstance().getString("NullOrInvalidRequest", locale),
                        ResponseType.WARNING,
                        ResponseStatusCode.BAD_REQUEST));
    }

    protected APIGatewayProxyResponseEvent onException(String method, String locale) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(ResponseStatusCode.INTERNAL_SERVER)
                .withHeaders(Headers.headers(method))
                .withBody(catchResponse(
                        Bundle.getInstance().getString("InternalServer", locale),
                        ResponseType.ERROR,
                        ResponseStatusCode.INTERNAL_SERVER));
    }

    private String catchResponse(String text, String type, int status) {
        return GsonHelper.getInstance().toJson(response(text, type, status));
    }

    private Response<?> response(String text, String type, int status) {
        Message RESPONSE_MESSAGE = new Message();

        RESPONSE_MESSAGE.setStatus(status);
        RESPONSE_MESSAGE.setType(type);
        RESPONSE_MESSAGE.setText(text);
        return Response.error(RESPONSE_MESSAGE);
    }
}