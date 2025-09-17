/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : ParticipantMessageProcessor.java
 * Package : com.paradisecloud.fcm.cdr.service.core.listener
 * 
 * @author sinhy
 * 
 * @since 2021-12-15 16:11
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.cdr.service.core.listener;

import java.util.Date;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.service.interfaces.IBusiHistoryParticipantTerminalService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.paradisecloud.fcm.common.enumer.ParticipantState;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryParticipantMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiHistoryParticipant;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.attendee.model.core.ParticipantMessageQueue;
import com.paradisecloud.fcm.fme.cache.model.ParticipantInfo;
import com.paradisecloud.fcm.fme.model.cms.callleg.CallLeg;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import com.sinhy.model.AsyncBlockingMessageProcessor;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * <pre>Participant消息更新器</pre>
 * 
 * @author sinhy
 * @since 2021-12-15 16:11
 * @version V1.0
 */
@Component
public class ParticipantMessageProcessor extends AsyncBlockingMessageProcessor<ParticipantInfo> implements InitializingBean
{
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private BusiHistoryParticipantMapper busiHistoryParticipantMapper;
    
    @Autowired
    private BusiHistoryConferenceMapper busiHistoryConferenceMapper;
    
    @Autowired
    private IAttendeeService attendeeService;

    @Resource
    private IBusiHistoryParticipantTerminalService busiHistoryParticipantTerminalService;
    
    /**
     * <pre>构造方法</pre>
     * 
     * @author sinhy
     * @since 2021-12-15 16:11
     * @param name
     * @param queueSize
     */
    protected ParticipantMessageProcessor()
    {
        super("ParticipantMessageProcessor", ParticipantMessageQueue.getInstance());
    }
    
    @Override
    protected void process(ParticipantInfo participantInfo)
    {
        Participant participant = participantInfo.getParticipant();
        try
        {
            if (participant.getStatus() != null)
            {
                // 只处理入会和离会两种状态
                if (participant.is(ParticipantState.CONNECTED))
                {
                    CallLeg callLeg = participant.getCallLeg();
                    BusiHistoryParticipant busiHistoryParticipant = busiHistoryParticipantMapper.selectBusiHistoryParticipantByCallLegId(participant.getCallLeg().getId());
                    if (busiHistoryParticipant != null)
                    {
                        if (busiHistoryParticipant.getJoined() == null || !busiHistoryParticipant.getJoined())
                        {
                            busiHistoryParticipant.setJoined(true);
                            busiHistoryParticipant.setJoinTime(new Date());
                            busiHistoryParticipant.setUpdateTime(new Date());
                            busiHistoryParticipantMapper.updateBusiHistoryParticipant(busiHistoryParticipant);
                        }
                        else
                        {
                            busiHistoryParticipant.setMediaInfo(attendeeService.toDetail(callLeg));
                            busiHistoryParticipant.setUpdateTime(new Date());
                            busiHistoryParticipantMapper.updateBusiHistoryParticipantForJoin(busiHistoryParticipant);
                        }
                    }
                    else
                    {
                        BusiHistoryConference bhc = busiHistoryConferenceMapper.selectBusiHistoryConferenceByCallId(participant.getCall());
                        if (bhc != null)
                        {
                            busiHistoryParticipant = new BusiHistoryParticipant();
                            busiHistoryParticipant.setCreateTime(new Date());
                            busiHistoryParticipant.setDeptId(bhc.getDeptId().intValue());
                            busiHistoryParticipant.setCallId(participant.getCall());
                            busiHistoryParticipant.setCoSpace(bhc.getCoSpace());
                            busiHistoryParticipant.setCallLegId(participant.getCallLeg().getId());
                            busiHistoryParticipant.setRemoteParty(participant.getUri());
                            busiHistoryParticipant.setName(participant.getName());
                            busiHistoryParticipant.setHistoryConferenceId(bhc.getId());
                            busiHistoryParticipant.setJoinTime(new Date());
                            busiHistoryParticipant.setCreateTime(new Date());
                            busiHistoryParticipant.setJoined(true);
                            busiHistoryParticipant.setMediaInfo(attendeeService.toDetail(callLeg));
                            String remoteParty = participant.getUri();
                            if (remoteParty.contains(":")) {
                                remoteParty = remoteParty.substring(0, remoteParty.indexOf(":"));
                            }
                            BusiTerminal busiTerminal = TerminalCache.getInstance().getByRemoteParty(remoteParty);
                            if (busiTerminal == null) {
                                if (remoteParty.contains("@")) {
                                    try {
                                        String[] remotePartyArr = remoteParty.split("@");
                                        String credential = remotePartyArr[0];
                                        String ip = remotePartyArr[1];
                                        if (StringUtils.hasText(ip)) {
                                            FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getByDomainName(ip);
                                            if (fsbcBridge != null) {
                                                String remotePartyNew = credential + "@" + fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                                                busiTerminal = TerminalCache.getInstance().getByRemoteParty(remotePartyNew);
                                            }
                                            if (busiTerminal == null) {
                                                FcmBridge fcmBridge = FcmBridgeCache.getInstance().getByDomainName(ip);
                                                if (fcmBridge != null) {
                                                    String remotePartyNew = credential + "@" + fcmBridge.getBusiFreeSwitch().getIp();
                                                    busiTerminal = TerminalCache.getInstance().getByRemoteParty(remotePartyNew);
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }
                            if (busiTerminal != null) { {
                                busiHistoryParticipant.setTerminalId(busiTerminal.getId());
                            }}
                            busiHistoryParticipantMapper.insertBusiHistoryParticipant(busiHistoryParticipant);
                            logger.info("Participant joined and saved: " + participant.getRosterUpdate());
                        }
                    }
                    // 更新参会者终端信息
                    updateBusiHistoryParticipantTerminal(busiHistoryParticipant);
                }
                else if (participant.is(ParticipantState.DISCONNECT))
                {
                    if (participant.getCallLeg() != null)
                    {
                        BusiHistoryParticipant bhp = busiHistoryParticipantMapper.selectBusiHistoryParticipantByCallLegId(participant.getCallLeg().getId());
                        if (bhp != null && (bhp.getOutgoingTime() == null || bhp.getDurationSeconds() == null || bhp.getDurationSeconds().intValue() == 0))
                        {
                            bhp.setOutgoingTime(new Date());
                            bhp.setUpdateTime(new Date());
                            if (participant.getCallLeg().getStatus().getDurationSeconds() != null)
                            {
                                bhp.setDurationSeconds(participant.getCallLeg().getStatus().getDurationSeconds());
                            }
                            else
                            {
                                bhp.setDurationSeconds((int) ((bhp.getOutgoingTime().getTime() - bhp.getJoinTime().getTime()) / 1000));
                            }
                            busiHistoryParticipantMapper.updateBusiHistoryParticipant(bhp);
                            logger.info("Participant left and saved: " + participant.getRosterUpdate());

                            // 更新参会者终端信息
                            updateBusiHistoryParticipantTerminal(bhp);
                        }
                    }
                }
            }
        }
        catch (Throwable e)
        {
            logger.error("ParticipantMessageProcessor error", e);
        }
    }
    
    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.start();
    }

    /**
     * 更新参会者终端信息
     *
     * @param busiHistoryParticipant
     */
    private void updateBusiHistoryParticipantTerminal(BusiHistoryParticipant busiHistoryParticipant) {
        if (busiHistoryParticipant != null) {
            if (busiHistoryParticipant.getTerminalId() != null) {
                BusiTerminal busiTerminal = TerminalCache.getInstance().get(busiHistoryParticipant.getTerminalId());
                if (busiTerminal != null) {
                    busiHistoryParticipant.setName(busiTerminal.getName());
                }
            }
            try {
                busiHistoryParticipantTerminalService.updateBusiHistoryParticipantTerminalByBusiHistoryParticipant(busiHistoryParticipant);
            } catch (Exception e) {
            }
        }
    }
}
