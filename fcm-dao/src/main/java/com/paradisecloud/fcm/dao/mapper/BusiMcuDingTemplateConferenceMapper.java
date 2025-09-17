package com.paradisecloud.fcm.dao.mapper;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.BusiMcuDingTemplateConference;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Ding.0MCU会议模板Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuDingTemplateConferenceMapper
{
    /**
     * 查询Ding.0MCU会议模板
     * 
     * @param id Ding.0MCU会议模板ID
     * @return Ding.0MCU会议模板
     */
    public BusiMcuDingTemplateConference selectBusiMcuDingTemplateConferenceById(Long id);

    /**
     * 查询Ding.0MCU会议模板列表
     * 
     * @param busiMcuDingTemplateConference Ding.0MCU会议模板
     * @return Ding.0MCU会议模板集合
     */
    public List<BusiMcuDingTemplateConference> selectBusiMcuDingTemplateConferenceList(BusiMcuDingTemplateConference busiMcuDingTemplateConference);

    /**
     * 新增Ding.0MCU会议模板
     * 
     * @param busiMcuDingTemplateConference Ding.0MCU会议模板
     * @return 结果
     */
    public int insertBusiMcuDingTemplateConference(BusiMcuDingTemplateConference busiMcuDingTemplateConference);

    /**
     * 修改Ding.0MCU会议模板
     * 
     * @param busiMcuDingTemplateConference Ding.0MCU会议模板
     * @return 结果
     */
    public int updateBusiMcuDingTemplateConference(BusiMcuDingTemplateConference busiMcuDingTemplateConference);

    /**
     * 删除Ding.0MCU会议模板
     * 
     * @param id Ding.0MCU会议模板ID
     * @return 结果
     */
    public int deleteBusiMcuDingTemplateConferenceById(Long id);

    /**
     * 批量删除Ding.0MCU会议模板
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuDingTemplateConferenceByIds(Long[] ids);

    List<BusiMcuDingTemplateConference> selectAllBusiMcuDingTemplateConferenceListByKey(@Param("searchKey") String searchKey , @Param("deptId") Long deptId);

    Page<BusiMcuDingTemplateConference> selectBusiMcuDingTemplateConferenceListByKey(@Param("searchKey") String searchKey , @Param("deptId") Long deptId);

    List<DeptRecordCount> getDeptTemplateCount(Integer businessFieldType);
}
