package com.hakanyilmazz.seyahapp.cryptography;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class Crypter {

    public static String encryptMessage(String message) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        // generates unique id
        String publicIdentity = UUID.randomUUID().toString();

        // for random encryption
        int privateNumberKey = generatePrivateNumberKey(publicIdentity) + getRandomNumber();

        // for "Are messages equals?" control
        String sha256 = toSHA256(message);

        // for XOR algorithm
        int createdNumber = (privateNumberKey + publicIdentity.length()) * (message.length() + sha256.length());
        String encryptedMessage = encryptMessageWithXOR(message, createdNumber);

        String[] result = new String[]{encryptedMessage, sha256, publicIdentity};
        return Arrays.toString(result);
    }

    public static String decryptMessage(String message) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String[] messageContents = parseMessageToArray(message);

        String encryptedMessage = messageContents[0];
        String sha256 = messageContents[1];
        String publicIdentity = messageContents[2];

        return findMessage(encryptedMessage, sha256, publicIdentity);
    }

    private static String[] parseMessageToArray(String message) {
        String temp = message.substring(1, message.length() - 1);
        int messageAreaIndex = temp.indexOf("], ") + 1;

        String messageText = temp.substring(0, messageAreaIndex);

        String secondArea = temp.substring(messageAreaIndex + 2);
        String[] shaAndPublicIdentity = secondArea.split(", ");

        String sha256Text = shaAndPublicIdentity[0];
        String publicIdentityText = shaAndPublicIdentity[1];

        return new String[]{messageText, sha256Text, publicIdentityText};
    }

    private static String toSHA256(final String base) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        final byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));

        final StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            final String hex = Integer.toHexString(0xff & b);

            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    private static int generatePrivateNumberKey(String publicIdentity) {
        char[] privateKeyCharArray = publicIdentity.toCharArray();
        Set<Integer> uniquePrivateKeyNumbers = new HashSet<>();

        for (char character : privateKeyCharArray) {
            if (Character.isDigit(character)) {
                uniquePrivateKeyNumbers.add((int) character);
            }
        }

        int sumOfUniqueNumbers = 0;
        for (int number : uniquePrivateKeyNumbers) {
            sumOfUniqueNumbers += number;
        }

        return sumOfUniqueNumbers / uniquePrivateKeyNumbers.size();
    }

    private static int getRandomNumber() {
        Random random = new Random();
        return random.nextInt(100);
    }

    private static String encryptMessageWithXOR(String message, int createdNumber) {
        int[] result = new int[message.length()];

        for (int i = 0; i < message.length(); i++) {
            result[i] = createdNumber ^ message.charAt(i);
        }

        return Arrays.toString(result);
    }

    private static String findMessage(String encryptedMessage, String messageSHA256, String publicIdentity)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        int publicIdentityLength = publicIdentity.length();
        int encryptedMessageLength = getEncryptedMessageLength(encryptedMessage);
        int messageSHA256Length = messageSHA256.length();

        int generatedNumberKey = generatePrivateNumberKey(publicIdentity);

        int i = 0;
        while (i < 100) {
            int privateNumberKey = generatedNumberKey + i;
            int tempCreatedNumber = (privateNumberKey + publicIdentityLength) * (encryptedMessageLength + messageSHA256Length);

            String decryptedMessage = decryptMessageWithXOR(encryptedMessage, tempCreatedNumber);
            String tempSHA256 = toSHA256(decryptedMessage);

            if (tempSHA256.equals(messageSHA256)) {
                return decryptedMessage;
            }

            i++;
        }

        return "Error! Message couldn't decrypted.";
    }

    private static int getEncryptedMessageLength(String encryptedMessage) {
        String temp = encryptedMessage.substring(1, encryptedMessage.length() - 1);
        temp = temp.replaceAll(", ", "-");
        String[] messageArray = temp.split("-");

        return messageArray.length;
    }

    private static String decryptMessageWithXOR(String encryptedMessage, int createdNumber) {
        String temp = encryptedMessage.substring(1, encryptedMessage.length() - 1);
        temp = temp.replaceAll(", ", "-");

        String[] messageArray = temp.split("-");

        StringBuilder result = new StringBuilder();
        for (String s : messageArray) {
            int firstDecrypt = createdNumber ^ Integer.parseInt(s) ^ createdNumber;
            int secondDecrypt = createdNumber ^ firstDecrypt;

            result.append((char) secondDecrypt);
        }

        return result.toString();
    }

}
