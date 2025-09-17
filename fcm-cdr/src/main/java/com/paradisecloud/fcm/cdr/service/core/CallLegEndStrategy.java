package com.paradisecloud.fcm.cdr.service.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.cdr.service.core.listener.AllCallLegEndMediaEvent;
import com.paradisecloud.fcm.cdr.service.interfaces.report.IHistoryAllService;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryAllParticipantMapper;
import com.paradisecloud.fcm.dao.mapper.CdrCallLegStartMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.service.interfaces.IBusiHistoryParticipantTerminalService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.paradisecloud.fcm.cdr.service.core.cache.CdrCache;
import com.paradisecloud.fcm.cdr.service.core.listener.CallLegEndMediaEvent;
import com.paradisecloud.fcm.cdr.service.interfaces.ICdrCallLegEndAlarmService;
import com.paradisecloud.fcm.cdr.service.interfaces.ICdrCallLegEndMediaInfoService;
import com.paradisecloud.fcm.cdr.service.interfaces.ICdrCallLegEndService;
import com.paradisecloud.fcm.cdr.service.interfaces.report.IHistoryService;
import com.paradisecloud.fcm.cdr.service.model.AlarmElement;
import com.paradisecloud.fcm.cdr.service.model.CallLegElement;
import com.paradisecloud.fcm.cdr.service.model.MediaInfoElement;
import com.paradisecloud.fcm.cdr.service.model.PacketStatisticsElement;
import com.paradisecloud.fcm.cdr.service.model.RecordElement;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.enums.CallLegEndAlarmTypeEnum;
import com.paradisecloud.fcm.dao.enums.CallLegEndReasonEnum;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryParticipantMapper;

/**
 * @author johnson liu
 * @date 2021/5/14 10:11
 */
@Component
public class CallLegEndStrategy implements XmlReadStrategy<CdrCallLegEnd>
{
    private static final Logger logger = LoggerFactory.getLogger(CallLegEndStrategy.class);
    
    @Autowired
    private ICdrCallLegEndService cdrCallLegEndService;
    
    @Autowired
    private ICdrCallLegEndMediaInfoService cdrCallLegEndMediaInfoService;
    
    @Autowired
    private ICdrCallLegEndAlarmService cdrCallLegEndAlarmService;
    
    @Autowired
    private IHistoryService iHistoryService;
    
    @Resource
    private ApplicationContext applicationContext;
    
    @Autowired
    private BusiHistoryParticipantMapper busiHistoryParticipantMapper;

    @Resource
    private IHistoryAllService iHistoryAllService;

    @Resource
    private BusiHistoryAllParticipantMapper busiHistoryAllParticipantMapper;

    @Resource
    private CdrCallLegStartMapper cdrCallLegStartMapper;

    @Resource
    private IBusiHistoryParticipantTerminalService busiHistoryParticipantTerminalService;
    
    @Override
    public CdrCallLegEnd readToBean(String session, RecordElement recordElement)
    {
        CdrCallLegEnd cdrCallLegEnd = new CdrCallLegEnd();
        
        String time = recordElement.getTime();
        Integer recordIndex = recordElement.getRecordIndex();
        Integer correlatorIndex = recordElement.getCorrelatorIndex();
        CallLegElement callLeg = recordElement.getCallLeg();
        
        Date date = DateUtil.convertDateByString(time);
        
        cdrCallLegEnd.setRecordIndex(recordIndex);
        cdrCallLegEnd.setCorrelatorIndex(correlatorIndex);
        cdrCallLegEnd.setTime(date);
        cdrCallLegEnd.setSession(session);
        cdrCallLegEnd.setCreateTime(new Date());
        
        BeanUtils.copyProperties(callLeg, cdrCallLegEnd);
        cdrCallLegEnd.setCdrId(callLeg.getId());
        cdrCallLegEnd.setReason(CallLegEndReasonEnum.getEnumObjectByName(callLeg.getReason()));
        if (callLeg.getMediaUsagePercentages() != null)
        {
            cdrCallLegEnd.setMainVideoViewer(callLeg.getMediaUsagePercentages().getMainVideoViewer());
            cdrCallLegEnd.setMainVideoContributor(callLeg.getMediaUsagePercentages().getMainVideoContributor());
            cdrCallLegEnd.setPresentationViewer(callLeg.getMediaUsagePercentages().getPresentationViewer());
            cdrCallLegEnd.setPresentationContributor(callLeg.getMediaUsagePercentages().getPresentationContributor());
        }
        
        List<CdrCallLegEndMediaInfo> list = getMediaInfoList(recordIndex, correlatorIndex, callLeg);
        cdrCallLegEnd.setCallLegEndMediaInfoList(list);
        // 读取告警信息
        List<CdrCallLegEndAlarm> cdrCallLegEndAlarmList = getAlarmList(date, callLeg);
        cdrCallLegEnd.setCdrCallLegEndAlarmList(cdrCallLegEndAlarmList);
        return cdrCallLegEnd;
    }
    
    @Override
    @Transactional
    public synchronized void executeAdd(CdrCallLegEnd cdrCallLegEnd)
    {
        cdrCallLegEndService.insertCdrCallLegEnd(cdrCallLegEnd);
        List<CdrCallLegEndMediaInfo> callLegEndMediaInfoList = cdrCallLegEnd.getCallLegEndMediaInfoList();
        for (CdrCallLegEndMediaInfo cdrCallLegEndMediaInfo : callLegEndMediaInfoList)
        {
            cdrCallLegEndMediaInfoService.insertCdrCallLegEndMediaInfo(cdrCallLegEndMediaInfo);
        }
    }
    
    @Override
    @Transactional
    public synchronized void executeAdd(String session, RecordElement recordElement, String fmeIp)
    {
        CdrCallLegEnd cdrCallLegEnd = readToBean(session, recordElement);
        logger.info("转化JavaBean后:{}", cdrCallLegEnd);
        
        cdrCallLegEndService.insertCdrCallLegEnd(cdrCallLegEnd);
        
        // 更新与会者记录表
        if (cdrCallLegEnd.getDurationSeconds() == null || cdrCallLegEnd.getDurationSeconds().intValue() == 0)
        {
            cdrCallLegEnd.setDurationSeconds(null);
        }

        List<CdrCallLegEndMediaInfo> callLegEndMediaInfoList = cdrCallLegEnd.getCallLegEndMediaInfoList();
        List<CdrCallLegEndAlarm> cdrCallLegEndAlarmList = cdrCallLegEnd.getCdrCallLegEndAlarmList();

        if (!CollectionUtils.isEmpty(callLegEndMediaInfoList))
        {
            for (CdrCallLegEndMediaInfo cdrCallLegEndMediaInfo : callLegEndMediaInfoList)
            {
                cdrCallLegEndMediaInfoService.insertCdrCallLegEndMediaInfo(cdrCallLegEndMediaInfo);
            }
        }
        if (!CollectionUtils.isEmpty(cdrCallLegEndAlarmList))
        {
            for (CdrCallLegEndAlarm cdrCallLegEndAlarm : cdrCallLegEndAlarmList)
            {
                cdrCallLegEndAlarmService.insertCdrCallLegEndAlarm(cdrCallLegEndAlarm);
            }
        }

        // 全会议与会者记录 start 2022.05.30
        BusiHistoryAllParticipant busiHistoryAllParticipant = null;
        if (ExternalConfigCache.getInstance().isEnableCdrAll()) {
            busiHistoryAllParticipant = busiHistoryAllParticipantMapper.selectBusiHistoryAllParticipantByCallLegId(cdrCallLegEnd.getCdrId());
            if (busiHistoryAllParticipant != null) {
                if (cdrCallLegEnd.getDurationSeconds() == null) {
                    Date outgoingTime = new Date();
                    busiHistoryAllParticipant.setOutgoingTime(outgoingTime);
                    busiHistoryAllParticipant.setDurationSeconds((int) ((busiHistoryAllParticipant.getOutgoingTime().getTime() - busiHistoryAllParticipant.getJoinTime().getTime()) / 1000));
                } else {
                    Date outgoingTime = new Date(busiHistoryAllParticipant.getJoinTime().getTime() + cdrCallLegEnd.getDurationSeconds() * 1000);
                    busiHistoryAllParticipant.setOutgoingTime(outgoingTime);
                    busiHistoryAllParticipant.setDurationSeconds(cdrCallLegEnd.getDurationSeconds());
                }
                busiHistoryAllParticipant.setMediaInfo(toMediaInfo(cdrCallLegEnd));
                busiHistoryAllParticipantMapper.updateBusiHistoryAllParticipant(busiHistoryAllParticipant);
            }

            BusiHistoryAllParticipant historyAllParticipant = iHistoryAllService.findHistoryParticipantByCallLegId(cdrCallLegEnd.getCdrId());
            if (historyAllParticipant != null) {
                applicationContext.publishEvent(new AllCallLegEndMediaEvent(this, historyAllParticipant.getCallId(), fmeIp, null));
            }
        }
        // 全会议与会者记录 end 2022.05.30
        
        BusiHistoryParticipant busiHistoryParticipant = busiHistoryParticipantMapper.selectBusiHistoryParticipantByCallLegId(cdrCallLegEnd.getCdrId());
        if (busiHistoryParticipant != null)
        {
            if (cdrCallLegEnd.getDurationSeconds() == null)
            {
                Date outgoingTime = new Date();
                busiHistoryParticipant.setOutgoingTime(outgoingTime);
                busiHistoryParticipant.setDurationSeconds((int) ((busiHistoryParticipant.getOutgoingTime().getTime() - busiHistoryParticipant.getJoinTime().getTime()) / 1000));
            }
            else
            {
                Date outgoingTime = new Date(busiHistoryParticipant.getJoinTime().getTime() + cdrCallLegEnd.getDurationSeconds() * 1000);
                busiHistoryParticipant.setOutgoingTime(outgoingTime);
                busiHistoryParticipant.setDurationSeconds(cdrCallLegEnd.getDurationSeconds());
            }
            busiHistoryParticipantMapper.updateBusiHistoryParticipant(busiHistoryParticipant);
            // 更新参会者终端信息
            updateBusiHistoryParticipantTerminal(busiHistoryParticipant);
            // 更新全与会者记录媒体信息 start 2022.05.30
            if (ExternalConfigCache.getInstance().isEnableCdrAll()) {
                if (busiHistoryAllParticipant != null) {
                    if (busiHistoryParticipant.getMediaInfo() != null && busiHistoryParticipant.getMediaInfo().size() > 0) {
                        busiHistoryAllParticipant.setMediaInfo(busiHistoryParticipant.getMediaInfo());
                        busiHistoryAllParticipantMapper.updateBusiHistoryAllParticipant(busiHistoryAllParticipant);
                    }
                }
            }
            // 更新全与会者记录媒体信息 end 2022.05.30
        }
        
        CdrCache.getInstance().removeCallLegSaveMap(cdrCallLegEnd.getCdrId());
        BusiHistoryParticipant historyParticipant = CdrCache.getInstance().getHistoryParticipant(cdrCallLegEnd.getCdrId());
        historyParticipant = (historyParticipant == null) ? iHistoryService.findHistoryParticipantByCallLegId(cdrCallLegEnd.getCdrId()) : historyParticipant;
        if (historyParticipant != null)
        {
            applicationContext.publishEvent(new CallLegEndMediaEvent(this, historyParticipant.getDeptId().longValue(), historyParticipant.getCallId(), null));
        }
        CdrCache.getInstance().removeHistoryParticipantMap(cdrCallLegEnd.getCdrId());

    }
    
    private List<CdrCallLegEndMediaInfo> getMediaInfoList(Integer recordIndex, Integer correlatorIndex, CallLegElement callLeg)
    {
        List<CdrCallLegEndMediaInfo> legEndMediaInfoList = new ArrayList<>();
        if (callLeg.getTxAudio() != null)
        {
            CdrCallLegEndMediaInfo txAudio = buildMediaInfo(recordIndex, correlatorIndex, callLeg.getTxAudio(), callLeg);
            txAudio.setType("txAudio");
            legEndMediaInfoList.add(txAudio);
        }
        if (callLeg.getTxVideo() != null)
        {
            CdrCallLegEndMediaInfo txVideo = buildMediaInfo(recordIndex, correlatorIndex, callLeg.getTxVideo(), callLeg);
            txVideo.setType("txVideo");
            legEndMediaInfoList.add(txVideo);
        }
        if (callLeg.getRxVideo() != null)
        {
            CdrCallLegEndMediaInfo rxVideo = buildMediaInfo(recordIndex, correlatorIndex, callLeg.getRxVideo(), callLeg);
            rxVideo.setType("rxVideo");
            legEndMediaInfoList.add(rxVideo);
        }
        if (callLeg.getRxAudio() != null)
        {
            CdrCallLegEndMediaInfo rxAudio = buildMediaInfo(recordIndex, correlatorIndex, callLeg.getRxAudio(), callLeg);
            rxAudio.setType("rxAudio");
            legEndMediaInfoList.add(rxAudio);
        }
        return legEndMediaInfoList;
    }
    
    private CdrCallLegEndMediaInfo buildMediaInfo(Integer recordIndex, Integer correlatorIndex, MediaInfoElement element, CallLegElement callLeg)
    {
        CdrCallLegEndMediaInfo cdrCallLegEndMediaInfo = new CdrCallLegEndMediaInfo();
        cdrCallLegEndMediaInfo.setCorrelatorIndex(correlatorIndex);
        cdrCallLegEndMediaInfo.setRecordIndex(recordIndex);
        
        cdrCallLegEndMediaInfo.setCdrId(callLeg.getId());
        
        cdrCallLegEndMediaInfo.setCodec(element.getCodec());
        cdrCallLegEndMediaInfo.setMaxSizeHeight(element.getMaxSizeHeight());
        cdrCallLegEndMediaInfo.setMaxSizeWidth(element.getMaxSizeWidth());
        PacketStatisticsElement packetStatistics = element.getPacketStatistics();
        if (packetStatistics != null)
        {
            cdrCallLegEndMediaInfo.setPacketGapDensity((packetStatistics.getPacketGap() == null) ? BigDecimal.ZERO : packetStatistics.getPacketGap().getDensity());
            cdrCallLegEndMediaInfo.setPacketGapDuration((packetStatistics.getPacketGap() == null) ? BigDecimal.ZERO : packetStatistics.getPacketGap().getDuration());
            cdrCallLegEndMediaInfo.setPacketLossBurstsDensity((packetStatistics.getPacketLossBursts() == null) ? BigDecimal.ZERO : packetStatistics.getPacketLossBursts().getDensity());
            cdrCallLegEndMediaInfo.setPacketLossBurstsDuration((packetStatistics.getPacketLossBursts() == null) ? BigDecimal.ZERO : packetStatistics.getPacketLossBursts().getDuration());
        }
        else
        {
            cdrCallLegEndMediaInfo.setPacketGapDensity(BigDecimal.ZERO);
            cdrCallLegEndMediaInfo.setPacketGapDuration(BigDecimal.ZERO);
            cdrCallLegEndMediaInfo.setPacketLossBurstsDensity(BigDecimal.ZERO);
            cdrCallLegEndMediaInfo.setPacketLossBurstsDuration(BigDecimal.ZERO);
        }
        cdrCallLegEndMediaInfo.setCreateTime(new Date());
        return cdrCallLegEndMediaInfo;
    }
    
    private List<CdrCallLegEndAlarm> getAlarmList(Date time, CallLegElement callLeg)
    {
        List<CdrCallLegEndAlarm> legEndAlarmList = new ArrayList<>();
        List<AlarmElement> alarmElementList = callLeg.getAlarm();
        if (!CollectionUtils.isEmpty(alarmElementList))
        {
            for (AlarmElement alarmElement : alarmElementList)
            {
                CdrCallLegEndAlarm cdrCallLegEndAlarm = new CdrCallLegEndAlarm();
                
                cdrCallLegEndAlarm.setCdrLegEndId(callLeg.getId());
                cdrCallLegEndAlarm.setType(CallLegEndAlarmTypeEnum.getEnumObjectByName(alarmElement.getType()));
                cdrCallLegEndAlarm.setDurationPercentage(alarmElement.getDurationPercentage());
                cdrCallLegEndAlarm.setTime(new Date());
                cdrCallLegEndAlarm.setCreateTime(new Date());
                legEndAlarmList.add(cdrCallLegEndAlarm);
            }
        }
        return legEndAlarmList;
    }

    public JSONObject toMediaInfo(CdrCallLegEnd cdrCallLegEnd) {
        if (cdrCallLegEnd != null) {
            CdrCallLegStart cdrCallLegStartCon = new CdrCallLegStart();
            cdrCallLegStartCon.setCdrId(cdrCallLegEnd.getCdrId());
            List<CdrCallLegStart> cdrCallLegStartList = cdrCallLegStartMapper.selectCdrCallLegStartList(cdrCallLegStartCon);
            if (cdrCallLegStartList != null && cdrCallLegStartList.size() > 0) {
                CdrCallLegStart cdrCallLegStart = cdrCallLegStartList.get(0);
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("direction", cdrCallLegStart.getDirection());
                jsonObj.put("type", cdrCallLegStart.getType());
                jsonObj.put("isEncrypted", cdrCallLegEnd.getEncryptedMedia());
                jsonObj.put("durationSeconds", cdrCallLegEnd.getDurationSeconds());
                jsonObj.put("sipCallId", cdrCallLegEnd.getSipCallId());
                jsonObj.put("remoteParty", cdrCallLegStart.getRemoteParty());

                JSONObject downLink = new JSONObject();
                jsonObj.put("downLink", downLink);

                JSONObject upLink = new JSONObject();
                jsonObj.put("upLink", upLink);

                List<CdrCallLegEndMediaInfo> callLegEndMediaInfoList = cdrCallLegEnd.getCallLegEndMediaInfoList();
                for (CdrCallLegEndMediaInfo cdrCallLegEndMediaInfo : callLegEndMediaInfoList) {
                    // 下行视频
                    if ("txVideo".equals(cdrCallLegEndMediaInfo.getType())) {
                        JSONArray videos = downLink.getJSONArray("videos");
                        if (videos == null) {
                            videos = new JSONArray();
                            downLink.put("videos", videos);
                        }
                        {
                            JSONObject video = new JSONObject();
                            video.put("role", "main");
                            video.put("resolutionRatio", cdrCallLegEndMediaInfo.getMaxSizeWidth() != null ? cdrCallLegEndMediaInfo.getMaxSizeWidth() + "x" + cdrCallLegEndMediaInfo.getMaxSizeHeight() : null);
                            video.put("frameRate", null);
                            video.put("videoCodec", cdrCallLegEndMediaInfo.getCodec());
                            video.put("bandwidth", null);
                            video.put("jitter", null);
                            video.put("roundTripTime", null);
                            video.put("packetLossPercentage", cdrCallLegEndMediaInfo.getPacketLossBurstsDensity());

                            videos.add(video);
                        }
                        // 辅流
                        if (cdrCallLegEnd.getPresentationViewer() != null) {
                            JSONObject video = new JSONObject();
                            video.put("role", "presentation");
                            video.put("resolutionRatio", null);
                            video.put("frameRate", null);
                            video.put("videoCodec", cdrCallLegEndMediaInfo.getCodec());
                            video.put("bandwidth", null);
                            video.put("jitter", null);
                            video.put("roundTripTime", null);
                            video.put("packetLossPercentage", 0);

                            videos.add(video);
                        }
                    }

                    if ("txAudio".equals(cdrCallLegEndMediaInfo.getType())) {
                        JSONObject audio = new JSONObject();
                        audio.put("codec", cdrCallLegEndMediaInfo.getCodec());
                        audio.put("bandwidth", null);
                        audio.put("packetLossPercentage", cdrCallLegEndMediaInfo.getPacketLossBurstsDensity());
                        audio.put("codecBitRate", null);
                        audio.put("jitter", null);
                        audio.put("roundTripTime", null);
                        audio.put("gainApplied", null);

                        downLink.put("audio", audio);
                    }

                    // 上行视频
                    if ("rxVideo".equals(cdrCallLegEndMediaInfo.getType())) {
                        JSONArray videos = upLink.getJSONArray("videos");
                        if (videos == null) {
                            videos = new JSONArray();
                            downLink.put("videos", videos);
                        }
                        {
                            JSONObject video = new JSONObject();
                            video.put("role", "main");
                            video.put("resolutionRatio", cdrCallLegEndMediaInfo.getMaxSizeWidth() != null ? cdrCallLegEndMediaInfo.getMaxSizeWidth() + "x" + cdrCallLegEndMediaInfo.getMaxSizeHeight() : null);
                            video.put("frameRate", null);
                            video.put("videoCodec", cdrCallLegEndMediaInfo.getCodec());
                            video.put("bandwidth", null);
                            video.put("jitter", null);
                            video.put("roundTripTime", null);
                            video.put("packetLossPercentage", cdrCallLegEndMediaInfo.getPacketLossBurstsDensity());

                            videos.add(video);
                        }
                        // 辅流
                        if (cdrCallLegEnd.getPresentationContributor() != null) {
                            JSONObject video = new JSONObject();
                            video.put("role", "presentation");
                            video.put("resolutionRatio", null);
                            video.put("frameRate", null);
                            video.put("videoCodec", cdrCallLegEndMediaInfo.getCodec());
                            video.put("bandwidth", null);
                            video.put("jitter", null);
                            video.put("roundTripTime", null);
                            video.put("packetLossPercentage", 0);

                            videos.add(video);
                        }
                    }

                    if ("rxAudio".equals(cdrCallLegEndMediaInfo.getType())) {
                        JSONObject audio = new JSONObject();
                        audio.put("codec", cdrCallLegEndMediaInfo.getCodec());
                        audio.put("bandwidth", null);
                        audio.put("packetLossPercentage", cdrCallLegEndMediaInfo.getPacketLossBurstsDensity());
                        audio.put("codecBitRate", null);
                        audio.put("jitter", null);
                        audio.put("roundTripTime", null);
                        audio.put("gainApplied", null);

                        downLink.put("audio", audio);
                    }
                }

                return jsonObj;
            }
        }
        return null;
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
