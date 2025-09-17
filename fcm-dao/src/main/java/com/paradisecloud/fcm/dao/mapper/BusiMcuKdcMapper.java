package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuKdc;

import java.util.List;

/**
 * 紫荆MCU终端信息Mapper接口
 * 
 * @author lilinhai
 * @date 2022-10-10
 */
public interface BusiMcuKdcMapper 
{
    /**
     * 查询紫荆MCU终端信息
     * 
     * @param id 紫荆MCU终端信息ID
     * @return 紫荆MCU终端信息
     */
    public BusiMcuKdc selectBusiMcuKdcById(Long id);

    /**
     * 查询紫荆MCU终端信息列表
     * 
     * @param busiMcuKdc 紫荆MCU终端信息
     * @return 紫荆MCU终端信息集合
     */
    public List<BusiMcuKdc> selectBusiMcuKdcList(BusiMcuKdc busiMcuKdc);

    /**
     * 新增紫荆MCU终端信息
     * 
     * @param busiMcuKdc 紫荆MCU终端信息
     * @return 结果
     */
    public int insertBusiMcuKdc(BusiMcuKdc busiMcuKdc);

    /**
     * 修改紫荆MCU终端信息
     * 
     * @param busiMcuKdc 紫荆MCU终端信息
     * @return 结果
     */
    public int updateBusiMcuKdc(BusiMcuKdc busiMcuKdc);

    /**
     * 删除紫荆MCU终端信息
     * 
     * @param id 紫荆MCU终端信息ID
     * @return 结果
     */
    public int deleteBusiMcuKdcById(Long id);

    /**
     * 批量删除紫荆MCU终端信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuKdcByIds(Long[] ids);
}
