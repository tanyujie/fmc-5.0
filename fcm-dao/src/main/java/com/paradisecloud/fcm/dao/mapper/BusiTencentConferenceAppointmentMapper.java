package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiTencentConferenceAppointment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会议预约记录Mapper接口
 * 
 * @author lilinhai
 * @date 2023-07-05
 */
public interface BusiTencentConferenceAppointmentMapper 
{
    /**
     * 查询会议预约记录
     * 
     * @param id 会议预约记录ID
     * @return 会议预约记录
     */
    public BusiTencentConferenceAppointment selectBusiTencentConferenceAppointmentById(Long id);

    /**
     * 查询会议预约记录列表
     * 
     * @param busiTencentConferenceAppointment 会议预约记录
     * @return 会议预约记录集合
     */
    public List<BusiTencentConferenceAppointment> selectBusiTencentConferenceAppointmentList(BusiTencentConferenceAppointment busiTencentConferenceAppointment);

    /**
     * 新增会议预约记录
     * 
     * @param busiTencentConferenceAppointment 会议预约记录
     * @return 结果
     */
    public int insertBusiTencentConferenceAppointment(BusiTencentConferenceAppointment busiTencentConferenceAppointment);

    /**
     * 修改会议预约记录
     * 
     * @param busiTencentConferenceAppointment 会议预约记录
     * @return 结果
     */
    public int updateBusiTencentConferenceAppointment(BusiTencentConferenceAppointment busiTencentConferenceAppointment);

    /**
     * 删除会议预约记录
     * 
     * @param id 会议预约记录ID
     * @return 结果
     */
    public int deleteBusiTencentConferenceAppointmentById(Long id);

    /**
     * 批量删除会议预约记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiTencentConferenceAppointmentByIds(Long[] ids);

    List<BusiTencentConferenceAppointment> selectBusiAppointmentConferenceQuery(@Param("deptId") String deptId, @Param("searchKey") String searchKey,
                                                                                @Param("startTime") String startTime, @Param("endTime") String endTime,
                                                                                @Param("active") int active);
}
