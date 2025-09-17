package com.paradisecloud.fcm.terminal.fsbc.service.impls;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.mapper.BusiFsbcRegistrationServerMapper;
import com.paradisecloud.fcm.dao.model.BusiFsbcRegistrationServer;
import com.paradisecloud.fcm.terminal.fsbc.cache.DeptFsbcMappingCache;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.fcm.terminal.fsbc.service.intefaces.IBusiFsbcRegistrationServerService;
import com.sinhy.exception.SystemException;

/**
 * 终端FSBC注册服务器Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-04-21
 */
@Service
public class BusiFsbcRegistrationServerServiceImpl implements IBusiFsbcRegistrationServerService 
{
    @Autowired
    private BusiFsbcRegistrationServerMapper busiFsbcRegistrationServerMapper;
    
    /**
     * 查询终端FSBC注册服务器
     * 
     * @param id 终端FSBC注册服务器ID
     * @return 终端FSBC注册服务器
     */
    @Override
    public BusiFsbcRegistrationServer selectBusiFsbcRegistrationServerById(Long id)
    {
        return busiFsbcRegistrationServerMapper.selectBusiFsbcRegistrationServerById(id);
    }

    /**
     * 查询终端FSBC注册服务器列表
     * 
     * @param busiFsbcRegistrationServer 终端FSBC注册服务器
     * @return 终端FSBC注册服务器
     */
    @Override
    public List<ModelBean> selectBusiFsbcRegistrationServerList(BusiFsbcRegistrationServer busiFsbcRegistrationServer)
    {
        List<ModelBean> mbs = new ArrayList<>();
        List<BusiFsbcRegistrationServer> s = busiFsbcRegistrationServerMapper.selectBusiFsbcRegistrationServerList(busiFsbcRegistrationServer);
        for (BusiFsbcRegistrationServer busiFsbcRegistrationServer2 : s)
        {
            FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(busiFsbcRegistrationServer2.getId());
            ModelBean mb = new ModelBean(busiFsbcRegistrationServer2);
            mb.remove("password");
            mb.put("bindCount", DeptFsbcMappingCache.getInstance().getBindDeptCount(busiFsbcRegistrationServer2.getId()));
            mb.put("params", fsbcBridge.getBusiFsbcRegistrationServer().getParams());
            mbs.add(mb);
        }
        return mbs;
    }

    /**
     * 新增终端FSBC注册服务器
     * 
     * @param busiFsbcRegistrationServer 终端FSBC注册服务器
     * @return 结果
     */
    @Override
    public int insertBusiFsbcRegistrationServer(BusiFsbcRegistrationServer busiFsbcRegistrationServer)
    {
        busiFsbcRegistrationServer.setCreateTime(new Date());
		
		if (ObjectUtils.isEmpty(busiFsbcRegistrationServer.getUsername()))
        {
		    busiFsbcRegistrationServer.setUsername("ttadmin");
        }
        
        if (ObjectUtils.isEmpty(busiFsbcRegistrationServer.getPassword()))
        {
            busiFsbcRegistrationServer.setPassword("tTcl0uds@cn");
        }
        
        if (ObjectUtils.isEmpty(busiFsbcRegistrationServer.getPort()))
        {
            busiFsbcRegistrationServer.setPort(443);
        }
        
        if (ObjectUtils.isEmpty(busiFsbcRegistrationServer.getSipPort()))
        {
            busiFsbcRegistrationServer.setSipPort(5060);
        }
		
        int c = busiFsbcRegistrationServerMapper.insertBusiFsbcRegistrationServer(busiFsbcRegistrationServer);
        if (c > 0)
        {
            FsbcBridgeCache.getInstance().update(new FsbcBridge(busiFsbcRegistrationServer));
        }
        
        return c;
    }

    /**
     * 修改终端FSBC注册服务器
     * 
     * @param busiFsbcRegistrationServer 终端FSBC注册服务器
     * @return 结果
     */
    @Override
    public int updateBusiFsbcRegistrationServer(BusiFsbcRegistrationServer busiFsbcRegistrationServer)
    {
        busiFsbcRegistrationServer.setUpdateTime(new Date());

        int c = busiFsbcRegistrationServerMapper.updateBusiFsbcRegistrationServer(busiFsbcRegistrationServer);
        if (c > 0)
        {
            BusiFsbcRegistrationServer busiFsbcRegistrationServer1 = busiFsbcRegistrationServerMapper.selectBusiFsbcRegistrationServerById(busiFsbcRegistrationServer.getId());
            FsbcBridgeCache.getInstance().update(new FsbcBridge(busiFsbcRegistrationServer1));
        }

        return c;
    }

    /**
     * 批量删除终端FSBC注册服务器
     * 
     * @param ids 需要删除的终端FSBC注册服务器ID
     * @return 结果
     */
    @Override
    public int deleteBusiFsbcRegistrationServerByIds(Long[] ids)
    {
        return busiFsbcRegistrationServerMapper.deleteBusiFsbcRegistrationServerByIds(ids);
    }

    /**
     * 删除终端FSBC注册服务器信息
     * 
     * @param id 终端FSBC注册服务器ID
     * @return 结果
     */
    @Override
    public int deleteBusiFsbcRegistrationServerById(Long id)
    {
        try
        {
            int c = busiFsbcRegistrationServerMapper.deleteBusiFsbcRegistrationServerById(id);
            FsbcBridgeCache.getInstance().remove(id);
            return c;
        }
        catch (Exception e)
        {
            throw new SystemException(1004354, "删除注册服务器失败，请确认该注册服务器未绑定任何租户！");
        }
    }
}
