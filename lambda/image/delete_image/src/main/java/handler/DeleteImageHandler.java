package handler;

import bundles.Bundle;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import configuration.DynamoDBImage;
import configuration.GenericRequestHandler;
import configuration.Response;
import exceptions.NotFound;
import exceptions.Unauthorized;
import facade.DeleteImageFacade;

public class DeleteImageHandler extends GenericRequestHandler {
    private final DeleteImageFacade deleteImageFacade = new DeleteImageFacade();

    /**
     * @param request
     * <ul>
     * <li>EAN da localidade de armazenagem no caminho.</li>
     * <li>JWT Token ou Api Key no cabeçalho.</li>
     * </ul>
     * @return {@link model.ImageResponseModel} modelo de resposta.
     * @throws Unauthorized caso Api Key ou Token JWT seja nulo ou expirado.
     * @throws NotFound caso unitizador não seja encontrado.
     * @throws Exception caso aconteça um erro desconhecido.
     * @since 0.1
     * @see "https://admin.tracebox.com.br/docs/#/Localidade%20de%20armazenagem/putImage"
     * @author Rafael Castilhos
     */
    @Override
    public Response<?> execute(APIGatewayProxyRequestEvent request, Context context, String locale)
            throws Unauthorized, NotFound {

            return deleteImageFacade.facade(
                    request.getPathParameters().get(DynamoDBImage.IMAGEID),
                    locale);
        }
}