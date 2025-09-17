package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplateConferenceDefaultViewCellScreen;

import java.util.List;

/**
 * 中兴MCU默认视图下指定的多分频单元格Mapper接口
 * 
 * @author lilinhai
 * @date 2024-04-09
 */
public interface BusiMcuZteTemplateConferenceDefaultViewCellScreenMapper 
{
    /**
     * 查询中兴MCU默认视图下指定的多分频单元格
     * 
     * @param id 中兴MCU默认视图下指定的多分频单元格ID
     * @return 中兴MCU默认视图下指定的多分频单元格
     */
    public BusiMcuZteTemplateConferenceDefaultViewCellScreen selectBusiMcuZteTemplateConferenceDefaultViewCellScreenById(Long id);

    /**
     * 查询中兴MCU默认视图下指定的多分频单元格列表
     * 
     * @param busiMcuZteTemplateConferenceDefaultViewCellScreen 中兴MCU默认视图下指定的多分频单元格
     * @return 中兴MCU默认视图下指定的多分频单元格集合
     */
    public List<BusiMcuZteTemplateConferenceDefaultViewCellScreen> selectBusiMcuZteTemplateConferenceDefaultViewCellScreenList(BusiMcuZteTemplateConferenceDefaultViewCellScreen busiMcuZteTemplateConferenceDefaultViewCellScreen);

    /**
     * 新增中兴MCU默认视图下指定的多分频单元格
     * 
     * @param busiMcuZteTemplateConferenceDefaultViewCellScreen 中兴MCU默认视图下指定的多分频单元格
     * @return 结果
     */
    public int insertBusiMcuZteTemplateConferenceDefaultViewCellScreen(BusiMcuZteTemplateConferenceDefaultViewCellScreen busiMcuZteTemplateConferenceDefaultViewCellScreen);

    /**
     * 修改中兴MCU默认视图下指定的多分频单元格
     * 
     * @param busiMcuZteTemplateConferenceDefaultViewCellScreen 中兴MCU默认视图下指定的多分频单元格
     * @return 结果
     */
    public int updateBusiMcuZteTemplateConferenceDefaultViewCellScreen(BusiMcuZteTemplateConferenceDefaultViewCellScreen busiMcuZteTemplateConferenceDefaultViewCellScreen);

    /**
     * 删除中兴MCU默认视图下指定的多分频单元格
     * 
     * @param id 中兴MCU默认视图下指定的多分频单元格ID
     * @return 结果
     */
    public int deleteBusiMcuZteTemplateConferenceDefaultViewCellScreenById(Long id);

    /**
     * 批量删除中兴MCU默认视图下指定的多分频单元格
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuZteTemplateConferenceDefaultViewCellScreenByIds(Long[] ids);


    /**
     * 根据模板ID删除分频信息
     * @author lilinhai
     * @since 2021-04-08 15:54
     * @param conferenceTemplateId
     * @return int
     */
    public int deleteBusiMcuZteTemplateConferenceDefaultViewCellScreenByTemplateConferenceId(Long conferenceTemplateId);

}
