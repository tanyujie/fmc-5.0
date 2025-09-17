package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiInfoDisplay;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;

import java.util.List;

/**
 * 信息展示Mapper接口
 * 
 * @author lilinhai
 * @date 2024-05-13
 */
public interface BusiInfoDisplayMapper 
{
    /**
     * 查询信息展示
     * 
     * @param id 信息展示ID
     * @return 信息展示
     */
    public BusiInfoDisplay selectBusiInfoDisplayById(Long id);

    /**
     * 查询信息展示列表
     * 
     * @param busiInfoDisplay 信息展示
     * @return 信息展示集合
     */
    public List<BusiInfoDisplay> selectBusiInfoDisplayList(BusiInfoDisplay busiInfoDisplay);

    /**
     * 新增信息展示
     * 
     * @param busiInfoDisplay 信息展示
     * @return 结果
     */
    public int insertBusiInfoDisplay(BusiInfoDisplay busiInfoDisplay);

    /**
     * 修改信息展示
     * 
     * @param busiInfoDisplay 信息展示
     * @return 结果
     */
    public int updateBusiInfoDisplay(BusiInfoDisplay busiInfoDisplay);

    /**
     * 删除信息展示
     * 
     * @param id 信息展示ID
     * @return 结果
     */
    public int deleteBusiInfoDisplayById(Long id);

    /**
     * 批量删除信息展示
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiInfoDisplayByIds(Long[] ids);

    List<DeptRecordCount> getDeptRecordCounts();
}
