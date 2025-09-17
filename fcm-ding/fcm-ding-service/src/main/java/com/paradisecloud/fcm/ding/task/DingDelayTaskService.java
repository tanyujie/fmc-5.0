package com.paradisecloud.fcm.ding.task;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;

@Component
public class DingDelayTaskService {
    private  DelayQueue<DingDelayTask> delayQueue =  new DelayQueue<DingDelayTask>();

    @PostConstruct
    private void init() {

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        DingDelayTask task = delayQueue.take();
                        task.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void addTask(DingDelayTask task){
        if(delayQueue.contains(task)){
            return;
        }
        delayQueue.add(task);
    }

    public void removeTask(DingDelayTask task){
        delayQueue.remove(task);
    }

}