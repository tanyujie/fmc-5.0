package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiLiveCluster;

import java.util.List;

/**
 * 直播服务器集群Mapper接口
 * 
 * @author lilinhai
 * @date 2022-10-26
 */
public interface BusiLiveClusterMapper 
{
    /**
     * 查询直播服务器集群
     * 
     * @param id 直播服务器集群ID
     * @return 直播服务器集群
     */
    public BusiLiveCluster selectBusiLiveClusterById(Long id);

    /**
     * 查询直播服务器集群列表
     * 
     * @param busiLiveCluster 直播服务器集群
     * @return 直播服务器集群集合
     */
    public List<BusiLiveCluster> selectBusiLiveClusterList(BusiLiveCluster busiLiveCluster);

    /**
     * 新增直播服务器集群
     * 
     * @param busiLiveCluster 直播服务器集群
     * @return 结果
     */
    public int insertBusiLiveCluster(BusiLiveCluster busiLiveCluster);

    /**
     * 修改直播服务器集群
     * 
     * @param busiLiveCluster 直播服务器集群
     * @return 结果
     */
    public int updateBusiLiveCluster(BusiLiveCluster busiLiveCluster);

    /**
     * 删除直播服务器集群
     * 
     * @param id 直播服务器集群ID
     * @return 结果
     */
    public int deleteBusiLiveClusterById(Long id);

    /**
     * 批量删除直播服务器集群
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiLiveClusterByIds(Long[] ids);
}
