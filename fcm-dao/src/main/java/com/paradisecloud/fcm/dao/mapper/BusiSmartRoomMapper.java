package com.paradisecloud.fcm.dao.mapper;


import com.paradisecloud.fcm.dao.model.BusiSmartRoom;

import java.util.List;

/**
 * 智慧办公房间Mapper接口
 *
 * @author lilinhai
 * @date 2024-02-19
 */
public interface BusiSmartRoomMapper
{
    /**
     * 查询智慧办公房间
     *
     * @param id 智慧办公房间ID
     * @return 智慧办公房间
     */
    public BusiSmartRoom selectBusiSmartRoomById(Long id);

    /**
     * 查询智慧办公房间列表
     *
     * @param busiSmartRoom 智慧办公房间
     * @return 智慧办公房间集合
     */
    public List<BusiSmartRoom> selectBusiSmartRoomList(BusiSmartRoom busiSmartRoom);

    /**
     * 新增智慧办公房间
     *
     * @param busiSmartRoom 智慧办公房间
     * @return 结果
     */
    public int insertBusiSmartRoom(BusiSmartRoom busiSmartRoom);

    /**
     * 修改智慧办公房间
     *
     * @param busiSmartRoom 智慧办公房间
     * @return 结果
     */
    public int updateBusiSmartRoom(BusiSmartRoom busiSmartRoom);

    /**
     * 删除智慧办公房间
     *
     * @param id 智慧办公房间ID
     * @return 结果
     */
    public int deleteBusiSmartRoomById(Long id);

    /**
     * 批量删除智慧办公房间
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSmartRoomByIds(Long[] ids);

    List<BusiSmartRoom> selectBusiSmartRoomListForDoorplateNotBound(Long doorplateId);
}