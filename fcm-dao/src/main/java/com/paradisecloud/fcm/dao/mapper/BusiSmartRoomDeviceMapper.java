package com.paradisecloud.fcm.dao.mapper;


import com.paradisecloud.fcm.dao.model.BusiSmartRoomDevice;

import java.util.List;

/**
 * 智慧办公房间设备Mapper接口
 *
 * @author lilinhai
 * @date 2024-02-19
 */
public interface BusiSmartRoomDeviceMapper
{
    /**
     * 查询智慧办公房间设备
     *
     * @param id 智慧办公房间设备ID
     * @return 智慧办公房间设备
     */
    public BusiSmartRoomDevice selectBusiSmartRoomDeviceById(Long id);

    /**
     * 查询智慧办公房间设备列表
     *
     * @param busiSmartRoomDevice 智慧办公房间设备
     * @return 智慧办公房间设备集合
     */
    public List<BusiSmartRoomDevice> selectBusiSmartRoomDeviceList(BusiSmartRoomDevice busiSmartRoomDevice);

    /**
     * 新增智慧办公房间设备
     *
     * @param busiSmartRoomDevice 智慧办公房间设备
     * @return 结果
     */
    public int insertBusiSmartRoomDevice(BusiSmartRoomDevice busiSmartRoomDevice);

    /**
     * 修改智慧办公房间设备
     *
     * @param busiSmartRoomDevice 智慧办公房间设备
     * @return 结果
     */
    public int updateBusiSmartRoomDevice(BusiSmartRoomDevice busiSmartRoomDevice);

    /**
     * 删除智慧办公房间设备
     *
     * @param id 智慧办公房间设备ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDeviceById(Long id);

    /**
     * 批量删除智慧办公房间设备
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDeviceByIds(Long[] ids);

    /**
     * 查询未绑定的设备列表
     *
     * @return
     * @param busiSmartRoomDevice
     */
    public List<BusiSmartRoomDevice> selectBusiSmartRoomDeviceListForUnbind(BusiSmartRoomDevice busiSmartRoomDevice);

    /**
     * 查询未绑定的物联网设备列表
     *
     * @return
     */
    public List<BusiSmartRoomDevice> selectBusiSmartRoomDeviceListForUnbindLotDevice();
}