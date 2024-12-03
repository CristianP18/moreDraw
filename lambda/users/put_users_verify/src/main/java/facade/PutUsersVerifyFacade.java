package facade;

import bundles.Bundle;
import com.google.firebase.messaging.FirebaseMessagingException;
import configuration.Response;
import configuration.ResponseStatusCode;
import configuration.ResponseType;
import exceptions.NotFound;
import helper.GenerateCodeHelper;
import helper.GsonHelper;
import model.User;
import model.UsersVerifyRequestModel;
import service.UserService;

import java.io.IOException;

public class PutUsersVerifyFacade {
    public Response<?> facade(String input, String locale)
            throws NotFound, IOException, FirebaseMessagingException {
        putUserVerify(input, locale);
        return UserService.getInstance().response(null,
                Bundle.getInstance().getString("PutUsersVerify", locale),
                ResponseType.SUCCESS, ResponseStatusCode.OK);
    }

    /**
     * Solicita novo envio de código verificador OTP por SMS.
     * Gerado um novo código e enviado para o número de telefone celular.
     *
     * @param input {@link UsersVerifyRequestModel} Corpo da requisição
     */
    private void putUserVerify(String input, String locale)
            throws NotFound, IOException, FirebaseMessagingException {
        UsersVerifyRequestModel request =
                GsonHelper.getInstance().getGson().fromJson(input, UsersVerifyRequestModel.class);
        User user = UserService.getInstance().getVerify(request, locale);
        user.setOneTimePassword(GenerateCodeHelper.generateCode(6));

        String title = "Código de verificação";
        String body = "O código OTP é: " + user.getOneTimePassword();
//        String fcm = user.getFcm();

        UserService.getInstance().putUserVerify(user, locale);
    }
}
