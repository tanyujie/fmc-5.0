/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : BusinessProperties.java
 * Package : com.sinhy.pnsp.common.springmodel
 * 
 * @author lilinhai
 * 
 * @since 2020-07-23 23:24
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.common.business;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <pre>业务属性</pre>
 * 
 * @author lilinhai
 * @since 2020-07-23 23:24
 * @version V1.0
 */
@Component
@ConfigurationProperties(prefix = "business")
public class BusinessProperties
{
    
    /**
     * 缓存类型
     */
    private CacheType cacheType = CacheType.JAVA;
    
    /**
     * 老会控的ip地址根
     */
    private String oldFcmBaseAddress;
    
    /**
     * license文件路径
     */
    private String licenseFilePath;
    
    /**
     * mqtt消息推送截流时间间隔（毫秒）
     */
    private long mqttMsgSentInterceptInterval;
    
    /**
     * <p>
     * Get Method : cacheType CacheType
     * </p>
     * 
     * @return cacheType
     */
    public CacheType getCacheType()
    {
        return cacheType;
    }
    
    /**
     * <p>
     * Set Method : cacheType CacheType
     * </p>
     * 
     * @param cacheType
     */
    public void setCacheType(CacheType cacheType)
    {
        this.cacheType = cacheType;
    }
    
    /**
     * <p>Get Method   :   oldFcmBaseAddress String</p>
     * @return oldFcmBaseAddress
     */
    public String getOldFcmBaseAddress()
    {
        return oldFcmBaseAddress;
    }

    /**
     * <p>Set Method   :   oldFcmBaseAddress String</p>
     * @param oldFcmBaseAddress
     */
    public void setOldFcmBaseAddress(String oldFcmBaseAddress)
    {
        this.oldFcmBaseAddress = oldFcmBaseAddress;
    }

    /**
     * <p>Get Method   :   licenseFilePath String</p>
     * @return licenseFilePath
     */
    public String getLicenseFilePath()
    {
        return licenseFilePath;
    }

    /**
     * <p>Set Method   :   licenseFilePath String</p>
     * @param licenseFilePath
     */
    public void setLicenseFilePath(String licenseFilePath)
    {
        this.licenseFilePath = licenseFilePath;
    }
    
    /**
     * <p>Get Method   :   mqttMsgSentInterceptInterval long</p>
     * @return mqttMsgSentInterceptInterval
     */
    public long getMqttMsgSentInterceptInterval()
    {
        return mqttMsgSentInterceptInterval;
    }

    /**
     * <p>Set Method   :   mqttMsgSentInterceptInterval long</p>
     * @param mqttMsgSentInterceptInterval
     */
    public void setMqttMsgSentInterceptInterval(long mqttMsgSentInterceptInterval)
    {
        this.mqttMsgSentInterceptInterval = mqttMsgSentInterceptInterval;
    }



    public static enum CacheType
    {
        /**
         * java内存做的缓存
         */
        JAVA(1, "Java缓存"),
        
        /**
         * redis做的缓存
         */
        REDIS(2, "Redis缓存");
        
        /**
         * 枚举值
         */
        private int value;
        
        /**
         * 缓存名
         */
        private String name;
        
        /**
         * <pre>构造方法</pre>
         * 
         * @author lilinhai
         * @since 2020-12-08 09:51
         * @param value
         * @param name
         */
        private CacheType(int value, String name)
        {
            this.value = value;
            this.name = name;
        }
        
        /**
         * <p>
         * Get Method : value int
         * </p>
         * 
         * @return value
         */
        public int getValue()
        {
            return value;
        }
        
        /**
         * <p>
         * Get Method : name String
         * </p>
         * 
         * @return name
         */
        public String getName()
        {
            return name;
        }
        
    }
}
