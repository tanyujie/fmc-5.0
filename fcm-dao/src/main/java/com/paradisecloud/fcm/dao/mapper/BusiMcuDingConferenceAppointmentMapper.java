package com.paradisecloud.fcm.dao.mapper;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.BusiMcuDingConferenceAppointment;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Ding.0MCU会议预约记录Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuDingConferenceAppointmentMapper
{
    /**
     * 查询Ding.0MCU会议预约记录
     * 
     * @param id Ding.0MCU会议预约记录ID
     * @return Ding.0MCU会议预约记录
     */
    public BusiMcuDingConferenceAppointment selectBusiMcuDingConferenceAppointmentById(Long id);

    /**
     * 查询Ding.0MCU会议预约记录列表
     * 
     * @param busiMcuDingConferenceAppointment Ding.0MCU会议预约记录
     * @return Ding.0MCU会议预约记录集合
     */
    public List<BusiMcuDingConferenceAppointment> selectBusiMcuDingConferenceAppointmentList(BusiMcuDingConferenceAppointment busiMcuDingConferenceAppointment);

    /**
     * 新增Ding.0MCU会议预约记录
     * 
     * @param busiMcuDingConferenceAppointment Ding.0MCU会议预约记录
     * @return 结果
     */
    public int insertBusiMcuDingConferenceAppointment(BusiMcuDingConferenceAppointment busiMcuDingConferenceAppointment);

    /**
     * 修改Ding.0MCU会议预约记录
     * 
     * @param busiMcuDingConferenceAppointment Ding.0MCU会议预约记录
     * @return 结果
     */
    public int updateBusiMcuDingConferenceAppointment(BusiMcuDingConferenceAppointment busiMcuDingConferenceAppointment);

    /**
     * 删除Ding.0MCU会议预约记录
     * 
     * @param id Ding.0MCU会议预约记录ID
     * @return 结果
     */
    public int deleteBusiMcuDingConferenceAppointmentById(Long id);

    /**
     * 批量删除Ding.0MCU会议预约记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuDingConferenceAppointmentByIds(Long[] ids);

    List<DeptRecordCount> getDeptRecordCounts(Integer businessFieldType);

    List<BusiMcuDingConferenceAppointment> selectBusiMcuDingConferenceAppointmentByTemplateId(Long id);

    Page<BusiMcuDingConferenceAppointment> selectBusiMcuDingConferenceAppointmentListByKey(@Param("searchKey") String searchKey, @Param("deptId")Long deptId);
}
