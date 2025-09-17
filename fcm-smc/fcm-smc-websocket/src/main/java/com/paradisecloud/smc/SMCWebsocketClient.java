package com.paradisecloud.smc;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.com.fcm.smc.modle.ParticipantState;
import com.paradisecloud.com.fcm.smc.modle.SmcBridgeStatus;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.smc.interfaces.ISmcWebSocketService;
import com.sinhy.spring.BeanFactory;
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
public class SMCWebsocketClient extends SSLWebSocketClient {

    public static final String baseUrl = "/conf-portal/websocket";

    private SmcWebSocketProcessor webSocketProcessor;
    private ISmcWebSocketService mcuNodeWebSocketService;
    private String token;

    private String ticket;

    private String username;

    private String timestamp;
    private String ip;
    private String password;

    private SmcBridge smcBridge;

    /**
     * 所有对于会议控制订阅接口的操作，需要基于Websocket建链成功后方可以进行。
     * 其中timestamp是由客户端随机生成的当前客户端时间戳，username是当前登录用户
     * 名，signature则比较复杂，这是一个由sha256算法加密后的字符串。加密规则如下
     * signature =SHA256("timestamp=" + timeStamp + "|" + "username=" + username+ "|" + "ticket=" + ticket +
     * "|" + "token=" + token)
     * 其中timeStamp传递刚才客户端生成的时间戳，username传递当前登录用户名，
     * ticket则需要向服务器进行请求。参见获取会议服务器ticket。
     * <pre>构造方法</pre>
     * 当前用户登录的token
     *
     * @param serverUri
     * @author sinhy
     * @since 2021-09-16 18:24
     */
    public SMCWebsocketClient(URI serverUri, String ip, String token, String ticket, String username, String password, SmcBridge smcBridge) {
        super(serverUri, new Draft_6455());
        this.ip = ip;
        this.token = token;
        this.ticket = ticket;
        this.username = username;
        this.password = password;
        this.smcBridge = smcBridge;
        this.mcuNodeWebSocketService = BeanFactory.getBean(ISmcWebSocketService.class);
        this.webSocketProcessor = new SmcWebSocketProcessor(this, smcBridge, this.mcuNodeWebSocketService);
    }

    public SMCWebsocketClient(URI serverUri, String ip, String username, String password, SmcBridge smcBridge) {
        super(serverUri, new Draft_6455());
        this.ip = ip;
        this.username = username;
        this.password = password;
        this.smcBridge = smcBridge;
    }


    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        TopicMessage subscribe1 = new TopicMessage();
        String connectMessage = subscribe1.getConnectMessage();
        this.send(connectMessage);
        TopicMessage subscribeStatus = new TopicMessage();
        String statusMessage = subscribeStatus.getConferenceStatus();
        this.send(statusMessage);

    }


    @Override
    public void onMessage(String message) {
        webSocketProcessor.processMessage(message);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        SmcWebsocketReconnecter.getInstance().add(smcBridge);
        smcBridge.setBridgeStatus(SmcBridgeStatus.NOT_AVAILABLE);
        smcBridge.setWebsocketAvailable(false);

    }

    @Override
    public void onError(Exception e) {
        log.error(" SMC-WEBSOCKET onError" + e.getMessage());
        onClose(CloseFrame.ABNORMAL_CLOSE, CauseUtils.getRootCause(e), false);
        SmcWebsocketReconnecter.getInstance().add(smcBridge);
    }

    public SmcWebSocketProcessor getWebSocketProcessor() {
        return webSocketProcessor;
    }

    public  void sendMessage(String message){
        this.send(message);
    }


}
