/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CurrentHandler.java
 * Package     : com.paradisecloud.fcm.fme.model.busi
 * @author lilinhai 
 * @since 2021-02-20 16:37
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.zte.model.busi.operation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paradisecloud.fcm.service.conference.operation.BaseAttendeeOperation;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.model.busi.attendee.AttendeeForMcuZte;
import com.paradisecloud.fcm.zte.model.busi.layout.SplitScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**  
 * <pre>当前正在进行的操作</pre>
 * @author lilinhai
 * @since 2021-02-20 16:37
 * @version V1.0  
 */
public abstract class AttendeeOperation extends BaseAttendeeOperation implements Serializable
{
    
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-22 12:48 
     */
    private static final long serialVersionUID = 1L;
    
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 发起点名操作操作员所属的会议上下文
     */
    @JsonIgnore
    protected volatile McuZteConferenceContext conferenceContext;
    
    /**
     * 分屏
     */
    protected SplitScreen splitScreen;
    
    /**
     * 分屏需要渲染的参会者，该集合不能大于分屏数
     */
    protected volatile List<AttendeeForMcuZte> attendees;
    
    /**
     * 标记是否取消
     */
    @JsonIgnore
    protected volatile boolean isCancel;
    
    /**
     * 下一个即将被执行的参会操作
     */
    @JsonIgnore
    protected AttendeeOperation nextAttendeeOperation;
    
    @JsonIgnore
    private boolean pushOperatedAttendeesToMonitor = true;
    
    @JsonIgnore
    protected List<AttendeeForMcuZte> operatedAttendees = new ArrayList<>();
    
    private long operationCreateTime = System.currentTimeMillis();
    private volatile long lastUpdateTime = 0;
    private volatile boolean forceUpdateView = false;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-22 13:47 
     * @param conferenceContext
     */
    protected AttendeeOperation(McuZteConferenceContext conferenceContext)
    {
        super();
        this.conferenceContext = conferenceContext;
    }
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-22 13:47 
     * @param conferenceContext
     * @param splitScreen 
     */
    protected AttendeeOperation(McuZteConferenceContext conferenceContext, SplitScreen splitScreen)
    {
        this(conferenceContext);
        this.splitScreen = splitScreen;
    }

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-22 13:22 
     * @param conferenceContext
     * @param splitScreen
     * @param attendees 
     */
    protected AttendeeOperation(McuZteConferenceContext conferenceContext, SplitScreen splitScreen, List<AttendeeForMcuZte> attendees)
    {
        this(conferenceContext, splitScreen);
        this.attendees = attendees;
    }

    /**
     * 操作方法
     * @author lilinhai
     * @since 2021-02-20 16:39  void
     */
    public abstract void operate() throws Exception;
    
    /**
     * 抽象的取消操作方法
     * @author lilinhai
     * @since 2021-02-20 16:40  void
     */
    public void cancel()
    {
        this.isCancel = true;
    }
    
    /**
     * <p>Get Method   :   isCancel boolean</p>
     * @return isCancel
     */
    public boolean isCancel()
    {
        return isCancel;
    }

    /**
     * <p>Get Method   :   attendees List<Attendee></p>
     * @return attendees
     */
    public List<AttendeeForMcuZte> getAttendees()
    {
        return attendees;
    }
    
    /**
     * <p>Set Method   :   attendees List<Attendee></p>
     * @param attendees
     */
    public void setAttendees(List<AttendeeForMcuZte> attendees)
    {
        this.attendees = attendees;
    }

    /**
     * <p>Get Method   :   splitScreen SplitScreen</p>
     * @return splitScreen
     */
    public SplitScreen getSplitScreen()
    {
        return splitScreen;
    }
    
    public boolean contains(AttendeeForMcuZte attendee)
    {
        return attendees != null && attendees.contains(attendee);
    }

    /**
     * <p>Get Method   :   operatedAttendees List<Attendee></p>
     * @return operatedAttendees
     */
    public List<AttendeeForMcuZte> getOperatedAttendees()
    {
        return operatedAttendees;
    }
    
    protected void clearOperatedAttendees()
    {
        operatedAttendees.clear();
    }
    
    public void addOperatedAttendee(AttendeeForMcuZte a)
    {
        operatedAttendees.add(a);
    }

    /**
     * <p>Get Method   :   isPushOperatedAttendeesToMonitor boolean</p>
     * @return isPushOperatedAttendeesToMonitor
     */
    @JsonIgnore
    public boolean isPushOperatedAttendeesToMonitor()
    {
        return pushOperatedAttendeesToMonitor;
    }

    /**
     * <p>Set Method   :   isPushOperatedAttendeesToMonitor boolean</p>
     * @param pushOperatedAttendeesToMonitor
     */
    @JsonIgnore
    public void setPushOperatedAttendeesToMonitor(boolean pushOperatedAttendeesToMonitor)
    {
        this.pushOperatedAttendeesToMonitor = pushOperatedAttendeesToMonitor;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public boolean isForceUpdateView() {
        return forceUpdateView;
    }

    public void setForceUpdateView(boolean forceUpdateView) {
        this.forceUpdateView = forceUpdateView;
    }

    public long getOperationCreateTime() {
        return operationCreateTime;
    }
}
