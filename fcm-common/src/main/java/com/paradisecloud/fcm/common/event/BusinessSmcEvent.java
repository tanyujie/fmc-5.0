package com.paradisecloud.fcm.common.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author nj
 * @date 2022/10/25 10:54
 */
public class BusinessSmcEvent extends ApplicationEvent {

    public BusinessSmcEvent(SmcEventModel smcEventModel) {
        super(smcEventModel);
    }

}
