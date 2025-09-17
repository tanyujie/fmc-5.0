package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiSmc2TemplateTerminal;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author lilinhai
 * @date 2023-04-20
 */
public interface BusiSmc2TemplateTerminalMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public BusiSmc2TemplateTerminal selectBusiSmc2TemplateTerminalById(Integer id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busiSmc2TemplateTerminal 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<BusiSmc2TemplateTerminal> selectBusiSmc2TemplateTerminalList(BusiSmc2TemplateTerminal busiSmc2TemplateTerminal);

    /**
     * 新增【请填写功能名称】
     * 
     * @param busiSmc2TemplateTerminal 【请填写功能名称】
     * @return 结果
     */
    public int insertBusiSmc2TemplateTerminal(BusiSmc2TemplateTerminal busiSmc2TemplateTerminal);

    /**
     * 修改【请填写功能名称】
     * 
     * @param busiSmc2TemplateTerminal 【请填写功能名称】
     * @return 结果
     */
    public int updateBusiSmc2TemplateTerminal(BusiSmc2TemplateTerminal busiSmc2TemplateTerminal);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmc2TemplateTerminalById(Integer id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSmc2TemplateTerminalByIds(Integer[] ids);

    void deleteBusiSmc2TemplateTerminalByTemplateId(int templateId);
}
