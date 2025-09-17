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
import com.paradisecloud.fcm.dao.mapper.BusiProfileCallMapper;
import com.paradisecloud.fcm.dao.model.BusiProfileCall;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileCallService;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils;
import com.paradisecloud.fcm.fme.model.cms.CallProfile;
import com.paradisecloud.fcm.fme.model.response.callprofile.ActiveCallProfilesResponse;
import com.paradisecloud.fcm.fme.model.response.callprofile.CallProfileInfoResponse;
import com.paradisecloud.fcm.fme.model.response.callprofile.CallProfilesResponse;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;

/**
 * call模板Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-07-26
 */
@Service
public class BusiProfileCallServiceImpl implements IBusiProfileCallService 
{
    @Autowired
    private BusiProfileCallMapper busiProfileCallMapper;

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-10-29 12:09 
     * @return
     * @see com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileCallService#getDeptRecordCounts()
     */
    @Override
    public List<DeptRecordCount> getDeptRecordCounts()
    {
        return busiProfileCallMapper.getDeptRecordCounts();
    }

    /**
     * <pre>同步所有</pre>
     * @author lilinhai
     * @since 2021-02-01 16:28 
     * @param fmeBridge
     * @param callLegProfileProcessor void
     */
    public void syncAllProfile(FmeBridge fmeBridge, CallProfileProcessor callProfileProcessor)
    {
        int offset = 0;
        AtomicInteger totalCount = new AtomicInteger();
        while (true)
        {
            CallProfilesResponse response = fmeBridge.getCallProfileInvoker().getCallProfiles(offset);
            if (response != null)
            {
                ActiveCallProfilesResponse activeCallProfilesResponse = response.getCallProfiles();
                if (activeCallProfilesResponse != null)
                {
                    List<CallProfile> callLegProfile = activeCallProfilesResponse.getCallProfile();
                    if (callLegProfile != null)
                    {
                        for (CallProfile callLegProfile2 : callLegProfile)
                        {
                            CallProfileInfoResponse callLegProfileInfoResponse = fmeBridge.getCallProfileInvoker().getCallProfile(callLegProfile2.getId());
                            if (callLegProfileInfoResponse != null && callLegProfileInfoResponse.getCallProfile() != null)
                            {
                                callLegProfile2 = callLegProfileInfoResponse.getCallProfile();
                                callProfileProcessor.process(callLegProfile2);
                            }
                        }
                        
                        // 业务处理
                        Integer total = activeCallProfilesResponse.getTotal();
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
    
    public List<ModelBean> getAllCallProfiles(Long deptId)
    {
        deptId = deptId == null ? SecurityUtils.getLoginUser().getUser().getDeptId() : deptId;
        FmeBridge mainMaxPriorityFmeHttpInvoker = BridgeUtils.getAvailableFmeBridge(deptId);
        BusiProfileCall condition = new BusiProfileCall();
        condition.setDeptId(deptId);
        List<BusiProfileCall> busiProfiles = busiProfileCallMapper.selectBusiProfileCallList(condition);
        final long tmpDeptId = deptId;
        List<ModelBean> mbs = new ArrayList<>();
        if (!ObjectUtils.isEmpty(busiProfiles))
        {
            for (BusiProfileCall profile : busiProfiles)
            {
                CallProfile fp = mainMaxPriorityFmeHttpInvoker.getDataCache().getCallProfile(profile.getCallProfileUuid());
                if (fp == null)
                {
                    busiProfileCallMapper.deleteBusiProfileCallById(profile.getId());
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
     * 查询call模板
     * 
     * @param id call模板ID
     * @return call模板
     */
    @Override
    public BusiProfileCall selectBusiProfileCallById(Long id)
    {
        return busiProfileCallMapper.selectBusiProfileCallById(id);
    }

    /**
     * 查询call模板列表
     * 
     * @param busiProfileCall call模板
     * @return call模板
     */
    @Override
    public List<BusiProfileCall> selectBusiProfileCallList(BusiProfileCall busiProfileCall)
    {
        return busiProfileCallMapper.selectBusiProfileCallList(busiProfileCall);
    }

    /**
     * 新增call模板
     * 
     * @param busiProfileCall call模板
     * @return 结果
     */
    @Override
    public int insertBusiProfileCall(BusiProfileCall busiProfileCall)
    {
        Assert.isTrue(!ObjectUtils.isEmpty(busiProfileCall.getName()), "name不能为空");
        Long deptId = busiProfileCall.getDeptId() == null ? SecurityUtils.getLoginUser().getUser().getDeptId() : busiProfileCall.getDeptId();
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(deptId);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        buildParams(busiProfileCall, nameValuePairs);
        
        String profileId = fmeBridge.getCallProfileInvoker().createCallProfile(nameValuePairs);
        if (ObjectUtils.isEmpty(profileId))
        {
            throw new SystemException(1001098, "创建Call profile失败");
        }
        
        CallProfileInfoResponse profileInfoResponse = fmeBridge.getCallProfileInvoker().getCallProfile(profileId);
        if (profileInfoResponse != null && profileInfoResponse.getCallProfile() != null)
        {
            // 更新缓存
            FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(deptId, new FmeBridgeAddpterProcessor()
            {
                public void process(FmeBridge fmeBridge)
                {
                    fmeBridge.getDataCache().update(profileInfoResponse.getCallProfile());
                }
            });
        }
        
        busiProfileCall.getParams().put("id", profileId);
        busiProfileCall.getParams().put("deptId", deptId);
        
        // 处理保存
        busiProfileCall.setCreateTime(new Date());
        busiProfileCall.setDeptId(deptId);
        busiProfileCall.setCallProfileUuid(profileId);
        busiProfileCallMapper.insertBusiProfileCall(busiProfileCall);
        
        return ObjectUtils.isEmpty(profileId) ? 0 : 1;
    }
    
    private void buildParams(BusiProfileCall busiProfileCall, List<NameValuePair> nameValuePairs)
    {
        Assert.isTrue(busiProfileCall.getParams().containsKey("participantLimit"), "participantLimit不能为空");
//        Assert.isTrue(busiProfileCall.getParams().containsKey("messageBoardEnabled"), "messageBoardEnabled不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("locked"), "locked不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("lockMode"), "lockMode不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("recordingMode"), "recordingMode不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("streamingMode"), "streamingMode不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("passcodeMode"), "passcodeMode不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("passcodeTimeout"), "passcodeTimeout不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("gatewayAudioCallOptimization"), "gatewayAudioCallOptimization不能为空");
//        Assert.isTrue(busiProfileCall.getParams().containsKey("lyncConferenceMode"), "lyncConferenceMode不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("messageBannerText"), "messageBannerText不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("muteBehavior"), "muteBehavior不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("sipRecorderUri"), "sipRecorderUri不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("sipStreamerUri"), "sipStreamerUri不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("chatAllowed"), "chatAllowed不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("raiseHandEnabled"), "raiseHandEnabled不能为空");
        
        nameValuePairs.add(new BasicNameValuePair("participantLimit", busiProfileCall.getParams().get("participantLimit").toString()));
//        nameValuePairs.add(new BasicNameValuePair("messageBoardEnabled", busiProfileCall.getParams().get("messageBoardEnabled").toString()));
        nameValuePairs.add(new BasicNameValuePair("locked", busiProfileCall.getParams().get("locked").toString()));
        nameValuePairs.add(new BasicNameValuePair("lockMode", busiProfileCall.getParams().get("lockMode").toString()));
        nameValuePairs.add(new BasicNameValuePair("recordingMode", busiProfileCall.getParams().get("recordingMode").toString()));
        nameValuePairs.add(new BasicNameValuePair("streamingMode", busiProfileCall.getParams().get("streamingMode").toString()));
        nameValuePairs.add(new BasicNameValuePair("passcodeMode", busiProfileCall.getParams().get("passcodeMode").toString()));
        nameValuePairs.add(new BasicNameValuePair("passcodeTimeout", busiProfileCall.getParams().get("passcodeTimeout").toString()));
        nameValuePairs.add(new BasicNameValuePair("gatewayAudioCallOptimization", busiProfileCall.getParams().get("gatewayAudioCallOptimization").toString()));
//        nameValuePairs.add(new BasicNameValuePair("lyncConferenceMode", busiProfileCall.getParams().get("lyncConferenceMode").toString()));
        nameValuePairs.add(new BasicNameValuePair("messageBannerText", busiProfileCall.getParams().get("messageBannerText").toString()));
        nameValuePairs.add(new BasicNameValuePair("muteBehavior", busiProfileCall.getParams().get("muteBehavior").toString()));
        nameValuePairs.add(new BasicNameValuePair("sipRecorderUri", busiProfileCall.getParams().get("sipRecorderUri").toString()));
        nameValuePairs.add(new BasicNameValuePair("sipStreamerUri", busiProfileCall.getParams().get("sipStreamerUri").toString()));
        nameValuePairs.add(new BasicNameValuePair("chatAllowed", busiProfileCall.getParams().get("chatAllowed").toString()));
        nameValuePairs.add(new BasicNameValuePair("raiseHandEnabled", busiProfileCall.getParams().get("raiseHandEnabled").toString()));
    }

    /**
     * 修改call模板
     * 
     * @param busiProfileCall call模板
     * @return 结果
     */
    @Override
    public int updateBusiProfileCall(BusiProfileCall busiProfileCall)
    {
        Assert.isTrue(!ObjectUtils.isEmpty(busiProfileCall.getName()), "name不能为空");
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(busiProfileCall.getDeptId());
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        buildParams(busiProfileCall, nameValuePairs);
        RestResponse restResponse = fmeBridge.getCallProfileInvoker().updateCallProfile(busiProfileCall.getParams().get("id").toString(), nameValuePairs);
        if (restResponse.isSuccess())
        {
            // 更新内存
            CallProfileInfoResponse profileInfoResponse = fmeBridge.getCallProfileInvoker().getCallProfile(busiProfileCall.getParams().get("id").toString());
            if (profileInfoResponse != null && profileInfoResponse.getCallProfile() != null)
            {
                // 更新入会方案缓存
                FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(busiProfileCall.getDeptId(), new FmeBridgeAddpterProcessor()
                {
                    public void process(FmeBridge fmeBridge)
                    {
                        fmeBridge.getDataCache().update(profileInfoResponse.getCallProfile());
                    }
                });
            }
            
            long deptId = busiProfileCall.getDeptId() == null ? SecurityUtils.getLoginUser().getUser().getDeptId() : busiProfileCall.getDeptId();
            busiProfileCall.getParams().put("deptId", deptId);
            
            // 处理保存和默认入会方案设置
            BusiProfileCall condition = new BusiProfileCall();
            condition.setDeptId(deptId);
            condition.setCallProfileUuid(busiProfileCall.getParams().get("id").toString());
            List<BusiProfileCall> busiProfiles = busiProfileCallMapper.selectBusiProfileCallList(condition);
            if (!ObjectUtils.isEmpty(busiProfiles))
            {
                for (BusiProfileCall dtmf : busiProfiles)
                {
                    dtmf.setUpdateTime(new Date());
                    dtmf.setName(busiProfileCall.getName());
                    busiProfileCallMapper.updateBusiProfileCall(dtmf);
                }
            }
            
            return 1;
        }
        throw new SystemException(1009999, restResponse.getMessage());
    }

    /**
     * 批量删除call模板
     * 
     * @param ids 需要删除的call模板ID
     * @return 结果
     */
    @Override
    public int deleteBusiProfileCallByIds(Long[] ids)
    {
        return busiProfileCallMapper.deleteBusiProfileCallByIds(ids);
    }

    /**
     * 删除call模板信息
     * 
     * @param id call模板ID
     * @return 结果
     */
    @Override
    public RestResponse deleteBusiProfileCallById(BusiProfileCall busiProfileCall)
    {
        Object idObj = busiProfileCall.getParams().get("id");
        if (idObj == null)
        {
            throw new SystemException(1002321, "Profile ID为空，删除出错！");
        }
        
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(busiProfileCall.getDeptId());
        RestResponse result = fmeBridge.getCallProfileInvoker().deleteCallProfile(idObj.toString());
        if (result.isSuccess())
        {
            // 删除内存中的入会方案
            fmeBridge.getDataCache().deleteCallProfile(idObj.toString());
            BusiProfileCall profileDeleteCon = new BusiProfileCall();
            profileDeleteCon.setCallProfileUuid(idObj.toString());
            List<BusiProfileCall> ps = busiProfileCallMapper.selectBusiProfileCallList(profileDeleteCon);
            if (!ObjectUtils.isEmpty(ps))
            {
                for (BusiProfileCall d : ps)
                {
                    busiProfileCallMapper.deleteBusiProfileCallById(d.getId());
                }
            }
        }
        return result;
    }
}
