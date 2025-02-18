package facade;

import bundles.Bundle;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import configuration.Response;
import configuration.ResponseStatusCode;
import configuration.ResponseType;
import exceptions.Conflict;
import exceptions.InvalidRequest;
import exceptions.NotFound;
import helper.GsonHelper;
import model.Image;
import model.ImageRequestModel;
import org.apache.commons.fileupload.MultipartStream;
import repository.S3;
import service.DocService;
import service.ImageService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

public class PostImageFacade {

    String bucket = "moredraw/pictures";

    public Response<?> facade(APIGatewayProxyRequestEvent event, Context context, String userId, String locale) throws NotFound, Conflict, InvalidRequest, IOException {
        Image image = postImage(event, context, userId, locale);
        return ImageService.getInstance().response(image,
                Bundle.getInstance().getString("PostImage", locale),
                ResponseType.SUCCESS, ResponseStatusCode.CREATED);
    }

    private Image postImage(APIGatewayProxyRequestEvent event, Context context, String clientId, String locale)
            throws NotFound, Conflict, InvalidRequest, IOException {
        LambdaLogger logger = context.getLogger();
        String contentType = event.getHeaders().getOrDefault("Content-Type", event.getHeaders().getOrDefault("content-type", null));

        if (contentType == null || !contentType.startsWith("multipart/form-data")) {
            throw new InvalidRequest("O tipo de conteúdo da requisição não é multipart/form-data.");
        }

        String[] boundaryArray = contentType.split("=");
        if (boundaryArray.length < 2) {
            throw new InvalidRequest("Boundary não encontrado no Content-Type.");
        }
        byte[] boundaryBytes = boundaryArray[1].getBytes();

        byte[] body;
        try {
            body = Base64.getDecoder().decode(event.getBody());
        } catch (IllegalArgumentException e) {
            throw new InvalidRequest("Falha ao decodificar Base64: " + e.getMessage());
        }

        ByteArrayInputStream content = new ByteArrayInputStream(body);
        MultipartStream multipartStream = new MultipartStream(content, boundaryBytes, body.length, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String jsonMetadata = null;
        ByteArrayOutputStream imageOut = new ByteArrayOutputStream();

        boolean nextPart = multipartStream.skipPreamble();
        while (nextPart) {
            String headers = multipartStream.readHeaders();
            logger.log("Headers: " + headers);
            out.reset();
            multipartStream.readBodyData(out);

            if (headers.contains("filename=")) {
                imageOut.write(out.toByteArray());
            } else if (headers.contains("name=\"jsonImage\"")) {
                jsonMetadata = new String(out.toByteArray());
            }
            nextPart = multipartStream.readBoundary();
        }

        if (jsonMetadata == null || jsonMetadata.isEmpty()) {
            throw new InvalidRequest("Metadados (JSON) não encontrados na requisição.");
        }
        if (imageOut.size() == 0) {
            throw new InvalidRequest("Arquivo de imagem não encontrado na requisição.");
        }

        ImageRequestModel request = GsonHelper.getInstance().getGson().fromJson(jsonMetadata, ImageRequestModel.class);
        Image image = new Image(request, clientId);
        String imageUrl = postPicture(event, context, locale);
        image.setImageUrl(imageUrl);

        ImageService.postImage(image);
        return image;
    }

    private String postPicture(APIGatewayProxyRequestEvent event, Context context, String locale) throws IOException, InvalidRequest {
        String key = UUID.randomUUID() + ".jpg";
        DocService.getInstance().uploadS3(bucket, key, event, context, locale);
        return S3.link(bucket, key);
    }
}
