package com.paradisecloud.fcm.service.live.config;

import com.paradisecloud.fcm.service.live.LiveService;
import com.paradisecloud.fcm.service.live.TencentLive;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LiveProperties.class)
public class LiveAutoConfiguration {

    private final LiveProperties properties;

    public LiveAutoConfiguration(LiveProperties properties) {
        this.properties = properties;
    }

    @Bean
    public LiveService liveService() {
        LiveService liveService = new LiveService();
        String active = this.properties.getActive();
        liveService.setActive(active);
        liveService.setEnable(this.properties.isEnable());
        if (active.equals("tencent")) {
            liveService.setLive(tencentLive());
        }

        return liveService;
    }

    @Bean
    public TencentLive tencentLive() {
        TencentLive tencentLive = new TencentLive();
        LiveProperties.Tencent tencent = this.properties.getTencent();
        tencentLive.setSecretId(tencent.getSecretId());
        tencentLive.setSecretKey(tencent.getSecretKey());
        tencentLive.setAuthKey(tencent.getAuthKey());
        tencentLive.setRegion(tencent.getRegion());
        tencentLive.setDomainName(tencent.getDomainName());
        tencentLive.setAppName(tencent.getAppName());
        tencentLive.setPullDomainName(tencent.getPullDomainName());
        tencentLive.setPlaybackDomainName(tencent.getPlaybackDomainName());
        tencentLive.setNoticeSecretKey(tencent.getNoticeSecretKey());
        return tencentLive;
    }
}
