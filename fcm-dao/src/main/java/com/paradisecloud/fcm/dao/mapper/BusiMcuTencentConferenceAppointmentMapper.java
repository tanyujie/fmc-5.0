package com.paradisecloud.fcm.dao.mapper;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentConferenceAppointment;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Tencent.0MCU会议预约记录Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuTencentConferenceAppointmentMapper
{
    /**
     * 查询Tencent.0MCU会议预约记录
     * 
     * @param id Tencent.0MCU会议预约记录ID
     * @return Tencent.0MCU会议预约记录
     */
    public BusiMcuTencentConferenceAppointment selectBusiMcuTencentConferenceAppointmentById(Long id);

    /**
     * 查询Tencent.0MCU会议预约记录列表
     * 
     * @param busiMcuTencentConferenceAppointment Tencent.0MCU会议预约记录
     * @return Tencent.0MCU会议预约记录集合
     */
    public List<BusiMcuTencentConferenceAppointment> selectBusiMcuTencentConferenceAppointmentList(BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointment);

    /**
     * 新增Tencent.0MCU会议预约记录
     * 
     * @param busiMcuTencentConferenceAppointment Tencent.0MCU会议预约记录
     * @return 结果
     */
    public int insertBusiMcuTencentConferenceAppointment(BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointment);

    /**
     * 修改Tencent.0MCU会议预约记录
     * 
     * @param busiMcuTencentConferenceAppointment Tencent.0MCU会议预约记录
     * @return 结果
     */
    public int updateBusiMcuTencentConferenceAppointment(BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointment);

    /**
     * 删除Tencent.0MCU会议预约记录
     * 
     * @param id Tencent.0MCU会议预约记录ID
     * @return 结果
     */
    public int deleteBusiMcuTencentConferenceAppointmentById(Long id);

    /**
     * 批量删除Tencent.0MCU会议预约记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuTencentConferenceAppointmentByIds(Long[] ids);

    List<DeptRecordCount> getDeptRecordCounts(Integer businessFieldType);

    List<BusiMcuTencentConferenceAppointment> selectBusiMcuTencentConferenceAppointmentByTemplateId(Long id);

    Page<BusiMcuTencentConferenceAppointment> selectBusiMcuTencentConferenceAppointmentListByKey(@Param("searchKey") String searchKey, @Param("deptId")Long deptId);
}
