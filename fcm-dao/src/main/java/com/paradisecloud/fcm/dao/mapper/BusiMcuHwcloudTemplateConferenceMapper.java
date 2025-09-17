package com.paradisecloud.fcm.dao.mapper;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudTemplateConference;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Hwcloud.0MCU会议模板Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuHwcloudTemplateConferenceMapper
{
    /**
     * 查询Hwcloud.0MCU会议模板
     * 
     * @param id Hwcloud.0MCU会议模板ID
     * @return Hwcloud.0MCU会议模板
     */
    public BusiMcuHwcloudTemplateConference selectBusiMcuHwcloudTemplateConferenceById(Long id);

    /**
     * 查询Hwcloud.0MCU会议模板列表
     * 
     * @param busiMcuHwcloudTemplateConference Hwcloud.0MCU会议模板
     * @return Hwcloud.0MCU会议模板集合
     */
    public List<BusiMcuHwcloudTemplateConference> selectBusiMcuHwcloudTemplateConferenceList(BusiMcuHwcloudTemplateConference busiMcuHwcloudTemplateConference);

    /**
     * 新增Hwcloud.0MCU会议模板
     * 
     * @param busiMcuHwcloudTemplateConference Hwcloud.0MCU会议模板
     * @return 结果
     */
    public int insertBusiMcuHwcloudTemplateConference(BusiMcuHwcloudTemplateConference busiMcuHwcloudTemplateConference);

    /**
     * 修改Hwcloud.0MCU会议模板
     * 
     * @param busiMcuHwcloudTemplateConference Hwcloud.0MCU会议模板
     * @return 结果
     */
    public int updateBusiMcuHwcloudTemplateConference(BusiMcuHwcloudTemplateConference busiMcuHwcloudTemplateConference);

    /**
     * 删除Hwcloud.0MCU会议模板
     * 
     * @param id Hwcloud.0MCU会议模板ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudTemplateConferenceById(Long id);

    /**
     * 批量删除Hwcloud.0MCU会议模板
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudTemplateConferenceByIds(Long[] ids);

    List<BusiMcuHwcloudTemplateConference> selectAllBusiMcuHwcloudTemplateConferenceListByKey(@Param("searchKey") String searchKey , @Param("deptId") Long deptId);

    Page<BusiMcuHwcloudTemplateConference> selectBusiMcuHwcloudTemplateConferenceListByKey(@Param("searchKey") String searchKey , @Param("deptId") Long deptId);

    List<DeptRecordCount> getDeptTemplateCount(Integer businessFieldType);
}
