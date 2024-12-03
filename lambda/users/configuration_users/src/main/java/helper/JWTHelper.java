package helper;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import configuration.DynamoDBUser;
import model.User;
import java.util.Date;

public final class JWTHelper {
    private static JWTHelper INSTANCE;

    public static JWTHelper getInstance() {
        if(INSTANCE == null)
            INSTANCE = new JWTHelper();
        return INSTANCE;
    }

    private static final Algorithm algorithm = Algorithm.HMAC256("@-myJWT_mor3Dr4w_-@-_m0r3Sh3f31-#");
    private static final String ISSUER = "moreDraw";
    private static final Algorithm relation = Algorithm.HMAC256(ISSUER);
    private static final Integer TEN_HOURS = 600 * 60 * 1000;
    private static final Integer SIXTEN_MINUTES = 60 * 60 * 1000;


    public static String createJwtUser(User user) {
        if (user == null || user.getUserId() == null || user.getFederalTax() == null) {
            throw new IllegalArgumentException("Usuário inválido para gerar JWT.");
        }

        // Gera o token JWT com userId e federalTax como claims
        return JWT.create()
                .withIssuer(ISSUER)  // Define o emissor do token
                .withClaim(DynamoDBUser.USER_ID, user.getUserId())  // Adiciona o userId como claim
                .withClaim(DynamoDBUser.FEDERAL_TAX, user.getFederalTax())  // Adiciona o federalTax como claim
                .withIssuedAt(new Date())  // Define a data de emissão
                .withExpiresAt(new Date(System.currentTimeMillis() + TEN_HOURS))  // Define a data de expiração
                .sign(algorithm);  // Assina o token com o algoritmo configurado
    }

    public static Boolean isValidJwt(String jwt) {
        if (jwt == null || jwt.isEmpty()) {
            return false;
        }

        try {
            // Configura o verificador JWT
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)  // Garante que o emissor corresponda
                    .build();

            // Decodifica o token para verificar sua expiração
            Date expiresAt = JWT.decode(jwt).getExpiresAt();
            Date now = new Date();

            // Verifica o token e a validade de sua expiração
            verifier.verify(jwt);  // Valida assinatura, emissor e formato do token
            return now.before(expiresAt);  // Confirma que o token ainda não expirou
        } catch (JWTVerificationException e) {
            // Captura qualquer erro na verificação do token
            return false;
        }
    }


    public static String getToken(APIGatewayProxyRequestEvent request){
        String keyUpper = "Authorization";
        String keyDown = "authorization";

        if(request.getHeaders().containsKey(keyUpper)){
            if(request.getHeaders().get(keyUpper) != null &&
                    request.getHeaders().get(keyUpper).length() > 7){
                return request.getHeaders().get(keyUpper).substring(7);
            }

            else
                return null;
        }

        else if(request.getHeaders().containsKey(keyDown)){
            if(request.getHeaders().get(keyDown) != null &&
                    request.getHeaders().get(keyDown).length() > 7){
                return request.getHeaders().get(keyDown).substring(7);
            }

            else
                return null;
        }

        else
            return null;
    }

    public static String decodeRequestId(String jwt){
        if(jwt != null)
            return JWT.decode(jwt).getClaim(DynamoDBUser.USER_ID).asString();

        else
            return null;
    }

    public static String decodeOperatorType(String jwt){
        return JWT.decode(jwt).getClaim(DynamoDBUser.OPERATOR_TYPE).asString();
    }
}
