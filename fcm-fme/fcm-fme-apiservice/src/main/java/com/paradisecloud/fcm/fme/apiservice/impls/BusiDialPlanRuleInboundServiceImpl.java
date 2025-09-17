package com.paradisecloud.fcm.fme.apiservice.impls;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiDialPlanRuleInbound;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiDialPlanRuleInboundService;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.enumer.FmeBridgeProcessingStrategy;
import com.paradisecloud.fcm.fme.model.cms.InboundDialPlanRule;
import com.paradisecloud.fcm.fme.model.response.indialplan.ActiveInboundDialPlanRulesResponse;
import com.paradisecloud.fcm.fme.model.response.indialplan.InboundDialPlanRuleInfoResponse;
import com.paradisecloud.fcm.fme.model.response.indialplan.InboundDialPlanRulesResponse;
import com.sinhy.exception.SystemException;

/**
 * 内呼计划Service业务层处理
 * 
 * @author lilinhai
 * @date 2022-05-13
 */
@Service
public class BusiDialPlanRuleInboundServiceImpl implements IBusiDialPlanRuleInboundService 
{

    @Override
    public void syncAllPlan(FmeBridge fmeBridge, InboundDialPlanRuleProcessor inboundDialPlanRuleProcessor)
    {
        int offset = 0;
        AtomicInteger totalCount = new AtomicInteger();
        while (true)
        {
            InboundDialPlanRulesResponse response = fmeBridge.getInboundDialPlanRuleInvoker().getInboundDialPlanRules(offset);
            if (response != null)
            {
                ActiveInboundDialPlanRulesResponse activeInboundDialPlanRulesResponse = response.getInboundDialPlanRules();
                if (activeInboundDialPlanRulesResponse != null)
                {
                    List<InboundDialPlanRule> callLegProfile = activeInboundDialPlanRulesResponse.getInboundDialPlanRule();
                    if (callLegProfile != null)
                    {
                        for (InboundDialPlanRule callLegProfile2 : callLegProfile)
                        {
                            InboundDialPlanRuleInfoResponse callLegProfileInfoResponse = fmeBridge.getInboundDialPlanRuleInvoker().getInboundDialPlanRule(callLegProfile2.getId());
                            if (callLegProfileInfoResponse != null && callLegProfileInfoResponse.getInboundDialPlanRule() != null)
                            {
                                callLegProfile2 = callLegProfileInfoResponse.getInboundDialPlanRule();
                                inboundDialPlanRuleProcessor.process(callLegProfile2);
                            }
                        }
                        
                        // 业务处理
                        Integer total = activeInboundDialPlanRulesResponse.getTotal();
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
    
    public List<ModelBean> getAllRecords(Long fmeId)
    {
        FmeBridge mainMaxPriorityFmeHttpInvoker = FmeBridgeCache.getInstance().get(fmeId);
        List<ModelBean> mbs = new ArrayList<>();
        List<InboundDialPlanRule> ins = mainMaxPriorityFmeHttpInvoker.getDataCache().getInboundDialPlanRules();
        for (InboundDialPlanRule inboundDialPlanRule : ins)
        {
            ModelBean mb = new ModelBean(inboundDialPlanRule);
            mbs.add(mb);
        }
        return mbs;
    }

    private void buildParams(BusiDialPlanRuleInbound busiProfileCall, List<NameValuePair> nameValuePairs)
    {
        Assert.isTrue(busiProfileCall.getParams().containsKey("domain"), "domain不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("priority"), "priority不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("resolveTocoSpaces"), "resolveTocoSpaces不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("resolveToIvrs"), "resolveToIvrs不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("resolveToLyncConferences"), "resolveToLyncConferences不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("resolveToLyncSimplejoin"), "resolveToLyncSimplejoin不能为空");
        
        nameValuePairs.add(new BasicNameValuePair("domain", busiProfileCall.getParams().get("domain").toString()));
        nameValuePairs.add(new BasicNameValuePair("priority", busiProfileCall.getParams().get("priority").toString()));
        nameValuePairs.add(new BasicNameValuePair("resolveTocoSpaces", busiProfileCall.getParams().get("resolveTocoSpaces").toString()));
        nameValuePairs.add(new BasicNameValuePair("resolveToIvrs", busiProfileCall.getParams().get("resolveToIvrs").toString()));
        nameValuePairs.add(new BasicNameValuePair("resolveToLyncConferences", busiProfileCall.getParams().get("resolveToLyncConferences").toString()));
        nameValuePairs.add(new BasicNameValuePair("resolveToLyncSimplejoin", busiProfileCall.getParams().get("resolveToLyncSimplejoin").toString()));
    }

    /**
     * 新增内呼计划
     * 
     * @param busiDialPlanRuleInbound 内呼计划
     * @return 结果
     */
    @Override
    public int insertBusiDialPlanRuleInbound(BusiDialPlanRuleInbound busiProfileCall)
    {
        long fmeId = (Integer)busiProfileCall.getParams().get("fmeId");
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().get(fmeId);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        buildParams(busiProfileCall, nameValuePairs);
        
        String profileId = fmeBridge.getInboundDialPlanRuleInvoker().createInboundDialPlanRule(nameValuePairs);
        if (ObjectUtils.isEmpty(profileId))
        {
            throw new SystemException(1001098, "创建内呼计划失败");
        }
        
        InboundDialPlanRuleInfoResponse profileInfoResponse = fmeBridge.getInboundDialPlanRuleInvoker().getInboundDialPlanRule(profileId);
        if (profileInfoResponse != null && profileInfoResponse.getInboundDialPlanRule() != null)
        {
            // 更新缓存
            fmeBridge.doFmeBridgeBusiness(FmeBridgeProcessingStrategy.TRAVERSE, new FmeBridgeAddpterProcessor()
            {
                public void process(FmeBridge fmeBridge)
                {
                    fmeBridge.getDataCache().update(profileInfoResponse.getInboundDialPlanRule());
                }
            });
        }
        
        busiProfileCall.getParams().put("id", profileId);
        
        // 处理保存
        busiProfileCall.setCreateTime(new Date());
        busiProfileCall.setPlanUuid(profileId);
        return ObjectUtils.isEmpty(profileId) ? 0 : 1;
    }

    /**
     * 修改内呼计划
     * 
     * @param busiDialPlanRuleInbound 内呼计划
     * @return 结果
     */
    @Override
    public int updateBusiDialPlanRuleInbound(BusiDialPlanRuleInbound busiDialPlanRuleInbound)
    {
        long fmeId = (Integer)busiDialPlanRuleInbound.getParams().get("fmeId");
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().get(fmeId);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        buildParams(busiDialPlanRuleInbound, nameValuePairs);
        RestResponse restResponse = fmeBridge.getInboundDialPlanRuleInvoker().updateInboundDialPlanRule(busiDialPlanRuleInbound.getParams().get("id").toString(), nameValuePairs);
        if (restResponse.isSuccess())
        {
            // 更新内存
            InboundDialPlanRuleInfoResponse profileInfoResponse = fmeBridge.getInboundDialPlanRuleInvoker().getInboundDialPlanRule(busiDialPlanRuleInbound.getParams().get("id").toString());
            if (profileInfoResponse != null && profileInfoResponse.getInboundDialPlanRule() != null)
            {
                // 更新入会方案缓存
                fmeBridge.doFmeBridgeBusiness(FmeBridgeProcessingStrategy.TRAVERSE, new FmeBridgeAddpterProcessor()
                {
                    public void process(FmeBridge fmeBridge)
                    {
                        fmeBridge.getDataCache().update(profileInfoResponse.getInboundDialPlanRule());
                    }
                });
            }
            return 1;
        }
        throw new SystemException(1009999, restResponse.getMessage());
    }

    
    public RestResponse deleteBusiProfileCallById(BusiDialPlanRuleInbound busiProfileCall)
    {
        Object idObj = busiProfileCall.getParams().get("id");
        if (idObj == null)
        {
            throw new SystemException(1002321, "Profile ID为空，删除出错！");
        }
        
        long fmeId = (Integer)busiProfileCall.getParams().get("fmeId");
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().get(fmeId);
        RestResponse result = fmeBridge.getInboundDialPlanRuleInvoker().deleteInboundDialPlanRule(idObj.toString());
        if (result.isSuccess())
        {
            // 删除内存中的入会方案
            fmeBridge.doFmeBridgeBusiness(FmeBridgeProcessingStrategy.TRAVERSE, new FmeBridgeAddpterProcessor()
            {
                public void process(FmeBridge fmeBridge)
                {
                    fmeBridge.getDataCache().deleteInboundDialPlanRule(idObj.toString());
                }
            });
        }
        return result;
    }
}
