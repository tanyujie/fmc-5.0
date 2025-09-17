package com.paradisecloud.fcm.license;


import com.paradisecloud.fcm.common.cache.CommonConfigCache;
import com.paradisecloud.fcm.common.cache.LicenseCache;
import de.schlichtherle.license.LicenseContent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * @author nj
 * @date 2022/7/26 11:06
 */
@Component
@Slf4j
public class LicenseCheckListener implements ApplicationListener<ContextRefreshedEvent> {

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
     * 密钥库存储路径
     */
    @Value("${license.publicKeysStorePath}")
    private String publicKeysStorePath;

    @Value("${license.defaultLimit}")
    private int defaultLimit;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("===========================================================LICENSE INIT+ ++++++++");
        try {
            LicenseCache.getInstance().setSn(FmcVerInfoController.getSn(null));
        } catch (Exception e) {
        }
        install();

    }

    public  void install() {
        try {
            if(StringUtils.isNotBlank(licensePath)){
                log.info("++++++++ 开始安装证书 ++++++++");
                LicenseVerifyParam param = getLicenseVerifyParam();
                LicenseVerify licenseVerify = new LicenseVerify();
                //安装证书
                LicenseContent content = licenseVerify.install(param);
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

                    String streamQuality=(String)extra.get("streamQuality");
                    Integer tencentTime=(Integer)extra.get("tencentTime");
                    Integer asrTime=(Integer)extra.get("asrTime");
                    Integer cloudLiveTime=(Integer)extra.get("cloudLiveTime");
                    LicenseCache.getInstance().setTencentTime(tencentTime);
                    LicenseCache.getInstance().setAsrTime(asrTime);
                    LicenseCache.getInstance().setStreamQuality(streamQuality);

                    LicenseCache.getInstance().setTermianlType(termianlType);
                    LicenseCache.getInstance().setUseableSpace(useableSpace);
                    LicenseCache.getInstance().setLocalRecoder(localRecoder);
                    LicenseCache.getInstance().setConferenceLimit(conferenceLimit);
                    LicenseCache.getInstance().setRecorderLimit(recorderLimit);
                    LicenseCache.getInstance().setUseRecorderLimit(useRecorderLimit);
                    LicenseCache.getInstance().setCloudLiveTime(cloudLiveTime);


                    LicenseExecutor.streamerAndRecorderSet(extra);
                } catch (Exception e) {
                    log.info("++++++++ LicenseExecutor error ++++++++"+e.getMessage());
                }
                // 有License需求但没有授权时，初始化为默认值。无需License的项目则保持为空
                if (LicenseCache.getInstance().getUseableSpace() == null) {
                    LicenseCache.getInstance().setUseableSpace(0);
                }
                if (LicenseCache.getInstance().getLocalRecoder() == null) {
                    LicenseCache.getInstance().setLocalRecoder(false);
                }
                if (LicenseCache.getInstance().getConferenceLimit() == null) {
                    LicenseCache.getInstance().setConferenceLimit(0);
                }
                if (LicenseCache.getInstance().getRecorderLimit() == null) {
                    LicenseCache.getInstance().setRecorderLimit(0);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getDefaultLimit(){
        return defaultLimit;
    }

    private LicenseVerifyParam getLicenseVerifyParam() {
        LicenseVerifyParam param = new LicenseVerifyParam();
        param.setSubject(subject);
        param.setPublicAlias(publicAlias);
        param.setStorePass(storePass);
        param.setLicensePath(licensePath);
        param.setPublicKeysStorePath(publicKeysStorePath);
        param.setDefaultLimit(defaultLimit);
        return param;
    }


}
