package facade;

import bundles.Bundle;
import configuration.Response;
import configuration.ResponseStatusCode;
import configuration.ResponseType;
import exceptions.Forbidden;
import exceptions.NotFound;
import exceptions.Unauthorized;
import helper.GsonHelper;
import helper.JWTHelper;
import model.PostUsersLoginRequestModel;
import model.User;
import service.UserService;
import org.mindrot.jbcrypt.BCrypt;

import static helper.JWTHelper.*;

public class PostUsersLoginFacade {
    /**
     * Login do usuário.
     * Em usuários web, é informado também o CNPJ do cliente em que deseja fazer login.
     * Em usuários web, é informado também os detalhes da plataforma em que realizou login.
     * Em usuários mobile, é informado também o fcm para identificar o dispositivo para envio de notificações.
     *
     * @param input {@link PostUsersLoginRequestModel} Corpo da requisição
     * @param locale Idioma e região para mensagens de erro e validação
     * @return {@link Response} Resposta com informações do usuário logado
     * @throws Unauthorized Se a senha for inválida.
     * @throws NotFound Se o usuário não for encontrado.
     */
    public Response<?> facade(String input, String locale)
            throws Unauthorized, NotFound, Forbidden {
        // Converter o JSON para o modelo de requisição
        PostUsersLoginRequestModel request =
                GsonHelper.getInstance().getGson().fromJson(input, PostUsersLoginRequestModel.class);

        // Buscar o usuário pelo CPF (federalTax)
        User user = UserService.getInstance().getUserFederalTax(request.getFederalTax());

        // Verificar se o usuário foi encontrado
        if (user == null) {
            System.out.println("USUÁRIO NÃO ENCONTRADO");
            throw new NotFound(Bundle.getInstance().getString("UserNotFound", locale));
        }

        // Verificar se a senha é válida
        boolean isValidPassword = isPasswordCorrect(request.getPassword(), user.getPassword());
        if (!isValidPassword) {
            System.out.println("TELEFONE OU SENHA INVÁLIDOS");
            throw new Unauthorized(Bundle.getInstance().getString("InvalidLoginUser", locale));
        }
        System.out.println("LOGIN REALIZADO COM SUCESSO");
        String jwt = JWTHelper.createJwtUser(user);


        // Retornar o usuário como resposta de sucesso
        return UserService.getInstance().responseLogin(
                user, jwt,
                Bundle.getInstance().getString("ValidLogin", locale),
                ResponseType.SUCCESS,
                ResponseStatusCode.CREATED
        );
    }

    /**
     * Verifica se a senha informada é válida.
     *
     * @param input Senha enviada.
     * @param password Senha cadastrada.
     * @return {@code true} se a senha for válida; caso contrário, {@code false}.
     */
    private Boolean isPasswordCorrect(String input, String password) {
        return BCrypt.checkpw(input, password);
    }
}
