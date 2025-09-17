package com.paradisecloud.fcm.smc.cache.modle.util;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.com.fcm.smc.modle.MeetingRoomRep;
import com.paradisecloud.com.fcm.smc.modle.ScEndpointInfoListRequest;
import com.paradisecloud.com.fcm.smc.modle.ScEndpointRep;
import com.paradisecloud.com.fcm.smc.modle.response.UserInfoRep;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author nj
 * @date 2022/8/30 15:47
 */
public class ScEndpointUtil {


    public static final String UNAUTHORIZED = "Unauthorized";

    public static List<ScEndpointRep>  getEndpoints(SmcBridge bridge, int page, int size){
        String user= bridge.getSmcUserInvoker().getUserInfo(bridge.getBusiSMC().getUsername(),bridge.getSmcportalTokenInvoker().getSystemHeaders());

        if(StringUtils.isBlank(user)||user.contains(UNAUTHORIZED)){
            return null;
        }
        UserInfoRep   userInfoREP= JSON.parseObject(user, UserInfoRep.class);
        if(userInfoREP==null||userInfoREP.getAccount()==null){
            return null;
        }
        UserInfoRep.AccountDTO.OrganizationDTO organization = userInfoREP.getAccount().getOrganization();
        String orgId = organization.getId();
        ScEndpointInfoListRequest scEndpointInfoListRequest = new ScEndpointInfoListRequest();
        List<ScEndpointInfoListRequest.ScEndpointInfoListDTO> scEndpointInfoList = new ArrayList<>();
        while (true){
            MeetingRoomRep meetingRoomsRep = bridge.getSmcMeetingroomsInvoker().getMeetingRoomsRep(orgId, null, page, size, bridge.getSmcportalTokenInvoker().getSystemHeaders(bridge.getIp()));
            if (meetingRoomsRep == null||meetingRoomsRep.getContent()==null) {
                return null;
            }
            List<MeetingRoomRep.ContentDTO> contents = meetingRoomsRep.getContent();
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
            Boolean last = meetingRoomsRep.getLast();
            if(last){
                break;
            }else {
                page++;
            }

        }
        List<ScEndpointRep> scEndpointReps = bridge.getSmcMeetingroomsInvoker().getEndpointsstatus(scEndpointInfoListRequest, bridge.getSmcportalTokenInvoker().getSystemHeaders());
        return scEndpointReps;
    }
}
