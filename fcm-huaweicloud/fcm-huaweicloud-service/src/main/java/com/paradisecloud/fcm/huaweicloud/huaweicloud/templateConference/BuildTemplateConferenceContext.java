package com.paradisecloud.fcm.huaweicloud.huaweicloud.templateConference;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huaweicloud.sdk.meeting.v1.model.ExternalContactDTO;
import com.huaweicloud.sdk.meeting.v1.model.UserDTO;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.uuid.UUID;
import com.paradisecloud.fcm.common.enumer.AppointmentConferenceStatus;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceApprovalMapper;
import com.paradisecloud.fcm.dao.mapper.ViewTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiConferenceApproval;
import com.paradisecloud.fcm.dao.model.ViewTemplateConference;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.CropDirAttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.McuAttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudBridge;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudBridgeCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContextCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.ConfPresetParamDTO;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.HwcloudCropDirAttendee;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.PresetMultiPicReqDto;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.dao.mapper.BusiMcuHwcloudConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuHwcloudTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudTemplateConference;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.spring.BeanFactory;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nj
 * @date 2023/4/20 11:04
 */
public class BuildTemplateConferenceContext {


    public HwcloudConferenceContext buildTemplateConferenceContext(long templateConferenceId) {

        BusiMcuHwcloudTemplateConference tc = BeanFactory.getBean(BusiMcuHwcloudTemplateConferenceMapper.class).selectBusiMcuHwcloudTemplateConferenceById(templateConferenceId);
        if (tc == null) {
            return null;
        }
        HwcloudBridge bridge = HwcloudBridgeCache.getInstance().getAvailableBridgesByDept(tc.getDeptId());
        if (bridge == null) {
            throw new CustomException("未找到MCU,或正在初始化中...,稍后再试");
        }
        HwcloudConferenceContext conferenceContext = null;
        if (tc.getConfId() != null) {
            // 缓存中还有会议上下文，则证明会议正在进行，直接返回给前端
            conferenceContext = HwcloudConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(tc.getId(), McuType.MCU_HWCLOUD));
            if (conferenceContext != null) {
                bindCascadeConference(conferenceContext, tc);
                return conferenceContext;
            }
        }

        conferenceContext = new HwcloudConferenceContext(bridge);
        if (conferenceContext.getConferenceNumber() != null) {
            String conferenceRemoteParty = conferenceContext.getConferenceNumber() + "@" + conferenceContext.getMcuCallIp();
            conferenceContext.setConferenceRemoteParty(conferenceRemoteParty);
        }

        // 拷贝模板参数到会议上下文
        copyTemplateAttrs(conferenceContext, tc);
        conferenceContext.setHwcloudBridge(bridge);
        conferenceContext.setHwcloudUser(bridge.getHwcloudUserId());
        conferenceContext.setInstanceid(1);

        // 会议室的id
        conferenceContext.setId(EncryptIdUtil.generateConferenceId(conferenceContext.getContextKey()));

        if(conferenceContext.getConferenceNumber()!=null){
            String conferenceRemoteParty = conferenceContext.getTenantId()==null?"":conferenceContext.getTenantId() + conferenceContext.getConferenceNumber() + "@" + conferenceContext.getMcuCallIp();
            if (conferenceContext.getMcuCallPort() != null && conferenceContext.getMcuCallPort() != 5060) {
                conferenceRemoteParty += ":" + conferenceContext.getMcuCallPort();
            }
            conferenceContext.setConferenceRemoteParty(conferenceRemoteParty);
        }


        //邀请的企业联系人
        Map<String, Object> businessProperties = tc.getBusinessProperties();
        if(businessProperties!=null){
            Object attendees = businessProperties.get("attendees");
            if(attendees!=null){
                List<HwcloudCropDirAttendee> userDTOS = JSONArray.parseArray(JSONObject.toJSONString(attendees), HwcloudCropDirAttendee.class);

                for (HwcloudCropDirAttendee userDTO : userDTOS) {
                    CropDirAttendeeHwcloud cropDirAttendeeHwcloud = new CropDirAttendeeHwcloud();
                    cropDirAttendeeHwcloud.setName(userDTO.getName());
                    cropDirAttendeeHwcloud.setPhone(userDTO.getSip());
                    cropDirAttendeeHwcloud.setUserRole(userDTO.getRole());
                    cropDirAttendeeHwcloud.setRemoteParty(userDTO.getSip());
                    cropDirAttendeeHwcloud.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                    cropDirAttendeeHwcloud.setId(userDTO.getUserUUID()+ userDTO.getType()+tc.getId());
                    cropDirAttendeeHwcloud.setAccountId(userDTO.getAccountId());
                    cropDirAttendeeHwcloud.setUserUUID(userDTO.getUserUUID());
                    cropDirAttendeeHwcloud.setType(userDTO.getType());
                    cropDirAttendeeHwcloud.setSms(userDTO.getSms());
                    cropDirAttendeeHwcloud.setLoginType(userDTO.getType());
                    cropDirAttendeeHwcloud.setDeptId(conferenceContext.getDeptId());
                    conferenceContext.addAttendee(cropDirAttendeeHwcloud);

                }
            }
            Object confPresetParam = businessProperties.get("confPresetParam");
            if(confPresetParam!=null) {
                ConfPresetParamDTO confPresetParamDTO = new ConfPresetParamDTO();
                Map<String, Object> objectMap = (Map<String, Object>) confPresetParam;
                Object presetMultiPics = objectMap.get("presetMultiPics");
                List<PresetMultiPicReqDto> presetMultiPicReqDto = JSONArray.parseArray(JSONObject.toJSONString(presetMultiPics), PresetMultiPicReqDto.class);
                confPresetParamDTO.setPresetMultiPics(presetMultiPicReqDto);
                conferenceContext.setConfPresetParam(confPresetParamDTO);
            }else {
                ConfPresetParamDTO confPresetParamDTO = new ConfPresetParamDTO();
                conferenceContext.setConfPresetParam(confPresetParamDTO);
            }
        }



        //是否预约会议
        BusiMcuHwcloudConferenceAppointment con = new BusiMcuHwcloudConferenceAppointment();
        con.setTemplateId(templateConferenceId);
        con.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
        List<BusiMcuHwcloudConferenceAppointment> cas = BeanFactory.getBean(BusiMcuHwcloudConferenceAppointmentMapper.class).selectBusiMcuHwcloudConferenceAppointmentList(con);
        if (!ObjectUtils.isEmpty(cas)) {
            conferenceContext.setAppointment(true);
            conferenceContext.setAppointmentType(cas.get(0).getType());

            BusiConferenceAppointment busiConferenceAppointment = cas.get(0);
            if (busiConferenceAppointment.getApprovalId() != null) {
                BusiConferenceApproval busiConferenceApproval = BeanFactory.getBean(BusiConferenceApprovalMapper.class).selectBusiConferenceApprovalById(busiConferenceAppointment.getApprovalId());
                if (busiConferenceApproval != null) {
                    conferenceContext.setApprovedConference(true);
                }
            }
        }
        // 简单级联
        bindCascadeConference(conferenceContext, tc);
        return conferenceContext;

    }

    private void copyTemplateAttrs(HwcloudConferenceContext conferenceContext, BusiMcuHwcloudTemplateConference tc) {
        conferenceContext.setName(tc.getName());
        conferenceContext.setRemarks(tc.getRemarks());
        conferenceContext.setDeptId(tc.getDeptId());
        conferenceContext.setBandwidth(tc.getBandwidth());
        if (tc.getConferenceNumber() != null) {
            conferenceContext.setConferenceNumber(String.valueOf(tc.getConferenceNumber()));
        }
        conferenceContext.setType(tc.getType());
        conferenceContext.setTemplateConferenceId(tc.getId());
        conferenceContext.setBusinessFieldType(tc.getBusinessFieldType());
        conferenceContext.setConferencePassword(tc.getConferencePassword());
        conferenceContext.setTemplateCreateTime(new Date());

        // 呼叫设置
        conferenceContext.setAutoCallTerminal(tc.getIsAutoCall() != null && YesOrNo.convert(tc.getIsAutoCall()) == YesOrNo.YES);

        // 录制开关
        if (tc.getRecordingEnabled() != null && YesOrNo.convert(tc.getRecordingEnabled()) == YesOrNo.YES) {
            conferenceContext.setRecordingEnabled(YesOrNo.YES.getValue());
            conferenceContext.setRecorded(true);
        } else {
            conferenceContext.setRecordingEnabled(YesOrNo.NO.getValue());
            conferenceContext.setRecorded(false);
        }

        // 直播开关
        if (tc.getStreamingEnabled() != null && YesOrNo.convert(tc.getStreamingEnabled()) == YesOrNo.YES) {
            conferenceContext.setStreamingEnabled(YesOrNo.YES.getValue());
            conferenceContext.setStreaming(true);
        } else {
            conferenceContext.setStreamingEnabled(YesOrNo.NO.getValue());
            conferenceContext.setStreaming(false);
        }
        conferenceContext.setStreamingUrl(tc.getStreamUrl());


        conferenceContext.setDurationEnabled(tc.getDurationEnabled());
        conferenceContext.setDurationTime(tc.getDurationTime());
        conferenceContext.setCreateUserId(tc.getCreateUserId());
    }

    /**
     * <pre>绑定级联的MCU参会信息</pre>
     *
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-03-23 18:05
     */
    private void bindCascadeConference(HwcloudConferenceContext conferenceContext, BusiMcuHwcloudTemplateConference tc) {
        Map<String, ViewTemplateConference> newConferenceIdMap = new HashMap<>();
        Map<String, McuAttendeeHwcloud> oldConferenceIdMap = new HashMap<>();

        List<McuAttendeeHwcloud> mcuAttendees = conferenceContext.getMcuAttendees();
        for (McuAttendeeHwcloud attendee : mcuAttendees) {
            if (com.paradisecloud.common.utils.StringUtils.isNotEmpty(attendee.getId())) {
                String contextKey = EncryptIdUtil.parasToContextKey(attendee.getId());
                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                if (baseConferenceContext != null) {
                    String remoteParty = baseConferenceContext.getTenantId() + baseConferenceContext.getConferenceNumber() + "@" + baseConferenceContext.getMcuCallIp();
                    if (baseConferenceContext.getMcuCallPort() != null && baseConferenceContext.getMcuCallPort() != 5060) {
                        remoteParty += ":" + baseConferenceContext.getMcuCallPort();
                    }
                    attendee.setRemoteParty(remoteParty);
                    attendee.setIp(baseConferenceContext.getMcuCallIp());
                    attendee.setName(baseConferenceContext.getName());
                    attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                } else {
                    attendee.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                }
                oldConferenceIdMap.put(attendee.getId(), attendee);
            }
        }
        ViewTemplateConferenceMapper viewTemplateConferenceMapper = BeanFactory.getBean(ViewTemplateConferenceMapper.class);
        ViewTemplateConference viewTemplateConferenceConCascade = new ViewTemplateConference();
        viewTemplateConferenceConCascade.setUpCascadeId(conferenceContext.getTemplateConferenceId());
        viewTemplateConferenceConCascade.setUpCascadeMcuType(conferenceContext.getMcuType());
        List<ViewTemplateConference> viewTemplateConferenceList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceConCascade);
        for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceList) {
            newConferenceIdMap.put(viewTemplateConference.getConferenceId(), viewTemplateConference);
        }
        for (String conferenceIdTemp : oldConferenceIdMap.keySet()) {
            if (!newConferenceIdMap.containsKey(conferenceIdTemp)) {
                conferenceContext.removeMcuAttendee(oldConferenceIdMap.get(conferenceIdTemp));
            }
        }
        for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceList) {
            if (!oldConferenceIdMap.containsKey(viewTemplateConference.getConferenceId())) {
                bindCascadeConference(conferenceContext, viewTemplateConference);
            }
        }
        // 上级级联
        String upCascadeConferenceId = EncryptIdUtil.generateConferenceId(tc.getUpCascadeId(), tc.getUpCascadeMcuType());
        conferenceContext.setUpCascadeConferenceId(upCascadeConferenceId);
        conferenceContext.setUpCascadeIndex(tc.getUpCascadeIndex());
    }

    /**
     * <pre>绑定级联的MCU参会信息</pre>
     *
     * @param conferenceContext
     * @param viewTemplateConference void
     * @author lilinhai
     * @since 2021-03-23 18:05
     */
    private void bindCascadeConference(HwcloudConferenceContext conferenceContext, ViewTemplateConference viewTemplateConference) {
        McuAttendeeHwcloud mcuAttendee = new McuAttendeeHwcloud();
        mcuAttendee.setDeptId(conferenceContext.getDeptId());
        mcuAttendee.setDeptName(SysDeptCache.getInstance().get(conferenceContext.getDeptId()).getDeptName());
        mcuAttendee.setName(viewTemplateConference.getName());
        mcuAttendee.setWeight(0);
        mcuAttendee.setUserRole(0);
        mcuAttendee.setHost(false);
        mcuAttendee.setId(viewTemplateConference.getConferenceId());
        mcuAttendee.setUpCascadeConferenceId(viewTemplateConference.getUpCascadeConferenceId());
        mcuAttendee.setUpCascadeIndex(viewTemplateConference.getUpCascadeIndex());
        mcuAttendee.setCascadeConferenceId(viewTemplateConference.getConferenceId());
        mcuAttendee.setCascadeMcuType(viewTemplateConference.getMcuType());
        mcuAttendee.setCascadeTemplateId(viewTemplateConference.getId());
        String contextKey = EncryptIdUtil.parasToContextKey(mcuAttendee.getId());
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (baseConferenceContext != null) {
            String remoteParty = baseConferenceContext.getTenantId() + baseConferenceContext.getConferenceNumber() + "@" + baseConferenceContext.getMcuCallIp();
            if (baseConferenceContext.getMcuCallPort() != null && baseConferenceContext.getMcuCallPort() != 5060) {
                remoteParty += ":" + baseConferenceContext.getMcuCallPort();
            }
            mcuAttendee.setRemoteParty(remoteParty);
            mcuAttendee.setIp(baseConferenceContext.getMcuCallIp());
            mcuAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
        } else {
            mcuAttendee.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
        }
        mcuAttendee.setCascadeTemplateId(viewTemplateConference.getId());

        mcuAttendee.setCascadeDeptId(viewTemplateConference.getDeptId());
        mcuAttendee.setCascadeDeptName(SysDeptCache.getInstance().get(viewTemplateConference.getDeptId()).getDeptName());
        if (viewTemplateConference.getConferenceNumber() != null) {
            mcuAttendee.setCascadeConferenceNumber(viewTemplateConference.getConferenceNumber().toString());
        }
        conferenceContext.addMcuAttendee(mcuAttendee);
    }
}
