/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : IVideoConferenceService.java
 * Package     : com.paradiscloud.fcm.videosdk.service.interfaces
 * @author sinhy 
 * @since 2021-10-28 10:51
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.sdk.video.service.interfaces;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.common.enumer.RaiseHandStatus;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;

/**  
 * <pre>云视讯对外接口</pre>
 * @author sinhy
 * @since 2021-10-28 10:51
 * @version V1.0  
 */
public interface IVideoConferenceSDKService
{
    
    /**
     * 根据会议号获取会议详情
     * @author sinhy
     * @since 2021-11-23 16:33 
     * @param conferenceNumber
     * @return String
     */
    String getConferenceInfo(String conferenceNumber);
    
    /**
     * 根据终端SN分页获取包含该终端的会议列表
     * @author sinhy
     * @since 2021-10-28 10:57 
     * @param sn
     * @param page
     * @param size
     * @return PaginationData<JSONObject>
     */
    String getVideoConference(String sn, Integer page, Integer size);
    
    /**
     * 点名：**+会场数字
     * @author sinhy
     * @since 2021-11-24 17:15 
     * @param conferenceContext
     * @param number
     * @return RestResponse
     */
    RestResponse callTheRoll(ConferenceContext conferenceContext, int number);
    
    /**
     * 对话
     * @author sinhy
     * @since 2021-12-09 15:41 
     * @param conferenceContext
     * @param number
     * @return RestResponse
     */
    RestResponse talk(ConferenceContext conferenceContext, int number);
    
    /**
     * 显示布局(广播)：*3分屏数*窗格模式
     * @author sinhy
     * @since 2021-11-24 17:15 
     * @param conferenceContext
     * @param layout
     * @return RestResponse
     */
    RestResponse displayLayoutGB(ConferenceContext conferenceContext, String layout, int mode);
    
    /**
     * 显示布局(选看)：*2分屏数*窗格模式
     * @author sinhy
     * @since 2021-11-24 17:15 
     * @param conferenceContext
     * @param layout
     * @return RestResponse
     */
    RestResponse displayLayoutXK(ConferenceContext conferenceContext, String layout, int mode);
    
    /**
     * 轮询（广播）：*4分屏数*窗格模式
     * @author sinhy
     * @since 2021-11-24 17:15 
     * @param conferenceContext
     * @param layout
     * @return RestResponse
     */
    RestResponse pollingGB(ConferenceContext conferenceContext, String layout, int mode);
    
    /**
     * 轮询（选看）：*5分屏数*窗格模式
     * @author sinhy
     * @since 2021-11-24 17:15 
     * @param conferenceContext
     * @param layout
     * @return RestResponse
     */
    RestResponse pollingXK(ConferenceContext conferenceContext, String layout, int mode);
    
    /**
     * 举手
     * @author sinhy
     * @since 2021-12-07 10:29 
     * @param conferenceContext
     * @param attendeeId
     * @param raiseHandStatus
     * @return RestResponse
     */
    RestResponse raiseHand(ConferenceContext conferenceContext, String attendeeId, RaiseHandStatus raiseHandStatus);
    
    /**
     * 讨论
     * @author sinhy
     * @since 2021-11-24 17:15 
     * @return RestResponse
     */
    RestResponse discuss(ConferenceContext conferenceContext);
    
    /**
     * 回到显示布局
     * @author sinhy
     * @since 2021-11-24 17:15 
     * @return RestResponse
     */
    RestResponse backToDisplayLayout(ConferenceContext conferenceContext);
}
