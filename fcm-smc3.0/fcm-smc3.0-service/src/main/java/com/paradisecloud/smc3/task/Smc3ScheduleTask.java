package com.paradisecloud.smc3.task;

import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务类
 */
@Component
public class Smc3ScheduleTask {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 每5分钟启动删除SMC3模板任务
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void checkConferenceThread() {
        logger.info("删除SMC3模板任务");

        Smc3DeleteConferenceTemplateTask smc3DeleteConferenceTemplateTask = new Smc3DeleteConferenceTemplateTask("1", 5000);
        BeanFactory.getBean(Smc3DelayTaskService.class).addTask(smc3DeleteConferenceTemplateTask);
    }
}
