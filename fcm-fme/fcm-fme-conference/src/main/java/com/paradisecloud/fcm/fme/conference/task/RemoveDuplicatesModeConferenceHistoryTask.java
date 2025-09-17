package com.paradisecloud.fcm.fme.conference.task;

import com.alibaba.fastjson.JSONArray;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateParticipantMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.paradisecloud.fcm.dao.model.BusiTemplateParticipant;
import com.sinhy.spring.BeanFactory;

import java.util.*;


public class RemoveDuplicatesModeConferenceHistoryTask extends Task {
    public RemoveDuplicatesModeConferenceHistoryTask(String id, long delayInMilliseconds) {
        super("remove_duplicates_c_" + id, delayInMilliseconds);
    }

    @Override
    public void run() {
        BusiHistoryConferenceMapper busiHistoryConferenceMapper = BeanFactory.getBean(BusiHistoryConferenceMapper.class);
        BusiTemplateConferenceMapper busiTemplateConferenceMapper = BeanFactory.getBean(BusiTemplateConferenceMapper.class);
        BusiTemplateParticipantMapper busiTemplateParticipantMapper = BeanFactory.getBean(BusiTemplateParticipantMapper.class);
        List<BusiHistoryConference> needRemoveModeConferenceHistoryList = new ArrayList<>();
        HashSet<String> checkKeySet = new HashSet<>();
        List<BusiHistoryConference> busiHistoryConferenceList = busiHistoryConferenceMapper.selectBusiHistoryConferenceModeList();
        for (int i = 0; i < busiHistoryConferenceList.size(); i++) {
            BusiHistoryConference busiHistoryConference = busiHistoryConferenceList.get(i);
            BusiTemplateConference busiTemplateConference = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiHistoryConference.getTemplateId());
            if (busiTemplateConference == null) {
                needRemoveModeConferenceHistoryList.add(busiHistoryConference);
                continue;
            }
            StringBuilder checkSb = new StringBuilder();
            checkSb.append(busiHistoryConference.getNumber()).append("_");
            checkSb.append(busiTemplateConference.getName()).append("_");
            checkSb.append(busiTemplateConference.getBandwidth()).append("_");
            checkSb.append(busiTemplateConference.getStreamingEnabled()).append("_");
            checkSb.append(busiTemplateConference.getRecordingEnabled()).append("_");
            if (StringUtils.isNotEmpty(busiTemplateConference.getConferenceMode())) {
                checkSb.append(busiTemplateConference.getConferenceMode()).append("_");
            } else {
                checkSb.append("0_");
            }
            checkSb.append(busiTemplateConference.getDurationTime()).append("_");
            String isMute = "false";
            String quality = "720p";
            String cloudMcus = "cms(";
            Map<String, Object> businessProperties = busiTemplateConference.getBusinessProperties();
            if (businessProperties != null) {
                Object cloudMcuType = businessProperties.get("cloudMcuType");
                if (cloudMcuType != null) {
                    if (cloudMcuType instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray) cloudMcuType;
                        for (Object cloudMcuObj : jsonArray) {
                            cloudMcus += cloudMcuObj + "_";
                        }
                    }
                    if (cloudMcuType instanceof String) {
                        cloudMcus += cloudMcuType + "_";
                    }
                }
                Object isMuteObj = businessProperties.get("isMute");
                if (isMuteObj != null) {
                    isMute = isMuteObj.toString();
                }
                Object qualityObj = businessProperties.get("quality");
                if (qualityObj != null) {
                    quality = qualityObj.toString();
                }
            }
            cloudMcus += ")";
            checkSb.append(isMute).append("_");
            checkSb.append(quality).append("_");
            checkSb.append(cloudMcus).append("_");
            String terminals = "tms(";
            TreeSet<Long> terminalIdSet = new TreeSet<>();
            BusiTemplateParticipant busiTemplateParticipantCon = new BusiTemplateParticipant();
            busiTemplateParticipantCon.setTemplateConferenceId(busiHistoryConference.getTemplateId());
            List<BusiTemplateParticipant> busiTemplateParticipantList = busiTemplateParticipantMapper.selectBusiTemplateParticipantList(busiTemplateParticipantCon);
            for (BusiTemplateParticipant busiTemplateParticipant : busiTemplateParticipantList) {
                terminalIdSet.add(busiTemplateParticipant.getTerminalId());
            }
            SortedSet<Long> sortedSet = terminalIdSet.tailSet(0L);
            for (Long terminalId : sortedSet) {
                terminals += terminalId + "_";
            }
            terminals += ")";
            checkSb.append(terminals).append("_");

            String checkKey = checkSb.toString();
            if (checkKeySet.contains(checkKey)) {
                needRemoveModeConferenceHistoryList.add(busiHistoryConference);
            } else {
                checkKeySet.add(checkKey);
            }
        }
        for (BusiHistoryConference busiHistoryConference : needRemoveModeConferenceHistoryList) {
            busiHistoryConferenceMapper.deleteModeHistoryConference(busiHistoryConference);
        }
    }
}
