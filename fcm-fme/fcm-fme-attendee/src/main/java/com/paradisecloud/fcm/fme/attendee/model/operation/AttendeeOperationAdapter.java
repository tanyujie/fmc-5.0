/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeOperationAdapter.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.operation
 * @author lilinhai 
 * @since 2021-04-12 16:56
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.operation;

import java.util.List;

import com.paradisecloud.fcm.common.enumer.PanePlacementSelfPaneMode;
import com.paradisecloud.fcm.fme.apiservice.interfaces.ICoSpaceService;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeLayoutService;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeLayoutSetMode;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.layout.SplitScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.InheritSplitScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.OneSplitScreen;
import com.paradisecloud.fcm.fme.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.fme.model.parambuilder.CoSpaceParamBuilder;
import com.sinhy.spring.BeanFactory;

/**  
 * <pre>请加上该类的描述</pre>
 * @author lilinhai
 * @since 2021-04-12 16:56
 * @version V1.0  
 */
public abstract class AttendeeOperationAdapter extends AttendeeOperation
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-04-12 16:56 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-04-12 16:56 
     * @param conferenceContext 
     */
    protected AttendeeOperationAdapter(ConferenceContext conferenceContext)
    {
        super(conferenceContext);
    }

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-04-12 17:00 
     * @param conferenceContext
     * @param splitScreen
     * @param attendees 
     */
    protected AttendeeOperationAdapter(ConferenceContext conferenceContext, SplitScreen splitScreen, List<Attendee> attendees)
    {
        super(conferenceContext, splitScreen, attendees);
    }

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-04-12 17:00 
     * @param conferenceContext
     * @param splitScreen 
     */
    protected AttendeeOperationAdapter(ConferenceContext conferenceContext, SplitScreen splitScreen)
    {
        super(conferenceContext, splitScreen);
    }
    
    /**
     * <pre>单分屏初始化</pre>
     * @author lilinhai
     * @since 2021-04-09 18:00  void
     */
    protected void initOneSplitScreen()
    {
        initSplitScreen(OneSplitScreen.LAYOUT);
    }
    
    /**
     * <pre>单分屏初始化</pre>
     * @author lilinhai
     * @since 2021-04-09 18:00  void
     */
    protected void initSplitScreen(String layout)
    {
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceContext(conferenceContext);
        CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(conferenceContext.getConferenceNumber());
        CoSpaceParamBuilder coSpaceParamBuilder = new CoSpaceParamBuilder();
        
        // 设置窗格最高权重值
        coSpaceParamBuilder.panePlacementHighestImportance();
        
        // 设置是否显示自己
        coSpaceParamBuilder.panePlacementSelfPaneMode(PanePlacementSelfPaneMode.OFF.getStringValue());
        coSpaceParamBuilder.defaultLayout(layout);
        
        // 更新CoSpace缓存
        BeanFactory.getBean(ICoSpaceService.class).updateCoSpace(fmeBridge, coSpace, coSpaceParamBuilder);
        
        // 设置所有参会终端的布局，为指定的布局（单分屏或多分频）
        BeanFactory.getBean(IAttendeeLayoutService.class).setAttendeeLayout(conferenceContext, InheritSplitScreen.LAYOUT, AttendeeLayoutSetMode.ALL);
    }
}
