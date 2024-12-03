package facade;

import bundles.Bundle;
import configuration.Response;
import configuration.ResponseStatusCode;
import configuration.ResponseType;
import exceptions.NotFound;
import model.User;
import service.UserService;

public class DeleteUsersFacade {
    public Response<?> facade(String userId, String locale) throws NotFound {
        User user = UserService.getInstance().getUserById(userId, locale);
//        user.setDisabled(true);
        UserService.getInstance().putUser(user);

        return UserService.getInstance().response(null,
                Bundle.getInstance().getString("DeleteUser", locale), ResponseType.SUCCESS, ResponseStatusCode.NO_CONTENT);
    }
}
