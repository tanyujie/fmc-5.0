package com.paradisecloud.smc;

import com.paradisecloud.com.fcm.smc.modle.SmcBridgeStatus;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.sinhy.utils.HostUtils;
import org.apache.commons.codec.binary.Base64;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.framing.CloseFrame;
import org.springframework.util.ObjectUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author nj
 * @date 2022/10/8 14:40
 */
public class SmcWebsocketClientObj {


    public static final int TIME_OUT = 1000;

    /**
     * 创建链接
     *
     * @param smcBridge
     * @return
     */
    public  static SMCWebsocketClient createWebsocketClientObj(SmcBridge smcBridge) {

        SMCWebsocketClient mwsc = null;
        try {
            try {
                if (!smcBridge.getWebsocketAvailable()) {
                    if (HostUtils.isHostReachable(smcBridge.getBusiSMC().getIp(), TIME_OUT)) {
                        String timestamp = DateUtil.convertDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
                        timestamp = timestamp.replaceAll(" ", "").replaceAll("-", "").replaceAll(":", "");
                        String username = smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getUsername();
                        String password = smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getPassword();

                        Map<String, String> meetingHeaders = smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders();
                        String ticket = smcBridge.getTicketAPIInvoker().getTicket(username, meetingHeaders);
                        String token = meetingHeaders.get("token");
                        String str = "timestamp=" + timestamp + "|" + "username=" + username + "|" + "ticket=" + ticket + "|" + "token=" + token;
                        String sha256StrJava = Sha256.getSHA256StrJava(str);

                        String url = "wss://" + smcBridge.getIp() + "/conf-portal/websocket?timestamp=" + timestamp + "&signature=" + sha256StrJava + "&username=" + username;
                        if (!ObjectUtils.isEmpty(url)) {
                            try {
                                String auth = username + ":" + password;
                                byte[] encodedAuth = Base64.encodeBase64(
                                        auth.getBytes(StandardCharsets.UTF_8));
                                String authHeader = "Basic " + new String(encodedAuth);
                                String s = "wss://"+smcBridge.getIp()+"/conf-portal/websocket";
                                mwsc = new SMCWebsocketClient(new URI(s), smcBridge.getIp(), token, ticket, username,password,smcBridge);
                                mwsc.addHeader("Authorization", authHeader);
                                mwsc.addHeader("Origin", "https://"+smcBridge.getIp());
                                connectBlocking(mwsc, smcBridge);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            smcBridge.setBridgeStatus(SmcBridgeStatus.NOT_AVAILABLE);
                        }
                    } else {
                        smcBridge.setBridgeStatus(SmcBridgeStatus.NOT_AVAILABLE);
                        smcBridge.setConnectionFailedReason("会控无法拼通HostReachable=false!");
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mwsc;
    }

    public static void connectBlocking(SMCWebsocketClient mwsc, SmcBridge smcBridge) {
        // 连接失败，会在onclose执行重试，因此不必单独处理try-catch
        if (mwsc != null) {
            try {
                if (Objects.equals(mwsc.getReadyState(), ReadyState.NOT_YET_CONNECTED)) {

                    if (mwsc.connectBlocking()) {
                        smcBridge.setWebsocketAvailable(true);
                        smcBridge.setBridgeStatus(SmcBridgeStatus.AVAILABLE);
                        SmcWebsocketContext.getInstance().put(smcBridge.getIp(), mwsc);
                    } else {
                        mwsc.closeConnection(CloseFrame.ABNORMAL_CLOSE, "连接失败，connectBlocking=false");
                        mwsc.onClose(CloseFrame.ABNORMAL_CLOSE, "连接失败，connectBlocking=false", false);
                    }
                } else if (Objects.equals(mwsc.getReadyState(), ReadyState.CLOSED) || Objects.equals(mwsc.getReadyState(), ReadyState.CLOSING)) {
                    if (mwsc.reconnectBlocking()) {
                        smcBridge.setWebsocketAvailable(true);
                        smcBridge.setBridgeStatus(SmcBridgeStatus.AVAILABLE);
                    } else {
                        mwsc.closeConnection(CloseFrame.ABNORMAL_CLOSE, "连接失败，reconnectBlocking=false");
                        mwsc.onClose(CloseFrame.ABNORMAL_CLOSE, "连接失败，reconnectBlocking=false", false);
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



}
