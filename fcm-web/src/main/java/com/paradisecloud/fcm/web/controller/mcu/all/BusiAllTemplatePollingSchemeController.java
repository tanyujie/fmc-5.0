package com.paradisecloud.fcm.web.controller.mcu.all;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.fme.attendee.interfaces.IBusiTemplatePollingSchemeService;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcTemplatePollingSchemeService;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcTemplatePollingSchemeService;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjTemplatePollingSchemeService;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2TemplatePollingDept;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2TemplatePollingPaticipant;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2TemplatePollingScheme;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiSmc2TemplatePollingSchemeService;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplatePollingDept;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplatePollingPaticipant;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplatePollingScheme;
import com.paradisecloud.fcm.zte.service.interfaces.IBusiMcuZteTemplatePollingSchemeService;
import com.paradisecloud.smc3.service.interfaces.IBusiSmc3TemplatePollingSchemeService;
import com.sinhy.exception.SystemException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 轮询方案Controller
 * 
 * @author lilinhai
 * @date 2021-02-25
 */
@RestController
@RequestMapping("/busi/mcu/all/templatePollingScheme")
@Tag(name = "轮询方案")
public class BusiAllTemplatePollingSchemeController extends BaseController
{

    @Resource
    private IBusiTemplatePollingSchemeService busiTemplatePollingSchemeService;
    @Resource
    private IBusiMcuZjTemplatePollingSchemeService busiMcuZjTemplatePollingSchemeService;
    @Resource
    private IBusiMcuPlcTemplatePollingSchemeService busiMcuPlcTemplatePollingSchemeService;
    @Resource
    private IBusiMcuKdcTemplatePollingSchemeService busiMcuKdcTemplatePollingSchemeService;
    @Resource
    private IBusiSmc3TemplatePollingSchemeService busiSmc3TemplatePollingSchemeService;
    @Resource
    private IBusiSmc2TemplatePollingSchemeService busiSmc2TemplatePollingSchemeService;
    @Resource
    private IBusiMcuZteTemplatePollingSchemeService busiMcuZteTemplatePollingSchemeService;
    /**
     * 查询轮询方案列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询轮询方案列表")
    public RestResponse list(String conferenceId)
    {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                BusiTemplatePollingScheme busiTemplatePollingScheme = new BusiTemplatePollingScheme();
                busiTemplatePollingScheme.setTemplateConferenceId(id);
                List<BusiTemplatePollingScheme> busiTemplatePollingSchemeList = busiTemplatePollingSchemeService.selectBusiTemplatePollingSchemeList(busiTemplatePollingScheme);
                List<ModelBean> list = new ArrayList<>();
                for (BusiTemplatePollingScheme busiTemplatePollingSchemeTemp : busiTemplatePollingSchemeList) {
                    ModelBean modelBean = new ModelBean(busiTemplatePollingSchemeTemp);
                    modelBean.put("conferenceId", conferenceId);
                    list.add(modelBean);
                }
                return success(list);
            }
            case MCU_ZJ: {
                BusiMcuZjTemplatePollingScheme busiMcuZjTemplatePollingScheme = new BusiMcuZjTemplatePollingScheme();
                busiMcuZjTemplatePollingScheme.setTemplateConferenceId(id);
                List<BusiMcuZjTemplatePollingScheme> busiMcuZjTemplatePollingSchemeList = busiMcuZjTemplatePollingSchemeService.selectBusiMcuZjTemplatePollingSchemeList(busiMcuZjTemplatePollingScheme);
                List<ModelBean> list = new ArrayList<>();
                for (BusiMcuZjTemplatePollingScheme busiMcuZjTemplatePollingSchemeTemp : busiMcuZjTemplatePollingSchemeList) {
                    ModelBean modelBean = new ModelBean(busiMcuZjTemplatePollingSchemeTemp);
                    modelBean.put("conferenceId", conferenceId);
                    list.add(modelBean);
                }
                return success(list);
            }
            case MCU_PLC: {
                BusiMcuPlcTemplatePollingScheme busiMcuPlcTemplatePollingScheme = new BusiMcuPlcTemplatePollingScheme();
                busiMcuPlcTemplatePollingScheme.setTemplateConferenceId(id);
                List<BusiMcuPlcTemplatePollingScheme> busiMcuPlcTemplatePollingSchemeList = busiMcuPlcTemplatePollingSchemeService.selectBusiMcuPlcTemplatePollingSchemeList(busiMcuPlcTemplatePollingScheme);
                List<ModelBean> list = new ArrayList<>();
                for (BusiMcuPlcTemplatePollingScheme busiMcuPlcTemplatePollingSchemeTemp : busiMcuPlcTemplatePollingSchemeList) {
                    ModelBean modelBean = new ModelBean(busiMcuPlcTemplatePollingSchemeTemp);
                    modelBean.put("conferenceId", conferenceId);
                    list.add(modelBean);
                }
                return success(list);
            }
            case MCU_KDC: {
                BusiMcuKdcTemplatePollingScheme busiMcuKdcTemplatePollingScheme = new BusiMcuKdcTemplatePollingScheme();
                busiMcuKdcTemplatePollingScheme.setTemplateConferenceId(id);
                List<BusiMcuKdcTemplatePollingScheme> busiMcuKdcTemplatePollingSchemeList = busiMcuKdcTemplatePollingSchemeService.selectBusiMcuKdcTemplatePollingSchemeList(busiMcuKdcTemplatePollingScheme);
                List<ModelBean> list = new ArrayList<>();
                for (BusiMcuKdcTemplatePollingScheme busiMcuKdcTemplatePollingSchemeTemp : busiMcuKdcTemplatePollingSchemeList) {
                    ModelBean modelBean = new ModelBean(busiMcuKdcTemplatePollingSchemeTemp);
                    modelBean.put("conferenceId", conferenceId);
                    list.add(modelBean);
                }
                return success(list);
            }
            case SMC3: {
                BusiMcuSmc3TemplatePollingScheme busiMcuSmc3TemplatePollingScheme = new BusiMcuSmc3TemplatePollingScheme();
                busiMcuSmc3TemplatePollingScheme.setTemplateConferenceId(id);
                List<BusiMcuSmc3TemplatePollingScheme> busiMcuSmc3TemplatePollingSchemeList = busiSmc3TemplatePollingSchemeService.selectBusiTemplatePollingSchemeList(busiMcuSmc3TemplatePollingScheme);
                List<ModelBean> list = new ArrayList<>();
                for (BusiMcuSmc3TemplatePollingScheme busiMcuSmc3TemplatePollingSchemeTemp : busiMcuSmc3TemplatePollingSchemeList) {
                    ModelBean modelBean = new ModelBean(busiMcuSmc3TemplatePollingSchemeTemp);
                    modelBean.put("conferenceId", conferenceId);
                    list.add(modelBean);
                }
                return success(list);
            }
            case SMC2: {
                BusiMcuSmc2TemplatePollingScheme busiMcuSmc2TemplatePollingScheme = new BusiMcuSmc2TemplatePollingScheme();
                busiMcuSmc2TemplatePollingScheme.setTemplateConferenceId(id);
                List<BusiMcuSmc2TemplatePollingScheme> busiMcuSmc2TemplatePollingSchemeList = busiSmc2TemplatePollingSchemeService.selectBusiTemplatePollingSchemeList(busiMcuSmc2TemplatePollingScheme);
                List<ModelBean> list = new ArrayList<>();
                for (BusiMcuSmc2TemplatePollingScheme busiMcuSmc3TemplatePollingSchemeTemp : busiMcuSmc2TemplatePollingSchemeList) {
                    ModelBean modelBean = new ModelBean(busiMcuSmc3TemplatePollingSchemeTemp);
                    modelBean.put("conferenceId", conferenceId);
                    list.add(modelBean);
                }
                return success(list);
            }
            case MCU_ZTE: {
                BusiMcuZteTemplatePollingScheme busiMcuZteTemplatePollingScheme = new BusiMcuZteTemplatePollingScheme();
                busiMcuZteTemplatePollingScheme.setTemplateConferenceId(id);
                List<BusiMcuZteTemplatePollingScheme> busiMcuZteTemplatePollingSchemeList = busiMcuZteTemplatePollingSchemeService.selectBusiMcuZteTemplatePollingSchemeList(busiMcuZteTemplatePollingScheme);
                List<ModelBean> list = new ArrayList<>();
                for (BusiMcuZteTemplatePollingScheme busiMcuZteTemplatePollingSchemeTemp : busiMcuZteTemplatePollingSchemeList) {
                    ModelBean modelBean = new ModelBean(busiMcuZteTemplatePollingSchemeTemp);
                    modelBean.put("conferenceId", conferenceId);
                    list.add(modelBean);
                }
                return success(list);
            }

        }
        return RestResponse.fail();
    }

    /**
     * 获取轮询方案详细信息
     */
    @GetMapping(value = "/{conferenceId}/{id}")
    @Operation(summary = "获取轮询方案详细信息")
    public RestResponse getInfo(@PathVariable("conferenceId") String conferenceId, @PathVariable("id") Long id)
    {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                ModelBean modelBean = busiTemplatePollingSchemeService.selectBusiTemplatePollingSchemeById(id);
                modelBean.put("conferenceId", conferenceId);
                return RestResponse.success(modelBean);
            }
            case MCU_ZJ: {
                ModelBean modelBean = busiMcuZjTemplatePollingSchemeService.selectBusiMcuZjTemplatePollingSchemeById(id);
                modelBean.put("conferenceId", conferenceId);
                return RestResponse.success(modelBean);
            }
            case MCU_PLC: {
                ModelBean modelBean = busiMcuPlcTemplatePollingSchemeService.selectBusiMcuPlcTemplatePollingSchemeById(id);
                modelBean.put("conferenceId", conferenceId);
                return RestResponse.success(modelBean);
            }
            case MCU_KDC: {
                ModelBean modelBean = busiMcuKdcTemplatePollingSchemeService.selectBusiMcuKdcTemplatePollingSchemeById(id);
                modelBean.put("conferenceId", conferenceId);
                return RestResponse.success(modelBean);
            }
            case SMC3: {
                ModelBean modelBean = busiSmc3TemplatePollingSchemeService.selectBusiTemplatePollingSchemeById(id);
                modelBean.put("conferenceId", conferenceId);
                return RestResponse.success(modelBean);
            }
            case SMC2: {
                ModelBean modelBean = busiSmc2TemplatePollingSchemeService.selectBusiTemplatePollingSchemeById(id);
                modelBean.put("conferenceId", conferenceId);
                return RestResponse.success(modelBean);
            }
            case MCU_ZTE: {
                ModelBean modelBean = busiMcuZteTemplatePollingSchemeService.selectBusiMcuZteTemplatePollingSchemeById(id);
                modelBean.put("conferenceId", conferenceId);
                return RestResponse.success(modelBean);
            }
        }
        return RestResponse.fail();
    }

    /**
     * 新增轮询方案
     */
    @Log(title = "轮询方案", businessType = BusinessType.INSERT)
    @PostMapping("/{conferenceId}")
    @Operation(summary = "新增轮询方案", description = "新增轮询方案")
    public RestResponse add(@RequestBody JSONObject jsonObj, @PathVariable("conferenceId") String conferenceId)
    {
        if (StringUtils.isEmpty(conferenceId)) {
            throw new SystemException(1110098, "conferenceId不能为空！");
        }
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                BusiTemplatePollingScheme templatePollingScheme = jsonObj.getObject("pollingScheme", BusiTemplatePollingScheme.class);
                if (templatePollingScheme == null)
                {
                    throw new SystemException(1110098, "轮询方案不能为空！");
                }
                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("pollingParticipants");
                List<BusiTemplatePollingPaticipant> templatePollingPaticipants = new ArrayList<>();
                for (int i = 0; i < busiTemplateParticipantArr.size(); i++)
                {
                    BusiTemplatePollingPaticipant busiTemplatePollingPaticipant = busiTemplateParticipantArr.getObject(i, BusiTemplatePollingPaticipant.class);
                    Object busiTemplateParticipantObj = busiTemplateParticipantArr.get(i);
                    if (busiTemplateParticipantObj instanceof Map) {
                        Map busiTemplateParticipantMap = (Map) busiTemplateParticipantObj;
                        if (busiTemplateParticipantMap.containsKey("downCascadeConferenceId")) {
                            String downCascadeConferenceId = (String) busiTemplateParticipantMap.get("downCascadeConferenceId");
                            ConferenceIdVo downCascadeConferenceIdVo = EncryptIdUtil.parasConferenceId(downCascadeConferenceId);
                            busiTemplatePollingPaticipant.setDownCascadeTemplateId(downCascadeConferenceIdVo.getId());
                            busiTemplatePollingPaticipant.setDownCascadeMcuType(downCascadeConferenceIdVo.getMcuType().getCode());
                        }
                    }
                    templatePollingPaticipants.add(busiTemplatePollingPaticipant);
                }

                // 部门顺序
                JSONArray templateDeptArr = jsonObj.getJSONArray("pollingDepts");
                List<BusiTemplatePollingDept> templatePollingDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++)
                {
                    templatePollingDepts.add(templateDeptArr.getObject(i, BusiTemplatePollingDept.class));
                }
                return toAjax(busiTemplatePollingSchemeService.insertBusiTemplatePollingScheme(templatePollingScheme, templatePollingDepts, templatePollingPaticipants));
            }
            case MCU_ZJ: {
                BusiMcuZjTemplatePollingScheme templatePollingScheme = jsonObj.getObject("pollingScheme", BusiMcuZjTemplatePollingScheme.class);
                if (templatePollingScheme == null)
                {
                    throw new SystemException(1110098, "轮询方案不能为空！");
                }
                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("pollingParticipants");
                List<BusiMcuZjTemplatePollingPaticipant> templatePollingPaticipants = new ArrayList<>();
                for (int i = 0; i < busiTemplateParticipantArr.size(); i++)
                {
                    BusiMcuZjTemplatePollingPaticipant busiTemplatePollingPaticipant = busiTemplateParticipantArr.getObject(i, BusiMcuZjTemplatePollingPaticipant.class);
                    Object busiTemplateParticipantObj = busiTemplateParticipantArr.get(i);
                    if (busiTemplateParticipantObj instanceof Map) {
                        Map busiTemplateParticipantMap = (Map) busiTemplateParticipantObj;
                        if (busiTemplateParticipantMap.containsKey("downCascadeConferenceId")) {
                            String downCascadeConferenceId = (String) busiTemplateParticipantMap.get("downCascadeConferenceId");
                            ConferenceIdVo downCascadeConferenceIdVo = EncryptIdUtil.parasConferenceId(downCascadeConferenceId);
                            busiTemplatePollingPaticipant.setDownCascadeTemplateId(downCascadeConferenceIdVo.getId());
                            busiTemplatePollingPaticipant.setDownCascadeMcuType(downCascadeConferenceIdVo.getMcuType().getCode());
                        }
                    }
                    templatePollingPaticipants.add(busiTemplatePollingPaticipant);
                }

                // 部门顺序
                JSONArray templateDeptArr = jsonObj.getJSONArray("pollingDepts");
                List<BusiMcuZjTemplatePollingDept> templatePollingDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++)
                {
                    templatePollingDepts.add(templateDeptArr.getObject(i, BusiMcuZjTemplatePollingDept.class));
                }
                return toAjax(busiMcuZjTemplatePollingSchemeService.insertBusiMcuZjTemplatePollingScheme(templatePollingScheme, templatePollingDepts, templatePollingPaticipants));
            }
            case MCU_PLC: {
                BusiMcuPlcTemplatePollingScheme templatePollingScheme = jsonObj.getObject("pollingScheme", BusiMcuPlcTemplatePollingScheme.class);
                if (templatePollingScheme == null)
                {
                    throw new SystemException(1110098, "轮询方案不能为空！");
                }
                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("pollingParticipants");
                List<BusiMcuPlcTemplatePollingPaticipant> templatePollingPaticipants = new ArrayList<>();
                for (int i = 0; i < busiTemplateParticipantArr.size(); i++)
                {
                    BusiMcuPlcTemplatePollingPaticipant busiTemplatePollingPaticipant = busiTemplateParticipantArr.getObject(i, BusiMcuPlcTemplatePollingPaticipant.class);
                    Object busiTemplateParticipantObj = busiTemplateParticipantArr.get(i);
                    if (busiTemplateParticipantObj instanceof Map) {
                        Map busiTemplateParticipantMap = (Map) busiTemplateParticipantObj;
                        if (busiTemplateParticipantMap.containsKey("downCascadeConferenceId")) {
                            String downCascadeConferenceId = (String) busiTemplateParticipantMap.get("downCascadeConferenceId");
                            ConferenceIdVo downCascadeConferenceIdVo = EncryptIdUtil.parasConferenceId(downCascadeConferenceId);
                            busiTemplatePollingPaticipant.setDownCascadeTemplateId(downCascadeConferenceIdVo.getId());
                            busiTemplatePollingPaticipant.setDownCascadeMcuType(downCascadeConferenceIdVo.getMcuType().getCode());
                        }
                    }
                    templatePollingPaticipants.add(busiTemplatePollingPaticipant);
                }

                // 部门顺序
                JSONArray templateDeptArr = jsonObj.getJSONArray("pollingDepts");
                List<BusiMcuPlcTemplatePollingDept> templatePollingDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++)
                {
                    templatePollingDepts.add(templateDeptArr.getObject(i, BusiMcuPlcTemplatePollingDept.class));
                }
                return toAjax(busiMcuPlcTemplatePollingSchemeService.insertBusiMcuPlcTemplatePollingScheme(templatePollingScheme, templatePollingDepts, templatePollingPaticipants));
            }
            case MCU_KDC: {
                BusiMcuKdcTemplatePollingScheme templatePollingScheme = jsonObj.getObject("pollingScheme", BusiMcuKdcTemplatePollingScheme.class);
                if (templatePollingScheme == null)
                {
                    throw new SystemException(1110098, "轮询方案不能为空！");
                }
                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("pollingParticipants");
                List<BusiMcuKdcTemplatePollingPaticipant> templatePollingPaticipants = new ArrayList<>();
                for (int i = 0; i < busiTemplateParticipantArr.size(); i++)
                {
                    BusiMcuKdcTemplatePollingPaticipant busiTemplatePollingPaticipant = busiTemplateParticipantArr.getObject(i, BusiMcuKdcTemplatePollingPaticipant.class);
                    Object busiTemplateParticipantObj = busiTemplateParticipantArr.get(i);
                    if (busiTemplateParticipantObj instanceof Map) {
                        Map busiTemplateParticipantMap = (Map) busiTemplateParticipantObj;
                        if (busiTemplateParticipantMap.containsKey("downCascadeConferenceId")) {
                            String downCascadeConferenceId = (String) busiTemplateParticipantMap.get("downCascadeConferenceId");
                            ConferenceIdVo downCascadeConferenceIdVo = EncryptIdUtil.parasConferenceId(downCascadeConferenceId);
                            busiTemplatePollingPaticipant.setDownCascadeTemplateId(downCascadeConferenceIdVo.getId());
                            busiTemplatePollingPaticipant.setDownCascadeMcuType(downCascadeConferenceIdVo.getMcuType().getCode());
                        }
                    }
                    templatePollingPaticipants.add(busiTemplatePollingPaticipant);
                }

                // 部门顺序
                JSONArray templateDeptArr = jsonObj.getJSONArray("pollingDepts");
                List<BusiMcuKdcTemplatePollingDept> templatePollingDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++)
                {
                    templatePollingDepts.add(templateDeptArr.getObject(i, BusiMcuKdcTemplatePollingDept.class));
                }
                return toAjax(busiMcuKdcTemplatePollingSchemeService.insertBusiMcuKdcTemplatePollingScheme(templatePollingScheme, templatePollingDepts, templatePollingPaticipants));
            }
            case SMC3: {
                BusiMcuSmc3TemplatePollingScheme templatePollingScheme = jsonObj.getObject("pollingScheme", BusiMcuSmc3TemplatePollingScheme.class);
                if (templatePollingScheme == null)
                {
                    throw new SystemException(1110098, "轮询方案不能为空！");
                }
                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("pollingParticipants");
                List<BusiMcuSmc3TemplatePollingPaticipant> templatePollingPaticipants = new ArrayList<>();
                for (int i = 0; i < busiTemplateParticipantArr.size(); i++)
                {
                    templatePollingPaticipants.add(busiTemplateParticipantArr.getObject(i, BusiMcuSmc3TemplatePollingPaticipant.class));
                }

                // 部门顺序
                JSONArray templateDeptArr = jsonObj.getJSONArray("pollingDepts");
                List<BusiMcuSmc3TemplatePollingDept> templatePollingDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++)
                {
                    templatePollingDepts.add(templateDeptArr.getObject(i, BusiMcuSmc3TemplatePollingDept.class));
                }
                return toAjax(busiSmc3TemplatePollingSchemeService.insertBusiTemplatePollingScheme(templatePollingScheme, templatePollingDepts, templatePollingPaticipants));
            }
            case SMC2: {
                BusiMcuSmc2TemplatePollingScheme templatePollingScheme = jsonObj.getObject("pollingScheme", BusiMcuSmc2TemplatePollingScheme.class);
                if (templatePollingScheme == null)
                {
                    throw new SystemException(1110098, "轮询方案不能为空！");
                }
                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("pollingParticipants");
                List<BusiMcuSmc2TemplatePollingPaticipant> templatePollingPaticipants = new ArrayList<>();
                for (int i = 0; i < busiTemplateParticipantArr.size(); i++)
                {
                    templatePollingPaticipants.add(busiTemplateParticipantArr.getObject(i, BusiMcuSmc2TemplatePollingPaticipant.class));
                }

                // 部门顺序
                JSONArray templateDeptArr = jsonObj.getJSONArray("pollingDepts");
                List<BusiMcuSmc2TemplatePollingDept> templatePollingDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++)
                {
                    templatePollingDepts.add(templateDeptArr.getObject(i, BusiMcuSmc2TemplatePollingDept.class));
                }
                return toAjax(busiSmc2TemplatePollingSchemeService.insertBusiTemplatePollingScheme(templatePollingScheme, templatePollingDepts, templatePollingPaticipants));
            }
            case MCU_ZTE: {
                BusiMcuZteTemplatePollingScheme templatePollingScheme = jsonObj.getObject("pollingScheme", BusiMcuZteTemplatePollingScheme.class);
                if (templatePollingScheme == null)
                {
                    throw new SystemException(1110098, "轮询方案不能为空！");
                }
                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("pollingParticipants");
                List<BusiMcuZteTemplatePollingPaticipant> templatePollingPaticipants = new ArrayList<>();
                for (int i = 0; i < busiTemplateParticipantArr.size(); i++)
                {
                    BusiMcuZteTemplatePollingPaticipant busiTemplatePollingPaticipant = busiTemplateParticipantArr.getObject(i, BusiMcuZteTemplatePollingPaticipant.class);
                    Object busiTemplateParticipantObj = busiTemplateParticipantArr.get(i);
                    if (busiTemplateParticipantObj instanceof Map) {
                        Map busiTemplateParticipantMap = (Map) busiTemplateParticipantObj;
                        if (busiTemplateParticipantMap.containsKey("downCascadeConferenceId")) {
                            String downCascadeConferenceId = (String) busiTemplateParticipantMap.get("downCascadeConferenceId");
                            ConferenceIdVo downCascadeConferenceIdVo = EncryptIdUtil.parasConferenceId(downCascadeConferenceId);
                            busiTemplatePollingPaticipant.setDownCascadeTemplateId(downCascadeConferenceIdVo.getId());
                            busiTemplatePollingPaticipant.setDownCascadeMcuType(downCascadeConferenceIdVo.getMcuType().getCode());
                        }
                    }
                    templatePollingPaticipants.add(busiTemplatePollingPaticipant);
                }

                // 部门顺序
                JSONArray templateDeptArr = jsonObj.getJSONArray("pollingDepts");
                List<BusiMcuZteTemplatePollingDept> templatePollingDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++)
                {
                    templatePollingDepts.add(templateDeptArr.getObject(i, BusiMcuZteTemplatePollingDept.class));
                }
                return toAjax(busiMcuZteTemplatePollingSchemeService.insertBusiMcuZteTemplatePollingScheme(templatePollingScheme, templatePollingDepts, templatePollingPaticipants));
            }
        }
        return RestResponse.fail();
    }

    /**
     * 修改轮询方案
     */
    @Log(title = "轮询方案", businessType = BusinessType.UPDATE)
    @PutMapping("/{conferenceId}/{id}")
    @Operation(summary = "修改轮询方案", description = "修改轮询方案")
    public RestResponse edit(@RequestBody JSONObject jsonObj, @PathVariable("conferenceId") String conferenceId, @PathVariable("id") Long id)
    {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                BusiTemplatePollingScheme templatePollingScheme = jsonObj.getObject("pollingScheme", BusiTemplatePollingScheme.class);
                if (templatePollingScheme == null)
                {
                    throw new SystemException(1110098, "轮询方案不能为空！");
                }

                templatePollingScheme.setId(id);

                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("pollingParticipants");
                List<BusiTemplatePollingPaticipant> templatePollingPaticipants = new ArrayList<>();
                for (int i = 0; i < busiTemplateParticipantArr.size(); i++)
                {
                    BusiTemplatePollingPaticipant busiTemplatePollingPaticipant = busiTemplateParticipantArr.getObject(i, BusiTemplatePollingPaticipant.class);
                    Object busiTemplateParticipantObj = busiTemplateParticipantArr.get(i);
                    if (busiTemplateParticipantObj instanceof Map) {
                        Map busiTemplateParticipantMap = (Map) busiTemplateParticipantObj;
                        if (busiTemplateParticipantMap.containsKey("downCascadeConferenceId")) {
                            String downCascadeConferenceId = (String) busiTemplateParticipantMap.get("downCascadeConferenceId");
                            ConferenceIdVo downCascadeConferenceIdVo = EncryptIdUtil.parasConferenceId(downCascadeConferenceId);
                            busiTemplatePollingPaticipant.setDownCascadeTemplateId(downCascadeConferenceIdVo.getId());
                            busiTemplatePollingPaticipant.setDownCascadeMcuType(downCascadeConferenceIdVo.getMcuType().getCode());
                        }
                    }
                    templatePollingPaticipants.add(busiTemplatePollingPaticipant);
                }

                // 部门顺序
                JSONArray templateDeptArr = jsonObj.getJSONArray("pollingDepts");
                List<BusiTemplatePollingDept> templatePollingDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++)
                {
                    templatePollingDepts.add(templateDeptArr.getObject(i, BusiTemplatePollingDept.class));
                }
                return toAjax(busiTemplatePollingSchemeService.updateBusiTemplatePollingScheme(templatePollingScheme, templatePollingDepts, templatePollingPaticipants));
            }
            case MCU_ZJ: {
                BusiMcuZjTemplatePollingScheme templatePollingScheme = jsonObj.getObject("pollingScheme", BusiMcuZjTemplatePollingScheme.class);
                if (templatePollingScheme == null)
                {
                    throw new SystemException(1110098, "轮询方案不能为空！");
                }

                templatePollingScheme.setId(id);

                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("pollingParticipants");
                List<BusiMcuZjTemplatePollingPaticipant> templatePollingPaticipants = new ArrayList<>();
                for (int i = 0; i < busiTemplateParticipantArr.size(); i++)
                {
                    BusiMcuZjTemplatePollingPaticipant busiTemplatePollingPaticipant = busiTemplateParticipantArr.getObject(i, BusiMcuZjTemplatePollingPaticipant.class);
                    Object busiTemplateParticipantObj = busiTemplateParticipantArr.get(i);
                    if (busiTemplateParticipantObj instanceof Map) {
                        Map busiTemplateParticipantMap = (Map) busiTemplateParticipantObj;
                        if (busiTemplateParticipantMap.containsKey("downCascadeConferenceId")) {
                            String downCascadeConferenceId = (String) busiTemplateParticipantMap.get("downCascadeConferenceId");
                            ConferenceIdVo downCascadeConferenceIdVo = EncryptIdUtil.parasConferenceId(downCascadeConferenceId);
                            busiTemplatePollingPaticipant.setDownCascadeTemplateId(downCascadeConferenceIdVo.getId());
                            busiTemplatePollingPaticipant.setDownCascadeMcuType(downCascadeConferenceIdVo.getMcuType().getCode());
                        }
                    }
                    templatePollingPaticipants.add(busiTemplatePollingPaticipant);
                }

                // 部门顺序
                JSONArray templateDeptArr = jsonObj.getJSONArray("pollingDepts");
                List<BusiMcuZjTemplatePollingDept> templatePollingDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++)
                {
                    templatePollingDepts.add(templateDeptArr.getObject(i, BusiMcuZjTemplatePollingDept.class));
                }
                return toAjax(busiMcuZjTemplatePollingSchemeService.updateBusiMcuZjTemplatePollingScheme(templatePollingScheme, templatePollingDepts, templatePollingPaticipants));
            }
            case MCU_PLC: {
                BusiMcuPlcTemplatePollingScheme templatePollingScheme = jsonObj.getObject("pollingScheme", BusiMcuPlcTemplatePollingScheme.class);
                if (templatePollingScheme == null)
                {
                    throw new SystemException(1110098, "轮询方案不能为空！");
                }

                templatePollingScheme.setId(id);

                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("pollingParticipants");
                List<BusiMcuPlcTemplatePollingPaticipant> templatePollingPaticipants = new ArrayList<>();
                for (int i = 0; i < busiTemplateParticipantArr.size(); i++)
                {
                    BusiMcuPlcTemplatePollingPaticipant busiTemplatePollingPaticipant = busiTemplateParticipantArr.getObject(i, BusiMcuPlcTemplatePollingPaticipant.class);
                    Object busiTemplateParticipantObj = busiTemplateParticipantArr.get(i);
                    if (busiTemplateParticipantObj instanceof Map) {
                        Map busiTemplateParticipantMap = (Map) busiTemplateParticipantObj;
                        if (busiTemplateParticipantMap.containsKey("downCascadeConferenceId")) {
                            String downCascadeConferenceId = (String) busiTemplateParticipantMap.get("downCascadeConferenceId");
                            ConferenceIdVo downCascadeConferenceIdVo = EncryptIdUtil.parasConferenceId(downCascadeConferenceId);
                            busiTemplatePollingPaticipant.setDownCascadeTemplateId(downCascadeConferenceIdVo.getId());
                            busiTemplatePollingPaticipant.setDownCascadeMcuType(downCascadeConferenceIdVo.getMcuType().getCode());
                        }
                    }
                    templatePollingPaticipants.add(busiTemplatePollingPaticipant);
                }

                // 部门顺序
                JSONArray templateDeptArr = jsonObj.getJSONArray("pollingDepts");
                List<BusiMcuPlcTemplatePollingDept> templatePollingDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++)
                {
                    templatePollingDepts.add(templateDeptArr.getObject(i, BusiMcuPlcTemplatePollingDept.class));
                }
                return toAjax(busiMcuPlcTemplatePollingSchemeService.updateBusiMcuPlcTemplatePollingScheme(templatePollingScheme, templatePollingDepts, templatePollingPaticipants));
            }
            case MCU_KDC: {
                BusiMcuKdcTemplatePollingScheme templatePollingScheme = jsonObj.getObject("pollingScheme", BusiMcuKdcTemplatePollingScheme.class);
                if (templatePollingScheme == null)
                {
                    throw new SystemException(1110098, "轮询方案不能为空！");
                }

                templatePollingScheme.setId(id);

                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("pollingParticipants");
                List<BusiMcuKdcTemplatePollingPaticipant> templatePollingPaticipants = new ArrayList<>();
                for (int i = 0; i < busiTemplateParticipantArr.size(); i++)
                {
                    BusiMcuKdcTemplatePollingPaticipant busiTemplatePollingPaticipant = busiTemplateParticipantArr.getObject(i, BusiMcuKdcTemplatePollingPaticipant.class);
                    Object busiTemplateParticipantObj = busiTemplateParticipantArr.get(i);
                    if (busiTemplateParticipantObj instanceof Map) {
                        Map busiTemplateParticipantMap = (Map) busiTemplateParticipantObj;
                        if (busiTemplateParticipantMap.containsKey("downCascadeConferenceId")) {
                            String downCascadeConferenceId = (String) busiTemplateParticipantMap.get("downCascadeConferenceId");
                            ConferenceIdVo downCascadeConferenceIdVo = EncryptIdUtil.parasConferenceId(downCascadeConferenceId);
                            busiTemplatePollingPaticipant.setDownCascadeTemplateId(downCascadeConferenceIdVo.getId());
                            busiTemplatePollingPaticipant.setDownCascadeMcuType(downCascadeConferenceIdVo.getMcuType().getCode());
                        }
                    }
                    templatePollingPaticipants.add(busiTemplatePollingPaticipant);
                }

                // 部门顺序
                JSONArray templateDeptArr = jsonObj.getJSONArray("pollingDepts");
                List<BusiMcuKdcTemplatePollingDept> templatePollingDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++)
                {
                    templatePollingDepts.add(templateDeptArr.getObject(i, BusiMcuKdcTemplatePollingDept.class));
                }
                return toAjax(busiMcuKdcTemplatePollingSchemeService.updateBusiMcuKdcTemplatePollingScheme(templatePollingScheme, templatePollingDepts, templatePollingPaticipants));
            }
            case SMC3: {
                BusiMcuSmc3TemplatePollingScheme templatePollingScheme = jsonObj.getObject("pollingScheme", BusiMcuSmc3TemplatePollingScheme.class);
                if (templatePollingScheme == null)
                {
                    throw new SystemException(1110098, "轮询方案不能为空！");
                }
                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("pollingParticipants");
                List<BusiMcuSmc3TemplatePollingPaticipant> templatePollingPaticipants = new ArrayList<>();
                for (int i = 0; i < busiTemplateParticipantArr.size(); i++)
                {
                    templatePollingPaticipants.add(busiTemplateParticipantArr.getObject(i, BusiMcuSmc3TemplatePollingPaticipant.class));
                }

                // 部门顺序
                JSONArray templateDeptArr = jsonObj.getJSONArray("pollingDepts");
                List<BusiMcuSmc3TemplatePollingDept> templatePollingDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++)
                {
                    templatePollingDepts.add(templateDeptArr.getObject(i, BusiMcuSmc3TemplatePollingDept.class));
                }
                return toAjax(busiSmc3TemplatePollingSchemeService.updateBusiTemplatePollingScheme(templatePollingScheme, templatePollingDepts, templatePollingPaticipants));
            }
            case SMC2: {
                BusiMcuSmc2TemplatePollingScheme templatePollingScheme = jsonObj.getObject("pollingScheme", BusiMcuSmc2TemplatePollingScheme.class);
                if (templatePollingScheme == null)
                {
                    throw new SystemException(1110098, "轮询方案不能为空！");
                }
                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("pollingParticipants");
                List<BusiMcuSmc2TemplatePollingPaticipant> templatePollingPaticipants = new ArrayList<>();
                for (int i = 0; i < busiTemplateParticipantArr.size(); i++)
                {
                    templatePollingPaticipants.add(busiTemplateParticipantArr.getObject(i, BusiMcuSmc2TemplatePollingPaticipant.class));
                }

                // 部门顺序
                JSONArray templateDeptArr = jsonObj.getJSONArray("pollingDepts");
                List<BusiMcuSmc2TemplatePollingDept> templatePollingDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++)
                {
                    templatePollingDepts.add(templateDeptArr.getObject(i, BusiMcuSmc2TemplatePollingDept.class));
                }
                return toAjax(busiSmc2TemplatePollingSchemeService.updateBusiTemplatePollingScheme(templatePollingScheme, templatePollingDepts, templatePollingPaticipants));
            }
            case MCU_ZTE: {
                BusiMcuZteTemplatePollingScheme templatePollingScheme = jsonObj.getObject("pollingScheme", BusiMcuZteTemplatePollingScheme.class);
                if (templatePollingScheme == null)
                {
                    throw new SystemException(1110098, "轮询方案不能为空！");
                }

                templatePollingScheme.setId(id);

                JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("pollingParticipants");
                List<BusiMcuZteTemplatePollingPaticipant> templatePollingPaticipants = new ArrayList<>();
                for (int i = 0; i < busiTemplateParticipantArr.size(); i++)
                {
                    BusiMcuZteTemplatePollingPaticipant busiTemplatePollingPaticipant = busiTemplateParticipantArr.getObject(i, BusiMcuZteTemplatePollingPaticipant.class);
                    Object busiTemplateParticipantObj = busiTemplateParticipantArr.get(i);
                    if (busiTemplateParticipantObj instanceof Map) {
                        Map busiTemplateParticipantMap = (Map) busiTemplateParticipantObj;
                        if (busiTemplateParticipantMap.containsKey("downCascadeConferenceId")) {
                            String downCascadeConferenceId = (String) busiTemplateParticipantMap.get("downCascadeConferenceId");
                            ConferenceIdVo downCascadeConferenceIdVo = EncryptIdUtil.parasConferenceId(downCascadeConferenceId);
                            busiTemplatePollingPaticipant.setDownCascadeTemplateId(downCascadeConferenceIdVo.getId());
                            busiTemplatePollingPaticipant.setDownCascadeMcuType(downCascadeConferenceIdVo.getMcuType().getCode());
                        }
                    }
                    templatePollingPaticipants.add(busiTemplatePollingPaticipant);
                }

                // 部门顺序
                JSONArray templateDeptArr = jsonObj.getJSONArray("pollingDepts");
                List<BusiMcuZteTemplatePollingDept> templatePollingDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++)
                {
                    templatePollingDepts.add(templateDeptArr.getObject(i, BusiMcuZteTemplatePollingDept.class));
                }
                return toAjax(busiMcuZteTemplatePollingSchemeService.updateBusiMcuZteTemplatePollingScheme(templatePollingScheme, templatePollingDepts, templatePollingPaticipants));
            }

        }
        return RestResponse.fail();
    }
    
    /**
     * 修改轮询方案
     */
    @Log(title = "轮询方案", businessType = BusinessType.UPDATE)
    @PutMapping("/edit/{conferenceId}")
    @Operation(summary = "修改轮询方案", description = "修改轮询方案")
    public RestResponse editList(@RequestBody JSONArray jsonObj, @PathVariable("conferenceId") String conferenceId)
    {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                List<BusiTemplatePollingScheme> templatePollingSchemes = jsonObj.toJavaObject(new TypeReference<List<BusiTemplatePollingScheme>>() {});
                busiTemplatePollingSchemeService.updateBusiTemplatePollingSchemes(templatePollingSchemes);
                break;
            }
            case MCU_ZJ: {
                List<BusiMcuZjTemplatePollingScheme> templatePollingSchemes = jsonObj.toJavaObject(new TypeReference<List<BusiMcuZjTemplatePollingScheme>>() {});
                busiMcuZjTemplatePollingSchemeService.updateBusiMcuZjTemplatePollingSchemes(templatePollingSchemes);
                break;
            }
            case MCU_PLC: {
                List<BusiMcuPlcTemplatePollingScheme> templatePollingSchemes = jsonObj.toJavaObject(new TypeReference<List<BusiMcuPlcTemplatePollingScheme>>() {});
                busiMcuPlcTemplatePollingSchemeService.updateBusiMcuPlcTemplatePollingSchemes(templatePollingSchemes);
                break;
            }
            case MCU_KDC: {
                List<BusiMcuKdcTemplatePollingScheme> templatePollingSchemes = jsonObj.toJavaObject(new TypeReference<List<BusiMcuKdcTemplatePollingScheme>>() {});
                busiMcuKdcTemplatePollingSchemeService.updateBusiMcuKdcTemplatePollingSchemes(templatePollingSchemes);
                break;
            }
            case SMC3: {
                List<BusiMcuSmc3TemplatePollingScheme> templatePollingSchemes = jsonObj.toJavaObject(new TypeReference<List<BusiMcuSmc3TemplatePollingScheme>>() {});
                busiSmc3TemplatePollingSchemeService.updateBusiTemplatePollingSchemes(templatePollingSchemes);
                break;
            }
            case SMC2: {
                List<BusiMcuSmc2TemplatePollingScheme> templatePollingSchemes = jsonObj.toJavaObject(new TypeReference<List<BusiMcuSmc2TemplatePollingScheme>>() {});
                busiSmc2TemplatePollingSchemeService.updateBusiTemplatePollingSchemes(templatePollingSchemes);
                break;
            }
            case MCU_ZTE: {
                List<BusiMcuZteTemplatePollingScheme> templatePollingSchemes = jsonObj.toJavaObject(new TypeReference<List<BusiMcuZteTemplatePollingScheme>>() {});
                busiMcuZteTemplatePollingSchemeService.updateBusiMcuZteTemplatePollingSchemes(templatePollingSchemes);
                break;
            }
        }
        return RestResponse.success();
    }

    /**
     * 删除轮询方案
     */
    @Log(title = "轮询方案", businessType = BusinessType.DELETE)
	@DeleteMapping("/{conferenceId}/{id}")
	@Operation(summary = "删除轮询方案", description = "删除轮询方案")
    public RestResponse remove(@PathVariable("conferenceId") String conferenceId, @PathVariable("id") Long id)
    {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                return toAjax(busiTemplatePollingSchemeService.deleteBusiTemplatePollingSchemeById(id));
            }
            case MCU_ZJ: {
                return toAjax(busiMcuZjTemplatePollingSchemeService.deleteBusiMcuZjTemplatePollingSchemeById(id));
            }
            case MCU_PLC: {
                return toAjax(busiMcuPlcTemplatePollingSchemeService.deleteBusiMcuPlcTemplatePollingSchemeById(id));
            }
            case MCU_KDC: {
                return toAjax(busiMcuKdcTemplatePollingSchemeService.deleteBusiMcuKdcTemplatePollingSchemeById(id));
            }
            case SMC3: {
                return toAjax(busiSmc3TemplatePollingSchemeService.deleteBusiTemplatePollingSchemeById(id));
            }
            case SMC2: {
                return toAjax(busiSmc2TemplatePollingSchemeService.deleteBusiTemplatePollingSchemeById(id));
            }
            case MCU_ZTE: {
                return toAjax(busiMcuZteTemplatePollingSchemeService.deleteBusiMcuZteTemplatePollingSchemeById(id));
            }
        }
        return RestResponse.fail();
    }
}
