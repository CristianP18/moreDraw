package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import configuration.GenericRequestHandler;
import configuration.Response;
import exceptions.InvalidRequest;
import exceptions.NotFound;
import facade.PostUsersVerifyPasswordFacade;

public class PostUsersVerifyPasswordHandler extends GenericRequestHandler {
    private final PostUsersVerifyPasswordFacade postUsersForgetPasswordFacade = new PostUsersVerifyPasswordFacade();

    @Override
    public Response<?> execute(APIGatewayProxyRequestEvent request, Context context, String locale)
            throws InvalidRequest, NotFound {
        return postUsersForgetPasswordFacade.facade(
                request.getBody(),
                locale);
    }
}
