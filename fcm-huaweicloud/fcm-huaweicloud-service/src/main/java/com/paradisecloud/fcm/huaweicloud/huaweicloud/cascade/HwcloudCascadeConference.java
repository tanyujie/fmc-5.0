package com.paradisecloud.fcm.huaweicloud.huaweicloud.cascade;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.ConferenceEndType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.UpCascadeType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContextCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IAttendeeHwcloudService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiHwcloudConferenceService;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.cascade.AbstractConference;
import com.paradisecloud.fcm.dao.mapper.BusiMcuHwcloudTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudTemplateConference;
import com.sinhy.spring.BeanFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author nj
 * @date 2023/8/3 9:59
 */

@Component
public class HwcloudCascadeConference extends AbstractConference {

    @Resource
    private IBusiHwcloudConferenceService busiHwcloudConferenceService;
    @Resource
    private IAttendeeHwcloudService attendeeHwcloudService;

    @Override
    public void startConference(McuType mcuType, Long templateId) {

        BusiMcuHwcloudTemplateConferenceMapper busiHwcloudTemplateConferenceMapper = BeanFactory.getBean(BusiMcuHwcloudTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuHwcloudTemplateConference tc = busiHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(templateId);
        if (tc == null) {
            return;
        }

        String contextKey = EncryptIdUtil.generateContextKey(templateId, mcuType);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            if (tc.getUpCascadeType() != null && UpCascadeType.AUTO_CREATE.getCode() != tc.getUpCascadeType() && UpCascadeType.SELECT_TEMPLATE_OUT_MEETING.getCode() != tc.getUpCascadeType()) {
                tc.setUpCascadeType(UpCascadeType.SELECT_TEMPLATE_OUT_MEETING.getCode());
                busiHwcloudTemplateConferenceMapper.updateBusiMcuHwcloudTemplateConference(tc);
            }
            String contextKeyT = busiHwcloudConferenceService.startConference(templateId);
            if (StringUtils.isNotEmpty(contextKeyT)) {
                conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
            }
        } else {
            if (tc.getUpCascadeType() != null && UpCascadeType.AUTO_CREATE.getCode() != tc.getUpCascadeType() && UpCascadeType.SELECT_TEMPLATE_IN_MEETING.getCode() != tc.getUpCascadeType()) {
                tc.setUpCascadeType(UpCascadeType.SELECT_TEMPLATE_IN_MEETING.getCode());
                busiHwcloudTemplateConferenceMapper.updateBusiMcuHwcloudTemplateConference(tc);
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
        busiHwcloudConferenceService.endConference(conferenceId, ConferenceEndType.COMMON.getValue(), EndReasonsType.ADMINISTRATOR_HANGS_UP);
    }

    /**
     * 开始会议
     *
     * @param templateId
     */
    @Override
    public BaseConferenceContext startConference(Long templateId) {
        startConference(McuType.MCU_HWCLOUD, templateId);
        String contextKey = EncryptIdUtil.generateContextKey(templateId, McuType.MCU_HWCLOUD);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
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
        BusiMcuHwcloudTemplateConferenceMapper busiMcuHwcloudTemplateConferenceMapper = BeanFactory.getBean(BusiMcuHwcloudTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuHwcloudTemplateConference tc = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(id);
        if (tc == null) {
            return null;
        }
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        if (endType == ConferenceEndType.CASCADE.getValue() || tc.getUpCascadeType() == null || tc.getUpCascadeType() == UpCascadeType.AUTO_CREATE.getCode()) {
            busiHwcloudConferenceService.endConference(contextKey, endType, true, false);
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
        attendeeHwcloudService.chooseSee(conferenceId, attendeeId);
    }

    @Override
    public void callTheRoll(String conferenceId, String attendeeId) {
        attendeeHwcloudService.callTheRoll(conferenceId, attendeeId);
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
        attendeeHwcloudService.chooseSee(conferenceId, attendeeId, upCascadeOperate, upCascadeBroadcast, upCascadePolling, upCascadeRollCall);
    }


}
