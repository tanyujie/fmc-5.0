package com.paradisecloud.fcm.mqtt.task;

import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.dao.model.BusiClient;
import com.paradisecloud.fcm.mqtt.interfaces.IClientActionService;
import com.paradisecloud.fcm.ops.cloud.cache.ClientCache;
import com.sinhy.spring.BeanFactory;

public class ClientPushRegisterTask extends Task {

    private long clientId;

    public ClientPushRegisterTask(String id, long delayInMilliseconds, long clientId) {
        super("client_push_register_" + id, delayInMilliseconds);
        this.clientId = clientId;
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
        BusiClient busiClient = ClientCache.getInstance().get(clientId);
        if (busiClient != null) {
            IClientActionService clientActionService = BeanFactory.getBean(IClientActionService.class);
            clientActionService.pushRegister(busiClient.getSn());
        }
    }
}
