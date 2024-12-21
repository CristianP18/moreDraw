package facade;


import bundles.Bundle;
import configuration.Response;
import configuration.ResponseStatusCode;
import configuration.ResponseType;
import exceptions.NotFound;
import model.Image;
import service.DocService;
import service.ImageService;

public class DeleteImageFacade {

    String bucket = "docs.tracebox.com.br/imageyard";

    /**
     * Atualiza localidade de armazenagem.'

     * @param imageId Ean da localidade de armazenagem
     */
    public Response<?> facade(String imageId, String locale) throws NotFound {
        Image image = ImageService.getInstance().getImageById(imageId, locale);
        String oldPhoto = null;
        if(image.getImageUrl() != null) {
            oldPhoto = ImageService.getInstance().getFilenameFromAvatar(image.getImageUrl());
        }
        System.out.println("Name picture image: " + oldPhoto);
        if(oldPhoto != null && !oldPhoto.equals("mapyard_gray.jpg")) {
            DocService.getInstance().removeLastAvatarFromS3(bucket, oldPhoto);
        }
        ImageService.getInstance().deleteImage(image);

        return ImageService.getInstance().response(image, Bundle.getInstance().getString("DeleteImage", locale),
                ResponseType.SUCCESS, ResponseStatusCode.OK);
    }
}
