package facade;

import bundles.Bundle;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import configuration.Response;
import configuration.ResponseStatusCode;
import configuration.ResponseType;
import exceptions.InvalidRequest;
import exceptions.NotFound;
import model.Image;
import repository.S3;

import service.BucketS3Service;
import service.ImageService;
import java.io.IOException;
import java.util.UUID;

public class PutImagePictureFacade {
    String bucket = "image.moreDraw.com.br/picmapyard";

    /**
     * Atualiza foto do mapa.
     *
     * @param event Corpo e cabeçalho da requisição.
     */
    public Response<?> facade(String imageId, APIGatewayProxyRequestEvent event, Context context,
                           String locale) throws IOException, InvalidRequest, NotFound {
        
            Image image = ImageService.getInstance().getImageById(imageId, locale);
            String oldPhoto = null;
            if(image.getImageUrl() != null) {
                oldPhoto = getFilenameFromAvatar(image.getImageUrl());
            }

            String pic = postPicture(event, context, locale);
            image.setImageUrl(pic);
            ImageService.getInstance().saveImage(image);
        System.out.println("Name picture image: " + oldPhoto);
            if(oldPhoto != null && !oldPhoto.equals("mapyard_gray.jpg")) {
                BucketS3Service.getInstance().removeLastAvatarFromS3(bucket, oldPhoto);
            }

            return ImageService.getInstance().response(image, Bundle.getInstance().getString("PostImagePic", locale),
                    ResponseType.SUCCESS, ResponseStatusCode.OK);

    }

    /**
     * Upload da foto no bucket.
     *
     * @param event Corpo e cabeçalho da requisição.
     */
    private String postPicture(APIGatewayProxyRequestEvent event, Context context, String locale)
            throws IOException, InvalidRequest {
        String key = UUID.randomUUID() + ".jpg";
        BucketS3Service.getInstance().uploadS3(bucket, key, event, context, locale);

        return S3.link(bucket, key);
    }

    public String getFilenameFromAvatar(String oldPhoto){
        // extrai nome da foto dentro da URL
        return oldPhoto.substring(57);
    }
}
