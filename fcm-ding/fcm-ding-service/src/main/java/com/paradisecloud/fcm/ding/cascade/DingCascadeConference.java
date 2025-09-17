package com.paradisecloud.fcm.ding.cascade;

import com.paradisecloud.fcm.ding.cache.DingConferenceContext;
import com.paradisecloud.fcm.ding.cache.DingConferenceContextCache;
import com.paradisecloud.fcm.ding.service2.interfaces.IAttendeeDingService;
import com.paradisecloud.fcm.ding.service2.interfaces.IBusiDingConferenceService;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.ConferenceEndType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.UpCascadeType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.cascade.AbstractConference;
import com.paradisecloud.fcm.dao.mapper.BusiMcuDingTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuDingTemplateConference;
import com.sinhy.spring.BeanFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author nj
 * @date 2023/8/3 9:59
 */

@Component
public class DingCascadeConference extends AbstractConference {

    @Resource
    private IBusiDingConferenceService busiDingConferenceService;
    @Resource
    private IAttendeeDingService attendeeDingService;

    @Override
    public void startConference(McuType mcuType, Long templateId) {

        BusiMcuDingTemplateConferenceMapper busiDingTemplateConferenceMapper = BeanFactory.getBean(BusiMcuDingTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuDingTemplateConference tc = busiDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceById(templateId);
        if (tc == null) {
            return;
        }

        String contextKey = EncryptIdUtil.generateContextKey(templateId, mcuType);
        DingConferenceContext conferenceContext = DingConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            if (tc.getUpCascadeType() != null && UpCascadeType.AUTO_CREATE.getCode() != tc.getUpCascadeType() && UpCascadeType.SELECT_TEMPLATE_OUT_MEETING.getCode() != tc.getUpCascadeType()) {
                tc.setUpCascadeType(UpCascadeType.SELECT_TEMPLATE_OUT_MEETING.getCode());
                busiDingTemplateConferenceMapper.updateBusiMcuDingTemplateConference(tc);
            }
            String contextKeyT = busiDingConferenceService.startConference(templateId);
            if (StringUtils.isNotEmpty(contextKeyT)) {
                conferenceContext = DingConferenceContextCache.getInstance().get(contextKey);
            }
        } else {
            if (tc.getUpCascadeType() != null && UpCascadeType.AUTO_CREATE.getCode() != tc.getUpCascadeType() && UpCascadeType.SELECT_TEMPLATE_IN_MEETING.getCode() != tc.getUpCascadeType()) {
                tc.setUpCascadeType(UpCascadeType.SELECT_TEMPLATE_IN_MEETING.getCode());
                busiDingTemplateConferenceMapper.updateBusiMcuDingTemplateConference(tc);
            }
        }
        if (conferenceContext != null) {
            setConferenceId(conferenceContext.getId());
        }


    }

    @Override
    public void processCascade(String name, String uri, String ipProtocolType) {

    }

    @Override
    public void end() {
        String conferenceId = this.getConferenceId();
        busiDingConferenceService.endConference(conferenceId, ConferenceEndType.COMMON.getValue(), EndReasonsType.ADMINISTRATOR_HANGS_UP);
    }

    /**
     * 开始会议
     *
     * @param templateId
     */
    @Override
    public BaseConferenceContext startConference(Long templateId) {
        startConference(McuType.MCU_DING, templateId);
        String contextKey = EncryptIdUtil.generateContextKey(templateId, McuType.MCU_DING);
        DingConferenceContext conferenceContext = DingConferenceContextCache.getInstance().get(contextKey);
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
        BusiMcuDingTemplateConferenceMapper busiMcuDingTemplateConferenceMapper = BeanFactory.getBean(BusiMcuDingTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuDingTemplateConference tc = busiMcuDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceById(id);
        if (tc == null) {
            return null;
        }
        DingConferenceContext conferenceContext = DingConferenceContextCache.getInstance().get(contextKey);
        if (endType == ConferenceEndType.CASCADE.getValue() || tc.getUpCascadeType() == null || tc.getUpCascadeType() == UpCascadeType.AUTO_CREATE.getCode()) {
            busiDingConferenceService.endConference(contextKey, endType, true, false);
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


    }

    /**
     * 选看
     *
     * @param conferenceId
     * @param attendeeId
     */
    @Override
    public void chooseSee(String conferenceId, String attendeeId) {
        attendeeDingService.chooseSee(conferenceId, attendeeId);
    }

    @Override
    public void callTheRoll(String conferenceId, String attendeeId) {
        attendeeDingService.callTheRoll(conferenceId, attendeeId);
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
        attendeeDingService.chooseSee(conferenceId, attendeeId, upCascadeOperate, upCascadeBroadcast, upCascadePolling, upCascadeRollCall);
    }


}
