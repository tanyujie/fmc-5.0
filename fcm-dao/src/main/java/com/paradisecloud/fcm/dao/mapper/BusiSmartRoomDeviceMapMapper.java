package com.paradisecloud.fcm.dao.mapper;


import com.paradisecloud.fcm.dao.model.BusiSmartRoomDeviceMap;

import java.util.List;

/**
 * 智慧办公房间设备关联Mapper接口
 *
 * @author lilinhai
 * @date 2024-02-19
 */
public interface BusiSmartRoomDeviceMapMapper
{
    /**
     * 查询智慧办公房间设备关联
     *
     * @param id 智慧办公房间设备关联ID
     * @return 智慧办公房间设备关联
     */
    public BusiSmartRoomDeviceMap selectBusiSmartRoomDeviceMapById(Long id);

    /**
     * 查询智慧办公房间设备关联列表
     *
     * @param busiSmartRoomDeviceMap 智慧办公房间设备关联
     * @return 智慧办公房间设备关联集合
     */
    public List<BusiSmartRoomDeviceMap> selectBusiSmartRoomDeviceMapList(BusiSmartRoomDeviceMap busiSmartRoomDeviceMap);

    /**
     * 新增智慧办公房间设备关联
     *
     * @param busiSmartRoomDeviceMap 智慧办公房间设备关联
     * @return 结果
     */
    public int insertBusiSmartRoomDeviceMap(BusiSmartRoomDeviceMap busiSmartRoomDeviceMap);

    /**
     * 修改智慧办公房间设备关联
     *
     * @param busiSmartRoomDeviceMap 智慧办公房间设备关联
     * @return 结果
     */
    public int updateBusiSmartRoomDeviceMap(BusiSmartRoomDeviceMap busiSmartRoomDeviceMap);

    /**
     * 删除智慧办公房间设备关联
     *
     * @param id 智慧办公房间设备关联ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDeviceMapById(Long id);

    /**
     * 批量删除智慧办公房间设备关联
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDeviceMapByIds(Long[] ids);
}