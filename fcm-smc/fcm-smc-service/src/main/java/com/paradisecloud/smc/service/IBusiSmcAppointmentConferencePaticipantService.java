package com.paradisecloud.smc.service;

import com.paradisecloud.smc.dao.model.BusiSmcAppointmentConferencePaticipant;

import java.util.List;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author lilinhai
 * @date 2023-03-16
 */
public interface IBusiSmcAppointmentConferencePaticipantService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public BusiSmcAppointmentConferencePaticipant selectBusiSmcAppointmentConferencePaticipantById(Integer id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busiSmcAppointmentConferencePaticipant 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<BusiSmcAppointmentConferencePaticipant> selectBusiSmcAppointmentConferencePaticipantList(BusiSmcAppointmentConferencePaticipant busiSmcAppointmentConferencePaticipant);

    /**
     * 新增【请填写功能名称】
     * 
     * @param busiSmcAppointmentConferencePaticipant 【请填写功能名称】
     * @return 结果
     */
    public int insertBusiSmcAppointmentConferencePaticipant(BusiSmcAppointmentConferencePaticipant busiSmcAppointmentConferencePaticipant);

    /**
     * 修改【请填写功能名称】
     * 
     * @param busiSmcAppointmentConferencePaticipant 【请填写功能名称】
     * @return 结果
     */
    public int updateBusiSmcAppointmentConferencePaticipant(BusiSmcAppointmentConferencePaticipant busiSmcAppointmentConferencePaticipant);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmcAppointmentConferencePaticipantByIds(Integer[] ids);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmcAppointmentConferencePaticipantById(Integer id);

    public int deleteBusiSmcAppointmentConferencePaticipantByConferenceId(String conferenceId);

    List<BusiSmcAppointmentConferencePaticipant> selectBusiSmcAppointmentConferencePaticipantListByAppointId(Integer id);
}
