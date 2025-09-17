package com.paradisecloud.fcm.fme.model.websocket.callinfo;

import com.paradisecloud.fcm.fme.model.websocket.UpdateResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>CallInfoUpdateResponse websocket响应结构体封装</pre>
 * 
 * @author lilinhai
 * @since 2020-12-11 18:13
 * @version V1.0
 */
@Getter
@Setter
@ToString
public class CallInfoUpdateResponse extends UpdateResponse<CallInfoUpdate, CallInfoUpdateMessage>
{
    
}
