package com.paradisecloud.fcm.common.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author nj
 * @date 2022/10/25 11:04
 */
@Component
public class PublisherCustomsEvent {

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public void publisherEvent(ApplicationEvent applicationEvent){
        applicationEventPublisher.publishEvent(applicationEvent);
    }

}
