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
import service.BucketS3Service;
import service.ImageService;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import javax.imageio.ImageIO;

/**
 * Classe responsável por receber uma requisição multipart/form-data
 * contendo um arquivo de imagem e um JSON (jsonImage).
 * Extrai as duas partes (arquivo + JSON), faz uma validação simples
 * de que o arquivo é uma imagem, salva no S3 e persiste os dados.
 */
public class PostImageFacade {

    private static final String BUCKET_NAME = "moredraw/pictures";

    public Response<?> facade(
            APIGatewayProxyRequestEvent event,
            Context context,
            String clientId,
            String locale
    ) throws NotFound, Conflict, InvalidRequest, IOException {

        Image image = postImage(event, context, clientId, locale);

        return ImageService.getInstance().response(
                image,
                Bundle.getInstance().getString("PostImage", locale),
                ResponseType.SUCCESS,
                ResponseStatusCode.CREATED
        );
    }

    private Image postImage(
            APIGatewayProxyRequestEvent event,
            Context context,
            String clientId,
            String locale
    ) throws NotFound, Conflict, InvalidRequest, IOException {

        LambdaLogger logger = context.getLogger();

        // 1) Verifica se é multipart/form-data
        String contentType = event.getHeaders().getOrDefault(
                "Content-Type",
                event.getHeaders().getOrDefault("content-type", null)
        );
        if (contentType == null || !contentType.startsWith("multipart/form-data")) {
            throw new InvalidRequest("O tipo de conteúdo da requisição não é multipart/form-data.");
        }

        // 2) Extrai o boundary e remove aspas, se houver
        String[] boundaryArray = contentType.split("=");
        if (boundaryArray.length < 2) {
            throw new InvalidRequest("Boundary não encontrado no Content-Type.");
        }
        String boundary = boundaryArray[1].trim();
        if (boundary.startsWith("\"") && boundary.endsWith("\"")) {
            boundary = boundary.substring(1, boundary.length() - 1);
        }
        byte[] boundaryBytes = boundary.getBytes(StandardCharsets.UTF_8);

        // 3) Obter o body de acordo com isBase64Encoded
        byte[] body;
        if (Boolean.TRUE.equals(event.getIsBase64Encoded())) {
            logger.log("Requisição com isBase64Encoded=true");
            body = Base64.getDecoder().decode(event.getBody());
        } else {
            logger.log("Requisição com isBase64Encoded=false");
            String bodyString = event.getBody() != null ? event.getBody() : "";
            body = bodyString.getBytes(StandardCharsets.UTF_8);
        }

        // 4) Usa MultipartStream para separar as partes do multipart
        ByteArrayInputStream input = new ByteArrayInputStream(body);
        MultipartStream multipartStream = new MultipartStream(
                input,
                boundaryBytes,
                body.length,
                null
        );

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream imageOut = new ByteArrayOutputStream();
        String jsonMetadata = null;

        // 5) Itera nas partes
        boolean nextPart = multipartStream.skipPreamble();
        while (nextPart) {
            String headers = multipartStream.readHeaders();
            logger.log("Headers: " + headers);

            out.reset();
            multipartStream.readBodyData(out);
            byte[] partData = out.toByteArray();

            // Se essa parte for o arquivo (contém filename=)
            if (headers.contains("filename=")) {
                imageOut.write(partData);
            }
            // Se for o JSON (procura por name="jsonImage")
            else if (headers.contains("name=\"jsonImage\"") || headers.contains("name=\"jsonImageYard\"")) {
                jsonMetadata = new String(partData, StandardCharsets.UTF_8);
            }

            nextPart = multipartStream.readBoundary();
        }

        // 6) Valida se pegamos JSON e arquivo
        if (jsonMetadata == null || jsonMetadata.isEmpty()) {
            throw new InvalidRequest("Metadados (JSON) não encontrados na requisição.");
        }
        if (imageOut.size() == 0) {
            throw new InvalidRequest("Arquivo de imagem não encontrado na requisição.");
        }

        // 7) Verifica se é realmente uma imagem (usando ImageIO)
        if (!isValidImage(imageOut.toByteArray())) {
            throw new InvalidRequest("O arquivo enviado não é uma imagem válida ou está corrompido.");
        }

        // 8) Converte JSON -> objeto ImageRequestModel
        ImageRequestModel request = GsonHelper.getInstance()
                .getGson()
                .fromJson(jsonMetadata, ImageRequestModel.class);

        // 9) Cria objeto Image
        Image image = new Image(request, clientId);

        // 10) Faz upload no S3
        String imageUrl = uploadToS3(imageOut.toByteArray(), context);
        image.setImageUrl(imageUrl);

        // 11) Persiste no serviço (banco)
        ImageService.postImage(image);

        return image;
    }

    /**
     * Tenta ler os bytes da imagem usando ImageIO.
     * Retorna true se conseguir decodificar a imagem (não for null).
     */
    private boolean isValidImage(byte[] imageBytes) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
            BufferedImage bufferedImage = ImageIO.read(bais);
            return bufferedImage != null;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Faz upload dos bytes do arquivo para o S3 e retorna a URL do arquivo.
     */
    private String uploadToS3(byte[] fileBytes, Context context)
            throws IOException, InvalidRequest {

        // Gera uma chave única para o arquivo
        String key = UUID.randomUUID() + ".jpg";

        // Envia os bytes para o S3 via BucketS3Service
        BucketS3Service.getInstance().uploadS3(
                BUCKET_NAME,
                key,
                fileBytes,
                "application/octet-stream",
                context
        );

        // Retorna a URL do arquivo no S3
        return S3.link(BUCKET_NAME, key);
    }
}
