package server;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DatabaseHasher {
    public static String encryptStringSHA1(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance ("SHA-1");
            byte[] digest = md. digest (string.getBytes (StandardCharsets.UTF_8));
            BigInteger numRepresentation = new BigInteger(1, digest);
            String hashedString = numRepresentation. toString ( 16);
            while (hashedString. length () < 32) {
                hashedString = "0" + hashedString;
            }
            return hashedString;
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Нет такого алгоритма");
        }
        return null;
    }
}
