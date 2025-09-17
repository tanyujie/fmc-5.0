package com.paradisecloud.fcm.smartroom.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiSmartRoom;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDevice;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDeviceMap;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomVo;
import com.paradisecloud.fcm.smartroom.model.MeetingRoomInfo;

import java.util.List;

/**
 * 会议室Service接口
 * 
 * @author lilinhai
 * @date 2024-01-26
 */
public interface IBusiSmartRoomService
{
    /**
     * 查询会议室
     * 
     * @param id 会议室ID
     * @return 会议室
     */
    public BusiSmartRoom selectBusiSmartRoomById(Long id);

    /**
     * 查询会议室列表
     * 
     * @param busiSmartRoomVo 会议室
     * @return 会议室集合
     */
    public List<BusiSmartRoom> selectBusiSmartRoomList(BusiSmartRoomVo busiSmartRoomVo);

    /**
     * 新增会议室
     * 
     * @param busiSmartRoom 会议室
     * @return 结果
     */
    public int insertBusiSmartRoom(BusiSmartRoom busiSmartRoom);

    /**
     * 修改会议室
     * 
     * @param busiSmartRoom 会议室
     * @return 结果
     */
    public int updateBusiSmartRoom(BusiSmartRoom busiSmartRoom);

    /**
     * 批量删除会议室
     * 
     * @param ids 需要删除的会议室ID
     * @return 结果
     */
    public int deleteBusiSmartRoomByIds(Long[] ids);

    /**
     * 删除会议室信息
     * 
     * @param id 会议室ID
     * @return 结果
     */
    public int deleteBusiSmartRoomById(Long id);

    /**
     * 获取未绑定门牌智慧办公房间
     * @param doorplateId
     * @return
     */
    List<BusiSmartRoom> selectBusiSmartRoomListForDoorplateNotBound(Long doorplateId);

    /**
     * 查询智慧办公房间预约列表通过部门
     * @param deptId
     * @return
     */
    List<MeetingRoomInfo> getSmartRoomBookListByDeptId(Long deptId);

    /**
     * 查询房间下的设备列表
     * @param roomId
     * @return
     */
    List<BusiSmartRoomDevice> selectBusiSmartRoomBoundDevice(Long roomId);

    /**
     * 绑定设备到房间
     * @param busiSmartRoomDeviceMapList
     * @return
     */
    int bindDevice(List<BusiSmartRoomDeviceMap> busiSmartRoomDeviceMapList);

    /**
     * 房间解除设备绑定
     * @param busiSmartRoomDeviceMap
     * @return
     */
    int unBindDevice(BusiSmartRoomDeviceMap busiSmartRoomDeviceMap);
}
