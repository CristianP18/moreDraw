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
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PostImageFacade {

    // Ajuste o nome do seu bucket se necessário
    String bucket = "moredraw/pictures";

    public Response<?> facade(APIGatewayProxyRequestEvent event, Context context, String userId, String locale)
            throws NotFound, Conflict, InvalidRequest, IOException {

        Image image = postImage(event, context, userId, locale);

        return ImageService.getInstance().response(
                image,
                Bundle.getInstance().getString("PostImage", locale),
                ResponseType.SUCCESS,
                ResponseStatusCode.CREATED
        );
    }

    private Image postImage(APIGatewayProxyRequestEvent event, Context context, String clientId, String locale)
            throws NotFound, Conflict, InvalidRequest, IOException {

        LambdaLogger logger = context.getLogger();

        // 1) Verifica se é multipart/form-data
        String contentType = event.getHeaders().getOrDefault("Content-Type",
                event.getHeaders().getOrDefault("content-type", null));
        if (contentType == null || !contentType.startsWith("multipart/form-data")) {
            throw new InvalidRequest("O tipo de conteúdo da requisição não é multipart/form-data.");
        }

        // 2) Extrai o boundary do header
        String[] boundaryArray = contentType.split("=");
        if (boundaryArray.length < 2) {
            throw new InvalidRequest("Boundary não encontrado no Content-Type.");
        }
        byte[] boundaryBytes = boundaryArray[1].getBytes(StandardCharsets.UTF_8);

        // 3) Converte o corpo em bytes SEM decodificar em Base64
        //    (o front está enviando multipart puro, então não há Base64).
        //    Caso o API Gateway envie em Base64 (com isBase64Encoded=true), adapte aqui se necessário.
        String bodyString = event.getBody() != null ? event.getBody() : "";
        byte[] body = bodyString.getBytes(StandardCharsets.UTF_8);

        // 4) Configura o MultipartStream
        ByteArrayInputStream content = new ByteArrayInputStream(body);
        MultipartStream multipartStream = new MultipartStream(
                content,
                boundaryBytes,
                body.length,
                null
        );

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String jsonMetadata = null;
        ByteArrayOutputStream imageOut = new ByteArrayOutputStream();

        // 5) Lê cada parte do multipart
        boolean nextPart = multipartStream.skipPreamble();
        while (nextPart) {
            String headers = multipartStream.readHeaders();
            logger.log("Headers: " + headers);

            out.reset();
            multipartStream.readBodyData(out);

            // Se for a parte do arquivo (filename=...), grava no imageOut
            if (headers.contains("filename=")) {
                imageOut.write(out.toByteArray());
            }
            // Se for a parte do JSON (name="jsonImageYard"), converte em String
            else if (headers.contains("name=\"jsonImageYard\"")) {
                jsonMetadata = new String(out.toByteArray(), StandardCharsets.UTF_8);
            }

            nextPart = multipartStream.readBoundary();
        }

        // 6) Valida se temos o JSON e o arquivo
        if (jsonMetadata == null || jsonMetadata.isEmpty()) {
            throw new InvalidRequest("Metadados (JSON) não encontrados na requisição.");
        }
        if (imageOut.size() == 0) {
            throw new InvalidRequest("Arquivo de imagem não encontrado na requisição.");
        }

        // 7) Converte o JSON em objeto
        ImageRequestModel request = GsonHelper.getInstance().getGson().fromJson(jsonMetadata, ImageRequestModel.class);

        // 8) Cria o objeto Image
        Image image = new Image(request, clientId);

        // Exemplo de upload S3 (pode ser ajustado conforme sua lógica)
        String imageUrl = postPicture(event, context, locale);
        image.setImageUrl(imageUrl);

        // 9) Salva no banco (ou em outro lugar)
        ImageService.postImage(image);

        return image;
    }

    private String postPicture(APIGatewayProxyRequestEvent event, Context context, String locale)
            throws IOException, InvalidRequest {

        // Gera um nome único para o arquivo
        String key = UUID.randomUUID() + ".jpg";

        // Sobrescreva este método ou a lógica interna para usar os bytes do arquivo
        // se necessário. Aqui, supostamente, o DocService.getInstance().uploadS3()
        // já consegue extrair do event algo ou do body. Ajuste conforme necessidade.

        DocService.getInstance().uploadS3(bucket, key, event, context, locale);
        return S3.link(bucket, key);
    }
}
