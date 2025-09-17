package com.paradisecloud.fcm.fme.apiservice.impls;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceNumberMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTenantSettingsMapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceNumber;
import com.paradisecloud.fcm.dao.model.BusiTenantSettings;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiTenantSettingsService;
import com.paradisecloud.fcm.fme.cache.DeptTenantCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.fme.model.cms.Tenant;
import com.paradisecloud.fcm.fme.model.parambuilder.CoSpaceParamBuilder;
import com.paradisecloud.fcm.fme.model.response.tenant.ActiveTenantsResponse;
import com.paradisecloud.fcm.fme.model.response.tenant.TenantInfoResponse;
import com.paradisecloud.fcm.fme.model.response.tenant.TenantsResponse;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.exception.SystemException;

/**
 * 租户设置Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-08-04
 */
@Service
public class BusiTenantSettingsServiceImpl implements IBusiTenantSettingsService 
{
    @Autowired
    private BusiTenantSettingsMapper busiTenantSettingsMapper;
    
    @Autowired
    private BusiConferenceNumberMapper busiConferenceNumberMapper;

    /**
     * <pre>同步所有</pre>
     * @author lilinhai
     * @since 2021-02-01 16:28 
     * @param fmeBridge
     * @param callLegProfileProcessor void
     */
    public void syncAllProfile(FmeBridge fmeBridge, TenantProcessor tenantProcessor)
    {
        int offset = 0;
        AtomicInteger totalCount = new AtomicInteger();
        while (true)
        {
            TenantsResponse response = fmeBridge.getTenantInvoker().getTenants(offset);
            if (response != null)
            {
                ActiveTenantsResponse activeTenantsResponse = response.getTenants();
                if (activeTenantsResponse != null)
                {
                    List<Tenant> ts = activeTenantsResponse.getTenants();
                    if (ts != null)
                    {
                        for (Tenant callLegProfile2 : ts)
                        {
                            TenantInfoResponse callLegProfileInfoResponse = fmeBridge.getTenantInvoker().getTenant(callLegProfile2.getId());
                            if (callLegProfileInfoResponse != null && callLegProfileInfoResponse.getTenant() != null)
                            {
                                callLegProfile2 = callLegProfileInfoResponse.getTenant();
                                tenantProcessor.process(callLegProfile2);
                            }
                        }
                        
                        // 业务处理
                        Integer total = activeTenantsResponse.getTotal();
                        totalCount.addAndGet(ts.size());
                        if (totalCount.get() < total.intValue())
                        {
                            offset = totalCount.get();
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }
                else
                {
                    break;
                }
            }
            else
            {
                break;
            }
        }
    }
    
    /**
     * 获取所有租户信息
     * @author sinhy
     * @since 2021-08-05 14:27 
     * @return List<ModelBean>
     */
    public List<ModelBean> getAllTenants()
    {
        BusiTenantSettings condition = new BusiTenantSettings();
        List<BusiTenantSettings> busiProfiles = busiTenantSettingsMapper.selectBusiTenantSettingsList(condition);
        List<ModelBean> mbs = new ArrayList<>();
        if (!ObjectUtils.isEmpty(busiProfiles))
        {
            for (BusiTenantSettings profile : busiProfiles)
            {
                FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(profile.getDeptId());
                Tenant t = fmeBridge.getDataCache().getTenant(profile.getFmeTenantUuid());
                if (t == null)
                {
                    busiTenantSettingsMapper.deleteBusiTenantSettingsById(profile.getId());
                }
                else
                {
                    ModelBean mb = new ModelBean(t);
                    mb.put("deptId", profile.getDeptId());
                    mbs.add(mb);
                }
            }
        }
        return mbs;
    }
    
    /**
     * 查询租户设置
     * 
     * @param id 租户设置ID
     * @return 租户设置
     */
    @Override
    public BusiTenantSettings selectBusiTenantSettingsById(Long id)
    {
        return busiTenantSettingsMapper.selectBusiTenantSettingsById(id);
    }

    /**
     * 查询租户设置列表
     * 
     * @param busiTenantSettings 租户设置
     * @return 租户设置
     */
    @Override
    public List<BusiTenantSettings> selectBusiTenantSettingsList(BusiTenantSettings busiTenantSettings)
    {
        return busiTenantSettingsMapper.selectBusiTenantSettingsList(busiTenantSettings);
    }

    /**
     * 新增租户设置
     * 
     * @param busiTenantSettings 租户设置
     * @return 结果
     */
    @Override
    public int insertBusiTenantSettings(BusiTenantSettings busiTenantSettings)
    {
        Assert.isTrue(busiTenantSettings.getDeptId() != null, "部门ID不能为空");
        Long deptId = busiTenantSettings.getDeptId();
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(deptId);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        
        busiTenantSettings.getParams().put("name", deptId.toString() + "-" + SysDeptCache.getInstance().get(deptId).getDeptName());
        buildParams(busiTenantSettings, nameValuePairs);

        String profileId = fmeBridge.getTenantInvoker().createTenant(nameValuePairs);
        if (ObjectUtils.isEmpty(profileId))
        {
            throw new SystemException(1001098, "创建Tenant失败");
        }
        
        busiTenantSettings.setFmeTenantUuid(profileId);
        busiTenantSettings.getParams().put("id", profileId);
        busiTenantSettings.getParams().put("deptId", deptId);
        TenantInfoResponse profileInfoResponse = fmeBridge.getTenantInvoker().getTenant(profileId);
        if (profileInfoResponse != null && profileInfoResponse.getTenant() != null)
        {
            // 更新缓存
            FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(deptId, new FmeBridgeAddpterProcessor()
            {
                public void process(FmeBridge fmeBridge)
                {
                    fmeBridge.getDataCache().update(profileInfoResponse.getTenant());
                }
            });
            
            // 存入缓存
            DeptTenantCache.getInstance().put(deptId, busiTenantSettings);
            
            // 将租户ID设置到该租户所有coSpace对象上
            BusiConferenceNumber con = new BusiConferenceNumber();
            con.setDeptId(deptId);
            List<BusiConferenceNumber> cns = busiConferenceNumberMapper.selectBusiConferenceNumberList(con);
            if (!ObjectUtils.isEmpty(cns))
            {
                for (BusiConferenceNumber busiConferenceNumber : cns)
                {
                    CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(busiConferenceNumber.getId());
                    if (coSpace == null)
                    {
                        continue;   
                    }
                    CoSpaceParamBuilder coSpaceParamBuilder = new CoSpaceParamBuilder();
                    coSpaceParamBuilder.tenant(busiTenantSettings.getFmeTenantUuid());
                    RestResponse rr = fmeBridge.getCoSpaceInvoker().updateCoSpace(coSpace.getId(), coSpaceParamBuilder.build());
                    if (!rr.isSuccess())
                    {
                        throw new SystemException(1008989, rr.getMessage());
                    }
                    
                    coSpace.setTenant(profileId);
                    FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(deptId, new FmeBridgeAddpterProcessor()
                    {
                        public void process(FmeBridge fmeBridge)
                        {
                            fmeBridge.getDataCache().update(coSpace);
                        }
                    });
                }
            }
        }
        
        // 处理保存
        busiTenantSettings.setCreateTime(new Date());
        busiTenantSettingsMapper.insertBusiTenantSettings(busiTenantSettings);
        return ObjectUtils.isEmpty(profileId) ? 0 : 1;
    }
    
    private void buildParams(BusiTenantSettings busiTenantSettings, List<NameValuePair> nameValuePairs)
    {
        Assert.isTrue(busiTenantSettings.getParams().containsKey("name"), "name不能为空");
        Assert.isTrue(busiTenantSettings.getParams().containsKey("callLegProfile"), "callLegProfile不能为空");
        Assert.isTrue(busiTenantSettings.getParams().containsKey("callProfile"), "callProfile不能为空");
        Assert.isTrue(busiTenantSettings.getParams().containsKey("dtmfProfile"), "dtmfProfile不能为空");
        Assert.isTrue(busiTenantSettings.getParams().containsKey("ivrBrandingProfile"), "ivrBrandingProfile不能为空");
        Assert.isTrue(busiTenantSettings.getParams().containsKey("callBrandingProfile"), "callBrandingProfile不能为空");
        Assert.isTrue(busiTenantSettings.getParams().containsKey("dialInSecurityProfile"), "dialInSecurityProfile不能为空");
//        Assert.isTrue(busiTenantSettings.getParams().containsKey("webBridgeProfile"), "webBridgeProfile不能为空");
//        Assert.isTrue(busiTenantSettings.getParams().containsKey("userProfile"), "userProfile不能为空");
        Assert.isTrue(busiTenantSettings.getParams().containsKey("participantLimit"), "participantLimit不能为空");
        nameValuePairs.add(new BasicNameValuePair("name", busiTenantSettings.getParams().get("name").toString()));
        nameValuePairs.add(new BasicNameValuePair("callLegProfile", busiTenantSettings.getParams().get("callLegProfile").toString()));
        nameValuePairs.add(new BasicNameValuePair("callProfile", busiTenantSettings.getParams().get("callProfile").toString()));
        nameValuePairs.add(new BasicNameValuePair("dtmfProfile", busiTenantSettings.getParams().get("dtmfProfile").toString()));
        nameValuePairs.add(new BasicNameValuePair("ivrBrandingProfile", busiTenantSettings.getParams().get("ivrBrandingProfile").toString()));
        nameValuePairs.add(new BasicNameValuePair("callBrandingProfile", busiTenantSettings.getParams().get("callBrandingProfile").toString()));
        nameValuePairs.add(new BasicNameValuePair("dialInSecurityProfile", busiTenantSettings.getParams().get("dialInSecurityProfile").toString()));
//        nameValuePairs.add(new BasicNameValuePair("webBridgeProfile", busiTenantSettings.getParams().get("webBridgeProfile").toString()));
//        nameValuePairs.add(new BasicNameValuePair("userProfile", busiTenantSettings.getParams().get("userProfile").toString()));
        nameValuePairs.add(new BasicNameValuePair("participantLimit", busiTenantSettings.getParams().get("participantLimit").toString()));
    }

    /**
     * 修改租户设置
     * 
     * @param busiTenantSettings 租户设置
     * @return 结果
     */
    @Override
    public int updateBusiTenantSettings(BusiTenantSettings busiTenantSettings)
    {
        Assert.isTrue(busiTenantSettings.getDeptId() != null, "部门ID不能为空");
        Long deptId = busiTenantSettings.getDeptId();
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(deptId);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        
        busiTenantSettings.setDeptId(deptId);
        busiTenantSettings.getParams().put("name", deptId.toString() + "-" + SysDeptCache.getInstance().get(deptId).getDeptName());
        
        buildParams(busiTenantSettings, nameValuePairs);
        RestResponse restResponse = fmeBridge.getTenantInvoker().updateTenant(busiTenantSettings.getParams().get("id").toString(), nameValuePairs);
        if (restResponse.isSuccess())
        {
            // 更新内存
            TenantInfoResponse profileInfoResponse = fmeBridge.getTenantInvoker().getTenant(busiTenantSettings.getParams().get("id").toString());
            if (profileInfoResponse != null && profileInfoResponse.getTenant() != null)
            {
                // 更新入会方案缓存
                FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(busiTenantSettings.getDeptId(), new FmeBridgeAddpterProcessor()
                {
                    public void process(FmeBridge fmeBridge)
                    {
                        fmeBridge.getDataCache().update(profileInfoResponse.getTenant());
                    }
                });
            }
            
            busiTenantSettings.getParams().put("deptId", deptId);
            
            // 处理保存和默认入会方案设置
            BusiTenantSettings condition = new BusiTenantSettings();
            condition.setDeptId(deptId);
            condition.setFmeTenantUuid(busiTenantSettings.getParams().get("id").toString());
            List<BusiTenantSettings> busiProfiles = busiTenantSettingsMapper.selectBusiTenantSettingsList(condition);
            if (!ObjectUtils.isEmpty(busiProfiles))
            {
                for (BusiTenantSettings dtmf : busiProfiles)
                {
                    dtmf.setUpdateTime(new Date());
                    busiTenantSettingsMapper.updateBusiTenantSettings(dtmf);
                }
            }
            
            return 1;
        }
        throw new SystemException(1009999, restResponse.getMessage());
    }

    /**
     * 批量删除租户设置
     * 
     * @param ids 需要删除的租户设置ID
     * @return 结果
     */
    @Override
    public int deleteBusiTenantSettingsByIds(Long[] ids)
    {
        return busiTenantSettingsMapper.deleteBusiTenantSettingsByIds(ids);
    }

    /**
     * 删除租户设置信息
     * 
     * @param id 租户设置ID
     * @return 结果
     */
    public RestResponse deleteBusiTenantSettingsById(BusiTenantSettings busiTenantSettings)
    {
        Object idObj = busiTenantSettings.getParams().get("id");
        if (idObj == null)
        {
            throw new SystemException(1002321, "ID为空，删除出错！");
        }
        
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(busiTenantSettings.getDeptId());
        
        // 将租户ID设置到该租户所有coSpace对象上
        BusiConferenceNumber con = new BusiConferenceNumber();
        con.setDeptId(busiTenantSettings.getDeptId());
        List<BusiConferenceNumber> cns = busiConferenceNumberMapper.selectBusiConferenceNumberList(con);
        if (!ObjectUtils.isEmpty(cns))
        {
            for (BusiConferenceNumber busiConferenceNumber : cns)
            {
                CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(busiConferenceNumber.getId());
                CoSpaceParamBuilder coSpaceParamBuilder = new CoSpaceParamBuilder();
                coSpaceParamBuilder.tenant("");
                RestResponse rr = fmeBridge.getCoSpaceInvoker().updateCoSpace(coSpace.getId(), coSpaceParamBuilder.build());
                if (!rr.isSuccess())
                {
                    throw new SystemException(1008989, rr.getMessage());
                }
                
                coSpace.setTenant(null);
                FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(busiTenantSettings.getDeptId(), new FmeBridgeAddpterProcessor()
                {
                    public void process(FmeBridge fmeBridge)
                    {
                        fmeBridge.getDataCache().update(coSpace);
                    }
                });
            }
        }
        
        // 清除缓存
        DeptTenantCache.getInstance().remove(busiTenantSettings.getDeptId());
        
        RestResponse result = fmeBridge.getTenantInvoker().deleteTenant(idObj.toString());
        if (result.isSuccess())
        {
            // 删除内存中的入会方案
            fmeBridge.getDataCache().deleteTenant(idObj.toString());
            BusiTenantSettings profileDeleteCon = new BusiTenantSettings();
            profileDeleteCon.setFmeTenantUuid(idObj.toString());
            List<BusiTenantSettings> ps = busiTenantSettingsMapper.selectBusiTenantSettingsList(profileDeleteCon);
            if (!ObjectUtils.isEmpty(ps))
            {
                for (BusiTenantSettings d : ps)
                {
                    busiTenantSettingsMapper.deleteBusiTenantSettingsById(d.getId());
                }
            }
        }
        return result;
    }
}
