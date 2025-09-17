package com.paradisecloud.fcm.huaweicloud.huaweicloud.service.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.mapper.BusiMcuHwcloudDeptMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudCluster;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudDept;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.*;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiMcuHwcloudDeptService;
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
public class BusiMcuHwcloudDeptServiceImpl implements IBusiMcuHwcloudDeptService
{
    @Resource
    private BusiMcuHwcloudDeptMapper busiMcuHwcloudDeptMapper;
    @Resource
    private BusiTerminalMapper busiTerminalMapper;

    @Override
    public ModelBean selectBusiMcuHwcloudDeptById(Long id)
    {
        return toModelBean(busiMcuHwcloudDeptMapper.selectBusiMcuHwcloudDeptById(id));
    }

    @Override
    public ModelBean selectBusiMcuHwcloudDeptByDeptId(Long deptId)
    {
        return toModelBean(busiMcuHwcloudDeptMapper.selectBusiMcuHwcloudDeptById(deptId));
    }

    /**
     * 查询MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）列表
     *
     * @param busiMcuHwcloudDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
    @Override
    public List<ModelBean> selectBusiMcuHwcloudDeptList(BusiMcuHwcloudDept busiMcuHwcloudDept)
    {
        List<BusiMcuHwcloudDept> gds = busiMcuHwcloudDeptMapper.selectBusiMcuHwcloudDeptList(busiMcuHwcloudDept);
        List<ModelBean> mbs = new ArrayList<>();
        for (BusiMcuHwcloudDept busiMcuHwcloudGroupDept2 : gds)
        {
            mbs.add(toModelBean(busiMcuHwcloudGroupDept2));
        }
        return mbs;
    }

    /**
     * 新增MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     *
     * @param busiMcuHwcloudDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int insertBusiMcuHwcloudDept(BusiMcuHwcloudDept busiMcuHwcloudDept)
    {
        busiMcuHwcloudDept.setCreateTime(new Date());
        if (DeptHwcloudMappingCache.getInstance().containsKey(busiMcuHwcloudDept.getDeptId()))
        {
            throw new SystemException(1002422, "该租户已绑定MCU，不能多次绑定！");
        }
        int c = busiMcuHwcloudDeptMapper.insertBusiMcuHwcloudDept(busiMcuHwcloudDept);
        if (c > 0)
        {
            DeptHwcloudMappingCache.getInstance().put(busiMcuHwcloudDept.getDeptId(), busiMcuHwcloudDept);
        }
        return c;

    }

    /**
     * 修改MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     *
     * @param busiMcuHwcloudDept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int updateBusiMcuHwcloudDept(BusiMcuHwcloudDept busiMcuHwcloudDept)
    {
        busiMcuHwcloudDept.setUpdateTime(new Date());
        int c = busiMcuHwcloudDeptMapper.updateBusiMcuHwcloudDept(busiMcuHwcloudDept);
        if (c > 0)
        {
            DeptHwcloudMappingCache.getInstance().put(busiMcuHwcloudDept.getDeptId(), busiMcuHwcloudDeptMapper.selectBusiMcuHwcloudDeptById(busiMcuHwcloudDept.getId()));
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
    public int deleteBusiMcuHwcloudDeptByIds(Long[] ids)
    {
        return busiMcuHwcloudDeptMapper.deleteBusiMcuHwcloudDeptByIds(ids);
    }

    /**
     * 删除MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）信息
     *
     * @param id MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuHwcloudDeptById(Long id)
    {
        BusiMcuHwcloudDept busiMcuHwcloudDept = busiMcuHwcloudDeptMapper.selectBusiMcuHwcloudDeptById(id);
        {
            BusiTerminal busiTerminal = new BusiTerminal();
            busiTerminal.setType(TerminalType.SMC_SIP.getId());
            busiTerminal.setDeptId(busiMcuHwcloudDept.getDeptId());

            List<BusiTerminal> terminalList = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
            if (null != terminalList && terminalList.size() > 0) {
                throw new SystemException(1000012, "MCU节点的删除，请先删除该节点下的终端！");
            }
        }
        {
            BusiTerminal busiTerminal = new BusiTerminal();
            busiTerminal.setType(TerminalType.SMC_NUMBER.getId());
            busiTerminal.setDeptId(busiMcuHwcloudDept.getDeptId());

            List<BusiTerminal> terminalList = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
            if (null != terminalList && terminalList.size() > 0) {
                throw new SystemException(1000012, "MCU节点的删除，请先删除该节点下的终端！");
            }
        }
        int c = busiMcuHwcloudDeptMapper.deleteBusiMcuHwcloudDeptById(id);
        if (c > 0)
        {
            DeptHwcloudMappingCache.getInstance().remove(busiMcuHwcloudDept.getDeptId());
        }
        return c;
    }

    @Override
    public ModelBean toModelBean(BusiMcuHwcloudDept busiMcuHwcloudDept)
    {
        ModelBean mb = new ModelBean(busiMcuHwcloudDept);
        mb.put("deptName", SysDeptCache.getInstance().get(busiMcuHwcloudDept.getDeptId()).getDeptName());
        mb.put("mcuTypeAcName", FmeType.convert(busiMcuHwcloudDept.getMcuType()).getName());
        StringBuilder fmeInfoBuilder = new StringBuilder();
        if (FmeType.convert(busiMcuHwcloudDept.getMcuType()) == FmeType.CLUSTER) {
            BusiMcuHwcloudCluster busiMcuSmc2Cluster = HwcloudClusterCache.getInstance().get(busiMcuHwcloudDept.getMcuId());
            if (busiMcuSmc2Cluster != null) {
                fmeInfoBuilder.append("【").append(busiMcuSmc2Cluster.getName()).append("】");
            }

        } else {
            HwcloudBridge fmeBridge = HwcloudBridgeCache.getInstance().getHwcloudBridgeMap().get(busiMcuHwcloudDept.getMcuId());
            if (fmeBridge != null) {
                fmeInfoBuilder.append("【").append(fmeBridge.getBusiHwcloud().getName()).append("】");
            }

        }
        HwcloudBridgeCollection fmeBridgeCollection = HwcloudBridgeCache.getInstance().getAvailableHwcloudBridgesByDept(busiMcuHwcloudDept.getDeptId());
        mb.put("existAvailableMcuBridge", fmeBridgeCollection != null);
        if (fmeBridgeCollection == null) {
            fmeInfoBuilder.append("-").append("当前无可用的MCU信息");
            mb.put("mcus", new ArrayList<>());
        } else {
            fmeInfoBuilder.append("MCU[");

            List<String> fmes = new ArrayList<String>();
            StringBuilder fmeIpInfoBuilder = new StringBuilder();
            fmeBridgeCollection.getHwcloudBridges().forEach((fmeBridge) -> {
                if (!ObjectUtils.isEmpty(fmeIpInfoBuilder)) {
                    fmeIpInfoBuilder.append(", ");
                }
                fmeIpInfoBuilder.append(fmeBridge.getBusiHwcloud().getAppId());

                fmes.add(fmeBridge.getBusiHwcloud().getAppId());
            });
            fmeInfoBuilder.append(fmeIpInfoBuilder);
            fmeInfoBuilder.append("]");
            mb.put("mcus", fmes);
        }
        mb.put("mcuInfo", fmeInfoBuilder.toString());
        return mb;
    }
}
