package com.paradisecloud.fcm.dao.mapper;


import com.paradisecloud.fcm.dao.model.BusiSmartRoomDoorplateRegister;

import java.util.List;

/**
 * 智慧办公房间门牌注册Mapper接口
 *
 * @author lilinhai
 * @date 2024-02-19
 */
public interface BusiSmartRoomDoorplateRegisterMapper
{
    /**
     * 查询智慧办公房间门牌注册
     *
     * @param id 智慧办公房间门牌注册ID
     * @return 智慧办公房间门牌注册
     */
    public BusiSmartRoomDoorplateRegister selectBusiSmartRoomDoorplateRegisterById(Long id);

    /**
     * 查询智慧办公房间门牌注册列表
     *
     * @param busiSmartRoomDoorplateRegister 智慧办公房间门牌注册
     * @return 智慧办公房间门牌注册集合
     */
    public List<BusiSmartRoomDoorplateRegister> selectBusiSmartRoomDoorplateRegisterList(BusiSmartRoomDoorplateRegister busiSmartRoomDoorplateRegister);

    /**
     * 新增智慧办公房间门牌注册
     *
     * @param busiSmartRoomDoorplateRegister 智慧办公房间门牌注册
     * @return 结果
     */
    public int insertBusiSmartRoomDoorplateRegister(BusiSmartRoomDoorplateRegister busiSmartRoomDoorplateRegister);

    /**
     * 修改智慧办公房间门牌注册
     *
     * @param busiSmartRoomDoorplateRegister 智慧办公房间门牌注册
     * @return 结果
     */
    public int updateBusiSmartRoomDoorplateRegister(BusiSmartRoomDoorplateRegister busiSmartRoomDoorplateRegister);

    /**
     * 删除智慧办公房间门牌注册
     *
     * @param id 智慧办公房间门牌注册ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDoorplateRegisterById(Long id);

    /**
     * 批量删除智慧办公房间门牌注册
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDoorplateRegisterByIds(Long[] ids);
}