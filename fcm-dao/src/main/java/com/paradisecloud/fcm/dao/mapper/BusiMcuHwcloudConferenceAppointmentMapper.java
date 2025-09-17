package com.paradisecloud.fcm.dao.mapper;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudConferenceAppointment;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Hwcloud.0MCU会议预约记录Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuHwcloudConferenceAppointmentMapper
{
    /**
     * 查询Hwcloud.0MCU会议预约记录
     * 
     * @param id Hwcloud.0MCU会议预约记录ID
     * @return Hwcloud.0MCU会议预约记录
     */
    public BusiMcuHwcloudConferenceAppointment selectBusiMcuHwcloudConferenceAppointmentById(Long id);

    /**
     * 查询Hwcloud.0MCU会议预约记录列表
     * 
     * @param busiMcuHwcloudConferenceAppointment Hwcloud.0MCU会议预约记录
     * @return Hwcloud.0MCU会议预约记录集合
     */
    public List<BusiMcuHwcloudConferenceAppointment> selectBusiMcuHwcloudConferenceAppointmentList(BusiMcuHwcloudConferenceAppointment busiMcuHwcloudConferenceAppointment);

    /**
     * 新增Hwcloud.0MCU会议预约记录
     * 
     * @param busiMcuHwcloudConferenceAppointment Hwcloud.0MCU会议预约记录
     * @return 结果
     */
    public int insertBusiMcuHwcloudConferenceAppointment(BusiMcuHwcloudConferenceAppointment busiMcuHwcloudConferenceAppointment);

    /**
     * 修改Hwcloud.0MCU会议预约记录
     * 
     * @param busiMcuHwcloudConferenceAppointment Hwcloud.0MCU会议预约记录
     * @return 结果
     */
    public int updateBusiMcuHwcloudConferenceAppointment(BusiMcuHwcloudConferenceAppointment busiMcuHwcloudConferenceAppointment);

    /**
     * 删除Hwcloud.0MCU会议预约记录
     * 
     * @param id Hwcloud.0MCU会议预约记录ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudConferenceAppointmentById(Long id);

    /**
     * 批量删除Hwcloud.0MCU会议预约记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudConferenceAppointmentByIds(Long[] ids);

    List<DeptRecordCount> getDeptRecordCounts(Integer businessFieldType);

    List<BusiMcuHwcloudConferenceAppointment> selectBusiMcuHwcloudConferenceAppointmentByTemplateId(Long id);

    Page<BusiMcuHwcloudConferenceAppointment> selectBusiMcuHwcloudConferenceAppointmentListByKey(@Param("searchKey") String searchKey, @Param("deptId")Long deptId);
}
