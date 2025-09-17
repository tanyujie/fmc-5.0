/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : PollingAttendeeOpreation.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.operation
 * @author lilinhai
 * @since 2021-02-26 15:55
 * @version  V1.0
 */
package com.paradisecloud.fcm.smc2.model.attendee.operation;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.PollOperateTypeDto;
import com.paradisecloud.com.fcm.smc.modle.request.MultiPicPollRequest;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.cascade.ConferenceCascadeHandler;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContextCache;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.model.AttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.McuAttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.operation.polling.PollingScheme;
import com.paradisecloud.fcm.smc2.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.smc2.model.layout.ChairManSmc2PollingThread;
import com.paradisecloud.fcm.smc2.model.layout.ContinuousPresenceModeEnum;
import com.suntek.smc.esdk.common.State;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 主席轮询
 *
 * @author nj
 * @date 2016/10/21
 */
public class ChairmanPollingAttendeeOperation extends AttendeeOperation {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     *
     * @since 2021-02-26 15:55
     */
    private static final long serialVersionUID = 1L;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private volatile PollingScheme pollingScheme;
    /**
     * 暂停
     */
    private volatile boolean isPause = false;
    private volatile long lastUpdateTime = 0;

    private final Set<AttendeeSmc2> pollingAttendeeList = new HashSet<>();
    private  List<AttendeeSmc2> pollingAttendeeSmc2List = new ArrayList<>();


    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @param pollingScheme
     * @author lilinhai
     * @since 2021-02-26 15:56
     */
    public ChairmanPollingAttendeeOperation(Smc2ConferenceContext conferenceContext, PollingScheme pollingScheme) {
        super(conferenceContext);
        this.pollingScheme = pollingScheme;
        initSplitScreen();
    }

    public ChairmanPollingAttendeeOperation(Smc2ConferenceContext conferenceContext, JSONObject jsonObject) {
        super(conferenceContext);

    }

    public ChairmanPollingAttendeeOperation(Smc2ConferenceContext conferenceContext) {
        super(conferenceContext);
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public void initSplitScreen() {
//        int maxImportance = this.pollingScheme.getIsBroadcast() == YesOrNo.YES ? AttendeeImportance.BROADCAST.getEndValue() : AttendeeImportance.CHOOSE_SEE.getEndValue();
//        String defaultViewLayout = this.pollingScheme.getLayout();

    }

    /**
     * 解析
     *
     * @return List<PollingAttendee>
     * @author lilinhai
     * @since 2021-04-09 14:01
     */
    private void parse() throws Exception {

    }

    /**
     * <p>Get Method   :   isPause boolean</p>
     *
     * @return isPause
     */
    public boolean isPause() {
        return isPause;
    }

    /**
     * <p>Set Method   :   isPause boolean</p>
     *
     * @param isPause
     */
    public synchronized void setPause(boolean isPause) {
        this.isPause = isPause;
        synchronized (this) {
            if (isPause) {
                conferenceContext.setChairmanPollStatus(com.paradisecloud.fcm.common.enumer.PollOperateTypeDto.STOP);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("chairmanPollStatus", PollOperateTypeDto.STOP.name());
                Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                ChairManSmc2PollingThread chairManSmc2PollingThread = conferenceContext.getChairManSmc2PollingThread();
                if (chairManSmc2PollingThread != null) {
                    chairManSmc2PollingThread.supend();
                }
                logger.info("主席轮询广播:" +conferenceContext.getId()+ State.ThREAD_STOP);
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, true);
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询已进入暂停状态");
            } else {
                conferenceContext.setChairmanPollStatus(com.paradisecloud.fcm.common.enumer.PollOperateTypeDto.START);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("chairmanPollStatus", PollOperateTypeDto.START.name());
                Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                //轮询设置 主席选看多画面
                ChairManSmc2PollingThread chairManSmc2PollingThread = conferenceContext.getChairManSmc2PollingThread();
                if (chairManSmc2PollingThread != null) {
                    chairManSmc2PollingThread.starton();
                }
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询已恢复运行状态");
            }
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        ChairManSmc2PollingThread chairManSmc2PollingThread = conferenceContext.getChairManSmc2PollingThread();
        if (chairManSmc2PollingThread != null) {
            chairManSmc2PollingThread.stops();
        }
        conferenceContext.setChairManSmc2PollingThread(null);
        conferenceContext.setChairmanPollStatus(com.paradisecloud.fcm.common.enumer.PollOperateTypeDto.CANCEL);

        conferenceContext.setStartRound(false);
        conferenceContext.setChairmanPollStatus(com.paradisecloud.fcm.common.enumer.PollOperateTypeDto.CANCEL);
        conferenceContext.getChairmanMultiPicPollRequest().setPollStatus(com.paradisecloud.fcm.common.enumer.PollOperateTypeDto.CANCEL.name());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("chairmanPollStatus", PollOperateTypeDto.CANCEL.name());
        Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);



        logger.info("----结束轮询----" + conferenceContext.getName());
        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_ENDED, "轮询结束");

        StringBuilder messageTip1 = new StringBuilder();
        messageTip1.append("轮询已结束");
        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip1);
        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, false);
        if(!CollectionUtils.isEmpty(pollingAttendeeList)){
            for (AttendeeSmc2 attendeeSmc2 : pollingAttendeeList) {
                AttendeeImportance.COMMON.processAttendeeWebsocketMessage(attendeeSmc2);
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeSmc2.getUpdateMap());
            }
        }
        pollingAttendeeList.clear();
        List<McuAttendeeSmc2> mcuAttendees = conferenceContext.getMcuAttendees();
        for (McuAttendeeSmc2 mcuAttendee : mcuAttendees) {
            BaseConferenceContext mcuConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(mcuAttendee.getCascadeConferenceId()));
            if (mcuConferenceContext != null) {
                try {
                    ConferenceCascadeHandler.defaultChooseSee(mcuAttendee.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 操作方法
     *
     * @author lilinhai
     * @since 2021-02-20 16:29  void
     */
    @Override
    public void operate() {

        if (conferenceContext.getMasterAttendee() == null) {
            return;
        }

        if (conferenceContext.getMasterAttendee() == null || !conferenceContext.getMasterAttendee().isMeetingJoined() || !conferenceContext.getMasterAttendee().isOnline()) {
            return;
        }

        com.paradisecloud.fcm.common.enumer.PollOperateTypeDto chairmanPollStatus = conferenceContext.getChairmanPollStatus();
        if (Objects.equals(chairmanPollStatus, PollOperateTypeDto.START) && conferenceContext.getChairManSmc2PollingThread() != null) {
            return;
        }
        MultiPicPollRequest multiPicPollRequest = conferenceContext.getChairmanMultiPicPollRequest();
        if (multiPicPollRequest == null) {
            throw new CustomException("未设置主席轮询");
        }
        String target = "(%CP)";
        List<List<String>> subPics = new ArrayList<>();
        Integer picNum = multiPicPollRequest.getPicNum();
        Integer mode = multiPicPollRequest.getMode();
        int presenceMode = ContinuousPresenceModeEnum.getModelValue(picNum, mode);
        if (presenceMode == -1) {
            throw new CustomException("多画面设置失败:不支持该" + picNum + "画面");
        }

        String confId = conferenceContext.getSmc2conferenceId();
        AttendeeSmc2 masterAttendee = conferenceContext.getMasterAttendee();

        ConferenceServiceEx conferenceServiceEx = conferenceContext.getSmc2Bridge().getConferenceServiceEx();
        pollingAttendeeList.clear();

        if (conferenceContext.isUpCascadeConference() && multiPicPollRequest.getPicNum() == 1 && isCascadePolling(conferenceContext, multiPicPollRequest)) {
            Integer interval = subPicsSetting(multiPicPollRequest, conferenceContext, multiPicPollRequest.getPicNum(), subPics);

            Integer resultCode = conferenceServiceEx.setBroadcastSiteEx(confId, masterAttendee.getRemoteParty(), 0);
            if (resultCode != 0) {
                logger.error(masterAttendee.getName() + "广播主席失败：" + resultCode);
            }

            parseFromSmc();
            //轮询设置 主席选看多画面
            ChairManSmc2PollingThread chairManSmc2PollingThread = conferenceContext.getChairManSmc2PollingThread();
            if (chairManSmc2PollingThread != null) {
                chairManSmc2PollingThread.starton();
            } else {
                chairManSmc2PollingThread = new ChairManSmc2PollingThread(interval, subPics, confId, target, presenceMode, masterAttendee.getRemoteParty(), conferenceServiceEx, conferenceContext,multiPicPollRequest,pollingAttendeeSmc2List);
                chairManSmc2PollingThread.start();
                conferenceContext.setChairManSmc2PollingThread(chairManSmc2PollingThread);
            }
            conferenceContext.setChairmanPollStatus(com.paradisecloud.fcm.common.enumer.PollOperateTypeDto.START);
        }else {
            Integer interval = subPicsSetting(multiPicPollRequest, conferenceContext, multiPicPollRequest.getPicNum(), subPics);

            Integer resultCode = conferenceServiceEx.setBroadcastSiteEx(confId, masterAttendee.getRemoteParty(), 0);
            if (resultCode != 0) {
                logger.error(masterAttendee.getName() + "广播主席失败：" + resultCode);
            }
            //轮询设置 主席选看多画面
            ChairManSmc2PollingThread chairManSmc2PollingThread = conferenceContext.getChairManSmc2PollingThread();
            if (chairManSmc2PollingThread != null) {
                chairManSmc2PollingThread.starton();
            } else {
                chairManSmc2PollingThread = new ChairManSmc2PollingThread(interval, subPics, confId, target, presenceMode, masterAttendee.getRemoteParty(), conferenceServiceEx, conferenceContext,multiPicPollRequest,pollingAttendeeSmc2List);
                chairManSmc2PollingThread.start();
                conferenceContext.setChairManSmc2PollingThread(chairManSmc2PollingThread);
            }
            conferenceContext.setChairmanPollStatus(com.paradisecloud.fcm.common.enumer.PollOperateTypeDto.START);
        }


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("chairmanPollStatus", PollOperateTypeDto.START.name());
        Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
    }


    private Integer subPicsSetting(MultiPicPollRequest multiPicPollRequest, Smc2ConferenceContext smc2ConferenceContext, Integer picNum, List<List<String>> subPics) {
        Integer interval;
        List<MultiPicPollRequest.SubPicPollInfoListDTO> subPicPollInfoList = multiPicPollRequest.getSubPicPollInfoList();

        MultiPicPollRequest.SubPicPollInfoListDTO msubPicPollInfoListDTOMax = subPicPollInfoList.stream().max(Comparator.comparingInt(dto -> dto.getParticipantIds().size())).get();
        interval = msubPicPollInfoListDTOMax.getInterval() == null ? 5 : msubPicPollInfoListDTOMax.getInterval();
        List<MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO> participantIdsMax = msubPicPollInfoListDTOMax.getParticipantIds();

        for (int i = 0; i < participantIdsMax.size(); i++) {
            List<String> sub = new ArrayList<>();

            for (Integer integer = 0; integer < picNum; integer++) {
                List<MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO> participantIds = subPicPollInfoList.get(integer).getParticipantIds();
                if (CollectionUtils.isEmpty(participantIds)) {
                    sub.add("");
                } else {
                    if (participantIds.size() == 1) {
                        MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantIdsDTO = participantIds.get(0);
                        subaddUri(smc2ConferenceContext, sub, participantIdsDTO);
                    } else if (participantIds.size() > i) {
                        MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantIdsDTO = participantIds.get(i);
                        subaddUri(smc2ConferenceContext, sub, participantIdsDTO);
                    } else if (participantIds.size() > 1 && participantIds.size() <= i) {
                        int size = i - participantIds.size();
                        if (size < participantIds.size()) {
                            MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantIdsDTO = participantIds.get(size);
                            subaddUri(smc2ConferenceContext, sub, participantIdsDTO);
                        } else {
                            int i1 = i % (participantIds.size());
                            MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantIdsDTO = participantIds.get(i1);
                            subaddUri(smc2ConferenceContext, sub, participantIdsDTO);
                        }

                    }

                }
            }
            subPics.add(sub);
        }
        return interval;
    }

    private void subaddUri(Smc2ConferenceContext smc2ConferenceContext, List<String> sub, MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantIdsDTO) {
        AttendeeSmc2 attendeeBySmc2Id = smc2ConferenceContext.getAttendeeBySmc2Id(participantIdsDTO.getParticipantId());
        if (attendeeBySmc2Id != null) {
            SmcParitipantsStateRep.ContentDTO smcParticipant = attendeeBySmc2Id.getSmcParticipant();
            if (!attendeeBySmc2Id.isMeetingJoined()) {
                throw new CustomException("离线终端不能参与");
            }
            sub.add(smcParticipant.getGeneralParam().getUri());
            pollingAttendeeList.add(attendeeBySmc2Id);
        } else {
            List<McuAttendeeSmc2> mcuAttendees = conferenceContext.getMcuAttendees();
            for (McuAttendeeSmc2 mcuAttendee : mcuAttendees) {
                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(mcuAttendee.getCascadeConferenceId()));
                BaseAttendee attendeeById = baseConferenceContext.getAttendeeById(participantIdsDTO.getParticipantId());
                if(attendeeById==null){
                    attendeeById = baseConferenceContext.getAttendeeByPUuid(participantIdsDTO.getParticipantId());
                }
                if (attendeeById != null) {
                    sub.add(attendeeById.getRemoteParty());
                    pollingAttendeeList.add(mcuAttendee);
                    break;
                }
            }
        }
    }



    public boolean isCascadePolling(Smc2ConferenceContext conferenceContext, MultiPicPollRequest multiPicPollRequest) {
        List<MultiPicPollRequest.SubPicPollInfoListDTO> subPicPollInfoList = multiPicPollRequest.getSubPicPollInfoList();
        for (MultiPicPollRequest.SubPicPollInfoListDTO subPicPollInfoListDTO : subPicPollInfoList) {
            List<MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO> participantIds = subPicPollInfoListDTO.getParticipantIds();
            for (MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantId : participantIds) {
                AttendeeSmc2 attendeeBySmc3Id = conferenceContext.getAttendeeBySmc2Id(participantId.getParticipantId());
                if (attendeeBySmc3Id == null) {
                    return true;
                }
            }
        }
        return false;
    }

    private void parseFromSmc() {
        MultiPicPollRequest multiPicPollRequest = conferenceContext.getChairmanMultiPicPollRequest();
        if (multiPicPollRequest != null) {
            List<MultiPicPollRequest.SubPicPollInfoListDTO> subPicPollInfoList = multiPicPollRequest.getSubPicPollInfoList();

            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(subPicPollInfoList)) {
                for (MultiPicPollRequest.SubPicPollInfoListDTO subPicPollInfoListDTO : subPicPollInfoList) {
                    List<MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO> participantIds = subPicPollInfoListDTO.getParticipantIds();
                    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(participantIds)) {
                        for (MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantId : participantIds) {
                            AttendeeSmc2 attendeeBySmc3 = conferenceContext.getAttendeeBySmc2Id(participantId.getParticipantId());
                            if (attendeeBySmc3 == null) {
                                List<McuAttendeeSmc2> mcuAttendees = conferenceContext.getMcuAttendees();
                                for (McuAttendeeSmc2 mcuAttendee : mcuAttendees) {
                                    String cascadeMcuType = mcuAttendee.getCascadeMcuType();
                                    if (!Objects.equals(McuType.SMC2.getCode(), cascadeMcuType)) {
                                        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(mcuAttendee.getCascadeConferenceId()));
                                        BaseAttendee attendeeById;
                                        if (Objects.equals(McuType.SMC3.getCode(), cascadeMcuType)) {
                                            attendeeById = baseConferenceContext.getAttendeeByPUuid(participantId.getParticipantId());
                                            McuAttendeeSmc2 mcuAttendeeSmc2 = new McuAttendeeSmc2();
                                            mcuAttendeeSmc2.setCascadeAttendeId(mcuAttendee.getId());
                                            mcuAttendeeSmc2.setCascadeMcuType(mcuAttendee.getCascadeMcuType());
                                            mcuAttendeeSmc2.setCascadeConferenceId(mcuAttendee.getCascadeConferenceId());
                                            mcuAttendeeSmc2.setId(attendeeById.getId());
                                            mcuAttendeeSmc2.setParticipantUuid(mcuAttendee.getParticipantUuid());
                                            mcuAttendeeSmc2.setRemoteParty(mcuAttendee.getRemoteParty());
                                            pollingAttendeeSmc2List.add(mcuAttendeeSmc2);
                                            break;
                                        } else {
                                            attendeeById = baseConferenceContext.getAttendeeById(participantId.getParticipantId());
                                            if (attendeeById != null) {
                                                String id = attendeeById.getId();
                                                if (Objects.equals(id, participantId.getParticipantId())) {
                                                    McuAttendeeSmc2 mcuAttendeeSmc2 = new McuAttendeeSmc2();
                                                    mcuAttendeeSmc2.setCascadeAttendeId(id);
                                                    mcuAttendeeSmc2.setCascadeMcuType(mcuAttendee.getCascadeMcuType());
                                                    mcuAttendeeSmc2.setCascadeConferenceId(mcuAttendee.getCascadeConferenceId());
                                                    mcuAttendeeSmc2.setId(id);
                                                    mcuAttendeeSmc2.setParticipantUuid(mcuAttendee.getParticipantUuid());
                                                    mcuAttendeeSmc2.setRemoteParty(mcuAttendee.getRemoteParty());
                                                    pollingAttendeeSmc2List.add(mcuAttendeeSmc2);
                                                    break;
                                                }
                                            }
                                        }
                                    } else {
                                        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(mcuAttendee.getCascadeConferenceId()));
                                        AttendeeSmc2 attendeeSmc2 = smc2ConferenceContext.getAttendeeBySmc2Id(participantId.getParticipantId());
                                        McuAttendeeSmc2 mcuAttendeeSmc2 = new McuAttendeeSmc2();
                                        mcuAttendeeSmc2.setCascadeAttendeId(attendeeSmc2.getId());
                                        mcuAttendeeSmc2.setCascadeMcuType(mcuAttendee.getCascadeMcuType());
                                        mcuAttendeeSmc2.setCascadeConferenceId(mcuAttendee.getCascadeConferenceId());
                                        mcuAttendeeSmc2.setId(attendeeSmc2.getId());
                                        mcuAttendeeSmc2.setParticipantUuid(mcuAttendee.getParticipantUuid());
                                        pollingAttendeeSmc2List.add(mcuAttendeeSmc2);
                                        break;
                                    }
                                }
                            } else {
                                pollingAttendeeSmc2List.add(attendeeBySmc3);
                            }
                        }
                    }
                }
            }
        }
    }

}
