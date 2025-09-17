package com.paradisecloud.fcm.mcu.plc.service.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.McuPlcType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.mapper.BusiMcuPlcDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuPlcCluster;
import com.paradisecloud.fcm.dao.model.BusiMcuPlcDept;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.mcu.plc.cache.DeptMcuPlcMappingCache;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcBridgeCache;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcClusterCache;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcBridge;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcBridgeCollection;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcDeptService;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.exception.SystemException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-03-17
 */
@Service
public class BusiMcuPlcDeptServiceImpl implements IBusiMcuPlcDeptService
{
    @Resource
    private BusiMcuPlcDeptMapper busiMcuPlcDeptMapper;
    @Resource
    private BusiTerminalMapper busiTerminalMapper;

    @Override
    public ModelBean selectBusiMcuPlcDeptById(Long id)
    {
        return toModelBean(busiMcuPlcDeptMapper.selectBusiMcuPlcDeptById(id));
    }
    
    public ModelBean selectBusiMcuPlcDeptByDeptId(Long deptId)
    {
        return toModelBean(busiMcuPlcDeptMapper.selectBusiMcuPlcDeptByDeptId(deptId));
    }
    
    /**
     * 查询MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）列表
     * 
     * @param busiMcuPlcDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
    @Override
    public List<ModelBean> selectBusiMcuPlcDeptList(BusiMcuPlcDept busiMcuPlcDept)
    {
        List<BusiMcuPlcDept> gds = busiMcuPlcDeptMapper.selectBusiMcuPlcDeptList(busiMcuPlcDept);
        List<ModelBean> mbs = new ArrayList<>();
        for (BusiMcuPlcDept busiMcuPlcGroupDept2 : gds)
        {
            mbs.add(toModelBean(busiMcuPlcGroupDept2));
        }
        return mbs;
    }

    /**
     * 新增MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuPlcDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int insertBusiMcuPlcDept(BusiMcuPlcDept busiMcuPlcDept)
    {
        busiMcuPlcDept.setCreateTime(new Date());
        if (DeptMcuPlcMappingCache.getInstance().containsKey(busiMcuPlcDept.getDeptId()))
        {
            throw new SystemException(1003432, "该租户已绑定MCU，不能多次绑定！");
        }
        int c = busiMcuPlcDeptMapper.insertBusiMcuPlcDept(busiMcuPlcDept);
        if (c > 0)
        {
            DeptMcuPlcMappingCache.getInstance().put(busiMcuPlcDept.getDeptId(), busiMcuPlcDept);
        }
        return c;
    
    }

    /**
     * 修改MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuPlcDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int updateBusiMcuPlcDept(BusiMcuPlcDept busiMcuPlcDept)
    {
        busiMcuPlcDept.setUpdateTime(new Date());
        int c = busiMcuPlcDeptMapper.updateBusiMcuPlcDept(busiMcuPlcDept);
        if (c > 0)
        {
            DeptMcuPlcMappingCache.getInstance().put(busiMcuPlcDept.getDeptId(), busiMcuPlcDeptMapper.selectBusiMcuPlcDeptById(busiMcuPlcDept.getId()));
        }
        return c;
    
    }

    /**
     * 批量删除MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param ids 需要删除的MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuPlcDeptByIds(Long[] ids)
    {
        return busiMcuPlcDeptMapper.deleteBusiMcuPlcDeptByIds(ids);
    }

    /**
     * 删除MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）信息
     * 
     * @param id MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuPlcDeptById(Long id)
    {
        BusiMcuPlcDept busiMcuPlcDept = busiMcuPlcDeptMapper.selectBusiMcuPlcDeptById(id);
        int c = busiMcuPlcDeptMapper.deleteBusiMcuPlcDeptById(id);
        if (c > 0)
        {
            DeptMcuPlcMappingCache.getInstance().remove(busiMcuPlcDept.getDeptId());
        }
        return c;
    }
    
    public ModelBean toModelBean(BusiMcuPlcDept busiMcuPlcDept)
    {
        ModelBean mb = new ModelBean(busiMcuPlcDept);
        mb.put("deptName", SysDeptCache.getInstance().get(busiMcuPlcDept.getDeptId()).getDeptName());
        mb.put("mcuTypeAcName", McuPlcType.convert(busiMcuPlcDept.getMcuType()).getName());
        StringBuilder fmeInfoBuilder = new StringBuilder();
        if (McuPlcType.convert(busiMcuPlcDept.getMcuType()) == McuPlcType.CLUSTER)
        {
            BusiMcuPlcCluster busiMcuPlcCluster = McuPlcClusterCache.getInstance().get(busiMcuPlcDept.getMcuId());
            fmeInfoBuilder.append("【").append(busiMcuPlcCluster.getName()).append("】");
        }
        else
        {
            McuPlcBridge fmeBridge = McuPlcBridgeCache.getInstance().get(busiMcuPlcDept.getMcuId());
            fmeInfoBuilder.append("【").append(fmeBridge.getBusiMcuPlc().getName()).append("】");
        }
        McuPlcBridgeCollection fmeBridgeCollection = McuPlcBridgeCache.getInstance().getAvailableMcuPlcBridgesByDept(busiMcuPlcDept.getDeptId());
        mb.put("existAvailableMcuBridge", fmeBridgeCollection != null);
        if (fmeBridgeCollection == null)
        {
            fmeInfoBuilder.append("-").append("当前无可用的MCU信息");
            mb.put("mcus", new ArrayList<>());
        }
        else
        {
            fmeInfoBuilder.append(McuType.MCU_PLC.getAlias() + "[");
            
            List<String> fmes = new ArrayList<String>();
            StringBuilder fmeIpInfoBuilder = new StringBuilder();
            fmeBridgeCollection.getMcuPlcBridges().forEach((fmeBridge) -> {
                if (!ObjectUtils.isEmpty(fmeIpInfoBuilder))
                {
                    fmeIpInfoBuilder.append(", ");
                }
                fmeIpInfoBuilder.append(fmeBridge.getBusiMcuPlc().getIp());
                
                fmes.add(fmeBridge.getBusiMcuPlc().getIp());
            });
            fmeInfoBuilder.append(fmeIpInfoBuilder);
            fmeInfoBuilder.append("]");
            mb.put("mcus", fmes);
        }
        mb.put("mcuInfo", fmeInfoBuilder.toString());
        return mb;
    }
}
