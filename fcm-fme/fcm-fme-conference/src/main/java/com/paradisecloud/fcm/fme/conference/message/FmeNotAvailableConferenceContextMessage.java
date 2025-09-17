/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ConferenceContextMessage.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.message.conference
 * @author lilinhai 
 * @since 2021-03-08 13:53
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.message;

import java.text.MessageFormat;

import com.paradisecloud.fcm.common.enumer.ConferenceType;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceNumberMapper;
import com.paradisecloud.fcm.fme.attendee.model.attendeemessage.AttendeeMessageQueue;
import com.paradisecloud.fcm.fme.attendee.model.attendeemessage.FmeNotAvailableAttendeeMessage;
import com.paradisecloud.fcm.fme.attendee.utils.AttendeeMessagePusher;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.conference.model.core.ConferenceStarter;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.FmeAttendee;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ThreadUtils;

/**  
 * <pre>会议上下文消息</pre>
 * @author lilinhai
 * @since 2021-03-08 13:53
 * @version V1.0  
 */
public class FmeNotAvailableConferenceContextMessage extends ConferenceContextMessage
{

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-03-08 14:15 
     * @param conferenceContext 
     * @param inMeetingStatusAttendeeIdSet 
     */
    public FmeNotAvailableConferenceContextMessage(ConferenceContext conferenceContext)
    {
        super(conferenceContext);
    }
    
    public boolean isNeedProcess()
    {
        // 创建call
        try
        {
            FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceNumber(conferenceContext.getDeptId(), conferenceContext.getConferenceNumber(), false);
            if (fmeBridge == null || !fmeBridge.isAvailable())
            {
                return false;
            }
        }
        catch (Throwable e)
        {
            logger.error(getClass().getSimpleName() + " isNeedProcess error: ", e);
            return false;
        }
        return true;
    }

    @Override
    public void process()
    {
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceNumber(conferenceContext.getDeptId(), conferenceContext.getConferenceNumber(), false);
        if (fmeBridge.isDeleted())
        {
            fmeBridge.getFmeLogger().logInfo("FME节点被删除，此消息作废，无需重复处理", true, true);
            return;
        }
        
        if (!fmeBridge.getDataCache().available())
        {
            fmeBridge.getFmeLogger().logInfo("FME节点数据缓存对象不可用", true, true);
            return;
        }
        
        // 会议启动器
        ConferenceStarter confefrenceStarter = new ConferenceStarter(fmeBridge, conferenceContext.getAttendees());
        ConferenceContext upConferenceContext = ConferenceContextCache.getInstance().getUpConferenceContext(conferenceContext);
        if (upConferenceContext == null)
        {
            upConferenceContext = conferenceContext;
        }
        ConferenceType conferenceType = ConferenceType.convert(upConferenceContext.getType());
        String conferenceNameFormat = conferenceType.getName() + "会议【" + conferenceContext.getConferenceNumber() + "】{0}";
        
        confefrenceStarter.setConferenceStandardName(conferenceContext.getName());
        confefrenceStarter.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        confefrenceStarter.setBusiConferenceNumber(BeanFactory.getBean(BusiConferenceNumberMapper.class).selectBusiConferenceNumberById(Long.parseLong(conferenceContext.getConferenceNumber())));
        confefrenceStarter.setDeptId(conferenceContext.getDeptId());
        if (upConferenceContext == conferenceContext)
        {
            confefrenceStarter.setConferenceName(MessageFormat.format(conferenceNameFormat, "发起方：" + SysDeptCache.getInstance().get(conferenceContext.getDeptId()).getDeptName()));
        }
        else
        {
            confefrenceStarter.setConferenceName(MessageFormat.format(conferenceNameFormat, "下级集群：" + SysDeptCache.getInstance().get(conferenceContext.getDeptId()).getDeptName()));
            FmeAttendee fmeAttendee1 = null;
            for (FmeAttendee fmeAttendee : upConferenceContext.getFmeAttendees())
            {
                if (fmeAttendee.getCascadeConferenceNumber().equals(conferenceContext.getConferenceNumber()))
                {
                    fmeAttendee1 = fmeAttendee;
                    break;
                }
            }
            
            if (fmeAttendee1 != null)
            {
                fmeAttendee1.resetUpdateMap();
                fmeAttendee1.setIp(fmeBridge.getAttendeeIp());
                fmeAttendee1.setFmeId(fmeBridge.getBusiFme().getId());
                fmeAttendee1.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                fmeAttendee1.setCascadeConferenceNumber(fmeAttendee1.getCascadeConferenceNumber());
                fmeAttendee1.updateOnlineStatus();
                AttendeeMessagePusher.getInstance().pushOnlineMessage(fmeAttendee1, upConferenceContext);
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, fmeAttendee1.getUpdateMap());
                conferenceContext.setFmeAttendeeRemoteParty(fmeAttendee1.getIp());
                upConferenceContext.addAttendeeToRemotePartyMap(fmeAttendee1);
                AttendeeMessageQueue.getInstance().put(new FmeNotAvailableAttendeeMessage(fmeAttendee1));
            }
        }
        confefrenceStarter.setBandwidth(conferenceContext.getBandwidth());
        
        try
        {
            confefrenceStarter.init();
            confefrenceStarter.start();
        }
        catch (Exception e)
        {
            logger.error("会议室启动器ConfefrenceStarter启动失败：" + conferenceContext.getConferenceNumber(), e);
            ThreadUtils.sleep(5 * 1000);
            logger.error("会议室启动器ConfefrenceStarter初始化失败，重新走监听流程：" + conferenceContext.getConferenceNumber());
            ConferenceContextMessage conferenceContextMessage = new FmeNotAvailableConferenceContextMessage(confefrenceStarter.getConferenceContext());
            ConferenceContextMessageQueue.getInstance().add(conferenceContextMessage);
        }
    }
}
