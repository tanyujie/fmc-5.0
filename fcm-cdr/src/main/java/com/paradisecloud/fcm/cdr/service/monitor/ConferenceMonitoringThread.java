/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ConferenceMonitoringThread.java
 * Package     : com.paradisecloud.fcm.cdr.service.task
 * @author sinhy
 * @since 2021-12-24 15:22
 * @version  V1.0
 */
package com.paradisecloud.fcm.cdr.service.monitor;

import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.ConferenceEndType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryCallMapper;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryParticipantMapper;
import com.paradisecloud.fcm.dao.mapper.CdrCallMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeDataCache;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiRecordsService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.paradisecloud.fcm.service.interfaces.IBusiHistoryParticipantTerminalService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.sinhy.utils.ThreadUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 会议监听线程
 * @author sinhy
 * @since 2021-12-24 15:22
 * @version V1.0
 */
@Component
public class ConferenceMonitoringThread extends Thread implements InitializingBean
{

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private BusiHistoryCallMapper busiHistoryCallMapper;

    @Resource
    private BusiHistoryConferenceMapper busiHistoryConferenceMapper;

    @Resource
    private BusiHistoryParticipantMapper busiHistoryParticipantMapper;

    @Resource
    private IBusiHistoryParticipantTerminalService busiHistoryParticipantTerminalService;

    @Resource
    private CdrCallMapper cdrCallMapper;

    @Resource
    private IBusiConferenceService busiConferenceService;

    @Resource
    private IBusiRecordsService iBusiRecordsService;

    @Override
    public void run() {
        logger.info("ConferenceMonitoringThread start successfully!");

        // 休眠30秒，让fme缓存初始化完成，否则造成误判，导致错误结束
        ThreadUtils.sleep(60000);
        while (true) {
            try
            {
                List<BusiHistoryConference> notEndConferences = busiHistoryConferenceMapper.selectNotEndHistoryConferenceList(McuType.FME.getCode());
                if (!ObjectUtils.isEmpty(notEndConferences))
                {
                    for (BusiHistoryConference busiHistoryConference : notEndConferences)
                    {
                        BusiHistoryCall con = new BusiHistoryCall();
                        con.setHistoryConferenceId(busiHistoryConference.getId());
                        List<BusiHistoryCall> calls = busiHistoryCallMapper.selectBusiHistoryCallList(con);
                        if (!ObjectUtils.isEmpty(calls))
                        {
                            boolean isEnd = true;
                            for (BusiHistoryCall call : calls)
                            {
                                Call c = FmeDataCache.getCallByUuid(call.getCallId());
                                if (c != null)
                                {
                                    isEnd = false;
                                }
                                if (!isEnd) {
                                    try {
                                        CdrCall cdrCallCon = new CdrCall();
                                        cdrCallCon.setCdrId(call.getCallId());
                                        List<CdrCall> cdrCallList = cdrCallMapper.selectCdrCallList(cdrCallCon);
                                        for (CdrCall cdrCall : cdrCallList) {
                                            if (cdrCall.getRecordType() != null && cdrCall.getRecordType() == 0) {
                                                isEnd = true;
                                            }
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }

                            if (isEnd) {

                                String conferenceNumber = busiHistoryConference.getNumber();
                                if (Strings.isNotBlank(conferenceNumber)) {
                                    ConferenceContext cc = null;
                                    Collection<ConferenceContext> conferenceContextList = ConferenceContextCache.getInstance().getConferenceContextListByConferenceNum(conferenceNumber);
                                    if (conferenceContextList != null) {
                                        for (ConferenceContext conferenceContext : conferenceContextList) {
                                            if (conferenceContext.getCoSpaceId().equals(busiHistoryConference.getCoSpace())) {
                                                cc = conferenceContext;
                                            }
                                        }
                                    }
                                    if (cc != null) {
                                        BusiConferenceAppointment conferenceAppointment = cc.getConferenceAppointment();
                                        if (conferenceAppointment != null) {
                                            String endTime = conferenceAppointment.getEndTime();
                                            if (StringUtils.hasText(endTime)) {
                                                Date date = DateUtil.convertDateByString(endTime, null);
                                                if (System.currentTimeMillis() >= date.getTime()) {
                                                    busiConferenceService.endConference(cc.getId(), ConferenceEndType.COMMON.getValue(), EndReasonsType.ABNORMAL_END);
                                                } else {
                                                    continue;
                                                }
                                            }

                                        } else {
                                            Date endTime = cc.getEndTime();
                                            if (endTime!=null) {
                                                if (System.currentTimeMillis() >= endTime.getTime()) {
                                                    busiConferenceService.endConference(cc.getId(), ConferenceEndType.COMMON.getValue(), EndReasonsType.ABNORMAL_END);
                                                } else {
                                                    continue;
                                                }
                                            }else {
                                                continue;
                                            }
                                        }
                                    }
                                }


                                Date currentDate = new Date();
                                busiHistoryConference.setConferenceEndTime(currentDate);
                                busiHistoryConference.setUpdateTime(currentDate);
                                if (busiHistoryConference.getConferenceStartTime() == null) {
                                    busiHistoryConference.setConferenceStartTime(new Date(System.currentTimeMillis() - new Random().nextInt(18) * 1000 * 60));
                                }
                                busiHistoryConference.setDuration((int) ((currentDate.getTime() - busiHistoryConference.getConferenceStartTime().getTime()) / 1000));
                                busiHistoryConferenceMapper.updateBusiHistoryConference(busiHistoryConference);
                                logger.info("检测到会议已结束，更新历史记录：" + busiHistoryConference);

                                iBusiRecordsService.updateBusiRecords(!isEnd, conferenceNumber);
                            }
                        }
                    }
                }

                List<BusiHistoryParticipant> notEndParticipants = busiHistoryParticipantMapper.selectNotEndHistoryParticipantList();
                if (!ObjectUtils.isEmpty(notEndParticipants))
                {
                    for (BusiHistoryParticipant participant : notEndParticipants)
                    {
                        if (participant.getCoSpace() != null && (participant.getCoSpace().length() <= 22  || participant.getCoSpace().endsWith("-zj") || participant.getCoSpace().endsWith("-plc") || participant.getCoSpace().endsWith("-kdc"))) {
                            continue;
                        }
                        if (FmeDataCache.getParticipantByUuid(participant.getCallLegId()) == null)
                        {
                            participant.setOutgoingTime(new Date());
                            participant.setUpdateTime(new Date());
                            participant.setDurationSeconds((int)((System.currentTimeMillis() - participant.getJoinTime().getTime()) / 1000));
                            busiHistoryParticipantMapper.updateBusiHistoryParticipant(participant);
                            logger.info("检测到参会者已离会，更新历史记录：" + participant);
                            // 更新参会者终端信息
                            updateBusiHistoryParticipantTerminal(participant);
                        }
                    }
                }
            }
            catch (Throwable e)
            {
                logger.error("ConferenceMonitoringThread error", e);
            }
            finally
            {
                ThreadUtils.sleep(5000);
            }
        }
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

    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.start();
    }
}
