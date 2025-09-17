package com.paradisecloud.fcm.mcu.zj.service.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.McuZjType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZjDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.dao.model.BusiMcuZjCluster;
import com.paradisecloud.fcm.dao.model.BusiMcuZjDept;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.mcu.zj.cache.DeptMcuZjMappingCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjClusterCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridgeCollection;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjDeptService;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
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
public class BusiMcuZjDeptServiceImpl implements IBusiMcuZjDeptService
{
    @Resource
    private BusiMcuZjDeptMapper busiMcuZjDeptMapper;
    @Resource
    private BusiTerminalMapper busiTerminalMapper;

    @Override
    public ModelBean selectBusiMcuZjDeptById(Long id)
    {
        return toModelBean(busiMcuZjDeptMapper.selectBusiMcuZjDeptById(id));
    }
    
    public ModelBean selectBusiMcuZjDeptByDeptId(Long deptId)
    {
        return toModelBean(busiMcuZjDeptMapper.selectBusiMcuZjDeptByDeptId(deptId));
    }
    
    /**
     * 查询MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）列表
     * 
     * @param busiMcuZjDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
    @Override
    public List<ModelBean> selectBusiMcuZjDeptList(BusiMcuZjDept busiMcuZjDept)
    {
        List<BusiMcuZjDept> gds = busiMcuZjDeptMapper.selectBusiMcuZjDeptList(busiMcuZjDept);
        List<ModelBean> mbs = new ArrayList<>();
        for (BusiMcuZjDept busiMcuZjGroupDept2 : gds)
        {
            mbs.add(toModelBean(busiMcuZjGroupDept2));
        }
        return mbs;
    }

    /**
     * 新增MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuZjDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int insertBusiMcuZjDept(BusiMcuZjDept busiMcuZjDept)
    {
        busiMcuZjDept.setCreateTime(new Date());
        if (DeptMcuZjMappingCache.getInstance().containsKey(busiMcuZjDept.getDeptId()))
        {
            throw new SystemException(1003432, "该租户已绑定MCU，不能多次绑定！");
        }
        int c = busiMcuZjDeptMapper.insertBusiMcuZjDept(busiMcuZjDept);
        if (c > 0)
        {
            DeptMcuZjMappingCache.getInstance().put(busiMcuZjDept.getDeptId(), busiMcuZjDept);
        }
        return c;
    
    }

    /**
     * 修改MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuZjDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int updateBusiMcuZjDept(BusiMcuZjDept busiMcuZjDept)
    {
        busiMcuZjDept.setUpdateTime(new Date());
        int c = busiMcuZjDeptMapper.updateBusiMcuZjDept(busiMcuZjDept);
        if (c > 0)
        {
            DeptMcuZjMappingCache.getInstance().put(busiMcuZjDept.getDeptId(), busiMcuZjDeptMapper.selectBusiMcuZjDeptById(busiMcuZjDept.getId()));
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
    public int deleteBusiMcuZjDeptByIds(Long[] ids)
    {
        return busiMcuZjDeptMapper.deleteBusiMcuZjDeptByIds(ids);
    }

    /**
     * 删除MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）信息
     * 
     * @param id MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuZjDeptById(Long id)
    {
        BusiMcuZjDept busiMcuZjDept = busiMcuZjDeptMapper.selectBusiMcuZjDeptById(id);
        {
            BusiTerminal busiTerminal = new BusiTerminal();
            busiTerminal.setType(TerminalType.ZJ_H323.getId());
            busiTerminal.setZjServerId(busiMcuZjDept.getMcuId());

            List<BusiTerminal> terminalList = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
            if (null != terminalList && terminalList.size() > 0) {
                throw new SystemException(1000013, "MCU节点的删除，请先删除该节点下的终端！");
            }
        }
        {
            BusiTerminal busiTerminal = new BusiTerminal();
            busiTerminal.setType(TerminalType.ZJ_SIP.getId());
            busiTerminal.setZjServerId(busiMcuZjDept.getMcuId());

            List<BusiTerminal> terminalList = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
            if (null != terminalList && terminalList.size() > 0) {
                throw new SystemException(1000013, "MCU节点的删除，请先删除该节点下的终端！");
            }
        }
        int c = busiMcuZjDeptMapper.deleteBusiMcuZjDeptById(id);
        if (c > 0)
        {
            DeptMcuZjMappingCache.getInstance().remove(busiMcuZjDept.getDeptId());
        }
        return c;
    }
    
    public ModelBean toModelBean(BusiMcuZjDept busiMcuZjDept)
    {
        ModelBean mb = new ModelBean(busiMcuZjDept);
        mb.put("deptName", SysDeptCache.getInstance().get(busiMcuZjDept.getDeptId()).getDeptName());
        mb.put("mcuTypeAcName", McuZjType.convert(busiMcuZjDept.getMcuType()).getName());
        StringBuilder fmeInfoBuilder = new StringBuilder();
        if (McuZjType.convert(busiMcuZjDept.getMcuType()) == McuZjType.CLUSTER)
        {
            BusiMcuZjCluster busiMcuZjCluster = McuZjClusterCache.getInstance().get(busiMcuZjDept.getMcuId());
            fmeInfoBuilder.append("【").append(busiMcuZjCluster.getName()).append("】");
        }
        else
        {
            McuZjBridge fmeBridge = McuZjBridgeCache.getInstance().get(busiMcuZjDept.getMcuId());
            fmeInfoBuilder.append("【").append(fmeBridge.getBusiMcuZj().getName()).append("】");
        }
        McuZjBridgeCollection fmeBridgeCollection = McuZjBridgeCache.getInstance().getAvailableMcuZjBridgesByDept(busiMcuZjDept.getDeptId());
        mb.put("existAvailableMcuBridge", fmeBridgeCollection != null);
        if (fmeBridgeCollection == null)
        {
            fmeInfoBuilder.append("-").append("当前无可用的MCU信息");
            mb.put("mcus", new ArrayList<>());
        }
        else
        {
            fmeInfoBuilder.append(McuType.MCU_ZJ.getAlias() + "[");
            
            List<String> fmes = new ArrayList<String>();
            StringBuilder fmeIpInfoBuilder = new StringBuilder();
            fmeBridgeCollection.getMcuZjBridges().forEach((fmeBridge) -> {
                if (!ObjectUtils.isEmpty(fmeIpInfoBuilder))
                {
                    fmeIpInfoBuilder.append(", ");
                }
                fmeIpInfoBuilder.append(fmeBridge.getBusiMcuZj().getIp());
                
                fmes.add(fmeBridge.getBusiMcuZj().getIp());
            });
            fmeInfoBuilder.append(fmeIpInfoBuilder);
            fmeInfoBuilder.append("]");
            mb.put("mcus", fmes);
        }
        mb.put("mcuInfo", fmeInfoBuilder.toString());
        return mb;
    }
}
