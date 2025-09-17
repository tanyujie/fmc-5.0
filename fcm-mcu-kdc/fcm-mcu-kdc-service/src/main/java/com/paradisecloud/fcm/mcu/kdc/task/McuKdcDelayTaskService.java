package com.paradisecloud.fcm.mcu.kdc.task;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;

@Component
public class McuKdcDelayTaskService {
    private McuKdcDelayTaskService taskService;
    private DelayQueue<DelayTask> delayQueue =  new DelayQueue<DelayTask>();

    @PostConstruct
    private void init() {
        taskService = this;

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        DelayTask task = delayQueue.take();
                        task.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void addTask(DelayTask task){
        if(delayQueue.contains(task)){
            return;
        }
        delayQueue.add(task);
    }

    public void removeTask(DelayTask task){
        delayQueue.remove(task);
    }

}