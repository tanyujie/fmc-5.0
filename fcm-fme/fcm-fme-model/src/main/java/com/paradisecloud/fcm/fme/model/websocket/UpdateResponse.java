/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : UpdateResponse.java
 * Package : com.paradisecloud.common.response.websocket
 * 
 * @author lilinhai
 * 
 * @since 2020-12-11 18:24
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.fme.model.websocket;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>抽象的更新响应</pre>
 * 
 * @author lilinhai
 * @since 2020-12-11 18:24
 * @version V1.0
 */
@Getter
@Setter
@ToString
public abstract class UpdateResponse<E, T extends UpdateMessage<E>>
{
    
    /**
     * 类型
     */
    private String type;
    
    /**
     * 响应消息
     */
    private T message;
}
