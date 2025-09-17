package com.paradisecloud.fcm.fme.conference.cascade;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.fme.attendee.model.busiprocessor.RtspCallAttendeeProcessor;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.model.busi.attendee.AttendeeCountingStatistics;
import com.paradisecloud.fcm.fme.model.busi.attendee.InvitedAttendee;
import com.paradisecloud.fcm.service.conference.cascade.AbstractConference;
import com.paradisecloud.fcm.common.enumer.ConferenceEndType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.UpCascadeType;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.ITemplateConferenceStartService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.sinhy.spring.BeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author nj
 * @date 2023/8/3 9:59
 */

@Component
public class FmeCascadeConference extends AbstractConference {

    @Resource
    private ITemplateConferenceStartService templateConferenceStartService;
    @Resource
    private IBusiConferenceService busiConferenceService;
    @Resource
    private IAttendeeService attendeeService;

    private String code;

    @Override
    public void startConference(McuType mcuType, Long templateId) {

        BusiTemplateConferenceMapper busiTemplateConferenceMapper = BeanFactory.getBean(BusiTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiTemplateConference tc = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(templateId);
        if (tc == null) {
            return;
        }
        String contextKey = EncryptIdUtil.generateContextKey(templateId, mcuType);
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            if (tc.getUpCascadeType() != null && UpCascadeType.AUTO_CREATE.getCode() != tc.getUpCascadeType() && UpCascadeType.SELECT_TEMPLATE_OUT_MEETING.getCode() != tc.getUpCascadeType()) {
                tc.setUpCascadeType(UpCascadeType.SELECT_TEMPLATE_OUT_MEETING.getCode());
                busiTemplateConferenceMapper.updateBusiTemplateConference(tc);
            }
            String contextKeyT = templateConferenceStartService.startTemplateConference(templateId);
            if (StringUtils.isNotEmpty(contextKeyT)) {
                conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
            }
        } else {
            if (tc.getUpCascadeType() != null && UpCascadeType.AUTO_CREATE.getCode() != tc.getUpCascadeType() && UpCascadeType.SELECT_TEMPLATE_IN_MEETING.getCode() != tc.getUpCascadeType()) {
                tc.setUpCascadeType(UpCascadeType.SELECT_TEMPLATE_IN_MEETING.getCode());
                busiTemplateConferenceMapper.updateBusiTemplateConference(tc);
            }
        }
        if (conferenceContext != null) {
            String account = conferenceContext.getTenantId() + conferenceContext.getConferenceNumber();
            FmeBridge fmeBridge = FmeBridgeCache.getInstance().get(conferenceContext.getMcuId());
            String ip = fmeBridge.getBusiFme().getIp();
            setUri(account + "@" + ip);
            this.code = account;
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
        startConference(McuType.FME, templateId);
        String contextKey = EncryptIdUtil.generateContextKey(templateId, McuType.FME);
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
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
        BusiTemplateConferenceMapper busiTemplateConferenceMapper = BeanFactory.getBean(BusiTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiTemplateConference tc = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(id);
        if (tc == null) {
            return null;
        }
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
        if (endType == ConferenceEndType.CASCADE.getValue() || tc.getUpCascadeType() == null || tc.getUpCascadeType() == UpCascadeType.AUTO_CREATE.getCode()) {
            busiConferenceService.endConference(conferenceId, endType, EndReasonsType.ADMINISTRATOR_HANGS_UP);
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
        attendeeService.recall(conferenceId, attendeeId);
    }

    /**
     * 选看
     *
     * @param conferenceId
     * @param attendeeId
     */
    @Override
    public void chooseSee(String conferenceId, String attendeeId) {
        attendeeService.chooseSee(conferenceId, attendeeId);
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
        attendeeService.chooseSee(conferenceId, attendeeId, upCascadeOperate, upCascadeBroadcast, upCascadePolling, upCascadeRollCall);
    }

    @Override
    public void invite(String conferenceId,String conferenceName,String tencentRemoteParty,String password) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            InvitedAttendee ia = new InvitedAttendee();
            ia.setConferenceNumber(conferenceContext.getConferenceNumber());
            ia.setId(UUID.randomUUID().toString());
            ia.setName(conferenceName);
            ia.setRemoteParty(tencentRemoteParty);
            ia.setWeight(1);
            ia.setDtmfStr(password);
            ia.setDeptId(conferenceContext.getDeptId());
            conferenceContext.addAttendee(ia);
            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, ia);
            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + ia.getName() + "】被邀请加入");
            recall(conferenceId, ia.getId());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
            conferenceContext.setTencentRemoteParty(tencentRemoteParty);
        }
    }
}
