package com.paradisecloud.smc.service.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.ParticipantRspDto;
import com.paradisecloud.com.fcm.smc.modle.ParticipantState;
import com.paradisecloud.com.fcm.smc.modle.SmcConferenceContext;
import com.paradisecloud.fcm.smc.cache.modle.SmcConferenceContextCache;
import com.paradisecloud.fcm.smc.cache.modle.SmcUpdateParticipantQueue;
import com.paradisecloud.smc.service.IBusiSmc3HistoryConferenceService;
import com.sinhy.model.AsyncBlockingMessageProcessor;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ReflectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * @author nj
 * @date 2023/6/21 14:03
 */
@Component
public class SmcUpdateParticipantProcessor extends AsyncBlockingMessageProcessor<JSONObject> implements InitializingBean {

    public static final int type_3 = 3;

    public SmcUpdateParticipantProcessor() {

        super("SmcUpdateParticipantQueue", (SmcUpdateParticipantQueue)(ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(SmcUpdateParticipantQueue.class, "getInstance"), null)));
    }

    @Override
    protected void process(JSONObject jsonObject) {
        try {
            String conferenceId = (String) jsonObject.get("conferenceId");
            Object changeList = jsonObject.get("changeList");
            String subscription = (String) jsonObject.get("subscription");
            Object type1 = jsonObject.get("type");
            if (type1 == null) {
                return;
            }
            int type = (int) jsonObject.get("type");
            if (Objects.isNull(changeList)) {
                return;
            }
            List<JSONObject> stateList = JSONArray.parseArray(JSON.toJSONString(changeList), JSONObject.class);

            SmcConferenceContext smcConferenceContext = SmcConferenceContextCache.getInstance().getSmcConferenceContextMap().get(conferenceId);
            if (smcConferenceContext == null) {
                return;
            }
            if (type == type_3) {
                for (JSONObject object : stateList) {
                    if (smcConferenceContext != null) {
                        List<ParticipantRspDto> participants = smcConferenceContext.getParticipants();
                        if (CollectionUtils.isEmpty(participants)) {
                            return;
                        }
                        Optional<ParticipantRspDto> optional = participants.stream().filter(p -> Objects.equals(p.getId(), (String) object.get("participantId"))).findFirst();
                        if (optional.isPresent()) {
                            ParticipantRspDto participantRspDto = optional.get();
                            participantRspDto.setIsOnline(true);
                            processUpdateParticipant(smcConferenceContext, participantRspDto, true, subscription);
                        }
                    }
                }
            }else if(type==1) {
                for (JSONObject object : stateList) {
                    ParticipantRspDto contentDTO = new ParticipantRspDto();
                    contentDTO.setId((String) object.get("id"));
                    contentDTO.setName((String) object.get("name"));
                    contentDTO.setUri((String) object.get("uri"));
                    contentDTO.setIpProtocolType((int) object.get("type"));
                    ParticipantState participantState = new ParticipantState();
                    participantState.setParticipantId((String) object.get("id"));
                    participantState.setOnline(true);
                    contentDTO.setParticipantState(participantState);
                    if(smcConferenceContext!=null){
                        processUpdateParticipant(smcConferenceContext,contentDTO,true,subscription);
                    }
                }
            }else {
                for (JSONObject object : stateList) {
                    if(smcConferenceContext!=null) {
                        List<ParticipantRspDto> participants = smcConferenceContext.getParticipants();
                        if (participants.isEmpty()) {
                            return;
                        }
                        Optional<ParticipantRspDto> optional = participants.stream().filter(p -> Objects.equals(p.getId(), (String) object.get("id"))).findFirst();
                        if (optional.isPresent()) {
                            ParticipantRspDto participantRspDto = optional.get();
                            participantRspDto.setIsOnline(false);
                            Integer callFailReason = (Integer) object.get("callFailReason");
                            ParticipantState participantState = new ParticipantState();
                            participantState.setParticipantId((String) object.get("id"));
                            participantState.setCallFailReason(callFailReason);
                            participantRspDto.setParticipantState(participantState);
                            processUpdateParticipant(smcConferenceContext, participantRspDto, false, subscription);
                        }
                    }
                }
            }




        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }

    private void processUpdateParticipant(SmcConferenceContext conferenceContext,ParticipantRspDto contentDTO, boolean updateMediaInfo,String subscription) {
        IBusiSmc3HistoryConferenceService busiSmc3HistoryConferenceService = BeanFactory.getBean(IBusiSmc3HistoryConferenceService.class);
        busiSmc3HistoryConferenceService.updateBusiHistoryParticipant(conferenceContext, contentDTO, updateMediaInfo,subscription);
    }
}
