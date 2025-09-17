package com.paradisecloud.fcm.dao.mapper;




import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudTemplatePollingDept;

import java.util.List;

/**
 * Hwcloud.0MCU轮询方案的部门Mapper接口
 * 
 * @author lilinhai
 * @date 2022-09-19
 */
public interface BusiMcuHwcloudTemplatePollingDeptMapper
{
    /**
     * 查询Hwcloud.0MCU轮询方案的部门
     * 
     * @param id Hwcloud.0MCU轮询方案的部门ID
     * @return Hwcloud.0MCU轮询方案的部门
     */
    public BusiMcuHwcloudTemplatePollingDept selectBusiMcuHwcloudTemplatePollingDeptById(Long id);

    /**
     * 查询Hwcloud.0MCU轮询方案的部门列表
     * 
     * @param busiMcuHwcloudTemplatePollingDept Hwcloud.0MCU轮询方案的部门
     * @return Hwcloud.0MCU轮询方案的部门集合
     */
    public List<BusiMcuHwcloudTemplatePollingDept> selectBusiMcuHwcloudTemplatePollingDeptList(BusiMcuHwcloudTemplatePollingDept busiMcuHwcloudTemplatePollingDept);

    /**
     * 新增Hwcloud.0MCU轮询方案的部门
     * 
     * @param busiMcuHwcloudTemplatePollingDept Hwcloud.0MCU轮询方案的部门
     * @return 结果
     */
    public int insertBusiMcuHwcloudTemplatePollingDept(BusiMcuHwcloudTemplatePollingDept busiMcuHwcloudTemplatePollingDept);

    /**
     * 修改Hwcloud.0MCU轮询方案的部门
     * 
     * @param busiMcuHwcloudTemplatePollingDept Hwcloud.0MCU轮询方案的部门
     * @return 结果
     */
    public int updateBusiMcuHwcloudTemplatePollingDept(BusiMcuHwcloudTemplatePollingDept busiMcuHwcloudTemplatePollingDept);

    /**
     * 删除Hwcloud.0MCU轮询方案的部门
     * 
     * @param id Hwcloud.0MCU轮询方案的部门ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudTemplatePollingDeptById(Long id);

    /**
     * 批量删除Hwcloud.0MCU轮询方案的部门
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuHwcloudTemplatePollingDeptByIds(Long[] ids);

    void deletePollingDeptByPollingSchemeId(Long id);
}
