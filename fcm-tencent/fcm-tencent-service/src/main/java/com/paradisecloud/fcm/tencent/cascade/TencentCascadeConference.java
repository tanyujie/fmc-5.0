package com.paradisecloud.fcm.tencent.cascade;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.ConferenceEndType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.UpCascadeType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.cascade.AbstractConference;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContextCache;
import com.paradisecloud.fcm.dao.mapper.BusiMcuTencentTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentTemplateConference;
import com.paradisecloud.fcm.tencent.service2.interfaces.IAttendeeTencentService;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiTencentConferenceService;
import com.sinhy.spring.BeanFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author nj
 * @date 2023/8/3 9:59
 */

@Component
public class TencentCascadeConference extends AbstractConference {

    @Resource
    private IBusiTencentConferenceService busiTencentConferenceService;
    @Resource
    private IAttendeeTencentService attendeeTencentService;

    @Override
    public void startConference(McuType mcuType, Long templateId) {

        BusiMcuTencentTemplateConferenceMapper busiTencentTemplateConferenceMapper = BeanFactory.getBean(BusiMcuTencentTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuTencentTemplateConference tc = busiTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceById(templateId);
        if (tc == null) {
            return;
        }

        String contextKey = EncryptIdUtil.generateContextKey(templateId, mcuType);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            if (tc.getUpCascadeType() != null && UpCascadeType.AUTO_CREATE.getCode() != tc.getUpCascadeType() && UpCascadeType.SELECT_TEMPLATE_OUT_MEETING.getCode() != tc.getUpCascadeType()) {
                tc.setUpCascadeType(UpCascadeType.SELECT_TEMPLATE_OUT_MEETING.getCode());
                busiTencentTemplateConferenceMapper.updateBusiMcuTencentTemplateConference(tc);
            }
            String contextKeyT = busiTencentConferenceService.startConference(templateId);
            if (StringUtils.isNotEmpty(contextKeyT)) {
                conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
            }
        } else {
            if (tc.getUpCascadeType() != null && UpCascadeType.AUTO_CREATE.getCode() != tc.getUpCascadeType() && UpCascadeType.SELECT_TEMPLATE_IN_MEETING.getCode() != tc.getUpCascadeType()) {
                tc.setUpCascadeType(UpCascadeType.SELECT_TEMPLATE_IN_MEETING.getCode());
                busiTencentTemplateConferenceMapper.updateBusiMcuTencentTemplateConference(tc);
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
        busiTencentConferenceService.endConference(conferenceId, ConferenceEndType.COMMON.getValue(), EndReasonsType.ADMINISTRATOR_HANGS_UP);
    }

    /**
     * 开始会议
     *
     * @param templateId
     */
    @Override
    public BaseConferenceContext startConference(Long templateId) {
        startConference(McuType.MCU_TENCENT, templateId);
        String contextKey = EncryptIdUtil.generateContextKey(templateId, McuType.MCU_TENCENT);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
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
        BusiMcuTencentTemplateConferenceMapper busiMcuTencentTemplateConferenceMapper = BeanFactory.getBean(BusiMcuTencentTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuTencentTemplateConference tc = busiMcuTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceById(id);
        if (tc == null) {
            return null;
        }
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (endType == ConferenceEndType.CASCADE.getValue() || tc.getUpCascadeType() == null || tc.getUpCascadeType() == UpCascadeType.AUTO_CREATE.getCode()) {
            busiTencentConferenceService.endConference(contextKey, endType, true, false);
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
        attendeeTencentService.chooseSee(conferenceId, attendeeId);
    }

    @Override
    public void callTheRoll(String conferenceId, String attendeeId) {
        attendeeTencentService.callTheRoll(conferenceId, attendeeId);
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
        attendeeTencentService.chooseSee(conferenceId, attendeeId, upCascadeOperate, upCascadeBroadcast, upCascadePolling, upCascadeRollCall);
    }


}
