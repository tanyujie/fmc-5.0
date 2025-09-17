package com.paradisecloud.fcm.smc2.setvice2.interfaces;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2ConferenceAppointment;

import java.util.List;
import java.util.Map;

/**
 * 会议预约记录Service接口
 * 
 * @author lilinhai
 * @date 2021-05-24
 */
public interface IBusiMcuSmc2ConferenceAppointmentService
{
    /**
     * 查询会议预约记录
     * 
     * @param id 会议预约记录ID
     * @return 会议预约记录
     */
    public BusiMcuSmc2ConferenceAppointment selectBusiMcuSmc2ConferenceAppointmentById(Long id);

    /**
     * 查询会议预约记录列表
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 会议预约记录集合
     */
    public List<BusiMcuSmc2ConferenceAppointment> selectBusiMcuSmc2ConferenceAppointmentList(BusiMcuSmc2ConferenceAppointment busiConferenceAppointment);
    List<BusiMcuSmc2ConferenceAppointment> selectBusiMcuSmc2ConferenceAppointmentListWithOutBusinessFieldType(BusiMcuSmc2ConferenceAppointment busiConferenceAppointment);

    /**
     * 新增会议预约记录
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 结果
     */
    public Map<String, Object> insertBusiMcuSmc2ConferenceAppointment(BusiMcuSmc2ConferenceAppointment busiConferenceAppointment);

    /**
     * 修改会议预约记录
     * 
     * @param busiConferenceAppointment 会议预约记录
     * @return 结果
     */
    public int updateBusiMcuSmc2ConferenceAppointment(BusiMcuSmc2ConferenceAppointment busiConferenceAppointment, boolean checkApproval);

    /**
     * 批量删除会议预约记录
     * 
     * @param ids 需要删除的会议预约记录ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2ConferenceAppointmentByIds(Long[] ids);

    /**
     * 删除会议预约记录信息
     * 
     * @param id 会议预约记录ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2ConferenceAppointmentById(Long id);

    List<DeptRecordCount> getDeptRecordCounts(Integer businessFieldType);

    /**
     * 查询预约会议列表
     * @param searchKey
     * @param deptId
     * @return
     */
    Page<BusiMcuSmc2ConferenceAppointment> selectBusiMcuSmc2ConferenceAppointmentListByKey(String searchKey, Long deptId, Integer pageIndex, Integer pageSize);

    Map<String, Object> insertBusiMcuSmc2ConferenceAppointmentIsMute(BusiMcuSmc2ConferenceAppointment busiConferenceAppointment, boolean isMute, Long userId);

    public  List<BusiMcuSmc2ConferenceAppointment> selectBusiMcuSmc2ConferenceAppointmentByTemplateId(Long id);
}
