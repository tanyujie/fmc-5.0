package com.paradisecloud.fcm.web.controller.smc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.SmcConferenceTemplate;
import com.paradisecloud.com.fcm.smc.modle.StartConference;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.paradisecloud.smc.service.TemplateService;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author nj
 * @date 2022/8/15 9:35
 */
@RestController
@RequestMapping("/smc")
public class SmcController {

    @Resource
    private TemplateService templateService;


    /**
     * 创建模板会议
     */
    @PostMapping("/conferences/templates/create")
    public RestResponse addmeetingRooms(@RequestBody JSONObject jsonObj) {

        String type = jsonObj.getString("templateConferenceType");
        Assert.isTrue(type != null, "会议模板不能为空！");

        if (Objects.equals("smc", type)) {
            JSONObject smcConferenceTemplateRquestjson = jsonObj.getJSONObject("smcConferenceTemplateRquest");
            SmcConferenceTemplate smcConferenceTemplateRquest = smcConferenceTemplateRquestjson.toJavaObject(SmcConferenceTemplate.class);
            SmcConferenceTemplate smcConferenceTemplate = templateService.addTemplateRoom(smcConferenceTemplateRquest,null,smcConferenceTemplateRquest.getMasterTerminalId());
            return RestResponse.success(smcConferenceTemplate);
        }
        return RestResponse.success();


    }

    /**
     * 查询模板会议
     */
    @PostMapping("/conferences/templates/query")
    public RestResponse queryMeetingRooms(@RequestParam Long deptId) {
        String s = templateService.queryConferenceTemplates(null, deptId);
        return RestResponse.success(JSON.parseObject(s, Object.class));

    }

    /**
     * 删除模板会议
     */
    @DeleteMapping("/conferences/templates/{id}")
    public RestResponse deleteMeetingRooms(@PathVariable String id) {
        return RestResponse.success(templateService.deleteTemplateById(id));

    }

    /**
     * 修改模板会议
     */
    @PutMapping("/conferences/templates/{id}")
    public RestResponse updateMeetingRooms(@PathVariable String id,@RequestBody JSONObject jsonObj) {
        JSONObject smcConferenceTemplateRquestjson = jsonObj.getJSONObject("smcConferenceTemplateRquest");
        SmcConferenceTemplate smcConferenceTemplateRquest = smcConferenceTemplateRquestjson.toJavaObject(SmcConferenceTemplate.class);
        return RestResponse.success(templateService.putTemplate(smcConferenceTemplateRquest.getId(),smcConferenceTemplateRquest));

    }

    /**
     * 模板的 ID 找到会议模板
     * @return
     */
    @GetMapping("/conferences/templates/{id}")
   public RestResponse getTemplateInfo(@PathVariable String id){
        return RestResponse.success(JSON.parseObject(templateService.getTemplateById(id),Object.class));
    }

    /**
     * 开始模板会议
     *
     * @return
     */
    @PostMapping("/conferences/templates/{id}/start")
    public RestResponse startConferenceTemplate(@RequestBody StartConference startConference) {
        return RestResponse.success(JSON.parseObject(templateService.startConferenceTemplate(startConference), Object.class));
    }

    /**
     * 模板的 ID 找到会议模板
     *
     * @return
     */
    @GetMapping("/systemTime")
    public RestResponse getsystemTime() {
        return RestResponse.success();
//        SmcBridge smcBridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
//        if(smcBridge==null){
//            return RestResponse.success();
//        }
//        String systemtimezone = smcBridge.getSmcConferencesInvoker().getSystemtimezone(smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
//        return RestResponse.success(JSON.parseObject(systemtimezone, Object.class));
    }
}



