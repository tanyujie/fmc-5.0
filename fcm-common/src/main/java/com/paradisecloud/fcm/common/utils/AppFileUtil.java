package com.paradisecloud.fcm.common.utils;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.AppType;
import org.apache.commons.codec.binary.Hex;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.MessageDigest;
import java.util.Date;

public class AppFileUtil {

    public static String saveFile(MultipartFile file) throws Exception {
        return saveFile(file, "", "");
    }

    public static String saveFile(MultipartFile file, String terminalType, String versionName) throws Exception {
        String filePath = "";
        if (file.isEmpty()) {
            throw new CustomException("文件为空");
        }
        String rootUrl = ExternalConfigCache.getInstance().getAppFileRootUrl();
        if (StringUtils.isEmpty(rootUrl)) {
            throw new CustomException("未配置文件存储地址");
        }
        try {
            // 给文件重命名
            String fileName = DateUtil.convertDateToString(new Date(), "yyyyMMddHHmmss") + "_" + file.getOriginalFilename();
            // 获取保存路径
            String path = getSavePath();
            AppType appType = AppType.convert(terminalType);
            if (appType != null) {
                path += "/" + appType.getCode();
            }
            File files = new File(path, fileName);
            File parentFile = files.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            } else {
                if (appType != null) {
                    try {
                        File[] filesExist = parentFile.listFiles();
                        if (filesExist != null) {
                            for (File fileExist : filesExist) {
                                fileExist.delete();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            file.transferTo(files);
            File fileNew = new File(path, fileName);
            fileNew.setReadable(true, false);
//            fileNew.setExecutable(true, false);
            fileNew.setWritable(true, false);
            if (rootUrl.endsWith("/")) {
                if (appType != null) {
                    if (appType.isOtaUpdate()) {
                        filePath = rootUrl + appType.getCode();
                    } else {
                        filePath = rootUrl + appType.getCode() + "/" + fileName;
                    }
                } else {
                    filePath = rootUrl + fileName;
                }
            } else {
                if (appType != null) {
                    if (appType.isOtaUpdate()) {
                        filePath = rootUrl + appType.getCode();
                    } else {
                        filePath = rootUrl + appType.getCode() + "/" + fileName;
                    }
                } else {
                    filePath = rootUrl + "/" + fileName;
                }
            }
            if (appType != null) {
                if (appType.isOtaUpdate()) {
                    // OTA 生成update.xml文件
                    String md5 = getFileMd5(fileNew);
                    String fileNameXml = "update.xml";
                    File fileXml = new File(path, fileNameXml);
                    FileWriter fileWriter = new FileWriter(fileXml);
                    fileWriter.write("<update>\n");
                    fileWriter.write("<updatenode version=\"" + appType.getTypes()[0].replace("VHD ", "") + "\">\n");
                    fileWriter.write("<newVersion>" + versionName + "</newVersion>\n");
                    fileWriter.write("<md5>" + md5 + "</md5>\n");
                    fileWriter.write("<newurl>" + fileName + "</newurl>\n");
                    fileWriter.write("</updatenode>\n");
                    fileWriter.write("</update>\n");
                    fileWriter.flush();

                    fileWriter.close();
                }
            }
        } catch (IOException e) {
            throw e;
        }
        return filePath;
    }



    public static String saveMeetingFile(MultipartFile file, String savePath,String fileName) throws Exception {
        String filePath = "";
        if (file.isEmpty()) {
            throw new CustomException("文件为空");
        }
        String rootUrl = ExternalConfigCache.getInstance().getAppFileRootUrl();
        if (StringUtils.isEmpty(rootUrl)) {
            throw new CustomException("未配置文件存储地址");
        }
        try {

            // 获取保存路径
            File files = new File(savePath, fileName);
            File parentFile = files.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            file.transferTo(files);
            File fileNew = new File(savePath, fileName);
            fileNew.setReadable(true, false);
            fileNew.setWritable(true, false);
            if (rootUrl.endsWith("/")) {
                filePath = rootUrl + fileName;
            } else {
                filePath = rootUrl + "/" + fileName;
            }
        } catch (IOException e) {
            throw e;
        }
        return filePath;
    }


    private static String getSavePath() {
        String os = System.getProperty("os.name");
        if (os.contains("indows")) {
            return PathUtil.getRootPath();
        } else {
            return "/mnt/nfs/appupdate";
        }
    }

    public static String getFileMd5(File file) {
        String md5 = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[10240];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                messageDigest.update(buffer, 0, length);
            }
            md5 = new String(Hex.encodeHex(messageDigest.digest()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return md5;
    }


}
