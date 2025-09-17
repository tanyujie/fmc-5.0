package com.paradisecloud.fcm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Order(1)
@Component
public class Init implements ApplicationRunner {

    protected final Logger log = LoggerFactory.getLogger(Init.class);

    private final String uploadUrl = "/home/upload";
    private final String mtrUrl = "/home/mtr";
    private final String appupdateUrl = "/mnt/nfs/appupdate";
    private final String certUrl = "/home/fcm/fcm-application/lib";
    private final String downloadsUrl = "/mnt/nfs/downloads";
    private final String spacesUrl = "/mnt/nfs/spaces";
    private final String snapshotUrl = "/mnt/nfs/snapshot";
    private final String callBrandingUrl = "/mnt/nfs/callBranding";
    private final String ossUrl = "/mnt/nfs/oss";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Init Check！");

        fileCheck();
    }

    /**
     * 检查文件
     */
    private void fileCheck() {
        log.info("fileCheck!");
        List<String> urlList = new ArrayList<>();
        urlList.add(uploadUrl);
        urlList.add(mtrUrl);
        urlList.add(appupdateUrl);
        urlList.add(certUrl);
        urlList.add(downloadsUrl);
        urlList.add(spacesUrl);
        urlList.add(snapshotUrl);
        urlList.add(callBrandingUrl);
        urlList.add(ossUrl);

        String os = System.getProperty("os.name");
        if (os.contains("Windows")) {
            return;
        } else {
            for (String url : urlList) {
                File file = new File(url);
                if (file.exists() && file.isDirectory()) {
                    file.setWritable(true, false);
                    file.setReadable(true, false);
                    log.info("文件url ===> " + url);
                } else {
                    boolean mkdir = file.mkdir();
                    if (mkdir) {
                        file.setWritable(true, false);
                        file.setReadable(true, false);
                    }
                    boolean exists = file.exists();
                    log.info("文件url ===> " + exists);
                }
            }
        }
        log.info("fileCheck Over！");
    }
}
