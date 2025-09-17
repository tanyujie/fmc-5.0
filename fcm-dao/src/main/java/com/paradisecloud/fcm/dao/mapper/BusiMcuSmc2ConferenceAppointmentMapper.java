package com.paradisecloud.fcm.dao.mapper;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2ConferenceAppointment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SMC2.0MCU会议预约记录Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuSmc2ConferenceAppointmentMapper
{
    /**
     * 查询SMC2.0MCU会议预约记录
     * 
     * @param id SMC2.0MCU会议预约记录ID
     * @return SMC2.0MCU会议预约记录
     */
    public BusiMcuSmc2ConferenceAppointment selectBusiMcuSmc2ConferenceAppointmentById(Long id);

    /**
     * 查询SMC2.0MCU会议预约记录列表
     * 
     * @param busiMcuSmc2ConferenceAppointment SMC2.0MCU会议预约记录
     * @return SMC2.0MCU会议预约记录集合
     */
    public List<BusiMcuSmc2ConferenceAppointment> selectBusiMcuSmc2ConferenceAppointmentList(BusiMcuSmc2ConferenceAppointment busiMcuSmc2ConferenceAppointment);

    /**
     * 新增SMC2.0MCU会议预约记录
     * 
     * @param busiMcuSmc2ConferenceAppointment SMC2.0MCU会议预约记录
     * @return 结果
     */
    public int insertBusiMcuSmc2ConferenceAppointment(BusiMcuSmc2ConferenceAppointment busiMcuSmc2ConferenceAppointment);

    /**
     * 修改SMC2.0MCU会议预约记录
     * 
     * @param busiMcuSmc2ConferenceAppointment SMC2.0MCU会议预约记录
     * @return 结果
     */
    public int updateBusiMcuSmc2ConferenceAppointment(BusiMcuSmc2ConferenceAppointment busiMcuSmc2ConferenceAppointment);

    /**
     * 删除SMC2.0MCU会议预约记录
     * 
     * @param id SMC2.0MCU会议预约记录ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2ConferenceAppointmentById(Long id);

    /**
     * 批量删除SMC2.0MCU会议预约记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2ConferenceAppointmentByIds(Long[] ids);

    List<DeptRecordCount> getDeptRecordCounts(Integer businessFieldType);

    List<BusiMcuSmc2ConferenceAppointment> selectBusiMcuSmc2ConferenceAppointmentByTemplateId(Long id);

    Page<BusiMcuSmc2ConferenceAppointment> selectBusiMcuSmc2ConferenceAppointmentListByKey(@Param("searchKey") String searchKey, @Param("deptId")Long deptId);
}
