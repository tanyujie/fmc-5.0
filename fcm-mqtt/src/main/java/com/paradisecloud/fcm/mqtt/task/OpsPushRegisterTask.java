package com.paradisecloud.fcm.mqtt.task;

import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.dao.model.BusiOps;
import com.paradisecloud.fcm.mqtt.interfaces.IOpsActionService;
import com.paradisecloud.fcm.ops.cloud.cache.OpsCache;
import com.sinhy.spring.BeanFactory;

public class OpsPushRegisterTask extends Task {

    private long opsId;

    public OpsPushRegisterTask(String id, long delayInMilliseconds, long opsId) {
        super("ops_push_register_" + id, delayInMilliseconds);
        this.opsId = opsId;
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        BusiOps busiOps = OpsCache.getInstance().get(opsId);
        if (busiOps != null) {
            IOpsActionService opsActionService = BeanFactory.getBean(IOpsActionService.class);
            opsActionService.pushRegister(busiOps.getSn());
        }
    }
}
