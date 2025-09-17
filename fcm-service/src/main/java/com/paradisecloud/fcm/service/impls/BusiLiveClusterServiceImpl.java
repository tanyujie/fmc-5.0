package com.paradisecloud.fcm.service.impls;

import java.util.List;
import java.util.Date;

import com.paradisecloud.fcm.dao.mapper.BusiLiveClusterMapMapper;
import com.paradisecloud.fcm.dao.mapper.BusiLiveClusterMapper;
import com.paradisecloud.fcm.dao.mapper.BusiLiveDeptMapper;
import com.paradisecloud.fcm.dao.model.BusiLiveCluster;
import com.paradisecloud.fcm.dao.model.BusiLiveClusterMap;
import com.paradisecloud.fcm.dao.model.BusiLiveDept;
import com.paradisecloud.fcm.service.interfaces.IBusiLiveClusterService;
import com.paradisecloud.fcm.terminal.fs.cache.LiveBridgeCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveClusterCache;
import com.sinhy.exception.SystemException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 直播服务器集群Service业务层处理
 * 
 * @author lilinhai
 * @date 2022-10-26
 */
@Service
public class BusiLiveClusterServiceImpl implements IBusiLiveClusterService
{
    @Resource
    private BusiLiveClusterMapper busiLiveClusterMapper;

    @Resource
    private BusiLiveClusterMapMapper busiLiveClusterMapMapper;

    @Resource
    private BusiLiveDeptMapper busiLiveDeptMapper;

    /**
     * 查询直播服务器集群
     * 
     * @param id 直播服务器集群ID
     * @return 直播服务器集群
     */
    @Override
    public BusiLiveCluster selectBusiLiveClusterById(Long id)
    {
        return busiLiveClusterMapper.selectBusiLiveClusterById(id);
    }

    /**
     * 查询直播服务器集群列表
     * 
     * @param busiLiveCluster 直播服务器集群
     * @return 直播服务器集群
     */
    @Override
    public List<BusiLiveCluster> selectBusiLiveClusterList(BusiLiveCluster busiLiveCluster)
    {
        return busiLiveClusterMapper.selectBusiLiveClusterList(busiLiveCluster);
    }

    /**
     * 新增直播服务器集群
     * 
     * @param busiLiveCluster 直播服务器集群
     * @return 结果
     */
    @Override
    public int insertBusiLiveCluster(BusiLiveCluster busiLiveCluster)
    {
        busiLiveCluster.setCreateTime(new Date());
        int i = 0;
        i = busiLiveClusterMapper.insertBusiLiveCluster(busiLiveCluster);
        if (i > 0) {
            LiveClusterCache.getInstance().put(busiLiveCluster.getId(), busiLiveCluster);
        }
        return i;
    }

    /**
     * 修改直播服务器集群
     * 
     * @param busiLiveCluster 直播服务器集群
     * @return 结果
     */
    @Override
    public int updateBusiLiveCluster(BusiLiveCluster busiLiveCluster)
    {
        busiLiveCluster.setUpdateTime(new Date());
        int i = busiLiveClusterMapper.updateBusiLiveCluster(busiLiveCluster);
        if (i > 0) {
            LiveClusterCache.getInstance().put(busiLiveCluster.getId(), busiLiveCluster);
        }
        return i;
    }

    /**
     * 批量删除直播服务器集群
     * 
     * @param ids 需要删除的直播服务器集群ID
     * @param id
     * @return 结果
     */
    @Override
    public int deleteBusiLiveClusterByIds(Long[] ids) {
        int i = 0;
        for (Long id : ids) {
            BusiLiveDept busiLiveDept = new BusiLiveDept();
            busiLiveDept.setLiveId(id);
            busiLiveDept.setLiveType(100);
            List<BusiLiveDept> busiLiveDepts = busiLiveDeptMapper.selectBusiLiveDeptList(busiLiveDept);
            BusiLiveClusterMap busiLiveClusterMap = new BusiLiveClusterMap();
            busiLiveClusterMap.setClusterId(id);
            List<BusiLiveClusterMap> busiLiveClusterMaps1 = busiLiveClusterMapMapper.selectBusiLiveClusterMapList(busiLiveClusterMap);
            if (busiLiveDepts != null && busiLiveDepts.size() > 0) {
                throw new SystemException(1000016, "该直播服务器集群已绑定部门，不能删除！请先解除绑定");
            }
            if (busiLiveClusterMaps1 != null && busiLiveClusterMaps1.size() > 0) {
                throw new SystemException(1000016, "该直播服务器集群下已有直播服务器，不能删除！请先清空集群下的服务器");
            }

            i = busiLiveClusterMapper.deleteBusiLiveClusterById(id);
            if (i > 0) {
                LiveClusterCache.getInstance().remove(id);
            }
        }

        return i;
    }

    /**
     * 删除直播服务器集群信息
     * 
     * @param id 直播服务器集群ID
     * @return 结果
     */
    @Override
    public int deleteBusiLiveClusterById(Long id)
    {
        return busiLiveClusterMapper.deleteBusiLiveClusterById(id);
    }
}
