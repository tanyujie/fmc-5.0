package com.paradisecloud.fcm.smartroom.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiSmartRoomParticipant;

import java.util.List;

/**
 * 智慧办公房间参与人Service接口
 *
 * @author lilinhai
 * @date 2024-04-07
 */
public interface IBusiSmartRoomParticipantService
{
    /**
     * 查询智慧办公房间参与人
     *
     * @param id 智慧办公房间参与人ID
     * @return 智慧办公房间参与人
     */
    public BusiSmartRoomParticipant selectBusiSmartRoomParticipantById(Long id);

    /**
     * 查询智慧办公房间参与人列表
     *
     * @param busiSmartRoomParticipant 智慧办公房间参与人
     * @return 智慧办公房间参与人集合
     */
    public List<BusiSmartRoomParticipant> selectBusiSmartRoomParticipantList(BusiSmartRoomParticipant busiSmartRoomParticipant);

    /**
     * 新增智慧办公房间参与人
     *
     * @param busiSmartRoomParticipant 智慧办公房间参与人
     * @return 结果
     */
    public int insertBusiSmartRoomParticipant(BusiSmartRoomParticipant busiSmartRoomParticipant);

    /**
     * 修改智慧办公房间参与人
     *
     * @param busiSmartRoomParticipant 智慧办公房间参与人
     * @return 结果
     */
    public int updateBusiSmartRoomParticipant(BusiSmartRoomParticipant busiSmartRoomParticipant);

    /**
     * 批量删除智慧办公房间参与人
     *
     * @param ids 需要删除的智慧办公房间参与人ID
     * @return 结果
     */
    public int deleteBusiSmartRoomParticipantByIds(Long[] ids);

    /**
     * 删除智慧办公房间参与人信息
     *
     * @param id 智慧办公房间参与人ID
     * @return 结果
     */
    public int deleteBusiSmartRoomParticipantById(Long id);

    /**
     * 预约签到
     * @param busiSmartRoomParticipant
     * @return
     */
    int signIn(BusiSmartRoomParticipant busiSmartRoomParticipant);
}
