/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeUtils.java
 * Package     : com.paradisecloud.fcm.fme.attendee.utils
 * @author sinhy 
 * @since 2021-09-22 21:04
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.utils;

import java.util.*;

import com.paradisecloud.fcm.common.enumer.FcmType;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridgeCluster;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.fcm.wvp.gb28181.WvpBridge;
import com.paradisecloud.fcm.wvp.gb28181.WvpBridgeCache;
import com.paradisecloud.fcm.wvp.gb28181.reponse.WvpChannelsResponse;
import com.paradisecloud.fcm.wvp.gb28181.reponse.WvpPlayStartResponse;
import com.paradisecloud.fcm.wvp.gb28181.reponse.WvpSyncStatusResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.ObjectUtils;

import com.paradiscloud.fcm.business.model.enumer.BusinessFieldType;
import com.paradisecloud.fcm.common.enumer.AttendType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.model.BusiTemplateParticipant;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.fme.model.busi.attendee.TerminalAttendee;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;

public class AttendeeUtils
{
    
    public static TerminalAttendee packTerminalAttendee(long terminalId, AttendType attendType, Map<String, Object> businessProperties, int weight, String uuid)
    {

        BusiTerminal terminal = TerminalCache.getInstance().get(terminalId);
        TerminalAttendee ta = new TerminalAttendee();
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
        ta.setPhone(terminal.getPhone());
        if (TerminalType.isFSBC(terminal.getType()))
        {
            FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(terminal.getFsbcServerId());
            String ip = fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
            String domainName = fsbcBridge.getBusiFsbcRegistrationServer().getDomainName();
            ta.setIp(ip);
            ta.setRemoteParty(terminal.getCredential() + "@" + ta.getIp());
            if (!ObjectUtils.isEmpty(domainName)) {
                ta.setIpNew(domainName);
                ta.setRemotePartyNew(terminal.getCredential() + "@" + domainName);
            }
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
            String domainName = fcmBridge.getBusiFreeSwitch().getDomainName();
            if (!ObjectUtils.isEmpty(domainName)) {
                ta.setIpNew(domainName);
                ta.setRemotePartyNew(terminal.getCredential() + "@" + domainName);
            }
        }
        else if (TerminalType.isRtsp(terminal.getType()))
        {
            ta.setRemoteParty(terminal.getProtocol());
            Map<String, Object> properties =new HashMap<>();
            properties.put("rtsp_uri",terminal.getProtocol());
            ta.putBusinessProperties(properties);
        }
        else if (TerminalType.isGB28181(terminal.getType()))
        {
            if(Strings.isNotBlank(terminal.getProtocol())){
                ta.setRemoteParty(terminal.getProtocol());
                Map<String, Object> properties =new HashMap<>();
                properties.put("rtsp_uri",terminal.getProtocol());
                ta.putBusinessProperties(properties);
            }else {
                WvpBridge wvpBridge = WvpBridgeCache.getInstance().get();
                wvpBridge.getWvpControllApi().syncDevice(terminal.getNumber());
                WvpChannelsResponse wvpChannelsResponse = wvpBridge.getWvpControllApi().queryChannels(terminal.getNumber());
                if (wvpChannelsResponse != null && wvpChannelsResponse.getCode() == 0) {
                    List<WvpChannelsResponse.DataDTO.ListDTO> list = wvpChannelsResponse.getData().getList();
                    if(CollectionUtils.isNotEmpty(list)){
                        WvpPlayStartResponse wvpPlayStartResponse = wvpBridge.getWvpControllApi().playStart(terminal.getNumber(), list.get(0).getChannelId());
                        String s = Optional.ofNullable(wvpPlayStartResponse).map(WvpPlayStartResponse::getData).map(WvpPlayStartResponse.DataDTO::getRtsp).get();
                        ta.setRemoteParty(s);
                    }

                }

                Map<String, Object> properties =new HashMap<>();
                properties.put("rtsp_uri",ta.getRemoteParty());
                ta.putBusinessProperties(properties);
            }
        }
        else
        {
            if (ObjectUtils.isEmpty(terminal.getNumber()))
            {
                ta.setRemoteParty(ta.getIp());
            }
            else
            {
                ta.setRemoteParty(terminal.getNumber() + "@" + ta.getIp());
            }
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
    
    public static TerminalAttendee packTerminalAttendee(long terminalId)
    {
        return packTerminalAttendee(terminalId, null, null, 1, UUID.randomUUID().toString());
    }
    
    public static TerminalAttendee packTerminalAttendee(BusiTemplateParticipant busiTemplateParticipant)
    {

        if(busiTemplateParticipant==null){
            return null;
        }
        return packTerminalAttendee(busiTemplateParticipant.getTerminalId(), AttendType.convert(busiTemplateParticipant.getAttendType()), busiTemplateParticipant.getBusinessProperties(), busiTemplateParticipant.getWeight(), busiTemplateParticipant.getUuid());
    }

}
