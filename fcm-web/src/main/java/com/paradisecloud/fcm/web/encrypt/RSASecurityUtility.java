package com.paradisecloud.fcm.web.encrypt;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSASecurityUtility {

    private static final int KEY_SIZE = 2048;
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String KEY_ALGORITHM_RSA = "RSA";
    public static final String KEY_ALGORITHM_OAEP_SHA256= "RSA/NONE/OAEPWithSHA256AndMGF1Padding";

    /**
     *
     */
    public static AsymmetricCipherKeyPair generateKeyPair() {
        RSAKeyPairGenerator rsaKeyPairGenerator = new RSAKeyPairGenerator();
        SecureRandom secureRandom = new SecureRandom();
        RSAKeyGenerationParameters rsaKeyGenerationParameters = new RSAKeyGenerationParameters(BigInteger.valueOf(3), secureRandom, KEY_SIZE, 25);
        rsaKeyPairGenerator.init(rsaKeyGenerationParameters);
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = rsaKeyPairGenerator.generateKeyPair();
        return asymmetricCipherKeyPair;
    }

    /**
     * @param publicKey
     * @param privateKey
     * @param CN
     * @param OU
     * @param O
     * @param L
     * @param ST
     * @param C
     * @return
     */
    public static PKCS10CertificationRequest generateCSR(PublicKey publicKey, PrivateKey privateKey,
                                                         String CN, String OU, String O, String L, String ST, String C) {

        PKCS10CertificationRequest certificationRequest = null;
        try {
            certificationRequest = generatePKCS10(publicKey, privateKey, CN, OU, O, L, ST, C);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return certificationRequest;
    }

    /**
     * @param CN Common Name, is X.509 speak for the name that distinguishes
     *           the Certificate best, and ties it to your Organization
     * @param OU Organizational unit
     * @param O  Organization NAME
     * @param L  Location
     * @param ST State
     * @param C  Country
     * @return
     * @throws Exception
     */
    public static PKCS10CertificationRequest generatePKCS10(PublicKey publicKey, PrivateKey privateKey,
                                                            String CN, String OU, String O, String L, String ST, String C) throws Exception {

        // common, orgUnit, org, locality, state, country
        X500NameBuilder nameBuilder = new X500NameBuilder();
        nameBuilder.addRDN(BCStyle.CN, CN);
        nameBuilder.addRDN(BCStyle.OU, OU);
        nameBuilder.addRDN(BCStyle.O, O);
        nameBuilder.addRDN(BCStyle.L, L);
        nameBuilder.addRDN(BCStyle.ST, ST);
        nameBuilder.addRDN(BCStyle.C, C);
        X500Name x500Name = nameBuilder.build();

        PKCS10CertificationRequestBuilder builder = new PKCS10CertificationRequestBuilder(x500Name, SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));

        JcaContentSignerBuilder signerBuilder = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM);
        ContentSigner contentSigner = signerBuilder.build(privateKey);

        return builder.build(contentSigner);
    }

    /**
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] sign(byte[] data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    /**
     * @param publicKey
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(PublicKey publicKey, byte[] data) throws Exception {
        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        return encrypt(subjectPublicKeyInfo, data);
    }

    /**
     * @param publicKey
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(SubjectPublicKeyInfo publicKey, byte[] data) throws Exception {
        AsymmetricKeyParameter asymmetricKeyParameter = PublicKeyFactory.createKey(publicKey);
        AsymmetricBlockCipher cipher = new RSAEngine();
        cipher.init(true, asymmetricKeyParameter);
        byte[] encryptData = cipher.processBlock(data, 0, data.length);
        return encryptData;
    }

    /**
     * @param privateKey
     * @param encryptData
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(PrivateKey privateKey, byte[] encryptData) throws Exception {
        PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(privateKey.getEncoded());
        return decrypt(privateKeyInfo, encryptData);
    }

    /**
     * @param privateKey
     * @param encryptData
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(PrivateKey privateKey, byte[] encryptData, String keyAlgorithm) throws Exception {
        Cipher cipher = Cipher.getInstance(keyAlgorithm);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptData);
    }

    /**
     * @param privateKey
     * @param encryptData
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(PrivateKeyInfo privateKey, byte[] encryptData) throws Exception {
        AsymmetricKeyParameter asymmetricKeyParameter = PrivateKeyFactory.createKey(privateKey);
        AsymmetricBlockCipher cipher = new RSAEngine();
        cipher.init(false, asymmetricKeyParameter);
        byte[] data = cipher.processBlock(encryptData, 0, encryptData.length);
        return data;
    }

    /**
     * @param privateKey
     * @param encryptData
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(PrivateKeyInfo privateKey, byte[] encryptData, String keyAlgorithm) throws Exception {
        Cipher cipher = Cipher.getInstance(keyAlgorithm);
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKey.getEncoded()));
        return cipher.doFinal(encryptData);
    }

    /**
     *
     * @param publicKeyByte
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKey(byte[] publicKeyByte) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyByte);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     *
     * @param privateKeyByte
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(byte[] privateKeyByte) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyByte);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }
}
