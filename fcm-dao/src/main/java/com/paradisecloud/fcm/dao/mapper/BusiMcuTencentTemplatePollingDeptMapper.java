package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuTencentTemplatePollingDept;

import java.util.List;

/**
 * Tencent.0MCU轮询方案的部门Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuTencentTemplatePollingDeptMapper
{
    /**
     * 查询Tencent.0MCU轮询方案的部门
     * 
     * @param id Tencent.0MCU轮询方案的部门ID
     * @return Tencent.0MCU轮询方案的部门
     */
    public BusiMcuTencentTemplatePollingDept selectBusiMcuTencentTemplatePollingDeptById(Long id);

    /**
     * 查询Tencent.0MCU轮询方案的部门列表
     * 
     * @param busiMcuTencentTemplatePollingDept Tencent.0MCU轮询方案的部门
     * @return Tencent.0MCU轮询方案的部门集合
     */
    public List<BusiMcuTencentTemplatePollingDept> selectBusiMcuTencentTemplatePollingDeptList(BusiMcuTencentTemplatePollingDept busiMcuTencentTemplatePollingDept);

    /**
     * 新增Tencent.0MCU轮询方案的部门
     * 
     * @param busiMcuTencentTemplatePollingDept Tencent.0MCU轮询方案的部门
     * @return 结果
     */
    public int insertBusiMcuTencentTemplatePollingDept(BusiMcuTencentTemplatePollingDept busiMcuTencentTemplatePollingDept);

    /**
     * 修改Tencent.0MCU轮询方案的部门
     * 
     * @param busiMcuTencentTemplatePollingDept Tencent.0MCU轮询方案的部门
     * @return 结果
     */
    public int updateBusiMcuTencentTemplatePollingDept(BusiMcuTencentTemplatePollingDept busiMcuTencentTemplatePollingDept);

    /**
     * 删除Tencent.0MCU轮询方案的部门
     * 
     * @param id Tencent.0MCU轮询方案的部门ID
     * @return 结果
     */
    public int deleteBusiMcuTencentTemplatePollingDeptById(Long id);

    /**
     * 批量删除Tencent.0MCU轮询方案的部门
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuTencentTemplatePollingDeptByIds(Long[] ids);

    void deletePollingDeptByPollingSchemeId(Long id);
}
