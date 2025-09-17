package com.paradisecloud.fcm.dao.mapper;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomParticipant;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 智慧办公房间参与人Mapper接口
 *
 * @author lilinhai
 * @date 2024-04-07
 */
public interface BusiSmartRoomParticipantMapper
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
     * 查询智慧办公房间参与人列表（有签到码）
     *
     * @return 智慧办公房间参与人集合
     */
    public List<BusiSmartRoomParticipant> selectBusiSmartRoomParticipantListForSignInCode();

    /**
     * 查询智慧办公房间参与人列表（时间点之前有签到码）
     *
     * @return 智慧办公房间参与人集合
     */
    public List<BusiSmartRoomParticipant> selectBusiSmartRoomParticipantListForSignInCodeEnd(@Param("endTime") Date endTime);

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
     * 删除智慧办公房间参与人
     *
     * @param id 智慧办公房间参与人ID
     * @return 结果
     */
    public int deleteBusiSmartRoomParticipantById(Long id);

    /**
     * 批量删除智慧办公房间参与人
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSmartRoomParticipantByIds(Long[] ids);
}