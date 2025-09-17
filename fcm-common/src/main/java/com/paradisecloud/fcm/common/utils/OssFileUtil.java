package com.paradisecloud.fcm.common.utils;

import com.paradisecloud.common.exception.CustomException;
import com.sinhy.utils.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class OssFileUtil {

    public static String saveFile(MultipartFile file) throws Exception {
        String filePath = "";
        if (file.isEmpty()) {
            throw new CustomException("文件为空");
        }
        try {
            // 给文件重命名

            String fileName = Base64Utils.encode(UUID.randomUUID() + file.getOriginalFilename());
            if (file.getOriginalFilename().contains(".")) {
                fileName = fileName + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            }
            // 获取保存路径
            String path = getSavePath();
            File files = new File(path, fileName);
            File parentFile = files.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            file.transferTo(files);
            File fileNew = new File(path, fileName);
            fileNew.setReadable(true, false);
            fileNew.setWritable(true, false);
            String ossPath = getOssPath();
            if (ossPath.endsWith("/")) {
                filePath = ossPath + fileName;
            } else {
                filePath = ossPath + "/" + fileName;
            }
        } catch (IOException e) {
            throw e;
        }
        return filePath;
    }


    private static String getSavePath() {
        String os = System.getProperty("os.name");
        if (os.contains("indows")) {
            return PathUtil.getRootPath() + "/oss";
        } else {
            return "/mnt/nfs/oss";
        }
    }

    public static String getOssPath() {
        return "/oss";
    }

}
