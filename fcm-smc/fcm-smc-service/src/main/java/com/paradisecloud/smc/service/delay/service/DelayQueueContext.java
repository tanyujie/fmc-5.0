package com.paradisecloud.smc.service.delay.service;


import com.paradisecloud.smc.service.delay.business.BusinessDelayQueueService;
import com.paradisecloud.smc.service.delay.item.DelayValues;
import com.paradisecloud.smc.service.delay.item.DelayedItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;

/**
 * 队列上下文
 * @author nj
 *
 */
@Slf4j
@Component
public class DelayQueueContext implements ApplicationContextAware {

    private DelayQueue<DelayedItem<DelayValues>> queue = new DelayQueue<>();

    private ConcurrentHashMap<String, DelayValues> delayMap = new ConcurrentHashMap<>();

    public static Map<String, DelayService> delayServiceMap = new HashMap<>();

    public static BusinessDelayQueueService businessDelayQueueService = null;

    private static DelayQueueContext context = null;

    private DelayQueueContext() {

    }

    public static DelayQueueContext instance() {
        if (context == null) {
            synchronized (DelayQueueContext.class) {
                if (context == null) {
                    context = new DelayQueueContext();
                    context.createDelayThread();
                }
            }
        }
        return context;
    }

    private void createDelayThread() {
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 2, 5,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                executeDelay();
            }
        });
    }



    public void put(DelayValues v) {
        put(v.getId(), v, true);
    }

    public void put(String k, DelayValues v) {
        put(k, v, true);
    }

    /**
     * 添加队列
     *
     * @param k
     * @param v
     * @param hasAdd 是否添加到数据库
     */
    public void put(String k, DelayValues v, boolean hasAdd) {
        DelayValues v2 = delayMap.put(k, v);
        DelayedItem<DelayValues> tmpItem = new DelayedItem<>(v, v.getExpireTime());
        if (!ObjectUtils.isEmpty(v2)) {
            queue.remove(tmpItem);
        }
        //添加到数据库（id,对象，移除时间）
        if (hasAdd) {
            businessDelayQueueService.add(v);
        }
        queue.offer(tmpItem);
    }

    public void destory() {
        queue = null;
        delayMap = null;
    }

    private void executeDelay() {
        while (true) {
            try {
                DelayedItem<DelayValues> delayedItem = queue.take();
                if (!ObjectUtils.isEmpty(delayedItem)) {
                    DelayValues delayValue = delayedItem.getItem();
                    String delayType = delayValue.getType().toString();
                    DelayService delayService = delayServiceMap.get(delayType);
                    if (Objects.nonNull(delayService)) {
                        boolean executeResult = delayService.execute(delayValue.getBusinessId());
                        if (executeResult) {
                            if (delayMap.contains(delayedItem.getItem())) {
                                delayMap.remove(delayedItem.getItem());
                            }
                        }
                        //移除数据库数据
                        businessDelayQueueService.remove(delayValue);
                    }
                }
                LockSupport.parkNanos(300);
            } catch (Exception e) {
                log.error("延迟队列异常", e);
            }
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        initService(applicationContext);
    }

    private void initService(ApplicationContext applicationContext) {
        Map<String, DelayService> beansOfType = applicationContext.getBeansOfType(DelayService.class);
        beansOfType.entrySet().forEach(p -> {
            if (!delayServiceMap.containsKey(p)) {
                delayServiceMap.put(p.getKey(), p.getValue());
            }
        });
        businessDelayQueueService = applicationContext.getBean("businessDelayQueueService", BusinessDelayQueueService.class);
    }
}
