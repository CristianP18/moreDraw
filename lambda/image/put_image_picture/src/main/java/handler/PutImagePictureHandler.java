package handler;

import bundles.Bundle;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import configuration.*;
import exceptions.InvalidRequest;
import exceptions.NotFound;
import exceptions.Unauthorized;
import facade.PutImagePictureFacade;
import helper.JWTHelper;
import helper.JwtApiKeyHelper;
import service.RoleService;
import java.io.IOException;

public class PutImagePictureHandler extends GenericRequestHandler {
    private final PutImagePictureFacade putImagePictureFacade = new PutImagePictureFacade();

    /**
     * <h3>Envia foto de avatar usuário.</h3>
     *
     * @param request
     * <ul>
     * <li>Foto de avatar no multipart/form-data.</li>
     * <li>JWT Token ou Api Key no cabeçalho.</li>
     * </ul>
     * @return {@link model.UserResponseModel} modelo de resposta.
     * @throws Unauthorized caso Token JWT seja nulo ou expirado.
     * @throws NotFound caso alguma entidade não seja encontrada.
     * @throws Exception caso aconteça um erro desconhecido.
     * @since 0.1
     * @see "https://admin.moreDraw.com.br/docs/#/Usu%C3%A1rios/postImagePicture"
     * @author Rafael Castilhos
     */
    @Override
    public Response<?> execute(APIGatewayProxyRequestEvent request, Context context, String locale)
            throws Unauthorized, InvalidRequest, NotFound, IOException {

        if (JWTHelper.isValidJwt(JWTHelper.getToken(request))) {
            return putImagePictureFacade.facade(
                    request.getPathParameters().get(DynamoDBImage.IMAGEID),
                    request,
                    context,
                    locale);
        }
        else
            throw new Unauthorized(Bundle.getInstance().getString("Unauthorized", locale));
    }
}
