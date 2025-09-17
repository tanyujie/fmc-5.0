/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CallTheRollOperation.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.operation
 * @author lilinhai
 * @since 2021-02-20 16:42
 * @version  V1.0
 */
package com.paradisecloud.fcm.smc2.model.attendee.operation;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.enumer.AttendeeCallTheRollStatus;
import com.paradisecloud.fcm.common.enumer.AttendeeMixingStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.model.AttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.enumer.AttendeeImportance;
import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.pojo.local.WSCtrlSiteCommParamEx;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <pre>点名与会者操作</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-20 16:42
 */
public class CallTheRollAttendeeOperation extends AttendeeOperation {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     *
     * @since 2021-02-22 12:48
     */
    private static final long serialVersionUID = 1L;
    private AttendeeSmc2 callTheRollAttendee;

    public CallTheRollAttendeeOperation(Smc2ConferenceContext conferenceContext) {
        super(conferenceContext);
    }

    public CallTheRollAttendeeOperation(Smc2ConferenceContext conferenceContext, AttendeeSmc2 attendees) {
        super(conferenceContext);
        this.callTheRollAttendee = attendees;
    }

    /**
     * 调用此接口前，需确保会议中存在主席会场。
     * 如果指定的会场已经处于发言状态，该操作仍返回成功。
     * 主席会场可以通过点名发言来指定自己或者其他会场发言，其他非被点名发言的会场自动进入麦克风关闭状态，而点名发言的会场麦克风进入开启状态。
     * 通过取消广播操作可以取消点名发言，并且需要手动恢复在点名发言期间被自动关闭的麦克风。
     */
    @Override
    public void operate() {
        if (conferenceContext == null || conferenceContext.isEnd()) {
            return;
        }
        AttendeeSmc2 masterAttendee = conferenceContext.getMasterAttendee();
        if (masterAttendee == null) {
            throw new CustomException("没有主会场,无法选看");
        }

        if (this.callTheRollAttendee.getCallTheRollStatus() == AttendeeCallTheRollStatus.YES.getValue()) {
            return;
        }
        String confId = conferenceContext.getSmc2conferenceId();
        //获取会议相关服务实例
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        String videoSourceUri = masterAttendee.getSmcParticipant().getGeneralParam().getUri();
        String siteUri = callTheRollAttendee.getSmcParticipant().getGeneralParam().getUri();

        conferenceServiceEx.setSitesMuteEx(confId, Collections.singletonList(siteUri), 0);
        callTheRollAttendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());


        //设置观看对象为主席
        //锁定视频源
        List<WSCtrlSiteCommParamEx> wsCtrlSiteCommParams = new ArrayList<>();
        WSCtrlSiteCommParamEx item1 = new WSCtrlSiteCommParamEx();
        //锁定
        item1.setOperaTypeParam(1);
        item1.setSiteUri(siteUri);
        wsCtrlSiteCommParams.add(item1);
        conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParams);

        Integer erroCode = conferenceServiceEx.setVideoSourceEx(confId, siteUri, videoSourceUri, 0);
        if (erroCode != 0) {
            logger.error(callTheRollAttendee.getName() + "视频源设置错误：" + erroCode);
        }
        broadcast(callTheRollAttendee.getSmcParticipant().getGeneralParam().getUri(), confId, 0);
        //指定会场发言
        Integer resultCode = conferenceServiceEx.setFloorEx(confId, callTheRollAttendee.getSmcParticipant().getGeneralParam().getUri());
        if (resultCode != 0) {
            logger.error(callTheRollAttendee.getSmcParticipant().getGeneralParam().getName() + "点名失败：" + resultCode);
            throw new CustomException("点名失败" + resultCode);
        }
        //解锁
        item1.setOperaTypeParam(0);
        item1.setSiteUri(siteUri);
        wsCtrlSiteCommParams.add(item1);
        conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParams);

        if (callTheRollAttendee != null) {
            AttendeeImportance.POINT.processAttendeeWebsocketMessage(callTheRollAttendee);
            conferenceContext.setSpokesmanId(callTheRollAttendee.getId());
            callTheRollAttendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
            callTheRollAttendee.getUpdateMap().put("mixingStatus",AttendeeMixingStatus.YES.getValue());
            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, callTheRollAttendee.getUpdateMap());
        }

    }

    @Override
    public void cancel() {
        if (conferenceContext == null || conferenceContext.isEnd()) {
            return;
        }
        if (callTheRollAttendee != null) {

            String confId = conferenceContext.getSmc2conferenceId();
            //关闭麦克风
            ConferenceServiceEx conferenceServiceEx = conferenceContext.getSmc2Bridge().getConferenceServiceEx();
            List<String> list = new ArrayList<>();
            list.add(callTheRollAttendee.getSmcParticipant().getGeneralParam().getUri());
            if (!Objects.equals(conferenceContext.getMasterAttendee().getId(), callTheRollAttendee.getId())) {
                conferenceServiceEx.setSitesMuteEx(confId, list, 1);
            }
            AttendeeImportance.COMMON.processAttendeeWebsocketMessage(callTheRollAttendee);
            conferenceContext.setSpokesmanId("");
            callTheRollAttendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, callTheRollAttendee.getUpdateMap());

            //取消广播
            broadcast(callTheRollAttendee.getSmcParticipant().getGeneralParam().getUri(), confId, 1);
            Threads.sleep(200);
        }

    }


}
