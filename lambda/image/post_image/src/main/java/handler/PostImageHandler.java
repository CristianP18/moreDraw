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
import helper.JWTHelper;
import helper.JwtApiKeyHelper;
import service.RoleService;
import facade.PostImageFacade;

public class PostImageHandler  extends GenericRequestHandler {
    private final PostImageFacade postImageFacade = new PostImageFacade();
    /**
     * <h3>Cadastra Eventos de pátio.</h3>
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
     * @author Cristian Pegoraro
     * @since 0.1
     */
    @Override
    public Response<?> execute(APIGatewayProxyRequestEvent request, Context context, String locale)
            throws Unauthorized, NotFound, Conflict {
        if (JWTHelper.isValidJwt(JWTHelper.getToken(request))) {
            return postImageFacade.facade(
                    JWTHelper.decodeRequestId(JWTHelper.getToken(request)),
                    request.getBody(),
                    locale);
        }
        else
            throw new Unauthorized(Bundle.getInstance().getString("Unauthorized", locale));
    }
}
