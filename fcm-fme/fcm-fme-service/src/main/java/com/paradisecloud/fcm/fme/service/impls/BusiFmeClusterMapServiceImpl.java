package com.paradisecloud.fcm.fme.service.impls;

import java.util.Date;
import java.util.List;

import com.paradisecloud.fcm.common.enumer.FcmType;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.fme.cache.DeptFmeMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.paradisecloud.fcm.dao.mapper.BusiFmeClusterMapMapper;
import com.paradisecloud.fcm.dao.model.BusiFmeClusterMap;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.service.interfaces.IBusiFmeClusterMapService;
import com.sinhy.exception.SystemException;

/**
 * FME-终端组中间（多对多）Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-03-17
 */
@Service
public class BusiFmeClusterMapServiceImpl implements IBusiFmeClusterMapService 
{
    @Autowired
    private BusiFmeClusterMapMapper busiFmeClusterMapMapper;

    /**
     * 查询FME-终端组中间（多对多）
     * 
     * @param id FME-终端组中间（多对多）ID
     * @return FME-终端组中间（多对多）
     */
    @Override
    public BusiFmeClusterMap selectBusiFmeClusterMapById(Long id)
    {
        return busiFmeClusterMapMapper.selectBusiFmeClusterMapById(id);
    }

    /**
     * 查询FME-终端组中间（多对多）列表
     * 
     * @param busiFmeClusterMap FME-终端组中间（多对多）
     * @return FME-终端组中间（多对多）
     */
    @Override
    public List<BusiFmeClusterMap> selectBusiFmeClusterMapList(BusiFmeClusterMap busiFmeClusterMap)
    {
        return busiFmeClusterMapMapper.selectBusiFmeClusterMapList(busiFmeClusterMap);
    }

    /**
     * 新增FME-终端组中间（多对多）
     * 
     * @param busiFmeClusterMap FME-终端组中间（多对多）
     * @return 结果
     */
    @Override
    public int insertBusiFmeClusterMap(BusiFmeClusterMap busiFmeClusterMap)
    {
        try
        {
            busiFmeClusterMap.setCreateTime(new Date());
            int c = busiFmeClusterMapMapper.insertBusiFmeClusterMap(busiFmeClusterMap);
            if (c > 0)
            {
                FmeBridgeCache.getInstance().update(busiFmeClusterMap);
            }
            return c;
        }
        catch (Throwable e)
        {
            throw new SystemException(1004322, "同一个集群中，同一个FME只能添加一次，且FME权重不能相同！");
        }
    }
    
    /**
     *修改FME-终端组中间（多对多）
     * 
     * @param busiFmeClusterMap FME-终端组中间（多对多）
     * @return 结果
     */
    public int updateBusiFmeClusterMap(BusiFmeClusterMap busiFmeClusterMap)
    {
        BusiFmeClusterMap oldBusiFmeClusterMap = selectBusiFmeClusterMapById(busiFmeClusterMap.getId());
        busiFmeClusterMap.setUpdateTime(new Date());
        Assert.state(busiFmeClusterMap.getClusterId().longValue() == oldBusiFmeClusterMap.getClusterId().longValue(), "FME节点归属集群不能改！");
        Assert.state(busiFmeClusterMap.getFmeId().longValue() == oldBusiFmeClusterMap.getFmeId().longValue(), "FME集群节点指向的FME实体ID不能改！");
        int c = busiFmeClusterMapMapper.updateBusiFmeClusterMap(busiFmeClusterMap);
        if (c > 0)
        {
            FmeBridgeCache.getInstance().update(selectBusiFmeClusterMapById(busiFmeClusterMap.getId()));
        }
        return c;
    }

    /**
     * 删除FME-终端组中间（多对多）信息
     * 
     * @param id FME-终端组中间（多对多）ID
     * @return 结果
     */
    @Override
    public int deleteBusiFmeClusterMapById(Long id)
    {
        BusiFmeClusterMap busiFmeClusterMap = selectBusiFmeClusterMapById(id);
        int bindDeptCount = DeptFmeMappingCache.getInstance().getBindDeptCount(FmeType.CLUSTER, busiFmeClusterMap.getClusterId());
        if (bindDeptCount > 0) {
            throw new SystemException(1004322, "该集群已绑定租户，不能删除该集群下的FME！");
        }
        int c = busiFmeClusterMapMapper.deleteBusiFmeClusterMapById(id);
        if (c > 0)
        {
            FmeBridgeCache.getInstance().removeFmeFromCluster(busiFmeClusterMap);
        }
        return c;
    }
}
