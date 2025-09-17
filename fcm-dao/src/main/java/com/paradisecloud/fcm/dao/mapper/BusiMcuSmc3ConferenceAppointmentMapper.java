package com.paradisecloud.fcm.dao.mapper;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3ConferenceAppointment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SMC3.0MCU会议预约记录Mapper接口
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
public interface BusiMcuSmc3ConferenceAppointmentMapper 
{
    /**
     * 查询SMC3.0MCU会议预约记录
     * 
     * @param id SMC3.0MCU会议预约记录ID
     * @return SMC3.0MCU会议预约记录
     */
    public BusiMcuSmc3ConferenceAppointment selectBusiMcuSmc3ConferenceAppointmentById(Long id);

    /**
     * 查询SMC3.0MCU会议预约记录列表
     * 
     * @param busiMcuSmc3ConferenceAppointment SMC3.0MCU会议预约记录
     * @return SMC3.0MCU会议预约记录集合
     */
    public List<BusiMcuSmc3ConferenceAppointment> selectBusiMcuSmc3ConferenceAppointmentList(BusiMcuSmc3ConferenceAppointment busiMcuSmc3ConferenceAppointment);

    /**
     * 新增SMC3.0MCU会议预约记录
     * 
     * @param busiMcuSmc3ConferenceAppointment SMC3.0MCU会议预约记录
     * @return 结果
     */
    public int insertBusiMcuSmc3ConferenceAppointment(BusiMcuSmc3ConferenceAppointment busiMcuSmc3ConferenceAppointment);

    /**
     * 修改SMC3.0MCU会议预约记录
     * 
     * @param busiMcuSmc3ConferenceAppointment SMC3.0MCU会议预约记录
     * @return 结果
     */
    public int updateBusiMcuSmc3ConferenceAppointment(BusiMcuSmc3ConferenceAppointment busiMcuSmc3ConferenceAppointment);

    /**
     * 删除SMC3.0MCU会议预约记录
     * 
     * @param id SMC3.0MCU会议预约记录ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3ConferenceAppointmentById(Long id);

    /**
     * 批量删除SMC3.0MCU会议预约记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3ConferenceAppointmentByIds(Long[] ids);

    List<DeptRecordCount> getDeptRecordCounts(Integer businessFieldType);

    List<BusiMcuSmc3ConferenceAppointment> selectBusiMcuSmc3ConferenceAppointmentByTemplateId(Long id);

    Page<BusiMcuSmc3ConferenceAppointment> selectBusiMcuSmc3ConferenceAppointmentListByKey(@Param("searchKey") String searchKey, @Param("deptId")Long deptId);
}
