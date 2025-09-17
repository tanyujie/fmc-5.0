/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeController.java
 * Package     : com.paradisecloud.fcm.web.controller.business
 * @author lilinhai
 * @since 2021-02-05 17:35
 * @version  V1.0
 */
package com.paradisecloud.fcm.web.controller.mobile.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.AttendeeCallTheRollStatus;
import com.paradisecloud.fcm.common.enumer.AttendeeChooseSeeStatus;
import com.paradisecloud.fcm.common.enumer.AttendeeMixingStatus;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.ding.service2.interfaces.IAttendeeDingService;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.CropDirAttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IAttendeeHwcloudService;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IAttendeeForMcuKdcService;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IAttendeeForMcuPlcService;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IAttendeeForMcuZjService;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.attendee.BaseFixedParamValue;
import com.paradisecloud.fcm.service.conference.cascade.ConferenceCascadeHandler;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IAttendeeSmc2Service;
import com.paradisecloud.fcm.tencent.service2.interfaces.IAttendeeTencentService;
import com.paradisecloud.fcm.zte.service.interfaces.IAttendeeForMcuZteService;
import com.paradisecloud.smc3.model.mix.ConferenceControllerRequest;
import com.paradisecloud.smc3.service.interfaces.IAttendeeSmc3Service;
import com.sinhy.exception.SystemException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * <pre>参会者控制器</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-05 17:35
 */
@RestController
@RequestMapping("/mobileWeb/mcu/all/attendee")
@Tag(name = "参会者控制器")
public class MobileWebAttendeeForAllController extends BaseController {

    @Resource
    private IAttendeeService attendeeService;
    @Resource
    private IAttendeeForMcuZjService attendeeForMcuZjService;
    @Resource
    private IAttendeeForMcuPlcService attendeeForMcuPlcService;
    @Resource
    private IAttendeeForMcuKdcService attendeeForMcuKdcService;
    @Resource
    private IAttendeeSmc3Service attendeeSmc3Service;
    @Resource
    private IAttendeeSmc2Service attendeeSmc2Service;
    @Resource
    private IAttendeeTencentService attendeeTencentService;
    @Resource
    private IAttendeeDingService attendeeDingService;
    @Resource
    private IAttendeeHwcloudService attendeeHwcloudService;
    @Resource
    private IAttendeeForMcuZteService attendeeForMcuZteService;

    /**
     * 参会者详情
     */
    @PostMapping("/detail/{conferenceId}/{attendeeId}")
    @Operation(summary = "参会者详情")
    public RestResponse detail(@PathVariable String conferenceId, @PathVariable String attendeeId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
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
            case SMC3: {
                return success(attendeeSmc3Service.detail(conferenceId, attendeeId));
            }
            case SMC2: {
                return success(attendeeSmc2Service.detail(conferenceId, attendeeId));
            }
        }
        return RestResponse.fail();
    }

    /**
     * 批量获取参会者详情
     */
    @PostMapping("/details/{conferenceId}")
    @Operation(summary = "参会者详情")
    public RestResponse details(@PathVariable String conferenceId, @RequestBody List<String> attendeeIds) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                JSONArray ja = new JSONArray();
                for (String attendeeId : attendeeIds) {
                    ja.add(attendeeService.detail(conferenceId, attendeeId));
                }
                return success(ja);
            }
            case MCU_ZJ: {
                JSONArray ja = new JSONArray();
                for (String attendeeId : attendeeIds) {
                    ja.add(attendeeForMcuZjService.detail(conferenceId, attendeeId));
                }
                return success(ja);
            }
            case MCU_PLC: {
                JSONArray ja = new JSONArray();
                for (String attendeeId : attendeeIds) {
                    ja.add(attendeeForMcuPlcService.detail(conferenceId, attendeeId));
                }
                return success(ja);
            }
            case MCU_KDC: {
                JSONArray ja = new JSONArray();
                for (String attendeeId : attendeeIds) {
                    ja.add(attendeeForMcuKdcService.detail(conferenceId, attendeeId));
                }
                return success(ja);
            }
            case SMC3: {
                JSONArray ja = new JSONArray();
                for (String attendeeId : attendeeIds) {
                    ja.add(attendeeSmc3Service.detail(conferenceId, attendeeId));
                }
                return success(ja);
            }
            case SMC2: {
                JSONArray ja = new JSONArray();
                for (String attendeeId : attendeeIds) {
                    ja.add(attendeeSmc2Service.detail(conferenceId, attendeeId));
                }
                return success(ja);
            }
        }
        return RestResponse.fail();
    }

    /**
     * 参会者页面上重呼
     */
    @PostMapping("/recall/{conferenceId}/{attendeeId}")
    @Operation(summary = "参会者页面上重呼", description = "重呼")
    public RestResponse recall(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody(required = false) JSONObject params) {
        if (params != null) {
            String downCascadeConferenceId = params.getString("downCascadeConferenceId");
            if (StringUtils.isNotEmpty(downCascadeConferenceId)) {
                BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(downCascadeConferenceId));
                if (downCascadeConferenceContext != null) {
                    BaseAttendee downCascadeAttendee = downCascadeConferenceContext.getAttendeeById(attendeeId);
                    if (downCascadeAttendee != null) {
                        RestResponse restResponse = recall(downCascadeConferenceId, downCascadeAttendee.getId(), null);
                        return restResponse;
                    }
                }
            }
        }
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
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
            case SMC3: {
                attendeeSmc3Service.recall(conferenceId, attendeeId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.recall(conferenceId, attendeeId);
                break;
            }
            case MCU_TENCENT: {
                attendeeTencentService.recall(conferenceId, attendeeId);
                break;
            }

            case MCU_DING: {
                attendeeDingService.recall(conferenceId, attendeeId);
                break;
            }
            case MCU_HWCLOUD: {
                attendeeHwcloudService.recall(conferenceId, attendeeId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.recall(conferenceId, attendeeId);
                break;
            }
        }
        return success();
    }

    /**
     * 参会者页面上挂断
     */
    @PostMapping("/hangUp/{conferenceId}/{attendeeId}")
    @Operation(summary = "参会者页面上重呼", description = "挂断")
    public RestResponse hangUp(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody(required = false) JSONObject params) {
        if (params != null) {
            String downCascadeConferenceId = params.getString("downCascadeConferenceId");
            if (StringUtils.isNotEmpty(downCascadeConferenceId)) {
                BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(downCascadeConferenceId));
                if (downCascadeConferenceContext != null) {
                    BaseAttendee downCascadeAttendee = downCascadeConferenceContext.getAttendeeById(attendeeId);
                    if (downCascadeAttendee != null) {
                        RestResponse restResponse = hangUp(downCascadeConferenceId, downCascadeAttendee.getId(), null);
                        return restResponse;
                    }
                }
            }
        }
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
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
            case SMC3: {
                attendeeSmc3Service.hangUp(conferenceId, attendeeId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.hangUp(conferenceId, attendeeId);
                break;
            }

            case MCU_TENCENT: {
                attendeeTencentService.hangUp(conferenceId, attendeeId);
                break;
            }

            case MCU_DING: {
                attendeeDingService.hangUp(conferenceId, attendeeId);
                break;
            }
            case MCU_HWCLOUD: {
                attendeeHwcloudService.hangUp(conferenceId, attendeeId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.hangUp(conferenceId, attendeeId);
                break;
            }
        }
        return success();
    }

    /**
     * 参会者页面上移除
     */
    @DeleteMapping("/remove/{conferenceId}/{attendeeId}")
    @Operation(summary = "参会者页面上移除", description = "移除")
    public RestResponse remove(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody(required = false) JSONObject params) {
        if (params != null) {
            String downCascadeConferenceId = params.getString("downCascadeConferenceId");
            if (StringUtils.isNotEmpty(downCascadeConferenceId)) {
                BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(downCascadeConferenceId));
                if (downCascadeConferenceContext != null) {
                    BaseAttendee downCascadeAttendee = downCascadeConferenceContext.getAttendeeById(attendeeId);
                    if (downCascadeAttendee != null) {
                        RestResponse restResponse = remove(downCascadeConferenceId, downCascadeAttendee.getId(), null);
                        return restResponse;
                    }
                }
            }
        }
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
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
            case SMC3: {
                attendeeSmc3Service.remove(conferenceId, attendeeId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.remove(conferenceId, attendeeId);
                break;
            }
            case MCU_TENCENT: {
                attendeeTencentService.remove(conferenceId, attendeeId);
                break;
            }

            case MCU_DING: {
                attendeeDingService.remove(conferenceId, attendeeId);
                break;
            }
            case MCU_HWCLOUD: {
                attendeeHwcloudService.remove(conferenceId, attendeeId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.remove(conferenceId, attendeeId);
                break;
            }
        }
        return success();
    }

    /**
     * 主会场变更
     */
    @PostMapping("/changeMaster/{conferenceId}/{attendeeId}")
    @Operation(summary = "主会场变更", description = "主会场变更")
    public RestResponse changeMaster(@PathVariable String conferenceId, @PathVariable String attendeeId) {
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
            case SMC3: {
                attendeeSmc3Service.changeMaster(conferenceId, attendeeId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.changeMaster(conferenceId, attendeeId);
                break;
            }
            case MCU_TENCENT: {
                attendeeTencentService.changeMaster(conferenceId, attendeeId);
                break;
            }

            case MCU_HWCLOUD: {
                attendeeHwcloudService.changeMaster(conferenceId, attendeeId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.changeMaster(conferenceId, attendeeId);
                break;
            }
        }
        // 下级会议重置为默认选看
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        List<BaseAttendee> mcuAttendees = baseConferenceContext.getMcuAttendees();
        for (BaseAttendee mcuAttendee : mcuAttendees) {
            try {
                ConferenceCascadeHandler.defaultChooseSee(mcuAttendee.getId());
            } catch (Exception e) {
            }
        }

        return success();
    }

    /**
     * 选看
     */
    @PostMapping("/chooseSee/{conferenceId}/{attendeeId}")
    @Operation(summary = "选看", description = "选看")
    public RestResponse chooseSee(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody(required = false) JSONObject params) {
        if (params != null) {
            String downCascadeConferenceId = params.getString("downCascadeConferenceId");
            if (StringUtils.isNotEmpty(downCascadeConferenceId)) {
                BaseConferenceContext mainConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
                if (mainConferenceContext.getMasterAttendee() == null || !mainConferenceContext.getMasterAttendee().isMeetingJoined()) {
                    throw new SystemException(1005454, "主会场未设置，无法进行选看操作！");
                }
                BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(downCascadeConferenceId));
                if (downCascadeConferenceContext != null) {
                    BaseAttendee downCascadeAttendee = downCascadeConferenceContext.getAttendeeById(attendeeId);
                    if (downCascadeAttendee != null) {
                        RestResponse restResponse = chooseSee(downCascadeConferenceId, downCascadeAttendee.getId());
                        if (!restResponse.isSuccess()) {
                            return restResponse;
                        }
                        BaseAttendee targetMcuAttendee = mainConferenceContext.getAttendeeById(downCascadeConferenceId);
                        if (targetMcuAttendee.getChooseSeeStatus() == AttendeeChooseSeeStatus.YES.getValue()) {
                            return restResponse;
                        }
                        restResponse = chooseSee(conferenceId, downCascadeConferenceId);
                        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
                        List<BaseAttendee> mcuAttendees = conferenceContext.getMcuAttendees();
                        for (BaseAttendee mcuAttendee : mcuAttendees) {
                            BaseConferenceContext mcuConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(mcuAttendee.getCascadeConferenceId()));
                            if (mcuConferenceContext != null && !mcuConferenceContext.getId().equals(downCascadeConferenceId)) {
                                try {
                                    ConferenceCascadeHandler.defaultChooseSee(mcuAttendee.getId());
                                } catch (Exception e) {
                                }
                            }
                        }
                        return restResponse;
                    }
                }
            }
        }
        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
        BaseAttendee attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee != null && attendee.isMcuAttendee() && attendee.isMeetingJoined()) {
            BaseConferenceContext mcuConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(attendee.getCascadeConferenceId()));
            if (mcuConferenceContext != null) {
                try {
                    ConferenceCascadeHandler.defaultChooseSee(attendee.getId());
                } catch (Exception e) {
                }
            }
        }
        chooseSee(conferenceId, attendeeId);
        List<BaseAttendee> mcuAttendees = conferenceContext.getMcuAttendees();
        for (BaseAttendee mcuAttendee : mcuAttendees) {
            BaseConferenceContext mcuConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(mcuAttendee.getCascadeConferenceId()));
            if (mcuConferenceContext != null && !mcuConferenceContext.getId().equals(attendeeId)) {
                try {
                    ConferenceCascadeHandler.defaultChooseSee(mcuAttendee.getId());
                } catch (Exception e) {
                }
            }
        }
        return success();
    }

    private RestResponse chooseSee(String conferenceId, String attendeeId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
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
            case SMC3: {
                attendeeSmc3Service.chooseSee(conferenceId, attendeeId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.chooseSee(conferenceId, attendeeId);
                break;
            }
            case MCU_TENCENT: {
                attendeeTencentService.chooseSee(conferenceId, attendeeId);
                break;
            }
            case MCU_HWCLOUD: {
                attendeeHwcloudService.chooseSee(conferenceId, attendeeId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.chooseSee(conferenceId, attendeeId);
                break;
            }
        }
        return success();
    }

    /**
     * 点名
     */
    @PostMapping("/callTheRoll/{conferenceId}/{attendeeId}")
    @Operation(summary = "点名", description = "点名")
    public RestResponse callTheRoll(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody(required = false) JSONObject params) {
        if (params != null) {
            String downCascadeConferenceId = params.getString("downCascadeConferenceId");
            if (StringUtils.isNotEmpty(downCascadeConferenceId)) {
                BaseConferenceContext mainConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
                if (mainConferenceContext.getMasterAttendee() == null || !mainConferenceContext.getMasterAttendee().isMeetingJoined()) {
                    throw new SystemException(1005454, "主会场未设置，无法进行点名操作！");
                }
                BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(downCascadeConferenceId));
                if (downCascadeConferenceContext != null) {
                    BaseAttendee downCascadeAttendee = downCascadeConferenceContext.getAttendeeById(attendeeId);
                    if (downCascadeAttendee != null) {
                        if (mainConferenceContext.isMultiScreenRollCall()) {
                            ConferenceCascadeHandler.chooseSee(downCascadeConferenceId, downCascadeAttendee.getId(), true);
                        } else {
                            callTheRoll(downCascadeConferenceId, downCascadeAttendee.getId());
                        }
                        BaseAttendee targetMcuAttendee = mainConferenceContext.getAttendeeById(downCascadeConferenceId);
                        if (targetMcuAttendee.getCallTheRollStatus() == AttendeeCallTheRollStatus.YES.getValue()) {
                            if (targetMcuAttendee.getMixingStatus() != AttendeeMixingStatus.YES.getValue()) {
                                openMixing(conferenceId, downCascadeConferenceId, null);
                            }
                            return success();
                        }
                        RestResponse restResponse = callTheRoll(conferenceId, downCascadeConferenceId);
                        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
                        List<BaseAttendee> mcuAttendees = conferenceContext.getMcuAttendees();
                        for (BaseAttendee mcuAttendee : mcuAttendees) {
                            BaseConferenceContext mcuConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(mcuAttendee.getCascadeConferenceId()));
                            if (mcuConferenceContext != null && !mcuConferenceContext.getId().equals(downCascadeConferenceId)) {
                                try {
                                    ConferenceCascadeHandler.defaultChooseSee(mcuAttendee.getId());
                                } catch (Exception e) {
                                }
                            }
                        }
                        return restResponse;
                    }
                }
            }
        }
        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
        BaseAttendee attendee = conferenceContext.getAttendeeById(attendeeId);
        if (attendee != null && attendee.isMcuAttendee() && attendee.isMeetingJoined()) {
            BaseConferenceContext mcuConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(attendee.getCascadeConferenceId()));
            if (mcuConferenceContext != null) {
                try {
                    ConferenceCascadeHandler.defaultChooseSee(attendee.getId(), true);
                } catch (Exception e) {
                }
            }
        }
        callTheRoll(conferenceId, attendeeId);
        List<BaseAttendee> mcuAttendees = conferenceContext.getMcuAttendees();
        for (BaseAttendee mcuAttendee : mcuAttendees) {
            BaseConferenceContext mcuConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(mcuAttendee.getCascadeConferenceId()));
            if (mcuConferenceContext != null && !mcuConferenceContext.getId().equals(attendeeId)) {
                try {
                    ConferenceCascadeHandler.defaultChooseSee(mcuAttendee.getId());
                } catch (Exception e) {
                }
            }
        }
        return success();
    }

    private RestResponse callTheRoll(String conferenceId, String attendeeId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
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
            case SMC3: {
                attendeeSmc3Service.callTheRoll(conferenceId, attendeeId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.callTheRoll(conferenceId, attendeeId);
                break;
            }
            case MCU_TENCENT: {
                attendeeTencentService.callTheRoll(conferenceId, attendeeId);
                break;
            }
            case MCU_HWCLOUD: {
                attendeeHwcloudService.callTheRoll(conferenceId, attendeeId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.callTheRoll(conferenceId, attendeeId);
                break;
            }
        }
        return success();
    }

    /**
     * 对话
     */
    @PostMapping("/talk/{conferenceId}/{attendeeId}")
    @Operation(summary = "对话", description = "对话")
    public RestResponse talk(@PathVariable String conferenceId, @PathVariable String attendeeId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
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
            case SMC3: {
                attendeeSmc3Service.talk(conferenceId, attendeeId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.talk(conferenceId, attendeeId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.talk(conferenceId, attendeeId);
                break;
            }
        }
        // 下级会议重置为默认选看
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        List<BaseAttendee> mcuAttendees = baseConferenceContext.getMcuAttendees();
        for (BaseAttendee mcuAttendee : mcuAttendees) {
            try {
                ConferenceCascadeHandler.defaultChooseSee(mcuAttendee.getId());
            } catch (Exception e) {
            }
        }
        return success();
    }

    /**
     * 私密对话
     */
    @PostMapping("/privateTalk/{conferenceId}")
    @Operation(summary = "对话", description = "私密对话")
    public RestResponse privateTalk(@PathVariable String conferenceId, @RequestBody JSONObject jsonObject) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case SMC3: {
                attendeeSmc3Service.privateTalk(conferenceId, jsonObject);
                break;
            }

            case SMC2: {
                attendeeSmc2Service.privateTalk(conferenceId, jsonObject);
                break;
            }

        }
        return success();
    }

    /**
     * 取消私密对话
     */
    @PostMapping("/cancel/privateTalk/{conferenceId}")
    @Operation(summary = "取消私密对话", description = "取消私密对话")
    public RestResponse cancelPrivateTalk(@PathVariable String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case SMC3: {
                attendeeSmc3Service.cancelPrivateTalk(conferenceId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.cancelPrivateTalk(conferenceId);
                break;
            }

        }
        return success();
    }


    /**
     * cancelCallTheRoll点名
     */
    @PostMapping("/cancelCallTheRoll/{conferenceId}")
    @Operation(summary = "取消对话", description = "取消点名")
    public RestResponse cancelCallTheRoll(@PathVariable String conferenceId, @RequestBody(required = false) JSONObject params) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
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
            case SMC3: {
                attendeeSmc3Service.cancelCallTheRoll(conferenceId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.cancelCallTheRoll(conferenceId);
                break;
            }
            case MCU_TENCENT: {
                attendeeTencentService.cancelCallTheRoll(conferenceId);
                break;
            }
            case MCU_HWCLOUD: {
                attendeeHwcloudService.cancelCallTheRoll(conferenceId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.cancelCallTheRoll(conferenceId);
                break;
            }

        }
        if (params != null) {
            String downCascadeConferenceId = params.getString("downCascadeConferenceId");
            if (StringUtils.isNotEmpty(downCascadeConferenceId)) {
                BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(downCascadeConferenceId));
                if (downCascadeConferenceContext != null) {
                    try {
                        ConferenceCascadeHandler.defaultChooseSee(downCascadeConferenceId);
                    } catch (Exception e) {
                    }
                }
            }
        }
        return success();
    }

    /**
     * 取消对话
     *
     * @param conferenceId
     * @return
     */
    @PostMapping("/cancelTalk/{conferenceId}")
    @Operation(summary = "取消对话", description = "取消对话")
    public RestResponse cancelTalk(@PathVariable String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                attendeeService.cancelTalk(conferenceId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.cancelTalk(conferenceId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.cancelTalk(conferenceId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.cancelTalk(conferenceId);
                break;
            }
            case SMC3: {
                attendeeSmc3Service.cancelTalk(conferenceId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.cancelTalk(conferenceId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.cancelTalk(conferenceId);
                break;
            }
        }
        return success();
    }

    /**
     * 开启混音
     */
    @PostMapping("/openMixing/{conferenceId}/{attendeeId}")
    @Operation(summary = "开启混音", description = "开启混音")
    public RestResponse openMixing(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody(required = false) JSONObject params) {
        if (params != null) {
            String downCascadeConferenceId = params.getString("downCascadeConferenceId");
            if (StringUtils.isNotEmpty(downCascadeConferenceId)) {
                BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(downCascadeConferenceId));
                if (downCascadeConferenceContext != null) {
                    BaseAttendee downCascadeAttendee = downCascadeConferenceContext.getAttendeeById(attendeeId);
                    if (downCascadeAttendee != null) {
                        RestResponse restResponse = openMixing(downCascadeConferenceId, downCascadeAttendee.getId(), null);
                        if (!restResponse.isSuccess()) {
                            return restResponse;
                        }
                        restResponse = openMixing(conferenceId, downCascadeConferenceId, null);
                        return restResponse;
                    }
                }
            }
        }
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
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
            case SMC3: {
                attendeeSmc3Service.openMixing(conferenceId, attendeeId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.openMixing(conferenceId, attendeeId);
                break;
            }
            case MCU_TENCENT: {
                attendeeTencentService.openMixing(conferenceId, attendeeId);
                break;
            }

            case MCU_HWCLOUD: {
                attendeeHwcloudService.openMixing(conferenceId, attendeeId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.openMixing(conferenceId, attendeeId);
                break;
            }
        }
        return success();
    }

    /**
     * 关闭混音
     */
    @PostMapping("/closeMixing/{conferenceId}/{attendeeId}")
    @Operation(summary = "关闭混音", description = "关闭混音")
    public RestResponse closeMixing(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody(required = false) JSONObject params) {
        if (params != null) {
            String downCascadeConferenceId = params.getString("downCascadeConferenceId");
            if (StringUtils.isNotEmpty(downCascadeConferenceId)) {
                BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(downCascadeConferenceId));
                if (downCascadeConferenceContext != null) {
                    BaseAttendee downCascadeAttendee = downCascadeConferenceContext.getAttendeeById(attendeeId);
                    if (downCascadeAttendee != null) {
                        RestResponse restResponse = closeMixing(downCascadeConferenceId, downCascadeAttendee.getId(), null);
                        return restResponse;
                    }
                }
            }
        }
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
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
            case SMC3: {
                attendeeSmc3Service.closeMixing(conferenceId, attendeeId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.closeMixing(conferenceId, attendeeId);
                break;
            }
            case MCU_TENCENT: {
                attendeeTencentService.closeMixing(conferenceId, attendeeId);
                break;
            }

            case MCU_HWCLOUD: {
                attendeeHwcloudService.closeMixing(conferenceId, attendeeId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.closeMixing(conferenceId, attendeeId);
                break;
            }
        }
        return success();
    }

    /**
     * 开启混音
     */
    @PostMapping("/openMixing/{conferenceId}")
    @Operation(summary = "开启混音", description = "开启混音")
    public RestResponse openMixing(@PathVariable String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
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
            case SMC3: {
                attendeeSmc3Service.openMixing(conferenceId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.openMixing(conferenceId);
                break;
            }
            case MCU_TENCENT: {
                attendeeTencentService.openMixing(conferenceId);
                break;
            }

            case MCU_HWCLOUD: {
                attendeeHwcloudService.openMixing(conferenceId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.openMixing(conferenceId);
                break;
            }
        }
        return success();
    }

    /**
     * 关闭混音
     */
    @PostMapping("/closeMixing/{conferenceId}")
    @Operation(summary = "关闭混音", description = "关闭混音")
    public RestResponse closeMixing(@PathVariable String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
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
            case SMC3: {
                attendeeSmc3Service.closeMixing(conferenceId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.closeMixing(conferenceId);
                break;
            }
            case MCU_TENCENT: {
                attendeeTencentService.closeMixing(conferenceId);
                break;
            }
            case MCU_HWCLOUD: {
                attendeeHwcloudService.closeMixing(conferenceId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.closeMixing(conferenceId);
                break;
            }
        }
        return success();
    }

    @PostMapping("/acceptRaiseHand/{conferenceId}/{attendeeId}")
    @Operation(summary = "拒绝举手", description = "同意对话")
    public RestResponse acceptRaiseHand(@PathVariable String conferenceId, @PathVariable String attendeeId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                attendeeService.acceptRaiseHand(conferenceId, attendeeId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.acceptRaiseHand(conferenceId, attendeeId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.acceptRaiseHand(conferenceId, attendeeId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.acceptRaiseHand(conferenceId, attendeeId);
                break;
            }
            case SMC3: {
                attendeeSmc3Service.acceptRaiseHand(conferenceId, attendeeId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.acceptRaiseHand(conferenceId, attendeeId);
                break;
            }
            case MCU_HWCLOUD: {
                attendeeHwcloudService.acceptRaiseHand(conferenceId,attendeeId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.acceptRaiseHand(conferenceId, attendeeId);
                break;
            }
        }
        return success();
    }

    /**
     * 拒绝举手
     *
     * @param conferenceId
     * @param attendeeId
     * @author sinhy
     * @since 2021-12-07 10:27
     */
    @PostMapping("/rejectRaiseHand/{conferenceId}/{attendeeId}")
    @Operation(summary = "拒绝举手", description = "拒绝对话")
    public RestResponse rejectRaiseHand(@PathVariable String conferenceId, @PathVariable String attendeeId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                attendeeService.rejectRaiseHand(conferenceId, attendeeId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.rejectRaiseHand(conferenceId, attendeeId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.rejectRaiseHand(conferenceId, attendeeId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.rejectRaiseHand(conferenceId, attendeeId);
                break;
            }
            case SMC3: {
                attendeeSmc3Service.rejectRaiseHand(conferenceId, attendeeId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.rejectRaiseHand(conferenceId, attendeeId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.rejectRaiseHand(conferenceId, attendeeId);
                break;
            }

        }
        return success();
    }

    /**
     * 开启镜头
     */
    @PostMapping("/openCamera/{conferenceId}")
    @Operation(summary = "开启镜头", description = "全员开启镜头")
    public RestResponse openCamera(@PathVariable String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
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
            case SMC3: {
                attendeeSmc3Service.openCamera(conferenceId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.openCamera(conferenceId);
                break;
            }

            case MCU_TENCENT: {
                attendeeTencentService.openCamera(conferenceId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.openCamera(conferenceId);
                break;
            }
        }
        return success();
    }

    /**
     * 关闭镜头
     */
    @PostMapping("/closeCamera/{conferenceId}")
    @Operation(summary = "关闭镜头", description = "全员关闭镜头")
    public RestResponse closeCamera(@PathVariable String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
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
            case SMC3: {
                attendeeSmc3Service.closeCamera(conferenceId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.closeCamera(conferenceId);
                break;
            }
            case MCU_TENCENT: {
                attendeeTencentService.closeCamera(conferenceId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.closeCamera(conferenceId);
                break;
            }
        }
        return success();
    }

    /**
     * 单个开镜
     */
    @PostMapping("/openCamera/{conferenceId}/{attendeeId}")
    @Operation(summary = "单个开镜", description = "开启镜头")
    public RestResponse openCamera(@PathVariable String conferenceId, @PathVariable String attendeeId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
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
            case SMC3: {
                attendeeSmc3Service.openCamera(conferenceId, attendeeId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.openCamera(conferenceId, attendeeId);
                break;
            }
            case MCU_TENCENT: {
                attendeeTencentService.openCamera(conferenceId,attendeeId);
                break;
            }
            case MCU_HWCLOUD: {
                attendeeHwcloudService.openCamera(conferenceId,attendeeId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.openCamera(conferenceId, attendeeId);
                break;
            }
        }
        return success();
    }

    /**
     * 单个关镜
     */
    @PostMapping("/closeCamera/{conferenceId}/{attendeeId}")
    @Operation(summary = "单个关镜", description = "关闭镜头")
    public RestResponse closeCamera(@PathVariable String conferenceId, @PathVariable String attendeeId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
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
            case SMC3: {
                attendeeSmc3Service.closeCamera(conferenceId, attendeeId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.closeCamera(conferenceId, attendeeId);
                break;
            }
            case MCU_TENCENT: {
                attendeeTencentService.closeCamera(conferenceId,attendeeId);
                break;
            }
            case MCU_HWCLOUD: {
                attendeeHwcloudService.closeCamera(conferenceId,attendeeId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.closeCamera(conferenceId, attendeeId);
                break;
            }
        }
        return success();
    }

    /**
     * 单个开扬声器
     */
    @PostMapping("/openSpeaker/{conferenceId}/{attendeeId}")
    @Operation(summary = "单个开扬声器", description = "开启扬声器")
    public RestResponse openSpeaker(@PathVariable String conferenceId, @PathVariable String attendeeId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {

            case SMC3: {
                attendeeSmc3Service.openSpeaker(conferenceId, attendeeId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.openSpeaker(conferenceId, attendeeId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.openSpeaker(conferenceId, attendeeId);
                break;
            }

        }
        return success();
    }

    /**
     * 单个关闭扬声器
     */
    @PostMapping("/closeSpeaker/{conferenceId}/{attendeeId}")
    @Operation(summary = "单个关闭扬声器", description = "关闭扬声器")
    public RestResponse closeSpeaker(@PathVariable String conferenceId, @PathVariable String attendeeId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {

            case SMC3: {
                attendeeSmc3Service.closeSpeaker(conferenceId, attendeeId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.closeSpeaker(conferenceId, attendeeId);
                break;
            }

            case MCU_ZTE: {
                attendeeForMcuZteService.closeSpeaker(conferenceId, attendeeId);
                break;
            }

        }
        return success();
    }

    /**
     * 开启显示器
     */
    @PostMapping("/openDisplayDevice/{conferenceId}")
    @Operation(summary = "开启镜头", description = "全员开启显示画面")
    public RestResponse openDisplayDevice(@PathVariable String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
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
            case SMC3: {
                attendeeSmc3Service.openDisplayDevice(conferenceId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.openDisplayDevice(conferenceId);
                break;
            }
        }
        return success();
    }

    /**
     * 关闭显示器
     */
    @PostMapping("/closeDisplayDevice/{conferenceId}")
    @Operation(summary = "关闭镜头", description = "全员关闭显示画面")
    public RestResponse closeDisplayDevice(@PathVariable String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
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
            case SMC3: {
                attendeeSmc3Service.closeDisplayDevice(conferenceId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.closeDisplayDevice(conferenceId);
                break;
            }
        }
        return success();
    }

    /**
     * 单个开显示器
     */
    @PostMapping("/openDisplayDevice/{conferenceId}/{attendeeId}")
    @Operation(summary = "单个开镜", description = "开启显示画面")
    public RestResponse openDisplayDevice(@PathVariable String conferenceId, @PathVariable String attendeeId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
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
            case SMC3: {
                attendeeSmc3Service.openDisplayDevice(conferenceId, attendeeId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.openDisplayDevice(conferenceId, attendeeId);
                break;
            }

            case MCU_HWCLOUD: {
                attendeeHwcloudService.openDisplayDevice(conferenceId, attendeeId);
                break;
            }
        }
        return success();
    }

    /**
     * 单个关显示器
     */
    @PostMapping("/closeDisplayDevice/{conferenceId}/{attendeeId}")
    @Operation(summary = "单个关镜", description = "关闭显示画面")
    public RestResponse closeDisplayDevice(@PathVariable String conferenceId, @PathVariable String attendeeId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
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
            case SMC3: {
                attendeeSmc3Service.closeDisplayDevice(conferenceId, attendeeId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.closeDisplayDevice(conferenceId, attendeeId);
                break;
            }

            case MCU_HWCLOUD: {
                attendeeHwcloudService.closeDisplayDevice(conferenceId, attendeeId);
                break;
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
            case SMC3: {
                return success(attendeeSmc3Service.takeSnapshot(conferenceId, attendeeId, params));
            }
            case SMC2: {
                return success(attendeeSmc2Service.takeSnapshot(conferenceId, attendeeId, params));
            }
        }
        return success();
    }

    /**
     * 摄像机控制
     */
    @PostMapping("/cameraControl/{conferenceId}/{attendeeId}")
    @Operation(summary = "摄像机控制", description = "镜头控制")
    public RestResponse cameraControl(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody JSONObject params) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                attendeeService.cameraControl(conferenceId, attendeeId, params);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.cameraControl(conferenceId, attendeeId, params);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.cameraControl(conferenceId, attendeeId, params);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.cameraControl(conferenceId, attendeeId, params);
                break;
            }
            case SMC3: {
                attendeeSmc3Service.cameraControl(conferenceId, attendeeId, params);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.cameraControl(conferenceId, attendeeId, params);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.cameraControl(conferenceId, attendeeId, params);
                break;
            }
        }
        return success();
    }

    /**
     * 获取会场摄像头信息
     */
    @GetMapping("/getCameraInfo/{conferenceId}/{attendeeId}")
    @Operation(summary = "获取会场摄像头信息", description = "获取会场摄像头信息")
    public RestResponse getParticipantCameraInfo(@PathVariable String conferenceId, @PathVariable String attendeeId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case MCU_ZTE: {
                Object cameraInfo = attendeeForMcuZteService.getCameraInfo(conferenceId, attendeeId);
                return RestResponse.success(cameraInfo);
            }
        }
        return success();
    }

    @PutMapping("/presentationSetting/{conferenceId}/{attendeeId}")
    @Operation(summary = "辅流设置", description = "辅流设置")
    public RestResponse presentationSetting(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody List<BaseFixedParamValue> params) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
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
            case SMC3: {
                attendeeSmc3Service.presentationSetting(conferenceId, attendeeId, params);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.presentationSetting(conferenceId, attendeeId, params);
                break;
            }
        }
        return success();
    }

    @PutMapping("/mainSetting/{conferenceId}/{attendeeId}")
    @Operation(summary = "主流设置", description = "主流设置")
    public RestResponse mainSetting(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody List<BaseFixedParamValue> params) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
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
            case SMC3: {
                attendeeSmc3Service.mainSetting(conferenceId, attendeeId, params);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.mainSetting(conferenceId, attendeeId, params);
                break;
            }
        }
        return success();
    }

    @PutMapping("/subtitle/{conferenceId}/{attendeeId}")
    @Operation(summary = "字幕设置", description = "字幕设置")
    public RestResponse subtitle(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody List<BaseFixedParamValue> params) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                attendeeService.subtitle(conferenceId, attendeeId, params);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.subtitle(conferenceId, attendeeId, params);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.subtitle(conferenceId, attendeeId, params);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.subtitle(conferenceId, attendeeId, params);
                break;
            }
            case SMC3: {
                attendeeSmc3Service.subtitle(conferenceId, attendeeId, params);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.subtitle(conferenceId, attendeeId, params);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.subtitle(conferenceId, attendeeId, params);
                break;
            }
        }
        return success();
    }

    @PutMapping("/setBanner/{conferenceId}/{attendeeId}")
    @Operation(summary = "设置横幅", description = "设置横幅")
    public RestResponse setBanner(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody JSONObject params) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                attendeeService.setBanner(conferenceId, attendeeId, params);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.setBanner(conferenceId, attendeeId, params);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.setBanner(conferenceId, attendeeId, params);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.setBanner(conferenceId, attendeeId, params);
                break;
            }
            case SMC3: {
                attendeeSmc3Service.setBanner(conferenceId, attendeeId, params);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.setBanner(conferenceId, attendeeId, params);
                break;
            }
        }
        return success();
    }

    @PutMapping("/layoutSetting/{conferenceId}/{attendeeId}")
    @Operation(summary = "布局设置", description = "修改多画面")
    public RestResponse layoutSetting(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody List<BaseFixedParamValue> params) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
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
            case SMC3: {
                attendeeSmc3Service.layoutSetting(conferenceId, attendeeId, params);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.layoutSetting(conferenceId, attendeeId, params);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.layoutSetting(conferenceId, attendeeId, params);
                break;
            }
        }
        return success();
    }

    @PutMapping("/recordStreamSetting/{conferenceId}/{attendeeId}")
    @Operation(summary = "直播录制设置", description = "直播录制设置")
    public RestResponse recordStreamSetting(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody List<BaseFixedParamValue> params) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
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
            case SMC3: {
                attendeeSmc3Service.recordStreamSetting(conferenceId, attendeeId, params);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.recordStreamSetting(conferenceId, attendeeId, params);
                break;
            }
        }
        return success();
    }

    @PutMapping("/advanceSetting/{conferenceId}/{attendeeId}")
    @Operation(summary = "高级设置", description = "高级设置")
    public RestResponse advanceSetting(@PathVariable String conferenceId, @PathVariable String attendeeId, @RequestBody List<BaseFixedParamValue> params) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                attendeeService.advanceSetting(conferenceId, attendeeId, params);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.advanceSetting(conferenceId, attendeeId, params);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.advanceSetting(conferenceId, attendeeId, params);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.advanceSetting(conferenceId, attendeeId, params);
                break;
            }
            case SMC3: {
                attendeeSmc3Service.advanceSetting(conferenceId, attendeeId, params);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.advanceSetting(conferenceId, attendeeId, params);
                break;
            }
        }
        return success();
    }

    @GetMapping("/attendeeCallLegSetting/{conferenceId}/{attendeeId}")
    @Operation(summary = "获取CallLeg")
    public RestResponse attendeeCallLegSetting(@PathVariable String conferenceId, @PathVariable String attendeeId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                return success(attendeeService.attendeeCallLegSetting(conferenceId, attendeeId));
            }
            case MCU_ZJ: {
                return success(attendeeForMcuZjService.attendeeCallLegSetting(conferenceId, attendeeId));
            }
            case MCU_PLC: {
                return success(attendeeForMcuPlcService.attendeeCallLegSetting(conferenceId, attendeeId));
            }
            case MCU_KDC: {
                return success(attendeeForMcuKdcService.attendeeCallLegSetting(conferenceId, attendeeId));
            }
            case SMC3: {
                return success(attendeeSmc3Service.attendeeCallLegSetting(conferenceId, attendeeId));
            }
            case SMC2: {
                return success(attendeeSmc2Service.attendeeCallLegSetting(conferenceId, attendeeId));
            }
        }
        return RestResponse.fail();
    }

    /**
     * 消息发送
     */
    @PostMapping("/sendMessage/{conferenceId}")
    @Operation(summary = "消息发送", description = "消息发送")
    public RestResponse sendMessage(@PathVariable String conferenceId, @RequestBody JSONObject jsonObject) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                attendeeService.sendMessage(conferenceId, jsonObject);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.sendMessage(conferenceId, jsonObject);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.sendMessage(conferenceId, jsonObject);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.sendMessage(conferenceId, jsonObject);
                break;
            }
            case SMC3: {
                return success(attendeeSmc3Service.sendMessage(conferenceId, jsonObject));
            }
            case SMC2: {
                attendeeSmc2Service.sendMessage(conferenceId, jsonObject);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.sendMessage(conferenceId, jsonObject);
                break;
            }
        }
        // 下级会议设置字幕
        if (mcuType != McuType.MCU_ZJ) {
            BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
            List<BaseAttendee> mcuAttendees = baseConferenceContext.getMcuAttendees();
            for (BaseAttendee mcuAttendee : mcuAttendees) {
                try {
                    sendMessage(mcuAttendee.getId(), jsonObject);
                } catch (Exception e) {
                }
            }
        }
        return success();
    }


    /**
     * 横幅设置发送
     */
    @PostMapping("/setMessageBannerText/{conferenceId}")
    @Operation(summary = "横幅设置发送", description = "设置横幅")
    public RestResponse setMessageBannerText(@PathVariable String conferenceId, @RequestBody JSONObject jsonObject) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                attendeeService.setMessageBannerText(conferenceId, jsonObject);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.setMessageBannerText(conferenceId, jsonObject);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.setMessageBannerText(conferenceId, jsonObject);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.setMessageBannerText(conferenceId, jsonObject);
                break;
            }
            case SMC3: {
                return success(attendeeSmc3Service.setMessageBannerText(conferenceId, jsonObject));
            }
            case SMC2: {
                attendeeSmc2Service.setMessageBannerText(conferenceId, jsonObject);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.setMessageBannerText(conferenceId, jsonObject);
                break;
            }
        }
        return success();
    }

    /**
     * 批量设置横幅
     */
    @PostMapping("/sendBanner/{conferenceId}")
    @Operation(summary = "批量设置横幅", description = "设置横幅")
    public RestResponse sendBanner(@PathVariable String conferenceId, @RequestBody JSONObject jsonObject) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                attendeeService.sendBanner(conferenceId, jsonObject);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.sendBanner(conferenceId, jsonObject);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.sendBanner(conferenceId, jsonObject);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.sendBanner(conferenceId, jsonObject);
                break;
            }
            case SMC3: {
                attendeeSmc3Service.sendBanner(conferenceId, jsonObject);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.sendBanner(conferenceId, jsonObject);
                break;
            }
        }
        return success();
    }

    /**
     * 轮询
     */
    @PostMapping("/polling/{conferenceId}")
    @Operation(summary = "轮询", description = "轮询")
    public RestResponse polling(@PathVariable String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                attendeeService.polling(conferenceId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.polling(conferenceId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.polling(conferenceId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.polling(conferenceId);
                break;
            }
            case SMC3: {
                attendeeSmc3Service.polling(conferenceId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.polling(conferenceId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.polling(conferenceId);
                break;
            }
        }
        return success();
    }

    /**
     * 轮询暂停
     *
     * @author sinhy
     * @since 2022-04-11 10:09  void
     */
    @PostMapping("/pollingPause/{conferenceId}")
    @Operation(summary = "轮询暂停", description = "暂停轮询")
    public RestResponse pollingPause(@PathVariable String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                attendeeService.pollingPause(conferenceId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.pollingPause(conferenceId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.pollingPause(conferenceId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.pollingPause(conferenceId);
                break;
            }
            case SMC3: {
                attendeeSmc3Service.pollingPause(conferenceId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.pollingPause(conferenceId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.pollingPause(conferenceId);
                break;
            }
        }
        return success();
    }

    /**
     * 轮询恢复运行
     *
     * @author sinhy
     * @since 2022-04-11 10:09  void
     */
    @PostMapping("/pollingResume/{conferenceId}")
    @Operation(summary = "轮询恢复运行", description = "恢复轮询")
    public RestResponse pollingResume(@PathVariable String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                attendeeService.pollingResume(conferenceId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.pollingResume(conferenceId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.pollingResume(conferenceId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.pollingResume(conferenceId);
                break;
            }
            case SMC3: {
                attendeeSmc3Service.pollingResume(conferenceId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.pollingResume(conferenceId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.pollingResume(conferenceId);
                break;
            }
        }
        return success();
    }

    /**
     * 取消轮询
     */
    @PostMapping("/cancelPolling/{conferenceId}")
    @Operation(summary = "取消轮询", description = "取消轮询")
    public RestResponse cancelPolling(@PathVariable String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                attendeeService.cancelPolling(conferenceId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.cancelPolling(conferenceId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.cancelPolling(conferenceId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.cancelPolling(conferenceId);
                break;
            }
            case SMC3: {
                attendeeSmc3Service.cancelPolling(conferenceId);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.cancelPolling(conferenceId);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.cancelPolling(conferenceId);
                break;
            }
        }
        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
        List<BaseAttendee> mcuAttendees = conferenceContext.getMcuAttendees();
        for (BaseAttendee mcuAttendee : mcuAttendees) {
            BaseConferenceContext mcuConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(mcuAttendee.getCascadeConferenceId()));
            if (mcuConferenceContext != null) {
                try {
                    ConferenceCascadeHandler.defaultChooseSee(mcuAttendee.getId());
                } catch (Exception e) {
                }
            }
        }
        return success();
    }

    /**
     * 批量邀请
     */
    @PostMapping("/batchInvite/{conferenceId}")
    @Operation(summary = "批量邀请", description = "添加会场")
    public RestResponse batchInvite(@PathVariable String conferenceId, @RequestBody List<Object> attendeeList) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        List<Long> terminalIds=null;
        List<CropDirAttendeeHwcloud> attendeeHwclouds=null;
        List<BusiTerminal> attendeeSmc3s=null;
        if(Objects.equals(mcuType,McuType.MCU_HWCLOUD)){
            attendeeHwclouds=JSONObject.parseArray(JSONObject.toJSONString(attendeeList),CropDirAttendeeHwcloud.class);

        }else if(Objects.equals(mcuType,McuType.SMC3)){

            attendeeSmc3s=JSONObject.parseArray(JSONObject.toJSONString(attendeeList), BusiTerminal.class);
            if(CollectionUtils.isEmpty(attendeeSmc3s)){
                terminalIds= JSONObject.parseArray(JSONObject.toJSONString(attendeeList),Long.class);
            }

        }else {
            terminalIds= JSONObject.parseArray(JSONObject.toJSONString(attendeeList),Long.class);
        }



        switch (mcuType) {
            case FME: {
                attendeeService.invite(conferenceId, terminalIds);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.invite(conferenceId, terminalIds);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.invite(conferenceId, terminalIds);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.invite(conferenceId, terminalIds);
                break;
            }
            case SMC3: {
                if(CollectionUtils.isNotEmpty(attendeeSmc3s)){
                    attendeeSmc3Service.batchInvite(conferenceId, attendeeSmc3s);
                }else {
                    attendeeSmc3Service.invite(conferenceId, terminalIds);
                }
                break;
            }
            case SMC2: {
                attendeeSmc2Service.invite(conferenceId, terminalIds);
                break;
            }
            case MCU_HWCLOUD: {
                attendeeHwcloudService.batchInvite(conferenceId, attendeeHwclouds);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.invite(conferenceId, terminalIds);
                break;
            }
        }
        return success();
    }

    /**
     * uri邀请
     */
    @PostMapping("/inviteByUri/{conferenceId}")
    @Operation(summary = "uri邀请", description = "邀请临时会场")
    public RestResponse invite(@PathVariable String conferenceId, @RequestBody JSONObject jsonObj) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                attendeeService.invite(conferenceId, jsonObj);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.invite(conferenceId, jsonObj);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.invite(conferenceId, jsonObj);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.invite(conferenceId, jsonObj);
                break;
            }
            case SMC3: {
                attendeeSmc3Service.invite(conferenceId, jsonObj);
                break;
            }
            case SMC2: {
                attendeeSmc2Service.invite(conferenceId, jsonObj);
                break;
            }
            case MCU_ZTE: {
                attendeeForMcuZteService.invite(conferenceId, jsonObj);
                break;
            }
        }
        return success();
    }


    /**
     * 修改会场名称
     */
    @PutMapping("/changeAttendeeName/{conferenceId}")
    @Operation(summary = "修改会场名称", description = "修改与会者名称")
    public RestResponse changeAttendeeName(@PathVariable String conferenceId, @RequestBody JSONObject jsonObj) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case SMC3: {
                attendeeSmc3Service.changeAttendeeName(conferenceId, jsonObj);
                break;
            }

            case MCU_TENCENT: {
                attendeeTencentService.changeAttendeeName(conferenceId, jsonObj);
                break;
            }

            case MCU_HWCLOUD: {
                attendeeHwcloudService.changeAttendeeName(conferenceId, jsonObj);
                break;
            }

        }
        return success();
    }

    /**
     * 轮询获取快照
     */
    @PostMapping("/takeSnapshot/polling/{conferenceId}")
    @Operation(summary = "获取快照")
    public RestResponse takeSnapshotPolling(@PathVariable String conferenceId, @RequestBody JSONObject params) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                return success(attendeeService.takeSnapshotPolling(conferenceId, params));
            }

        }
        return success();
    }

    /**
     * 锁定会议材料(取消)
     */
    @PatchMapping("/lockPresenter/{conferenceId}/{participantId}/{lock}")
    @Operation(summary = "锁定会议材料")
    public RestResponse lockPresenter(@PathVariable String conferenceId, @PathVariable String participantId, @PathVariable Boolean lock) {

        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case SMC3: {
                return success(attendeeSmc3Service.lockPresenter(conferenceId, participantId, lock));
            }
            case SMC2: {
                return success(attendeeSmc2Service.lockPresenter(conferenceId, participantId, lock));
            }

        }
        return success();

    }


    /**
     * 锁定视频源
     *
     * @param conferenceControllerRequest
     */
    @PatchMapping("/videoSwitchAttribute/lock")
    @Operation(summary = "锁定视频源")
    public RestResponse videoSwitchAttribute(@RequestBody ConferenceControllerRequest conferenceControllerRequest) {
        String conferenceId = conferenceControllerRequest.getConferenceId();
        String participantId = conferenceControllerRequest.getParticipantId();
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case SMC3: {
                return success(attendeeSmc3Service.videoSwitchAttribute(conferenceId, participantId, true));
            }
            case SMC2: {
                return success(attendeeSmc2Service.videoSwitchAttribute(conferenceId, participantId, true));
            }
            case MCU_HWCLOUD: {
                return success(attendeeHwcloudService.videoSwitchAttribute(conferenceId, participantId, true));
            }

        }
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
        String conferenceId = conferenceControllerRequest.getConferenceId();
        String participantId = conferenceControllerRequest.getParticipantId();
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case SMC3: {
                return success(attendeeSmc3Service.videoSwitchAttribute(conferenceId, participantId, false));
            }
            case SMC2: {
                return success(attendeeSmc2Service.videoSwitchAttribute(conferenceId, participantId, false));
            }
            case MCU_HWCLOUD: {
                return success(attendeeHwcloudService.videoSwitchAttribute(conferenceId, participantId, false));
            }

        }
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

        String conferenceId = conferenceControllerRequest.getConferenceId();
        String participantId = conferenceControllerRequest.getParticipantId();
        int volume = conferenceControllerRequest.getVolume();
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case SMC3: {
                return success(attendeeSmc3Service.setVolume(conferenceId, participantId, volume));
            }
            case SMC2: {
                return success(attendeeSmc2Service.setVolume(conferenceId, participantId, volume));
            }

        }
        return RestResponse.success();

    }




    /**
     * 设置联席主持人
     *
     */
    @PutMapping("/cohost/{conferenceId}/{attendeeId}/{action}")
    @Operation(summary = "设置联席主持人")
    public RestResponse cohost(@PathVariable String conferenceId, @PathVariable String attendeeId, @PathVariable Boolean action) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();

        switch (mcuType){
            case MCU_TENCENT:{
                try {
                    attendeeTencentService.cohost(conferenceId,attendeeId,action);
                } catch (Exception e) {
                      logger.info(e.getMessage());
                    return RestResponse.fail(e.getMessage());
                }
                break;
            }
            case MCU_HWCLOUD:{
                try {
                    attendeeHwcloudService.cohost(conferenceId,attendeeId,action);
                } catch (Exception e) {
                      logger.info(e.getMessage());
                    return RestResponse.fail(e.getMessage());
                }
                break;
            }
        }
        return RestResponse.success();

    }

    @PutMapping("/chair/{conferenceId}/{attendeeId}/{action}")
    @Operation(summary = "设置主持人")
    public RestResponse applyChair(@PathVariable String conferenceId, @PathVariable String attendeeId, @PathVariable Boolean action) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();

        switch (mcuType){

            case MCU_HWCLOUD:{
                try {
                    attendeeHwcloudService.applyChair(conferenceId,attendeeId,action);
                } catch (Exception e) {
                    logger.info(e.getMessage());
                    return RestResponse.fail(e.getMessage());
                }
                break;
            }
        }
        return RestResponse.success();

    }

    @GetMapping("/rooms/{deptId}")
    @Operation(summary = "获取rooms列表")
    public RestResponse rooms(@PathVariable Long deptId, @RequestParam(required = false,defaultValue = "1") Integer pageIndex,@RequestParam(required = false,defaultValue = "20") Integer pageSize,@RequestParam(required = false) String meetingRoomName) {
        return RestResponse.success(attendeeTencentService.rooms(deptId,pageIndex,pageSize,meetingRoomName));
    }

    @PutMapping("/rooms/invited/{conferenceId}")
    @Operation(summary = "获取rooms列表")
    public RestResponse roomsInvite(@PathVariable String conferenceId,@RequestBody  List<String> roomIds) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType){
            case MCU_TENCENT:{
                try {
                    return RestResponse.success(attendeeTencentService.roomsInvite(conferenceId,roomIds));
                } catch (Exception e) {
                    logger.info(e.getMessage());
                    return RestResponse.fail(e.getMessage());
                }
            }
        }
        return RestResponse.success();
    }
}
