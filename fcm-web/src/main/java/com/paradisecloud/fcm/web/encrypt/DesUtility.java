package com.paradisecloud.fcm.web.encrypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;

public class DesUtility {

    private static final String KEY_ALGORITHM = "DES";
    private static final String CIPHER_ALGORITHM_CBC = "DES/CBC/NoPadding";
    private static final String CIPHER_ALGORITHM_ECB = "DES/ECB/NoPadding";
    public static final byte[] DEFAULT_IV = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};

    /**
     * @param byte1
     * @param byte2
     * @return
     */
    public static byte[] xor(byte[] byte1, byte[] byte2) {
        int xorLength = Math.min(byte1.length, byte2.length);
        byte[] xorByte = new byte[xorLength];
        for (int i = 0; i < xorLength; i++) {
            xorByte[i] = (byte) (byte1[i] ^ byte2[i]);
        }

        return xorByte;
    }

    /**
     * @param key
     * @return
     * @throws Exception
     */
    private static Key toKey(byte[] key) throws Exception {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (key.length != 8 && key.length != 16 && key.length != 24) {
            throw new IllegalArgumentException();
        }
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        SecretKey secretKey = skf.generateSecret(dks);
        return secretKey;
    }

    /**
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        Key k = toKey(key);
        return encryptEBC(data, k);
    }

    /**
     * @param data
     * @param key
     * @param iv
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, byte[] key, byte[] iv) throws Exception {
        Key k = toKey(key);
        return encryptCBC(data, k, iv);
    }

    /**
     * @param data
     * @param key
     * @return byte[]
     * @throws Exception
     */
    public static byte[] encryptEBC(byte[] data, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_ECB);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * @param data
     * @param key
     * @param iv
     * @return byte[]
     * @throws Exception
     */
    public static byte[] encryptCBC(byte[] data, Key key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
        IvParameterSpec ips = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ips);
        return cipher.doFinal(data);
    }

    /**
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        Key k = toKey(key);
        return decryptEBC(data, k);
    }

    /**
     * @param data
     * @param key
     * @param iv
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, byte[] key, byte[] iv) throws Exception {
        Key k = toKey(key);
        return decryptCBC(data, k, iv);
    }

    /**
     * @param data
     * @param key
     * @return byte[]
     * @throws Exception
     */
    public static byte[] decryptEBC(byte[] data, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_ECB);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * @param data
     * @param key
     * @param iv
     * @return byte[]
     * @throws Exception
     */
    public static byte[] decryptCBC(byte[] data, Key key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
        IvParameterSpec ips = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ips);
        return cipher.doFinal(data);
    }

}
