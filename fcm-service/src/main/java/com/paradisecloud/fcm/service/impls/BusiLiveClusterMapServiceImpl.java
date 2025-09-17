package com.paradisecloud.fcm.service.impls;

import java.util.List;
import java.util.Date;

import com.paradisecloud.fcm.common.enumer.FcmType;
import com.paradisecloud.fcm.dao.mapper.BusiLiveClusterMapMapper;
import com.paradisecloud.fcm.dao.model.BusiLiveClusterMap;
import com.paradisecloud.fcm.service.interfaces.IBusiLiveClusterMapService;
import com.paradisecloud.fcm.terminal.fs.cache.LiveBridgeCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveClusterCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveDeptCache;
import com.sinhy.exception.SystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 直播服务器-直播集群组中间（多对多）Service业务层处理
 * 
 * @author lilinhai
 * @date 2022-10-26
 */
@Service
public class BusiLiveClusterMapServiceImpl implements IBusiLiveClusterMapService
{
    @Resource
    private BusiLiveClusterMapMapper busiLiveClusterMapMapper;

    /**
     * 查询直播服务器-直播集群组中间（多对多）
     * 
     * @param id 直播服务器-直播集群组中间（多对多）ID
     * @return 直播服务器-直播集群组中间（多对多）
     */
    @Override
    public BusiLiveClusterMap selectBusiLiveClusterMapById(Long id)
    {
        return busiLiveClusterMapMapper.selectBusiLiveClusterMapById(id);
    }

    /**
     * 查询直播服务器-直播集群组中间（多对多）列表
     * 
     * @param busiLiveClusterMap 直播服务器-直播集群组中间（多对多）
     * @return 直播服务器-直播集群组中间（多对多）
     */
    @Override
    public List<BusiLiveClusterMap> selectBusiLiveClusterMapList(BusiLiveClusterMap busiLiveClusterMap)
    {
        return busiLiveClusterMapMapper.selectBusiLiveClusterMapList(busiLiveClusterMap);
    }

    /**
     * 新增直播服务器-直播集群组中间（多对多）
     * 
     * @param busiLiveClusterMap 直播服务器-直播集群组中间（多对多）
     * @return 结果
     */
    @Override
    public int insertBusiLiveClusterMap(BusiLiveClusterMap busiLiveClusterMap) {
        BusiLiveClusterMap busiLiveClusterMapQuery = new BusiLiveClusterMap();
        busiLiveClusterMapQuery.setClusterId(busiLiveClusterMap.getClusterId());
//        List<BusiLiveClusterMap> busiLiveClusterMaps = busiLiveClusterMapMapper.selectBusiLiveClusterMapList(busiLiveClusterMapQuery);
//        if (busiLiveClusterMaps != null && busiLiveClusterMaps.size() > 0) {
//            if (busiLiveClusterMap.getLiveType() == 1) {
//                for (BusiLiveClusterMap liveClusterMap : busiLiveClusterMaps) {
//                    if (liveClusterMap.getLiveType() == 1) {
//                        liveClusterMap.setLiveType(0);
//                        busiLiveClusterMapMapper.updateBusiLiveClusterMap(liveClusterMap);
//                    }
//                }
//            }
//        } else {
            busiLiveClusterMap.setLiveType(1);
//        }
        try {
            busiLiveClusterMap.setCreateTime(new Date());
            int i = busiLiveClusterMapMapper.insertBusiLiveClusterMap(busiLiveClusterMap);
            if (i > 0) {
                LiveBridgeCache.getInstance().update(busiLiveClusterMap);
            }
            return i;
        }
        catch (Throwable e)
        {
            throw new SystemException(1004322, "同一个集群中，同一个直播服务器只能添加一次，且一个集群只能有一个推流服务器");
        }

    }

    /**
     * 修改直播服务器-直播集群组中间（多对多）
     * 
     * @param busiLiveClusterMap 直播服务器-直播集群组中间（多对多）
     * @return 结果
     */
    @Override
    public int updateBusiLiveClusterMap(BusiLiveClusterMap busiLiveClusterMap)
    {
//        if (busiLiveClusterMap.getLiveType() == 1) {
//            BusiLiveClusterMap busiLiveClusterMapQuery = new BusiLiveClusterMap();
//            busiLiveClusterMapQuery.setClusterId(busiLiveClusterMap.getClusterId());
//            List<BusiLiveClusterMap> busiLiveClusterMaps = busiLiveClusterMapMapper.selectBusiLiveClusterMapList(busiLiveClusterMapQuery);
//            if (busiLiveClusterMaps != null && busiLiveClusterMaps.size() > 0) {
//                for (BusiLiveClusterMap liveClusterMap : busiLiveClusterMaps) {
//                    if (liveClusterMap.getLiveType() == 1) {
//                        liveClusterMap.setLiveType(0);
//                        busiLiveClusterMapMapper.updateBusiLiveClusterMap(liveClusterMap);
//                    }
//                }
//            }
//        }
        busiLiveClusterMap.setLiveType(1);

        busiLiveClusterMap.setUpdateTime(new Date());
        int i = busiLiveClusterMapMapper.updateBusiLiveClusterMap(busiLiveClusterMap);
        if (i > 0) {
            LiveBridgeCache.getInstance().update(busiLiveClusterMap);
        }
        return i;
    }

    /**
     * 批量删除直播服务器-直播集群组中间（多对多）
     * 
     * @param ids 需要删除的直播服务器-直播集群组中间（多对多）ID
     * @return 结果
     */
    @Override
    public int deleteBusiLiveClusterMapByIds(Long[] ids)
    {
        int i = 0;
        for (Long id : ids) {
            BusiLiveClusterMap busiLiveClusterMap = busiLiveClusterMapMapper.selectBusiLiveClusterMapById(id);
            int bindDeptCount = LiveDeptCache.getInstance().getBindDeptCount(FcmType.CLUSTER, busiLiveClusterMap.getClusterId());
            if (bindDeptCount > 0 ) {
                throw new SystemException(1004322, "该集群已绑定租户，不能删除该集群下的直播服务器！");
            }
            i = busiLiveClusterMapMapper.deleteBusiLiveClusterMapById(id);
            if (i > 0) {
                LiveBridgeCache.getInstance().removeliveFromCluster(busiLiveClusterMap);
            }
        }
        return i;
    }

    /**
     * 删除直播服务器-直播集群组中间（多对多）信息
     * 
     * @param id 直播服务器-直播集群组中间（多对多）ID
     * @return 结果
     */
    @Override
    public int deleteBusiLiveClusterMapById(Long id)
    {
        return busiLiveClusterMapMapper.deleteBusiLiveClusterMapById(id);
    }
}
