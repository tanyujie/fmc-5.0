package com.paradisecloud.fcm.service.interfaces;
import com.paradisecloud.fcm.dao.model.BusiPacket;

import java.util.List;

/**
 * 抓包服务器Service接口
 * 
 * @author lilinhai
 * @date 2024-09-04
 */
public interface IBusiPacketService 
{
    /**
     * 查询抓包服务器
     * 
     * @param id 抓包服务器ID
     * @return 抓包服务器
     */
    public BusiPacket selectBusiPacketById(Integer id);

    /**
     * 查询抓包服务器列表
     * 
     * @param busiPacket 抓包服务器
     * @return 抓包服务器集合
     */
    public List<BusiPacket> selectBusiPacketList(BusiPacket busiPacket);

    /**
     * 新增抓包服务器
     * 
     * @param busiPacket 抓包服务器
     * @return 结果
     */
    public int insertBusiPacket(BusiPacket busiPacket);

    /**
     * 修改抓包服务器
     * 
     * @param busiPacket 抓包服务器
     * @return 结果
     */
    public int updateBusiPacket(BusiPacket busiPacket);

    /**
     * 批量删除抓包服务器
     * 
     * @param ids 需要删除的抓包服务器ID
     * @return 结果
     */
    public int deleteBusiPacketByIds(Integer[] ids);

    /**
     * 删除抓包服务器信息
     * 
     * @param id 抓包服务器ID
     * @return 结果
     */
    public int deleteBusiPacketById(Integer id);
}
