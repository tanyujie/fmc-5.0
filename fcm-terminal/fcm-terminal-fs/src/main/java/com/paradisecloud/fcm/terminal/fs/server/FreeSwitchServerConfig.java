package com.paradisecloud.fcm.terminal.fs.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FreeSwitchServerConfig {

    @Bean
    public FreeSwitchServerPool freeSwitchServerPool() {
        return new FreeSwitchServerPool();
    }
}
