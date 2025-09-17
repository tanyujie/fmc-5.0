package com.paradisecloud.fcm.smc.cache.modle.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author nj
 * @date 2022/8/15 15:29
 */
@Configuration
public class ResttemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
