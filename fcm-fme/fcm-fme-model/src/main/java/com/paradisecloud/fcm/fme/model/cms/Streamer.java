package com.paradisecloud.fcm.fme.model.cms;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 直播配置实体类
 *
 * @Author yl
 * @Date 2020/4/17 10:31
 */
@Getter
@Setter
@ToString
public class Streamer
{
    
    /**
     * 唯一id
     */
    @JSONField(name = "@id")
    private String id;
    
    /**
     * callBridge用来连接这个直播配置的地址
     */
    private String url;
    
    /**
     * 与此直播配置关联的callBridge的ID(来自版本2.1)
     */
    private String callBridge;
    
    /**
     * 与此直播配置关联的callBridgeGroup的ID(来自版本2.1)
     */
    private String callBridgeGroup;
    
}
