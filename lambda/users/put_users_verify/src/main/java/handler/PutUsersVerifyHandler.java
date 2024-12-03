package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.firebase.messaging.FirebaseMessagingException;
import configuration.GenericRequestHandler;
import configuration.Response;
import exceptions.NotFound;
import facade.PutUsersVerifyFacade;

import java.io.IOException;

public class PutUsersVerifyHandler extends GenericRequestHandler {
    private final PutUsersVerifyFacade putUsersVerifyFacade = new PutUsersVerifyFacade();

    /**
     * <h3>Atualiza e reenvia código OTP.</h3>
     *
     * <ul>
     * <li>{@link model.UsersVerifyRequestModel} no corpo.</li>
     * </ul>
     *
     * @return
     * @throws Exception caso aconteça um erro desconhecido.
     * @author Rafael Castilhos
     * @see "https://admin.moreDraw.com.br/docs/#/Usu%C3%A1rios/putUsersVerify"
     * @since 0.1
     */
    @Override
    public Response<?> execute(APIGatewayProxyRequestEvent request, Context context, String locale)
            throws NotFound, IOException, FirebaseMessagingException {
        return putUsersVerifyFacade.facade(
                request.getBody(),
                locale);
    }
}
