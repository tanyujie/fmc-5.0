package com.paradisecloud.fcm.smc2.setvice2.impls;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc2DeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2Cluster;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2Dept;
import com.paradisecloud.fcm.smc2.cache.*;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiMcuSmc2DeptService;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.exception.SystemException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）Service业务层处理
 *
 * @author lilinhai
 * @date 2021-02-17
 */
@Service
public class BusiMcuSmc2DeptServiceImpl implements IBusiMcuSmc2DeptService {
    @Resource
    private BusiMcuSmc2DeptMapper busiMcuSmc2DeptMapper;
    @Resource
    private BusiTerminalMapper busiTerminalMapper;

    @Override
    public ModelBean selectBusiMcuSmc2DeptById(Long id) {
        return toModelBean(busiMcuSmc2DeptMapper.selectBusiMcuSmc2DeptById(id));
    }

    @Override
    public ModelBean selectBusiMcuSmc2DeptByDeptId(Long deptId) {
        return toModelBean(busiMcuSmc2DeptMapper.selectBusiMcuSmc2DeptById(deptId));
    }

    /**
     * 查询MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）列表
     *
     * @param busiMcuSmc2Dept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
    @Override
    public List<ModelBean> selectBusiMcuSmc2DeptList(BusiMcuSmc2Dept busiMcuSmc2Dept) {
        List<BusiMcuSmc2Dept> gds = busiMcuSmc2DeptMapper.selectBusiMcuSmc2DeptList(busiMcuSmc2Dept);
        List<ModelBean> mbs = new ArrayList<>();
        for (BusiMcuSmc2Dept busiMcuSmc2GroupDept2 : gds) {
            mbs.add(toModelBean(busiMcuSmc2GroupDept2));
        }
        return mbs;
    }

    /**
     * 新增MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     *
     * @param busiMcuSmc2Dept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int insertBusiMcuSmc2Dept(BusiMcuSmc2Dept busiMcuSmc2Dept) {
        busiMcuSmc2Dept.setCreateTime(new Date());
        if (DeptSmc2MappingCache.getInstance().containsKey(busiMcuSmc2Dept.getDeptId())) {
            throw new SystemException(1002422, "该租户已绑定MCU，不能多次绑定！");
        }
        int c = busiMcuSmc2DeptMapper.insertBusiMcuSmc2Dept(busiMcuSmc2Dept);
        if (c > 0) {
            DeptSmc2MappingCache.getInstance().put(busiMcuSmc2Dept.getDeptId(), busiMcuSmc2Dept);
        }
        return c;

    }

    /**
     * 修改MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     *
     * @param busiMcuSmc2Dept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int updateBusiMcuSmc2Dept(BusiMcuSmc2Dept busiMcuSmc2Dept) {
        busiMcuSmc2Dept.setUpdateTime(new Date());
        int c = busiMcuSmc2DeptMapper.updateBusiMcuSmc2Dept(busiMcuSmc2Dept);
        if (c > 0) {
            DeptSmc2MappingCache.getInstance().put(busiMcuSmc2Dept.getDeptId(), busiMcuSmc2DeptMapper.selectBusiMcuSmc2DeptById(busiMcuSmc2Dept.getId()));
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
    public int deleteBusiMcuSmc2DeptByIds(Long[] ids) {
        return busiMcuSmc2DeptMapper.deleteBusiMcuSmc2DeptByIds(ids);
    }

    /**
     * 删除MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）信息
     *
     * @param id MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuSmc2DeptById(Long id) {


        BusiMcuSmc2Dept busiMcuSmc2Dept = busiMcuSmc2DeptMapper.selectBusiMcuSmc2DeptById(id);
        Collection<Smc2ConferenceContext> values = Smc2ConferenceContextCache.getInstance().values();
        if (CollectionUtils.isEmpty(values)) {
            int c = busiMcuSmc2DeptMapper.deleteBusiMcuSmc2DeptById(id);
            if (c > 0) {
                DeptSmc2MappingCache.getInstance().remove(busiMcuSmc2Dept.getDeptId());
            }
            return c;
        } else {
            {
                Smc2Bridge bridgesByDept = Smc2BridgeCache.getInstance().getBridgesByDept(busiMcuSmc2Dept.getDeptId());
                if (!CollectionUtils.isEmpty(values)) {
                    for (Smc2ConferenceContext value : values) {
                        if (value.isStart()) {
                            Long id1 = value.getSmc2Bridge().getBusiSmc2().getId();
                            Long id2 = bridgesByDept.getBusiSmc2().getId();
                            if (Objects.equals(id1, id2)) {
                                throw new CustomException("MCU节点的下有正在召开的会议不能解绑！");
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public ModelBean toModelBean(BusiMcuSmc2Dept busiMcuSmc2Dept) {
        ModelBean mb = new ModelBean(busiMcuSmc2Dept);
        mb.put("deptName", SysDeptCache.getInstance().get(busiMcuSmc2Dept.getDeptId()).getDeptName());
        mb.put("mcuTypeAcName", FmeType.convert(busiMcuSmc2Dept.getMcuType()).getName());
        StringBuilder fmeInfoBuilder = new StringBuilder();
        if (FmeType.convert(busiMcuSmc2Dept.getMcuType()) == FmeType.CLUSTER) {
            BusiMcuSmc2Cluster busiMcuSmc2Cluster = Smc2ClusterCache.getInstance().get(busiMcuSmc2Dept.getMcuId());
            if (busiMcuSmc2Cluster != null) {
                fmeInfoBuilder.append("【").append(busiMcuSmc2Cluster.getName()).append("】");
            }

        } else {
            Smc2Bridge fmeBridge = Smc2BridgeCache.getInstance().getSmc2BridgeMap().get(busiMcuSmc2Dept.getMcuId());
            if (fmeBridge != null) {
                fmeInfoBuilder.append("【").append(fmeBridge.getBusiSmc2().getName()).append("】");
            }

        }
        Smc2BridgeCollection fmeBridgeCollection = Smc2BridgeCache.getInstance().getAvailableSmc2BridgesByDept(busiMcuSmc2Dept.getDeptId());
        mb.put("existAvailableMcuBridge", fmeBridgeCollection != null);
        if (fmeBridgeCollection == null) {
            fmeInfoBuilder.append("-").append("当前无可用的MCU信息");
            mb.put("mcus", new ArrayList<>());
        } else {
            fmeInfoBuilder.append("MCU[");

            List<String> fmes = new ArrayList<String>();
            StringBuilder fmeIpInfoBuilder = new StringBuilder();
            fmeBridgeCollection.getSmc2Bridges().forEach((fmeBridge) -> {
                if (!ObjectUtils.isEmpty(fmeIpInfoBuilder)) {
                    fmeIpInfoBuilder.append(", ");
                }
                fmeIpInfoBuilder.append(fmeBridge.getBusiSmc2().getIp());

                fmes.add(fmeBridge.getBusiSmc2().getIp());
            });
            fmeInfoBuilder.append(fmeIpInfoBuilder);
            fmeInfoBuilder.append("]");
            mb.put("mcus", fmes);
        }
        mb.put("mcuInfo", fmeInfoBuilder.toString());
        return mb;
    }
}
