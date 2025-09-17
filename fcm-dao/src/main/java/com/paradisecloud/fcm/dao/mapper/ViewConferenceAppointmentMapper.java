package com.paradisecloud.fcm.dao.mapper;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.dao.model.ViewConferenceAppointment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会议预约记录Mapper接口
 *
 * @author lilinhai
 * @date 2021-05-24
 */
public interface ViewConferenceAppointmentMapper {

    /**
     * 查询会议预约记录
     *
     * @param mcuType MCU类型
     * @param id 会议预约记录ID
     * @return 会议预约记录
     */
    ViewConferenceAppointment selectViewConferenceAppointmentById(@Param("mcuType") String mcuType, @Param("id") Long id);

    /**
     * 查询会议预约记录
     *
     * @param mcuType MCU类型
     * @param templateId
     * @return
     */
    List<ViewConferenceAppointment> selectViewConferenceAppointmentByTemplateId(@Param("mcuType") String mcuType, @Param("templateId") Long templateId);

    /**
     * 查询会议预约记录列表
     *
     * @param viewConferenceAppointment 会议预约记录
     * @return 会议预约记录集合
     */
    List<ViewConferenceAppointment> selectViewConferenceAppointmentList(ViewConferenceAppointment viewConferenceAppointment);

    /**
     * 预约计数
     *
     * @author sinhy
     * @since 2021-10-29 10:54
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts(Integer businessFieldType);

    /**
     * 搜获会议列表
     *
     * @param searchKey
     * @param deptId
     * @return
     */
    Page<ViewConferenceAppointment> selectViewConferenceAppointmentListBykey(@Param("searchKey") String searchKey, @Param("deptId")Long deptId);

    /**
     * 查询会议预约记录列表
     *
     * @param viewConferenceAppointment 会议预约记录
     * @return 会议预约记录集合
     */
    List<ViewConferenceAppointment> selectViewConferenceAppointmentByTypesList(ViewConferenceAppointment viewConferenceAppointment);
}
