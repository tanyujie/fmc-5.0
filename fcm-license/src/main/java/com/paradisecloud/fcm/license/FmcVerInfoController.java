package com.paradisecloud.fcm.license;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.cache.LicenseCache;
import com.paradisecloud.fcm.dao.mapper.BusiOpsInfoMapper;
import com.paradisecloud.fcm.dao.model.BusiFme;
import com.paradisecloud.fcm.dao.model.BusiOpsInfo;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.invoker.JschInvoker;
import com.paradisecloud.fcm.fme.model.cms.OutboundDialPlanRule;
import com.paradisecloud.fcm.fme.model.response.outdialplan.OutboundDialPlanRulesResponse;
import com.paradisecloud.fcm.service.ops.OpsDataCache;
import com.paradisecloud.smc.dao.model.BusiSmcHistoryConference;
import com.paradisecloud.smc.service.IBusiSmcHistoryConferenceService;
import com.paradisecloud.smc.service.SmcParticipantsService;
import com.sinhy.spring.BeanFactory;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author nj
 * @date 2022/7/29 9:35
 */
@RestController
@RequestMapping("/license/info")
@Slf4j
public class FmcVerInfoController {

    /**
     * 证书subject
     */
    @Value("${license.subject}")
    private String subject;

    /**
     * 公钥别称
     */
    @Value("${license.publicAlias}")
    private String publicAlias;

    /**
     * 访问公钥库的密码
     */
    @Value("${license.storePass}")
    private String storePass;

    /**
     * 证书生成路径
     */
    @Value("${license.licensePath}")
    private String licensePath;

    /**
     * 证书生成路径
     */
    @Value("${license.serverInfoKey}")
    private static String key;


    public static final String LICENSE_PUBLIC_KEY = "TTC3XXfsFD25Vdf1";

    /**
     * 密钥库存储路径
     */
    @Value("${license.publicKeysStorePath}")
    private String publicKeysStorePath;

    @Value("${license.defaultLimit}")
    private int defaultLimit;


    @Resource
    private SmcParticipantsService smcParticipantsService;

    @Resource
    private IBusiSmcHistoryConferenceService smcHistoryConferenceService;




    /**
     * 获取服务器硬件信息
     * @param osName 操作系统类型，如果为空则自动判断
     */
    @RequestMapping(value = "/getServerInfos")
    public String getServerInfos(@RequestParam(value = "osName",required = false) String osName) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, IOException, InvalidKeyException {
        String last20 = getSn(osName);
        return last20;
    }

    public static String getSn(String osName) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {
        String opsVersion = ExternalConfigCache.getInstance().getOpsVersion();
        if(Objects.equals("windows",opsVersion)){
            BusiOpsInfoMapper busiOpsInfoMapper = BeanFactory.getBean(BusiOpsInfoMapper.class);
            List<BusiOpsInfo> busiOpsInfos = busiOpsInfoMapper.selectBusiOpsInfoList(new BusiOpsInfo());
            if(!CollectionUtils.isEmpty(busiOpsInfos)){
              return   busiOpsInfos.get(0).getSn();
            }
        }
        if(StringUtils.isBlank(osName)){
            osName = System.getProperty("os.name");
        }
        osName = osName.toLowerCase();
        log.info("osName info :"+ osName);
        AbstractServerInfos abstractServerInfos = null;
        if (osName.startsWith("windows")) {
            abstractServerInfos = new WindowsServerInfos();
        } else if (osName.startsWith("linux")) {
            abstractServerInfos = new LinuxServerInfos();
        }else{//其他服务器类型
            abstractServerInfos = new LinuxServerInfos();
        }
        String content = JSONObject.toJSONString(abstractServerInfos.getServerInfos());
        log.info("server info :"+content);
        String s = AESUtil.encryptAESByte2HexStr(content, LICENSE_PUBLIC_KEY);
        log.info("server info encryptAESByte2HexStr :"+s);
        // 获取字符串的后20位
        String last20 = s.length() > 40 ? s.substring(s.length() - 40) : s;
        return last20;
    }

    /**
     * 获取服务器校验信息
     */
    @RequestMapping(value = "/getTermianlInfos")
    public Map<String, Object> getServerInfos() throws Exception {
        HashMap<String, Object> obj = new HashMap<>();
        LicenseContent licenseContent=null;
        try {
            LicenseManager licenseManager = LicenseManagerHolder.getInstance(null);
            licenseContent = licenseManager.verify();
        } catch (Exception e) {

        }
        if(licenseContent==null){
            obj.put("licenseContent",null);
            return obj;
        }else {
            Map extra = (Map<String, Object>) licenseContent.getExtra();
            if (OpsDataCache.getInstance().getCloudLiveTime() != 0) {
                extra.put("cloudLiveTime", OpsDataCache.getInstance().getCloudLiveTime());
            }
            if (OpsDataCache.getInstance().getAsrTime() != 0) {
                extra.put("asrTime", OpsDataCache.getInstance().getAsrTime());
            }
            if (OpsDataCache.getInstance().getTencentTime() != 0) {
                extra.put("tencentTime", OpsDataCache.getInstance().getTencentTime());
            }
            if (OpsDataCache.getInstance().getImTime() != 0) {
                extra.put("imTime", OpsDataCache.getInstance().getImTime());
            }
            obj.put("licenseContent",extra);
        }
        return obj;
    }


    /**
     * 获取服务器校验信息
     */
    @RequestMapping(value = "/getTermianlInfos/smc")
    public Map<String, Object> getSmcServerInfos() throws Exception {

        HashMap<String, Object> obj = new HashMap<>();

        LicenseVerify licenseVerify = new LicenseVerify();
        boolean verify = licenseVerify.verify();
        if(!verify){
            HashMap<String, Object> extraR = new HashMap<>();
            extraR.put("smcParticipantLimit", 0);
            extraR.put("smcSchedule", false);
            extraR.put("monitor", false);
            extraR.put("smcParticipantLimitTime",new Date());
            obj.put("licenseContent",extraR);
            return obj;
        }

        LicenseManager licenseManager = LicenseManagerHolder.getInstance(null);
        LicenseContent licenseContent = null;
        try {
            licenseContent = licenseManager.verify();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(licenseContent==null){
            HashMap<String, Object> extraR = new HashMap<>();
            extraR.put("smcParticipantLimit", 0);
            extraR.put("smcSchedule", false);
            extraR.put("monitor", false);
            extraR.put("smcParticipantLimitTime",new Date());
            obj.put("licenseContent",extraR);
        }else {
            Map extra = (Map<String, Object>) licenseContent.getExtra();
            HashMap<String, Object> extraR = new HashMap<>();
            extraR.put("smcParticipantLimit", extra.get("smcParticipantLimit"));
            extraR.put("smcSchedule", extra.get("smcSchedule"));
            extraR.put("monitor", extra.get("monitor"));
            extraR.put("smcParticipantLimitTime",extra.get("smcParticipantLimitTime"));
            extraR.put("smcMonitorLimitTime",extra.get("smcMonitorLimitTime"));
            obj.put("licenseContent",extraR);
        }

        return obj;
    }


    @RequestMapping(value = "/installLicense")
    public  RestResponse install(){
        if(StringUtils.isNotBlank(licensePath)){
            try {
                log.info("++++++++ 开始安装证书 ++++++++");
                LicenseVerifyParam param = new LicenseVerifyParam();
                param.setSubject(subject);
                param.setPublicAlias(publicAlias);
                param.setStorePass(storePass);
                param.setLicensePath(licensePath);
                param.setPublicKeysStorePath(publicKeysStorePath);
                param.setDefaultLimit(defaultLimit);
                LicenseVerify licenseVerify = new LicenseVerify();
                //安装证书
                LicenseContent content = licenseVerify.install(param);
                if(content==null){
                    return RestResponse.fail("证书安装失败");
                }
                LicenseExecutor.limitScheduledExecutorService(content,defaultLimit);
                log.info("++++++++ 证书安装结束 ++++++++");
                return RestResponse.success();
            } catch (Exception e) {
                e.printStackTrace();
                return RestResponse.fail("证书安装失败");
            }
        }
        return RestResponse.fail();
    }


    public  boolean installLicense(){
        if(StringUtils.isNotBlank(licensePath)){
            try {
                log.info("++++++++ 开始安装证书 ++++++++");
                LicenseVerifyParam param = new LicenseVerifyParam();
                param.setSubject(subject);
                param.setPublicAlias(publicAlias);
                param.setStorePass(storePass);
                param.setLicensePath(licensePath);
                param.setPublicKeysStorePath(publicKeysStorePath);
                param.setDefaultLimit(defaultLimit);
                LicenseVerify licenseVerify = new LicenseVerify();
                //安装证书
                LicenseContent content = licenseVerify.install(param);
                if(content==null){
                    return false;
                }
                LicenseExecutor.limitScheduledExecutorService(content,defaultLimit);
                log.info("++++++++ 证书安装结束 ++++++++");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public  boolean installLicense(String filePath){
        if(StringUtils.isNotBlank(filePath)){
            try {
                log.info("++++++++ 开始安装证书 ++++++++");
                LicenseVerifyParam param = new LicenseVerifyParam();
                param.setSubject(subject);
                param.setPublicAlias(publicAlias);
                param.setStorePass(storePass);
                param.setLicensePath(filePath);
                param.setPublicKeysStorePath(publicKeysStorePath);
                param.setDefaultLimit(defaultLimit);
                LicenseVerify licenseVerify = new LicenseVerify();
                //安装证书
                LicenseContent content = licenseVerify.install(param);
                if(content==null){
                    return false;
                }
                log.info("++++++++ 证书安装结束 ++++++++");
                try {

                    LicenseExecutor.limitScheduledExecutorService(content,0);
                    Map extra = (Map) content.getExtra();
                    Integer conferenceLimit=(Integer)extra.get("conferenceLimit");
                    Integer recorderLimit=(Integer)extra.get("recorderLimit");
                    boolean localRecoder=(boolean)extra.get("localRecoder");
                    boolean useRecorderLimit=(boolean)extra.get("useRecorderLimit");
                    Integer useableSpace=(Integer)extra.get("useableSpace");
                    String termianlType=(String)extra.get("termianlType");

                    LicenseCache.getInstance().setTermianlType(termianlType);
                    LicenseCache.getInstance().setUseableSpace(useableSpace);
                    LicenseCache.getInstance().setLocalRecoder(localRecoder);
                    LicenseCache.getInstance().setConferenceLimit(conferenceLimit);
                    LicenseCache.getInstance().setRecorderLimit(recorderLimit);
                    LicenseCache.getInstance().setUseRecorderLimit(useRecorderLimit);

                    String streamQuality=(String)extra.get("streamQuality");
                    Integer tencentTime=(Integer)extra.get("tencentTime");
                    Integer asrTime=(Integer)extra.get("asrTime");
                    Integer cloudLiveTime=(Integer)extra.get("cloudLiveTime");
                    LicenseCache.getInstance().setTencentTime(tencentTime);
                    LicenseCache.getInstance().setAsrTime(asrTime);
                    LicenseCache.getInstance().setStreamQuality(streamQuality);
                    LicenseCache.getInstance().setCloudLiveTime(cloudLiveTime);
                    LicenseExecutor.streamerAndRecorderSet(extra);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * license文件的上传
     *
     * @param uploadFile
     * @throws Exception
     */
    @PostMapping("/licenseFilesUpload")
    public RestResponse appFilesUpload(@RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile) {
        if (uploadFile != null && !uploadFile.isEmpty()) {
            // 保存
            try {
                String url = LicenseFileUtil.saveFile(uploadFile);
                boolean aBoolean = installLicense(url);
                if(aBoolean){
                    return RestResponse.success(url);
                }else {
                    return RestResponse.fail("证书安装失败");
                }

            } catch (Exception e) {
                if (e instanceof CustomException) {
                    return RestResponse.fail(e.getMessage());
                } else {
                    return RestResponse.fail("证书文件上传错误");
                }
            }
        }
        return RestResponse.fail();
    }
    @PutMapping("/update/{limit}")
    public  void updateLimit(@PathVariable(value = "limit") int limit){
        List<FmeBridge> fmeBridges = FmeBridgeCache.getInstance().getFmeBridges();
        int sublimit=1;
        if(fmeBridges.size()>1){
           sublimit=Math.round(limit/fmeBridges.size());
        }
        for (FmeBridge fmeBridge : fmeBridges) {
            if(fmeBridge.isAvailable()){
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("participantLimit",String.valueOf(sublimit)));
                fmeBridge.getClusterConfigInvoker().updateSystemConfigurationCluster(nameValuePairs);
            }
        }

    }

    @PutMapping("/streamer")
    public  void streamer(){
        List<FmeBridge> fmeBridges = FmeBridgeCache.getInstance().getFmeBridges();
        for (FmeBridge fmeBridge : fmeBridges) {
            OutboundDialPlanRulesResponse outboundDialPlanRules = fmeBridges.get(2).getOutboundDialPlanRuleInvoker().getOutboundDialPlanRules(100);
            if(outboundDialPlanRules==null){
                fmeBridge.getJschInvoker().execCmd("streamer disable");
            }else {
                List<OutboundDialPlanRule> outboundDialPlanRule = outboundDialPlanRules.getOutboundDialPlanRules().getOutboundDialPlanRule();
                if(CollectionUtils.isEmpty(outboundDialPlanRule)){
                    return;
                }
                OutboundDialPlanRule outboundDialPlanRule1 = outboundDialPlanRule.stream().filter(c -> c.getDomain().equals("streamer.com")).max(Comparator.comparingInt(OutboundDialPlanRule::getPriority)).get();
                BusiFme busiFme = new BusiFme();
                busiFme.setAdminUsername(fmeBridge.getBusiFme().getAdminUsername());
                busiFme.setAdminPassword(fmeBridge.getBusiFme().getAdminPassword());
                String sipProxy = outboundDialPlanRule1.getSipProxy();
                int indexOf = sipProxy.indexOf(":");
                if(indexOf>0){
                    busiFme.setIp(sipProxy.substring(0, indexOf));
                }else {
                    busiFme.setIp(sipProxy);
                }
                JschInvoker jschInvoker = new JschInvoker(busiFme);
                jschInvoker.execCmd("streamer disable");
            }

        }


    }

    /**
     * license 参会者数量统计
     *
     * @throws Exception
     */
    @GetMapping("/licenseParticipantNumberCount")
    public RestResponse licenseParticipantNumberCount() {
        HashMap<String, Object> obj = new HashMap<>();
        obj.put("smcParticipantLimit",0);
        obj.put("useNum",0);
        LicenseContent licenseContent = null;
        try {
            LicenseManager licenseManager = LicenseManagerHolder.getInstance(null);
            licenseContent = licenseManager.verify();
        } catch (Exception e) {
           return RestResponse.success(obj);
        }

        Map extra = (Map<String, Object>) licenseContent.getExtra();
        int limit = (int) extra.get("smcParticipantLimit");
        obj.put("smcParticipantLimit",limit);
        AtomicInteger atomicInteger=new AtomicInteger();
        BusiSmcHistoryConference historyConference = new BusiSmcHistoryConference();
        historyConference.setEndStatus(2);
        List<BusiSmcHistoryConference> busiSmcHistoryConferences = smcHistoryConferenceService.selectBusiSmcHistoryConferenceList(historyConference);
        if(!CollectionUtils.isEmpty(busiSmcHistoryConferences)){
            // 活跃会议室的数量
            for (BusiSmcHistoryConference busiSmcHistoryConference : busiSmcHistoryConferences) {
                String conferenceId = busiSmcHistoryConference.getConferenceId();
                SmcParitipantsStateRep conferencesParticipantsState = smcParticipantsService.getConferencesParticipantsState(conferenceId, 0, 10000);

                List<SmcParitipantsStateRep.ContentDTO> content = conferencesParticipantsState.getContent();
                if (!CollectionUtils.isEmpty(content)) {
                    content.stream().forEach(m->{
                        atomicInteger.incrementAndGet();
                    });
                }
            }
        }
        obj.put("useNum",atomicInteger.get());
        return RestResponse.success(obj);
    }

}
