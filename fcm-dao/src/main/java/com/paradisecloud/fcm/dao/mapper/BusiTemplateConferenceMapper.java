package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import org.apache.ibatis.annotations.Param;

/**
 * 会议模板Mapper接口
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
public interface BusiTemplateConferenceMapper 
{
    /**
     * 查询会议模板
     * 
     * @param id 会议模板ID
     * @return 会议模板
     */
    public BusiTemplateConference selectBusiTemplateConferenceById(Long id);
    
    /**
     * <pre>查询会议封面</pre>
     * @author sinhy
     * @since 2021-10-25 11:53 
     * @param id
     * @return BusiTemplateConference
     */
    public BusiTemplateConference selectBusiTemplateConferenceCoverById(Long id);

    /**
     * 查询会议模板列表
     * 
     * @param busiTemplateConference 会议模板
     * @return 会议模板集合
     */
    public List<BusiTemplateConference> selectBusiTemplateConferenceList(BusiTemplateConference busiTemplateConference);

     Page<BusiTemplateConference> selectBusiTemplateConferenceListByKey(@Param("searchKey") String searchKey , @Param("deptId") Long deptId);

    List<BusiTemplateConference> selectAllBusiTemplateConferenceListByKey(@Param("searchKey") String searchKey , @Param("deptId") Long deptId);
    
    List<BusiTemplateConference> selectAllBusiTemplateConferenceList(BusiTemplateConference busiTemplateConference);
    
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
     * @param busiTemplateConference 会议模板
     * @return 结果
     */
    public int insertBusiTemplateConference(BusiTemplateConference busiTemplateConference);

    /**
     * 修改会议模板
     * 
     * @param busiTemplateConference 会议模板
     * @return 结果
     */
    public int updateBusiTemplateConference(BusiTemplateConference busiTemplateConference);

    /**
     * 删除会议模板
     * 
     * @param id 会议模板ID
     * @return 结果
     */
    public int deleteBusiTemplateConferenceById(Long id);

    /**
     * 批量删除会议模板
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiTemplateConferenceByIds(Long[] ids);

    /**
     * 更新会议号
     *
     * @param busiTemplateConference 会议模板
     * @return 结果
     */
    public int updateConferenceNumber(BusiTemplateConference busiTemplateConference);
}
