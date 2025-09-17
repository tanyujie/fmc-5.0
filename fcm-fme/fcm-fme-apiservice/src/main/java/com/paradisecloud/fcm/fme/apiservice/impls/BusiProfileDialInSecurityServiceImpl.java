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
import com.paradisecloud.fcm.dao.mapper.BusiProfileDialInSecurityMapper;
import com.paradisecloud.fcm.dao.model.BusiProfileDialInSecurity;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileDialInSecurityService;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils;
import com.paradisecloud.fcm.fme.model.cms.DialInSecurityProfile;
import com.paradisecloud.fcm.fme.model.response.dialinsecurity.ActiveDialInSecurityProfilesResponse;
import com.paradisecloud.fcm.fme.model.response.dialinsecurity.DialInSecurityProfileInfoResponse;
import com.paradisecloud.fcm.fme.model.response.dialinsecurity.DialInSecurityProfilesResponse;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;

/**
 * 呼入安全模板Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-07-26
 */
@Service
public class BusiProfileDialInSecurityServiceImpl implements IBusiProfileDialInSecurityService 
{
    @Autowired
    private BusiProfileDialInSecurityMapper busiProfileDialInSecurityMapper;

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-10-29 12:13 
     * @return
     * @see com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileDialInSecurityService#getDeptRecordCounts()
     */
    @Override
    public List<DeptRecordCount> getDeptRecordCounts()
    {
        return busiProfileDialInSecurityMapper.getDeptRecordCounts();
    }

    /**
     * <pre>同步所有</pre>
     * @author lilinhai
     * @since 2021-02-01 16:28 
     * @param fmeBridge
     * @param callLegProfileProcessor void
     */
    public void syncAllProfile(FmeBridge fmeBridge, DialInSecurityProfileProcessor dialInSecurityProfileProcessor)
    {
        int offset = 0;
        AtomicInteger totalCount = new AtomicInteger();
        while (true)
        {
            DialInSecurityProfilesResponse response = fmeBridge.getDialInSecurityProfileInvoker().getDialInSecurityProfiles(offset);
            if (response != null)
            {
                ActiveDialInSecurityProfilesResponse activeDialInSecurityProfilesResponse = response.getDialInSecurityProfiles();
                if (activeDialInSecurityProfilesResponse != null)
                {
                    List<DialInSecurityProfile> callLegProfile = activeDialInSecurityProfilesResponse.getDialInSecurityProfile();
                    if (callLegProfile != null)
                    {
                        for (DialInSecurityProfile callLegProfile2 : callLegProfile)
                        {
                            DialInSecurityProfileInfoResponse callLegProfileInfoResponse = fmeBridge.getDialInSecurityProfileInvoker().getDialInSecurityProfile(callLegProfile2.getId());
                            if (callLegProfileInfoResponse != null && callLegProfileInfoResponse.getDialInSecurityProfile() != null)
                            {
                                callLegProfile2 = callLegProfileInfoResponse.getDialInSecurityProfile();
                                dialInSecurityProfileProcessor.process(callLegProfile2);
                            }
                        }
                        
                        // 业务处理
                        Integer total = activeDialInSecurityProfilesResponse.getTotal();
                        totalCount.addAndGet(callLegProfile.size());
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
    
    public List<ModelBean> getAllDialInSecurityProfiles(Long deptId)
    {
        deptId = deptId == null ? SecurityUtils.getLoginUser().getUser().getDeptId() : deptId;
        FmeBridge mainMaxPriorityFmeHttpInvoker = BridgeUtils.getAvailableFmeBridge(deptId);
        BusiProfileDialInSecurity condition = new BusiProfileDialInSecurity();
        condition.setDeptId(deptId);
        List<BusiProfileDialInSecurity> busiProfiles = busiProfileDialInSecurityMapper.selectBusiProfileDialInSecurityList(condition);
        final long tmpDeptId = deptId;
        List<ModelBean> mbs = new ArrayList<>();
        if (!ObjectUtils.isEmpty(busiProfiles))
        {
            for (BusiProfileDialInSecurity profile : busiProfiles)
            {
                DialInSecurityProfile fp = mainMaxPriorityFmeHttpInvoker.getDataCache().getDialInSecurityProfile(profile.getDialInSecurityProfileUuid());
                if (fp == null)
                {
                    busiProfileDialInSecurityMapper.deleteBusiProfileDialInSecurityById(profile.getId());
                }
                else
                {
                    ModelBean mb = new ModelBean(fp);
                    mb.put("deptId", tmpDeptId);
                    mbs.add(mb);
                }
            }
        }
        return mbs;
    }
    
    /**
     * 查询呼入安全模板
     * 
     * @param id 呼入安全模板ID
     * @return 呼入安全模板
     */
    @Override
    public BusiProfileDialInSecurity selectBusiProfileDialInSecurityById(Long id)
    {
        return busiProfileDialInSecurityMapper.selectBusiProfileDialInSecurityById(id);
    }

    /**
     * 查询呼入安全模板列表
     * 
     * @param busiProfileDialInSecurity 呼入安全模板
     * @return 呼入安全模板
     */
    @Override
    public List<BusiProfileDialInSecurity> selectBusiProfileDialInSecurityList(BusiProfileDialInSecurity busiProfileDialInSecurity)
    {
        return busiProfileDialInSecurityMapper.selectBusiProfileDialInSecurityList(busiProfileDialInSecurity);
    }

    /**
     * 新增呼入安全模板
     * 
     * @param busiProfileDialInSecurity 呼入安全模板
     * @return 结果
     */
    @Override
    public int insertBusiProfileDialInSecurity(BusiProfileDialInSecurity busiProfileDialInSecurity)
    {
        Long deptId = busiProfileDialInSecurity.getDeptId() == null ? SecurityUtils.getLoginUser().getUser().getDeptId() : busiProfileDialInSecurity.getDeptId();
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(deptId);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        buildParams(busiProfileDialInSecurity, nameValuePairs);
        
        String profileId = fmeBridge.getDialInSecurityProfileInvoker().createDialInSecurityProfile(nameValuePairs);
        if (ObjectUtils.isEmpty(profileId))
        {
            throw new SystemException(1001098, "创建DialInSecurity profile失败");
        }
        
        DialInSecurityProfileInfoResponse profileInfoResponse = fmeBridge.getDialInSecurityProfileInvoker().getDialInSecurityProfile(profileId);
        if (profileInfoResponse != null && profileInfoResponse.getDialInSecurityProfile() != null)
        {
            // 更新缓存
            FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(deptId, new FmeBridgeAddpterProcessor()
            {
                public void process(FmeBridge fmeBridge)
                {
                    fmeBridge.getDataCache().update(profileInfoResponse.getDialInSecurityProfile());
                }
            });
        }
        
        busiProfileDialInSecurity.getParams().put("id", profileId);
        busiProfileDialInSecurity.getParams().put("deptId", deptId);
        
        // 处理保存
        busiProfileDialInSecurity.setCreateTime(new Date());
        busiProfileDialInSecurity.setDeptId(deptId);
        busiProfileDialInSecurity.setDialInSecurityProfileUuid(profileId);
        busiProfileDialInSecurityMapper.insertBusiProfileDialInSecurity(busiProfileDialInSecurity);
        
        return ObjectUtils.isEmpty(profileId) ? 0 : 1;
    }
    
    private void buildParams(BusiProfileDialInSecurity busiProfileDialInSecurity, List<NameValuePair> nameValuePairs)
    {
        Assert.isTrue(busiProfileDialInSecurity.getParams().containsKey("name"), "name不能为空");
        Assert.isTrue(busiProfileDialInSecurity.getParams().containsKey("minPasscodeLength"), "minPasscodeLength不能为空");
        Assert.isTrue(busiProfileDialInSecurity.getParams().containsKey("allowOutOfPolicy"), "allowOutOfPolicy不能为空");
        nameValuePairs.add(new BasicNameValuePair("name", busiProfileDialInSecurity.getParams().get("name").toString()));
        nameValuePairs.add(new BasicNameValuePair("minPasscodeLength", busiProfileDialInSecurity.getParams().get("minPasscodeLength").toString()));
        nameValuePairs.add(new BasicNameValuePair("allowOutOfPolicy", busiProfileDialInSecurity.getParams().get("allowOutOfPolicy").toString()));
    }

    /**
     * 修改呼入安全模板
     * 
     * @param busiProfileDialInSecurity 呼入安全模板
     * @return 结果
     */
    @Override
    public int updateBusiProfileDialInSecurity(BusiProfileDialInSecurity busiProfileDialInSecurity)
    {
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(busiProfileDialInSecurity.getDeptId());
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        buildParams(busiProfileDialInSecurity, nameValuePairs);
        RestResponse restResponse = fmeBridge.getDialInSecurityProfileInvoker().updateDialInSecurityProfile(busiProfileDialInSecurity.getParams().get("id").toString(), nameValuePairs);
        if (restResponse.isSuccess())
        {
            // 更新内存
            DialInSecurityProfileInfoResponse profileInfoResponse = fmeBridge.getDialInSecurityProfileInvoker().getDialInSecurityProfile(busiProfileDialInSecurity.getParams().get("id").toString());
            if (profileInfoResponse != null && profileInfoResponse.getDialInSecurityProfile() != null)
            {
                // 更新入会方案缓存
                FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(busiProfileDialInSecurity.getDeptId(), new FmeBridgeAddpterProcessor()
                {
                    public void process(FmeBridge fmeBridge)
                    {
                        fmeBridge.getDataCache().update(profileInfoResponse.getDialInSecurityProfile());
                    }
                });
            }
            
            long deptId = busiProfileDialInSecurity.getDeptId() == null ? SecurityUtils.getLoginUser().getUser().getDeptId() : busiProfileDialInSecurity.getDeptId();
            busiProfileDialInSecurity.getParams().put("deptId", deptId);
            
            // 处理保存和默认入会方案设置
            BusiProfileDialInSecurity condition = new BusiProfileDialInSecurity();
            condition.setDeptId(deptId);
            condition.setDialInSecurityProfileUuid(busiProfileDialInSecurity.getParams().get("id").toString());
            List<BusiProfileDialInSecurity> busiProfiles = busiProfileDialInSecurityMapper.selectBusiProfileDialInSecurityList(condition);
            if (!ObjectUtils.isEmpty(busiProfiles))
            {
                for (BusiProfileDialInSecurity dtmf : busiProfiles)
                {
                    dtmf.setUpdateTime(new Date());
                    busiProfileDialInSecurityMapper.updateBusiProfileDialInSecurity(dtmf);
                }
            }
            
            return 1;
        }
        throw new SystemException(1009999, restResponse.getMessage());
    }

    /**
     * 批量删除呼入安全模板
     * 
     * @param ids 需要删除的呼入安全模板ID
     * @return 结果
     */
    @Override
    public int deleteBusiProfileDialInSecurityByIds(Long[] ids)
    {
        return busiProfileDialInSecurityMapper.deleteBusiProfileDialInSecurityByIds(ids);
    }

    /**
     * 删除呼入安全模板信息
     * 
     * @param id 呼入安全模板ID
     * @return 结果
     */
    @Override
    public RestResponse deleteBusiProfileDialInSecurityById(BusiProfileDialInSecurity busiProfileDialInSecurity)
    {
        Object idObj = busiProfileDialInSecurity.getParams().get("id");
        if (idObj == null)
        {
            throw new SystemException(1002321, "Profile ID为空，删除出错！");
        }
        
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(busiProfileDialInSecurity.getDeptId());
        RestResponse result = fmeBridge.getDialInSecurityProfileInvoker().deleteDialInSecurityProfile(idObj.toString());
        if (result.isSuccess())
        {
            // 删除内存中的入会方案
            fmeBridge.getDataCache().deleteDialInSecurityProfile(idObj.toString());
            BusiProfileDialInSecurity profileDeleteCon = new BusiProfileDialInSecurity();
            profileDeleteCon.setDialInSecurityProfileUuid(idObj.toString());
            List<BusiProfileDialInSecurity> ps = busiProfileDialInSecurityMapper.selectBusiProfileDialInSecurityList(profileDeleteCon);
            if (!ObjectUtils.isEmpty(ps))
            {
                for (BusiProfileDialInSecurity d : ps)
                {
                    busiProfileDialInSecurityMapper.deleteBusiProfileDialInSecurityById(d.getId());
                }
            }
        }
        return result;
    }
}
