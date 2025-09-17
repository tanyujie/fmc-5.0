package com.paradisecloud.fcm.web.controller.business;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.IDefaultAttendeeOperationPackageService;
import com.paradisecloud.fcm.fme.conference.interfaces.ITemplateConferenceStartService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 活跃会议室信息，用于存放活跃的会议室Controller
 *
 * @author lilinhai
 * @date 2021-02-02
 */
//@RestController
@RequestMapping("/busi/conference")
@Tag(name = "活跃会议室信息，用于存放活跃的会议室")
public class BusiConferenceController extends BaseController
{
    @Autowired
    private IBusiConferenceService busiConferenceService;

    @Autowired
    private ITemplateConferenceStartService templateConferenceStartService;

    @Autowired
    private IDefaultAttendeeOperationPackageService defaultAttendeeOperationPackageService;

    @Autowired
    private IBusiTemplateConferenceService iBusiTemplateConferenceService;

    @GetMapping("/customLayoutTemplates/{deptId}")
    @Operation(summary = "所有布局集合")
    public RestResponse getLayoutTemplates(@PathVariable Long deptId)
    {
        return RestResponse.success(busiConferenceService.getLayoutTemplates(deptId));
    }

    @GetMapping("/customLayoutTemplate/{deptId}/{name}")
    @Operation(summary = "指定布局")
    public RestResponse getLayoutTemplate(@PathVariable Long deptId, @PathVariable String name)
    {
        return RestResponse.success(busiConferenceService.getLayoutTemplate(deptId, name));
    }

    /**
     * 新增活跃会议室信息，用于存放活跃的会议室
     */
    @PostMapping("/startByTemplate/{templateId}")
    @Operation(summary = "新增活跃会议室信息，用于存放活跃的会议室")
    public RestResponse startByTemplate(@PathVariable Long templateId)
    {
        String contextKey = templateConferenceStartService.startTemplateConference(templateId);
        String cnStr = contextKey == null ? null : AesEnsUtils.getAesEncryptor().encryptToHex(contextKey);
        return success(cnStr);
    }

    @PostMapping("/endConference/{conferenceId}/{endType}")
    @Operation(summary = "挂断会议")
    public RestResponse endConference(@PathVariable String conferenceId, @PathVariable int endType)
    {
        busiConferenceService.endConference(conferenceId, endType, EndReasonsType.ADMINISTRATOR_HANGS_UP);
        return success();
    }

    @PutMapping("/updateDefaultViewConfigInfo/{conferenceId}")
    @Operation(summary = "修改会议的默认视图，只更新内存")
    public RestResponse updateDefaultViewConfigInfo(@PathVariable String conferenceId, @RequestBody JSONObject jsonObj)
    {
        defaultAttendeeOperationPackageService.updateDefaultViewConfigInfo(conferenceId, jsonObj);
        return success();
    }

    @GetMapping("/defaultViewData/{conferenceId}")
    @Operation(summary = "显示布局数据")
    public RestResponse defaultViewData(@PathVariable String conferenceId)
    {
        return success(defaultAttendeeOperationPackageService.defaultViewData(conferenceId));
    }

    @PostMapping("/discuss/{conferenceId}")
    @Operation(summary = "会议讨论")
    public RestResponse discuss(@PathVariable String conferenceId)
    {
        busiConferenceService.discuss(conferenceId);
        return success();
    }

    @PostMapping("/cancelDiscuss/{conferenceId}")
    @Operation(summary = "取消会议讨论")
    public RestResponse cancelDiscuss(@PathVariable String conferenceId)
    {
        busiConferenceService.cancelDiscuss(conferenceId);
        return success();
    }

    @PutMapping("/extendMinutes/{conferenceId}/{minutes}")
    @Operation(summary = "延长会议时间，单位（分钟）")
    public RestResponse extendMinutes(@PathVariable String conferenceId, @PathVariable int minutes)
    {
        return success(busiConferenceService.extendMinutes(conferenceId, minutes));
    }

    @PutMapping("/lock/{conferenceId}/{locked}")
    @Operation(summary = "会议讨论")
    public RestResponse lock(@PathVariable String conferenceId, @PathVariable Boolean locked)
    {
        busiConferenceService.lock(conferenceId, locked);
        return success();
    }

    @PutMapping("/allowAllMuteSelf/{conferenceId}/{enabled}")
    @Operation(summary = "允许所有人静音自己")
    public RestResponse allowAllMuteSelf(@PathVariable String conferenceId, @PathVariable Boolean enabled)
    {
        busiConferenceService.allowAllMuteSelf(conferenceId, enabled);
        return success();
    }

    @PutMapping("/allowAllPresentationContribution/{conferenceId}/{enabled}")
    @Operation(summary = "允许辅流控制")
    public RestResponse allowAllPresentationContribution(@PathVariable String conferenceId, @PathVariable Boolean enabled)
    {
        busiConferenceService.allowAllPresentationContribution(conferenceId, enabled);
        return success();
    }

    @PutMapping("/joinAudioMuteOverride/{conferenceId}/{enabled}")
    @Operation(summary = "新加入用户静音")
    public RestResponse joinAudioMuteOverride(@PathVariable String conferenceId, @PathVariable Boolean enabled)
    {
        busiConferenceService.joinAudioMuteOverride(conferenceId, enabled);
        return success();
    }

    @PutMapping("/stream/{conferenceId}/{enabled}")
    @Operation(summary = "会议直播")
    public RestResponse stream(@PathVariable String conferenceId, @PathVariable Boolean enabled, @RequestBody JSONObject jsonObject)
    {
        Assert.isTrue(!enabled || enabled && !ObjectUtils.isEmpty(jsonObject.getString("streamingUrl")), "开启直播时，直播地址不能为空");
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().getByConferenceId(conferenceId);
        if (conferenceContext != null) {
            String streamingUrl = jsonObject.getString("streamingUrl");
            if (enabled) {
                if (StringUtils.hasText(conferenceContext.getStreamingRemoteParty())) {
                    if (!conferenceContext.getStreamingUrl().equals(streamingUrl)) {
                        return fail(1, "该会议过程中不允许变更直播地址！");
                    }
                }
            }
            busiConferenceService.stream(conferenceId, enabled, streamingUrl);
            return success();
        }
        return fail(1, "操作失败！");
    }

    @PostMapping("/sync/{conferenceId}")
    @Operation(summary = "一键同步")
    public RestResponse sync(@PathVariable String conferenceId)
    {
        busiConferenceService.sync(conferenceId);
        return success();
    }

    @PostMapping("/reCall/{conferenceId}")
    @Operation(summary = "一键呼入")
    public RestResponse reCall(@PathVariable String conferenceId) {
        busiConferenceService.reCall(conferenceId);
        return success();
    }

    @GetMapping("/layoutTemplates")
    @Operation(summary = "查询所有自定义布局模版")
    public RestResponse layoutTemplates(@RequestParam("deptId") Long deptId) {
        List<String> sortList = new ArrayList<>();

        List<Map<String, String>> resultLayoutTemplate = new ArrayList<>();
        Set<String> layoutTemplates = busiConferenceService.getLayoutTemplates(deptId);
        setSortList(sortList, layoutTemplates);
        convertMap(sortList, layoutTemplates, resultLayoutTemplate);
        return success(resultLayoutTemplate);
    }

    @PutMapping("/name/{conferenceId}/{templateId}/{name}")
    @Operation(summary = "会议名称修改")
    public RestResponse updateName(@PathVariable String conferenceId,@PathVariable Long templateId, @PathVariable String name)
    {
        iBusiTemplateConferenceService.updateBusiTemplateConferenceName(templateId,name);
        busiConferenceService.updateConferenceName(conferenceId, name);
        return success();
    }

    @GetMapping("/attendeeList/{conferenceId}")
    @Operation(summary = "根据Ip、域名、账号查询与会者列表")
    public RestResponse attendeeList(@PathVariable String conferenceId, @RequestParam("searchKey") String searchKey) {
        List<Attendee> attendeeList = busiConferenceService.attendeeList(conferenceId, searchKey);
        return RestResponse.success(attendeeList);
    }

    private void convertMap(List<String> sortList, Set<String> layoutTemplates, List<Map<String, String>> resultLayoutTemplate) {
        if (CollectionUtils.isEmpty(layoutTemplates)) {
            return;
        }
        if (CollectionUtils.isEmpty(sortList)) {
            for (String layoutTemplate : layoutTemplates) {
                packageLayout(resultLayoutTemplate, layoutTemplate);
            }
            return;
        }
        for (String layoutTemplate : sortList) {
            packageLayout(resultLayoutTemplate, layoutTemplate);
        }

    }

    private void packageLayout(List<Map<String, String>> resultLayoutTemplate, String layoutTemplate) {
        HashMap<String, String> obj = new HashMap<>(2);
        obj.put("name", layoutTemplate);
        obj.put("value", layoutTemplate);
        resultLayoutTemplate.add(obj);
    }

    private void setSortList(List<String> sortList, Set<String> layoutTemplates) {
        if (CollectionUtils.isEmpty(layoutTemplates)) {
            return;
        }
        List<String> collect = layoutTemplates.stream().collect(Collectors.toList());
        if (collect.stream().filter(p -> !matcherStr(p)).findAny().isPresent()) {
            return;
        }
        Map<String, List<String>> collect1 = collect.stream().collect(Collectors.groupingBy(s -> s.substring(0, s.lastIndexOf("+"))));
        List<String> collect2 = collect1.keySet().stream().sorted().collect(Collectors.toList());
        for (String s : collect2) {
            List<String> key1 = collect1.get(s);
            //key1进行第2次分组
            Map<String, List<String>> collect4 = key1.stream().collect(Collectors.groupingBy(k1 -> k1.substring(k1.lastIndexOf("+") + 1, k1.lastIndexOf("_"))));
            List<String> collect5 = collect4.keySet().stream().sorted(Comparator.comparingInt(m -> Integer.parseInt(m))).collect(Collectors.toList());
            for (String s2 : collect5) {
                //key2 进行第3次分组
                List<String> key2 = collect4.get(s2);
                Map<String, List<String>> collect7 = key2.stream().collect(Collectors.groupingBy(k1 -> k1.substring(k1.lastIndexOf("_") + 1)));
                List<String> collect8 = collect7.keySet().stream().sorted().collect(Collectors.toList());
                for (String s3 : collect8) {
                    List<String> key3 = collect7.get(s3);
                    for (String re : key3) {
                        sortList.add(re);
                    }
                }
            }
        }
    }

    private boolean matcherStr(String str) {
        String pattern = "^[1-9]+[+]+\\w+[A-Za-z]";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(str);
        return matcher.matches();
    }


}
