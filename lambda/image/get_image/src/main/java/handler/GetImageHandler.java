package handler;


import bundles.Bundle;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import configuration.DynamoDBImage;
import configuration.GenericRequestHandler;
import configuration.Response;
import exceptions.InvalidRequest;
import exceptions.NotFound;
import exceptions.Unauthorized;
import helper.JWTHelper;
import helper.JwtApiKeyHelper;
import service.RoleService;
import facade.GetImageFacade;
public class GetImageHandler extends GenericRequestHandler {
    private final GetImageFacade getImageFacade = new GetImageFacade();
    /**
     * <h3>Captura Eventos de pátio.</h3>
     *
     * @param request <ul>
     *                <li>Api Key no cabeçalho.</li>
     *                </ul>
     * @return {@link model.ImageResponseModel} modelo de resposta.
     * @throws InvalidRequest       caso corpo possuir equívocos, atributos inválidos.
     * @throws Unauthorized         caso Api Key seja nula.
     * @throws Exception            caso aconteça um erro desconhecido.
     * @author Cristian Pegoraro
     * @since 0.1
     */
    @Override
    public Response<?> execute(APIGatewayProxyRequestEvent request, Context context, String locale)
            throws Unauthorized, NotFound {
        if (JWTHelper.isValidJwt(JWTHelper.getToken(request))) {
            return getImageFacade.facade(
                    request.getPathParameters().get(DynamoDBImage.IMAGEID),
                    JWTHelper.decodeRequestId(JWTHelper.getToken(request)),
                    locale);
        }
        else
            throw new Unauthorized(Bundle.getInstance().getString("Unauthorized", locale));
    }
}
