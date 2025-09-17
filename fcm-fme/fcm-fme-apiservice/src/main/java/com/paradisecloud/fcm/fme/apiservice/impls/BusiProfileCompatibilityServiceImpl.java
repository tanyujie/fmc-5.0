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
import com.paradisecloud.fcm.dao.mapper.BusiProfileCompatibilityMapper;
import com.paradisecloud.fcm.dao.model.BusiProfileCompatibility;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileCompatibilityService;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils;
import com.paradisecloud.fcm.fme.model.cms.CompatibilityProfile;
import com.paradisecloud.fcm.fme.model.response.compatibilityprofile.ActiveCompatibilityProfilesResponse;
import com.paradisecloud.fcm.fme.model.response.compatibilityprofile.CompatibilityProfileInfoResponse;
import com.paradisecloud.fcm.fme.model.response.compatibilityprofile.CompatibilityProfilesResponse;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;

/**
 * 兼容性参数模板Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-07-27
 */
@Service
public class BusiProfileCompatibilityServiceImpl implements IBusiProfileCompatibilityService 
{
    @Autowired
    private BusiProfileCompatibilityMapper busiProfileCompatibilityMapper;
    
    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-10-29 12:11 
     * @return
     * @see com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileCompatibilityService#getDeptRecordCounts()
     */
    @Override
    public List<DeptRecordCount> getDeptRecordCounts()
    {
        return busiProfileCompatibilityMapper.getDeptRecordCounts();
    }

    /**
     * <pre>同步所有</pre>
     * @author lilinhai
     * @since 2021-02-01 16:28 
     * @param fmeBridge
     * @param callLegProfileProcessor void
     */
    public void syncAllProfile(FmeBridge fmeBridge, CompatibilityProfileProcessor compatibilityProfileProcessor)
    {
        int offset = 0;
        AtomicInteger totalCount = new AtomicInteger();
        while (true)
        {
            CompatibilityProfilesResponse response = fmeBridge.getCompatibilityProfileInvoker().getCompatibilityProfiles(offset);
            if (response != null)
            {
                ActiveCompatibilityProfilesResponse activeCompatibilityProfilesResponse = response.getCompatibilityProfiles();
                if (activeCompatibilityProfilesResponse != null)
                {
                    List<CompatibilityProfile> callLegProfile = activeCompatibilityProfilesResponse.getCompatibilityProfile();
                    if (callLegProfile != null)
                    {
                        for (CompatibilityProfile callLegProfile2 : callLegProfile)
                        {
                            CompatibilityProfileInfoResponse callLegProfileInfoResponse = fmeBridge.getCompatibilityProfileInvoker().getCompatibilityProfile(callLegProfile2.getId());
                            if (callLegProfileInfoResponse != null && callLegProfileInfoResponse.getCompatibilityProfile() != null)
                            {
                                callLegProfile2 = callLegProfileInfoResponse.getCompatibilityProfile();
                                compatibilityProfileProcessor.process(callLegProfile2);
                            }
                        }
                        
                        // 业务处理
                        Integer total = activeCompatibilityProfilesResponse.getTotal();
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
    
    public List<ModelBean> getAllCompatibilityProfiles(Long deptId)
    {
        deptId = deptId == null ? SecurityUtils.getLoginUser().getUser().getDeptId() : deptId;
        FmeBridge mainMaxPriorityFmeHttpInvoker = BridgeUtils.getAvailableFmeBridge(deptId);
        BusiProfileCompatibility condition = new BusiProfileCompatibility();
        condition.setDeptId(deptId);
        List<BusiProfileCompatibility> busiProfiles = busiProfileCompatibilityMapper.selectBusiProfileCompatibilityList(condition);
        final long tmpDeptId = deptId;
        List<ModelBean> mbs = new ArrayList<>();
        if (!ObjectUtils.isEmpty(busiProfiles))
        {
            for (BusiProfileCompatibility profile : busiProfiles)
            {
                CompatibilityProfile fp = mainMaxPriorityFmeHttpInvoker.getDataCache().getCompatibilityProfile(profile.getCompatibilityProfileUuid());
                if (fp == null)
                {
                    busiProfileCompatibilityMapper.deleteBusiProfileCompatibilityById(profile.getId());
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
     * 查询兼容性参数模板
     * 
     * @param id 兼容性参数模板ID
     * @return 兼容性参数模板
     */
    @Override
    public BusiProfileCompatibility selectBusiProfileCompatibilityById(Long id)
    {
        return busiProfileCompatibilityMapper.selectBusiProfileCompatibilityById(id);
    }

    /**
     * 查询兼容性参数模板列表
     * 
     * @param busiProfileCompatibility 兼容性参数模板
     * @return 兼容性参数模板
     */
    @Override
    public List<BusiProfileCompatibility> selectBusiProfileCompatibilityList(BusiProfileCompatibility busiProfileCompatibility)
    {
        return busiProfileCompatibilityMapper.selectBusiProfileCompatibilityList(busiProfileCompatibility);
    }

    /**
     * 新增兼容性参数模板
     * 
     * @param busiProfileCompatibility 兼容性参数模板
     * @return 结果
     */
    @Override
    public int insertBusiProfileCompatibility(BusiProfileCompatibility busiProfileCompatibility)
    {
        Assert.isTrue(!ObjectUtils.isEmpty(busiProfileCompatibility.getName()), "name不能为空");
        Long deptId = busiProfileCompatibility.getDeptId() == null ? SecurityUtils.getLoginUser().getUser().getDeptId() : busiProfileCompatibility.getDeptId();
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(deptId);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        buildParams(busiProfileCompatibility, nameValuePairs);
        
        String profileId = fmeBridge.getCompatibilityProfileInvoker().createCompatibilityProfile(nameValuePairs);
        if (ObjectUtils.isEmpty(profileId))
        {
            throw new SystemException(1001098, "创建Compatibility profile失败");
        }
        
        CompatibilityProfileInfoResponse profileInfoResponse = fmeBridge.getCompatibilityProfileInvoker().getCompatibilityProfile(profileId);
        if (profileInfoResponse != null && profileInfoResponse.getCompatibilityProfile() != null)
        {
            // 更新缓存
            FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(deptId, new FmeBridgeAddpterProcessor()
            {
                public void process(FmeBridge fmeBridge)
                {
                    fmeBridge.getDataCache().update(profileInfoResponse.getCompatibilityProfile());
                }
            });
        }
        
        busiProfileCompatibility.getParams().put("id", profileId);
        busiProfileCompatibility.getParams().put("deptId", deptId);
        
        // 处理保存
        busiProfileCompatibility.setCreateTime(new Date());
        busiProfileCompatibility.setDeptId(deptId);
        busiProfileCompatibility.setCompatibilityProfileUuid(profileId);
        busiProfileCompatibilityMapper.insertBusiProfileCompatibility(busiProfileCompatibility);
        
        return ObjectUtils.isEmpty(profileId) ? 0 : 1;
    }
    
    private void buildParams(BusiProfileCompatibility busiProfileCompatibility, List<NameValuePair> nameValuePairs)
    {
        Assert.isTrue(busiProfileCompatibility.getParams().containsKey("sipUdt"), "sipUdt不能为空");
        Assert.isTrue(busiProfileCompatibility.getParams().containsKey("sipMultistream"), "sipMultistream不能为空");
        Assert.isTrue(busiProfileCompatibility.getParams().containsKey("sipMediaPayloadTypeMode"), "sipMediaPayloadTypeMode不能为空");
        Assert.isTrue(busiProfileCompatibility.getParams().containsKey("h264CHPMode"), "h264CHPMode不能为空");
        Assert.isTrue(busiProfileCompatibility.getParams().containsKey("chromeWebRtcVideoCodec"), "chromeWebRtcVideoCodec不能为空");
        Assert.isTrue(busiProfileCompatibility.getParams().containsKey("chromeWebRtcH264interopMode"), "chromeWebRtcH264interopMode不能为空");
        Assert.isTrue(busiProfileCompatibility.getParams().containsKey("safariWebRtcH264interopMode"), "safariWebRtcH264interopMode不能为空");
        Assert.isTrue(busiProfileCompatibility.getParams().containsKey("passthroughMode"), "passthroughMode不能为空");
        Assert.isTrue(busiProfileCompatibility.getParams().containsKey("sipH224"), "sipH224不能为空");
        Assert.isTrue(busiProfileCompatibility.getParams().containsKey("distributionLinkMediaTraversal"), "distributionLinkMediaTraversal不能为空");
        nameValuePairs.add(new BasicNameValuePair("sipUdt", busiProfileCompatibility.getParams().get("sipUdt").toString()));
        nameValuePairs.add(new BasicNameValuePair("sipMultistream", busiProfileCompatibility.getParams().get("sipMultistream").toString()));
        nameValuePairs.add(new BasicNameValuePair("sipMediaPayloadTypeMode", busiProfileCompatibility.getParams().get("sipMediaPayloadTypeMode").toString()));
        nameValuePairs.add(new BasicNameValuePair("h264CHPMode", busiProfileCompatibility.getParams().get("h264CHPMode").toString()));
        nameValuePairs.add(new BasicNameValuePair("chromeWebRtcVideoCodec", busiProfileCompatibility.getParams().get("chromeWebRtcVideoCodec").toString()));
        nameValuePairs.add(new BasicNameValuePair("chromeWebRtcH264interopMode", busiProfileCompatibility.getParams().get("chromeWebRtcH264interopMode").toString()));
        nameValuePairs.add(new BasicNameValuePair("safariWebRtcH264interopMode", busiProfileCompatibility.getParams().get("safariWebRtcH264interopMode").toString()));
        nameValuePairs.add(new BasicNameValuePair("passthroughMode", busiProfileCompatibility.getParams().get("passthroughMode").toString()));
        nameValuePairs.add(new BasicNameValuePair("sipH224", busiProfileCompatibility.getParams().get("sipH224").toString()));
        nameValuePairs.add(new BasicNameValuePair("distributionLinkMediaTraversal", busiProfileCompatibility.getParams().get("distributionLinkMediaTraversal").toString()));
    }

    /**
     * 修改兼容性参数模板
     * 
     * @param busiProfileCompatibility 兼容性参数模板
     * @return 结果
     */
    @Override
    public int updateBusiProfileCompatibility(BusiProfileCompatibility busiProfileCompatibility)
    {
        Assert.isTrue(!ObjectUtils.isEmpty(busiProfileCompatibility.getName()), "name不能为空");
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(busiProfileCompatibility.getDeptId());
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        buildParams(busiProfileCompatibility, nameValuePairs);
        RestResponse restResponse = fmeBridge.getCompatibilityProfileInvoker().updateCompatibilityProfile(busiProfileCompatibility.getParams().get("id").toString(), nameValuePairs);
        if (restResponse.isSuccess())
        {
            // 更新内存
            CompatibilityProfileInfoResponse profileInfoResponse = fmeBridge.getCompatibilityProfileInvoker().getCompatibilityProfile(busiProfileCompatibility.getParams().get("id").toString());
            if (profileInfoResponse != null && profileInfoResponse.getCompatibilityProfile() != null)
            {
                // 更新入会方案缓存
                FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(busiProfileCompatibility.getDeptId(), new FmeBridgeAddpterProcessor()
                {
                    public void process(FmeBridge fmeBridge)
                    {
                        fmeBridge.getDataCache().update(profileInfoResponse.getCompatibilityProfile());
                    }
                });
            }
            
            long deptId = busiProfileCompatibility.getDeptId() == null ? SecurityUtils.getLoginUser().getUser().getDeptId() : busiProfileCompatibility.getDeptId();
            busiProfileCompatibility.getParams().put("deptId", deptId);
            
            // 处理保存和默认入会方案设置
            BusiProfileCompatibility condition = new BusiProfileCompatibility();
            condition.setDeptId(deptId);
            condition.setCompatibilityProfileUuid(busiProfileCompatibility.getParams().get("id").toString());
            List<BusiProfileCompatibility> busiProfiles = busiProfileCompatibilityMapper.selectBusiProfileCompatibilityList(condition);
            if (!ObjectUtils.isEmpty(busiProfiles))
            {
                for (BusiProfileCompatibility dtmf : busiProfiles)
                {
                    dtmf.setUpdateTime(new Date());
                    dtmf.setName(busiProfileCompatibility.getName());
                    busiProfileCompatibilityMapper.updateBusiProfileCompatibility(dtmf);
                }
            }
            
            return 1;
        }
        throw new SystemException(1009999, restResponse.getMessage());
    }

    /**
     * 批量删除兼容性参数模板
     * 
     * @param ids 需要删除的兼容性参数模板ID
     * @return 结果
     */
    @Override
    public int deleteBusiProfileCompatibilityByIds(Long[] ids)
    {
        return busiProfileCompatibilityMapper.deleteBusiProfileCompatibilityByIds(ids);
    }

    /**
     * 删除兼容性参数模板信息
     * 
     * @param id 兼容性参数模板ID
     * @return 结果
     */
    @Override
    public RestResponse deleteBusiProfileCompatibilityById(BusiProfileCompatibility busiProfileCompatibility)
    {
        Object idObj = busiProfileCompatibility.getParams().get("id");
        if (idObj == null)
        {
            throw new SystemException(1002321, "Profile ID为空，删除出错！");
        }
        
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(busiProfileCompatibility.getDeptId());
        RestResponse result = fmeBridge.getCompatibilityProfileInvoker().deleteCompatibilityProfile(idObj.toString());
        if (result.isSuccess())
        {
            // 删除内存中的入会方案
            fmeBridge.getDataCache().deleteCompatibilityProfile(idObj.toString());
            BusiProfileCompatibility profileDeleteCon = new BusiProfileCompatibility();
            profileDeleteCon.setCompatibilityProfileUuid(idObj.toString());
            List<BusiProfileCompatibility> ps = busiProfileCompatibilityMapper.selectBusiProfileCompatibilityList(profileDeleteCon);
            if (!ObjectUtils.isEmpty(ps))
            {
                for (BusiProfileCompatibility d : ps)
                {
                    busiProfileCompatibilityMapper.deleteBusiProfileCompatibilityById(d.getId());
                }
            }
        }
        return result;
    }
}
