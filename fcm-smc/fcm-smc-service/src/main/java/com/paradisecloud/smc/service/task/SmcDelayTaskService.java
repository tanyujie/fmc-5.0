package com.paradisecloud.smc.service.task;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;

@Component
public class SmcDelayTaskService {
    private SmcDelayTaskService taskService;
    private DelayQueue<SmcDelayTask> delayQueue =  new DelayQueue<>();

    @PostConstruct
    private void init() {
        taskService = this;
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                       SmcDelayTask task = delayQueue.take();
                        task.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void addTask(SmcDelayTask task){
        if(delayQueue.contains(task)){
            return;
        }
        delayQueue.add(task);
    }

    public void removeTask(SmcDelayTask task){
        delayQueue.remove(task);
    }

}