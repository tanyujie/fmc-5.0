package com.paradisecloud.fcm.smc2.service.impl;

import java.util.List;

import com.paradisecloud.fcm.dao.mapper.BusiSmc2TemplateTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiSmc2TemplateTerminal;
import com.paradisecloud.fcm.smc2.service.IBusiSmc2TemplateTerminalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author lilinhai
 * @date 2023-04-20
 */
@Service
public class BusiSmc2TemplateTerminalServiceImpl implements IBusiSmc2TemplateTerminalService
{
    @Autowired
    private BusiSmc2TemplateTerminalMapper busiSmc2TemplateTerminalMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public BusiSmc2TemplateTerminal selectBusiSmc2TemplateTerminalById(Integer id)
    {
        return busiSmc2TemplateTerminalMapper.selectBusiSmc2TemplateTerminalById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busiSmc2TemplateTerminal 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<BusiSmc2TemplateTerminal> selectBusiSmc2TemplateTerminalList(BusiSmc2TemplateTerminal busiSmc2TemplateTerminal)
    {
        return busiSmc2TemplateTerminalMapper.selectBusiSmc2TemplateTerminalList(busiSmc2TemplateTerminal);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param busiSmc2TemplateTerminal 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertBusiSmc2TemplateTerminal(BusiSmc2TemplateTerminal busiSmc2TemplateTerminal)
    {


        return busiSmc2TemplateTerminalMapper.insertBusiSmc2TemplateTerminal(busiSmc2TemplateTerminal);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param busiSmc2TemplateTerminal 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateBusiSmc2TemplateTerminal(BusiSmc2TemplateTerminal busiSmc2TemplateTerminal)
    {
        return busiSmc2TemplateTerminalMapper.updateBusiSmc2TemplateTerminal(busiSmc2TemplateTerminal);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmc2TemplateTerminalByIds(Integer[] ids)
    {
        return busiSmc2TemplateTerminalMapper.deleteBusiSmc2TemplateTerminalByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmc2TemplateTerminalById(Integer id)
    {
        return busiSmc2TemplateTerminalMapper.deleteBusiSmc2TemplateTerminalById(id);
    }
}
