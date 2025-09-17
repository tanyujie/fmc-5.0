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
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.PollOperateTypeDto;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.cascade.ConferenceCascadeHandler;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.McuAttendeeSmc3;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.enumer.AttendeeImportance;
import com.paradisecloud.smc3.busi.layout.*;
import com.paradisecloud.smc3.busi.operation.polling.PollingScheme;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.smc3.model.ConstAPI;
import com.paradisecloud.smc3.model.ParticipantStatus;
import com.paradisecloud.smc3.model.ParticipantStatusDto;
import com.paradisecloud.smc3.model.request.ChairmanPollOperateReq;
import com.paradisecloud.smc3.model.request.ConferenceStatusRequest;
import com.paradisecloud.smc3.model.request.MultiPicInfoReq;
import com.paradisecloud.smc3.model.request.MultiPicPollRequest;
import com.paradisecloud.smc3.model.response.VideoSourceRep;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 主席轮询
 *
 * @author nj
 * @date 2016/10/31
 */
public class ChairmanPollingAttendeeOperation extends AttendeeOperation {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     *
     * @since 2021-02-26 15:55
     */
    private static final long serialVersionUID = 1L;
    private final List<AttendeeSmc3> pollingAttendeeSmc3List = new ArrayList<>();
    private volatile PollingScheme pollingScheme;
    /**
     * 暂停
     */
    private volatile boolean isPause = false;
    private volatile long lastUpdateTime = 0;
    private final Set<String> sourceP = new HashSet<>();
    private volatile Thread pollingThread = null;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @param pollingScheme
     * @author lilinhai
     * @since 2021-02-26 15:56
     */
    public ChairmanPollingAttendeeOperation(Smc3ConferenceContext conferenceContext, PollingScheme pollingScheme) {
        super(conferenceContext);
        this.pollingScheme = pollingScheme;
        initSplitScreen();
        Assert.isTrue(!(this.splitScreen instanceof AutomaticSplitScreen), "轮询操作不支持自动分屏");
    }

    public ChairmanPollingAttendeeOperation(Smc3ConferenceContext conferenceContext) {
        super(conferenceContext);

    }


    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
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
        this.isPause = isPause;
        synchronized (this) {
            if (isPause) {
                ChairmanPollOperateReq chairmanPollOperateReq = new ChairmanPollOperateReq();
                chairmanPollOperateReq.setPollStatus(PollOperateTypeDto.STOP);
                conferenceContext.setChairmanPollStatus(PollOperateTypeDto.STOP);
                if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                    bridge.getSmcMultiPicPollInvoker().chairmanParticipantMultiPicPollOperateCascade(smc3conferenceId, chairmanPollOperateReq, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                } else {
                    bridge.getSmcMultiPicPollInvoker().chairmanParticipantMultiPicPollOperate(smc3conferenceId, chairmanPollOperateReq, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }

                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, true);
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询已进入暂停状态");
            } else {
                this.notify();
                ChairmanPollOperateReq chairmanPollOperateReq = new ChairmanPollOperateReq();
                chairmanPollOperateReq.setPollStatus(PollOperateTypeDto.START);
                conferenceContext.setChairmanPollStatus(PollOperateTypeDto.START);
                if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                    bridge.getSmcMultiPicPollInvoker().chairmanParticipantMultiPicPollOperateCascade(smc3conferenceId, chairmanPollOperateReq, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                } else {
                    bridge.getSmcMultiPicPollInvoker().chairmanParticipantMultiPicPollOperate(smc3conferenceId, chairmanPollOperateReq, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, false);
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询已恢复运行状态");
            }
        }
    }

    @Override
    public void cancel() {
        try {
            try {
                if (this.pollingThread != null) {
                    pollingThread.interrupt();
                    this.pollingThread = null;
                }
                Thread.sleep(200);
            } catch (Exception e) {
                e.printStackTrace();
            }
            boolean canceled = isCancel();
            super.cancel();
            conferenceContext.setStartRound(false);
            conferenceContext.setChairmanPollStatus(PollOperateTypeDto.CANCEL);
            conferenceContext.getChairmanMultiPicPollRequest().setPollStatus(PollOperateTypeDto.CANCEL.name());
            Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
            String smc3conferenceId = conferenceContext.getSmc3conferenceId();
            ChairmanPollOperateReq chairmanPollOperateReq = new ChairmanPollOperateReq();
            chairmanPollOperateReq.setConferenceId(smc3conferenceId);
            chairmanPollOperateReq.setPollStatus(PollOperateTypeDto.CANCEL);
            if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                bridge.getSmcMultiPicPollInvoker().chairmanParticipantMultiPicPollOperateCascade(smc3conferenceId, chairmanPollOperateReq, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            } else {
                bridge.getSmcMultiPicPollInvoker().chairmanParticipantMultiPicPollOperate(smc3conferenceId, chairmanPollOperateReq, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }

            if (!canceled) {
                logger.info("----结束轮询----" + conferenceContext.getName());
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_ENDED, "轮询结束");

                StringBuilder messageTip1 = new StringBuilder();
                messageTip1.append("轮询已结束");
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip1);
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_ENDED, false);
                sourceP.clear();
                getAlLPollingIds();
                for (String s : sourceP) {
                    AttendeeSmc3 attendeeBySmc3Id = conferenceContext.getAttendeeBySmc3Id(s);
                    if(attendeeBySmc3Id!=null){
                        AttendeeImportance.COMMON.processAttendeeWebsocketMessage(attendeeBySmc3Id);
                        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeBySmc3Id.getUpdateMap());
                    }
                }
                sourceP.clear();


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

            for (AttendeeSmc3 attendeeSmc3 : pollingAttendeeSmc3List) {
                if (attendeeSmc3 instanceof McuAttendeeSmc3) {
                    AttendeeSmc3 attendeeById = conferenceContext.getAttendeeBySmc3Id(attendeeSmc3.getParticipantUuid());
                    AttendeeImportance.COMMON.processAttendeeWebsocketMessage(attendeeById);
                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeById.getUpdateMap());

                }else {
                    AttendeeImportance.COMMON.processAttendeeWebsocketMessage(attendeeSmc3);
                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeSmc3.getUpdateMap());
                }
            }
            pollingAttendeeSmc3List.clear();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void getAlLPollingIds() {
        MultiPicPollRequest multiPicPollRequest = conferenceContext.getChairmanMultiPicPollRequest();
        List<MultiPicPollRequest.SubPicPollInfoListDTO> subPicPollInfoList = multiPicPollRequest.getSubPicPollInfoList();
        for (MultiPicPollRequest.SubPicPollInfoListDTO subPicPollInfoListDTO : subPicPollInfoList) {
            List<MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO> participantIds = subPicPollInfoListDTO.getParticipantIds();
            for (MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantIdChair : participantIds) {
                String participantId1 = participantIdChair.getParticipantId();
                sourceP.add(participantId1);
            }
        }
    }


    /**
     * 操作方法
     *
     * @author lilinhai
     * @since 2021-02-20 16:39  void
     */
    @Override
    public void operate() {
        if (isCancel() || isPause()) {
            return;
        }
        if (conferenceContext.getMasterAttendee() == null||!conferenceContext.getMasterAttendee().isMeetingJoined()||!conferenceContext.getMasterAttendee().isOnline()) {
            return;
        }

        if (Objects.equals(conferenceContext.getChairmanPollStatus().name(), PollOperateTypeDto.START.name())) {
            return;
        }
        String chairmanId = conferenceContext.getMasterAttendee().getSmcParticipant().getGeneralParam().getId();
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
        String smc3conferenceId = conferenceContext.getSmc3conferenceId();
        MultiPicPollRequest multiPicPollRequest = conferenceContext.getChairmanMultiPicPollRequest();
        if (multiPicPollRequest == null) {
            throw new CustomException("未设置主席轮询");
        }
        multiPicPollRequest.setConferenceId(smc3conferenceId);

        if (Objects.equals(multiPicPollRequest.getPollStatus(), PollOperateTypeDto.START.name())) {
            conferenceContext.setChairmanPollStatus(PollOperateTypeDto.START);
            //锁定视频源
            ParticipantStatus participantStatus = new ParticipantStatus();
            participantStatus.setVideoSwitchAttribute("CUSTOMIZED");
            if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                bridge.getSmcParticipantsInvoker().conferencesParticipantStatusOnlyCascade(smc3conferenceId, chairmanId, participantStatus, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            } else {
                bridge.getSmcParticipantsInvoker().conferencesParticipantStatusOnly(smc3conferenceId, chairmanId, participantStatus, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }

//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("isMute", true);
//            if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
//                bridge.getSmcConferencesInvoker().conferencesControlCascade(smc3conferenceId, jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
//            } else {
//                bridge.getSmcConferencesInvoker().conferencesControl(smc3conferenceId, jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
//            }
            ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
            conferenceStatusRequest.setBroadcaster(chairmanId);
            if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                bridge.getSmcConferencesInvoker().conferencesStatusControlCascade(smc3conferenceId, conferenceStatusRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            } else {
                bridge.getSmcConferencesInvoker().conferencesStatusControl(smc3conferenceId, conferenceStatusRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }

            if (multiPicPollRequest.getPicNum() == 1 && conferenceContext.isUpCascadeConference() && isCascadePolling(conferenceContext, multiPicPollRequest)) {

                pollingAttendeeSmc3List.clear();
                parseFromSmc();
                for (AttendeeSmc3 attendeeSmc3 : pollingAttendeeSmc3List) {
                    if (attendeeSmc3 instanceof McuAttendeeSmc3) {
                        AttendeeSmc3 attendeeById = conferenceContext.getAttendeeBySmc3Id(attendeeSmc3.getParticipantUuid());
                        AttendeeImportance.COMMON.processAttendeeWebsocketMessage(attendeeById);
                        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeById.getUpdateMap());

                    }else {
                        AttendeeImportance.COMMON.processAttendeeWebsocketMessage(attendeeSmc3);
                        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeSmc3.getUpdateMap());
                    }
                }
                conferenceContext.setChairmanPollStatus(PollOperateTypeDto.START);
                Thread thread = new Thread(() -> {
                    while (true) {
                        try {
                            Smc3ConferenceContext smc3ConferenceContext = Smc3ConferenceContextCache.getInstance().get(conferenceContext.getContextKey());
                            if(smc3ConferenceContext==null){
                                break;
                            }
                            if (isCancel || conferenceContext.isEnd() || conferenceContext.getAttendeeOperation() != ChairmanPollingAttendeeOperation.this) {
                                pollingAttendeeSmc3List.clear();
                                break;
                            }
                            if (Thread.currentThread().isInterrupted()) {
                                break;
                            }
                            Integer interval = multiPicPollRequest.getSubPicPollInfoList().get(0).getInterval();
                            if (isPause) {
                                synchronized (this) {
                                    if (isPause) {
                                        this.wait();
                                        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, false);
                                        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "主席轮询暂停状态");
                                    }
                                }
                            }

                            if (!isPause) {

                                for (AttendeeSmc3 attendeeSmc3 : pollingAttendeeSmc3List) {
                                    if (isCancel || conferenceContext.isEnd() || conferenceContext.getAttendeeOperation() != ChairmanPollingAttendeeOperation.this) {
                                        pollingAttendeeSmc3List.clear();
                                        break;
                                    }
                                    Smc3ConferenceContext smc3ConferenceContext3 = Smc3ConferenceContextCache.getInstance().get(conferenceContext.getContextKey());
                                    if(smc3ConferenceContext3==null){
                                        return;
                                    }

                                    if (Thread.currentThread().isInterrupted()) {
                                        break;
                                    }
                                    if (isPause) {
                                        synchronized (this) {
                                            if (isPause) {
                                                this.wait();
                                                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, false);
                                                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "主席轮询暂停状态");
                                            }
                                        }
                                    }
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


                                    if (attendeeSmc3 instanceof McuAttendeeSmc3) {
                                        AttendeeSmc3 attendeeById = conferenceContext.getAttendeeBySmc3Id(attendeeSmc3.getParticipantUuid());
                                        AttendeeImportance.ROUND.processAttendeeWebsocketMessage(attendeeById);
                                        BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(attendeeSmc3.getCascadeConferenceId()));
                                        if (downCascadeConferenceContext != null) {
                                            ConferenceCascadeHandler.chooseSee(downCascadeConferenceContext.getId(), attendeeSmc3.getId(), true, false, true);
                                        }
                                    }


                                    MultiPicPollRequest multiPicPollRequest1 = parseMutilPic(multiPicPollRequest, attendeeSmc3);
                                    multiPicPollRequest1.setPollStatus(PollOperateTypeDto.SET_AND_START.name());
                                    bridge.getSmcMultiPicPollInvoker().chairmanParticipantMultiPicPoll(smc3conferenceId, multiPicPollRequest1, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

                                    //bridge.getSmcMultiPicPollInvoker().chairmanParticipantMultiPicPollOperate(smc3conferenceId, chairmanPollOperateReq, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());




                                    AttendeeImportance.ROUND.processAttendeeWebsocketMessage(attendeeSmc3);


                                    //解锁视频源
                                    participantStatus.setVideoSwitchAttribute("AUTO");
                                    bridge.getSmcParticipantsInvoker().conferencesParticipantStatusOnly(smc3conferenceId, chairmanId, participantStatus, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                                    reInterrupt(interval * 1000);
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                });
                thread.start();
                this.pollingThread = thread;

            } else {
                ChairmanPollOperateReq chairmanPollOperateReq = new ChairmanPollOperateReq();
                chairmanPollOperateReq.setPollStatus(PollOperateTypeDto.START);
                if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                    bridge.getSmcMultiPicPollInvoker().chairmanParticipantMultiPicPollOperateCascade(smc3conferenceId, chairmanPollOperateReq, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

                } else {
                    bridge.getSmcMultiPicPollInvoker().chairmanParticipantMultiPicPollOperate(smc3conferenceId, chairmanPollOperateReq, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }
                smc3polling(chairmanId, bridge, smc3conferenceId);
            }
            //解锁视频源
            participantStatus.setVideoSwitchAttribute("AUTO");
            if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                bridge.getSmcParticipantsInvoker().conferencesParticipantStatusOnlyCascade(smc3conferenceId, chairmanId, participantStatus, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            } else {
                bridge.getSmcParticipantsInvoker().conferencesParticipantStatusOnly(smc3conferenceId, chairmanId, participantStatus, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }
            conferenceContext.setChairmanPollStatus(PollOperateTypeDto.START);
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

    private void parseFromSmc() {
        MultiPicPollRequest multiPicPollRequest = conferenceContext.getChairmanMultiPicPollRequest();
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


    private void smc3polling(String chairmanId, Smc3Bridge bridge, String smc3conferenceId) {
        sourceP.clear();
        getAlLPollingIds();
        Thread thread = new Thread(() -> {
            Set<String> tempIds = new HashSet<>();
            while (true) {
                try {
                    if (isCancel || conferenceContext.isEnd() || conferenceContext.getAttendeeOperation() != ChairmanPollingAttendeeOperation.this) {
                        tempIds.clear();
                        break;
                    }
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }

                    if (isPause) {
                        synchronized (this) {
                            if (isPause) {
                                this.wait();
                                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, false);
                                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "主席轮询暂停状态");
                            }
                        }
                    }

                    if (!isPause) {

                        AttendeeOperation lastAttendeeOperation = conferenceContext.getLastAttendeeOperation();
                        if(lastAttendeeOperation!=null){
                            Threads.sleep(1000);
                        }

                        List<String> chairmanIdList = new ArrayList<>();
                        chairmanIdList.add(chairmanId);
                        List<VideoSourceRep> chairmanIdvideoSourceReps = bridge.getSmcConferencesInvoker().conferencesVideoSource(smc3conferenceId, chairmanIdList, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

                        if (!CollectionUtils.isEmpty(chairmanIdvideoSourceReps)) {
                            if (!CollectionUtils.isEmpty(tempIds)) {
                                for (String tempId : tempIds) {
                                    AttendeeSmc3 attendeeBySmc3Id = conferenceContext.getAttendeeBySmc3Id(tempId);
                                    AttendeeImportance.COMMON.processAttendeeWebsocketMessage(attendeeBySmc3Id);
                                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeBySmc3Id.getUpdateMap());
                                }
                                tempIds.clear();
                            }
                            for (VideoSourceRep chairmanIdvideoSourceRep : chairmanIdvideoSourceReps) {
                                MultiPicInfoReq.MultiPicInfoDTO multiPicInfo = chairmanIdvideoSourceRep.getMultiPicInfo();
                                if (multiPicInfo != null) {
                                    List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList = multiPicInfo.getSubPicList();
                                    if (!CollectionUtils.isEmpty(subPicList)) {
                                        List<String> participants = new ArrayList<>();
                                        for (MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicList) {
                                            String participantId = subPicListDTO.getParticipantId();
                                            if(Strings.isNotBlank(participantId)){
                                                participants.add(participantId);
                                                tempIds.add(participantId);
                                                AttendeeImportance.ROUND.processAttendeeWebsocketMessage(conferenceContext.getAttendeeBySmc3Id(participantId));
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                    reInterrupt(2500);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }

            }


        });
        thread.start();
        this.pollingThread = thread;
    }


    private void reInterrupt(long timemillis) {
        try {
            long sleepSeconds = timemillis / 1000;
            long remainMillis = timemillis % 1000;
            if(timemillis>1000){
                for (int i = 0; i < sleepSeconds; i++) {
                    if (isMasterLeft()) {
                        cancel();
                    }
                    Thread.sleep(1000);
                }
                if (isMasterLeft()) {
                    cancel();
                }
                Thread.sleep(remainMillis);
            }else {
                if (isMasterLeft()) {
                    cancel();
                }
                Thread.sleep(timemillis);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    private boolean isMasterLeft() {
        if(conferenceContext.getMasterAttendee()==null){
            return true;
        }
        if(!conferenceContext.getMasterAttendee().isMeetingJoined()||!conferenceContext.getMasterAttendee().isOnline()){
            return true;
        }
        return false;
    }

    private void muteSite(String conferenceId, String participantId, Smc3Bridge bridge,Boolean mute) {
        List<ParticipantStatusDto> participantStatusList = new ArrayList<>();
        ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
        participantStatusDto.setId(participantId);
        participantStatusDto.setIsMute(mute);
        participantStatusList.add(participantStatusDto);
        if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
            bridge.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(conferenceId, participantStatusList, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }else {
            bridge.getSmcParticipantsInvoker().PATCHParticipantsOnly(conferenceId, participantStatusList, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }
    }

}
