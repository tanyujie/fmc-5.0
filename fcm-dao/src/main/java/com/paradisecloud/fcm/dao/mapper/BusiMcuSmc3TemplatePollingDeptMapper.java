package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplatePollingDept;

import java.util.List;

/**
 * SMC3.0MCU轮询方案的部门Mapper接口
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
public interface BusiMcuSmc3TemplatePollingDeptMapper 
{
    /**
     * 查询SMC3.0MCU轮询方案的部门
     * 
     * @param id SMC3.0MCU轮询方案的部门ID
     * @return SMC3.0MCU轮询方案的部门
     */
    public BusiMcuSmc3TemplatePollingDept selectBusiMcuSmc3TemplatePollingDeptById(Long id);

    /**
     * 查询SMC3.0MCU轮询方案的部门列表
     * 
     * @param busiMcuSmc3TemplatePollingDept SMC3.0MCU轮询方案的部门
     * @return SMC3.0MCU轮询方案的部门集合
     */
    public List<BusiMcuSmc3TemplatePollingDept> selectBusiMcuSmc3TemplatePollingDeptList(BusiMcuSmc3TemplatePollingDept busiMcuSmc3TemplatePollingDept);

    /**
     * 新增SMC3.0MCU轮询方案的部门
     * 
     * @param busiMcuSmc3TemplatePollingDept SMC3.0MCU轮询方案的部门
     * @return 结果
     */
    public int insertBusiMcuSmc3TemplatePollingDept(BusiMcuSmc3TemplatePollingDept busiMcuSmc3TemplatePollingDept);

    /**
     * 修改SMC3.0MCU轮询方案的部门
     * 
     * @param busiMcuSmc3TemplatePollingDept SMC3.0MCU轮询方案的部门
     * @return 结果
     */
    public int updateBusiMcuSmc3TemplatePollingDept(BusiMcuSmc3TemplatePollingDept busiMcuSmc3TemplatePollingDept);

    /**
     * 删除SMC3.0MCU轮询方案的部门
     * 
     * @param id SMC3.0MCU轮询方案的部门ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3TemplatePollingDeptById(Long id);

    /**
     * 批量删除SMC3.0MCU轮询方案的部门
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc3TemplatePollingDeptByIds(Long[] ids);

    void deletePollingDeptByPollingSchemeId(Long id);
}
