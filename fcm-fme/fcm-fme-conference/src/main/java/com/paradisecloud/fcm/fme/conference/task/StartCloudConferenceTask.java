package com.paradisecloud.fcm.fme.conference.task;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiMcuHwcloudConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuTencentConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.ViewTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.fme.attendee.model.busiprocessor.CallAttendeeProcessor;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.AttendeeCountingStatistics;
import com.paradisecloud.fcm.fme.model.busi.attendee.InvitedAttendee;
import com.paradisecloud.fcm.service.conference.cache.BaseWebSocketMessagePusher;
import com.paradisecloud.fcm.service.model.CloudConference;
import com.paradisecloud.fcm.service.util.HuaweiCloudUtil;
import com.paradisecloud.fcm.service.util.TencentCloudUtil;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class StartCloudConferenceTask extends Task {

    private Logger logger = LoggerFactory.getLogger(StartCloudConferenceTask.class);

    private BusiTemplateConference busiTemplateConference;
    private boolean needRun = false;

    public StartCloudConferenceTask(String id, long delayInMilliseconds, BusiTemplateConference busiTemplateConference) {
        super("start_cloud_c_" + id, delayInMilliseconds);
        this.busiTemplateConference = busiTemplateConference;
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        if (busiTemplateConference != null) {
            String contextKey = EncryptIdUtil.generateContextKey(busiTemplateConference.getId(), McuType.FME);
            for (int i = 0; i < 3; i++) {
                ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
                if (conferenceContext != null && !conferenceContext.isEnd()) {
                    startCloudConference(busiTemplateConference, conferenceContext);
                }
                if (!needRun) {
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
            }
        }
    }

    private void startCloudConference(BusiTemplateConference tc, ConferenceContext conferenceContext) {
        BusiMcuTencentConferenceAppointmentMapper busiMcuTencentConferenceAppointmentMapper = BeanFactory.getBean(BusiMcuTencentConferenceAppointmentMapper.class);
        BusiMcuHwcloudConferenceAppointmentMapper busiMcuHwcloudConferenceAppointmentMapper = BeanFactory.getBean(BusiMcuHwcloudConferenceAppointmentMapper.class);
        ViewTemplateConferenceMapper viewTemplateConferenceMapper = BeanFactory.getBean(ViewTemplateConferenceMapper.class);
        ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
        viewTemplateConferenceCon.setUpCascadeId(tc.getId());
        viewTemplateConferenceCon.setUpCascadeMcuType(McuType.FME.getCode());
        List<ViewTemplateConference> downCascadeList = viewTemplateConferenceMapper.selectViewTemplateConferenceList(viewTemplateConferenceCon);
        for (ViewTemplateConference viewTemplateConference : downCascadeList) {
            if (McuType.MCU_TENCENT.getCode().equals(viewTemplateConference.getMcuType())) {
                List<BusiMcuTencentConferenceAppointment> busiMcuTencentConferenceAppointmentList = busiMcuTencentConferenceAppointmentMapper.selectBusiMcuTencentConferenceAppointmentByTemplateId(viewTemplateConference.getId());
                if (busiMcuTencentConferenceAppointmentList.size() > 0) {
                    BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointment = busiMcuTencentConferenceAppointmentList.get(0);
                    if (busiMcuTencentConferenceAppointment.getIsCloudConference() != null && busiMcuTencentConferenceAppointment.getIsCloudConference() == 1) {
                        cloudConference(tc, viewTemplateConference.getMcuType(), busiMcuTencentConferenceAppointment, conferenceContext);
                    }
                }
            } else if (McuType.MCU_HWCLOUD.getCode().equals(viewTemplateConference.getMcuType())) {
                List<BusiMcuHwcloudConferenceAppointment> busiMcuHwcloudConferenceAppointmentList = busiMcuHwcloudConferenceAppointmentMapper.selectBusiMcuHwcloudConferenceAppointmentByTemplateId(viewTemplateConference.getId());
                if (busiMcuHwcloudConferenceAppointmentList.size() > 0) {
                    BusiMcuHwcloudConferenceAppointment busiMcuHwcloudConferenceAppointment = busiMcuHwcloudConferenceAppointmentList.get(0);
                    if (busiMcuHwcloudConferenceAppointment.getIsCloudConference() != null && busiMcuHwcloudConferenceAppointment.getIsCloudConference() == 1) {
                        cloudConference(tc, viewTemplateConference.getMcuType(), busiMcuHwcloudConferenceAppointment, conferenceContext);
                    }
                }
            }
        }

    }

    private void cloudConference(BusiTemplateConference tc, String cloudMcuType_s, BusiConferenceAppointment busiConferenceAppointment, ConferenceContext conferenceContext) {
        if (Objects.equals(cloudMcuType_s, McuType.MCU_TENCENT.getCode())) {
            BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointment = (BusiMcuTencentConferenceAppointment) busiConferenceAppointment;
            if (StringUtils.isNotEmpty(busiMcuTencentConferenceAppointment.getCloudConferenceId())) {
                return;
            }
            Map<String, String> mapTencent = TencentCloudUtil.getConferenceNumber(tc.getName(), busiMcuTencentConferenceAppointment.getCloudConferenceId());
            if (mapTencent == null) {
                needRun = true;
                return;
            }
            String cloudConferenceId = mapTencent.get("conferenceId");
            if (!cloudConferenceId.equals(busiMcuTencentConferenceAppointment.getCloudConferenceId())) {
                BusiMcuTencentConferenceAppointmentMapper busiMcuTencentConferenceAppointmentMapper = BeanFactory.getBean(BusiMcuTencentConferenceAppointmentMapper.class);
                BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointmentUpdate = new BusiMcuTencentConferenceAppointment();
                busiMcuTencentConferenceAppointmentUpdate.setId(busiConferenceAppointment.getId());
                busiMcuTencentConferenceAppointmentUpdate.setCloudConferenceId(cloudConferenceId);
                busiMcuTencentConferenceAppointmentMapper.updateBusiMcuTencentConferenceAppointment(busiMcuTencentConferenceAppointmentUpdate);
            }
            mapTencent.put("conferenceNumber", mapTencent.get("conferenceNumber"));

            CloudConference cloudConference = new CloudConference();
            cloudConference.setConferenceNumber(mapTencent.get("conferenceNumber"));
            cloudConference.setCascadeMcuType(McuType.MCU_TENCENT.getCode());
            cloudConference.setCascadeConferenceId(cloudConferenceId);
            cloudConference.setName("腾讯会议" + mapTencent.get("conferenceNumber"));
            conferenceContext.getCloudConferenceList().add(cloudConference);
            conferenceContext.setCloudtencentId(cloudConference.getCascadeConferenceId());
            if (mapTencent != null) {

                logger.info("腾讯云会议号:{}", mapTencent.get("conferenceNumber"));
                logger.info("腾讯云会议号:{}", mapTencent.get("conferenceNumber"));
                logger.info("腾讯云会议号:{}", mapTencent.get("conferenceNumber"));
                logger.info("腾讯云会议号:{}", mapTencent.get("conferenceNumber"));

                InvitedAttendee ia = new InvitedAttendee();
                ia.setConferenceNumber(conferenceContext.getConferenceNumber());
                ia.setId(UUID.randomUUID().toString());
                ia.setName("腾讯会议" + mapTencent.get("conferenceNumber"));
                ia.setRemoteParty(mapTencent.get("conferenceNumber") + "@" + getMraIp());
                ia.setWeight(1);
                ia.setDeptId(conferenceContext.getDeptId());
                ia.setUpCascadeConferenceId(conferenceContext.getId());
                conferenceContext.addAttendee(ia);
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, ia);
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + ia.getName() + "】被邀请加入");
                new CallAttendeeProcessor(ia).process();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);

                HashMap hashMap = new HashMap();
                hashMap.put("conferenceId", conferenceContext.getId());
                hashMap.put("isCloudConference", true);
                hashMap.put("downCascadeConferenceId", cloudConferenceId);
                hashMap.put("cloudConference", cloudConference);
                BaseWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext.getId(), WebsocketMessageType.DOWN_CASCADE_CONFERENCE_ADDED, hashMap);
            }

        } else if (Objects.equals(cloudMcuType_s, McuType.MCU_HWCLOUD.getCode())) {
            BusiMcuHwcloudConferenceAppointment busiMcuHwcloudConferenceAppointment = (BusiMcuHwcloudConferenceAppointment) busiConferenceAppointment;
            if (StringUtils.isNotEmpty(busiMcuHwcloudConferenceAppointment.getCloudConferenceId())) {
                return;
            }
            Map<String, String> mapHwcloud = HuaweiCloudUtil.getConferenceNumber(tc.getName(), busiMcuHwcloudConferenceAppointment.getCloudConferenceId());
            if (mapHwcloud == null) {
                needRun = true;
                return;
            }
            String cloudConferenceId = mapHwcloud.get("conferenceId");
            if (!cloudConferenceId.equals(busiMcuHwcloudConferenceAppointment.getCloudConferenceId())) {
                BusiMcuHwcloudConferenceAppointmentMapper busiMcuHwcloudConferenceAppointmentMapper = BeanFactory.getBean(BusiMcuHwcloudConferenceAppointmentMapper.class);
                BusiMcuHwcloudConferenceAppointment busiMcuHwcloudConferenceAppointmentUpdate = new BusiMcuHwcloudConferenceAppointment();
                busiMcuHwcloudConferenceAppointmentUpdate.setId(busiConferenceAppointment.getId());
                busiMcuHwcloudConferenceAppointmentUpdate.setCloudConferenceId(cloudConferenceId);
                busiMcuHwcloudConferenceAppointmentMapper.updateBusiMcuHwcloudConferenceAppointment(busiMcuHwcloudConferenceAppointmentUpdate);
            }
            mapHwcloud.put("conferenceNumber", mapHwcloud.get("conferenceNumber"));

            CloudConference cloudConference = new CloudConference();
            cloudConference.setConferenceNumber(mapHwcloud.get("conferenceNumber"));
            cloudConference.setCascadeMcuType(McuType.MCU_HWCLOUD.getCode());
            cloudConference.setCascadeConferenceId(mapHwcloud.get("conferenceId"));
            cloudConference.setName("华为云会议" + mapHwcloud.get("conferenceNumber"));
            conferenceContext.getCloudConferenceList().add(cloudConference);
            conferenceContext.setCloudHwcloudId(cloudConference.getCascadeConferenceId());
            if (mapHwcloud != null) {

                logger.info("华为云会议号:{}", mapHwcloud.get("conferenceNumber"));
                logger.info("华为云会议号:{}", mapHwcloud.get("conferenceNumber"));
                logger.info("华为云会议号:{}", mapHwcloud.get("conferenceNumber"));
                logger.info("华为云会议号:{}", mapHwcloud.get("conferenceNumber"));

                InvitedAttendee ia = new InvitedAttendee();
                ia.setConferenceNumber(conferenceContext.getConferenceNumber());
                ia.setId(UUID.randomUUID().toString());
                ia.setName("华为云" + mapHwcloud.get("conferenceNumber"));
                ia.setRemoteParty(mapHwcloud.get("conferenceNumber") + "@" + getMraIp());
                ia.setWeight(1);
                ia.setDeptId(conferenceContext.getDeptId());
                ia.setUpCascadeConferenceId(conferenceContext.getId());
                conferenceContext.addAttendee(ia);
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, ia);
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + ia.getName() + "】被邀请加入");
                new CallAttendeeProcessor(ia).process();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);

                HashMap hashMap = new HashMap();
                hashMap.put("conferenceId", conferenceContext.getId());
                hashMap.put("isCloudConference", true);
                hashMap.put("downCascadeConferenceId", cloudConferenceId);
                hashMap.put("cloudConference", cloudConference);
                BaseWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext.getId(), WebsocketMessageType.DOWN_CASCADE_CONFERENCE_ADDED, hashMap);
            }

        }
    }

    public String getMraIp() {
        Set<String> mraIpList = ExternalConfigCache.getInstance().getMRAIpList();
        Iterator<String> iterator = mraIpList.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return "1.13.136.2";
    }
}
