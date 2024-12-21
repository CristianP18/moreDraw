package facade;


import bundles.Bundle;
import configuration.Response;
import configuration.ResponseStatusCode;
import configuration.ResponseType;
import exceptions.NotFound;
import helper.GsonHelper;
import model.Image;
import model.ImageRequestModel;
import service.ImageService;

public class PutImageFacade {
    /**
     * Atualiza localidade de armazenagem.'

     * @param imageId Ean da localidade de armazenagem
     */
    public Response<?> facade(String imageId, String input, String locale) throws NotFound {
        Image image = ImageService.getInstance().getImageById(imageId, locale);

        ImageRequestModel request =
                GsonHelper.getInstance().getGson().fromJson(input, ImageRequestModel.class);

        Image.update(request, image);
        ImageService.getInstance().saveImage(image);

        return ImageService.getInstance().response(image, Bundle.getInstance().getString("PutImage", locale),
                ResponseType.SUCCESS, ResponseStatusCode.OK);
    }
}
