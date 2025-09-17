package com.paradisecloud.fcm.dao.mapper;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentTemplateConference;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Tencent.0MCU会议模板Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuTencentTemplateConferenceMapper
{
    /**
     * 查询Tencent.0MCU会议模板
     * 
     * @param id Tencent.0MCU会议模板ID
     * @return Tencent.0MCU会议模板
     */
    public BusiMcuTencentTemplateConference selectBusiMcuTencentTemplateConferenceById(Long id);

    /**
     * 查询Tencent.0MCU会议模板列表
     * 
     * @param busiMcuTencentTemplateConference Tencent.0MCU会议模板
     * @return Tencent.0MCU会议模板集合
     */
    public List<BusiMcuTencentTemplateConference> selectBusiMcuTencentTemplateConferenceList(BusiMcuTencentTemplateConference busiMcuTencentTemplateConference);

    /**
     * 新增Tencent.0MCU会议模板
     * 
     * @param busiMcuTencentTemplateConference Tencent.0MCU会议模板
     * @return 结果
     */
    public int insertBusiMcuTencentTemplateConference(BusiMcuTencentTemplateConference busiMcuTencentTemplateConference);

    /**
     * 修改Tencent.0MCU会议模板
     * 
     * @param busiMcuTencentTemplateConference Tencent.0MCU会议模板
     * @return 结果
     */
    public int updateBusiMcuTencentTemplateConference(BusiMcuTencentTemplateConference busiMcuTencentTemplateConference);

    /**
     * 删除Tencent.0MCU会议模板
     * 
     * @param id Tencent.0MCU会议模板ID
     * @return 结果
     */
    public int deleteBusiMcuTencentTemplateConferenceById(Long id);

    /**
     * 批量删除Tencent.0MCU会议模板
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuTencentTemplateConferenceByIds(Long[] ids);

    List<BusiMcuTencentTemplateConference> selectAllBusiMcuTencentTemplateConferenceListByKey(@Param("searchKey") String searchKey , @Param("deptId") Long deptId);

    Page<BusiMcuTencentTemplateConference> selectBusiMcuTencentTemplateConferenceListByKey(@Param("searchKey") String searchKey , @Param("deptId") Long deptId);

    List<DeptRecordCount> getDeptTemplateCount(Integer businessFieldType);
}
