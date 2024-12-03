package facade;

import bundles.Bundle;
import configuration.Response;
import configuration.ResponseStatusCode;
import configuration.ResponseType;
import exceptions.InvalidRequest;
import exceptions.NotFound;
import helper.GsonHelper;
import model.PostUsersVerifyPasswordModel;
import model.User;
import model.UsersVerifyRequestModel;
import org.mindrot.jbcrypt.BCrypt;
import service.UserService;

public class PostUsersVerifyPasswordFacade {
    public Response<?> facade(String input, String locale) throws NotFound, InvalidRequest {
        postUsersVerifyPassword(input, locale);
        return UserService.getInstance().response(null,
                Bundle.getInstance().getString("UpdatePassword", locale),
                ResponseType.SUCCESS, ResponseStatusCode.OK);
    }

    /**
     * Finaliza processo de recuperação/alteração de senha.
     * Verifica se código OTP é válido.
     * Realiza criptografia na nova senha inserida.
     *
     * @param input {@link UsersVerifyRequestModel} Corpo da requisição
     */
    private void postUsersVerifyPassword(String input, String locale) throws NotFound, InvalidRequest {
        PostUsersVerifyPasswordModel request =
                GsonHelper.getInstance().getGson().fromJson(input, PostUsersVerifyPasswordModel.class);
        User userAlreadyExists = UserService.getInstance().getUserFederalTax(request.getFederalTax());

        if(userAlreadyExists.getUserId() != null){
            User user = UserService.getInstance().getUserById(userAlreadyExists.getUserId(), locale);

            if(user.getOneTimePassword().equals(request.getOneTimePassword())){
                user.setPassword(request.getPassword());

                if(UserService.getInstance().isValidPassword(user, locale)){
                    user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
                    user.setOneTimePassword(null);

                    UserService.getInstance().putUser(user);
                }
            }
            else
                throw new NotFound(Bundle.getInstance().getString("InvalidOneTimePassword", locale));
        }

        else
            throw new NotFound(Bundle.getInstance().getString("UserNotFound", locale));
    }
}
