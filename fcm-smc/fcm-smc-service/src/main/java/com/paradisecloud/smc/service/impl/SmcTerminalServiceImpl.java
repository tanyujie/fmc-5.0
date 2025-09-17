package com.paradisecloud.smc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.*;
import com.paradisecloud.com.fcm.smc.modle.response.UserInfoRep;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.utils.CheckPassword;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.paradisecloud.smc.service.DeviceroutesService;
import com.paradisecloud.smc.service.SmcTerminalserice;
import com.paradisecloud.smc.service.SmcUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nj
 * @date 2022/8/22 10:39
 */
@Service
public class SmcTerminalServiceImpl implements SmcTerminalserice {


    public static final String SMC_PASSWORD = "admin@2021";

    // public static final String CUSTOMIZE_TERMINAL_TYPE = "CUSTOMIZE_TERMINAL";
    public static final String CUSTOMIZE_TERMINAL_TYPE = "SKMCU";
    public static final String TRUSTED_ZONE = "TRUSTED_ZONE";
    public static final String KBIT_S = "1920 Kbit/s";
    public static final String MULTIMEDIA_CONF = "MULTIMEDIA_CONF";
    public static final String DEVICE_MANAGE = "DEVICE_MANAGE";

    public static final String regex = "^(?![a-zA-Z]+$)(?![A-Z0-9]+$)(?![A-Z\\W_]+$)(?![a-z0-9]+$)(?![a-z\\W_]+$)(?![0-9\\W_]+$)(?!.*(.)\\\\1\\\\1.*)[a-zA-Z0-9\\W_]{8,32}$";
    public static final String MEETINGROOM_NAME_EXIST = "MEETINGROOM_NAME_EXIST";
    public static final String TERMINAL_PARAM_LOGIN_SMC_NAME_EXIST = "TERMINAL_PARAM_LOGIN_SMC_NAME_EXIST";

    @Resource
    private DeviceroutesService deviceroutesService;

    @Resource
    private SmcServiceZoneId smcServiceZoneId;
    @Resource
    private SmcUserService smcUserService;




    @Override
    public String addTerminal(MeetingRoomCreateReq req) {
        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
        String jsonString = JSONObject.toJSONString(req);
        String s = bridge.getSmcMeetingroomsInvoker().creatMeetingrooms(jsonString, bridge.getSmcportalTokenInvoker().getSystemHeaders());
        return s;
    }

    @Override
    public MeetingRoomResponse autoAddTerminal(String name, String account, String password) {
        CheckPassword.checkPasswordRule(password);
        MeetingRoomCreateReq meetingRoomReq = new MeetingRoomCreateReq();
        meetingRoomReq.setName(name);
        meetingRoomReq.setProvisionEua("true");

        DefaultServiceZoneIdRep smcServiceZoneId = this.smcServiceZoneId.getSmcServiceZoneId();
        String serviceZoneId = smcServiceZoneId.getContent().get(0).getId();
        String scIpAddress = smcServiceZoneId.getContent().get(0).getScIpAddress();
        UserInfoRep userInfo = smcUserService.getUserInfo();
        String id = userInfo.getAccount().getOrganization().getId();
        meetingRoomReq.setOrganizationId(id);
        meetingRoomReq.setServiceZoneId(serviceZoneId);
        TerminalParam terminalParam = new TerminalParam();
        String deviceroutes = deviceroutesService.getDeviceroutes(serviceZoneId);

        terminalParam.setTerminalType(CUSTOMIZE_TERMINAL_TYPE);
        terminalParam.setMiddleUri(deviceroutes);
        terminalParam.setNwZoneType(TRUSTED_ZONE);
        terminalParam.setRate(KBIT_S);
        terminalParam.setScRegisterAddress(scIpAddress);

        terminalParam.setIpProtocolType(SMcipProtocolType.AUTO.name());
        terminalParam.setLoginScName(account);
        terminalParam.setLoginSmcName(account);

        terminalParam.setLoginScPassword(password);
        terminalParam.setLoginSmcPassword(password);


        meetingRoomReq.setTerminalParam(terminalParam);
        ArrayList<String> serviceList = new ArrayList<>();
        serviceList.add(MULTIMEDIA_CONF);
        serviceList.add(DEVICE_MANAGE);
        terminalParam.setServiceList(serviceList);
        String res = addTerminal(meetingRoomReq);
        if (res.contains(MEETINGROOM_NAME_EXIST)) {
            throw new CustomException("SMC侧设备名称重复");
        }
        if (res.contains(TERMINAL_PARAM_LOGIN_SMC_NAME_EXIST)) {
            throw new CustomException("账号重复");
        }
        return JSON.parseObject(res, MeetingRoomResponse.class);
    }

    @Override
    public void delete(List<String> ids) {
        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);

        bridge.getSmcMeetingroomsInvoker().deleteRooms(ids, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public Object list(String orgId, int page, int size) {
        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);

        MeetingRoomRep meetingRoomsRep = bridge.getSmcMeetingroomsInvoker().getMeetingRoomsRep(orgId, null, 0, 10, bridge.getSmcportalTokenInvoker().getSystemHeaders());
        if (meetingRoomsRep == null) {
            return null;
        }
        List<MeetingRoomRep.ContentDTO> contents = meetingRoomsRep.getContent();
        ScEndpointInfoListRequest scEndpointInfoListRequest = new ScEndpointInfoListRequest();
        List<ScEndpointInfoListRequest.ScEndpointInfoListDTO> scEndpointInfoList = new ArrayList<>();
        for (MeetingRoomRep.ContentDTO content : contents) {
            String middleUri = content.getTerminalParam().getMiddleUri();
            String serviceZoneId = content.getServiceZoneId();
            ScEndpointInfoListRequest.ScEndpointInfoListDTO scEndpointInfoListDTO = new ScEndpointInfoListRequest.ScEndpointInfoListDTO();
            scEndpointInfoListDTO.setServiceZoneId(serviceZoneId);
            scEndpointInfoListDTO.setNwZoneType("TRUSTED_ZONE");
            scEndpointInfoListDTO.setUri(middleUri);
            scEndpointInfoList.add(scEndpointInfoListDTO);
        }
        scEndpointInfoListRequest.setScEndpointInfoList(scEndpointInfoList);


        List<ScEndpointRep> scEndpointReps = bridge.getSmcMeetingroomsInvoker().getEndpointsstatus(scEndpointInfoListRequest, bridge.getSmcportalTokenInvoker().getSystemHeaders());
        return scEndpointReps;

    }

    @Override
    public MeetingRoomRep list(SmcBridge bridge, String orgId, String key, int page, int size) {

        MeetingRoomRep meetingRoomsRep = bridge.getSmcMeetingroomsInvoker().getMeetingRoomsRep(orgId, null, 0, 10, bridge.getSmcportalTokenInvoker().getSystemHeaders());
        if (meetingRoomsRep == null) {
            return null;
        }
        return meetingRoomsRep;
    }

    @Override
    public MeetingRoomRep update(MeetingRoomCreateReq createReq) {
        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
        String jsonString = JSONObject.toJSONString(createReq);
        return bridge.getSmcMeetingroomsInvoker().updateMeetingrooms(createReq.getId(), jsonString, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public MeetingRoomRep getInfoById(String id) {
        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
        String meetingRooms = bridge.getSmcMeetingroomsInvoker().getMeetingRooms(id, bridge.getSmcportalTokenInvoker().getSystemHeaders());
        return JSON.parseObject(meetingRooms, MeetingRoomRep.class);
    }

    @Override
    public Object endpoints() {
        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
        String userInfo = bridge.getSmcUserInvoker().getUserInfo(bridge.getSmcportalTokenInvoker().getUsername(), bridge.getSmcportalTokenInvoker().getSystemHeaders());
        UserInfoRep userInfoRep = JSON.parseObject(userInfo, UserInfoRep.class);
        String id = userInfoRep.getAccount().getOrganization().getId();
        MeetingRoomRep meetingRoomsRep = bridge.getSmcMeetingroomsInvoker().getMeetingRoomsRep(id, null, 0, 10, bridge.getSmcportalTokenInvoker().getSystemHeaders());
        if (meetingRoomsRep == null) {
            return null;
        }
        List<MeetingRoomRep.ContentDTO> contents = meetingRoomsRep.getContent();
        ScEndpointInfoListRequest scEndpointInfoListRequest = new ScEndpointInfoListRequest();
        List<ScEndpointInfoListRequest.ScEndpointInfoListDTO> scEndpointInfoList = new ArrayList<>();
        for (MeetingRoomRep.ContentDTO content : contents) {
            String middleUri = content.getTerminalParam().getMiddleUri();
            String serviceZoneId = content.getServiceZoneId();
            ScEndpointInfoListRequest.ScEndpointInfoListDTO scEndpointInfoListDTO = new ScEndpointInfoListRequest.ScEndpointInfoListDTO();
            scEndpointInfoListDTO.setServiceZoneId(serviceZoneId);
            scEndpointInfoListDTO.setNwZoneType("TRUSTED_ZONE");
            scEndpointInfoListDTO.setUri(middleUri);
            scEndpointInfoList.add(scEndpointInfoListDTO);
        }
        scEndpointInfoListRequest.setScEndpointInfoList(scEndpointInfoList);


        List<ScEndpointRep> scEndpointReps = bridge.getSmcMeetingroomsInvoker().getEndpointsstatus(scEndpointInfoListRequest, bridge.getSmcportalTokenInvoker().getSystemHeaders());
        return scEndpointReps;
    }

}
