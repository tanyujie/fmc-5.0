package com.paradisecloud.fcm.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

/**
 * @author nj
 * @date 2022/6/15 12:27
 */
@Configuration
public class WebSecurityConfig {


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/mobile/user/login","/user/login","/user/loginAuto","/tencent/event/**","/test/**",
                "/busi/terminal/appupdate/check","/license/**","/ops/loginAuto","/busi/system/about","/busi/terminalAction/logsUploadMulti","/mobile/user/getToken",
                "/mobile/server/getServerInfo","/mobileWeb/signIn","/mobileWeb/signIn/list","/mobile/user/loginAuto","/mobile/room/**","/mobile/roomBook/**",
                "/busi/meetingFile/callback","/mobileWeb/captchaImage","/mobileWeb/user/**","/busi/srs/v1/streams","/ops/mcuStatus","/user/loginNoCode",
                "/test/server/**","/ops/firstInit","/ops/initCheck", "/busi/history/downHistoryExcel/**", "/busi/mcu/all/conference/getSimpleConferenceInfo",
                "/busi/recording/download/**", "/test/server/**","/im/**");
    }


}
