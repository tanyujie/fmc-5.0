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
import com.paradisecloud.fcm.dao.mapper.BusiProfileIvrBrandingMapper;
import com.paradisecloud.fcm.dao.model.BusiProfileIvrBranding;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileIvrBrandingService;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils;
import com.paradisecloud.fcm.fme.model.cms.IvrBrandingProfile;
import com.paradisecloud.fcm.fme.model.response.ivrbranding.ActiveIvrBrandingProfilesResponse;
import com.paradisecloud.fcm.fme.model.response.ivrbranding.IvrBrandingProfileInfoResponse;
import com.paradisecloud.fcm.fme.model.response.ivrbranding.IvrBrandingProfilesResponse;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;

/**
 * ivrBranding模板Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-07-26
 */
@Service
public class BusiProfileIvrBrandingServiceImpl implements IBusiProfileIvrBrandingService 
{
    
    @Autowired
    private BusiProfileIvrBrandingMapper busiProfileIvrBrandingMapper;

    /**
     * <pre>同步所有</pre>
     * @author lilinhai
     * @since 2021-02-01 16:28 
     * @param fmeBridge
     * @param callLegProfileProcessor void
     */
    public void syncAllProfile(FmeBridge fmeBridge, IvrBrandingProfileProcessor ivrBrandingProfileProcessor)
    {
        int offset = 0;
        AtomicInteger totalCount = new AtomicInteger();
        while (true)
        {
            IvrBrandingProfilesResponse response = fmeBridge.getIvrBrandingProfileInvoker().getIvrBrandingProfiles(offset);
            if (response != null)
            {
                ActiveIvrBrandingProfilesResponse activeIvrBrandingProfilesResponse = response.getIvrBrandingProfiles();
                if (activeIvrBrandingProfilesResponse != null)
                {
                    List<IvrBrandingProfile> callLegProfile = activeIvrBrandingProfilesResponse.getIvrBrandingProfile();
                    if (callLegProfile != null)
                    {
                        for (IvrBrandingProfile callLegProfile2 : callLegProfile)
                        {
                            IvrBrandingProfileInfoResponse callLegProfileInfoResponse = fmeBridge.getIvrBrandingProfileInvoker().getIvrBrandingProfile(callLegProfile2.getId());
                            if (callLegProfileInfoResponse != null && callLegProfileInfoResponse.getIvrBrandingProfile() != null)
                            {
                                callLegProfile2 = callLegProfileInfoResponse.getIvrBrandingProfile();
                                ivrBrandingProfileProcessor.process(callLegProfile2);
                            }
                        }
                        
                        // 业务处理
                        Integer total = activeIvrBrandingProfilesResponse.getTotal();
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
    
    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-10-29 12:15 
     * @return
     * @see com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileIvrBrandingService#getDeptRecordCounts()
     */
    @Override
    public List<DeptRecordCount> getDeptRecordCounts()
    {
        return busiProfileIvrBrandingMapper.getDeptRecordCounts();
    }

    /**
     * <pre>获取当前登录用户所属部门的主用的FME的DTMF列表</pre>
     * @author lilinhai
     * @since 2021-01-26 15:28 
     * @return List<ModelBean>
     */
    public List<ModelBean> getAllIvrBrandingProfiles(Long deptId)
    {
        deptId = deptId == null ? SecurityUtils.getLoginUser().getUser().getDeptId() : deptId;
        FmeBridge mainMaxPriorityFmeHttpInvoker = BridgeUtils.getAvailableFmeBridge(deptId);
        BusiProfileIvrBranding condition = new BusiProfileIvrBranding();
        condition.setDeptId(deptId);
        List<BusiProfileIvrBranding> busiProfiles = busiProfileIvrBrandingMapper.selectBusiProfileIvrBrandingList(condition);
        final long tmpDeptId = deptId;
        List<ModelBean> mbs = new ArrayList<>();
        if (!ObjectUtils.isEmpty(busiProfiles))
        {
            for (BusiProfileIvrBranding profile : busiProfiles)
            {
                IvrBrandingProfile fp = mainMaxPriorityFmeHttpInvoker.getDataCache().getIvrBrandingProfile(profile.getIvrBrandingProfileUuid());
                if (fp == null)
                {
                    busiProfileIvrBrandingMapper.deleteBusiProfileIvrBrandingById(profile.getId());
                }
                else
                {
                    ModelBean mb = new ModelBean(fp);
                    mb.put("deptId", tmpDeptId);
                    mb.put("name", profile.getName());
                    mbs.add(mb);
                }
            }
        }
        return mbs;
    }
    
    /**
     * 查询ivrBranding模板
     * 
     * @param id ivrBranding模板ID
     * @return ivrBranding模板
     */
    @Override
    public BusiProfileIvrBranding selectBusiProfileIvrBrandingById(Long id)
    {
        return busiProfileIvrBrandingMapper.selectBusiProfileIvrBrandingById(id);
    }

    /**
     * 查询ivrBranding模板列表
     * 
     * @param busiProfileIvrBranding ivrBranding模板
     * @return ivrBranding模板
     */
    @Override
    public List<BusiProfileIvrBranding> selectBusiProfileIvrBrandingList(BusiProfileIvrBranding busiProfileIvrBranding)
    {
        return busiProfileIvrBrandingMapper.selectBusiProfileIvrBrandingList(busiProfileIvrBranding);
    }

    /**
     * 新增ivrBranding模板
     * 
     * @param busiProfileIvrBranding ivrBranding模板
     * @return 结果
     */
    @Override
    public int insertBusiProfileIvrBranding(BusiProfileIvrBranding busiProfileIvrBranding)
    {
        Assert.isTrue(!ObjectUtils.isEmpty(busiProfileIvrBranding.getName()), "name不能为空");
        Long deptId = busiProfileIvrBranding.getDeptId() == null ? SecurityUtils.getLoginUser().getUser().getDeptId() : busiProfileIvrBranding.getDeptId();
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(deptId);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        buildParams(busiProfileIvrBranding, nameValuePairs);
        
        String profileId = fmeBridge.getIvrBrandingProfileInvoker().createIvrBrandingProfile(nameValuePairs);
        if (ObjectUtils.isEmpty(profileId))
        {
            throw new SystemException(1001098, "创建IvrBranding profile失败");
        }
        
        IvrBrandingProfileInfoResponse profileInfoResponse = fmeBridge.getIvrBrandingProfileInvoker().getIvrBrandingProfile(profileId);
        if (profileInfoResponse != null && profileInfoResponse.getIvrBrandingProfile() != null)
        {
            // 更新缓存
            FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(deptId, new FmeBridgeAddpterProcessor()
            {
                public void process(FmeBridge fmeBridge)
                {
                    fmeBridge.getDataCache().update(profileInfoResponse.getIvrBrandingProfile());
                }
            });
        }
        
        busiProfileIvrBranding.getParams().put("id", profileId);
        busiProfileIvrBranding.getParams().put("deptId", deptId);
        
        // 处理保存
        busiProfileIvrBranding.setCreateTime(new Date());
        busiProfileIvrBranding.setDeptId(deptId);
        busiProfileIvrBranding.setIvrBrandingProfileUuid(profileId);
        busiProfileIvrBrandingMapper.insertBusiProfileIvrBranding(busiProfileIvrBranding);
        
        return ObjectUtils.isEmpty(profileId) ? 0 : 1;
    }
    
    private void buildParams(BusiProfileIvrBranding busiProfileIvrBranding, List<NameValuePair> nameValuePairs)
    {
        Assert.isTrue(busiProfileIvrBranding.getParams().containsKey("resourceLocation"), "resourceLocation不能为空");
        nameValuePairs.add(new BasicNameValuePair("resourceLocation", busiProfileIvrBranding.getParams().get("resourceLocation").toString()));
    }

    /**
     * 修改ivrBranding模板
     * 
     * @param busiProfileIvrBranding ivrBranding模板
     * @return 结果
     */
    @Override
    public int updateBusiProfileIvrBranding(BusiProfileIvrBranding busiProfileIvrBranding)
    {
        Assert.isTrue(!ObjectUtils.isEmpty(busiProfileIvrBranding.getName()), "name不能为空");
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(busiProfileIvrBranding.getDeptId());
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        buildParams(busiProfileIvrBranding, nameValuePairs);
        RestResponse restResponse = fmeBridge.getIvrBrandingProfileInvoker().updateIvrBrandingProfile(busiProfileIvrBranding.getParams().get("id").toString(), nameValuePairs);
        if (restResponse.isSuccess())
        {
            // 更新内存
            IvrBrandingProfileInfoResponse profileInfoResponse = fmeBridge.getIvrBrandingProfileInvoker().getIvrBrandingProfile(busiProfileIvrBranding.getParams().get("id").toString());
            if (profileInfoResponse != null && profileInfoResponse.getIvrBrandingProfile() != null)
            {
                // 更新入会方案缓存
                FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(busiProfileIvrBranding.getDeptId(), new FmeBridgeAddpterProcessor()
                {
                    public void process(FmeBridge fmeBridge)
                    {
                        fmeBridge.getDataCache().update(profileInfoResponse.getIvrBrandingProfile());
                    }
                });
            }
            
            long deptId = busiProfileIvrBranding.getDeptId() == null ? SecurityUtils.getLoginUser().getUser().getDeptId() : busiProfileIvrBranding.getDeptId();
            busiProfileIvrBranding.getParams().put("deptId", deptId);
            
            // 处理保存和默认入会方案设置
            BusiProfileIvrBranding condition = new BusiProfileIvrBranding();
            condition.setDeptId(deptId);
            condition.setIvrBrandingProfileUuid(busiProfileIvrBranding.getParams().get("id").toString());
            List<BusiProfileIvrBranding> busiProfiles = busiProfileIvrBrandingMapper.selectBusiProfileIvrBrandingList(condition);
            if (!ObjectUtils.isEmpty(busiProfiles))
            {
                for (BusiProfileIvrBranding dtmf : busiProfiles)
                {
                    dtmf.setUpdateTime(new Date());
                    dtmf.setName(busiProfileIvrBranding.getName());
                    busiProfileIvrBrandingMapper.updateBusiProfileIvrBranding(dtmf);
                }
            }
            
            return 1;
        }
        throw new SystemException(1009999, restResponse.getMessage());
    }

    /**
     * 批量删除ivrBranding模板
     * 
     * @param ids 需要删除的ivrBranding模板ID
     * @return 结果
     */
    @Override
    public int deleteBusiProfileIvrBrandingByIds(Long[] ids)
    {
        return busiProfileIvrBrandingMapper.deleteBusiProfileIvrBrandingByIds(ids);
    }

    /**
     * 删除ivrBranding模板信息
     * 
     * @param id ivrBranding模板ID
     * @return 结果
     */
    @Override
    public RestResponse deleteBusiProfileIvrBrandingById(BusiProfileIvrBranding busiProfileIvrBranding)
    {
        Object idObj = busiProfileIvrBranding.getParams().get("id");
        if (idObj == null)
        {
            throw new SystemException(1002321, "Profile ID为空，删除出错！");
        }
        
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(busiProfileIvrBranding.getDeptId());
        RestResponse result = fmeBridge.getIvrBrandingProfileInvoker().deleteIvrBrandingProfile(idObj.toString());
        if (result.isSuccess())
        {
            // 删除内存中的入会方案
            fmeBridge.getDataCache().deleteIvrBrandingProfile(idObj.toString());
            BusiProfileIvrBranding profileDeleteCon = new BusiProfileIvrBranding();
            profileDeleteCon.setIvrBrandingProfileUuid(idObj.toString());
            List<BusiProfileIvrBranding> ps = busiProfileIvrBrandingMapper.selectBusiProfileIvrBrandingList(profileDeleteCon);
            if (!ObjectUtils.isEmpty(ps))
            {
                for (BusiProfileIvrBranding d : ps)
                {
                    busiProfileIvrBrandingMapper.deleteBusiProfileIvrBrandingById(d.getId());
                }
            }
        }
        return result;
    }
}
