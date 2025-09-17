/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : LayoutUtils.java
 * Package     : com.paradisecloud.fcm.fme.attendee.utils
 * @author sinhy 
 * @since 2021-08-13 15:46
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.utils;

import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.dao.model.BusiTerminalMeetingJoinSettings;
import com.paradisecloud.fcm.fme.attendee.model.operation.PollingAttendeeOpreationImpl;
import com.paradisecloud.fcm.fme.cache.FmeDataCache;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.AutomaticSplitScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.OneSplitScreen;
import com.paradisecloud.fcm.fme.model.busi.operation.DefaultViewOperation;
import com.paradisecloud.fcm.fme.model.cms.CallLegProfile;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;

/**  
 * <pre>布局获取工具类</pre>
 * @author sinhy
 * @since 2021-08-13 15:46
 * @version V1.0  
 */
public abstract class LayoutUtils
{
    
    /**
     * 获取会议正确的布局方式
     * @author sinhy
     * @since 2021-08-13 15:48 
     * @param conferenceContext
     * @return String
     */
    public static String getRightLayout(ConferenceContext conferenceContext)
    {
        return getRightLayout(conferenceContext, null);
    }
    
    /**
     * 获取会议正确的布局方式
     * @author sinhy
     * @since 2021-08-13 15:48 
     * @param conferenceContext
     * @return String
     */
    public static String getRightLayout(ConferenceContext conferenceContext, BusiTerminalMeetingJoinSettings busiTerminalMeetingJoinSettings)
    {
        String defaultLayout = null;
        boolean f = false;
        
        // 会议中操作的布局是优先级最高布局
        if (conferenceContext.getAttendeeOperation() != null)
        {
            if (conferenceContext.getAttendeeOperation() instanceof DefaultViewOperation)
            {
                DefaultViewOperation defaultAttendeeOperation = (DefaultViewOperation) conferenceContext.getAttendeeOperation();
                f = YesOrNo.convert(defaultAttendeeOperation.getDefaultViewIsBroadcast()) == YesOrNo.YES;
                if (f || defaultAttendeeOperation.getDefaultViewLayout().equals(AutomaticSplitScreen.LAYOUT))
                {
                    defaultLayout = AutomaticSplitScreen.LAYOUT;
                }
                else
                {
                    defaultLayout = OneSplitScreen.LAYOUT;
                }
            }
            else if (conferenceContext.getAttendeeOperation() instanceof PollingAttendeeOpreationImpl)
            {
                PollingAttendeeOpreationImpl pollingAttendeeOpreation = (PollingAttendeeOpreationImpl) conferenceContext.getAttendeeOperation();
                f = pollingAttendeeOpreation.getPollingScheme().getIsBroadcast() == YesOrNo.YES;
                if (f)
                {
                    defaultLayout = pollingAttendeeOpreation.getPollingScheme().getLayout();
                }
            }
        }
        
        // 如果会议中的布局为空，则用终端设置的布局
        if (defaultLayout == null)
        {
            if (busiTerminalMeetingJoinSettings != null && !ObjectUtils.isEmpty(busiTerminalMeetingJoinSettings.getDefaultLayout()))
            {
                defaultLayout = busiTerminalMeetingJoinSettings.getDefaultLayout();
            }
        }
        
        if (defaultLayout == null)
        {
            CoSpace coSpace = FmeDataCache.getCoSpaceByConferenceNumber(conferenceContext.getDeptId(), conferenceContext.getConferenceNumber());
            if (!ObjectUtils.isEmpty(coSpace.getDefaultLayout()))
            {
                defaultLayout = coSpace.getDefaultLayout();
            }
        }
        
        if (defaultLayout == null)
        {
            CallLegProfile callLegProfile = conferenceContext.getCallLegProfile();
            if (!ObjectUtils.isEmpty(callLegProfile.getDefaultLayout()))
            {
                defaultLayout = callLegProfile.getDefaultLayout();
            }
        }
        
        if (defaultLayout == null)
        {
            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().isOnline())
            {
                defaultLayout = OneSplitScreen.LAYOUT;
            }
            else
            {
                defaultLayout = AutomaticSplitScreen.LAYOUT;
            }
        }
        return defaultLayout;
    }
}
