package com.paradisecloud.fcm.zte.cascade;

import com.paradisecloud.fcm.common.enumer.ConferenceEndType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.UpCascadeType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZteTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplateConference;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.cascade.AbstractConference;
import com.paradisecloud.fcm.zte.cache.McuZteConferenceContextCache;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.model.busi.attendee.AttendeeForMcuZte;
import com.paradisecloud.fcm.zte.model.busi.attendee.InvitedAttendeeForMcuZte;
import com.paradisecloud.fcm.zte.model.templateconference.StartTemplateConference;
import com.paradisecloud.fcm.zte.service.interfaces.IAttendeeForMcuZteService;
import com.paradisecloud.fcm.zte.service.interfaces.IBusiMcuZteConferenceService;
import com.paradisecloud.fcm.zte.task.InviteAttendeesTask;
import com.paradisecloud.fcm.zte.task.McuZteDelayTaskService;
import com.sinhy.spring.BeanFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author nj
 * @date 2023/8/3 9:59
 */

@Component
public class McuZteCascadeConference extends AbstractConference {

    @Resource
    private IBusiMcuZteConferenceService busiMcuZteConferenceService;
    @Resource
    private IAttendeeForMcuZteService attendeeForMcuZteService;

    private String code;

    @Override
    public void startConference(McuType mcuType, Long templateId) {

        BusiMcuZteTemplateConferenceMapper busiMcuZteTemplateConferenceMapper = BeanFactory.getBean(BusiMcuZteTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuZteTemplateConference tc = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(templateId);
        if (tc == null) {
            return;
        }
        String contextKey = EncryptIdUtil.generateContextKey(templateId, mcuType);
        McuZteConferenceContext mcuZteConferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        if (mcuZteConferenceContext == null) {
            if (tc.getUpCascadeType() != null && UpCascadeType.AUTO_CREATE.getCode() != tc.getUpCascadeType() && UpCascadeType.SELECT_TEMPLATE_OUT_MEETING.getCode() != tc.getUpCascadeType()) {
                tc.setUpCascadeType(UpCascadeType.SELECT_TEMPLATE_OUT_MEETING.getCode());
                busiMcuZteTemplateConferenceMapper.updateBusiMcuZteTemplateConference(tc);
            }
            StartTemplateConference startTemplateConference = new StartTemplateConference();
            String contextKeyT = startTemplateConference.startTemplateConference(templateId);
            if (contextKeyT != null) {
                mcuZteConferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
            }
        } else {
            if (tc.getUpCascadeType() != null && UpCascadeType.AUTO_CREATE.getCode() != tc.getUpCascadeType() && UpCascadeType.SELECT_TEMPLATE_IN_MEETING.getCode() != tc.getUpCascadeType()) {
                tc.setUpCascadeType(UpCascadeType.SELECT_TEMPLATE_IN_MEETING.getCode());
                busiMcuZteTemplateConferenceMapper.updateBusiMcuZteTemplateConference(tc);
            }
        }
        if (mcuZteConferenceContext != null) {
            String account =mcuZteConferenceContext.getConferenceNumber();
            String ip = mcuZteConferenceContext.getMcuZteBridge().getBusiMcuZte().getProxyHost();
            setUri(account + "@" + ip);
            this.code = account;
            setConferenceId(mcuZteConferenceContext.getId());
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
        startConference(McuType.MCU_ZTE, templateId);
        String contextKey = EncryptIdUtil.generateContextKey(templateId, McuType.MCU_ZTE);
        McuZteConferenceContext mcuZteConferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        return mcuZteConferenceContext;
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
        BusiMcuZteTemplateConferenceMapper busiMcuZteTemplateConferenceMapper = BeanFactory.getBean(BusiMcuZteTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuZteTemplateConference tc = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(id);
        if (tc == null) {
            return null;
        }
        McuZteConferenceContext mcuZteConferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        if (endType == ConferenceEndType.CASCADE.getValue() || tc.getUpCascadeType() == null || tc.getUpCascadeType() == UpCascadeType.AUTO_CREATE.getCode()) {
            busiMcuZteConferenceService.endConference(conferenceId, endType);
        }
        return mcuZteConferenceContext;
    }

    /**
     * 获取会议详情
     *
     * @param templateId
     * @return
     */
    @Override
    public BaseConferenceContext<AttendeeForMcuZte> buildTemplateConferenceContext(Long templateId) {
        return busiMcuZteConferenceService.buildTemplateConferenceContext(templateId);
    }

    /**
     * 重呼
     *
     * @param conferenceId
     * @param attendeeId
     */
    @Override
    public void recall(String conferenceId, String attendeeId) {
        attendeeForMcuZteService.recall(conferenceId, attendeeId);
    }

    /**
     * 选看
     *
     * @param conferenceId
     * @param attendeeId
     */
    @Override
    public void chooseSee(String conferenceId, String attendeeId) {
        attendeeForMcuZteService.chooseSee(conferenceId, attendeeId);
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
        attendeeForMcuZteService.chooseSee(conferenceId, attendeeId, upCascadeOperate, upCascadeBroadcast, upCascadePolling, upCascadeRollCall);
    }

    @Override
    public void invite(String conferenceId,String conferenceName,String tencentRemoteParty,String password) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            McuZteDelayTaskService delayTaskService = BeanFactory.getBean(McuZteDelayTaskService.class);
            InvitedAttendeeForMcuZte invitedAttendee= new InvitedAttendeeForMcuZte();
            invitedAttendee.setRemoteParty(tencentRemoteParty);
            invitedAttendee.setName(conferenceName);
            invitedAttendee.setDtmfStr(password);
            InviteAttendeesTask inviteAttendeesTask = new InviteAttendeesTask(conferenceContext.getConferenceNumber(), 0, conferenceContext, invitedAttendee);
            delayTaskService.addTask(inviteAttendeesTask);
            conferenceContext.setTencentRemoteParty(tencentRemoteParty);
        }
    }
}
