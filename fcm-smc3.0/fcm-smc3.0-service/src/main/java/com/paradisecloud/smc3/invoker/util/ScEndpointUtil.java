package com.paradisecloud.smc3.invoker.util;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.model.MeetingRoomRep;
import com.paradisecloud.smc3.model.ScEndpointInfoListRequest;
import com.paradisecloud.smc3.model.ScEndpointRep;
import com.paradisecloud.smc3.model.response.UserInfoRep;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nj
 * @date 2022/8/30 15:47
 */
public class ScEndpointUtil {


    public static final String UNAUTHORIZED = "Unauthorized";

    public static List<ScEndpointRep>  getEndpoints(Smc3Bridge bridge, int page, int size){

        int sourcePage = page;
        int sourceSize = size;
        String user= bridge.getSmcUserInvoker().getUserInfo(bridge.getBusiSMC().getUsername(),bridge.getSmcportalTokenInvoker().getSystemHeaders());

        if(StringUtils.isBlank(user)||user.contains(UNAUTHORIZED)){
            return null;
        }
        UserInfoRep userInfoREP= JSON.parseObject(user, UserInfoRep.class);
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
        int page1 = sourcePage;
        while (true){
            MeetingRoomRep meetingRoomsRep = bridge.getSmcMeetingroomsInvoker().getMeetingRoomsRepAreaId(orgId, null, page1, sourceSize, bridge.getSmcportalTokenInvoker().getSystemHeaders(bridge.getIp()));
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
                page1++;
            }

        }

        List<ScEndpointRep> scEndpointReps = bridge.getSmcMeetingroomsInvoker().getEndpointsstatus(scEndpointInfoListRequest, bridge.getSmcportalTokenInvoker().getSystemHeaders());
        return scEndpointReps;
    }
}
