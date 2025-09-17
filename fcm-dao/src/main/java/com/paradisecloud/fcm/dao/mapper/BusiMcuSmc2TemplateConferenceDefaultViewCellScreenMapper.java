package com.paradisecloud.fcm.dao.mapper;


import com.paradisecloud.fcm.dao.model.BusiMcuSmc2TemplateConferenceDefaultViewCellScreen;

import java.util.List;

/**
 * SMC2.0MCU默认视图下指定的多分频单元格Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuSmc2TemplateConferenceDefaultViewCellScreenMapper
{
    /**
     * 查询SMC2.0MCU默认视图下指定的多分频单元格
     * 
     * @param id SMC2.0MCU默认视图下指定的多分频单元格ID
     * @return SMC2.0MCU默认视图下指定的多分频单元格
     */
    public BusiMcuSmc2TemplateConferenceDefaultViewCellScreen selectBusiMcuSmc2TemplateConferenceDefaultViewCellScreenById(Long id);

    /**
     * 查询SMC2.0MCU默认视图下指定的多分频单元格列表
     * 
     * @param busiMcuSmc2TemplateConferenceDefaultViewCellScreen SMC2.0MCU默认视图下指定的多分频单元格
     * @return SMC2.0MCU默认视图下指定的多分频单元格集合
     */
    public List<BusiMcuSmc2TemplateConferenceDefaultViewCellScreen> selectBusiMcuSmc2TemplateConferenceDefaultViewCellScreenList(BusiMcuSmc2TemplateConferenceDefaultViewCellScreen busiMcuSmc2TemplateConferenceDefaultViewCellScreen);

    /**
     * 新增SMC2.0MCU默认视图下指定的多分频单元格
     * 
     * @param busiMcuSmc2TemplateConferenceDefaultViewCellScreen SMC2.0MCU默认视图下指定的多分频单元格
     * @return 结果
     */
    public int insertBusiMcuSmc2TemplateConferenceDefaultViewCellScreen(BusiMcuSmc2TemplateConferenceDefaultViewCellScreen busiMcuSmc2TemplateConferenceDefaultViewCellScreen);

    /**
     * 修改SMC2.0MCU默认视图下指定的多分频单元格
     * 
     * @param busiMcuSmc2TemplateConferenceDefaultViewCellScreen SMC2.0MCU默认视图下指定的多分频单元格
     * @return 结果
     */
    public int updateBusiMcuSmc2TemplateConferenceDefaultViewCellScreen(BusiMcuSmc2TemplateConferenceDefaultViewCellScreen busiMcuSmc2TemplateConferenceDefaultViewCellScreen);

    /**
     * 删除SMC2.0MCU默认视图下指定的多分频单元格
     * 
     * @param id SMC2.0MCU默认视图下指定的多分频单元格ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2TemplateConferenceDefaultViewCellScreenById(Long id);

    /**
     * 批量删除SMC2.0MCU默认视图下指定的多分频单元格
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2TemplateConferenceDefaultViewCellScreenByIds(Long[] ids);

    void deleteBusiMcuSmc2TemplateConferenceDefaultViewCellScreenByTemplateConferenceId(Long id);
}
