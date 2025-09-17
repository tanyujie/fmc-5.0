/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : TerminalInfo.java
 * Package     : com.paradisecloud.fcm.common.model.terminal
 * @author lilinhai 
 * @since 2021-03-02 13:05
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.attendeemessage;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.fme.attendee.model.busiprocessor.RecallAttendeeProcessor;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.FmeAttendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.TerminalAttendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.UpFmeAttendee;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;

/**  
 * <pre>参会者消息</pre>
 * @author lilinhai
 * @since 2021-03-02 13:05
 * @version V1.0  
 */
public abstract class AttendeeMessage
{

    protected Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * 参会者
     */
    protected Attendee attendee;
    
    /**
     * 消息创建时间
     */
    protected long timestamp;
    
    /**
     * 是否作废
     */
    private boolean isDiscard;

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-03-02 14:04 
     * @param attendee 
     */
    protected AttendeeMessage(Attendee attendee)
    {
        this.attendee = attendee;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * <p>Get Method   :   attendee Attendee</p>
     * @return attendee
     */
    public Attendee getAttendee()
    {
        return attendee;
    }
    
    /**
     * <p>Get Method   :   timestamp long</p>
     * @return timestamp
     */
    public long getTimestamp()
    {
        return timestamp;
    }
    
    /**
     * <p>Get Method   :   isDiscard boolean</p>
     * @return isDiscard
     */
    public boolean isDiscard()
    {
        return isDiscard;
    }
    
    /**
     * <p>Set Method   :   isDiscard boolean</p>
     * @param isDiscard
     */
    public void setDiscard(boolean isDiscard)
    {
        this.isDiscard = isDiscard;
    }

    /**
     * 判断该消息是否需要执行
     * @author lilinhai
     * @since 2021-03-02 15:43 
     * @return boolean
     */
    public boolean isNeedProcess()
    {
        if (attendee.getMeetingStatus() == AttendeeMeetingStatus.IN.getValue())
        {
            setDiscard(true);
            return false;
        }
        if (attendee.isHangUp())
        {
            setDiscard(true);
            return false;
        }
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceNumber(attendee.getDeptId(), attendee.getConferenceNumber(), false);
        if (fmeBridge == null || !fmeBridge.isAvailable() || fmeBridge.isDeleted() || !fmeBridge.getDataCache().available())
        {
            setDiscard(true);
            return false;
        }
        
        if (attendee instanceof FmeAttendee && !(attendee instanceof UpFmeAttendee))
        {
            FmeAttendee fmeAttendee = (FmeAttendee) attendee;
            FmeBridge subFmeBridge = FmeBridgeCache.getInstance().getCascadeFmeBridgeByFmeAttendee(fmeAttendee, false);
            if (subFmeBridge == null)
            {
                return false;
            }
            fmeAttendee.setIp(subFmeBridge.getAttendeeIp());
            fmeAttendee.setFmeId(subFmeBridge.getBusiFme().getId());
            fmeAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
            fmeAttendee.setCascadeConferenceNumber(fmeAttendee.getCascadeConferenceNumber());
            
            Call call = subFmeBridge.getDataCache().getCallByConferenceNumber(fmeAttendee.getCascadeConferenceNumber());
            if (call == null)
            {
                setDiscard(true);
                return false;
            }
        }
        else if (attendee instanceof TerminalAttendee)
        {
            TerminalAttendee terminalAttendee = (TerminalAttendee) attendee;
            BusiTerminal bt = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
            terminalAttendee.setName(bt.getName());
            terminalAttendee.setTerminalType(bt.getType());
            terminalAttendee.setTerminalTypeName(TerminalType.convert(bt.getType()).getDisplayName());
            terminalAttendee.setOnlineStatus(bt.getOnlineStatus());
        }
        
        ConferenceContext cc = ConferenceContextCache.getInstance().get(attendee.getContextKey());
        if (cc == null || cc.isEnd())
        {
            // 会议不存在，则放弃
            setDiscard(true);
            return false;
        }
        
        // FME桥可用且call已创建，才会执行重呼
        return true;
    }
    
    /**
     * 消息对应的处理方法
     */
    public void process()
    {
        RecallAttendeeProcessor attendeeBusiProcessor = new RecallAttendeeProcessor(attendee.getConferenceNumber(), attendee.getId());
        attendeeBusiProcessor.getConferenceContext().addRecallAttendeeBeginTime(attendeeBusiProcessor.getTargetAttendee());
        Long startRecallTime = attendeeBusiProcessor.getConferenceContext().getRecallAttendeeBeginTime(attendeeBusiProcessor.getTargetAttendee());
        
        // 超过30分钟自动中断系统重试
        if (System.currentTimeMillis() - startRecallTime >= TimeUnit.MINUTES.toMillis(30))
        {
            logger.info(attendeeBusiProcessor.getTargetAttendee() + "---重试已超过30分钟---已中断系统重试！");
            
            // 移除重乎开始时间，好让下次重呼的时候再次能触发系统呼叫
            attendeeBusiProcessor.getConferenceContext().removeRecallAttendeeBeginTime(attendee);
            return;
        }
        
        logger.info("系统自动重呼请求开始发起：" + attendee);
        
        // 执行重试
        attendeeBusiProcessor.process();
    }

    @Override
    public String toString()
    {
        return "AttendeeMessage [attendee=" + attendee + ", timestamp=" + timestamp + ", isDiscard=" + isDiscard + "]";
    }
}
