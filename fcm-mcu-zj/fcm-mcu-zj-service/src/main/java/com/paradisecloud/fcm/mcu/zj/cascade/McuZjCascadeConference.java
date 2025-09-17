package com.paradisecloud.fcm.mcu.zj.cascade;

import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.InvitedAttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.task.DelayTaskService;
import com.paradisecloud.fcm.mcu.zj.task.InviteAttendeesTask;
import com.paradisecloud.fcm.service.conference.cascade.AbstractConference;
import com.paradisecloud.fcm.common.enumer.ConferenceEndType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.UpCascadeType;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZjTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuZjTemplateConference;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjConferenceContextCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.conference.model.templateconference.StartTemplateConference;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IAttendeeForMcuZjService;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjConferenceService;
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
public class McuZjCascadeConference extends AbstractConference {

    @Resource
    private IBusiMcuZjConferenceService busiMcuZjConferenceService;
    @Resource
    private IAttendeeForMcuZjService attendeeForMcuZjService;

    private String code;

    @Override
    public void startConference(McuType mcuType, Long templateId) {

        BusiMcuZjTemplateConferenceMapper busiMcuZjTemplateConferenceMapper = BeanFactory.getBean(BusiMcuZjTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuZjTemplateConference tc = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(templateId);
        if (tc == null) {
            return;
        }
        String contextKey = EncryptIdUtil.generateContextKey(templateId, mcuType);
        McuZjConferenceContext mcuZjConferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (mcuZjConferenceContext == null) {
            if (tc.getUpCascadeType() != null && UpCascadeType.AUTO_CREATE.getCode() != tc.getUpCascadeType() && UpCascadeType.SELECT_TEMPLATE_OUT_MEETING.getCode() != tc.getUpCascadeType()) {
                tc.setUpCascadeType(UpCascadeType.SELECT_TEMPLATE_OUT_MEETING.getCode());
                busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(tc);
            }
            StartTemplateConference startTemplateConference = new StartTemplateConference();
            String contextKeyT = startTemplateConference.startTemplateConference(templateId);
            if (contextKeyT != null) {
                mcuZjConferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
            }
        } else {
            if (tc.getUpCascadeType() != null && UpCascadeType.AUTO_CREATE.getCode() != tc.getUpCascadeType() && UpCascadeType.SELECT_TEMPLATE_IN_MEETING.getCode() != tc.getUpCascadeType()) {
                tc.setUpCascadeType(UpCascadeType.SELECT_TEMPLATE_IN_MEETING.getCode());
                busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(tc);
            }
        }
        if (mcuZjConferenceContext != null) {
            String account = mcuZjConferenceContext.getTenantId() + mcuZjConferenceContext.getConferenceNumber();
            String ip = mcuZjConferenceContext.getMcuZjBridge().getBusiMcuZj().getIp();
            setUri(account + "@" + ip);
            this.code = account;
            setConferenceId(mcuZjConferenceContext.getId());
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
        startConference(McuType.MCU_ZJ, templateId);
        String contextKey = EncryptIdUtil.generateContextKey(templateId, McuType.MCU_ZJ);
        McuZjConferenceContext mcuZjConferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        return mcuZjConferenceContext;
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
        BusiMcuZjTemplateConferenceMapper busiMcuZjTemplateConferenceMapper = BeanFactory.getBean(BusiMcuZjTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuZjTemplateConference tc = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(id);
        if (tc == null) {
            return null;
        }
        McuZjConferenceContext mcuZjConferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (endType == ConferenceEndType.CASCADE.getValue() || tc.getUpCascadeType() == null || tc.getUpCascadeType() == UpCascadeType.AUTO_CREATE.getCode()) {
            busiMcuZjConferenceService.endConference(conferenceId, endType);
        }
        return mcuZjConferenceContext;
    }

    /**
     * 获取会议详情
     *
     * @param templateId
     * @return
     */
    @Override
    public BaseConferenceContext buildTemplateConferenceContext(Long templateId) {
        return busiMcuZjConferenceService.buildTemplateConferenceContext(templateId);
    }

    /**
     * 重呼
     *
     * @param conferenceId
     * @param attendeeId
     */
    @Override
    public void recall(String conferenceId, String attendeeId) {
        attendeeForMcuZjService.recall(conferenceId, attendeeId);
    }

    /**
     * 选看
     *
     * @param conferenceId
     * @param attendeeId
     */
    @Override
    public void chooseSee(String conferenceId, String attendeeId) {
        attendeeForMcuZjService.chooseSee(conferenceId, attendeeId);
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
        attendeeForMcuZjService.chooseSee(conferenceId, attendeeId, upCascadeOperate, upCascadeBroadcast, upCascadePolling, upCascadeRollCall);
    }

    @Override
    public void invite(String conferenceId,String conferenceName,String tencentRemoteParty,String password) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            DelayTaskService delayTaskService = BeanFactory.getBean(DelayTaskService.class);
            InvitedAttendeeForMcuZj invitedAttendeeForMcuZj= new InvitedAttendeeForMcuZj();
            invitedAttendeeForMcuZj.setRemoteParty(tencentRemoteParty);
            invitedAttendeeForMcuZj.setName(conferenceName);
            invitedAttendeeForMcuZj.setDtmfStr(password);
            InviteAttendeesTask inviteAttendeesTask = new InviteAttendeesTask(conferenceContext.getConferenceNumber(), 0, conferenceContext, invitedAttendeeForMcuZj);
            delayTaskService.addTask(inviteAttendeesTask);
            conferenceContext.setTencentRemoteParty(tencentRemoteParty);
        }
    }
}
