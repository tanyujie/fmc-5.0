/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeController.java
 * Package     : com.paradisecloud.fcm.web.controller.business
 * @author lilinhai
 * @since 2021-02-05 17:35
 * @version  V1.0
 */
package com.paradisecloud.fcm.web.controller.mobile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IAttendeeForMcuPlcService;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IAttendeeForMcuKdcService;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IAttendeeForMcuZjService;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.attendee.BaseFixedParamValue;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.web.model.mobile.req.MobileAttendeeLayoutSettingReq;
import com.paradisecloud.fcm.web.model.mobile.vo.MobileAttendeeFixedParamVo;
import com.paradisecloud.fcm.web.model.mobile.vo.MobileAttendeeListVo;
import com.paradisecloud.fcm.web.model.mobile.vo.MobileAttendeeVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 参会者控制器
 *
 * @author nj
 * @date 2022-06-16 14:35
 */
@RestController
@RequestMapping("/mobile/attendee")
@Tag(name = "参会者控制器")
public class MobileAttendeeController extends MobileBaseController {

    @Resource
    private IAttendeeService attendeeService;
    @Resource
    private IAttendeeForMcuZjService attendeeForMcuZjService;
    @Resource
    private IAttendeeForMcuPlcService attendeeForMcuPlcService;
    @Resource
    private IAttendeeForMcuKdcService attendeeForMcuKdcService;

    /**
     * 参会者详情
     */
    @PostMapping("/detail")
    @Operation(summary = "参会者详情")
    public RestResponse detail(@Valid @RequestBody MobileAttendeeVo mobileAttendeeVo) {
        String attendeeId = mobileAttendeeVo.getAttendeeId();
        String conferenceId = mobileAttendeeVo.getConferenceId();
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                return success(attendeeService.detail(conferenceId, attendeeId));
            }
            case MCU_ZJ: {
                return success(attendeeForMcuZjService.detail(conferenceId, attendeeId));
            }
            case MCU_PLC: {
                return success(attendeeForMcuPlcService.detail(conferenceId, attendeeId));
            }
            case MCU_KDC: {
                return success(attendeeForMcuKdcService.detail(conferenceId, attendeeId));
            }
        }

        return fail(1, "获取失败");
    }

    /**
     * 批量获取参会者详情
     */
    @PostMapping("/details")
    @Operation(summary = "参会者详情")
    public RestResponse details(@Valid @RequestBody MobileAttendeeListVo mobileAttendeeListVo) {
        List<String> attendeeIds = mobileAttendeeListVo.getAttendeeIds();
        String conferenceId = mobileAttendeeListVo.getConferenceId();
        JSONArray ja = new JSONArray();

        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                for (String attendeeId : attendeeIds) {
                    ja.add(attendeeService.detail(conferenceId, attendeeId));
                }
                break;
            }
            case MCU_ZJ: {
                for (String attendeeId : attendeeIds) {
                    ja.add(attendeeForMcuZjService.detail(conferenceId, attendeeId));
                }
                break;
            }
            case MCU_PLC: {
                for (String attendeeId : attendeeIds) {
                    ja.add(attendeeForMcuPlcService.detail(conferenceId, attendeeId));
                }
                break;
            }
            case MCU_KDC: {
                for (String attendeeId : attendeeIds) {
                    ja.add(attendeeForMcuKdcService.detail(conferenceId, attendeeId));
                }
                break;
            }
        }

        return success(ja);
    }

    /**
     * 参会者页面上重呼
     */
    @PostMapping("/recall")
    @Operation(summary = "参会者页面上重呼")
    public RestResponse recall(@Valid @RequestBody MobileAttendeeVo mobileAttendeeVo) {
        String attendeeId = mobileAttendeeVo.getAttendeeId();
        String conferenceId = mobileAttendeeVo.getConferenceId();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.recall(conferenceId, attendeeId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.recall(conferenceId, attendeeId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.recall(conferenceId, attendeeId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.recall(conferenceId, attendeeId);
                break;
            }
        }

        return success();
    }

    /**
     * 参会者页面上挂断
     */
    @PostMapping("/hangUp")
    @Operation(summary = "参会者页面上挂断")
    public RestResponse hangUp(@Valid @RequestBody MobileAttendeeVo mobileAttendeeVo) {
        String attendeeId = mobileAttendeeVo.getAttendeeId();
        String conferenceId = mobileAttendeeVo.getConferenceId();


        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.hangUp(conferenceId, attendeeId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.hangUp(conferenceId, attendeeId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.hangUp(conferenceId, attendeeId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.hangUp(conferenceId, attendeeId);
                break;
            }
        }

        return success();
    }

    /**
     * 参会者页面上移除
     */
    @PostMapping("/remove")
    @Operation(summary = "参会者页面上移除")
    public RestResponse remove(@Valid @RequestBody MobileAttendeeVo mobileAttendeeVo) {
        String attendeeId = mobileAttendeeVo.getAttendeeId();
        String conferenceId = mobileAttendeeVo.getConferenceId();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.remove(conferenceId, attendeeId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.remove(conferenceId, attendeeId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.remove(conferenceId, attendeeId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.remove(conferenceId, attendeeId);
                break;
            }
        }

        return success();
    }

    /**
     * 主会场变更
     */
    @PostMapping("/changeMaster")
    @Operation(summary = "主会场变更")
    public RestResponse changeMaster(@Valid @RequestBody MobileAttendeeVo mobileAttendeeVo) {
        String attendeeId = mobileAttendeeVo.getAttendeeId();
        String conferenceId = mobileAttendeeVo.getConferenceId();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.changeMaster(conferenceId, attendeeId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.changeMaster(conferenceId, attendeeId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.changeMaster(conferenceId, attendeeId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.changeMaster(conferenceId, attendeeId);
                break;
            }
        }

        return success();
    }

    /**
     * 主会场变更
     */
    @PostMapping("/pre/changeMaster")
    @Operation(summary = "主会场变更")
    public RestResponse preChangeMaster(@Valid @RequestBody MobileAttendeeVo mobileAttendeeVo) {
        String attendeeId = mobileAttendeeVo.getAttendeeId();
        String conferenceId = mobileAttendeeVo.getConferenceId();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                attendeeService.changeMaster(conferenceId, attendeeId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.changeMaster(conferenceId, attendeeId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.changeMaster(conferenceId, attendeeId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.changeMaster(conferenceId, attendeeId);
                break;
            }
        }

        return success();
    }

    /**
     * 选看
     */
    @PostMapping("/chooseSee")
    @Operation(summary = "选看")
    public RestResponse chooseSee(@Valid @RequestBody MobileAttendeeVo mobileAttendeeVo) {
        String attendeeId = mobileAttendeeVo.getAttendeeId();
        String conferenceId = mobileAttendeeVo.getConferenceId();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.chooseSee(conferenceId, attendeeId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.chooseSee(conferenceId, attendeeId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.chooseSee(conferenceId, attendeeId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.chooseSee(conferenceId, attendeeId);
                break;
            }
        }

        return success();
    }

    /**
     * 点名
     */
    @PostMapping("/callTheRoll")
    @Operation(summary = "点名")
    public RestResponse callTheRoll(@Valid @RequestBody MobileAttendeeVo mobileAttendeeVo) {
        String attendeeId = mobileAttendeeVo.getAttendeeId();
        String conferenceId = mobileAttendeeVo.getConferenceId();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.callTheRoll(conferenceId, attendeeId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.callTheRoll(conferenceId, attendeeId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.callTheRoll(conferenceId, attendeeId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.callTheRoll(conferenceId, attendeeId);
                break;
            }
        }

        return success();
    }

    /**
     * 对话
     */
    @PostMapping("/talk")
    @Operation(summary = "对话")
    public RestResponse talk(@Valid @RequestBody MobileAttendeeVo mobileAttendeeVo) {
        String attendeeId = mobileAttendeeVo.getAttendeeId();
        String conferenceId = mobileAttendeeVo.getConferenceId();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.talk(conferenceId, attendeeId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.talk(conferenceId, attendeeId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.talk(conferenceId, attendeeId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.talk(conferenceId, attendeeId);
                break;
            }
        }

        return success();
    }

    /**
     * 取消点名
     */
    @GetMapping("/cancelCallTheRoll")
    @Operation(summary = "取消点名")
    public RestResponse cancelCallTheRoll(@RequestParam("conferenceId") String conferenceId) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.cancelCallTheRoll(conferenceId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.cancelCallTheRoll(conferenceId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.cancelCallTheRoll(conferenceId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.cancelCallTheRoll(conferenceId);
                break;
            }
        }

        return success();
    }

    /**
     * 开启混音
     */
    @PostMapping("/openMixingOne")
    @Operation(summary = "开启混音")
    public RestResponse openMixing(@Valid @RequestBody MobileAttendeeVo mobileAttendeeVo) {
        String attendeeId = mobileAttendeeVo.getAttendeeId();
        String conferenceId = mobileAttendeeVo.getConferenceId();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.openMixing(conferenceId, attendeeId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.openMixing(conferenceId, attendeeId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.openMixing(conferenceId, attendeeId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.openMixing(conferenceId, attendeeId);
                break;
            }
        }

        return success();
    }

    /**
     * 关闭混音
     */
    @PostMapping("/closeMixingOne")
    @Operation(summary = "关闭混音")
    public RestResponse closeMixing(@Valid @RequestBody MobileAttendeeVo mobileAttendeeVo) {
        String attendeeId = mobileAttendeeVo.getAttendeeId();
        String conferenceId = mobileAttendeeVo.getConferenceId();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.closeMixing(conferenceId, attendeeId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.closeMixing(conferenceId, attendeeId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.closeMixing(conferenceId, attendeeId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.closeMixing(conferenceId, attendeeId);
                break;
            }
        }

        return success();
    }

    /**
     * 开启混音
     */
    @GetMapping("/openMixing")
    @Operation(summary = "开启混音")
    public RestResponse openMixing(@RequestParam("conferenceId") String conferenceId) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.openMixing(conferenceId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.openMixing(conferenceId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.openMixing(conferenceId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.openMixing(conferenceId);
                break;
            }
        }

        return success();
    }

    /**
     * 关闭混音
     */
    @GetMapping("/closeMixing")
    @Operation(summary = "关闭混音")
    public RestResponse closeMixing(@RequestParam("conferenceId") String conferenceId) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.closeMixing(conferenceId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.closeMixing(conferenceId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.closeMixing(conferenceId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.closeMixing(conferenceId);
                break;
            }
        }

        return success();
    }

    /**
     * 开启镜头
     */
    @GetMapping("/openCamera")
    @Operation(summary = "开启镜头")
    public RestResponse openCamera(@RequestParam("conferenceId") String conferenceId) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.openCamera(conferenceId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.openCamera(conferenceId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.openCamera(conferenceId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.openCamera(conferenceId);
                break;
            }
        }

        return success();
    }

    /**
     * 关闭镜头
     */
    @GetMapping("/closeCamera")
    @Operation(summary = "关闭镜头")
    public RestResponse closeCamera(@RequestParam("conferenceId") String conferenceId) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.closeCamera(conferenceId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.closeCamera(conferenceId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.closeCamera(conferenceId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.closeCamera(conferenceId);
                break;
            }
        }

        return success();
    }

    /**
     * 单个开镜
     */
    @PostMapping("/openCameraOne")
    @Operation(summary = "单个开镜")
    public RestResponse openCamera(@Valid @RequestBody MobileAttendeeVo mobileAttendeeVo) {
        String attendeeId = mobileAttendeeVo.getAttendeeId();
        String conferenceId = mobileAttendeeVo.getConferenceId();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.openCamera(conferenceId, attendeeId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.openCamera(conferenceId, attendeeId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.openCamera(conferenceId, attendeeId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.openCamera(conferenceId, attendeeId);
                break;
            }
        }

        return success();
    }

    /**
     * 单个关镜
     */
    @PostMapping("/closeCameraOne")
    @Operation(summary = "单个关镜")
    public RestResponse closeCamera(@Valid @RequestBody MobileAttendeeVo mobileAttendeeVo) {
        String attendeeId = mobileAttendeeVo.getAttendeeId();
        String conferenceId = mobileAttendeeVo.getConferenceId();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.closeCamera(conferenceId, attendeeId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.closeCamera(conferenceId, attendeeId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.closeCamera(conferenceId, attendeeId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.closeCamera(conferenceId, attendeeId);
                break;
            }
        }

        return success();
    }

    /**
     * 开启显示器
     */
    @GetMapping("/openDisplayDevice")
    @Operation(summary = "开启镜头")
    public RestResponse openDisplayDevice(@RequestParam("conferenceId") String conferenceId) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.openDisplayDevice(conferenceId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.openDisplayDevice(conferenceId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.openDisplayDevice(conferenceId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.openDisplayDevice(conferenceId);
                break;
            }
        }

        return success();
    }

    /**
     * 关闭显示器
     */
    @GetMapping("/closeDisplayDevice")
    @Operation(summary = "关闭镜头")
    public RestResponse closeDisplayDevice(@RequestParam("conferenceId") String conferenceId) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.closeDisplayDevice(conferenceId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.closeDisplayDevice(conferenceId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.closeDisplayDevice(conferenceId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.closeDisplayDevice(conferenceId);
                break;
            }
        }

        return success();
    }

    /**
     * 单个开显示器
     */
    @PostMapping("/openDisplayDeviceOne")
    @Operation(summary = "单个开镜")
    public RestResponse openDisplayDevice(@Valid @RequestBody MobileAttendeeVo mobileAttendeeVo) {
        String attendeeId = mobileAttendeeVo.getAttendeeId();
        String conferenceId = mobileAttendeeVo.getConferenceId();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.openDisplayDevice(conferenceId, attendeeId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.openDisplayDevice(conferenceId, attendeeId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.openDisplayDevice(conferenceId, attendeeId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.openDisplayDevice(conferenceId, attendeeId);
                break;
            }
        }

        return success();
    }

    /**
     * 单个关显示器
     */
    @PostMapping("/closeDisplayDeviceOne")
    @Operation(summary = "单个关镜")
    public RestResponse closeDisplayDevice(@Valid @RequestBody MobileAttendeeVo mobileAttendeeVo) {
        String attendeeId = mobileAttendeeVo.getAttendeeId();
        String conferenceId = mobileAttendeeVo.getConferenceId();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.closeDisplayDevice(conferenceId, attendeeId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.closeDisplayDevice(conferenceId, attendeeId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.closeDisplayDevice(conferenceId, attendeeId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.closeDisplayDevice(conferenceId, attendeeId);
                break;
            }
        }

        return success();
    }


    /**
     * 摄像机控制
     */
    @PostMapping("/cameraControl")
    @Operation(summary = "摄像机控制")
    public RestResponse cameraControl(@RequestBody JSONObject params) {
        String conferenceId = params.getString("conferenceId");
        String attendeeId = params.getString("attendeeId");
        JSONObject jsonObject = params.getJSONObject("params");

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.cameraControl(conferenceId, attendeeId, jsonObject);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.cameraControl(conferenceId, attendeeId, jsonObject);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.cameraControl(conferenceId, attendeeId, jsonObject);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.cameraControl(conferenceId, attendeeId, jsonObject);
                break;
            }
        }

        return success();
    }

    @PostMapping("/presentationSetting")
    @Operation(summary = "辅流设置")
    public RestResponse presentationSetting(@Valid @RequestBody MobileAttendeeFixedParamVo mobileAttendeeFixedParamVo) {
        List<BaseFixedParamValue> params = mobileAttendeeFixedParamVo.getParams();
        String conferenceId = mobileAttendeeFixedParamVo.getConferenceId();
        String attendeeId = mobileAttendeeFixedParamVo.getAttendeeId();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.presentationSetting(conferenceId, attendeeId, params);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.presentationSetting(conferenceId, attendeeId, params);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.presentationSetting(conferenceId, attendeeId, params);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.presentationSetting(conferenceId, attendeeId, params);
                break;
            }
        }

        return success();
    }

    @PostMapping("/mainSetting")
    @Operation(summary = "主流设置")
    public RestResponse mainSetting(@Valid @RequestBody MobileAttendeeFixedParamVo mobileAttendeeFixedParamVo) {
        List<BaseFixedParamValue> params = mobileAttendeeFixedParamVo.getParams();
        String conferenceId = mobileAttendeeFixedParamVo.getConferenceId();
        String attendeeId = mobileAttendeeFixedParamVo.getAttendeeId();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.mainSetting(conferenceId, attendeeId, params);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.mainSetting(conferenceId, attendeeId, params);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.mainSetting(conferenceId, attendeeId, params);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.mainSetting(conferenceId, attendeeId, params);
                break;
            }
        }

        return success();
    }

    @PostMapping("/recordStreamSetting")
    @Operation(summary = "直播录制设置")
    public RestResponse recordStreamSetting(@Valid @RequestBody MobileAttendeeFixedParamVo mobileAttendeeFixedParamVo) {
        List<BaseFixedParamValue> params = mobileAttendeeFixedParamVo.getParams();
        String conferenceId = mobileAttendeeFixedParamVo.getConferenceId();
        String attendeeId = mobileAttendeeFixedParamVo.getAttendeeId();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.recordStreamSetting(conferenceId, attendeeId, params);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.recordStreamSetting(conferenceId, attendeeId, params);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.recordStreamSetting(conferenceId, attendeeId, params);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.recordStreamSetting(conferenceId, attendeeId, params);
                break;
            }
        }

        return success();
    }

    @PostMapping("/layoutSetting")
    @Operation(summary = "布局设置")
    public RestResponse layoutSetting(@Valid @RequestBody MobileAttendeeLayoutSettingReq mobileAttendeeLayoutSettingReq) {
        List<BaseFixedParamValue> params = new ArrayList<>();
        BaseFixedParamValue fixedParamValue = new BaseFixedParamValue();
        fixedParamValue.setName("chosenLayout");
        fixedParamValue.setFixed(true);
        fixedParamValue.setValue(mobileAttendeeLayoutSettingReq.getLayout());

        BaseFixedParamValue fixedParamValueDefaultLayout = new BaseFixedParamValue();
        fixedParamValueDefaultLayout.setName("defaultLayout");
        fixedParamValueDefaultLayout.setFixed(true);
        fixedParamValueDefaultLayout.setValue(mobileAttendeeLayoutSettingReq.getLayout());
        params.add(fixedParamValue);
        params.add(fixedParamValueDefaultLayout);

        String conferenceId = mobileAttendeeLayoutSettingReq.getConferenceId();
        String attendeeId = mobileAttendeeLayoutSettingReq.getAttendeeId();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.layoutSetting(conferenceId, attendeeId, params);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.layoutSetting(conferenceId, attendeeId, params);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.layoutSetting(conferenceId, attendeeId, params);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.layoutSetting(conferenceId, attendeeId, params);
                break;
            }
        }

        return success();
    }


    @PutMapping("/presentationSetting/{conferenceId}/presentationContributionAllowed/{enable}")
    @Operation(summary = "辅流设置")
    public RestResponse presentationSetting(@PathVariable String conferenceId, @RequestBody List<String> attendeeIds, @PathVariable String enable) {
        List<BaseFixedParamValue> params = new ArrayList<>();
        BaseFixedParamValue fixedParamValue = new BaseFixedParamValue();
        fixedParamValue.setName("presentationContributionAllowed");
        fixedParamValue.setFixed(true);
        fixedParamValue.setValue(enable);
        params.add(fixedParamValue);

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        if (CollectionUtils.isNotEmpty(attendeeIds)) {
            for (String attendeeId : attendeeIds) {
                switch (mcuType) {
                    case FME: {
                        attendeeService.presentationSetting(conferenceId, attendeeId, params);
                        break;
                    }
                    case MCU_ZJ: {
                        attendeeForMcuZjService.presentationSetting(conferenceId, attendeeId, params);
                        break;
                    }
                    case MCU_PLC: {
                        attendeeForMcuPlcService.presentationSetting(conferenceId, attendeeId, params);
                        break;
                    }
                    case MCU_KDC: {
                        attendeeForMcuKdcService.presentationSetting(conferenceId, attendeeId, params);
                        break;
                    }
                }
            }
        }
        
        return success();
    }

    /**
     * 获取快照
     */
    @PostMapping("/takeSnapshot/{conferenceId}/{attendeeId}")
    @Operation(summary = "获取快照", description = "获取快照")
    public RestResponse takeSnapshot(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody JSONObject params) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                return success(attendeeService.takeSnapshot(conferenceId, attendeeId, params));
            }
            case MCU_ZJ: {
                return success(attendeeForMcuZjService.takeSnapshot(conferenceId, attendeeId, params));
            }
            case MCU_PLC: {
                return success(attendeeForMcuPlcService.takeSnapshot(conferenceId, attendeeId, params));
            }
            case MCU_KDC: {
                return success(attendeeForMcuKdcService.takeSnapshot(conferenceId, attendeeId, params));
            }
        }
        return success();
    }

}
