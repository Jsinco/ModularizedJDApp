package dev.jsinco.discord.modules.cryptography;

import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.modules.util.StringUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESEncrypt {

    private static final String ALGORITHM = "AES";
    private static AESEncrypt instance;
    private final String key;

    private AESEncrypt(String key) {
        this.key = key;
    }

    public String encrypt(String data) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedData = cipher.doFinal(data.getBytes());

        // Return encrypted data in Base64 encoding
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public String decrypt(String encryptedData) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);

        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));

        return new String(decryptedData);
    }

    public static AESEncrypt getInstance() {
        if (instance == null) {
            String key = StringUtil.getFromEnvironment("encrypt_key");
            if (key == null) {
                FrameWorkLogger.error("Failed to initialize AESEncrypt, key is null.");
                return null;
            }
            instance = new AESEncrypt(key);
        }
        return instance;
    }
}
