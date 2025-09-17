package com.paradisecloud.smc3.monitor;

import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.operation.AttendeeOperation;
import com.paradisecloud.smc3.busi.operation.TalkPrivateAttendeeOperation;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.model.ConstAPI;
import com.paradisecloud.smc3.model.ParticipantStatusDto;
import com.paradisecloud.smc3.model.request.MultiPicInfoReq;
import com.paradisecloud.smc3.model.response.SmcParitipantsStateRep;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author nj
 * @date 2023/9/5 15:21
 */
@Component
public class ConferenceSmc3AttendeeOperationThread extends Thread implements InitializingBean {

    public static Set<AttendeeSmc3> setSmc3 = new HashSet();
    public static Set<AttendeeSmc3> removeSmc3 = new HashSet();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static Set<AttendeeSmc3> getSmc3Set() {
        return setSmc3;
    }

    public static void add(AttendeeSmc3 attendeeSmc3) {
        setSmc3.add(attendeeSmc3);
    }

    @Override
    public void run() {

        while (true) {

            try {
                Collection<Smc3ConferenceContext> values = Smc3ConferenceContextCache.getInstance().values();

                if (CollectionUtils.isNotEmpty(values)) {

                    for (Smc3ConferenceContext conferenceContext : values) {

                        if (conferenceContext != null && conferenceContext.isStart()) {
                            if (conferenceContext != null) {
                                try {
                                    if (conferenceContext.isEnd()) {
                                        break;
                                    }
                                    AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
                                    Smc3Bridge smc3Bridge = conferenceContext.getSmc3Bridge();
                                    String conferenceId = conferenceContext.getSmc3conferenceId();
                                    if (attendeeOperation != null) {
                                        try {
                                            if (attendeeOperation instanceof TalkPrivateAttendeeOperation) {
                                                List<String> participantIds = conferenceContext.getMultiPicInfoTalkReq().getParticipantIds();
                                                Iterator<AttendeeSmc3> iterator = setSmc3.iterator();
                                                List<ParticipantStatusDto> participantStatusList = new ArrayList<>();
                                                while (iterator.hasNext()) {
                                                    AttendeeSmc3 attendeeSmc3 = iterator.next();

                                                    AttendeeSmc3 attendee = conferenceContext.getAttendeeById(attendeeSmc3.getId());
                                                    if (attendee != null && !participantIds.contains(attendee.getSmcParticipant().getGeneralParam().getId())) {
                                                        //观看自己
                                                        SmcParitipantsStateRep.ContentDTO contentDTO = attendeeSmc3.getSmcParticipant();

                                                        ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
                                                        participantStatusList.add(participantStatusDto);
                                                        String id = contentDTO.getGeneralParam().getId();
                                                        participantStatusDto.setId(id);
                                                        participantStatusDto.setIsQuiet(true);
                                                        participantStatusDto.setIsMute(true);
                                                        MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTOOne = new MultiPicInfoReq.MultiPicInfoDTO();
                                                        multiPicInfoDTOOne.setPicNum(1);
                                                        multiPicInfoDTOOne.setMode(1);
                                                        List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList = new ArrayList<>();
                                                        MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO = new MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO();
                                                        subPicListDTO.setParticipantId(id);
                                                        subPicListDTO.setStreamNumber(0);
                                                        subPicList.add(subPicListDTO);
                                                        multiPicInfoDTOOne.setSubPicList(subPicList);

                                                        conferencesControlChoose(conferenceId, id, multiPicInfoDTOOne, smc3Bridge);
                                                        //关闭声音 麦克风
                                                        //发送字幕
                                                        if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                                                            smc3Bridge.getSmcParticipantsInvoker().textTipsSettingCascade(conferenceId, id, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                                                        } else {
                                                            smc3Bridge.getSmcParticipantsInvoker().textTipsSetting(conferenceId, id, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                                                        }
                                                        iterator.remove();
                                                    }

                                                    removeSmc3.add(attendeeSmc3);

                                                }
                                            } else {
                                                Iterator<AttendeeSmc3> iterator = removeSmc3.iterator();
                                                while (iterator.hasNext()) {
                                                    AttendeeSmc3 attendeeSmc3 = iterator.next();
                                                    if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                                                        smc3Bridge.getSmcParticipantsInvoker().textTipsSettingCancelCascade(conferenceId, attendeeSmc3.getSmcParticipant().getGeneralParam().getId(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                                                    } else {
                                                        smc3Bridge.getSmcParticipantsInvoker().textTipsSettingCancel(conferenceId, attendeeSmc3.getSmcParticipant().getGeneralParam().getId(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                                                    }
                                                    iterator.remove();
                                                }
                                            }
                                        } catch (Exception e) {
                                            logger.info(e.getMessage());
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    logger.info(e.getMessage());
                                } finally {
                                    Threads.sleep(200);
                                }
                            }

                        }

                    }

                }
            } catch (Exception e) {
                logger.info(e.getMessage());
            } finally {
                Threads.sleep(1000);
            }

        }
    }

    public void conferencesControlChoose(String conferenceId, String participantId, MultiPicInfoReq.MultiPicInfoDTO multiPicInfoD, Smc3Bridge bridge) {
        bridge.getSmcConferencesInvoker().conferencesControlChoose(conferenceId, participantId, multiPicInfoD, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}
