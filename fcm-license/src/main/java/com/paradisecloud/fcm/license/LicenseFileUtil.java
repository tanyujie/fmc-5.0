package com.paradisecloud.fcm.license;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.utils.PathUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author nj
 * @date 2022/7/29 11:48
 */
public class LicenseFileUtil {

    public static final String WINDOWS = "windows";

    public static String saveFile(MultipartFile file) throws Exception {
        String filePath = "";
        if (file.isEmpty()) {
            throw new CustomException("文件为空");
        }

        // 给文件重命名
        String fileName ="license.lic";
      // fileName="license.lic";
        try {
            // 获取保存路径
            String path = getSavePath();
            File fileold = new File(path+"/license.lic");
            if(fileold.exists()){
                fileold.delete();
            }
            File files = new File(path, fileName);
            File parentFile = files.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdir();
            }
            file.transferTo(files);
            File fileNew = new File(path, fileName);
            fileNew.setReadable(true, false);
            fileNew.setExecutable(true, false);
            fileNew.setWritable(true, false);
            filePath=fileNew.getPath();
        } catch (IOException e) {
            throw e;
        }
        return filePath;
    }

    public static String getSavePath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains(WINDOWS)) {
            return PathUtil.getRootPath();
        } else {
            return "/home/fcm/fcm-application/lib";
        }
    }
}
