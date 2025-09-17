package com.paradisecloud.fcm.web.task;

import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryParticipantImgMapper;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryParticipantMapper;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryParticipantTerminalMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.BusiHistoryParticipantVo;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.web.utils.DocMeetingMinutesGenerator;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConferenceTakeSnapshotDocTask extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(ConferenceTakeSnapshotDocTask.class);

    private final ConferenceContext conferenceContext;
    private final long delayInMilliseconds;

    public ConferenceTakeSnapshotDocTask(String id, long delayInMilliseconds, ConferenceContext conferenceContext) {
        //  super("t_s_doc_" +id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.delayInMilliseconds = delayInMilliseconds;


    }

    @Override
    public void run() {
        if (conferenceContext == null) {
            return;
        }
        logger.info("快照文档任务>>>>>>>>>>>>>>>>>>>>>>>会议号:" + conferenceContext.getConferenceNumber() + "会议ID:" + conferenceContext.getId());
        Threads.sleep(delayInMilliseconds);
        try {
            BusiHistoryParticipantImgMapper busiHistoryParticipantImgMapper = BeanFactory.getBean(BusiHistoryParticipantImgMapper.class);
            BusiHistoryParticipantTerminalMapper busiHistoryParticipantTerminalMapper = BeanFactory.getBean(BusiHistoryParticipantTerminalMapper.class);
            BusiHistoryParticipantMapper busiHistoryParticipantMapper = BeanFactory.getBean(BusiHistoryParticipantMapper.class);
            BusiHistoryConferenceMapper busiHistoryConferenceMapper = BeanFactory.getBean(BusiHistoryConferenceMapper.class);
            BusiHistoryConference busiHistoryConference = conferenceContext.getHistoryConference();

            List<BusiHistoryParticipantTerminal> historyParticipantTerminalList = new ArrayList<>();
            List<BusiHistoryParticipant> historyParticipantList = new ArrayList<>();
            int num = busiHistoryConference.getDeviceNum();
            if (num == 0) {
                BusiHistoryParticipantTerminal busiHistoryParticipantTerminalCon = new BusiHistoryParticipantTerminal();
                busiHistoryParticipantTerminalCon.setHistoryConferenceId(busiHistoryConference.getId());
                historyParticipantTerminalList = busiHistoryParticipantTerminalMapper.selectBusiHistoryParticipantTerminalList(busiHistoryParticipantTerminalCon);
                num = historyParticipantTerminalList.size();
                if (num == 0) {
                    BusiHistoryParticipant participant = new BusiHistoryParticipant();
                    participant.setHistoryConferenceId(busiHistoryConference.getId());
                    historyParticipantList = busiHistoryParticipantMapper.selectBusiHistoryParticipantList(participant);
                    num = (CollectionUtils.isEmpty(historyParticipantList)) ? 0 : historyParticipantList.size();
                }
            }

            List<BusiHistoryParticipantVo> busiHistoryParticipantVos = new ArrayList<>();


            if (CollectionUtils.isNotEmpty(historyParticipantTerminalList)) {
                for (BusiHistoryParticipantTerminal busiHistoryParticipantTerminal : historyParticipantTerminalList) {
                    BusiHistoryParticipantVo busiHistoryParticipantVo = new BusiHistoryParticipantVo();
                    busiHistoryParticipantVo.setName(busiHistoryParticipantTerminal.getName());
                    busiHistoryParticipantVo.setJoinTime(busiHistoryParticipantTerminal.getJoinTime());
                    busiHistoryParticipantVo.setRemoteParty(busiHistoryParticipantTerminal.getRemoteParty());
                    busiHistoryParticipantVo.setOutgoingTime(busiHistoryParticipantTerminal.getOutgoingTime());
                    busiHistoryParticipantVo.setJoined(busiHistoryParticipantTerminal.getJoined());
                    busiHistoryParticipantVo.setDurationSeconds(busiHistoryParticipantTerminal.getDurationSeconds());
                    List<String> imgs = busiHistoryParticipantImgMapper.selectImgs(busiHistoryConference.getId(), busiHistoryParticipantTerminal.getRemoteParty());
                    String imgsJoin = String.join(",", imgs);
                    busiHistoryParticipantVo.setImages(imgsJoin);

                    try {
                        Long terminalId = busiHistoryParticipantTerminal.getTerminalId();
                        if(terminalId!=null){
                            BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
                            if(busiTerminal!=null){
                                busiHistoryParticipantVo.setTerminalTypeName(TerminalType.convert(busiTerminal.getType()).getDisplayName());
                            }else {
                                String region = ExternalConfigCache.getInstance().getRegion();
                                if(Objects.equals(region,"ops")){
                                    busiHistoryParticipantVo.setTerminalTypeName("RTSP");
                                }else {
                                    busiHistoryParticipantVo.setTerminalTypeName("未知");
                                }
                            }
                        }
                    } catch (Exception e) {
                    }

                    busiHistoryParticipantVos.add(busiHistoryParticipantVo);
                }
            } else {
                for (BusiHistoryParticipant busiHistoryParticipant : historyParticipantList) {
                    BusiHistoryParticipantVo busiHistoryParticipantVo = new BusiHistoryParticipantVo();
                    busiHistoryParticipantVo.setName(busiHistoryParticipant.getName());
                    busiHistoryParticipantVo.setJoinTime(busiHistoryParticipant.getJoinTime());
                    busiHistoryParticipantVo.setRemoteParty(busiHistoryParticipant.getRemoteParty());
                    busiHistoryParticipantVo.setOutgoingTime(busiHistoryParticipant.getOutgoingTime());
                    busiHistoryParticipantVo.setJoined(busiHistoryParticipant.getJoined());
                    busiHistoryParticipantVo.setDurationSeconds(busiHistoryParticipant.getDurationSeconds());
                    List<String> imgs = busiHistoryParticipantImgMapper.selectImgs(busiHistoryConference.getId(), busiHistoryParticipant.getRemoteParty());
                    String imgsJoin = String.join(",", imgs);
                    busiHistoryParticipantVo.setImages(imgsJoin);
                    Long terminalId = busiHistoryParticipantVo.getTerminalId();
                    try {
                        if(terminalId!=null){
                            BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
                            if(busiTerminal!=null){
                                busiHistoryParticipantVo.setTerminalTypeName(TerminalType.convert(busiTerminal.getType()).getDisplayName());
                            }else {
                                String region = ExternalConfigCache.getInstance().getRegion();
                                if(Objects.equals(region,"ops")){
                                    busiHistoryParticipantVo.setTerminalTypeName("RTSP");
                                }else {
                                    busiHistoryParticipantVo.setTerminalTypeName("未知");
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                    busiHistoryParticipantVos.add(busiHistoryParticipantVo);
                }

            }


            DocMeetingMinutesGenerator.generateMinutes(conferenceContext.getName(), conferenceContext.getConferenceNumber(),
                    conferenceContext.getStartTime(), conferenceContext.getEndTime(), num, busiHistoryParticipantVos, busiHistoryConference.getCoSpace(), busiHistoryConference.getNumber(), busiHistoryConference.getId());
            BusiHistoryConference busiHistoryConferenceQuery = busiHistoryConferenceMapper.selectBusiHistoryConferenceById(busiHistoryConference.getId());
            busiHistoryConferenceQuery.setMinutesDoc(1);
            busiHistoryConferenceMapper.updateBusiHistoryConference(busiHistoryConferenceQuery);
        } catch (Exception e) {
            logger.info("纪要文档生成错误" + e.getMessage());
        }

    }


}
