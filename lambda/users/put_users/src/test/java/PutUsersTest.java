import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.tests.annotations.Event;
import helper.GsonHelper;
import model.PutUsersRequestModel;
import model.User;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.params.ParameterizedTest;
import java.util.Arrays;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.nullValue;

public class PutUsersTest {
    @ParameterizedTest
    @Event(value="success.json", type = APIGatewayProxyRequestEvent.class)
    public void testSuccess(APIGatewayProxyRequestEvent event) {
        try {
            PutUsersRequestModel request =
                    GsonHelper.getInstance().getGson().fromJson(event.getBody(), PutUsersRequestModel.class);

            User user = new User();
            User userUpdate = User.update(request, user);
            MatcherAssert.assertThat(userUpdate.getPhone(), notNullValue());
            MatcherAssert.assertThat(userUpdate.getNotify(), notNullValue());
            MatcherAssert.assertThat(userUpdate.getDisability(), notNullValue());

        }
        catch (Exception e){
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    @ParameterizedTest
    @Event(value="fail.json", type = APIGatewayProxyRequestEvent.class)
    public void testFail(APIGatewayProxyRequestEvent event) {
        try {
            PutUsersRequestModel request =
                    GsonHelper.getInstance().getGson().fromJson(event.getBody(), PutUsersRequestModel.class);

            User user = new User();
            User userUpdate = User.update(request, user);
            MatcherAssert.assertThat(userUpdate.getFirstName(), nullValue());
            MatcherAssert.assertThat(userUpdate.getLastName(), nullValue());
            MatcherAssert.assertThat(userUpdate.getPhone(), nullValue());
            MatcherAssert.assertThat(userUpdate.getNotify(), nullValue());
            MatcherAssert.assertThat(userUpdate.getDisability(), nullValue());

        }
        catch (Exception ignored){
        }
    }
}
