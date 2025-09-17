package com.paradisecloud.smc3.websocket.client;


import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.websocket.interfaces.ISmc3WebSocketService;
import com.paradisecloud.smc3.model.SmcBridgeStatus;
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
public class SMC3WebsocketClient extends SSLWebSocketClient {

    public static final String baseUrl = "/conf-portal/websocket";

    private Smc3WebSocketProcessor webSocketProcessor;
    private ISmc3WebSocketService mcuNodeWebSocketService;
    private String token;

    private String ticket;

    private String username;

    private String timestamp;
    private String ip;
    private String password;

    private Smc3Bridge smcBridge;

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
    public SMC3WebsocketClient(URI serverUri, String ip, String token, String ticket, String username, String password, Smc3Bridge smcBridge) {
        super(serverUri, new Draft_6455());
        this.ip = ip;
        this.token = token;
        this.ticket = ticket;
        this.username = username;
        this.password = password;
        this.smcBridge = smcBridge;
        this.mcuNodeWebSocketService = BeanFactory.getBean(ISmc3WebSocketService.class);
        this.webSocketProcessor = new Smc3WebSocketProcessor(this, smcBridge, this.mcuNodeWebSocketService);
    }

    public SMC3WebsocketClient(URI serverUri, String ip, String username, String password, Smc3Bridge smcBridge) {
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
        smcBridge.setBridgeStatus(SmcBridgeStatus.NOT_AVAILABLE,this.getToken());
        smcBridge.setWebsocketAvailable(false);
        Smc3WebsocketReconnecter.getInstance().add(smcBridge);


    }

    @Override
    public void onError(Exception e) {
        log.error(" SMC-WEBSOCKET onError" + e.getMessage());
        onClose(CloseFrame.ABNORMAL_CLOSE, CauseUtils.getRootCause(e), false);
        Smc3WebsocketReconnecter.getInstance().add(smcBridge);
    }

    public Smc3WebSocketProcessor getWebSocketProcessor() {
        return webSocketProcessor;
    }

    public  void sendMessage(String message){
        this.send(message);
    }

    public String getToken() {
        return token;
    }

    public Smc3Bridge getSmcBridge() {
        return smcBridge;
    }
}
