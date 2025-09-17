package com.paradisecloud.fcm.tencent.service2.interfaces;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentConferenceAppointment;

import java.util.List;
import java.util.Map;

/**
 * 会议预约记录Service接口
 * 
 * @author lilinhai
 * @date 2021-05-24
 */
public interface IBusiMcuTencentConferenceAppointmentService
{
    /**
     * 查询会议预约记录
     * 
     * @param id 会议预约记录ID
     * @return 会议预约记录
     */
    public BusiMcuTencentConferenceAppointment selectBusiMcuTencentConferenceAppointmentById(Long id);

    /**
     * 查询会议预约记录列表
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 会议预约记录集合
     */
    public List<BusiMcuTencentConferenceAppointment> selectBusiMcuTencentConferenceAppointmentList(BusiMcuTencentConferenceAppointment busiConferenceAppointment);
    List<BusiMcuTencentConferenceAppointment> selectBusiMcuTencentConferenceAppointmentListWithOutBusinessFieldType(BusiMcuTencentConferenceAppointment busiConferenceAppointment);

    /**
     * 新增会议预约记录
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 结果
     */
    public Map<String, Object> insertBusiMcuTencentConferenceAppointment(BusiMcuTencentConferenceAppointment busiConferenceAppointment);

    /**
     * 修改会议预约记录
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 结果
     */
    public int updateBusiMcuTencentConferenceAppointment(BusiMcuTencentConferenceAppointment busiConferenceAppointment, boolean checkApproval);

    /**
     * 批量删除会议预约记录
     * 
     * @param ids 需要删除的会议预约记录ID
     * @return 结果
     */
    public int deleteBusiMcuTencentConferenceAppointmentByIds(Long[] ids);

    /**
     * 删除会议预约记录信息
     * 
     * @param id 会议预约记录ID
     * @return 结果
     */
    public int deleteBusiMcuTencentConferenceAppointmentById(Long id);

    List<DeptRecordCount> getDeptRecordCounts(Integer businessFieldType);

    /**
     * 查询预约会议列表
     * @param searchKey
     * @param deptId
     * @return
     */
    Page<BusiMcuTencentConferenceAppointment> selectBusiMcuTencentConferenceAppointmentListByKey(String searchKey, Long deptId, Integer pageIndex, Integer pageSize);

    Map<String, Object> insertBusiMcuTencentConferenceAppointmentIsMute(BusiMcuTencentConferenceAppointment busiConferenceAppointment, boolean isMute, Long userId);

    public  List<BusiMcuTencentConferenceAppointment> selectBusiMcuTencentConferenceAppointmentByTemplateId(Long id);
}
