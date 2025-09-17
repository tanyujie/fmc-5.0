package com.paradisecloud.fcm.fme.apiservice.interfaces;

import java.util.List;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiProfileDialInSecurity;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.cms.DialInSecurityProfile;

/**
 * 呼入安全模板Service接口
 * 
 * @author lilinhai
 * @date 2021-07-26
 */
public interface IBusiProfileDialInSecurityService 
{
    
    /**
     * <pre>同步所有</pre>
     * @author lilinhai
     * @since 2021-02-01 16:28 
     * @param fmeBridge
     * @param callLegProfileProcessor void
     */
    void syncAllProfile(FmeBridge fmeBridge, DialInSecurityProfileProcessor dialInSecurityProfileProcessor);
    
    public List<ModelBean> getAllDialInSecurityProfiles(Long deptId);
    
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
     * 批量删除呼入安全模板
     * 
     * @param ids 需要删除的呼入安全模板ID
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

    /**
     * 删除呼入安全模板信息
     * 
     * @param id 呼入安全模板ID
     * @return 结果
     */
    public RestResponse deleteBusiProfileDialInSecurityById(BusiProfileDialInSecurity busiProfileDialInSecurity);
    
    public static interface DialInSecurityProfileProcessor
    {
        void process(DialInSecurityProfile dialInSecurityProfile);
    }
}
