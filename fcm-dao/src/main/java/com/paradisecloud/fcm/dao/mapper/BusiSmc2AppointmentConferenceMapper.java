package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiSmc2AppointmentConference;
import org.apache.ibatis.annotations.Param;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author lilinhai
 * @date 2023-05-04
 */
public interface BusiSmc2AppointmentConferenceMapper 
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
    public int insertBusiSmc2AppointmentConference(BusiSmc2AppointmentConference busiSmc2AppointmentConference);

    /**
     * 修改【请填写功能名称】
     * 
     * @param busiSmc2AppointmentConference 【请填写功能名称】
     * @return 结果
     */
    public int updateBusiSmc2AppointmentConference(BusiSmc2AppointmentConference busiSmc2AppointmentConference);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmc2AppointmentConferenceById(Integer id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSmc2AppointmentConferenceByIds(Integer[] ids);

    List<BusiSmc2AppointmentConference> selectBusiSmcAppointmentConferenceQuery(@Param("deptId") Long deptId, @Param("searchKey") String searchKey,
                                                                               @Param("startTime") String startTime, @Param("endTime") String endTime,
                                                                               @Param("active") int active);
}
