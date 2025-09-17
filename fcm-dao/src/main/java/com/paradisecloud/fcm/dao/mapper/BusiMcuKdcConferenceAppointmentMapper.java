package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.BusiMcuKdcConferenceAppointment;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import org.apache.ibatis.annotations.Param;

/**
 * 会议预约记录Mapper接口
 * 
 * @author lilinhai
 * @date 2021-05-24
 */
public interface BusiMcuKdcConferenceAppointmentMapper
{
    /**
     * 查询会议预约记录
     * 
     * @param id 会议预约记录ID
     * @return 会议预约记录
     */
    public BusiMcuKdcConferenceAppointment selectBusiMcuKdcConferenceAppointmentById(Long id);

    /**
     * 查询会议预约记录列表
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 会议预约记录集合
     */
    public List<BusiMcuKdcConferenceAppointment> selectBusiMcuKdcConferenceAppointmentList(BusiMcuKdcConferenceAppointment busiConferenceAppointment);

    /**
     * 新增会议预约记录
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 结果
     */
    public int insertBusiMcuKdcConferenceAppointment(BusiMcuKdcConferenceAppointment busiConferenceAppointment);

    /**
     * 修改会议预约记录
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 结果
     */
    public int updateBusiMcuKdcConferenceAppointment(BusiMcuKdcConferenceAppointment busiConferenceAppointment);
    
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
    public int deleteBusiMcuKdcConferenceAppointmentById(Long id);

    /**
     * 批量删除会议预约记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuKdcConferenceAppointmentByIds(Long[] ids);

    Page<BusiMcuKdcConferenceAppointment> selectBusiMcuKdcConferenceAppointmentListByKey(@Param("searchKey") String searchKey, @Param("deptId")Long deptId);

    /**
     * 查询会议预约记录
     * @param templateId
     * @return
     */
    public  List<BusiMcuKdcConferenceAppointment> selectBusiMcuKdcConferenceAppointmentByTemplateId(Long templateId);
}
