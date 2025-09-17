package com.paradisecloud.fcm.web.controller.mcu.all;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.common.utils.bean.BeanUtils;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.EncryptIdVo;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.ding.service2.interfaces.IBusiMcuDingDeptService;
import com.paradisecloud.fcm.fme.service.interfaces.IBusiFmeDeptService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiMcuHwcloudDeptService;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcDeptService;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcDeptService;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjDeptService;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2Dept;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiMcuSmc2DeptService;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3Dept;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentDept;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiMcuTencentDeptService;
import com.paradisecloud.fcm.zte.service.interfaces.IBusiMcuZteDeptService;
import com.paradisecloud.smc3.service.interfaces.IBusiMcuSmc3DeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.*;

/**
 * MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）Controller
 * 
 * @author lilinhai
 * @date 2021-01-28
 */
@RestController
@RequestMapping("/busi/mcu/all/dept")
@Tag(name = "租户-MCU映射表（一个MCU/MCU集群可以分配给多个租户，一对多）")
public class BusiAllDeptController extends BaseController
{

    @Resource
    private IBusiFmeDeptService busiFmeDeptService;
    @Resource
    private IBusiMcuZjDeptService busiMcuZjDeptService;
    @Resource
    private IBusiMcuPlcDeptService busiMcuPlcDeptService;
    @Resource
    private IBusiMcuKdcDeptService busiMcuKdcDeptService;
    @Resource
    private IBusiMcuSmc3DeptService busiMcuSmc3DeptService;
    @Resource
    private IBusiMcuSmc2DeptService busiMcuSmc2DeptService;
    @Resource
    private IBusiMcuTencentDeptService busiMcuTencentDeptService;
    @Resource
    private IBusiMcuDingDeptService busiMcuDingDeptService;
    @Resource
    private IBusiMcuHwcloudDeptService busiMcuHwcloudDeptService;
    @Resource
    private IBusiMcuZteDeptService busiMcuZteDeptService;
    /**
     * 查询MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询租户--MCU集群分配记录")
    public RestResponse list(BusiFmeDept busiFmeDept)
    {
        TreeMap<Long, ModelBean> treeMap = new TreeMap<>();
        // fme
        {
            McuType mcuType = McuType.FME;
            BusiFmeDept busiMcuDeptCon = new BusiFmeDept();
            BeanUtils.copyBeanProp(busiMcuDeptCon, busiFmeDept);
            List<ModelBean> busiMcuDeptList = busiFmeDeptService.selectBusiFmeDeptList(busiMcuDeptCon);
            for (ModelBean modelBean : busiMcuDeptList) {
                Long deptId = (Long) modelBean.get("deptId");
                String deptName = (String) modelBean.get("deptName");
                Long id = (Long) modelBean.get("id");
                Integer mcuTypeAc = (Integer) modelBean.get("fmeType");
                Long mcuId = (Long) modelBean.get("fmeId");
                String mcuDeptId = EncryptIdUtil.generateEncryptId(id, McuType.FME.getCode());
                ModelBean modelBeanDept = treeMap.get(deptId);
                ModelBean modelBeanMcu = modelBean;
                modelBeanMcu.remove("deptId");
                modelBeanMcu.remove("deptName");
                modelBeanMcu.remove("fmeType");
                modelBeanMcu.remove("fmeId");
                modelBeanMcu.put("mcuTypeAc", mcuTypeAc);
                modelBeanMcu.put("mcuId", mcuId);
                modelBeanMcu.put("mcuType", mcuType.getCode());
                modelBeanMcu.put("mcuTypeAlias", mcuType.getAlias());
                modelBeanMcu.put("mcuDeptId", mcuDeptId);
                List<ModelBean> modelBeanMcuList;
                if (modelBeanDept == null) {
                    modelBeanDept = new ModelBean();
                    treeMap.put(deptId, modelBeanDept);
                    modelBeanDept.put("id", id);
                    modelBeanDept.put("deptId", deptId);
                    modelBeanDept.put("deptName", deptName);
                    modelBeanMcuList = new ArrayList<>();
                    modelBeanDept.put("mcuList", modelBeanMcuList);
                } else {
                    modelBeanMcuList = (List<ModelBean>) modelBeanDept.get("mcuList");
                }
                modelBeanMcuList.add(modelBeanMcu);
            }
        }
        // mcu-zj
        {
            McuType mcuType = McuType.MCU_ZJ;
            BusiMcuZjDept busiMcuDeptCon = new BusiMcuZjDept();
            BeanUtils.copyBeanProp(busiMcuDeptCon, busiFmeDept);
            List<ModelBean> busiMcuDeptList = busiMcuZjDeptService.selectBusiMcuZjDeptList(busiMcuDeptCon);
            for (ModelBean modelBean : busiMcuDeptList) {
                Long deptId = (Long) modelBean.get("deptId");
                String deptName = (String) modelBean.get("deptName");
                Long id = (Long) modelBean.get("id");
                Integer mcuTypeAc = (Integer) modelBean.get("mcuType");
                String mcuDeptId = EncryptIdUtil.generateEncryptId(id, McuType.MCU_ZJ.getCode());
                ModelBean modelBeanDept = treeMap.get(deptId);
                ModelBean modelBeanMcu = modelBean;
                modelBeanMcu.remove("deptId");
                modelBeanMcu.remove("deptName");
                modelBeanMcu.put("mcuTypeAc", mcuTypeAc);
                modelBeanMcu.put("mcuType", mcuType.getCode());
                modelBeanMcu.put("mcuTypeAlias", mcuType.getAlias());
                modelBeanMcu.put("mcuDeptId", mcuDeptId);
                List<ModelBean> modelBeanMcuList;
                if (modelBeanDept == null) {
                    modelBeanDept = new ModelBean();
                    treeMap.put(deptId, modelBeanDept);
                    modelBeanDept.put("id", id);
                    modelBeanDept.put("deptId", deptId);
                    modelBeanDept.put("deptName", deptName);
                    modelBeanMcuList = new ArrayList<>();
                    modelBeanDept.put("mcuList", modelBeanMcuList);
                } else {
                    modelBeanMcuList = (List<ModelBean>) modelBeanDept.get("mcuList");
                }
                modelBeanMcuList.add(modelBeanMcu);
            }
        }
        // mcu-plc
        {
            McuType mcuType = McuType.MCU_PLC;
            BusiMcuPlcDept busiMcuDeptCon = new BusiMcuPlcDept();
            BeanUtils.copyBeanProp(busiMcuDeptCon, busiFmeDept);
            List<ModelBean> busiMcuDeptList = busiMcuPlcDeptService.selectBusiMcuPlcDeptList(busiMcuDeptCon);
            for (ModelBean modelBean : busiMcuDeptList) {
                Long deptId = (Long) modelBean.get("deptId");
                String deptName = (String) modelBean.get("deptName");
                Long id = (Long) modelBean.get("id");
                Integer mcuTypeAc = (Integer) modelBean.get("mcuType");
                String mcuDeptId = EncryptIdUtil.generateEncryptId(id, McuType.MCU_PLC.getCode());
                ModelBean modelBeanDept = treeMap.get(deptId);
                ModelBean modelBeanMcu = modelBean;
                modelBeanMcu.remove("deptId");
                modelBeanMcu.remove("deptName");
                modelBeanMcu.put("mcuTypeAc", mcuTypeAc);
                modelBeanMcu.put("mcuType", mcuType.getCode());
                modelBeanMcu.put("mcuTypeAlias", mcuType.getAlias());
                modelBeanMcu.put("mcuDeptId", mcuDeptId);
                List<ModelBean> modelBeanMcuList;
                if (modelBeanDept == null) {
                    modelBeanDept = new ModelBean();
                    treeMap.put(deptId, modelBeanDept);
                    modelBeanDept.put("id", id);
                    modelBeanDept.put("deptId", deptId);
                    modelBeanDept.put("deptName", deptName);
                    modelBeanMcuList = new ArrayList<>();
                    modelBeanDept.put("mcuList", modelBeanMcuList);
                } else {
                    modelBeanMcuList = (List<ModelBean>) modelBeanDept.get("mcuList");
                }
                modelBeanMcuList.add(modelBeanMcu);
            }
        }
        // mcu-kdc
        {
            McuType mcuType = McuType.MCU_KDC;
            BusiMcuKdcDept busiMcuDeptCon = new BusiMcuKdcDept();
            BeanUtils.copyBeanProp(busiMcuDeptCon, busiFmeDept);
            List<ModelBean> busiMcuDeptList = busiMcuKdcDeptService.selectBusiMcuKdcDeptList(busiMcuDeptCon);
            for (ModelBean modelBean : busiMcuDeptList) {
                Long deptId = (Long) modelBean.get("deptId");
                String deptName = (String) modelBean.get("deptName");
                Long id = (Long) modelBean.get("id");
                Integer mcuTypeAc = (Integer) modelBean.get("mcuType");
                String mcuDeptId = EncryptIdUtil.generateEncryptId(id, McuType.MCU_KDC.getCode());
                ModelBean modelBeanDept = treeMap.get(deptId);
                ModelBean modelBeanMcu = modelBean;
                modelBeanMcu.remove("deptId");
                modelBeanMcu.remove("deptName");
                modelBeanMcu.put("mcuTypeAc", mcuTypeAc);
                modelBeanMcu.put("mcuType", mcuType.getCode());
                modelBeanMcu.put("mcuTypeAlias", mcuType.getAlias());
                modelBeanMcu.put("mcuDeptId", mcuDeptId);
                List<ModelBean> modelBeanMcuList;
                if (modelBeanDept == null) {
                    modelBeanDept = new ModelBean();
                    treeMap.put(deptId, modelBeanDept);
                    modelBeanDept.put("id", id);
                    modelBeanDept.put("deptId", deptId);
                    modelBeanDept.put("deptName", deptName);
                    modelBeanMcuList = new ArrayList<>();
                    modelBeanDept.put("mcuList", modelBeanMcuList);
                } else {
                    modelBeanMcuList = (List<ModelBean>) modelBeanDept.get("mcuList");
                }
                modelBeanMcuList.add(modelBeanMcu);
            }
        }
        // smc3
        {
            McuType mcuType = McuType.SMC3;
            BusiMcuSmc3Dept busiMcuDeptCon = new BusiMcuSmc3Dept();
            BeanUtils.copyBeanProp(busiMcuDeptCon, busiFmeDept);
            List<ModelBean> busiMcuDeptList = busiMcuSmc3DeptService.selectBusiMcuSmc3DeptList(busiMcuDeptCon);
            for (ModelBean modelBean : busiMcuDeptList) {
                Long deptId = (Long) modelBean.get("deptId");
                String deptName = (String) modelBean.get("deptName");
                Long id = (Long) modelBean.get("id");
                Integer mcuTypeAc = (Integer) modelBean.get("mcuType");
                String mcuDeptId = EncryptIdUtil.generateEncryptId(id, McuType.SMC3.getCode());
                ModelBean modelBeanDept = treeMap.get(deptId);
                ModelBean modelBeanMcu = modelBean;
                modelBeanMcu.remove("deptId");
                modelBeanMcu.remove("deptName");
                modelBeanMcu.put("mcuTypeAc", mcuTypeAc);
                modelBeanMcu.put("mcuType", mcuType.getCode());
                modelBeanMcu.put("mcuTypeAlias", mcuType.getAlias());
                modelBeanMcu.put("mcuDeptId", mcuDeptId);
                List<ModelBean> modelBeanMcuList;
                if (modelBeanDept == null) {
                    modelBeanDept = new ModelBean();
                    treeMap.put(deptId, modelBeanDept);
                    modelBeanDept.put("id", id);
                    modelBeanDept.put("deptId", deptId);
                    modelBeanDept.put("deptName", deptName);
                    modelBeanMcuList = new ArrayList<>();
                    modelBeanDept.put("mcuList", modelBeanMcuList);
                } else {
                    modelBeanMcuList = (List<ModelBean>) modelBeanDept.get("mcuList");
                }
                modelBeanMcuList.add(modelBeanMcu);
            }
        }
        // SMC2
        {
            McuType mcuType = McuType.SMC2;
            BusiMcuSmc2Dept busiMcuDeptCon = new BusiMcuSmc2Dept();
            BeanUtils.copyBeanProp(busiMcuDeptCon, busiFmeDept);
            List<ModelBean> busiMcuDeptList = busiMcuSmc2DeptService.selectBusiMcuSmc2DeptList(busiMcuDeptCon);
            for (ModelBean modelBean : busiMcuDeptList) {
                Long deptId = (Long) modelBean.get("deptId");
                String deptName = (String) modelBean.get("deptName");
                Long id = (Long) modelBean.get("id");
                Integer mcuTypeAc = (Integer) modelBean.get("mcuType");
                String mcuDeptId = EncryptIdUtil.generateEncryptId(id, McuType.SMC2.getCode());
                ModelBean modelBeanDept = treeMap.get(deptId);
                ModelBean modelBeanMcu = modelBean;
                modelBeanMcu.remove("deptId");
                modelBeanMcu.remove("deptName");
                modelBeanMcu.put("mcuTypeAc", mcuTypeAc);
                modelBeanMcu.put("mcuType", mcuType.getCode());
                modelBeanMcu.put("mcuTypeAlias", mcuType.getAlias());
                modelBeanMcu.put("mcuDeptId", mcuDeptId);
                List<ModelBean> modelBeanMcuList;
                if (modelBeanDept == null) {
                    modelBeanDept = new ModelBean();
                    treeMap.put(deptId, modelBeanDept);
                    modelBeanDept.put("id", id);
                    modelBeanDept.put("deptId", deptId);
                    modelBeanDept.put("deptName", deptName);
                    modelBeanMcuList = new ArrayList<>();
                    modelBeanDept.put("mcuList", modelBeanMcuList);
                } else {
                    modelBeanMcuList = (List<ModelBean>) modelBeanDept.get("mcuList");
                }
                modelBeanMcuList.add(modelBeanMcu);
            }
        }
        // tencent
        {
            McuType mcuType = McuType.MCU_TENCENT;
            BusiMcuTencentDept busiMcuDeptCon = new BusiMcuTencentDept();
            BeanUtils.copyBeanProp(busiMcuDeptCon, busiFmeDept);
            List<ModelBean> busiMcuDeptList = busiMcuTencentDeptService.selectBusiMcuTencentDeptList(busiMcuDeptCon);
            for (ModelBean modelBean : busiMcuDeptList) {
                Long deptId = (Long) modelBean.get("deptId");
                String deptName = (String) modelBean.get("deptName");
                Long id = (Long) modelBean.get("id");
                Integer mcuTypeAc = (Integer) modelBean.get("mcuType");
                String mcuDeptId = EncryptIdUtil.generateEncryptId(id, McuType.MCU_TENCENT.getCode());
                ModelBean modelBeanDept = treeMap.get(deptId);
                ModelBean modelBeanMcu = modelBean;
                modelBeanMcu.remove("deptId");
                modelBeanMcu.remove("deptName");
                modelBeanMcu.put("mcuTypeAc", mcuTypeAc);
                modelBeanMcu.put("mcuType", mcuType.getCode());
                modelBeanMcu.put("mcuTypeAlias", mcuType.getAlias());
                modelBeanMcu.put("mcuDeptId", mcuDeptId);
                List<ModelBean> modelBeanMcuList;
                if (modelBeanDept == null) {
                    modelBeanDept = new ModelBean();
                    treeMap.put(deptId, modelBeanDept);
                    modelBeanDept.put("id", id);
                    modelBeanDept.put("deptId", deptId);
                    modelBeanDept.put("deptName", deptName);
                    modelBeanMcuList = new ArrayList<>();
                    modelBeanDept.put("mcuList", modelBeanMcuList);
                } else {
                    modelBeanMcuList = (List<ModelBean>) modelBeanDept.get("mcuList");
                }
                modelBeanMcuList.add(modelBeanMcu);
            }
        }
        // dingding
        {
            McuType mcuType = McuType.MCU_DING;
            BusiMcuDingDept busiMcuDeptCon = new BusiMcuDingDept();
            BeanUtils.copyBeanProp(busiMcuDeptCon, busiFmeDept);
            List<ModelBean> busiMcuDeptList = busiMcuDingDeptService.selectBusiMcuDingDeptList(busiMcuDeptCon);
            for (ModelBean modelBean : busiMcuDeptList) {
                Long deptId = (Long) modelBean.get("deptId");
                String deptName = (String) modelBean.get("deptName");
                Long id = (Long) modelBean.get("id");
                Integer mcuTypeAc = (Integer) modelBean.get("mcuType");
                String mcuDeptId = EncryptIdUtil.generateEncryptId(id, McuType.MCU_DING.getCode());
                ModelBean modelBeanDept = treeMap.get(deptId);
                ModelBean modelBeanMcu = modelBean;
                modelBeanMcu.remove("deptId");
                modelBeanMcu.remove("deptName");
                modelBeanMcu.put("mcuTypeAc", mcuTypeAc);
                modelBeanMcu.put("mcuType", mcuType.getCode());
                modelBeanMcu.put("mcuTypeAlias", mcuType.getAlias());
                modelBeanMcu.put("mcuDeptId", mcuDeptId);
                List<ModelBean> modelBeanMcuList;
                if (modelBeanDept == null) {
                    modelBeanDept = new ModelBean();
                    treeMap.put(deptId, modelBeanDept);
                    modelBeanDept.put("id", id);
                    modelBeanDept.put("deptId", deptId);
                    modelBeanDept.put("deptName", deptName);
                    modelBeanMcuList = new ArrayList<>();
                    modelBeanDept.put("mcuList", modelBeanMcuList);
                } else {
                    modelBeanMcuList = (List<ModelBean>) modelBeanDept.get("mcuList");
                }
                modelBeanMcuList.add(modelBeanMcu);
            }
        }
        // 华为云
        {
            McuType mcuType = McuType.MCU_HWCLOUD;
            BusiMcuHwcloudDept busiMcuDeptCon = new BusiMcuHwcloudDept();
            BeanUtils.copyBeanProp(busiMcuDeptCon, busiFmeDept);
            List<ModelBean> busiMcuDeptList = busiMcuHwcloudDeptService.selectBusiMcuHwcloudDeptList(busiMcuDeptCon);
            for (ModelBean modelBean : busiMcuDeptList) {
                Long deptId = (Long) modelBean.get("deptId");
                String deptName = (String) modelBean.get("deptName");
                Long id = (Long) modelBean.get("id");
                Integer mcuTypeAc = (Integer) modelBean.get("mcuType");
                String mcuDeptId = EncryptIdUtil.generateEncryptId(id, McuType.MCU_HWCLOUD.getCode());
                ModelBean modelBeanDept = treeMap.get(deptId);
                ModelBean modelBeanMcu = modelBean;
                modelBeanMcu.remove("deptId");
                modelBeanMcu.remove("deptName");
                modelBeanMcu.put("mcuTypeAc", mcuTypeAc);
                modelBeanMcu.put("mcuType", mcuType.getCode());
                modelBeanMcu.put("mcuTypeAlias", mcuType.getAlias());
                modelBeanMcu.put("mcuDeptId", mcuDeptId);
                List<ModelBean> modelBeanMcuList;
                if (modelBeanDept == null) {
                    modelBeanDept = new ModelBean();
                    treeMap.put(deptId, modelBeanDept);
                    modelBeanDept.put("id", id);
                    modelBeanDept.put("deptId", deptId);
                    modelBeanDept.put("deptName", deptName);
                    modelBeanMcuList = new ArrayList<>();
                    modelBeanDept.put("mcuList", modelBeanMcuList);
                } else {
                    modelBeanMcuList = (List<ModelBean>) modelBeanDept.get("mcuList");
                }
                modelBeanMcuList.add(modelBeanMcu);
            }
        }
        // mcu-zte
        {
            McuType mcuType = McuType.MCU_ZTE;
            BusiMcuZteDept busiMcuDeptCon = new BusiMcuZteDept();
            BeanUtils.copyBeanProp(busiMcuDeptCon, busiFmeDept);
            List<ModelBean> busiMcuDeptList = busiMcuZteDeptService.selectBusiMcuZteDeptList(busiMcuDeptCon);
            for (ModelBean modelBean : busiMcuDeptList) {
                Long deptId = (Long) modelBean.get("deptId");
                String deptName = (String) modelBean.get("deptName");
                Long id = (Long) modelBean.get("id");
                Integer mcuTypeAc = (Integer) modelBean.get("mcuType");
                String mcuDeptId = EncryptIdUtil.generateEncryptId(id, McuType.MCU_ZTE.getCode());
                ModelBean modelBeanDept = treeMap.get(deptId);
                ModelBean modelBeanMcu = modelBean;
                modelBeanMcu.remove("deptId");
                modelBeanMcu.remove("deptName");
                modelBeanMcu.put("mcuTypeAc", mcuTypeAc);
                modelBeanMcu.put("mcuType", mcuType.getCode());
                modelBeanMcu.put("mcuTypeAlias", mcuType.getAlias());
                modelBeanMcu.put("mcuDeptId", mcuDeptId);
                List<ModelBean> modelBeanMcuList;
                if (modelBeanDept == null) {
                    modelBeanDept = new ModelBean();
                    treeMap.put(deptId, modelBeanDept);
                    modelBeanDept.put("id", id);
                    modelBeanDept.put("deptId", deptId);
                    modelBeanDept.put("deptName", deptName);
                    modelBeanMcuList = new ArrayList<>();
                    modelBeanDept.put("mcuList", modelBeanMcuList);
                } else {
                    modelBeanMcuList = (List<ModelBean>) modelBeanDept.get("mcuList");
                }
                modelBeanMcuList.add(modelBeanMcu);
            }
        }

        SortedMap<Long, ModelBean> sortedMap = treeMap.tailMap(0l);
        return RestResponse.success(sortedMap.values());
    }

    /**
     * 获取MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）详细信息
     */
    @GetMapping(value = "/{mcuDeptId}")
    @Operation(summary = "获取租户--MCU集群分配记录详细信息")
    public RestResponse getInfo(@PathVariable("mcuDeptId") String mcuDeptId)
    {
        EncryptIdVo encryptIdVo = EncryptIdUtil.parasEncryptId(mcuDeptId);
        Long id = encryptIdVo.getId();
        McuType mcuType = encryptIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                ModelBean modelBean = busiFmeDeptService.selectBusiFmeDeptById(id);
                Long mcuId = (Long) modelBean.get("fmeId");
                Integer mcuTypeAc = (Integer) modelBean.get("fmeType");
                modelBean.put("mcuId", mcuId);
                modelBean.put("mcuTypeAc", mcuTypeAc);
                return RestResponse.success(modelBean);
            }
            case MCU_ZJ: {
                ModelBean modelBean = busiMcuZjDeptService.selectBusiMcuZjDeptById(id);
                return RestResponse.success(modelBean);
            }
            case MCU_PLC: {
                ModelBean modelBean = busiMcuPlcDeptService.selectBusiMcuPlcDeptById(id);
                return RestResponse.success(modelBean);
            }
            case MCU_KDC: {
                ModelBean modelBean = busiMcuKdcDeptService.selectBusiMcuKdcDeptById(id);
                return RestResponse.success(modelBean);
            }
            case SMC3: {
                ModelBean modelBean = busiMcuSmc3DeptService.selectBusiMcuSmc3DeptByDeptId(id);
                return RestResponse.success(modelBean);
            }
            case SMC2: {
                ModelBean modelBean = busiMcuSmc2DeptService.selectBusiMcuSmc2DeptByDeptId(id);
                return RestResponse.success(modelBean);
            }
            case MCU_TENCENT: {
                ModelBean modelBean = busiMcuTencentDeptService.selectBusiMcuTencentDeptByDeptId(id);
                return RestResponse.success(modelBean);
            }
            case MCU_DING: {
                ModelBean modelBean = busiMcuDingDeptService.selectBusiMcuDingDeptByDeptId(id);
                return RestResponse.success(modelBean);
            }
            case MCU_HWCLOUD: {
                ModelBean modelBean = busiMcuHwcloudDeptService.selectBusiMcuHwcloudDeptByDeptId(id);
                return RestResponse.success(modelBean);
            }
            case MCU_ZTE: {
                ModelBean modelBean = busiMcuZteDeptService.selectBusiMcuZteDeptById(id);
                return RestResponse.success(modelBean);
            }

        }
        return RestResponse.fail();
    }

    /**
     * 新增MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
    @PostMapping("")
    @Operation(summary = "新增租户--MCU集群分配记录", description = "绑定MCU")
    public RestResponse add(@RequestBody JSONObject jsonObject)
    {
        Integer mcuTypeAc= jsonObject.getInteger("mcuTypeAc");
        String mcuIdStr = jsonObject.getString("mcuId");
        jsonObject.remove("mcuId");
        EncryptIdVo encryptIdVo = EncryptIdUtil.parasEncryptId(mcuIdStr);
        Long mcuId = encryptIdVo.getId();
        McuType mcuType = encryptIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                BusiFmeDept busiFmeDept = jsonObject.toJavaObject(BusiFmeDept.class);
                busiFmeDept.setFmeId(mcuId);
                busiFmeDept.setFmeType(mcuTypeAc);
                return toAjax(busiFmeDeptService.insertBusiFmeDept(busiFmeDept));
            }
            case MCU_ZJ: {
                BusiMcuZjDept busiMcuZjDept = jsonObject.toJavaObject(BusiMcuZjDept.class);
                busiMcuZjDept.setMcuId(mcuId);
                busiMcuZjDept.setMcuType(mcuTypeAc);
                return toAjax(busiMcuZjDeptService.insertBusiMcuZjDept(busiMcuZjDept));
            }
            case MCU_PLC: {
                BusiMcuPlcDept busiMcuPlcDept = jsonObject.toJavaObject(BusiMcuPlcDept.class);
                busiMcuPlcDept.setMcuId(mcuId);
                busiMcuPlcDept.setMcuType(mcuTypeAc);
                return toAjax(busiMcuPlcDeptService.insertBusiMcuPlcDept(busiMcuPlcDept));
            }
            case MCU_KDC: {
                BusiMcuKdcDept busiMcuKdcDept = jsonObject.toJavaObject(BusiMcuKdcDept.class);
                busiMcuKdcDept.setMcuId(mcuId);
                busiMcuKdcDept.setMcuType(mcuTypeAc);
                return toAjax(busiMcuKdcDeptService.insertBusiMcuKdcDept(busiMcuKdcDept));
            }
            case SMC3: {
                BusiMcuSmc3Dept busiMcuSmc3Dept = jsonObject.toJavaObject(BusiMcuSmc3Dept.class);
                busiMcuSmc3Dept.setMcuId(mcuId);
                busiMcuSmc3Dept.setMcuType(mcuTypeAc);
                return toAjax(busiMcuSmc3DeptService.insertBusiMcuSmc3Dept(busiMcuSmc3Dept));
            }
            case SMC2: {
                BusiMcuSmc2Dept busiMcuSmc2Dept = jsonObject.toJavaObject(BusiMcuSmc2Dept.class);
                busiMcuSmc2Dept.setMcuId(mcuId);
                busiMcuSmc2Dept.setMcuType(mcuTypeAc);
                return toAjax(busiMcuSmc2DeptService.insertBusiMcuSmc2Dept(busiMcuSmc2Dept));
            }
            case MCU_TENCENT: {
                BusiMcuTencentDept busiMcuTencentDept = jsonObject.toJavaObject(BusiMcuTencentDept.class);
                busiMcuTencentDept.setMcuId(mcuId);
                busiMcuTencentDept.setMcuType(mcuTypeAc);
                return toAjax(busiMcuTencentDeptService.insertBusiMcuTencentDept(busiMcuTencentDept));
            }

            case MCU_DING: {
                BusiMcuDingDept busiMcuDingDept = jsonObject.toJavaObject(BusiMcuDingDept.class);
                busiMcuDingDept.setMcuId(mcuId);
                busiMcuDingDept.setMcuType(mcuTypeAc);
                return toAjax(busiMcuDingDeptService.insertBusiMcuDingDept(busiMcuDingDept));
            }

            case MCU_HWCLOUD: {
                BusiMcuHwcloudDept busiMcuhwcloudDept = jsonObject.toJavaObject(BusiMcuHwcloudDept.class);
                busiMcuhwcloudDept.setMcuId(mcuId);
                busiMcuhwcloudDept.setMcuType(mcuTypeAc);
                return toAjax(busiMcuHwcloudDeptService.insertBusiMcuHwcloudDept(busiMcuhwcloudDept));
            }
            case MCU_ZTE: {
                BusiMcuZteDept busiMcuZteDept = jsonObject.toJavaObject(BusiMcuZteDept.class);
                busiMcuZteDept.setMcuId(mcuId);
                busiMcuZteDept.setMcuType(mcuTypeAc);
                return toAjax(busiMcuZteDeptService.insertBusiMcuZteDept(busiMcuZteDept));
            }
        }
        return RestResponse.fail();
    }

    /**
     * 修改MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
    @PutMapping("{mcuDeptId}")
    @Operation(summary = "修改租户--MCU集群分配记录", description = "修改MCU绑定")
    public RestResponse edit(@RequestBody JSONObject jsonObject, @PathVariable("mcuDeptId") String mcuDeptId)
    {
        Integer mcuTypeAc= jsonObject.getInteger("mcuTypeAc");
        String mcuIdStr = jsonObject.getString("mcuId");
        EncryptIdVo encryptIdVo = EncryptIdUtil.parasEncryptId(mcuIdStr);
        Long mcuId = encryptIdVo.getId();

        EncryptIdVo encryptIdVoTemp = EncryptIdUtil.parasEncryptId(mcuDeptId);
        Long id = encryptIdVoTemp.getId();
        McuType mcuType = encryptIdVoTemp.getMcuType();
        switch (mcuType) {
            case FME: {
                BusiFmeDept busiFmeDept = jsonObject.toJavaObject(BusiFmeDept.class);
                busiFmeDept.setId(id);
                busiFmeDept.setFmeId(mcuId);
                busiFmeDept.setFmeType(mcuTypeAc);
                return toAjax(busiFmeDeptService.updateBusiFmeDept(busiFmeDept));
            }
            case MCU_ZJ: {
                BusiMcuZjDept busiMcuZjDept = jsonObject.toJavaObject(BusiMcuZjDept.class);
                busiMcuZjDept.setId(id);
                busiMcuZjDept.setMcuId(mcuId);
                busiMcuZjDept.setMcuType(mcuTypeAc);
                return toAjax(busiMcuZjDeptService.updateBusiMcuZjDept(busiMcuZjDept));
            }
            case MCU_PLC: {
                BusiMcuPlcDept busiMcuPlcDept = jsonObject.toJavaObject(BusiMcuPlcDept.class);
                busiMcuPlcDept.setId(id);
                busiMcuPlcDept.setMcuId(mcuId);
                busiMcuPlcDept.setMcuType(mcuTypeAc);
                return toAjax(busiMcuPlcDeptService.updateBusiMcuPlcDept(busiMcuPlcDept));
            }
            case MCU_KDC: {
                BusiMcuKdcDept busiMcuKdcDept = jsonObject.toJavaObject(BusiMcuKdcDept.class);
                busiMcuKdcDept.setId(id);
                busiMcuKdcDept.setMcuId(mcuId);
                busiMcuKdcDept.setMcuType(mcuTypeAc);
                return toAjax(busiMcuKdcDeptService.updateBusiMcuKdcDept(busiMcuKdcDept));
            }
            case SMC3: {
                BusiMcuSmc3Dept busiMcuSmc3Dept = jsonObject.toJavaObject(BusiMcuSmc3Dept.class);
                busiMcuSmc3Dept.setId(id);
                busiMcuSmc3Dept.setMcuId(mcuId);
                busiMcuSmc3Dept.setMcuType(mcuTypeAc);
                return toAjax(busiMcuSmc3DeptService.updateBusiMcuSmc3Dept(busiMcuSmc3Dept));
            }
            case SMC2: {
                BusiMcuSmc2Dept busiMcuSmc2Dept = jsonObject.toJavaObject(BusiMcuSmc2Dept.class);
                busiMcuSmc2Dept.setId(id);
                busiMcuSmc2Dept.setMcuId(mcuId);
                busiMcuSmc2Dept.setMcuType(mcuTypeAc);
                return toAjax(busiMcuSmc2DeptService.updateBusiMcuSmc2Dept(busiMcuSmc2Dept));
            }
            case MCU_TENCENT: {
                BusiMcuTencentDept busiMcutencent2Dept = jsonObject.toJavaObject(BusiMcuTencentDept.class);
                busiMcutencent2Dept.setId(id);
                busiMcutencent2Dept.setMcuId(mcuId);
                busiMcutencent2Dept.setMcuType(mcuTypeAc);
                return toAjax(busiMcuTencentDeptService.updateBusiMcuTencentDept(busiMcutencent2Dept));
            }
            case MCU_DING: {
                BusiMcuDingDept busidingDept = jsonObject.toJavaObject(BusiMcuDingDept.class);
                busidingDept.setId(id);
                busidingDept.setMcuId(mcuId);
                busidingDept.setMcuType(mcuTypeAc);
                return toAjax(busiMcuDingDeptService.updateBusiMcuDingDept(busidingDept));
            }
            case MCU_HWCLOUD: {
                BusiMcuHwcloudDept busihwcloudDept = jsonObject.toJavaObject(BusiMcuHwcloudDept.class);
                busihwcloudDept.setId(id);
                busihwcloudDept.setMcuId(mcuId);
                busihwcloudDept.setMcuType(mcuTypeAc);
                return toAjax(busiMcuHwcloudDeptService.updateBusiMcuHwcloudDept(busihwcloudDept));
            }
            case MCU_ZTE: {
                BusiMcuZteDept busiMcuZteDept = jsonObject.toJavaObject(BusiMcuZteDept.class);
                busiMcuZteDept.setId(id);
                busiMcuZteDept.setMcuId(mcuId);
                busiMcuZteDept.setMcuType(mcuTypeAc);
                return toAjax(busiMcuZteDeptService.updateBusiMcuZteDept(busiMcuZteDept));
            }
        }
        return RestResponse.fail();
    }

    /**
     * 删除MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
	@DeleteMapping("/{mcuDeptId}")
	@Operation(summary = "删除租户--MCU集群分配记录", description = "解绑MCU")
    public RestResponse remove(@PathVariable("mcuDeptId") String mcuDeptId)
    {
        EncryptIdVo encryptIdVo = EncryptIdUtil.parasEncryptId(mcuDeptId);
        Long id = encryptIdVo.getId();
        McuType mcuType = encryptIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                return toAjax(busiFmeDeptService.deleteBusiFmeDeptById(id));
            }
            case MCU_ZJ: {
                return toAjax(busiMcuZjDeptService.deleteBusiMcuZjDeptById(id));
            }
            case MCU_PLC: {
                return toAjax(busiMcuPlcDeptService.deleteBusiMcuPlcDeptById(id));
            }
            case MCU_KDC: {
                return toAjax(busiMcuKdcDeptService.deleteBusiMcuKdcDeptById(id));
            }
            case SMC3: {
                return toAjax(busiMcuSmc3DeptService.deleteBusiMcuSmc3DeptById(id));
            }
            case SMC2: {
                return toAjax(busiMcuSmc2DeptService.deleteBusiMcuSmc2DeptById(id));
            }
            case MCU_TENCENT: {
                return toAjax(busiMcuTencentDeptService.deleteBusiMcuTencentDeptById(id));
            }
            case MCU_DING: {
                return toAjax(busiMcuDingDeptService.deleteBusiMcuDingDeptById(id));
            }
            case MCU_HWCLOUD: {
                return toAjax(busiMcuHwcloudDeptService.deleteBusiMcuHwcloudDeptById(id));
            }
            case MCU_ZTE: {
                return toAjax(busiMcuZteDeptService.deleteBusiMcuZteDeptById(id));
            }
        }
        return RestResponse.fail();
    }
}
