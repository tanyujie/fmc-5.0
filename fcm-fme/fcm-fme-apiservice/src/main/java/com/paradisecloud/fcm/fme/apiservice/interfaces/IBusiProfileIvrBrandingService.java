package com.paradisecloud.fcm.fme.apiservice.interfaces;

import java.util.List;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiProfileIvrBranding;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.cms.IvrBrandingProfile;

/**
 * ivrBranding模板Service接口
 * 
 * @author lilinhai
 * @date 2021-07-26
 */
public interface IBusiProfileIvrBrandingService 
{
    
    /**
     * <pre>同步所有</pre>
     * @author lilinhai
     * @since 2021-02-01 16:28 
     * @param fmeBridge
     * @param callLegProfileProcessor void
     */
    void syncAllProfile(FmeBridge fmeBridge, IvrBrandingProfileProcessor ivrBrandingProfileProcessor);
    
    /**
     * <pre>获取当前登录用户所属部门的主用的FME的DTMF列表</pre>
     * @author lilinhai
     * @since 2021-01-26 15:28 
     * @return List<ModelBean>
     */
    List<ModelBean> getAllIvrBrandingProfiles(Long deptId);
    
    /**
     * 查询ivrBranding模板
     * 
     * @param id ivrBranding模板ID
     * @return ivrBranding模板
     */
    public BusiProfileIvrBranding selectBusiProfileIvrBrandingById(Long id);

    /**
     * 查询ivrBranding模板列表
     * 
     * @param busiProfileIvrBranding ivrBranding模板
     * @return ivrBranding模板集合
     */
    public List<BusiProfileIvrBranding> selectBusiProfileIvrBrandingList(BusiProfileIvrBranding busiProfileIvrBranding);

    /**
     * 新增ivrBranding模板
     * 
     * @param busiProfileIvrBranding ivrBranding模板
     * @return 结果
     */
    public int insertBusiProfileIvrBranding(BusiProfileIvrBranding busiProfileIvrBranding);

    /**
     * 修改ivrBranding模板
     * 
     * @param busiProfileIvrBranding ivrBranding模板
     * @return 结果
     */
    public int updateBusiProfileIvrBranding(BusiProfileIvrBranding busiProfileIvrBranding);

    /**
     * 批量删除ivrBranding模板
     * 
     * @param ids 需要删除的ivrBranding模板ID
     * @return 结果
     */
    public int deleteBusiProfileIvrBrandingByIds(Long[] ids);
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();

    /**
     * 删除ivrBranding模板信息
     * 
     * @param id ivrBranding模板ID
     * @return 结果
     */
    public RestResponse deleteBusiProfileIvrBrandingById(BusiProfileIvrBranding busiProfileIvrBranding);
    
    public static interface IvrBrandingProfileProcessor
    {
        void process(IvrBrandingProfile ivrBrandingProfile);
    }
}
