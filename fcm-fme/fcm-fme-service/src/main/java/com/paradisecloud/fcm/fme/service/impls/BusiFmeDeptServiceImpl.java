package com.paradisecloud.fcm.fme.service.impls;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.paradisecloud.fcm.common.enumer.McuType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.dao.mapper.BusiFmeDeptMapper;
import com.paradisecloud.fcm.dao.model.BusiFmeCluster;
import com.paradisecloud.fcm.dao.model.BusiFmeDept;
import com.paradisecloud.fcm.fme.cache.DeptFmeMappingCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.FmeClusterCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.FmeBridgeCollection;
import com.paradisecloud.fcm.fme.service.interfaces.IBusiFmeDeptService;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.exception.SystemException;

/**
 * FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-03-17
 */
@Service
public class BusiFmeDeptServiceImpl implements IBusiFmeDeptService 
{
    @Autowired
    private BusiFmeDeptMapper busiFmeDeptMapper;

    @Override
    public ModelBean selectBusiFmeDeptById(Long id)
    {
        return toModelBean(busiFmeDeptMapper.selectBusiFmeDeptById(id));
    }
    
    public ModelBean selectBusiFmeDeptByDeptId(Long deptId)
    {
        return toModelBean(busiFmeDeptMapper.selectBusiFmeDeptByDeptId(deptId));
    }
    
    /**
     * 查询FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）列表
     * 
     * @param busiFmeDept FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * @return FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     */
    @Override
    public List<ModelBean> selectBusiFmeDeptList(BusiFmeDept busiFmeDept)
    {
        List<BusiFmeDept> gds = busiFmeDeptMapper.selectBusiFmeDeptList(busiFmeDept);
        List<ModelBean> mbs = new ArrayList<>();
        for (BusiFmeDept busiFmeGroupDept2 : gds)
        {
            mbs.add(toModelBean(busiFmeGroupDept2));
        }
        return mbs;
    }

    /**
     * 新增FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * 
     * @param busiFmeDept FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int insertBusiFmeDept(BusiFmeDept busiFmeDept)
    {
        busiFmeDept.setCreateTime(new Date());
        if (DeptFmeMappingCache.getInstance().containsKey(busiFmeDept.getDeptId()))
        {
            throw new SystemException(1003432, "该租户已绑定FME，不能多次绑定！");
        }
        int c = busiFmeDeptMapper.insertBusiFmeDept(busiFmeDept);
        if (c > 0)
        {
            DeptFmeMappingCache.getInstance().put(busiFmeDept.getDeptId(), busiFmeDept);
        }
        return c;
    
    }

    /**
     * 修改FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * 
     * @param busiFmeDept FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int updateBusiFmeDept(BusiFmeDept busiFmeDept)
    {
        busiFmeDept.setUpdateTime(new Date());
        int c = busiFmeDeptMapper.updateBusiFmeDept(busiFmeDept);
        if (c > 0)
        {
            DeptFmeMappingCache.getInstance().put(busiFmeDept.getDeptId(), busiFmeDeptMapper.selectBusiFmeDeptById(busiFmeDept.getId()));
        }
        return c;
    
    }

    /**
     * 批量删除FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     * 
     * @param ids 需要删除的FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    @Override
    public int deleteBusiFmeDeptByIds(Long[] ids)
    {
        return busiFmeDeptMapper.deleteBusiFmeDeptByIds(ids);
    }

    /**
     * 删除FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）信息
     * 
     * @param id FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    @Override
    public int deleteBusiFmeDeptById(Long id)
    {
        BusiFmeDept busiFmeDept = busiFmeDeptMapper.selectBusiFmeDeptById(id);
        int c = busiFmeDeptMapper.deleteBusiFmeDeptById(id);
        if (c > 0)
        {
            DeptFmeMappingCache.getInstance().remove(busiFmeDept.getDeptId());
        }
        return c;
    }
    
    public ModelBean toModelBean(BusiFmeDept busiFmeDept)
    {
        ModelBean mb = new ModelBean(busiFmeDept);
        mb.put("deptName", SysDeptCache.getInstance().get(busiFmeDept.getDeptId()).getDeptName());
        mb.put("mcuTypeAcName", FmeType.convert(busiFmeDept.getFmeType()).getName());
        StringBuilder fmeInfoBuilder = new StringBuilder();
        if (FmeType.convert(busiFmeDept.getFmeType()) == FmeType.CLUSTER)
        {
            BusiFmeCluster busiFmeCluster = FmeClusterCache.getInstance().get(busiFmeDept.getFmeId());
            fmeInfoBuilder.append("【").append(busiFmeCluster.getName()).append("】");
        }
        else
        {
            FmeBridge fmeBridge = FmeBridgeCache.getInstance().get(busiFmeDept.getFmeId());
            fmeInfoBuilder.append("【").append(fmeBridge.getBusiFme().getName()).append("】");
        }
        FmeBridgeCollection fmeBridgeCollection = FmeBridgeCache.getInstance().getAvailableFmeBridgesByDept(busiFmeDept.getDeptId());
        mb.put("existAvailableMcuBridge", fmeBridgeCollection != null);
        if (fmeBridgeCollection == null)
        {
            fmeInfoBuilder.append("-").append("当前无可用的" + McuType.FME.getAlias() + "信息");
            mb.put("mcus", new ArrayList<>());
        }
        else
        {
            fmeInfoBuilder.append(McuType.FME.getAlias() + "[");
            
            List<String> fmes = new ArrayList<String>();
            StringBuilder fmeIpInfoBuilder = new StringBuilder();
            fmeBridgeCollection.getFmeBridges().forEach((fmeBridge) -> {
                if (!ObjectUtils.isEmpty(fmeIpInfoBuilder))
                {
                    fmeIpInfoBuilder.append(", ");
                }
                fmeIpInfoBuilder.append(fmeBridge.getBusiFme().getIp());
                
                fmes.add(fmeBridge.getBusiFme().getIp());
            });
            fmeInfoBuilder.append(fmeIpInfoBuilder);
            fmeInfoBuilder.append("]");
            mb.put("mcus", fmes);
        }
        mb.put("mcuInfo", fmeInfoBuilder.toString());
        return mb;
    }
}
