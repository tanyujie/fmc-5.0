package com.paradisecloud.fcm.dao.mapper;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.ViewTemplateConference;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ViewTemplateConferenceMapper {
    
    /**
     * 查询会议模板
     *
     * @param id 会议模板ID
     * @return 会议模板
     */
    ViewTemplateConference selectViewTemplateConferenceById(@Param("mcuType") String mcuType, @Param("id") Long id);

    /**
     * <pre>查询会议封面</pre>
     * @author sinhy
     * @since 2021-10-25 11:53 
     * @param id
     * @return ViewTemplateConference
     */
    ViewTemplateConference selectViewTemplateConferenceCoverById(@Param("mcuType") String mcuType, @Param("id") Long id);

    /**
     * 查询会议模板列表
     *
     * @param viewTemplateConference 会议模板
     * @return 会议模板集合
     */
    List<ViewTemplateConference> selectViewTemplateConferenceList(ViewTemplateConference viewTemplateConference);

    /**
     *
     * @param searchKey
     * @param deptId
     * @return
     */
    Page<ViewTemplateConference> selectViewTemplateConferenceListByKey(@Param("searchKey") String searchKey , @Param("deptId") Long deptId);

    /**
     *
     * @param searchKey
     * @param deptId
     * @return
     */
    List<ViewTemplateConference> selectAllViewTemplateConferenceListByKey(@Param("searchKey") String searchKey , @Param("deptId") Long deptId);

    /**
     *
     * @param viewTemplateConference
     * @return
     */
    List<ViewTemplateConference> selectAllViewTemplateConferenceList(ViewTemplateConference viewTemplateConference);

    /**
     * 模板计数
     * @author sinhy
     * @since 2021-10-25 12:02 
     * @param businessFieldType 业务领域类型
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptTemplateCount(Integer businessFieldType);

    /**
     * 查询会议模板列表
     *
     * @param viewTemplateConference 会议模板
     * @return 会议模板集合
     */
    List<ViewTemplateConference> selectCanDownCascadeViewTemplateConferenceList(ViewTemplateConference viewTemplateConference);

    /**
     * 查询会议模板列表
     *
     * @param viewTemplateConference 会议模板
     * @return 会议模板集合
     */
    List<ViewTemplateConference> selectCanDownCascadeViewTemplateConferenceListExcludeHasDownCascade(ViewTemplateConference viewTemplateConference);
}
