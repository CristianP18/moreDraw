package bundles;

import java.util.Locale;
import java.util.ResourceBundle;

public class Bundle {
    private static Bundle INSTANCE;

    public static Bundle getInstance() {
        if(INSTANCE == null)
            INSTANCE = new Bundle();
        return INSTANCE;
    }

    /**
     * Captura o valor no bundle respectivo a chave e localização.
     * Caso localização seja nulo, português do Brasil será definido como padrão.
     */
    public String getString(String key, String locale){
        if(locale == null || (!locale.equals("pt_BR") && !locale.equals("en_US") && !locale.equals("es_ES")))
            locale = "pt_BR";

        Locale language = new Locale(locale.substring(0, 2), locale.substring(3, 5));
        ResourceBundle bundle = ResourceBundle.getBundle("bundle", language);

        return bundle.getString(key);
    }
}