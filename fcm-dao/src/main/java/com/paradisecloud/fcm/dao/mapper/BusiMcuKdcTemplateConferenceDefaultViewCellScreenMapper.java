package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiMcuKdcTemplateConferenceDefaultViewCellScreen;

/**
 * 默认视图下指定的多分频单元格Mapper接口
 * 
 * @author lilinhai
 * @date 2021-04-08
 */
public interface BusiMcuKdcTemplateConferenceDefaultViewCellScreenMapper 
{
    /**
     * 查询默认视图下指定的多分频单元格
     * 
     * @param id 默认视图下指定的多分频单元格ID
     * @return 默认视图下指定的多分频单元格
     */
    public BusiMcuKdcTemplateConferenceDefaultViewCellScreen selectBusiMcuKdcTemplateConferenceDefaultViewCellScreenById(Long id);

    /**
     * 查询默认视图下指定的多分频单元格列表
     * 
     * @param busiTemplateConferenceDefaultViewCellScreen 默认视图下指定的多分频单元格
     * @return 默认视图下指定的多分频单元格集合
     */
    public List<BusiMcuKdcTemplateConferenceDefaultViewCellScreen> selectBusiMcuKdcTemplateConferenceDefaultViewCellScreenList(BusiMcuKdcTemplateConferenceDefaultViewCellScreen busiTemplateConferenceDefaultViewCellScreen);

    /**
     * 新增默认视图下指定的多分频单元格
     * 
     * @param busiTemplateConferenceDefaultViewCellScreen 默认视图下指定的多分频单元格
     * @return 结果
     */
    public int insertBusiMcuKdcTemplateConferenceDefaultViewCellScreen(BusiMcuKdcTemplateConferenceDefaultViewCellScreen busiTemplateConferenceDefaultViewCellScreen);

    /**
     * 修改默认视图下指定的多分频单元格
     * 
     * @param busiTemplateConferenceDefaultViewCellScreen 默认视图下指定的多分频单元格
     * @return 结果
     */
    public int updateBusiMcuKdcTemplateConferenceDefaultViewCellScreen(BusiMcuKdcTemplateConferenceDefaultViewCellScreen busiTemplateConferenceDefaultViewCellScreen);

    /**
     * 删除默认视图下指定的多分频单元格
     * 
     * @param id 默认视图下指定的多分频单元格ID
     * @return 结果
     */
    public int deleteBusiMcuKdcTemplateConferenceDefaultViewCellScreenById(Long id);
    
    /**
     * 根据模板ID删除分频信息
     * @author lilinhai
     * @since 2021-04-08 15:54 
     * @param conferenceTemplateId
     * @return int
     */
    public int deleteBusiMcuKdcTemplateConferenceDefaultViewCellScreenByTemplateConferenceId(Long conferenceTemplateId);

    /**
     * 批量删除默认视图下指定的多分频单元格
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuKdcTemplateConferenceDefaultViewCellScreenByIds(Long[] ids);
}
