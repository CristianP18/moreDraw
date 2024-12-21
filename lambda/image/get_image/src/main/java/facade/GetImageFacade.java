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
        Iterator<Page<Image>> imageList = null;
        if(imageId.length() > 5){
           Image image = ImageService.getInstance().getImageById(imageId, locale);
            return ImageService.getInstance().response(image,
                    Bundle.getInstance().getString("Image", locale),
                    ResponseType.SUCCESS, ResponseStatusCode.OK);
        }else{
//            imageList = ImageService.getInstance().getImage(userId, locale);

            return ImageService.getInstance().responseList(imageList,
                    Bundle.getInstance().getString("ListImage", locale),
                    ResponseType.SUCCESS, ResponseStatusCode.OK);
        }
    }
}