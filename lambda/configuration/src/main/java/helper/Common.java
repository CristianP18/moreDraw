package helper;

import java.text.Normalizer;

public class Common {
    public String created;
    public String modified;

    public static String removeSpecialCharacters(String input) {
        String output = null;
        if (input != null && !input.equals("")) {
            output = Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
            output = output.replaceAll("[^\\x00-\\x7F]", "").toUpperCase();
            output = output.replaceAll("null", "");
        }

        return output;
    }

    public static String removeSpecialCharactersAddress(String input) {
        String output = null;
        if (input != null && !input.equals("")) {
            output = removeSpecialCharacters(input);
            output = output.replaceAll("&", " ");
        }

        return output;
    }

    public static String removeDashAndDotAndSlash(String input) {
        String output = null;
        if (input != null && !input.equals("")) {
            output = input.replaceAll("^%20+|%20+$", "");
            output = output.replaceAll("-", "");
            output = output.replaceAll("\\.", "");
            output = output.replaceAll("/", "");
            output = output.replaceAll(" ", "");
        }

        return output;
    }

    public static String removeBrackets(String input) {
        String output = null;
        if (input != null && !input.equals("")) {
            output = input.replaceAll("[()]", "");
            output = output.replaceAll(" ", "");
        }

        return output;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }
}