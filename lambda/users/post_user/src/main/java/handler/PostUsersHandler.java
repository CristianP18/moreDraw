package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.firebase.messaging.FirebaseMessagingException;
import configuration.GenericRequestHandler;
import configuration.Response;
import exceptions.InvalidAddress;
import exceptions.InvalidOneTimePassword;
import exceptions.NotFound;
import facade.PostUsersFacade;

import java.io.IOException;

public class PostUsersHandler extends GenericRequestHandler {
    private final PostUsersFacade postUsersFacade = new PostUsersFacade();

    /**
     * <h3>Verifica código OTP.</h3>
     *
     * @param request
     * <ul>
     * <li>{@link model.UsersVerifyRequestModel} no corpo.</li>
     * </ul>
     * @throws InvalidOneTimePassword caso código OTP seja inválido.
     * @throws Exception caso aconteça um erro desconhecido.
     * @since 0.1
     * @see "https://admin.moreDraw.com.br/docs/#/Usu%C3%A1rios/postVerifyUsers"
     * @author Rafael Castilhos
     * @return
     */
    @Override
    public Response<?> execute(APIGatewayProxyRequestEvent request, Context context, String locale)
            throws NotFound, InvalidOneTimePassword, InvalidAddress, IOException, FirebaseMessagingException {
        return postUsersFacade.facade(
                request.getBody(),
                locale);
    }
}
