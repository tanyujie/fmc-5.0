package com.paradisecloud.fcm.web.controller.smc;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.utils.PathUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author nj
 * @date 2023/4/25 9:51
 */
@Slf4j
@RestController
@RequestMapping("/smc/license/info")
@Tag(name = "【请填写功能名称】")
public class LicenseSmcController extends BaseController {

    /**
     * smc license文件的上传
     *
     * @param uploadFile
     * @throws Exception
     */
    @PostMapping("/licenseFilesUpload")
    public RestResponse smcLicenseFilesUpload(@RequestParam(value = "uploadFile")MultipartFile uploadFile) throws Exception {
        if (uploadFile != null && !uploadFile.isEmpty()) {
            // 保存
            try {
                String url = saveFile(uploadFile);
            } catch (Exception e) {
                if (e instanceof CustomException) {
                    return RestResponse.fail(e.getMessage());
                } else {
                    return RestResponse.fail("证书文件上传错误");
                }
            }
        }
        return RestResponse.fail();
    }


    public static final String WINDOWS = "windows";

    public static String saveFile(MultipartFile file) throws Exception {
        String filePath = "";
        if (file.isEmpty()) {
            throw new CustomException("文件为空");
        }

        // 给文件重命名
        String fileName = file.getOriginalFilename();
        try {
            // 获取保存路径
            String path = getSavePath();
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
