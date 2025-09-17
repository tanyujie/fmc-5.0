package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiFmeCluster;

/**
 * FME集群Mapper接口
 * 
 * @author lilinhai
 * @date 2021-03-17
 */
public interface BusiFmeClusterMapper 
{
    /**
     * 查询FME集群
     * 
     * @param id FME集群ID
     * @return FME集群
     */
    public BusiFmeCluster selectBusiFmeClusterById(Long id);

    /**
     * 查询FME集群列表
     * 
     * @param busiFmeCluster FME集群
     * @return FME集群集合
     */
    public List<BusiFmeCluster> selectBusiFmeClusterList(BusiFmeCluster busiFmeCluster);

    /**
     * 新增FME集群
     * 
     * @param busiFmeCluster FME集群
     * @return 结果
     */
    public int insertBusiFmeCluster(BusiFmeCluster busiFmeCluster);

    /**
     * 修改FME集群
     * 
     * @param busiFmeCluster FME集群
     * @return 结果
     */
    public int updateBusiFmeCluster(BusiFmeCluster busiFmeCluster);

    /**
     * 删除FME集群
     * 
     * @param id FME集群ID
     * @return 结果
     */
    public int deleteBusiFmeClusterById(Long id);

    /**
     * 批量删除FME集群
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiFmeClusterByIds(Long[] ids);
}
