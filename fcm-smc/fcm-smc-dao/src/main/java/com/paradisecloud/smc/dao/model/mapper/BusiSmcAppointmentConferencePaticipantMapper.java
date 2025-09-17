package com.paradisecloud.smc.dao.model.mapper;

import com.paradisecloud.smc.dao.model.BusiSmcAppointmentConferencePaticipant;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author lilinhai
 * @date 2023-03-16
 */
public interface BusiSmcAppointmentConferencePaticipantMapper 
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

    public List<BusiSmcAppointmentConferencePaticipant> selectBusiSmcAppointmentConferencePaticipantListByAppointId(@Param("appointmentId") Integer appointmentId);

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
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmcAppointmentConferencePaticipantById(Integer id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSmcAppointmentConferencePaticipantByIds(Integer[] ids);

    int deleteBusiSmcAppointmentConferencePaticipantByConferenceId(@Param("conferenceId") String conferenceId);
}
