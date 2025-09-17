package com.paradisecloud.fcm.fme.attendee.model.busiprocessor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.dao.mapper.BusiTransServerMapper;
import com.paradisecloud.fcm.dao.model.BusiTransServer;
import com.paradisecloud.fcm.fme.attendee.model.attendeemessage.AttendeeMessageQueue;
import com.paradisecloud.fcm.fme.attendee.model.attendeemessage.CallFailedAttendeeMessage;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author nj
 * @date 2023/4/11 17:15
 */
public class RtspCallAttendeeProcessor extends AttendeeBusiProcessor {
    public static final String HTTP = "http://";
    public static final String START_STREAM = ":9900/start_stream";
    public static final int HTTPCODE = 200;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String rtspUri;

    public RtspCallAttendeeProcessor(String contextKey, String attendeeId, String rtspUri) {
        super(contextKey, attendeeId);
        this.rtspUri = rtspUri;
    }


    public RtspCallAttendeeProcessor(Attendee attendee, String rtspUri) {
        super(attendee);
        this.rtspUri = rtspUri;
    }

    @Override
    public void process() {

        if (targetAttendee == null) {
            return;
        }

        if (targetAttendee.getCallRequestSentTime() != null && (System.currentTimeMillis() - targetAttendee.getCallRequestSentTime()) < 10 * 1000)
        {
            StringBuilder messageTip = new StringBuilder();
            messageTip.append("【").append(targetAttendee.getName()).append("】重呼请求已发起，请耐心等待响应结果，期间不要频繁发起！");
            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
            return;
        }

        synchronized (targetAttendee) {
            if (conferenceContext.isEnd() || targetAttendee.isMeetingJoined()) {
                return;
            }
            // 还原主动挂断为false
            targetAttendee.setHangUp(false);

            // 清空与会者UUID
            targetAttendee.setParticipantUuid(null);

            FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceContext(conferenceContext);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("rtsp_uri", rtspUri);
            jsonObject.put("meeting_room", conferenceContext.getConferenceNumber());
            jsonObject.put("fme_ip", fmeBridge.getBusiFme().getIp());
            jsonObject.put("display_name", targetAttendee.getName());
            jsonObject.put("session_id", targetAttendee.getId());


            BusiTransServerMapper busiTransServerMapper = BeanFactory.getBean(BusiTransServerMapper.class);

            List<BusiTransServer> busiTransServers = busiTransServerMapper.selectBusiTransServerList(new BusiTransServer());
            if(CollectionUtils.isEmpty(busiTransServers)){
                return;
            }
            BusiTransServer busiTransServer = busiTransServers.get(0);
            String ip = busiTransServer.getIp();


            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());
            headers.add("Accept-Charset", "UTF-8");
            HttpEntity<String> formEntity = new HttpEntity<>(JSON.toJSONString(jsonObject), headers);

            ResponseEntity<String> stringResponseEntity = null;
            try {
                stringResponseEntity = restTemplate.postForEntity(HTTP + ip + START_STREAM, formEntity, String.class);
                int statusCodeValue = stringResponseEntity.getStatusCodeValue();
                if (statusCodeValue == HTTPCODE) {
                    String body = stringResponseEntity.getBody();
                    JSONObject parseObject = JSONObject.parseObject(body);
                    Object data = parseObject.get("data");
                    JSONObject sessionJSON = JSONObject.parseObject(JSONObject.toJSONString(data));
                    Object session_id = sessionJSON.get("session_id");
                    targetAttendee.setRemoteParty(session_id + "@" + ip);
                    conferenceContext.addAttendeeToRemotePartyMap(targetAttendee);
                    targetAttendee.setCallRequestSentTime(System.currentTimeMillis());
                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("【").append(targetAttendee.getName()).append("】呼叫已发起！");
                    WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

                }
            } catch (RestClientException e) {
                e.printStackTrace();
                logger.error("呼叫与会者发生异常-doCall：" + targetAttendee, e);
                StringBuilder messageTip = new StringBuilder();
                messageTip.append("【").append(targetAttendee.getName()).append("】呼叫失败：").append(e.getMessage());
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip);
                AttendeeMessageQueue.getInstance().put(new CallFailedAttendeeMessage(targetAttendee));
            }

        }

    }
}
