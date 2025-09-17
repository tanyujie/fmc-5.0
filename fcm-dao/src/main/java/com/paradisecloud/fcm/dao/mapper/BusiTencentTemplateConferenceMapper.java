package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiTencentTemplateConference;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 华为MCU会议模板Mapper接口
 * 
 * @author lilinhai
 * @date 2023-07-05
 */
public interface BusiTencentTemplateConferenceMapper 
{
    /**
     * 查询华为MCU会议模板
     * 
     * @param id 华为MCU会议模板ID
     * @return 华为MCU会议模板
     */
    public BusiTencentTemplateConference selectBusiTencentTemplateConferenceById(Long id);

    /**
     * 查询华为MCU会议模板列表
     * 
     * @param busiTencentTemplateConference 华为MCU会议模板
     * @return 华为MCU会议模板集合
     */
    public List<BusiTencentTemplateConference> selectBusiTencentTemplateConferenceList(BusiTencentTemplateConference busiTencentTemplateConference);

    /**
     * 新增华为MCU会议模板
     * 
     * @param busiTencentTemplateConference 华为MCU会议模板
     * @return 结果
     */
    public int insertBusiTencentTemplateConference(BusiTencentTemplateConference busiTencentTemplateConference);

    /**
     * 修改华为MCU会议模板
     * 
     * @param busiTencentTemplateConference 华为MCU会议模板
     * @return 结果
     */
    public int updateBusiTencentTemplateConference(BusiTencentTemplateConference busiTencentTemplateConference);

    /**
     * 删除华为MCU会议模板
     * 
     * @param id 华为MCU会议模板ID
     * @return 结果
     */
    public int deleteBusiTencentTemplateConferenceById(Long id);

    /**
     * 批量删除华为MCU会议模板
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiTencentTemplateConferenceByIds(Long[] ids);

    List<BusiTencentTemplateConference> queryTemplateListByDeptId(@Param("deptId") Long deptId, @Param("name")String name);
}
