package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudTemplateConferenceDefaultViewCellScreen;

import java.util.List;

/**
 * Hwcloud.0MCU默认视图下指定的多分频单元格Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuHwcloudTemplateConferenceDefaultViewCellScreenMapper
{
    /**
     * 查询Hwcloud.0MCU默认视图下指定的多分频单元格
     * 
     * @param id Hwcloud.0MCU默认视图下指定的多分频单元格ID
     * @return Hwcloud.0MCU默认视图下指定的多分频单元格
     */
    public BusiMcuHwcloudTemplateConferenceDefaultViewCellScreen selectBusiMcuHwcloudTemplateConferenceDefaultViewCellScreenById(Long id);

    /**
     * 查询Hwcloud.0MCU默认视图下指定的多分频单元格列表
     * 
     * @param busiMcuHwcloudTemplateConferenceDefaultViewCellScreen Hwcloud.0MCU默认视图下指定的多分频单元格
     * @return Hwcloud.0MCU默认视图下指定的多分频单元格集合
     */
    public List<BusiMcuHwcloudTemplateConferenceDefaultViewCellScreen> selectBusiMcuHwcloudTemplateConferenceDefaultViewCellScreenList(BusiMcuHwcloudTemplateConferenceDefaultViewCellScreen busiMcuHwcloudTemplateConferenceDefaultViewCellScreen);

    /**
     * 新增Hwcloud.0MCU默认视图下指定的多分频单元格
     * 
     * @param busiMcuHwcloudTemplateConferenceDefaultViewCellScreen Hwcloud.0MCU默认视图下指定的多分频单元格
     * @return 结果
     */
    public int insertBusiMcuHwcloudTemplateConferenceDefaultViewCellScreen(BusiMcuHwcloudTemplateConferenceDefaultViewCellScreen busiMcuHwcloudTemplateConferenceDefaultViewCellScreen);

    /**
     * 修改Hwcloud.0MCU默认视图下指定的多分频单元格
     * 
     * @param busiMcuHwcloudTemplateConferenceDefaultViewCellScreen Hwcloud.0MCU默认视图下指定的多分频单元格
     * @return 结果
     */
    public int updateBusiMcuHwcloudTemplateConferenceDefaultViewCellScreen(BusiMcuHwcloudTemplateConferenceDefaultViewCellScreen busiMcuHwcloudTemplateConferenceDefaultViewCellScreen);

    /**
     * 删除Hwcloud.0MCU默认视图下指定的多分频单元格
     * 
     * @param id Hwcloud.0MCU默认视图下指定的多分频单元格ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudTemplateConferenceDefaultViewCellScreenById(Long id);

    /**
     * 批量删除Hwcloud.0MCU默认视图下指定的多分频单元格
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudTemplateConferenceDefaultViewCellScreenByIds(Long[] ids);

    void deleteBusiMcuHwcloudTemplateConferenceDefaultViewCellScreenByTemplateConferenceId(Long id);
}
