package com.paradisecloud.fcm.web.task;

import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.fme.attendee.utils.ConferenceContextUtils;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author nj
 * @date 2024/7/10
 */
@Component
public class ConferenceMotionCaputureThread extends Thread implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private TaskService taskService;

    @Override
    public void run() {

        while (true) {
            try {
                Collection<ConferenceContext> values = ConferenceContextCache.getInstance().values();
                if (CollectionUtils.isNotEmpty(values)) {
                    for (ConferenceContext conferenceContext : values) {
                        if (conferenceContext != null && conferenceContext.isStart()) {
                            // 动作捕捉
                            if (conferenceContext.isMotionCapture()) {
                                AtomicInteger atomicInteger = new AtomicInteger();
                                ConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
                                    int num = atomicInteger.getAndIncrement();
                                    long delayInMilliseconds = 2000 * num;
                                    QwenActionCheckTask qwenActionCheckTask = new QwenActionCheckTask(a.getId(), delayInMilliseconds, conferenceContext.getId(), a.getId());
                                    taskService.addTask(qwenActionCheckTask);
                                });
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Threads.sleep(1000 * 30);
            }

        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}