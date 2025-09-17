package com.paradisecloud.smc3.utils;

import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.dao.model.BusiFsbcServerDept;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.DeptFsbcMappingCache;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import io.jsonwebtoken.lang.Strings;
import org.apache.commons.lang3.StringUtils;

/**
 * @author nj
 * @date 2023/11/27 17:18
 */
public class BusiTerminalUtils {

    public static String getUri(BusiTerminal busiTerminal) {
        if (busiTerminal == null) {
            return null;
        }

        String uri = null;
        String number = busiTerminal.getNumber();
        if (StringUtils.isBlank(number)) {
            if (TerminalType.isCisco(busiTerminal.getType())) {
                uri = busiTerminal.getName() + "@" + busiTerminal.getIp();
            } else {
                uri = busiTerminal.getIp();
            }
        }
        if (StringUtils.isNotBlank(busiTerminal.getIp()) && Strings.hasText(busiTerminal.getNumber())) {
            uri = busiTerminal.getName() + "@" + busiTerminal.getIp();
        }
        if (TerminalType.isFSBC(busiTerminal.getType())) {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(busiTerminal.getIp())) {
                uri = busiTerminal.getCredential() + "@" + busiTerminal.getIp() + ":5060";
            } else {
                BusiFsbcServerDept busiFsbcServerDept = DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId());
                FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(busiFsbcServerDept.getFsbcServerId());
                String callIp = fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                Integer sipPort = fsbcBridge.getBusiFsbcRegistrationServer().getSipPort();
                if (sipPort == null || sipPort == 5060) {
                    uri = busiTerminal.getCredential() + "@" + busiTerminal.getIp() + ":5060";
                } else {
                    uri = busiTerminal.getCredential() + "@" + callIp + ":" + sipPort;
                }
            }

        }
        if (TerminalType.isFCMSIP(busiTerminal.getType())) {
            BusiFreeSwitchDept busiFreeSwitchDept = DeptFcmMappingCache.getInstance().get(busiTerminal.getDeptId());
            FcmBridge fcmBridge = FcmBridgeCache.getInstance().getById(busiFreeSwitchDept.getServerId());
            String callIp = fcmBridge.getBusiFreeSwitch().getIp();
            Integer callPort = fcmBridge.getBusiFreeSwitch().getCallPort();
            if (callPort == null || callPort == 5060) {
                uri = busiTerminal.getCredential() + "@" + busiTerminal.getIp() + ":5060";
            } else {
                uri = busiTerminal.getCredential() + "@" + callIp + ":" + callPort;
            }

        }

        if (TerminalType.isFmeTemplate(busiTerminal.getType())) {
            uri = busiTerminal.getNumber() + "@" + busiTerminal.getIp() + ":5060";

        }

        if (TerminalType.isCisco(busiTerminal.getType())) {
            if (busiTerminal.getNumber() == null) {
                uri = busiTerminal.getIp();
            } else {
                uri = busiTerminal.getNumber() + "@" + busiTerminal.getIp();
            }
        }
        if (TerminalType.isSMCNUMBER(busiTerminal.getType())) {
            uri = busiTerminal.getNumber();
        }
        if (TerminalType.isWindows(busiTerminal.getType())) {
            uri = busiTerminal.getIp();
        }

        if (TerminalType.isSMCSIP(busiTerminal.getType())) {
            uri = busiTerminal.getNumber();
        }

        return uri;
    }
}
