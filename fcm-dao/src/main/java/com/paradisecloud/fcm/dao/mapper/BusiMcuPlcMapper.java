package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuPlc;

import java.util.List;

/**
 * 紫荆MCU终端信息Mapper接口
 * 
 * @author lilinhai
 * @date 2022-10-10
 */
public interface BusiMcuPlcMapper 
{
    /**
     * 查询紫荆MCU终端信息
     * 
     * @param id 紫荆MCU终端信息ID
     * @return 紫荆MCU终端信息
     */
    public BusiMcuPlc selectBusiMcuPlcById(Long id);

    /**
     * 查询紫荆MCU终端信息列表
     * 
     * @param busiMcuPlc 紫荆MCU终端信息
     * @return 紫荆MCU终端信息集合
     */
    public List<BusiMcuPlc> selectBusiMcuPlcList(BusiMcuPlc busiMcuPlc);

    /**
     * 新增紫荆MCU终端信息
     * 
     * @param busiMcuPlc 紫荆MCU终端信息
     * @return 结果
     */
    public int insertBusiMcuPlc(BusiMcuPlc busiMcuPlc);

    /**
     * 修改紫荆MCU终端信息
     * 
     * @param busiMcuPlc 紫荆MCU终端信息
     * @return 结果
     */
    public int updateBusiMcuPlc(BusiMcuPlc busiMcuPlc);

    /**
     * 删除紫荆MCU终端信息
     * 
     * @param id 紫荆MCU终端信息ID
     * @return 结果
     */
    public int deleteBusiMcuPlcById(Long id);

    /**
     * 批量删除紫荆MCU终端信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuPlcByIds(Long[] ids);
}
