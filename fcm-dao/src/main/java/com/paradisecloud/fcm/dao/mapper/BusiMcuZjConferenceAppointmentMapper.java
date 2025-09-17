package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.BusiMcuZjConferenceAppointment;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import org.apache.ibatis.annotations.Param;

/**
 * 会议预约记录Mapper接口
 * 
 * @author lilinhai
 * @date 2021-05-24
 */
public interface BusiMcuZjConferenceAppointmentMapper
{
    /**
     * 查询会议预约记录
     * 
     * @param id 会议预约记录ID
     * @return 会议预约记录
     */
    public BusiMcuZjConferenceAppointment selectBusiMcuZjConferenceAppointmentById(Long id);

    /**
     * 查询会议预约记录列表
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 会议预约记录集合
     */
    public List<BusiMcuZjConferenceAppointment> selectBusiMcuZjConferenceAppointmentList(BusiMcuZjConferenceAppointment busiConferenceAppointment);

    /**
     * 新增会议预约记录
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 结果
     */
    public int insertBusiMcuZjConferenceAppointment(BusiMcuZjConferenceAppointment busiConferenceAppointment);

    /**
     * 修改会议预约记录
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 结果
     */
    public int updateBusiMcuZjConferenceAppointment(BusiMcuZjConferenceAppointment busiConferenceAppointment);
    
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
    public int deleteBusiMcuZjConferenceAppointmentById(Long id);

    /**
     * 批量删除会议预约记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuZjConferenceAppointmentByIds(Long[] ids);

    Page<BusiMcuZjConferenceAppointment> selectBusiMcuZjConferenceAppointmentListByKey(@Param("searchKey") String searchKey, @Param("deptId")Long deptId);

    /**
     * 查询会议预约记录
     * @param templateId
     * @return
     */
    public  List<BusiMcuZjConferenceAppointment> selectBusiMcuZjConferenceAppointmentByTemplateId(Long templateId);
}
