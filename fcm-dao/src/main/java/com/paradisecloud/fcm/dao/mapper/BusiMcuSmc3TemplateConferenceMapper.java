package com.paradisecloud.fcm.dao.mapper;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplateConference;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SMC3.0MCU会议模板Mapper接口
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
public interface BusiMcuSmc3TemplateConferenceMapper 
{
    /**
     * 查询SMC3.0MCU会议模板
     * 
     * @param id SMC3.0MCU会议模板ID
     * @return SMC3.0MCU会议模板
     */
    public BusiMcuSmc3TemplateConference selectBusiMcuSmc3TemplateConferenceById(Long id);

    /**
     * 查询SMC3.0MCU会议模板列表
     * 
     * @param busiMcuSmc3TemplateConference SMC3.0MCU会议模板
     * @return SMC3.0MCU会议模板集合
     */
    public List<BusiMcuSmc3TemplateConference> selectBusiMcuSmc3TemplateConferenceList(BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference);

    /**
     * 新增SMC3.0MCU会议模板
     * 
     * @param busiMcuSmc3TemplateConference SMC3.0MCU会议模板
     * @return 结果
     */
    public int insertBusiMcuSmc3TemplateConference(BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference);

    /**
     * 修改SMC3.0MCU会议模板
     * 
     * @param busiMcuSmc3TemplateConference SMC3.0MCU会议模板
     * @return 结果
     */
    public int updateBusiMcuSmc3TemplateConference(BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference);

    /**
     * 删除SMC3.0MCU会议模板
     * 
     * @param id SMC3.0MCU会议模板ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3TemplateConferenceById(Long id);

    /**
     * 批量删除SMC3.0MCU会议模板
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3TemplateConferenceByIds(Long[] ids);

    List<BusiMcuSmc3TemplateConference> selectAllBusiMcuSmc3TemplateConferenceListByKey(@Param("searchKey") String searchKey , @Param("deptId") Long deptId);

    Page<BusiMcuSmc3TemplateConference> selectBusiMcuSmc3TemplateConferenceListByKey(@Param("searchKey") String searchKey , @Param("deptId") Long deptId);

    List<DeptRecordCount> getDeptTemplateCount(Integer businessFieldType);

    BusiMcuSmc3TemplateConference selectBusiMcuSmc3TemplateConferenceBySmcTemplateId(@Param("smcTemplateId") String smcTemplateId);


    List<BusiMcuSmc3TemplateConference> selectCascadeConferenceList(@Param("deptId") Long deptId);
}
