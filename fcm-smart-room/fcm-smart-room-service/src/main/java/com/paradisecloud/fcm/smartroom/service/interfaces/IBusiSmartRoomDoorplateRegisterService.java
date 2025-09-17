package com.paradisecloud.fcm.smartroom.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiSmartRoomDoorplateRegister;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomDoorplateRegisterVO;

import java.util.List;;

/**
 * 会议室门牌注册Service接口
 * 
 * @author lilinhai
 * @date 2024-01-26
 */
public interface IBusiSmartRoomDoorplateRegisterService
{
    /**
     * 查询会议室门牌注册
     * 
     * @param id 会议室门牌注册ID
     * @return 会议室门牌注册
     */
    public BusiSmartRoomDoorplateRegister selectBusiSmartRoomDoorplateRegisterById(Long id);

    /**
     * 查询会议室门牌注册列表
     * 
     * @param busiSmartRoomDoorplateRegister 会议室门牌注册
     * @return 会议室门牌注册集合
     */
    public List<BusiSmartRoomDoorplateRegister> selectBusiSmartRoomDoorplateRegisterList(BusiSmartRoomDoorplateRegisterVO busiSmartRoomDoorplateRegister);

    /**
     * 新增会议室门牌注册
     * 
     * @param busiSmartRoomDoorplateRegister 会议室门牌注册
     * @return 结果
     */
    public int insertBusiSmartRoomDoorplateRegister(BusiSmartRoomDoorplateRegister busiSmartRoomDoorplateRegister);

    /**
     * 修改会议室门牌注册
     * 
     * @param busiSmartRoomDoorplateRegister 会议室门牌注册
     * @return 结果
     */
    public int updateBusiSmartRoomDoorplateRegister(BusiSmartRoomDoorplateRegister busiSmartRoomDoorplateRegister);

    /**
     * 批量删除会议室门牌注册
     * 
     * @param ids 需要删除的会议室门牌注册ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDoorplateRegisterByIds(Long[] ids);

    /**
     * 删除会议室门牌注册信息
     * 
     * @param id 会议室门牌注册ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDoorplateRegisterById(Long id);
}
