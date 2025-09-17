package com.paradisecloud.fcm.web.controller.im.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Base64;

public class SM4Utils {
    // 算法名称
    public static final String ALGORITHM_NAME = "SM4";
    // 加密模式：CBC（需偏移量）
    public static final String ALGORITHM_NAME_CBC_PADDING = "SM4/CBC/PKCS5Padding";
    // 密钥长度必须16字节（128位）
    private static final int KEY_SIZE = 16;
    // 偏移量IV长度必须16字节
    private static final int IV_SIZE = 16;

    static {
        // 加入BouncyCastleProvider支持SM4
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 加密
     * @param plainText 明文
     * @param key 密钥（16字节）
     * @param iv 偏移量（16字节）
     * @return Base64编码的密文
     */
    public static String encrypt(String plainText, String key, String iv) throws Exception {
        validateKeyAndIv(key, iv);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM_NAME);
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));

        Cipher cipher = Cipher.getInstance(ALGORITHM_NAME_CBC_PADDING, "BC");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * 解密
     * @param cipherText Base64编码的密文
     * @param key 密钥（16字节）
     * @param iv 偏移量（16字节）
     * @return 明文
     */
    public static String decrypt(String cipherText, String key, String iv) throws Exception {
        validateKeyAndIv(key, iv);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM_NAME);
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));

        Cipher cipher = Cipher.getInstance(ALGORITHM_NAME_CBC_PADDING, "BC");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // 校验密钥和偏移量长度
    private static void validateKeyAndIv(String key, String iv) {
        if (key == null || key.getBytes(StandardCharsets.UTF_8).length != KEY_SIZE) {
            throw new IllegalArgumentException("SM4密钥必须是16字节");
        }
        if (iv == null || iv.getBytes(StandardCharsets.UTF_8).length != IV_SIZE) {
            throw new IllegalArgumentException("SM4偏移量必须是16字节");
        }
    }
    public static void main(String[] args) throws Exception {
        String key = "1234567890abcdef"; // 16字节密钥（实际项目中使用更复杂的密钥）
        String iv="fedcba0987654321";//   # 16字节偏移量
        String plainText = "{\n" +
                "    \"username\": \"admin\",\n" +
                "    \"password\": \"123456\"\n" +
                "}";

        String encrypted = encrypt(plainText, key,iv);
        String decrypted = decrypt(encrypted, key,iv);
        System.out.println("加密后：" + encrypted);
        System.out.println("解密后：" + decrypted);
    }
}
