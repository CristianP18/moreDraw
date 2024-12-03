package facade;

import bundles.Bundle;
import com.google.firebase.messaging.FirebaseMessagingException;
import configuration.Response;
import configuration.ResponseStatusCode;
import configuration.ResponseType;
import exceptions.InvalidAddress;
import exceptions.NotFound;
import helper.GsonHelper;
import model.PostUsersRequestModel;
import model.User;
import org.mindrot.jbcrypt.BCrypt;
import service.UserService;

import java.io.IOException;

public class PostUsersFacade {

    /**
     * Cadastra usuário.
     *
     * @param input  JSON contendo os dados do usuário.
     * @param locale Localidade para mensagens de erro ou retorno.
     * @return O usuário recém-cadastrado.
     * @throws NotFound Se não encontrar recursos necessários para o cadastro.
     */
    public Response<?> facade(String input, String locale) throws InvalidAddress, IOException, FirebaseMessagingException {
        // Converter o JSON de entrada para o modelo
        PostUsersRequestModel request =
                GsonHelper.getInstance().getGson().fromJson(input, PostUsersRequestModel.class);
        User newUser = new User(request);
        String senhaCriptografada = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        newUser.setPassword(senhaCriptografada);
                // Persistir o novo usuário no banco de dados
        UserService.getInstance().saveUser(newUser, locale);

        // Retornar o usuário recém-criado como resposta
        return UserService.getInstance().response(newUser,
                Bundle.getInstance().getString("USER_CREATED_SUCCESSFULLY", locale),
                ResponseType.SUCCESS,
                ResponseStatusCode.CREATED);
    }
}
