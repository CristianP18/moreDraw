package facade;

import bundles.Bundle;
import configuration.Response;
import configuration.ResponseStatusCode;
import configuration.ResponseType;
import exceptions.NotFound;
import helper.GenerateCodeHelper;
import helper.GsonHelper;
import model.PostUsersForgetPasswordModel;
import model.User;
import service.UserService;

public class PostUsersForgetPasswordFacade {
    public Response<?> facade(String input, String locale)
            throws NotFound {
        postUsersForgetPassword(input, locale);
        return UserService.getInstance().response(null,
                Bundle.getInstance().getString("PostOneTimePassword", locale),
                ResponseType.SUCCESS, ResponseStatusCode.OK);
    }

    /**
     * Inicia processo de recuperação/alteração de senha.
     * Envia código verificador OTP por SMS.
     *
     * @param input {@link PostUsersForgetPasswordModel} Corpo da requisição
     */
    private void postUsersForgetPassword(String input, String locale)
            throws NotFound {
        PostUsersForgetPasswordModel request =
                GsonHelper.getInstance().getGson().fromJson(input, PostUsersForgetPasswordModel.class);
        User userIdAlreadyExists = UserService.getInstance().getUserMobilePhone(request.getMobilePhone());

        if(userIdAlreadyExists != null){
            User user = UserService.getInstance().getUserById(userIdAlreadyExists.getUserId(), locale);

            user.setOneTimePassword(GenerateCodeHelper.generateCode(6));

            UserService.getInstance().putUser(user);
            UserService.getInstance().sendPasswordVerificationSMS(user, locale);
        }

        else
            throw new NotFound(Bundle.getInstance().getString("UserNotFound", locale));
    }
}
