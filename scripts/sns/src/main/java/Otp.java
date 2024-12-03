import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

import javax.crypto.KeyGenerator;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

public class Otp {
    public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException {
        for(int i = 0; i < 5; i++)
            System.out.println("codigo: " + generateCode());
    }
    public static String generateCode() throws NoSuchAlgorithmException, InvalidKeyException {
        final TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator();

        final Key key;
        final KeyGenerator keyGenerator = KeyGenerator.getInstance(totp.getAlgorithm());

        keyGenerator.init(1024);
        key = keyGenerator.generateKey();

        final Instant now = Instant.now();
        return String.format("%08d", totp.generateOneTimePassword(key, now));
    }
}
