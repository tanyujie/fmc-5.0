package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.BusiMcuPlcConferenceAppointment;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import org.apache.ibatis.annotations.Param;

/**
 * 会议预约记录Mapper接口
 * 
 * @author lilinhai
 * @date 2021-05-24
 */
public interface BusiMcuPlcConferenceAppointmentMapper
{
    /**
     * 查询会议预约记录
     * 
     * @param id 会议预约记录ID
     * @return 会议预约记录
     */
    public BusiMcuPlcConferenceAppointment selectBusiMcuPlcConferenceAppointmentById(Long id);

    /**
     * 查询会议预约记录列表
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 会议预约记录集合
     */
    public List<BusiMcuPlcConferenceAppointment> selectBusiMcuPlcConferenceAppointmentList(BusiMcuPlcConferenceAppointment busiConferenceAppointment);

    /**
     * 新增会议预约记录
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 结果
     */
    public int insertBusiMcuPlcConferenceAppointment(BusiMcuPlcConferenceAppointment busiConferenceAppointment);

    /**
     * 修改会议预约记录
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 结果
     */
    public int updateBusiMcuPlcConferenceAppointment(BusiMcuPlcConferenceAppointment busiConferenceAppointment);
    
    /**
     * 预约计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts(Integer businessFieldType);

    /**
     * 删除会议预约记录
     * 
     * @param id 会议预约记录ID
     * @return 结果
     */
    public int deleteBusiMcuPlcConferenceAppointmentById(Long id);

    /**
     * 批量删除会议预约记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuPlcConferenceAppointmentByIds(Long[] ids);

    Page<BusiMcuPlcConferenceAppointment> selectBusiMcuPlcConferenceAppointmentListByKey(@Param("searchKey") String searchKey, @Param("deptId")Long deptId);

    /**
     * 查询会议预约记录
     * @param templateId
     * @return
     */
    public  List<BusiMcuPlcConferenceAppointment> selectBusiMcuPlcConferenceAppointmentByTemplateId(Long templateId);
}
