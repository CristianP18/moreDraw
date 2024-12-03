package facade;

import bundles.Bundle;
import configuration.Response;
import configuration.ResponseStatusCode;
import configuration.ResponseType;
import exceptions.InvalidAddress;
import exceptions.InvalidRequest;
import exceptions.NotFound;
import helper.GenerateCodeHelper;
import helper.GsonHelper;
import model.*;
import service.UserService;
import java.io.IOException;
import static helper.Common.removeBrackets;

public class PutUsersFacade {
    public Response<?> facade(String userId, String input, String locale)
            throws NotFound, InvalidAddress, IOException, InvalidRequest {
        User user = putUser(userId, input, locale);
        return UserService.getInstance().response(user, Bundle.getInstance().getString("PutUser", locale),
                ResponseType.SUCCESS, ResponseStatusCode.OK);
    }

    /**
     * Atualiza usuário.
     *
     * @param userId Identificador do usuário.
     * @param input {@link PutUsersRequestModel} Corpo da requisição.

     * @return {@link User}
     */
    private User putUser(String userId, String input, String locale)
            throws NotFound, InvalidAddress, IOException, InvalidRequest {
        User user = UserService.getInstance().getUserById(userId, locale);

        PutUsersRequestModel request =
                GsonHelper.getInstance().getGson().fromJson(input, PutUsersRequestModel.class);

        return updateUser(user, request, locale);
    }

    /**
     * Verifica atualização de número de telefone.
     * Verifica atualização de endereço.
     *
     * @param user Instância do usuário.
     * @param request {@link PutUsersRequestModel} Corpo da requisição.
     * @return {@link User}
     */
    private User updateUser(User user, PutUsersRequestModel request, String locale)
            throws InvalidAddress, IOException, InvalidRequest {

        if(request.getMobilePhone() != null){
            String newMobilePhone = removeBrackets(request.getMobilePhone());
            if(!newMobilePhone.equals(user.getMobilePhone()) && UserService.isValidPhone(newMobilePhone)){
//                UserService.getInstance().unsubscribe(user.getMobilePhone());
                user.setMobilePhone(newMobilePhone);
                user.setOneTimePassword(GenerateCodeHelper.generateCode(6));
//                UserService.getInstance().resendSMS(user, locale);
            }
            else
                throw new InvalidRequest(Bundle.getInstance().getString("InvalidMobilePhone", locale));
        }

        User userUp = User.update(request, user);
        UserService.getInstance().putUser(userUp);
        return userUp;
    }
}
