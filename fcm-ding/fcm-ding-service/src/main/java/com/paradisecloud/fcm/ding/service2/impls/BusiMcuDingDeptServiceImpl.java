package com.paradisecloud.fcm.ding.service2.impls;

import com.paradisecloud.fcm.ding.service2.interfaces.IBusiMcuDingDeptService;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.mapper.BusiMcuDingDeptMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuDingCluster;
import com.paradisecloud.fcm.dao.model.BusiMcuDingDept;
import com.paradisecloud.fcm.ding.cache.*;
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
 * @date 2021-02-17
 */
@Service
public class BusiMcuDingDeptServiceImpl implements IBusiMcuDingDeptService
{
    @Resource
    private BusiMcuDingDeptMapper busiMcuDingDeptMapper;
    @Resource
    private BusiTerminalMapper busiTerminalMapper;

    @Override
    public ModelBean selectBusiMcuDingDeptById(Long id)
    {
        return toModelBean(busiMcuDingDeptMapper.selectBusiMcuDingDeptById(id));
    }

    @Override
    public ModelBean selectBusiMcuDingDeptByDeptId(Long deptId)
    {
        return toModelBean(busiMcuDingDeptMapper.selectBusiMcuDingDeptById(deptId));
    }

    /**
     * 查询MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）列表
     *
     * @param busiMcuDingDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
    @Override
    public List<ModelBean> selectBusiMcuDingDeptList(BusiMcuDingDept busiMcuDingDept)
    {
        List<BusiMcuDingDept> gds = busiMcuDingDeptMapper.selectBusiMcuDingDeptList(busiMcuDingDept);
        List<ModelBean> mbs = new ArrayList<>();
        for (BusiMcuDingDept busiMcuDingGroupDept2 : gds)
        {
            mbs.add(toModelBean(busiMcuDingGroupDept2));
        }
        return mbs;
    }

    /**
     * 新增MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     *
     * @param busiMcuDingDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int insertBusiMcuDingDept(BusiMcuDingDept busiMcuDingDept)
    {
        busiMcuDingDept.setCreateTime(new Date());
        if (DeptDingMappingCache.getInstance().containsKey(busiMcuDingDept.getDeptId()))
        {
            throw new SystemException(1002422, "该租户已绑定MCU，不能多次绑定！");
        }
        int c = busiMcuDingDeptMapper.insertBusiMcuDingDept(busiMcuDingDept);
        if (c > 0)
        {
            DeptDingMappingCache.getInstance().put(busiMcuDingDept.getDeptId(), busiMcuDingDept);
        }
        return c;

    }

    /**
     * 修改MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     *
     * @param busiMcuDingDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int updateBusiMcuDingDept(BusiMcuDingDept busiMcuDingDept)
    {
        busiMcuDingDept.setUpdateTime(new Date());
        int c = busiMcuDingDeptMapper.updateBusiMcuDingDept(busiMcuDingDept);
        if (c > 0)
        {
            DeptDingMappingCache.getInstance().put(busiMcuDingDept.getDeptId(), busiMcuDingDeptMapper.selectBusiMcuDingDeptById(busiMcuDingDept.getId()));
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
    public int deleteBusiMcuDingDeptByIds(Long[] ids)
    {
        return busiMcuDingDeptMapper.deleteBusiMcuDingDeptByIds(ids);
    }

    /**
     * 删除MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）信息
     *
     * @param id MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuDingDeptById(Long id)
    {
        BusiMcuDingDept busiMcuDingDept = busiMcuDingDeptMapper.selectBusiMcuDingDeptById(id);
        {
            BusiTerminal busiTerminal = new BusiTerminal();
            busiTerminal.setType(TerminalType.SMC_SIP.getId());
            busiTerminal.setDeptId(busiMcuDingDept.getDeptId());

            List<BusiTerminal> terminalList = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
            if (null != terminalList && terminalList.size() > 0) {
                throw new SystemException(1000012, "MCU节点的删除，请先删除该节点下的终端！");
            }
        }
        {
            BusiTerminal busiTerminal = new BusiTerminal();
            busiTerminal.setType(TerminalType.SMC_NUMBER.getId());
            busiTerminal.setDeptId(busiMcuDingDept.getDeptId());

            List<BusiTerminal> terminalList = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
            if (null != terminalList && terminalList.size() > 0) {
                throw new SystemException(1000012, "MCU节点的删除，请先删除该节点下的终端！");
            }
        }
        int c = busiMcuDingDeptMapper.deleteBusiMcuDingDeptById(id);
        if (c > 0)
        {
            DeptDingMappingCache.getInstance().remove(busiMcuDingDept.getDeptId());
        }
        return c;
    }

    @Override
    public ModelBean toModelBean(BusiMcuDingDept busiMcuDingDept)
    {
        ModelBean mb = new ModelBean(busiMcuDingDept);
        mb.put("deptName", SysDeptCache.getInstance().get(busiMcuDingDept.getDeptId()).getDeptName());
        mb.put("MzuDingTypeName", FmeType.convert(busiMcuDingDept.getMcuType()).getName());
        StringBuilder fmeInfoBuilder = new StringBuilder();
        if (FmeType.convert(busiMcuDingDept.getMcuType()) == FmeType.CLUSTER)
        {
            BusiMcuDingCluster busiMcuDingCluster = DingClusterCache.getInstance().get(busiMcuDingDept.getMcuId());
            fmeInfoBuilder.append("【").append(busiMcuDingCluster.getName()).append("】");
        }
        else
        {
            DingBridge fmeBridge = DingBridgeCache.getInstance().getDingBridgeMap().get(busiMcuDingDept.getMcuId());
            fmeInfoBuilder.append("【").append(fmeBridge.getBusiDing().getName()).append("】");
        }
        DingBridgeCollection fmeBridgeCollection = DingBridgeCache.getInstance().getAvailableDingBridgesByDept(busiMcuDingDept.getDeptId());
        mb.put("existAvailableMcuDingBridge", fmeBridgeCollection != null);
        if (fmeBridgeCollection == null)
        {
            fmeInfoBuilder.append("-").append("当前无可用的MCU信息");
            mb.put("McuDings", new ArrayList<>());
        }
        else
        {
            fmeInfoBuilder.append("MCU[");

            List<String> fmes = new ArrayList<String>();
            StringBuilder fmeIpInfoBuilder = new StringBuilder();
            fmeBridgeCollection.getDingBridges().forEach((fmeBridge) -> {
                if (!ObjectUtils.isEmpty(fmeIpInfoBuilder))
                {
                    fmeIpInfoBuilder.append(", ");
                }
                fmeIpInfoBuilder.append(fmeBridge.getBusiDing().getAppId());

                fmes.add(fmeBridge.getBusiDing().getAppId());
            });
            fmeInfoBuilder.append(fmeIpInfoBuilder);
            fmeInfoBuilder.append("]");
            mb.put("McuDings", fmes);
        }
        mb.put("McuDingInfo", fmeInfoBuilder.toString());
        return mb;
    }
}
