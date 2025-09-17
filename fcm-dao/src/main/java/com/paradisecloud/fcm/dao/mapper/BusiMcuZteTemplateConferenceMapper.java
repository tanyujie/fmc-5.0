package com.paradisecloud.fcm.dao.mapper;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplateConference;
import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplateConference;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 中兴MCU会议模板Mapper接口
 * 
 * @author lilinhai
 * @date 2024-04-09
 */
public interface BusiMcuZteTemplateConferenceMapper 
{
    /**
     * 查询会议模板
     *
     * @param id 会议模板ID
     * @return 会议模板
     */
    public BusiMcuZteTemplateConference selectBusiMcuZteTemplateConferenceById(Long id);

    /**
     * <pre>查询会议封面</pre>
     * @author sinhy
     * @since 2021-10-25 11:53 
     * @param id
     * @return BusiMcuZteTemplateConference
     */
    public BusiMcuZteTemplateConference selectBusiMcuZteTemplateConferenceCoverById(Long id);

    /**
     * 查询会议模板列表
     *
     * @param busiMcuZteTemplateConference 会议模板
     * @return 会议模板集合
     */
    public List<BusiMcuZteTemplateConference> selectBusiMcuZteTemplateConferenceList(BusiMcuZteTemplateConference busiMcuZteTemplateConference);

    List<BusiMcuZteTemplateConference> selectAllBusiMcuZteTemplateConferenceList(BusiMcuZteTemplateConference busiMcuZteTemplateConference);

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
     * @param busiMcuZteTemplateConference 会议模板
     * @return 结果
     */
    public int insertBusiMcuZteTemplateConference(BusiMcuZteTemplateConference busiMcuZteTemplateConference);

    /**
     * 修改会议模板
     *
     * @param busiMcuZteTemplateConference 会议模板
     * @return 结果
     */
    public int updateBusiMcuZteTemplateConference(BusiMcuZteTemplateConference busiMcuZteTemplateConference);

    /**
     * 删除会议模板
     *
     * @param id 会议模板ID
     * @return 结果
     */
    public int deleteBusiMcuZteTemplateConferenceById(Long id);

    /**
     * 批量删除会议模板
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuZteTemplateConferenceByIds(Long[] ids);

    Page<BusiMcuZteTemplateConference> selectBusiMcuZteTemplateConferenceListByKey(@Param("searchKey") String searchKey , @Param("deptId") Long deptId);

    List<BusiMcuZteTemplateConference> selectAllBusiMcuZteTemplateConferenceListByKey(@Param("searchKey") String searchKey , @Param("deptId") Long deptId);
}
