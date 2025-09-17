package com.paradisecloud.fcm.smc2.service;

import java.util.List;

import com.paradisecloud.com.fcm.smc.modle.request.BusiSmcAppointmentConferenceQuery;
import com.paradisecloud.fcm.dao.model.BusiSmc2AppointmentConference;
import com.paradisecloud.fcm.smc2.model.BusiSmc2AppointmentConferenceRequest;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author lilinhai
 * @date 2023-05-04
 */
public interface IBusiSmc2AppointmentConferenceService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public BusiSmc2AppointmentConference selectBusiSmc2AppointmentConferenceById(Integer id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busiSmc2AppointmentConference 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<BusiSmc2AppointmentConference> selectBusiSmc2AppointmentConferenceList(BusiSmc2AppointmentConference busiSmc2AppointmentConference);

    /**
     * 新增【请填写功能名称】
     * 
     * @param busiSmc2AppointmentConference 【请填写功能名称】
     * @return 结果
     */
    public Object insertBusiSmc2AppointmentConference(BusiSmc2AppointmentConferenceRequest appointmentConferenceRequest) throws Exception;

    /**
     * 修改【请填写功能名称】
     * 
     * @param busiSmc2AppointmentConference 【请填写功能名称】
     * @return 结果
     */
    public int updateBusiSmc2AppointmentConference(BusiSmc2AppointmentConferenceRequest appointmentConferenceRequest);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmc2AppointmentConferenceByIds(Integer[] ids);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmc2AppointmentConferenceById(Integer id);

    List<BusiSmc2AppointmentConference> selectBusiSmcAppointmentConferenceQuery(BusiSmcAppointmentConferenceQuery query);
}
