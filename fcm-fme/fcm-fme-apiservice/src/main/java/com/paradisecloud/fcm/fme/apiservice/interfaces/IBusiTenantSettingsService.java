package com.paradisecloud.fcm.fme.apiservice.interfaces;

import java.util.List;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiTenantSettings;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.cms.Tenant;

/**
 * 租户设置Service接口
 * 
 * @author lilinhai
 * @date 2021-08-04
 */
public interface IBusiTenantSettingsService 
{
    
    /**
     * <pre>同步所有</pre>
     * @author lilinhai
     * @since 2021-02-01 16:28 
     * @param fmeBridge
     * @param callLegProfileProcessor void
     */
    public void syncAllProfile(FmeBridge fmeBridge, TenantProcessor tenantProcessor);
    
    /**
     * 获取所有租户信息
     * @author sinhy
     * @since 2021-08-05 14:27 
     * @return List<ModelBean>
     */
    public List<ModelBean> getAllTenants();
    
    /**
     * 查询租户设置
     * 
     * @param id 租户设置ID
     * @return 租户设置
     */
    public BusiTenantSettings selectBusiTenantSettingsById(Long id);

    /**
     * 查询租户设置列表
     * 
     * @param busiTenantSettings 租户设置
     * @return 租户设置集合
     */
    public List<BusiTenantSettings> selectBusiTenantSettingsList(BusiTenantSettings busiTenantSettings);

    /**
     * 新增租户设置
     * 
     * @param busiTenantSettings 租户设置
     * @return 结果
     */
    public int insertBusiTenantSettings(BusiTenantSettings busiTenantSettings);

    /**
     * 修改租户设置
     * 
     * @param busiTenantSettings 租户设置
     * @return 结果
     */
    public int updateBusiTenantSettings(BusiTenantSettings busiTenantSettings);

    /**
     * 批量删除租户设置
     * 
     * @param ids 需要删除的租户设置ID
     * @return 结果
     */
    public int deleteBusiTenantSettingsByIds(Long[] ids);

    /**
     * 删除租户设置信息
     * 
     * @param id 租户设置ID
     * @return 结果
     */
    public RestResponse deleteBusiTenantSettingsById(BusiTenantSettings busiTenantSettings);
    
    public static interface TenantProcessor
    {
        void process(Tenant tenant);
    }
}
