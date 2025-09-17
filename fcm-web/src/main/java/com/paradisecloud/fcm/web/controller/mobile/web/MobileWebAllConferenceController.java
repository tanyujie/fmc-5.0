package com.paradisecloud.fcm.web.controller.mobile.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huaweicloud.sdk.meeting.v1.model.MultiPicDisplayDO;
import com.huaweicloud.sdk.meeting.v1.model.PicInfoNotify;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.constant.ConfigConstant;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.model.ViewTemplateConference;
import com.paradisecloud.fcm.ding.cache.DingConferenceContext;
import com.paradisecloud.fcm.ding.service2.interfaces.IBusiDingConferenceService;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.IDefaultAttendeeOperationPackageService;
import com.paradisecloud.fcm.fme.conference.interfaces.ITemplateConferenceStartService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContextCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudWebSocketMessagePusher;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.ImageTypeEnum;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.PresetMultiPicReqDto;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiHwcloudConferenceService;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcConferenceService;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IDefaultAttendeeOperationPackageForMcuKdcService;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcConferenceService;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IDefaultAttendeeOperationPackageForMcuPlcService;
import com.paradisecloud.fcm.mcu.zj.attendee.model.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.mcu.zj.attendee.model.operation.DefaultAttendeeOperationForGuest;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjConferenceService;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IDefaultAttendeeOperationPackageForMcuZjService;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.cache.BaseWebSocketMessagePusher;
import com.paradisecloud.fcm.service.conference.cascade.ConferenceCascadeHandler;
import com.paradisecloud.fcm.service.conference.task.EndDownCascadeConferenceTask;
import com.paradisecloud.fcm.service.conference.task.StartDownCascadeConferenceTask;
import com.paradisecloud.fcm.service.conference.utils.AllConferenceContextUtils;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContextCache;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiSmc2ConferenceService;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IDefaultAttendeeSmc2OperationPackageService;
import com.paradisecloud.fcm.tencent.busi.attende.AttendeeTencent;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContextCache;
import com.paradisecloud.fcm.tencent.model.reponse.WaitingRoomResponse;
import com.paradisecloud.fcm.tencent.model.request.ModifyConferenceRequest;
import com.paradisecloud.fcm.tencent.model.vo.WaitingRoomParticipantSetting;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiTencentConferenceService;
import com.paradisecloud.fcm.terminal.fs.cache.LiveBridgeCache;
import com.paradisecloud.fcm.web.task.ConferenceTakeSnapshotPdfTask;
import com.paradisecloud.fcm.zte.cache.McuZteConferenceContextCache;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.service.interfaces.IBusiMcuZteConferenceService;
import com.paradisecloud.fcm.zte.service.interfaces.IDefaultAttendeeOperationPackageForMcuZteService;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.operation.ChangeMasterAttendeeOperation;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.service.interfaces.IBusiSmc3ConferenceService;
import com.paradisecloud.smc3.service.interfaces.IDefaultAttendeeSmc3OperationPackageService;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.service.ISysConfigService;
import com.paradisecloud.system.service.ISysDeptService;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.zte.m900.bean.ParticipantStatusV3;
import com.zte.m900.request.GetParticipantStatusV3Request;
import com.zte.m900.response.GetParticipantStatusV3Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 活跃会议室信息，用于存放活跃的会议室Controller
 *
 * @author lilinhai
 * @date 2021-02-02
 */
@RestController
@RequestMapping("/mobileWeb/mcu/all/conference")
@Tag(name = "活跃会议室信息，用于存放活跃的会议室")
public class MobileWebAllConferenceController extends BaseController
{
    @Resource
    private IBusiConferenceService busiConferenceService;
    @Resource
    private ITemplateConferenceStartService templateConferenceStartService;
    @Resource
    private IDefaultAttendeeOperationPackageService defaultAttendeeOperationPackageService;
    @Resource
    private IBusiMcuZjConferenceService busiMcuZjConferenceService;
    @Resource
    private IDefaultAttendeeOperationPackageForMcuZjService defaultAttendeeOperationPackageForMcuZjService;
    @Resource
    private IBusiMcuPlcConferenceService busiMcuPlcConferenceService;
    @Resource
    private IBusiMcuZteConferenceService busiMcuZteConferenceService;
    @Resource
    private IDefaultAttendeeOperationPackageForMcuPlcService defaultAttendeeOperationPackageForMcuPlcService;
    @Resource
    private IDefaultAttendeeOperationPackageForMcuZteService defaultAttendeeOperationPackageForMcuZteService;
    @Resource
    private IBusiMcuKdcConferenceService busiMcuKdcConferenceService;
    @Resource
    private IDefaultAttendeeOperationPackageForMcuKdcService defaultAttendeeOperationPackageForMcuKdcService;
    @Resource
    private IBusiSmc3ConferenceService busiSmc3ConferenceService;
    @Resource
    private IDefaultAttendeeSmc3OperationPackageService defaultAttendeeSmc3OperationPackageService;
    @Resource
    private IBusiTemplateConferenceService iBusiTemplateConferenceService;
    @Resource
    private IBusiSmc2ConferenceService busiSmc2ConferenceService;
    @Resource
    private IBusiTencentConferenceService busiTencentConferenceService;
    @Resource
    private IDefaultAttendeeSmc2OperationPackageService defaultAttendeeSmc2OperationPackageService;
    @Resource
    private ISysConfigService sysConfigService;
    @Resource
    private ISysDeptService sysDeptService;

    @Resource
    private IBusiDingConferenceService busiDingConferenceService;

    @Resource
    private IBusiHwcloudConferenceService busiHwcloudConferenceService;

    @GetMapping("/customLayoutTemplates/{deptId}")
    @Operation(summary = "所有布局集合")
    public RestResponse getLayoutTemplates(@PathVariable Long deptId, @RequestParam("mcuType") String mcuTypeStr)
    {
        McuType mcuType = McuType.convert(mcuTypeStr);
        switch (mcuType) {
            case FME: {
                return RestResponse.success(busiConferenceService.getLayoutTemplates(deptId));
            }
            case MCU_ZJ: {
                return RestResponse.success(busiMcuZjConferenceService.getLayoutTemplates(deptId));
            }
            case MCU_PLC: {
                return RestResponse.success(busiMcuPlcConferenceService.getLayoutTemplates(deptId));
            }
            case MCU_KDC: {
                return RestResponse.success(busiMcuKdcConferenceService.getLayoutTemplates(deptId));
            }
            case SMC3: {
                return RestResponse.success(busiSmc3ConferenceService.getLayoutTemplates(deptId));
            }
            case SMC2: {
                return RestResponse.success(busiSmc2ConferenceService.getLayoutTemplates(deptId));
            }
            case MCU_ZTE: {
                return RestResponse.success(busiMcuZteConferenceService.getLayoutTemplates(deptId));
            }
        }
        return RestResponse.fail();
    }

    @GetMapping("/customLayoutTemplate/{deptId}/{name}")
    @Operation(summary = "指定布局")
    public RestResponse getLayoutTemplate(@PathVariable Long deptId, @PathVariable String name, @RequestParam("mcuType") String mcuTypeStr)
    {
        McuType mcuType = McuType.convert(mcuTypeStr);
        switch (mcuType) {
            case FME: {
                return RestResponse.success(busiConferenceService.getLayoutTemplate(deptId, name));
            }
        }
        return RestResponse.fail();
    }

    /**
     * 新增活跃会议室信息，用于存放活跃的会议室
     */
    @PostMapping("/startByTemplate/{conferenceId}")
    @Operation(summary = "新增活跃会议室信息，用于存放活跃的会议室", description = "开始会议")
    public RestResponse startByTemplate(@PathVariable("conferenceId") String conferenceId)
    {
        String conferenceApprovalEnable = sysConfigService.selectConfigByKey(ConfigConstant.CONFIG_KEY_CONFERENCE_APPROVAL_ENABLE);
        if (ConfigConstant.CONFERENCE_APPROVAL_ENABLED.equals(conferenceApprovalEnable)) {
            return RestResponse.fail("会议已开启审批，请从预约会议菜单创建会议。审批通过后，到时间会自动开始。");
        }
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                templateConferenceStartService.startTemplateConference(id);
                break;
            }
            case MCU_ZJ: {
                busiMcuZjConferenceService.startTemplateConference(id);
                break;
            }
            case MCU_PLC: {
                busiMcuPlcConferenceService.startTemplateConference(id);
                break;
            }
            case MCU_KDC: {
                busiMcuKdcConferenceService.startTemplateConference(id);
                break;
            }
            case SMC3: {
                busiSmc3ConferenceService.startTemplateConference(id);
                break;
            }
            case SMC2: {
                busiSmc2ConferenceService.startTemplateConference(id);
                break;
            }
            case MCU_TENCENT: {
                busiTencentConferenceService.startTemplateConference(id);
                break;
            }
            case MCU_DING: {
                busiDingConferenceService.startTemplateConference(id);
                break;
            }

            case MCU_HWCLOUD: {
                busiHwcloudConferenceService.startTemplateConference(id);
                break;
            }
            case MCU_ZTE: {
                busiMcuZteConferenceService.startTemplateConference(id);
                break;
            }
        }
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (baseConferenceContext != null) {
            StartDownCascadeConferenceTask startDownCascadeConferenceTask = new StartDownCascadeConferenceTask(baseConferenceContext.getId(), 0, baseConferenceContext);
            BeanFactory.getBean(TaskService.class).addTask(startDownCascadeConferenceTask);
        }
        return success(conferenceId);
    }

    @PostMapping("/endConference/{conferenceId}/{endType}")
    @Operation(summary = "挂断会议", description = "结束会议")
    public RestResponse endConference(@PathVariable String conferenceId, @PathVariable int endType)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                busiConferenceService.endConference(conferenceId, endType, EndReasonsType.ADMINISTRATOR_HANGS_UP);

                try {
                    ConferenceContext conferenceContext = (ConferenceContext) baseConferenceContext;

                    ConferenceTakeSnapshotPdfTask conferenceTakeSnapshotPdfTask = new ConferenceTakeSnapshotPdfTask(conferenceContext.getId(), 5000,conferenceContext);
                    conferenceTakeSnapshotPdfTask.start();

                } catch (Exception e) {
                    logger.info("纪要生成 error" + e.getMessage());
                }
                break;
            }
            case MCU_ZJ: {
                busiMcuZjConferenceService.endConference(conferenceId, endType);
                break;
            }
            case MCU_PLC: {
                busiMcuPlcConferenceService.endConference(conferenceId, endType);
                break;
            }
            case MCU_KDC: {
                busiMcuKdcConferenceService.endConference(conferenceId, endType);
                break;
            }
            case SMC3: {
                busiSmc3ConferenceService.endConference(conferenceId, EndReasonsType.ADMINISTRATOR_HANGS_UP);
                break;
            }
            case SMC2: {
                busiSmc2ConferenceService.endConference(conferenceId, EndReasonsType.ADMINISTRATOR_HANGS_UP);
                break;
            }
            case MCU_TENCENT: {
                busiTencentConferenceService.endConference(conferenceId, EndReasonsType.ADMINISTRATOR_HANGS_UP);
                break;
            }

            case MCU_DING: {
                busiDingConferenceService.endConference(conferenceId, EndReasonsType.ADMINISTRATOR_HANGS_UP);
                break;
            }

            case MCU_HWCLOUD: {
                busiHwcloudConferenceService.endConference(conferenceId, EndReasonsType.ADMINISTRATOR_HANGS_UP);
                break;
            }
            case MCU_ZTE: {
                busiMcuZteConferenceService.endConference(conferenceId, endType);
                break;
            }
        }
        if (baseConferenceContext != null) {
            EndDownCascadeConferenceTask endDownCascadeConferenceTask = new EndDownCascadeConferenceTask(conferenceId, 0, baseConferenceContext, endType);
            BeanFactory.getBean(TaskService.class).addTask(endDownCascadeConferenceTask);
        }
        return success();
    }

    @PutMapping("/updateDefaultViewConfigInfo/{conferenceId}")
    @Operation(summary = "修改会议的默认视图，只更新内存", description = "修改多画面布局")
    public RestResponse updateDefaultViewConfigInfo(@PathVariable String conferenceId, @RequestBody JSONObject jsonObj)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (baseConferenceContext != null) {
            if (baseConferenceContext instanceof ConferenceContext) {
                defaultAttendeeOperationPackageService.updateDefaultViewConfigInfo(conferenceId, jsonObj);
            } else if (baseConferenceContext instanceof McuZjConferenceContext) {
                McuZjConferenceContext conferenceContext = (McuZjConferenceContext) baseConferenceContext;
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
            } else if (baseConferenceContext instanceof McuPlcConferenceContext) {
                JSONObject jsonObjectSpeaker = jsonObj.getJSONObject("speakerDefaultViewData");
                defaultAttendeeOperationPackageForMcuPlcService.updateDefaultViewConfigInfo(conferenceId, jsonObjectSpeaker);
            } else if (baseConferenceContext instanceof McuKdcConferenceContext) {
                JSONObject jsonObjectSpeaker = jsonObj.getJSONObject("speakerDefaultViewData");
                defaultAttendeeOperationPackageForMcuKdcService.updateDefaultViewConfigInfo(conferenceId, jsonObjectSpeaker);
            } else if (baseConferenceContext instanceof Smc3ConferenceContext) {
                Smc3ConferenceContext cc = Smc3ConferenceContextCache.getInstance().get(contextKey);
                com.paradisecloud.smc3.busi.operation.AttendeeOperation old = cc.getAttendeeOperation();

                cc.setLastAttendeeOperation(old);
                if(!(old instanceof ChangeMasterAttendeeOperation)){
                    old.cancel();
                }else {
                    ((ChangeMasterAttendeeOperation) old).cancelChooseStatus();
                }
                com.paradisecloud.smc3.busi.operation.AttendeeOperation attendeeOperation = new com.paradisecloud.smc3.busi.DefaultAttendeeOperation(cc,jsonObj);
                cc.setAttendeeOperation(attendeeOperation);
                attendeeOperation.operate();

            } else if (baseConferenceContext instanceof Smc2ConferenceContext) {
                Smc2ConferenceContext cc = Smc2ConferenceContextCache.getInstance().get(contextKey);
                com.paradisecloud.fcm.smc2.model.AttendeeOperation attendeeOperationSmc2Old = cc.getAttendeeOperation();
                cc.setLastAttendeeOperation(attendeeOperationSmc2Old);
                attendeeOperationSmc2Old.cancel();
                com.paradisecloud.fcm.smc2.model.attendee.operation.DefaultAttendeeOperation defaultAttendeeOperation = new com.paradisecloud.fcm.smc2.model.attendee.operation.DefaultAttendeeOperation(cc, jsonObj);
                cc.setAttendeeOperation(defaultAttendeeOperation);
                defaultAttendeeOperation.operate();
            }
            else if (baseConferenceContext instanceof HwcloudConferenceContext) {

                //多画面设置
                if(jsonObj!=null){

                    PresetMultiPicReqDto multiPicInfo = JSONObject.parseObject(jsonObj.toJSONString(), PresetMultiPicReqDto.class);
                    MultiPicDisplayDO multiPicDisplayDO = new MultiPicDisplayDO();
                    multiPicDisplayDO.setImageType(ImageTypeEnum.getByNumberAndMode(multiPicInfo.getPicNum(), multiPicInfo.getMode()).getName());
                    multiPicDisplayDO.setSwitchTime(multiPicInfo.getSwitchTime());
                    multiPicDisplayDO.setManualSet(1);

                    List<PresetMultiPicReqDto.PresetMultiPicRollsDTO> subPicList = multiPicInfo.getSubPicPollInfoList();
                    List<PicInfoNotify>  list=new ArrayList<>();

                    for (int i = 0; i < subPicList.size(); i++) {

                        List<String> ids = new ArrayList<>();

                        PicInfoNotify picInfoNotify = new PicInfoNotify();
                        picInfoNotify.setIndex(i+1);
                        List<PresetMultiPicReqDto.PresetMultiPicRollsDTO.SubPicListDTO> subPicListDTOS = subPicList.get(i).getParticipantIds();
                        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(subPicListDTOS)){
                            picInfoNotify.setShare(subPicListDTOS.get(0).getStreamNumber());

                            for (PresetMultiPicReqDto.PresetMultiPicRollsDTO.SubPicListDTO subPicListDTO : subPicListDTOS) {
                                ids.add(subPicListDTO.getParticipantId());
                            }
                        }

                        picInfoNotify.setId(ids);

                        list.add(picInfoNotify);
                    }
                    multiPicDisplayDO.setSubscriberInPics(list);

                    multiPicDisplayDO.setManualSet(multiPicInfo.getManualSet()==null?1:multiPicInfo.getManualSet());
                    HwcloudConferenceContext cc = HwcloudConferenceContextCache.getInstance().get(contextKey);


                    Boolean autoBroadCast = multiPicInfo.getAutoBroadCast();

                    Boolean multiPicSaveOnly=true;
                    if(autoBroadCast!=null&&autoBroadCast){
                        multiPicSaveOnly=false;
                    }
                    com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation.AttendeeOperation old = cc.getAttendeeOperation();
                    if(old!=null){
                        cc.setLastAttendeeOperation(old);
                        if(!(old instanceof com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation.ChangeMasterAttendeeOperation)){
                            old.cancel();
                        }else {
                            ((com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation.ChangeMasterAttendeeOperation) old).cancelChooseStatus();
                        }
                    }


                    com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation.DefaultAttendeeOperation defaultAttendeeOperation = new com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation.DefaultAttendeeOperation(cc,JSONObject.parseObject(JSON.toJSONString(multiPicDisplayDO)),multiPicSaveOnly);
                    cc.setAttendeeOperation(defaultAttendeeOperation);
                    defaultAttendeeOperation.operate();
                    cc.setMultiPicInfo(multiPicInfo);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("multiPicInfo",multiPicInfo);
                    HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);

                }


            }
            else if (baseConferenceContext instanceof McuZteConferenceContext) {
                defaultAttendeeOperationPackageForMcuZteService.updateDefaultViewConfigInfo(conferenceId, jsonObj);
            }

            // 下级会议重置为默认选看
            List<BaseAttendee> mcuAttendees = baseConferenceContext.getMcuAttendees();
            for (BaseAttendee mcuAttendee : mcuAttendees) {
                BaseConferenceContext mcuConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(mcuAttendee.getCascadeConferenceId()));
                if (mcuConferenceContext != null) {
                    try {
                        ConferenceCascadeHandler.defaultChooseSee(mcuAttendee.getId());
                    } catch (Exception e) {
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (baseConferenceContext instanceof ConferenceContext) {
            return success(defaultAttendeeOperationPackageService.defaultViewData(conferenceId));
        } else if (baseConferenceContext instanceof McuZjConferenceContext) {
            return success(defaultAttendeeOperationPackageForMcuZjService.defaultViewData(conferenceId));
        } else if (baseConferenceContext instanceof McuPlcConferenceContext) {
            return success(defaultAttendeeOperationPackageForMcuPlcService.defaultViewData(conferenceId));
        } else if (baseConferenceContext instanceof McuKdcConferenceContext) {
            return success(defaultAttendeeOperationPackageForMcuKdcService.defaultViewData(conferenceId));
        } else if (baseConferenceContext instanceof Smc3ConferenceContext) {
            return success(defaultAttendeeSmc3OperationPackageService.defaultViewData(conferenceId));
        } else if (baseConferenceContext instanceof Smc2ConferenceContext) {
            return success(defaultAttendeeSmc2OperationPackageService.defaultViewData(conferenceId));
        } else if (baseConferenceContext instanceof McuZteConferenceContext) {
            return success(defaultAttendeeOperationPackageForMcuZteService.defaultViewData(conferenceId));
        }
        return RestResponse.fail();
    }

    @PostMapping("/discuss/{conferenceId}")
    @Operation(summary = "会议讨论", description = "讨论")
    public RestResponse discuss(@PathVariable String conferenceId)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (baseConferenceContext instanceof ConferenceContext) {
            busiConferenceService.discuss(conferenceId);
        } else if (baseConferenceContext instanceof McuZjConferenceContext) {
            busiMcuZjConferenceService.discuss(conferenceId);
        } else if (baseConferenceContext instanceof McuPlcConferenceContext) {
            busiMcuPlcConferenceService.discuss(conferenceId);
        } else if (baseConferenceContext instanceof McuKdcConferenceContext) {
            busiMcuKdcConferenceService.discuss(conferenceId);
        } else if (baseConferenceContext instanceof Smc3ConferenceContext) {
            busiSmc3ConferenceService.discuss(conferenceId);
        } else if (baseConferenceContext instanceof Smc2ConferenceContext) {
            busiSmc2ConferenceService.discuss(conferenceId);
        }else if (baseConferenceContext instanceof McuZteConferenceContext) {
            busiMcuZteConferenceService.discuss(conferenceId);
        }
        return success();
    }

    @PostMapping("/cancelDiscuss/{conferenceId}")
    @Operation(summary = "取消会议讨论", description = "取消讨论")
    public RestResponse cancelDiscuss(@PathVariable String conferenceId)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (baseConferenceContext instanceof ConferenceContext) {
            busiConferenceService.cancelDiscuss(conferenceId);
        } else if (baseConferenceContext instanceof McuZjConferenceContext) {
            busiMcuZjConferenceService.cancelDiscuss(conferenceId);
        } else if (baseConferenceContext instanceof McuPlcConferenceContext) {
            busiMcuPlcConferenceService.cancelDiscuss(conferenceId);
        } else if (baseConferenceContext instanceof McuKdcConferenceContext) {
            busiMcuKdcConferenceService.cancelDiscuss(conferenceId);
        } else if (baseConferenceContext instanceof Smc3ConferenceContext) {
            busiSmc3ConferenceService.cancelDiscuss(conferenceId);
        } else if (baseConferenceContext instanceof Smc2ConferenceContext) {
            busiSmc2ConferenceService.cancelDiscuss(conferenceId);
        }else if (baseConferenceContext instanceof McuZteConferenceContext) {
            busiMcuZteConferenceService.cancelDiscuss(conferenceId);
        }
        return success();
    }

    @PutMapping("/extendMinutes/{conferenceId}/{minutes}")
    @Operation(summary = "延长会议时间，单位（分钟）", description = "延长会议时间")
    public RestResponse extendMinutes(@PathVariable String conferenceId, @PathVariable int minutes)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (baseConferenceContext instanceof ConferenceContext) {
            return success(busiConferenceService.extendMinutes(conferenceId, minutes));
        } else if (baseConferenceContext instanceof McuZjConferenceContext) {
            return success(busiMcuZjConferenceService.extendMinutes(conferenceId, minutes));
        } else if (baseConferenceContext instanceof McuPlcConferenceContext) {
            return success(busiMcuPlcConferenceService.extendMinutes(conferenceId, minutes));
        } else if (baseConferenceContext instanceof McuKdcConferenceContext) {
            return success(busiMcuKdcConferenceService.extendMinutes(conferenceId, minutes));
        } else if (baseConferenceContext instanceof Smc3ConferenceContext) {
            return success(busiSmc3ConferenceService.extendMinutes(conferenceId, minutes));
        } else if (baseConferenceContext instanceof Smc2ConferenceContext) {
            return success(busiSmc2ConferenceService.extendMinutes(conferenceId, minutes));
        }else if (baseConferenceContext instanceof HwcloudConferenceContext) {
            return success(busiHwcloudConferenceService.extendMinutes(conferenceId, minutes));
        }else if (baseConferenceContext instanceof McuZteConferenceContext) {
            return success(busiMcuZteConferenceService.extendMinutes(conferenceId, minutes));
        }
        return RestResponse.fail();
    }

    @PutMapping("/lock/{conferenceId}/{locked}")
    @Operation(summary = "锁定会议", description = "锁定会议")
    public RestResponse lock(@PathVariable String conferenceId, @PathVariable Boolean locked)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (baseConferenceContext instanceof ConferenceContext) {
            busiConferenceService.lock(conferenceId, locked);
        } else if (baseConferenceContext instanceof McuZjConferenceContext) {
            busiMcuZjConferenceService.lock(conferenceId, locked);
        } else if (baseConferenceContext instanceof McuPlcConferenceContext) {
            busiMcuPlcConferenceService.lock(conferenceId, locked);
        } else if (baseConferenceContext instanceof McuKdcConferenceContext) {
            busiMcuKdcConferenceService.lock(conferenceId, locked);
        } else if (baseConferenceContext instanceof Smc3ConferenceContext) {
            busiSmc3ConferenceService.lock(conferenceId, locked);
        } else if (baseConferenceContext instanceof Smc2ConferenceContext) {
            busiSmc2ConferenceService.lock(conferenceId, locked);
        }else if (baseConferenceContext instanceof TencentConferenceContext) {
            busiTencentConferenceService.lock(conferenceId, locked);
        }
        else if (baseConferenceContext instanceof HwcloudConferenceContext) {
            busiHwcloudConferenceService.lock(conferenceId, locked);
        }
        else if (baseConferenceContext instanceof McuZteConferenceContext) {
            busiMcuZteConferenceService.lock(conferenceId, locked);
        }
        return success();
    }

    @PutMapping("/allowAllMuteSelf/{conferenceId}/{enabled}")
    @Operation(summary = "允许所有人静音自己", description = "允许所有人静音自己")
    public RestResponse allowAllMuteSelf(@PathVariable String conferenceId, @PathVariable Boolean enabled)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (baseConferenceContext instanceof ConferenceContext) {
            busiConferenceService.allowAllMuteSelf(conferenceId, enabled);
        } else if (baseConferenceContext instanceof McuZjConferenceContext) {
            busiMcuZjConferenceService.allowAllMuteSelf(conferenceId, enabled);
        } else if (baseConferenceContext instanceof McuPlcConferenceContext) {
            busiMcuPlcConferenceService.allowAllMuteSelf(conferenceId, enabled);
        } else if (baseConferenceContext instanceof McuKdcConferenceContext) {
            busiMcuKdcConferenceService.allowAllMuteSelf(conferenceId, enabled);
        } else if (baseConferenceContext instanceof Smc3ConferenceContext) {
            busiSmc3ConferenceService.allowAllMuteSelf(conferenceId, enabled);
        } else if (baseConferenceContext instanceof Smc2ConferenceContext) {
            busiSmc2ConferenceService.allowAllMuteSelf(conferenceId, enabled);
        } else if (baseConferenceContext instanceof TencentConferenceContext) {
            busiTencentConferenceService.allowAllMuteSelf(conferenceId, enabled);
        }
        else if (baseConferenceContext instanceof HwcloudConferenceContext) {
            busiHwcloudConferenceService.allowAllMuteSelf(conferenceId, enabled);
        } else if (baseConferenceContext instanceof McuZteConferenceContext) {
            busiMcuZteConferenceService.allowAllMuteSelf(conferenceId, enabled);
        }
        return success();
    }

    @PutMapping("/allowAllPresentationContribution/{conferenceId}/{enabled}")
    @Operation(summary = "允许辅流控制", description = "允许辅流控制")
    public RestResponse allowAllPresentationContribution(@PathVariable String conferenceId, @PathVariable Boolean enabled)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (baseConferenceContext instanceof ConferenceContext) {
            busiConferenceService.allowAllPresentationContribution(conferenceId, enabled);
        } else if (baseConferenceContext instanceof McuZjConferenceContext) {
            busiMcuZjConferenceService.allowAllPresentationContribution(conferenceId, enabled);
        } else if (baseConferenceContext instanceof McuPlcConferenceContext) {
            busiMcuPlcConferenceService.allowAllPresentationContribution(conferenceId, enabled);
        } else if (baseConferenceContext instanceof McuKdcConferenceContext) {
            busiMcuKdcConferenceService.allowAllPresentationContribution(conferenceId, enabled);
        } else if (baseConferenceContext instanceof Smc3ConferenceContext) {
            busiSmc3ConferenceService.allowAllPresentationContribution(conferenceId, enabled);
        } else if (baseConferenceContext instanceof Smc2ConferenceContext) {
            busiSmc2ConferenceService.allowAllPresentationContribution(conferenceId, enabled);
        } else if (baseConferenceContext instanceof TencentConferenceContext) {
            busiTencentConferenceService.allowAllPresentationContribution(conferenceId, enabled);
        }
        return success();
    }

    @PutMapping("/joinAudioMuteOverride/{conferenceId}/{enabled}")
    @Operation(summary = "新加入用户静音", description = "新加入的用户静音")
    public RestResponse joinAudioMuteOverride(@PathVariable String conferenceId, @PathVariable Boolean enabled)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (baseConferenceContext instanceof ConferenceContext) {
            busiConferenceService.joinAudioMuteOverride(conferenceId, enabled);
        } else if (baseConferenceContext instanceof McuZjConferenceContext) {
            busiMcuZjConferenceService.joinAudioMuteOverride(conferenceId, enabled);
        } else if (baseConferenceContext instanceof McuPlcConferenceContext) {
            busiMcuPlcConferenceService.joinAudioMuteOverride(conferenceId, enabled);
        } else if (baseConferenceContext instanceof McuKdcConferenceContext) {
            busiMcuKdcConferenceService.joinAudioMuteOverride(conferenceId, enabled);
        } else if (baseConferenceContext instanceof Smc3ConferenceContext) {
            busiSmc3ConferenceService.joinAudioMuteOverride(conferenceId, enabled);
        } else if (baseConferenceContext instanceof Smc2ConferenceContext) {
            busiSmc2ConferenceService.joinAudioMuteOverride(conferenceId, enabled);
        } else if (baseConferenceContext instanceof TencentConferenceContext) {
            busiTencentConferenceService.joinAudioMuteOverride(conferenceId, enabled);
        }
        return success();
    }

    @PutMapping("/stream/{conferenceId}/{enabled}")
    @Operation(summary = "直播会议", description = "直播会议")
    public RestResponse stream(@PathVariable String conferenceId, @PathVariable Boolean enabled, @RequestBody JSONObject json)
    {
        Assert.isTrue(!enabled || (enabled && !ObjectUtils.isEmpty(json.getString("streamingUrl"))), "开启直播时，直播地址不能为空");
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (baseConferenceContext.isApprovedConference()) {
            if (baseConferenceContext.getRecordingEnabled() == YesOrNo.NO.getValue() && enabled) {
                throw new SystemException("当前会议未获取直播许可,不能开启直播！");
            }
        }
        if (baseConferenceContext instanceof ConferenceContext) {
            ConferenceContext conferenceContext = (ConferenceContext) baseConferenceContext;
            String streamingUrl = json.getString("streamingUrl");
            if (enabled) {
                if (StringUtils.hasText(conferenceContext.getStreamingRemoteParty())) {
                    if (!conferenceContext.getStreamingUrl().equals(streamingUrl)) {
                        return fail(1, "该会议过程中不允许变更直播地址！");
                    }
                }
            }
            busiConferenceService.stream(conferenceId, enabled, streamingUrl);
        } else if (baseConferenceContext instanceof McuZjConferenceContext) {
            busiMcuZjConferenceService.stream(conferenceId, enabled, json.getString("streamingUrl"));
        } else if (baseConferenceContext instanceof McuPlcConferenceContext) {
            busiMcuPlcConferenceService.stream(conferenceId, enabled, json.getString("streamingUrl"));
        } else if (baseConferenceContext instanceof McuKdcConferenceContext) {
            busiMcuKdcConferenceService.stream(conferenceId, enabled, json.getString("streamingUrl"));
        } else if (baseConferenceContext instanceof Smc3ConferenceContext) {
            busiSmc3ConferenceService.stream(conferenceId, enabled, json.getString("streamingUrl"));
        } else if (baseConferenceContext instanceof Smc2ConferenceContext) {
            busiSmc2ConferenceService.stream(conferenceId, enabled, json.getString("streamingUrl"));
        }
        else if (baseConferenceContext instanceof McuZteConferenceContext) {
            busiMcuZteConferenceService.stream(conferenceId, enabled, json.getString("streamingUrl"));
        }
        return success();
    }

    @PostMapping("/reCall/{conferenceId}")
    @Operation(summary = "一键呼入", description = "一键呼入")
    public RestResponse reCall(@PathVariable String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (baseConferenceContext instanceof ConferenceContext) {
            busiConferenceService.reCall(conferenceId);
        } else if (baseConferenceContext instanceof McuZjConferenceContext) {
            busiMcuZjConferenceService.reCall(conferenceId);
        } else if (baseConferenceContext instanceof McuPlcConferenceContext) {
            busiMcuPlcConferenceService.reCall(conferenceId);
        } else if (baseConferenceContext instanceof McuKdcConferenceContext) {
            busiMcuKdcConferenceService.reCall(conferenceId);
        } else if (baseConferenceContext instanceof Smc3ConferenceContext) {
            busiSmc3ConferenceService.reCall(conferenceId);
        } else if (baseConferenceContext instanceof Smc2ConferenceContext) {
            busiSmc2ConferenceService.reCall(conferenceId);
        } else if (baseConferenceContext instanceof DingConferenceContext) {
            busiDingConferenceService.reCall(conferenceId);
        }else if (baseConferenceContext instanceof HwcloudConferenceContext) {
            busiHwcloudConferenceService.reCall(conferenceId);
        }else if (baseConferenceContext instanceof McuZteConferenceContext) {
            busiMcuZteConferenceService.reCall(conferenceId);
        }

        return success();
    }

    @PostMapping("/sync/{conferenceId}")
    @Operation(summary = "一键同步", description = "一键同步")
    public RestResponse sync(@PathVariable String conferenceId)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (baseConferenceContext instanceof ConferenceContext) {
            busiConferenceService.sync(conferenceId);
        } else if (baseConferenceContext instanceof McuZjConferenceContext) {
            busiMcuZjConferenceService.sync(conferenceId);
        } else if (baseConferenceContext instanceof McuPlcConferenceContext) {
            busiMcuPlcConferenceService.sync(conferenceId);
        } else if (baseConferenceContext instanceof McuKdcConferenceContext) {
            busiMcuKdcConferenceService.sync(conferenceId);
        } else if (baseConferenceContext instanceof Smc3ConferenceContext) {
            busiSmc3ConferenceService.sync(conferenceId);
        } else if (baseConferenceContext instanceof Smc2ConferenceContext) {
            busiSmc2ConferenceService.sync(conferenceId);
        } else if (baseConferenceContext instanceof TencentConferenceContext) {
            busiTencentConferenceService.sync(conferenceId);
        }else if (baseConferenceContext instanceof HwcloudConferenceContext) {
            busiHwcloudConferenceService.sync(conferenceId);
        }
        else if (baseConferenceContext instanceof McuZteConferenceContext) {
            busiMcuZteConferenceService.sync(conferenceId);
        }

        return success();
    }

    @GetMapping("/layoutTemplates")
    @Operation(summary = "查询所有自定义布局模版")
    public RestResponse layoutTemplates(@RequestParam("deptId") Long deptId, @RequestParam("mcuType") String mcuTypeStr) {
        McuType mcuType = McuType.convert(mcuTypeStr);
        switch (mcuType) {
            case FME: {
                List<String> sortList = new ArrayList<>();

                List<Map<String, String>> resultLayoutTemplate = new ArrayList<>();
                Set<String> layoutTemplates = busiConferenceService.getLayoutTemplates(deptId);
                setSortList(sortList, layoutTemplates);
                convertMap(sortList, layoutTemplates, resultLayoutTemplate);
                return success(resultLayoutTemplate);
            }
        }
        return RestResponse.fail();
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
        int count = LiveBridgeCache.getInstance().getLiveConferenceTerminalCount(conferenceId);
        Map<String, Integer> map = new HashMap<>();
        map.put("liveTerminalCount", count);
        return RestResponse.success(map);
    }

    /**
     * 添加常用参会者
     * @param conferenceId
     * @param attendeeId
     * @return
     */
    @PostMapping("/commonlyUsedAttendee/{conferenceId}/{attendeeId}")
    @Operation(summary = "添加常用参会者", description = "设置常用与会者")
    public RestResponse addCommonlyUsedAttendee(@PathVariable String conferenceId, @PathVariable String attendeeId)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (baseConferenceContext != null) {
            BaseAttendee attendee = baseConferenceContext.getAttendeeById(attendeeId);
            if (attendee != null) {
                baseConferenceContext.addCommonlyUsedAttendees(attendee);
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("id", attendee.getId());
                updateMap.put("commonlyUsed", attendee.isCommonlyUsed());
                BaseWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(baseConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, updateMap);
            }
            return RestResponse.success(attendee);
        }
        return RestResponse.fail();
    }

    /**
     * 删除常用参会者
     * @param conferenceId
     * @param attendeeId
     * @return
     */
    @DeleteMapping("/commonlyUsedAttendee/{conferenceId}/{attendeeId}")
    @Operation(summary = "删除常用参会者", description = "删除常用与会者")
    public RestResponse deleteCommonlyUsedAttendee(@PathVariable String conferenceId, @PathVariable String attendeeId)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (baseConferenceContext != null) {
            BaseAttendee attendee = baseConferenceContext.getAttendeeById(attendeeId);
            if (attendee != null) {
                baseConferenceContext.removeCommonlyUsedAttendees(attendee);
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("id", attendee.getId());
                updateMap.put("commonlyUsed", attendee.isCommonlyUsed());
                BaseWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(baseConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, updateMap);
            }
            return RestResponse.success(attendee);
        }
        return RestResponse.fail();
    }

    /**
     * 会议名称修改
     * @param conferenceId
     * @param templateId
     * @param name
     * @return
     */
    @PutMapping("/name/{conferenceId}/{templateId}/{name}")
    @Operation(summary = "会议名称修改", description = "修改会议名称")
    public RestResponse updateName(@PathVariable String conferenceId,@PathVariable Long templateId, @PathVariable String name)
    {
//        iBusiTemplateConferenceService.updateBusiTemplateConferenceName(templateId,name);
        busiConferenceService.updateConferenceName(conferenceId, name);
        return success();
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

    /**
     * 查询会议中的会议终端
     * @param conferenceId
     * @return
     */
    @Log(title = "查询会议中的会议终端")
    @GetMapping("/getAttendees/{conferenceId}")
    @Operation(summary = "查询会议中的会议终端")
    public RestResponse getAttendees(@PathVariable String conferenceId) {
        List list = new ArrayList();
        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
        if (conferenceContext != null) {
            list.addAll(conferenceContext.getAttendees());
            list.addAll(conferenceContext.getMasterAttendees());
            for (Object value : conferenceContext.getCascadeAttendeesMap().values()) {
                if (!ObjectUtils.isEmpty(value)) {
                    list.addAll((Collection) value);
                }
            }
        }
        return RestResponse.success(list);
    }

    /**
     * 查询会议中的下级会议终端
     * @param conferenceId
     * @return
     */
    @Log(title = "查询会议中的下级会议终端")
    @GetMapping("/getDownCascadeAttendees/{conferenceId}")
    @Operation(summary = "查询会议中的下级会议终端")
    public RestResponse getDownCascadeAttendees(@PathVariable String conferenceId) {
        List<Map<String, Object>> list = new ArrayList<>();
        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
        if (conferenceContext != null) {
            List<BaseAttendee> mcuAttendees = conferenceContext.getMcuAttendees();
            for (BaseAttendee baseAttendee : mcuAttendees) {
                long deptId = -baseAttendee.getUpCascadeIndex();
                if (baseAttendee.isMeetingJoined()) {
                    BaseConferenceContext mcuConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(baseAttendee.getCascadeConferenceId()));
                    if (mcuConferenceContext != null) {
                        List<BaseAttendee> tempList = new ArrayList<>();
                        tempList.addAll(mcuConferenceContext.getAttendees());
                        tempList.addAll(mcuConferenceContext.getMasterAttendees());
                        for (Object value : mcuConferenceContext.getCascadeAttendeesMap().values()) {
                            if (!ObjectUtils.isEmpty(value)) {
                                tempList.addAll((Collection) value);
                            }
                        }
                        List<BaseAttendee> attendees = new ArrayList();
                        for (BaseAttendee tempAttendee : tempList) {
                            BaseAttendee attendee = new BaseAttendee();
                            BeanUtils.copyProperties(tempAttendee, attendee);
                            attendee.setDeptId(deptId);
                            attendee.setDeptName(mcuConferenceContext.getName());
                            attendees.add(attendee);
                        }

                        if(!(mcuConferenceContext instanceof Smc3ConferenceContext)){
                            for (BaseAttendee attendee : attendees) {
                                attendee.setParticipantUuid(attendee.getId());
                            }
                        }
                        Map<String, Object> map = new HashMap<>();
                        map.put("downCascadeConferenceId", mcuConferenceContext.getId());
                        map.put("downCascadeConferenceName", mcuConferenceContext.getName());
                        map.put("deptId", deptId);
                        map.put("attendees", attendees);
                        list.add(map);
                    }
                }
            }
        }
        return RestResponse.success(list);
    }



    /**
     * 多画面广播、取消广播
     *
     */
    @PostMapping("/multiPicBroadcast/{conferenceId}/{enable}")
    @Operation(summary = "多画面广播、取消广播", description = "多画面广播、取消广播")
    public RestResponse broadcastStatusOpreation(@PathVariable String conferenceId,@PathVariable Boolean enable)
    {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {

            case SMC3: {
                busiSmc3ConferenceService.multiPicBroad(conferenceId,enable);
                break;
            }
            case SMC2: {
                busiSmc2ConferenceService.multiPicBroad(conferenceId,enable);
                break;
            }

            case MCU_HWCLOUD: {
                busiHwcloudConferenceService.multiPicBroad(conferenceId,enable);
                break;
            }

        }
        return success();
    }


    /**
     * 全体扬声器打开/关闭
     * @param conferenceId
     */
    @PatchMapping("/quiet/{conferenceId}/{enable}")
    @Operation(summary = "", description = "全体扬声器打开关闭")
    public RestResponse changeQuietFalse(@PathVariable String conferenceId,@PathVariable boolean enable){
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();

        switch (mcuType) {

            case SMC3: {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("isQuiet",enable);
                busiSmc3ConferenceService.changeQuiet(conferenceId,jsonObject);
                break;
            }
            case SMC2: {
                busiSmc2ConferenceService.changeQuiet(conferenceId,enable);
                break;
            }
            case MCU_ZTE: {
                busiMcuZteConferenceService.changeQuiet(conferenceId,enable);
                break;
            }
        }
        return success();

    }

    /**
     * 获取部门当前会议计数
     */
    @GetMapping(value = "/getDeptActiveCount/{businessFieldType}")
    @Operation(summary = "获取部门会议模板计数")
    public RestResponse getDeptActiveCount(@PathVariable("businessFieldType") Integer businessFieldType) {
        List<ModelBean> list = new ArrayList<>();
        Map<Long, Long> map = new HashMap<>();
        Collection<BaseConferenceContext> cc = AllConferenceContextCache.getInstance().values();
        for (Iterator<BaseConferenceContext> iterator = cc.iterator(); iterator.hasNext(); ) {
            BaseConferenceContext conferenceContext = iterator.next();
            boolean needAdd = false;
            if (businessFieldType != null) {
                if (conferenceContext.getBusinessFieldType().intValue() == businessFieldType) {
                    needAdd = true;
                }
            } else {
                needAdd = true;
            }
            if (needAdd) {
                Long deptId = conferenceContext.getDeptId();
                Long count = map.get(deptId);
                if (count == null) {
                    count = 0l;
                }
                count = count + 1;
                map.put(deptId, count);
            }
        }
        for (Long deptId : map.keySet()) {
            ModelBean modelBean = new ModelBean();
            modelBean.put("deptId", deptId);
            modelBean.put("count", map.get(deptId));
            list.add(modelBean);
        }

        return success(list);
    }

    /**
     * 获取部门当前会议计数
     */
    @PostMapping(value = "/getActiveConferences")
    @Operation(summary = "获取部门会议模板计数")
    public RestResponse getActiveConferences(@RequestBody ViewTemplateConference viewTemplateConference) {
        if (viewTemplateConference.getDeptId() == null) {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            viewTemplateConference.setDeptId(loginUser.getUser().getDeptId());
        }
        List<JSONObject> jsons = new ArrayList<>();
        Collection<BaseConferenceContext> cc = AllConferenceContextCache.getInstance().values();
        SysDept con = new SysDept();
        con.setDeptId(viewTemplateConference.getDeptId());
        List<SysDept> sds = sysDeptService.selectDeptList(con);
        for (Iterator<BaseConferenceContext> iterator = cc.iterator(); iterator.hasNext(); ) {
            BaseConferenceContext conferenceContext = iterator.next();
            boolean needAdd = false;
            for (SysDept sysDept : sds) {
                boolean mcuTypeOk = false;
                boolean nameOk = false;
                boolean businessOk = false;
                if (conferenceContext.getDeptId().longValue() == sysDept.getDeptId().longValue()) {
                    if (StringUtils.hasText(viewTemplateConference.getMcuType())) {
                        if (conferenceContext.getMcuType().equals(viewTemplateConference.getMcuType())) {
                            mcuTypeOk = true;
                        }
                    } else {
                        mcuTypeOk = true;
                    }
                    if (StringUtils.hasText(viewTemplateConference.getName())) {
                        if (conferenceContext.getName().contains(viewTemplateConference.getName())) {
                            nameOk = true;
                        }
                    } else {
                        nameOk = true;
                    }
                    if (viewTemplateConference.getBusinessFieldType() != null) {
                        if (viewTemplateConference.getBusinessFieldType().intValue() == conferenceContext.getBusinessFieldType()) {
                            businessOk = true;
                        }
                    } else {
                        businessOk = true;
                    }
                    if (mcuTypeOk && nameOk && businessOk) {
                        needAdd = true;
                        break;
                    }
                }
            }
            if (needAdd) {
                JSONObject json = toJson(conferenceContext);
                if (!jsons.contains(json)) {
                    jsons.add(json);
                }
            }
        }

        return success(jsons);
    }

    /**
     * 锁定会议材料(取消)
     */
    @PatchMapping("/lockPresenter/{conferenceId}/{lock}")
    @Operation(summary = "锁定会议材料")
    public RestResponse lockPresenter(@PathVariable String conferenceId, @PathVariable Boolean lock) {

        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();


        switch (mcuType) {

            case SMC3: {
                busiSmc3ConferenceService.lockPresenter(conferenceId,lock);
                return RestResponse.success();
            }
            case SMC2: {
                busiSmc2ConferenceService.lockPresenter(conferenceId,lock);
                return RestResponse.success();
            }

            case MCU_HWCLOUD: {
                busiHwcloudConferenceService.lockPresenter(conferenceId,lock);
                return RestResponse.success();
            }

        }
        return success();

    }


    /**
     * 打开、关闭声控
     *
     * @param conferenceId
     */
    @PatchMapping("/voiceActive/{conferenceId}/{enable}")
    @Operation(summary = "打开、关闭声控")
    public RestResponse isVoiceActive(@PathVariable String conferenceId,@PathVariable Boolean enable) {

        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();

        switch (mcuType) {

            case SMC3: {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("isVoiceActive",enable);
                busiSmc3ConferenceService.voiceActive(conferenceId,jsonObject);


                return RestResponse.success();
            }
            case SMC2: {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("isVoiceActive",enable);
                busiSmc2ConferenceService.voiceActive(conferenceId,enable);
                return RestResponse.success();
            }

            case MCU_ZTE: {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("isVoiceActive",enable);
                busiMcuZteConferenceService.voiceActive(conferenceId,enable);
                return RestResponse.success();
            }

        }
        return success();


    }


    private JSONObject toJson(BaseConferenceContext conferenceContext) {
        JSONObject json = new JSONObject();
        json.put("conferenceId", conferenceContext.getId());
        json.put("mcuType", conferenceContext.getMcuType());
        json.put("mcuTypeAlias", conferenceContext.getMcuTypeAlias());
        json.put("conferenceName", conferenceContext.getName());
        json.put("tenantId", conferenceContext.getTenantId());
        json.put("conferenceNumber", conferenceContext.getConferenceNumber());
        json.put("templateId", conferenceContext.getTemplateConferenceId());
        json.put("bindwidth", conferenceContext.getBandwidth());

        AtomicInteger as = new AtomicInteger();
        AtomicInteger inMeetings = new AtomicInteger();
        AllConferenceContextUtils.eachNonMcuAttendeeInConference(conferenceContext, (a) -> {
            as.incrementAndGet();
            if (a.isMeetingJoined()) {
                inMeetings.incrementAndGet();
            }
        });

        json.put("terminalCount", as.get());
        json.put("inMeetingTerminalCount", inMeetings.get());
        json.put("conferenceStartTime", conferenceContext.getStartTime());
        json.put("masterName", conferenceContext.getMasterAttendee() != null ? conferenceContext.getMasterAttendee().getName() : null);
        return json;
    }

    @PutMapping("/meetingstatus/{conferenceId}")
    @Operation(summary = "会中状态设置")
    public RestResponse meetingstatus(@PathVariable String conferenceId, @RequestBody ModifyConferenceRequest request)
    {

        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();

        switch (mcuType) {
            case MCU_TENCENT:{
                String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
                TencentConferenceContext tencentConferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
                if (tencentConferenceContext == null) {
                    return null;
                }
                request.setMeetingId(tencentConferenceContext.getMeetingId());
                try {
                    busiTencentConferenceService.meetingstatus(request);
                } catch (Exception e) {
                    if(e.getMessage().contains("error msg:mute_all必传且mute_all为true时才允许设置allow_unmute_by_self")){
                        throw new CustomException("全体闭麦时才允许设置");
                    }
                    return RestResponse.fail(com.paradisecloud.fcm.tencent.utils.StringUtils.removeMatch(e.getMessage()));
                }
                break;
            }
            case MCU_HWCLOUD:{

                Integer callInRestriction=null;

                if(request.getCallInRestriction()!=null){
                    callInRestriction=request.getCallInRestriction();
                }

                busiHwcloudConferenceService.meetingstatus(conferenceId,null,callInRestriction);

                break;
            }
        }


        return RestResponse.success();
    }

    @GetMapping("/waitingRoomParticipants/{conferenceId}")
    @Operation(summary = "获取实时等候室成员列表")
    public RestResponse queryWaitingRoomParticipants(@PathVariable String conferenceId,@RequestParam(required = false,defaultValue = "1") int page,@RequestParam(required = false,defaultValue = "10") int pageSize) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();

        switch (mcuType) {
            case MCU_TENCENT:{
                String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
                TencentConferenceContext tencentConferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
                if (tencentConferenceContext == null) {
                    return null;
                }
                WaitingRoomResponse waitingRoomResponse=null;
                try {
                    waitingRoomResponse =  busiTencentConferenceService.queryWaitingRoomParticipants(conferenceId,page,pageSize);
                } catch (Exception e) {
                    return RestResponse.fail(com.paradisecloud.fcm.tencent.utils.StringUtils.removeMatch(e.getMessage()));
                }
                return RestResponse.success(waitingRoomResponse);
            }

            case MCU_HWCLOUD:{
                String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
                HwcloudConferenceContext hwcloudConferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
                if (hwcloudConferenceContext == null) {
                    return null;
                }

                Map<String, Object> waitingParticipantMap = hwcloudConferenceContext.getWaitingParticipantMap();
                if(waitingParticipantMap==null){
                    return RestResponse.success();
                }

                List<Map.Entry<String, Object>> entryList = new LinkedList<>(waitingParticipantMap.entrySet());
                // 计算起始索引
                int startIndex = (page - 1) * pageSize;
                int endIndex = Math.min(startIndex + pageSize, entryList.size());
                // 获取指定页码的数据
                List<Map.Entry<String, Object>> pageEntries = entryList.subList(startIndex, endIndex);

                List<WaitingRoomResponse.ParticipantsDTO> participant=new ArrayList<>();

                for (Map.Entry<String, Object> pageEntry : pageEntries) {

                    WaitingRoomResponse.ParticipantsDTO wa=new WaitingRoomResponse.ParticipantsDTO();
                    wa.setMsOpenId(pageEntry.getKey());
                    HashMap<String,Object> map = (HashMap)pageEntry.getValue();
                    wa.setUserName( (String) map.get("name"));
                    wa.setLoginType( (String) map.get("clientLoginType"));
                    wa.setJoinTime((String)map.get("addtime"));
                    participant.add(wa);

                }


                WaitingRoomResponse waitingRoomResponse=new WaitingRoomResponse();

                waitingRoomResponse.setCurrentPage(page);
                waitingRoomResponse.setCurrentSize(pageSize);
                waitingRoomResponse.setTotalCount(entryList.size());

                waitingRoomResponse.setParticipants(participant);

                return RestResponse.success(waitingRoomResponse);

            }
        }


        return RestResponse.success();


    }



    @PutMapping("/waitingRoomParticipants/setting/{conferenceId}")
    @Operation(summary = "用户等候室设置")
    public RestResponse waitingRoomParticipantSetting(@PathVariable String conferenceId,@RequestBody WaitingRoomParticipantSetting waitingRoomParticipantSetting) {

        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();

        switch (mcuType){
            case MCU_HWCLOUD: {

                com.paradisecloud.fcm.huaweicloud.huaweicloud.model.WaitingRoomParticipantSetting waitingRoomParticipantSetting1 = JSONObject.parseObject(JSON.toJSONString(waitingRoomParticipantSetting), com.paradisecloud.fcm.huaweicloud.huaweicloud.model.WaitingRoomParticipantSetting.class);
                busiHwcloudConferenceService.waitingRoomParticipantSetting(conferenceId,waitingRoomParticipantSetting1);
                break;
            }
            case MCU_TENCENT:{
                String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
                TencentConferenceContext tencentConferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
                if (tencentConferenceContext == null) {
                    return null;
                }
                Integer operateType = waitingRoomParticipantSetting.getOperateType();
                AttendeeTencent masterAttendee = tencentConferenceContext.getMasterAttendee();
                if(masterAttendee!=null){
                    List<WaitingRoomParticipantSetting.UsersDTO> users = waitingRoomParticipantSetting.getUsers();
                    if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(users)){
                        for (WaitingRoomParticipantSetting.UsersDTO user : users) {
                            String msOpenId = user.getMsOpenId();
                            if(Objects.equals(masterAttendee.getMs_open_id(),msOpenId)){
                                return RestResponse.fail("主会场不允许");
                            }
                        }
                    }
                }
                try {
                    busiTencentConferenceService.waitingRoomParticipantSetting(conferenceId,waitingRoomParticipantSetting);
                } catch (Exception e) {
                    return RestResponse.fail(com.paradisecloud.fcm.tencent.utils.StringUtils.removeMatch(e.getMessage()));
                }
            }
        }

        return RestResponse.success();

    }


    @PutMapping("/switchMode/{conferenceId}/{switchMode}/{imageType}")
    @Operation(summary = "模式")
    public RestResponse switchMode(@PathVariable String conferenceId,@PathVariable String switchMode,@PathVariable Integer imageType) {

        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType){
            case MCU_HWCLOUD: {

                String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
                HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
                if (conferenceContext == null) {
                    return null;
                }
                busiHwcloudConferenceService.switchMode(conferenceId,switchMode,imageType);
                break;
            }
        }
        return RestResponse.success();
    }

    /**
     * 查询会议中的会议终端状态
     * @param conferenceId
     * @return
     */
    @Log(title = "查询会议中的会议终端状态")
    @GetMapping("/participantStatus/{conferenceId}")
    @Operation(summary = "查询会议中的会议终端状态")
    public RestResponse getParticipantStatus(@PathVariable String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType){
            case MCU_ZTE : {
                String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
                McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
                if (conferenceContext == null) {
                    return null;
                }
                GetParticipantStatusV3Request getParticipantStatusV3Request = new GetParticipantStatusV3Request();
                getParticipantStatusV3Request.setConferenceIdentifier(conferenceContext.getConfId());
                getParticipantStatusV3Request.setPage(-1);
                getParticipantStatusV3Request.setNumPerPage(-1);
                GetParticipantStatusV3Response participantStatusV3Response = conferenceContext.getConferenceControlApi().getParticipantStatusV3(getParticipantStatusV3Request);

                if(participantStatusV3Response!=null){
                    ParticipantStatusV3[] participantStatus = participantStatusV3Response.getParticipantStatus();
                    if(participantStatus!=null){
                        List<ParticipantStatusV3> connected = Arrays.stream(participantStatus).filter(participantStatusV3 -> Objects.equals(participantStatusV3.getParticipantStatus().getStatus(), "connected")).collect(Collectors.toList());
                        return RestResponse.success(connected);
                    }
                }

                return RestResponse.success(participantStatusV3Response);
            }
        }
        return RestResponse.success();
    }




    @PostMapping("/conferenceModel/{conferenceId}/{model}")
    @Operation(summary = "模式切换", description = "模式切换")
    public RestResponse talk(@PathVariable String conferenceId,@PathVariable String model) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        if(conferenceContext==null){
            throw new CustomException("会议不存在");
        }
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                if(Objects.equals(model, ConferenceOpsModeEnum.CHAIRMAN_POLLING.name())){
                    if (conferenceContext.getMasterAttendee() == null || !conferenceContext.getMasterAttendee().isMeetingJoined()) {
                        throw new SystemException(1005454, "主会场未设置,无法进入主席模式！");
                    }
                }
                busiConferenceService.mode(conferenceId,model);
                break;
            }
        }
        return success();
    }






}
