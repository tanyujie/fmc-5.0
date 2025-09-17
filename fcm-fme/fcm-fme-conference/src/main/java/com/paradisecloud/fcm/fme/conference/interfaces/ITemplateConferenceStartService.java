/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ICallService.java
 * Package     : com.paradisecloud.fcm.fme.service.interfaces
 * @author lilinhai 
 * @since 2021-01-30 16:51
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.interfaces;

import org.springframework.transaction.annotation.Transactional;

import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;

/**  
 * <pre>活跃会议室业务处理类</pre>
 * @author lilinhai
 * @since 2021-01-30 16:51
 * @version V1.0  
 */
@Transactional
public interface ITemplateConferenceStartService
{
    
    /**
     * <pre>根据模板会议数据库ID启动会议</pre>
     * @author lilinhai
     * @since 2021-01-30 16:53 
     * @param templateConferenceId void
     */
    String startTemplateConference(long templateConferenceId);
    
    /**
     * <pre>根据模板会议数据库ID启动会议</pre>
     * @author lilinhai
     * @since 2021-01-30 16:53 
     * @param templateConferenceId void
     */
    void startConference(long templateConferenceId);
    
    /**
     * <pre>根据模板ID构建会议上下文对象</pre>
     * @author lilinhai
     * @since 2021-02-03 13:14 
     * @param templateConferenceId
     * @return ConferenceContext
     */
    ConferenceContext buildTemplateConferenceContext(long templateConferenceId);
}
