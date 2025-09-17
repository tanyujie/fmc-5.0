package com.paradisecloud.fcm.dao.mapper;





import com.paradisecloud.fcm.dao.model.BusiMcuHwcloud;

import java.util.List;

/**
 * Hwcloud.0MCU终端信息Mapper接口
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
public interface BusiMcuHwcloudMapper
{
    /**
     * 查询Hwcloud.0MCU终端信息
     * 
     * @param id Hwcloud.0MCU终端信息ID
     * @return Hwcloud.0MCU终端信息
     */
    public BusiMcuHwcloud selectBusiMcuHwcloudById(Long id);

    /**
     * 查询Hwcloud.0MCU终端信息列表
     * 
     * @param busiMcuHwcloud Hwcloud.0MCU终端信息
     * @return Hwcloud.0MCU终端信息集合
     */
    public List<BusiMcuHwcloud> selectBusiMcuHwcloudList(BusiMcuHwcloud busiMcuHwcloud);

    /**
     * 新增Hwcloud.0MCU终端信息
     * 
     * @param busiMcuHwcloud Hwcloud.0MCU终端信息
     * @return 结果
     */
    public int insertBusiMcuHwcloud(BusiMcuHwcloud busiMcuHwcloud);

    /**
     * 修改Hwcloud.0MCU终端信息
     * 
     * @param busiMcuHwcloud Hwcloud.0MCU终端信息
     * @return 结果
     */
    public int updateBusiMcuHwcloud(BusiMcuHwcloud busiMcuHwcloud);

    /**
     * 删除Hwcloud.0MCU终端信息
     * 
     * @param id Hwcloud.0MCU终端信息ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudById(Long id);

    /**
     * 批量删除Hwcloud.0MCU终端信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudByIds(Long[] ids);
}
