package com.paradisecloud.smc3.busi.cascade;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc3TemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplateConference;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.cascade.AbstractConference;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.InvitedAttendeeSmc3;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.service.interfaces.IAttendeeSmc3Service;
import com.paradisecloud.smc3.service.interfaces.IBusiSmc3ConferenceService;
import com.paradisecloud.smc3.task.InviteAttendeeSmc3Task;
import com.paradisecloud.smc3.task.Smc3DelayTaskService;
import com.sinhy.spring.BeanFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author nj
 * @date 2023/8/3 9:59
 */

@Component
public class Smc3CascadeConference extends AbstractConference {

    @Resource
    private IBusiSmc3ConferenceService busiSmc3ConferenceService;

    @Resource
    private IAttendeeSmc3Service attendeeSmc3Service;

    private String code;

    @Override
    public void startConference(McuType mcuType, Long templateId) {

        BusiMcuSmc3TemplateConferenceMapper busiTemplateConferenceMapper = BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuSmc3TemplateConference tc = busiTemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(templateId);
        if (tc == null) {
            return;
        }
        String contextKey = EncryptIdUtil.generateContextKey(templateId, mcuType);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            if (tc.getUpCascadeType() != null && UpCascadeType.AUTO_CREATE.getCode() != tc.getUpCascadeType() && UpCascadeType.SELECT_TEMPLATE_OUT_MEETING.getCode() != tc.getUpCascadeType()) {
                tc.setUpCascadeType(UpCascadeType.SELECT_TEMPLATE_OUT_MEETING.getCode());
                busiTemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(tc);
            }
            String contextKeyT = busiSmc3ConferenceService.startConference(templateId);
            if (StringUtils.isNotEmpty(contextKeyT)) {
                conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
            }
        } else {
            if (tc.getUpCascadeType() != null && UpCascadeType.AUTO_CREATE.getCode() != tc.getUpCascadeType() && UpCascadeType.SELECT_TEMPLATE_IN_MEETING.getCode() != tc.getUpCascadeType()) {
                tc.setUpCascadeType(UpCascadeType.SELECT_TEMPLATE_IN_MEETING.getCode());
                busiTemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(tc);
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
        startConference(McuType.SMC3, templateId);
        String contextKey = EncryptIdUtil.generateContextKey(templateId, McuType.SMC3);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
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
        BusiMcuSmc3TemplateConferenceMapper busiMcuSmc3TemplateConferenceMapper = BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuSmc3TemplateConference tc = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(id);
        if (tc == null) {
            return null;
        }
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (endType == ConferenceEndType.CASCADE.getValue() || tc.getUpCascadeType() == null || tc.getUpCascadeType() == UpCascadeType.AUTO_CREATE.getCode()) {
            busiSmc3ConferenceService.endConference(conferenceId, endType,true,false);
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
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeSmc3 attendee = conferenceContext.getAttendeeById(attendeeId);

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
                    Smc3DelayTaskService delayTaskService = BeanFactory.getBean(Smc3DelayTaskService.class);
                    InviteAttendeeSmc3Task inviteAttendeesTask = new InviteAttendeeSmc3Task(conferenceContext.getConferenceNumber(), 0, conferenceContext, attendee);
                    delayTaskService.addTask(inviteAttendeesTask);
                }else {
                    attendeeSmc3Service.recall(conferenceId, attendeeId);
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
        attendeeSmc3Service.chooseSee(conferenceId, attendeeId);
    }

    @Override
    public void callTheRoll(String conferenceId, String attendeeId) {
        attendeeSmc3Service.callTheRoll(conferenceId, attendeeId);
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
       attendeeSmc3Service.chooseSee(conferenceId, attendeeId, upCascadeOperate, upCascadeBroadcast, upCascadePolling, upCascadeRollCall);
    }

    @Override
    public void invite(String conferenceId,String conferenceName,String tencentRemoteParty,String password) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            Smc3DelayTaskService delayTaskService = BeanFactory.getBean(Smc3DelayTaskService.class);
            InvitedAttendeeSmc3 invitedAttendeeSmc3 = new InvitedAttendeeSmc3();
            invitedAttendeeSmc3.setRemoteParty(tencentRemoteParty);
            invitedAttendeeSmc3.setName(conferenceName);
            invitedAttendeeSmc3.setDtmfStr(password);
            InviteAttendeeSmc3Task inviteAttendeesTask = new InviteAttendeeSmc3Task(conferenceContext.getConferenceNumber(), 0, conferenceContext, invitedAttendeeSmc3);
            delayTaskService.addTask(inviteAttendeesTask);
            conferenceContext.setTencentRemoteParty(tencentRemoteParty);
        }
    }


}
