package com.paradisecloud.smc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.ConstAPI;
import com.paradisecloud.com.fcm.smc.modle.SmcConferenceTemplate;
import com.paradisecloud.com.fcm.smc.modle.StartConference;
import com.paradisecloud.com.fcm.smc.modle.response.SmcErrorResponse;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.paradisecloud.smc.dao.model.BusiSmcDeptTemplate;
import com.paradisecloud.smc.dao.model.SmcTemplateTerminal;
import com.paradisecloud.smc.service.BusiSmcDeptTemplateService;
import com.paradisecloud.smc.service.SmcTemplateTerminalService;
import com.paradisecloud.smc.service.TemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * @author nj
 * @date 2022/8/15 10:38
 */
@Service
@Transactional
public class TemplateServiceImpl implements TemplateService {

    public static final String ERROR_NO = "errorNo";
    @Resource
    private BusiSmcDeptTemplateService busiSmcDeptTemplateService;
    @Resource
    private SmcTemplateTerminalService smcTemplateTerminalService;




    @Override
    public String queryConferenceTemplates(String name, Long deptId) {
        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(deptId);
        String s = bridge.getSmcConferencesTemplateInvoker().queryConferencesTemplate(name, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        return s;
    }

    @Override
    public String deleteTemplateById(String id) {
        BusiSmcDeptTemplate busiSmcDeptTemplate = busiSmcDeptTemplateService.queryTemplate(id);
        if (Objects.isNull(busiSmcDeptTemplate)) {
            return null;
        }
        SmcBridge bridge;
        if (Objects.isNull(busiSmcDeptTemplate)) {
            bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
        } else {
            bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(busiSmcDeptTemplate.getDeptId());
        }
        busiSmcDeptTemplateService.delete(busiSmcDeptTemplate.getId());
        smcTemplateTerminalService.deleteBytemplateId(id);
        String s = bridge.getSmcConferencesTemplateInvoker().deleteConferencesTemplate(id, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        return s;
    }

    @Override
    public String putTemplate(String id, SmcConferenceTemplate smcConferenceTemplate) {

        BusiSmcDeptTemplate busiSmcDeptTemplate = busiSmcDeptTemplateService.queryTemplate(id);
        SmcBridge bridge;
        if (Objects.isNull(busiSmcDeptTemplate)) {
            bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
        } else {
            bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(busiSmcDeptTemplate.getDeptId());
        }
        String jsonString = JSONObject.toJSONString(smcConferenceTemplate);
        String s = bridge.getSmcConferencesTemplateInvoker().putConferencesTemplate(id, jsonString, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        return s;
    }

    @Override
    public String getTemplateById(String id) {
        BusiSmcDeptTemplate busiSmcDeptTemplate = busiSmcDeptTemplateService.queryTemplate(id);
        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(busiSmcDeptTemplate.getDeptId());
        String s = bridge.getSmcConferencesTemplateInvoker().getConferencesTemplateById(id,bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        return s;
    }

    @Override
    public String startConferenceTemplate(StartConference startConference) {
        BusiSmcDeptTemplate busiSmcDeptTemplate = busiSmcDeptTemplateService.queryTemplate(startConference.getId());
        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(busiSmcDeptTemplate.getDeptId());
        String res= bridge.getSmcConferencesTemplateInvoker().startConferencesTemplateById(startConference.getId(),JSONObject.toJSONString(startConference),bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        if (res != null&& res.contains(ERROR_NO)) {
            SmcErrorResponse smcErrorResponse = JSON.parseObject(res, SmcErrorResponse.class);
            throw new CustomException("开始模板会议:" + smcErrorResponse.getErrorDesc());
        }
        return res;
    }

    @Override
    public SmcConferenceTemplate addTemplateRoom(SmcConferenceTemplate smcConferenceTemplate,Long deptId,long masterTerminalId) {
        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(deptId);
        String jsonString = JSONObject.toJSONString(smcConferenceTemplate);
        String result = bridge.getSmcConferencesTemplateInvoker().creatConferencesTemplate(jsonString, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        //{"state":104, "message": "token not exist"}
        if(result!=null&&result.contains(ConstAPI.TOKEN_NOT_EXIST)){
            bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders();
        }
        // {"errorType": "SMC", "errorNo": "0x20020004", "errorDesc": "PARTICIPANT_URI_DUPLICATE"}
        if (result != null&&result.contains(ERROR_NO)) {
            SmcErrorResponse smcErrorResponse = JSON.parseObject(result, SmcErrorResponse.class);

            throw new CustomException("新增模板失败:" + smcErrorResponse.getErrorDesc());
        }
        SmcConferenceTemplate smcConferenceTemplate1 = JSON.parseObject(result, SmcConferenceTemplate.class);
        if (Objects.isNull(smcConferenceTemplate1)) {
            throw new CustomException("创建模板会议失败");
        }
        String template1Id = smcConferenceTemplate1.getId();
        BusiSmcDeptTemplate busiSmcDeptTemplate = new BusiSmcDeptTemplate();
        busiSmcDeptTemplate.setDeptId(deptId);
        busiSmcDeptTemplate.setSmcTemplateId(template1Id);
        busiSmcDeptTemplate.setCreateTime(new Date());
        busiSmcDeptTemplate.setTemplateName(smcConferenceTemplate.getSubject());
        busiSmcDeptTemplate.setDuration(smcConferenceTemplate1.getDuration());
        busiSmcDeptTemplate.setType(smcConferenceTemplate.getConferenceCapabilitySetting().getType());

        busiSmcDeptTemplate.setAmcRecord(smcConferenceTemplate.getStreamService().getAmcRecord()==null?2:(smcConferenceTemplate.getStreamService().getAmcRecord()==true?1:2));
        busiSmcDeptTemplate.setSupportLive(smcConferenceTemplate.getStreamService().getSupportLive()==null?2:(smcConferenceTemplate.getStreamService().getSupportLive()==true?1:2));
        busiSmcDeptTemplate.setSupportRecord(smcConferenceTemplate.getStreamService().getSupportRecord()==null?2:(smcConferenceTemplate.getStreamService().getSupportRecord()==true?1:2));

        busiSmcDeptTemplate.setVmrNumber(smcConferenceTemplate.getVmrNumber());
        busiSmcDeptTemplate.setChairmanPassword(smcConferenceTemplate.getChairmanPassword());
        busiSmcDeptTemplate.setGuestPassword(smcConferenceTemplate.getGuestPassword());
        busiSmcDeptTemplate.setRate(smcConferenceTemplate.getConferenceCapabilitySetting().getRate());
        busiSmcDeptTemplate.setMaxParticipantNum(smcConferenceTemplate.getConferencePolicySetting().getMaxParticipantNum());
        busiSmcDeptTemplate.setAutoMute(smcConferenceTemplate.getConferencePolicySetting().getAutoMute()==null?2:(smcConferenceTemplate.getConferencePolicySetting().getAutoMute()==true?1:2));
        busiSmcDeptTemplate.setEnableDataConf(smcConferenceTemplate.getConferenceCapabilitySetting().getEnableDataConf()==null?2:(smcConferenceTemplate.getConferenceCapabilitySetting().getEnableDataConf()==true?1:2));
        busiSmcDeptTemplate.setMaxParticipantNum(smcConferenceTemplate.getConferencePolicySetting().getMaxParticipantNum());
        busiSmcDeptTemplate.setMasterTerminalId(masterTerminalId);
        busiSmcDeptTemplateService.add(busiSmcDeptTemplate);
        return smcConferenceTemplate1;
    }

    @Override
    public SmcConferenceTemplate addTemplateRoomSmc(SmcConferenceTemplate smcConferenceTemplate,Long deptId) {
        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(deptId);
        String jsonString = JSONObject.toJSONString(smcConferenceTemplate);
        String result = bridge.getSmcConferencesTemplateInvoker().creatConferencesTemplate(jsonString, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        //{"state":104, "message": "token not exist"}
        if(result!=null&&result.contains(ConstAPI.TOKEN_NOT_EXIST)){
            bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders();
        }
        // {"errorType": "SMC", "errorNo": "0x20020004", "errorDesc": "PARTICIPANT_URI_DUPLICATE"}
        if (result != null&&result.contains(ERROR_NO)) {
            SmcErrorResponse smcErrorResponse = JSON.parseObject(result, SmcErrorResponse.class);

            throw new CustomException("新增模板失败:" + smcErrorResponse.getErrorDesc());
        }
        SmcConferenceTemplate smcConferenceTemplate1 = JSON.parseObject(result, SmcConferenceTemplate.class);
        if (Objects.isNull(smcConferenceTemplate1)) {
            throw new CustomException("创建模板会议失败");
        }
        return smcConferenceTemplate1;
    }
}
