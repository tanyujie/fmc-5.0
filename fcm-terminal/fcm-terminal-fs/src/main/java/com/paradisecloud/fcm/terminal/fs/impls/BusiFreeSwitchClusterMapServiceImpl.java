package com.paradisecloud.fcm.terminal.fs.impls;

import java.util.Date;
import java.util.List;

import com.paradisecloud.fcm.common.enumer.FcmType;
import com.paradisecloud.fcm.dao.mapper.BusiFreeSwitchClusterMapMapper;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchClusterMap;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.interfaces.IBusiFreeSwitchClusterMapService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.sinhy.exception.SystemException;

import javax.annotation.Resource;

/**
 * FreeSwitch-终端组中间（多对多）Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-03-17
 */
@Service
public class BusiFreeSwitchClusterMapServiceImpl implements IBusiFreeSwitchClusterMapService 
{
    @Resource
    private BusiFreeSwitchClusterMapMapper busiFreeSwitchClusterMapMapper;

    /**
     * 查询FreeSwitch-终端组中间（多对多）
     * 
     * @param id FreeSwitch-终端组中间（多对多）ID
     * @return FreeSwitch-终端组中间（多对多）
     */
    @Override
    public BusiFreeSwitchClusterMap selectBusiFreeSwitchClusterMapById(Long id)
    {
        return busiFreeSwitchClusterMapMapper.selectBusiFreeSwitchClusterMapById(id);
    }

    /**
     * 查询FreeSwitch-终端组中间（多对多）列表
     * 
     * @param busiFreeSwitchClusterMap FreeSwitch-终端组中间（多对多）
     * @return FreeSwitch-终端组中间（多对多）
     */
    @Override
    public List<BusiFreeSwitchClusterMap> selectBusiFreeSwitchClusterMapList(BusiFreeSwitchClusterMap busiFreeSwitchClusterMap)
    {
        return busiFreeSwitchClusterMapMapper.selectBusiFreeSwitchClusterMapList(busiFreeSwitchClusterMap);
    }

    /**
     * 新增FreeSwitch-终端组中间（多对多）
     * 
     * @param busiFreeSwitchClusterMap FreeSwitch-终端组中间（多对多）
     * @return 结果
     */
    @Override
    public int insertBusiFreeSwitchClusterMap(BusiFreeSwitchClusterMap busiFreeSwitchClusterMap)
    {
        try
        {
            busiFreeSwitchClusterMap.setCreateTime(new Date());
            int c = busiFreeSwitchClusterMapMapper.insertBusiFreeSwitchClusterMap(busiFreeSwitchClusterMap);
            if (c > 0)
            {
                FcmBridgeCache.getInstance().update(busiFreeSwitchClusterMap);
            }
            return c;
        }
        catch (Throwable e)
        {
            throw new SystemException(1004322, "同一个集群中，同一个FCM只能添加一次，且FCM权重不能相同！");
        }
    }
    
    /**
     *修改FreeSwitch-终端组中间（多对多）
     * 
     * @param busiFreeSwitchClusterMap FreeSwitch-终端组中间（多对多）
     * @return 结果
     */
    public int updateBusiFreeSwitchClusterMap(BusiFreeSwitchClusterMap busiFreeSwitchClusterMap)
    {
        BusiFreeSwitchClusterMap oldBusiFreeSwitchClusterMap = selectBusiFreeSwitchClusterMapById(busiFreeSwitchClusterMap.getId());
        busiFreeSwitchClusterMap.setUpdateTime(new Date());
        Assert.state(busiFreeSwitchClusterMap.getClusterId().longValue() == oldBusiFreeSwitchClusterMap.getClusterId().longValue(), "FCM节点归属集群不能改！");
        Assert.state(busiFreeSwitchClusterMap.getFreeSwitchId().longValue() == oldBusiFreeSwitchClusterMap.getFreeSwitchId().longValue(), "FCM集群节点指向的FCM实体ID不能改！");
        int c = busiFreeSwitchClusterMapMapper.updateBusiFreeSwitchClusterMap(busiFreeSwitchClusterMap);
        if (c > 0)
        {
            FcmBridgeCache.getInstance().update(selectBusiFreeSwitchClusterMapById(busiFreeSwitchClusterMap.getId()));
        }
        return c;
    }

    /**
     * 删除FreeSwitch-终端组中间（多对多）信息
     * 
     * @param id FreeSwitch-终端组中间（多对多）ID
     * @return 结果
     */
    @Override
    public int deleteBusiFreeSwitchClusterMapById(Long id)
    {
        BusiFreeSwitchClusterMap busiFreeSwitchClusterMap1 = busiFreeSwitchClusterMapMapper.selectBusiFreeSwitchClusterMapById(id);
        int bindDeptCount = DeptFcmMappingCache.getInstance().getBindDeptCount(FcmType.CLUSTER, busiFreeSwitchClusterMap1.getClusterId());
        if (bindDeptCount > 0) {
            throw new SystemException(1004322, "该集群已绑定租户，不能删除该集群下的FCM！");
        }
        BusiFreeSwitchClusterMap busiFreeSwitchClusterMap = selectBusiFreeSwitchClusterMapById(id);
        int c = busiFreeSwitchClusterMapMapper.deleteBusiFreeSwitchClusterMapById(id);
        if (c > 0)
        {
            FcmBridgeCache.getInstance().removeFcmFromCluster(busiFreeSwitchClusterMap);
        }
        return c;
    }
}
