package com.paradisecloud.fcm.telep.service.interfaces;

import com.paradisecloud.fcm.telep.dao.model.BusiTeleParticipant;

import java.util.List;

/**
 * @author nj
 * @date 2022/10/21 11:02
 */
public interface IBusiTeleParticipantService
{
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public BusiTeleParticipant selectBusiTeleParticipantById(Integer id);

    /**
     * 查询【请填写功能名称】列表
     *
     * @param busiTeleParticipant 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<BusiTeleParticipant> selectBusiTeleParticipantList(BusiTeleParticipant busiTeleParticipant);

    /**
     * 新增【请填写功能名称】
     *
     * @param busiTeleParticipant 【请填写功能名称】
     * @return 结果
     */
    public int insertBusiTeleParticipant(BusiTeleParticipant busiTeleParticipant);

    /**
     * 修改【请填写功能名称】
     *
     * @param busiTeleParticipant 【请填写功能名称】
     * @return 结果
     */
    public int updateBusiTeleParticipant(BusiTeleParticipant busiTeleParticipant);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiTeleParticipantByIds(Integer[] ids);

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiTeleParticipantById(Integer id);

    /**
     * 删除【请填写功能名称】信息
     *
     * @param number 【请填写功能名称】ID
     * @return 结果
     */
    int deleteBusiTeleParticipantByConferenceNumber(String number);
}
