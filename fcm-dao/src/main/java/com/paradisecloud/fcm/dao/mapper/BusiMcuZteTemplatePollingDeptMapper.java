package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplatePollingDept;
import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplatePollingDept;

import java.util.List;

/**
 * 中兴MCU轮询方案的部门Mapper接口
 * 
 * @author lilinhai
 * @date 2024-04-09
 */
public interface BusiMcuZteTemplatePollingDeptMapper 
{
    /**
     * 查询轮询方案的部门
     *
     * @param id 轮询方案的部门ID
     * @return 轮询方案的部门
     */
    public BusiMcuZteTemplatePollingDept selectBusiMcuZteTemplatePollingDeptById(Long id);

    /**
     * 查询轮询方案的部门列表
     *
     * @param busiTemplatePollingDept 轮询方案的部门
     * @return 轮询方案的部门集合
     */
    public List<BusiMcuZteTemplatePollingDept> selectBusiMcuZteTemplatePollingDeptList(BusiMcuZteTemplatePollingDept busiTemplatePollingDept);

    /**
     * 新增轮询方案的部门
     *
     * @param busiTemplatePollingDept 轮询方案的部门
     * @return 结果
     */
    public int insertBusiMcuZteTemplatePollingDept(BusiMcuZteTemplatePollingDept busiTemplatePollingDept);

    /**
     * 修改轮询方案的部门
     *
     * @param busiTemplatePollingDept 轮询方案的部门
     * @return 结果
     */
    public int updateBusiMcuZteTemplatePollingDept(BusiMcuZteTemplatePollingDept busiTemplatePollingDept);

    /**
     * 删除轮询方案的部门
     *
     * @param id 轮询方案的部门ID
     * @return 结果
     */
    public int deleteBusiMcuZteTemplatePollingDeptById(Long id);

    /**
     * <pre>根据轮询方案ID删除部门记录</pre>
     * @author lilinhai
     * @since 2021-02-25 13:54 
     * @param pollingSchemeId
     * @return int
     */
    public int deletePollingDeptByPollingSchemeId(Long pollingSchemeId);

    /**
     * 批量删除轮询方案的部门
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuZteTemplatePollingDeptByIds(Long[] ids);
}
