/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeUtils.java
 * Package     : com.paradisecloud.fcm.fme.attendee.utils
 * @author sinhy 
 * @since 2021-09-22 21:04
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.zte.attendee.utils;

import com.paradiscloud.fcm.business.model.enumer.BusinessFieldType;
import com.paradisecloud.fcm.common.enumer.AttendType;
import com.paradisecloud.fcm.common.enumer.FcmType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplateParticipant;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridgeCluster;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.zte.model.busi.attendee.TerminalAttendeeForMcuZte;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AttendeeUtils
{
    
    public static TerminalAttendeeForMcuZte packTerminalAttendee(long terminalId, AttendType attendType, Map<String, Object> businessProperties, int weight, String uuid)
    {
        BusiTerminal terminal = TerminalCache.getInstance().get(terminalId);
        TerminalAttendeeForMcuZte ta = new TerminalAttendeeForMcuZte();
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

        if (TerminalType.isFSBC(terminal.getType()))
        {
            ta.setIp(FsbcBridgeCache.getInstance().getById(terminal.getFsbcServerId()).getBusiFsbcRegistrationServer().getCallIp());
            ta.setRemoteParty(terminal.getCredential() + "@" + ta.getIp());
        }
        else if (TerminalType.isFCMSIP(terminal.getType()))
        {
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
                ta.setIp(fcmBridge.getBusiFreeSwitch().getIp());
                ta.setRemoteParty(terminal.getCredential() + "@" + ta.getIp());
            } else {
                 fcmBridge = FcmBridgeCache.getInstance().getById(fsd.getServerId());
            }
            ta.setIp(fcmBridge.getBusiFreeSwitch().getIp());
            ta.setRemoteParty(terminal.getCredential() + "@" + ta.getIp());
        }
        else if (TerminalType.isSMCSIP(terminal.getType())) {
            ta.setRemoteParty(terminal.getNumber());
            ta.setRemotePartyNew(terminal.getNumber());
        }else if(TerminalType.isZTE(terminal.getType())){
            if(terminal.getCallmodel()==0){
                ta.setRemoteParty(terminal.getCredential());
            }else {
                ta.setRemoteParty(ta.getIp());
            }
        }
        else {
            if (ObjectUtils.isEmpty(terminal.getNumber())) {
                ta.setRemoteParty(ta.getIp());
            } else {
                ta.setRemoteParty(terminal.getNumber() + "@" + ta.getIp());
            }
        }
        if (TerminalType.isWindows(terminal.getType()) || TerminalType.isIp(terminal.getType())) {
            ta.setRemoteParty(ta.getIp());
        }


        // 业务属性为空就获取终端的
        businessProperties = businessProperties == null ? terminal.getBusinessProperties() : businessProperties;
        Map<String, Object> properties = BusinessFieldType.convert(terminal.getBusinessFieldType()).getBusinessFieldService().parseTerminalBusinessProperties(businessProperties);
        if (!ObjectUtils.isEmpty(properties))
        {
            ta.putBusinessProperties(properties);
        }

        return ta;
    }

    public static TerminalAttendeeForMcuZte packTerminalAttendee(long terminalId)
    {
        return packTerminalAttendee(terminalId, null, null, 1, UUID.randomUUID().toString());
    }

    public static TerminalAttendeeForMcuZte packTerminalAttendee(BusiMcuZteTemplateParticipant busiMcuZteTemplateParticipant)
    {
        return packTerminalAttendee(busiMcuZteTemplateParticipant.getTerminalId(), AttendType.convert(busiMcuZteTemplateParticipant.getAttendType()), busiMcuZteTemplateParticipant.getBusinessProperties(), busiMcuZteTemplateParticipant.getWeight(), busiMcuZteTemplateParticipant.getUuid());
    }

}
