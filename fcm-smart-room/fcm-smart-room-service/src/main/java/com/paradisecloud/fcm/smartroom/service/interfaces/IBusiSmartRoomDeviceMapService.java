package com.paradisecloud.fcm.smartroom.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiSmartRoomDeviceMap;

import java.util.List;

/**
 * 会议室设备关联Service接口
 * 
 * @author lilinhai
 * @date 2024-01-26
 */
public interface IBusiSmartRoomDeviceMapService
{
    /**
     * 查询会议室设备关联
     * 
     * @param id 会议室设备关联ID
     * @return 会议室设备关联
     */
    public BusiSmartRoomDeviceMap selectBusiSmartRoomDeviceMapById(Long id);

    /**
     * 查询会议室设备关联列表
     * 
     * @param busiSmartRoomDeviceMap 会议室设备关联
     * @return 会议室设备关联集合
     */
    public List<BusiSmartRoomDeviceMap> selectBusiSmartRoomDeviceMapList(BusiSmartRoomDeviceMap busiSmartRoomDeviceMap);

    /**
     * 新增会议室设备关联
     * 
     * @param busiSmartRoomDeviceMap 会议室设备关联
     * @return 结果
     */
    public int insertBusiSmartRoomDeviceMap(BusiSmartRoomDeviceMap busiSmartRoomDeviceMap);

    /**
     * 修改会议室设备关联
     * 
     * @param busiSmartRoomDeviceMap 会议室设备关联
     * @return 结果
     */
    public int updateBusiSmartRoomDeviceMap(BusiSmartRoomDeviceMap busiSmartRoomDeviceMap);

    /**
     * 批量删除会议室设备关联
     * 
     * @param ids 需要删除的会议室设备关联ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDeviceMapByIds(Long[] ids);

    /**
     * 删除会议室设备关联信息
     * 
     * @param id 会议室设备关联ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDeviceMapById(Long id);
}
