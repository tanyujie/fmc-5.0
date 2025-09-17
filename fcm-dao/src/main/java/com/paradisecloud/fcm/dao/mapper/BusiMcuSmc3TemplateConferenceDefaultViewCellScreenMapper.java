package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplateConferenceDefaultViewCellScreen;

import java.util.List;

/**
 * SMC3.0MCU默认视图下指定的多分频单元格Mapper接口
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
public interface BusiMcuSmc3TemplateConferenceDefaultViewCellScreenMapper 
{
    /**
     * 查询SMC3.0MCU默认视图下指定的多分频单元格
     * 
     * @param id SMC3.0MCU默认视图下指定的多分频单元格ID
     * @return SMC3.0MCU默认视图下指定的多分频单元格
     */
    public BusiMcuSmc3TemplateConferenceDefaultViewCellScreen selectBusiMcuSmc3TemplateConferenceDefaultViewCellScreenById(Long id);

    /**
     * 查询SMC3.0MCU默认视图下指定的多分频单元格列表
     * 
     * @param busiMcuSmc3TemplateConferenceDefaultViewCellScreen SMC3.0MCU默认视图下指定的多分频单元格
     * @return SMC3.0MCU默认视图下指定的多分频单元格集合
     */
    public List<BusiMcuSmc3TemplateConferenceDefaultViewCellScreen> selectBusiMcuSmc3TemplateConferenceDefaultViewCellScreenList(BusiMcuSmc3TemplateConferenceDefaultViewCellScreen busiMcuSmc3TemplateConferenceDefaultViewCellScreen);

    /**
     * 新增SMC3.0MCU默认视图下指定的多分频单元格
     * 
     * @param busiMcuSmc3TemplateConferenceDefaultViewCellScreen SMC3.0MCU默认视图下指定的多分频单元格
     * @return 结果
     */
    public int insertBusiMcuSmc3TemplateConferenceDefaultViewCellScreen(BusiMcuSmc3TemplateConferenceDefaultViewCellScreen busiMcuSmc3TemplateConferenceDefaultViewCellScreen);

    /**
     * 修改SMC3.0MCU默认视图下指定的多分频单元格
     * 
     * @param busiMcuSmc3TemplateConferenceDefaultViewCellScreen SMC3.0MCU默认视图下指定的多分频单元格
     * @return 结果
     */
    public int updateBusiMcuSmc3TemplateConferenceDefaultViewCellScreen(BusiMcuSmc3TemplateConferenceDefaultViewCellScreen busiMcuSmc3TemplateConferenceDefaultViewCellScreen);

    /**
     * 删除SMC3.0MCU默认视图下指定的多分频单元格
     * 
     * @param id SMC3.0MCU默认视图下指定的多分频单元格ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3TemplateConferenceDefaultViewCellScreenById(Long id);

    /**
     * 批量删除SMC3.0MCU默认视图下指定的多分频单元格
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3TemplateConferenceDefaultViewCellScreenByIds(Long[] ids);

    void deleteBusiMcuSmc3TemplateConferenceDefaultViewCellScreenByTemplateConferenceId(Long id);
}
