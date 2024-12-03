package service;

import bundles.Bundle;
import configuration.Response;
import exceptions.NotFound;
import model.Image;
import model.ImageResponseModel;
import model.ListImageResponseModel;
import repository.ImageRepository;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

import java.util.ArrayList;
import java.util.Iterator;

public final class ImageService extends Service {
    private static ImageService INSTANCE;

    public static ImageService getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ImageService();
        return INSTANCE;
    }

    public static void postImage(Image image) {
        ImageRepository.getInstance().postImage(image);
    }


    public Iterator<Page<Image>> getImage(String createdBy, String locale) throws NotFound {
        Iterator<Page<Image>> storloc = ImageRepository.getInstance().getImage(createdBy);
        if (storloc == null)
            throw new NotFound(Bundle.getInstance().getString("StorlocNotFound", locale));

        return storloc;
    }

    public Response<?> responseList(Iterator<Page<Image>> images, String text, String type, int status) {
        MESSAGE.setStatus(status);
        MESSAGE.setType(type);
        MESSAGE.setText(text);

        Page<Image> storlocPage = images.next();

        ArrayList<ImageResponseModel> imageResponseModelArray = new ArrayList<>();
        for (Image image : storlocPage.items()) {
            ImageResponseModel imageResponseModel = new ImageResponseModel(image);
            imageResponseModelArray.add(imageResponseModel);
        }

        String next;
        if (storlocPage.lastEvaluatedKey() != null) {
            String createdBy = storlocPage.lastEvaluatedKey().get("createdBy").toString().substring(17, 53);
            next = createdBy.concat(",");

            if (storlocPage.lastEvaluatedKey().get("created") != null) {
                String created = storlocPage.lastEvaluatedKey().get("created").toString().substring(17, 41);
                next = next.concat(created);
            }
        } else
            next = null;

        ListImageResponseModel content = new ListImageResponseModel(imageResponseModelArray, next);
        return Response.success(content, MESSAGE);
    }

    public Response<?> response(Image image, String text, String type, int status) {
        MESSAGE.setStatus(status);
        MESSAGE.setType(type);
        MESSAGE.setText(text);
        if (image != null) {
            ImageResponseModel imageResponseModel = new ImageResponseModel(image);

            // Criar o modelo de resposta contendo o modelo de resposta do image e o próximo token
            ImageResponseModel content = new ImageResponseModel(image);
            return Response.success(content, MESSAGE);
        }
        // Caso não haja images, retorne uma resposta indicando que não foi encontrado
        return Response.error(MESSAGE);
    }


    public Image getImageById(String imageId, String locale) throws NotFound {
        Image image =  ImageRepository.getInstance().getImageById(imageId);

        if(image == null)
            throw new NotFound(Bundle.getInstance().getString("ImageNotFound", locale));
        return image;
    }

    public void saveImage(Image image) {
        ImageRepository.getInstance().saveImage(image);
    }
}