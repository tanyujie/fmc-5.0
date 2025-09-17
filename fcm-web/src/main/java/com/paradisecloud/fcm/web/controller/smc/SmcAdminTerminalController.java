package com.paradisecloud.fcm.web.controller.smc;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.com.fcm.smc.modle.MeetingRoomCreateReq;
import com.paradisecloud.com.fcm.smc.modle.MeetingRoomQueryRequest;
import com.paradisecloud.com.fcm.smc.modle.MeetingRoomRep;
import com.paradisecloud.com.fcm.smc.modle.MeetingRoomResponse;
import com.paradisecloud.com.fcm.smc.modle.request.MeetingRoomAutoCreateReq;
import com.paradisecloud.com.fcm.smc.modle.response.UserInfoRep;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.paradisecloud.smc.service.SmcTerminalserice;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author nj
 * @date 2022/8/23 11:50
 */


@RestController
@RequestMapping("/smc/admin/terminal")
public class SmcAdminTerminalController {

    @Resource
    private SmcTerminalserice smcTerminalserice;

    @DeleteMapping("/delete")
    public  void  delete(@RequestBody List<String> ids){
        smcTerminalserice.delete(ids);
    }

    @PostMapping("/list")
    public RestResponse list(@RequestBody MeetingRoomQueryRequest meetingRoomQueryRequest) {
        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
        String userInfo = bridge.getSmcUserInvoker().getUserInfo(bridge.getSmcportalTokenInvoker().getUsername(), bridge.getSmcportalTokenInvoker().getSystemHeaders());
        UserInfoRep userInfoRep = JSON.parseObject(userInfo, UserInfoRep.class);
        String id = userInfoRep.getAccount().getOrganization().getId();
        meetingRoomQueryRequest.setOrganizationId(id);
        Object list = smcTerminalserice.list(meetingRoomQueryRequest.getOrganizationId(), meetingRoomQueryRequest.getPage(), meetingRoomQueryRequest.getSize());
        return RestResponse.success(list);
    }

    @PostMapping("/endpoints")
    public RestResponse list() {
        return RestResponse.success(smcTerminalserice.endpoints());
    }

    @PostMapping("/addAuto")
    public RestResponse addAuto(@RequestBody MeetingRoomAutoCreateReq createReq) {
        MeetingRoomResponse meetingRoomResponse = smcTerminalserice.autoAddTerminal(createReq.getName(), createReq.getAccount(),createReq.getPassword());
        return RestResponse.success(meetingRoomResponse);
    }

    @PostMapping("/add")
    public RestResponse add(@RequestBody MeetingRoomCreateReq createReq) {
        smcTerminalserice.addTerminal(createReq);
        return RestResponse.success();
    }

    @PutMapping("/update")
    public RestResponse update(@RequestBody MeetingRoomCreateReq createReq) {
        MeetingRoomRep update = smcTerminalserice.update(createReq);
        return RestResponse.success(update);
    }

    /**
     * 会议室详情查看
     *
     * @param id
     */
    @GetMapping("/info")
    public RestResponse getInfoById(@RequestParam String id) {
        return RestResponse.success(smcTerminalserice.getInfoById(id));
    }
}
