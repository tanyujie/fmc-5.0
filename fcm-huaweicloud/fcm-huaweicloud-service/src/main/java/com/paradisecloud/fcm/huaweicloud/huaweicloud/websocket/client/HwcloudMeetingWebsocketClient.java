package com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.client;


import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudMeetingBridge;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.event.SubscriptionTypeEnum;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.HwcloudBridgeStatus;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.utils.StringUtils;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.interfaces.IHwcloudWebSocketService;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.Base64Utils;
import com.sinhy.utils.CauseUtils;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;


/**
 * @author nj
 * @date 2022/8/19 11:41
 */
@Slf4j
public class HwcloudMeetingWebsocketClient extends SSLWebSocketClient {


    private final HwcloudWebSocketProcessor webSocketProcessor;
    private final IHwcloudWebSocketService mcuNodeWebSocketService;
    private final HwcloudMeetingBridge hwcloudMeetingBridge;

    private String token;

    /**
     * 所有对于会议控制订阅接口的操作，需要基于Websocket建链成功后方可以进行。
     */
    public HwcloudMeetingWebsocketClient(URI serverUri, HwcloudMeetingBridge hwcloudMeetingBridge) {
        super(serverUri, new Draft_6455());
        this.hwcloudMeetingBridge = hwcloudMeetingBridge;
        this.mcuNodeWebSocketService = BeanFactory.getBean(IHwcloudWebSocketService.class);
        this.webSocketProcessor = new HwcloudWebSocketProcessor(this, hwcloudMeetingBridge, this.mcuNodeWebSocketService);
    }


    @Override
    public void onOpen(ServerHandshake serverHandshake) {

        log.info("会议【"+hwcloudMeetingBridge.getConfID()+"】建立连接");

        SubscribeData subscribeData = new SubscribeData();
        subscribeData.setSubscribeType(SubscriptionTypeEnum.getCodeList());
        subscribeData.setConferenceID(hwcloudMeetingBridge.getConfID());
        subscribeData.setConfToken("Basic " +Base64Utils.encode(hwcloudMeetingBridge.getTokenInfo().getToken()));
        String confToken= "Basic " +Base64Utils.encode(hwcloudMeetingBridge.getTokenInfo().getToken());
        //\"ConfBasicInfoNotify\",\"ConfDynamicInfoNotify\",\"ParticipantsNotify\",\"AttendeesNotify\",\"SpeakerChangeNotify\",\"NetConditionNotify\",\"CustomMultiPicNotify\",\"InviteResultNotify\",\"InterpreterGroupNotify\",\"NetworkQualityNotify\",\"WaitingListNotify\",\"OperationResultNotify\",\"BreakoutSettingNotify\",\"DynamicBreakoutSubConfsNotify\",\"DynamicBCAttendeesNotify\",\"ViewPriorityListNotify\

        String a="{\"subscribeType\":[\"ConfBasicInfoNotify\",\"ConfDynamicInfoNotify\",\"ParticipantsNotify\",\"AttendeesNotify\",\"SpeakerChangeNotify\",\"NetConditionNotify\",\"CustomMultiPicNotify\",\"InviteResultNotify\",\"InterpreterGroupNotify\",\"NetworkQualityNotify\",\"WaitingListNotify\"],\"confToken\""+":\""+confToken+"\"}";


        JSONObject sub = new JSONObject();
        sub.put("action", "Subscribe");
        sub.put("sequence", StringUtils.generateNumericSequence(20, 30));
        sub.put("data", a);
        this.send(sub.toJSONString());

    }


    @Override
    public void onMessage(String message) {
        webSocketProcessor.processMessage(message);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        hwcloudMeetingBridge.setBridgeStatus(HwcloudBridgeStatus.NOT_AVAILABLE,token);
        hwcloudMeetingBridge.setWebsocketAvailable(false);
        if (!hwcloudMeetingBridge.isDeleted()) {
            HwcloudMeetingWebsocketReconnecter.getInstance().add(hwcloudMeetingBridge);
        }
        log.info("会议【"+hwcloudMeetingBridge.getConfID()+"】连接关闭");

    }

    @Override
    public void onError(Exception e) {
        log.error(hwcloudMeetingBridge.getConfID()+" Hwcloud-WEBSOCKET onError" + e.getMessage());
        onClose(CloseFrame.ABNORMAL_CLOSE, CauseUtils.getRootCause(e), false);
        HwcloudMeetingWebsocketReconnecter.getInstance().add(hwcloudMeetingBridge);
    }

    public HwcloudWebSocketProcessor getWebSocketProcessor() {
        return webSocketProcessor;
    }

    public void sendMessage(String message) {
        this.send(message);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public HwcloudMeetingBridge getHwcloudBridge() {
        return hwcloudMeetingBridge;
    }
}
