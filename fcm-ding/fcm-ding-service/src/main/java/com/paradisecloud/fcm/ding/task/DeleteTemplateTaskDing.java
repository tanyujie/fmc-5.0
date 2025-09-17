package com.paradisecloud.fcm.ding.task;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nj
 * @date 2023/5/15 10:03
 */
public class DeleteTemplateTaskDing extends DingDelayTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteTemplateTaskDing.class);

    private Long templateId;

    public DeleteTemplateTaskDing(String id, long delayInMilliseconds, Long templateId) {
        super(id, delayInMilliseconds);
        this.templateId = templateId;
    }

    @Override
    public void run() {

    }


}
