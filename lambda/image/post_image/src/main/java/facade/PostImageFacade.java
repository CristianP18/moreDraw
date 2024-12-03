package facade;


import bundles.Bundle;
import configuration.Response;
import configuration.ResponseStatusCode;
import configuration.ResponseType;
import exceptions.Conflict;
import exceptions.NotFound;
import helper.GsonHelper;
import model.Image;
import model.ImageRequestModel;
import service.ImageService;

public class PostImageFacade {
    public Response<?> facade(String userId, String input, String locale) throws NotFound, Conflict {
        Image image = postImage(userId, input, locale);
        return ImageService.getInstance().response(image,
                Bundle.getInstance().getString("PostImage", locale),
                ResponseType.SUCCESS, ResponseStatusCode.CREATED);
    }

    private Image postImage(String userId, String input, String locale) throws NotFound, Conflict {
        ImageRequestModel request = GsonHelper.getInstance().getGson().fromJson(input, ImageRequestModel.class);
        Image image = new Image(request,  userId);
        ImageService.postImage(image);

        return image;
    }

}
