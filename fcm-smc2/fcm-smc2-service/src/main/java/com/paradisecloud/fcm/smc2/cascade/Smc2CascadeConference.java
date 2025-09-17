package com.paradisecloud.fcm.smc2.cascade;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc2TemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2TemplateConference;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.cascade.AbstractConference;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContextCache;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.InvitedAttendeeSmc2;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IAttendeeSmc2Service;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiSmc2ConferenceService;
import com.paradisecloud.fcm.smc2.task.InviteAttendeeSmc2Task;
import com.paradisecloud.fcm.smc2.task.Smc2DelayTaskService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.sinhy.spring.BeanFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author nj
 * @date 2023/8/3 9:59
 */

@Component
public class Smc2CascadeConference extends AbstractConference {

    @Resource
    private IBusiSmc2ConferenceService busiSmc2ConferenceService;

    @Resource
    private IAttendeeSmc2Service attendeeSmc2Service;

    private String code;

    @Override
    public void startConference(McuType mcuType, Long templateId) {

        BusiMcuSmc2TemplateConferenceMapper busiTemplateConferenceMapper = BeanFactory.getBean(BusiMcuSmc2TemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuSmc2TemplateConference tc = busiTemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(templateId);
        if (tc == null) {
            return;
        }
        String contextKey = EncryptIdUtil.generateContextKey(templateId, mcuType);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            if (tc.getUpCascadeType() != null && UpCascadeType.AUTO_CREATE.getCode() != tc.getUpCascadeType() && UpCascadeType.SELECT_TEMPLATE_OUT_MEETING.getCode() != tc.getUpCascadeType()) {
                tc.setUpCascadeType(UpCascadeType.SELECT_TEMPLATE_OUT_MEETING.getCode());
                busiTemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(tc);
            }
            String contextKeyT = busiSmc2ConferenceService.startConference(templateId);
            if (StringUtils.isNotEmpty(contextKeyT)) {
                conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
            }
        } else {
            if (tc.getUpCascadeType() != null && UpCascadeType.AUTO_CREATE.getCode() != tc.getUpCascadeType() && UpCascadeType.SELECT_TEMPLATE_IN_MEETING.getCode() != tc.getUpCascadeType()) {
                tc.setUpCascadeType(UpCascadeType.SELECT_TEMPLATE_IN_MEETING.getCode());
                busiTemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(tc);
            }
        }
        if (conferenceContext != null) {
            setConferenceId(conferenceContext.getId());
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
        startConference(McuType.SMC2, templateId);
        String contextKey = EncryptIdUtil.generateContextKey(templateId, McuType.SMC2);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        return conferenceContext;
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
        BusiMcuSmc2TemplateConferenceMapper busiMcuSmc2TemplateConferenceMapper = BeanFactory.getBean(BusiMcuSmc2TemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuSmc2TemplateConference tc = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(id);
        if (tc == null) {
            return null;
        }
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        if (endType == ConferenceEndType.CASCADE.getValue() || tc.getUpCascadeType() == null || tc.getUpCascadeType() == UpCascadeType.AUTO_CREATE.getCode()) {
            busiSmc2ConferenceService.endConference(conferenceId, endType);
        }
        return conferenceContext;
    }

    @Override
    public BaseConferenceContext buildTemplateConferenceContext(Long templateId) {
        return null;
    }

    /**
     * 重呼
     *
     * @param conferenceId
     * @param attendeeId
     */
    @Override
    public void recall(String conferenceId, String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeSmc2 attendee = conferenceContext.getAttendeeById(attendeeId);

            if(attendee!=null&&attendee.isMcuAttendee()){
                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(attendee.getCascadeConferenceId()));
                if (baseConferenceContext != null) {
                    baseConferenceContext.setUpCascadeRemoteParty(conferenceContext.getConferenceRemoteParty());
                }
                if(attendee.getParticipantUuid()==null){
                    BusiTerminal terminal = TerminalCache.getInstance().getBySn(attendee.getSn());
                    if (terminal != null) {
                        if (TerminalType.isFCMSIP(terminal.getType())) {
                            BeanFactory.getBean(IMqttService.class).inviteAttendeeJoinConference(attendee, conferenceContext, AttendType.AUTO_JOIN.getValue());
                            return;
                        }
                    }
                    Smc2DelayTaskService delayTaskService = BeanFactory.getBean(Smc2DelayTaskService.class);
                    InviteAttendeeSmc2Task inviteAttendeesTask = new InviteAttendeeSmc2Task(conferenceContext.getConferenceNumber(), 0, conferenceContext, attendee);
                    delayTaskService.addTask(inviteAttendeesTask);
                }else {
                    attendeeSmc2Service.recall(conferenceId, attendeeId);
                }
            }

        }

    }

    /**
     * 选看
     *
     * @param conferenceId
     * @param attendeeId
     */
    @Override
    public void chooseSee(String conferenceId, String attendeeId) {
        attendeeSmc2Service.chooseSee(conferenceId, attendeeId);
    }

    @Override
    public void callTheRoll(String conferenceId, String attendeeId) {
        attendeeSmc2Service.callTheRoll(conferenceId, attendeeId);
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
       attendeeSmc2Service.chooseSee(conferenceId, attendeeId, upCascadeOperate, upCascadeBroadcast, upCascadePolling, upCascadeRollCall);
    }

    @Override
    public void invite(String conferenceId,String conferenceName,String tencentRemoteParty,String password) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            Smc2DelayTaskService delayTaskService = BeanFactory.getBean(Smc2DelayTaskService.class);
            InvitedAttendeeSmc2 invitedAttendeeSmc2 = new InvitedAttendeeSmc2();
            invitedAttendeeSmc2.setRemoteParty(tencentRemoteParty);
            invitedAttendeeSmc2.setName(conferenceName);
            invitedAttendeeSmc2.setDtmfStr(password);
            InviteAttendeeSmc2Task inviteAttendeesTask = new InviteAttendeeSmc2Task(conferenceContext.getConferenceNumber(), 0, conferenceContext, invitedAttendeeSmc2);
            delayTaskService.addTask(inviteAttendeesTask);
            conferenceContext.setTencentRemoteParty(tencentRemoteParty);
        }
    }
}
