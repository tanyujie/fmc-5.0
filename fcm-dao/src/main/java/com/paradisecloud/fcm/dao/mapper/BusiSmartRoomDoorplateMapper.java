package com.paradisecloud.fcm.dao.mapper;


import com.paradisecloud.fcm.dao.model.BusiSmartRoomDoorplate;

import java.util.List;

/**
 * 智慧办公房间门牌Mapper接口
 *
 * @author lilinhai
 * @date 2024-02-19
 */
public interface BusiSmartRoomDoorplateMapper
{
    /**
     * 查询智慧办公房间门牌
     *
     * @param id 智慧办公房间门牌ID
     * @return 智慧办公房间门牌
     */
    public BusiSmartRoomDoorplate selectBusiSmartRoomDoorplateById(Long id);

    /**
     * 查询智慧办公房间门牌列表
     *
     * @param busiSmartRoomDoorplate 智慧办公房间门牌
     * @return 智慧办公房间门牌集合
     */
    public List<BusiSmartRoomDoorplate> selectBusiSmartRoomDoorplateList(BusiSmartRoomDoorplate busiSmartRoomDoorplate);

    /**
     * 新增智慧办公房间门牌
     *
     * @param busiSmartRoomDoorplate 智慧办公房间门牌
     * @return 结果
     */
    public int insertBusiSmartRoomDoorplate(BusiSmartRoomDoorplate busiSmartRoomDoorplate);

    /**
     * 修改智慧办公房间门牌
     *
     * @param busiSmartRoomDoorplate 智慧办公房间门牌
     * @return 结果
     */
    public int updateBusiSmartRoomDoorplate(BusiSmartRoomDoorplate busiSmartRoomDoorplate);

    /**
     * 删除智慧办公房间门牌
     *
     * @param id 智慧办公房间门牌ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDoorplateById(Long id);

    /**
     * 批量删除智慧办公房间门牌
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDoorplateByIds(Long[] ids);

    List<BusiSmartRoomDoorplate> selectNotBound(Long roomId);
}