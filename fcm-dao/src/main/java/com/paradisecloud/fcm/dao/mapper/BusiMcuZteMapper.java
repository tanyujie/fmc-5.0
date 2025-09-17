package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuZte;

import java.util.List;

/**
 * 中兴MCU终端信息Mapper接口
 * 
 * @author lilinhai
 * @date 2024-04-09
 */
public interface BusiMcuZteMapper 
{
    /**
     * 查询中兴MCU终端信息
     * 
     * @param id 中兴MCU终端信息ID
     * @return 中兴MCU终端信息
     */
    public BusiMcuZte selectBusiMcuZteById(Long id);

    /**
     * 查询中兴MCU终端信息列表
     * 
     * @param busiMcuZte 中兴MCU终端信息
     * @return 中兴MCU终端信息集合
     */
    public List<BusiMcuZte> selectBusiMcuZteList(BusiMcuZte busiMcuZte);

    /**
     * 新增中兴MCU终端信息
     * 
     * @param busiMcuZte 中兴MCU终端信息
     * @return 结果
     */
    public int insertBusiMcuZte(BusiMcuZte busiMcuZte);

    /**
     * 修改中兴MCU终端信息
     * 
     * @param busiMcuZte 中兴MCU终端信息
     * @return 结果
     */
    public int updateBusiMcuZte(BusiMcuZte busiMcuZte);

    /**
     * 删除中兴MCU终端信息
     * 
     * @param id 中兴MCU终端信息ID
     * @return 结果
     */
    public int deleteBusiMcuZteById(Long id);

    /**
     * 批量删除中兴MCU终端信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuZteByIds(Long[] ids);
}
