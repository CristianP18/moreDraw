package handler;

import bundles.Bundle;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import configuration.GenericRequestHandler;
import configuration.Response;
import exceptions.NotFound;
import exceptions.Unauthorized;
import facade.DeleteUsersFacade;
import helper.JWTHelper;

public class DeleteUsersHandler extends GenericRequestHandler {
    private final DeleteUsersFacade deleteUsersFacade = new DeleteUsersFacade();

    /**
     * <h3>Remove usuário.</h3>
     *
     * @param request
     * <ul>
     * <li>JWT Token no cabeçalho.</li>
     * </ul>
     * @return {@link model.Ok} modelo de resposta.
     * @throws Unauthorized caso Token JWT seja nulo ou expirado.
     * @throws NotFound caso alguma entidade não seja encontrada.
     * @throws Exception caso aconteça um erro desconhecido.
     * @since 0.1
     * @see "https://admin.moreDraw.com.br/docs/#/Usu%C3%A1rios/deleteUsers"
     * @author Rafael Castilhos
     */
    @Override
    public Response<?> execute(APIGatewayProxyRequestEvent request, Context context, String locale)
            throws Unauthorized, NotFound {
        if(JWTHelper.isValidJwt(JWTHelper.getToken(request))) {
            return deleteUsersFacade.facade(
                    JWTHelper.decodeRequestId(JWTHelper.getToken(request)),
                    locale);
        }
        else
            throw new Unauthorized(Bundle.getInstance().getString("Unauthorized", locale));
    }
}
