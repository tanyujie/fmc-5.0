package com.paradisecloud.fcm.dao.mapper;


import com.paradisecloud.fcm.dao.model.BusiMcuSmc2TemplatePollingDept;

import java.util.List;

/**
 * SMC2.0MCU轮询方案的部门Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuSmc2TemplatePollingDeptMapper
{
    /**
     * 查询SMC2.0MCU轮询方案的部门
     * 
     * @param id SMC2.0MCU轮询方案的部门ID
     * @return SMC2.0MCU轮询方案的部门
     */
    public BusiMcuSmc2TemplatePollingDept selectBusiMcuSmc2TemplatePollingDeptById(Long id);

    /**
     * 查询SMC2.0MCU轮询方案的部门列表
     * 
     * @param busiMcuSmc2TemplatePollingDept SMC2.0MCU轮询方案的部门
     * @return SMC2.0MCU轮询方案的部门集合
     */
    public List<BusiMcuSmc2TemplatePollingDept> selectBusiMcuSmc2TemplatePollingDeptList(BusiMcuSmc2TemplatePollingDept busiMcuSmc2TemplatePollingDept);

    /**
     * 新增SMC2.0MCU轮询方案的部门
     * 
     * @param busiMcuSmc2TemplatePollingDept SMC2.0MCU轮询方案的部门
     * @return 结果
     */
    public int insertBusiMcuSmc2TemplatePollingDept(BusiMcuSmc2TemplatePollingDept busiMcuSmc2TemplatePollingDept);

    /**
     * 修改SMC2.0MCU轮询方案的部门
     * 
     * @param busiMcuSmc2TemplatePollingDept SMC2.0MCU轮询方案的部门
     * @return 结果
     */
    public int updateBusiMcuSmc2TemplatePollingDept(BusiMcuSmc2TemplatePollingDept busiMcuSmc2TemplatePollingDept);

    /**
     * 删除SMC2.0MCU轮询方案的部门
     * 
     * @param id SMC2.0MCU轮询方案的部门ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2TemplatePollingDeptById(Long id);

    /**
     * 批量删除SMC2.0MCU轮询方案的部门
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuSmc2TemplatePollingDeptByIds(Long[] ids);

    void deletePollingDeptByPollingSchemeId(Long id);
}
