/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : RosterProcessorUpdateMessage.java
 * Package     : com.paradisecloud.fcm.fme.websocket.model.roster.message
 * @author sinhy 
 * @since 2021-09-07 23:48
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.websocket.model.callinfo.message;


import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.ConferenceEndType;
import com.paradisecloud.fcm.common.enumer.ConferenceNumberCreateType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceNumberMapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiConferenceNumber;
import com.paradisecloud.fcm.fme.model.busi.attendee.AttendeeCountingStatistics;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.LiveBroadcastAttendee;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.sinhy.spring.BeanFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

public class CallInfoProcessorRemoveMessage extends CallInfoProcessorMessage
{


    public static final String ALL_LEFT = "allLeft";
    public static final String PARTICIPANT_INITIATED = "participantInitiated";

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-07 23:46 
     * @param fmeBridge
     * @param updateItem 
     */
    public CallInfoProcessorRemoveMessage(FmeBridge fmeBridge, JSONObject updateItem)
    {
        super(fmeBridge, updateItem);
    }

    @Override
    protected void process0()
    {
        String cn = fmeBridge.getDataCache().getConferenceNumberByCallId(itemId);
        if (cn != null)
        {
            endConference(cn);

        }
    }

    private void endCospace() {
        // 会议call的remove消息只是表明当前会议桥上的该call结束了，但对应的coSpace不一定结束
        // 因此coSpace在删除的时候需要到fme端做校验，判断是否真实存在，若是不存在，再进行删除操作
        String cn;
        Call call = fmeBridge.getDataCache().deleteCallByUuid(itemId);
        if (call != null)
        {
            cn = fmeBridge.getDataCache().getConferenceNumberByCoSpaceId(call.getCoSpace());
            endConference(cn);

        }
    }

    private void endConference(String cn) {

        ConferenceContext conferenceContextExist = null;
        Collection<ConferenceContext> conferenceContextList = ConferenceContextCache.getInstance().getConferenceContextListByConferenceNum(cn);
        if (conferenceContextList != null && conferenceContextList.size() > 0) {
            if (fmeBridge != null) {
                CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(cn);
                if (coSpace != null) {
                    for (ConferenceContext conferenceContextTemp : conferenceContextList) {
                        if (coSpace.getId().equals(conferenceContextTemp.getCoSpaceId())) {
                            conferenceContextExist = conferenceContextTemp;
                            break;
                        }
                    }
                }
            }
        }

        ConferenceContext conferenceContext = conferenceContextExist;
        if(conferenceContext!=null){
            String reason = updateItem.getString("reason");
            if (PARTICIPANT_INITIATED.equals(reason)) {
                boolean needEnd = true;
//                BusiConferenceNumberMapper busiConferenceNumberMapper = BeanFactory.getBean(BusiConferenceNumberMapper.class);
//                BusiConferenceNumber busiConferenceNumber= busiConferenceNumberMapper.selectBusiConferenceNumberById(Long.valueOf(conferenceContext.getConferenceNumber()));
//                if (busiConferenceNumber != null && busiConferenceNumber.getCreateType().intValue() == ConferenceNumberCreateType.MANUAL.getValue()) {
//                    needEnd = false;
//                }
                if (needEnd) {
                    BeanFactory.getBean(IBusiConferenceService.class).endConference(conferenceContext.getId(), ConferenceEndType.COMMON.getValue(), EndReasonsType.PARTICIPANT_HANGS_UP);
                    endCospace();
                    return;
                } else {
                    removeAttendee(conferenceContext);
                }

            }
            BusiConferenceAppointment conferenceAppointment = conferenceContext.getConferenceAppointment();
            if(conferenceAppointment!=null){
                String endTime = conferenceAppointment.getEndTime();
                if(StringUtils.hasText(endTime)){
                    Date date = DateUtil.convertDateByString(endTime,null);
                    if(System.currentTimeMillis()>=date.getTime()){
                        BeanFactory.getBean(IBusiConferenceService.class).endConference(conferenceContext.getId(), ConferenceEndType.COMMON.getValue(), EndReasonsType.ABNORMAL_END);
                        endCospace();
                    }else {
                        removeAttendee(conferenceContext);
                    }
                }

            }else {
                Date endTime = conferenceContext.getEndTime();
                if(endTime==null){
                    removeAttendee(conferenceContext);
                }else {
                    if(System.currentTimeMillis()>=endTime.getTime()){
                        BeanFactory.getBean(IBusiConferenceService.class).endConference(conferenceContext.getId(), ConferenceEndType.COMMON.getValue(), EndReasonsType.ABNORMAL_END);
                        endCospace();
                    }else {
                        removeAttendee(conferenceContext);
                    }
                }

            }

        }else {
            // 结束会议，清理内存
            BeanFactory.getBean(IBusiConferenceService.class).endConference(cn, EndReasonsType.ABNORMAL_END);
            endCospace();
        }
    }

    private void removeAttendee(ConferenceContext conferenceContext) {
        String reason = updateItem.getString("reason");
        if(Objects.equals(reason, ALL_LEFT) || Objects.equals(reason, PARTICIPANT_INITIATED)){
            List<Attendee> attendees = conferenceContext.getAttendees();
            if(!CollectionUtils.isEmpty(attendees)){
                for (Attendee attendee : attendees) {
                    if (attendee instanceof LiveBroadcastAttendee) {
                        LiveBroadcastAttendee liveBroadcastAttendee = (LiveBroadcastAttendee) attendee;
                        if (StringUtils.hasText(liveBroadcastAttendee.getRemotePartyOrigin())) {
                            if (conferenceContext.isStreaming())
                            {
                                conferenceContext.setStreaming(false);

                                // 向所有客户端通知会议的录制状态
                                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.STREAMING, false);
                                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已关闭直播");
                            }
                        }
                    }
                    conferenceContext.removeAttendeeById(attendee.getId());
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】被移除");

                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("id", attendee.getId());
                    updateMap.put("deptId", attendee.getDeptId());
                    updateMap.put("mcuAttendee", attendee.isMcuAttendee());
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                    BeanFactory.getBean(IMqttService.class).sendLeftConferenceToPushTargetTerminal(conferenceContext, attendee);
                }
            }
        }
    }
}
