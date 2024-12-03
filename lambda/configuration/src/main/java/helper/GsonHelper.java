package helper;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;

public class GsonHelper {
    private static GsonHelper INSTANCE;
    private final Gson mGson;

    private GsonHelper() {
        mGson = new GsonBuilder()
                .setDateFormat("dd/MM/yyyy HH:mm")
                .setPrettyPrinting()
                .registerTypeAdapter(Double.class, new JsonSerializer<Double>() {
                    @Override
                    public JsonElement serialize(Double originalValue, Type typeOf, JsonSerializationContext context) {
                        BigDecimal bigValue = BigDecimal.valueOf(originalValue);

                        return new JsonPrimitive(bigValue.toPlainString());
                    }
                })
                .create();
    }

    public static GsonHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GsonHelper();
        }
        return INSTANCE;
    }

    public String toJson(Object object) {
        return mGson.toJson(object);
    }

    public Gson getGson() {
        return mGson;
    }
}
