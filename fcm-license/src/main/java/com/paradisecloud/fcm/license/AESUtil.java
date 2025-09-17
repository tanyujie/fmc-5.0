package com.paradisecloud.fcm.license;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author nj
 * @date 2022/8/1 15:44
 */
public class AESUtil {

    private static final String IV_STRING = "HLS3XXfsFD25Vdf1";

    private static final String encoding = "UTF-8";

    public static String encryptAES(String content, String key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {

        byte[] byteContent = content.getBytes(encoding);
        byte[] enCodeFormat = key.getBytes(encoding);

        SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
        byte[] initParam = IV_STRING.getBytes(encoding);

        IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
        // 指定加密的算法、工作模式和填充方式
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] encryptedBytes = cipher.doFinal(byteContent);
        // 同样对加密后数据进行 base64 编码
        String base64 = new Base64().encodeToString(encryptedBytes);

        return URLEncoder.encode(base64, encoding);

    }


    public static String encryptAESByte2HexStr(String content, String key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {

        byte[] byteContent = content.getBytes(encoding);
        byte[] enCodeFormat = key.getBytes(encoding);

        SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
        byte[] initParam = IV_STRING.getBytes(encoding);

        IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
        // 指定加密的算法、工作模式和填充方式
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] encryptedBytes = cipher.doFinal(byteContent);
        // 同样对加密后数据进行 base64 编码


        return parseByte2HexStr(encryptedBytes);

    }

    public static String decryptAES(String content, String key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {

        //URL解码
        content = URLDecoder.decode(content, encoding);
        // base64 解码
        byte[] encryptedBytes = Base64.decodeBase64(content);

        byte[] enCodeFormat = key.getBytes(encoding);

        SecretKeySpec secretKey = new SecretKeySpec(enCodeFormat, "AES");
        byte[] initParam = IV_STRING.getBytes(encoding);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        byte[] result = cipher.doFinal(encryptedBytes);
        return new String(result, encoding);

    }


    public static String decryptAESparseHexStr2Byte(String content, String key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {


        byte[] bytes = parseHexStr2Byte(content);
        byte[] enCodeFormat = key.getBytes(encoding);

        SecretKeySpec secretKey = new SecretKeySpec(enCodeFormat, "AES");
        byte[] initParam = IV_STRING.getBytes(encoding);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        byte[] result = cipher.doFinal(bytes);
        return new String(result, encoding);

    }

    public static byte[] parseHexStr2Byte(String hexStr) {

        if (hexStr.length() < 1)

            return null;

        byte[] result = new byte[hexStr.length() / 2];

        for (int i = 0; i < hexStr.length() / 2; i++) {

            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);

            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);

            result[i] = (byte) (high * 16 + low);

        }

        return result;

    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }



}
