package com.paradisecloud.fcm.mcu.kdc.cascade;

import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.InvitedAttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.task.InviteAttendeesTask;
import com.paradisecloud.fcm.mcu.kdc.task.McuKdcDelayTaskService;
import com.paradisecloud.fcm.service.conference.cascade.AbstractConference;
import com.paradisecloud.fcm.common.enumer.ConferenceEndType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.UpCascadeType;
import com.paradisecloud.fcm.dao.mapper.BusiMcuKdcTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuKdcTemplateConference;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcConferenceContextCache;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.conference.model.templateconference.StartTemplateConference;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IAttendeeForMcuKdcService;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcConferenceService;
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
public class McuKdcCascadeConference extends AbstractConference {

    @Resource
    private IBusiMcuKdcConferenceService busiMcuKdcConferenceService;
    @Resource
    private IAttendeeForMcuKdcService attendeeForMcuKdcService;

    private String code;

    @Override
    public void startConference(McuType mcuType, Long templateId) {

        BusiMcuKdcTemplateConferenceMapper busiMcuKdcTemplateConferenceMapper = BeanFactory.getBean(BusiMcuKdcTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuKdcTemplateConference tc = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(templateId);
        if (tc == null) {
            return;
        }
        String contextKey = EncryptIdUtil.generateContextKey(templateId, mcuType);
        McuKdcConferenceContext mcuKdcConferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        if (mcuKdcConferenceContext == null) {
            if (tc.getUpCascadeType() != null && UpCascadeType.AUTO_CREATE.getCode() != tc.getUpCascadeType() && UpCascadeType.SELECT_TEMPLATE_OUT_MEETING.getCode() != tc.getUpCascadeType()) {
                tc.setUpCascadeType(UpCascadeType.SELECT_TEMPLATE_OUT_MEETING.getCode());
                busiMcuKdcTemplateConferenceMapper.updateBusiMcuKdcTemplateConference(tc);
            }
            StartTemplateConference startTemplateConference = new StartTemplateConference();
            String contextKeyT = startTemplateConference.startTemplateConference(templateId);
            if (contextKeyT != null) {
                mcuKdcConferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
            }
        } else {
            if (tc.getUpCascadeType() != null && UpCascadeType.AUTO_CREATE.getCode() != tc.getUpCascadeType() && UpCascadeType.SELECT_TEMPLATE_IN_MEETING.getCode() != tc.getUpCascadeType()) {
                tc.setUpCascadeType(UpCascadeType.SELECT_TEMPLATE_IN_MEETING.getCode());
                busiMcuKdcTemplateConferenceMapper.updateBusiMcuKdcTemplateConference(tc);
            }
        }
        if (mcuKdcConferenceContext != null) {
            String account = mcuKdcConferenceContext.getTenantId() + mcuKdcConferenceContext.getConferenceNumber();
            String ip = mcuKdcConferenceContext.getMcuKdcBridge().getBusiMcuKdc().getIp();
            setUri(account + "@" + ip);
            this.code = account;
            setConferenceId(mcuKdcConferenceContext.getId());
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
        startConference(McuType.MCU_KDC, templateId);
        String contextKey = EncryptIdUtil.generateContextKey(templateId, McuType.MCU_KDC);
        McuKdcConferenceContext mcuKdcConferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        return mcuKdcConferenceContext;
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
        BusiMcuKdcTemplateConferenceMapper busiMcuKdcTemplateConferenceMapper = BeanFactory.getBean(BusiMcuKdcTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuKdcTemplateConference tc = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(id);
        if (tc == null) {
            return null;
        }
        McuKdcConferenceContext mcuKdcConferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        if (endType == ConferenceEndType.CASCADE.getValue() || tc.getUpCascadeType() == null || tc.getUpCascadeType() == UpCascadeType.AUTO_CREATE.getCode()) {
            busiMcuKdcConferenceService.endConference(conferenceId, endType);
        }
        return mcuKdcConferenceContext;
    }

    /**
     * 获取会议详情
     *
     * @param templateId
     * @return
     */
    @Override
    public BaseConferenceContext buildTemplateConferenceContext(Long templateId) {
        return busiMcuKdcConferenceService.buildTemplateConferenceContext(templateId);
    }

    /**
     * 重呼
     *
     * @param conferenceId
     * @param attendeeId
     */
    @Override
    public void recall(String conferenceId, String attendeeId) {
        attendeeForMcuKdcService.recall(conferenceId, attendeeId);
    }

    /**
     * 选看
     *
     * @param conferenceId
     * @param attendeeId
     */
    @Override
    public void chooseSee(String conferenceId, String attendeeId) {
        attendeeForMcuKdcService.chooseSee(conferenceId, attendeeId);
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
        attendeeForMcuKdcService.chooseSee(conferenceId, attendeeId, upCascadeOperate, upCascadeBroadcast, upCascadePolling, upCascadeRollCall);
    }

    @Override
    public void invite(String conferenceId,String conferenceName,String tencentRemoteParty,String password) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            McuKdcDelayTaskService delayTaskService = BeanFactory.getBean(McuKdcDelayTaskService.class);
            InvitedAttendeeForMcuKdc invitedAttendee= new InvitedAttendeeForMcuKdc();
            invitedAttendee.setRemoteParty(tencentRemoteParty);
            invitedAttendee.setName(conferenceName);
            invitedAttendee.setDtmfStr(password);
            InviteAttendeesTask inviteAttendeesTask = new InviteAttendeesTask(conferenceContext.getConferenceNumber(), 0, conferenceContext, invitedAttendee);
            delayTaskService.addTask(inviteAttendeesTask);
            conferenceContext.setTencentRemoteParty(tencentRemoteParty);
        }
    }
}
