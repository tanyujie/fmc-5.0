/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DefaultViewOperation.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.operation
 * @author sinhy 
 * @since 2021-08-20 22:03
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.kdc.model.busi.operation;


import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;

/**
 * <pre>请加上该类的描述</pre>
 * @author sinhy
 * @since 2021-08-20 22:03
 * @version V1.0  
 */
public abstract class DefaultViewOperation extends AttendeeOperation
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-08-20 22:03
     */
    private static final long serialVersionUID = 1L;

    /**
     * <pre>构造方法</pre>
     * @author sinhy
     * @since 2021-08-20 22:03
     * @param conferenceContext
     */
    protected DefaultViewOperation(McuKdcConferenceContext conferenceContext)
    {
        super(conferenceContext);
    }

    /** 
     * 默认视图布局类型 
     */
    protected String defaultViewLayout;

    /** 
     * 默认视图是否广播(1是，2否) 
     */
    protected volatile Integer defaultViewIsBroadcast;

    /** 
     * 默认视图是否显示自己 
     */
    protected volatile Integer defaultViewIsDisplaySelf;

    /**
     * 默认视图是否补位 
     */
    protected Integer defaultViewIsFill;
    
    /**
     * 默认视图轮询时间间隔
     */
    protected Integer defaultViewPollingInterval;
    
    /**
     * <p>Get Method   :   defaultViewLayout String</p>
     * @return defaultViewLayout
     */
    public String getDefaultViewLayout()
    {
        return defaultViewLayout;
    }

    /**
     * <p>Set Method   :   defaultViewLayout String</p>
     * @param defaultViewLayout
     */
    public void setDefaultViewLayout(String defaultViewLayout)
    {
        this.defaultViewLayout = defaultViewLayout;
    }

    /**
     * <p>Get Method   :   defaultViewIsBroadcast Integer</p>
     * @return defaultViewIsBroadcast
     */
    public Integer getDefaultViewIsBroadcast()
    {
        return defaultViewIsBroadcast;
    }

    /**
     * <p>Set Method   :   defaultViewIsBroadcast Integer</p>
     * @param defaultViewIsBroadcast
     */
    public void setDefaultViewIsBroadcast(Integer defaultViewIsBroadcast)
    {
        this.defaultViewIsBroadcast = defaultViewIsBroadcast;
    }

    /**
     * <p>Get Method   :   defaultViewIsDisplaySelf Integer</p>
     * @return defaultViewIsDisplaySelf
     */
    public Integer getDefaultViewIsDisplaySelf()
    {
        return defaultViewIsDisplaySelf;
    }

    /**
     * <p>Set Method   :   defaultViewIsDisplaySelf Integer</p>
     * @param defaultViewIsDisplaySelf
     */
    public void setDefaultViewIsDisplaySelf(Integer defaultViewIsDisplaySelf)
    {
        this.defaultViewIsDisplaySelf = defaultViewIsDisplaySelf;
    }

    /**
     * <p>Get Method   :   defaultViewIsFill Integer</p>
     * @return defaultViewIsFill
     */
    public Integer getDefaultViewIsFill()
    {
        return defaultViewIsFill;
    }

    /**
     * <p>Set Method   :   defaultViewIsFill Integer</p>
     * @param defaultViewIsFill
     */
    public void setDefaultViewIsFill(Integer defaultViewIsFill)
    {
        this.defaultViewIsFill = defaultViewIsFill;
    }

    /**
     * <p>Get Method   :   defaultViewPollingInterval Integer</p>
     * @return defaultViewPollingInterval
     */
    public Integer getDefaultViewPollingInterval()
    {
        return defaultViewPollingInterval;
    }

    /**
     * <p>Set Method   :   defaultViewPollingInterval Integer</p>
     * @param defaultViewPollingInterval
     */
    public void setDefaultViewPollingInterval(Integer defaultViewPollingInterval)
    {
        this.defaultViewPollingInterval = defaultViewPollingInterval;
    }
}
