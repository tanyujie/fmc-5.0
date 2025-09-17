package com.paradisecloud.fcm.web.controller.ops;

import com.alibaba.fastjson.JSONObject;
import com.paradiscloud.fcm.business.model.enumer.BusinessFieldType;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.constant.DeptConstant;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc3ConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc3TemplateConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuTencentConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuTencentTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3ConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplateConference;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentTemplateConference;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.attendee.model.busiprocessor.CallAttendeeProcessor;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.conference.interfaces.ITemplateConferenceStartService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.AttendeeCountingStatistics;
import com.paradisecloud.fcm.fme.model.busi.attendee.InvitedAttendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.McuAttendee;
import com.paradisecloud.fcm.mcu.zj.model.busi.layout.splitscreen.AutomaticSplitScreen;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.model.CloudConference;
import com.paradisecloud.fcm.service.task.OpsNotifyTask;
import com.paradisecloud.fcm.service.util.HuaweiCloudUtil;
import com.paradisecloud.fcm.service.util.TencentCloudUtil;
import com.paradisecloud.fcm.web.model.SensSmcVo;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author 刘禧龙
 * Ops 初始化
 */
@RestController
@RequestMapping({"/ops/web"})
public class OpsController extends BaseController {

    @Resource
    private TaskService taskService;
    @Resource
    private BusiMcuTencentTemplateConferenceMapper busiMcuTencentTemplateConferenceMapper;
    @Resource
    private BusiMcuTencentConferenceAppointmentMapper busiMcuTencentConferenceAppointmentMapper;
    @Resource
    private BusiMcuSmc3TemplateConferenceMapper busiMcuSmc3TemplateConferenceMapper;
    @Resource
    private BusiMcuSmc3ConferenceAppointmentMapper busiMcuSmc3ConferenceAppointmentMapper;
    @Resource
    private ITemplateConferenceStartService templateConferenceStartService;
    @Resource
    private IAttendeeService attendeeService;
    /**
     * 获取所有终端类型
     */
    @GetMapping("/getAllTerminalType")
    @Operation(summary = "获取所有终端类型")
    public RestResponse getAllTerminalType() {
        List<Map<String, Object>> all = new ArrayList<>();
        List<Integer> terminalTypeList = ExternalConfigCache.getInstance().getTerminalTypeList();
        if (terminalTypeList != null && terminalTypeList.size() > 0) {
            for (Integer terminalTypeInt : terminalTypeList) {
                TerminalType terminalType = TerminalType.convert(terminalTypeInt, false);
                if (terminalType != null) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("type", terminalType.getId());
                    item.put("name", terminalType.getDisplayName());
                    all.add(item);
                }
            }
        } else {
            for (TerminalType terminalType : TerminalType.values()) {
                Map<String, Object> itam = new HashMap<>();
                itam.put("type", terminalType.getId());
                itam.put("name", terminalType.getDisplayName());
                all.add(itam);
            }
        }

        return RestResponse.success(all);
    }

    @PostMapping("/sendSms")
    @Operation(summary = "发送短信到管理员")
    public RestResponse sendSms(@RequestBody SensSmcVo sensSmcVo) {
        logger.info("发送短信到管理员========================================================================================");
        if (sensSmcVo != null) {
            logger.info("================" + JSONObject.toJSONString(sensSmcVo));
        } else {
            logger.info("================发送短信到管理员====参数错误");
        }

        OpsNotifyTask opsNotifyTask = new OpsNotifyTask("sendSms", 100, sensSmcVo.getConferenceName(), sensSmcVo.getConferenceNumber(), sensSmcVo.getPhone(), sensSmcVo.getStartTime(), sensSmcVo.getEndTime(), sensSmcVo.getNotifyType());
        taskService.addTask(opsNotifyTask);
        return RestResponse.success();
    }

    @PostMapping("/inviteConference/{conferenceId}")
    public RestResponse inviteConference(@PathVariable String conferenceId, @RequestBody JSONObject jsonObject) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        Object cloudMcuType = jsonObject.get("cloudMcuType");
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                ConferenceContext conferenceContext = (ConferenceContext) baseConferenceContext;

                //开启云
                try {
                    if (cloudMcuType != null) {
                        if (cloudMcuType instanceof ArrayList) {
                            ArrayList arrayList = (ArrayList) cloudMcuType;
                            for (Object cloudMcuType_s : arrayList) {
                                cloudConference(conferenceContext, conferenceContext.getName(), (String) cloudMcuType_s);
                            }
                        }
                        if (cloudMcuType instanceof String) {
                            cloudConference(conferenceContext, conferenceContext.getName(), (String) cloudMcuType);
                        }
                    }

                } catch (Exception e) {
                    logger.info(e.getMessage());
                    if (e instanceof CustomException) {
                        String message = e.getMessage();
                        if (message.contains("无权限")) {
                            message = "创建云会议失败：无权限创建会议！";
                        }
                        return RestResponse.fail(message);
                    }
                    return RestResponse.fail();
                }
                return RestResponse.success();
            }
        }
        return RestResponse.success();
    }

    private void cloudConference(ConferenceContext conferenceContext, String conferenceName, String cloudMcuType_s) throws CustomException {
        if (Objects.equals(cloudMcuType_s, McuType.MCU_TENCENT.getCode())) {

            BusiMcuTencentTemplateConference busiMcuTencentTemplateConference = new BusiMcuTencentTemplateConference();
            busiMcuTencentTemplateConference.setUpCascadeId(conferenceContext.getTemplateConferenceId());
            busiMcuTencentTemplateConference.setUpCascadeMcuType(conferenceContext.getMcuType());
            busiMcuTencentTemplateConference.setUpCascadeIndex(0);
            busiMcuTencentTemplateConference.setCreateTime(new Date());
            busiMcuTencentTemplateConference.setName("腾讯会议");
            busiMcuTencentTemplateConference.setDeptId(DeptConstant.OPS_DEPT_ID);
            busiMcuTencentTemplateConference.setRemarks(null);
            busiMcuTencentTemplateConference.setBusinessFieldType(BusinessFieldType.COMMON.getValue());
            busiMcuTencentTemplateConference.setViewType(1);
            // 默认自动分屏
            busiMcuTencentTemplateConference.setDefaultViewLayout(AutomaticSplitScreen.LAYOUT);
            busiMcuTencentTemplateConference.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
            busiMcuTencentTemplateConference.setDefaultViewIsFill(YesOrNo.YES.getValue());
            busiMcuTencentTemplateConference.setPollingInterval(10);
            busiMcuTencentTemplateConference.setDefaultViewIsDisplaySelf(-1);
            // 模板会议是否允许被级联：1允许，2不允许
            busiMcuTencentTemplateConference.setType(ConferenceType.SINGLE.getValue());
            // 模板创建类型
            busiMcuTencentTemplateConference.setCreateType(ConferenceTemplateCreateType.AUTO.getValue());
            // 会议号是否自动创建
            busiMcuTencentTemplateConference.setIsAutoCreateConferenceNumber(ConferenceNumberCreateType.MANUAL.getValue());
            busiMcuTencentTemplateConference.setStreamingEnabled(2);
            busiMcuTencentTemplateConference.setStreamUrl(null);
            // 是否录制
            busiMcuTencentTemplateConference.setRecordingEnabled(2);
            // 自动呼入与会者
            busiMcuTencentTemplateConference.setIsAutoCall(2);
            busiMcuTencentTemplateConference.setIsAutoMonitor(ConferenceAutoMonitor.NO.getValue());
            busiMcuTencentTemplateConference.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
            busiMcuTencentTemplateConference.setDefaultViewIsFill(YesOrNo.NO.getValue());
            busiMcuTencentTemplateConference.setBandwidth(2);

            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null) {
                busiMcuTencentTemplateConference.setCreateUserName(loginUser.getUsername());
                busiMcuTencentTemplateConference.setCreateUserId(loginUser.getUser().getUserId());
            }
            busiMcuTencentTemplateConferenceMapper.insertBusiMcuTencentTemplateConference(busiMcuTencentTemplateConference);

            BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointment = new BusiMcuTencentConferenceAppointment();
            busiMcuTencentConferenceAppointment.setIsCloudConference(1);
            busiMcuTencentConferenceAppointment.setCreateTime(new Date());
            busiMcuTencentConferenceAppointment.setDeptId(DeptConstant.OPS_DEPT_ID);
            busiMcuTencentConferenceAppointment.setTemplateId(busiMcuTencentTemplateConference.getId());
            busiMcuTencentConferenceAppointment.setIsAutoCreateTemplate(1);
            busiMcuTencentConferenceAppointment.setStartTime("9999-01-01 00:00:00");
            busiMcuTencentConferenceAppointment.setEndTime("9999-01-01 23:59:59");
            busiMcuTencentConferenceAppointment.setExtendMinutes(0);
            busiMcuTencentConferenceAppointment.setIsHangUp(2);
            busiMcuTencentConferenceAppointment.setStatus(2);
            busiMcuTencentConferenceAppointment.setIsStart(2);
            busiMcuTencentConferenceAppointment.setRepeatRate(1);
            busiMcuTencentConferenceAppointment.setType(2);
            busiMcuTencentConferenceAppointmentMapper.insertBusiMcuTencentConferenceAppointment(busiMcuTencentConferenceAppointment);

            Map<String, String> mapTencent = TencentCloudUtil.getConferenceNumber(conferenceName, null);
            String success = mapTencent.get("success");
            if (!"true".equals(success)) {
                String message = mapTencent.get("message");
                throw new CustomException(message);
            }
            String cloudConferenceId = mapTencent.get("conferenceId");
            BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointmentUpdate = new BusiMcuTencentConferenceAppointment();
            busiMcuTencentConferenceAppointmentUpdate.setId(busiMcuTencentConferenceAppointment.getId());
            busiMcuTencentConferenceAppointmentUpdate.setCloudConferenceId(cloudConferenceId);
            busiMcuTencentConferenceAppointmentMapper.updateBusiMcuTencentConferenceAppointment(busiMcuTencentConferenceAppointmentUpdate);
            mapTencent.put("conferenceNumber", mapTencent.get("conferenceNumber"));

            CloudConference cloudConference = new CloudConference();
            cloudConference.setConferenceNumber(mapTencent.get("conferenceNumber"));
            cloudConference.setCascadeMcuType(McuType.MCU_TENCENT.getCode());
            cloudConference.setCascadeConferenceId(mapTencent.get("conferenceId"));
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


            }

        } else if (Objects.equals(cloudMcuType_s, McuType.MCU_HWCLOUD.getCode())) {

            Map<String, String> mapHwcloud = HuaweiCloudUtil.getConferenceNumber(conferenceName, null);
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


            }

        } else if (Objects.equals(cloudMcuType_s, McuType.SMC3.getCode())) {
            List<McuAttendee> mcuAttendeesOlds = conferenceContext.getMcuAttendees();
            if(CollectionUtils.isNotEmpty(mcuAttendeesOlds)){
                for (McuAttendee mcuAttendeesOld : mcuAttendeesOlds) {

                    if(Objects.equals(mcuAttendeesOld.getCascadeMcuType(),McuType.SMC3.getCode())){
                        return;
                    }
                }
            }
            Smc3Bridge bridgesByDept = Smc3BridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId());
            if(bridgesByDept==null||!bridgesByDept.isAvailable()){
                throw new CustomException("没有SMC3资源");
            }
            BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = new BusiMcuSmc3TemplateConference();
            busiMcuSmc3TemplateConference.setUpCascadeId(conferenceContext.getTemplateConferenceId());
            busiMcuSmc3TemplateConference.setUpCascadeMcuType(conferenceContext.getMcuType());
            busiMcuSmc3TemplateConference.setUpCascadeIndex(0);
            busiMcuSmc3TemplateConference.setCreateTime(new Date());
            busiMcuSmc3TemplateConference.setName("SMC3会议");
            busiMcuSmc3TemplateConference.setDeptId(DeptConstant.OPS_DEPT_ID);
            busiMcuSmc3TemplateConference.setRemarks(null);
            busiMcuSmc3TemplateConference.setBusinessFieldType(BusinessFieldType.COMMON.getValue());
            busiMcuSmc3TemplateConference.setViewType(1);

            // 默认自动分屏
            busiMcuSmc3TemplateConference.setDefaultViewLayout(AutomaticSplitScreen.LAYOUT);
            busiMcuSmc3TemplateConference.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
            busiMcuSmc3TemplateConference.setDefaultViewIsFill(YesOrNo.YES.getValue());
            busiMcuSmc3TemplateConference.setPollingInterval(10);
            busiMcuSmc3TemplateConference.setDefaultViewIsDisplaySelf(-1);
            // 模板会议是否允许被级联：1允许，2不允许
            busiMcuSmc3TemplateConference.setType(ConferenceType.SINGLE.getValue());
            // 模板创建类型
            busiMcuSmc3TemplateConference.setCreateType(ConferenceTemplateCreateType.AUTO.getValue());
            // 会议号是否自动创建
            busiMcuSmc3TemplateConference.setIsAutoCreateConferenceNumber(ConferenceNumberCreateType.MANUAL.getValue());
            busiMcuSmc3TemplateConference.setStreamingEnabled(2);
            busiMcuSmc3TemplateConference.setStreamUrl(null);
            // 是否录制
            busiMcuSmc3TemplateConference.setRecordingEnabled(2);
            // 自动呼入与会者
            busiMcuSmc3TemplateConference.setIsAutoCall(2);
            busiMcuSmc3TemplateConference.setIsAutoMonitor(ConferenceAutoMonitor.NO.getValue());
            busiMcuSmc3TemplateConference.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
            busiMcuSmc3TemplateConference.setDefaultViewIsFill(YesOrNo.NO.getValue());
            busiMcuSmc3TemplateConference.setBandwidth(2);
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null) {
                busiMcuSmc3TemplateConference.setCreateUserName(loginUser.getUsername());
                busiMcuSmc3TemplateConference.setCreateUserId(loginUser.getUser().getUserId());
            }
            busiMcuSmc3TemplateConferenceMapper.insertBusiMcuSmc3TemplateConference(busiMcuSmc3TemplateConference);

            BusiMcuSmc3ConferenceAppointment busiMcuSmc3ConferenceAppointment = new BusiMcuSmc3ConferenceAppointment();
            busiMcuSmc3ConferenceAppointment.setCreateTime(new Date());
            busiMcuSmc3ConferenceAppointment.setDeptId(DeptConstant.OPS_DEPT_ID);
            busiMcuSmc3ConferenceAppointment.setTemplateId(busiMcuSmc3TemplateConference.getId());
            busiMcuSmc3ConferenceAppointment.setIsAutoCreateTemplate(1);
            busiMcuSmc3ConferenceAppointment.setStartTime(DateUtil.convertDateToString(new Date(), null));
            busiMcuSmc3ConferenceAppointment.setEndTime("9999-01-01 23:59:59");
            busiMcuSmc3ConferenceAppointment.setExtendMinutes(0);
            busiMcuSmc3ConferenceAppointment.setIsHangUp(2);
            busiMcuSmc3ConferenceAppointment.setStatus(1);
            busiMcuSmc3ConferenceAppointment.setIsStart(2);
            busiMcuSmc3ConferenceAppointment.setRepeatRate(1);
            busiMcuSmc3ConferenceAppointment.setType(2);
            busiMcuSmc3ConferenceAppointmentMapper.insertBusiMcuSmc3ConferenceAppointment(busiMcuSmc3ConferenceAppointment);
            logger.info("smc3会议号:{}", busiMcuSmc3TemplateConference.getConferenceNumber());

            Threads.sleep(2000);
            conferenceContext = templateConferenceStartService.buildTemplateConferenceContext(conferenceContext.getTemplateConferenceId());
            List<McuAttendee> mcuAttendees = conferenceContext.getMcuAttendees();
            if(CollectionUtils.isNotEmpty(mcuAttendees)){
                for (McuAttendee mcuAttendee : mcuAttendees) {
                    attendeeService.recall(conferenceContext.getId(), mcuAttendee.getId());
                    WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, mcuAttendee);
                    WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + mcuAttendee.getName() + "】被邀请加入");
                }

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
