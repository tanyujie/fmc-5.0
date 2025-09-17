package com.paradisecloud.smc.dao.model.mapper;

import java.util.Date;
import java.util.List;

import com.paradisecloud.smc.dao.model.BusiSmcAppointmentConference;
import org.apache.ibatis.annotations.Param;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author lilinhai
 * @date 2023-03-15
 */
public interface BusiSmcAppointmentConferenceMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public BusiSmcAppointmentConference selectBusiSmcAppointmentConferenceById(Integer id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busiSmcAppointmentConference 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<BusiSmcAppointmentConference> selectBusiSmcAppointmentConferenceList(BusiSmcAppointmentConference busiSmcAppointmentConference);

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
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmcAppointmentConferenceById(Integer id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSmcAppointmentConferenceByIds(Integer[] ids);

    List<BusiSmcAppointmentConference> selectBusiSmcAppointmentConferenceQuery(@Param("deptId") Long deptId,@Param("searchKey") String searchKey,
                                                                               @Param("startTime") String startTime,@Param("endTime") String endTime,
                                                                               @Param("active") Integer active);
    List<BusiSmcAppointmentConference> selectBusiSmcAppointmentConferenceHistoryQuery(@Param("startTime") String startTime);

    BusiSmcAppointmentConference selectBusiSmcAppointmentConferenceByConferenceId(@Param("conferenceId") String conferenceId);

    List<BusiSmcAppointmentConference> selectBusiSmcAppointmentConferenceByStartTime();

    List<BusiSmcAppointmentConference> selectBusiSmcAppointmentConferenceByStartTimeLt();

    List<BusiSmcAppointmentConference> selectBusiSmcAppointmentConferenceByTime(Date startDay);

    List<BusiSmcAppointmentConference> selectBusiSmcAppointmentConferenceByStartTimeLtNoExisTHistory();
}
