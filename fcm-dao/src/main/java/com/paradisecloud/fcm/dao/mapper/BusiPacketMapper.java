package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiPacket;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author lilinhai
 * @date 2024-07-09
 */
public interface BusiPacketMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public BusiPacket selectBusiPacketById(Integer id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busiPacket 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<BusiPacket> selectBusiPacketList(BusiPacket busiPacket);

    /**
     * 新增【请填写功能名称】
     * 
     * @param busiPacket 【请填写功能名称】
     * @return 结果
     */
    public int insertBusiPacket(BusiPacket busiPacket);

    /**
     * 修改【请填写功能名称】
     * 
     * @param busiPacket 【请填写功能名称】
     * @return 结果
     */
    public int updateBusiPacket(BusiPacket busiPacket);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiPacketById(Integer id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiPacketByIds(Integer[] ids);
}
