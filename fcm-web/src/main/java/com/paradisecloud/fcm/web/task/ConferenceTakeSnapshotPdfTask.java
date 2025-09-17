package com.paradisecloud.fcm.web.task;

import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryParticipantImgMapper;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryParticipantMapper;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryParticipantTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiHistoryParticipant;
import com.paradisecloud.fcm.dao.model.BusiHistoryParticipantTerminal;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.vo.BusiHistoryParticipantVo;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.web.utils.PDFMeetingMinutesGenerator;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConferenceTakeSnapshotPdfTask extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(ConferenceTakeSnapshotPdfTask.class);

    private final ConferenceContext conferenceContext;
    private final long delayInMilliseconds;

    public ConferenceTakeSnapshotPdfTask(String id, long delayInMilliseconds, ConferenceContext conferenceContext) {
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
                    if(busiHistoryParticipantTerminal.getRemoteParty()!=null&&busiHistoryParticipantTerminal.getRemoteParty().contains("minutes@")){
                        continue;
                    }

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
                                    String remoteParty = busiHistoryParticipantTerminal.getRemoteParty();
                                    boolean validIPv4 = isValidIPv4(remoteParty);
                                    if(validIPv4){
                                        busiHistoryParticipantVo.setTerminalTypeName("IP终端");
                                    }

                                }else {
                                    busiHistoryParticipantVo.setTerminalTypeName("未知");
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                    if(Objects.equals(busiHistoryParticipantTerminal.getRemoteParty(),"streaming@streamer.com")){
                        busiHistoryParticipantVo.setName("会议直播");
                    }
                    if(Objects.equals(busiHistoryParticipantTerminal.getRemoteParty(),"recording@@recorder.com")){
                        busiHistoryParticipantVo.setName("会议录制");
                    }
                    busiHistoryParticipantVos.add(busiHistoryParticipantVo);
                }
            } else {
                for (BusiHistoryParticipant busiHistoryParticipant : historyParticipantList) {
                    if(busiHistoryParticipant.getRemoteParty()!=null&&busiHistoryParticipant.getRemoteParty().contains("minutes@")){
                        continue;
                    }
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
                                    String remoteParty = busiHistoryParticipant.getRemoteParty();
                                    boolean validIPv4 = isValidIPv4(remoteParty);
                                    if(validIPv4){
                                        busiHistoryParticipantVo.setTerminalTypeName("IP终端");
                                    }
                                }else {
                                    busiHistoryParticipantVo.setTerminalTypeName("未知");
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                    if(Objects.equals(busiHistoryParticipant.getRemoteParty(),"streaming@streamer.com")){
                        busiHistoryParticipantVo.setName("会议直播");
                    }
                    if(Objects.equals(busiHistoryParticipant.getRemoteParty(),"recording@@recorder.com")){
                        busiHistoryParticipantVo.setName("会议录制");
                    }
                    busiHistoryParticipantVos.add(busiHistoryParticipantVo);
                }

            }

            PDFMeetingMinutesGenerator pdfMeetingMinutesGenerator = new PDFMeetingMinutesGenerator();
            pdfMeetingMinutesGenerator.generateMinutes(conferenceContext.getName(), conferenceContext.getConferenceNumber(),
                    conferenceContext.getStartTime(), conferenceContext.getEndTime(), num, busiHistoryParticipantVos,busiHistoryConference.getCoSpace(),busiHistoryConference.getNumber(), busiHistoryConference.getId());
            BusiHistoryConference busiHistoryConferenceQuery = busiHistoryConferenceMapper.selectBusiHistoryConferenceById(busiHistoryConference.getId());
            busiHistoryConferenceQuery.setMinutesDoc(1);
            busiHistoryConferenceMapper.updateBusiHistoryConference(busiHistoryConferenceQuery);
        } catch (Exception e) {
            logger.info("纪要文档pdf生成错误" + e.getMessage());
        }

    }
    public static boolean isValidIPv4(String ip) {
        String regex = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";
        return ip.matches(regex);
    }


}
