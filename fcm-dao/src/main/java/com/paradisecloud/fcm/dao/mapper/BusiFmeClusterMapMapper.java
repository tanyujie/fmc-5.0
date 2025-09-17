package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiFmeClusterMap;

/**
 * FME-终端组中间（多对多）Mapper接口
 * 
 * @author lilinhai
 * @date 2021-03-17
 */
public interface BusiFmeClusterMapMapper 
{
    /**
     * 查询FME-终端组中间（多对多）
     * 
     * @param id FME-终端组中间（多对多）ID
     * @return FME-终端组中间（多对多）
     */
    public BusiFmeClusterMap selectBusiFmeClusterMapById(Long id);

    /**
     * 查询FME-终端组中间（多对多）列表
     * 
     * @param busiFmeClusterMap FME-终端组中间（多对多）
     * @return FME-终端组中间（多对多）集合
     */
    public List<BusiFmeClusterMap> selectBusiFmeClusterMapList(BusiFmeClusterMap busiFmeClusterMap);

    /**
     * 新增FME-终端组中间（多对多）
     * 
     * @param busiFmeClusterMap FME-终端组中间（多对多）
     * @return 结果
     */
    public int insertBusiFmeClusterMap(BusiFmeClusterMap busiFmeClusterMap);

    /**
     * 修改FME-终端组中间（多对多）
     * 
     * @param busiFmeClusterMap FME-终端组中间（多对多）
     * @return 结果
     */
    public int updateBusiFmeClusterMap(BusiFmeClusterMap busiFmeClusterMap);

    /**
     * 删除FME-终端组中间（多对多）
     * 
     * @param id FME-终端组中间（多对多）ID
     * @return 结果
     */
    public int deleteBusiFmeClusterMapById(Long id);

    /**
     * 批量删除FME-终端组中间（多对多）
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiFmeClusterMapByIds(Long[] ids);
}
