/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CurrentHandler.java
 * Package     : com.paradisecloud.fcm.fme.model.busi
 * @author lilinhai 
 * @since 2021-02-20 16:37
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.smc2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paradisecloud.com.fcm.smc.modle.PollOperateTypeDto;
import com.paradisecloud.com.fcm.smc.modle.request.ChairmanPollOperateReq;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.fcm.service.conference.operation.BaseAttendeeOperation;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.layout.SplitScreen;
import com.paradisecloud.fcm.smc2.service.Smc2ConferenceService;
import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**  
 * <pre>当前正在进行的操作</pre>
 * @author lilinhai
 * @since 2021-02-20 16:37
 * @version V1.0  
 */
public abstract class AttendeeOperation extends BaseAttendeeOperation implements Serializable
{
    public static final String CANCEL = "CANCEL";
    private Smc2ConferenceService smc2ConferenceService;
    
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
    protected volatile Smc2ConferenceContext conferenceContext;


    /**
     * 分屏
     */
    protected SplitScreen splitScreen;
    
    /**
     * 分屏需要渲染的参会者，该集合不能大于分屏数
     */
    protected volatile List<AttendeeSmc2> attendees;
    
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
    protected List<SmcParitipantsStateRep.ContentDTO> operatedAttendees = new ArrayList<>();
    
    private long operationCreateTime = System.currentTimeMillis();
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-22 13:47 
     * @param conferenceContext
     */
    protected AttendeeOperation(Smc2ConferenceContext conferenceContext)
    {
        super();
        this.conferenceContext = conferenceContext;
    }

    public AttendeeOperation(Smc2ConferenceContext conferenceContext,Smc2ConferenceService smc2ConferenceService) {
        super();
        this.conferenceContext = conferenceContext;
        this.smc2ConferenceService = smc2ConferenceService;
    }

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-22 13:47 
     * @param conferenceContext
     * @param splitScreen 
     */
    protected AttendeeOperation(Smc2ConferenceContext conferenceContext, SplitScreen splitScreen)
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
    protected AttendeeOperation(Smc2ConferenceContext conferenceContext, SplitScreen splitScreen, List<AttendeeSmc2> attendees)
    {
        this(conferenceContext, splitScreen);
        this.attendees = attendees;
    }

    /**
     * 操作方法
     * @author lilinhai
     * @since 2021-02-20 16:39  void
     */
    public abstract void operate();
    
    /**
     * 取消操作方法
     * @author lilinhai
     * @since 2021-02-20 16:40  void
     */
    public void cancel(AttendeeOperation nextAttendeeOperation)
    {
        if (!isCancel)
        {
            try
            {
                this.cancel();
                this.nextAttendeeOperation = nextAttendeeOperation;
                if (nextAttendeeOperation != null)
                {
                    nextAttendeeOperation.operate();
                }
            }
            catch (Throwable e)
            {
                logger.error("cancel(AttendeeOperation nextAttendeeOperation) error: ", e);
            }
            finally 
            {
                isCancel = true;
            }
        }
    }
    
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
    public List<AttendeeSmc2> getAttendees()
    {
        return attendees;
    }
    
    /**
     * <p>Set Method   :   attendees List<Attendee></p>
     * @param attendees
     */
    public void setAttendees(List<AttendeeSmc2> attendees)
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
    
    public boolean contains(AttendeeSmc2 attendee)
    {
        return attendees != null && attendees.contains(attendee);
    }

    /**
     * <p>Get Method   :   operatedAttendees List<Attendee></p>
     * @return operatedAttendees
     */
    public List<SmcParitipantsStateRep.ContentDTO> getOperatedAttendees()
    {
        return operatedAttendees;
    }
    
    protected void clearOperatedAttendees()
    {
        operatedAttendees.clear();
    }
    
    public void addOperatedAttendee(SmcParitipantsStateRep.ContentDTO a)
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
     * @param
     */
    @JsonIgnore
    public void setPushOperatedAttendeesToMonitor(boolean pushOperatedAttendeesToMonitor)
    {
        this.pushOperatedAttendeesToMonitor = pushOperatedAttendeesToMonitor;
    }

    public void broadcast(String uri, String confId,int isBroadcast) {
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        Integer resultCode = conferenceServiceEx.setBroadcastSiteEx(confId, uri,isBroadcast);
        if(resultCode!=0){
            logger.error(uri +"广播主席失败："+resultCode);
        }
    }

    public void cancelPoll(){
        try {
            String chairmanPollStatus = conferenceContext.getDetailConference().getConferenceState().getChairmanPollStatus();

            if(!Objects.equals(chairmanPollStatus,CANCEL)){
                ChairmanPollOperateReq chairmanPollOperateReq = new ChairmanPollOperateReq();
                chairmanPollOperateReq.setConferenceId(conferenceContext.getConference().getId());
                chairmanPollOperateReq.setPollStatus(PollOperateTypeDto.CANCEL);
                smc2ConferenceService.chairmanParticipantMultiPicPollOperate(chairmanPollOperateReq);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            String multiPicPollStatus = conferenceContext.getDetailConference().getConferenceState().getMultiPicPollStatus();
            if(!Objects.equals(multiPicPollStatus, CANCEL)){
                smc2ConferenceService.cancelMultiPicPoll(conferenceContext.getConference().getId());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
