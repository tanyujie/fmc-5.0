package com.paradisecloud.fcm.dao.mapper;





import com.paradisecloud.fcm.dao.model.BusiMcuTencent;

import java.util.List;

/**
 * Tencent.0MCU终端信息Mapper接口
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
public interface BusiMcuTencentMapper 
{
    /**
     * 查询Tencent.0MCU终端信息
     * 
     * @param id Tencent.0MCU终端信息ID
     * @return Tencent.0MCU终端信息
     */
    public BusiMcuTencent selectBusiMcuTencentById(Long id);

    /**
     * 查询Tencent.0MCU终端信息列表
     * 
     * @param busiMcuTencent Tencent.0MCU终端信息
     * @return Tencent.0MCU终端信息集合
     */
    public List<BusiMcuTencent> selectBusiMcuTencentList(BusiMcuTencent busiMcuTencent);

    /**
     * 新增Tencent.0MCU终端信息
     * 
     * @param busiMcuTencent Tencent.0MCU终端信息
     * @return 结果
     */
    public int insertBusiMcuTencent(BusiMcuTencent busiMcuTencent);

    /**
     * 修改Tencent.0MCU终端信息
     * 
     * @param busiMcuTencent Tencent.0MCU终端信息
     * @return 结果
     */
    public int updateBusiMcuTencent(BusiMcuTencent busiMcuTencent);

    /**
     * 删除Tencent.0MCU终端信息
     * 
     * @param id Tencent.0MCU终端信息ID
     * @return 结果
     */
    public int deleteBusiMcuTencentById(Long id);

    /**
     * 批量删除Tencent.0MCU终端信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuTencentByIds(Long[] ids);
}
