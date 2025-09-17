package com.paradisecloud.fcm.smc2.model.notice;


import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.*;
import com.paradisecloud.com.fcm.smc.modle.request.MultiPicPollRequest;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.BroadcastStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.smc2.cache.Smc2Bridge;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContextCache;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.conference.updateprocess.SelfCallAttendeeNewSmc2Processor;
import com.paradisecloud.fcm.smc2.model.AttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeCountingStatistics;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.operation.PollingAttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.operation.TalkAttendeeOperation;
import com.paradisecloud.fcm.smc2.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq;
import com.paradisecloud.fcm.smc2.service.IBusiSmc2HistoryConferenceService;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiSmc2ConferenceService;
import com.paradisecloud.fcm.smc2.utils.AttendeeSmc2Utils;
import com.sinhy.core.processormessage.ProcessorMessage;
import com.sinhy.spring.BeanFactory;
import com.suntek.smc.esdk.pojo.local.*;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author nj
 * @date 2023/4/26 10:54
 */
public class OngoingConfNotificationExMessage extends ProcessorMessage<JSONObject> {

    public static final String CP = "(%CP)";

    private OngoingConfNotificationEx ongoingConfNotificationEx;
    private Smc2Bridge smc2Bridge;

    public OngoingConfNotificationExMessage(OngoingConfNotificationEx ongoingConfNotificationEx, Smc2Bridge smc2Bridge) {
        super(JSONObject.parseObject(JSONObject.toJSONString(ongoingConfNotificationEx)), ongoingConfNotificationEx.getConfId());
        this.ongoingConfNotificationEx = ongoingConfNotificationEx;
        this.smc2Bridge = smc2Bridge;
    }

    public static ParticipantRspDto setMasterParticipant(Smc2ConferenceContext smc2ConferenceContext, AttendeeSmc2 value) {
        ParticipantRspDto participantRspDto = new ParticipantRspDto();
        participantRspDto.setUri(value.getSmcParticipant().getGeneralParam().getUri());
        participantRspDto.setName(value.getSmcParticipant().getGeneralParam().getName());
        participantRspDto.setId(value.getSmcParticipant().getState().getParticipantId());
        participantRspDto.setDeptId(value.getDeptId());
        participantRspDto.setIsOnline(value.getSmcParticipant().getState().getOnline());
        participantRspDto.setTerminalId(value.getTerminalId());
        participantRspDto.setIsMute(value.getSmcParticipant().getState().getMute());
        smc2ConferenceContext.setMasterParticipant(participantRspDto);
        return participantRspDto;
    }

    /**
     * 通知事件类型。
     * 0：start。订阅的会议开始时，返回事件类型为该类型的活动会议通知消息。
     * 1：end。订阅的会议开始后被结束，返回事件类型为该类型的活动会议通知消息。
     * 2：change。订阅的会议开始后被修改，返回事件类型为该类型的活动会议通知消息。如延长会议时间、设置声控切换、添加会场、呼叫会场等事件。
     * 3：site_add。订阅的会议已开始且向会议中添加会场，返回事件类型为该类型的活动会议通知消息。
     * 4：site_delete。订阅的会议已开始且从会议中删除会场，返回事件类型为该类型的活动会议通知消息。
     *  5：site_status_change。订阅的会议已开始且会议中的会场状态发生变化，返回事件类型为该类型的活动会议通知消息。如会场入会、会场被挂断、呼叫会场失败等事件。
     */
    @Override
    protected void process0() {

        String confId = ongoingConfNotificationEx.getConfId();
        Integer event = ongoingConfNotificationEx.getEvent();
        String siteUri = ongoingConfNotificationEx.getSiteUri();

        List<NotificationStatusEx> changes = ongoingConfNotificationEx.getChanges();
        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(confId);
        if (smc2ConferenceContext == null) {
            return;
        }
        Map<String, AttendeeSmc2> uuidAttendeeMap = null;
        if (siteUri != null) {
            uuidAttendeeMap = smc2ConferenceContext.getUuidAttendeeMapByUri(siteUri);
        }
        switch (event) {
            case 0:
                break;
            case 1:
                if (smc2ConferenceContext != null) {
                    try {
                        BeanFactory.getBean(IBusiSmc2ConferenceService.class).endConference(smc2ConferenceContext.getContextKey(), EndReasonsType.AUTO_END);
                    } catch (Exception e) {
                        logger.info("smc2 notice end conference error",e.getMessage());
                    }
                }
                break;
            case 2:
                //订阅的会议开始后被修改，
                if (!CollectionUtils.isEmpty(changes)) {
                    for (NotificationStatusEx notificationStatusEx : changes) {

                        if (notificationStatusEx instanceof ConferenceStatusEx) {
                            DetailConference detailConference = smc2ConferenceContext.getDetailConference();
                            ConferenceState conferenceState = new ConferenceState();
                            ConferenceUiParam conferenceUiParam = detailConference.getConferenceUiParam();
                            ConferenceStatusEx conferenceStatusEx = (ConferenceStatusEx) notificationStatusEx;
                            //广播会场
                            /**
                             * 0：未知状态（保留）
                             * 1：会议不存在
                             * 2：会议已调度，未召开
                             * 3：会议已经召开
                             * 4：没有权限查询会议状态
                             * 5：会议已经结束
                             */
                            Integer status = conferenceStatusEx.getStatus();
                            String broadcast = conferenceStatusEx.getBroadcast();//广播会场。
                            String chair = conferenceStatusEx.getChair();//主席会场
                            /**
                             * 会议是否锁定。
                             * 0：否
                             * 1：是
                             */
                            Integer isLock = conferenceStatusEx.getIsLock();
                            String speaking = conferenceStatusEx.getSpeaking();//正在发言的会场（音量最大的会场）
                            String presentation = conferenceStatusEx.getPresentation();//正在发送辅流的会场
                            /**
                             * 会议是否处于锁定辅流令牌状态。
                             * 0：否
                             * 1：是
                             */
                            Integer isLockPresentation = conferenceStatusEx.getIsLockPresentation();
                            if (isLockPresentation == 0) {
                                conferenceState.setLockPresenterId("");
                            } else {
                                conferenceState.setLockPresenterId("00000000-0000-0000-0000-000000000000");
                            }

                            /**
                             * 锁定辅流令牌的会场
                             */
                            String lockPresentation = conferenceStatusEx.getLockPresentation();
                            /**
                             * 是否支持录播/直播及其相关状态
                             */
                            RecordStatusEx recording = conferenceStatusEx.getRecording();


                            Integer isAudioSwitch = conferenceStatusEx.getIsAudioSwitch();
                            if (isAudioSwitch == 0) {
                                conferenceState.setEnableVoiceActive(false);
                            } else {
                                conferenceState.setEnableVoiceActive(true);
                            }
                            smc2ConferenceContext.setEnableVoiceActive(conferenceState.getEnableVoiceActive());
                            if (isLock != null) {
                                conferenceState.setLock(isLock == 1 ? true : false);
                                smc2ConferenceContext.setLocked(conferenceState.getLock());
                            }
                            if (status != null) {
                                if (status == 3) {
                                    //smc2ConferenceContext.getConference().setStage("ONLINE");
                                }
                                if (status == 5) {
                                    smc2ConferenceContext.getConference().setStage("CANCEL");
                                    BeanFactory.getBean(IBusiSmc2ConferenceService.class).endConference(smc2ConferenceContext.getId(), EndReasonsType.AUTO_END, true, true);
                                }
                                if (status == 2) {
                                    // smc2ConferenceContext.getConference().setStage("OFFLINE");
                                }
                            }
                            if (Strings.isNotBlank(broadcast)) {
                                if (Objects.equals("(%CP)", broadcast)) {

                                    conferenceState.setBroadcastId("00000000-0000-0000-0000-000000000000");
                                    smc2ConferenceContext.setMultiPicBroadcastStatus(true);
                                    if (Objects.equals("START", smc2ConferenceContext.getMultiPicPollStatus())) {



                                        com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq.MultiPicInfoDTO multiPicInfo = new com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq.MultiPicInfoDTO();
                                        //查询广播多画面
                                        ConferenceServiceEx conferenceServiceEx = smc2ConferenceContext.getSmc2Bridge().getConferenceServiceEx();
                                        GetContinuousPresenceParamExResponse result
                                                = conferenceServiceEx.getContinuousPresenceParamEx(confId, "(%CP)");
                                        if (0 == result.getResultCode()) {
                                            Set<String> broadSet = new HashSet<>();
                                            List<com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> list = new ArrayList<>();
                                            List<String> subPics = result.getSubPics();
                                            for (String subPic : subPics) {
                                                com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO = new com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO();
                                                String pantId = smc2ConferenceContext.getParticiPantIdBySiteUri(subPic);
                                                subPicListDTO.setParticipantId(pantId);
                                                broadSet.add(pantId);
                                                list.add(subPicListDTO);
                                            }
                                            multiPicInfo.setPicNum(smc2ConferenceContext.getMultiPicPollRequest().getPicNum());
                                            multiPicInfo.setSubPicList(list);

                                            AttendeeOperation attendeeOperation = smc2ConferenceContext.getAttendeeOperation();
                                            if (attendeeOperation instanceof PollingAttendeeOperation) {
                                                PollingAttendeeOperation pollingAttendeeOperation = (PollingAttendeeOperation) attendeeOperation;
                                                if (!pollingAttendeeOperation.isCascadePolling(smc2ConferenceContext, smc2ConferenceContext.getMultiPicPollRequest())) {
                                                    List<AttendeeSmc2> pollingAttendeeSmc2List = pollingAttendeeOperation.getPollingAttendeeSmc2List();
                                                    if (org.apache.commons.collections.CollectionUtils.isNotEmpty(pollingAttendeeSmc2List)) {
                                                        for (AttendeeSmc2 attendeeSmc2 : pollingAttendeeSmc2List) {
                                                            boolean contains = broadSet.contains(attendeeSmc2.getParticipantUuid());
                                                            if (contains) {
                                                                AttendeeSmc2 attendeeBySmc2Id = smc2ConferenceContext.getAttendeeById(attendeeSmc2.getParticipantUuid());
                                                                if (attendeeBySmc2Id != null) {
                                                                    AttendeeImportance.ROUND_BROADCAST.processAttendeeWebsocketMessage(attendeeBySmc2Id);
                                                                }
                                                                AttendeeImportance.ROUND_BROADCAST.processAttendeeWebsocketMessage(attendeeSmc2);
                                                            } else {
                                                                if (attendeeSmc2.getBroadcastStatus() == BroadcastStatus.YES.getValue()) {
                                                                    AttendeeImportance.COMMON.processAttendeeWebsocketMessage(attendeeSmc2);
                                                                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc2ConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeSmc2.getUpdateMap());
                                                                }
                                                            }

                                                        }
                                                    }
                                                }
                                            }

                                        }
                                        smc2ConferenceContext.setMultiPicInfo(multiPicInfo);


                                    }

                                } else {
                                    Map<String, AttendeeSmc2> uuidAttendeeMapByUri = smc2ConferenceContext.getUuidAttendeeMapByUri(broadcast);
                                    if (uuidAttendeeMapByUri != null) {
                                        Collection<AttendeeSmc2> values = uuidAttendeeMapByUri.values();
                                        for (AttendeeSmc2 value : values) {
                                            conferenceState.setBroadcastId(value.getSmcParticipant().getGeneralParam().getId());
                                        }
                                    }
                                }
                                smc2ConferenceContext.setBroadId(conferenceState.getBroadcastId());
                            }
                            if (Strings.isNotBlank(speaking)) {
                                Map<String, AttendeeSmc2> uuidAttendeeMapByUri = smc2ConferenceContext.getUuidAttendeeMapByUri(speaking);
                                if (uuidAttendeeMapByUri != null) {
                                    Collection<AttendeeSmc2> values = uuidAttendeeMapByUri.values();
                                    for (AttendeeSmc2 value : values) {
                                        List<String> speakers = new ArrayList<>();
                                        speakers.add(value.getSmcParticipant().getGeneralParam().getId());
                                        conferenceState.setCurrentSpeakers(speakers);
                                    }
                                }
                            }
                            String presenterId="";
                            String presenterId_old=smc2ConferenceContext.getPresentationId();
                            if (Strings.isNotBlank(presentation)) {
                                Map<String, AttendeeSmc2> uuidAttendeeMapByUri = smc2ConferenceContext.getUuidAttendeeMapByUri(presentation);
                                if (uuidAttendeeMapByUri != null) {
                                    Collection<AttendeeSmc2> values = uuidAttendeeMapByUri.values();
                                    for (AttendeeSmc2 value : values) {
                                        conferenceState.setPresenterId(value.getSmcParticipant().getGeneralParam().getId());
                                        value.setPresentStatus(YesOrNo.YES.getValue());
                                        presenterId=value.getId();
                                        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc2ConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, value.getUpdateMap());
                                    }
                                }
                            } else {
                                conferenceState.setPresenterId("");
                            }

                            if(Strings.isNotBlank(presenterId_old)){
                                if(!Objects.equals(presenterId_old, presenterId)){
                                    AttendeeSmc2 attendee = smc2ConferenceContext.getAttendeeById(presenterId_old);
                                    attendee.setPresentStatus(YesOrNo.NO.getValue());
                                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc2ConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                                }
                            }

                            String lockPresenterId="";
                            if (Strings.isNotBlank(lockPresentation)) {
                                Map<String, AttendeeSmc2> uuidAttendeeMapByUri = smc2ConferenceContext.getUuidAttendeeMapByUri(lockPresentation);
                                if (uuidAttendeeMapByUri != null) {
                                    Collection<AttendeeSmc2> values = uuidAttendeeMapByUri.values();
                                    for (AttendeeSmc2 value : values) {
                                        conferenceState.setLockPresenterId(value.getSmcParticipant().getGeneralParam().getId());
                                        lockPresenterId=value.getId();
                                    }
                                }
                            } else {
                                conferenceState.setLockPresenterId("");
                            }

                            String lockPresenterId_o = smc2ConferenceContext.getLockPresenterId();
                            if (Strings.isBlank(lockPresenterId)) {
                                if (Strings.isNotBlank(lockPresenterId_o)) {
                                    AttendeeSmc2 attendeeBySmc2Id = smc2ConferenceContext.getAttendeeById(lockPresenterId_o);
                                    if (attendeeBySmc2Id != null) {
                                        attendeeBySmc2Id.setLockPresenter(false);
                                        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc2ConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeBySmc2Id.getUpdateMap());
                                    }
                                }
                                smc2ConferenceContext.setLockPresenterStatus(false);
                            } else {
                                AttendeeSmc2 attendeeBySmc2Id = smc2ConferenceContext.getAttendeeById(lockPresenterId);
                                if (attendeeBySmc2Id != null) {
                                    attendeeBySmc2Id.setLockPresenter(true);
                                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc2ConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeBySmc2Id.getUpdateMap());
                                }
                                smc2ConferenceContext.setLockPresenterStatus(true);

                                if (Strings.isNotBlank(lockPresenterId_o)) {
                                    AttendeeSmc2 attendeeBySmc3Id_O = smc2ConferenceContext.getAttendeeBySmc2Id(lockPresenterId_o);
                                    if (attendeeBySmc3Id_O != null) {
                                        attendeeBySmc3Id_O.setLockPresenter(false);
                                        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc2ConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeBySmc3Id_O.getUpdateMap());
                                    }
                                }
                            }
                            smc2ConferenceContext.setLockPresenterId(lockPresenterId);
                            smc2ConferenceContext.setPresentation(presentation);
                            smc2ConferenceContext.setPresentationId(presenterId);
                            smc2ConferenceContext.setPresentAttendeeId(presenterId);

                            if (Strings.isNotBlank(chair)) {
                                Map<String, AttendeeSmc2> uuidAttendeeMapByUri = smc2ConferenceContext.getUuidAttendeeMapByUri(chair);
                                if (uuidAttendeeMapByUri != null) {
                                    Collection<AttendeeSmc2> values = uuidAttendeeMapByUri.values();
                                    for (AttendeeSmc2 value : values) {
                                        conferenceState.setChairmanId(value.getSmcParticipant().getGeneralParam().getId());
                                        smc2ConferenceContext.setChairmanId(value.getSmcParticipant().getGeneralParam().getId());
                                        ParticipantRspDto participantRspDto = setMasterParticipant(smc2ConferenceContext, value);
                                        conferenceState.setChairman(participantRspDto);
                                    }
                                }
                            } else {
                                conferenceState.setChairmanId("");
                                conferenceState.setChairman(null);
                                smc2ConferenceContext.setChairmanId(null);
                                smc2ConferenceContext.setMasterParticipant(null);
                                conferenceState.setChooseId("");
                            }

                            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc2ConferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, conferenceState);
                        }
                    }

                }
                break;
            case 3:
                //site_add
                if (uuidAttendeeMap == null) {
                    addSitUri(confId, siteUri, changes, smc2ConferenceContext);
                    JSONObject jsonObjectjs = new JSONObject();
                    jsonObjectjs.put("attendeeCountingStatistics", new AttendeeCountingStatistics(smc2ConferenceContext));
                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc2ConferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObjectjs);
                } else {
                    for (NotificationStatusEx change : changes) {
                        if (change instanceof SiteStatusEx) {
                            SiteStatusEx statusEx = (SiteStatusEx) change;
                            Integer status = statusEx.getStatus();
                            if (status != null) {
                                if (status.equals(AttendeeSmc2Utils.NOT_MEETING)) {
                                    //呼叫
                                    ConferenceServiceEx conferenceServiceEx = smc2Bridge.getConferenceServiceEx();
                                    List<String> list = new ArrayList<>();
                                    list.add(siteUri);
                                    conferenceServiceEx.connectSitesEx(confId, list);
                                }
                            }
                        }
                    }
                }
                break;
            case 4:
                //site_delete
                if (uuidAttendeeMap != null) {
                    uuidAttendeeMap.forEach((k, v) -> {
                        if (Objects.equals(v.getSmcParticipant().getGeneralParam().getUri(), siteUri)) {

                            SmcParitipantsStateRep.ContentDTO smcParticipant = v.getSmcParticipant();
                            AttendeeSmc2 attendee = smc2ConferenceContext.removeAttendeeById(v.getId());
                            if(attendee!=null){
                                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc2ConferenceContext, WebsocketMessageType.ATTENDEE_DELETE, attendee);
                            }

                            attendee.leaveMeeting();
                            // 从缓存中移除
                            Map<String, Object> updateMap = new HashMap<>();
                            updateMap.put("id", attendee.getId());
                            updateMap.put("deptId", attendee.getDeptId());
                            updateMap.put("mcuAttendee", attendee.isMcuAttendee());
                            Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(smc2ConferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                            String reason = "【" + attendee.getName() + "】离会";
                            Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(smc2ConferenceContext, WebsocketMessageType.MESSAGE_TIP, reason);

                            if (attendee == smc2ConferenceContext.getMasterAttendee()) {
                                Map<String, Object> data = new HashMap<>();
                                data.put("oldMasterAttendee", attendee);
                                data.put("newMasterAttendee", null);
                                Smc2WebSocketMessagePusher.getInstance().pushConferenceMessageToAll(smc2ConferenceContext, WebsocketMessageType.MASTER_CHANGED, data);

                                StringBuilder messageTip = new StringBuilder();
                                messageTip.append("主会场已离会【").append(attendee.getName()).append("】");
                                Smc2WebSocketMessagePusher.getInstance().pushConferenceMessageToAll(smc2ConferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                                smc2ConferenceContext.clearMasterAttendee();
                            }

                            processUpdateParticipant(smc2ConferenceContext, smcParticipant, false);
                            JSONObject jsonObjectjs = new JSONObject();
                            jsonObjectjs.put("attendeeCountingStatistics", new AttendeeCountingStatistics(smc2ConferenceContext));
                            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc2ConferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObjectjs);
                        }
                    });
                }
                JSONObject jsonObjectjs = new JSONObject();
                jsonObjectjs.put("attendeeCountingStatistics", new AttendeeCountingStatistics(smc2ConferenceContext));
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc2ConferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObjectjs);
                break;
            case 5:
                //site_status_change
                if (uuidAttendeeMap != null) {
                    uuidAttendeeMap.forEach((k, v) -> {
                        if (!CollectionUtils.isEmpty(changes)) {
                            for (NotificationStatusEx change : changes) {
                                SiteStatusEx statusEx =  (SiteStatusEx) change;
                                updateStatusChange(smc2ConferenceContext, statusEx, v);
                            }
                        }
                    });
                }

                break;
            default:
                break;
        }

    }

    private void updateStatusChange(Smc2ConferenceContext smc2ConferenceContext, SiteStatusEx change, AttendeeSmc2 a) {
        SiteStatusEx statusEx = change;
        if (smc2ConferenceContext != null) {
            if (a != null) {
                SmcParitipantsStateRep.ContentDTO smcParticipant = a.getSmcParticipant();
                if (smcParticipant != null) {
                    smcParticipant = initContent(smcParticipant, smc2ConferenceContext, statusEx);
                    if (smcParticipant.getId() == null) {
                        smcParticipant.setId(a.getId());
                    }
                    SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam = smcParticipant.getGeneralParam();
                    if (generalParam != null && generalParam.getId() == null) {
                        generalParam.setId(a.getId());
                    }
                    smcParticipant.getState().setVolume(a.getVolume());
                    smcParticipant.setAttendeeId(a.getId());
                    smcParticipant.getState().setParticipantId(a.getId());
                    generalParam.setType(smcParticipant.getGeneralParam().getType());
                    generalParam.setUri(smcParticipant.getGeneralParam().getUri());
                } else {
                    smcParticipant = initContent(null, smc2ConferenceContext, statusEx);
                    SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam = smcParticipant.getGeneralParam();
                    generalParam.setId(a.getId());
                    smcParticipant.setId(a.getId());
                    smcParticipant.getState().setParticipantId(a.getId());
                    generalParam.setUri(statusEx.getUri());
                    generalParam.setType(statusEx.getType());
                }
                processParticipants(smc2ConferenceContext, smcParticipant, smcParticipant.getGeneralParam(), a);
            }
        }
    }

    private void processParticipants(Smc2ConferenceContext smc2ConferenceContext, SmcParitipantsStateRep.ContentDTO contentDTO, SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam, AttendeeSmc2 a) {
        smc2ConferenceContext.getParticipantAttendeeAllMap().put(generalParam.getId(), a);
        a.setConferenceNumber(smc2ConferenceContext.getConferenceNumber());
        a.setSmcParticipant(contentDTO);
        AttendeeSmc2Utils.updateByParticipant(smc2ConferenceContext, contentDTO, a);
        processUpdateParticipant(smc2ConferenceContext, contentDTO, contentDTO.getChangeType()==null?false:(contentDTO.getChangeType() != 1));
    }

    private SmcParitipantsStateRep.ContentDTO initContent(SmcParitipantsStateRep.ContentDTO contentDTO, Smc2ConferenceContext smc2ConferenceContext, SiteStatusEx statusEx) {
        if (contentDTO == null) {
            contentDTO = new SmcParitipantsStateRep.ContentDTO();
            SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam = new SmcParitipantsStateRep.ContentDTO.GeneralParamDTO();
            ParticipantState participantState = new ParticipantState();
            contentDTO.setGeneralParam(generalParam);
            contentDTO.setState(participantState);
        }
        SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam = contentDTO.getGeneralParam();
        ParticipantState participantState = contentDTO.getState();
        if (statusEx.getUri() != null) {
            generalParam.setUri(statusEx.getUri());
        }
        generalParam.setType(statusEx.getType());

        participantState.setVolume(statusEx.getVolume());
        participantState.setCallFailReason(statusEx.getCallFailedReason().getErrCode());

        generalParam.setName(statusEx.getName());


        participantState.setMute(statusEx.getIsMute() == 1 ? true : false);
        participantState.setQuiet(statusEx.getIsQuiet() == 1 ? true : false);
        if (statusEx.getIsDataOnline() != null) {
            participantState.setDataOnline(statusEx.getIsDataOnline() == 1 ? true : false);
        }

        if (statusEx.getIsLocalVideoOpen() != null) {
            participantState.setVideoMute(statusEx.getIsLocalVideoOpen() == 1 ? false : true);
        }


        Integer status = statusEx.getStatus();
        if (status != null) {
            if (status.equals(AttendeeSmc2Utils.INMEETING)) {
                participantState.setOnline(true);
            } else if (status == AttendeeSmc2Utils.RING || status == AttendeeSmc2Utils.rINGING) {
                participantState.setOnline(false);
                participantState.setCalling(true);
            } else if (status.equals(AttendeeSmc2Utils.NOT_MEETING)) {
                participantState.setOnline(false);
            } else if (status == 1) {
                participantState.setOnline(false);
            } else {
                participantState.setOnline(false);
            }
        }
        if (Strings.isNotBlank(statusEx.getVideoSource())) {
            if (Objects.equals(CP, statusEx.getVideoSource())) {
                ChooseMultiPicInfo.MultiPicInfoDTO source = new ChooseMultiPicInfo.MultiPicInfoDTO();

                MultiPicPollRequest multiPicPollRequest = smc2ConferenceContext.getMultiPicPollRequest();
                MultiPicPollRequest chairmanMultiPicPollRequest = smc2ConferenceContext.getChairmanMultiPicPollRequest();
                if (multiPicPollRequest != null || chairmanMultiPicPollRequest != null) {
                    Integer picNum = multiPicPollRequest == null ? chairmanMultiPicPollRequest.getPicNum() : multiPicPollRequest.getPicNum();
                    source.setPicNum(picNum);
                    if (picNum == 1) {
                        //查询广播多画面
                        ConferenceServiceEx conferenceServiceEx = smc2ConferenceContext.getSmc2Bridge().getConferenceServiceEx();
                        String confId = smc2ConferenceContext.getSmc2conferenceId();
                        GetContinuousPresenceParamExResponse result
                                = conferenceServiceEx.getContinuousPresenceParamEx(confId, CP);
                        if (0 == result.getResultCode()) {
                            List<ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO> list = new ArrayList<>();
                            ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO subPicListDTO = new ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO();
                            String s = result.getSubPics().get(0);
                            subPicListDTO.setParticipantId(smc2ConferenceContext.getParticiPantIdBySiteUri(s));
                            list.add(subPicListDTO);
                            source.setSubPicList(list);
                        }

                    }
                    participantState.setMultiPicInfo(source);
                } else {
                    MultiPicInfoReq.MultiPicInfoDTO multiPicInfo = smc2ConferenceContext.getMultiPicInfo();
                    if (multiPicInfo != null) {
                        ChooseMultiPicInfo.MultiPicInfoDTO multiPicInfoDTO = new ChooseMultiPicInfo.MultiPicInfoDTO();
                        multiPicInfoDTO.setMode(multiPicInfo.getMode());
                        multiPicInfoDTO.setPicNum(multiPicInfo.getPicNum());
                        List<ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO> list = new ArrayList<>();
                        List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList = multiPicInfo.getSubPicList();
                        for (MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicList) {
                            ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO subPicListDTO1 = new ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO();
                            subPicListDTO1.setParticipantId(subPicListDTO.getParticipantId());
                            subPicListDTO1.setStreamNumber(subPicListDTO.getStreamNumber());
                            list.add(subPicListDTO1);
                        }
                        multiPicInfoDTO.setSubPicList(list);
                        participantState.setMultiPicInfo(multiPicInfoDTO);
                    }else {
                        AttendeeOperation attendeeOperation = smc2ConferenceContext.getAttendeeOperation();

                        if(attendeeOperation instanceof TalkAttendeeOperation){
                            TalkAttendeeOperation talkAttendeeOperation = (TalkAttendeeOperation) attendeeOperation;
                            MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO = talkAttendeeOperation.getMultiPicInfoDTO();
                            if(multiPicInfoDTO!=null){
                                String s = JSONObject.toJSONString(multiPicInfoDTO);
                                ChooseMultiPicInfo.MultiPicInfoDTO multiPicInfoDTO1 = JSONObject.parseObject(s, ChooseMultiPicInfo.MultiPicInfoDTO.class);
                                participantState.setMultiPicInfo(multiPicInfoDTO1);
                            }

                        }

                       }

                }

            } else {
                String particiPantIdBySiteUri = smc2ConferenceContext.getParticiPantIdBySiteUri(statusEx.getVideoSource());
                ChooseMultiPicInfo.MultiPicInfoDTO multiPicInfoDTO = new ChooseMultiPicInfo.MultiPicInfoDTO();
                multiPicInfoDTO.setPicNum(1);
                List<ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO> list = new ArrayList<>();
                ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO subPicListDTO = new ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO();
                subPicListDTO.setParticipantId(particiPantIdBySiteUri);
                list.add(subPicListDTO);
                multiPicInfoDTO.setSubPicList(list);
                participantState.setMultiPicInfo(multiPicInfoDTO);
            }
        }
        return contentDTO;
    }

    private void addSitUri(String confId, String siteUri, List<NotificationStatusEx> changes, Smc2ConferenceContext smc2ConferenceContext) {
        SmcParitipantsStateRep.ContentDTO contentDTO = new SmcParitipantsStateRep.ContentDTO();
        SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam = new SmcParitipantsStateRep.ContentDTO.GeneralParamDTO();
        contentDTO.setGeneralParam(generalParam);
        ParticipantState state = new ParticipantState();
        contentDTO.setState(state);
        contentDTO.setWeight(0);
        if (!CollectionUtils.isEmpty(changes)) {
            for (NotificationStatusEx change : changes) {
                if (change instanceof SiteStatusEx) {
                    List<SmcParitipantsStateRep.ContentDTO> contentDTOList = new ArrayList<>();
                    SiteStatusEx statusEx = (SiteStatusEx) change;
                    generalParam.setName(statusEx.getName());
                    generalParam.setUri(statusEx.getUri());
                    String uuid = UUID.randomUUID().toString().replaceAll("//-", "");
                    generalParam.setId(uuid);
                    contentDTO.setId(uuid);
                    state.setParticipantId(uuid);
                    state.setOnline(true);
                    state.setVolume(statusEx.getVolume());
                    state.setMute(statusEx.getIsMute() == null ? false : (statusEx.getIsMute() == 1 ? true : false));
                    state.setQuiet(statusEx.getIsQuiet() == null ? false : (statusEx.getIsQuiet() == 1 ? true : false));
                    state.setParticipantId(uuid);
                    generalParam.setType(statusEx.getType());
                    state.setOnline(true);
                    contentDTO.setTerminalOnline(true);
                    Integer status = statusEx.getStatus();
                    /**
                     * 会场状态。
                     * 0：未知状态（保留）
                     * 1：会场不存在
                     * 2：在会议中
                     * 3：未入会
                     * 4：正在呼叫
                     * 5：正在振铃
                     */
                    if (status != null) {
                        if (status.equals(AttendeeSmc2Utils.INMEETING)) {
                            state.setOnline(true);
                        } else if (status.equals(AttendeeSmc2Utils.NOT_MEETING)) {
                            //呼叫
                            ConferenceServiceEx conferenceServiceEx = smc2Bridge.getConferenceServiceEx();
                            List<String> list = new ArrayList<>();
                            list.add(siteUri);
                            conferenceServiceEx.connectSitesEx(confId, list);
                        } else {
                            state.setOnline(false);
                        }

                    }
                    new SelfCallAttendeeNewSmc2Processor(contentDTO, smc2ConferenceContext).process();
                }

            }
        }
    }

    private void processUpdateParticipant(Smc2ConferenceContext conferenceContext, SmcParitipantsStateRep.ContentDTO contentDTO, boolean updateMediaInfo) {
        IBusiSmc2HistoryConferenceService busiSmc2HistoryConferenceService = BeanFactory.getBean(IBusiSmc2HistoryConferenceService.class);
        busiSmc2HistoryConferenceService.updateBusiHistoryParticipant(conferenceContext, contentDTO, updateMediaInfo);
    }

}
