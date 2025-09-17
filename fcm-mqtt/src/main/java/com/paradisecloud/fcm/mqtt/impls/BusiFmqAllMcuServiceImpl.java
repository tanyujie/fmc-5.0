package com.paradisecloud.fcm.mqtt.impls;

import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.McuTypeVo;
import com.paradisecloud.fcm.ding.cache.DeptDingMappingCache;
import com.paradisecloud.fcm.ding.cache.DingBridgeCache;
import com.paradisecloud.fcm.ding.cache.DingBridgeCollection;
import com.paradisecloud.fcm.fme.cache.DeptFmeMappingCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridgeCollection;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.DeptHwcloudMappingCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudBridgeCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudBridgeCollection;
import com.paradisecloud.fcm.mcu.kdc.cache.DeptMcuKdcMappingCache;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcBridgeCache;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcBridgeCollection;
import com.paradisecloud.fcm.mcu.plc.cache.DeptMcuPlcMappingCache;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcBridgeCache;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcBridgeCollection;
import com.paradisecloud.fcm.mcu.zj.cache.DeptMcuZjMappingCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridgeCollection;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiAllMcuService;
import com.paradisecloud.fcm.smc2.cache.DeptSmc2MappingCache;
import com.paradisecloud.fcm.smc2.cache.Smc2BridgeCache;
import com.paradisecloud.fcm.smc2.cache.Smc2BridgeCollection;
import com.paradisecloud.fcm.tencent.cache.DeptTencentMappingCache;
import com.paradisecloud.fcm.tencent.cache.TencentBridgeCache;
import com.paradisecloud.fcm.tencent.cache.TencentBridgeCollection;
import com.paradisecloud.smc3.busi.cache.DeptSmc3MappingCache;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCollection;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Service
public class BusiFmqAllMcuServiceImpl implements IBusiAllMcuService {

    /**
     * 获取部门绑定MCU类型列表
     *
     * @param deptId
     * @return
     */
    @Override
    public List<McuTypeVo> getMcuTypeList(Long deptId) {
        Assert.isTrue(deptId != null, "部门ID不能为空！");
        List<McuTypeVo> mcuTypeList = new ArrayList<>();
        //fme
        {
            FmeBridgeCollection mcuBridgeCollection = FmeBridgeCache.getInstance().getAvailableFmeBridgesByDept(deptId);
            if (mcuBridgeCollection != null) {
                McuType mcuType = McuType.FME;
                McuTypeVo mcuTypeVo = new McuTypeVo();
                mcuTypeVo.setCode(mcuType.getCode());
                mcuTypeVo.setName(mcuType.getName());
                mcuTypeVo.setAlias(mcuType.getAlias());
                Long deptIdBind = null;
                String deptAncestors = "";
                BusiFmeDept busiFmeDept = DeptFmeMappingCache.getInstance().getBindFme(deptId);
                if (busiFmeDept != null) {
                    Long deptIdNew = busiFmeDept.getDeptId();
                    deptIdBind = deptIdNew;
                    SysDept sysDept = SysDeptCache.getInstance().get(deptIdNew);
                    if (sysDept != null) {
                        deptAncestors = sysDept.getAncestors();
                    }
                }
                mcuTypeVo.setBindDeptId(deptIdBind);
                mcuTypeVo.setBindDeptAncestors(deptAncestors);
                mcuTypeList.add(mcuTypeVo);
            }
        }
        //mcu-zj
        {
            McuZjBridgeCollection mcuBridgeCollection = McuZjBridgeCache.getInstance().getAvailableMcuZjBridgesByDept(deptId);
            if (mcuBridgeCollection != null) {
                McuType mcuType = McuType.MCU_ZJ;
                McuTypeVo mcuTypeVo = new McuTypeVo();
                mcuTypeVo.setCode(mcuType.getCode());
                mcuTypeVo.setName(mcuType.getName());
                mcuTypeVo.setAlias(mcuType.getAlias());
                Long deptIdBind = null;
                String deptAncestors = "";
                BusiMcuZjDept busiMcuZjDept = DeptMcuZjMappingCache.getInstance().getBindMcu(deptId);
                if (busiMcuZjDept != null) {
                    Long deptIdNew = busiMcuZjDept.getDeptId();
                    deptIdBind = deptIdNew;
                    SysDept sysDept = SysDeptCache.getInstance().get(deptIdNew);
                    if (sysDept != null) {
                        deptAncestors = sysDept.getAncestors();
                    }
                }
                mcuTypeVo.setBindDeptId(deptIdBind);
                mcuTypeVo.setBindDeptAncestors(deptAncestors);
                mcuTypeList.add(mcuTypeVo);
            }
        }
        //mcu-plc
        {
            McuPlcBridgeCollection mcuBridgeCollection = McuPlcBridgeCache.getInstance().getAvailableMcuPlcBridgesByDept(deptId);
            if (mcuBridgeCollection != null) {
                McuType mcuType = McuType.MCU_PLC;
                McuTypeVo mcuTypeVo = new McuTypeVo();
                mcuTypeVo.setCode(mcuType.getCode());
                mcuTypeVo.setName(mcuType.getName());
                mcuTypeVo.setAlias(mcuType.getAlias());
                Long deptIdBind = null;
                String deptAncestors = "";
                BusiMcuPlcDept busiMcuPlcDept = DeptMcuPlcMappingCache.getInstance().getBindMcu(deptId);
                if (busiMcuPlcDept != null) {
                    Long deptIdNew = busiMcuPlcDept.getDeptId();
                    deptIdBind = deptIdNew;
                    SysDept sysDept = SysDeptCache.getInstance().get(deptIdNew);
                    if (sysDept != null) {
                        deptAncestors = sysDept.getAncestors();
                    }
                }
                mcuTypeVo.setBindDeptId(deptIdBind);
                mcuTypeVo.setBindDeptAncestors(deptAncestors);
                mcuTypeList.add(mcuTypeVo);
            }
        }
        //mcu-kdc
        {
            McuKdcBridgeCollection mcuBridgeCollection = McuKdcBridgeCache.getInstance().getAvailableMcuKdcBridgesByDept(deptId);
            if (mcuBridgeCollection != null) {
                McuType mcuType = McuType.MCU_KDC;
                McuTypeVo mcuTypeVo = new McuTypeVo();
                mcuTypeVo.setCode(mcuType.getCode());
                mcuTypeVo.setName(mcuType.getName());
                mcuTypeVo.setAlias(mcuType.getAlias());
                Long deptIdBind = null;
                String deptAncestors = "";
                BusiMcuKdcDept busiMcuKdcDept = DeptMcuKdcMappingCache.getInstance().getBindMcu(deptId);
                if (busiMcuKdcDept != null) {
                    Long deptIdNew = busiMcuKdcDept.getDeptId();
                    deptIdBind = deptIdNew;
                    SysDept sysDept = SysDeptCache.getInstance().get(deptIdNew);
                    if (sysDept != null) {
                        deptAncestors = sysDept.getAncestors();
                    }
                }
                mcuTypeVo.setBindDeptId(deptIdBind);
                mcuTypeVo.setBindDeptAncestors(deptAncestors);
                mcuTypeList.add(mcuTypeVo);
            }
        }
        // smc3
        {
            Smc3BridgeCollection mcuBridgeCollection = Smc3BridgeCache.getInstance().getAvailableSmc3BridgesByDept(deptId);
            if (mcuBridgeCollection != null) {
                McuType mcuType = McuType.SMC3;
                McuTypeVo mcuTypeVo = new McuTypeVo();
                mcuTypeVo.setCode(mcuType.getCode());
                mcuTypeVo.setName(mcuType.getName());
                mcuTypeVo.setAlias(mcuType.getAlias());
                Long deptIdBind = null;
                String deptAncestors = "";
                BusiMcuSmc3Dept busiMcuSmc3Dept = DeptSmc3MappingCache.getInstance().getBindSmc3(deptId);
                if (busiMcuSmc3Dept != null) {
                    Long deptIdNew = busiMcuSmc3Dept.getDeptId();
                    deptIdBind = deptIdNew;
                    SysDept sysDept = SysDeptCache.getInstance().get(deptIdNew);
                    if (sysDept != null) {
                        deptAncestors = sysDept.getAncestors();
                    }
                }
                mcuTypeVo.setBindDeptId(deptIdBind);
                mcuTypeVo.setBindDeptAncestors(deptAncestors);
                mcuTypeList.add(mcuTypeVo);
            }
        }
        // smc2
        {
            Smc2BridgeCollection mcuBridgeCollection = Smc2BridgeCache.getInstance().getAvailableSmc2BridgesByDept(deptId);
            if (mcuBridgeCollection != null) {
                McuType mcuType = McuType.SMC2;
                McuTypeVo mcuTypeVo = new McuTypeVo();
                mcuTypeVo.setCode(mcuType.getCode());
                mcuTypeVo.setName(mcuType.getName());
                mcuTypeVo.setAlias(mcuType.getAlias());;
                Long deptIdBind = null;
                String deptAncestors = "";
                BusiMcuSmc2Dept busiMcuSmc2Dept = DeptSmc2MappingCache.getInstance().getBindSmc(deptId);
                if (busiMcuSmc2Dept != null) {
                    Long deptIdNew = busiMcuSmc2Dept.getDeptId();
                    deptIdBind = deptIdNew;
                    SysDept sysDept = SysDeptCache.getInstance().get(deptIdNew);
                    if (sysDept != null) {
                        deptAncestors = sysDept.getAncestors();
                    }
                }
                mcuTypeVo.setBindDeptId(deptIdBind);
                mcuTypeVo.setBindDeptAncestors(deptAncestors);
                mcuTypeList.add(mcuTypeVo);
            }
        }

        // tencent
        {
            TencentBridgeCollection mcuBridgeCollection = TencentBridgeCache.getInstance().getAvailableTencentBridgesByDept(deptId);
            if (mcuBridgeCollection != null) {
                McuType mcuType = McuType.MCU_TENCENT;
                McuTypeVo mcuTypeVo = new McuTypeVo();
                mcuTypeVo.setCode(mcuType.getCode());
                mcuTypeVo.setName(mcuType.getName());
                mcuTypeVo.setAlias(mcuType.getAlias());
                Long deptIdBind = null;
                String deptAncestors = "";
                BusiMcuTencentDept busiTencentDept = DeptTencentMappingCache.getInstance().getBindSmc(deptId);
                if (busiTencentDept != null) {
                    Long deptIdNew = busiTencentDept.getDeptId();
                    deptIdBind = deptIdNew;
                    SysDept sysDept = SysDeptCache.getInstance().get(deptIdNew);
                    if (sysDept != null) {
                        deptAncestors = sysDept.getAncestors();
                    }
                }
                mcuTypeVo.setBindDeptId(deptIdBind);
                mcuTypeVo.setBindDeptAncestors(deptAncestors);
                mcuTypeList.add(mcuTypeVo);
            }
        }
        //ding
        {
            DingBridgeCollection mcuBridgeCollection = DingBridgeCache.getInstance().getAvailableDingBridgesByDept(deptId);
            if (mcuBridgeCollection != null) {
                McuType mcuType = McuType.MCU_DING;
                McuTypeVo mcuTypeVo = new McuTypeVo();
                mcuTypeVo.setCode(mcuType.getCode());
                mcuTypeVo.setName(mcuType.getName());
                mcuTypeVo.setAlias(mcuType.getAlias());
                Long deptIdBind = null;
                String deptAncestors = "";
                BusiMcuDingDept busiDingDept = DeptDingMappingCache.getInstance().getBindSmc(deptId);
                if (busiDingDept != null) {
                    Long deptIdNew = busiDingDept.getDeptId();
                    deptIdBind = deptIdNew;
                    SysDept sysDept = SysDeptCache.getInstance().get(deptIdNew);
                    if (sysDept != null) {
                        deptAncestors = sysDept.getAncestors();
                    }
                }
                mcuTypeVo.setBindDeptId(deptIdBind);
                mcuTypeVo.setBindDeptAncestors(deptAncestors);
                mcuTypeList.add(mcuTypeVo);
            }
        }
        //hwcloud
        {
            HwcloudBridgeCollection mcuBridgeCollection = HwcloudBridgeCache.getInstance().getAvailableHwcloudBridgesByDept(deptId);
            if (mcuBridgeCollection != null) {
                McuType mcuType = McuType.MCU_HWCLOUD;
                McuTypeVo mcuTypeVo = new McuTypeVo();
                mcuTypeVo.setCode(mcuType.getCode());
                mcuTypeVo.setName(mcuType.getName());
                mcuTypeVo.setAlias(mcuType.getAlias());
                Long deptIdBind = null;
                String deptAncestors = "";
                BusiMcuHwcloudDept busiHwcloudDept = DeptHwcloudMappingCache.getInstance().getBindSmc(deptId);
                if (busiHwcloudDept != null) {
                    Long deptIdNew = busiHwcloudDept.getDeptId();
                    deptIdBind = deptIdNew;
                    SysDept sysDept = SysDeptCache.getInstance().get(deptIdNew);
                    if (sysDept != null) {
                        deptAncestors = sysDept.getAncestors();
                    }
                }
                mcuTypeVo.setBindDeptId(deptIdBind);
                mcuTypeVo.setBindDeptAncestors(deptAncestors);
                mcuTypeList.add(mcuTypeVo);
            }
        }
        if (mcuTypeList.size() <= 1) {
            return mcuTypeList;
        }
        List<McuTypeVo> bindMcuTypeList = new ArrayList<>();
        int bindDeptAncestorsLength = 0;
        for (McuTypeVo mcuTypeVoTemp : mcuTypeList) {
            String bindDeptAncestorsTemp = mcuTypeVoTemp.getBindDeptAncestors();
            if (bindDeptAncestorsLength < bindDeptAncestorsTemp.length()) {
                bindDeptAncestorsLength = bindDeptAncestorsTemp.length();
            }
        }
        for (McuTypeVo mcuTypeVoTemp : mcuTypeList) {
            String bindDeptAncestorsTemp = mcuTypeVoTemp.getBindDeptAncestors();
            if (bindDeptAncestorsTemp.length() == bindDeptAncestorsLength) {
                bindMcuTypeList.add(mcuTypeVoTemp);
            }
        }
        return bindMcuTypeList;
    }

    /**
     * 获取部门绑定默认MCU类型
     *
     * @param deptId
     * @return
     */
    @Override
    public McuTypeVo getDefaultMcuType(Long deptId) {
        McuTypeVo mcuTypeVo = null;
        List<McuTypeVo> mcuTypeVoList = getMcuTypeList(deptId);
        if (mcuTypeVoList.size() > 0) {
            mcuTypeVo = mcuTypeVoList.get(0);
        }

        return mcuTypeVo;
    }
}
