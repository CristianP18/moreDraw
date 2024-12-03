package facade;


import bundles.Bundle;
import configuration.Response;
import configuration.ResponseStatusCode;
import configuration.ResponseType;
import exceptions.NotFound;
import model.Image;
import service.ImageService;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

import java.util.Iterator;

public class GetImageFacade {

    public Response<?> facade(String imageId, String userId, String locale) throws NotFound {
        Iterator<Page<Image>> image = ImageService.getInstance().getImage(imageId, locale);

        return ImageService.getInstance().responseList(image,
                Bundle.getInstance().getString("ListImage", locale),
                ResponseType.SUCCESS, ResponseStatusCode.OK);
    }
}