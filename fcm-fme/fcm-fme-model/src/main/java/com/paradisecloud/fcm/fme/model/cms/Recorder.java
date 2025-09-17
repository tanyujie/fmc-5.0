package com.paradisecloud.fcm.fme.model.cms;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 录制配置实体类
 *
 * @Author yl
 * @Date 2020/4/16 10:31
 */
@Getter
@Setter
@ToString
public class Recorder
{
    
    /**
     * 唯一id
     */
    @JSONField(name = "@id")
    private String id;
    
    /**
     * callBridge用来连接这个录制配置的地址
     */
    private String url;
    
    /**
     * 与此录制配置关联的callBridge的ID(来自版本2.1)
     */
    private String callBridge;
    
    /**
     * 与此录制配置关联的callBridgeGroup的ID(来自版本2.1)
     */
    private String callBridgeGroup;
    
}
