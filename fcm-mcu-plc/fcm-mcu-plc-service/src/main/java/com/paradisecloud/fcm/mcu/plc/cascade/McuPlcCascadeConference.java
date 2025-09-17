package com.paradisecloud.fcm.mcu.plc.cascade;

import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.AttendeeForMcuPlc;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.InvitedAttendeeForMcuPlc;
import com.paradisecloud.fcm.mcu.plc.task.InviteAttendeesTask;
import com.paradisecloud.fcm.mcu.plc.task.McuPlcDelayTaskService;
import com.paradisecloud.fcm.service.conference.cascade.AbstractConference;
import com.paradisecloud.fcm.common.enumer.ConferenceEndType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.UpCascadeType;
import com.paradisecloud.fcm.dao.mapper.BusiMcuPlcTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuPlcTemplateConference;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcConferenceContextCache;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.conference.model.templateconference.StartTemplateConference;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IAttendeeForMcuPlcService;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcConferenceService;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.sinhy.spring.BeanFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author nj
 * @date 2023/8/3 9:59
 */

@Component
public class McuPlcCascadeConference extends AbstractConference {

    @Resource
    private IBusiMcuPlcConferenceService busiMcuPlcConferenceService;
    @Resource
    private IAttendeeForMcuPlcService attendeeForMcuPlcService;

    private String code;

    @Override
    public void startConference(McuType mcuType, Long templateId) {

        BusiMcuPlcTemplateConferenceMapper busiMcuPlcTemplateConferenceMapper = BeanFactory.getBean(BusiMcuPlcTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuPlcTemplateConference tc = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(templateId);
        if (tc == null) {
            return;
        }
        String contextKey = EncryptIdUtil.generateContextKey(templateId, mcuType);
        McuPlcConferenceContext mcuPlcConferenceContext = McuPlcConferenceContextCache.getInstance().get(contextKey);
        if (mcuPlcConferenceContext == null) {
            if (tc.getUpCascadeType() != null && UpCascadeType.AUTO_CREATE.getCode() != tc.getUpCascadeType() && UpCascadeType.SELECT_TEMPLATE_OUT_MEETING.getCode() != tc.getUpCascadeType()) {
                tc.setUpCascadeType(UpCascadeType.SELECT_TEMPLATE_OUT_MEETING.getCode());
                busiMcuPlcTemplateConferenceMapper.updateBusiMcuPlcTemplateConference(tc);
            }
            StartTemplateConference startTemplateConference = new StartTemplateConference();
            String contextKeyT = startTemplateConference.startTemplateConference(templateId);
            if (contextKeyT != null) {
                mcuPlcConferenceContext = McuPlcConferenceContextCache.getInstance().get(contextKey);
            }
        } else {
            if (tc.getUpCascadeType() != null && UpCascadeType.AUTO_CREATE.getCode() != tc.getUpCascadeType() && UpCascadeType.SELECT_TEMPLATE_IN_MEETING.getCode() != tc.getUpCascadeType()) {
                tc.setUpCascadeType(UpCascadeType.SELECT_TEMPLATE_IN_MEETING.getCode());
                busiMcuPlcTemplateConferenceMapper.updateBusiMcuPlcTemplateConference(tc);
            }
        }
        if (mcuPlcConferenceContext != null) {
            String account = mcuPlcConferenceContext.getTenantId() + mcuPlcConferenceContext.getConferenceNumber();
            String ip = mcuPlcConferenceContext.getMcuPlcBridge().getBusiMcuPlc().getIp();
            setUri(account + "@" + ip);
            this.code = account;
            setConferenceId(mcuPlcConferenceContext.getId());
        }
    }

    @Override
    public void processCascade(String name, String uri,String ipProtocolType) {

    }

    @Override
    public void end() {
        String conferenceId = this.getConferenceId();
        endConference(conferenceId, ConferenceEndType.CASCADE.getValue());
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 开始会议
     *
     * @param templateId
     */
    @Override
    public BaseConferenceContext startConference(Long templateId) {
        startConference(McuType.MCU_PLC, templateId);
        String contextKey = EncryptIdUtil.generateContextKey(templateId, McuType.MCU_PLC);
        McuPlcConferenceContext mcuPlcConferenceContext = McuPlcConferenceContextCache.getInstance().get(contextKey);
        return mcuPlcConferenceContext;
    }

    /**
     * 结束会议
     *
     * @param conferenceId
     */
    @Override
    public BaseConferenceContext endConference(String conferenceId, int endType) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        Long id = conferenceIdVo.getId();
        BusiMcuPlcTemplateConferenceMapper busiMcuPlcTemplateConferenceMapper = BeanFactory.getBean(BusiMcuPlcTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuPlcTemplateConference tc = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(id);
        if (tc == null) {
            return null;
        }
        McuPlcConferenceContext mcuPlcConferenceContext = McuPlcConferenceContextCache.getInstance().get(contextKey);
        if (endType == ConferenceEndType.CASCADE.getValue() || tc.getUpCascadeType() == null || tc.getUpCascadeType() == UpCascadeType.AUTO_CREATE.getCode()) {
            busiMcuPlcConferenceService.endConference(conferenceId, endType);
        }
        return mcuPlcConferenceContext;
    }

    /**
     * 获取会议详情
     *
     * @param templateId
     * @return
     */
    @Override
    public BaseConferenceContext<AttendeeForMcuPlc> buildTemplateConferenceContext(Long templateId) {
        return busiMcuPlcConferenceService.buildTemplateConferenceContext(templateId);
    }

    /**
     * 重呼
     *
     * @param conferenceId
     * @param attendeeId
     */
    @Override
    public void recall(String conferenceId, String attendeeId) {
        attendeeForMcuPlcService.recall(conferenceId, attendeeId);
    }

    /**
     * 选看
     *
     * @param conferenceId
     * @param attendeeId
     */
    @Override
    public void chooseSee(String conferenceId, String attendeeId) {
        attendeeForMcuPlcService.chooseSee(conferenceId, attendeeId);
    }

    /**
     * 选看
     *
     * @param conferenceId
     * @param attendeeId
     * @param upCascadeOperate   是否为上级会议操作
     * @param upCascadeBroadcast 是否为上级会议广播
     * @param upCascadePolling   是否为上级会议轮询
     * @param upCascadeRollCall  是否为上级点名
     */
    @Override
    public void chooseSee(String conferenceId, String attendeeId, boolean upCascadeOperate, boolean upCascadeBroadcast, boolean upCascadePolling, boolean upCascadeRollCall) {
        attendeeForMcuPlcService.chooseSee(conferenceId, attendeeId, upCascadeOperate, upCascadeBroadcast, upCascadePolling, upCascadeRollCall);
    }

    @Override
    public void invite(String conferenceId,String conferenceName,String tencentRemoteParty,String password) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuPlcConferenceContext conferenceContext = McuPlcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            McuPlcDelayTaskService delayTaskService = BeanFactory.getBean(McuPlcDelayTaskService.class);
            InvitedAttendeeForMcuPlc invitedAttendee= new InvitedAttendeeForMcuPlc();
            invitedAttendee.setRemoteParty(tencentRemoteParty);
            invitedAttendee.setName(conferenceName);
            invitedAttendee.setDtmfStr(password);
            InviteAttendeesTask inviteAttendeesTask = new InviteAttendeesTask(conferenceContext.getConferenceNumber(), 0, conferenceContext, invitedAttendee);
            delayTaskService.addTask(inviteAttendeesTask);
            conferenceContext.setTencentRemoteParty(tencentRemoteParty);
        }
    }
}
