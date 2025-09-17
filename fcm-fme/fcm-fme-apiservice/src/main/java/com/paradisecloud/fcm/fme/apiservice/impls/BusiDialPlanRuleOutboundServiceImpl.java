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
import com.paradisecloud.fcm.dao.model.BusiDialPlanRuleOutbound;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiDialPlanRuleOutboundService;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.enumer.FmeBridgeProcessingStrategy;
import com.paradisecloud.fcm.fme.model.cms.OutboundDialPlanRule;
import com.paradisecloud.fcm.fme.model.response.outdialplan.ActiveOutboundDialPlanRulesResponse;
import com.paradisecloud.fcm.fme.model.response.outdialplan.OutboundDialPlanRuleInfoResponse;
import com.paradisecloud.fcm.fme.model.response.outdialplan.OutboundDialPlanRulesResponse;
import com.sinhy.exception.SystemException;

/**
 * 外呼计划Service业务层处理
 * 
 * @author lilinhai
 * @date 2022-05-13
 */
@Service
public class BusiDialPlanRuleOutboundServiceImpl implements IBusiDialPlanRuleOutboundService 
{
    @Override
    public void syncAllPlan(FmeBridge fmeBridge, OutboundDialPlanRuleProcessor outboundDialPlanRuleProcessor)
    {
        int offset = 0;
        AtomicInteger totalCount = new AtomicInteger();
        while (true)
        {
            OutboundDialPlanRulesResponse response = fmeBridge.getOutboundDialPlanRuleInvoker().getOutboundDialPlanRules(offset);
            if (response != null)
            {
                ActiveOutboundDialPlanRulesResponse activeOutboundDialPlanRulesResponse = response.getOutboundDialPlanRules();
                if (activeOutboundDialPlanRulesResponse != null)
                {
                    List<OutboundDialPlanRule> callLegProfile = activeOutboundDialPlanRulesResponse.getOutboundDialPlanRule();
                    if (callLegProfile != null)
                    {
                        for (OutboundDialPlanRule callLegProfile2 : callLegProfile)
                        {
                            OutboundDialPlanRuleInfoResponse callLegProfileInfoResponse = fmeBridge.getOutboundDialPlanRuleInvoker().getOutboundDialPlanRule(callLegProfile2.getId());
                            if (callLegProfileInfoResponse != null && callLegProfileInfoResponse.getOutboundDialPlanRule() != null)
                            {
                                callLegProfile2 = callLegProfileInfoResponse.getOutboundDialPlanRule();
                                outboundDialPlanRuleProcessor.process(callLegProfile2);
                            }
                        }
                        
                        // 业务处理
                        Integer total = activeOutboundDialPlanRulesResponse.getTotal();
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
        List<OutboundDialPlanRule> ins = mainMaxPriorityFmeHttpInvoker.getDataCache().getOutboundDialPlanRules();
        for (OutboundDialPlanRule inboundDialPlanRule : ins)
        {
            ModelBean mb = new ModelBean(inboundDialPlanRule);
            mbs.add(mb);
        }
        return mbs;
    }

    /**
     * 新增外呼计划
     * 
     * @param busiDialPlanRuleOutbound 外呼计划
     * @return 结果
     */
    @Override
    public int insertBusiDialPlanRuleOutbound(BusiDialPlanRuleOutbound busiProfileCall)
    {
        long fmeId = (Integer)busiProfileCall.getParams().get("fmeId");
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().get(fmeId);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        buildParams(busiProfileCall, nameValuePairs);
        
        String profileId = fmeBridge.getOutboundDialPlanRuleInvoker().createOutboundDialPlanRule(nameValuePairs);
        if (ObjectUtils.isEmpty(profileId))
        {
            throw new SystemException(1001098, "创建外呼计划失败");
        }
        
        OutboundDialPlanRuleInfoResponse profileInfoResponse = fmeBridge.getOutboundDialPlanRuleInvoker().getOutboundDialPlanRule(profileId);
        if (profileInfoResponse != null && profileInfoResponse.getOutboundDialPlanRule() != null)
        {
            // 更新缓存
            fmeBridge.doFmeBridgeBusiness(FmeBridgeProcessingStrategy.TRAVERSE, new FmeBridgeAddpterProcessor()
            {
                public void process(FmeBridge fmeBridge)
                {
                    fmeBridge.getDataCache().update(profileInfoResponse.getOutboundDialPlanRule());
                }
            });
        }
        
        busiProfileCall.getParams().put("id", profileId);
        
        // 处理保存
        busiProfileCall.setCreateTime(new Date());
        busiProfileCall.setPlanUuid(profileId);
        return ObjectUtils.isEmpty(profileId) ? 0 : 1;
    }
    
    private void buildParams(BusiDialPlanRuleOutbound busiProfileCall, List<NameValuePair> nameValuePairs)
    {
        Assert.isTrue(busiProfileCall.getParams().containsKey("domain"), "domain不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("priority"), "priority不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("localContactDomain"), "localContactDomain不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("localFromDomain"), "localFromDomain不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("sipProxy"), "sipProxy不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("trunkType"), "trunkType不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("failureAction"), "failureAction不能为空");
        Assert.isTrue(busiProfileCall.getParams().containsKey("sipControlEncryption"), "sipControlEncryption不能为空");
//        Assert.isTrue(busiProfileCall.getParams().containsKey("scope"), "scope不能为空");
//        Assert.isTrue(busiProfileCall.getParams().containsKey("callRouting"), "callRouting不能为空");
        
        nameValuePairs.add(new BasicNameValuePair("domain", busiProfileCall.getParams().get("domain").toString()));
        nameValuePairs.add(new BasicNameValuePair("priority", busiProfileCall.getParams().get("priority").toString()));
        nameValuePairs.add(new BasicNameValuePair("localContactDomain", busiProfileCall.getParams().get("localContactDomain").toString()));
        nameValuePairs.add(new BasicNameValuePair("localFromDomain", busiProfileCall.getParams().get("localFromDomain").toString()));
        nameValuePairs.add(new BasicNameValuePair("sipProxy", busiProfileCall.getParams().get("sipProxy").toString()));
        nameValuePairs.add(new BasicNameValuePair("trunkType", busiProfileCall.getParams().get("trunkType").toString()));
        nameValuePairs.add(new BasicNameValuePair("failureAction", busiProfileCall.getParams().get("failureAction").toString()));
        nameValuePairs.add(new BasicNameValuePair("sipControlEncryption", busiProfileCall.getParams().get("sipControlEncryption").toString()));
//        nameValuePairs.add(new BasicNameValuePair("scope", busiProfileCall.getParams().get("scope").toString()));
//        nameValuePairs.add(new BasicNameValuePair("callRouting", busiProfileCall.getParams().get("callRouting").toString()));
    }

    /**
     * 修改外呼计划
     * 
     * @param busiDialPlanRuleOutbound 外呼计划
     * @return 结果
     */
    @Override
    public int updateBusiDialPlanRuleOutbound(BusiDialPlanRuleOutbound busiDialPlanRuleOutbound)
    {
        long fmeId = (Integer)busiDialPlanRuleOutbound.getParams().get("fmeId");
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().get(fmeId);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        buildParams(busiDialPlanRuleOutbound, nameValuePairs);
        OutboundDialPlanRule rule = fmeBridge.getDataCache().getOutboundDialPlanRule(busiDialPlanRuleOutbound.getParams().get("id").toString());
        if (rule == null)
        {
            throw new RuntimeException("记录不存在：" + busiDialPlanRuleOutbound.getParams().get("id").toString()); 
        }
        RestResponse restResponse = fmeBridge.getOutboundDialPlanRuleInvoker().updateOutboundDialPlanRule(busiDialPlanRuleOutbound.getParams().get("id").toString(), nameValuePairs);
        if (restResponse.isSuccess())
        {
            // 更新内存
            OutboundDialPlanRuleInfoResponse profileInfoResponse = fmeBridge.getOutboundDialPlanRuleInvoker().getOutboundDialPlanRule(busiDialPlanRuleOutbound.getParams().get("id").toString());
            if (profileInfoResponse != null && profileInfoResponse.getOutboundDialPlanRule() != null)
            {
                // 更新入会方案缓存
                fmeBridge.doFmeBridgeBusiness(FmeBridgeProcessingStrategy.TRAVERSE, new FmeBridgeAddpterProcessor()
                {
                    public void process(FmeBridge fmeBridge)
                    {
                        fmeBridge.getDataCache().update(profileInfoResponse.getOutboundDialPlanRule());
                    }
                });
            }
            
            return 1;
        }
        throw new SystemException(1009999, restResponse.getMessage());
    }

    public RestResponse deleteBusiProfileCallById(BusiDialPlanRuleOutbound busiProfileCall)
    {
        Object idObj = busiProfileCall.getParams().get("id");
        if (idObj == null)
        {
            throw new SystemException(1002321, "Profile ID为空，删除出错！");
        }
        
        long fmeId = (Integer)busiProfileCall.getParams().get("fmeId");
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().get(fmeId);
        OutboundDialPlanRule rule = fmeBridge.getDataCache().getOutboundDialPlanRule(idObj.toString());
        RestResponse result = RestResponse.success();
        if (rule != null)
        {
            result = fmeBridge.getOutboundDialPlanRuleInvoker().deleteOutboundDialPlanRule(idObj.toString());
            if (result.isSuccess())
            {
                // 删除内存中的入会方案
                fmeBridge.doFmeBridgeBusiness(FmeBridgeProcessingStrategy.TRAVERSE, new FmeBridgeAddpterProcessor()
                {
                    public void process(FmeBridge fmeBridge)
                    {
                        fmeBridge.getDataCache().deleteOutboundDialPlanRule(idObj.toString());
                    }
                });
            }
        }
    
        return result;
    }
}
