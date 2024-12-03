package facade;

import bundles.Bundle;
import configuration.Response;
import configuration.ResponseStatusCode;
import configuration.ResponseType;
import exceptions.InvalidOneTimePassword;
import exceptions.NotFound;
import helper.GsonHelper;
import model.User;
import model.UsersVerifyRequestModel;
import service.UserService;

public class PostUsersVerifyFacade {
    public Response<?> facade(String input, String locale)
            throws NotFound, InvalidOneTimePassword {
        if(postUserVerify(input, locale))
            return UserService.getInstance().response(null,
                    Bundle.getInstance().getString("PostUserVerify", locale),
                    ResponseType.SUCCESS, ResponseStatusCode.OK);

        throw new InvalidOneTimePassword(Bundle.getInstance().getString("InvalidOneTimePassword", locale));
    }

    /**
     * Autentica dispositivo celular com o código OTP enviado por SMS no cadastro.
     * Inscreve número de telefone celular em tópico no SNS.
     *
     * @param input {@link UsersVerifyRequestModel} Corpo da requisição.
     */
    private Boolean postUserVerify(String input, String locale) throws NotFound {
        UsersVerifyRequestModel request =
                GsonHelper.getInstance().getGson().fromJson(input, UsersVerifyRequestModel.class);
        User user = UserService.getInstance().getVerify(request, locale);

        if(request.getOneTimePassword().equals(user.getOneTimePassword())){
            user.setOneTimePassword(null);
            user.setVerified(true);
            UserService.getInstance().subscribe(user.getMobilePhone());
            UserService.getInstance().putUser(user);
            return true;
        }
        return false;
    }
}
