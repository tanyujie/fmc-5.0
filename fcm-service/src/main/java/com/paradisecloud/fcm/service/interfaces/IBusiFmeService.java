package com.paradisecloud.fcm.service.interfaces;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiFme;

/**
 * FME终端信息Service接口
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
public interface IBusiFmeService 
{
    /**
     * 查询FME终端信息
     * 
     * @param id FME终端信息ID
     * @return FME终端信息
     */
    public BusiFme selectBusiFmeById(Long id);

    /**
     * 查询FME终端信息列表
     * 
     * @param busiFme FME终端信息
     * @return FME终端信息集合
     */
    public List<BusiFme> selectBusiFmeList(BusiFme busiFme);

    /**
     * 新增FME终端信息
     * 
     * @param busiFme FME终端信息
     * @return 结果
     */
    public int insertBusiFme(BusiFme busiFme);

    /**
     * 修改FME终端信息
     * 
     * @param busiFme FME终端信息
     * @return 结果
     */
    public int updateBusiFme(BusiFme busiFme);

    /**
     * 批量删除FME终端信息
     * 
     * @param ids 需要删除的FME终端信息ID
     * @return 结果
     */
    public int deleteBusiFmeByIds(Long[] ids);

    /**
     * 删除FME终端信息信息
     * 
     * @param id FME终端信息ID
     * @return 结果
     */
    public int deleteBusiFmeById(Long id);
}
