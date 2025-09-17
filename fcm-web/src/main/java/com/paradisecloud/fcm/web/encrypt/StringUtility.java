package com.paradisecloud.fcm.web.encrypt;

public class StringUtility {

	public static boolean isEmpty(String str) {
		if (str == null || str.length() == 0) {
			return true;
		}
		return false;
	}

	public static boolean isEmptyAfterTrim(String str) {
		if (str == null || str.trim().length() == 0) {
			return true;
		}
		return false;
	}

	public static String nullToBlank(String str) {
		if (str == null) {
			str = "";
		}
		return str;
	}

	public static String getPrefix(String str1, String str2) {
		String prefix = "";
		if (isEmpty(str1) || isEmpty(str2)) {
			return prefix;
		}
		int length = Math.min(str1.length(), str2.length());
		for (int i = 0; i < length; i++) {
			if (str1.charAt(i) == str2.charAt(i)) {
				prefix += str1.charAt(i);
			} else {
				break;
			}
		}

		return prefix;
	}

}
