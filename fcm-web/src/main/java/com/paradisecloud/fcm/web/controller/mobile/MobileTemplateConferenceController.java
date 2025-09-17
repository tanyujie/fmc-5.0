package com.paradisecloud.fcm.web.controller.mobile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.paradiscloud.fcm.business.model.enumer.BusinessFieldType;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.common.utils.bean.BeanUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.ConferenceTemplateCreateType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.mapper.ViewTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.McuTypeVo;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiCallLegProfileService;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.ITemplateConferenceStartService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.*;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcConferenceService;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcTemplateConferenceService;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcConferenceService;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcTemplateConferenceService;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.busi.layout.splitscreen.AutomaticSplitScreen;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjConferenceService;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjTemplateConferenceService;
import com.paradisecloud.fcm.fme.model.cms.CallLegProfile;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberService;
import com.paradisecloud.fcm.web.model.mobile.req.DefaultViewCellScreens;
import com.paradisecloud.fcm.web.service.interfaces.IBusiAllMcuService;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.spring.BeanFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

import static com.paradisecloud.fcm.web.controller.mobile.MobileConferenceAppointmentController.INT4;
import static com.paradisecloud.fcm.web.controller.mobile.MobileConferenceAppointmentController.INT8;

/**
 * 会议模板Controller
 *
 * @author lilinhai
 * @date 2021-01-20
 */
@RestController
@RequestMapping("/mobile/templateConference")
@Slf4j
@Tag(name = "移动端会议模板")
public class MobileTemplateConferenceController extends BaseController {

    @Resource
    private IBusiTemplateConferenceService busiTemplateConferenceService;
    @Resource
    private ITemplateConferenceStartService templateConferenceStartService;
    @Resource
    private PlatformTransactionManager transactionManager;
    @Resource
    private IBusiConferenceNumberService busiConferenceNumberService;
    @Resource
    private IBusiCallLegProfileService busiCallLegProfileService;
    @Resource
    private IBusiMcuZjTemplateConferenceService busiMcuZjTemplateConferenceService;
    @Resource
    private IBusiMcuZjConferenceService busiMcuZjConferenceService;
    @Resource
    private IBusiMcuPlcTemplateConferenceService busiMcuPlcTemplateConferenceService;
    @Resource
    private IBusiMcuPlcConferenceService busiMcuPlcConferenceService;
    @Resource
    private IBusiMcuKdcTemplateConferenceService busiMcuKdcTemplateConferenceService;
    @Resource
    private IBusiMcuKdcConferenceService busiMcuKdcConferenceService;
    @Resource
    private IBusiAllMcuService busiAllMcuService;
    @Resource
    private ViewTemplateConferenceMapper viewTemplateConferenceMapper;

    /**
     * 查询会议模板列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询会议模板列表")
    public RestResponse list() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long deptId = loginUser.getUser().getDeptId();
        List<String> mcuTypes = new ArrayList<>();
        mcuTypes.add(McuType.FME.getCode());
        mcuTypes.add(McuType.MCU_ZJ.getCode());
        mcuTypes.add(McuType.MCU_PLC.getCode());
        mcuTypes.add(McuType.MCU_KDC.getCode());
        ViewTemplateConference viewTemplateConference = new ViewTemplateConference();
        viewTemplateConference.setDeptId(deptId);
        viewTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
        viewTemplateConference.getParams().put("mcuTypes", mcuTypes);
        List<ViewTemplateConference> list = viewTemplateConferenceMapper.selectViewTemplateConferenceList(viewTemplateConference);
        List<ModelBean> mbs = new ArrayList<>();
        for (ViewTemplateConference viewTemplateConferenceTemp : list) {
            McuType mcuType = McuType.convert(viewTemplateConferenceTemp.getMcuType());
            switch (mcuType) {
                case FME: {
                    BusiTemplateConference busiTemplateConference = new BusiTemplateConference();
                    BeanUtils.copyBeanProp(busiTemplateConference, viewTemplateConferenceTemp);
                    ModelBean modelBean = busiTemplateConferenceService.getTemplateConferenceDetails(busiTemplateConference);
                    modelBean.put("conferenceId", viewTemplateConferenceTemp.getConferenceId());
                    modelBean.put("mcuType", viewTemplateConferenceTemp.getMcuType());
                    modelBean.put("mcuTypeAlias", McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
                    if (StringUtils.isNotEmpty(viewTemplateConferenceTemp.getUpCascadeConferenceId())) {
                        modelBean.put("isDownCascade", true);
                    } else {
                        modelBean.put("isDownCascade", false);
                    }
                    ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
                    viewTemplateConferenceCon.setUpCascadeId(viewTemplateConferenceTemp.getId());
                    viewTemplateConferenceCon.setUpCascadeMcuType(viewTemplateConferenceTemp.getMcuType());
                    List<ViewTemplateConference> downCascadeList = viewTemplateConferenceMapper.selectViewTemplateConferenceList(viewTemplateConferenceCon);
                    if (downCascadeList.size() > 0) {
                        modelBean.put("isUpCascade", true);
                    } else {
                        modelBean.put("isUpCascade", false);
                    }
                    mbs.add(modelBean);
                    break;
                }
                case MCU_ZJ: {
                    BusiMcuZjTemplateConference busiMcuZjTemplateConference = new BusiMcuZjTemplateConference();
                    BeanUtils.copyBeanProp(busiMcuZjTemplateConference, viewTemplateConferenceTemp);
                    ModelBean modelBean = busiMcuZjTemplateConferenceService.getTemplateConferenceDetails(busiMcuZjTemplateConference);
                    modelBean.put("conferenceId", viewTemplateConferenceTemp.getConferenceId());
                    modelBean.put("mcuType", viewTemplateConferenceTemp.getMcuType());
                    modelBean.put("mcuTypeAlias", McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
                    if (StringUtils.isNotEmpty(viewTemplateConferenceTemp.getUpCascadeConferenceId())) {
                        modelBean.put("isDownCascade", true);
                    } else {
                        modelBean.put("isDownCascade", false);
                    }
                    ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
                    viewTemplateConferenceCon.setUpCascadeId(viewTemplateConferenceTemp.getId());
                    viewTemplateConferenceCon.setUpCascadeMcuType(viewTemplateConferenceTemp.getMcuType());
                    List<ViewTemplateConference> downCascadeList = viewTemplateConferenceMapper.selectViewTemplateConferenceList(viewTemplateConferenceCon);
                    if (downCascadeList.size() > 0) {
                        modelBean.put("isUpCascade", true);
                    } else {
                        modelBean.put("isUpCascade", false);
                    }
                    mbs.add(modelBean);
                    break;
                }
                case MCU_PLC: {
                    BusiMcuPlcTemplateConference busiMcuPlcTemplateConference = new BusiMcuPlcTemplateConference();
                    BeanUtils.copyBeanProp(busiMcuPlcTemplateConference, viewTemplateConferenceTemp);
                    ModelBean modelBean = busiMcuPlcTemplateConferenceService.getTemplateConferenceDetails(busiMcuPlcTemplateConference);
                    modelBean.put("conferenceId", viewTemplateConferenceTemp.getConferenceId());
                    modelBean.put("mcuType", viewTemplateConferenceTemp.getMcuType());
                    modelBean.put("mcuTypeAlias", McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
                    if (StringUtils.isNotEmpty(viewTemplateConferenceTemp.getUpCascadeConferenceId())) {
                        modelBean.put("isDownCascade", true);
                    } else {
                        modelBean.put("isDownCascade", false);
                    }
                    ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
                    viewTemplateConferenceCon.setUpCascadeId(viewTemplateConferenceTemp.getId());
                    viewTemplateConferenceCon.setUpCascadeMcuType(viewTemplateConferenceTemp.getMcuType());
                    List<ViewTemplateConference> downCascadeList = viewTemplateConferenceMapper.selectViewTemplateConferenceList(viewTemplateConferenceCon);
                    if (downCascadeList.size() > 0) {
                        modelBean.put("isUpCascade", true);
                    } else {
                        modelBean.put("isUpCascade", false);
                    }
                    mbs.add(modelBean);
                    break;
                }
                case MCU_KDC: {
                    BusiMcuKdcTemplateConference busiMcuKdcTemplateConference = new BusiMcuKdcTemplateConference();
                    BeanUtils.copyBeanProp(busiMcuKdcTemplateConference, viewTemplateConferenceTemp);
                    ModelBean modelBean = busiMcuKdcTemplateConferenceService.getTemplateConferenceDetails(busiMcuKdcTemplateConference);
                    modelBean.put("conferenceId", viewTemplateConferenceTemp.getConferenceId());
                    modelBean.put("mcuType", viewTemplateConferenceTemp.getMcuType());
                    modelBean.put("mcuTypeAlias", McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
                    if (StringUtils.isNotEmpty(viewTemplateConferenceTemp.getUpCascadeConferenceId())) {
                        modelBean.put("isDownCascade", true);
                    } else {
                        modelBean.put("isDownCascade", false);
                    }
                    ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
                    viewTemplateConferenceCon.setUpCascadeId(viewTemplateConferenceTemp.getId());
                    viewTemplateConferenceCon.setUpCascadeMcuType(viewTemplateConferenceTemp.getMcuType());
                    List<ViewTemplateConference> downCascadeList = viewTemplateConferenceMapper.selectViewTemplateConferenceList(viewTemplateConferenceCon);
                    if (downCascadeList.size() > 0) {
                        modelBean.put("isUpCascade", true);
                    } else {
                        modelBean.put("isUpCascade", false);
                    }
                    mbs.add(modelBean);
                    break;
                }
            }
        }
        return RestResponse.success(0, "查询成功", mbs);
    }

    /**
     * 获取会议模板详细信息
     */
    @GetMapping(value = "/{conferenceId}")
    @Operation(summary = "获取会议模板详细信息")
    public RestResponse getInfo(@PathVariable("conferenceId") String conferenceId)
    {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        if (McuType.FME == mcuType) {
            return RestResponse.success(busiTemplateConferenceService.selectBusiTemplateConferenceById(id));
        }
        else if (McuType.MCU_ZJ == mcuType) {
            return RestResponse.success(busiMcuZjTemplateConferenceService.selectBusiMcuZjTemplateConferenceById(id));
        }
        else if (McuType.MCU_PLC == mcuType) {
            return RestResponse.success(busiMcuPlcTemplateConferenceService.selectBusiMcuPlcTemplateConferenceById(id));
        }
        else if (McuType.MCU_KDC == mcuType) {
            return RestResponse.success(busiMcuKdcTemplateConferenceService.selectBusiMcuKdcTemplateConferenceById(id));
        }
        return RestResponse.fail();
    }

    /**
     * 获取会议模板详细信息
     */
    @GetMapping(value = "/getCurrentConferenceInfo/{conferenceId}")
    @Operation(summary = "获取会议模板详细信息")
    public RestResponse getCurrentConferenceInfo(@PathVariable("conferenceId") String conferenceId)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        if (McuType.FME == mcuType) {
            ConferenceContext conferenceContext = templateConferenceStartService.buildTemplateConferenceContext(id);
            ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
            JSONObject json = null;
            try {
                json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
            } catch (JsonProcessingException e) {
            }
            boolean isPresenter = false;
            boolean isMyConference = false;
            if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                isPresenter = true;
            }
            if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                isMyConference = true;
            }
            json.put("isPresenter", isPresenter);
            json.put("isMyConference", isMyConference);

            List<ModelBean> splitScreenList = new ArrayList<>();
            {
                ModelBean modelBean = new ModelBean();
                modelBean.put("name", "一分屏");
                modelBean.put("value", OneSplitScreen.LAYOUT);
                modelBean.put("isDefault", false);
                splitScreenList.add(modelBean);
            }
            {
                ModelBean modelBean = new ModelBean();
                modelBean.put("name", "四分屏");
                modelBean.put("value", FourSplitScreen.LAYOUT);
                modelBean.put("isDefault", false);
                splitScreenList.add(modelBean);
            }
            {
                ModelBean modelBean = new ModelBean();
                modelBean.put("name", "九分屏");
                modelBean.put("value", NineSplitScreen.LAYOUT);
                modelBean.put("isDefault", false);
                splitScreenList.add(modelBean);
            }
            {
                ModelBean modelBean = new ModelBean();
                modelBean.put("name", "自动");
                modelBean.put("value", AutomaticSplitScreen.LAYOUT);
                modelBean.put("isDefault", false);
                splitScreenList.add(modelBean);
            }
            {
                ModelBean modelBean = new ModelBean();
                modelBean.put("name", "全等");
                modelBean.put("value", AllEqualSplitScreen.LAYOUT);
                modelBean.put("isDefault", true);
                splitScreenList.add(modelBean);
            }
            {
                ModelBean modelBean = new ModelBean();
                modelBean.put("name", "一大N小");
                modelBean.put("value", OnePlusNSplitScreen.LAYOUT);
                modelBean.put("isDefault", false);
                splitScreenList.add(modelBean);
            }
            json.put("splitScreenList", splitScreenList);
            return RestResponse.success(json);
        }
        else if (McuType.MCU_ZJ == mcuType) {
            McuZjConferenceContext conferenceContext = busiMcuZjConferenceService.buildTemplateConferenceContext(id);
            ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
            JSONObject json = null;
            try {
                json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
            } catch (JsonProcessingException e) {
            }
            boolean isPresenter = false;
            boolean isMyConference = false;
            if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                isPresenter = true;
            }
            if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                isMyConference = true;
            }
            json.put("isPresenter", isPresenter);
            json.put("isMyConference", isMyConference);
            json.remove("supervisorPassword");

            List<ModelBean> splitScreenList = new ArrayList<>();

            if (conferenceContext.isSingleView()) {
                splitScreenList = conferenceContext.getSpeakerSplitScreenList();
            } else {
                splitScreenList = conferenceContext.getGuestSplitScreenList();
            }
            for (int i = 0; i < splitScreenList.size(); i++) {
                ModelBean modelBean = splitScreenList.get(i);
                if (i == 0) {
                    modelBean.put("isDefault", true);
                } else {
                    modelBean.put("isDefault", false);
                }
            }
            json.put("splitScreenList", splitScreenList);
            return RestResponse.success(json);
        }
        else if (McuType.MCU_PLC == mcuType) {
            McuPlcConferenceContext conferenceContext = busiMcuPlcConferenceService.buildTemplateConferenceContext(id);
            ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
            JSONObject json = null;
            try {
                json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
            } catch (JsonProcessingException e) {
            }
            boolean isPresenter = false;
            boolean isMyConference = false;
            if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                isPresenter = true;
            }
            if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                isMyConference = true;
            }
            json.put("isPresenter", isPresenter);
            json.put("isMyConference", isMyConference);
            json.remove("supervisorPassword");

            List<ModelBean> splitScreenList = conferenceContext.getSpeakerSplitScreenList();
            json.put("splitScreenList", splitScreenList);
            return RestResponse.success(json);
        }
        else if (McuType.MCU_KDC == mcuType) {
            McuKdcConferenceContext conferenceContext = busiMcuKdcConferenceService.buildTemplateConferenceContext(id);
            ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
            JSONObject json = null;
            try {
                json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
            } catch (JsonProcessingException e) {
            }
            boolean isPresenter = false;
            boolean isMyConference = false;
            if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                isPresenter = true;
            }
            if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                isMyConference = true;
            }
            json.put("isPresenter", isPresenter);
            json.put("isMyConference", isMyConference);
            json.remove("supervisorPassword");

            List<ModelBean> splitScreenList = conferenceContext.getSpeakerSplitScreenList();
            json.put("splitScreenList", splitScreenList);
            return RestResponse.success(json);
        }
        return RestResponse.fail();
    }

    /**
     * 获取部门会议模板计数
     */
    @GetMapping(value = "/getDeptTemplateCount")
    @Operation(summary = "获取部门会议模板计数")
    public RestResponse getDeptTemplateCount()
    {
        return RestResponse.success(viewTemplateConferenceMapper.getDeptTemplateCount(BusinessFieldType.COMMON.getValue()));
    }

    /**
     * 新增会议模板
     */
    @Log(title = "会议模板", businessType = BusinessType.INSERT)
    @PostMapping("")
    @Operation(summary = "新增会议模板")
    public RestResponse add(@RequestBody JSONObject jsonObj)
    {
        String mcuTypeStr = "";
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long deptId = loginUser.getUser().getDeptId();
        McuTypeVo mcuTypeVo = busiAllMcuService.getDefaultMcuType(deptId);
        mcuTypeStr = mcuTypeVo.getCode();
        McuType mcuType = McuType.convert(mcuTypeStr);
        if (McuType.FME == mcuType) {
            synchronized (MobileTemplateConferenceController.class){
                JSONObject templateConferenceObj = jsonObj.getJSONObject("templateConference");
                Boolean isMute = jsonObj.getBoolean("isMute");
                Assert.isTrue(templateConferenceObj != null, "会议模板不能为空！");
                Long masterTerminalId = templateConferenceObj.getLong("masterTerminalId");
                BusiTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiTemplateConference.class);

                busiTemplateConference.setDeptId(deptId);
                busiTemplateConference.setType(2);
                busiTemplateConference.setViewType(1);
                busiTemplateConference.setIsAutoCreateConferenceNumber(2);
                busiTemplateConference.setIsAutoMonitor(2);
                busiTemplateConference.setBusinessFieldType(100);
                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("templateParticipants");
                //主持人
                Long presenter = jsonObj.getLong("presenter");
                if(presenter!=null){
                    busiTemplateConference.setPresenter(presenter);
                }

                List<BusiTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
                if (busiTemplateParticipantArr != null)
                {
                    for (int i = 0; i < busiTemplateParticipantArr.size(); i++)
                    {
                        BusiTemplateParticipant busiTemplateParticipant = busiTemplateParticipantArr.getObject(i, BusiTemplateParticipant.class);
                        Assert.notNull(busiTemplateParticipant.getTerminalId(), "参会者终端ID不能为空");
                        Assert.notNull(busiTemplateParticipant.getWeight(), "参会者weight顺序不能为空");
                        Assert.notNull(busiTemplateParticipant.getAttendType(), "参会者入会/直播类型不能为空");
                        busiTemplateParticipants.add(busiTemplateParticipant);
                    }
                }

                Long conferenceNumber = busiTemplateConference.getConferenceNumber();
                if(conferenceNumber==null){
                    throw new CustomException("会议号码不能为空");
                }

                // 部门顺序
                List<BusiTemplateDept> templateDepts = new ArrayList<>();
                busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());

                DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
                defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
                TransactionStatus transactionStatus = transactionManager.getTransaction(defaultTransactionDefinition);
                try {
                    BusiConferenceNumber busiConferenceNumber = new BusiConferenceNumber();
                    busiConferenceNumber.setId(busiTemplateConference.getConferenceNumber());
                    busiConferenceNumber.setDeptId(deptId);
                    busiConferenceNumber.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                    busiConferenceNumber.setType(2);
                    if(!isMute){

                        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(deptId);
                        String profileId = null;

                        BusiCallLegProfile con = new BusiCallLegProfile();
                        con.setDeptId(deptId);
                        List<BusiCallLegProfile> clps = busiCallLegProfileService.selectBusiCallLegProfileList(con);
                        if (!ObjectUtils.isEmpty(clps))
                        {
                            for (BusiCallLegProfile busiCallLegProfile : clps)
                            {
                                CallLegProfile clp = fmeBridge.getDataCache().getCallLegProfile(busiCallLegProfile.getCallLegProfileUuid());
                                if (clp != null)
                                {
                                    if(!clp.getRxAudioMute()){
                                        profileId = busiCallLegProfile.getCallLegProfileUuid();
                                        break;
                                    }
                                }
                            }

                            if (profileId == null)
                            {
                                logger.info("createDefaultCalllegProfileIsMute: " + busiTemplateConference.getConferenceNumber());
                                profileId = busiCallLegProfileService.createDefaultCalllegProfileIsMute(fmeBridge, deptId,false);
                            }
                        }
                        else
                        {
                            logger.info("createDefaultCalllegProfileIsMute: " + busiTemplateConference.getConferenceNumber());
                            profileId = busiCallLegProfileService.createDefaultCalllegProfileIsMute(fmeBridge, deptId,false);
                        }
                        busiConferenceNumber.getParams().put("callLegProfileId", profileId);
                        busiTemplateConference.setCallLegProfileId(profileId);
                    }

                    busiConferenceNumberService.insertBusiConferenceNumber(busiConferenceNumber);
                    int c = busiTemplateConferenceService.insertBusiTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
                    if (c > 0) {
                        setDefaultViewLayout(jsonObj, busiTemplateConference);
                        transactionManager.commit(transactionStatus);
                        return RestResponse.success(busiTemplateConferenceService.selectBusiTemplateConferenceById(busiTemplateConference.getId()));
                    } else {
                        return RestResponse.fail();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("创建模版会议失败", e);
                    transactionManager.rollback(transactionStatus);
                }
            }
            return RestResponse.fail();
        }
        if (McuType.MCU_ZJ == mcuType) {
            JSONObject templateConferenceObj = jsonObj.getJSONObject("templateConference");
            Assert.isTrue(templateConferenceObj != null, "会议模板不能为空！");
            Long masterTerminalId = templateConferenceObj.getLong("masterTerminalId");
            BusiMcuZjTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuZjTemplateConference.class);
            busiTemplateConference.setDeptId(deptId);
            busiTemplateConference.setType(2);
            busiTemplateConference.setViewType(1);
            busiTemplateConference.setIsAutoCreateConferenceNumber(2);
            busiTemplateConference.setIsAutoMonitor(2);
            busiTemplateConference.setBusinessFieldType(100);
            //主持人
            Long presenter = jsonObj.getLong("presenter");
            if (presenter != null) {
                busiTemplateConference.setPresenter(presenter);
            }
            JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("templateParticipants");
            List<BusiMcuZjTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
            if (busiTemplateParticipantArr != null) {
                for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                    BusiMcuZjTemplateParticipant busiTemplateParticipant = busiTemplateParticipantArr.getObject(i, BusiMcuZjTemplateParticipant.class);
                    Assert.notNull(busiTemplateParticipant.getTerminalId(), "参会者终端ID不能为空");
                    Assert.notNull(busiTemplateParticipant.getWeight(), "参会者weight顺序不能为空");
                    Assert.notNull(busiTemplateParticipant.getAttendType(), "参会者入会/直播类型不能为空");
                    busiTemplateParticipants.add(busiTemplateParticipant);
                }
            }

            // 部门顺序
            JSONArray templateDeptArr = jsonObj.getJSONArray("templateDepts");
            List<BusiMcuZjTemplateDept> templateDepts = new ArrayList<>();
            for (int i = 0; i < templateDeptArr.size(); i++) {
                BusiMcuZjTemplateDept busiTemplateDept = templateDeptArr.getObject(i, BusiMcuZjTemplateDept.class);
                Assert.notNull(busiTemplateDept.getDeptId(), "部门ID不能为空");
                Assert.notNull(busiTemplateDept.getWeight(), "部门weight顺序不能为空");
                templateDepts.add(busiTemplateDept);
            }

            busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());

            // 默认24小时
            busiTemplateConference.setDurationEnabled(1);// 开始会议时长限制
            busiTemplateConference.setDurationTime(1440);

            Integer muteType = busiTemplateConference.getMuteType();
            if (muteType == null || muteType != 0) {
                muteType = 1;// 0 不静音 1 静音
            }
            busiTemplateConference.setMuteType(muteType);
            // 默认自动分屏
            busiTemplateConference.setDefaultViewLayout(AutomaticSplitScreen.LAYOUT);
            busiTemplateConference.setDefaultViewIsFill(1);
            busiTemplateConference.setPollingInterval(10);
            busiTemplateConference.setDefaultViewIsDisplaySelf(-1);
            busiTemplateConference.setDefaultViewLayoutGuest(AutomaticSplitScreen.LAYOUT);
            busiTemplateConference.setDefaultViewIsFillGuest(1);
            busiTemplateConference.setPollingIntervalGuest(10);

            int c = busiMcuZjTemplateConferenceService.insertBusiMcuZjTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
            if (c > 0) {
                return RestResponse.success(busiMcuZjTemplateConferenceService.selectBusiMcuZjTemplateConferenceById(busiTemplateConference.getId()));
            } else {
                return RestResponse.fail();
            }
        }
        else if (McuType.MCU_PLC == mcuType) {
            JSONObject templateConferenceObj = jsonObj.getJSONObject("templateConference");
            Assert.isTrue(templateConferenceObj != null, "会议模板不能为空！");
            Long masterTerminalId = templateConferenceObj.getLong("masterTerminalId");
            BusiMcuPlcTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuPlcTemplateConference.class);
            busiTemplateConference.setDeptId(deptId);
            busiTemplateConference.setType(2);
            busiTemplateConference.setViewType(1);
            busiTemplateConference.setIsAutoCreateConferenceNumber(2);
            busiTemplateConference.setIsAutoMonitor(2);
            busiTemplateConference.setBusinessFieldType(100);
            //主持人
            Long presenter = jsonObj.getLong("presenter");
            if (presenter != null) {
                busiTemplateConference.setPresenter(presenter);
            }
            JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("templateParticipants");
            List<BusiMcuPlcTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
            if (busiTemplateParticipantArr != null) {
                for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                    BusiMcuPlcTemplateParticipant busiTemplateParticipant = busiTemplateParticipantArr.getObject(i, BusiMcuPlcTemplateParticipant.class);
                    Assert.notNull(busiTemplateParticipant.getTerminalId(), "参会者终端ID不能为空");
                    Assert.notNull(busiTemplateParticipant.getWeight(), "参会者weight顺序不能为空");
                    Assert.notNull(busiTemplateParticipant.getAttendType(), "参会者入会/直播类型不能为空");
                    busiTemplateParticipants.add(busiTemplateParticipant);
                }
            }

            // 部门顺序
            JSONArray templateDeptArr = jsonObj.getJSONArray("templateDepts");
            List<BusiMcuPlcTemplateDept> templateDepts = new ArrayList<>();
            for (int i = 0; i < templateDeptArr.size(); i++) {
                BusiMcuPlcTemplateDept busiTemplateDept = templateDeptArr.getObject(i, BusiMcuPlcTemplateDept.class);
                Assert.notNull(busiTemplateDept.getDeptId(), "部门ID不能为空");
                Assert.notNull(busiTemplateDept.getWeight(), "部门weight顺序不能为空");
                templateDepts.add(busiTemplateDept);
            }

            busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());

//            // 默认24小时
//            busiTemplateConference.setDurationEnabled(1);// 开始会议时长限制
//            busiTemplateConference.setDurationTime(1440);

            Integer muteType = busiTemplateConference.getMuteType();
            if (muteType == null || muteType != 0) {
                muteType = 1;// 0 不静音 1 静音
            }
            busiTemplateConference.setMuteType(muteType);
            // 默认自动分屏
            busiTemplateConference.setDefaultViewLayout(AutomaticSplitScreen.LAYOUT);
            busiTemplateConference.setDefaultViewIsFill(1);
            busiTemplateConference.setPollingInterval(10);
            busiTemplateConference.setDefaultViewIsDisplaySelf(-1);

            int c = busiMcuPlcTemplateConferenceService.insertBusiMcuPlcTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
            if (c > 0) {
                return RestResponse.success(busiMcuPlcTemplateConferenceService.selectBusiMcuPlcTemplateConferenceById(busiTemplateConference.getId()));
            } else {
                return RestResponse.fail();
            }
        }
        else if (McuType.MCU_KDC == mcuType) {
            JSONObject templateConferenceObj = jsonObj.getJSONObject("templateConference");
            Assert.isTrue(templateConferenceObj != null, "会议模板不能为空！");
            Long masterTerminalId = templateConferenceObj.getLong("masterTerminalId");
            BusiMcuKdcTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuKdcTemplateConference.class);
            busiTemplateConference.setDeptId(deptId);
            busiTemplateConference.setType(2);
            busiTemplateConference.setViewType(1);
            busiTemplateConference.setIsAutoCreateConferenceNumber(2);
            busiTemplateConference.setIsAutoMonitor(2);
            busiTemplateConference.setBusinessFieldType(100);
            //主持人
            Long presenter = jsonObj.getLong("presenter");
            if (presenter != null) {
                busiTemplateConference.setPresenter(presenter);
            }
            JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("templateParticipants");
            List<BusiMcuKdcTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
            if (busiTemplateParticipantArr != null) {
                for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                    BusiMcuKdcTemplateParticipant busiTemplateParticipant = busiTemplateParticipantArr.getObject(i, BusiMcuKdcTemplateParticipant.class);
                    Assert.notNull(busiTemplateParticipant.getTerminalId(), "参会者终端ID不能为空");
                    Assert.notNull(busiTemplateParticipant.getWeight(), "参会者weight顺序不能为空");
                    Assert.notNull(busiTemplateParticipant.getAttendType(), "参会者入会/直播类型不能为空");
                    busiTemplateParticipants.add(busiTemplateParticipant);
                }
            }

            // 部门顺序
            JSONArray templateDeptArr = jsonObj.getJSONArray("templateDepts");
            List<BusiMcuKdcTemplateDept> templateDepts = new ArrayList<>();
            for (int i = 0; i < templateDeptArr.size(); i++) {
                BusiMcuKdcTemplateDept busiTemplateDept = templateDeptArr.getObject(i, BusiMcuKdcTemplateDept.class);
                Assert.notNull(busiTemplateDept.getDeptId(), "部门ID不能为空");
                Assert.notNull(busiTemplateDept.getWeight(), "部门weight顺序不能为空");
                templateDepts.add(busiTemplateDept);
            }

            busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());

//            // 默认24小时
//            busiTemplateConference.setDurationEnabled(1);// 开始会议时长限制
//            busiTemplateConference.setDurationTime(1440);

            Integer muteType = busiTemplateConference.getMuteType();
            if (muteType == null || muteType != 0) {
                muteType = 1;// 0 不静音 1 静音
            }
            busiTemplateConference.setMuteType(muteType);
            // 默认自动分屏
            busiTemplateConference.setDefaultViewLayout(AutomaticSplitScreen.LAYOUT);
            busiTemplateConference.setDefaultViewIsFill(1);
            busiTemplateConference.setPollingInterval(10);
            busiTemplateConference.setDefaultViewIsDisplaySelf(-1);

            int c = busiMcuKdcTemplateConferenceService.insertBusiMcuKdcTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
            if (c > 0) {
                return RestResponse.success(busiMcuKdcTemplateConferenceService.selectBusiMcuKdcTemplateConferenceById(busiTemplateConference.getId()));
            } else {
                return RestResponse.fail();
            }
        }
        return RestResponse.fail();
    }

    private void createDefaultCallLegProfileIsNoMute(Long deptId, BusiConferenceNumber busiConferenceNumber) {
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(deptId);
        String calllegProfileId = busiCallLegProfileService.createDefaultCalllegProfileIsMute(fmeBridge, deptId, false);
        HashMap<String, Object> params = new HashMap<>();
        params.put("callLegProfileId",calllegProfileId);
        busiConferenceNumber.setParams(params);
    }

    private void setDefaultViewLayout(JSONObject jsonObj, BusiTemplateConference busiTemplateConference) {
        ModelBean modelBean = busiTemplateConferenceService.selectBusiTemplateConferenceById(busiTemplateConference.getId());
        if(!Objects.isNull(modelBean)){
            Object obj = modelBean.get("templateConference");
            String jsonString = JSON.toJSONString(obj);
            BusiTemplateConference templateConference = JSON.parseObject(jsonString, BusiTemplateConference.class);
            Long id = templateConference.getId();
            JSONObject templateConferenceLayout = jsonObj.getJSONObject("layout");
            String defaultViewLayout = templateConferenceLayout.getString("defaultViewLayout");
            List<DefaultViewCellScreens> defaultViewLayoutObj = new ArrayList<>();

            switch (defaultViewLayout) {
                case OneSplitScreen.LAYOUT:
                    defaultViewLayoutObj.add(new DefaultViewCellScreens(1));
                    templateConferenceLayout.put("defaultViewCellScreens", defaultViewLayoutObj);
                    break;
                case FourSplitScreen.LAYOUT:
                    for (int i = 0; i < INT4; i++) {
                        defaultViewLayoutObj.add(new DefaultViewCellScreens(i + 1));
                    }
                    templateConferenceLayout.put("defaultViewCellScreens", defaultViewLayoutObj);
                    break;
                case NineSplitScreen.LAYOUT:
                    for (int i = 0; i < INT8; i++) {
                        defaultViewLayoutObj.add(new DefaultViewCellScreens(i + 1));
                    }
                    templateConferenceLayout.put("defaultViewCellScreens", defaultViewLayoutObj);
                    break;
                default:
                    break;
            }

            busiTemplateConferenceService.updateDefaultViewConfigInfo(templateConferenceLayout, id);
        }
    }

    /**
     * 修改会议模板
     */
    @Log(title = "会议模板", businessType = BusinessType.UPDATE)
    @PutMapping("/{conferenceId}")
    @Operation(summary = "修改会议模板")
    public RestResponse edit(@RequestBody JSONObject jsonObj, @PathVariable("conferenceId") String conferenceId)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long deptId = loginUser.getUser().getDeptId();
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        if (McuType.FME == mcuType) {
            JSONObject templateConferenceObj = jsonObj.getJSONObject("templateConference");
            Assert.isTrue(templateConferenceObj != null, "会议模板不能为空！");
            Long masterTerminalId = templateConferenceObj.getLong("masterTerminalId");
            BusiTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiTemplateConference.class);

            busiTemplateConference.setDeptId(deptId);
            busiTemplateConference.setType(2);
            busiTemplateConference.setViewType(1);
            busiTemplateConference.setIsAutoCreateConferenceNumber(2);
            busiTemplateConference.setIsAutoMonitor(2);
            busiTemplateConference.setBusinessFieldType(100);


            busiTemplateConference.setId(id);

            JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("templateParticipants");
            List<BusiTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
            if (busiTemplateParticipantArr != null) {
                for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                    BusiTemplateParticipant p = busiTemplateParticipantArr.getObject(i, BusiTemplateParticipant.class);
                    p.setId(null);
                    Assert.notNull(p.getTerminalId(), "参会者终端ID不能为空");
                    Assert.notNull(p.getWeight(), "参会者weight顺序不能为空");
                    Assert.notNull(p.getAttendType(), "参会者入会/直播类型不能为空");
                    busiTemplateParticipants.add(p);
                }
            }

            // 部门顺序
            JSONArray templateDeptArr = jsonObj.getJSONArray("templateDepts");
            List<BusiTemplateDept> templateDepts = new ArrayList<>();
            if (!CollectionUtils.isEmpty(templateDeptArr)) {
                for (int i = 0; i < templateDeptArr.size(); i++) {
                    templateDepts.add(templateDeptArr.getObject(i, BusiTemplateDept.class));
                }

            }
            //主持人
            Long presenter = jsonObj.getLong("presenter");
            if (presenter != null) {
                busiTemplateConference.setPresenter(presenter);
            }

            busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
            int c = busiTemplateConferenceService.updateBusiTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
            if (c > 0) {
                return RestResponse.success(busiTemplateConferenceService.selectBusiTemplateConferenceById(busiTemplateConference.getId()));
            } else {
                return RestResponse.fail();
            }
        }
        else if (McuType.MCU_ZJ == mcuType) {
            JSONObject templateConferenceObj = jsonObj.getJSONObject("templateConference");
            Assert.isTrue(templateConferenceObj != null, "会议模板不能为空！");
            Long masterTerminalId = templateConferenceObj.getLong("masterTerminalId");
            BusiMcuZjTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuZjTemplateConference.class);

            busiTemplateConference.setDeptId(deptId);
            busiTemplateConference.setType(2);
            busiTemplateConference.setViewType(1);
            busiTemplateConference.setIsAutoCreateConferenceNumber(2);
            busiTemplateConference.setIsAutoMonitor(2);
            busiTemplateConference.setBusinessFieldType(100);
            //主持人
            Long presenter = jsonObj.getLong("presenter");
            if (presenter != null) {
                busiTemplateConference.setPresenter(presenter);
            }

            busiTemplateConference.setId(id);

            JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("templateParticipants");
            List<BusiMcuZjTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
            if (busiTemplateParticipantArr != null) {
                for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                    BusiMcuZjTemplateParticipant p = busiTemplateParticipantArr.getObject(i, BusiMcuZjTemplateParticipant.class);
                    p.setId(null);
                    Assert.notNull(p.getTerminalId(), "参会者终端ID不能为空");
                    Assert.notNull(p.getWeight(), "参会者weight顺序不能为空");
                    Assert.notNull(p.getAttendType(), "参会者入会/直播类型不能为空");
                    busiTemplateParticipants.add(p);
                }
            }

            // 部门顺序
            JSONArray templateDeptArr = jsonObj.getJSONArray("templateDepts");
            List<BusiMcuZjTemplateDept> templateDepts = new ArrayList<>();
            for (int i = 0; i < templateDeptArr.size(); i++) {
                templateDepts.add(templateDeptArr.getObject(i, BusiMcuZjTemplateDept.class));
            }

            busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
            int c = busiMcuZjTemplateConferenceService.updateBusiMcuZjTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
            if (c > 0) {
                return RestResponse.success(busiMcuZjTemplateConferenceService.selectBusiMcuZjTemplateConferenceById(busiTemplateConference.getId()));
            } else {
                return RestResponse.fail();
            }
        }
        else if (McuType.MCU_PLC == mcuType) {
            JSONObject templateConferenceObj = jsonObj.getJSONObject("templateConference");
            Assert.isTrue(templateConferenceObj != null, "会议模板不能为空！");
            Long masterTerminalId = templateConferenceObj.getLong("masterTerminalId");
            BusiMcuPlcTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuPlcTemplateConference.class);

            busiTemplateConference.setDeptId(deptId);
            busiTemplateConference.setType(2);
            busiTemplateConference.setViewType(1);
            busiTemplateConference.setIsAutoCreateConferenceNumber(2);
            busiTemplateConference.setIsAutoMonitor(2);
            busiTemplateConference.setBusinessFieldType(100);
            //主持人
            Long presenter = jsonObj.getLong("presenter");
            if (presenter != null) {
                busiTemplateConference.setPresenter(presenter);
            }

            busiTemplateConference.setId(id);

            JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("templateParticipants");
            List<BusiMcuPlcTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
            if (busiTemplateParticipantArr != null) {
                for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                    BusiMcuPlcTemplateParticipant p = busiTemplateParticipantArr.getObject(i, BusiMcuPlcTemplateParticipant.class);
                    p.setId(null);
                    Assert.notNull(p.getTerminalId(), "参会者终端ID不能为空");
                    Assert.notNull(p.getWeight(), "参会者weight顺序不能为空");
                    Assert.notNull(p.getAttendType(), "参会者入会/直播类型不能为空");
                    busiTemplateParticipants.add(p);
                }
            }

            // 部门顺序
            JSONArray templateDeptArr = jsonObj.getJSONArray("templateDepts");
            List<BusiMcuPlcTemplateDept> templateDepts = new ArrayList<>();
            for (int i = 0; i < templateDeptArr.size(); i++) {
                templateDepts.add(templateDeptArr.getObject(i, BusiMcuPlcTemplateDept.class));
            }

            busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
            int c = busiMcuPlcTemplateConferenceService.updateBusiMcuPlcTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
            if (c > 0) {
                return RestResponse.success(busiMcuPlcTemplateConferenceService.selectBusiMcuPlcTemplateConferenceById(busiTemplateConference.getId()));
            } else {
                return RestResponse.fail();
            }
        }
        else if (McuType.MCU_KDC == mcuType) {
            JSONObject templateConferenceObj = jsonObj.getJSONObject("templateConference");
            Assert.isTrue(templateConferenceObj != null, "会议模板不能为空！");
            Long masterTerminalId = templateConferenceObj.getLong("masterTerminalId");
            BusiMcuKdcTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuKdcTemplateConference.class);

            busiTemplateConference.setDeptId(deptId);
            busiTemplateConference.setType(2);
            busiTemplateConference.setViewType(1);
            busiTemplateConference.setIsAutoCreateConferenceNumber(2);
            busiTemplateConference.setIsAutoMonitor(2);
            busiTemplateConference.setBusinessFieldType(100);
            //主持人
            Long presenter = jsonObj.getLong("presenter");
            if (presenter != null) {
                busiTemplateConference.setPresenter(presenter);
            }

            busiTemplateConference.setId(id);

            JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("templateParticipants");
            List<BusiMcuKdcTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
            if (busiTemplateParticipantArr != null) {
                for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                    BusiMcuKdcTemplateParticipant p = busiTemplateParticipantArr.getObject(i, BusiMcuKdcTemplateParticipant.class);
                    p.setId(null);
                    Assert.notNull(p.getTerminalId(), "参会者终端ID不能为空");
                    Assert.notNull(p.getWeight(), "参会者weight顺序不能为空");
                    Assert.notNull(p.getAttendType(), "参会者入会/直播类型不能为空");
                    busiTemplateParticipants.add(p);
                }
            }

            // 部门顺序
            JSONArray templateDeptArr = jsonObj.getJSONArray("templateDepts");
            List<BusiMcuKdcTemplateDept> templateDepts = new ArrayList<>();
            for (int i = 0; i < templateDeptArr.size(); i++) {
                templateDepts.add(templateDeptArr.getObject(i, BusiMcuKdcTemplateDept.class));
            }

            busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
            int c = busiMcuKdcTemplateConferenceService.updateBusiMcuKdcTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
            if (c > 0) {
                return RestResponse.success(busiMcuKdcTemplateConferenceService.selectBusiMcuKdcTemplateConferenceById(busiTemplateConference.getId()));
            } else {
                return RestResponse.fail();
            }
        }
        return RestResponse.fail();
    }

    /**
     * 删除会议模板
     */
    @Log(title = "会议模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/{conferenceId}")
    @Operation(summary = "删除会议模板")
    public RestResponse remove(@PathVariable String conferenceId)
    {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        if (McuType.FME == mcuType) {
            return toAjax(busiTemplateConferenceService.deleteMobileBusiTemplateConferenceById(id));
        }
        else if (McuType.MCU_ZJ == mcuType) {
            return toAjax(busiMcuZjTemplateConferenceService.deleteBusiMcuZjTemplateConferenceById(id));
        }
        else if (McuType.MCU_PLC == mcuType) {
            return toAjax(busiMcuPlcTemplateConferenceService.deleteBusiMcuPlcTemplateConferenceById(id));
        }
        else if (McuType.MCU_KDC == mcuType) {
            return toAjax(busiMcuKdcTemplateConferenceService.deleteBusiMcuKdcTemplateConferenceById(id));
        }
        return toAjax(0);
    }

    /**
     * 开始会议
     */
    @PostMapping("/startByTemplate/{conferenceId}")
    @Operation(summary = "开始会议")
    public RestResponse startByTemplate(@PathVariable String conferenceId)
    {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        if (McuType.FME == mcuType) {
            String contextKey = templateConferenceStartService.startTemplateConference(id);
            String cnStr = EncryptIdUtil.generateConferenceId(contextKey);
            return success(cnStr);
        }
        else if (McuType.MCU_ZJ.getCode().equals(mcuType)) {
            String contextKey = busiMcuZjConferenceService.startTemplateConference(id);
            String cnStr = EncryptIdUtil.generateConferenceId(contextKey);
            return success(cnStr);
        }
        else if (McuType.MCU_PLC.getCode().equals(mcuType)) {
            String contextKey = busiMcuPlcConferenceService.startTemplateConference(id);
            String cnStr = EncryptIdUtil.generateConferenceId(contextKey);
            return success(cnStr);
        }
        else if (McuType.MCU_KDC.getCode().equals(mcuType)) {
            String contextKey = busiMcuKdcConferenceService.startTemplateConference(id);
            String cnStr = EncryptIdUtil.generateConferenceId(contextKey);
            return success(cnStr);
        }
        return RestResponse.fail();
    }

    /**
     * 查询会议模板列表
     */
    @GetMapping("/list/searchkey")
    @Operation(summary = "通过关键字查询会议模板列表")
    public RestResponse listKeySearch(@RequestParam("searchKey")String searchKey,
                                      @RequestParam(value = "pageIndex",defaultValue = "1") int pageIndex,
                                      @RequestParam(value="pageSize",defaultValue = "10") int pageSize)
    {
        PageHelper.startPage(pageIndex, pageSize);
        Page<ViewTemplateConference> page = viewTemplateConferenceMapper.selectViewTemplateConferenceListByKey(searchKey, null);
        PaginationData<Object> pd = new PaginationData<>();
        pd.setTotal(new PageInfo<>(page.getResult()).getTotal());
        for (ViewTemplateConference viewTemplateConferenceTemp : page) {

            McuType mcuType = McuType.convert(viewTemplateConferenceTemp.getMcuType());
            switch (mcuType) {
                case FME: {
                    BusiTemplateConference busiTemplateConference = new BusiTemplateConference();
                    BeanUtils.copyBeanProp(busiTemplateConference, viewTemplateConferenceTemp);
                    ModelBean modelBean = busiTemplateConferenceService.getTemplateConferenceDetails(busiTemplateConference);
                    modelBean.put("conferenceId", viewTemplateConferenceTemp.getConferenceId());
                    modelBean.put("mcuType", viewTemplateConferenceTemp.getMcuType());
                    modelBean.put("mcuTypeAlias", McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
                    if (StringUtils.isNotEmpty(viewTemplateConferenceTemp.getUpCascadeConferenceId())) {
                        modelBean.put("isDownCascade", true);
                    } else {
                        modelBean.put("isDownCascade", false);
                    }
                    ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
                    viewTemplateConferenceCon.setUpCascadeId(viewTemplateConferenceTemp.getId());
                    viewTemplateConferenceCon.setUpCascadeMcuType(viewTemplateConferenceTemp.getMcuType());
                    List<ViewTemplateConference> downCascadeList = viewTemplateConferenceMapper.selectViewTemplateConferenceList(viewTemplateConferenceCon);
                    if (downCascadeList.size() > 0) {
                        modelBean.put("isUpCascade", true);
                    } else {
                        modelBean.put("isUpCascade", false);
                    }
                    pd.addRecord(modelBean);
                    break;
                }
                case MCU_ZJ: {
                    BusiMcuZjTemplateConference busiMcuZjTemplateConference = new BusiMcuZjTemplateConference();
                    BeanUtils.copyBeanProp(busiMcuZjTemplateConference, viewTemplateConferenceTemp);
                    ModelBean modelBean = busiMcuZjTemplateConferenceService.getTemplateConferenceDetails(busiMcuZjTemplateConference);
                    modelBean.put("conferenceId", viewTemplateConferenceTemp.getConferenceId());
                    modelBean.put("mcuType", viewTemplateConferenceTemp.getMcuType());
                    modelBean.put("mcuTypeAlias", McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
                    if (StringUtils.isNotEmpty(viewTemplateConferenceTemp.getUpCascadeConferenceId())) {
                        modelBean.put("isDownCascade", true);
                    } else {
                        modelBean.put("isDownCascade", false);
                    }
                    ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
                    viewTemplateConferenceCon.setUpCascadeId(viewTemplateConferenceTemp.getId());
                    viewTemplateConferenceCon.setUpCascadeMcuType(viewTemplateConferenceTemp.getMcuType());
                    List<ViewTemplateConference> downCascadeList = viewTemplateConferenceMapper.selectViewTemplateConferenceList(viewTemplateConferenceCon);
                    if (downCascadeList.size() > 0) {
                        modelBean.put("isUpCascade", true);
                    } else {
                        modelBean.put("isUpCascade", false);
                    }
                    pd.addRecord(modelBean);
                    break;
                }
                case MCU_PLC: {
                    BusiMcuPlcTemplateConference busiMcuPlcTemplateConference = new BusiMcuPlcTemplateConference();
                    BeanUtils.copyBeanProp(busiMcuPlcTemplateConference, viewTemplateConferenceTemp);
                    ModelBean modelBean = busiMcuPlcTemplateConferenceService.getTemplateConferenceDetails(busiMcuPlcTemplateConference);
                    modelBean.put("conferenceId", viewTemplateConferenceTemp.getConferenceId());
                    modelBean.put("mcuType", viewTemplateConferenceTemp.getMcuType());
                    modelBean.put("mcuTypeAlias", McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
                    if (StringUtils.isNotEmpty(viewTemplateConferenceTemp.getUpCascadeConferenceId())) {
                        modelBean.put("isDownCascade", true);
                    } else {
                        modelBean.put("isDownCascade", false);
                    }
                    ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
                    viewTemplateConferenceCon.setUpCascadeId(viewTemplateConferenceTemp.getId());
                    viewTemplateConferenceCon.setUpCascadeMcuType(viewTemplateConferenceTemp.getMcuType());
                    List<ViewTemplateConference> downCascadeList = viewTemplateConferenceMapper.selectViewTemplateConferenceList(viewTemplateConferenceCon);
                    if (downCascadeList.size() > 0) {
                        modelBean.put("isUpCascade", true);
                    } else {
                        modelBean.put("isUpCascade", false);
                    }
                    pd.addRecord(modelBean);
                    break;
                }
                case MCU_KDC: {
                    BusiMcuKdcTemplateConference busiMcuKdcTemplateConference = new BusiMcuKdcTemplateConference();
                    BeanUtils.copyBeanProp(busiMcuKdcTemplateConference, viewTemplateConferenceTemp);
                    ModelBean modelBean = busiMcuKdcTemplateConferenceService.getTemplateConferenceDetails(busiMcuKdcTemplateConference);
                    modelBean.put("conferenceId", viewTemplateConferenceTemp.getConferenceId());
                    modelBean.put("mcuType", viewTemplateConferenceTemp.getMcuType());
                    modelBean.put("mcuTypeAlias", McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
                    if (StringUtils.isNotEmpty(viewTemplateConferenceTemp.getUpCascadeConferenceId())) {
                        modelBean.put("isDownCascade", true);
                    } else {
                        modelBean.put("isDownCascade", false);
                    }
                    ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
                    viewTemplateConferenceCon.setUpCascadeId(viewTemplateConferenceTemp.getId());
                    viewTemplateConferenceCon.setUpCascadeMcuType(viewTemplateConferenceTemp.getMcuType());
                    List<ViewTemplateConference> downCascadeList = viewTemplateConferenceMapper.selectViewTemplateConferenceList(viewTemplateConferenceCon);
                    if (downCascadeList.size() > 0) {
                        modelBean.put("isUpCascade", true);
                    } else {
                        modelBean.put("isUpCascade", false);
                    }
                    pd.addRecord(modelBean);
                    break;
                }
            }
        }
        return RestResponse.success(0, "查询成功", pd);
    }

    /**
     * 通过会议号码查询查询会议详情
     */
    @GetMapping("/conferenceNumber/{conferenceNumber}")
    @Operation(summary = "通过会议号码查询查询会议详情")
    public RestResponse listKeySearch(@PathVariable Long conferenceNumber)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long deptId = loginUser.getUser().getDeptId();
        List<String> mcuTypes = new ArrayList<>();
        mcuTypes.add(McuType.FME.getCode());
        mcuTypes.add(McuType.MCU_ZJ.getCode());
        mcuTypes.add(McuType.MCU_PLC.getCode());
        mcuTypes.add(McuType.MCU_KDC.getCode());
        ViewTemplateConference viewTemplateConference = new ViewTemplateConference();
        viewTemplateConference.setDeptId(deptId);
        viewTemplateConference.setConferenceNumber(conferenceNumber);
        viewTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
        viewTemplateConference.getParams().put("mcuTypes", mcuTypes);
        List<ViewTemplateConference> allList = viewTemplateConferenceMapper.selectViewTemplateConferenceList(viewTemplateConference);
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (ViewTemplateConference viewTemplateConferenceTemp : allList) {
            McuType mcuType = McuType.convert(viewTemplateConferenceTemp.getMcuType());
            switch (mcuType) {
                case FME: {
                    BusiTemplateConference templateConferenceCon = new BusiTemplateConference();
                    templateConferenceCon.setId(viewTemplateConferenceTemp.getId());
                    templateConferenceCon.setBusinessFieldType(BusinessFieldType.COMMON.getValue());
                    List<BusiTemplateConference> list = busiTemplateConferenceService.selectBusiTemplateConferenceList(templateConferenceCon);
                    if (!CollectionUtils.isEmpty(list)) {
                        BusiTemplateConference templateConference = list.get(0);
                        Long templateConferenceId = templateConference.getId();
                        ConferenceContext conferenceContext = templateConferenceStartService.buildTemplateConferenceContext(templateConferenceId);
                        JSONObject json = null;
                        ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                        try {
                            json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
                        } catch (Exception e) {
                        }
                        boolean isPresenter = false;
                        boolean isMyConference = false;
                        if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isPresenter = true;
                        }
                        if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isMyConference = true;
                        }
                        json.put("isPresenter", isPresenter);
                        json.put("isMyConference", isMyConference);
                        List<ModelBean> splitScreenList = new ArrayList<>();
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "一分屏");
                            modelBean.put("value", OneSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "四分屏");
                            modelBean.put("value", FourSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "九分屏");
                            modelBean.put("value", NineSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "自动");
                            modelBean.put("value", AutomaticSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "全等");
                            modelBean.put("value", AllEqualSplitScreen.LAYOUT);
                            modelBean.put("isDefault", true);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "一大N小");
                            modelBean.put("value", OnePlusNSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }

                        json.put("splitScreenList", splitScreenList);
                        jsonObjectList.add(json);
                    }
                    break;
                }
                case MCU_ZJ: {
                    BusiMcuZjTemplateConference templateConferenceCon = new BusiMcuZjTemplateConference();
                    templateConferenceCon.setId(viewTemplateConferenceTemp.getId());
                    templateConferenceCon.setBusinessFieldType(BusinessFieldType.COMMON.getValue());
                    List<BusiMcuZjTemplateConference> list = busiMcuZjTemplateConferenceService.selectBusiMcuZjTemplateConferenceList(templateConferenceCon);
                    if (!CollectionUtils.isEmpty(list)) {
                        BusiMcuZjTemplateConference templateConference = list.get(0);
                        Long templateConferenceId = templateConference.getId();
                        McuZjConferenceContext conferenceContext = busiMcuZjConferenceService.buildTemplateConferenceContext(templateConferenceId);
                        JSONObject json = null;
                        ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                        try {
                            json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
                        } catch (Exception e) {
                        }
                        boolean isPresenter = false;
                        boolean isMyConference = false;
                        if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isPresenter = true;
                        }
                        if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isMyConference = true;
                        }
                        json.put("isPresenter", isPresenter);
                        json.put("isMyConference", isMyConference);
                        json.remove("supervisorPassword");
                        List<ModelBean> splitScreenList = new ArrayList<>();

                        if (conferenceContext.isSingleView()) {
                            splitScreenList = conferenceContext.getSpeakerSplitScreenList();
                        } else {
                            splitScreenList = conferenceContext.getGuestSplitScreenList();
                        }
                        for (int i = 0; i < splitScreenList.size(); i++) {
                            ModelBean modelBean = splitScreenList.get(i);
                            if (i == 0) {
                                modelBean.put("isDefault", true);
                            } else {
                                modelBean.put("isDefault", false);
                            }
                        }
                        json.put("splitScreenList", splitScreenList);
                        jsonObjectList.add(json);
                    }
                    break;
                }
                case MCU_PLC: {
                    BusiMcuPlcTemplateConference templateConferenceCon = new BusiMcuPlcTemplateConference();
                    templateConferenceCon.setId(viewTemplateConferenceTemp.getId());
                    templateConferenceCon.setBusinessFieldType(BusinessFieldType.COMMON.getValue());
                    List<BusiMcuPlcTemplateConference> list = busiMcuPlcTemplateConferenceService.selectBusiMcuPlcTemplateConferenceList(templateConferenceCon);
                    if (!CollectionUtils.isEmpty(list)) {
                        BusiMcuPlcTemplateConference templateConference = list.get(0);
                        Long templateConferenceId = templateConference.getId();
                        McuPlcConferenceContext conferenceContext = busiMcuPlcConferenceService.buildTemplateConferenceContext(templateConferenceId);
                        JSONObject json = null;
                        ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                        try {
                            json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
                        } catch (Exception e) {
                        }
                        boolean isPresenter = false;
                        boolean isMyConference = false;
                        if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isPresenter = true;
                        }
                        if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isMyConference = true;
                        }
                        json.put("isPresenter", isPresenter);
                        json.put("isMyConference", isMyConference);
                        json.remove("supervisorPassword");
                        List<ModelBean> splitScreenList = conferenceContext.getSpeakerSplitScreenList();
                        json.put("splitScreenList", splitScreenList);
                        jsonObjectList.add(json);
                    }
                    break;
                }
                case MCU_KDC: {
                    BusiMcuKdcTemplateConference templateConferenceCon = new BusiMcuKdcTemplateConference();
                    templateConferenceCon.setId(viewTemplateConferenceTemp.getId());
                    templateConferenceCon.setBusinessFieldType(BusinessFieldType.COMMON.getValue());
                    List<BusiMcuKdcTemplateConference> list = busiMcuKdcTemplateConferenceService.selectBusiMcuKdcTemplateConferenceList(templateConferenceCon);
                    if (!CollectionUtils.isEmpty(list)) {
                        BusiMcuKdcTemplateConference templateConference = list.get(0);
                        Long templateConferenceId = templateConference.getId();
                        McuKdcConferenceContext conferenceContext = busiMcuKdcConferenceService.buildTemplateConferenceContext(templateConferenceId);
                        JSONObject json = null;
                        ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                        try {
                            json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
                        } catch (Exception e) {
                        }
                        boolean isPresenter = false;
                        boolean isMyConference = false;
                        if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isPresenter = true;
                        }
                        if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isMyConference = true;
                        }
                        json.put("isPresenter", isPresenter);
                        json.put("isMyConference", isMyConference);
                        json.remove("supervisorPassword");
                        List<ModelBean> splitScreenList = conferenceContext.getSpeakerSplitScreenList();
                        json.put("splitScreenList", splitScreenList);
                        jsonObjectList.add(json);
                    }
                    break;
                }
            }
        }
        return RestResponse.success(0, "查询成功", jsonObjectList);
    }

    private JSONObject getJsonObject(String s) {
        return JSONObject.parseObject(s);
    }

}
