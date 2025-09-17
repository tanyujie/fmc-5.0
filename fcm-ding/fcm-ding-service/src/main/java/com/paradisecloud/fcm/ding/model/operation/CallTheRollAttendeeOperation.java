/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CallTheRollOperation.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.operation
 * @author lilinhai
 * @since 2021-02-20 16:42
 * @version  V1.0
 */
package com.paradisecloud.fcm.ding.model.operation;

import com.paradisecloud.fcm.ding.busi.attende.AttendeeDing;
import com.paradisecloud.fcm.ding.cache.DingConferenceContext;



/**
 * <pre>点名与会者操作</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-20 16:42
 */
public class CallTheRollAttendeeOperation extends AttendeeOperation {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     *
     * @since 2021-02-22 12:48
     */
    private static final long serialVersionUID = 1L;
    private AttendeeDing callTheRollAttendee;



    public CallTheRollAttendeeOperation(DingConferenceContext conferenceContext, AttendeeDing attendee) {
        super(conferenceContext);
        this.callTheRollAttendee = attendee;
    }

    @Override
    public void operate() {


    }




    @Override
    public void cancel() {

    }


}
