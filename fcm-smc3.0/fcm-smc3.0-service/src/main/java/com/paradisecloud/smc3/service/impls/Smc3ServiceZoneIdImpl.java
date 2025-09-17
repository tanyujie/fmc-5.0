package com.paradisecloud.smc3.service.impls;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.model.DefaultServiceZoneIdRep;
import com.paradisecloud.smc3.model.MeetingRoomResponse;
import com.paradisecloud.smc3.model.response.Devicecapabilities;
import com.paradisecloud.smc3.model.response.McuResponse;
import com.paradisecloud.smc3.model.response.QueryAREASResponse;
import com.paradisecloud.smc3.model.response.SmcOrganization;
import com.paradisecloud.smc3.service.interfaces.Smc3ServiceZoneId;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nj
 * @date 2022/8/23 10:58
 */
@Service
public class Smc3ServiceZoneIdImpl implements Smc3ServiceZoneId {


    public static final String UNAUTHORIZED = "Unauthorized";

    @Override
    public DefaultServiceZoneIdRep getSmcServiceZoneId(Long deptId) {
        Smc3Bridge bridge = Smc3BridgeCache.getInstance().getBridgesByDept(deptId);
        String s = bridge.getSmcServiceZoneIdInvoker().getServiceZoneId(bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        DefaultServiceZoneIdRep defaultServiceZoneIdRep = JSON.parseObject(s, DefaultServiceZoneIdRep.class);
        return defaultServiceZoneIdRep;
    }

    @Override
    public Object getSmcArea(Long deptId) {

        try {
            Smc3Bridge bridge = Smc3BridgeCache.getInstance().getBridgesByDept(deptId);
            String s = bridge.getSmcMeetingroomsInvoker().queryAreaId(bridge.getSmcportalTokenInvoker().getSystemHeaders());
            List<QueryAREASResponse> queryAREASResponses = JSONArray.parseArray(s, QueryAREASResponse.class);
            return queryAREASResponses;
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object getSmcDevicecapabilities(Long deptId) {
        try {
            Smc3Bridge bridge = Smc3BridgeCache.getInstance().getBridgesByDept(deptId);
            String s = bridge.getSmcMeetingroomsInvoker().getDevicecapabilities(bridge.getSmcportalTokenInvoker().getSystemHeaders());
            List<Devicecapabilities> queryAREASResponses = JSONArray.parseArray(s, Devicecapabilities.class);
            return queryAREASResponses;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object searchName(String name, Long deptId) {
        Smc3Bridge bridge = Smc3BridgeCache.getInstance().getBridgesByDept(deptId);
        String s = bridge.getSmcMeetingroomsInvoker().getMeetingRoomsByName(name, bridge.getSmcportalTokenInvoker().getSystemHeaders());
        if (Strings.isNotBlank(s) && UNAUTHORIZED.equals(s)) {
            throw new CustomException("用户认证失败");
        }
        return s;
    }

    @Override
    public Object resetactivecode(String id, Long deptId) {
        try {
            Smc3Bridge bridge = Smc3BridgeCache.getInstance().getBridgesByDept(deptId);
            String s = bridge.getSmcMeetingroomsInvoker().resetactivecode(id, bridge.getSmcportalTokenInvoker().getSystemHeaders());

            Object object = JSONObject.parseObject(s, MeetingRoomResponse.TerminalParamDTO.ActiveCodeDTO.class);

            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object getSmcMcu(Long deptId) {
        try {
            Smc3Bridge bridge = Smc3BridgeCache.getInstance().getBridgesByDept(deptId);
            String s = bridge.getSmcMeetingroomsInvoker().getSmcMcu(bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            McuResponse mcuResponse = JSONObject.parseObject(s, McuResponse.class);
            return mcuResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public  List<SmcOrganization> getOrganizationsList(Long deptId) {
        try {
            Smc3Bridge bridge = Smc3BridgeCache.getInstance().getBridgesByDept(deptId);
            List<SmcOrganization> list = bridge.getSmcOrganizationsInvoker().getOrganizationsList(bridge.getSmcportalTokenInvoker().getSystemHeaders());
            Map<String, SmcOrganization> smcOrganizationMap = new HashMap<>();
            for (SmcOrganization smcOrganization : list) {
                smcOrganizationMap.put(smcOrganization.getName(), smcOrganization);
            }
            bridge.setSmcOrganizationMap(smcOrganizationMap);
            return list;
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return null;
    }
}
