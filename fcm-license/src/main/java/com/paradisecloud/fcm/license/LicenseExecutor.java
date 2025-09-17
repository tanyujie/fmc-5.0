package com.paradisecloud.fcm.license;


import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiOpsInfoMapper;
import com.paradisecloud.fcm.dao.model.BusiFme;
import com.paradisecloud.fcm.dao.model.BusiFmeCluster;
import com.paradisecloud.fcm.dao.model.BusiOpsInfo;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.FmeClusterCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.FmeBridgeCluster;
import com.paradisecloud.fcm.fme.cache.model.invoker.JschInvoker;
import com.paradisecloud.fcm.fme.model.cms.CallProfile;
import com.paradisecloud.fcm.fme.model.cms.OutboundDialPlanRule;
import com.paradisecloud.fcm.fme.model.response.callprofile.CallProfilesResponse;
import com.paradisecloud.fcm.fme.model.response.outdialplan.OutboundDialPlanRuleInfoResponse;
import com.paradisecloud.fcm.fme.model.response.outdialplan.OutboundDialPlanRulesResponse;
import com.paradisecloud.fcm.license.task.SshRemoteServerOperateForOpsQuality;
import com.paradisecloud.fcm.terminal.fs.util.SpringContextUtil;
import com.sinhy.spring.BeanFactory;
import de.schlichtherle.license.LicenseContent;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author nj
 * @date 2022/8/1 14:49
 */
public class LicenseExecutor  {

    private static Logger logger= LoggerFactory.getLogger(LicenseExecutor.class);
    /**
     * 定时任务
     */
    public static void limitScheduledExecutorService(LicenseContent content,int limit) {

        Map extra = (Map) content.getExtra();
        boolean schedule = (boolean) extra.get("schedule");
        if (schedule) {
            String participantLimitTime = (String) extra.get("participantLimitTime");
            long willTime = DateUtil.convertDateByString(participantLimitTime, null).getTime();
            if (System.currentTimeMillis() - willTime >= 0) {
                execParticipantLimit(0);
            }else {
                limitParti(content);
            }
        } else {
            limitParti(content);
        }
    }

    private static void limitParti(LicenseContent content) {

        Environment environment = SpringContextUtil.getApplicationContext().getEnvironment();
        String activeProfile = environment.getActiveProfiles()[0];
        if(!Objects.equals("prod",activeProfile)){
            return ;
        }

        Map extra = (Map) content.getExtra();
        int limit = (int)extra.get("participantLimit");
        execParticipantLimit(limit);

    }

    private static void streamerAndRecorderStatus(Map extra) {
        boolean recorder = (boolean) extra.get("recorder");
        boolean streamer = (boolean) extra.get("streamer");
        streamerAndRecorderStatus(recorder,streamer);
    }

    private static void streamerAndRecorderStatus( boolean recorder, boolean streamer) {
        if (recorder) {
            streamerOrRecorderCmd("recorder.com", "recorder enable");
        } else {
            streamerOrRecorderCmd("recorder.com", "recorder disable");
        }
        if (streamer) {
            streamerOrRecorderCmd("streamer.com", "streamer enable");
        } else {
            streamerOrRecorderCmd("streamer.com", "streamer disable");
        }
    }

    public static void streamerAndRecorderSet(Map extra) {

        String streamQuality=(String)extra.get("streamQuality");
        String recordQuality=(String)extra.get("recordQuality");
        if(Strings.isBlank(streamQuality)){
             streamQuality="720p";
        }
        if(Strings.isBlank(recordQuality)){
            recordQuality="720p";
        }

        BusiOpsInfoMapper busiOpsInfoMapper = BeanFactory.getBean(BusiOpsInfoMapper.class);
        BusiOpsInfo busiOpsInfo = new BusiOpsInfo();

        List<BusiOpsInfo> busiOpsInfos = busiOpsInfoMapper.selectBusiOpsInfoList(busiOpsInfo);
        if(org.apache.commons.collections4.CollectionUtils.isEmpty(busiOpsInfos)){
            return;
        }
        BusiOpsInfo busiOpsInfo1 = busiOpsInfos.get(0);
        String fmeIp = busiOpsInfo1.getFmeIp();
        if(Strings.isBlank(fmeIp)){
            return;
        }


        SshRemoteServerOperateForOpsQuality sshRemoteServerOperate_fme = SshRemoteServerOperateForOpsQuality.getInstance();
        try {
            sshRemoteServerOperate_fme.sshRemoteCallLogin(fmeIp, "ttadmin", "tTcl0uds@cn", 22);
            boolean logined_fme = sshRemoteServerOperate_fme.isLogined();
            if (logined_fme) {
                sshRemoteServerOperate_fme.execCommand("recorder disable");
                Threads.sleep(500);
                sshRemoteServerOperate_fme.execCommand("recorder sip listen a 6000 6001");
                Threads.sleep(500);
                sshRemoteServerOperate_fme.execCommand("recorder sip certs dbs.key dbs.crt root.crt");
                Threads.sleep(500);
                sshRemoteServerOperate_fme.execCommand("recorder nfs " + busiOpsInfo1.getIpAddress() + ":/mnt/nfs");
                Threads.sleep(500);
                sshRemoteServerOperate_fme.execCommand("recorder resolution "+recordQuality);
                Threads.sleep(500);
                String recorder_enable = sshRemoteServerOperate_fme.execCommand("recorder enable");
                logger.info("修改FME-resolution recorder_enable=============================================="+recorder_enable);
                Threads.sleep(500);
                sshRemoteServerOperate_fme.execCommand("streamer disable");
                Threads.sleep(500);
                sshRemoteServerOperate_fme.execCommand("streamer sip listen a 7000 7000");
                Threads.sleep(500);
                sshRemoteServerOperate_fme.execCommand("streamer sip certs dbs.key dbs.crt root.crt");
                Threads.sleep(500);
                sshRemoteServerOperate_fme.execCommand("streamer sip resolution "+streamQuality);
                Threads.sleep(500);
                String streamer_enable = sshRemoteServerOperate_fme.execCommand("streamer enable");
                logger.info("修改FME-resolution streamer_enable=============================================="+streamer_enable);
                logger.info("修改FME-resolution成功=============================================="+streamQuality,recordQuality);
            } else {
                logger.info("修改FME-resolution登录失败：");
            }
        } catch (Exception e) {
            logger.info("修改FME-resolution登录失败登录失败：");
        } finally {
            sshRemoteServerOperate_fme.closeSession();
        }


    }

    /**
     * 路数限制
     * @param limit
     */
    public static void execParticipantLimit(int limit) {

        if (limit < 0) {
            participantLimitExec(FmeBridgeCache.getInstance().getFmeBridges(),"");
        } else if (limit == 0) {
            participantLimitExec(FmeBridgeCache.getInstance().getFmeBridges(),"0");
        } else {
            participantLimitExec(FmeBridgeCache.getInstance().getFmeBridges(),limit+"");


            Collection<BusiFmeCluster> values =  FmeClusterCache.getInstance().values();
            if (CollectionUtils.isEmpty(values)) {
                return;
            }
            for (BusiFmeCluster value : values) {
                FmeBridgeCluster fmeBridgeCluster =FmeBridgeCache.getInstance().getByFmeClusterId(value.getId());
                if(fmeBridgeCluster == null){
                    return;
                }
                List<FmeBridge> fmeBridges = fmeBridgeCluster.getFmeBridges();
                int sublimit = 1;
                int mod = 0;
                if (fmeBridges.size() >= 1) {
                    sublimit = Math.round(limit / fmeBridges.size());
                    mod = Math.floorMod(limit, fmeBridges.size());
                }
                int[] ints = new int[fmeBridges.size()];
                for (int i = 0; i < fmeBridges.size(); i++) {
                    ints[i] = sublimit;
                    if (i == fmeBridges.size() - 1) {
                        ints[i] = sublimit + mod;
                    }
                }
                for (int i = 0; i < fmeBridges.size(); i++) {
                    if (fmeBridges.get(i).isAvailable()) {
                        List<NameValuePair> nameValuePairs = new ArrayList<>();
                        nameValuePairs.add(new BasicNameValuePair("participantLimit", String.valueOf(ints[i])));
                        fmeBridges.get(i).getClusterConfigInvoker().updateSystemConfigurationCluster(nameValuePairs);
                    }
                }
            }

        }

    }

    public static void participantLimitExec(List<FmeBridge> fmeBridges,String limit) {
        if(CollectionUtils.isEmpty(fmeBridges)){
            return;
        }
        fmeBridges.forEach(fmeBridge -> {
            if (fmeBridge.isAvailable()) {
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("participantLimit", limit));
                fmeBridge.getClusterConfigInvoker().updateSystemConfigurationCluster(nameValuePairs);

            }
        });
    }

    /**
     *
     */
    public static void callProfileLimitInit(int limit) {
        List<FmeBridge> fmeBridges = FmeBridgeCache.getInstance().getFmeBridges();
        try {
            for (FmeBridge fmeBridge : fmeBridges) {
                CallProfilesResponse callProfiles = fmeBridge.getCallProfileInvoker().getCallProfiles(0);
                if (callProfiles == null) {
                    return;
                }
                List<CallProfile> callProfile = callProfiles.getCallProfiles().getCallProfile();
                if (CollectionUtils.isEmpty(callProfile)) {
                    return;
                }
                for (CallProfile profile : callProfile) {
                    List<NameValuePair> nameValuePairs = new ArrayList<>();

                    if(limit<0){
                        nameValuePairs.add(new BasicNameValuePair("participantLimit", ""));
                    }else {
                        nameValuePairs.add(new BasicNameValuePair("participantLimit", String.valueOf(limit)));
                    }
                    fmeBridge.getCallProfileInvoker().updateCallProfile(profile.getId(), nameValuePairs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * streamer disable
     * streamer.com
     * 直播 录制
     */
    public static void streamerOrRecorderCmd(String type, String cmd) {
        List<FmeBridge> fmeBridges = FmeBridgeCache.getInstance().getFmeBridges();
        for (FmeBridge fmeBridge : fmeBridges) {
            OutboundDialPlanRulesResponse outboundDialPlanRules = fmeBridge.getOutboundDialPlanRuleInvoker().getOutboundDialPlanRules(0);
            if (outboundDialPlanRules == null) {
                fmeBridge.getJschInvoker().execCmd(cmd);
            } else {
                List<OutboundDialPlanRule> outboundDialPlanRule = outboundDialPlanRules.getOutboundDialPlanRules().getOutboundDialPlanRule();
                if (CollectionUtils.isEmpty(outboundDialPlanRule)) {
                    return;
                }
                OutboundDialPlanRule outboundDialPlanRule1 = outboundDialPlanRule.stream().filter(c -> c.getDomain().equals(type)).max(Comparator.comparingInt(OutboundDialPlanRule::getPriority)).get();
                if(Objects.isNull(outboundDialPlanRule1)){
                    return;
                }
                OutboundDialPlanRuleInfoResponse outboundDialPlanRule2 = fmeBridge.getOutboundDialPlanRuleInvoker().getOutboundDialPlanRule(outboundDialPlanRule1.getId());
                if(Objects.isNull(outboundDialPlanRule2)){
                    return;
                }
                BusiFme busiFme = new BusiFme();
                busiFme.setAdminUsername(fmeBridge.getBusiFme().getAdminUsername());
                busiFme.setAdminPassword(fmeBridge.getBusiFme().getAdminPassword());
                String sipProxy = outboundDialPlanRule2.getOutboundDialPlanRule().getSipProxy();
                if(Strings.isNotBlank(sipProxy)){
                    int indexOf = sipProxy.indexOf(":");
                    if (indexOf > 0) {
                        busiFme.setIp(sipProxy.substring(0, indexOf));
                    } else {
                        busiFme.setIp(sipProxy);
                    }
                    JschInvoker jschInvoker = new JschInvoker(busiFme);
                    jschInvoker.execCmd(cmd);
                }

            }

        }

    }



}
