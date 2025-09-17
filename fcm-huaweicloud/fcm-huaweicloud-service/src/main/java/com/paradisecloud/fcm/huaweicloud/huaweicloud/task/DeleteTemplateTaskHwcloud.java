package com.paradisecloud.fcm.huaweicloud.huaweicloud.task;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nj
 * @date 2023/5/15 10:03
 */
public class DeleteTemplateTaskHwcloud extends HwcloudDelayTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteTemplateTaskHwcloud.class);

    private Long templateId;

    public DeleteTemplateTaskHwcloud(String id, long delayInMilliseconds, Long templateId) {
        super(id, delayInMilliseconds);
        this.templateId = templateId;
    }

    @Override
    public void run() {

    }


}
