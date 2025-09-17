package com.paradisecloud.fcm.zte.service.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.McuZteType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZteDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuZteCluster;
import com.paradisecloud.fcm.dao.model.BusiMcuZteDept;
import com.paradisecloud.fcm.zte.cache.DeptMcuZteMappingCache;
import com.paradisecloud.fcm.zte.cache.McuZteBridgeCache;
import com.paradisecloud.fcm.zte.cache.McuZteClusterCache;
import com.paradisecloud.fcm.zte.cache.model.McuZteBridge;
import com.paradisecloud.fcm.zte.cache.model.McuZteBridgeCollection;
import com.paradisecloud.fcm.zte.service.interfaces.IBusiMcuZteDeptService;
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
public class BusiMcuZteDeptServiceImpl implements IBusiMcuZteDeptService
{
    @Resource
    private BusiMcuZteDeptMapper busiMcuZteDeptMapper;
    @Resource
    private BusiTerminalMapper busiTerminalMapper;

    @Override
    public ModelBean selectBusiMcuZteDeptById(Long id)
    {
        return toModelBean(busiMcuZteDeptMapper.selectBusiMcuZteDeptById(id));
    }

    @Override
    public ModelBean selectBusiMcuZteDeptByDeptId(Long deptId)
    {
        return toModelBean(busiMcuZteDeptMapper.selectBusiMcuZteDeptByDeptId(deptId));
    }

    /**
     * 查询MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）列表
     *
     * @param busiMcuZteDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
    @Override
    public List<ModelBean> selectBusiMcuZteDeptList(BusiMcuZteDept busiMcuZteDept)
    {
        List<BusiMcuZteDept> gds = busiMcuZteDeptMapper.selectBusiMcuZteDeptList(busiMcuZteDept);
        List<ModelBean> mbs = new ArrayList<>();
        for (BusiMcuZteDept busiMcuZteGroupDept2 : gds)
        {
            mbs.add(toModelBean(busiMcuZteGroupDept2));
        }
        return mbs;
    }

    /**
     * 新增MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     *
     * @param busiMcuZteDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int insertBusiMcuZteDept(BusiMcuZteDept busiMcuZteDept)
    {
        busiMcuZteDept.setCreateTime(new Date());
        if (DeptMcuZteMappingCache.getInstance().containsKey(busiMcuZteDept.getDeptId()))
        {
            throw new SystemException(1003432, "该租户已绑定MCU，不能多次绑定！");
        }
        int c = busiMcuZteDeptMapper.insertBusiMcuZteDept(busiMcuZteDept);
        if (c > 0)
        {
            DeptMcuZteMappingCache.getInstance().put(busiMcuZteDept.getDeptId(), busiMcuZteDept);
        }
        return c;

    }

    /**
     * 修改MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     *
     * @param busiMcuZteDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int updateBusiMcuZteDept(BusiMcuZteDept busiMcuZteDept)
    {
        busiMcuZteDept.setUpdateTime(new Date());
        int c = busiMcuZteDeptMapper.updateBusiMcuZteDept(busiMcuZteDept);
        if (c > 0)
        {
            DeptMcuZteMappingCache.getInstance().put(busiMcuZteDept.getDeptId(), busiMcuZteDeptMapper.selectBusiMcuZteDeptById(busiMcuZteDept.getId()));
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
    public int deleteBusiMcuZteDeptByIds(Long[] ids)
    {
        return busiMcuZteDeptMapper.deleteBusiMcuZteDeptByIds(ids);
    }

    /**
     * 删除MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）信息
     *
     * @param id MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuZteDeptById(Long id)
    {
        BusiMcuZteDept busiMcuZteDept = busiMcuZteDeptMapper.selectBusiMcuZteDeptById(id);
        int c = busiMcuZteDeptMapper.deleteBusiMcuZteDeptById(id);
        if (c > 0)
        {
            DeptMcuZteMappingCache.getInstance().remove(busiMcuZteDept.getDeptId());
        }
        return c;
    }

    @Override
    public ModelBean toModelBean(BusiMcuZteDept busiMcuZteDept)
    {
        ModelBean mb = new ModelBean(busiMcuZteDept);
        mb.put("deptName", SysDeptCache.getInstance().get(busiMcuZteDept.getDeptId()).getDeptName());
        mb.put("mcuTypeAcName", McuZteType.convert(busiMcuZteDept.getMcuType()).getName());
        StringBuilder fmeInfoBuilder = new StringBuilder();
        if (McuZteType.convert(busiMcuZteDept.getMcuType()) == McuZteType.CLUSTER)
        {
            BusiMcuZteCluster busiMcuZteCluster = McuZteClusterCache.getInstance().get(busiMcuZteDept.getMcuId());
            fmeInfoBuilder.append("【").append(busiMcuZteCluster.getName()).append("】");
        }
        else
        {
            McuZteBridge fmeBridge = McuZteBridgeCache.getInstance().get(busiMcuZteDept.getMcuId());
            fmeInfoBuilder.append("【").append(fmeBridge.getBusiMcuZte().getName()).append("】");
        }
        McuZteBridgeCollection fmeBridgeCollection = McuZteBridgeCache.getInstance().getAvailableMcuZteBridgesByDept(busiMcuZteDept.getDeptId());
        mb.put("existAvailableMcuBridge", fmeBridgeCollection != null);
        if (fmeBridgeCollection == null)
        {
            fmeInfoBuilder.append("-").append("当前无可用的MCU信息");
            mb.put("mcus", new ArrayList<>());
        }
        else
        {
            fmeInfoBuilder.append(McuType.MCU_ZTE.getAlias() + "[");

            List<String> fmes = new ArrayList<String>();
            StringBuilder fmeIpInfoBuilder = new StringBuilder();
            fmeBridgeCollection.getMcuZteBridges().forEach((fmeBridge) -> {
                if (!ObjectUtils.isEmpty(fmeIpInfoBuilder))
                {
                    fmeIpInfoBuilder.append(", ");
                }
                fmeIpInfoBuilder.append(fmeBridge.getBusiMcuZte().getIp());

                fmes.add(fmeBridge.getBusiMcuZte().getIp());
            });
            fmeInfoBuilder.append(fmeIpInfoBuilder);
            fmeInfoBuilder.append("]");
            mb.put("mcus", fmes);
        }
        mb.put("mcuInfo", fmeInfoBuilder.toString());
        return mb;
    }
}
