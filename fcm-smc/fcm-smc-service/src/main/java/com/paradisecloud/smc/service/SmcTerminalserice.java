package com.paradisecloud.smc.service;


import com.paradisecloud.com.fcm.smc.modle.MeetingRoomCreateReq;
import com.paradisecloud.com.fcm.smc.modle.MeetingRoomRep;
import com.paradisecloud.com.fcm.smc.modle.MeetingRoomResponse;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;

import java.util.List;


/**
 * @author nj
 * @date 2022/8/22 10:12
 */
public interface SmcTerminalserice {

    String addTerminal(MeetingRoomCreateReq meetingRoomReq);

    MeetingRoomResponse autoAddTerminal(String name, String account,String password);

    void delete(List<String> ids);

    Object list(String orgId, int page, int size);

    MeetingRoomRep list(SmcBridge bridge, String orgId, String key, int page, int size);

    MeetingRoomRep update(MeetingRoomCreateReq createReq);

    MeetingRoomRep getInfoById(String id);

    Object endpoints();
}
