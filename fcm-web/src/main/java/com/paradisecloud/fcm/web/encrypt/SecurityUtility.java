package com.paradisecloud.fcm.web.encrypt;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class SecurityUtility {

	private final String PUBLIC_KEY_FILE_NAME = "pk";
	private final String TDES_KEY_FILE_NAME = "tk";
	private final int ENCRYPT_FILE_CONTENT_LENGTH = 16;
	private final int ENCRYPT_ZIP_FILE_MIN_LENGTH = 688;
	private final String fileExtention = ".tty";

	private byte[] createChecksum(RandomAccessFile randomAccessFile, long length, String sn) {
		try {
			int readTotalLength = 0;
			randomAccessFile.seek(0);
			byte[] buffer = new byte[10240];
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

			int readLength;
			while ((readLength = randomAccessFile.read(buffer)) != -1) {
				if (readLength > 0) {
					if (readTotalLength + readLength > length) {
						int readLengthNew = (int) length - readTotalLength;
						readTotalLength += readLength;
						messageDigest.update(buffer, 0, readLengthNew);
						break;
					} else {
						readTotalLength += readLength;
						messageDigest.update(buffer, 0, readLength);
					}
				}
			}
			if (!StringUtility.isEmptyAfterTrim(sn)) {
				messageDigest.update(sn.getBytes());
			}

			return messageDigest.digest();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	private byte[] removePrefixPadding(byte[] array) {
		if (array == null || array.length == 0) {
			return array;
		}
		int startIndex = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] != 0x00) {
				startIndex = i;
				break;
			}
		}
		byte[] newArray = new byte[array.length - startIndex];
		System.arraycopy(array, startIndex, newArray, 0, newArray.length);

		return newArray;
	}

	/* decrypt start */
	public ResultValue decryptFile(String inputFilePath, String outputFilePath, String sn) {
		ResultValue resultValue = new ResultValue();
		List<String> messages = new ArrayList<>();
		if (!inputFilePath.endsWith(fileExtention)) {
			resultValue.setSuccess(false);
			messages.add("输入文件错误");
			resultValue.setMessages(messages);
			return resultValue;
		}
		boolean success = false;
		RandomAccessFile randomAccessFile = null;
		File outputFileTemp = null;
		File outputEncFile = null;
		File outputZipFile = null;
		try {
			File inputFileSource = new File(inputFilePath);
			if (inputFileSource.exists() && inputFileSource.isFile()) {
				messages.add("输入:" + inputFileSource.getAbsolutePath());
				String osName = System.getProperty("os.name");
				String tmpDir = System.getProperty("java.io.tmpdir");
				inputFileSource = new File(inputFileSource.getAbsolutePath());
				String inputFileParentPath = inputFileSource.getParent();
				String inputFileName = inputFileSource.getName();
				String outputFileNameTemp = inputFileName.replace(fileExtention, ".tmp");
				outputFileTemp = new File(tmpDir + "/" + outputFileNameTemp);
				FileUtils.copyFile(inputFileSource, outputFileTemp);
				randomAccessFile = new RandomAccessFile(outputFileTemp, "rw");
				if (randomAccessFile.length() < ENCRYPT_ZIP_FILE_MIN_LENGTH) {
					randomAccessFile.close();
					outputFileTemp.delete();
					randomAccessFile = null;
					outputFileTemp = null;
					resultValue.setSuccess(false);
					messages.add("输入文件大小错误");

					resultValue.setMessages(messages);
					return resultValue;
				}

				randomAccessFile.seek(randomAccessFile.length() - 4);
				byte[] infoLengthByte = new byte[4];
				randomAccessFile.read(infoLengthByte);
				int infoLength = Integer.valueOf(Converter.byteArray2HexString(infoLengthByte), 16);
				if (infoLength <= 0 || infoLength > randomAccessFile.length() || infoLength > 5000) {
					randomAccessFile.close();
					outputFileTemp.delete();
					randomAccessFile = null;
					outputFileTemp = null;
					resultValue.setSuccess(false);
					messages.add("输入文件错误");

					resultValue.setMessages(messages);
					return resultValue;
				}
				long outputFileLength = randomAccessFile.length() - 4 - infoLength;

				byte[] fileInfoByte = new byte[infoLength];
				randomAccessFile.seek(outputFileLength);
				randomAccessFile.read(fileInfoByte);
				byte[] encryptedMdLengthByte = new byte[2];
				randomAccessFile.seek(outputFileLength);
				randomAccessFile.read(encryptedMdLengthByte);
				int encryptedMdLength = Integer.valueOf(Converter.byteArray2HexString(encryptedMdLengthByte), 16);
				if (encryptedMdLength > infoLength) {
					randomAccessFile.close();
					outputFileTemp.delete();
					randomAccessFile = null;
					outputFileTemp = null;
					resultValue.setSuccess(false);
					messages.add("输入文件错误");

					resultValue.setMessages(messages);
					return resultValue;
				}
				byte[] encryptedMdByte = new byte[encryptedMdLength];
				randomAccessFile.seek(outputFileLength + 2);
				randomAccessFile.read(encryptedMdByte);
				String encryptedMdStr = Converter.byteArray2HexString(encryptedMdByte);
				byte[] randomKeyLengthByte = new byte[2];
				randomAccessFile.seek(outputFileLength + 2 + encryptedMdLength);
				randomAccessFile.read(randomKeyLengthByte);
				int randomKeyLength = Integer.valueOf(Converter.byteArray2HexString(randomKeyLengthByte), 16);
				if (encryptedMdLength > infoLength) {
					randomAccessFile.close();
					outputFileTemp.delete();
					randomAccessFile = null;
					outputFileTemp = null;
					resultValue.setSuccess(false);
					messages.add("输入文件错误");

					resultValue.setMessages(messages);
					return resultValue;
				}
				byte[] randomKey = new byte[randomKeyLength];
				randomAccessFile.seek(outputFileLength + 2 + encryptedMdLength + 2);
				randomAccessFile.read(randomKey);
				byte[] encryptedFileNameLengthByte = new byte[2];
				randomAccessFile.seek(outputFileLength + 2 + encryptedMdLength + 2 + randomKeyLength);
				randomAccessFile.read(encryptedFileNameLengthByte);
				int encryptedFileNameLength = Integer.valueOf(Converter.byteArray2HexString(encryptedFileNameLengthByte), 16);
				if (encryptedMdLength > infoLength) {
					randomAccessFile.close();
					outputFileTemp.delete();
					randomAccessFile = null;
					outputFileTemp = null;
					resultValue.setSuccess(false);
					messages.add("输入文件错误");

					resultValue.setMessages(messages);
					return resultValue;
				}
				byte[] fileNameByte = new byte[encryptedFileNameLength];
				randomAccessFile.seek(outputFileLength + 2 + encryptedMdLength + 2 + randomKeyLength + 2);
				randomAccessFile.read(fileNameByte);
				String fileName = new String(fileNameByte);

				byte[] tdesKey = getTDESKey(randomKey);
				decryptFile(randomAccessFile, tdesKey, outputFileLength);
				byte[] checkSum = createChecksum(randomAccessFile, outputFileLength, sn);
				byte[] encryptedCheckSum = encryptRSA(checkSum);
				encryptedCheckSum = removePrefixPadding(encryptedCheckSum);
				String encryptedCheckSumStr = Converter.byteArray2HexString(encryptedCheckSum);
				if (encryptedCheckSumStr.equals(encryptedMdStr)) {
					outputEncFile = new File(tmpDir + "/" + fileName);
					FileOutputStream fileOutputStream = new FileOutputStream(outputEncFile);
					int readTotalLength = 0;
					randomAccessFile.seek(0);
					byte[] buffer = new byte[10240];

					int readLength;
					while ((readLength = randomAccessFile.read(buffer)) != -1) {
						if (readLength > 0) {
							if (readTotalLength + readLength > outputFileLength) {
								int readLengthNew = (int) outputFileLength - readTotalLength;
								readTotalLength += readLength;
								fileOutputStream.write(buffer, 0, readLengthNew);
								break;
							} else {
								readTotalLength += readLength;
								fileOutputStream.write(buffer, 0, readLength);
							}
						}
					}
					fileOutputStream.flush();
					fileOutputStream.close();
					String password = getZipPassword(tdesKey);
					if (StringUtility.isEmpty(outputFilePath)) {
						outputFilePath = inputFileParentPath;
					} else if (osName.toLowerCase().startsWith("win")) {
						if (!(outputFilePath.length() >= 3 && (outputFilePath.substring(1, 3).equals(":/")
								|| outputFilePath.substring(1, 3).equals(":\\")))) {
							outputFilePath = inputFileParentPath + "/" + outputFilePath;
						}
					} else {
						if (!outputFilePath.startsWith("/")) {
							outputFilePath = inputFileParentPath + "/" + outputFilePath;
						}
					}
					ZipUtility.upZipFile(outputEncFile.getAbsolutePath(), tmpDir, password);
					outputZipFile = new File(tmpDir + "/" + fileName.replace(".enc", ".zip"));
					ZipUtility.upZipFile(outputZipFile.getAbsolutePath(), outputFilePath, password);
					messages.add("输出:" + outputFilePath);
					outputEncFile.delete();
					outputZipFile.delete();
					success = true;
				} else {
					messages.add("签名验证错误");
				}

				randomAccessFile.close();
				outputFileTemp.delete();
				randomAccessFile = null;
				outputFileTemp = null;
				outputEncFile = null;
				outputZipFile = null;
				resultValue.setSuccess(success);
			} else {
				resultValue.setSuccess(false);
				messages.add("找不到输入文件");
			}
		} catch (Exception e) {
			messages.add("其它错误");
			messages.add(e.toString());
		}

		if (randomAccessFile != null) {
			try {
				randomAccessFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (outputFileTemp != null) {
			outputFileTemp.delete();
		}
		if (outputEncFile != null) {
			outputEncFile.delete();
		}
		if (outputZipFile != null) {
			outputZipFile.delete();
		}

		resultValue.setMessages(messages);
		return resultValue;
	}

	private byte[] getDecryptInternalKey(String keyFileName) {
		byte[] keyByte = new byte[0];
		try {
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(keyFileName);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byte[] readByte = new byte[1024];
			int readLength = 0;
			while ((readLength = inputStream.read(readByte)) != -1) {
				if (readLength > 0) {
					byteArrayOutputStream.write(readByte, 0, readLength);
				}
			}
			inputStream.close();
			keyByte = Converter.hexString2ByteArray(new String(byteArrayOutputStream.toByteArray()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return keyByte;
	}

	private byte[] getDecryptExternalKey(String keyFileName) {
		byte[] keyByte = new byte[0];
		URL url = SecurityUtility.class.getProtectionDomain().getCodeSource().getLocation();
		String filePath;
		try {
			filePath = URLDecoder.decode(url.getPath(), "utf-8");
		} catch (Exception e) {
			filePath = "";
		}
		boolean runJar = false;
		if (filePath.endsWith(".jar")) {
			runJar = true;
			filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
		}
		File file = new File(filePath);
		filePath = file.getAbsolutePath();

		if (!runJar) {
			filePath = file.getParent() + "/classes";
			System.out.println(keyFileName + ":" + filePath);
		}

		filePath = filePath + "/" + keyFileName;
		File keyFile = new File(filePath);
		if (keyFile.exists()) {
			try {
				InputStream inputStream = new FileInputStream(keyFile);
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				byte[] readByte = new byte[1024];
				int readLength = 0;
				while ((readLength = inputStream.read(readByte)) != -1) {
					if (readLength > 0) {
						byteArrayOutputStream.write(readByte, 0, readLength);
					}
				}
				inputStream.close();
				keyByte = Converter.hexString2ByteArray(new String(byteArrayOutputStream.toByteArray()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return keyByte;
	}

	private byte[] encryptRSA(byte[] data) throws Exception {
		byte[] key = getDecryptExternalKey(PUBLIC_KEY_FILE_NAME);
		if (key.length == 0) {
			key = getDecryptInternalKey(PUBLIC_KEY_FILE_NAME);
		}
		SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(key);
		byte[] encryptDataByte = RSASecurityUtility.encrypt(subjectPublicKeyInfo, data);
		return encryptDataByte;
	}

	private byte[] decryptTDES(byte[] data, byte[] key) throws Exception {
		byte[] decryptDataByte = TDesUtility.decrypt(data, key);
		return decryptDataByte;
	}

	private byte[] getTDESKey(byte[] randomKey) {
		byte[] key = getDecryptExternalKey(TDES_KEY_FILE_NAME);
		if (key.length == 0) {
			key = getDecryptInternalKey(TDES_KEY_FILE_NAME);
		}
		key = DesUtility.xor(key, randomKey);
		return key;
	}

	private String getZipPassword(byte[] tdesKey) {
		String password = Converter.byteArray2HexString(tdesKey);
		return password;
	}

	private void decryptFile(RandomAccessFile randomAccessFile, byte[] key, long fileLength) {
		try {
			int encryptMaxLength = (ENCRYPT_ZIP_FILE_MIN_LENGTH / 2) + ENCRYPT_FILE_CONTENT_LENGTH * 1000;
			long fileMaxLength = fileLength / 2;
			if (fileMaxLength < encryptMaxLength) {
				encryptMaxLength = (int) fileMaxLength;
				encryptMaxLength = (encryptMaxLength / ENCRYPT_FILE_CONTENT_LENGTH) * ENCRYPT_FILE_CONTENT_LENGTH;
			}
			byte[] bufferHead = new byte[encryptMaxLength];
			long pointHead = 0;
			randomAccessFile.seek(pointHead);
			randomAccessFile.read(bufferHead);
			byte[] decryptedDataHead = decryptTDES(bufferHead, key);
			randomAccessFile.seek(pointHead);
			randomAccessFile.write(decryptedDataHead);

			byte[] bufferFoot = new byte[encryptMaxLength];
			long pointFoot = fileLength - encryptMaxLength;
			randomAccessFile.seek(pointFoot);
			randomAccessFile.read(bufferFoot);
			byte[] decryptedDataFoot = decryptTDES(bufferFoot, key);
			randomAccessFile.seek(pointFoot);
			randomAccessFile.write(decryptedDataFoot);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/* decrypt end */

}
