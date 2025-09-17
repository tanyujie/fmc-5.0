package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiTemplatePollingDept;

/**
 * 轮询方案的部门Mapper接口
 * 
 * @author lilinhai
 * @date 2021-02-25
 */
public interface BusiTemplatePollingDeptMapper 
{
    /**
     * 查询轮询方案的部门
     * 
     * @param id 轮询方案的部门ID
     * @return 轮询方案的部门
     */
    public BusiTemplatePollingDept selectBusiTemplatePollingDeptById(Long id);

    /**
     * 查询轮询方案的部门列表
     * 
     * @param busiTemplatePollingDept 轮询方案的部门
     * @return 轮询方案的部门集合
     */
    public List<BusiTemplatePollingDept> selectBusiTemplatePollingDeptList(BusiTemplatePollingDept busiTemplatePollingDept);

    /**
     * 新增轮询方案的部门
     * 
     * @param busiTemplatePollingDept 轮询方案的部门
     * @return 结果
     */
    public int insertBusiTemplatePollingDept(BusiTemplatePollingDept busiTemplatePollingDept);

    /**
     * 修改轮询方案的部门
     * 
     * @param busiTemplatePollingDept 轮询方案的部门
     * @return 结果
     */
    public int updateBusiTemplatePollingDept(BusiTemplatePollingDept busiTemplatePollingDept);

    /**
     * 删除轮询方案的部门
     * 
     * @param id 轮询方案的部门ID
     * @return 结果
     */
    public int deleteBusiTemplatePollingDeptById(Long id);
    
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
    public int deleteBusiTemplatePollingDeptByIds(Long[] ids);
}
