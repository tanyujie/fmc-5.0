package com.paradisecloud.fcm.service.im.config;

import com.paradisecloud.fcm.service.im.IMService;
import com.paradisecloud.fcm.service.im.TencentIM;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(IMProperties.class)
public class IMAutoConfiguration {

    private final IMProperties properties;

    public IMAutoConfiguration(IMProperties properties) {
        this.properties = properties;
    }

    @Bean
    public IMService imService() {
        IMService imService = new IMService();
        String active = this.properties.getActive();
        imService.setActive(active);
        if (active.equals("tencent")) {
            imService.setIm(tencentIM());
        }

        return imService;
    }

    @Bean
    public TencentIM tencentIM() {
        TencentIM tencentIM = new TencentIM();
        IMProperties.Tencent tencent = this.properties.getTencent();
        tencentIM.setSdkAppId(tencent.getSdkAppId());
        tencentIM.setSecretKey(tencent.getSecretKey());
        tencentIM.setAdminUserId(tencent.getAdminUserId());
        return tencentIM;
    }
}
