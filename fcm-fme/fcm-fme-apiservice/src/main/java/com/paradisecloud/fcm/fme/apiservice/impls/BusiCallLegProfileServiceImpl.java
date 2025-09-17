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
import com.paradisecloud.fcm.dao.mapper.BusiCallLegProfileMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiCallLegProfile;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiCallLegProfileService;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.enumer.FmeBridgeProcessingStrategy;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils;
import com.paradisecloud.fcm.fme.model.cms.CallLegProfile;
import com.paradisecloud.fcm.fme.model.response.calllegprofile.ActiveCallLegProfilesResponse;
import com.paradisecloud.fcm.fme.model.response.calllegprofile.CallLegProfileInfoResponse;
import com.paradisecloud.fcm.fme.model.response.calllegprofile.CallLegProfilesResponse;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;

/**
 * 入会方案配置，控制参会者进入会议的方案Service业务层处理
 *
 * @author lilinhai
 * @date 2021-01-26
 */
@Service
public class BusiCallLegProfileServiceImpl implements IBusiCallLegProfileService
{
    @Autowired
    private BusiCallLegProfileMapper busiCallLegProfileMapper;

    @Autowired
    private BusiTemplateConferenceMapper busiTemplateConferenceMapper;

    /**
     * 查询入会方案配置，控制参会者进入会议的方案
     *
     * @param id 入会方案配置，控制参会者进入会议的方案ID
     * @return 入会方案配置，控制参会者进入会议的方案
     */
    @Override
    public BusiCallLegProfile selectBusiCallLegProfileById(Long id)
    {
        return busiCallLegProfileMapper.selectBusiCallLegProfileById(id);
    }

    /**
     * 查询入会方案配置，控制参会者进入会议的方案列表
     *
     * @param busiCallLegProfile 入会方案配置，控制参会者进入会议的方案
     * @return 入会方案配置，控制参会者进入会议的方案
     */
    @Override
    public List<BusiCallLegProfile> selectBusiCallLegProfileList(BusiCallLegProfile busiCallLegProfile)
    {
        return busiCallLegProfileMapper.selectBusiCallLegProfileList(busiCallLegProfile);
    }

    /**
     * <pre>创建默认入会方案</pre>
     * @author lilinhai
     * @since 2021-03-08 15:36
     * @param fmeBridge
     * @return String
     */
    public String createDefaultCalllegProfile(FmeBridge fmeBridge, long deptId)
    {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("name", "(系统)静音入会方案"));
        nameValuePairs.add(new BasicNameValuePair("rxAudioMute", "true"));
        nameValuePairs.add(new BasicNameValuePair("allowAllPresentationContributionAllowed", "true"));
        nameValuePairs.add(new BasicNameValuePair("qualityMain", "max1080p30"));
        nameValuePairs.add(new BasicNameValuePair("qualityPresentation", "max1080p30"));
        nameValuePairs.add(new BasicNameValuePair("participantCounter", "never"));
        nameValuePairs.add(new BasicNameValuePair("participantLabels", "true"));
        nameValuePairs.add(new BasicNameValuePair("muteSelfAllowed", "true"));
        nameValuePairs.add(new BasicNameValuePair("defaultLayout", ""));
        String profileId = fmeBridge.getCallLegProfileInvoker().createCallLegProfile(nameValuePairs);

        // 添加入会方案成功后，更新缓存
        if (addBusiCallLegProfile(fmeBridge, deptId, profileId))
        {

            // 更新内存
            fmeBridge.getDataCache().update(fmeBridge.getCallLegProfileInvoker().getCallLegProfile(profileId).getCallLegProfile());
        }
        return profileId;
    }


    @Override
    public String createDefaultCalllegProfile(FmeBridge fmeBridge, long deptId, String quality) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("name", "(系统)静音入会方案"));
        nameValuePairs.add(new BasicNameValuePair("rxAudioMute", "true"));
        nameValuePairs.add(new BasicNameValuePair("allowAllPresentationContributionAllowed", "true"));
        nameValuePairs.add(new BasicNameValuePair("qualityMain", quality));
        nameValuePairs.add(new BasicNameValuePair("qualityPresentation", "max720p5"));
        nameValuePairs.add(new BasicNameValuePair("participantCounter", "never"));
        nameValuePairs.add(new BasicNameValuePair("participantLabels", "true"));
        nameValuePairs.add(new BasicNameValuePair("muteSelfAllowed", "true"));
        nameValuePairs.add(new BasicNameValuePair("defaultLayout", ""));
        String profileId = fmeBridge.getCallLegProfileInvoker().createCallLegProfile(nameValuePairs);

        // 添加入会方案成功后，更新缓存
        if (addBusiCallLegProfile(fmeBridge, deptId, profileId))
        {
            // 更新内存
            fmeBridge.getDataCache().update(fmeBridge.getCallLegProfileInvoker().getCallLegProfile(profileId).getCallLegProfile());
        }
        return profileId;
    }


    @Override
    public String createDefaultCalllegProfileNotInDb(FmeBridge fmeBridge, long deptId,boolean isMute, String quality) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("name", "(系统)静音入会方案"));
        nameValuePairs.add(new BasicNameValuePair("rxAudioMute", isMute? "true" : "false"));
        nameValuePairs.add(new BasicNameValuePair("allowAllPresentationContributionAllowed", "true"));
        nameValuePairs.add(new BasicNameValuePair("qualityMain", quality));
        nameValuePairs.add(new BasicNameValuePair("qualityPresentation", quality));
        nameValuePairs.add(new BasicNameValuePair("participantCounter", "never"));
        nameValuePairs.add(new BasicNameValuePair("participantLabels", "true"));
        nameValuePairs.add(new BasicNameValuePair("muteSelfAllowed", "true"));
        nameValuePairs.add(new BasicNameValuePair("defaultLayout", ""));
        String profileId = fmeBridge.getCallLegProfileInvoker().createCallLegProfile(nameValuePairs);
        // 更新内存
        fmeBridge.getDataCache().update(fmeBridge.getCallLegProfileInvoker().getCallLegProfile(profileId).getCallLegProfile());
        return profileId;
    }

    @Override
    public String createDefaultCalllegProfileIsMute(FmeBridge fmeBridge, long deptId, Boolean rxAudioMute)
    {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("name", "(系统)静音入会方案"));
        nameValuePairs.add(new BasicNameValuePair("rxAudioMute", rxAudioMute?"true":"false"));
        nameValuePairs.add(new BasicNameValuePair("allowAllPresentationContributionAllowed", "true"));
        nameValuePairs.add(new BasicNameValuePair("qualityMain", "max1080p30"));
        nameValuePairs.add(new BasicNameValuePair("qualityPresentation", "max1080p30"));
        nameValuePairs.add(new BasicNameValuePair("participantCounter", "never"));
        nameValuePairs.add(new BasicNameValuePair("participantLabels", "true"));
        nameValuePairs.add(new BasicNameValuePair("muteSelfAllowed", "true"));
        nameValuePairs.add(new BasicNameValuePair("defaultLayout", ""));
        String profileId = fmeBridge.getCallLegProfileInvoker().createCallLegProfile(nameValuePairs);

        // 添加入会方案成功后，更新缓存
        if (addBusiCallLegProfile(fmeBridge, deptId, profileId))
        {

            // 更新内存
            fmeBridge.getDataCache().update(fmeBridge.getCallLegProfileInvoker().getCallLegProfile(profileId).getCallLegProfile());
        }
        return profileId;
    }

    @Override
    public String createDefaultCalllegProfileIsMuteQuality(FmeBridge fmeBridge, long deptId, Boolean rxAudioMute, String qualityMain)
    {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("name", "(系统)静音入会方案"));
        nameValuePairs.add(new BasicNameValuePair("rxAudioMute", rxAudioMute?"true":"false"));
        nameValuePairs.add(new BasicNameValuePair("allowAllPresentationContributionAllowed", "true"));
        nameValuePairs.add(new BasicNameValuePair("qualityMain", qualityMain));
        nameValuePairs.add(new BasicNameValuePair("qualityPresentation", "max720p5"));
        nameValuePairs.add(new BasicNameValuePair("participantCounter", "never"));
        nameValuePairs.add(new BasicNameValuePair("participantLabels", "true"));
        nameValuePairs.add(new BasicNameValuePair("muteSelfAllowed", "true"));
        nameValuePairs.add(new BasicNameValuePair("defaultLayout", ""));
        String profileId = fmeBridge.getCallLegProfileInvoker().createCallLegProfile(nameValuePairs);

        // 添加入会方案成功后，更新缓存
        if (addBusiCallLegProfile(fmeBridge, deptId, profileId))
        {

            // 更新内存
            fmeBridge.getDataCache().update(fmeBridge.getCallLegProfileInvoker().getCallLegProfile(profileId).getCallLegProfile());
        }
        return profileId;
    }

    private boolean addBusiCallLegProfile(FmeBridge fmeBridge, long deptId, String profileId)
    {
        BusiCallLegProfile defaultBusiCallLegProfile = new BusiCallLegProfile();
        defaultBusiCallLegProfile.setDeptId(deptId);
        defaultBusiCallLegProfile.setCreateTime(new Date());
        defaultBusiCallLegProfile.setCallLegProfileUuid(profileId);
        defaultBusiCallLegProfile.setFmeId(fmeBridge.getBusiFme().getId());
        return busiCallLegProfileMapper.insertBusiCallLegProfile(defaultBusiCallLegProfile) == 1;
    }

    /**
     * <pre>获取当前登录用户所属部门的主用的FME的入会方案列表</pre>
     * @author lilinhai
     * @since 2021-01-26 15:28
     * @return List<ModelBean>
     */
    public List<ModelBean> getAllCallLegProfiles(Long deptId)
    {
        deptId = deptId == null ? SecurityUtils.getLoginUser().getUser().getDeptId() : deptId;
        FmeBridge mainMaxPriorityFmeHttpInvoker = BridgeUtils.getAvailableFmeBridge(deptId);
        BusiCallLegProfile condition = new BusiCallLegProfile();
        condition.setDeptId(deptId);
        List<BusiCallLegProfile> busiCallLegProfiles = busiCallLegProfileMapper.selectBusiCallLegProfileList(condition);
        final long tmpDeptId = deptId;
        List<ModelBean> mbs = new ArrayList<>();
        if (!ObjectUtils.isEmpty(busiCallLegProfiles))
        {
            for (BusiCallLegProfile busiCallLegProfile : busiCallLegProfiles)
            {
                CallLegProfile callLegProfile = mainMaxPriorityFmeHttpInvoker.getDataCache().getCallLegProfile(busiCallLegProfile.getCallLegProfileUuid());
                if (callLegProfile == null)
                {
                    busiCallLegProfileMapper.deleteBusiCallLegProfileById(busiCallLegProfile.getId());
                }
                else
                {
                    ModelBean mb = new ModelBean(callLegProfile);
                    mb.put("deptId", tmpDeptId);
                    mbs.add(mb);
                }
            }
        }
        return mbs;
    }

    /**
     * <pre>同步处理入会方案</pre>
     * @author lilinhai
     * @since 2021-02-01 16:28
     * @param fmeBridge
     * @param callLegProfileProcessor void
     */
    public void syncCallLegProfile(FmeBridge fmeBridge, CallLegProfileProcessor callLegProfileProcessor)
    {
        int offset = 0;
        AtomicInteger totalCount = new AtomicInteger();
        while (true)
        {
            CallLegProfilesResponse response = fmeBridge.getCallLegProfileInvoker().getCallLegProfiles(offset);
            if (response != null)
            {
                ActiveCallLegProfilesResponse activeCallLegProfilesResponse = response.getCallLegProfiles();
                if (activeCallLegProfilesResponse != null)
                {
                    List<CallLegProfile> callLegProfile = activeCallLegProfilesResponse.getCallLegProfile();
                    if (callLegProfile != null)
                    {
                        for (CallLegProfile callLegProfile2 : callLegProfile)
                        {
                            CallLegProfileInfoResponse callLegProfileInfoResponse = fmeBridge.getCallLegProfileInvoker().getCallLegProfile(callLegProfile2.getId());
                            if (callLegProfileInfoResponse != null && callLegProfileInfoResponse.getCallLegProfile() != null)
                            {
                                callLegProfile2 = callLegProfileInfoResponse.getCallLegProfile();
                                callLegProfileProcessor.process(callLegProfile2);
                            }
                        }

                        // 业务处理
                        Integer total = activeCallLegProfilesResponse.getTotal();
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
     * 新增入会方案配置，控制参会者进入会议的方案
     *
     * @param busiCallLegProfile 入会方案配置，控制参会者进入会议的方案
     * @return 结果
     */
    @Override
    public int insertBusiCallLegProfile(BusiCallLegProfile busiCallLegProfile)
    {
        Assert.notNull(busiCallLegProfile.getParams(), "入会方案参数不能为空");

        Long deptId = busiCallLegProfile.getDeptId() == null ? SecurityUtils.getLoginUser().getUser().getDeptId() : busiCallLegProfile.getDeptId();
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(deptId);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        buildParams(busiCallLegProfile, nameValuePairs);

        String profileId = fmeBridge.getCallLegProfileInvoker().createCallLegProfile(nameValuePairs);
        if (ObjectUtils.isEmpty(profileId))
        {
            throw new SystemException(1001098, "创建入会方案失败");
        }

        CallLegProfileInfoResponse callLegProfileInfoResponse = fmeBridge.getCallLegProfileInvoker().getCallLegProfile(profileId);
        if (callLegProfileInfoResponse != null && callLegProfileInfoResponse.getCallLegProfile() != null)
        {
            // 更新入会方案缓存
            FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(deptId, new FmeBridgeAddpterProcessor()
            {
                public void process(FmeBridge fmeBridge)
                {
                    fmeBridge.getDataCache().update(callLegProfileInfoResponse.getCallLegProfile());
                }
            });
        }

        busiCallLegProfile.getParams().put("id", profileId);
        busiCallLegProfile.getParams().put("deptId", deptId);

        // 处理保存和默认入会方案设置
        addBusiCallLegProfile(fmeBridge, deptId, profileId);
        return ObjectUtils.isEmpty(profileId) ? 0 : 1;
    }

    @Override
    public synchronized void updateCallLegProfile(FmeBridge fmeBridge, String id, List<NameValuePair> nameValuePairs)
    {
        RestResponse restResponse = fmeBridge.getCallLegProfileInvoker().updateCallLegProfile(id, nameValuePairs);
        if (restResponse.isSuccess())
        {
            // 更新内存
            CallLegProfileInfoResponse callLegProfileInfoResponse = fmeBridge.getCallLegProfileInvoker().getCallLegProfile(id);
            if (callLegProfileInfoResponse != null && callLegProfileInfoResponse.getCallLegProfile() != null)
            {
                fmeBridge.doFmeBridgeBusiness(FmeBridgeProcessingStrategy.TRAVERSE, new FmeBridgeAddpterProcessor()
                {
                    public void process(FmeBridge fmeBridge)
                    {
                        fmeBridge.getDataCache().update(callLegProfileInfoResponse.getCallLegProfile());
                    }
                });
            }
        }
        else
        {
            throw new SystemException(1009898, restResponse.getMessage());
        }
    }

    /**
     * 修改入会方案配置，控制参会者进入会议的方案
     *
     * @param busiCallLegProfile 入会方案配置，控制参会者进入会议的方案
     * @return 结果
     */
    @Override
    public int updateBusiCallLegProfile(BusiCallLegProfile busiCallLegProfile)
    {
        Assert.notNull(busiCallLegProfile.getParams(), "入会方案参数不能为空");
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(busiCallLegProfile.getDeptId());
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        buildParams(busiCallLegProfile, nameValuePairs);

        // 更新入会参数
        updateCallLegProfile(fmeBridge, busiCallLegProfile.getParams().get("id").toString(), nameValuePairs);
        long deptId = busiCallLegProfile.getDeptId() == null ? SecurityUtils.getLoginUser().getUser().getDeptId() : busiCallLegProfile.getDeptId();
        busiCallLegProfile.getParams().put("deptId", deptId);

        // 处理保存和默认入会方案设置
        BusiCallLegProfile condition = new BusiCallLegProfile();
        condition.setDeptId(deptId);
        condition.setCallLegProfileUuid(busiCallLegProfile.getParams().get("id").toString());
        List<BusiCallLegProfile> busiCallLegProfiles = busiCallLegProfileMapper.selectBusiCallLegProfileList(condition);
        if (!ObjectUtils.isEmpty(busiCallLegProfiles))
        {
            for (BusiCallLegProfile busiCallLegProfile0 : busiCallLegProfiles)
            {
                busiCallLegProfile0.setUpdateTime(new Date());
                busiCallLegProfileMapper.updateBusiCallLegProfile(busiCallLegProfile0);
            }
        }
        return 1;
    }

    /**
     * 删除入会方案配置，控制参会者进入会议的方案信息
     *
     * @param id 入会方案配置，控制参会者进入会议的方案ID
     * @return 结果
     */
    @Override
    public RestResponse deleteBusiCallLegProfileById(BusiCallLegProfile busiCallLegProfile)
    {
        Object idObj = busiCallLegProfile.getParams().get("id");
        if (idObj == null)
        {
            throw new SystemException(1002321, "入会方案ID为空，删除出错！");
        }
        BusiTemplateConference busiTemplateConference = new BusiTemplateConference();
        busiTemplateConference.setCallLegProfileId(idObj.toString());
        List<BusiTemplateConference> tcs = busiTemplateConferenceMapper.selectBusiTemplateConferenceList(busiTemplateConference);
        if (!ObjectUtils.isEmpty(tcs))
        {
            throw new SystemException(1002321, "该入会方案已被会议【" + tcs.get(0).getName() + "】使用，不能删除！");
        }

        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(busiCallLegProfile.getDeptId());
        RestResponse result = fmeBridge.getCallLegProfileInvoker().deleteCallLegProfile(idObj.toString());
        if (result.isSuccess())
        {
            // 删除内存中的入会方案
            fmeBridge.getDataCache().deleteCallLegProfile(idObj.toString());
            BusiCallLegProfile busiCallLegProfileDeleteCon = new BusiCallLegProfile();
            busiCallLegProfileDeleteCon.setCallLegProfileUuid(idObj.toString());
            List<BusiCallLegProfile> ps = busiCallLegProfileMapper.selectBusiCallLegProfileList(busiCallLegProfileDeleteCon);
            if (!ObjectUtils.isEmpty(ps))
            {
                for (BusiCallLegProfile busiCallLegProfile2 : ps)
                {
                    busiCallLegProfileMapper.deleteBusiCallLegProfileById(busiCallLegProfile2.getId());
                }
            }
        }
        return result;
    }

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-10-29 12:07
     * @return
     * @see com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiCallLegProfileService#getDeptRecordCounts()
     */
    @Override
    public List<DeptRecordCount> getDeptRecordCounts()
    {
        return busiCallLegProfileMapper.getDeptRecordCounts();
    }

    private void buildParams(BusiCallLegProfile busiCallLegProfile, List<NameValuePair> nameValuePairs)
    {
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("name"), "入会方案名称不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("rxAudioMute"), "入会方案rxAudioMute不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("allowAllPresentationContributionAllowed"), "入会方案allowAllPresentationContributionAllowed不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("qualityMain"), "入会方案qualityMain不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("qualityPresentation"), "入会方案qualityPresentation不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("participantCounter"), "入会方案participantCounter不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("participantLabels"), "入会方案participantLabels不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("muteSelfAllowed"), "入会方案muteSelfAllowed不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("txAudioMute"), "入会方案txAudioMute不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("defaultLayout"), "入会方案defaultLayout不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("presentationDisplayMode"), "入会方案presentationDisplayMode不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("presentationViewingAllowed"), "入会方案presentationViewingAllowed不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("sipPresentationChannelEnabled"), "入会方案sipPresentationChannelEnabled不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("bfcpMode"), "入会方案bfcpMode不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("controlRemoteCameraAllowed"), "入会方案controlRemoteCameraAllowed不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("audioGainMode"), "入会方案audioGainMode不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("setImportanceAllowed"), "入会方案setImportanceAllowed不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("sipMediaEncryption"), "入会方案sipMediaEncryption不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("rxAudioMute"), "入会方案rxAudioMute不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("rxVideoMute"), "入会方案rxVideoMute不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("txVideoMute"), "入会方案txVideoMute不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("needsActivation"), "入会方案needsActivation不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("deactivationMode"), "入会方案deactivationMode不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("deactivationModeTime"), "入会方案deactivationModeTime不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("telepresenceCallsAllowed"), "入会方案telepresenceCallsAllowed不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("callLockAllowed"), "入会方案callLockAllowed不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("changeJoinAudioMuteOverrideAllowed"), "入会方案changeJoinAudioMuteOverrideAllowed不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("endCallAllowed"), "入会方案endCallAllowed不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("disconnectOthersAllowed"), "入会方案disconnectOthersAllowed不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("addParticipantAllowed"), "入会方案addParticipantAllowed不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("muteOthersAllowed"), "入会方案muteOthersAllowed不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("videoMuteOthersAllowed"), "入会方案videoMuteOthersAllowed不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("videoMuteSelfAllowed"), "入会方案videoMuteSelfAllowed不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("changeLayoutAllowed"), "入会方案changeLayoutAllowed不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("allowAllMuteSelfAllowed"), "入会方案allowAllMuteSelfAllowed不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("maxCallDurationTime"), "入会方案maxCallDurationTime不能为空");
        Assert.isTrue(busiCallLegProfile.getParams().containsKey("meetingTitlePosition"), "入会方案meetingTitlePosition不能为空");

        nameValuePairs.add(new BasicNameValuePair("name", busiCallLegProfile.getParams().get("name").toString()));
        nameValuePairs.add(new BasicNameValuePair("rxAudioMute", busiCallLegProfile.getParams().get("rxAudioMute").toString()));
        nameValuePairs.add(new BasicNameValuePair("allowAllPresentationContributionAllowed", busiCallLegProfile.getParams().get("allowAllPresentationContributionAllowed").toString()));
        nameValuePairs.add(new BasicNameValuePair("qualityMain", busiCallLegProfile.getParams().get("qualityMain").toString()));
        nameValuePairs.add(new BasicNameValuePair("qualityPresentation", busiCallLegProfile.getParams().get("qualityPresentation").toString()));
        nameValuePairs.add(new BasicNameValuePair("participantCounter", busiCallLegProfile.getParams().get("participantCounter").toString()));
        nameValuePairs.add(new BasicNameValuePair("participantLabels", busiCallLegProfile.getParams().get("participantLabels").toString()));
        nameValuePairs.add(new BasicNameValuePair("muteSelfAllowed", busiCallLegProfile.getParams().get("muteSelfAllowed").toString()));
        nameValuePairs.add(new BasicNameValuePair("txAudioMute", busiCallLegProfile.getParams().get("txAudioMute").toString()));
        nameValuePairs.add(new BasicNameValuePair("defaultLayout", busiCallLegProfile.getParams().get("defaultLayout").toString()));
        nameValuePairs.add(new BasicNameValuePair("presentationDisplayMode", busiCallLegProfile.getParams().get("presentationDisplayMode").toString()));
        nameValuePairs.add(new BasicNameValuePair("presentationViewingAllowed", busiCallLegProfile.getParams().get("presentationViewingAllowed").toString()));
        nameValuePairs.add(new BasicNameValuePair("sipPresentationChannelEnabled", busiCallLegProfile.getParams().get("sipPresentationChannelEnabled").toString()));
        nameValuePairs.add(new BasicNameValuePair("bfcpMode", busiCallLegProfile.getParams().get("bfcpMode").toString()));
        nameValuePairs.add(new BasicNameValuePair("controlRemoteCameraAllowed", busiCallLegProfile.getParams().get("controlRemoteCameraAllowed").toString()));
        nameValuePairs.add(new BasicNameValuePair("audioGainMode", busiCallLegProfile.getParams().get("audioGainMode").toString()));
        nameValuePairs.add(new BasicNameValuePair("setImportanceAllowed", busiCallLegProfile.getParams().get("setImportanceAllowed").toString()));
        nameValuePairs.add(new BasicNameValuePair("sipMediaEncryption", busiCallLegProfile.getParams().get("sipMediaEncryption").toString()));
        nameValuePairs.add(new BasicNameValuePair("rxAudioMute", busiCallLegProfile.getParams().get("rxAudioMute").toString()));
        nameValuePairs.add(new BasicNameValuePair("rxVideoMute", busiCallLegProfile.getParams().get("rxVideoMute").toString()));
        nameValuePairs.add(new BasicNameValuePair("txVideoMute", busiCallLegProfile.getParams().get("txVideoMute").toString()));
        nameValuePairs.add(new BasicNameValuePair("needsActivation", busiCallLegProfile.getParams().get("needsActivation").toString()));
        nameValuePairs.add(new BasicNameValuePair("deactivationMode", busiCallLegProfile.getParams().get("deactivationMode").toString()));
        nameValuePairs.add(new BasicNameValuePair("deactivationModeTime", busiCallLegProfile.getParams().get("deactivationModeTime").toString()));
        nameValuePairs.add(new BasicNameValuePair("telepresenceCallsAllowed", busiCallLegProfile.getParams().get("telepresenceCallsAllowed").toString()));
        nameValuePairs.add(new BasicNameValuePair("callLockAllowed", busiCallLegProfile.getParams().get("callLockAllowed").toString()));
        nameValuePairs.add(new BasicNameValuePair("changeJoinAudioMuteOverrideAllowed", busiCallLegProfile.getParams().get("changeJoinAudioMuteOverrideAllowed").toString()));
        nameValuePairs.add(new BasicNameValuePair("endCallAllowed", busiCallLegProfile.getParams().get("endCallAllowed").toString()));
        nameValuePairs.add(new BasicNameValuePair("disconnectOthersAllowed", busiCallLegProfile.getParams().get("disconnectOthersAllowed").toString()));
        nameValuePairs.add(new BasicNameValuePair("addParticipantAllowed", busiCallLegProfile.getParams().get("addParticipantAllowed").toString()));
        nameValuePairs.add(new BasicNameValuePair("muteOthersAllowed", busiCallLegProfile.getParams().get("muteOthersAllowed").toString()));
        nameValuePairs.add(new BasicNameValuePair("videoMuteOthersAllowed", busiCallLegProfile.getParams().get("videoMuteOthersAllowed").toString()));
        nameValuePairs.add(new BasicNameValuePair("videoMuteSelfAllowed", busiCallLegProfile.getParams().get("videoMuteSelfAllowed").toString()));
        nameValuePairs.add(new BasicNameValuePair("changeLayoutAllowed", busiCallLegProfile.getParams().get("changeLayoutAllowed").toString()));
        nameValuePairs.add(new BasicNameValuePair("allowAllMuteSelfAllowed", busiCallLegProfile.getParams().get("allowAllMuteSelfAllowed").toString()));
        nameValuePairs.add(new BasicNameValuePair("maxCallDurationTime", busiCallLegProfile.getParams().get("maxCallDurationTime").toString()));
        nameValuePairs.add(new BasicNameValuePair("meetingTitlePosition", busiCallLegProfile.getParams().get("meetingTitlePosition").toString()));
    }
}
