package com.paradisecloud.fcm.zte.service.interfaces;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.BusiMcuZteConferenceAppointment;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;

import java.util.List;
import java.util.Map;

/**
 * 会议预约记录Service接口
 * 
 * @author lilinhai
 * @date 2021-05-24
 */
public interface IBusiMcuZteConferenceAppointmentService
{
    /**
     * 查询会议预约记录
     * 
     * @param id 会议预约记录ID
     * @return 会议预约记录
     */
    public BusiMcuZteConferenceAppointment selectBusiMcuZteConferenceAppointmentById(Long id);

    /**
     * 查询会议预约记录列表
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 会议预约记录集合
     */
    public List<BusiMcuZteConferenceAppointment> selectBusiMcuZteConferenceAppointmentList(BusiMcuZteConferenceAppointment busiConferenceAppointment);
    List<BusiMcuZteConferenceAppointment> selectBusiMcuZteConferenceAppointmentListWithOutBusinessFieldType(BusiMcuZteConferenceAppointment busiConferenceAppointment);

    /**
     * 新增会议预约记录
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 结果
     */
    public Map<String, Object> insertBusiMcuZteConferenceAppointment(BusiMcuZteConferenceAppointment busiConferenceAppointment);

    /**
     * 修改会议预约记录
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 结果
     */
    public int updateBusiMcuZteConferenceAppointment(BusiMcuZteConferenceAppointment busiConferenceAppointment, boolean checkApproval);

    /**
     * 批量删除会议预约记录
     * 
     * @param ids 需要删除的会议预约记录ID
     * @return 结果
     */
    public int deleteBusiMcuZteConferenceAppointmentByIds(Long[] ids);

    /**
     * 删除会议预约记录信息
     * 
     * @param id 会议预约记录ID
     * @return 结果
     */
    public int deleteBusiMcuZteConferenceAppointmentById(Long id);

    List<DeptRecordCount> getDeptRecordCounts(Integer businessFieldType);

    /**
     * 查询预约会议列表
     * @param searchKey
     * @param deptId
     * @return
     */
    Page<BusiMcuZteConferenceAppointment> selectBusiMcuZteConferenceAppointmentListByKey(String searchKey, Long deptId, Integer pageIndex, Integer pageSize);

    Map<String, Object> insertBusiMcuZteConferenceAppointmentIsMute(BusiMcuZteConferenceAppointment busiConferenceAppointment, boolean isMute, Long userId);

    public  List<BusiMcuZteConferenceAppointment> selectBusiMcuZteConferenceAppointmentByTemplateId(Long id);
}
