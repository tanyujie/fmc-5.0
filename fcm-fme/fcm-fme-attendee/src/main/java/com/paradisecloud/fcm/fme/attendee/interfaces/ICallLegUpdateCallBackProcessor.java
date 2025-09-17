/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ICallLegUpdateCallBacker.java
 * Package     : com.paradisecloud.fcm.fme.attendee.interfaces
 * @author sinhy 
 * @since 2021-09-02 19:36
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.interfaces;

import java.util.Map;

import com.paradisecloud.fcm.service.conference.attendee.BaseFixedParamValue;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;

/**  
 * <pre>callleg更新回调</pre>
 * @author sinhy
 * @since 2021-09-02 19:36
 * @version V1.0  
 */
public interface ICallLegUpdateCallBackProcessor
{
    
    void process(Map<String, BaseFixedParamValue> fpvMap, FmeBridge fmeBridge, Participant participant);
}
