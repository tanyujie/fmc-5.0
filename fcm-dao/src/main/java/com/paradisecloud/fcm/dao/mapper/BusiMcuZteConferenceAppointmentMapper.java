package com.paradisecloud.fcm.dao.mapper;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.BusiMcuZteConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiMcuZteConferenceAppointment;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 中兴MCU会议预约记录Mapper接口
 * 
 * @author lilinhai
 * @date 2024-04-09
 */
public interface BusiMcuZteConferenceAppointmentMapper 
{
    /**
     * 查询中兴MCU会议预约记录
     * 
     * @param id 中兴MCU会议预约记录ID
     * @return 中兴MCU会议预约记录
     */
    public BusiMcuZteConferenceAppointment selectBusiMcuZteConferenceAppointmentById(Long id);

    /**
     * 查询中兴MCU会议预约记录列表
     * 
     * @param busiMcuZteConferenceAppointment 中兴MCU会议预约记录
     * @return 中兴MCU会议预约记录集合
     */
    public List<BusiMcuZteConferenceAppointment> selectBusiMcuZteConferenceAppointmentList(BusiMcuZteConferenceAppointment busiMcuZteConferenceAppointment);

    /**
     * 新增中兴MCU会议预约记录
     * 
     * @param busiMcuZteConferenceAppointment 中兴MCU会议预约记录
     * @return 结果
     */
    public int insertBusiMcuZteConferenceAppointment(BusiMcuZteConferenceAppointment busiMcuZteConferenceAppointment);

    /**
     * 修改中兴MCU会议预约记录
     * 
     * @param busiMcuZteConferenceAppointment 中兴MCU会议预约记录
     * @return 结果
     */
    public int updateBusiMcuZteConferenceAppointment(BusiMcuZteConferenceAppointment busiMcuZteConferenceAppointment);

    /**
     * 删除中兴MCU会议预约记录
     * 
     * @param id 中兴MCU会议预约记录ID
     * @return 结果
     */
    public int deleteBusiMcuZteConferenceAppointmentById(Long id);

    /**
     * 批量删除中兴MCU会议预约记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuZteConferenceAppointmentByIds(Long[] ids);

    List<DeptRecordCount> getDeptRecordCounts(Integer businessFieldType);

    Page<BusiMcuZteConferenceAppointment> selectBusiMcuZteConferenceAppointmentListByKey(@Param("searchKey") String searchKey, @Param("deptId")Long deptId);

    /**
     * 查询会议预约记录
     * @param templateId
     * @return
     */
    public  List<BusiMcuZteConferenceAppointment> selectBusiMcuZteConferenceAppointmentByTemplateId(Long templateId);
}
