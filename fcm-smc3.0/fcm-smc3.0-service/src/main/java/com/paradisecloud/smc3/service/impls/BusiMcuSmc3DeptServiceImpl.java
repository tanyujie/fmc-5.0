package com.paradisecloud.smc3.service.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.cache.*;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc3DeptMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3Cluster;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3Dept;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.service.interfaces.IBusiMcuSmc3DeptService;
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
 * @date 2021-03-17
 */
@Service
public class BusiMcuSmc3DeptServiceImpl implements IBusiMcuSmc3DeptService
{
    @Resource
    private BusiMcuSmc3DeptMapper busiMcuSmc3DeptMapper;
    @Resource
    private BusiTerminalMapper busiTerminalMapper;

    @Override
    public ModelBean selectBusiMcuSmc3DeptById(Long id)
    {
        return toModelBean(busiMcuSmc3DeptMapper.selectBusiMcuSmc3DeptById(id));
    }
    
    public ModelBean selectBusiMcuSmc3DeptByDeptId(Long deptId)
    {
        return toModelBean(busiMcuSmc3DeptMapper.selectBusiMcuSmc3DeptById(deptId));
    }
    
    /**
     * 查询MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）列表
     * 
     * @param busiMcuSmc3Dept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
    @Override
    public List<ModelBean> selectBusiMcuSmc3DeptList(BusiMcuSmc3Dept busiMcuSmc3Dept)
    {
        List<BusiMcuSmc3Dept> gds = busiMcuSmc3DeptMapper.selectBusiMcuSmc3DeptList(busiMcuSmc3Dept);
        List<ModelBean> mbs = new ArrayList<>();
        for (BusiMcuSmc3Dept busiMcuSmc3GroupDept2 : gds)
        {
            mbs.add(toModelBean(busiMcuSmc3GroupDept2));
        }
        return mbs;
    }

    /**
     * 新增MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuSmc3Dept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int insertBusiMcuSmc3Dept(BusiMcuSmc3Dept busiMcuSmc3Dept)
    {
        busiMcuSmc3Dept.setCreateTime(new Date());
        if (DeptSmc3MappingCache.getInstance().containsKey(busiMcuSmc3Dept.getDeptId()))
        {
            throw new SystemException(1003432, "该租户已绑定MCU，不能多次绑定！");
        }
        int c = busiMcuSmc3DeptMapper.insertBusiMcuSmc3Dept(busiMcuSmc3Dept);
        if (c > 0)
        {
            DeptSmc3MappingCache.getInstance().put(busiMcuSmc3Dept.getDeptId(), busiMcuSmc3Dept);
        }
        return c;
    
    }

    /**
     * 修改MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * 
     * @param busiMcuSmc3Dept MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int updateBusiMcuSmc3Dept(BusiMcuSmc3Dept busiMcuSmc3Dept)
    {
        busiMcuSmc3Dept.setUpdateTime(new Date());
        int c = busiMcuSmc3DeptMapper.updateBusiMcuSmc3Dept(busiMcuSmc3Dept);
        if (c > 0)
        {
            DeptSmc3MappingCache.getInstance().put(busiMcuSmc3Dept.getDeptId(), busiMcuSmc3DeptMapper.selectBusiMcuSmc3DeptById(busiMcuSmc3Dept.getId()));
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
    public int deleteBusiMcuSmc3DeptByIds(Long[] ids)
    {
        return busiMcuSmc3DeptMapper.deleteBusiMcuSmc3DeptByIds(ids);
    }

    /**
     * 删除MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）信息
     * 
     * @param id MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuSmc3DeptById(Long id)
    {
        BusiMcuSmc3Dept busiMcuSmc3Dept = busiMcuSmc3DeptMapper.selectBusiMcuSmc3DeptById(id);
        {
            BusiTerminal busiTerminal = new BusiTerminal();
            busiTerminal.setType(TerminalType.SMC_SIP.getId());
            busiTerminal.setDeptId(busiMcuSmc3Dept.getDeptId());

            List<BusiTerminal> terminalList = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
            if (null != terminalList && terminalList.size() > 0) {
                throw new SystemException(1000013, "MCU节点的删除，请先删除该节点下的终端！");
            }
        }
        {
            BusiTerminal busiTerminal = new BusiTerminal();
            busiTerminal.setType(TerminalType.SMC_NUMBER.getId());
            busiTerminal.setDeptId(busiMcuSmc3Dept.getDeptId());

            List<BusiTerminal> terminalList = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
            if (null != terminalList && terminalList.size() > 0) {
                throw new SystemException(1000013, "MCU节点的删除，请先删除该节点下的终端！");
            }
        }

        {

            Smc3Bridge bridgesByDept = Smc3BridgeCache.getInstance().getBridgesByDept(busiMcuSmc3Dept.getDeptId());
            Collection<Smc3ConferenceContext> values = Smc3ConferenceContextCache.getInstance().values();
            if(!CollectionUtils.isEmpty(values)){
                for (Smc3ConferenceContext value : values) {
                    if(value.isStart()){
                        Long id1 = value.getSmc3Bridge().getBusiSMC().getId();
                        Long id2 = bridgesByDept.getBusiSMC().getId();
                        if(Objects.equals(id1,id2)){
                            throw new SystemException(1000015, "MCU节点的下有正在召开的会议不能解绑！");
                        }

                    }
                }
            }
        }
        int c = busiMcuSmc3DeptMapper.deleteBusiMcuSmc3DeptById(id);
        if (c > 0)
        {
            DeptSmc3MappingCache.getInstance().remove(busiMcuSmc3Dept.getDeptId());
        }
        return c;
    }
    
    public ModelBean toModelBean(BusiMcuSmc3Dept busiMcuSmc3Dept)
    {
        ModelBean mb = new ModelBean(busiMcuSmc3Dept);
        mb.put("deptName", SysDeptCache.getInstance().get(busiMcuSmc3Dept.getDeptId()).getDeptName());
        mb.put("mcuTypeAcName", FmeType.convert(busiMcuSmc3Dept.getMcuType()).getName());
        StringBuilder fmeInfoBuilder = new StringBuilder();
        if (FmeType.convert(busiMcuSmc3Dept.getMcuType()) == FmeType.CLUSTER)
        {
            BusiMcuSmc3Cluster busiMcuSmc3Cluster = Smc3ClusterCache.getInstance().get(busiMcuSmc3Dept.getMcuId());
            fmeInfoBuilder.append("【").append(busiMcuSmc3Cluster.getName()).append("】");
        }
        else
        {
            Smc3Bridge fmeBridge = Smc3BridgeCache.getInstance().get(busiMcuSmc3Dept.getMcuId());
            fmeInfoBuilder.append("【").append(fmeBridge.getBusiSMC().getName()).append("】");
        }
        Smc3BridgeCollection fmeBridgeCollection = Smc3BridgeCache.getInstance().getAvailableSmc3BridgesByDept(busiMcuSmc3Dept.getDeptId());
        mb.put("existAvailableMcuBridge", fmeBridgeCollection != null);
        if (fmeBridgeCollection == null)
        {
            fmeInfoBuilder.append("-").append("当前无可用的MCU信息");
            mb.put("mcus", new ArrayList<>());
        }
        else
        {
            fmeInfoBuilder.append("SMC[");
            
            List<String> fmes = new ArrayList<String>();
            StringBuilder fmeIpInfoBuilder = new StringBuilder();
            fmeBridgeCollection.getSmc3Bridges().forEach((fmeBridge) -> {
                if (!ObjectUtils.isEmpty(fmeIpInfoBuilder))
                {
                    fmeIpInfoBuilder.append(", ");
                }
                fmeIpInfoBuilder.append(fmeBridge.getBusiSMC().getIp());
                
                fmes.add(fmeBridge.getBusiSMC().getIp());
            });
            fmeInfoBuilder.append(fmeIpInfoBuilder);
            fmeInfoBuilder.append("]");
            mb.put("mcus", fmes);
        }
        mb.put("mcuInfo", fmeInfoBuilder.toString());
        return mb;
    }
}
