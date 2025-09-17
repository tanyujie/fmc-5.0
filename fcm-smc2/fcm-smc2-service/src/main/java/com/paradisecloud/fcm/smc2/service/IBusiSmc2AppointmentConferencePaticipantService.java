package com.paradisecloud.fcm.smc2.service;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiSmc2AppointmentConferencePaticipant;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author lilinhai
 * @date 2023-05-04
 */
public interface IBusiSmc2AppointmentConferencePaticipantService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public BusiSmc2AppointmentConferencePaticipant selectBusiSmc2AppointmentConferencePaticipantById(Integer id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busiSmc2AppointmentConferencePaticipant 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<BusiSmc2AppointmentConferencePaticipant> selectBusiSmc2AppointmentConferencePaticipantList(BusiSmc2AppointmentConferencePaticipant busiSmc2AppointmentConferencePaticipant);

    /**
     * 新增【请填写功能名称】
     * 
     * @param busiSmc2AppointmentConferencePaticipant 【请填写功能名称】
     * @return 结果
     */
    public int insertBusiSmc2AppointmentConferencePaticipant(BusiSmc2AppointmentConferencePaticipant busiSmc2AppointmentConferencePaticipant);

    /**
     * 修改【请填写功能名称】
     * 
     * @param busiSmc2AppointmentConferencePaticipant 【请填写功能名称】
     * @return 结果
     */
    public int updateBusiSmc2AppointmentConferencePaticipant(BusiSmc2AppointmentConferencePaticipant busiSmc2AppointmentConferencePaticipant);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmc2AppointmentConferencePaticipantByIds(Integer[] ids);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmc2AppointmentConferencePaticipantById(Integer id);

    List<BusiSmc2AppointmentConferencePaticipant> selectBusiSmc2AppointmentConferencePaticipantListByAppointId(Integer id);
}
