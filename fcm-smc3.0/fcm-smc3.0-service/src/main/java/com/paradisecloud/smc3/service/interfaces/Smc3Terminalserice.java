package com.paradisecloud.smc3.service.interfaces;




import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.model.MeetingRoomCreateReq;
import com.paradisecloud.smc3.model.MeetingRoomRep;
import com.paradisecloud.smc3.model.MeetingRoomResponse;

import java.util.List;


/**
 * @author nj
 * @date 2022/8/22 10:12
 */
public interface Smc3Terminalserice {

    String addTerminal(MeetingRoomCreateReq meetingRoomReq);

    MeetingRoomResponse autoAddTerminal(String name, String account, String password,Long deptId);

    void delete(List<String> ids,Long deptId);

    Object list(String orgId, int page, int size,Long deptId);

    MeetingRoomRep list(Smc3Bridge bridge, String orgId, String key, int page, int size);

    MeetingRoomResponse update(MeetingRoomCreateReq createReq);

    MeetingRoomRep getInfoById(String id,Long deptId);


    Object endpoints(Long deptId);
}
