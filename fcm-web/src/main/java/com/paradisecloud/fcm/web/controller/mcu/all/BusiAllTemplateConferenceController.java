package com.paradisecloud.fcm.web.controller.mcu.all;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.common.utils.bean.BeanUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.constant.DeptConstant;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.model.PaginationDataNew;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.ding.service2.interfaces.IBusiDingConferenceService;
import com.paradisecloud.fcm.ding.service2.interfaces.IBusiMcuDingTemplateConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.ITemplateConferenceStartService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.ConfPresetParamDTO;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiHwcloudConferenceService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiMcuHwcloudTemplateConferenceService;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcBridgeCache;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcBridge;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcConferenceService;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcTemplateConferenceService;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcBridgeCache;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcBridge;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcConferenceService;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcTemplateConferenceService;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.SourceTemplate;
import com.paradisecloud.fcm.mcu.zj.model.busi.layout.splitscreen.AutomaticSplitScreen;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjConferenceService;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjTemplateConferenceService;
import com.paradisecloud.fcm.ops.cloud.cache.OpsCache;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiMcuSmc2TemplateConferenceService;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiSmc2ConferenceService;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiMcuTencentTemplateConferenceService;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiTencentConferenceService;
import com.paradisecloud.fcm.zte.service.interfaces.IBusiMcuZteConferenceService;
import com.paradisecloud.fcm.zte.service.interfaces.IBusiMcuZteTemplateConferenceService;
import com.paradisecloud.smc3.model.request.TemplateNode;
import com.paradisecloud.smc3.model.request.TemplateNodeTemp;
import com.paradisecloud.smc3.service.interfaces.IBusiMcuSmc3TemplateConferenceService;
import com.paradisecloud.smc3.service.interfaces.IBusiSmc3ConferenceService;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 会议模板Controller
 *
 * @author lilinhai
 * @date 2021-01-20
 */
@RestController
@RequestMapping("/busi/mcu/all/templateConference")
@Tag(name = "会议模板")
public class BusiAllTemplateConferenceController extends BaseController {

    @Resource
    private ViewTemplateConferenceMapper viewTemplateConferenceMapper;
    @Resource
    private IBusiTemplateConferenceService busiTemplateConferenceService;
    @Resource
    private ITemplateConferenceStartService templateConferenceStartService;
    @Resource
    private BusiTemplateConferenceMapper busiTemplateConferenceMapper;
    @Resource
    private IBusiMcuZjTemplateConferenceService busiMcuZjTemplateConferenceService;
    @Resource
    private IBusiMcuZjConferenceService busiMcuZjConferenceService;
    @Resource
    private BusiMcuZjTemplateConferenceMapper busiMcuZjTemplateConferenceMapper;
    @Resource
    private IBusiMcuPlcTemplateConferenceService busiMcuPlcTemplateConferenceService;
    @Resource
    private IBusiMcuPlcConferenceService busiMcuPlcConferenceService;
    @Resource
    private BusiMcuPlcTemplateConferenceMapper busiMcuPlcTemplateConferenceMapper;
    @Resource
    private IBusiMcuKdcTemplateConferenceService busiMcuKdcTemplateConferenceService;
    @Resource
    private IBusiMcuKdcConferenceService busiMcuKdcConferenceService;
    @Resource
    private BusiMcuKdcTemplateConferenceMapper busiMcuKdcTemplateConferenceMapper;
    @Resource
    private IBusiMcuSmc3TemplateConferenceService busiMcuSmc3TemplateConferenceService;
    @Resource
    private IBusiSmc3ConferenceService busiSmc3ConferenceService;
    @Resource
    private BusiMcuSmc3TemplateConferenceMapper busiMcuSmc3TemplateConferenceMapper;
    @Resource
    private IBusiMcuSmc2TemplateConferenceService busiMcuSmc2TemplateConferenceService;
    @Resource
    private IBusiSmc2ConferenceService busiSmc2ConferenceService;
    @Resource
    private IBusiTencentConferenceService busiTencentConferenceService;
    @Resource
    private IBusiDingConferenceService busiDingConferenceService;
    @Resource
    private IBusiHwcloudConferenceService busiHwcloudConferenceService;
    @Resource
    private BusiMcuSmc2TemplateConferenceMapper busiMcuSmc2TemplateConferenceMapper;
    @Resource
    private BusiMcuTencentTemplateConferenceMapper busiMcuTencentTemplateConferenceMapper;
    @Resource
    private BusiMcuDingTemplateConferenceMapper busiMcuDingTemplateConferenceMapper;
    @Resource
    private BusiMcuHwcloudTemplateConferenceMapper busiMcuHwcloudTemplateConferenceMapper;
    @Resource
    private IBusiMcuTencentTemplateConferenceService busiMcuTencentTemplateConferenceService;
    @Resource
    private BusiMcuTencentConferenceAppointmentMapper busiMcuTencentConferenceAppointmentMapper;
    @Resource
    private IBusiMcuDingTemplateConferenceService busiMcuDingTemplateConferenceService;
    @Resource
    private IBusiMcuHwcloudTemplateConferenceService busiMcuHwcloudTemplateConferenceService;
    @Resource
    private BusiMcuHwcloudConferenceAppointmentMapper busiMcuHwcloudConferenceAppointmentMapper;
    @Resource
    private IBusiMcuZteTemplateConferenceService busiMcuZteTemplateConferenceService;
    @Resource
    private IBusiMcuZteConferenceService busiMcuZteConferenceService;
    @Resource
    private BusiMcuZteTemplateConferenceMapper busiMcuZteTemplateConferenceMapper;

    public  static String regex = "^\\d{4,6}$";
    public  static String regex_chair = "^\\d{6}$";
    /**
     * 查询会议模板列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询会议模板列表")
    public RestResponse list(ViewTemplateConference viewTemplateConference) {
        Map<String, String> data = new HashMap<>();
        viewTemplateConference.setMcuType(null);
        if (viewTemplateConference.isModeConference()) {
            viewTemplateConference.getParams().put("modeConference", "true");
        } else {
            viewTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
        }
        ViewTemplateConference viewTemplateConferenceAll = new ViewTemplateConference();
        BeanUtils.copyBeanProp(viewTemplateConferenceAll, viewTemplateConference);
        List<ViewTemplateConference> listAll = viewTemplateConferenceMapper.selectViewTemplateConferenceList(viewTemplateConferenceAll);
        for (ViewTemplateConference viewTemplateConferenceTemp : listAll) {
            data.put(viewTemplateConferenceTemp.getMcuType(), McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
        }
        startPage();
        List<ViewTemplateConference> list = viewTemplateConferenceMapper.selectViewTemplateConferenceList(viewTemplateConference);
        PaginationDataNew<ModelBean> pd = new PaginationDataNew<>();
        pd.setTotal(new PageInfo<>(list).getTotal());
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
                    data.put(viewTemplateConferenceTemp.getMcuType(), McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
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
                    data.put(viewTemplateConferenceTemp.getMcuType(), McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
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
                    data.put(viewTemplateConferenceTemp.getMcuType(), McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
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
                    data.put(viewTemplateConferenceTemp.getMcuType(), McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
                    break;
                }
                case SMC3: {
                    BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = new BusiMcuSmc3TemplateConference();
                    BeanUtils.copyBeanProp(busiMcuSmc3TemplateConference, viewTemplateConferenceTemp);
                    ModelBean modelBean = busiMcuSmc3TemplateConferenceService.getTemplateConferenceDetails(busiMcuSmc3TemplateConference);
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

                    ModelBean modelBean1 = busiMcuSmc3TemplateConferenceService.selectBusiTemplateConferenceById(viewTemplateConferenceTemp.getId());
                    Object templateConference = modelBean1.get("templateConference");
                    BusiMcuSmc3TemplateConference smc3TemplateConference = JSONObject.parseObject(JSONObject.toJSONString(templateConference), BusiMcuSmc3TemplateConference.class);
                    modelBean.put("category", smc3TemplateConference.getCategory());
                    modelBean.put("smcTemplateId", smc3TemplateConference.getSmcTemplateId());
                    mbs.add(modelBean);
                    data.put(viewTemplateConferenceTemp.getMcuType(), McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
                    break;
                }
                case SMC2: {
                    BusiMcuSmc2TemplateConference busiMcuSmc2TemplateConference = new BusiMcuSmc2TemplateConference();
                    BeanUtils.copyBeanProp(busiMcuSmc2TemplateConference, viewTemplateConferenceTemp);
                    ModelBean modelBean = busiMcuSmc2TemplateConferenceService.getTemplateConferenceDetails(busiMcuSmc2TemplateConference);
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
                    data.put(viewTemplateConferenceTemp.getMcuType(), McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
                    break;
                }

                case MCU_TENCENT: {
                    BusiMcuTencentTemplateConference busiMcuTencentTemplateConference = new BusiMcuTencentTemplateConference();
                    BeanUtils.copyBeanProp(busiMcuTencentTemplateConference, viewTemplateConferenceTemp);
                    ModelBean modelBean = busiMcuTencentTemplateConferenceService.getTemplateConferenceDetails(busiMcuTencentTemplateConference);
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
                    data.put(viewTemplateConferenceTemp.getMcuType(), McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
                    break;
                }

                case MCU_DING: {
                    BusiMcuDingTemplateConference busiMcuTencentTemplateConference = new BusiMcuDingTemplateConference();
                    BeanUtils.copyBeanProp(busiMcuTencentTemplateConference, viewTemplateConferenceTemp);
                    ModelBean modelBean = busiMcuDingTemplateConferenceService.getTemplateConferenceDetails(busiMcuTencentTemplateConference);
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
                    data.put(viewTemplateConferenceTemp.getMcuType(), McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
                    break;
                }

                case MCU_HWCLOUD: {
                    BusiMcuHwcloudTemplateConference busiMcuHwcloudTemplateConference = new BusiMcuHwcloudTemplateConference();
                    BeanUtils.copyBeanProp(busiMcuHwcloudTemplateConference, viewTemplateConferenceTemp);
                    ModelBean modelBean = busiMcuHwcloudTemplateConferenceService.getTemplateConferenceDetails(busiMcuHwcloudTemplateConference);
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

                    Map<String, Object> businessProperties = busiMcuHwcloudTemplateConference.getBusinessProperties();
                    if (businessProperties != null) {
                        ModelBean tmb = new ModelBean(busiMcuHwcloudTemplateConference);
                        tmb.put("callInRestriction", businessProperties.get("callInRestriction"));
                        tmb.put("enableWaitingRoom", businessProperties.get("enableWaitingRoom"));
                        tmb.put("allowGuestStartConf", businessProperties.get("allowGuestStartConf"));
                        tmb.put("isSendNotify", businessProperties.get("isSendNotify"));
                        tmb.put("isSendCalendar", businessProperties.get("isSendCalendar"));
                        modelBean.put("templateConference", tmb);
                        modelBean.put("templateParticipants", businessProperties.get("attendees"));
                    }
                    mbs.add(modelBean);
                    data.put(viewTemplateConferenceTemp.getMcuType(), McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
                    break;
                }
                case MCU_ZTE: {
                    BusiMcuZteTemplateConference busiMcuZteTemplateConference = new BusiMcuZteTemplateConference();
                    BeanUtils.copyBeanProp(busiMcuZteTemplateConference, viewTemplateConferenceTemp);
                    ModelBean modelBean = busiMcuZteTemplateConferenceService.getTemplateConferenceDetails(busiMcuZteTemplateConference);
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
                    Map<String, Object> businessProperties = busiMcuZteTemplateConference.getBusinessProperties();
                    if (businessProperties != null) {
                        ModelBean tmb = new ModelBean(busiMcuZteTemplateConference);
                        tmb.put("maxParticipantNum", businessProperties.get("maxParticipantNum"));
                        tmb.put("multiPicControl", businessProperties.get("multiPicControl"));
                        tmb.put("multiViewNumber", businessProperties.get("multiViewNumber"));
                        tmb.put("dynamicRes", businessProperties.get("dynamicRes"));
                        tmb.put("inviteWithSDP", businessProperties.get("inviteWithSDP"));
                        tmb.put("conferenceTemplet", businessProperties.get("conferenceTemplet"));

                        tmb.put("confCascadeMode", businessProperties.get("confCascadeMode"));
                        tmb.put("enableMcuTitle", businessProperties.get("enableMcuTitle"));
                        tmb.put("enableMcuBanner", businessProperties.get("enableMcuBanner"));
                        tmb.put("enableVoiceRecord", businessProperties.get("enableVoiceRecord"));
                        tmb.put("enableAutoVoiceRecord", businessProperties.get("enableAutoVoiceRecord"));
                        tmb.put("enableUpConf", businessProperties.get("enableUpConf"));
                        tmb.put("enableVoiceRecord", businessProperties.get("enableVoiceRecord"));

                        modelBean.put("templateConference", tmb);
                        modelBean.put("templateParticipants", businessProperties.get("attendees"));
                    }

                    mbs.add(modelBean);
                    data.put(viewTemplateConferenceTemp.getMcuType(), McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
                    break;
                }
            }
        }
        for (ModelBean mb : mbs) {
            pd.addRecord(mb);
        }
        pd.setRecords(mbs);
        pd.setData(data);
        return RestResponse.success(0, "查询成功", pd);
    }

    /**
     * 根据部门查询会议模板列表
     */
    @GetMapping("/list/{deptId}/{businessFieldType}")
    @Operation(summary = "根据部门查询会议模板列表")
    public RestResponse list(@PathVariable("deptId") Long deptId, @PathVariable("businessFieldType") Integer businessFieldType) {
        Assert.notNull(deptId, "部门ID不能为空");
        ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
        viewTemplateConferenceCon.setDeptId(deptId);
        viewTemplateConferenceCon.setBusinessFieldType(businessFieldType);
        viewTemplateConferenceCon.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
        List<ViewTemplateConference> list = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCon);
        return RestResponse.success(0, "查询成功", list);
    }

    /**
     * 获取会议模板详细信息
     */
    @GetMapping(value = "/{conferenceId}")
    @Operation(summary = "获取会议模板详细信息")
    public RestResponse getInfo(@PathVariable("conferenceId") String conferenceId) {
        ModelBean modelBean = getTemplateInfo(conferenceId);
        if (modelBean != null) {
            return RestResponse.success(modelBean);
        }
        return RestResponse.fail();
    }

    private ModelBean getTemplateInfo(String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                ModelBean modelBean = busiTemplateConferenceService.selectBusiTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }
                return modelBean;
            }
            case MCU_ZJ: {
                ModelBean modelBean = busiMcuZjTemplateConferenceService.selectBusiMcuZjTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }
                return modelBean;
            }
            case MCU_PLC: {
                ModelBean modelBean = busiMcuPlcTemplateConferenceService.selectBusiMcuPlcTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }
                return modelBean;
            }
            case MCU_KDC: {
                ModelBean modelBean = busiMcuKdcTemplateConferenceService.selectBusiMcuKdcTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }
                return modelBean;
            }
            case SMC3: {
                ModelBean modelBean = busiMcuSmc3TemplateConferenceService.selectBusiTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }

                Object templateConference = modelBean.get("templateConference");
                BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = JSONObject.parseObject(JSONObject.toJSONString(templateConference), BusiMcuSmc3TemplateConference.class);
//                String cascadeNodes = busiMcuSmc3TemplateConference.getCascadeNodes();
//                if (Strings.isNotBlank(cascadeNodes) && !Objects.equals("null", cascadeNodes)) {
//                    modelBean.put("cascadeNodes", JSONArray.parseArray(cascadeNodes, TemplateNode.class));
//                }
                String cascadeNodesTemp = busiMcuSmc3TemplateConference.getCascadeNodesTemp();
                if (Strings.isNotBlank(cascadeNodesTemp) && !Objects.equals("null", cascadeNodesTemp)) {
                    modelBean.put("cascadeNodesTemp", JSONArray.parseArray(cascadeNodesTemp, TemplateNodeTemp.class));
                }
                Map<String, Object> businessProperties = busiMcuSmc3TemplateConference.getBusinessProperties();
                if (businessProperties != null) {
                    ModelBean tmb = new ModelBean(busiMcuSmc3TemplateConference);
                    tmb.put("videoProtocol", businessProperties.get("videoProtocol"));
                    tmb.put("audioProtocol", businessProperties.get("audioProtocol"));
                    tmb.put("mainMcuId", businessProperties.get("mainMcuId"));
                    tmb.put("mainServiceZoneId", businessProperties.get("mainServiceZoneId"));
                    tmb.put("mainServiceZoneName", businessProperties.get("mainServiceZoneName"));
                    tmb.put("videoResolution", businessProperties.get("videoResolution"));
                    tmb.put("mainMcuName", businessProperties.get("mainMcuName"));
                    tmb.put("streamService", businessProperties.get("streamService"));
                    modelBean.put("templateConference", tmb);
                }
                return modelBean;
            }
            case SMC2: {
                ModelBean modelBean = busiMcuSmc2TemplateConferenceService.selectBusiTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }
                return modelBean;
            }
            case MCU_TENCENT: {
                ModelBean modelBean = busiMcuTencentTemplateConferenceService.selectBusiTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }

                Object templateConference = modelBean.get("templateConference");
                BusiMcuHwcloudTemplateConference busiMcuHwcloudTemplateConference = JSONObject.parseObject(JSONObject.toJSONString(templateConference), BusiMcuHwcloudTemplateConference.class);
                Map<String, Object> businessProperties = busiMcuHwcloudTemplateConference.getBusinessProperties();
                if (businessProperties != null) {
                    modelBean.put("templateParticipants", businessProperties.get("attendees"));
                }

                return modelBean;
            }
            case MCU_DING: {
                ModelBean modelBean = busiMcuDingTemplateConferenceService.selectBusiTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }
                return modelBean;
            }
            case MCU_HWCLOUD: {
                ModelBean modelBean = busiMcuHwcloudTemplateConferenceService.selectBusiTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }

                Object templateConference = modelBean.get("templateConference");
                BusiMcuHwcloudTemplateConference busiMcuHwcloudTemplateConference = JSONObject.parseObject(JSONObject.toJSONString(templateConference), BusiMcuHwcloudTemplateConference.class);

                Map<String, Object> businessProperties = busiMcuHwcloudTemplateConference.getBusinessProperties();
                if (businessProperties != null) {
                    ModelBean tmb = new ModelBean(busiMcuHwcloudTemplateConference);
                    tmb.put("callInRestriction", businessProperties.get("callInRestriction"));
                    tmb.put("enableWaitingRoom", businessProperties.get("enableWaitingRoom"));
                    tmb.put("allowGuestStartConf", businessProperties.get("allowGuestStartConf"));
                    tmb.put("isSendNotify", businessProperties.get("isSendNotify"));
                    tmb.put("isSendCalendar", businessProperties.get("isSendCalendar"));
                    if(businessProperties.get("confPresetParam")==null){
                        ConfPresetParamDTO confPresetParamDTO = new ConfPresetParamDTO();
                        tmb.put("confPresetParam",confPresetParamDTO);
                    }else {
                        tmb.put("confPresetParam", businessProperties.get("confPresetParam"));
                    }
                    modelBean.put("templateConference", tmb);
                    modelBean.put("templateParticipants", businessProperties.get("attendees"));
                }
                return modelBean;
            }

            case MCU_ZTE: {
                ModelBean modelBean = busiMcuZteTemplateConferenceService.selectBusiMcuZteTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }
                Object templateConference = modelBean.get("templateConference");
                BusiMcuZteTemplateConference busiMcuZteTemplateConference = JSONObject.parseObject(JSONObject.toJSONString(templateConference), BusiMcuZteTemplateConference.class);
                Map<String, Object> businessProperties = busiMcuZteTemplateConference.getBusinessProperties();
                if (businessProperties != null) {
                    ModelBean tmb = new ModelBean(busiMcuZteTemplateConference);
                    tmb.put("maxParticipantNum", businessProperties.get("maxParticipantNum"));
                    tmb.put("multiPicControl", businessProperties.get("multiPicControl"));
                    tmb.put("conferenceTemplet", businessProperties.get("conferenceTemplet"));
                    tmb.put("multiViewNumber", businessProperties.get("multiViewNumber"));
                    tmb.put("dynamicRes", businessProperties.get("dynamicRes"));
                    tmb.put("inviteWithSDP", businessProperties.get("inviteWithSDP"));
                    tmb.put("confCascadeMode", businessProperties.get("confCascadeMode"));
                    tmb.put("enableMcuTitle", businessProperties.get("enableMcuTitle"));
                    tmb.put("enableMcuBanner", businessProperties.get("enableMcuBanner"));
                    tmb.put("enableVoiceRecord", businessProperties.get("enableVoiceRecord"));
                    tmb.put("enableAutoVoiceRecord", businessProperties.get("enableAutoVoiceRecord"));
                    tmb.put("enableUpConf", businessProperties.get("enableUpConf"));
                    tmb.put("sendMail", businessProperties.get("sendMail"));
                    modelBean.put("templateConference", tmb);
                }

                return modelBean;
            }
        }
        return null;
    }

    /**
     * 获取会议模板详细信息
     */
    @GetMapping(value = "/getCurrentConferenceInfo/{conferenceId}")
    @Operation(summary = "获取会议模板详细信息")
    public RestResponse getCurrentConferenceInfo(HttpServletRequest request, @PathVariable("conferenceId") String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                ConferenceContext conferenceContext = templateConferenceStartService.buildTemplateConferenceContext(id);
//                if ("ops".equalsIgnoreCase(ExternalConfigCache.getInstance().getRegion())) {
                    List<Attendee> attendeesOps =  new ArrayList<>();
                    HashSet<String> attendeeIdSet = new HashSet<>();
                    if (conferenceContext.getAttendeesOps() != null) {
                        for (Attendee attendeeTemp : conferenceContext.getAttendeesOps()) {
                            boolean isMasterAttendee = false;
                            Attendee attendeeExist = conferenceContext.getAttendeeById(attendeeTemp.getId());
                            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeTemp.getId())) {
                                attendeeExist = conferenceContext.getMasterAttendee();
                                isMasterAttendee = true;
                            }
                            if (attendeeExist != null) {
                                if (!attendeeIdSet.contains(attendeeExist.getId())) {
                                    attendeeTemp.setMasterAttendee(isMasterAttendee);
                                    attendeesOps.add(attendeeExist);
                                    attendeeIdSet.add(attendeeExist.getId());
                                }
                            }
                        }
                    }
                    if (conferenceContext.getMasterAttendee() != null) {
                        Attendee attendeeTemp = conferenceContext.getMasterAttendee();
                        if (!attendeeIdSet.contains(attendeeTemp.getId())) {
                            attendeeTemp.setMasterAttendee(true);
                            attendeesOps.add(attendeeTemp);
                            attendeeIdSet.add(attendeeTemp.getId());
                        }
                    }
                    if (conferenceContext.getAttendees() != null) {
                        for (Attendee attendeeTemp : conferenceContext.getAttendees()) {
                            if (!attendeeIdSet.contains(attendeeTemp.getId())) {
                                attendeeTemp.setMasterAttendee(false);
                                attendeesOps.add(attendeeTemp);
                                attendeeIdSet.add(attendeeTemp.getId());
                            }
                        }
                    }
                    conferenceContext.setAttendeesOps(attendeesOps);
//                }
                return RestResponse.success(conferenceContext);
            }
            case MCU_ZJ: {
                McuZjConferenceContext mcuZjConferenceContext = busiMcuZjConferenceService.buildTemplateConferenceContext(id);
                Integer mcuPort = null;
                String fmcRootUrlExternal = ExternalConfigCache.getInstance().getFmcRootUrlExternal();
                if (StringUtils.isNotEmpty(fmcRootUrlExternal)) {
                    try {
                        String referer = request.getHeader("referer");
                        String ip = referer.replace("http://", "").replace("https://", "");
                        if (ip.indexOf(":") > 0) {
                            ip.substring(0, ip.indexOf(":"));
                        }
                        if (ip.indexOf("/") > 0) {
                            ip = ip.substring(0, ip.indexOf("/"));
                        }
                        String externalIp = fmcRootUrlExternal.replace("http://", "").replace("https://", "");
                        if (externalIp.indexOf(":") > 0) {
                            externalIp.substring(0, externalIp.indexOf(":"));
                        }
                        if (externalIp.indexOf("/") > 0) {
                            externalIp = externalIp.substring(0, externalIp.indexOf("/"));
                        }
                        if (externalIp.equals(ip)) {
                            mcuPort = mcuZjConferenceContext.getBusiMcuZj().getPort();
                        }
                    } catch (Exception e) {
                    }
                }
                if (mcuPort != null) {
                    ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                    String jsonStr = null;
                    try {
                        jsonStr = objectMapper.writeValueAsString(mcuZjConferenceContext);
                    } catch (JsonProcessingException e) {
                    }
                    JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                    jsonObject.put("mcuPort", mcuPort);
                    return RestResponse.success(jsonObject);
                } else {
                    return RestResponse.success(mcuZjConferenceContext);
                }
            }
            case MCU_PLC: {
                return RestResponse.success(busiMcuPlcConferenceService.buildTemplateConferenceContext(id));
            }
            case MCU_KDC: {
                return RestResponse.success(busiMcuKdcConferenceService.buildTemplateConferenceContext(id));
            }
            case SMC3: {
                return RestResponse.success(busiSmc3ConferenceService.buildTemplateConferenceContext(id));
            }
            case SMC2: {
                return RestResponse.success(busiSmc2ConferenceService.buildTemplateConferenceContext(id));
            }
            case MCU_TENCENT: {
                return RestResponse.success(busiTencentConferenceService.buildTemplateConferenceContext(id));
            }
            case MCU_DING: {
                return RestResponse.success(busiDingConferenceService.buildTemplateConferenceContext(id));
            }
            case MCU_HWCLOUD: {
                return RestResponse.success(busiHwcloudConferenceService.buildTemplateConferenceContext(id));
            }
            case MCU_ZTE: {
                return RestResponse.success(busiMcuZteConferenceService.buildTemplateConferenceContext(id));
            }
        }
        return RestResponse.fail();
    }

    /**
     * 获取部门会议模板计数
     */
    @GetMapping(value = "/getDeptTemplateCount/{businessFieldType}")
    @Operation(summary = "获取部门会议模板计数")
    public RestResponse getDeptTemplateCount(@PathVariable("businessFieldType") Integer businessFieldType) {
        List<DeptRecordCount> deptTemplateCountList = viewTemplateConferenceMapper.getDeptTemplateCount(businessFieldType);
        Map<Long, Map<String, Long>> deptCountMap = new HashMap<>();
        for (DeptRecordCount deptRecordCountTemp : deptTemplateCountList) {
            Long deptIdTemp = deptRecordCountTemp.getDeptId();
            Long count = deptRecordCountTemp.getCount().longValue();
            {
                Map<String, Long> deptMap = deptCountMap.get(deptIdTemp);
                if (deptMap == null) {
                    deptMap = new HashMap<>();
                    deptMap.put("deptId", deptIdTemp);
                    deptMap.put("count", count);
                    deptMap.put("totalCount", count);
                    deptCountMap.put(deptIdTemp, deptMap);
                } else {
                    Long countExist = deptMap.get("count");
                    Long totalCountExist = deptMap.get("totalCount");
                    deptMap.put("count", countExist + count);
                    deptMap.put("totalCount", totalCountExist + count);
                }
            }
            SysDept sysDept = SysDeptCache.getInstance().get(deptIdTemp);
            if (sysDept != null) {
                String ancestors = sysDept.getAncestors();
                if (StringUtils.isNotEmpty(ancestors)) {
                    String[] deptIdArr = ancestors.split(",");
                    for (String deptIdStr : deptIdArr) {
                        Long deptIdT = null;
                        try {
                            deptIdT = Long.valueOf(deptIdStr);
                        } catch (Exception e) {
                        }
                        if (deptIdT != null) {
                            if (deptIdT.longValue() == deptIdTemp.longValue()) {
                                continue;
                            }
                            Map<String, Long> deptMap = deptCountMap.get(deptIdT);
                            if (deptMap == null) {
                                deptMap = new HashMap<>();
                                deptMap.put("deptId", deptIdT);
                                if (deptIdT.longValue() == deptIdTemp.longValue()) {
                                    deptMap.put("count", count);
                                } else {
                                    deptMap.put("count", 0L);
                                }
                                deptMap.put("totalCount", count);
                                deptCountMap.put(deptIdT, deptMap);
                            } else {
                                Long countExist = deptMap.get("count");
                                Long totalCountExist = deptMap.get("totalCount");
                                if (deptIdT.longValue() == deptIdTemp.longValue()) {
                                    deptMap.put("count", countExist + count);
                                }
                                deptMap.put("totalCount", totalCountExist + count);
                            }
                        }
                    }
                }
            }
        }
        return RestResponse.success(deptCountMap.values());
    }

    /**
     * 获取会议模板的默认视图配置信息
     *
     * @return RestResponse
     * @author lilinhai
     * @since 2021-04-08 15:09
     */
    @PutMapping(value = "/updateDefaultViewConfigInfo/{conferenceId}")
    @Operation(summary = "修改会议模板的默认视图配置信息")
    public RestResponse updateDefaultViewConfigInfo(@RequestBody JSONObject jsonObj, @PathVariable("conferenceId") String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                busiTemplateConferenceService.updateDefaultViewConfigInfo(jsonObj, id);
                return RestResponse.success();
            }
            case MCU_ZJ: {
                BusiMcuZjTemplateConference busiMcuZjTemplateConference = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(id);
                if (busiMcuZjTemplateConference != null) {
                    McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().getAvailableMcuZjBridgesByDept(busiMcuZjTemplateConference.getDeptId()).getMasterMcuZjBridge();
                    if (mcuZjBridge != null) {
                        SourceTemplate sourceTemplate = mcuZjBridge.getSourceTemplateById(busiMcuZjTemplateConference.getResourceTemplateId());
                        if (sourceTemplate.getSingle_view() == 1) {
                            if (jsonObj.containsKey("guestDefaultViewData")) {
                                JSONObject jsonObjectGuest = jsonObj.getJSONObject("guestDefaultViewData");
                                busiMcuZjTemplateConferenceService.updateDefaultViewConfigInfoForGuest(jsonObjectGuest, id);
                            } else if (jsonObj.containsKey("speakerDefaultViewData")) {
                                JSONObject jsonObjectSpeaker = jsonObj.getJSONObject("speakerDefaultViewData");
                                busiMcuZjTemplateConferenceService.updateDefaultViewConfigInfoForGuest(jsonObjectSpeaker, id);
                            }
                        } else {
                            JSONObject jsonObjectSpeaker = jsonObj.getJSONObject("speakerDefaultViewData");
                            busiMcuZjTemplateConferenceService.updateDefaultViewConfigInfo(jsonObjectSpeaker, id);
                            if (jsonObj.containsKey("guestDefaultViewData")) {
                                JSONObject jsonObjectGuest = jsonObj.getJSONObject("guestDefaultViewData");
                                busiMcuZjTemplateConferenceService.updateDefaultViewConfigInfoForGuest(jsonObjectGuest, id);
                            }
                        }
                        return RestResponse.success();
                    }
                }
            }
            case MCU_PLC: {
                BusiMcuPlcTemplateConference busiMcuPlcTemplateConference = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(id);
                if (busiMcuPlcTemplateConference != null) {
                    McuPlcBridge mcuPlcBridge = McuPlcBridgeCache.getInstance().getAvailableMcuPlcBridgesByDept(busiMcuPlcTemplateConference.getDeptId()).getMasterMcuPlcBridge();
                    if (mcuPlcBridge != null) {
                        JSONObject jsonObjectSpeaker = jsonObj.getJSONObject("speakerDefaultViewData");
                        busiMcuPlcTemplateConferenceService.updateDefaultViewConfigInfo(jsonObjectSpeaker, id);
                        return RestResponse.success();
                    }
                }
            }
            case MCU_KDC: {
                BusiMcuKdcTemplateConference busiMcuKdcTemplateConference = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(id);
                if (busiMcuKdcTemplateConference != null) {
                    McuKdcBridge mcuKdcBridge = McuKdcBridgeCache.getInstance().getAvailableMcuKdcBridgesByDept(busiMcuKdcTemplateConference.getDeptId()).getMasterMcuKdcBridge();
                    if (mcuKdcBridge != null) {
                        JSONObject jsonObjectSpeaker = jsonObj.getJSONObject("speakerDefaultViewData");
                        busiMcuKdcTemplateConferenceService.updateDefaultViewConfigInfo(jsonObjectSpeaker, id);
                        return RestResponse.success();
                    }
                }
            }
            case SMC3: {
                busiMcuSmc3TemplateConferenceService.updateDefaultViewConfigInfo(jsonObj, id);

            }
            case SMC2: {
                busiMcuSmc2TemplateConferenceService.updateDefaultViewConfigInfo(jsonObj, id);
            }
            case MCU_HWCLOUD: {
                busiMcuHwcloudTemplateConferenceService.updateDefaultViewConfigInfo(jsonObj, id);
            }
            case MCU_ZTE: {
                busiMcuZteTemplateConferenceService.updateDefaultViewConfigInfo(jsonObj, id);
                return RestResponse.success();
            }
        }
        return RestResponse.fail();
    }

    /**
     * 新增会议模板
     */
    @Log(title = "会议模板", businessType = BusinessType.INSERT)
    @PostMapping("")
    @Operation(summary = "新增会议模板", description = "新增会议模板")
    public RestResponse add(@RequestBody JSONObject jsonObj) {
        JSONObject templateConferenceObj = jsonObj.getJSONObject("templateConference");
        Assert.isTrue(templateConferenceObj != null, "会议模板不能为空！");
        Long masterTerminalId = templateConferenceObj.getLong("masterTerminalId");
        String mcuTypeStr = templateConferenceObj.getString("mcuType");
        Assert.isTrue(StringUtils.isNotEmpty(mcuTypeStr), "MCU类型不能为空！");
        McuType mcuType = McuType.convert(mcuTypeStr);
        switch (mcuType) {
            case FME: {
                BusiTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiTemplateConference.class);
                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("templateParticipants");
                List<BusiTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
                if (busiTemplateParticipantArr != null) {
                    for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                        BusiTemplateParticipant busiTemplateParticipant = busiTemplateParticipantArr.getObject(i, BusiTemplateParticipant.class);
                        Assert.notNull(busiTemplateParticipant.getTerminalId(), "参会者终端ID不能为空");
                        Assert.notNull(busiTemplateParticipant.getWeight(), "参会者weight顺序不能为空");
                        Assert.notNull(busiTemplateParticipant.getAttendType(), "参会者入会/直播类型不能为空");
                        busiTemplateParticipants.add(busiTemplateParticipant);
                    }
                }

                // 部门顺序
                JSONArray templateDeptArr = jsonObj.getJSONArray("templateDepts");
                List<BusiTemplateDept> templateDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++) {
                    BusiTemplateDept busiTemplateDept = templateDeptArr.getObject(i, BusiTemplateDept.class);
                    Assert.notNull(busiTemplateDept.getDeptId(), "部门ID不能为空");
                    Assert.notNull(busiTemplateDept.getWeight(), "部门weight顺序不能为空");
                    templateDepts.add(busiTemplateDept);
                }

                if (busiTemplateConference.getCreateType() == null) {
                    busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                }
                if (busiTemplateConference.getIsAutoCreateStreamUrl() != null && busiTemplateConference.getIsAutoCreateStreamUrl() == 1) {
                    busiTemplateConference.setStreamUrl(null);
                }
                int c = busiTemplateConferenceService.insertBusiTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
                if (c > 0) {
                    String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), mcuType.getCode());
                    // 分屏
                    try {
                        if (jsonObj.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = jsonObj.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    // 级联
                    try {
                        addCascadeTemplateConference(jsonObj, conferenceId);
                    } catch (Exception e) {
                        try {
                            busiTemplateConferenceService.deleteBusiTemplateConferenceById(busiTemplateConference.getId());
                        } catch (Exception e2) {
                            logger.error(e2.getMessage());
                        }
                        return RestResponse.fail(e.getMessage());
                    }
                    return RestResponse.success(busiTemplateConferenceService.selectBusiTemplateConferenceById(busiTemplateConference.getId()));
                } else {
                    return RestResponse.fail();
                }
            }
            case MCU_ZJ: {
                BusiMcuZjTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuZjTemplateConference.class);
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

                if (busiTemplateConference.getCreateType() == null) {
                    busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                }

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
                busiTemplateConference.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
                busiTemplateConference.setDefaultViewIsFill(YesOrNo.YES.getValue());
                busiTemplateConference.setPollingInterval(10);
                busiTemplateConference.setDefaultViewIsDisplaySelf(-1);
                busiTemplateConference.setDefaultViewLayoutGuest(AutomaticSplitScreen.LAYOUT);
                busiTemplateConference.setDefaultViewIsFillGuest(YesOrNo.YES.getValue());
                busiTemplateConference.setPollingIntervalGuest(10);

                int c = busiMcuZjTemplateConferenceService.insertBusiMcuZjTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
                if (c > 0) {
                    String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), mcuType.getCode());
                    // 分屏
                    try {
                        if (jsonObj.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = jsonObj.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    try {
                        addCascadeTemplateConference(jsonObj, conferenceId);
                    } catch (Exception e) {
                        try {
                            busiMcuZjTemplateConferenceService.deleteBusiMcuZjTemplateConferenceById(busiTemplateConference.getId());
                        } catch (Exception e2) {
                            logger.error(e2.getMessage());
                        }
                        return RestResponse.fail(e.getMessage());
                    }
                    return RestResponse.success(busiMcuZjTemplateConferenceService.selectBusiMcuZjTemplateConferenceById(busiTemplateConference.getId()));
                } else {
                    return RestResponse.fail();
                }
            }
            case MCU_PLC: {
                BusiMcuPlcTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuPlcTemplateConference.class);
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

                if (busiTemplateConference.getCreateType() == null) {
                    busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                }



                Integer muteType = busiTemplateConference.getMuteType();
                if (muteType == null || muteType != 0) {
                    muteType = 1;// 0 不静音 1 静音
                }
                busiTemplateConference.setMuteType(muteType);
                // 默认自动分屏
                busiTemplateConference.setDefaultViewLayout(com.paradisecloud.fcm.mcu.plc.model.busi.layout.splitscreen.AutomaticSplitScreen.LAYOUT);
                busiTemplateConference.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
                busiTemplateConference.setDefaultViewIsFill(YesOrNo.YES.getValue());
                busiTemplateConference.setPollingInterval(10);
                busiTemplateConference.setDefaultViewIsDisplaySelf(-1);

                int c = busiMcuPlcTemplateConferenceService.insertBusiMcuPlcTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
                if (c > 0) {
                    String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), mcuType.getCode());
                    // 分屏
                    try {
                        if (jsonObj.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = jsonObj.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    // 级联
                    try {
                        addCascadeTemplateConference(jsonObj, conferenceId);
                    } catch (Exception e) {
                        try {
                            busiMcuPlcTemplateConferenceService.deleteBusiMcuPlcTemplateConferenceById(busiTemplateConference.getId());
                        } catch (Exception e2) {
                            logger.error(e2.getMessage());
                        }
                        return RestResponse.fail(e.getMessage());
                    }
                    return RestResponse.success(busiMcuPlcTemplateConferenceService.selectBusiMcuPlcTemplateConferenceById(busiTemplateConference.getId()));
                } else {
                    return RestResponse.fail();
                }
            }
            case MCU_KDC: {
                BusiMcuKdcTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuKdcTemplateConference.class);
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

                if (busiTemplateConference.getCreateType() == null) {
                    busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                }

//        // 默认24小时
//        busiTemplateConference.setDurationEnabled(1);// 开始会议时长限制
//        busiTemplateConference.setDurationTime(1440);

                Integer muteType = busiTemplateConference.getMuteType();
                if (muteType == null || muteType != 0) {
                    muteType = 1;// 0 不静音 1 静音
                }
                busiTemplateConference.setMuteType(muteType);
                // 默认自动分屏
                busiTemplateConference.setDefaultViewLayout(com.paradisecloud.fcm.mcu.kdc.model.busi.layout.splitscreen.AutomaticSplitScreen.LAYOUT);
                busiTemplateConference.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
                busiTemplateConference.setDefaultViewIsFill(YesOrNo.YES.getValue());
                busiTemplateConference.setPollingInterval(10);
                busiTemplateConference.setDefaultViewIsDisplaySelf(-1);

                int c = busiMcuKdcTemplateConferenceService.insertBusiMcuKdcTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
                if (c > 0) {
                    String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), mcuType.getCode());
                    // 分屏
                    try {
                        if (jsonObj.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = jsonObj.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    // 级联
                    try {
                        addCascadeTemplateConference(jsonObj, conferenceId);
                    } catch (Exception e) {
                        try {
                            busiMcuKdcTemplateConferenceService.deleteBusiMcuKdcTemplateConferenceById(busiTemplateConference.getId());
                        } catch (Exception e2) {
                            logger.error(e2.getMessage());
                        }
                        return RestResponse.fail(e.getMessage());
                    }
                    return RestResponse.success(busiMcuKdcTemplateConferenceService.selectBusiMcuKdcTemplateConferenceById(busiTemplateConference.getId()));
                } else {
                    return RestResponse.fail();
                }
            }
            case SMC3: {
                BusiMcuSmc3TemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuSmc3TemplateConference.class);
                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("templateParticipants");
                List<BusiMcuSmc3TemplateParticipant> busiTemplateParticipants = new ArrayList<>();
                if (busiTemplateParticipantArr != null) {
                    for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                        BusiMcuSmc3TemplateParticipant busiTemplateParticipant = busiTemplateParticipantArr.getObject(i, BusiMcuSmc3TemplateParticipant.class);
                        Assert.notNull(busiTemplateParticipant.getTerminalId(), "参会者终端ID不能为空");
                        Assert.notNull(busiTemplateParticipant.getWeight(), "参会者weight顺序不能为空");
                        Assert.notNull(busiTemplateParticipant.getAttendType(), "参会者入会/直播类型不能为空");
                        busiTemplateParticipants.add(busiTemplateParticipant);
                    }
                }

                // 部门顺序
                JSONArray templateDeptArr = jsonObj.getJSONArray("templateDepts");
                List<BusiMcuSmc3TemplateDept> templateDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++) {
                    BusiMcuSmc3TemplateDept busiTemplateDept = templateDeptArr.getObject(i, BusiMcuSmc3TemplateDept.class);
                    Assert.notNull(busiTemplateDept.getDeptId(), "部门ID不能为空");
                    Assert.notNull(busiTemplateDept.getWeight(), "部门weight顺序不能为空");
                    templateDepts.add(busiTemplateDept);
                }

                if (busiTemplateConference.getCreateType() == null) {
                    busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                }
                if (busiTemplateConference.getIsAutoCreateStreamUrl() != null && busiTemplateConference.getIsAutoCreateStreamUrl() == 1) {
                    busiTemplateConference.setStreamUrl(null);
                }
                Object confPresetParam = templateConferenceObj.get("confPresetParam");
                if (confPresetParam != null) {
                    busiTemplateConference.setConfPresetParam(JSONObject.parseObject(JSONObject.toJSONString(confPresetParam)));
                }

                Map<String, Object> businessProperties = busiTemplateConference.getBusinessProperties();
                if (businessProperties == null) {
                    businessProperties = new HashMap<>();
                }
                businessProperties.put("mainMcuId", templateConferenceObj.get("mainMcuId"));
                businessProperties.put("mainMcuName", templateConferenceObj.get("mainMcuName"));
                businessProperties.put("mainServiceZoneId", templateConferenceObj.get("mainServiceZoneId"));
                businessProperties.put("mainServiceZoneName", templateConferenceObj.get("mainServiceZoneName"));

                businessProperties.put("videoProtocol", templateConferenceObj.get("videoProtocol"));
                businessProperties.put("videoResolution", templateConferenceObj.get("videoResolution"));
                businessProperties.put("audioProtocol", templateConferenceObj.get("audioProtocol"));
                businessProperties.put("streamService", templateConferenceObj.get("streamService"));
                busiTemplateConference.setBusinessProperties(businessProperties);

//                JSONArray templateNodesJson = jsonObj.getJSONArray("cascadeNodes");
//                if (templateNodesJson != null) {
//                    busiTemplateConference.setCategory("CASCADE");
//                    busiTemplateConference.setIsAutoCreateConferenceNumber(1);
//                    busiTemplateConference.setBusinessFieldType(100);
//                    busiTemplateConference.setCreateType(2);
//                }
//                busiTemplateConference.setCascadeNodes(JSONArray.toJSONString(templateNodesJson));
                JSONArray templateNodesTempJson = jsonObj.getJSONArray("cascadeNodesTemp");
                if (templateNodesTempJson != null) {
                    busiTemplateConference.setCategory("CASCADE");
                    busiTemplateConference.setIsAutoCreateConferenceNumber(1);
                    busiTemplateConference.setBusinessFieldType(100);
                    busiTemplateConference.setCreateType(2);
                }
                busiTemplateConference.setCascadeNodesTemp(JSONArray.toJSONString(templateNodesTempJson));
                int c = busiMcuSmc3TemplateConferenceService.insertBusiTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
                if (c > 0) {
                    String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), mcuType.getCode());
                    // 分屏
                    try {
                        if (jsonObj.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = jsonObj.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    // 级联
                    try {
                        addCascadeTemplateConference(jsonObj, conferenceId);
                    } catch (Exception e) {
                        try {
                            busiMcuSmc3TemplateConferenceService.deleteBusiTemplateConferenceById(busiTemplateConference.getId());
                        } catch (Exception e2) {
                            logger.error(e2.getMessage());
                        }
                        return RestResponse.fail(e.getMessage());
                    }
                    return RestResponse.success(busiMcuSmc3TemplateConferenceService.selectBusiTemplateConferenceById(busiTemplateConference.getId()));
                } else {
                    return RestResponse.fail();
                }
            }
            case SMC2: {
                BusiMcuSmc2TemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuSmc2TemplateConference.class);
                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("templateParticipants");
                List<BusiMcuSmc2TemplateParticipant> busiTemplateParticipants = new ArrayList<>();
                if (busiTemplateParticipantArr != null) {
                    for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                        BusiMcuSmc2TemplateParticipant busiTemplateParticipant = busiTemplateParticipantArr.getObject(i, BusiMcuSmc2TemplateParticipant.class);
                        Assert.notNull(busiTemplateParticipant.getTerminalId(), "参会者终端ID不能为空");
                        Assert.notNull(busiTemplateParticipant.getWeight(), "参会者weight顺序不能为空");
                        Assert.notNull(busiTemplateParticipant.getAttendType(), "参会者入会/直播类型不能为空");
                        busiTemplateParticipants.add(busiTemplateParticipant);
                    }
                }

                // 部门顺序
                JSONArray templateDeptArr = jsonObj.getJSONArray("templateDepts");
                List<BusiMcuSmc2TemplateDept> templateDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++) {
                    BusiMcuSmc2TemplateDept busiTemplateDept = templateDeptArr.getObject(i, BusiMcuSmc2TemplateDept.class);
                    Assert.notNull(busiTemplateDept.getDeptId(), "部门ID不能为空");
                    Assert.notNull(busiTemplateDept.getWeight(), "部门weight顺序不能为空");
                    templateDepts.add(busiTemplateDept);
                }

                if (busiTemplateConference.getCreateType() == null) {
                    busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                }
                if (busiTemplateConference.getIsAutoCreateStreamUrl() != null && busiTemplateConference.getIsAutoCreateStreamUrl() == 1) {
                    busiTemplateConference.setStreamUrl(null);
                }
                if (templateConferenceObj.get("password") != null) {
                    busiTemplateConference.setGuestPassword((String) templateConferenceObj.get("password"));
                }

                int c = busiMcuSmc2TemplateConferenceService.insertBusiTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
                if (c > 0) {
                    String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), mcuType.getCode());
                    // 分屏
                    try {
                        if (jsonObj.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = jsonObj.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    // 级联
                    try {
                        addCascadeTemplateConference(jsonObj, conferenceId);
                    } catch (Exception e) {
                        try {
                            busiMcuSmc2TemplateConferenceService.deleteBusiTemplateConferenceById(busiTemplateConference.getId());
                        } catch (Exception e2) {
                            logger.error(e2.getMessage());
                        }
                        return RestResponse.fail(e.getMessage());
                    }
                    return RestResponse.success(busiMcuSmc2TemplateConferenceService.selectBusiTemplateConferenceById(busiTemplateConference.getId()));
                } else {
                    return RestResponse.fail();
                }
            }
            case MCU_TENCENT: {
                BusiMcuTencentTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuTencentTemplateConference.class);

                if (busiTemplateConference.getCreateType() == null) {
                    busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                }
                if (busiTemplateConference.getIsAutoCreateStreamUrl() != null && busiTemplateConference.getIsAutoCreateStreamUrl() == 1) {
                    busiTemplateConference.setStreamUrl(null);
                }
                busiTemplateConference.setIsAutoCreateConferenceNumber(ConferenceNumberCreateType.AUTO.getValue());
                Object password = templateConferenceObj.get("password");
                if (password != null) {
                    String pswStr=(String)password;
                    if(Strings.isNotBlank(pswStr)){
                        Pattern pattern = Pattern.compile(regex);
                        // 创建匹配器
                        Matcher matcher = pattern.matcher((String)password);
                        if (!matcher.matches()) {
                            throw new CustomException("密码格式不正确,是4到6位的数字");
                        }
                        busiTemplateConference.setGuestPassword((String) password);
                        busiTemplateConference.setConferencePassword((String) password);
                    }

                }
                Object chairmanPassword = templateConferenceObj.get("chairmanPassword");
                if (chairmanPassword != null) {
                    String chairmanPasswordStr=(String)chairmanPassword;
                    if(Strings.isNotBlank(chairmanPasswordStr)){
                        Pattern pattern = Pattern.compile(regex_chair);
                        // 创建匹配器
                        Matcher matcher = pattern.matcher((String)chairmanPassword);
                        if (!matcher.matches()) {
                            throw new CustomException("密码格式不正确,是6位的数字");
                        }
                        busiTemplateConference.setChairmanPassword(chairmanPasswordStr);
                    }
                }

                Object duration = templateConferenceObj.get("duration");
                if (duration != null) {
                    busiTemplateConference.setDurationTime((Integer) duration);
                }
                Object supportRecord = templateConferenceObj.get("supportRecord");

                Map<String, Object> businessProperties = busiTemplateConference.getBusinessProperties();
                if (businessProperties == null) {
                    businessProperties = new HashMap<>();
                }
                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("templateParticipants");
                if (busiTemplateParticipantArr != null) {
                    businessProperties.put("attendees",busiTemplateParticipantArr);
                }
                busiTemplateConference.setBusinessProperties(businessProperties);

                if (supportRecord != null) {
                    busiTemplateConference.setRecordingEnabled((Integer) supportRecord);
                }
                int c = busiMcuTencentTemplateConferenceService.insertBusiTemplateConference(busiTemplateConference, masterTerminalId, null, null);
                if (c > 0) {
                    // 级联
                    try {
                        String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), mcuType.getCode());
                        editCascadeTemplateConference(jsonObj, conferenceId);
                    } catch (Exception e) {
                        return RestResponse.fail(e.getMessage());
                    }
                    //云会议云端数据
                    Object opsSnObj = busiTemplateConference.getParams().get("opsSn");
                    if (opsSnObj != null) {
                        String opsSn = (String) opsSnObj;
                        BusiOps busiOps = OpsCache.getInstance().getBySn(opsSn);
                        if (busiOps != null) {
                            BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointment = new BusiMcuTencentConferenceAppointment();
                            busiMcuTencentConferenceAppointment.setOpsId(busiOps.getId());
                            busiMcuTencentConferenceAppointment.setCreateTime(new Date());
                            busiMcuTencentConferenceAppointment.setDeptId(DeptConstant.OPS_DEPT_ID);
                            busiMcuTencentConferenceAppointment.setTemplateId(busiTemplateConference.getId());
                            busiMcuTencentConferenceAppointment.setIsAutoCreateTemplate(1);
                            busiMcuTencentConferenceAppointment.setStartTime("9999-01-01 00:00:00");
                            busiMcuTencentConferenceAppointment.setEndTime("9999-01-01 23:59:59");
                            busiMcuTencentConferenceAppointment.setExtendMinutes(0);
                            busiMcuTencentConferenceAppointment.setIsHangUp(2);
                            busiMcuTencentConferenceAppointment.setStatus(AppointmentConferenceStatus.DISABLED.getValue());
                            busiMcuTencentConferenceAppointment.setIsStart(2);
                            busiMcuTencentConferenceAppointment.setRepeatRate(1);
                            busiMcuTencentConferenceAppointment.setType(2);
                            busiMcuTencentConferenceAppointmentMapper.insertBusiMcuTencentConferenceAppointment(busiMcuTencentConferenceAppointment);

                        }
                    }
                    return RestResponse.success(busiMcuTencentTemplateConferenceService.selectBusiTemplateConferenceById(busiTemplateConference.getId()));
                } else {
                    return RestResponse.fail();
                }
            }
            case MCU_DING: {
                BusiMcuDingTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuDingTemplateConference.class);

                if (busiTemplateConference.getCreateType() == null) {
                    busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                }
                if (busiTemplateConference.getIsAutoCreateStreamUrl() != null && busiTemplateConference.getIsAutoCreateStreamUrl() == 1) {
                    busiTemplateConference.setStreamUrl(null);
                }
                busiTemplateConference.setIsAutoCreateConferenceNumber(ConferenceNumberCreateType.AUTO.getValue());
                Object password = templateConferenceObj.get("password");
                if (password != null) {
                    busiTemplateConference.setConferencePassword((String) password);
                }
                Object duration = templateConferenceObj.get("duration");
                if (duration != null) {
                    busiTemplateConference.setDurationTime((Integer) duration);
                }
                Object supportRecord = templateConferenceObj.get("supportRecord");

                if (supportRecord != null) {
                    busiTemplateConference.setRecordingEnabled((Integer) supportRecord);
                }
                int c = busiMcuDingTemplateConferenceService.insertBusiTemplateConference(busiTemplateConference, masterTerminalId, null, null);
                if (c > 0) {
                    // 级联
                    try {
                        String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), mcuType.getCode());
                        editCascadeTemplateConference(jsonObj, conferenceId);
                    } catch (Exception e) {
                        return RestResponse.fail();
                    }
                    return RestResponse.success(busiMcuDingTemplateConferenceService.selectBusiTemplateConferenceById(busiTemplateConference.getId()));
                } else {
                    return RestResponse.fail();
                }
            }
            case MCU_HWCLOUD: {
                BusiMcuHwcloudTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuHwcloudTemplateConference.class);

                if (busiTemplateConference.getCreateType() == null) {
                    busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                }
                if (busiTemplateConference.getIsAutoCreateStreamUrl() != null && busiTemplateConference.getIsAutoCreateStreamUrl() == 1) {
                    busiTemplateConference.setStreamUrl(null);
                }
                busiTemplateConference.setIsAutoCreateConferenceNumber(ConferenceNumberCreateType.AUTO.getValue());

                Object duration = templateConferenceObj.get("duration");
                if (duration != null) {
                    busiTemplateConference.setDurationTime((Integer) duration);
                }
                Object supportRecord = templateConferenceObj.get("supportRecord");

                if (supportRecord != null) {
                    busiTemplateConference.setRecordingEnabled((Integer) supportRecord);
                }
                Map<String, Object> businessProperties = busiTemplateConference.getBusinessProperties();
                if (businessProperties == null) {
                    businessProperties = new HashMap<>();
                }
                Object callInRestriction = templateConferenceObj.get("callInRestriction");
                if(callInRestriction!=null){
                    businessProperties.put("callInRestriction",callInRestriction);
                }
                Object allowGuestStartConf = templateConferenceObj.get("allowGuestStartConf");
                if(callInRestriction!=null){
                    businessProperties.put("allowGuestStartConf",allowGuestStartConf);
                }


                Object enableWaitingRoom = templateConferenceObj.get("enableWaitingRoom");
                if(callInRestriction!=null){
                    businessProperties.put("enableWaitingRoom",enableWaitingRoom);
                }


                Object isSendNotify = templateConferenceObj.get("isSendNotify");
                if(callInRestriction!=null){
                    businessProperties.put("isSendNotify",isSendNotify);
                }


                Object isSendCalendar = templateConferenceObj.get("isSendCalendar");
                if(callInRestriction!=null){
                    businessProperties.put("isSendCalendar",isSendCalendar);
                }

                Object confPresetParam = templateConferenceObj.get("confPresetParam");
                if(confPresetParam!=null){
                    businessProperties.put("confPresetParam",confPresetParam);
                }


                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("templateParticipants");
                if (busiTemplateParticipantArr != null) {
                    businessProperties.put("attendees",busiTemplateParticipantArr);
                }
                busiTemplateConference.setBusinessProperties(businessProperties);

                int c = busiMcuHwcloudTemplateConferenceService.insertBusiTemplateConference(busiTemplateConference, masterTerminalId, null, null);
                if (c > 0) {
                    // 级联
                    try {
                        String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), mcuType.getCode());
                        editCascadeTemplateConference(jsonObj, conferenceId);
                    } catch (Exception e) {
                        return RestResponse.fail();
                    }
                    //云会议云端数据
                    Object opsSnObj = busiTemplateConference.getParams().get("opsSn");
                    if (opsSnObj != null) {
                        String opsSn = (String) opsSnObj;
                        BusiOps busiOps = OpsCache.getInstance().getBySn(opsSn);
                        if (busiOps != null) {
                            BusiMcuHwcloudConferenceAppointment busiMcuHwcloudConferenceAppointment = new BusiMcuHwcloudConferenceAppointment();
                            busiMcuHwcloudConferenceAppointment.setOpsId(busiOps.getId());
                            busiMcuHwcloudConferenceAppointment.setCreateTime(new Date());
                            busiMcuHwcloudConferenceAppointment.setDeptId(DeptConstant.OPS_DEPT_ID);
                            busiMcuHwcloudConferenceAppointment.setTemplateId(busiTemplateConference.getId());
                            busiMcuHwcloudConferenceAppointment.setIsAutoCreateTemplate(1);
                            busiMcuHwcloudConferenceAppointment.setStartTime("9999-01-01 00:00:00");
                            busiMcuHwcloudConferenceAppointment.setEndTime("9999-01-01 23:59:59");
                            busiMcuHwcloudConferenceAppointment.setExtendMinutes(0);
                            busiMcuHwcloudConferenceAppointment.setIsHangUp(2);
                            busiMcuHwcloudConferenceAppointment.setStatus(AppointmentConferenceStatus.DISABLED.getValue());
                            busiMcuHwcloudConferenceAppointment.setIsStart(2);
                            busiMcuHwcloudConferenceAppointment.setRepeatRate(1);
                            busiMcuHwcloudConferenceAppointment.setType(2);
                            busiMcuHwcloudConferenceAppointmentMapper.insertBusiMcuHwcloudConferenceAppointment(busiMcuHwcloudConferenceAppointment);

                        }
                    }
                    return RestResponse.success(busiMcuHwcloudTemplateConferenceService.selectBusiTemplateConferenceById(busiTemplateConference.getId()));
                } else {
                    return RestResponse.fail();
                }
            }
            case MCU_ZTE: {
                BusiMcuZteTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuZteTemplateConference.class);
                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("templateParticipants");
                List<BusiMcuZteTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
                if (busiTemplateParticipantArr != null) {
                    for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                        BusiMcuZteTemplateParticipant busiTemplateParticipant = busiTemplateParticipantArr.getObject(i, BusiMcuZteTemplateParticipant.class);
                        Assert.notNull(busiTemplateParticipant.getTerminalId(), "参会者终端ID不能为空");
                        Assert.notNull(busiTemplateParticipant.getWeight(), "参会者weight顺序不能为空");
                        Assert.notNull(busiTemplateParticipant.getAttendType(), "参会者入会/直播类型不能为空");
                        busiTemplateParticipants.add(busiTemplateParticipant);
                    }
                }

                // 部门顺序
                JSONArray templateDeptArr = jsonObj.getJSONArray("templateDepts");
                List<BusiMcuZteTemplateDept> templateDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++) {
                    BusiMcuZteTemplateDept busiTemplateDept = templateDeptArr.getObject(i, BusiMcuZteTemplateDept.class);
                    Assert.notNull(busiTemplateDept.getDeptId(), "部门ID不能为空");
                    Assert.notNull(busiTemplateDept.getWeight(), "部门weight顺序不能为空");
                    templateDepts.add(busiTemplateDept);
                }

                if (busiTemplateConference.getCreateType() == null) {
                    busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                }


                Integer muteType = busiTemplateConference.getMuteType();
                if (muteType == null || muteType != 0) {
                    muteType = 1;// 0 不静音 1 静音
                }
                busiTemplateConference.setMuteType(muteType);
                // 默认自动分屏
                busiTemplateConference.setDefaultViewLayout(com.paradisecloud.fcm.zte.model.busi.layout.splitscreen.AutomaticSplitScreen.LAYOUT);
                busiTemplateConference.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
                busiTemplateConference.setDefaultViewIsFill(YesOrNo.YES.getValue());
                busiTemplateConference.setPollingInterval(10);
                busiTemplateConference.setDefaultViewIsDisplaySelf(1);

                Map<String, Object> businessProperties = busiTemplateConference.getBusinessProperties();
                if (businessProperties == null) {
                    businessProperties = new HashMap<>();
                }
                businessProperties.put("maxParticipantNum", templateConferenceObj.get("maxParticipantNum"));
                businessProperties.put("multiPicControl", templateConferenceObj.get("multiPicControl"));
                businessProperties.put("multiViewNumber", templateConferenceObj.get("multiViewNumber"));
                businessProperties.put("dynamicRes", templateConferenceObj.get("dynamicRes"));
                businessProperties.put("inviteWithSDP", templateConferenceObj.get("inviteWithSDP"));
                businessProperties.put("conferenceTemplet", templateConferenceObj.get("conferenceTemplet"));

                businessProperties.put("confCascadeMode", templateConferenceObj.get("confCascadeMode"));
                businessProperties.put("enableMcuTitle", templateConferenceObj.get("enableMcuTitle"));
                businessProperties.put("enableMcuBanner", templateConferenceObj.get("enableMcuBanner"));
                businessProperties.put("enableVoiceRecord", templateConferenceObj.get("enableVoiceRecord"));

                businessProperties.put("enableAutoVoiceRecord", templateConferenceObj.get("enableAutoVoiceRecord"));
                businessProperties.put("enableUpConf", templateConferenceObj.get("enableUpConf"));
                businessProperties.put("sendMail", templateConferenceObj.get("sendMail"));
                busiTemplateConference.setBusinessProperties(businessProperties);

                int c = busiMcuZteTemplateConferenceService.insertBusiMcuZteTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
                if (c > 0) {
                    String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), mcuType.getCode());
                    // 分屏
                    try {
                        if (jsonObj.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = jsonObj.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    // 级联
                    try {
                        addCascadeTemplateConference(jsonObj, conferenceId);
                    } catch (Exception e) {
                        try {
                            busiMcuZteTemplateConferenceService.deleteBusiMcuZteTemplateConferenceById(busiTemplateConference.getId());
                        } catch (Exception e2) {
                            logger.error(e2.getMessage());
                        }
                        return RestResponse.fail(e.getMessage());
                    }
                    return RestResponse.success(busiMcuZteTemplateConferenceService.selectBusiMcuZteTemplateConferenceById(busiTemplateConference.getId()));
                } else {
                    return RestResponse.fail();
                }
            }
        }
        return RestResponse.fail();
    }

    private void deleteCascadeTemplateConference(ViewTemplateConference viewTemplateConference) {
        Long templateId = viewTemplateConference.getId();
        String mcuTypeStr = viewTemplateConference.getMcuType();
        McuType mcuType = McuType.convert(mcuTypeStr);
        switch (mcuType) {
            case FME: {
                busiTemplateConferenceMapper.deleteBusiTemplateConferenceById(templateId);
            }
            case MCU_ZJ: {
                busiMcuZjTemplateConferenceMapper.deleteBusiMcuZjTemplateConferenceById(templateId);
            }
            case MCU_PLC: {
                busiMcuPlcTemplateConferenceMapper.deleteBusiMcuPlcTemplateConferenceById(templateId);
            }
            case MCU_KDC: {
                busiMcuKdcTemplateConferenceMapper.deleteBusiMcuKdcTemplateConferenceById(templateId);
            }
            case SMC3: {
                busiMcuSmc3TemplateConferenceService.deleteBusiTemplateConferenceById(templateId);
            }
            case SMC2: {
                busiMcuSmc2TemplateConferenceService.deleteBusiTemplateConferenceById(templateId);
            }
            case MCU_TENCENT: {
                busiMcuTencentTemplateConferenceService.deleteBusiTemplateConferenceById(templateId);
            }
            case MCU_DING: {
                busiMcuDingTemplateConferenceService.deleteBusiTemplateConferenceById(templateId);
            }

            case MCU_HWCLOUD: {
                busiMcuHwcloudTemplateConferenceService.deleteBusiTemplateConferenceById(templateId);
            }
            case MCU_ZTE: {
                busiMcuZteTemplateConferenceMapper.deleteBusiMcuZteTemplateConferenceById(templateId);
            }
        }
    }

    private void updateCascadeTemplateConference(ViewTemplateConference viewTemplateConference, String upCascadeConferenceId, int upCascadeIndex) {
        updateCascadeTemplateConference(viewTemplateConference, upCascadeConferenceId, UpCascadeType.SELECT_TEMPLATE_OUT_MEETING, upCascadeIndex);
    }

    private void updateCascadeTemplateConference(ViewTemplateConference viewTemplateConference, String upCascadeConferenceId, UpCascadeType upCascadeType, int upCascadeIndex) {
        Long templateId = viewTemplateConference.getId();
        String mcuTypeStr = viewTemplateConference.getMcuType();
        McuType mcuType = McuType.convert(mcuTypeStr);
        switch (mcuType) {
            case FME: {
                BusiTemplateConference busiTemplateConferenceUpdate = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiTemplateConferenceMapper.updateBusiTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_ZJ: {
                BusiMcuZjTemplateConference busiTemplateConferenceUpdate = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_PLC: {
                BusiMcuPlcTemplateConference busiTemplateConferenceUpdate = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiMcuPlcTemplateConferenceMapper.updateBusiMcuPlcTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_KDC: {
                BusiMcuKdcTemplateConference busiTemplateConferenceUpdate = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiMcuKdcTemplateConferenceMapper.updateBusiMcuKdcTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case SMC3: {
                BusiMcuSmc3TemplateConference busiTemplateConferenceUpdate = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case SMC2: {
                BusiMcuSmc2TemplateConference busiTemplateConferenceUpdate = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiMcuSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_TENCENT: {
                BusiMcuTencentTemplateConference busiTemplateConferenceUpdate = busiMcuTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiMcuTencentTemplateConferenceMapper.updateBusiMcuTencentTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_DING: {
                BusiMcuDingTemplateConference busiTemplateConferenceUpdate = busiMcuDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiMcuDingTemplateConferenceMapper.updateBusiMcuDingTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_HWCLOUD: {
                BusiMcuHwcloudTemplateConference busiTemplateConferenceUpdate = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiMcuHwcloudTemplateConferenceMapper.updateBusiMcuHwcloudTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }

            case MCU_ZTE: {
                BusiMcuZteTemplateConference busiTemplateConferenceUpdate = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiMcuZteTemplateConferenceMapper.updateBusiMcuZteTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }

        }
    }

    private void recoverUpdateCascadeTemplateConference(ViewTemplateConference viewTemplateConference) {
        Long templateId = viewTemplateConference.getId();
        String mcuTypeStr = viewTemplateConference.getMcuType();
        McuType mcuType = McuType.convert(mcuTypeStr);
        switch (mcuType) {
            case FME: {
                BusiTemplateConference busiTemplateConferenceUpdate = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiTemplateConferenceMapper.updateBusiTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_ZJ: {
                BusiMcuZjTemplateConference busiTemplateConferenceUpdate = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_PLC: {
                BusiMcuPlcTemplateConference busiTemplateConferenceUpdate = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuPlcTemplateConferenceMapper.updateBusiMcuPlcTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_KDC: {
                BusiMcuKdcTemplateConference busiTemplateConferenceUpdate = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuKdcTemplateConferenceMapper.updateBusiMcuKdcTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case SMC3: {
                BusiMcuSmc3TemplateConference busiTemplateConferenceUpdate = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case SMC2: {
                BusiMcuSmc2TemplateConference busiTemplateConferenceUpdate = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_TENCENT: {
                BusiMcuTencentTemplateConference busiTemplateConferenceUpdate = busiMcuTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuTencentTemplateConferenceMapper.updateBusiMcuTencentTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_DING: {
                BusiMcuDingTemplateConference busiTemplateConferenceUpdate = busiMcuDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuDingTemplateConferenceMapper.updateBusiMcuDingTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_HWCLOUD: {
                BusiMcuHwcloudTemplateConference busiTemplateConferenceUpdate = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuHwcloudTemplateConferenceMapper.updateBusiMcuHwcloudTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_ZTE: {
                BusiMcuZteTemplateConference busiTemplateConferenceUpdate = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuZteTemplateConferenceMapper.updateBusiMcuZteTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
        }
        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(viewTemplateConference.getConferenceId()));
        if (conferenceContext != null) {
            conferenceContext.setUpCascadeConferenceId(null);
            conferenceContext.setUpCascadeIndex(null);
            conferenceContext.setUpCascadeRemoteParty(null);
        }
    }

    private void addCascadeTemplateConference(JSONObject jsonObj, String upCascadeConferenceId) throws Exception {
        // 级联
        List<ViewTemplateConference> templateIdListAdd = new ArrayList();
        List<ViewTemplateConference> templateIdListUpdate = new ArrayList();
        try {
            if (jsonObj.containsKey("cascadeTemplateConferences")) {
                JSONArray jsonArrayCascadeTemplateConferences = jsonObj.getJSONArray("cascadeTemplateConferences");
                if (jsonArrayCascadeTemplateConferences != null && jsonArrayCascadeTemplateConferences.size() > 0) {
                    int index = 0;
                    for (Object jsonObjectTemplateConferenceObj : jsonArrayCascadeTemplateConferences) {
                        index = index + 1;
                        if (jsonObjectTemplateConferenceObj instanceof Map) {
                            Map jsonObjectTemplateConference = (Map) jsonObjectTemplateConferenceObj;
                            if (jsonObjectTemplateConference.containsKey("id")) {
                                Long templateId = Long.valueOf(jsonObjectTemplateConference.get("id").toString());
                                String mcuTypeStrCascade = String.valueOf(jsonObjectTemplateConference.get("mcuType"));
                                ViewTemplateConference busiTemplateConferenceCascade = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuTypeStrCascade, templateId);
                                if (busiTemplateConferenceCascade != null) {
                                    if (StringUtils.isNotEmpty(busiTemplateConferenceCascade.getUpCascadeConferenceId()) && !upCascadeConferenceId.equals(busiTemplateConferenceCascade.getUpCascadeConferenceId())) {
                                        throw new Exception("会议模板:" + busiTemplateConferenceCascade.getName() + ":已被其它会议级联！");
                                    }
                                    updateCascadeTemplateConference(busiTemplateConferenceCascade, upCascadeConferenceId, index);
                                    templateIdListUpdate.add(busiTemplateConferenceCascade);
                                } else {
                                    throw new Exception("找不到会议模板:templateId:" + templateId);
                                }
                            } else {
                                jsonObjectTemplateConference.put("upCascadeConferenceId", upCascadeConferenceId);
                                String mcuTypeStrCascade = jsonObjectTemplateConference.get("mcuType").toString();
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("templateConference", jsonObjectTemplateConference);
                                RestResponse restResponse = add(jsonObject);
                                ModelBean modelBean = (ModelBean) restResponse.getData();
                                ModelBean templateConferenceModelBean = (ModelBean) modelBean.get("templateConference");
                                Long templateId = (Long) templateConferenceModelBean.get("id");
                                ViewTemplateConference busiTemplateConferenceCascade = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuTypeStrCascade, templateId);
                                updateCascadeTemplateConference(busiTemplateConferenceCascade, upCascadeConferenceId, UpCascadeType.AUTO_CREATE, index);
                                templateIdListAdd.add(busiTemplateConferenceCascade);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            for (ViewTemplateConference viewTemplateConference : templateIdListAdd) {
                try {
                    deleteCascadeTemplateConference(viewTemplateConference);
                } catch (Exception e1) {
                    logger.error(e1.getMessage());
                }
            }
            for (ViewTemplateConference viewTemplateConference : templateIdListUpdate) {
                try {
                    recoverUpdateCascadeTemplateConference(viewTemplateConference);
                } catch (Exception e1) {
                    logger.error(e1.getMessage());
                }
            }
            throw e;
        }
    }

    private void editCascadeTemplateConference(JSONObject jsonObj, String upCascadeConferenceId) throws Exception {
        // 级联
        List<ViewTemplateConference> templateIdListAdd = new ArrayList();
        List<ViewTemplateConference> templateIdListUpdate = new ArrayList();
        Set<String> templateIdListCascade = new HashSet<>();
        try {
            if (jsonObj.containsKey("cascadeTemplateConferences")) {
                JSONArray jsonArrayCascadeTemplateConferences = jsonObj.getJSONArray("cascadeTemplateConferences");
                if (jsonArrayCascadeTemplateConferences != null && jsonArrayCascadeTemplateConferences.size() > 0) {
                    int index = 0;
                    for (Object jsonObjectTemplateConferenceObj : jsonArrayCascadeTemplateConferences) {
                        index = index + 1;
                        if (jsonObjectTemplateConferenceObj instanceof Map) {
                            Map jsonObjectTemplateConference = (Map) jsonObjectTemplateConferenceObj;
                            if (jsonObjectTemplateConference.containsKey("id")) {
                                Long templateId = Long.valueOf(jsonObjectTemplateConference.get("id").toString());
                                String mcuTypeStrCascade = String.valueOf(jsonObjectTemplateConference.get("mcuType"));
                                if (Objects.equals(mcuTypeStrCascade, McuType.MCU_TENCENT.getCode())) {
                                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                                    if (Objects.equals(conferenceIdVo.getMcuType().getCode(), mcuTypeStrCascade)) {
                                        throw new Exception(McuType.MCU_TENCENT.getName() + "不支持级联腾讯会议");
                                    }
                                }
                                ViewTemplateConference busiTemplateConferenceCascade = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuTypeStrCascade, templateId);
                                if (busiTemplateConferenceCascade != null) {
                                    if (StringUtils.isNotEmpty(busiTemplateConferenceCascade.getUpCascadeConferenceId()) && !upCascadeConferenceId.equals(busiTemplateConferenceCascade.getUpCascadeConferenceId())) {
                                        throw new Exception("会议模板:" + busiTemplateConferenceCascade.getName() + ":已被其它会议级联！");
                                    }
                                    if (busiTemplateConferenceCascade.getUpCascadeId() != null) {
                                        if (busiTemplateConferenceCascade.getCreateType() == ConferenceTemplateCreateType.AUTO.getValue() && busiTemplateConferenceCascade.getUpCascadeType() == UpCascadeType.AUTO_CREATE.getCode()) {
                                            String conferenceIdTemp = EncryptIdUtil.generateConferenceId(templateId, mcuTypeStrCascade);
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("templateConference", jsonObjectTemplateConference);
                                            edit(jsonObject, conferenceIdTemp);
                                            templateIdListUpdate.add(busiTemplateConferenceCascade);
                                            busiTemplateConferenceCascade = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuTypeStrCascade, templateId);
                                        }
                                    }
                                    updateCascadeTemplateConference(busiTemplateConferenceCascade, upCascadeConferenceId, index);
                                    templateIdListUpdate.add(busiTemplateConferenceCascade);
                                    templateIdListCascade.add(busiTemplateConferenceCascade.getConferenceId());
                                } else {
                                    throw new Exception("找不到会议模板:templateId:" + templateId);
                                }
                            } else {
                                jsonObjectTemplateConference.put("upCascadeConferenceId", upCascadeConferenceId);
                                String mcuTypeStrCascade = String.valueOf(jsonObjectTemplateConference.get("mcuType"));
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("templateConference", jsonObjectTemplateConference);
                                RestResponse restResponse = add(jsonObject);
                                ModelBean modelBean = (ModelBean) restResponse.getData();
                                ModelBean templateConferenceModelBean = (ModelBean) modelBean.get("templateConference");
                                Long templateId = (Long) templateConferenceModelBean.get("id");
                                ViewTemplateConference busiTemplateConferenceCascade = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuTypeStrCascade, templateId);
                                updateCascadeTemplateConference(busiTemplateConferenceCascade, upCascadeConferenceId, UpCascadeType.AUTO_CREATE, index);
                                templateIdListAdd.add(busiTemplateConferenceCascade);
                                templateIdListCascade.add(busiTemplateConferenceCascade.getConferenceId());
                            }
                        }
                    }
                }
            }
            ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
            Long id = conferenceIdVo.getId();
            McuType mcuType = conferenceIdVo.getMcuType();
            ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
            viewTemplateConferenceCascadeCon.setUpCascadeId(id);
            viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
            List<ViewTemplateConference> viewTemplateConferenceList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
            for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceList) {
                if (!templateIdListCascade.contains(viewTemplateConference.getConferenceId())) {
                    if (viewTemplateConference.getUpCascadeType() == UpCascadeType.AUTO_CREATE.getCode()) {
                        deleteCascadeTemplateConference(viewTemplateConference);
                    } else {
                        recoverUpdateCascadeTemplateConference(viewTemplateConference);
                    }
                }
            }
        } catch (Exception e) {
            for (ViewTemplateConference viewTemplateConference : templateIdListAdd) {
                try {
                    deleteCascadeTemplateConference(viewTemplateConference);
                } catch (Exception e1) {
                    logger.error(e1.getMessage());
                }
            }
            for (ViewTemplateConference viewTemplateConference : templateIdListUpdate) {
                try {
                    recoverUpdateCascadeTemplateConference(viewTemplateConference);
                } catch (Exception e1) {
                    logger.error(e1.getMessage());
                }
            }
            throw e;
        }
    }

    /**
     * 修改会议模板
     */
    @Log(title = "会议模板", businessType = BusinessType.UPDATE)
    @PutMapping("/{conferenceId}")
    @Operation(summary = "修改会议模板", description = "修改会议模板")
    public RestResponse edit(@RequestBody JSONObject jsonObj, @PathVariable("conferenceId") String conferenceId) {
        JSONObject templateConferenceObj = jsonObj.getJSONObject("templateConference");
        Assert.isTrue(templateConferenceObj != null, "会议模板不能为空！");
        Long masterTerminalId = templateConferenceObj.getLong("masterTerminalId");

        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                BusiTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiTemplateConference.class);

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
                for (int i = 0; i < templateDeptArr.size(); i++) {
                    templateDepts.add(templateDeptArr.getObject(i, BusiTemplateDept.class));
                }

                busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                int c = busiTemplateConferenceService.updateBusiTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
                if (c > 0) {
                    // 分屏
                    try {
                        if (jsonObj.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = jsonObj.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    // 级联
                    try {
                        editCascadeTemplateConference(jsonObj, conferenceId);
                    } catch (Exception e) {
                        return RestResponse.fail();
                    }
                    return RestResponse.success(busiTemplateConferenceService.selectBusiTemplateConferenceById(busiTemplateConference.getId()));
                } else {
                    return RestResponse.fail();
                }
            }
            case MCU_ZJ: {
                BusiMcuZjTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuZjTemplateConference.class);

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
                    // 分屏
                    try {
                        if (jsonObj.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = jsonObj.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    // 级联
                    try {
                        editCascadeTemplateConference(jsonObj, conferenceId);
                    } catch (Exception e) {
                        return RestResponse.fail();
                    }
                    return RestResponse.success(busiMcuZjTemplateConferenceService.selectBusiMcuZjTemplateConferenceById(busiTemplateConference.getId()));
                } else {
                    return RestResponse.fail();
                }
            }
            case MCU_PLC: {
                BusiMcuPlcTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuPlcTemplateConference.class);

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
                    // 分屏
                    try {
                        if (jsonObj.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = jsonObj.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    // 级联
                    try {
                        editCascadeTemplateConference(jsonObj, conferenceId);
                    } catch (Exception e) {
                        return RestResponse.fail();
                    }
                    return RestResponse.success(busiMcuPlcTemplateConferenceService.selectBusiMcuPlcTemplateConferenceById(busiTemplateConference.getId()));
                } else {
                    return RestResponse.fail();
                }
            }
            case MCU_KDC: {
                BusiMcuKdcTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuKdcTemplateConference.class);

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
                    // 分屏
                    try {
                        if (jsonObj.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = jsonObj.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    // 级联
                    try {
                        editCascadeTemplateConference(jsonObj, conferenceId);
                    } catch (Exception e) {
                        return RestResponse.fail();
                    }
                    return RestResponse.success(busiMcuKdcTemplateConferenceService.selectBusiMcuKdcTemplateConferenceById(busiTemplateConference.getId()));
                } else {
                    return RestResponse.fail();
                }
            }
            case SMC3: {
                BusiMcuSmc3TemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuSmc3TemplateConference.class);

                busiTemplateConference.setId(id);

                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("templateParticipants");
                List<BusiMcuSmc3TemplateParticipant> busiTemplateParticipants = new ArrayList<>();
                if (busiTemplateParticipantArr != null) {
                    for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                        BusiMcuSmc3TemplateParticipant p = busiTemplateParticipantArr.getObject(i, BusiMcuSmc3TemplateParticipant.class);
                        p.setId(null);
                        Assert.notNull(p.getTerminalId(), "参会者终端ID不能为空");
                        Assert.notNull(p.getWeight(), "参会者weight顺序不能为空");
                        Assert.notNull(p.getAttendType(), "参会者入会/直播类型不能为空");
                        busiTemplateParticipants.add(p);
                    }
                }

                // 部门顺序
                JSONArray templateDeptArr = jsonObj.getJSONArray("templateDepts");
                List<BusiMcuSmc3TemplateDept> templateDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++) {
                    templateDepts.add(templateDeptArr.getObject(i, BusiMcuSmc3TemplateDept.class));
                }

                busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());

                Object confPresetParam = templateConferenceObj.get("confPresetParam");
                if (confPresetParam != null) {
                    busiTemplateConference.setConfPresetParam(JSONObject.parseObject(JSONObject.toJSONString(confPresetParam)));
                }

                Map<String, Object> businessProperties = busiTemplateConference.getBusinessProperties();
                if (businessProperties == null) {
                    businessProperties = new HashMap<>();
                }
                businessProperties.put("mainMcuId", templateConferenceObj.get("mainMcuId"));
                businessProperties.put("mainMcuName", templateConferenceObj.get("mainMcuName"));
                businessProperties.put("mainServiceZoneId", templateConferenceObj.get("mainServiceZoneId"));
                businessProperties.put("mainServiceZoneName", templateConferenceObj.get("mainServiceZoneName"));

                businessProperties.put("videoProtocol", templateConferenceObj.get("videoProtocol"));
                businessProperties.put("videoResolution", templateConferenceObj.get("videoResolution"));
                businessProperties.put("audioProtocol", templateConferenceObj.get("audioProtocol"));
                businessProperties.put("streamService", templateConferenceObj.get("streamService"));
                busiTemplateConference.setBusinessProperties(businessProperties);
                busiTemplateConference.setGuestPassword((templateConferenceObj.get("password") == null ? null : (String) templateConferenceObj.get("password")));
//                JSONArray templateNodesJson = jsonObj.getJSONArray("cascadeNodes");
//                if (templateNodesJson != null) {
//                    busiTemplateConference.setCategory("CASCADE");
//                    busiTemplateConference.setIsAutoCreateConferenceNumber(1);
//                    busiTemplateConference.setBusinessFieldType(100);
//                    busiTemplateConference.setCreateType(2);
//                }
//                busiTemplateConference.setCascadeNodes(JSONArray.toJSONString(templateNodesJson));
                JSONArray templateNodesTempJson = jsonObj.getJSONArray("cascadeNodesTemp");
                if (templateNodesTempJson != null) {
                    busiTemplateConference.setCategory("CASCADE");
                    busiTemplateConference.setIsAutoCreateConferenceNumber(1);
                    busiTemplateConference.setBusinessFieldType(100);
                    busiTemplateConference.setCreateType(2);
                }
                busiTemplateConference.setCascadeNodesTemp(JSONArray.toJSONString(templateNodesTempJson));
                int c = busiMcuSmc3TemplateConferenceService.updateBusiTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
                if (c > 0) {
                    // 分屏
                    try {
                        if (jsonObj.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = jsonObj.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    // 级联
                    try {
                        editCascadeTemplateConference(jsonObj, conferenceId);
                    } catch (Exception e) {
                        return RestResponse.fail();
                    }
                    return RestResponse.success(busiMcuSmc3TemplateConferenceService.selectBusiTemplateConferenceById(busiTemplateConference.getId()));
                } else {
                    return RestResponse.fail();
                }
            }
            case SMC2: {
                BusiMcuSmc2TemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuSmc2TemplateConference.class);

                busiTemplateConference.setId(id);

                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("templateParticipants");
                List<BusiMcuSmc2TemplateParticipant> busiTemplateParticipants = new ArrayList<>();
                if (busiTemplateParticipantArr != null) {
                    for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                        BusiMcuSmc2TemplateParticipant p = busiTemplateParticipantArr.getObject(i, BusiMcuSmc2TemplateParticipant.class);
                        p.setId(null);
                        Assert.notNull(p.getTerminalId(), "参会者终端ID不能为空");
                        Assert.notNull(p.getWeight(), "参会者weight顺序不能为空");
                        Assert.notNull(p.getAttendType(), "参会者入会/直播类型不能为空");
                        busiTemplateParticipants.add(p);
                    }
                }

                // 部门顺序
                JSONArray templateDeptArr = jsonObj.getJSONArray("templateDepts");
                List<BusiMcuSmc2TemplateDept> templateDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++) {
                    templateDepts.add(templateDeptArr.getObject(i, BusiMcuSmc2TemplateDept.class));
                }

                busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                int c = busiMcuSmc2TemplateConferenceService.updateBusiTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
                if (c > 0) {
                    // 分屏
                    try {
                        if (jsonObj.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = jsonObj.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    // 级联
                    try {
                        editCascadeTemplateConference(jsonObj, conferenceId);
                    } catch (Exception e) {
                        return RestResponse.fail();
                    }
                    return RestResponse.success(busiMcuSmc2TemplateConferenceService.selectBusiTemplateConferenceById(busiTemplateConference.getId()));
                } else {
                    return RestResponse.fail();
                }
            }
            case MCU_TENCENT: {
                BusiMcuTencentTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuTencentTemplateConference.class);

                busiTemplateConference.setId(id);


                busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                if (busiTemplateConference.getIsAutoCreateStreamUrl() != null && busiTemplateConference.getIsAutoCreateStreamUrl() == 1) {
                    busiTemplateConference.setStreamUrl(null);
                }
                busiTemplateConference.setIsAutoCreateConferenceNumber(ConferenceNumberCreateType.AUTO.getValue());
                busiTemplateConference.setGuestPassword(null);
                busiTemplateConference.setConferencePassword(null);
                busiTemplateConference.setChairmanPassword(null);
                Object password = templateConferenceObj.get("password");
                if (password != null) {
                    String passwordStr=(String) password;
                    if(Strings.isNotBlank(passwordStr)){
                        Pattern pattern = Pattern.compile(regex);
                        // 创建匹配器
                        Matcher matcher = pattern.matcher((String)password);
                        if (!matcher.matches()) {
                            throw new CustomException("密码格式不正确,是4到6位的数字");
                        }
                        busiTemplateConference.setGuestPassword((String) password);
                        busiTemplateConference.setConferencePassword((String) password);
                    }

                }
                Object chairmanPassword = templateConferenceObj.get("chairmanPassword");
                if (chairmanPassword != null) {
                    String chairmanPasswordStr=(String) chairmanPassword;
                    if(Strings.isNotBlank(chairmanPasswordStr)){
                        Pattern pattern = Pattern.compile(regex_chair);
                        // 创建匹配器
                        Matcher matcher = pattern.matcher(chairmanPasswordStr);
                        if (!matcher.matches()) {
                            throw new CustomException("密码格式不正确,是6位的数字");
                        }
                        busiTemplateConference.setChairmanPassword( chairmanPasswordStr);
                    }
                }
                Object duration = templateConferenceObj.get("duration");
                if (duration != null) {
                    busiTemplateConference.setDurationTime((Integer) duration);
                }
                Object supportRecord = templateConferenceObj.get("supportRecord");
                if (supportRecord != null) {
                    busiTemplateConference.setRecordingEnabled((Integer) supportRecord);
                }

                Map<String, Object> businessProperties = busiTemplateConference.getBusinessProperties();
                if (businessProperties == null) {
                    businessProperties = new HashMap<>();
                }
                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("templateParticipants");
                if (busiTemplateParticipantArr != null) {
                    businessProperties.put("attendees",busiTemplateParticipantArr);
                }
                busiTemplateConference.setBusinessProperties(businessProperties);



                int c = busiMcuTencentTemplateConferenceService.updateBusiTemplateConference(busiTemplateConference, masterTerminalId, null, null);
                if (c > 0) {
                    // 级联
                    try {
                        editCascadeTemplateConference(jsonObj, conferenceId);
                    } catch (Exception e) {
                        return RestResponse.fail(e.getMessage());
                    }
                    return RestResponse.success(busiMcuTencentTemplateConferenceService.selectBusiTemplateConferenceById(busiTemplateConference.getId()));
                } else {
                    return RestResponse.fail();
                }
            }
            case MCU_DING: {
                BusiMcuDingTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuDingTemplateConference.class);

                busiTemplateConference.setId(id);


                busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                if (busiTemplateConference.getIsAutoCreateStreamUrl() != null && busiTemplateConference.getIsAutoCreateStreamUrl() == 1) {
                    busiTemplateConference.setStreamUrl(null);
                }
                busiTemplateConference.setGuestPassword(null);
                busiTemplateConference.setConferencePassword(null);
                busiTemplateConference.setChairmanPassword(null);
                busiTemplateConference.setIsAutoCreateConferenceNumber(ConferenceNumberCreateType.AUTO.getValue());
                Object password = templateConferenceObj.get("password");
                if (password != null) {
                    String passwordStr=(String) password;
                    if(Strings.isNotBlank(passwordStr)){
                        Pattern pattern = Pattern.compile(regex);
                        // 创建匹配器
                        Matcher matcher = pattern.matcher((String)password);
                        if (!matcher.matches()) {
                            throw new CustomException("密码格式不正确,是4到6位的数字");
                        }
                        busiTemplateConference.setGuestPassword((String) password);
                        busiTemplateConference.setConferencePassword((String) password);
                    }

                }
                Object chairmanPassword = templateConferenceObj.get("chairmanPassword");
                if (chairmanPassword != null) {
                    String chairmanPasswordStr=(String) chairmanPassword;
                    if(Strings.isNotBlank(chairmanPasswordStr)){
                        Pattern pattern = Pattern.compile(regex_chair);
                        // 创建匹配器
                        Matcher matcher = pattern.matcher(chairmanPasswordStr);
                        if (!matcher.matches()) {
                            throw new CustomException("密码格式不正确,是6位的数字");
                        }
                        busiTemplateConference.setChairmanPassword(chairmanPasswordStr);
                    }
                }
                Object duration = templateConferenceObj.get("duration");
                if (duration != null) {
                    busiTemplateConference.setDurationTime((Integer) duration);
                }
                Object supportRecord = templateConferenceObj.get("supportRecord");
                if (supportRecord != null) {
                    busiTemplateConference.setRecordingEnabled((Integer) supportRecord);
                }

                int c = busiMcuDingTemplateConferenceService.updateBusiTemplateConference(busiTemplateConference, masterTerminalId, null, null);
                if (c > 0) {
                    // 级联
                    try {
                        editCascadeTemplateConference(jsonObj, conferenceId);
                    } catch (Exception e) {
                        return RestResponse.fail(e.getMessage());
                    }
                    return RestResponse.success(busiMcuDingTemplateConferenceService.selectBusiTemplateConferenceById(busiTemplateConference.getId()));
                } else {
                    return RestResponse.fail();
                }
            }
            case MCU_HWCLOUD: {
                BusiMcuHwcloudTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuHwcloudTemplateConference.class);

                busiTemplateConference.setId(id);

                busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                if (busiTemplateConference.getIsAutoCreateStreamUrl() != null && busiTemplateConference.getIsAutoCreateStreamUrl() == 1) {
                    busiTemplateConference.setStreamUrl(null);
                }
                busiTemplateConference.setIsAutoCreateConferenceNumber(ConferenceNumberCreateType.AUTO.getValue());
                busiTemplateConference.setGuestPassword(null);
                busiTemplateConference.setChairmanPassword(null);

                Object chairmanPassword = templateConferenceObj.get("chairmanPassword");
                if (chairmanPassword != null) {
                    String chairmanPasswordStr=(String) chairmanPassword;
                    if(Strings.isNotBlank(chairmanPasswordStr)){
                        Pattern pattern = Pattern.compile(regex_chair);
                        // 创建匹配器
                        Matcher matcher = pattern.matcher((String)chairmanPassword);
                        if (!matcher.matches()) {
                            throw new CustomException("密码格式不正确,是6位的数字");
                        }
                        busiTemplateConference.setChairmanPassword((String) chairmanPassword);
                    }
                }
                Object duration = templateConferenceObj.get("duration");
                if (duration != null) {
                    busiTemplateConference.setDurationTime((Integer) duration);
                }
                Object supportRecord = templateConferenceObj.get("supportRecord");
                if (supportRecord != null) {
                    busiTemplateConference.setRecordingEnabled((Integer) supportRecord);
                }

                Map<String, Object> businessProperties = busiTemplateConference.getBusinessProperties();
                if (businessProperties == null) {
                    businessProperties = new HashMap<>();
                }
                Object callInRestriction = templateConferenceObj.get("callInRestriction");
                if(callInRestriction!=null){
                    businessProperties.put("callInRestriction",callInRestriction);
                }
                Object allowGuestStartConf = templateConferenceObj.get("allowGuestStartConf");
                if(callInRestriction!=null){
                    businessProperties.put("allowGuestStartConf",allowGuestStartConf);
                }


                Object enableWaitingRoom = templateConferenceObj.get("enableWaitingRoom");
                if(callInRestriction!=null){
                    businessProperties.put("enableWaitingRoom",enableWaitingRoom);
                }


                Object isSendNotify = templateConferenceObj.get("isSendNotify");
                if(callInRestriction!=null){
                    businessProperties.put("isSendNotify",isSendNotify);
                }


                Object isSendCalendar = templateConferenceObj.get("isSendCalendar");
                if(callInRestriction!=null){
                    businessProperties.put("isSendCalendar",isSendCalendar);
                }

                Object confPresetParam = templateConferenceObj.get("confPresetParam");
                if(confPresetParam!=null){
                    businessProperties.put("confPresetParam",confPresetParam);
                }

                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("templateParticipants");
                if (busiTemplateParticipantArr != null) {
                    businessProperties.put("attendees",busiTemplateParticipantArr);
                }
                busiTemplateConference.setBusinessProperties(businessProperties);

                int c = busiMcuHwcloudTemplateConferenceService.updateBusiTemplateConference(busiTemplateConference, masterTerminalId, null, null);
                if (c > 0) {
                    // 级联
                    try {
                        editCascadeTemplateConference(jsonObj, conferenceId);
                    } catch (Exception e) {
                        return RestResponse.fail(e.getMessage());
                    }
                    return RestResponse.success(busiMcuHwcloudTemplateConferenceService.selectBusiTemplateConferenceById(busiTemplateConference.getId()));
                } else {
                    return RestResponse.fail();
                }
            }

            case MCU_ZTE: {
                BusiMcuZteTemplateConference busiTemplateConference = templateConferenceObj.toJavaObject(BusiMcuZteTemplateConference.class);

                busiTemplateConference.setId(id);

                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("templateParticipants");
                List<BusiMcuZteTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
                if (busiTemplateParticipantArr != null) {
                    for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                        BusiMcuZteTemplateParticipant p = busiTemplateParticipantArr.getObject(i, BusiMcuZteTemplateParticipant.class);
                        p.setId(null);
                        Assert.notNull(p.getTerminalId(), "参会者终端ID不能为空");
                        Assert.notNull(p.getWeight(), "参会者weight顺序不能为空");
                        Assert.notNull(p.getAttendType(), "参会者入会/直播类型不能为空");
                        busiTemplateParticipants.add(p);
                    }
                }

                // 部门顺序
                JSONArray templateDeptArr = jsonObj.getJSONArray("templateDepts");
                List<BusiMcuZteTemplateDept> templateDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++) {
                    templateDepts.add(templateDeptArr.getObject(i, BusiMcuZteTemplateDept.class));
                }

                Map<String, Object> businessProperties = busiTemplateConference.getBusinessProperties();
                if (businessProperties == null) {
                    businessProperties = new HashMap<>();
                }
                businessProperties.put("maxParticipantNum", templateConferenceObj.get("maxParticipantNum"));
                businessProperties.put("multiPicControl", templateConferenceObj.get("multiPicControl"));
                businessProperties.put("multiViewNumber", templateConferenceObj.get("multiViewNumber"));
                businessProperties.put("dynamicRes", templateConferenceObj.get("dynamicRes"));
                businessProperties.put("inviteWithSDP", templateConferenceObj.get("inviteWithSDP"));
                businessProperties.put("conferenceTemplet", templateConferenceObj.get("conferenceTemplet"));

                businessProperties.put("confCascadeMode", templateConferenceObj.get("confCascadeMode"));
                businessProperties.put("enableMcuTitle", templateConferenceObj.get("enableMcuTitle"));
                businessProperties.put("enableMcuBanner", templateConferenceObj.get("enableMcuBanner"));
                businessProperties.put("enableVoiceRecord", templateConferenceObj.get("enableVoiceRecord"));

                businessProperties.put("enableAutoVoiceRecord", templateConferenceObj.get("enableAutoVoiceRecord"));
                businessProperties.put("enableUpConf", templateConferenceObj.get("enableUpConf"));
                businessProperties.put("sendMail", templateConferenceObj.get("sendMail"));
                busiTemplateConference.setBusinessProperties(businessProperties);
                if(Objects.equals("auto",templateConferenceObj.get("multiPicControl"))){
                    busiTemplateConference.setDefaultViewLayout(AutomaticSplitScreen.LAYOUT);
                }
                busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                int c = busiMcuZteTemplateConferenceService.updateBusiMcuZteTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
                if (c > 0) {
                    // 分屏
                    try {
                        if (jsonObj.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = jsonObj.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    // 级联
                    try {
                        editCascadeTemplateConference(jsonObj, conferenceId);
                    } catch (Exception e) {
                        return RestResponse.fail();
                    }
                    return RestResponse.success(busiMcuZteTemplateConferenceService.selectBusiMcuZteTemplateConferenceById(busiTemplateConference.getId()));
                } else {
                    return RestResponse.fail();
                }
            }

        }
        return RestResponse.fail();
    }

    /**
     * 删除会议模板
     */
    @Log(title = "会议模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/{conferenceId}")
    @Operation(summary = "删除会议模板", description = "删除会议模板")
    public RestResponse remove(@PathVariable("conferenceId") String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (baseConferenceContext != null) {
            throw new SystemException("当前模板正在开会，不能删除！");
        }
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
        viewTemplateConferenceCascadeCon.setUpCascadeId(id);
        viewTemplateConferenceCascadeCon.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
        List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
        for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
            try {
                if (viewTemplateConference.getUpCascadeType() == UpCascadeType.AUTO_CREATE.getCode()) {
                    remove(viewTemplateConference.getConferenceId());
                } else {
                    recoverUpdateCascadeTemplateConference(viewTemplateConference);
                }
            } catch (Exception e1) {
                logger.error(e1.getMessage());
            }
        }
        switch (mcuType) {
            case FME: {
                return toAjax(busiTemplateConferenceService.deleteBusiTemplateConferenceById(id));
            }
            case MCU_ZJ: {
                return toAjax(busiMcuZjTemplateConferenceService.deleteBusiMcuZjTemplateConferenceById(id));
            }
            case MCU_PLC: {
                return toAjax(busiMcuPlcTemplateConferenceService.deleteBusiMcuPlcTemplateConferenceById(id));
            }
            case MCU_KDC: {
                return toAjax(busiMcuKdcTemplateConferenceService.deleteBusiMcuKdcTemplateConferenceById(id));
            }
            case SMC3: {
                return toAjax(busiMcuSmc3TemplateConferenceService.deleteBusiTemplateConferenceById(id));
            }
            case SMC2: {
                return toAjax(busiMcuSmc2TemplateConferenceService.deleteBusiTemplateConferenceById(id));
            }
            case MCU_TENCENT: {
                return toAjax(busiMcuTencentTemplateConferenceService.deleteBusiTemplateConferenceById(id));
            }
            case MCU_DING: {
                return toAjax(busiMcuDingTemplateConferenceService.deleteBusiTemplateConferenceById(id));
            }
            case MCU_HWCLOUD: {
                return toAjax(busiMcuHwcloudTemplateConferenceService.deleteBusiTemplateConferenceById(id));
            }
            case MCU_ZTE: {
                return toAjax(busiMcuZteTemplateConferenceService.deleteBusiMcuZteTemplateConferenceById(id));
            }
        }
        return RestResponse.fail();
    }

    /**
     * 根据部门查询资源模板列表
     */
    @GetMapping("/resourceTemplateList/{deptId}")
    @Operation(summary = "根据部门查询资源模板列表")
    public RestResponse resourceTemplateList(@PathVariable("deptId") Long deptId) {
        Assert.notNull(deptId, "部门ID不能为空");
        McuZjBridge mcuZjBridge = null;
        try {
            mcuZjBridge = McuZjBridgeCache.getInstance().getAvailableMcuZjBridgesByDept(deptId).getMasterMcuZjBridge();
        } catch (Exception e) {
        }
        Assert.notNull(mcuZjBridge, "当前MCU不可用！");
        List<SourceTemplate> sourceTemplateList = mcuZjBridge.getSourceTemplateList();
        if (sourceTemplateList == null || sourceTemplateList.size() == 0) {
            Assert.isTrue(false, "当前MCU不可用！");
        }
        List<ModelBean> modelBeanList = new ArrayList<>();
        for (int i = 0; i < sourceTemplateList.size(); i++) {
            SourceTemplate sourceTemplate = sourceTemplateList.get(i);
            ModelBean modelBean = new ModelBean();
            modelBean.put("deptId", deptId);
            modelBean.put("id", sourceTemplate.getId());
            modelBean.put("name", sourceTemplate.getName());
            modelBean.put("isSupportRollCall", sourceTemplate.isSupportRollCall());
            modelBean.put("isSupportSplitScreen", sourceTemplate.isSupportSplitScreen());
            modelBean.put("isSupportPolling", sourceTemplate.isSupportPolling());
            modelBean.put("isSupportChooseSee", sourceTemplate.isSupportChooseSee());
            modelBean.put("isSupportTalk", sourceTemplate.isSupportTalk());
            modelBean.put("isSupportBroadcast", sourceTemplate.isSupportBroadcast());
            boolean singleView = sourceTemplate.getSingle_view() != null && sourceTemplate.getSingle_view() == 1;
            modelBean.put("isSingleView", singleView);
            modelBean.put("speakerSplitScreenList", sourceTemplate.getSpeakerSplitScreenList());
            modelBean.put("guestSplitScreenList", sourceTemplate.getGuestSplitScreenList());
            if (sourceTemplate.getIs_default() != null && sourceTemplate.getIs_default() == 1) {
                modelBean.put("isDefault", true);
            } else {
                modelBean.put("isDefault", false);
            }
            modelBeanList.add(modelBean);
        }
        return RestResponse.success(0, "查询成功", modelBeanList);
    }

    /**
     * 查询可级联的会议模板列表
     */
    @GetMapping("/canDownCascadeTemplateList")
    @Operation(summary = "查询可级联的会议模板列表")
    public RestResponse canDownCascadeTemplateList(Long deptId, @RequestParam(required = false) String conferenceId) {
        Assert.notNull(deptId, "部门ID不能为空");
        ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
        ConferenceIdVo conferenceIdVo;
        if (StringUtils.isNotEmpty(conferenceId)) {
            conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
            viewTemplateConferenceCon.setId(conferenceIdVo.getId());
            viewTemplateConferenceCon.setMcuType(conferenceIdVo.getMcuType().getCode());
            ViewTemplateConference viewTemplateConference = viewTemplateConferenceMapper.selectViewTemplateConferenceById(conferenceIdVo.getMcuType().getCode(), conferenceIdVo.getId());
            if (Strings.isNotBlank(viewTemplateConference.getUpCascadeMcuType())) {
                return RestResponse.success(0, "查询成功", new ArrayList<>());
            }
        } else {
            conferenceIdVo = new ConferenceIdVo();
        }
        viewTemplateConferenceCon.setDeptId(deptId);
        startPage();
        List<ViewTemplateConference> list;
        Integer cascadeType = ExternalConfigCache.getInstance().getCascadeType();
        boolean isSelectAll = cascadeType != null && cascadeType == 1;
        if (isSelectAll) {
            list = viewTemplateConferenceMapper.selectCanDownCascadeViewTemplateConferenceList(viewTemplateConferenceCon);
        } else {
            list = viewTemplateConferenceMapper.selectCanDownCascadeViewTemplateConferenceListExcludeHasDownCascade(viewTemplateConferenceCon);
        }
        PaginationDataNew<ModelBean> pd = new PaginationDataNew<>();
        pd.setTotal(new PageInfo<>(list).getTotal());
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
                    modelBean.put("isDownCascade", false);
                    if (isSelectAll) {
                        ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                        viewTemplateConferenceCascadeCon.setUpCascadeId(conferenceIdVo.getId());
                        viewTemplateConferenceCascadeCon.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                        List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                        if (viewTemplateConferenceCascadeList.size() > 0) {
                            modelBean.put("isUpCascade", true);
                        } else {
                            modelBean.put("isUpCascade", false);
                        }
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
                    modelBean.put("isDownCascade", false);
                    if (isSelectAll) {
                        ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                        viewTemplateConferenceCascadeCon.setUpCascadeId(conferenceIdVo.getId());
                        viewTemplateConferenceCascadeCon.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                        List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                        if (viewTemplateConferenceCascadeList.size() > 0) {
                            modelBean.put("isUpCascade", true);
                        } else {
                            modelBean.put("isUpCascade", false);
                        }
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
                    modelBean.put("isDownCascade", false);
                    if (isSelectAll) {
                        ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                        viewTemplateConferenceCascadeCon.setUpCascadeId(conferenceIdVo.getId());
                        viewTemplateConferenceCascadeCon.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                        List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                        if (viewTemplateConferenceCascadeList.size() > 0) {
                            modelBean.put("isUpCascade", true);
                        } else {
                            modelBean.put("isUpCascade", false);
                        }
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
                    modelBean.put("isDownCascade", false);
                    if (isSelectAll) {
                        ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                        viewTemplateConferenceCascadeCon.setUpCascadeId(conferenceIdVo.getId());
                        viewTemplateConferenceCascadeCon.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                        List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                        if (viewTemplateConferenceCascadeList.size() > 0) {
                            modelBean.put("isUpCascade", true);
                        } else {
                            modelBean.put("isUpCascade", false);
                        }
                    } else {
                        modelBean.put("isUpCascade", false);
                    }
                    mbs.add(modelBean);
                    break;
                }
                case SMC3: {
                    BusiMcuSmc3TemplateConference busiTemplateConference = new BusiMcuSmc3TemplateConference();
                    BeanUtils.copyBeanProp(busiTemplateConference, viewTemplateConferenceTemp);
                    ModelBean modelBean = busiMcuSmc3TemplateConferenceService.getTemplateConferenceDetails(busiTemplateConference);
                    modelBean.put("conferenceId", viewTemplateConferenceTemp.getConferenceId());
                    modelBean.put("mcuType", viewTemplateConferenceTemp.getMcuType());
                    modelBean.put("mcuTypeAlias", McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
                    modelBean.put("isDownCascade", false);
                    if (isSelectAll) {
                        ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                        viewTemplateConferenceCascadeCon.setUpCascadeId(conferenceIdVo.getId());
                        viewTemplateConferenceCascadeCon.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                        List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                        if (viewTemplateConferenceCascadeList.size() > 0) {
                            modelBean.put("isUpCascade", true);
                        } else {
                            modelBean.put("isUpCascade", false);
                        }
                    } else {
                        modelBean.put("isUpCascade", false);
                    }
                    mbs.add(modelBean);
                    break;
                }
                case SMC2: {
                    BusiMcuSmc2TemplateConference busiTemplateConference = new BusiMcuSmc2TemplateConference();
                    BeanUtils.copyBeanProp(busiTemplateConference, viewTemplateConferenceTemp);
                    ModelBean modelBean = busiMcuSmc2TemplateConferenceService.getTemplateConferenceDetails(busiTemplateConference);
                    modelBean.put("conferenceId", viewTemplateConferenceTemp.getConferenceId());
                    modelBean.put("mcuType", viewTemplateConferenceTemp.getMcuType());
                    modelBean.put("mcuTypeAlias", McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
                    modelBean.put("isDownCascade", false);
                    if (isSelectAll) {
                        ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                        viewTemplateConferenceCascadeCon.setUpCascadeId(conferenceIdVo.getId());
                        viewTemplateConferenceCascadeCon.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                        List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                        if (viewTemplateConferenceCascadeList.size() > 0) {
                            modelBean.put("isUpCascade", true);
                        } else {
                            modelBean.put("isUpCascade", false);
                        }
                    } else {
                        modelBean.put("isUpCascade", false);
                    }
                    mbs.add(modelBean);
                    break;
                }
                case MCU_TENCENT: {
                    BusiMcuTencentTemplateConference busiTemplateConference = new BusiMcuTencentTemplateConference();
                    BeanUtils.copyBeanProp(busiTemplateConference, viewTemplateConferenceTemp);
                    ModelBean modelBean = busiMcuTencentTemplateConferenceService.getTemplateConferenceDetails(busiTemplateConference);
                    modelBean.put("conferenceId", viewTemplateConferenceTemp.getConferenceId());
                    modelBean.put("mcuType", viewTemplateConferenceTemp.getMcuType());
                    modelBean.put("mcuTypeAlias", McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
                    modelBean.put("isDownCascade", false);
                    if (isSelectAll) {
                        ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                        viewTemplateConferenceCascadeCon.setUpCascadeId(conferenceIdVo.getId());
                        viewTemplateConferenceCascadeCon.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                        List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                        if (viewTemplateConferenceCascadeList.size() > 0) {
                            modelBean.put("isUpCascade", true);
                        } else {
                            modelBean.put("isUpCascade", false);
                        }
                    } else {
                        modelBean.put("isUpCascade", false);
                    }
                    mbs.add(modelBean);
                    break;
                }
                case MCU_DING: {
                    BusiMcuDingTemplateConference busiTemplateConference = new BusiMcuDingTemplateConference();
                    BeanUtils.copyBeanProp(busiTemplateConference, viewTemplateConferenceTemp);
                    ModelBean modelBean = busiMcuDingTemplateConferenceService.getTemplateConferenceDetails(busiTemplateConference);
                    modelBean.put("conferenceId", viewTemplateConferenceTemp.getConferenceId());
                    modelBean.put("mcuType", viewTemplateConferenceTemp.getMcuType());
                    modelBean.put("mcuTypeAlias", McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
                    modelBean.put("isDownCascade", false);
                    if (isSelectAll) {
                        ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                        viewTemplateConferenceCascadeCon.setUpCascadeId(conferenceIdVo.getId());
                        viewTemplateConferenceCascadeCon.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                        List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                        if (viewTemplateConferenceCascadeList.size() > 0) {
                            modelBean.put("isUpCascade", true);
                        } else {
                            modelBean.put("isUpCascade", false);
                        }
                    } else {
                        modelBean.put("isUpCascade", false);
                    }
                    mbs.add(modelBean);
                    break;
                }

                case MCU_HWCLOUD: {
                    BusiMcuHwcloudTemplateConference busiTemplateConference = new BusiMcuHwcloudTemplateConference();
                    BeanUtils.copyBeanProp(busiTemplateConference, viewTemplateConferenceTemp);
                    ModelBean modelBean = busiMcuHwcloudTemplateConferenceService.getTemplateConferenceDetails(busiTemplateConference);
                    modelBean.put("conferenceId", viewTemplateConferenceTemp.getConferenceId());
                    modelBean.put("mcuType", viewTemplateConferenceTemp.getMcuType());
                    modelBean.put("mcuTypeAlias", McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
                    modelBean.put("isDownCascade", false);
                    if (isSelectAll) {
                        ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                        viewTemplateConferenceCascadeCon.setUpCascadeId(conferenceIdVo.getId());
                        viewTemplateConferenceCascadeCon.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                        List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                        if (viewTemplateConferenceCascadeList.size() > 0) {
                            modelBean.put("isUpCascade", true);
                        } else {
                            modelBean.put("isUpCascade", false);
                        }
                    } else {
                        modelBean.put("isUpCascade", false);
                    }
                    mbs.add(modelBean);
                    break;
                }

                case MCU_ZTE: {
                    BusiMcuZteTemplateConference busiMcuZteTemplateConference = new BusiMcuZteTemplateConference();
                    BeanUtils.copyBeanProp(busiMcuZteTemplateConference, viewTemplateConferenceTemp);
                    ModelBean modelBean = busiMcuZteTemplateConferenceService.getTemplateConferenceDetails(busiMcuZteTemplateConference);
                    modelBean.put("conferenceId", viewTemplateConferenceTemp.getConferenceId());
                    modelBean.put("mcuType", viewTemplateConferenceTemp.getMcuType());
                    modelBean.put("mcuTypeAlias", McuType.convert(viewTemplateConferenceTemp.getMcuType()).getAlias());
                    modelBean.put("isDownCascade", false);
                    if (isSelectAll) {
                        ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                        viewTemplateConferenceCascadeCon.setUpCascadeId(conferenceIdVo.getId());
                        viewTemplateConferenceCascadeCon.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                        List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                        if (viewTemplateConferenceCascadeList.size() > 0) {
                            modelBean.put("isUpCascade", true);
                        } else {
                            modelBean.put("isUpCascade", false);
                        }
                    } else {
                        modelBean.put("isUpCascade", false);
                    }
                    mbs.add(modelBean);
                    break;
                }
            }
        }
        for (ModelBean mb : mbs) {
            pd.addRecord(mb);
        }
        pd.setRecords(mbs);
        return RestResponse.success(0, "查询成功", pd);
    }

}
