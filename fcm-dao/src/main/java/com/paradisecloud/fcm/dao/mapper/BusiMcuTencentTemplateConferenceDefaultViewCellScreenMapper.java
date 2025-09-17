package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuTencentTemplateConferenceDefaultViewCellScreen;

import java.util.List;

/**
 * Tencent.0MCU默认视图下指定的多分频单元格Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuTencentTemplateConferenceDefaultViewCellScreenMapper
{
    /**
     * 查询Tencent.0MCU默认视图下指定的多分频单元格
     * 
     * @param id Tencent.0MCU默认视图下指定的多分频单元格ID
     * @return Tencent.0MCU默认视图下指定的多分频单元格
     */
    public BusiMcuTencentTemplateConferenceDefaultViewCellScreen selectBusiMcuTencentTemplateConferenceDefaultViewCellScreenById(Long id);

    /**
     * 查询Tencent.0MCU默认视图下指定的多分频单元格列表
     * 
     * @param busiMcuTencentTemplateConferenceDefaultViewCellScreen Tencent.0MCU默认视图下指定的多分频单元格
     * @return Tencent.0MCU默认视图下指定的多分频单元格集合
     */
    public List<BusiMcuTencentTemplateConferenceDefaultViewCellScreen> selectBusiMcuTencentTemplateConferenceDefaultViewCellScreenList(BusiMcuTencentTemplateConferenceDefaultViewCellScreen busiMcuTencentTemplateConferenceDefaultViewCellScreen);

    /**
     * 新增Tencent.0MCU默认视图下指定的多分频单元格
     * 
     * @param busiMcuTencentTemplateConferenceDefaultViewCellScreen Tencent.0MCU默认视图下指定的多分频单元格
     * @return 结果
     */
    public int insertBusiMcuTencentTemplateConferenceDefaultViewCellScreen(BusiMcuTencentTemplateConferenceDefaultViewCellScreen busiMcuTencentTemplateConferenceDefaultViewCellScreen);

    /**
     * 修改Tencent.0MCU默认视图下指定的多分频单元格
     * 
     * @param busiMcuTencentTemplateConferenceDefaultViewCellScreen Tencent.0MCU默认视图下指定的多分频单元格
     * @return 结果
     */
    public int updateBusiMcuTencentTemplateConferenceDefaultViewCellScreen(BusiMcuTencentTemplateConferenceDefaultViewCellScreen busiMcuTencentTemplateConferenceDefaultViewCellScreen);

    /**
     * 删除Tencent.0MCU默认视图下指定的多分频单元格
     * 
     * @param id Tencent.0MCU默认视图下指定的多分频单元格ID
     * @return 结果
     */
    public int deleteBusiMcuTencentTemplateConferenceDefaultViewCellScreenById(Long id);

    /**
     * 批量删除Tencent.0MCU默认视图下指定的多分频单元格
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuTencentTemplateConferenceDefaultViewCellScreenByIds(Long[] ids);

    void deleteBusiMcuTencentTemplateConferenceDefaultViewCellScreenByTemplateConferenceId(Long id);
}
