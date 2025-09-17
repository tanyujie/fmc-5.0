package com.paradisecloud.fcm.smartroom.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiSmartRoomDevice;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomDeviceVo;

import java.util.List;

/**
 * 会议室设备Service接口
 * 
 * @author lilinhai
 * @date 2024-02-02
 */
public interface IBusiSmartRoomDeviceService
{
    /**
     * 查询会议室设备
     * 
     * @param id 会议室设备ID
     * @return 会议室设备
     */
    public BusiSmartRoomDevice selectBusiSmartRoomDeviceById(Long id);

    /**
     * 查询会议室设备列表
     * 
     * @param busiSmartRoomDevice 会议室设备
     * @return 会议室设备集合
     */
    public List<BusiSmartRoomDevice> selectBusiSmartRoomDeviceList(BusiSmartRoomDeviceVo busiSmartRoomDevice);

    /**
     * 新增会议室设备
     * 
     * @param busiSmartRoomDevice 会议室设备
     * @return 结果
     */
    public int insertBusiSmartRoomDevice(BusiSmartRoomDevice busiSmartRoomDevice);

    /**
     * 修改会议室设备
     * 
     * @param busiSmartRoomDevice 会议室设备
     * @return 结果
     */
    public int updateBusiSmartRoomDevice(BusiSmartRoomDevice busiSmartRoomDevice);

    /**
     * 批量删除会议室设备
     * 
     * @param ids 需要删除的会议室设备ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDeviceByIds(Long[] ids);

    /**
     * 删除会议室设备信息
     * 
     * @param id 会议室设备ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDeviceById(Long id);

    /**
     * 查询未绑定的其他设备列表
     *
     * @return
     */
    public List<BusiSmartRoomDevice> selectBusiSmartRoomDeviceListForUnbind(BusiSmartRoomDevice busiSmartRoomDevice);

    /**
     * 查询未绑定的物联网设备列表
     *
     * @return
     */
    public List<BusiSmartRoomDevice> selectBusiSmartRoomDeviceListForUnbindLotDevice();

    /**
     * 打开设备某通道电源
     *
     * @param id
     * @param channel
     */
    public boolean powerOnChannel(Long id, int channel);

    /**
     * 关闭设备某通道电源
     *
     * @param id
     * @param channel
     */
    public boolean powerOffChannel(Long id, int channel);
}
