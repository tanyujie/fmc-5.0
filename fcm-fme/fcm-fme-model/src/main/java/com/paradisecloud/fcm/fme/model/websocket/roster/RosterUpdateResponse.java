package com.paradisecloud.fcm.fme.model.websocket.roster;

import com.paradisecloud.fcm.fme.model.websocket.UpdateResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 与会者更新响应类
 *
 * @author zt1994 2019/9/2 11:37
 */
@Getter
@Setter
@ToString
public class RosterUpdateResponse extends UpdateResponse<RosterUpdate, RosterUpdateMessage>
{
    
}
