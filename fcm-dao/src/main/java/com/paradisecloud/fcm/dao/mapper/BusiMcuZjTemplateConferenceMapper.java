package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.BusiMcuZjTemplateConference;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import org.apache.ibatis.annotations.Param;

/**
 * 紫荆MCU会议模板Mapper接口
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
public interface BusiMcuZjTemplateConferenceMapper 
{
    /**
     * 查询会议模板
     * 
     * @param id 会议模板ID
     * @return 会议模板
     */
    public BusiMcuZjTemplateConference selectBusiMcuZjTemplateConferenceById(Long id);
    
    /**
     * <pre>查询会议封面</pre>
     * @author sinhy
     * @since 2021-10-25 11:53 
     * @param id
     * @return BusiMcuZjTemplateConference
     */
    public BusiMcuZjTemplateConference selectBusiMcuZjTemplateConferenceCoverById(Long id);

    /**
     * 查询会议模板列表
     * 
     * @param busiMcuZjTemplateConference 会议模板
     * @return 会议模板集合
     */
    public List<BusiMcuZjTemplateConference> selectBusiMcuZjTemplateConferenceList(BusiMcuZjTemplateConference busiMcuZjTemplateConference);
    
    List<BusiMcuZjTemplateConference> selectAllBusiMcuZjTemplateConferenceList(BusiMcuZjTemplateConference busiMcuZjTemplateConference);
    
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
     * @param busiMcuZjTemplateConference 会议模板
     * @return 结果
     */
    public int insertBusiMcuZjTemplateConference(BusiMcuZjTemplateConference busiMcuZjTemplateConference);

    /**
     * 修改会议模板
     * 
     * @param busiMcuZjTemplateConference 会议模板
     * @return 结果
     */
    public int updateBusiMcuZjTemplateConference(BusiMcuZjTemplateConference busiMcuZjTemplateConference);

    /**
     * 删除会议模板
     * 
     * @param id 会议模板ID
     * @return 结果
     */
    public int deleteBusiMcuZjTemplateConferenceById(Long id);

    /**
     * 批量删除会议模板
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuZjTemplateConferenceByIds(Long[] ids);

    Page<BusiMcuZjTemplateConference> selectBusiMcuZjTemplateConferenceListByKey(@Param("searchKey") String searchKey , @Param("deptId") Long deptId);

    List<BusiMcuZjTemplateConference> selectAllBusiMcuZjTemplateConferenceListByKey(@Param("searchKey") String searchKey , @Param("deptId") Long deptId);
}
