package com.paradisecloud.fcm.fme.apiservice.interfaces;

import java.util.List;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiProfileCompatibility;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.cms.CompatibilityProfile;

/**
 * 兼容性参数模板Service接口
 * 
 * @author lilinhai
 * @date 2021-07-27
 */
public interface IBusiProfileCompatibilityService 
{
    
    /**
     * <pre>同步所有</pre>
     * @author lilinhai
     * @since 2021-02-01 16:28 
     * @param fmeBridge
     * @param callLegProfileProcessor void
     */
    void syncAllProfile(FmeBridge fmeBridge, CompatibilityProfileProcessor compatibilityProfileProcessor);
    
    List<ModelBean> getAllCompatibilityProfiles(Long deptId);
    
    /**
     * 查询兼容性参数模板
     * 
     * @param id 兼容性参数模板ID
     * @return 兼容性参数模板
     */
    public BusiProfileCompatibility selectBusiProfileCompatibilityById(Long id);

    /**
     * 查询兼容性参数模板列表
     * 
     * @param busiProfileCompatibility 兼容性参数模板
     * @return 兼容性参数模板集合
     */
    public List<BusiProfileCompatibility> selectBusiProfileCompatibilityList(BusiProfileCompatibility busiProfileCompatibility);

    /**
     * 新增兼容性参数模板
     * 
     * @param busiProfileCompatibility 兼容性参数模板
     * @return 结果
     */
    public int insertBusiProfileCompatibility(BusiProfileCompatibility busiProfileCompatibility);

    /**
     * 修改兼容性参数模板
     * 
     * @param busiProfileCompatibility 兼容性参数模板
     * @return 结果
     */
    public int updateBusiProfileCompatibility(BusiProfileCompatibility busiProfileCompatibility);

    /**
     * 批量删除兼容性参数模板
     * 
     * @param ids 需要删除的兼容性参数模板ID
     * @return 结果
     */
    public int deleteBusiProfileCompatibilityByIds(Long[] ids);
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();

    /**
     * 删除兼容性参数模板信息
     * 
     * @param id 兼容性参数模板ID
     * @return 结果
     */
    public RestResponse deleteBusiProfileCompatibilityById(BusiProfileCompatibility busiProfileCompatibility);
    
    public static interface CompatibilityProfileProcessor
    {
        void process(CompatibilityProfile compatibilityProfile);
    }
}
