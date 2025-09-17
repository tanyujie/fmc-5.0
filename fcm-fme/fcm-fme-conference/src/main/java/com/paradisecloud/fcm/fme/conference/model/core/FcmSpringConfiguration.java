/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : FcmSpringConfiguration.java
 * Package : com.paradisecloud.fcm.fme.conference.model.core
 * 
 * @author sinhy
 * 
 * @since 2021-09-20 22:47
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.fme.conference.model.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

/**
 * <pre>请加上该类的描述</pre>
 * 
 * @author sinhy
 * @since 2021-09-20 22:47
 * @version V1.0
 */
@Configuration
public class FcmSpringConfiguration
{
    
    @Bean
    WebSecurityCustomizer webSecurityCustomizery36427534()
    {
        return (web) -> {
            web.ignoring().antMatchers(
                    "/external/api/**",
                    "/busi/cdr/**",
                    "/client-message-entry/**",
                    "/conference-debugger-**/**",
                    "/fmm-api/**","mobile/user/login");
        };
    }
    
}
