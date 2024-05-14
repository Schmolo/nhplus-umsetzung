package de.hitec.nhplus.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {
    private static final String ALGORITHM = "SHA-512";

    public static String generatePassword(String password) {
        String salt = generateSalt();
        String hashed_password = hashPassword(password, salt);
        return salt + ":" + hashed_password;
    }

    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt.getBytes());
            byte[] bytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static boolean checkPassword(String password, String hashedPassword) {
        String[] parts = hashedPassword.split(":");
        String salt = parts[0];
        String hashedInputPassword = hashPassword(password, salt);
        return hashedInputPassword.equals(parts[1]);
    }

    public static void main(String[] args) {
        String password = "password";
        String hashedPassword = generatePassword(password);
        Boolean checkPassword = checkPassword(password, generatePassword(password));
        System.out.println(password);
        System.out.println(hashedPassword);
        System.out.println(checkPassword);
    }
}
