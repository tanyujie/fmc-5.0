/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ICallService.java
 * Package     : com.paradisecloud.sync.service.interfaces
 * @author lilinhai 
 * @since 2020-12-09 10:14
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.interfaces;

import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.cms.Call;

/**  
 * <pre>处理call的业务接口</pre>
 * @author lilinhai
 * @since 2020-12-09 10:14
 * @version V1.0  
 */
public interface ICallService
{
 
    /**
     * 通用创建call的方法
     * @author lilinhai
     * @since 2021-03-04 17:21 
     * @param deptId 
     * @param conferenceNumber
     * @param conferenceName
     * @return Call
     */
    Call createCall(Long deptId, String conferenceNumber, String conferenceName);
    
    /**
     * 创建Call
     * @author lilinhai
     * @since 2021-03-22 15:41 
     * @param fmeBridge
     * @param deptId
     * @param conferenceNumber
     * @param conferenceName
     * @return Call
     */
    Call createCall(FmeBridge fmeBridge, String conferenceNumber, String conferenceName);
    
    /**
     * <pre>同步MCU集群端的活跃会议室映射数据</pre>
     * @author lilinhai
     * @since 2020-12-08 17:28 
     * @param fmeBridge void
     */
    void syncCall(FmeBridge fmeBridge, CallConsumptionProcessor callConsumptionProcessor);
    
    /**
     * <pre>同步单个call</pre>
     * @author lilinhai
     * @since 2021-02-02 12:02 
     * @param fmeBridge
     * @param call void
     */
    void syncCall(FmeBridge fmeBridge, String call);
    
    public static interface CallConsumptionProcessor
    {
        void process(FmeBridge fmeBridge, Call call);
    }
}
