package com.paradisecloud.fcm.fme.conference.interfaces;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.fcm.dao.model.BusiConferenceAppointment;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;

/**
 * 会议预约记录Service接口
 * 
 * @author lilinhai
 * @date 2021-05-24
 */
public interface IBusiConferenceAppointmentService 
{
    /**
     * 查询会议预约记录
     * 
     * @param id 会议预约记录ID
     * @return 会议预约记录
     */
    public BusiConferenceAppointment selectBusiConferenceAppointmentById(Long id);


    public  List<BusiConferenceAppointment> selectBusiConferenceAppointmentByTemplateId(Long id);

    /**
     * 查询会议预约记录列表
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 会议预约记录集合
     */
    public List<BusiConferenceAppointment> selectBusiConferenceAppointmentList(BusiConferenceAppointment busiConferenceAppointment);
    List<BusiConferenceAppointment> selectBusiConferenceAppointmentListWithOutBusinessFieldType(BusiConferenceAppointment busiConferenceAppointment);

    /**
     * 新增会议预约记录
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 结果
     */
    public Map<String, Object> insertBusiConferenceAppointment(BusiConferenceAppointment busiConferenceAppointment);

    /**
     * 修改会议预约记录
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 结果
     */
    public int updateBusiConferenceAppointment(BusiConferenceAppointment busiConferenceAppointment, boolean checkApproval);

    /**
     * 批量删除会议预约记录
     * 
     * @param ids 需要删除的会议预约记录ID
     * @return 结果
     */
    public int deleteBusiConferenceAppointmentByIds(Long[] ids);

    /**
     * 删除会议预约记录信息
     * 
     * @param id 会议预约记录ID
     * @return 结果
     */
    public int deleteBusiConferenceAppointmentById(Long id);

    List<DeptRecordCount> getDeptRecordCounts(Integer businessFieldType);

    /**
     * 查询预约会议列表
     * @param searchKey
     * @param deptId
     * @return
     */
    Page<BusiConferenceAppointment> selectBusiConferenceAppointmentListBykey(String searchKey, Long deptId, Integer pageIndex, Integer pageSize);

    int insertBusiConferenceAppointmentIsMute(BusiConferenceAppointment busiConferenceAppointment, boolean isMute, Long userId);
}
