package com.paradisecloud.fcm.service.interfaces;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiTemplateConferenceDefaultViewCellScreen;

/**
 * 默认视图下指定的多分频单元格Service接口
 * 
 * @author lilinhai
 * @date 2021-04-08
 */
public interface IBusiTemplateConferenceDefaultViewCellScreenService 
{
    /**
     * 查询默认视图下指定的多分频单元格
     * 
     * @param id 默认视图下指定的多分频单元格ID
     * @return 默认视图下指定的多分频单元格
     */
    public BusiTemplateConferenceDefaultViewCellScreen selectBusiTemplateConferenceDefaultViewCellScreenById(Long id);

    /**
     * 查询默认视图下指定的多分频单元格列表
     * 
     * @param busiTemplateConferenceDefaultViewCellScreen 默认视图下指定的多分频单元格
     * @return 默认视图下指定的多分频单元格集合
     */
    public List<BusiTemplateConferenceDefaultViewCellScreen> selectBusiTemplateConferenceDefaultViewCellScreenList(BusiTemplateConferenceDefaultViewCellScreen busiTemplateConferenceDefaultViewCellScreen);

    /**
     * 新增默认视图下指定的多分频单元格
     * 
     * @param busiTemplateConferenceDefaultViewCellScreen 默认视图下指定的多分频单元格
     * @return 结果
     */
    public int insertBusiTemplateConferenceDefaultViewCellScreen(BusiTemplateConferenceDefaultViewCellScreen busiTemplateConferenceDefaultViewCellScreen);

    /**
     * 修改默认视图下指定的多分频单元格
     * 
     * @param busiTemplateConferenceDefaultViewCellScreen 默认视图下指定的多分频单元格
     * @return 结果
     */
    public int updateBusiTemplateConferenceDefaultViewCellScreen(BusiTemplateConferenceDefaultViewCellScreen busiTemplateConferenceDefaultViewCellScreen);

    /**
     * 批量删除默认视图下指定的多分频单元格
     * 
     * @param ids 需要删除的默认视图下指定的多分频单元格ID
     * @return 结果
     */
    public int deleteBusiTemplateConferenceDefaultViewCellScreenByIds(Long[] ids);

    /**
     * 删除默认视图下指定的多分频单元格信息
     * 
     * @param id 默认视图下指定的多分频单元格ID
     * @return 结果
     */
    public int deleteBusiTemplateConferenceDefaultViewCellScreenById(Long id);
}
