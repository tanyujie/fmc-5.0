package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiProfileDialInSecurity;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;

/**
 * 呼入安全模板Mapper接口
 * 
 * @author lilinhai
 * @date 2021-07-26
 */
public interface BusiProfileDialInSecurityMapper 
{
    /**
     * 查询呼入安全模板
     * 
     * @param id 呼入安全模板ID
     * @return 呼入安全模板
     */
    public BusiProfileDialInSecurity selectBusiProfileDialInSecurityById(Long id);

    /**
     * 查询呼入安全模板列表
     * 
     * @param busiProfileDialInSecurity 呼入安全模板
     * @return 呼入安全模板集合
     */
    public List<BusiProfileDialInSecurity> selectBusiProfileDialInSecurityList(BusiProfileDialInSecurity busiProfileDialInSecurity);

    /**
     * 新增呼入安全模板
     * 
     * @param busiProfileDialInSecurity 呼入安全模板
     * @return 结果
     */
    public int insertBusiProfileDialInSecurity(BusiProfileDialInSecurity busiProfileDialInSecurity);

    /**
     * 修改呼入安全模板
     * 
     * @param busiProfileDialInSecurity 呼入安全模板
     * @return 结果
     */
    public int updateBusiProfileDialInSecurity(BusiProfileDialInSecurity busiProfileDialInSecurity);

    /**
     * 删除呼入安全模板
     * 
     * @param id 呼入安全模板ID
     * @return 结果
     */
    public int deleteBusiProfileDialInSecurityById(Long id);

    /**
     * 批量删除呼入安全模板
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiProfileDialInSecurityByIds(Long[] ids);
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();
}
