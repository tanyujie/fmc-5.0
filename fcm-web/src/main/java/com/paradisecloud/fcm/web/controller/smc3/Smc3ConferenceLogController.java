package com.paradisecloud.fcm.web.controller.smc3;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.ConferenceTemplateCreateType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc3TemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplateConference;
import com.paradisecloud.fcm.mcu.plc.cache.utils.AesEnsUtils;
import com.paradisecloud.smc3.busi.ConferenceNode;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.model.ParticipantStatus;
import com.paradisecloud.smc3.model.mix.ConferenceControllerRequest;
import com.paradisecloud.smc3.model.request.SmcConferenceRequest;
import com.paradisecloud.smc3.model.response.LogsConferenceRep;
import com.paradisecloud.smc3.service.interfaces.IBusiMcuSmc3TemplateConferenceService;
import com.paradisecloud.smc3.service.interfaces.IBusiSmc3ConferenceService;
import com.paradisecloud.smc3.utils.UTCTimeFormatUtil;
import com.sinhy.utils.DateUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.Asserts;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author nj
 * @date 2022/8/16 15:32
 */
@Slf4j
@RestController
@RequestMapping("/smc3/conference")
public class Smc3ConferenceLogController {

    @Resource
    private IBusiSmc3ConferenceService iBusiSmc3ConferenceService;

    @Resource
    private IBusiMcuSmc3TemplateConferenceService busiMcuSmc3TemplateConferenceService;

    /**
     * 模板的 ID 找到会议模板
     *
     * @return
     */
    @GetMapping("/systemTime")
    public RestResponse getsystemTime() {
        Smc3Bridge smcBridge = Smc3BridgeCache.getInstance().getBridgesByDept(null);
        if(smcBridge==null){
            return RestResponse.success();
        }
        String systemtimezone = smcBridge.getSmcConferencesInvoker().getSystemtimezone(smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        return RestResponse.success(JSON.parseObject(systemtimezone, Object.class));
    }

    @GetMapping("/logs/search/findAllByConfId")
    public RestResponse  listLog(SmcConferenceRequest smcConferenceRequest) {
        try {
            LogsConferenceRep logsConferenceRep = iBusiSmc3ConferenceService.listLog(smcConferenceRequest);
            if (logsConferenceRep != null) {
                List<LogsConferenceRep.ContentDTO> content = logsConferenceRep.getContent();
                if (!CollectionUtils.isEmpty(content)) {
                    for (LogsConferenceRep.ContentDTO contentDTO : content) {
                        LogsConferenceRep.ContentDTO.OthersDTO others = contentDTO.getOthers();

                    }
                }
            }
            return RestResponse.success(logsConferenceRep);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RestResponse.success(null);
    }



    @GetMapping("/logs/export")
    public RestResponse  exportLog(SmcConferenceRequest smcConferenceRequest, HttpServletResponse response){
        convertToUtc(smcConferenceRequest);
        iBusiSmc3ConferenceService.downloadLog(smcConferenceRequest,response);
        return  RestResponse.success();
    }


    @GetMapping("/logs/search/findAllByConfIdpage")
    public RestResponse  listLogPage(SmcConferenceRequest smcConferenceRequest) {
        try {
            convertToUtc(smcConferenceRequest);
            Object obj = iBusiSmc3ConferenceService.httpGetListString(smcConferenceRequest);

            return RestResponse.success(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RestResponse.success(null);
    }


    /**
     * 锁定会议材料(取消)
     */
    @PatchMapping("/lockPresenter/{conferenceId}/{lock}")
    @Operation(summary = "锁定会议材料")
    public RestResponse lockPresenter(@PathVariable String conferenceId, @PathVariable Boolean lock) {
        iBusiSmc3ConferenceService.lockPresenter(conferenceId,lock);
        return RestResponse.success();
    }

    /**
     * 打开、关闭声控
     *
     * @param conferenceId
     */
    @PatchMapping("/voiceActive/{conferenceId}/{enable}")
    @Operation(summary = "打开、关闭声控")
    public RestResponse isVoiceActive(@PathVariable String conferenceId,@PathVariable Boolean enable) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isVoiceActive",enable);
        iBusiSmc3ConferenceService.voiceActive(conferenceId,jsonObject);
        return RestResponse.success();
    }




    /**
     * 预设画面查询
     *
     * @param conferenceId
     */
    @GetMapping("/presetParam/{conferenceId}")
    @Operation(summary = "预设画面查询")
    public RestResponse presetParam(@PathVariable String conferenceId) {

        Asserts.notNull(conferenceId, "请求参数,会议ID");
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        Asserts.notNull(conferenceContext, "会议不存在");
        return RestResponse.success(conferenceContext.getConfPresetParamDTO());
    }

    /**
     * 导播模式/自由模式
     * @param conferenceId
     */
    @PatchMapping("/directMode/{conferenceId}/{enable}")
    public RestResponse enableDIRECT_MODE(@PathVariable String conferenceId,@PathVariable boolean enable){
        JSONObject jsonObject=new JSONObject();
        if(enable){
            jsonObject.put("mode","DIRECT_MODE");
        }else {
            jsonObject.put("mode","FREE_MODE");
        }

        iBusiSmc3ConferenceService.setStatus(conferenceId,jsonObject);
        return  RestResponse.success();
    }


    private void convertToUtc(SmcConferenceRequest smcConferenceRequest) {
        String startTime = smcConferenceRequest.getStartTime();
        String endTime = smcConferenceRequest.getEndTime();
        if(!isValidDate(startTime)){
            startTime= DateUtils.formatTo("yyyy-MM-dd HH:mm:ss",new Date());
        }
        smcConferenceRequest.setStartTime(UTCTimeFormatUtil.convertToUtc(startTime));
        smcConferenceRequest.setEndTime( UTCTimeFormatUtil.convertToUtc(endTime));
    }

    public static boolean isValidDate(String str) {
        boolean convertSuccess=true;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            convertSuccess=false;
        }
        return convertSuccess;
    }



    /**
     * 允许非主席会场打开麦克风
     */
    @PatchMapping("/enableUnmuteByGuest/{conferenceId}/{enable}")
    @Operation(summary = "会场字体设置")
    public RestResponse enableUnmuteByGuest(@PathVariable String conferenceId,@PathVariable Boolean enable) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enableUnmuteByGuest",enable);
        iBusiSmc3ConferenceService.setStatus(conferenceId,jsonObject);
        return RestResponse.success();
    }


    /**
     * 最大人数设置
     * @param conferenceId
     */
    @PatchMapping("/maxParticipantNum/{conferenceId}/{number}")
    public RestResponse changemaxParticipantNum(@PathVariable String conferenceId,@PathVariable int number){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("maxParticipantNum",number);
        iBusiSmc3ConferenceService.setStatus(conferenceId,jsonObject);
        return  RestResponse.success();
    }

    /**
     * 打开(true)、关闭 非主席名称修改
     * @param conferenceId
     */
    @PatchMapping("/enableSiteNameEditByGuest/{conferenceId}/{enable}")
    public RestResponse enableSiteNameEditByGuest(@PathVariable String conferenceId,@PathVariable boolean enable){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("enableSiteNameEditByGuest",enable);
        iBusiSmc3ConferenceService.setStatus(conferenceId,jsonObject);
        return  RestResponse.success();
    }

    /**
     * 锁定视频源
     *
     * @param conferenceControllerRequest
     */
    @PatchMapping("/videoSwitchAttribute/lock")
    @Operation(summary = "锁定视频源")
    public RestResponse videoSwitchAttribute(@RequestBody ConferenceControllerRequest conferenceControllerRequest) {
        String participantId = conferenceControllerRequest.getParticipantId();
        String conferenceId = conferenceControllerRequest.getConferenceId();
        ParticipantStatus participantStatus=new ParticipantStatus();
        participantStatus.setVideoSwitchAttribute("CUSTOMIZED");
        iBusiSmc3ConferenceService.changeParticipantStatusOnly(conferenceId,participantId,participantStatus);

        return RestResponse.success();
    }

    /**
     * 解锁视频源
     *
     * @param conferenceControllerRequest
     */
    @PatchMapping("/videoSwitchAttribute/unLock")
    @Operation(summary = "解锁视频源")
    public RestResponse videoSwitchAttributeAUTO(@RequestBody ConferenceControllerRequest conferenceControllerRequest) {
        String participantId = conferenceControllerRequest.getParticipantId();
        String conferenceId = conferenceControllerRequest.getConferenceId();

        ParticipantStatus participantStatus=new ParticipantStatus();
        participantStatus.setVideoSwitchAttribute("AUTO");
        iBusiSmc3ConferenceService.changeParticipantStatusOnly(conferenceId,participantId,participantStatus);
        return RestResponse.success();
    }


    /**
     * 音量设置
     *
     * @param conferenceControllerRequest
     */
    @PatchMapping("/volume")
    @Operation(summary = "音量设置")
    public RestResponse volume(@RequestBody ConferenceControllerRequest conferenceControllerRequest) {
        String participantId = conferenceControllerRequest.getParticipantId();
        String conferenceId = conferenceControllerRequest.getConferenceId();
        int volume = conferenceControllerRequest.getVolume();
        ParticipantStatus participantStatus=new ParticipantStatus();
        participantStatus.setVolume(volume);
        iBusiSmc3ConferenceService.changeParticipantStatusOnly(conferenceId,participantId,participantStatus);
        return RestResponse.success();
    }


    /**
     * 锁定会议材料(取消)
     */
    @PatchMapping("/lockPresenter/{conferenceId}/{participantId}/{lock}")
    @Operation(summary = "锁定会议材料")
    public RestResponse lockPresenter(@PathVariable String conferenceId,@PathVariable String participantId,@PathVariable Boolean lock) {
        iBusiSmc3ConferenceService.lockPresenterParticipant(conferenceId,participantId,lock);
        return RestResponse.success();
    }



    /**
     * 查询多级会议模板列表
     */
    @GetMapping("/cascade/list")
    @Operation(summary = "查询会议模板列表")
    public RestResponse list(@RequestParam("deptId") Long  deptId )
    {
        List<BusiMcuSmc3TemplateConference> list = busiMcuSmc3TemplateConferenceService.selectCascadeConferenceList(deptId);
        if(!CollectionUtils.isEmpty(list)){
            List<ModelBean> modelBeans = new ArrayList<>();
            for (BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference : list) {
                ModelBean modelBean = new ModelBean();
                modelBean.put("category", busiMcuSmc3TemplateConference.getCategory());
                modelBean.put("templateId", busiMcuSmc3TemplateConference.getId());
                modelBean.put("smcTemplateId", busiMcuSmc3TemplateConference.getSmcTemplateId());
                modelBean.put("name", busiMcuSmc3TemplateConference.getName());
                modelBeans.add(modelBean);
            }
            return RestResponse.success(0, "查询成功", modelBeans);
        }
        return RestResponse.success(0, "查询成功", list);
    }



    /**
     * 查询本地多级会与会者
     */
    @GetMapping("/getCascadeParticipants/{conferenceId}/{notDisplay}")
    @Operation(summary = "查询本地多级会与会者")
    public RestResponse getCascadeParticipants(@PathVariable String conferenceId,@PathVariable Boolean notDisplay) {
        List<AttendeeSmc3> attendeeSmc3s = new ArrayList<>();

        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        Smc3ConferenceContext smc3ConferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if(!notDisplay){
            List<ConferenceNode> cascadeConferenceTree = smc3ConferenceContext.getCascadeConferenceTree();
            for (ConferenceNode conferenceNode : cascadeConferenceTree) {
                if(!Objects.equals(conferenceNode.getConferenceId(),conferenceId)){
                    String contextKey_S = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceNode.getConferenceId());
                    Smc3ConferenceContext smc3ConferenceContext_S = Smc3ConferenceContextCache.getInstance().get(contextKey_S);
                    List<AttendeeSmc3> attendees_s = smc3ConferenceContext_S.getAttendees();
                    attendeeSmc3s.addAll(attendees_s);
                }
            }
        }
        smc3ConferenceContext.setDisplayAttendees(attendeeSmc3s);
        return RestResponse.success(smc3ConferenceContext);
    }


    /**
     * 查询本地多级会与会者
     */
    @GetMapping("/tree/{conferenceId}")
    @Operation(summary = "查询本地多级会与会者")
    public RestResponse getCascadeTree(@PathVariable String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        if(!Objects.equals(McuType.SMC3,mcuType)){
            return RestResponse.success();
        }
        Smc3ConferenceContext smc3ConferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if(smc3ConferenceContext==null){
            Long id = conferenceIdVo.getId();
            return RestResponse.success(iBusiSmc3ConferenceService.buildTemplateConferenceContext(id).getCascadeConferenceTree());
        }
        return RestResponse.success( smc3ConferenceContext.getCascadeConferenceTree());
    }

}
