package com.paradisecloud.fcm.web.task;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.ConferenceTemplateCreateType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiPacketMapper;
import com.paradisecloud.fcm.dao.model.BusiCallLegProfile;
import com.paradisecloud.fcm.dao.model.BusiPacket;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiCallLegProfileService;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.ITemplateConferenceStartService;
import com.paradisecloud.fcm.fme.conference.task.PacketConferenceTask;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.cms.CallLegProfile;
import com.paradisecloud.fcm.fme.model.response.calllegprofile.CallLegProfileInfoResponse;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.zte.task.DelayTask;
import com.sinhy.spring.BeanFactory;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class McuZteMutePacketStartConferenceTask extends Task {
    private static final Logger LOGGER = LoggerFactory.getLogger(McuZteMutePacketStartConferenceTask.class);
    private String conferenceId;

    public McuZteMutePacketStartConferenceTask(String id, long delayInMilliseconds, String  conferenceId) {
        super("McuZte_Packet_S" + id, delayInMilliseconds);
        this.conferenceId = conferenceId;
    }

    @Override
    public void run() {
        BusiPacketMapper busiPacketMapper = BeanFactory.getBean(BusiPacketMapper.class);
        List<BusiPacket> busiPackets = busiPacketMapper.selectBusiPacketList(new BusiPacket());
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(busiPackets)) {
            LOGGER.info("抓包服务器不存在");
            return ;
        } else {
            BusiPacket busiPacket = busiPackets.get(0);
            String ip = busiPacket.getIp();
            try {
                InetAddress address = InetAddress.getByName(ip);
                boolean reachable = address.isReachable(1000);
                if (!reachable) {
                    LOGGER.info("抓包服务器不存在");
                    return ;
                }
            } catch (IOException e) {
                LOGGER.info("抓包服务器不存在");
                return ;
            }

        }


        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);

        FmeBridge fmeBridge;
        String packetConferenceId = null;

        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (baseConferenceContext != null) {
            Long deptId = baseConferenceContext.getDeptId();
            fmeBridge = BridgeUtils.getAvailableFmeBridge(deptId);
            if (fmeBridge == null) {
                LOGGER.info("无法开启会监,无可用FME");
                return;
            }
            packetConferenceId = baseConferenceContext.getPacketConferenceId();
            if (packetConferenceId != null) {
                return;
            }

            ITemplateConferenceStartService templateConferenceStartService = BeanFactory.getBean(ITemplateConferenceStartService.class);
            //创建模板会议
            Long templateId = notMuteTemp(fmeBridge, deptId);
            templateConferenceStartService.startTemplateConference(templateId);
            packetConferenceId = EncryptIdUtil.generateConferenceId(templateId, McuType.FME.getCode());

            baseConferenceContext.setPacketConferenceId(packetConferenceId);

            String contextKey_p = EncryptIdUtil.parasToContextKey(packetConferenceId);

            BaseConferenceContext baseConferenceContext_p = AllConferenceContextCache.getInstance().get(contextKey_p);
            ConferenceContext conferenceContext=(ConferenceContext)baseConferenceContext_p;
            conferenceContext.setMonitorId(packetConferenceId);
            conferenceContext.setMonitorNumber(baseConferenceContext_p.getConferenceNumber());
            //启动会议的抓包服务
            PacketConferenceTask packetConferenceTask = new PacketConferenceTask(packetConferenceId, 100, conferenceId);
            TaskService taskService = BeanFactory.getBean(TaskService.class);
            taskService.addTask(packetConferenceTask);
            HashMap<String, Object> map = new HashMap<>();

            map.put("monitorId", packetConferenceId);
            map.put("monitorNumber", baseConferenceContext_p.getConferenceNumber());

            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_MONITOR_START,map);
            return ;
        } else {
            LOGGER.info("会议不存在");
            return ;
        }
    }

    private Long notMuteTemp(FmeBridge fmeBridge, Long deptId) {
        //入会方案检查
        String calllegprofileId = "";
        BusiCallLegProfile con = new BusiCallLegProfile();
        con.setDeptId(deptId);
        List<BusiCallLegProfile> clps = BeanFactory.getBean(IBusiCallLegProfileService.class).selectBusiCallLegProfileList(con);
        if (!ObjectUtils.isEmpty(clps)) {
            for (BusiCallLegProfile busiCallLegProfile : clps) {
                CallLegProfile clp = fmeBridge.getDataCache().getCallLegProfile(busiCallLegProfile.getCallLegProfileUuid());
                if (clp != null) {
                    Boolean rxAudioMute = clp.getRxAudioMute();
                    if (rxAudioMute != null && rxAudioMute.equals(false)) {
                        calllegprofileId = clp.getId();
                        break;
                    }
                } else {
                    CallLegProfileInfoResponse callLegProfile = fmeBridge.getCallLegProfileInvoker().getCallLegProfile(busiCallLegProfile.getCallLegProfileUuid());
                    if (callLegProfile != null) {
                        if (callLegProfile.getCallLegProfile() != null) {

                            if (!callLegProfile.getCallLegProfile().getRxAudioMute()) {
                                calllegprofileId = callLegProfile.getCallLegProfile().getId();
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (Strings.isBlank(calllegprofileId)) {
            calllegprofileId = BeanFactory.getBean(IBusiCallLegProfileService.class).createDefaultCalllegProfileIsMute(fmeBridge, deptId, false);
        }
        //创建模板：

        BusiTemplateConference templateConference = new BusiTemplateConference();
        templateConference.setName("会议预览");
        templateConference.setCallLegProfileId(calllegprofileId);
        templateConference.setDeptId(deptId);
        templateConference.setIsAutoCall(2);
        templateConference.setBandwidth(2);
        templateConference.setBusinessFieldType(100);
        templateConference.setDurationEnabled(0);
        templateConference.setIsAutoCreateConferenceNumber(1);
        templateConference.setIsAutoCreateStreamUrl(2);
        templateConference.setViewType(1);
        templateConference.setCreateUserId(1L);
        templateConference.setCreateUserName("superAdmin");
        templateConference.setRecordingEnabled(2);
        templateConference.setStreamingEnabled(2);
        templateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());

        IBusiTemplateConferenceService busiTemplateConferenceService = BeanFactory.getBean(IBusiTemplateConferenceService.class);

        int c = busiTemplateConferenceService.insertBusiTemplateConferenceOps(templateConference, null, new ArrayList<>(), new ArrayList<>());
        if (c > 0) {
            Long id = templateConference.getId();

            // 分屏
            try {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("defaultViewLayout", "allEqual");
                jsonObj.put("defaultViewIsBroadcast", 1);
                jsonObj.put("defaultViewIsDisplaySelf", -1);
                jsonObj.put("defaultViewIsFill", -1);
                jsonObj.put("pollingInterval", 10);
                jsonObj.put("defaultViewCellScreens", new ArrayList<>());
                jsonObj.put("defaultViewDepts", new ArrayList<>());
                jsonObj.put("defaultViewPaticipants", new ArrayList<>());
                busiTemplateConferenceService.updateDefaultViewConfigInfo(jsonObj, templateConference.getId());
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            return id;

        }
        return null;
    }
}
