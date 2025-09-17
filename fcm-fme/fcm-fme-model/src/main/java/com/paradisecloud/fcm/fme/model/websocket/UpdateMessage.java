/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : UpdateMessage.java
 * Package : com.paradisecloud.common.response.websocket
 * 
 * @author lilinhai
 * 
 * @since 2020-12-11 18:20
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.fme.model.websocket;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>抽象的更新消息</pre>
 * 
 * @author lilinhai
 * @since 2020-12-11 18:20
 * @version V1.0
 */
@Getter
@Setter
@ToString
public abstract class UpdateMessage<T>
{
    
    /**
     * 消息id
     */
    private Integer messageId;
    
    /**
     * 消息类型
     */
    private String type;
    
    /**
     * 订阅索引
     */
    private Integer subscriptionIndex;
    
    /**
     * 更新数据
     */
    private List<T> updates;
}
