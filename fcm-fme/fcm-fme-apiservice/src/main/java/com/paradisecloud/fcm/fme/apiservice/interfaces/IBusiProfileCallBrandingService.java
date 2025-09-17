package com.paradisecloud.fcm.fme.apiservice.interfaces;

import java.io.IOException;
import java.util.List;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiProfileCallBranding;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.cms.CallBrandingProfile;

/**
 * callBranding模板Service接口
 * 
 * @author lilinhai
 * @date 2021-07-26
 */
public interface IBusiProfileCallBrandingService 
{
    
    /**
     * <pre>同步所有</pre>
     * @author lilinhai
     * @since 2021-02-01 16:28 
     * @param fmeBridge
     * @param callLegProfileProcessor void
     */
    void syncAllProfile(FmeBridge fmeBridge, CallBrandingProfileProcessor callBrandingProfileProcessor);
    
    public List<ModelBean> getAllCallBrandingProfiles(Long deptId);
    
    /**
     * 查询callBranding模板
     * 
     * @param id callBranding模板ID
     * @return callBranding模板
     */
    public BusiProfileCallBranding selectBusiProfileCallBrandingById(Long id);

    /**
     * 查询callBranding模板列表
     * 
     * @param busiProfileCallBranding callBranding模板
     * @return callBranding模板集合
     */
    public List<BusiProfileCallBranding> selectBusiProfileCallBrandingList(BusiProfileCallBranding busiProfileCallBranding);

    /**
     * 新增callBranding模板
     * 
     * @param busiProfileCallBranding callBranding模板
     * @return 结果
     */
    public int insertBusiProfileCallBranding(BusiProfileCallBranding busiProfileCallBranding) throws IOException;

    /**
     * 修改callBranding模板
     * 
     * @param busiProfileCallBranding callBranding模板
     * @return 结果
     */
    public int updateBusiProfileCallBranding(BusiProfileCallBranding busiProfileCallBranding) throws IOException;

    /**
     * 批量删除callBranding模板
     * 
     * @param ids 需要删除的callBranding模板ID
     * @return 结果
     */
    public int deleteBusiProfileCallBrandingByIds(Long[] ids);
    
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();

    /**
     * 删除callBranding模板信息
     * 
     * @param id callBranding模板ID
     * @return 结果
     */
    public RestResponse deleteBusiProfileCallBrandingById(BusiProfileCallBranding busiProfileCallBranding);
    
    public static interface CallBrandingProfileProcessor
    {
        void process(CallBrandingProfile callBrandingProfile);
    }
}
