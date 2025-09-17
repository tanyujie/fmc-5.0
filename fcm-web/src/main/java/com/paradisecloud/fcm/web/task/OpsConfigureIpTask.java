package com.paradisecloud.fcm.web.task;

import com.paradisecloud.fcm.common.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 刘禧龙
 */
public class OpsConfigureIpTask extends Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpsConfigureIpTask.class);

    private String ip;

    public OpsConfigureIpTask(String id, long delayInMilliseconds, String ip) {
        super(id, delayInMilliseconds);
        this.ip = ip;
    }

    @Override
    public void run() {
        LOGGER.info("Ops初始化配置IP！");
        if (StringUtils.isNotEmpty(ip)) {

        }
    }
}
