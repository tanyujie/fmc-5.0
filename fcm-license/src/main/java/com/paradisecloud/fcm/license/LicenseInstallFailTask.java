package com.paradisecloud.fcm.license;


import com.paradisecloud.fcm.terminal.fs.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.util.Objects;

/**
 * @author nj
 * @date 2022/8/3 10:55
 */
@Slf4j
public class LicenseInstallFailTask {


    public static void exec(int defaultLimit){
        try {
            Environment environment = SpringContextUtil.getApplicationContext().getEnvironment();
            String activeProfile = environment.getActiveProfiles()[0];
            if(!Objects.equals("prod",activeProfile)){
                return ;
            }

//            LicenseExecutor.streamerOrRecorderCmd("streamer.com", "streamer disable");
//            LicenseExecutor.streamerOrRecorderCmd("recorder.com", "recorder disable");
            LicenseExecutor.execParticipantLimit(defaultLimit);
       //     LicenseExecutor.callProfileLimitInit(defaultLimit);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}
