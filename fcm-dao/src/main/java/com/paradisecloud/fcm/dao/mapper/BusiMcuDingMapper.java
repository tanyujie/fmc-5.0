package com.paradisecloud.fcm.dao.mapper;





import com.paradisecloud.fcm.dao.model.BusiMcuDing;

import java.util.List;

/**
 * Ding.0MCU终端信息Mapper接口
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
public interface BusiMcuDingMapper
{
    /**
     * 查询Ding.0MCU终端信息
     * 
     * @param id Ding.0MCU终端信息ID
     * @return Ding.0MCU终端信息
     */
    public BusiMcuDing selectBusiMcuDingById(Long id);

    /**
     * 查询Ding.0MCU终端信息列表
     * 
     * @param busiMcuDing Ding.0MCU终端信息
     * @return Ding.0MCU终端信息集合
     */
    public List<BusiMcuDing> selectBusiMcuDingList(BusiMcuDing busiMcuDing);

    /**
     * 新增Ding.0MCU终端信息
     * 
     * @param busiMcuDing Ding.0MCU终端信息
     * @return 结果
     */
    public int insertBusiMcuDing(BusiMcuDing busiMcuDing);

    /**
     * 修改Ding.0MCU终端信息
     * 
     * @param busiMcuDing Ding.0MCU终端信息
     * @return 结果
     */
    public int updateBusiMcuDing(BusiMcuDing busiMcuDing);

    /**
     * 删除Ding.0MCU终端信息
     * 
     * @param id Ding.0MCU终端信息ID
     * @return 结果
     */
    public int deleteBusiMcuDingById(Long id);

    /**
     * 批量删除Ding.0MCU终端信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuDingByIds(Long[] ids);
}
