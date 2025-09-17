package com.paradisecloud.fcm.web.controller.smc3;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.common.enumer.BroadcastStatus;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.mcu.zj.attendee.model.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.mcu.zj.attendee.model.operation.DefaultAttendeeOperationForGuest;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjConferenceContextCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IDefaultAttendeeOperationPackageForMcuZjService;
import com.paradisecloud.smc3.service.interfaces.IBusiSmc3ConferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 活跃会议室信息，用于存放活跃的会议室Controller
 *
 * @author lilinhai
 * @date 2021-02-02
 */
@RestController
@RequestMapping("/busi/mcu/smc3/conference")
@Tag(name = "活跃会议室信息，用于存放活跃的会议室")
public class BusiSmc3ConferenceController extends BaseController
{
    @Resource
    private IBusiSmc3ConferenceService busiSmc3ConferenceService;
    @Resource
    private IDefaultAttendeeOperationPackageForMcuZjService defaultAttendeeOperationPackageForMcuZjService;

    @GetMapping("/customLayoutTemplates/{deptId}")
    @Operation(summary = "所有布局集合")
    public RestResponse getLayoutTemplates(@PathVariable Long deptId)
    {
        return RestResponse.success(busiSmc3ConferenceService.getLayoutTemplates(deptId));
    }

    @GetMapping("/customLayoutTemplate/{deptId}/{name}")
    @Operation(summary = "指定布局")
    public RestResponse getLayoutTemplate(@PathVariable Long deptId, @PathVariable String name)
    {
        return RestResponse.success(busiSmc3ConferenceService.getLayoutTemplate(deptId, name));
    }

    /**
     * 新增活跃会议室信息，用于存放活跃的会议室
     */
    @PostMapping("/startByTemplate/{templateId}")
    @Operation(summary = "新增活跃会议室信息，用于存放活跃的会议室")
    public RestResponse startByTemplate(@PathVariable Long templateId)
    {
        String cn = busiSmc3ConferenceService.startTemplateConference(templateId);
        String cnStr = cn == null ? null : AesEnsUtils.getAesEncryptor().encryptToHex(cn);
        return success(cnStr);
    }

    @PostMapping("/endConference/{conferenceId}/{endType}")
    @Operation(summary = "挂断会议")
    public RestResponse endConference(@PathVariable String conferenceId, @PathVariable int endType)
    {
        busiSmc3ConferenceService.endConference(conferenceId, endType);
        return success();
    }

    @PutMapping("/updateDefaultViewConfigInfo/{conferenceId}")
    @Operation(summary = "修改会议的默认视图，只更新内存")
    public RestResponse updateDefaultViewConfigInfo(@PathVariable String conferenceId, @RequestBody JSONObject jsonObj)
    {
        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        // 会议室上下文实例对象
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(conferenceNumber);
        if (conferenceContext != null) {
            if (conferenceContext.isSingleView()) {
                if (jsonObj.containsKey("guestDefaultViewData")) {
                    JSONObject jsonObjectGuest = jsonObj.getJSONObject("guestDefaultViewData");
                    defaultAttendeeOperationPackageForMcuZjService.updateDefaultViewConfigInfoForGuest(conferenceId, jsonObjectGuest);
                } else {
                    JSONObject jsonObjectSpeaker = jsonObj.getJSONObject("speakerDefaultViewData");
                    defaultAttendeeOperationPackageForMcuZjService.updateDefaultViewConfigInfo(conferenceId, jsonObjectSpeaker);
                }
            } else {
                JSONObject jsonObjectSpeaker = jsonObj.getJSONObject("speakerDefaultViewData");
                defaultAttendeeOperationPackageForMcuZjService.updateDefaultViewConfigInfo(conferenceId, jsonObjectSpeaker);
                AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
                if (attendeeOperation instanceof DefaultAttendeeOperation) {
                    DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) attendeeOperation;
                    if (defaultAttendeeOperation.getDefaultViewIsBroadcast() != BroadcastStatus.YES.getValue()) {
                        if (jsonObj.containsKey("guestDefaultViewData")) {
                            JSONObject jsonObjectGuest = jsonObj.getJSONObject("guestDefaultViewData");
                            defaultAttendeeOperationPackageForMcuZjService.updateDefaultViewConfigInfoForGuest(conferenceId, jsonObjectGuest);
                        } else {
                            if (!(conferenceContext.getDefaultViewOperationForGuest() instanceof DefaultAttendeeOperationForGuest)) {
                                conferenceContext.setAttendeeOperationForGuest(conferenceContext.getLastAttendeeOperationForGuest());
                            }
                        }
                    }
                }
            }
        }
        return success();
    }

    @GetMapping("/defaultViewData/{conferenceId}")
    @Operation(summary = "显示布局数据")
    public RestResponse defaultViewData(@PathVariable String conferenceId)
    {
        return success(defaultAttendeeOperationPackageForMcuZjService.defaultViewData(conferenceId));
    }

    @PostMapping("/discuss/{conferenceId}")
    @Operation(summary = "会议讨论")
    public RestResponse discuss(@PathVariable String conferenceId)
    {
        busiSmc3ConferenceService.discuss(conferenceId);
        return success();
    }

    @PostMapping("/cancelDiscuss/{conferenceId}")
    @Operation(summary = "取消会议讨论")
    public RestResponse cancelDiscuss(@PathVariable String conferenceId)
    {
        busiSmc3ConferenceService.cancelDiscuss(conferenceId);
        return success();
    }

    @PutMapping("/extendMinutes/{conferenceId}/{minutes}")
    @Operation(summary = "延长会议时间，单位（分钟）")
    public RestResponse extendMinutes(@PathVariable String conferenceId, @PathVariable int minutes)
    {
        return success(busiSmc3ConferenceService.extendMinutes(conferenceId, minutes));
    }

    @PutMapping("/lock/{conferenceId}/{locked}")
    @Operation(summary = "锁定会议")
    public RestResponse lock(@PathVariable String conferenceId, @PathVariable Boolean locked)
    {
        busiSmc3ConferenceService.lock(conferenceId, locked);
        return success();
    }

    /**
     * 全体一键静音
     * @param conferenceId
     */
    @PatchMapping("/mute/{conferenceId}/{mute}")
    public RestResponse changeParticipantsStatusMute(@PathVariable String conferenceId, @PathVariable Boolean mute){
        busiSmc3ConferenceService.setMute(conferenceId,mute);
        return  RestResponse.success();
    }



    @PutMapping("/allowAllMuteSelf/{conferenceId}/{enabled}")
    @Operation(summary = "允许所有人静音自己")
    public RestResponse allowAllMuteSelf(@PathVariable String conferenceId, @PathVariable Boolean enabled)
    {
        busiSmc3ConferenceService.allowAllMuteSelf(conferenceId, enabled);
        return success();
    }

    @PutMapping("/allowAllPresentationContribution/{conferenceId}/{enabled}")
    @Operation(summary = "允许辅流控制")
    public RestResponse allowAllPresentationContribution(@PathVariable String conferenceId, @PathVariable Boolean enabled)
    {
        busiSmc3ConferenceService.allowAllPresentationContribution(conferenceId, enabled);
        return success();
    }

    @PutMapping("/joinAudioMuteOverride/{conferenceId}/{enabled}")
    @Operation(summary = "新加入用户静音")
    public RestResponse joinAudioMuteOverride(@PathVariable String conferenceId, @PathVariable Boolean enabled)
    {
        busiSmc3ConferenceService.joinAudioMuteOverride(conferenceId, enabled);
        return success();
    }

    @PutMapping("/stream/{conferenceId}/{enabled}")
    @Operation(summary = "直播会议")
    public RestResponse stream(@PathVariable String conferenceId, @PathVariable Boolean enabled, @RequestBody JSONObject json)
    {
        Assert.isTrue(!enabled || (enabled && !ObjectUtils.isEmpty(json.getString("streamingUrl"))), "开启直播时，直播地址不能为空");
        busiSmc3ConferenceService.stream(conferenceId, enabled, json.getString("streamingUrl"));
        return success();
    }

    @PostMapping("/reCall/{conferenceId}")
    @Operation(summary = "一键呼入")
    public RestResponse reCall(@PathVariable String conferenceId) {
        busiSmc3ConferenceService.reCall(conferenceId);
        return success();
    }

    @PostMapping("/sync/{conferenceId}")
    @Operation(summary = "一键同步")
    public RestResponse sync(@PathVariable String conferenceId)
    {
        busiSmc3ConferenceService.sync(conferenceId);
        return success();
    }

    @GetMapping("/layoutTemplates")
    @Operation(summary = "查询所有自定义布局模版")
    public RestResponse layoutTemplates(@RequestParam("deptId") Long deptId) {
        List<Map<String, String>> layoutTemplates = busiSmc3ConferenceService.getLayoutTemplates(deptId);
        return success(layoutTemplates);
    }

    /**
     * 查询会议中直播终端数
     * @param conferenceId
     * @return
     */
    @Log(title = "查询会议中直播终端数")
    @GetMapping("/getLiveTerminalCount/{conferenceId}")
    @Operation(summary = "通过会议Id查询会议中直播终端数")
    public RestResponse getLiveTerminalCount(@PathVariable String conferenceId) {
        Integer count = busiSmc3ConferenceService.getLiveTerminalCount(conferenceId);
        Map<String, Integer> map = new HashMap<>();
        map.put("liveTerminalCount", count);
        return RestResponse.success(map);
    }


}
