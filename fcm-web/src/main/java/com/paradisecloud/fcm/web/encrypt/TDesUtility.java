package com.paradisecloud.fcm.web.encrypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;

public class TDesUtility {

	private static final String KEY_ALGORITHM = "DESede";
	private static final String CIPHER_ALGORITHM_CBC = "DESede/CBC/NoPadding";
	private static final String CIPHER_ALGORITHM_ECB = "DESede/ECB/NoPadding";
	public static final byte[] DEFAULT_IV = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };

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
		byte[] fixedKey = new byte[24];
		if (key.length == 8) {
			System.arraycopy(key, 0, fixedKey, 0, 8);
			System.arraycopy(key, 0, fixedKey, 8, 8);
			System.arraycopy(key, 0, fixedKey, 16, 8);
		} else if (key.length == 16) {
			System.arraycopy(key, 0, fixedKey, 0, 16);
			System.arraycopy(key, 0, fixedKey, 16, 8);
		} else {
			fixedKey = key;
		}
		DESedeKeySpec dks = new DESedeKeySpec(fixedKey);
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
		return encryptECB(data, k);
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
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptECB(byte[] data, Key key) throws Exception {
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_ECB);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(data);
	}

	/**
	 * @param data
	 * @param key
	 * @param iv
	 * @return
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
		return decryptECB(data, k);
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
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptECB(byte[] data, Key key) throws Exception {
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_ECB);
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(data);
	}

	/**
	 * @param data
	 * @param key
	 * @param iv
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptCBC(byte[] data, Key key, byte[] iv) throws Exception {
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
		IvParameterSpec ips = new IvParameterSpec(iv);
		cipher.init(Cipher.DECRYPT_MODE, key, ips);
		return cipher.doFinal(data);
	}

	/**
	 * @param data
	 * @param key
	 * @param iv
	 * @return
	 * @throws Exception
	 */
	public static byte[] cacMac(byte[] data, byte[] key, byte[] iv) throws Exception {
		byte[] leftKey = new byte[8];
		System.arraycopy(key, 0, leftKey, 0, 8);

		final int dataLength = data.length;
		final int blockCount = dataLength / 8 + 1;
		final int lastBlockLength = dataLength % 8;

		byte[][] dataBlock = new byte[blockCount][8];
		for (int i = 0; i < blockCount; i++) {
			int copyLength = i == blockCount - 1 ? lastBlockLength : 8;
			System.arraycopy(data, i * 8, dataBlock[i], 0, copyLength);
		}
		dataBlock[blockCount - 1][lastBlockLength] = (byte) 0x80;

		byte[] desXor = DesUtility.xor(dataBlock[0], iv);
		for (int i = 1; i < blockCount; i++) {
			byte[] des = DesUtility.encrypt(desXor, leftKey, iv);
			desXor = DesUtility.xor(dataBlock[i], des);
		}
		Key k = toKey(key);

		return encryptCBC(desXor, k, iv);
	}

}
