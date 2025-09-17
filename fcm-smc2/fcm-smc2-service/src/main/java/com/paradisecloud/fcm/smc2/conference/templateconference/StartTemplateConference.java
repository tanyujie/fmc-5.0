package com.paradisecloud.fcm.smc2.conference.templateconference;

import com.paradisecloud.common.utils.uuid.UUID;
import com.paradisecloud.fcm.common.enumer.AttendType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryCallMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc2TemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberService;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContextCache;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.McuAttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.TerminalAttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.enumer.Smc2ErrorCode;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiConferenceNumberSmc2Service;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiMcuSmc2HistoryConferenceService;
import com.paradisecloud.fcm.smc2.utils.Smc2ConferenceContextUtils;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.pojo.local.*;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;
import com.suntek.smc.esdk.service.client.SubscribeServiceEx;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.Duration;
import java.util.*;

/**
 * @author nj
 * @date 2023/4/20 11:53
 */
public class StartTemplateConference extends BuildTemplateConferenceContext {
    public static final int RATE_NUMBER = 8;
    public static final int SIP_PORT = 5060;
    Logger logger = LoggerFactory.getLogger(getClass());
    public static final int Max_INT = 1347420331;
    public synchronized Smc2ConferenceContext startTemplateConference(long templateConferenceId) {

        logger.info("模板会议启动入口：" + templateConferenceId);
        BusiMcuSmc2TemplateConferenceMapper busiMcuSmc2TemplateConferenceMapper = BeanFactory.getBean(BusiMcuSmc2TemplateConferenceMapper.class);
        IBusiMcuSmc2HistoryConferenceService busiSmc2HistoryConferenceService = BeanFactory.getBean(IBusiMcuSmc2HistoryConferenceService.class);
        // 获取模板会议实体对象
        BusiMcuSmc2TemplateConference tc = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(templateConferenceId);
        if (tc == null) {
            return null;
        }
        // 获取会议上下文
        Smc2ConferenceContext conferenceContext = buildTemplateConferenceContext(templateConferenceId);
        if (conferenceContext == null) {
            return null;
        }
        // 启动前校验会议是否已开始
        if (conferenceContext.isStart()) {
            throw new SystemException(1009874, "会议已开始，请勿重复开始");
        }


        if (conferenceContext.getConferenceNumber() == null) {
            BusiConferenceNumber busiConferenceNumber = BeanFactory.getBean(IBusiConferenceNumberService.class).autoCreateConferenceNumber(conferenceContext.getDeptId(), McuType.SMC2.getCode());
            tc.setConferenceNumber(busiConferenceNumber.getId());
            busiMcuSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(tc);
            conferenceContext.setAccessCode(busiConferenceNumber.getId().toString());
            conferenceContext.setConferenceNumber(busiConferenceNumber.getId().toString());
            logger.info("自动生成会议号成功：" + busiConferenceNumber.getId());
        }

        // TPSDKResponseEx<ConferenceInfoEx> result = getConferenceInfoExTPSDKResponseEx(tc);

        //新建一个ConferenceInfoEx对象
        ConferenceInfoEx scheduleConf = new ConferenceInfoEx();
        scheduleConf.setIsDataConference(1);
        scheduleConf.setAccessCode(conferenceContext.getConferenceNumber());
        String chairmanPassword = tc.getChairmanPassword();
        if(Strings.isNotBlank(chairmanPassword)){
            scheduleConf.setChairmanPassword(chairmanPassword);
        }
        String conferencePassword = tc.getConferencePassword();
        if(Strings.isNotBlank(conferencePassword)){
            scheduleConf.setPassword(conferencePassword);
        }
        //会议名称为test
        scheduleConf.setName(tc.getName());
        //会议速率为1920k
        Integer bandwidth = tc.getBandwidth();
        if(bandwidth==null){
            bandwidth=1920;
        }else {
            if(bandwidth<= RATE_NUMBER){
                bandwidth=bandwidth*1024;
            }
        }
        scheduleConf.setRate("1920k");
        if(tc.getLastConferenceId()!=null){
            BusiHistoryConference busiHistoryConference = busiSmc2HistoryConferenceService.selectBusiHistoryConferenceById(Long.valueOf(tc.getLastConferenceId()));
            if(busiHistoryConference.getConferenceEndTime()==null){
                scheduleConf.setConfId(tc.getConfId());
            }
        }
        Integer durationTime = tc.getDurationTime();
        if(durationTime!=null){
            try {
                //会议时长为60分钟1000 * 60 * 60
                Duration duration = javax.xml.datatype.DatatypeFactory.newInstance().newDuration(1000 * 60 * durationTime);
                scheduleConf.setDuration(duration);
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }
        }

        Boolean needSave=true;

        //获取会议相关服务实例
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        //调用会议服务的scheduleConfEx方法预约会议，返回TPSDKResponseEx<ConferenceInfoEx>对象
        TPSDKResponseEx<ConferenceInfoEx> result = conferenceServiceEx.scheduleConfEx(scheduleConf);
        //调用TPSDKResponseEx<T>中的getResultCode()方法获取返回值，如果返回值为0，则表示成功，否则表示失败，具体失败原因请参考错误码列表。
        Integer resultCode = result.getResultCode();
        if (0 == resultCode) {
            //预约成功，则返回预约后的会议信息
            ConferenceInfoEx conferenceInfo = result.getResult();
            conferenceContext.setSmc2conferenceId(conferenceInfo.getConfId());
            conferenceContext.setTenantId(conferenceInfo.getUnifiedAccessCode());
            tc.setSmcTemplateId(conferenceInfo.getConfId());
            tc.setConfId(conferenceInfo.getConfId());

            String conferenceRemoteParty = conferenceContext.getTenantId() + conferenceContext.getConferenceNumber() + "@" + conferenceContext.getMcuCallIp();
            if (conferenceContext.getMcuCallPort() != null && conferenceContext.getMcuCallPort() != SIP_PORT) {
                conferenceRemoteParty += ":" + conferenceContext.getMcuCallPort();
            }
            conferenceContext.setConferenceRemoteParty(conferenceRemoteParty);
            subcribel(conferenceContext.getSmc2conferenceId());
        } else {
            if(Smc2ErrorCode.AD_HOC_CODE_EXIST.getCode()==resultCode){
                //Ad hoc会议接入号重复。
                if(tc.getLastConferenceId()!=null){
                    BusiHistoryConference busiHistoryConference = busiSmc2HistoryConferenceService.selectBusiHistoryConferenceById(Long.valueOf(tc.getLastConferenceId()));
                    if(busiHistoryConference.getConferenceEndTime()!=null){
                        throw new SystemException(2, "开始会议失败" + Smc2ErrorCode.getReasonByCode(resultCode));
                    }else {
                        conferenceContext.setSmc2conferenceId(tc.getConfId());
                        needSave=false;
                    }
                }

            }else if(Smc2ErrorCode.AD_HOC_CODE_INCLUDE.getCode()==resultCode
                    ||Smc2ErrorCode.AD_HOC_CODE_RECORDE_INCLUDE.getCode()==resultCode
                    ||Smc2ErrorCode.AD_HOC_CODE_CST_INCLUDE.getCode()==resultCode){
                tc.setConferenceNumber(null);
                busiMcuSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(tc);
                throw new SystemException(1, "开始会议失败" + Smc2ErrorCode.getReasonByCode(resultCode));
            }
            else {
                throw new SystemException(1, "开始会议失败" + Smc2ErrorCode.getReasonByCode(resultCode));
            }
        }

        if(needSave){
            saveHistory(busiSmc2HistoryConferenceService, tc, conferenceContext);
        }
        subcribel(conferenceContext.getSmc2conferenceId());

        busiMcuSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(tc);
        String message = "会议【" + conferenceContext.getName() + "】启动成功！";
        Map<String, Object> obj = new HashMap<>(3);
        obj.put("startTime", conferenceContext.getStartTime());
        obj.put("message", message);
        obj.put("conferenceNumber", conferenceContext.getConferenceNumber());
        obj.put("streamingUrl", conferenceContext.getStreamingUrl());
        obj.put("streamingUrlList", conferenceContext.getStreamUrlList());
        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_STARTED, obj);
        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, message);
        conferenceContext.setId(EncryptIdUtil.generateConferenceId(conferenceContext.getContextKey()));
        Smc2ConferenceContextCache.getInstance().add(conferenceContext);
        conferenceContext.setAttendeeOperation(conferenceContext.getDefaultViewOperation());
        logger.info("已设置默认布局：" + conferenceContext.getConferenceNumber());
        conferenceContext.getDefaultViewOperation().operate();
        logger.info("默认布局已开始运行：" + conferenceContext.getConferenceNumber());
        conferenceContext.setStart(true);
        conferenceContext.setStartTime(new Date());

        if(conferenceContext.getConferenceNumber()!=null){
            String conferenceRemoteParty = conferenceContext.getTenantId()==null?"":conferenceContext.getTenantId() + conferenceContext.getConferenceNumber() + "@" + conferenceContext.getMcuCallIp();
            if (conferenceContext.getMcuCallPort() != null && conferenceContext.getMcuCallPort() != 5060) {
                conferenceRemoteParty += ":" + conferenceContext.getMcuCallPort();
            }
            conferenceContext.setConferenceRemoteParty(conferenceRemoteParty);
        }

        // 添加发起方会议上下文
        List<BaseAttendee> mqttJoinTerminals = new ArrayList<>();
        Smc2ConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (a instanceof TerminalAttendeeSmc2) {
                TerminalAttendeeSmc2 ta = (TerminalAttendeeSmc2) a;
                BusiTerminal bt = TerminalCache.getInstance().get(ta.getTerminalId());

                // 被叫由会控负责发起呼叫
                if (AttendType.convert(ta.getAttendType()) == AttendType.OUT_BOUND) {
                    if (!ta.isMeetingJoined()) {
                        doCall(conferenceContext, ta);

                    }
                }

                if (AttendType.convert(ta.getAttendType()) == AttendType.AUTO_JOIN) {
                    if (!ObjectUtils.isEmpty(bt.getSn())) {
                        if (!ta.isMeetingJoined()) {
                            // 自动主叫终端由mqtt下发入会邀请后主叫入会
                            mqttJoinTerminals.add(ta);
                        }
                    }
                }
            } else {
                if(!(a instanceof McuAttendeeSmc2)){
                    doCall(conferenceContext, a);
                }
            }
        });
        // 处理mqtt业务
        doMqttService(mqttJoinTerminals, conferenceContext);

        return conferenceContext;
    }

    private void saveHistory(IBusiMcuSmc2HistoryConferenceService busiSmc2HistoryConferenceService, BusiMcuSmc2TemplateConference tc, Smc2ConferenceContext conferenceContext) {
        // 保存历史记录
        String callId = UUID.randomUUID().toString();
        // 保存历史记录
        BusiHistoryConference busiHistoryConference = busiSmc2HistoryConferenceService.saveHistory(conferenceContext);
        conferenceContext.setHistoryConference(busiHistoryConference);
        tc.setLastConferenceId(String.valueOf(busiHistoryConference.getId()));
        //历史call保存
        BusiHistoryCall busiHistoryCall = new BusiHistoryCall();
        busiHistoryCall.setCallId(callId);
        busiHistoryCall.setCoSpace(conferenceContext.getCoSpaceId());
        busiHistoryCall.setDeptId(conferenceContext.getDeptId());
        busiHistoryCall.setCreateTime(new Date());
        busiHistoryCall.setHistoryConferenceId(busiHistoryConference.getId());
        BeanFactory.getBean(BusiHistoryCallMapper.class).insertBusiHistoryCall(busiHistoryCall);
    }

    private TPSDKResponseEx<ConferenceInfoEx> getConferenceInfoExTPSDKResponseEx(BusiMcuSmc2TemplateConference tc) {
        ConferenceInfoEx scheduleConf = new ConferenceInfoEx();
        //设置会议开始时间
        scheduleConf.setBeginTime(new Date());
        //会议名称为test
        scheduleConf.setName(tc.getName());
        //会议速率为1920k
        scheduleConf.setRate("1920k");
        try {
            //会议时长为60分钟
            Duration duration = javax.xml.datatype.DatatypeFactory.newInstance().newDuration(tc.getDurationTime() * 60 * 1000);
            scheduleConf.setDuration(duration);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        TPSDKResponseEx<ConferenceInfoEx> result = conferenceServiceEx.scheduleConfEx(scheduleConf);
        return result;
    }

    private void subcribel(String confId) {
        if (Strings.isBlank(confId)) {
            return;
        }
        //订阅或取消订阅信息的列表
        List<SubscribeInfoEx> subscribeInfoExs = new ArrayList<SubscribeInfoEx>();
        OngoingConfSubscribeEx subscribeInfoEx = new OngoingConfSubscribeEx();
        //订阅
        subscribeInfoEx.setIsSubscribe(1);
        //需要订阅或取消订阅的会议ID
        subscribeInfoEx.getConfIds().add(confId);
        subscribeInfoExs.add(subscribeInfoEx);
        //获取订阅推送服务实例
        SubscribeServiceEx subscribeServiceEx = ServiceFactoryEx.getService(SubscribeServiceEx.class);
        //调用订阅服务的subscribeEx方法修改推送消息的过滤条件，返回Integer对象
        Integer resultsubscribe = subscribeServiceEx.subscribeEx(subscribeInfoExs);
        logger.info("会议id:" + confId + "订阅会议结果" + resultsubscribe);

    }

    private boolean doCall(Smc2ConferenceContext conferenceContext, AttendeeSmc2 a) {
        try {
            SiteInfoEx siteInfo = new SiteInfoEx();
            siteInfo.setUri(a.getRemoteParty());
            siteInfo.setName(a.getName());
            siteInfo.setType(7);
            StringBuilder messageTips = new StringBuilder();
            messageTips.append("【").append(a.getName()).append("】正在呼叫：");
            Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTips);
            ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
            TPSDKResponseEx<List<SiteAccessInfoEx>> result = conferenceServiceEx.addSiteToConfEx(conferenceContext.getSmc2conferenceId(), siteInfo, null);
            if(result.getResultCode()!=0){
                StringBuilder messageTip = new StringBuilder();
                if(result.getResultCode()== Max_INT){
                    messageTip.append("【").append(a.getName()).append("】呼叫失败：").append("超出最大与会方数，添加会场失败");
                }else {
                    messageTip.append("【").append(a.getName()).append("】呼叫失败：").append(result.getResultCode());
                }
                Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("呼叫与会者发生异常-doCall：" + a, e);
            StringBuilder messageTip = new StringBuilder();
            messageTip.append("【").append(a.getName()).append("】呼叫失败：").append(e.getMessage());
            Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip);
        }
        return false;
    }

    private void doMqttService(List<BaseAttendee> mqttJoinTerminals, Smc2ConferenceContext conferenceContext) {
        new Thread(() -> {
            IMqttService mqttService = BeanFactory.getBean(IMqttService.class);
            mqttService.pushConferenceInfo(conferenceContext.getId(), conferenceContext.getConferencePassword(), mqttJoinTerminals, conferenceContext.getLiveTerminals());
            logger.info("pushConferenceInfo执行成功，发送mqtt终端数：" + mqttJoinTerminals.size() + ", 直播终端数：" + conferenceContext.getLiveTerminals().size());
        }).start();

    }

}
