package com.paradisecloud.fcm.terminal.fsbc.service.impls;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.dao.mapper.BusiFsbcServerDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiFsbcRegistrationServer;
import com.paradisecloud.fcm.dao.model.BusiFsbcServerDept;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.terminal.fsbc.cache.DeptFsbcMappingCache;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.service.intefaces.IBusiFsbcServerDeptService;
import com.sinhy.exception.SystemException;

/**
 * FSBC服务器-部门映射Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-04-21
 */
@Service
public class BusiFsbcServerDeptServiceImpl implements IBusiFsbcServerDeptService 
{
    @Autowired
    private BusiFsbcServerDeptMapper busiFsbcServerDeptMapper;
    
    @Autowired
    private BusiTerminalMapper busiTerminalMapper;
    
    /**
     * 查询FSBC服务器-部门映射
     * 
     * @param id FSBC服务器-部门映射ID
     * @return FSBC服务器-部门映射
     */
    @Override
    public BusiFsbcServerDept selectBusiFsbcServerDeptById(Long id)
    {
        return busiFsbcServerDeptMapper.selectBusiFsbcServerDeptById(id);
    }

    /**
     * 查询FSBC服务器-部门映射列表
     * 
     * @param busiFsbcServerDept FSBC服务器-部门映射
     * @return FSBC服务器-部门映射
     */
    @Override
    public List<ModelBean> selectBusiFsbcServerDeptList(BusiFsbcServerDept busiFsbcServerDept)
    {
        List<ModelBean> mbs = new ArrayList<>();
        List<BusiFsbcServerDept> fsbcMaps =  busiFsbcServerDeptMapper.selectBusiFsbcServerDeptList(busiFsbcServerDept);
        for (BusiFsbcServerDept m : fsbcMaps)
        {
            ModelBean mb = new ModelBean(m);
            BusiFsbcRegistrationServer s = FsbcBridgeCache.getInstance().getById(m.getFsbcServerId()).getBusiFsbcRegistrationServer();
            Integer status = (Integer)s.getParams().get("onlineStatus");
            boolean f = status != null && status.intValue() == TerminalOnlineStatus.ONLINE.getValue();
            if (f)
            {
                mb.put("fsbcInfo", "【" + s.getName() + "】" + s.getCallIp());
            }
            else
            {
                mb.put("fsbcInfo", "不可用【" + s.getName() + "】" + s.getCallIp());
            }
            mb.put("available", f);
            mbs.add(mb);
        }
        return mbs;
    }

    /**
     * 新增FSBC服务器-部门映射
     * 
     * @param busiFsbcServerDept FSBC服务器-部门映射
     * @return 结果
     */
    @Override
    public int insertBusiFsbcServerDept(BusiFsbcServerDept busiFsbcServerDept)
    {
        busiFsbcServerDept.setCreateTime(new Date());
        int c = busiFsbcServerDeptMapper.insertBusiFsbcServerDept(busiFsbcServerDept);
        if (c > 0)
        {
            DeptFsbcMappingCache.getInstance().put(busiFsbcServerDept.getDeptId(), busiFsbcServerDept);
        }
        return c;
    }

    /**
     * 修改FSBC服务器-部门映射
     * 
     * @param busiFsbcServerDept FSBC服务器-部门映射
     * @return 结果
     */
    @Override
    public int updateBusiFsbcServerDept(BusiFsbcServerDept busiFsbcServerDept)
    {
        busiFsbcServerDept.setUpdateTime(new Date());
        int c = busiFsbcServerDeptMapper.updateBusiFsbcServerDept(busiFsbcServerDept);
        if (c > 0)
        {
            busiFsbcServerDept = busiFsbcServerDeptMapper.selectBusiFsbcServerDeptById(busiFsbcServerDept.getId());
            DeptFsbcMappingCache.getInstance().put(busiFsbcServerDept.getDeptId(), busiFsbcServerDept);
        }
        return c;
    }

    /**
     * 批量删除FSBC服务器-部门映射
     * 
     * @param ids 需要删除的FSBC服务器-部门映射ID
     * @return 结果
     */
    @Override
    public int deleteBusiFsbcServerDeptByIds(Long[] ids)
    {
        return busiFsbcServerDeptMapper.deleteBusiFsbcServerDeptByIds(ids);
    }

    /**
     * 删除FSBC服务器-部门映射信息
     * 
     * @param id FSBC服务器-部门映射ID
     * @return 结果
     */
    @Override
    public int deleteBusiFsbcServerDeptById(Long id)
    {
        BusiFsbcServerDept busiFsbcServerDept = busiFsbcServerDeptMapper.selectBusiFsbcServerDeptById(id);
        if (busiFsbcServerDept == null)
        {
            return 0;
        }
        
        BusiTerminal con = new BusiTerminal();
        con.setDeptId(busiFsbcServerDept.getDeptId());
        con.setFsbcServerId(busiFsbcServerDept.getFsbcServerId());
        List<BusiTerminal> busiTerminals = busiTerminalMapper.selectBusiTerminalList(con);
        if (!ObjectUtils.isEmpty(busiTerminals))
        {
            throw new SystemException(1005434, "解绑租户fsbc服务器失败，该租户在该fsbc服务器中存在账号数据，请先删除后，再进行解绑！");
        }
        
        int c = busiFsbcServerDeptMapper.deleteBusiFsbcServerDeptById(id);
        if (c > 0)
        {
            DeptFsbcMappingCache.getInstance().remove(busiFsbcServerDept.getDeptId());
        }
        return c;
    }
}
