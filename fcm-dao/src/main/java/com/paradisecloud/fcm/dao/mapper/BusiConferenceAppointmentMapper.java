package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import com.github.pagehelper.Page;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.fcm.dao.model.BusiConferenceAppointment;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import org.apache.ibatis.annotations.Param;

/**
 * 会议预约记录Mapper接口
 * 
 * @author lilinhai
 * @date 2021-05-24
 */
public interface BusiConferenceAppointmentMapper 
{
    /**
     * 查询会议预约记录
     * 
     * @param id 会议预约记录ID
     * @return 会议预约记录
     */
    public BusiConferenceAppointment selectBusiConferenceAppointmentById(Long id);

    /**
     * 查询会议预约记录
     * @param templateId
     * @return
     */
    public  List<BusiConferenceAppointment> selectBusiConferenceAppointmentByTemplateId(Long templateId);

    /**
     * 查询会议预约记录列表
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 会议预约记录集合
     */
    public List<BusiConferenceAppointment> selectBusiConferenceAppointmentList(BusiConferenceAppointment busiConferenceAppointment);

    /**
     * 新增会议预约记录
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 结果
     */
    public int insertBusiConferenceAppointment(BusiConferenceAppointment busiConferenceAppointment);

    /**
     * 修改会议预约记录
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 结果
     */
    public int updateBusiConferenceAppointment(BusiConferenceAppointment busiConferenceAppointment);
    
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
    public int deleteBusiConferenceAppointmentById(Long id);

    /**
     * 批量删除会议预约记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiConferenceAppointmentByIds(Long[] ids);

    Page<BusiConferenceAppointment> selectBusiConferenceAppointmentListBykey(@Param("searchKey") String searchKey, @Param("deptId")Long deptId);
}
