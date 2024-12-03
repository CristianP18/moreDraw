package handler;

import bundles.Bundle;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import configuration.*;
import exceptions.*;
import facade.PutUsersFacade;
import helper.JWTHelper;
import java.io.IOException;

public class PutUsersHandler extends GenericRequestHandler {
    private final PutUsersFacade putUsersFacade = new PutUsersFacade();

    /**
     * <h3>Atualiza usuário.</h3>
     *
     * @param request
     * <ul>
     * <li>{@link model.PutUsersRequestModel} no corpo.</li>
     * <li>JWT Token no cabeçalho.</li>
     * </ul>
     * @return {@link model.UserResponseModel} modelo de resposta.
     * @throws InvalidRequest caso corpo possuir equívocos, atributos inválidos.
     * @throws InvalidAddress caso corpo possuir equívocos, atributos inválidos.
     * @throws Unauthorized caso Token JWT seja nulo ou expirado.
     * @throws NotFound caso alguma entidade não seja encontrada.
     * @throws Exception caso aconteça um erro desconhecido.
     * @since 0.1
     * @see "https://admin.moreDraw.com.br/docs/#/Usu%C3%A1rios/putUsers"
     * @author Rafael Castilhos
     */
    @Override
    public Response<?> execute(APIGatewayProxyRequestEvent request, Context context, String locale)
            throws Unauthorized, NotFound, InvalidAddress, IOException, InvalidRequest {
        if (JWTHelper.isValidJwt(JWTHelper.getToken(request))) {
            return putUsersFacade.facade(
                    JWTHelper.decodeRequestId(JWTHelper.getToken(request)),
                    request.getBody(),
                    locale);
        }
        else
            throw new Unauthorized(Bundle.getInstance().getString("Unauthorized", locale));
    }
}
