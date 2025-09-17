package com.paradisecloud.fcm.tencent.task;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nj
 * @date 2023/5/15 10:03
 */
public class DeleteTemplateTaskTencent extends TencentDelayTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteTemplateTaskTencent.class);

    private Long templateId;

    public DeleteTemplateTaskTencent(String id, long delayInMilliseconds, Long templateId) {
        super(id, delayInMilliseconds);
        this.templateId = templateId;
    }

    @Override
    public void run() {

    }


}
