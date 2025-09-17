package com.paradisecloud.fcm.smc2.task;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;

@Component
public class Smc2DelayTaskService {
    private DelayQueue<Smc2DelayTask> delayQueue =  new DelayQueue<Smc2DelayTask>();

    @PostConstruct
    private void init() {

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Smc2DelayTask task = delayQueue.take();
                        task.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void addTask(Smc2DelayTask task){
        if(delayQueue.contains(task)){
            return;
        }
        delayQueue.add(task);
    }

    public void removeTask(Smc2DelayTask task){
        delayQueue.remove(task);
    }

}