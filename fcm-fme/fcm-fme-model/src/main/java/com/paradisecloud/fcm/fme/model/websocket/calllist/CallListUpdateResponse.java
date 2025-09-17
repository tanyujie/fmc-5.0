package com.paradisecloud.fcm.fme.model.websocket.calllist;

import com.paradisecloud.fcm.fme.model.websocket.UpdateResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * webSocket 响应参数
 *
 * @author zt1994 2019/8/30 14:02
 */
@Getter
@Setter
@ToString
public class CallListUpdateResponse extends UpdateResponse<CallListUpdate, CallListUpdateMessage>
{
    
}
