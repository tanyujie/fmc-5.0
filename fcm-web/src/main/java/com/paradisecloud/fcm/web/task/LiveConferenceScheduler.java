package com.paradisecloud.fcm.web.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.dao.model.BusiLive;
import com.paradisecloud.fcm.dao.model.BusiLiveSetting;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.terminal.fs.cache.*;
import com.paradisecloud.fcm.zte.cache.McuZteWebSocketMessagePusher;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.sinhy.http.HttpObjectCreator;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;
import com.sinhy.utils.ThreadUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class LiveConferenceScheduler extends Thread implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private HttpRequester httpRequester = HttpObjectCreator.getInstance().createHttpRequester(false);

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }

    @Override
    public void run() {
        logger.info("监控观看直播终端数启动成功！");
        ThreadUtils.sleep(60 * 1000);
        while (true) {
            try {
                Set<String> ipSet = new TreeSet<>();
                for (BusiLive value : LiveCache.getInstance().values()) {
                    ipSet.add(value.getIp());
                }
                for (BusiLiveSetting value : LiveSettingCache.getInstance().values()) {
                    String url = value.getUrl();
                    String ip = StringUtils.substringBetween(url, "://", "/");
                    Boolean isPing = pingIp(ip);
                    if (isPing) {
                        ipSet.add(ip);
                    }
                }
                LiveBridgeCache.getInstance().clearLiveUrlTerminalCount();
                if (ipSet != null && ipSet.size() > 0) {
                    for (String ip : ipSet) {
                        setLiveTerminalCount(ip);
                    }
                }

                Collection<BaseConferenceContext> conferenceContexts = AllConferenceContextCache.getInstance().values();
                if (conferenceContexts != null && conferenceContexts.size() > 0) {
                    for (BaseConferenceContext conferenceContext : conferenceContexts) {
                        Map<String, Object> data = new HashMap<>();
                        if (conferenceContext.isStreaming()) {
                            liveTerminalCount(conferenceContext);
                            data.put("liveTerminalCount", conferenceContext.getLiveTerminalCount());
                            data.put("id", conferenceContext.getId());
                        } else {
                            conferenceContext.setLiveTerminalCount(0);
                        }

                        if (conferenceContext instanceof ConferenceContext) {
                            ConferenceContext conferenceContext1 = (ConferenceContext) conferenceContext;
                            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext1, WebsocketMessageType.LIVE_TERMINAL_COUNT, data);
                        } else if (conferenceContext instanceof McuZjConferenceContext) {
                            McuZjConferenceContext conferenceContext1 = (McuZjConferenceContext) conferenceContext;
                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext1, WebsocketMessageType.LIVE_TERMINAL_COUNT, data);
                        } else if (conferenceContext instanceof McuPlcConferenceContext) {
                            McuPlcConferenceContext conferenceContext1 = (McuPlcConferenceContext) conferenceContext;
                            McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext1, WebsocketMessageType.LIVE_TERMINAL_COUNT, data);
                        } else if (conferenceContext instanceof McuKdcConferenceContext) {
                            McuKdcConferenceContext conferenceContext1 = (McuKdcConferenceContext) conferenceContext;
                            McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext1, WebsocketMessageType.LIVE_TERMINAL_COUNT, data);
                        } else if (conferenceContext instanceof Smc3ConferenceContext) {
                            Smc3ConferenceContext conferenceContext1 = (Smc3ConferenceContext) conferenceContext;
                            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext1, WebsocketMessageType.LIVE_TERMINAL_COUNT, data);
                        } else if (conferenceContext instanceof Smc2ConferenceContext) {
                            Smc2ConferenceContext conferenceContext1 = (Smc2ConferenceContext) conferenceContext;
                            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext1, WebsocketMessageType.LIVE_TERMINAL_COUNT, data);
                        } else if (conferenceContext instanceof McuZteConferenceContext) {
                            McuZteConferenceContext conferenceContext1 = (McuZteConferenceContext) conferenceContext;
                            McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext1, WebsocketMessageType.LIVE_TERMINAL_COUNT, data);
                        }
                    }
                } else {
                    LiveBridgeCache.getInstance().clearLiveConferenceTerminalCount();
                }
            } catch (Throwable e) {
                logger.error("监控观看直播终端数出错", e);
            } finally {
                ThreadUtils.sleep(30 * 1000);
            }
        }
    }

    private void liveTerminalCount(BaseConferenceContext conferenceContext) {
        String streamingUrl = conferenceContext.getStreamingUrl();

        Integer liveUrlTerminalCount = LiveBridgeCache.getInstance().getLiveUrlTerminalCount(streamingUrl);
        LiveBridgeCache.getInstance().setLiveConferenceTerminalCount(conferenceContext.getId(), liveUrlTerminalCount);
        conferenceContext.setLiveTerminalCount(liveUrlTerminalCount);
    }

    private void setLiveTerminalCount(String ip) {
        // http://172.16.100.169:1985/api/v1/clients/
        String httpUrl = "http://" + ip + ":1985/api/v1/streams/";
        GenericValue<JSONObject> genericValue = new GenericValue<>();
        httpRequester.get(httpUrl, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                try {
                    String s = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
                    com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(s);
                    genericValue.setValue(jsonObject);
                } catch (IOException var4) {
                }
            }
        });
        JSONObject jsonObject = genericValue.getValue();
        if (jsonObject != null) {
            String streams = String.valueOf(jsonObject.get("streams"));
            JSONArray jsonArray = JSONArray.parseArray(streams);
            Map<String, Integer> map = new HashMap<>();
            if (jsonArray != null && jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObj = (JSONObject) jsonArray.get(i);
                    JSONObject publish = jsonObj.getJSONObject("publish");
                    Boolean active = publish.getBoolean("active");
                    if (active) {
                        String tcUrl = jsonObj.getString("tcUrl");
                        String url = jsonObj.getString("url");
                        Long clients = jsonObj.getLong("clients");
                        String replaceUrl = tcUrl.replace(":1935", "");
                        replaceUrl = replaceUrl.replace("/live", url);
                        if (clients != 0) {
                            clients = clients - 1;
                        }
                        map.put(replaceUrl, Math.toIntExact(clients));
                    }
                }
            }

            if (map != null && map.size() > 0) {
                for (String key : map.keySet()) {
                    LiveBridgeCache.getInstance().setLiveUrlTerminalCount(key, map.get(key));
                }
            }
        }
    }

    /**
     * ping服务器Ip
     *
     * @param ip
     * @return
     */
    public Boolean pingIp(String ip) {
        if (null == ip || 0 == ip.length()) {
            return false;
        }

        try {
            boolean reachable = InetAddress.getByName(ip).isReachable(500);//超过3秒
            if (reachable) {
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

}
