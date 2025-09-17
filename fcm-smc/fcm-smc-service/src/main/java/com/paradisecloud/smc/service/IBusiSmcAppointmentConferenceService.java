package com.paradisecloud.smc.service;

import com.paradisecloud.com.fcm.smc.modle.request.BusiSmcAppointmentConferenceQuery;
import com.paradisecloud.smc.dao.model.BusiSmcAppointmentConference;

import java.util.Date;
import java.util.List;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author lilinhai
 * @date 2023-03-15
 */
public interface IBusiSmcAppointmentConferenceService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public BusiSmcAppointmentConference selectBusiSmcAppointmentConferenceById(Integer id);


    public BusiSmcAppointmentConference selectBusiSmcAppointmentConferenceByConferenceId(String conferenceId);
    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busiSmcAppointmentConference 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<BusiSmcAppointmentConference> selectBusiSmcAppointmentConferenceList(BusiSmcAppointmentConference busiSmcAppointmentConference);

    /**
     * 查询【请填写功能名称】列表
     *
     * @param query 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<BusiSmcAppointmentConference> selectBusiSmcAppointmentConferenceQuery(BusiSmcAppointmentConferenceQuery query);


    public List<BusiSmcAppointmentConference> selectBusiSmcAppointmentConferenceByStartTime();

    /**
     * 新增【请填写功能名称】
     * 
     * @param busiSmcAppointmentConference 【请填写功能名称】
     * @return 结果
     */
    public int insertBusiSmcAppointmentConference(BusiSmcAppointmentConference busiSmcAppointmentConference);

    /**
     * 修改【请填写功能名称】
     * 
     * @param busiSmcAppointmentConference 【请填写功能名称】
     * @return 结果
     */
    public int updateBusiSmcAppointmentConference(BusiSmcAppointmentConference busiSmcAppointmentConference);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmcAppointmentConferenceByIds(Integer[] ids);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmcAppointmentConferenceById(Integer id);

    List<BusiSmcAppointmentConference> selectBusiSmcAppointmentConferenceHistoryQuery(String convertDateToString);

    List<BusiSmcAppointmentConference> selectBusiSmcAppointmentConferenceByStartTimeLt();

    List<BusiSmcAppointmentConference> selectBusiSmcAppointmentConferenceByTime(Date date);


    List<BusiSmcAppointmentConference> selectBusiSmcAppointmentConferenceByStartTimeLtNoExisTHistory();
}
