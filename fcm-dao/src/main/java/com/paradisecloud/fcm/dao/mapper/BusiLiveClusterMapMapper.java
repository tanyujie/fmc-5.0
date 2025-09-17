package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiLiveClusterMap;

import java.util.List;

/**
 * 直播服务器-直播集群组中间（多对多）Mapper接口
 * 
 * @author lilinhai
 * @date 2022-10-26
 */
public interface BusiLiveClusterMapMapper 
{
    /**
     * 查询直播服务器-直播集群组中间（多对多）
     * 
     * @param id 直播服务器-直播集群组中间（多对多）ID
     * @return 直播服务器-直播集群组中间（多对多）
     */
    public BusiLiveClusterMap selectBusiLiveClusterMapById(Long id);

    /**
     * 查询直播服务器-直播集群组中间（多对多）列表
     * 
     * @param busiLiveClusterMap 直播服务器-直播集群组中间（多对多）
     * @return 直播服务器-直播集群组中间（多对多）集合
     */
    public List<BusiLiveClusterMap> selectBusiLiveClusterMapList(BusiLiveClusterMap busiLiveClusterMap);

    /**
     * 新增直播服务器-直播集群组中间（多对多）
     * 
     * @param busiLiveClusterMap 直播服务器-直播集群组中间（多对多）
     * @return 结果
     */
    public int insertBusiLiveClusterMap(BusiLiveClusterMap busiLiveClusterMap);

    /**
     * 修改直播服务器-直播集群组中间（多对多）
     * 
     * @param busiLiveClusterMap 直播服务器-直播集群组中间（多对多）
     * @return 结果
     */
    public int updateBusiLiveClusterMap(BusiLiveClusterMap busiLiveClusterMap);

    /**
     * 删除直播服务器-直播集群组中间（多对多）
     * 
     * @param id 直播服务器-直播集群组中间（多对多）ID
     * @return 结果
     */
    public int deleteBusiLiveClusterMapById(Long id);

    /**
     * 批量删除直播服务器-直播集群组中间（多对多）
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiLiveClusterMapByIds(Long[] ids);
}
