package helper;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import java.util.Random;

public class GenerateCodeHelper {
    public static String generateCode(int size) {
        char[] alphabet = {'0','1','2','3','4','5','6','7','8','9'};
        Random random = new Random();
        return NanoIdUtils.randomNanoId(random, alphabet, size);
    }
}