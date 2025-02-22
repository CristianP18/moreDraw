package service;

import bundles.Bundle;
import configuration.Response;
import exceptions.NotFound;
import model.*;
import repository.ImageRepository;
import repository.S3;
import repository.UserRepository;
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

    public Response<?> response(User user, String text, String type, int status) {
        MESSAGE.setStatus(status);
        MESSAGE.setType(type);
        MESSAGE.setText(text);

        if (user != null) {
            UserResponseModel content = new UserResponseModel(user);
            return Response.success(content, MESSAGE);
        }

        return Response.success(MESSAGE);
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

        Page<Image> storlocPage = images.hasNext() ? images.next() : new Page<>(Collections.emptyList(), null);

        ArrayList<ImageResponseModel> imageResponseModelArray = new ArrayList<>();
        if (storlocPage.items() != null) {
            for (Image image : storlocPage.items()) {
                ImageResponseModel imageResponseModel = new ImageResponseModel(image);
                imageResponseModelArray.add(imageResponseModel);
            }
        }

        String next = null;
        if (storlocPage.lastEvaluatedKey() != null) {
            Map<String, AttributeValue> lastKey = storlocPage.lastEvaluatedKey();

            if (lastKey.containsKey("createdBy")) {
                String createdBy = lastKey.get("createdBy").toString();
                createdBy = createdBy.length() > 53 ? createdBy.substring(17, 53) : createdBy;
                next = createdBy.concat(",");
            }

            if (lastKey.containsKey("created")) {
                String created = lastKey.get("created").toString();
                created = created.length() > 41 ? created.substring(17, 41) : created;
                next = next != null ? next.concat(created) : created;
            }
        }

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

    public static final String image_GRAY =
            S3.link("docs.tracebox.com.br/imageyard", "imageyard_gray.jpg");

    public void saveImage(Image image) {
        ImageRepository.getInstance().saveImage(image);
    }

    public void deleteImage(Image image) {
        ImageRepository.getInstance().deleteImage(image);
    }

    public String getFilenameFromAvatar(String oldPhoto){
        // extrai nome da foto dentro da URL
        return oldPhoto.substring(56);
    }
}