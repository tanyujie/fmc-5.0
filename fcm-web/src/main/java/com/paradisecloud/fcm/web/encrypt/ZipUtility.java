package com.paradisecloud.fcm.web.encrypt;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;

public class ZipUtility {

	public static boolean zipFile(String inputPath, String outputPath, String password) {
		try {
			File inputFile = new File(inputPath);
			if (inputFile.exists()) {
				ZipParameters parameters = new ZipParameters();
				if (!StringUtility.isEmpty(password)) {
					parameters.setPassword(password);
					parameters.setEncryptFiles(true);
					parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
				}
				if (inputFile.isFile()) {
					ZipFile zipFile = new ZipFile(outputPath);
					zipFile.addFile(inputFile, parameters);
					return true;
				} else if (inputFile.isDirectory()) {
					File[] files = inputFile.listFiles();
					if (files.length > 0) {
						ZipFile zipFile = new ZipFile(outputPath);
						for (File file : files) {
							if (file.isFile()) {
								zipFile.addFile(file, parameters);
							} else if (file.isDirectory()) {
								zipFile.addFolder(file, parameters);
							}
						}
					} else {
						System.err.println("Empty folder");
					}
					return true;
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean upZipFile(String inputPath, String outputPath, String password) {
		try {
			ZipFile zipFile = new ZipFile(inputPath);
			if (!zipFile.isValidZipFile()) {
				throw new ZipException();
			}
			File desDir = new File(outputPath);
			if (!desDir.exists()) {
				desDir.mkdirs();
			}
			if (zipFile.isEncrypted()) {
				zipFile.setPassword(password);
			}
			zipFile.extractAll(outputPath);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

}
