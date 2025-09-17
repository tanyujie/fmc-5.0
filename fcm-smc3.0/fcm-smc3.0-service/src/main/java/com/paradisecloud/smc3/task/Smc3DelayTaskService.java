package com.paradisecloud.smc3.task;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;

@Component
public class Smc3DelayTaskService {
    private DelayQueue<Smc3DelayTask> delayQueue =  new DelayQueue<Smc3DelayTask>();

    @PostConstruct
    private void init() {

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Smc3DelayTask task = delayQueue.take();
                        task.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void addTask(Smc3DelayTask task){
        if(delayQueue.contains(task)){
            return;
        }
        delayQueue.add(task);
    }

    public void removeTask(Smc3DelayTask task){
        delayQueue.remove(task);
    }

}