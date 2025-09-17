package com.paradisecloud.fcm.mcu.kdc.service.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.McuKdcType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.mapper.BusiMcuKdcDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuKdcCluster;
import com.paradisecloud.fcm.dao.model.BusiMcuKdcDept;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.mcu.kdc.cache.DeptMcuKdcMappingCache;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcBridgeCache;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcClusterCache;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcBridge;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcBridgeCollection;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcDeptService;
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
public class BusiMcuKdcDeptServiceImpl implements IBusiMcuKdcDeptService
{
    @Resource
    private BusiMcuKdcDeptMapper busiMcuKdcDeptMapper;
    @Resource
    private BusiTerminalMapper busiTerminalMapper;

    @Override
    public ModelBean selectBusiMcuKdcDeptById(Long id)
    {
        return toModelBean(busiMcuKdcDeptMapper.selectBusiMcuKdcDeptById(id));
    }
    
    public ModelBean selectBusiMcuKdcDeptByDeptId(Long deptId)
    {
        return toModelBean(busiMcuKdcDeptMapper.selectBusiMcuKdcDeptByDeptId(deptId));
    }
    
    /**
     * 查询MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）列表
     * 
     * @param busiMcuKdcDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
    @Override
    public List<ModelBean> selectBusiMcuKdcDeptList(BusiMcuKdcDept busiMcuKdcDept)
    {
        List<BusiMcuKdcDept> gds = busiMcuKdcDeptMapper.selectBusiMcuKdcDeptList(busiMcuKdcDept);
        List<ModelBean> mbs = new ArrayList<>();
        for (BusiMcuKdcDept busiMcuKdcGroupDept2 : gds)
        {
            mbs.add(toModelBean(busiMcuKdcGroupDept2));
        }
        return mbs;
    }

    /**
     * 新增MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuKdcDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int insertBusiMcuKdcDept(BusiMcuKdcDept busiMcuKdcDept)
    {
        busiMcuKdcDept.setCreateTime(new Date());
        if (DeptMcuKdcMappingCache.getInstance().containsKey(busiMcuKdcDept.getDeptId()))
        {
            throw new SystemException(1003432, "该租户已绑定MCU，不能多次绑定！");
        }
        int c = busiMcuKdcDeptMapper.insertBusiMcuKdcDept(busiMcuKdcDept);
        if (c > 0)
        {
            DeptMcuKdcMappingCache.getInstance().put(busiMcuKdcDept.getDeptId(), busiMcuKdcDept);
        }
        return c;
    
    }

    /**
     * 修改MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuKdcDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int updateBusiMcuKdcDept(BusiMcuKdcDept busiMcuKdcDept)
    {
        busiMcuKdcDept.setUpdateTime(new Date());
        int c = busiMcuKdcDeptMapper.updateBusiMcuKdcDept(busiMcuKdcDept);
        if (c > 0)
        {
            DeptMcuKdcMappingCache.getInstance().put(busiMcuKdcDept.getDeptId(), busiMcuKdcDeptMapper.selectBusiMcuKdcDeptById(busiMcuKdcDept.getId()));
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
    public int deleteBusiMcuKdcDeptByIds(Long[] ids)
    {
        return busiMcuKdcDeptMapper.deleteBusiMcuKdcDeptByIds(ids);
    }

    /**
     * 删除MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）信息
     * 
     * @param id MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuKdcDeptById(Long id)
    {
        BusiMcuKdcDept busiMcuKdcDept = busiMcuKdcDeptMapper.selectBusiMcuKdcDeptById(id);
        int c = busiMcuKdcDeptMapper.deleteBusiMcuKdcDeptById(id);
        if (c > 0)
        {
            DeptMcuKdcMappingCache.getInstance().remove(busiMcuKdcDept.getDeptId());
        }
        return c;
    }
    
    public ModelBean toModelBean(BusiMcuKdcDept busiMcuKdcDept)
    {
        ModelBean mb = new ModelBean(busiMcuKdcDept);
        mb.put("deptName", SysDeptCache.getInstance().get(busiMcuKdcDept.getDeptId()).getDeptName());
        mb.put("mcuTypeAcName", McuKdcType.convert(busiMcuKdcDept.getMcuType()).getName());
        StringBuilder fmeInfoBuilder = new StringBuilder();
        if (McuKdcType.convert(busiMcuKdcDept.getMcuType()) == McuKdcType.CLUSTER)
        {
            BusiMcuKdcCluster busiMcuKdcCluster = McuKdcClusterCache.getInstance().get(busiMcuKdcDept.getMcuId());
            fmeInfoBuilder.append("【").append(busiMcuKdcCluster.getName()).append("】");
        }
        else
        {
            McuKdcBridge fmeBridge = McuKdcBridgeCache.getInstance().get(busiMcuKdcDept.getMcuId());
            fmeInfoBuilder.append("【").append(fmeBridge.getBusiMcuKdc().getName()).append("】");
        }
        McuKdcBridgeCollection fmeBridgeCollection = McuKdcBridgeCache.getInstance().getAvailableMcuKdcBridgesByDept(busiMcuKdcDept.getDeptId());
        mb.put("existAvailableMcuBridge", fmeBridgeCollection != null);
        if (fmeBridgeCollection == null)
        {
            fmeInfoBuilder.append("-").append("当前无可用的MCU信息");
            mb.put("mcus", new ArrayList<>());
        }
        else
        {
            fmeInfoBuilder.append(McuType.MCU_KDC.getAlias() + "[");
            
            List<String> fmes = new ArrayList<String>();
            StringBuilder fmeIpInfoBuilder = new StringBuilder();
            fmeBridgeCollection.getMcuKdcBridges().forEach((fmeBridge) -> {
                if (!ObjectUtils.isEmpty(fmeIpInfoBuilder))
                {
                    fmeIpInfoBuilder.append(", ");
                }
                fmeIpInfoBuilder.append(fmeBridge.getBusiMcuKdc().getIp());
                
                fmes.add(fmeBridge.getBusiMcuKdc().getIp());
            });
            fmeInfoBuilder.append(fmeIpInfoBuilder);
            fmeInfoBuilder.append("]");
            mb.put("mcus", fmes);
        }
        mb.put("mcuInfo", fmeInfoBuilder.toString());
        return mb;
    }
}
