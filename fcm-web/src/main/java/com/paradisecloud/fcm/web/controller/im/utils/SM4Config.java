package com.paradisecloud.fcm.web.controller.im.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

@Configuration
public class SM4Config {
    // 从配置文件读取SM4密钥和偏移量
    @Value("${sm4.key}")
    private String sm4Key;

    @Value("${sm4.iv}")
    private String sm4Iv;

    /**
     * 替换默认的消息转换器，加入SM4处理逻辑
     */
    @Bean
    public RequestMappingHandlerAdapter customRequestMappingHandlerAdapter(
            RequestMappingHandlerAdapter adapter,
            RequestMappingHandlerMapping requestMappingHandlerMapping) { // 注入RequestMappingHandlerMapping

        List<HttpMessageConverter<?>> messageConverters = adapter.getMessageConverters();
        // 使用注入的RequestMappingHandlerMapping作为构造参数
        SM4HttpMessageConverter sm4Converter = new SM4HttpMessageConverter(sm4Key, sm4Iv, requestMappingHandlerMapping);
        messageConverters.add(0, sm4Converter);
        adapter.setMessageConverters(messageConverters);
        return adapter;
    }
}
