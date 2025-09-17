package com.paradisecloud.fcm.web.task;

import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.mqtt.interfaces.ITerminalActionService;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendServerInfoTask extends Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendServerInfoTask.class);

    private long oldTime;

    public SendServerInfoTask(String id, long delayInMilliseconds, long oldTime) {
        super(id, delayInMilliseconds);
        this.oldTime = oldTime;
    }


    @Override
    public void run() {
        LOGGER.info("服务器信息推送开始。ID:" + getId());

        long newTime = System.currentTimeMillis();
        if (newTime - oldTime < -30000 || newTime - oldTime > 30000) {
            ITerminalActionService terminalActionService = BeanFactory.getBean(ITerminalActionService.class);
            terminalActionService.sendServerInfo();
        }
    }
}
