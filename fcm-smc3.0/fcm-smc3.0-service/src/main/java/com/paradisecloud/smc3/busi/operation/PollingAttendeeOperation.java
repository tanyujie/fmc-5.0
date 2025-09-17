/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : PollingAttendeeOpreation.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.operation
 * @author lilinhai
 * @since 2021-02-26 15:55
 * @version  V1.0
 */
package com.paradisecloud.smc3.busi.operation;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.cache.BaseWebSocketMessagePusher;
import com.paradisecloud.fcm.service.conference.cascade.ConferenceCascadeHandler;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.McuAttendeeSmc3;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.enumer.AttendeeImportance;
import com.paradisecloud.smc3.busi.layout.*;
import com.paradisecloud.smc3.busi.operation.polling.PollingAttendee;
import com.paradisecloud.smc3.busi.operation.polling.PollingScheme;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.smc3.model.ConstAPI;
import com.paradisecloud.smc3.model.PollOperateTypeDto;
import com.paradisecloud.smc3.model.request.MultiPicPollRequest;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * 多画面轮询
 */
public class PollingAttendeeOperation extends AttendeeOperation {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     *
     * @since 2021-02-26 15:55
     */
    private static final long serialVersionUID = 1L;
    private final List<AttendeeSmc3> pollingAttendeeSmc3List = new ArrayList<>();
    private final Set<String> autoPollingAttendeeIdSet = new HashSet<>();
    private final Set<String> checkedAttendeeIdSet = new HashSet<>();
    private final long lastUpdateTime = 0;
    private final Thread pollingThread = null;
    private volatile PollingScheme pollingScheme;
    private volatile List<PollingAttendee> pollingAttendeeList;
    private volatile AttendeeSmc3 lastAutoPollingAttendee;
    private volatile int autoPollingScreenCount = 0;
    private volatile int lastAutoPollingIdx = -1;
    private volatile List<AttendeeSmc3> targetAttendees = new ArrayList<>();
    /**
     * 暂停
     */
    private volatile boolean isPause;
    private volatile boolean isPolling;
    private volatile boolean cancel;
    private JSONObject jsonObject;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @param pollingScheme
     * @author lilinhai
     * @since 2021-02-26 15:56
     */
    public PollingAttendeeOperation(Smc3ConferenceContext conferenceContext, PollingScheme pollingScheme) {
        super(conferenceContext);
        this.pollingScheme = pollingScheme;
        initSplitScreen();
        Assert.isTrue(!(this.splitScreen instanceof AutomaticSplitScreen), "轮询操作不支持自动分屏");
    }

    public PollingAttendeeOperation(Smc3ConferenceContext conferenceContext, JSONObject jsonObject) {
        super(conferenceContext);
        this.jsonObject = jsonObject;
        parseFromSmc();

    }


    public void initSplitScreen() {
        int maxImportance = this.pollingScheme.getIsBroadcast() == YesOrNo.YES ? AttendeeImportance.BROADCAST.getEndValue() : AttendeeImportance.CHOOSE_SEE.getEndValue();
        String defaultViewLayout = this.pollingScheme.getLayout();
        if (OneSplitScreen.LAYOUT.equals(defaultViewLayout)) {
            this.splitScreen = new OneSplitScreen(maxImportance);
        } else if (FourSplitScreen.LAYOUT.equals(defaultViewLayout)) {
            this.splitScreen = new FourSplitScreen(maxImportance);
        } else if (NineSplitScreen.LAYOUT.equals(defaultViewLayout)) {
            this.splitScreen = new NineSplitScreen(maxImportance);
        } else if (SixteenSplitScreen.LAYOUT.equals(defaultViewLayout)) {
            this.splitScreen = new SixteenSplitScreen(maxImportance);
        } else if (OnePlusFiveSplitScreen.LAYOUT.equals(defaultViewLayout)) {
            this.splitScreen = new OnePlusFiveSplitScreen(maxImportance);
        } else if (OnePlusSevenSplitScreen.LAYOUT.equals(defaultViewLayout)) {
            this.splitScreen = new OnePlusSevenSplitScreen(maxImportance);
        } else if (OnePlusNineSplitScreen.LAYOUT.equals(defaultViewLayout)) {
            this.splitScreen = new OnePlusNineSplitScreen(maxImportance);
        } else if (TwoSplitScreen.LAYOUT.equals(defaultViewLayout)) {
            this.splitScreen = new TwoSplitScreen(maxImportance);
        } else {
            this.splitScreen = new OneSplitScreen(maxImportance);
        }
    }

    /**
     * 解析
     *
     * @return List<PollingAttendee>
     * @author lilinhai
     * @since 2021-04-09 14:01
     */
    private void parse() throws Exception {
        Set<String> idSet = new HashSet<>();
        pollingScheme.removeInvalidAttendee(conferenceContext);
        pollingAttendeeList = pollingScheme.getPollingStrategy().getStrategy().parse(conferenceContext, pollingScheme.getDeptPollingAttendeesList());
        for (PollingAttendee pollingAttendee : pollingAttendeeList) {
            if (!idSet.add(pollingAttendee.getAttendee().getId())) {
                StringBuilder messageTip1 = new StringBuilder();
                messageTip1.append("轮询列表存在重复参会【" + pollingAttendee.getAttendee().getId() + ", " + pollingAttendee.getAttendee().getName() + "】，异常终止！");
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip1);
                throw new CustomException(messageTip1.toString());
            }
        }
        idSet.clear();
    }

    private void parseFromSmc() {
        MultiPicPollRequest multiPicPollRequest = conferenceContext.getMultiPicPollRequest();
        if (multiPicPollRequest != null) {
            List<MultiPicPollRequest.SubPicPollInfoListDTO> subPicPollInfoList = multiPicPollRequest.getSubPicPollInfoList();

            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(subPicPollInfoList)) {
                for (MultiPicPollRequest.SubPicPollInfoListDTO subPicPollInfoListDTO : subPicPollInfoList) {
                    List<MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO> participantIds = subPicPollInfoListDTO.getParticipantIds();
                    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(participantIds)) {
                        for (MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantId : participantIds) {
                            AttendeeSmc3 attendeeBySmc3 = conferenceContext.getAttendeeBySmc3Id(participantId.getParticipantId());
                            if (attendeeBySmc3 == null) {
                                List<McuAttendeeSmc3> mcuAttendees = conferenceContext.getMcuAttendees();
                                for (McuAttendeeSmc3 mcuAttendee : mcuAttendees) {
                                    String cascadeMcuType = mcuAttendee.getCascadeMcuType();
                                    if (!Objects.equals(McuType.SMC3.getCode(), cascadeMcuType)) {
                                        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(mcuAttendee.getCascadeConferenceId()));
                                        BaseAttendee attendeeById = baseConferenceContext.getAttendeeById(participantId.getParticipantId());
                                        if (attendeeById != null) {
                                            String id = attendeeById.getId();
                                            if (Objects.equals(id, participantId.getParticipantId())) {
                                                McuAttendeeSmc3 mcuAttendeeSmc3 = new McuAttendeeSmc3();
                                                mcuAttendeeSmc3.setCascadeAttendeId(id);
                                                mcuAttendeeSmc3.setCascadeMcuType(mcuAttendee.getCascadeMcuType());
                                                mcuAttendeeSmc3.setCascadeConferenceId(mcuAttendee.getCascadeConferenceId());
                                                mcuAttendeeSmc3.setId(id);
                                                mcuAttendeeSmc3.setParticipantUuid(mcuAttendee.getParticipantUuid());
                                                pollingAttendeeSmc3List.add(mcuAttendeeSmc3);
                                                break;
                                            }
                                        }
                                    } else {
                                        Smc3ConferenceContext smc3ConferenceContext = Smc3ConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(mcuAttendee.getCascadeConferenceId()));
                                        AttendeeSmc3 attendeeSmc3 = smc3ConferenceContext.getAttendeeBySmc3Id(participantId.getParticipantId());
                                        McuAttendeeSmc3 mcuAttendeeSmc3 = new McuAttendeeSmc3();
                                        mcuAttendeeSmc3.setCascadeAttendeId(attendeeSmc3.getId());
                                        mcuAttendeeSmc3.setCascadeMcuType(mcuAttendee.getCascadeMcuType());
                                        mcuAttendeeSmc3.setCascadeConferenceId(mcuAttendee.getCascadeConferenceId());
                                        mcuAttendeeSmc3.setId(attendeeSmc3.getId());
                                        mcuAttendeeSmc3.setParticipantUuid(mcuAttendee.getParticipantUuid());
                                        pollingAttendeeSmc3List.add(mcuAttendeeSmc3);
                                        break;
                                    }
                                }
                            } else {
                                pollingAttendeeSmc3List.add(attendeeBySmc3);
                            }
                        }
                    }
                }
            }
        }
    }

    private MultiPicPollRequest parseMutilPic(MultiPicPollRequest multiPicPollRequest, AttendeeSmc3 attendeeSmc3) {
        MultiPicPollRequest multiPicPollRequest_Parse = new MultiPicPollRequest();
        multiPicPollRequest_Parse.setPollStatus(multiPicPollRequest.getPollStatus());
        multiPicPollRequest_Parse.setPicNum(1);
        multiPicPollRequest_Parse.setMode(1);
        multiPicPollRequest_Parse.setConferenceId(multiPicPollRequest.getConferenceId());
        multiPicPollRequest_Parse.setBroadcast(multiPicPollRequest.getBroadcast());

        Integer interval = multiPicPollRequest.getSubPicPollInfoList().get(0).getInterval();
        multiPicPollRequest_Parse.setInterval(interval);

        List<MultiPicPollRequest.SubPicPollInfoListDTO> subPicPollInfoListDTOS = new ArrayList<>();
        multiPicPollRequest_Parse.setSubPicPollInfoList(subPicPollInfoListDTOS);
        MultiPicPollRequest.SubPicPollInfoListDTO subPicPollInfoListDTO = new MultiPicPollRequest.SubPicPollInfoListDTO();
        subPicPollInfoListDTOS.add(subPicPollInfoListDTO);
        subPicPollInfoListDTO.setInterval(3600);
        List<MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO> participantIds = new ArrayList<>();
        subPicPollInfoListDTO.setParticipantIds(participantIds);


        MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantIdsDTO = new MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO();
        participantIds.add(participantIdsDTO);
        participantIdsDTO.setStreamNumber(0);
        participantIdsDTO.setParticipantId(attendeeSmc3.getParticipantUuid());

        MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantIdsDTO2 = new MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO();
        participantIds.add(participantIdsDTO2);
        participantIdsDTO2.setStreamNumber(0);
        participantIdsDTO2.setParticipantId(attendeeSmc3.getParticipantUuid());


        return multiPicPollRequest_Parse;
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
    public void setPause(boolean isPause) {
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
        String smc3conferenceId = conferenceContext.getSmc3conferenceId();
        MultiPicPollRequest multiPicPollRequest = conferenceContext.getMultiPicPollRequest();

        this.isPause = isPause;
        synchronized (this) {
            if (isPause) {
                multiPicPollRequest.setPollStatus(PollOperateTypeDto.STOP.name());
                conferenceContext.setMultiPicPollStatus(PollOperateTypeDto.STOP.name());
                if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                    bridge.getSmcMultiPicPollInvoker().stopMultiPicPollCascade(smc3conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                } else {
                    bridge.getSmcMultiPicPollInvoker().stopMultiPicPoll(smc3conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, true);
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询已进入暂停状态");
            } else {
                this.notify();
                multiPicPollRequest.setPollStatus(PollOperateTypeDto.START.name());
                conferenceContext.setMultiPicPollStatus(PollOperateTypeDto.START.name());

                if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                    bridge.getSmcMultiPicPollInvoker().startMultiPicPollCascade(smc3conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                } else {
                    bridge.getSmcMultiPicPollInvoker().startMultiPicPoll(smc3conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, false);
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询已恢复运行状态");
            }
        }
    }

    @Override
    public void cancel() {

        this.cancel = true;
        this.isPolling = false;
        boolean canceled = isCancel();
        super.cancel();
        MultiPicPollRequest multiPicPollRequest = conferenceContext.getMultiPicPollRequest();
        multiPicPollRequest.setPollStatus(PollOperateTypeDto.CANCEL.name());
        conferenceContext.setStartRound(false);
        conferenceContext.setMultiPicPollStatus(PollOperateTypeDto.CANCEL.name());
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
        String smc3conferenceId = conferenceContext.getSmc3conferenceId();

        if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
            bridge.getSmcMultiPicPollInvoker().cancelMultiPicPollCascade(smc3conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        } else {
            bridge.getSmcMultiPicPollInvoker().cancelMultiPicPoll(smc3conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }

        if (!canceled) {
            logger.info("----结束轮询----" + conferenceContext.getName());
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_ENDED, "轮询结束");

            StringBuilder messageTip1 = new StringBuilder();
            messageTip1.append("轮询已结束");
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip1);
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_ENDED, false);
            pollingAttendeeSmc3List.clear();
            parseFromSmc();
            for (AttendeeSmc3 smc3 : pollingAttendeeSmc3List) {
                if (smc3.isMeetingJoined()) {
                    AttendeeImportance.COMMON.processAttendeeWebsocketMessage(smc3);
                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, smc3.getUpdateMap());
                }
                if (smc3 instanceof McuAttendeeSmc3) {
                    AttendeeSmc3 attendeeById = conferenceContext.getAttendeeBySmc3Id(smc3.getParticipantUuid());
                    AttendeeImportance.COMMON.processAttendeeWebsocketMessage(attendeeById);
                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeById.getUpdateMap());
                }

            }
            this.pollingAttendeeSmc3List.clear();
        }

        List<McuAttendeeSmc3> mcuAttendees = conferenceContext.getMcuAttendees();
        for (McuAttendeeSmc3 mcuAttendee : mcuAttendees) {
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

    public boolean isCascadePolling(Smc3ConferenceContext conferenceContext, MultiPicPollRequest multiPicPollRequest) {
        List<MultiPicPollRequest.SubPicPollInfoListDTO> subPicPollInfoList = multiPicPollRequest.getSubPicPollInfoList();
        for (MultiPicPollRequest.SubPicPollInfoListDTO subPicPollInfoListDTO : subPicPollInfoList) {
            List<MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO> participantIds = subPicPollInfoListDTO.getParticipantIds();
            for (MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantId : participantIds) {
                AttendeeSmc3 attendeeBySmc3Id = conferenceContext.getAttendeeBySmc3Id(participantId.getParticipantId());
                if (attendeeBySmc3Id == null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 操作方法
     *
     * @author lilinhai
     * @since 2021-02-20 16:39  void
     */
    @Override
    public void operate() {
        this.cancel = false;
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
        String smc3conferenceId = conferenceContext.getSmc3conferenceId();
        MultiPicPollRequest multiPicPollRequest = conferenceContext.getMultiPicPollRequest();
        multiPicPollRequest.setConferenceId(smc3conferenceId);

        if (conferenceContext.isUpCascadeConference() && multiPicPollRequest.getPicNum() == 1 && isCascadePolling(conferenceContext, multiPicPollRequest)) {

            if (Objects.equals(multiPicPollRequest.getPollStatus(), PollOperateTypeDto.SET_AND_START.name()) || Objects.equals(multiPicPollRequest.getPollStatus(), PollOperateTypeDto.START.name())) {
                Integer interval = multiPicPollRequest.getSubPicPollInfoList().get(0).getInterval();
                conferenceContext.setStartRound(true);
                while (true) {
                    try {
                        Smc3ConferenceContext smc3ConferenceContext = Smc3ConferenceContextCache.getInstance().get(conferenceContext.getContextKey());
                        if (smc3ConferenceContext == null) {
                            break;
                        }
                        if (this.cancel || conferenceContext.isEnd() || conferenceContext.getAttendeeOperation() != PollingAttendeeOperation.this) {
                            break;
                        }
                        if (Thread.currentThread().isInterrupted()) {
                            return;
                        }
                        if (isPause) {
                            synchronized (this) {
                                if (isPause) {
                                    try {
                                        this.wait();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, false);
                                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "多画面轮询暂停状态");
                                }
                            }
                        }

                        pollingAttendeeSmc3List.clear();
                        parseFromSmc();
                        List<AttendeeSmc3> pollingAttendeeSmc3List = getPollingAttendeeSmc3List();

                        if (!isPause) {
                            for (int i = 0; i < pollingAttendeeSmc3List.size(); i++) {

                                if (Thread.currentThread().isInterrupted()) {
                                    return;
                                }
                                if (this.cancel || conferenceContext.isEnd() || conferenceContext.getAttendeeOperation() != PollingAttendeeOperation.this) {
                                    return;
                                }
                                if (isPause) {
                                    synchronized (this) {
                                        if (isPause) {
                                            try {
                                                this.wait();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, false);
                                            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "多画面轮询暂停状态");
                                        }
                                    }
                                }


                                AttendeeSmc3 attendeeSmc3 = pollingAttendeeSmc3List.get(i);

                                if (attendeeSmc3 instanceof McuAttendeeSmc3) {
                                    BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(attendeeSmc3.getCascadeConferenceId()));
                                    if (downCascadeConferenceContext != null) {
                                        ConferenceCascadeHandler.chooseSee(downCascadeConferenceContext.getId(), attendeeSmc3.getId(), true, true, true);
                                    }
                                }


                                MultiPicPollRequest multiPicPollRequest1 = parseMutilPic(multiPicPollRequest, attendeeSmc3);
                                multiPicPollRequest1.setPollStatus(PollOperateTypeDto.SET_AND_START.name());
                                bridge.getSmcMultiPicPollInvoker().createMultiPicPoll(smc3conferenceId, multiPicPollRequest1, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());


                                for (AttendeeSmc3 smc3 : pollingAttendeeSmc3List) {

                                    if (smc3 instanceof McuAttendeeSmc3) {
                                        AttendeeSmc3 attendeeById = conferenceContext.getAttendeeBySmc3Id(smc3.getParticipantUuid());
                                        AttendeeImportance.COMMON.processAttendeeWebsocketMessage(attendeeById);
                                        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeById.getUpdateMap());


                                    } else {
                                        if (smc3.isMeetingJoined()) {
                                            AttendeeImportance.COMMON.processAttendeeWebsocketMessage(smc3);
                                            if (smc3.getUpdateMap().size() > 1) {
                                                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, smc3.getUpdateMap());
                                            }
                                        }
                                    }
                                }


                                if (lastAutoPollingAttendee != null) {
                                    if (lastAutoPollingAttendee instanceof McuAttendeeSmc3) {
                                        BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(lastAutoPollingAttendee.getCascadeConferenceId()));
                                        BaseAttendee attendeeById = downCascadeConferenceContext.getAttendeeById(lastAutoPollingAttendee.getId());
                                        attendeeById.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                                        attendeeById.setBroadcastStatus(BroadcastStatus.NO.getValue());
                                        BaseWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(downCascadeConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeById);
                                    }
                                }

                                AttendeeImportance.ROUND_BROADCAST.processAttendeeWebsocketMessage(attendeeSmc3);
                                if (attendeeSmc3 instanceof McuAttendeeSmc3) {
                                    AttendeeSmc3 attendeeBySmc3Id = conferenceContext.getAttendeeBySmc3Id(attendeeSmc3.getParticipantUuid());
                                    if (attendeeBySmc3Id != null) {
                                        AttendeeImportance.ROUND_BROADCAST.processAttendeeWebsocketMessage(attendeeBySmc3Id);
                                    }
                                }


                                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_STARTED, true);
                                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询开始");
                                reInterrupt(interval * 1000);

                                if (Thread.currentThread().isInterrupted()) {
                                    return;
                                }
                                Smc3ConferenceContext s = Smc3ConferenceContextCache.getInstance().get(conferenceContext.getContextKey());
                                if (s == null) {
                                    return;
                                }
                                if (pollingAttendeeSmc3List.size() > 0) {
                                    this.lastAutoPollingAttendee = pollingAttendeeSmc3List.get(i);
                                }

                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }

                }


            }


        } else {
            if (Objects.equals(PollOperateTypeDto.SET.name(), multiPicPollRequest.getPollStatus())) {
                if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                    bridge.getSmcMultiPicPollInvoker().createMultiPicPollCascade(smc3conferenceId, multiPicPollRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                } else {
                    bridge.getSmcMultiPicPollInvoker().createMultiPicPoll(smc3conferenceId, multiPicPollRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }
                conferenceContext.setMultiPicPollRequest(multiPicPollRequest);
            }
            if (Objects.equals(PollOperateTypeDto.START.name(), multiPicPollRequest.getPollStatus())) {

                pollingAttendeeSmc3List.clear();
                parseFromSmc();
                if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                    bridge.getSmcMultiPicPollInvoker().startMultiPicPollCascade(smc3conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                } else {
                    bridge.getSmcMultiPicPollInvoker().startMultiPicPoll(smc3conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }
                conferenceContext.setStartRound(true);
            }
            if (Objects.equals(PollOperateTypeDto.SET_AND_START.name(), multiPicPollRequest.getPollStatus())) {

                pollingAttendeeSmc3List.clear();
                parseFromSmc();
                if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                    bridge.getSmcMultiPicPollInvoker().createMultiPicPollCascade(smc3conferenceId, multiPicPollRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                } else {
                    bridge.getSmcMultiPicPollInvoker().createMultiPicPoll(smc3conferenceId, multiPicPollRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }

                if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                    bridge.getSmcMultiPicPollInvoker().startMultiPicPollCascade(smc3conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                } else {
                    bridge.getSmcMultiPicPollInvoker().startMultiPicPoll(smc3conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }

                multiPicPollRequest.setPollStatus(PollOperateTypeDto.START.name());
                conferenceContext.setMultiPicPollRequest(multiPicPollRequest);
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_STARTED, true);
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询开始");
                conferenceContext.setStartRound(true);
            }

            if (Objects.equals(PollOperateTypeDto.STOP.name(), multiPicPollRequest.getPollStatus())) {
                multiPicPollRequest.setPollStatus(PollOperateTypeDto.STOP.name());
                conferenceContext.setMultiPicPollStatus(PollOperateTypeDto.STOP.name());

                if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                    bridge.getSmcMultiPicPollInvoker().stopMultiPicPollCascade(smc3conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                } else {
                    bridge.getSmcMultiPicPollInvoker().stopMultiPicPoll(smc3conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, true);
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询已进入暂停状态");
                conferenceContext.setStartRound(false);
            }

            if (Objects.equals(PollOperateTypeDto.CANCEL.name(), multiPicPollRequest.getPollStatus())) {
                if (Objects.equals(conferenceContext.getMultiPicPollStatus(), PollOperateTypeDto.CANCEL.name())) {
                    return;
                }
                multiPicPollRequest.setPollStatus(PollOperateTypeDto.CANCEL.name());
                conferenceContext.setStartRound(false);
                conferenceContext.setMultiPicPollStatus(PollOperateTypeDto.CANCEL.name());

                if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                    bridge.getSmcMultiPicPollInvoker().cancelMultiPicPollCascade(smc3conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                } else {
                    bridge.getSmcMultiPicPollInvoker().cancelMultiPicPoll(smc3conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }
                logger.info("----结束轮询----" + conferenceContext.getName());
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_ENDED, "轮询结束");

                StringBuilder messageTip1 = new StringBuilder();
                messageTip1.append("轮询已结束");
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip1);
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_ENDED, false);
                pollingAttendeeSmc3List.clear();
                parseFromSmc();
                for (AttendeeSmc3 attendeeSmc3 : pollingAttendeeSmc3List) {
                    AttendeeImportance.COMMON.processAttendeeWebsocketMessage(attendeeSmc3);
                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeSmc3.getUpdateMap());

                }
                conferenceContext.setStartRound(false);
            }
        }


    }

    private void defaultChooseSee(AttendeeSmc3 attendeeSmc3OLd) {
        Thread thread = new Thread(() -> {
            Threads.sleep(1000);
            BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(attendeeSmc3OLd.getCascadeConferenceId()));
            if (downCascadeConferenceContext != null) {
                ConferenceCascadeHandler.defaultChooseSee(downCascadeConferenceContext.getId());
            }
        });
        thread.start();
    }


    public void setMute(String conferenceId, boolean b, Smc3Bridge smc3Bridge) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isMute", b);
        smc3Bridge.getSmcConferencesInvoker().conferencesControl(conferenceId, jsonObject.toJSONString(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    private void initTargetAttendees() throws Exception {
        autoPollingScreenCount = getSplitScreen().getCellScreens().size();
        autoPollingAttendeeIdSet.clear();
        this.parse();

        if (pollingAttendeeList.size() <= autoPollingScreenCount) {
            lastAutoPollingIdx = -1;
        }
    }

    private void initOperationAttendees() {
        targetAttendees.clear();
        if (!ObjectUtils.isEmpty(attendees)) {
            targetAttendees.addAll(attendees);
        }

        if (conferenceContext.getMasterAttendee() != null) {
            if (!checkedAttendeeIdSet.contains(conferenceContext.getMasterAttendee().getId())) {
                targetAttendees.add(conferenceContext.getMasterAttendee());
            }

            if (conferenceContext.getMasterAttendee().getDeptId() != conferenceContext.getDeptId().longValue() && conferenceContext.getMasterAttendeeIdSet().contains(conferenceContext.getMasterAttendee().getId())) {
                List<AttendeeSmc3> as = conferenceContext.getCascadeAttendeesMap().get(conferenceContext.getMasterAttendee().getDeptId());
                if (as != null) {
                    for (AttendeeSmc3 attendee : new ArrayList<>(as)) {
                        if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                            targetAttendees.add(attendee);
                        }
                    }
                }
            }
        }

        for (AttendeeSmc3 attendee : new ArrayList<>(conferenceContext.getAttendees())) {
            if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                targetAttendees.add(attendee);
            }
        }

        for (AttendeeSmc3 attendee : conferenceContext.getMasterAttendees()) {
            if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                targetAttendees.add(attendee);
            }
            List<AttendeeSmc3> as = conferenceContext.getCascadeAttendeesMap().get(attendee.getDeptId());
            if (as != null) {
                for (AttendeeSmc3 a : new ArrayList<>(as)) {
                    if (!checkedAttendeeIdSet.contains(a.getId())) {
                        targetAttendees.add(a);
                    }
                }
            }
        }


        Set<String> idSet = new HashSet<>();

        if (!CollectionUtils.isEmpty(targetAttendees)) {
            List<AttendeeSmc3> arrayList = new ArrayList<>();
            for (AttendeeSmc3 attendee : targetAttendees) {
                if (idSet.add(attendee.getId())) {
                    arrayList.add(attendee);
                }
            }
            targetAttendees = arrayList;
        }
    }

    public List<AttendeeSmc3> getPollingAttendeeSmc3List() {
        return pollingAttendeeSmc3List;
    }

    private void reInterrupt(long timemillis) {
        try {
            long sleepSeconds = timemillis / 1000;
            long remainMillis = timemillis % 1000;
            if(timemillis>1000){
                for (int i = 0; i < sleepSeconds; i++) {
                    if (!isOk()) {
                        cancel();
                    }
                    Thread.sleep(1000);
                }
                if (!isOk()) {
                    cancel();
                }
                Thread.sleep(remainMillis);
            }else {
                if (!isOk()) {
                    cancel();
                }
                Thread.sleep(timemillis);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    public boolean isPolling() {
        return isPolling;
    }

    public void setPolling(boolean polling) {
        isPolling = polling;
    }


    public boolean isOk(){
        Smc3ConferenceContext s = Smc3ConferenceContextCache.getInstance().get(conferenceContext.getContextKey());
        if (s == null) {
            return false;
        }
        if (this.cancel || conferenceContext.isEnd() || conferenceContext.getAttendeeOperation() != PollingAttendeeOperation.this) {
            return false;
        }
        return true;
    }
}
