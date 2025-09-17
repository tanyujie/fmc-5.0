package com.paradisecloud.fcm.tencent.service2.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2Cluster;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.tencent.cache.*;
import com.paradisecloud.fcm.dao.mapper.BusiMcuTencentDeptMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentCluster;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentDept;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiMcuTencentDeptService;
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
public class BusiMcuTencentDeptServiceImpl implements IBusiMcuTencentDeptService
{
    @Resource
    private BusiMcuTencentDeptMapper busiMcuTencentDeptMapper;
    @Resource
    private BusiTerminalMapper busiTerminalMapper;

    @Override
    public ModelBean selectBusiMcuTencentDeptById(Long id)
    {
        return toModelBean(busiMcuTencentDeptMapper.selectBusiMcuTencentDeptById(id));
    }

    @Override
    public ModelBean selectBusiMcuTencentDeptByDeptId(Long deptId)
    {
        return toModelBean(busiMcuTencentDeptMapper.selectBusiMcuTencentDeptById(deptId));
    }

    /**
     * 查询MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）列表
     *
     * @param busiMcuTencentDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
    @Override
    public List<ModelBean> selectBusiMcuTencentDeptList(BusiMcuTencentDept busiMcuTencentDept)
    {
        List<BusiMcuTencentDept> gds = busiMcuTencentDeptMapper.selectBusiMcuTencentDeptList(busiMcuTencentDept);
        List<ModelBean> mbs = new ArrayList<>();
        for (BusiMcuTencentDept busiMcuTencentGroupDept2 : gds)
        {
            mbs.add(toModelBean(busiMcuTencentGroupDept2));
        }
        return mbs;
    }

    /**
     * 新增MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     *
     * @param busiMcuTencentDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int insertBusiMcuTencentDept(BusiMcuTencentDept busiMcuTencentDept)
    {
        busiMcuTencentDept.setCreateTime(new Date());
        if (DeptTencentMappingCache.getInstance().containsKey(busiMcuTencentDept.getDeptId()))
        {
            throw new SystemException(1002422, "该租户已绑定MCU，不能多次绑定！");
        }
        int c = busiMcuTencentDeptMapper.insertBusiMcuTencentDept(busiMcuTencentDept);
        if (c > 0)
        {
            DeptTencentMappingCache.getInstance().put(busiMcuTencentDept.getDeptId(), busiMcuTencentDept);
        }
        return c;

    }

    /**
     * 修改MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     *
     * @param busiMcuTencentDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int updateBusiMcuTencentDept(BusiMcuTencentDept busiMcuTencentDept)
    {
        busiMcuTencentDept.setUpdateTime(new Date());
        int c = busiMcuTencentDeptMapper.updateBusiMcuTencentDept(busiMcuTencentDept);
        if (c > 0)
        {
            DeptTencentMappingCache.getInstance().put(busiMcuTencentDept.getDeptId(), busiMcuTencentDeptMapper.selectBusiMcuTencentDeptById(busiMcuTencentDept.getId()));
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
    public int deleteBusiMcuTencentDeptByIds(Long[] ids)
    {
        return busiMcuTencentDeptMapper.deleteBusiMcuTencentDeptByIds(ids);
    }

    /**
     * 删除MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）信息
     *
     * @param id MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuTencentDeptById(Long id)
    {
        BusiMcuTencentDept busiMcuTencentDept = busiMcuTencentDeptMapper.selectBusiMcuTencentDeptById(id);
        {
            BusiTerminal busiTerminal = new BusiTerminal();
            busiTerminal.setType(TerminalType.SMC_SIP.getId());
            busiTerminal.setDeptId(busiMcuTencentDept.getDeptId());

            List<BusiTerminal> terminalList = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
            if (null != terminalList && terminalList.size() > 0) {
                throw new SystemException(1000012, "MCU节点的删除，请先删除该节点下的终端！");
            }
        }
        {
            BusiTerminal busiTerminal = new BusiTerminal();
            busiTerminal.setType(TerminalType.SMC_NUMBER.getId());
            busiTerminal.setDeptId(busiMcuTencentDept.getDeptId());

            List<BusiTerminal> terminalList = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
            if (null != terminalList && terminalList.size() > 0) {
                throw new SystemException(1000012, "MCU节点的删除，请先删除该节点下的终端！");
            }
        }
        int c = busiMcuTencentDeptMapper.deleteBusiMcuTencentDeptById(id);
        if (c > 0)
        {
            DeptTencentMappingCache.getInstance().remove(busiMcuTencentDept.getDeptId());
        }
        return c;
    }

    @Override
    public ModelBean toModelBean(BusiMcuTencentDept busiMcuTencentDept)
    {
        ModelBean mb = new ModelBean(busiMcuTencentDept);
        mb.put("deptName", SysDeptCache.getInstance().get(busiMcuTencentDept.getDeptId()).getDeptName());
        mb.put("mcuTypeAcName", FmeType.convert(busiMcuTencentDept.getMcuType()).getName());
        StringBuilder fmeInfoBuilder = new StringBuilder();
        if (FmeType.convert(busiMcuTencentDept.getMcuType()) == FmeType.CLUSTER) {
            BusiMcuTencentCluster busiMcuSmc2Cluster = TencentClusterCache.getInstance().get(busiMcuTencentDept.getMcuId());
            if (busiMcuSmc2Cluster != null) {
                fmeInfoBuilder.append("【").append(busiMcuSmc2Cluster.getName()).append("】");
            }

        } else {
            TencentBridge fmeBridge = TencentBridgeCache.getInstance().getTencentBridgeMap().get(busiMcuTencentDept.getMcuId());
            if (fmeBridge != null) {
                fmeInfoBuilder.append("【").append(fmeBridge.getBusiTencent().getName()).append("】");
            }

        }
        TencentBridgeCollection fmeBridgeCollection = TencentBridgeCache.getInstance().getAvailableTencentBridgesByDept(busiMcuTencentDept.getDeptId());
        mb.put("existAvailableMcuBridge", fmeBridgeCollection != null);
        if (fmeBridgeCollection == null) {
            fmeInfoBuilder.append("-").append("当前无可用的MCU信息");
            mb.put("mcus", new ArrayList<>());
        } else {
            fmeInfoBuilder.append("MCU[");

            List<String> fmes = new ArrayList<String>();
            StringBuilder fmeIpInfoBuilder = new StringBuilder();
            fmeBridgeCollection.getTencentBridges().forEach((fmeBridge) -> {
                if (!ObjectUtils.isEmpty(fmeIpInfoBuilder)) {
                    fmeIpInfoBuilder.append(", ");
                }
                fmeIpInfoBuilder.append(fmeBridge.getBusiTencent().getSdkId());

                fmes.add(fmeBridge.getBusiTencent().getSdkId());
            });
            fmeInfoBuilder.append(fmeIpInfoBuilder);
            fmeInfoBuilder.append("]");
            mb.put("mcus", fmes);
        }
        mb.put("mcuInfo", fmeInfoBuilder.toString());
        return mb;
    }
}
