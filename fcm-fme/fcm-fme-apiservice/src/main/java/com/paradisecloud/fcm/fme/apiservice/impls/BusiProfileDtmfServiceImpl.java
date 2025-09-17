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
import com.paradisecloud.fcm.dao.mapper.BusiProfileDtmfMapper;
import com.paradisecloud.fcm.dao.model.BusiProfileDtmf;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileDtmfService;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils;
import com.paradisecloud.fcm.fme.model.cms.DtmfProfile;
import com.paradisecloud.fcm.fme.model.response.dtmfprofile.ActiveDtmfProfilesResponse;
import com.paradisecloud.fcm.fme.model.response.dtmfprofile.DtmfProfileInfoResponse;
import com.paradisecloud.fcm.fme.model.response.dtmfprofile.DtmfProfilesResponse;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;

/**
 * DTMF模板Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-07-26
 */
@Service
public class BusiProfileDtmfServiceImpl implements IBusiProfileDtmfService 
{
    @Autowired
    private BusiProfileDtmfMapper busiProfileDtmfMapper;

    /**
     * <pre>同步所有</pre>
     * @author lilinhai
     * @since 2021-02-01 16:28 
     * @param fmeBridge
     * @param callLegProfileProcessor void
     */
    public void syncAllProfile(FmeBridge fmeBridge, DtmfProfileProcessor dtmfProfileProcessor)
    {
        int offset = 0;
        AtomicInteger totalCount = new AtomicInteger();
        while (true)
        {
            DtmfProfilesResponse response = fmeBridge.getDtmfProfileInvoker().getDtmfProfiles(offset);
            if (response != null)
            {
                ActiveDtmfProfilesResponse activeDtmfProfilesResponse = response.getDtmfProfiles();
                if (activeDtmfProfilesResponse != null)
                {
                    List<DtmfProfile> callLegProfile = activeDtmfProfilesResponse.getDtmfProfile();
                    if (callLegProfile != null)
                    {
                        for (DtmfProfile callLegProfile2 : callLegProfile)
                        {
                            DtmfProfileInfoResponse callLegProfileInfoResponse = fmeBridge.getDtmfProfileInvoker().getDtmfProfile(callLegProfile2.getId());
                            if (callLegProfileInfoResponse != null && callLegProfileInfoResponse.getDtmfProfile() != null)
                            {
                                callLegProfile2 = callLegProfileInfoResponse.getDtmfProfile();
                                dtmfProfileProcessor.process(callLegProfile2);
                            }
                        }
                        
                        // 业务处理
                        Integer total = activeDtmfProfilesResponse.getTotal();
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
     * @since 2021-10-29 12:14 
     * @return
     * @see com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileDtmfService#getDeptRecordCounts()
     */
    @Override
    public List<DeptRecordCount> getDeptRecordCounts()
    {
        return busiProfileDtmfMapper.getDeptRecordCounts();
    }

    /**
     * <pre>获取当前登录用户所属部门的主用的FME的DTMF列表</pre>
     * @author lilinhai
     * @since 2021-01-26 15:28 
     * @return List<ModelBean>
     */
    public List<ModelBean> getAllDtmfProfiles(Long deptId)
    {
        deptId = deptId == null ? SecurityUtils.getLoginUser().getUser().getDeptId() : deptId;
        FmeBridge mainMaxPriorityFmeHttpInvoker = BridgeUtils.getAvailableFmeBridge(deptId);
        BusiProfileDtmf condition = new BusiProfileDtmf();
        condition.setDeptId(deptId);
        List<BusiProfileDtmf> busiProfileDtmfs = busiProfileDtmfMapper.selectBusiProfileDtmfList(condition);
        final long tmpDeptId = deptId;
        List<ModelBean> mbs = new ArrayList<>();
        if (!ObjectUtils.isEmpty(busiProfileDtmfs))
        {
            for (BusiProfileDtmf busiProfileDtmf : busiProfileDtmfs)
            {
                DtmfProfile dtmfProfile = mainMaxPriorityFmeHttpInvoker.getDataCache().getDtmfProfile(busiProfileDtmf.getDtmfProfileUuid());
                if (dtmfProfile == null)
                {
                    busiProfileDtmfMapper.deleteBusiProfileDtmfById(busiProfileDtmf.getId());
                }
                else
                {
                    ModelBean mb = new ModelBean(dtmfProfile);
                    mb.put("deptId", tmpDeptId);
                    mb.put("name", busiProfileDtmf.getName());
                    mbs.add(mb);
                }
            }
        }
        return mbs;
    }
    
    /**
     * 查询DTMF模板
     * 
     * @param id DTMF模板ID
     * @return DTMF模板
     */
    @Override
    public BusiProfileDtmf selectBusiProfileDtmfById(Long id)
    {
        return busiProfileDtmfMapper.selectBusiProfileDtmfById(id);
    }

    /**
     * 查询DTMF模板列表
     * 
     * @param busiProfileDtmf DTMF模板
     * @return DTMF模板
     */
    @Override
    public List<BusiProfileDtmf> selectBusiProfileDtmfList(BusiProfileDtmf busiProfileDtmf)
    {
        return busiProfileDtmfMapper.selectBusiProfileDtmfList(busiProfileDtmf);
    }

    /**
     * 新增DTMF模板
     * 
     * @param busiProfileDtmf DTMF模板
     * @return 结果
     */
    @Override
    public int insertBusiProfileDtmf(BusiProfileDtmf busiProfileDtmf)
    {
        Assert.isTrue(!ObjectUtils.isEmpty(busiProfileDtmf.getName()), "name不能为空");
        Long deptId = busiProfileDtmf.getDeptId() == null ? SecurityUtils.getLoginUser().getUser().getDeptId() : busiProfileDtmf.getDeptId();
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(deptId);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        buildParams(busiProfileDtmf, nameValuePairs);
        
        String profileId = fmeBridge.getDtmfProfileInvoker().createDtmfProfile(nameValuePairs);
        if (ObjectUtils.isEmpty(profileId))
        {
            throw new SystemException(1001098, "创建DTMF profile失败");
        }
        
        DtmfProfileInfoResponse profileInfoResponse = fmeBridge.getDtmfProfileInvoker().getDtmfProfile(profileId);
        if (profileInfoResponse != null && profileInfoResponse.getDtmfProfile() != null)
        {
            // 更新缓存
            FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(deptId, new FmeBridgeAddpterProcessor()
            {
                public void process(FmeBridge fmeBridge)
                {
                    fmeBridge.getDataCache().update(profileInfoResponse.getDtmfProfile());
                }
            });
        }
        
        busiProfileDtmf.getParams().put("id", profileId);
        busiProfileDtmf.getParams().put("deptId", deptId);
        
        // 处理保存
        busiProfileDtmf.setCreateTime(new Date());
        busiProfileDtmf.setDeptId(deptId);
        busiProfileDtmf.setDtmfProfileUuid(profileId);
        busiProfileDtmfMapper.insertBusiProfileDtmf(busiProfileDtmf);
        
        return ObjectUtils.isEmpty(profileId) ? 0 : 1;
    }
    
    private void buildParams(BusiProfileDtmf busiProfileDtmf, List<NameValuePair> nameValuePairs)
    {
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("muteSelfAudio"), "muteSelfAudio不能为空");
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("unmuteSelfAudio"), "unmuteSelfAudio不能为空");
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("toggleMuteSelfAudio"), "toggleMuteSelfAudio不能为空");
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("lockCall"), "lockCall不能为空");
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("unlockCall"), "unlockCall不能为空");
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("nextLayout"), "入会方案nextLayout不能为空");
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("previousLayout"), "previousLayout不能为空");
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("muteAllExceptSelfAudio"), "muteAllExceptSelfAudio不能为空");
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("unmuteAllExceptSelfAudio"), "unmuteAllExceptSelfAudio不能为空");
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("startRecording"), "startRecording不能为空");
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("stopRecording"), "stopRecording不能为空");
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("startStreaming"), "startStreaming不能为空");
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("stopStreaming"), "stopStreaming不能为空");
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("allowAllMuteSelf"), "allowAllMuteSelf不能为空");
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("cancelAllowAllMuteSelf"), "cancelAllowAllMuteSelf不能为空");
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("allowAllPresentationContribution"), "allowAllPresentationContribution不能为空");
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("cancelAllowAllPresentationContribution"), "cancelAllowAllPresentationContribution不能为空");
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("muteAllNewAudio"), "muteAllNewAudio不能为空");
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("unmuteAllNewAudio"), "unmuteAllNewAudio不能为空");
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("defaultMuteAllNewAudio"), "defaultMuteAllNewAudio不能为空");
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("muteAllNewAndAllExceptSelfAudio"), "muteAllNewAndAllExceptSelfAudio不能为空");
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("unmuteAllNewAndAllExceptSelfAudio"), "unmuteAllNewAndAllExceptSelfAudio不能为空");
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("endCall"), "endCall不能为空");
        Assert.isTrue(busiProfileDtmf.getParams().containsKey("getTotalParticipantCount"), "getTotalParticipantCount不能为空");
        
        nameValuePairs.add(new BasicNameValuePair("muteSelfAudio", busiProfileDtmf.getParams().get("muteSelfAudio").toString()));
        nameValuePairs.add(new BasicNameValuePair("unmuteSelfAudio", busiProfileDtmf.getParams().get("unmuteSelfAudio").toString()));
        nameValuePairs.add(new BasicNameValuePair("toggleMuteSelfAudio", busiProfileDtmf.getParams().get("toggleMuteSelfAudio").toString()));
        nameValuePairs.add(new BasicNameValuePair("lockCall", busiProfileDtmf.getParams().get("lockCall").toString()));
        nameValuePairs.add(new BasicNameValuePair("unlockCall", busiProfileDtmf.getParams().get("unlockCall").toString()));
        nameValuePairs.add(new BasicNameValuePair("nextLayout", busiProfileDtmf.getParams().get("nextLayout").toString()));
        nameValuePairs.add(new BasicNameValuePair("previousLayout", busiProfileDtmf.getParams().get("previousLayout").toString()));
        nameValuePairs.add(new BasicNameValuePair("muteAllExceptSelfAudio", busiProfileDtmf.getParams().get("muteAllExceptSelfAudio").toString()));
        nameValuePairs.add(new BasicNameValuePair("unmuteAllExceptSelfAudio", busiProfileDtmf.getParams().get("unmuteAllExceptSelfAudio").toString()));
        nameValuePairs.add(new BasicNameValuePair("startRecording", busiProfileDtmf.getParams().get("startRecording").toString()));
        nameValuePairs.add(new BasicNameValuePair("stopRecording", busiProfileDtmf.getParams().get("stopRecording").toString()));
        nameValuePairs.add(new BasicNameValuePair("startStreaming", busiProfileDtmf.getParams().get("startStreaming").toString()));
        nameValuePairs.add(new BasicNameValuePair("stopStreaming", busiProfileDtmf.getParams().get("stopStreaming").toString()));
        nameValuePairs.add(new BasicNameValuePair("allowAllMuteSelf", busiProfileDtmf.getParams().get("allowAllMuteSelf").toString()));
        nameValuePairs.add(new BasicNameValuePair("cancelAllowAllMuteSelf", busiProfileDtmf.getParams().get("cancelAllowAllMuteSelf").toString()));
        nameValuePairs.add(new BasicNameValuePair("allowAllPresentationContribution", busiProfileDtmf.getParams().get("allowAllPresentationContribution").toString()));
        nameValuePairs.add(new BasicNameValuePair("cancelAllowAllPresentationContribution", busiProfileDtmf.getParams().get("cancelAllowAllPresentationContribution").toString()));
        nameValuePairs.add(new BasicNameValuePair("muteAllNewAudio", busiProfileDtmf.getParams().get("muteAllNewAudio").toString()));
        nameValuePairs.add(new BasicNameValuePair("unmuteAllNewAudio", busiProfileDtmf.getParams().get("unmuteAllNewAudio").toString()));
        nameValuePairs.add(new BasicNameValuePair("defaultMuteAllNewAudio", busiProfileDtmf.getParams().get("defaultMuteAllNewAudio").toString()));
        nameValuePairs.add(new BasicNameValuePair("muteAllNewAndAllExceptSelfAudio", busiProfileDtmf.getParams().get("muteAllNewAndAllExceptSelfAudio").toString()));
        nameValuePairs.add(new BasicNameValuePair("unmuteAllNewAndAllExceptSelfAudio", busiProfileDtmf.getParams().get("unmuteAllNewAndAllExceptSelfAudio").toString()));
        nameValuePairs.add(new BasicNameValuePair("endCall", busiProfileDtmf.getParams().get("endCall").toString()));
        nameValuePairs.add(new BasicNameValuePair("getTotalParticipantCount", busiProfileDtmf.getParams().get("getTotalParticipantCount").toString()));
    }

    /**
     * 修改DTMF模板
     * 
     * @param busiProfileDtmf DTMF模板
     * @return 结果
     */
    @Override
    public int updateBusiProfileDtmf(BusiProfileDtmf busiProfileDtmf)
    {
        Assert.isTrue(!ObjectUtils.isEmpty(busiProfileDtmf.getName()), "name不能为空");
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(busiProfileDtmf.getDeptId());
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        buildParams(busiProfileDtmf, nameValuePairs);
        RestResponse restResponse = fmeBridge.getDtmfProfileInvoker().updateDtmfProfile(busiProfileDtmf.getParams().get("id").toString(), nameValuePairs);
        if (restResponse.isSuccess())
        {
            // 更新内存
            DtmfProfileInfoResponse profileInfoResponse = fmeBridge.getDtmfProfileInvoker().getDtmfProfile(busiProfileDtmf.getParams().get("id").toString());
            if (profileInfoResponse != null && profileInfoResponse.getDtmfProfile() != null)
            {
                // 更新入会方案缓存
                FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(busiProfileDtmf.getDeptId(), new FmeBridgeAddpterProcessor()
                {
                    public void process(FmeBridge fmeBridge)
                    {
                        fmeBridge.getDataCache().update(profileInfoResponse.getDtmfProfile());
                    }
                });
            }
            
            long deptId = busiProfileDtmf.getDeptId() == null ? SecurityUtils.getLoginUser().getUser().getDeptId() : busiProfileDtmf.getDeptId();
            busiProfileDtmf.getParams().put("deptId", deptId);
            
            // 处理保存和默认入会方案设置
            BusiProfileDtmf condition = new BusiProfileDtmf();
            condition.setDeptId(deptId);
            condition.setDtmfProfileUuid(busiProfileDtmf.getParams().get("id").toString());
            List<BusiProfileDtmf> busiProfiles = busiProfileDtmfMapper.selectBusiProfileDtmfList(condition);
            if (!ObjectUtils.isEmpty(busiProfiles))
            {
                for (BusiProfileDtmf dtmf : busiProfiles)
                {
                    dtmf.setUpdateTime(new Date());
                    dtmf.setName(busiProfileDtmf.getName());
                    busiProfileDtmfMapper.updateBusiProfileDtmf(dtmf);
                }
            }
            
            return 1;
        }
        throw new SystemException(1009999, restResponse.getMessage());
    }

    /**
     * 批量删除DTMF模板
     * 
     * @param ids 需要删除的DTMF模板ID
     * @return 结果
     */
    @Override
    public int deleteBusiProfileDtmfByIds(Long[] ids)
    {
        return busiProfileDtmfMapper.deleteBusiProfileDtmfByIds(ids);
    }

    /**
     * 删除DTMF模板信息
     * 
     * @param id DTMF模板ID
     * @return 结果
     */
    @Override
    public RestResponse deleteBusiProfileDtmfById(BusiProfileDtmf busiProfileDtmf)
    {
        Object idObj = busiProfileDtmf.getParams().get("id");
        if (idObj == null)
        {
            throw new SystemException(1002321, "Profile ID为空，删除出错！");
        }
        
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(busiProfileDtmf.getDeptId());
        RestResponse result = fmeBridge.getDtmfProfileInvoker().deleteDtmfProfile(idObj.toString());
        if (result.isSuccess())
        {
            // 删除内存中的入会方案
            fmeBridge.getDataCache().deleteDtmfProfile(idObj.toString());
            BusiProfileDtmf profileDeleteCon = new BusiProfileDtmf();
            profileDeleteCon.setDtmfProfileUuid(idObj.toString());
            List<BusiProfileDtmf> ps = busiProfileDtmfMapper.selectBusiProfileDtmfList(profileDeleteCon);
            if (!ObjectUtils.isEmpty(ps))
            {
                for (BusiProfileDtmf d : ps)
                {
                    busiProfileDtmfMapper.deleteBusiProfileDtmfById(d.getId());
                }
            }
        }
        return result;
    
    }
}
