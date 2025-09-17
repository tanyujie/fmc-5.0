package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuDingTemplateConferenceDefaultViewCellScreen;

import java.util.List;

/**
 * Ding.0MCU默认视图下指定的多分频单元格Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuDingTemplateConferenceDefaultViewCellScreenMapper
{
    /**
     * 查询Ding.0MCU默认视图下指定的多分频单元格
     * 
     * @param id Ding.0MCU默认视图下指定的多分频单元格ID
     * @return Ding.0MCU默认视图下指定的多分频单元格
     */
    public BusiMcuDingTemplateConferenceDefaultViewCellScreen selectBusiMcuDingTemplateConferenceDefaultViewCellScreenById(Long id);

    /**
     * 查询Ding.0MCU默认视图下指定的多分频单元格列表
     * 
     * @param busiMcuDingTemplateConferenceDefaultViewCellScreen Ding.0MCU默认视图下指定的多分频单元格
     * @return Ding.0MCU默认视图下指定的多分频单元格集合
     */
    public List<BusiMcuDingTemplateConferenceDefaultViewCellScreen> selectBusiMcuDingTemplateConferenceDefaultViewCellScreenList(BusiMcuDingTemplateConferenceDefaultViewCellScreen busiMcuDingTemplateConferenceDefaultViewCellScreen);

    /**
     * 新增Ding.0MCU默认视图下指定的多分频单元格
     * 
     * @param busiMcuDingTemplateConferenceDefaultViewCellScreen Ding.0MCU默认视图下指定的多分频单元格
     * @return 结果
     */
    public int insertBusiMcuDingTemplateConferenceDefaultViewCellScreen(BusiMcuDingTemplateConferenceDefaultViewCellScreen busiMcuDingTemplateConferenceDefaultViewCellScreen);

    /**
     * 修改Ding.0MCU默认视图下指定的多分频单元格
     * 
     * @param busiMcuDingTemplateConferenceDefaultViewCellScreen Ding.0MCU默认视图下指定的多分频单元格
     * @return 结果
     */
    public int updateBusiMcuDingTemplateConferenceDefaultViewCellScreen(BusiMcuDingTemplateConferenceDefaultViewCellScreen busiMcuDingTemplateConferenceDefaultViewCellScreen);

    /**
     * 删除Ding.0MCU默认视图下指定的多分频单元格
     * 
     * @param id Ding.0MCU默认视图下指定的多分频单元格ID
     * @return 结果
     */
    public int deleteBusiMcuDingTemplateConferenceDefaultViewCellScreenById(Long id);

    /**
     * 批量删除Ding.0MCU默认视图下指定的多分频单元格
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuDingTemplateConferenceDefaultViewCellScreenByIds(Long[] ids);

    void deleteBusiMcuDingTemplateConferenceDefaultViewCellScreenByTemplateConferenceId(Long id);
}
