package com.paradisecloud.fcm.huaweicloud.huaweicloud.task;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;

@Component
public class HwcloudDelayTaskService {
    private DelayQueue<HwcloudDelayTask> delayQueue =  new DelayQueue<HwcloudDelayTask>();

    @PostConstruct
    private void init() {

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        HwcloudDelayTask task = delayQueue.take();
                        task.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void addTask(HwcloudDelayTask task){
        if(delayQueue.contains(task)){
            return;
        }
        delayQueue.add(task);
    }

    public void removeTask(HwcloudDelayTask task){
        delayQueue.remove(task);
    }

}