package com.paradisecloud.fcm.dao.mapper;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2TemplateConference;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SMC2.0MCU会议模板Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuSmc2TemplateConferenceMapper
{
    /**
     * 查询SMC2.0MCU会议模板
     * 
     * @param id SMC2.0MCU会议模板ID
     * @return SMC2.0MCU会议模板
     */
    public BusiMcuSmc2TemplateConference selectBusiMcuSmc2TemplateConferenceById(Long id);

    /**
     * 查询SMC2.0MCU会议模板列表
     * 
     * @param busiMcuSmc2TemplateConference SMC2.0MCU会议模板
     * @return SMC2.0MCU会议模板集合
     */
    public List<BusiMcuSmc2TemplateConference> selectBusiMcuSmc2TemplateConferenceList(BusiMcuSmc2TemplateConference busiMcuSmc2TemplateConference);

    /**
     * 新增SMC2.0MCU会议模板
     * 
     * @param busiMcuSmc2TemplateConference SMC2.0MCU会议模板
     * @return 结果
     */
    public int insertBusiMcuSmc2TemplateConference(BusiMcuSmc2TemplateConference busiMcuSmc2TemplateConference);

    /**
     * 修改SMC2.0MCU会议模板
     * 
     * @param busiMcuSmc2TemplateConference SMC2.0MCU会议模板
     * @return 结果
     */
    public int updateBusiMcuSmc2TemplateConference(BusiMcuSmc2TemplateConference busiMcuSmc2TemplateConference);

    /**
     * 删除SMC2.0MCU会议模板
     * 
     * @param id SMC2.0MCU会议模板ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2TemplateConferenceById(Long id);

    /**
     * 批量删除SMC2.0MCU会议模板
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2TemplateConferenceByIds(Long[] ids);

    List<BusiMcuSmc2TemplateConference> selectAllBusiMcuSmc2TemplateConferenceListByKey(@Param("searchKey") String searchKey , @Param("deptId") Long deptId);

    Page<BusiMcuSmc2TemplateConference> selectBusiMcuSmc2TemplateConferenceListByKey(@Param("searchKey") String searchKey , @Param("deptId") Long deptId);

    List<DeptRecordCount> getDeptTemplateCount(Integer businessFieldType);
}
