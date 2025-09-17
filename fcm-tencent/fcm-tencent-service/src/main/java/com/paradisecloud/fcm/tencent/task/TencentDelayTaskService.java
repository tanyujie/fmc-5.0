package com.paradisecloud.fcm.tencent.task;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;

@Component
public class TencentDelayTaskService {
    private DelayQueue<TencentDelayTask> delayQueue =  new DelayQueue<TencentDelayTask>();

    @PostConstruct
    private void init() {

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        TencentDelayTask task = delayQueue.take();
                        task.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void addTask(TencentDelayTask task){
        if(delayQueue.contains(task)){
            return;
        }
        delayQueue.add(task);
    }

    public void removeTask(TencentDelayTask task){
        delayQueue.remove(task);
    }

}