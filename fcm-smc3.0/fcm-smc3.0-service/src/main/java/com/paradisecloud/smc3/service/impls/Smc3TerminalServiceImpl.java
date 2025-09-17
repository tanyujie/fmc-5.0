package com.paradisecloud.smc3.service.impls;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.utils.CheckPassword;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.model.*;
import com.paradisecloud.smc3.model.response.UserInfoRep;
import com.paradisecloud.smc3.service.interfaces.Smc3DeviceroutesService;
import com.paradisecloud.smc3.service.interfaces.Smc3ServiceZoneId;
import com.paradisecloud.smc3.service.interfaces.Smc3Terminalserice;
import com.paradisecloud.smc3.service.interfaces.Smc3UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nj
 * @date 2022/8/22 10:39
 */
@Service
public class Smc3TerminalServiceImpl implements Smc3Terminalserice {



    public static final String CUSTOMIZE_TERMINAL_TYPE = "SKMCU";
    public static final String TRUSTED_ZONE = "TRUSTED_ZONE";
    public static final String KBIT_S = "1920 Kbit/s";
    public static final String MULTIMEDIA_CONF = "MULTIMEDIA_CONF";
    public static final String DEVICE_MANAGE = "DEVICE_MANAGE";

    public static final String regex = "^(?![a-zA-Z]+$)(?![A-Z0-9]+$)(?![A-Z\\W_]+$)(?![a-z0-9]+$)(?![a-z\\W_]+$)(?![0-9\\W_]+$)(?!.*(.)\\\\1\\\\1.*)[a-zA-Z0-9\\W_]{8,32}$";
    public static final String MEETINGROOM_NAME_EXIST = "MEETINGROOM_NAME_EXIST";
    public static final String TERMINAL_PARAM_LOGIN_SMC_NAME_EXIST = "TERMINAL_PARAM_LOGIN_SMC_NAME_EXIST";

    @Resource
    private Smc3DeviceroutesService smc3DeviceroutesService;

    @Resource
    private Smc3ServiceZoneId smc3ServiceZoneId;
    @Resource
    private Smc3UserService smc3UserService;




    @Override
    public String addTerminal(MeetingRoomCreateReq req) {
        Smc3Bridge bridge = Smc3BridgeCache.getInstance().getBridgesByDept(req.getDeptId());
        String jsonString = JSONObject.toJSONString(req);
        String s = bridge.getSmcMeetingroomsInvoker().creatMeetingrooms(jsonString, bridge.getSmcportalTokenInvoker().getSystemHeaders());
        return s;
    }

    @Override
    public MeetingRoomResponse autoAddTerminal(String name, String account, String password,Long deptId) {
        CheckPassword.checkPasswordRule(password);
        MeetingRoomCreateReq meetingRoomReq = new MeetingRoomCreateReq();
        meetingRoomReq.setName(name);
        meetingRoomReq.setProvisionEua("true");

        DefaultServiceZoneIdRep smcServiceZoneId = this.smc3ServiceZoneId.getSmcServiceZoneId(deptId);
        String serviceZoneId = smcServiceZoneId.getContent().get(0).getId();
        String scIpAddress = smcServiceZoneId.getContent().get(0).getScIpAddress();
        UserInfoRep userInfo = smc3UserService.getUserInfo(null);
        String id = userInfo.getAccount().getOrganization().getId();
        meetingRoomReq.setOrganizationId(id);
        meetingRoomReq.setServiceZoneId(serviceZoneId);
        TerminalParam terminalParam = new TerminalParam();
        String deviceroutes = smc3DeviceroutesService.getDeviceroutes(serviceZoneId,deptId);

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
    public void delete(List<String> ids,Long deptId) {
        Smc3Bridge bridge = Smc3BridgeCache.getInstance().getBridgesByDept(deptId);

        bridge.getSmcMeetingroomsInvoker().deleteRooms(ids, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public Object list(String orgId, int page, int size,Long deptId) {
        Smc3Bridge bridge = Smc3BridgeCache.getInstance().getBridgesByDept(deptId);

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
    public MeetingRoomRep list(Smc3Bridge bridge, String orgId, String key, int page, int size) {

        MeetingRoomRep meetingRoomsRep = bridge.getSmcMeetingroomsInvoker().getMeetingRoomsRep(orgId, null, 0, 10, bridge.getSmcportalTokenInvoker().getSystemHeaders());
        if (meetingRoomsRep == null) {
            return null;
        }
        return meetingRoomsRep;
    }

    @Override
    public MeetingRoomResponse update(MeetingRoomCreateReq createReq) {
        Smc3Bridge bridge = Smc3BridgeCache.getInstance().getBridgesByDept(createReq.getDeptId());
        String jsonString = JSONObject.toJSONString(createReq);
        return bridge.getSmcMeetingroomsInvoker().updateMeetingrooms(createReq.getId(), jsonString, bridge.getSmcportalTokenInvoker().getSystemHeaders());
    }

    @Override
    public MeetingRoomRep getInfoById(String id,Long deptId) {
        Smc3Bridge bridge = Smc3BridgeCache.getInstance().getBridgesByDept(deptId);
        String meetingRooms = bridge.getSmcMeetingroomsInvoker().getMeetingRooms(id, bridge.getSmcportalTokenInvoker().getSystemHeaders());
        return JSON.parseObject(meetingRooms, MeetingRoomRep.class);
    }

    @Override
    public Object endpoints(Long deptId) {
        Smc3Bridge bridge = Smc3BridgeCache.getInstance().getBridgesByDept(deptId);
        String userInfo = bridge.getSmcUserInvoker().getUserInfo(bridge.getSmcportalTokenInvoker().getUserName(), bridge.getSmcportalTokenInvoker().getSystemHeaders());
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
