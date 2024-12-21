package handler;

import bundles.Bundle;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import configuration.GenericRequestHandler;
import configuration.Response;
import exceptions.Conflict;
import exceptions.InvalidRequest;
import exceptions.NotFound;
import exceptions.Unauthorized;
import facade.PostImageFacade;
import helper.JWTHelper;
import java.io.IOException;

public class PostImageHandler extends GenericRequestHandler {
    private final PostImageFacade postImageFacade = new PostImageFacade();
    /**
     * <h3>Cadastra localidade de armazenagem.</h3>
     *
     * @param request <ul>
     *                <li>{@link model.ImageRequestModel} no corpo.</li>
     *                <li>JWT Token ou Api Key no cabeçalho.</li>
     *                </ul>
     * @return {@link model.ImageResponseModel} modelo de resposta.
     * @throws InvalidRequest       caso corpo possuir equívocos, atributos inválidos.
     * @throws NullPointerException caso corpo possuir atributos nulos.
     * @throws Unauthorized         caso Api Key ou Token JWT seja nulo ou expirado.
     * @throws Exception            caso aconteça um erro desconhecido.
     * @author Rafael Castilhos
     * @see "https://admin.tracebox.com.br/docs/#/Localidade%20de%20armazenagem/postGateway"
     * @since 0.1
     */
    @Override
    public Response<?> execute(APIGatewayProxyRequestEvent request, Context context, String locale)
            throws Unauthorized, NotFound, Conflict, InvalidRequest, IOException {
        if (JWTHelper.isValidJwt(JWTHelper.getToken(request))) {
            return postImageFacade.facade(
                    request,
                    context,
                    JWTHelper.decodeRequestId(JWTHelper.getToken(request)),
                    locale);
        } else
            throw new Unauthorized(Bundle.getInstance().getString("Unauthorized", locale));
    }
}
