/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeController.java
 * Package     : com.paradisecloud.fcm.web.controller.business
 * @author lilinhai 
 * @since 2021-02-05 17:35
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.web.controller.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.model.busi.attendee.FixedParamValue;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**  
 * <pre>参会者控制器</pre>
 * @author lilinhai
 * @since 2021-02-05 17:35
 * @version V1.0  
 */
//@RestController
@RequestMapping("/busi/attendee")
@Tag(name = "参会者控制器")
public class AttendeeController extends BaseController
{
    
    @Autowired
    private IAttendeeService attendeeService;
    
    /**
     * 参会者详情
     */
    @PostMapping("/detail/{conferenceId}/{attendeeId}")
    @Operation(summary = "参会者详情")
    public RestResponse detail(@PathVariable String conferenceId, @PathVariable String attendeeId)
    {
        return success(attendeeService.detail(conferenceId, attendeeId));
    }
    
    /**
     * 批量获取参会者详情
     */
    @PostMapping("/details/{conferenceId}")
    @Operation(summary = "参会者详情")
    public RestResponse details(@PathVariable String conferenceId, @RequestBody List<String> attendeeIds)
    {
        JSONArray ja = new JSONArray();
        for (String attendeeId : attendeeIds)
        {
            ja.add(attendeeService.detail(conferenceId, attendeeId));
        }
        return success(ja);
    }
    
    /**
     * 参会者页面上重呼
     */
    @PostMapping("/recall/{conferenceId}/{attendeeId}")
    @Operation(summary = "参会者页面上重呼")
    public RestResponse recall(@PathVariable String conferenceId, @PathVariable String attendeeId)
    {
        attendeeService.recall(conferenceId, attendeeId);
        return success();
    }
    
    /**
     * 参会者页面上挂断
     */
    @PostMapping("/hangUp/{conferenceId}/{attendeeId}")
    @Operation(summary = "参会者页面上重呼")
    public RestResponse hangUp(@PathVariable String conferenceId, @PathVariable String attendeeId)
    {
        attendeeService.hangUp(conferenceId, attendeeId);
        return success();
    }
    
    /**
     * 参会者页面上移除
     */
    @DeleteMapping("/remove/{conferenceId}/{attendeeId}")
    @Operation(summary = "参会者页面上移除")
    public RestResponse remove(@PathVariable String conferenceId, @PathVariable String attendeeId)
    {
        attendeeService.remove(conferenceId, attendeeId);
        return success();
    }
    
    /**
     * 主会场变更
     */
    @PostMapping("/changeMaster/{conferenceId}/{attendeeId}")
    @Operation(summary = "主会场变更")
    public RestResponse changeMaster(@PathVariable String conferenceId, @PathVariable String attendeeId)
    {
        attendeeService.changeMaster(conferenceId, attendeeId);
        return success();
    }
    
    /**
     * 选看
     */
    @PostMapping("/chooseSee/{conferenceId}/{attendeeId}")
    @Operation(summary = "选看")
    public RestResponse chooseSee(@PathVariable String conferenceId, @PathVariable String attendeeId)
    {
        attendeeService.chooseSee(conferenceId, attendeeId);
        return success();
    }
    
    /**
     * 点名
     */
    @PostMapping("/callTheRoll/{conferenceId}/{attendeeId}")
    @Operation(summary = "点名")
    public RestResponse callTheRoll(@PathVariable String conferenceId, @PathVariable String attendeeId)
    {
        attendeeService.callTheRoll(conferenceId, attendeeId);
        return success();
    }
    
    /**
     * 对话
     */
    @PostMapping("/talk/{conferenceId}/{attendeeId}")
    @Operation(summary = "对话")
    public RestResponse talk(@PathVariable String conferenceId, @PathVariable String attendeeId)
    {
        attendeeService.talk(conferenceId, attendeeId);
        return success();
    }
    
    /**
     * cancelCallTheRoll点名
     */
    @PostMapping("/cancelCallTheRoll/{conferenceId}")
    @Operation(summary = "点名")
    public RestResponse cancelCallTheRoll(@PathVariable String conferenceId)
    {
        attendeeService.cancelCallTheRoll(conferenceId);
        return success();
    }
    
    /**
     * 开启混音
     */
    @PostMapping("/openMixing/{conferenceId}/{attendeeId}")
    @Operation(summary = "开启混音")
    public RestResponse openMixing(@PathVariable String conferenceId, @PathVariable String attendeeId)
    {
        attendeeService.openMixing(conferenceId, attendeeId);
        return success();
    }
    
    /**
     * 关闭混音
     */
    @PostMapping("/closeMixing/{conferenceId}/{attendeeId}")
    @Operation(summary = "关闭混音")
    public RestResponse closeMixing(@PathVariable String conferenceId, @PathVariable String attendeeId)
    {
        attendeeService.closeMixing(conferenceId, attendeeId);
        return success();
    }
    
    /**
     * 开启混音
     */
    @PostMapping("/openMixing/{conferenceId}")
    @Operation(summary = "开启混音")
    public RestResponse openMixing(@PathVariable String conferenceId)
    {
        attendeeService.openMixing(conferenceId);
        return success();
    }
    
    /**
     * 关闭混音
     */
    @PostMapping("/closeMixing/{conferenceId}")
    @Operation(summary = "关闭混音")
    public RestResponse closeMixing(@PathVariable String conferenceId)
    {
        attendeeService.closeMixing(conferenceId);
        return success();
    }
    
    @PostMapping("/acceptRaiseHand/{conferenceId}/{attendeeId}")
    @Operation(summary = "拒绝举手")
    public RestResponse acceptRaiseHand(@PathVariable String conferenceId, @PathVariable String attendeeId)
    {
        attendeeService.acceptRaiseHand(conferenceId, attendeeId);
        return success();
    }
    
    /**
     * 拒绝举手
     * @author sinhy
     * @since 2021-12-07 10:27 
     * @param conferenceId
     * @param attendeeId
     * @param raiseHandStatus void
     */
    @PostMapping("/rejectRaiseHand/{conferenceId}/{attendeeId}")
    @Operation(summary = "拒绝举手")
    public RestResponse rejectRaiseHand(@PathVariable String conferenceId, @PathVariable String attendeeId)
    {
        attendeeService.rejectRaiseHand(conferenceId, attendeeId);
        return success();
    }
    
    /**
     * 开启镜头
     */
    @PostMapping("/openCamera/{conferenceId}")
    @Operation(summary = "开启镜头")
    public RestResponse openCamera(@PathVariable String conferenceId)
    {
        attendeeService.openCamera(conferenceId);
        return success();
    }
    
    /**
     * 关闭镜头
     */
    @PostMapping("/closeCamera/{conferenceId}")
    @Operation(summary = "关闭镜头")
    public RestResponse closeCamera(@PathVariable String conferenceId)
    {
        attendeeService.closeCamera(conferenceId);
        return success();
    }
    
    /**
     * 单个开镜
     */
    @PostMapping("/openCamera/{conferenceId}/{attendeeId}")
    @Operation(summary = "单个开镜")
    public RestResponse openCamera(@PathVariable String conferenceId, @PathVariable String attendeeId)
    {
        attendeeService.openCamera(conferenceId, attendeeId);
        return success();
    }
    
    /**
     * 单个关镜
     */
    @PostMapping("/closeCamera/{conferenceId}/{attendeeId}")
    @Operation(summary = "单个关镜")
    public RestResponse closeCamera(@PathVariable String conferenceId, @PathVariable String attendeeId)
    {
        attendeeService.closeCamera(conferenceId, attendeeId);
        return success();
    }
    
    /**
     * 开启显示器
     */
    @PostMapping("/openDisplayDevice/{conferenceId}")
    @Operation(summary = "开启镜头")
    public RestResponse openDisplayDevice(@PathVariable String conferenceId)
    {
        attendeeService.openDisplayDevice(conferenceId);
        return success();
    }
    
    /**
     * 关闭显示器
     */
    @PostMapping("/closeDisplayDevice/{conferenceId}")
    @Operation(summary = "关闭镜头")
    public RestResponse closeDisplayDevice(@PathVariable String conferenceId)
    {
        attendeeService.closeDisplayDevice(conferenceId);
        return success();
    }
    
    /**
     * 单个开显示器
     */
    @PostMapping("/openDisplayDevice/{conferenceId}/{attendeeId}")
    @Operation(summary = "单个开镜")
    public RestResponse openDisplayDevice(@PathVariable String conferenceId, @PathVariable String attendeeId)
    {
        attendeeService.openDisplayDevice(conferenceId, attendeeId);
        return success();
    }
    
    /**
     * 单个关显示器
     */
    @PostMapping("/closeDisplayDevice/{conferenceId}/{attendeeId}")
    @Operation(summary = "单个关镜")
    public RestResponse closeDisplayDevice(@PathVariable String conferenceId, @PathVariable String attendeeId)
    {
        attendeeService.closeDisplayDevice(conferenceId, attendeeId);
        return success();
    }
    
    /**
     * 获取快照
     */
    @PostMapping("/takeSnapshot/{conferenceId}/{attendeeId}")
    @Operation(summary = "获取快照")
    public RestResponse takeSnapshot(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody JSONObject params)
    {
        return success(attendeeService.takeSnapshot(conferenceId, attendeeId, params));
    }
    
    /**
     * 摄像机控制
     */
    @PostMapping("/cameraControl/{conferenceId}/{attendeeId}")
    @Operation(summary = "摄像机控制")
    public RestResponse cameraControl(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody JSONObject params)
    {
        attendeeService.cameraControl(conferenceId, attendeeId, params);
        return success();
    }
    
    @PutMapping("/presentationSetting/{conferenceId}/{attendeeId}")
    @Operation(summary = "辅流设置")
    public RestResponse presentationSetting(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody List<FixedParamValue> params)
    {
        attendeeService.presentationSetting(conferenceId, attendeeId, params);
        return success();
    }
    
    @PutMapping("/mainSetting/{conferenceId}/{attendeeId}")
    @Operation(summary = "主流设置")
    public RestResponse mainSetting(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody List<FixedParamValue> params)
    {
        attendeeService.mainSetting(conferenceId, attendeeId, params);
        return success();
    }
    
    @PutMapping("/subtitle/{conferenceId}/{attendeeId}")
    @Operation(summary = "字幕设置")
    public RestResponse subtitle(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody List<FixedParamValue> params)
    {
        attendeeService.subtitle(conferenceId, attendeeId, params);
        return success();
    }
    
    @PutMapping("/setBanner/{conferenceId}/{attendeeId}")
    @Operation(summary = "设置横幅")
    public RestResponse setBanner(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody JSONObject params)
    {
        attendeeService.setBanner(conferenceId, attendeeId, params);
        return success();
    }
    
    @PutMapping("/layoutSetting/{conferenceId}/{attendeeId}")
    @Operation(summary = "布局设置")
    public RestResponse layoutSetting(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody List<FixedParamValue> params)
    {
        attendeeService.layoutSetting(conferenceId, attendeeId, params);
        return success();
    }
    
    @PutMapping("/recordStreamSetting/{conferenceId}/{attendeeId}")
    @Operation(summary = "直播录制设置")
    public RestResponse recordStreamSetting(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody List<FixedParamValue> params)
    {
        attendeeService.recordStreamSetting(conferenceId, attendeeId, params);
        return success();
    }
    
    @PutMapping("/advanceSetting/{conferenceId}/{attendeeId}")
    @Operation(summary = "高级设置")
    public RestResponse advanceSetting(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody List<FixedParamValue> params)
    {
        attendeeService.advanceSetting(conferenceId, attendeeId, params);
        return success();
    }
    
    @GetMapping("/attendeeCallLegSetting/{conferenceId}/{attendeeId}")
    @Operation(summary = "获取CallLeg")
    public RestResponse attendeeCallLegSetting(@PathVariable String conferenceId, @PathVariable String attendeeId)
    {
        return success(attendeeService.attendeeCallLegSetting(conferenceId, attendeeId));
    }
    
    /**
     * 消息发送
     */
    @PostMapping("/sendMessage/{conferenceId}")
    @Operation(summary = "消息发送")
    public RestResponse sendMessage(@PathVariable String conferenceId, @RequestBody JSONObject jsonObject)
    {
        attendeeService.sendMessage(conferenceId, jsonObject);
        return success();
    }
    
    /**
     * 横幅设置发送
     */
    @PostMapping("/setMessageBannerText/{conferenceId}")
    @Operation(summary = "横幅设置发送")
    public RestResponse setMessageBannerText(@PathVariable String conferenceId, @RequestBody JSONObject jsonObject)
    {
        attendeeService.setMessageBannerText(conferenceId, jsonObject);
        return success();
    }
    
    /**
     * 批量设置横幅
     */
    @PostMapping("/sendBanner/{conferenceId}")
    @Operation(summary = "批量设置横幅")
    public RestResponse sendBanner(@PathVariable String conferenceId, @RequestBody JSONObject jsonObject)
    {
        attendeeService.sendBanner(conferenceId, jsonObject);
        return success();
    }
    
    /**
     * 轮询
     */
    @PostMapping("/polling/{conferenceId}")
    @Operation(summary = "轮询")
    public RestResponse polling(@PathVariable String conferenceId)
    {
        attendeeService.polling(conferenceId);
        return success();
    }
    
    /**
     * 轮询暂停
     * @author sinhy
     * @since 2022-04-11 10:09  void
     */
    @PostMapping("/pollingPause/{conferenceId}")
    @Operation(summary = "轮询暂停")
    public RestResponse pollingPause(@PathVariable String conferenceId)
    {
        attendeeService.pollingPause(conferenceId);
        return success();
    }
    
    /**
     * 轮询恢复运行
     * @author sinhy
     * @since 2022-04-11 10:09  void
     */
    @PostMapping("/pollingResume/{conferenceId}")
    @Operation(summary = "轮询恢复运行")
    public RestResponse pollingResume(@PathVariable String conferenceId)
    {
        attendeeService.pollingResume(conferenceId);
        return success();
    }
    
    /**
     * 取消轮询
     */
    @PostMapping("/cancelPolling/{conferenceId}")
    @Operation(summary = "取消轮询")
    public RestResponse cancelPolling(@PathVariable String conferenceId)
    {
        attendeeService.cancelCallTheRoll(conferenceId);
        return success();
    }
    
    /**
     * 批量邀请
     */
    @PostMapping("/batchInvite/{conferenceId}")
    @Operation(summary = "批量邀请")
    public RestResponse invite(@PathVariable String conferenceId, @RequestBody List<Long> terminalIds)
    {
        attendeeService.invite(conferenceId, terminalIds);
        return success();
    }
    
    /**
     * uri邀请
     */
    @PostMapping("/inviteByUri/{conferenceId}")
    @Operation(summary = "uri邀请")
    public RestResponse invite(@PathVariable String conferenceId, @RequestBody JSONObject jsonObj)
    {
        attendeeService.invite(conferenceId, jsonObj);
        return success();
    }


    /**
     * 轮询获取快照
     */
    @PostMapping("/takeSnapshot/polling/{conferenceId}")
    @Operation(summary = "获取快照")
    public RestResponse takeSnapshotPolling(@PathVariable String conferenceId, @RequestBody JSONObject params)
    {
        return success(attendeeService.takeSnapshotPolling(conferenceId, params));
    }
}
