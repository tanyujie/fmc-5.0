package com.paradisecloud.fcm.web.controller.smc3;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextUtils;
import com.paradisecloud.smc3.invoker.SmcConferencesInvoker;
import com.paradisecloud.smc3.model.response.SmcParitipantsStateRep;
import com.paradisecloud.smc3.websocket.client.SMC3WebsocketClient;
import com.paradisecloud.smc3.websocket.client.Smc3WebSocketProcessor;
import com.paradisecloud.smc3.websocket.client.Smc3WebsocketContext;
import com.paradisecloud.smc3.websocket.client.TopicMessage;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.util.Strings;
import org.java_websocket.enums.ReadyState;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * smc3实时信息Controller
 *
 */
@RestController
@RequestMapping("/smc3")
@Tag(name = "smc3信息")
@Slf4j
public class BusiSmc3RealTimeInfoController extends BaseController
{


    /**
     * 订阅实时信息
     */
    @PostMapping("/sub/{conferenceId}/{participantId}/{sub}")
    public RestResponse addSubcribe(@PathVariable String conferenceId,@PathVariable String participantId,@PathVariable String sub) throws URISyntaxException, InterruptedException {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);

        conferenceId = conferenceContext.getSmc3conferenceId();
        Smc3Bridge smcBridge = conferenceContext.getSmc3Bridge();

        SMC3WebsocketClient mwsc = getSmcWebsocketClient(smcBridge);

        String tokenByConferencesId = smcBridge.getSmcConferencesInvoker().getTokenByConferencesId(conferenceId, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

        List<String> participantIdList = new ArrayList<>();
        long groupId = System.currentTimeMillis();
        participantIdList.add(participantId);
        smcBridge.getSmcConferencesInvoker().realTimeInfoGroup(conferenceId, groupId + "", participantIdList, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        String destination = "/topic/conferences/" + conferenceId + "/participants/groups/" + groupId + "/realTimeInfo";
        TopicMessage subscribe = new TopicMessage("SUBSCRIBE", sub, tokenByConferencesId, destination);
        String subscribeMessage = subscribe.getSubscribeMessage();
        mwsc.sendMessage(subscribeMessage);

        JSONObject jsonObject = Smc3WebSocketProcessor.getRealTimeMap().get(participantId);
        return RestResponse.success(jsonObject);

    }

    /**
     * getRealTimeInfo
     * @param participantId
     * @return
     */
    @GetMapping("/getRealTimeInfo/participant/{participantId}")
    public RestResponse getRealTimeInfoParticipantOnly(@PathVariable String participantId){

        JSONObject jsonObject = Smc3WebSocketProcessor.getRealTimeMap().get(participantId);
        return RestResponse.success(jsonObject);
    }



    /**
     * 订阅实时信息
     */
    @PostMapping("/sub/{conferenceId}/paritipants/{sub}")
    public RestResponse addSubcribe(@PathVariable String conferenceId,@PathVariable String sub) throws URISyntaxException, InterruptedException {


        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);

        conferenceId = conferenceContext.getSmc3conferenceId();
        Smc3Bridge smcBridge = conferenceContext.getSmc3Bridge();

        SMC3WebsocketClient websocketClient = getSmcWebsocketClient(smcBridge);
        SmcConferencesInvoker smcConferencesInvoker = smcBridge.getSmcConferencesInvoker();

        String tokenByConferencesId = Smc3WebsocketContext.getConferenceTokenId(conferenceId);
        if(Strings.isBlank(tokenByConferencesId)){
            tokenByConferencesId = smcConferencesInvoker.getTokenByConferencesId(conferenceId, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            Smc3WebsocketContext.setConferenceTokenId(conferenceId,tokenByConferencesId);
        }

        List<String> participantIdList = new ArrayList<>();
        long groupId = System.currentTimeMillis();
//        String res = smcBridge.getSmcParticipantsInvoker().getConferencesParticipantsState(conferenceId, 0, 1000, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
//        SmcParitipantsStateRep smcParitipantsStateRep = JSON.parseObject(res, SmcParitipantsStateRep.class);
//        if(smcParitipantsStateRep!=null){
//            List<SmcParitipantsStateRep.ContentDTO> content = smcParitipantsStateRep.getContent();
//            if(!CollectionUtils.isEmpty(content)){
//                for (SmcParitipantsStateRep.ContentDTO contentDTO : content) {
//
//                    if(contentDTO.getState().getOnline()){
//                        String participantId = contentDTO.getState().getParticipantId();
//                        String name = contentDTO.getGeneralParam().getName();
//                        Smc3RealTimeInfoProcessorMessage.getSmcParticipantNames().put(participantId,name);
//                        //请求订阅
//                        participantIdList.add(participantId);
//                    }
//
//                }
//            }
//        }
        Smc3ConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {

            if(a.isMeetingJoined()&&a.getSmcParticipant().getState().getOnline()){
                        String participantId = a.getParticipantUuid();
                        String name = a.getName();
                        Smc3WebSocketProcessor.getSmcParticipantNames().put(participantId,name);
                        participantIdList.add(participantId);
            }

        });

        log.info("participantsGroups:"+ JSONObject.toJSON(participantIdList));
        smcBridge.getSmcConferencesInvoker().realTimeInfoGroup(conferenceId, groupId + "", participantIdList, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        String destination = "/topic/conferences/" + conferenceId + "/participants/groups/" + groupId + "/realTimeInfo";
        TopicMessage subscribe = new TopicMessage("SUBSCRIBE", sub, tokenByConferencesId, destination);
        String subscribeMessage = subscribe.getSubscribeMessage();
        websocketClient.sendMessage(subscribeMessage);
        Smc3WebSocketProcessor.getRealTimeGroupMap().put(conferenceId+sub,groupId);

        return RestResponse.success(Smc3WebSocketProcessor.getConferenceParitipantsRealTimeMap().get(conferenceId));
    }

    /**
     * getRealTimeInfo
     * @param conferenceId
     * @return
     */
    @GetMapping("/getRealTimeInfo/{conferenceId}/participants/{sub}")
    public RestResponse getRealTimeInfoParticipants(@PathVariable String conferenceId,@PathVariable String sub){
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        conferenceId = conferenceContext.getSmc3conferenceId();

        Map<String, JSONObject> stringJSONObjectMap = Smc3WebSocketProcessor.getConferenceParitipantsRealTimeMap().get(conferenceId+sub);
        if(CollectionUtils.isEmpty(stringJSONObjectMap)){
            return RestResponse.success(Smc3WebSocketProcessor.getConferenceParitipantsRealTimeMap().get(conferenceId));
        }
        return RestResponse.success(stringJSONObjectMap);
    }



    private SMC3WebsocketClient getSmcWebsocketClient(Smc3Bridge smcBridge) throws URISyntaxException, InterruptedException {
        SMC3WebsocketClient mwsc = Smc3WebsocketContext.getSmcWebsocketClientMap().get(smcBridge.getIp());
        if(mwsc==null|| (!mwsc.getReadyState().equals(ReadyState.OPEN))){
            String username = smcBridge.getBusiSMC().getMeetingUsername();
            String password = smcBridge.getBusiSMC().getMeetingPassword();
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            String s = "wss://"+ smcBridge.getIp()+"/conf-portal/websocket";
            mwsc = new SMC3WebsocketClient(new URI(s), smcBridge.getIp(), "token", "ticket", username,password, smcBridge);
            mwsc.addHeader("Authorization", authHeader);
            mwsc.addHeader("Origin", "https://"+ smcBridge.getIp());
            mwsc.connectBlocking();
            Smc3WebsocketContext.getSmcWebsocketClientMap().put(smcBridge.getIp(),mwsc);
        }
        return mwsc;
    }


    /**
     * 订阅会控状态
     */
    @PostMapping("/sub/state/{conferenceId}/{sub}")
    public RestResponse addSubcribeConferenceControl(@PathVariable String conferenceId,@PathVariable String sub)  {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);

        conferenceId = conferenceContext.getSmc3conferenceId();
        Smc3Bridge smcBridge = conferenceContext.getSmc3Bridge();
        try {
            SMC3WebsocketClient mwsc = getSmcWebsocketClient(smcBridge);
            String tokenByConferencesId = smcBridge.getSmcConferencesInvoker().getTokenByConferencesId(conferenceId, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            String sd = "/topic/conferences/" + conferenceId;
            TopicMessage subscribe= new TopicMessage("SUBSCRIBE", sub, tokenByConferencesId, sd);
            String subscribeMessage = subscribe.getSubscribeMessage();
            mwsc.sendMessage(subscribeMessage);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return RestResponse.success();
    }


    /**
     * 订阅会场状态
     */
    @PostMapping("/sub/changeList/{conferenceId}/{sub}")
    public RestResponse addSubcribeChangeList(@PathVariable String conferenceId,@PathVariable String sub)  {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);

        conferenceId = conferenceContext.getSmc3conferenceId();
        Smc3Bridge smcBridge = conferenceContext.getSmc3Bridge();
        try {
            SMC3WebsocketClient mwsc = getSmcWebsocketClient(smcBridge);
            String tokenByConferencesId = smcBridge.getSmcConferencesInvoker().getTokenByConferencesId(conferenceId,smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            long groupId = System.currentTimeMillis();
            //订阅变化changeList
            String res = smcBridge.getSmcParticipantsInvoker().getConferencesParticipantsState(conferenceId, 0, 1000, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            SmcParitipantsStateRep smcParitipantsStateRep = JSON.parseObject(res, SmcParitipantsStateRep.class);
            if(smcParitipantsStateRep!=null){
                List<SmcParitipantsStateRep.ContentDTO> content = smcParitipantsStateRep.getContent();
                List<String> participantIdList=new ArrayList<>();
                if(!CollectionUtils.isEmpty(content)){
                    for (SmcParitipantsStateRep.ContentDTO contentDTO : content) {
                        String participantId = contentDTO.getState().getParticipantId();
                        //请求订阅
                        if(contentDTO.getState().getOnline()){
                            participantIdList.add(participantId);
                            logger.info("订阅会场："+participantId);
                        }

                    }
                    smcBridge.getSmcConferencesInvoker().changeListGroup(conferenceId,groupId+"",participantIdList,10,smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                    String destination="/topic/conferences/"+conferenceId+"/participants/groups/"+groupId;
                    TopicMessage subscribe = new TopicMessage("SUBSCRIBE", sub, tokenByConferencesId, destination);
                    String subscribeMessage = subscribe.getSubscribeMessage();
                    mwsc.sendMessage(subscribeMessage);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RestResponse.success();
    }





    /**
     * DELETE /online/conferences/{conferenceId}/participants/groups/{groupId}/
     realTimeInfo
     *  CANCEL
     */
    @DeleteMapping("/realTimeInfo/{conferenceId}/cancel/{sub}")
    public RestResponse cancelRealTimeInfo(@PathVariable String conferenceId,@PathVariable String sub)  {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);

        conferenceId = conferenceContext.getSmc3conferenceId();
        Smc3Bridge smcBridge = conferenceContext.getSmc3Bridge();
        long groupId = (long)Smc3WebSocketProcessor.getRealTimeGroupMap().get(conferenceId + sub);
        smcBridge.getSmcConferencesInvoker().cancelRealTimeInfo(conferenceId,groupId,smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        try {
            SMC3WebsocketClient mwsc = getSmcWebsocketClient(smcBridge);
            mwsc.sendMessage(TopicMessage.getUNSubscribeMessage(sub));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return  RestResponse.success();
    }


}
