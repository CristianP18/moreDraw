package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import configuration.*;
import exceptions.Forbidden;
import exceptions.InvalidRequest;
import exceptions.NotFound;
import exceptions.Unauthorized;
import facade.PostUsersLoginFacade;

public class PostUsersLoginHandler extends GenericRequestHandler {
    private final PostUsersLoginFacade postUsersLoginFacade = new PostUsersLoginFacade();

    /**
     * <h3>Login.</h3>
     *
     * @param request
     * <ul>
     * <li>{@link model.PostUsersLoginRequestModel} no corpo.</li>
     * </ul>
     * @return {@link model.PostUsersLoginResponseModel} modelo de resposta.
     * @throws InvalidRequest caso corpo possuir equívocos, atributos inválidos.
     * @throws Unauthorized caso as credenciais sejam inválidas.
     * @throws Exception caso aconteça um erro desconhecido.
     * @since 0.1
     * @see "https://admin.moreDraw.com.br/docs/#/Usu%C3%A1rios/postLoginUsers"
     * @author Rafael Castilhos
     */
    @Override
    public Response<?> execute(APIGatewayProxyRequestEvent request, Context context, String locale)
            throws NotFound, Unauthorized, Forbidden {
        return postUsersLoginFacade.facade(
                request.getBody(),
                locale);
    }
}
