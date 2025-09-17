package com.paradisecloud.fcm.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RSAUtil {

    public static final String KEY_ALGORITHM = "RSA";

    private static final String PUBLIC_KEY = "RSAPublicKey";

    private static final String PRIVATE_KEY = "RSAPrivateKey";

    // 1024 bits 的 RSA 密钥对，最大加密明文大小
    private static final int MAX_ENCRYPT_BLOCK = 117;

    // 1024 bits 的 RSA 密钥对，最大解密密文大小
    private static final int MAX_DECRYPT_BLOCK = 128;

    // 生成密钥对
    public static Map<String, Object> initKey(int keySize) throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        // 设置密钥对的 bit 数，越大越安全
        keyPairGen.initialize(keySize);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        // 获取公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        // 获取私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    // 获取公钥字符串
    public static String getPublicKeyStr(Map<String, Object> keyMap) {
        // 获得 map 中的公钥对象，转为 key 对象
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        // 编码返回字符串
        return encryptBASE64(key.getEncoded());
    }

    // 获取私钥字符串
    public static String getPrivateKeyStr(Map<String, Object> keyMap) {
        // 获得 map 中的私钥对象，转为 key 对象
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        // 编码返回字符串
        return encryptBASE64(key.getEncoded());
    }

    // 获取公钥
    public static PublicKey getPublicKey(String publicKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicKeyByte = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyByte);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    // 获取私钥
    public static PrivateKey getPrivateKey(String privateKeyString) throws Exception {
        byte[] privateKeyByte = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyByte);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * BASE64 编码返回加密字符串
     *
     * @param key 需要编码的字节数组
     * @return 编码后的字符串
     */
    public static String encryptBASE64(byte[] key) {
        return new String(Base64.getEncoder().encode(key));
    }

    /**
     * BASE64 解码，返回字节数组
     *
     * @param key 待解码的字符串
     * @return 解码后的字节数组
     */
    public static byte[] decryptBASE64(String key) {
        return Base64.getDecoder().decode(key);
    }

    /**
     * 公钥加密
     *
     * @param text         待加密的明文字符串
     * @param publicKeyStr 公钥
     * @return 加密后的密文
     */
    public static String encrypt(String text, String publicKeyStr) {
        try {
            log.info("明文字符串为:[{}]", text);
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKeyStr));
            byte[] tempBytes = cipher.doFinal(text.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(tempBytes);
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + text + "]时遇到异常", e);
        }
    }

    /**
     * 公钥加密
     *
     * @param text         待加密的明文字符串
     * @param publicKey 公钥
     * @return 加密后的密文
     */
    public static String encrypt(String text, PublicKey publicKey) {
        try {
            log.info("明文字符串为:[{}]", text);
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] tempBytes = cipher.doFinal(text.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(tempBytes);
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + text + "]时遇到异常", e);
        }
    }

    /**
     * 私钥解密
     *
     * @param secretText    待解密的密文字符串
     * @param privateKeyStr 私钥
     * @return 解密后的明文
     */
    public static String decrypt(String secretText, String privateKeyStr) {
        try {
            // 生成私钥
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKeyStr));
            // 密文解码
            byte[] secretTextDecoded = Base64.getDecoder().decode(secretText.getBytes("UTF-8"));
            byte[] tempBytes = cipher.doFinal(secretTextDecoded);
            return new String(tempBytes);
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + secretText + "]时遇到异常", e);
        }
    }

    /**
     * 私钥解密
     *
     * @param secretText    待解密的密文字符串
     * @param privateKey 私钥
     * @return 解密后的明文
     */
    public static String decrypt(String secretText, PrivateKey privateKey) {
        try {
            // 生成私钥
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            // 密文解码
            byte[] secretTextDecoded = Base64.getDecoder().decode(secretText.getBytes("UTF-8"));
            byte[] tempBytes = cipher.doFinal(secretTextDecoded);
            return new String(tempBytes);
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + secretText + "]时遇到异常", e);
        }
    }

    /**
     * 从pkcs12文件中获取密钥
     *
     * @param inputStream
     * @param password
     * @return
     * @throws Exception
     */
    public static SignCertInfo parse(InputStream inputStream, String password) throws Exception {
        SignCertInfo signCertInfo = new SignCertInfo();
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(inputStream, password.toCharArray());
        String keyAlias = null;
        //解析证书，必须有别名
        Enumeration<String> aliases = ks.aliases();
        if (aliases.hasMoreElements()) {
            keyAlias = aliases.nextElement();
        }
        //解析私钥
        PrivateKey privateKey = (PrivateKey) ks.getKey(keyAlias, password.toCharArray());
        java.security.cert.Certificate cert = ks.getCertificate(keyAlias);
        BigInteger serialNumber = ((X509Certificate) cert).getSerialNumber();
        //证书一般都使用16进制表示
        String serialNo = serialNumber.toString(16);

        signCertInfo.setSerialNo(serialNo);
        signCertInfo.setPrivateKey(privateKey);
        signCertInfo.setPublicKey(cert.getPublicKey());

        //设置证书序列号和私钥
        return signCertInfo;
    }

    public static class SignCertInfo {

        /**
         * 证书序列号.
         */
        private String serialNo;

        /**
         * 证书秘钥.
         */
        private PrivateKey privateKey;

        /**
         * 公钥.
         */
        private PublicKey publicKey;

        public String getSerialNo() {
            return serialNo;
        }

        public void setSerialNo(String serialNo) {
            this.serialNo = serialNo;
        }

        public PrivateKey getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(PrivateKey privateKey) {
            this.privateKey = privateKey;
        }

        public PublicKey getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(PublicKey publicKey) {
            this.publicKey = publicKey;
        }
    }
}


