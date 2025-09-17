package com.paradisecloud.fcm.ops;

import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.cms.OutboundDialPlanRule;
import com.paradisecloud.fcm.fme.model.response.outdialplan.ActiveOutboundDialPlanRulesResponse;
import com.paradisecloud.fcm.fme.model.response.outdialplan.OutboundDialPlanRulesResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author nj
 * @date 2024/5/27 10:11
 */
public class FmeOutboundDialPlanRulesConfigModifier {


    public static void modify(FmeBridge fmeBridge) {
        if(fmeBridge==null||!fmeBridge.isAvailable()){
            return;
        }
        try {

            OutboundDialPlanRulesResponse outboundDialPlanRules = fmeBridge.getOutboundDialPlanRuleInvoker().getOutboundDialPlanRules(0);
            if (outboundDialPlanRules == null) {
                //新增配置
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                recorderBuildParams(nameValuePairs, fmeBridge.getBridgeAddress());
                String outboundDialPlanRule = fmeBridge.getOutboundDialPlanRuleInvoker().createOutboundDialPlanRule(nameValuePairs);

                List<NameValuePair> nameValuePairs_stream = new ArrayList<>();
                streamerBuildParams(nameValuePairs_stream, fmeBridge.getBridgeAddress());

                String outboundDialPlanRule_stream = fmeBridge.getOutboundDialPlanRuleInvoker().createOutboundDialPlanRule(nameValuePairs_stream);


            } else {
                ActiveOutboundDialPlanRulesResponse outboundDialPlanRulesResponse = outboundDialPlanRules.getOutboundDialPlanRules();

                if (outboundDialPlanRulesResponse.getTotal() > 0) {
                    //修改配置
                    List<OutboundDialPlanRule> outboundDialPlanRule = outboundDialPlanRulesResponse.getOutboundDialPlanRule();
                    for (OutboundDialPlanRule dialPlanRule : outboundDialPlanRule) {

                        String domain = dialPlanRule.getDomain();

                        if (Objects.equals("recorder.com", domain)) {
                            List<NameValuePair> nameValuePairs = new ArrayList<>();
                            recorderBuildParams(nameValuePairs, fmeBridge.getBusiFme().getIp());
                            fmeBridge.getOutboundDialPlanRuleInvoker().updateOutboundDialPlanRule(dialPlanRule.getId(), nameValuePairs);
                        }

                        if (Objects.equals("streamer.com", domain)) {
                            List<NameValuePair> nameValuePairs = new ArrayList<>();
                            streamerBuildParams(nameValuePairs, fmeBridge.getBusiFme().getIp());
                            fmeBridge.getOutboundDialPlanRuleInvoker().updateOutboundDialPlanRule(dialPlanRule.getId(), nameValuePairs);
                        }
                    }

                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void recorderBuildParams(List<NameValuePair> nameValuePairs, String fmeIp) {
        nameValuePairs.add(new BasicNameValuePair("domain", "recorder.com"));
        nameValuePairs.add(new BasicNameValuePair("priority", "3000"));
        nameValuePairs.add(new BasicNameValuePair("localContactDomain", ""));
        nameValuePairs.add(new BasicNameValuePair("localFromDomain", ""));
        nameValuePairs.add(new BasicNameValuePair("sipProxy", fmeIp + ":6000"));
        nameValuePairs.add(new BasicNameValuePair("trunkType", "sip"));
        nameValuePairs.add(new BasicNameValuePair("failureAction", "stop"));
        nameValuePairs.add(new BasicNameValuePair("sipControlEncryption", "unencrypted"));
        nameValuePairs.add(new BasicNameValuePair("scope", "global"));
        nameValuePairs.add(new BasicNameValuePair("callRouting", "default"));
    }

    public static void streamerBuildParams(List<NameValuePair> nameValuePairs, String fmeIp) {
        nameValuePairs.add(new BasicNameValuePair("domain", "streamer.com"));
        nameValuePairs.add(new BasicNameValuePair("priority", "2000"));
        nameValuePairs.add(new BasicNameValuePair("sipProxy", fmeIp + ":7000"));
        nameValuePairs.add(new BasicNameValuePair("trunkType", "sip"));
        nameValuePairs.add(new BasicNameValuePair("failureAction", "stop"));
        nameValuePairs.add(new BasicNameValuePair("sipControlEncryption", "unencrypted"));
        nameValuePairs.add(new BasicNameValuePair("scope", "global"));
        nameValuePairs.add(new BasicNameValuePair("callRouting", "default"));
    }


}
