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
import com.paradisecloud.com.fcm.smc.modle.ConferenceState;
import com.paradisecloud.com.fcm.smc.modle.request.MultiPicPollRequest;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.cache.BaseWebSocketMessagePusher;
import com.paradisecloud.fcm.service.conference.cascade.ConferenceCascadeHandler;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContextCache;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.model.AttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.McuAttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.operation.polling.PollingAttendee;
import com.paradisecloud.fcm.smc2.model.attendee.operation.polling.PollingScheme;
import com.paradisecloud.fcm.smc2.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.smc2.model.layout.ContinuousPresenceModeEnum;
import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.common.State;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;
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
    private final List<AttendeeSmc2> pollingAttendeeSmc2List = new ArrayList<>();
    private volatile PollingScheme pollingScheme;
    private volatile List<PollingAttendee> pollingAttendeeList;
    private volatile int lastAutoPollingIdx = -1;
    private volatile int autoPollingScreenCount = 0;
    private final Set<String> autoPollingAttendeeIdSet = new HashSet<>();
    private volatile List<AttendeeSmc2> targetAttendees = new ArrayList<>();
    private final Set<String> checkedAttendeeIdSet = new HashSet<>();
    /**
     * 暂停
     */
    private volatile boolean isPause;
    private volatile long lastUpdateTime = 0;
    private volatile boolean cancel;
    private volatile boolean isPolling;
    private JSONObject jsonObject;
    private volatile AttendeeSmc2 lastAutoPollingAttendee;
    private MultiPicPollRequest multiPicPollRequest;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @param pollingScheme
     * @author lilinhai
     * @since 2021-02-26 15:56
     */
    public PollingAttendeeOperation(Smc2ConferenceContext conferenceContext, PollingScheme pollingScheme) {
        super(conferenceContext);
        this.pollingScheme = pollingScheme;
        initSplitScreen();
    }

    public PollingAttendeeOperation(Smc2ConferenceContext conferenceContext, JSONObject jsonObject) {
        super(conferenceContext);
        this.jsonObject = jsonObject;
    }


    public PollingAttendeeOperation(Smc2ConferenceContext conferenceContext, MultiPicPollRequest multiPicPollRequest) {
        super(conferenceContext);
        this.multiPicPollRequest = multiPicPollRequest;

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
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip1);
                throw new CustomException(messageTip1.toString());
            }
        }
        idSet.clear();
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
        this.isPause = isPause;
        synchronized (this) {
            if (isPause) {
                multiPicPollRequest.setPollStatus(PollOperateTypeDto.STOP.name());
                ConferenceServiceEx conferenceServiceEx = conferenceContext.getSmc2Bridge().getConferenceServiceEx();
                State state = State.THREAD_SUPEND;
                int result = conferenceServiceEx.setContinuousPresencePollingStateEx(conferenceContext.getSmc2conferenceId(), state);

                conferenceContext.setMultiPicPollStatus(PollOperateTypeDto.STOP.name());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("multiPicPollStatus", com.paradisecloud.com.fcm.smc.modle.PollOperateTypeDto.STOP.name());
                Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, true);
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询已进入暂停状态");
            } else {
                this.notify();
                multiPicPollRequest.setPollStatus(PollOperateTypeDto.START.name());
                conferenceContext.setMultiPicPollStatus(PollOperateTypeDto.START.name());
                ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
                State state = State.THREAD_START;
                int result = conferenceServiceEx.setContinuousPresencePollingStateEx(conferenceContext.getSmc2conferenceId(), state);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("multiPicPollStatus", com.paradisecloud.com.fcm.smc.modle.PollOperateTypeDto.START.name());
                Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, false);
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询已恢复运行状态");
            }
        }
    }

    @Override
    public void cancel() {
        this.isPolling = false;
        this.isPause = false;
        super.cancel();
        if (conferenceContext.isUpCascadeConference() && multiPicPollRequest.getPicNum() == 1 && isCascadePolling(conferenceContext, multiPicPollRequest)) {
            Threads.sleep(500);
        }
        MultiPicPollRequest multiPicPollRequest = conferenceContext.getMultiPicPollRequest();
        multiPicPollRequest.setPollStatus(PollOperateTypeDto.CANCEL.name());
        conferenceContext.setStartRound(false);
        conferenceContext.setMultiPicPollStatus(PollOperateTypeDto.CANCEL.name());
        String smc2conferenceId = conferenceContext.getSmc2conferenceId();

        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        State state = State.ThREAD_STOP;
        int result = conferenceServiceEx.setContinuousPresencePollingStateEx(conferenceContext.getSmc2conferenceId(), state);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("multiPicPollStatus", com.paradisecloud.com.fcm.smc.modle.PollOperateTypeDto.CANCEL.name());
        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);

        logger.info("----结束轮询----" + conferenceContext.getName());
        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_ENDED, "轮询结束");

        StringBuilder messageTip1 = new StringBuilder();
        messageTip1.append("轮询已结束");
        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip1);
        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_ENDED, false);

        if(!this.cancel){
            pollingAttendeeSmc2List.clear();
            parseFromSmc();
            for (AttendeeSmc2 smc2 : pollingAttendeeSmc2List) {
                if (smc2.isMeetingJoined()) {
                    AttendeeImportance.COMMON.processAttendeeWebsocketMessage(smc2);
                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, smc2.getUpdateMap());
                }
                if (smc2 instanceof McuAttendeeSmc2) {
                    AttendeeSmc2 attendeeById = conferenceContext.getAttendeeBySmc2Id(smc2.getParticipantUuid());
                    AttendeeImportance.COMMON.processAttendeeWebsocketMessage(attendeeById);
                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeById.getUpdateMap());
                }

            }
            this.pollingAttendeeSmc2List.clear();

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

       this.cancel=true;

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

    /**
     * 操作方法
     *
     * @author lilinhai
     * @since 2021-02-20 16:29  void
     */
    @Override
    public void operate() {
        this.cancel=false;
        String smc2conferenceId = conferenceContext.getSmc2conferenceId();
        MultiPicPollRequest multiPicPollRequest = conferenceContext.getMultiPicPollRequest();
        multiPicPollRequest.setConferenceId(smc2conferenceId);


        if (conferenceContext.isUpCascadeConference() && multiPicPollRequest.getPicNum() == 1 && isCascadePolling(conferenceContext, multiPicPollRequest)) {
            if (Objects.equals(multiPicPollRequest.getPollStatus(), PollOperateTypeDto.SET_AND_START.name()) || Objects.equals(multiPicPollRequest.getPollStatus(), PollOperateTypeDto.START.name())) {
                Integer interval = multiPicPollRequest.getSubPicPollInfoList().get(0).getInterval();
                conferenceContext.setStartRound(true);
                conferenceContext.setMultiPicPollStatus(PollOperateTypeDto.START.name());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("multiPicPollStatus", com.paradisecloud.com.fcm.smc.modle.PollOperateTypeDto.START.name());
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                while (true) {
                    try {
                        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceContext.getContextKey());
                        if (smc2ConferenceContext == null) {
                            break;
                        }
                        if (this.cancel||conferenceContext.isEnd() || conferenceContext.getAttendeeOperation() != PollingAttendeeOperation.this) {
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
                                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, false);
                                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "多画面轮询暂停状态");
                                }
                            }
                        }

                        pollingAttendeeSmc2List.clear();
                        parseFromSmc();
                        List<AttendeeSmc2> pollingAttendeeSmc2List = getPollingAttendeeSmc2List();

                        if (!isPause) {
                            for (int i = 0; i < pollingAttendeeSmc2List.size(); i++) {

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
                                            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, false);
                                            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "多画面轮询暂停状态");
                                        }
                                    }
                                }


                                AttendeeSmc2 attendeeSmc2 = pollingAttendeeSmc2List.get(i);

                                if (attendeeSmc2 instanceof McuAttendeeSmc2) {
                                    BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(attendeeSmc2.getCascadeConferenceId()));
                                    if (downCascadeConferenceContext != null) {
                                        ConferenceCascadeHandler.chooseSee(downCascadeConferenceContext.getId(), attendeeSmc2.getId(), true, true, true);
                                    }
                                }


                                setMultiPic(multiPicPollRequest, attendeeSmc2);
                                ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
                                State state = State.THREAD_START;
                                conferenceServiceEx.setContinuousPresencePollingStateEx(smc2conferenceId, state);

                                //广播多画面
                                Integer integer = conferenceServiceEx.setBroadcastContinuousPresenceEx(conferenceContext.getSmc2conferenceId(), 0);


                                for (AttendeeSmc2 smc2 : pollingAttendeeSmc2List) {

                                    if (smc2 instanceof McuAttendeeSmc2) {
                                        AttendeeSmc2 attendeeById = conferenceContext.getAttendeeBySmc2Id(smc2.getParticipantUuid());
                                        AttendeeImportance.COMMON.processAttendeeWebsocketMessage(attendeeById);
                                        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeById.getUpdateMap());


                                    } else {
                                        if (smc2.isMeetingJoined()) {
                                            AttendeeImportance.COMMON.processAttendeeWebsocketMessage(smc2);
                                            if (smc2.getUpdateMap().size() > 1) {
                                                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, smc2.getUpdateMap());
                                            }
                                        }
                                    }
                                }


                                if (lastAutoPollingAttendee != null) {
                                    if (lastAutoPollingAttendee instanceof McuAttendeeSmc2) {
                                        BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(lastAutoPollingAttendee.getCascadeConferenceId()));
                                        BaseAttendee attendeeById = downCascadeConferenceContext.getAttendeeById(lastAutoPollingAttendee.getId());
                                        attendeeById.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                                        attendeeById.setBroadcastStatus(BroadcastStatus.NO.getValue());
                                        BaseWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(downCascadeConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeById);
                                    }
                                }

                                AttendeeImportance.ROUND_BROADCAST.processAttendeeWebsocketMessage(attendeeSmc2);
                                if (attendeeSmc2 instanceof McuAttendeeSmc2) {
                                    AttendeeSmc2 attendeeBySmc2Id = conferenceContext.getAttendeeBySmc2Id(attendeeSmc2.getParticipantUuid());
                                    if (attendeeBySmc2Id != null) {
                                        AttendeeImportance.ROUND_BROADCAST.processAttendeeWebsocketMessage(attendeeBySmc2Id);
                                    }
                                }


                                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_STARTED, true);
                                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询开始");
                                reInterrupt(interval * 1000);

                                if (Thread.currentThread().isInterrupted()) {
                                    return;
                                }
                                Smc2ConferenceContext s = Smc2ConferenceContextCache.getInstance().get(conferenceContext.getContextKey());
                                if (s == null) {
                                    return;
                                }
                                if (pollingAttendeeSmc2List.size() > 0) {
                                    this.lastAutoPollingAttendee = pollingAttendeeSmc2List.get(i);
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
                setMultiPicPoll(multiPicPollRequest);
            }
            if (Objects.equals(PollOperateTypeDto.START.name(), multiPicPollRequest.getPollStatus()) || Objects.equals(PollOperateTypeDto.SET_AND_START.name(), multiPicPollRequest.getPollStatus())) {
                pollingAttendeeSmc2List.clear();
                parseFromSmc();
                setMultiPicPoll(multiPicPollRequest);
                ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
                State state = State.THREAD_START;
                conferenceServiceEx.setContinuousPresencePollingStateEx(smc2conferenceId, state);
                multiPicPollRequest.setPollStatus(PollOperateTypeDto.START.name());
                conferenceContext.setMultiPicPollRequest(multiPicPollRequest);
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_STARTED, true);
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询开始");
                conferenceContext.setStartRound(true);
                conferenceContext.setMultiPicPollStatus(PollOperateTypeDto.START.name());

                //广播多画面
                Integer integer = conferenceServiceEx.setBroadcastContinuousPresenceEx(conferenceContext.getSmc2conferenceId(), 0);
                ConferenceState conferenceState = conferenceContext.getDetailConference().getConferenceState();
                conferenceState.setMultiPicPollStatus("START");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("multiPicPollStatus", com.paradisecloud.com.fcm.smc.modle.PollOperateTypeDto.START.name());
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);

            }

            if (Objects.equals(PollOperateTypeDto.STOP.name(), multiPicPollRequest.getPollStatus())) {
                multiPicPollRequest.setPollStatus(PollOperateTypeDto.STOP.name());
                conferenceContext.setMultiPicPollStatus(PollOperateTypeDto.STOP.name());
                ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
                State state = State.THREAD_SUPEND;
                conferenceServiceEx.setContinuousPresencePollingStateEx(conferenceContext.getSmc2conferenceId(), state);
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, true);
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询已进入暂停状态");
                conferenceContext.setStartRound(false);
                conferenceContext.setMultiPicPollStatus(PollOperateTypeDto.STOP.name());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("multiPicPollStatus", com.paradisecloud.com.fcm.smc.modle.PollOperateTypeDto.STOP.name());
                Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);

            }

            if (Objects.equals(PollOperateTypeDto.CANCEL.name(), multiPicPollRequest.getPollStatus())) {
                if (Objects.equals(conferenceContext.getMultiPicPollStatus(), PollOperateTypeDto.CANCEL.name())) {
                    return;
                }
                multiPicPollRequest.setPollStatus(PollOperateTypeDto.CANCEL.name());
                conferenceContext.setStartRound(false);
                conferenceContext.setMultiPicPollStatus(PollOperateTypeDto.CANCEL.name());
                ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
                State state = State.ThREAD_STOP;
                conferenceServiceEx.setContinuousPresencePollingStateEx(conferenceContext.getSmc2conferenceId(), state);
                logger.info("----结束轮询----" + conferenceContext.getName());
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_ENDED, "轮询结束");

                StringBuilder messageTip1 = new StringBuilder();
                messageTip1.append("轮询已结束");
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip1);
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_ENDED, false);
                pollingAttendeeSmc2List.clear();
                parseFromSmc();
                for (AttendeeSmc2 attendeeSmc3 : pollingAttendeeSmc2List) {
                    AttendeeImportance.COMMON.processAttendeeWebsocketMessage(attendeeSmc3);
                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeSmc3.getUpdateMap());

                }


                JSONObject jsonObject = new JSONObject();
                jsonObject.put("multiPicPollStatus", com.paradisecloud.com.fcm.smc.modle.PollOperateTypeDto.CANCEL.name());
                Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);

            }
        }
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
        AttendeeSmc2 attendeeSmc2 = smc2ConferenceContext.getAttendeeById(participantIdsDTO.getParticipantId());
        if (attendeeSmc2 == null) {
            throw new CustomException("离线终端不能参与");
        }
        if (!attendeeSmc2.isMeetingJoined()) {
            throw new CustomException("离线终端不能参与");
        }
        SmcParitipantsStateRep.ContentDTO m_participant = attendeeSmc2.getSmcParticipant();
        if (m_participant == null) {
            throw new CustomException("离线终端不能参与");
        }
        Boolean online = m_participant.getState().getOnline();
        if (!online) {
            throw new CustomException("离线终端不能参与");
        }
        sub.add(m_participant.getGeneralParam().getUri());
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
                List<AttendeeSmc2> as = conferenceContext.getCascadeAttendeesMap().get(conferenceContext.getMasterAttendee().getDeptId());
                if (as != null) {
                    for (AttendeeSmc2 attendee : new ArrayList<>(as)) {
                        if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                            targetAttendees.add(attendee);
                        }
                    }
                }
            }
        }

        for (AttendeeSmc2 attendee : new ArrayList<>(conferenceContext.getAttendees())) {
            if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                targetAttendees.add(attendee);
            }
        }

        for (AttendeeSmc2 attendee : conferenceContext.getMasterAttendees()) {
            if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                targetAttendees.add(attendee);
            }
            List<AttendeeSmc2> as = conferenceContext.getCascadeAttendeesMap().get(attendee.getDeptId());
            if (as != null) {
                for (AttendeeSmc2 a : new ArrayList<>(as)) {
                    if (!checkedAttendeeIdSet.contains(a.getId())) {
                        targetAttendees.add(a);
                    }
                }
            }
        }


        Set<String> idSet = new HashSet<>();

        if (!CollectionUtils.isEmpty(targetAttendees)) {
            List<AttendeeSmc2> arrayList = new ArrayList<>();
            for (AttendeeSmc2 attendee : targetAttendees) {
                if (idSet.add(attendee.getId())) {
                    arrayList.add(attendee);
                }
            }
            targetAttendees = arrayList;
        }
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

    public List<AttendeeSmc2> getPollingAttendeeSmc2List() {
        return pollingAttendeeSmc2List;
    }

    private void reInterrupt(long timemillis) {
        try {
            long sleepSeconds = timemillis / 1000;
            long remainMillis = timemillis % 1000;
            if (timemillis > 1000) {
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
            } else {
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


    public boolean isOk() {
        Smc2ConferenceContext s = Smc2ConferenceContextCache.getInstance().get(conferenceContext.getContextKey());
        if (s == null) {
            return false;
        }
        return !this.cancel && !conferenceContext.isEnd() && conferenceContext.getAttendeeOperation() == PollingAttendeeOperation.this;
    }


    public void setMultiPicPoll(MultiPicPollRequest multiPicPollRequest) {
        Integer interval = multiPicPollRequest.getInterval();
        String confId = conferenceContext.getSmc2conferenceId();
        String target = "(%CP)";
        Integer picNum = multiPicPollRequest.getPicNum();
        Integer mode = multiPicPollRequest.getMode();
        int presenceMode = ContinuousPresenceModeEnum.getModelValue(picNum, mode);
        if (presenceMode == -1) {
            throw new CustomException("多画面设置失败:不支持该" + picNum + "画面");
        }
        List<List<String>> subPics = new ArrayList<>();
        interval = subPicsSetting(multiPicPollRequest, conferenceContext, picNum, subPics);
        ConferenceServiceEx conferenceServiceEx = conferenceContext.getSmc2Bridge().getConferenceServiceEx();
        int result = conferenceServiceEx.setContinuousPresencePollingEx(confId, target, presenceMode, subPics, interval, -1);
        if (result != 0) {
            throw new CustomException("多画面轮询设置失败");
        } else {
            conferenceContext.setMultiPicPollRequest(multiPicPollRequest);
            this.multiPicPollRequest = multiPicPollRequest;
        }
    }

    public void setMultiPic(MultiPicPollRequest multiPicPollRequest, AttendeeSmc2 attendeeSmc2) {
        String confId = conferenceContext.getSmc2conferenceId();
        String target = "(%CP)";
        Integer picNum = multiPicPollRequest.getPicNum();
        Integer mode = multiPicPollRequest.getMode();
        int presenceMode = ContinuousPresenceModeEnum.getModelValue(picNum, mode);
        if (presenceMode == -1) {
            throw new CustomException("多画面设置失败:不支持该" + picNum + "画面");
        }
        List<List<String>> subPics = new ArrayList<>();
        Integer interval = 3600;
        List<String> sub = new ArrayList<>();
        String participantUuid = attendeeSmc2.getParticipantUuid();
        AttendeeSmc2 attendeeBySmc2Id = conferenceContext.getAttendeeBySmc2Id(participantUuid);
        sub.add(attendeeBySmc2Id.getSmcParticipant().getGeneralParam().getUri());
        subPics.add(sub);
        ConferenceServiceEx conferenceServiceEx = conferenceContext.getSmc2Bridge().getConferenceServiceEx();
        Integer resultCode = conferenceServiceEx.setContinuousPresenceEx(confId, target, presenceMode, sub);
        if (resultCode != 0) {
            throw new CustomException("多画面设置失败");
        } else {
            conferenceContext.setMultiPicPollRequest(multiPicPollRequest);
            this.multiPicPollRequest = multiPicPollRequest;
        }
    }

    public AttendeeSmc2 getLastAutoPollingAttendee() {
        return lastAutoPollingAttendee;
    }

    public void setLastAutoPollingAttendee(AttendeeSmc2 lastAutoPollingAttendee) {
        this.lastAutoPollingAttendee = lastAutoPollingAttendee;
    }
}
