import bundles.Bundle;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.tests.annotations.Event;
import configuration.Response;
import configuration.ResponseStatusCode;
import configuration.ResponseType;
import helper.GsonHelper;
import model.User;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.params.ParameterizedTest;
import service.UserService;
import java.util.Arrays;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class ResponseUserTest {
    @ParameterizedTest
    @Event(value="success.json", type = APIGatewayProxyRequestEvent.class)
    public void testSuccess(APIGatewayProxyRequestEvent event) {
        try {
            User user =
                    GsonHelper.getInstance().getGson().fromJson(event.getBody(), User.class);

            Response<?> response = UserService.getInstance().response(user,
                    Bundle.getInstance().getString("PostUser", "pt_BR"), ResponseType.SUCCESS, ResponseStatusCode.OK);
            MatcherAssert.assertThat(response.getContent(), notNullValue());
            MatcherAssert.assertThat(response.getMessage().getType(), equalTo(ResponseType.SUCCESS));
            MatcherAssert.assertThat(response.getMessage().getStatus(), equalTo(ResponseStatusCode.OK));
        }
        catch (Exception e){
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
