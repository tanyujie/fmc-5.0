package com.paradisecloud.fcm.fme.service.interfaces;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiFmeClusterMap;

/**
 * FME-终端组中间（多对多）Service接口
 * 
 * @author lilinhai
 * @date 2021-03-17
 */
public interface IBusiFmeClusterMapService 
{
    /**
     * 查询FME-终端组中间（多对多）
     * 
     * @param id FME-终端组中间（多对多）ID
     * @return FME-终端组中间（多对多）
     */
    BusiFmeClusterMap selectBusiFmeClusterMapById(Long id);

    /**
     * 查询FME-终端组中间（多对多）列表
     * 
     * @param busiFmeClusterMap FME-终端组中间（多对多）
     * @return FME-终端组中间（多对多）集合
     */
    List<BusiFmeClusterMap> selectBusiFmeClusterMapList(BusiFmeClusterMap busiFmeClusterMap);

    /**
     * 新增FME-终端组中间（多对多）
     * 
     * @param busiFmeClusterMap FME-终端组中间（多对多）
     * @return 结果
     */
    int insertBusiFmeClusterMap(BusiFmeClusterMap busiFmeClusterMap);
    
    /**
     *修改FME-终端组中间（多对多）
     * 
     * @param busiFmeClusterMap FME-终端组中间（多对多）
     * @return 结果
     */
    int updateBusiFmeClusterMap(BusiFmeClusterMap busiFmeClusterMap);

    /**
     * 删除FME-终端组中间（多对多）信息
     * 
     * @param id FME-终端组中间（多对多）ID
     * @return 结果
     */
    int deleteBusiFmeClusterMapById(Long id);
}
