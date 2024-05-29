package de.hitec.nhplus.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {
    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 20;
    private static final String DELIMITER = ":";

    public static String generatePassword(String password) {
        String salt = generateSalt();
        String hashed_password = hashPassword(password, salt);
        return salt + DELIMITER + hashed_password;
    }

    public static String hashPassword(String password, String salt) {
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = Base64.getDecoder().decode(salt);

        PBEKeySpec spec = new PBEKeySpec(passwordChars, saltBytes, ITERATIONS, KEY_LENGTH);
        Arrays.fill(passwordChars, Character.MIN_VALUE);

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[SALT_LENGTH];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static boolean checkPassword(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) { return false; }
        String[] parts = hashedPassword.split(DELIMITER);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid hashed password");
        }
        String salt = parts[0];
        String hashedInputPassword = hashPassword(password, salt);
        return hashedInputPassword.equals(parts[1]);
    }

    public static void main(String[] args) {
        String password = "ø1§$%&/()=?``%password\uD83D\uDD25::::::";
        String hashedPassword = generatePassword(password);
        Boolean checkPassword = checkPassword(password, generatePassword(password));
        System.out.println(password);
        System.out.println(hashedPassword);
        System.out.println(checkPassword);
    }
}
