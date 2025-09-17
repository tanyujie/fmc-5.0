package com.paradisecloud.fcm.smc2.utils;

import com.paradiscloud.fcm.business.model.enumer.BusinessFieldType;
import com.paradisecloud.fcm.common.enumer.AttendType;
import com.paradisecloud.fcm.common.enumer.FcmType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2TemplateParticipant;
import com.paradisecloud.fcm.smc2.model.attendee.TerminalAttendeeSmc2;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridgeCluster;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * @author nj
 * @date 2022/4/21 10:54
 */
public class AttendeeUtils {
    public static TerminalAttendeeSmc2 packTerminalAttendee(long terminalId, AttendType attendType, Map<String, Object> businessProperties, int weight, String uuid) {
        BusiTerminal terminal = TerminalCache.getInstance().get(terminalId);
        TerminalAttendeeSmc2 ta = new TerminalAttendeeSmc2();
        ta.setTerminalId(terminal.getId());
        ta.setTerminalTypeName(TerminalType.convert(terminal.getType()).getDisplayName());
        ta.setTerminalType(terminal.getType());
        ta.setAttendType(attendType == null ? terminal.getAttendType() : attendType.getValue());
        ta.setDeptId(terminal.getDeptId());
        ta.setIp(terminal.getIp());
        ta.setName(terminal.getName());
        ta.setWeight(weight);
        ta.setId(uuid);
        ta.setOnlineStatus(terminal.getOnlineStatus());
        ta.setSn(terminal.getSn());
        ta.setProtocol(TerminalType.isFSIP(terminal.getType()) == true ? "sip" : "h232");
        if (TerminalType.isFSBC(terminal.getType())) {
            FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(terminal.getFsbcServerId());
            String ip = fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
            String domainName = fsbcBridge.getBusiFsbcRegistrationServer().getDomainName();
            ta.setIp(ip);
            ta.setRemoteParty(terminal.getCredential() + "@" + ta.getIp());
            if (!ObjectUtils.isEmpty(domainName)) {
                ta.setIpNew(domainName);
                ta.setRemotePartyNew(terminal.getCredential() + "@" + domainName);
            }
            Integer sipPort = fsbcBridge.getBusiFsbcRegistrationServer().getSipPort();
            if (sipPort == null || sipPort == 5060) {
                ta.setRemoteParty(terminal.getCredential() + "@" + ta.getIp());
                ta.setRemoteParty(terminal.getCredential() + "@" + ip);
            } else {
                ta.setRemoteParty(terminal.getCredential() + "@" + ip + ":" + sipPort);
            }
        } else if (TerminalType.isFCMSIP(terminal.getType())) {
            FcmBridge fcmBridge = null;
            BusiFreeSwitchDept fsd = DeptFcmMappingCache.getInstance().get(terminal.getDeptId());
            if (FcmType.CLUSTER == FcmType.convert(fsd.getFcmType())) {
                if (terminal.getFsServerId() != null) {
                    fcmBridge = FcmBridgeCache.getInstance().get(terminal.getFsServerId());
                }
                if (fcmBridge == null) {
                    FcmBridgeCluster fcmBridgeCluster = FcmBridgeCache.getInstance().getByFcmClusterId(fsd.getServerId());
                    if (fcmBridgeCluster != null) {
                        List<FcmBridge> fcmBridges = fcmBridgeCluster.getFcmBridges();
                        // 由于使用固定用户信息数据库，任意一个FCM即可
                        fcmBridge = fcmBridges.get(0);
                    }
                }
                String callIp = fcmBridge.getBusiFreeSwitch().getIp();
                Integer callPort = fcmBridge.getBusiFreeSwitch().getCallPort();
                if (callPort == null || callPort == 5060) {
                    ta.setRemoteParty(terminal.getCredential() + "@" + ta.getIp());
                    ta.setRemoteParty(terminal.getCredential() + "@" + callIp);
                } else {
                    ta.setRemoteParty(terminal.getCredential() + "@" + callIp + ":" + callPort);
                }
                ta.setIp(callIp);
            } else {
                fcmBridge = FcmBridgeCache.getInstance().getById(fsd.getServerId());
            }
            ta.setIp(fcmBridge.getBusiFreeSwitch().getIp());
            ta.setRemoteParty(terminal.getCredential() + "@" + ta.getIp());
            String domainName = fcmBridge.getBusiFreeSwitch().getDomainName();
            if (!ObjectUtils.isEmpty(domainName)) {
                ta.setIpNew(domainName);
                ta.setRemotePartyNew(terminal.getCredential() + "@" + domainName);
            }
        } else if (TerminalType.isRtsp(terminal.getType())) {
            ta.setRemoteParty(terminal.getProtocol());
            Map<String, Object> properties = new HashMap<>();
            properties.put("rtsp_uri", terminal.getProtocol());
            ta.putBusinessProperties(properties);
        } else {
            if (ObjectUtils.isEmpty(terminal.getNumber())) {
                ta.setRemoteParty(ta.getIp());
            } else {
                ta.setRemoteParty(terminal.getNumber() + "@" + ta.getIp());
            }
        }
        if (TerminalType.isWindows(terminal.getType()) || TerminalType.isCisco(terminal.getType())) {
            ta.setProtocol("sip");
            if(TerminalType.isWindows(terminal.getType())){
                ta.setRemoteParty(UUID.randomUUID() + "@" + ta.getIp());
            }
        }
        // 业务属性为空就获取终端的
        businessProperties = businessProperties == null ? terminal.getBusinessProperties() : businessProperties;
        Map<String, Object> properties = BusinessFieldType.convert(terminal.getBusinessFieldType()).getBusinessFieldService().parseTerminalBusinessProperties(businessProperties);
        if (!ObjectUtils.isEmpty(properties)) {
            ta.putBusinessProperties(properties);
        }

        return ta;
    }

    public static TerminalAttendeeSmc2 packTerminalAttendee(long terminalId) {
        return packTerminalAttendee(terminalId, null, null, 1, UUID.randomUUID().toString());
    }

    public static TerminalAttendeeSmc2 packTerminalAttendee(BusiMcuSmc2TemplateParticipant busiTemplateParticipant) {
        return packTerminalAttendee(busiTemplateParticipant.getTerminalId(), AttendType.convert(busiTemplateParticipant.getAttendType()), busiTemplateParticipant.getBusinessProperties(), busiTemplateParticipant.getWeight(), busiTemplateParticipant.getUuid());
    }
}
