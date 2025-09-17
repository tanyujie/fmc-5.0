package com.paradisecloud.fcm.common.core;

import com.paradisecloud.fcm.common.cache.CommonConfigCache;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/*
 * 共通初始化
 */
@Order(2)
@Component
public class InitCommon implements ApplicationRunner {

    @Value("${recordConfig.recordingFilesStorageSpaceMax}")
    private Double recordingFilesStorageSpaceMax;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        CommonConfigCache commonConfigCache = CommonConfigCache.getInstance();
        commonConfigCache.initCommonConfig();
        commonConfigCache.setRecordingFilesStorageSpaceMax(recordingFilesStorageSpaceMax);
        ExternalConfigCache.getInstance().initExternalConfig();
    }
}
