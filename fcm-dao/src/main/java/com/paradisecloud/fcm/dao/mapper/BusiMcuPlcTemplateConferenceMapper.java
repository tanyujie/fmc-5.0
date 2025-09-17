package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.BusiMcuPlcTemplateConference;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import org.apache.ibatis.annotations.Param;

/**
 * 紫荆MCU会议模板Mapper接口
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
public interface BusiMcuPlcTemplateConferenceMapper
{
    /**
     * 查询会议模板
     * 
     * @param id 会议模板ID
     * @return 会议模板
     */
    public BusiMcuPlcTemplateConference selectBusiMcuPlcTemplateConferenceById(Long id);
    
    /**
     * <pre>查询会议封面</pre>
     * @author sinhy
     * @since 2021-10-25 11:53 
     * @param id
     * @return BusiMcuPlcTemplateConference
     */
    public BusiMcuPlcTemplateConference selectBusiMcuPlcTemplateConferenceCoverById(Long id);

    /**
     * 查询会议模板列表
     * 
     * @param busiMcuPlcTemplateConference 会议模板
     * @return 会议模板集合
     */
    public List<BusiMcuPlcTemplateConference> selectBusiMcuPlcTemplateConferenceList(BusiMcuPlcTemplateConference busiMcuPlcTemplateConference);
    
    List<BusiMcuPlcTemplateConference> selectAllBusiMcuPlcTemplateConferenceList(BusiMcuPlcTemplateConference busiMcuPlcTemplateConference);
    
    /**
     * 模板计数
     * @author sinhy
     * @since 2021-10-25 12:02 
     * @param businessFieldType 业务领域类型
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptTemplateCount(Integer businessFieldType);

    /**
     * 新增会议模板
     * 
     * @param busiMcuPlcTemplateConference 会议模板
     * @return 结果
     */
    public int insertBusiMcuPlcTemplateConference(BusiMcuPlcTemplateConference busiMcuPlcTemplateConference);

    /**
     * 修改会议模板
     * 
     * @param busiMcuPlcTemplateConference 会议模板
     * @return 结果
     */
    public int updateBusiMcuPlcTemplateConference(BusiMcuPlcTemplateConference busiMcuPlcTemplateConference);

    /**
     * 删除会议模板
     * 
     * @param id 会议模板ID
     * @return 结果
     */
    public int deleteBusiMcuPlcTemplateConferenceById(Long id);

    /**
     * 批量删除会议模板
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuPlcTemplateConferenceByIds(Long[] ids);

    Page<BusiMcuPlcTemplateConference> selectBusiMcuPlcTemplateConferenceListByKey(@Param("searchKey") String searchKey , @Param("deptId") Long deptId);

    List<BusiMcuPlcTemplateConference> selectAllBusiMcuPlcTemplateConferenceListByKey(@Param("searchKey") String searchKey , @Param("deptId") Long deptId);
}
