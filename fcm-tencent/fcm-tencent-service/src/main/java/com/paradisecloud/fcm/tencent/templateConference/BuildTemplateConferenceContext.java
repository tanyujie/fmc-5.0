package com.paradisecloud.fcm.tencent.templateConference;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.exception.CustomException;
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
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.tencent.busi.attende.InvitedAttendeeTencent;
import com.paradisecloud.fcm.tencent.busi.attende.McuAttendeeTencent;
import com.paradisecloud.fcm.tencent.busi.attende.RoomAttendeeTencent;
import com.paradisecloud.fcm.tencent.cache.TencentBridge;
import com.paradisecloud.fcm.tencent.cache.TencentBridgeCache;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContextCache;
import com.paradisecloud.fcm.dao.mapper.BusiMcuTencentConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuTencentTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentTemplateConference;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.spring.BeanFactory;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * @author nj
 * @date 2023/4/20 11:04
 */
public class BuildTemplateConferenceContext {


    public TencentConferenceContext buildTemplateConferenceContext(long templateConferenceId) {
        BusiMcuTencentTemplateConference tc = BeanFactory.getBean(BusiMcuTencentTemplateConferenceMapper.class).selectBusiMcuTencentTemplateConferenceById(templateConferenceId);
        if (tc == null) {
            return null;
        }
        TencentBridge bridge = TencentBridgeCache.getInstance().getAvailableBridgesByDept(tc.getDeptId());
        if (bridge == null) {
            throw new CustomException("未找到MCU,或正在初始化中...,稍后再试");
        }
        return  buildTemplateConferenceContext(templateConferenceId,bridge);

    }


    public TencentConferenceContext buildTemplateConferenceContext(long templateConferenceId, TencentBridge bridge) {

        BusiMcuTencentTemplateConference tc = BeanFactory.getBean(BusiMcuTencentTemplateConferenceMapper.class).selectBusiMcuTencentTemplateConferenceById(templateConferenceId);
        if (tc == null) {
            return null;
        }
        if (bridge == null) {
            throw new CustomException("未找到MCU,或正在初始化中...,稍后再试");
        }
        TencentConferenceContext conferenceContext = null;
        if (tc.getConfId() != null) {
            // 缓存中还有会议上下文，则证明会议正在进行，直接返回给前端
            conferenceContext = TencentConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(tc.getId(), McuType.MCU_TENCENT));
            if (conferenceContext != null) {
                bindCascadeConference(conferenceContext, tc);
                return conferenceContext;
            }
        }

        conferenceContext = new TencentConferenceContext(bridge);
        if (conferenceContext.getConferenceNumber() != null) {
            String conferenceRemoteParty = conferenceContext.getConferenceNumber() + "@" + conferenceContext.getMcuCallIp();
            conferenceContext.setConferenceRemoteParty(conferenceRemoteParty);
        }

        // 拷贝模板参数到会议上下文
        copyTemplateAttrs(conferenceContext, tc);
        conferenceContext.setTencentBridge(bridge);
        conferenceContext.setTencentUser(bridge.getTencentUserId());
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

        Map<String, Object> businessProperties = tc.getBusinessProperties();

        //邀请的企业联系人
        if(businessProperties!=null){
            Object attendees = businessProperties.get("attendees");
            if(attendees!=null){
                List<RoomAttendeeTencent> userDTOS = JSONArray.parseArray(JSONObject.toJSONString(attendees), RoomAttendeeTencent.class);
                for (RoomAttendeeTencent userDTO : userDTOS) {
                    RoomAttendeeTencent roomAttendeeTencent = new RoomAttendeeTencent();
                    roomAttendeeTencent.setName(userDTO.getName());
                    roomAttendeeTencent.setOnlineStatus(Objects.equals(userDTO.getMeetingRoomStatus(), 2)?1:2);
                    roomAttendeeTencent.setId(userDTO.getMeetingRoomId());
                    roomAttendeeTencent.setAccountType(userDTO.getAccountType());
                    roomAttendeeTencent.setIsallowCall(userDTO.getIsallowCall());
                    roomAttendeeTencent.setMeetingRoomName(userDTO.getMeetingRoomName());
                    roomAttendeeTencent.setName(userDTO.getMeetingRoomName());
                    roomAttendeeTencent.setDeptId(conferenceContext.getDeptId());
                    roomAttendeeTencent.setMeetingRoomId(userDTO.getMeetingRoomId());
                    conferenceContext.addAttendee(roomAttendeeTencent);
                }
            }
        }





        //是否预约会议
        BusiMcuTencentConferenceAppointment con = new BusiMcuTencentConferenceAppointment();
        con.setTemplateId(templateConferenceId);
        con.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
        List<BusiMcuTencentConferenceAppointment> cas = BeanFactory.getBean(BusiMcuTencentConferenceAppointmentMapper.class).selectBusiMcuTencentConferenceAppointmentList(con);
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

    private void copyTemplateAttrs(TencentConferenceContext conferenceContext, BusiMcuTencentTemplateConference tc) {
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
        conferenceContext.setChairmanPassword(tc.getChairmanPassword());
        conferenceContext.setTemplateCreateTime(new Date());
        if(tc.getMuteType()==null){
            conferenceContext.setParticipantJoinMute(0);
        }else {
            conferenceContext.setParticipantJoinMute(tc.getMuteType()==1?1:0);
        }

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
        conferenceContext.setDtmf(tc.getConferencePassword());
    }

    /**
     * <pre>绑定级联的MCU参会信息</pre>
     *
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-03-23 18:05
     */
    private void bindCascadeConference(TencentConferenceContext conferenceContext, BusiMcuTencentTemplateConference tc) {
        Map<String, ViewTemplateConference> newConferenceIdMap = new HashMap<>();
        Map<String, McuAttendeeTencent> oldConferenceIdMap = new HashMap<>();

        List<McuAttendeeTencent> mcuAttendees = conferenceContext.getMcuAttendees();
        for (McuAttendeeTencent attendee : mcuAttendees) {
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
    private void bindCascadeConference(TencentConferenceContext conferenceContext, ViewTemplateConference viewTemplateConference) {
        McuAttendeeTencent mcuAttendee = new McuAttendeeTencent();
        mcuAttendee.setDeptId(conferenceContext.getDeptId());
        mcuAttendee.setDeptName(SysDeptCache.getInstance().get(conferenceContext.getDeptId()).getDeptName());
        mcuAttendee.setName(viewTemplateConference.getName());
        mcuAttendee.setWeight(0);
        mcuAttendee.setUserRole(0);
        mcuAttendee.setHost(false);
        mcuAttendee.setInstanceid(9);
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
