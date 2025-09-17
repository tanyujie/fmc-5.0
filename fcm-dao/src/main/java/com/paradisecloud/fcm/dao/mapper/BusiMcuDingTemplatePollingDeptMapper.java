package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuDingTemplatePollingDept;

import java.util.List;

/**
 * Ding.0MCU轮询方案的部门Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuDingTemplatePollingDeptMapper
{
    /**
     * 查询Ding.0MCU轮询方案的部门
     * 
     * @param id Ding.0MCU轮询方案的部门ID
     * @return Ding.0MCU轮询方案的部门
     */
    public BusiMcuDingTemplatePollingDept selectBusiMcuDingTemplatePollingDeptById(Long id);

    /**
     * 查询Ding.0MCU轮询方案的部门列表
     * 
     * @param busiMcuDingTemplatePollingDept Ding.0MCU轮询方案的部门
     * @return Ding.0MCU轮询方案的部门集合
     */
    public List<BusiMcuDingTemplatePollingDept> selectBusiMcuDingTemplatePollingDeptList(BusiMcuDingTemplatePollingDept busiMcuDingTemplatePollingDept);

    /**
     * 新增Ding.0MCU轮询方案的部门
     * 
     * @param busiMcuDingTemplatePollingDept Ding.0MCU轮询方案的部门
     * @return 结果
     */
    public int insertBusiMcuDingTemplatePollingDept(BusiMcuDingTemplatePollingDept busiMcuDingTemplatePollingDept);

    /**
     * 修改Ding.0MCU轮询方案的部门
     * 
     * @param busiMcuDingTemplatePollingDept Ding.0MCU轮询方案的部门
     * @return 结果
     */
    public int updateBusiMcuDingTemplatePollingDept(BusiMcuDingTemplatePollingDept busiMcuDingTemplatePollingDept);

    /**
     * 删除Ding.0MCU轮询方案的部门
     * 
     * @param id Ding.0MCU轮询方案的部门ID
     * @return 结果
     */
    public int deleteBusiMcuDingTemplatePollingDeptById(Long id);

    /**
     * 批量删除Ding.0MCU轮询方案的部门
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuDingTemplatePollingDeptByIds(Long[] ids);

    void deletePollingDeptByPollingSchemeId(Long id);
}
