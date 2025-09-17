package com.paradisecloud.fcm.web.controller.smc;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.ConstAPI;
import com.paradisecloud.com.fcm.smc.modle.DetailConference;
import com.paradisecloud.com.fcm.smc.modle.ParticipantState;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.paradisecloud.fcm.smc.cache.modle.SmcConferencesInvoker;
import com.paradisecloud.fcm.web.utils.AuthenticationUtil;
import com.paradisecloud.smc.SMCWebsocketClient;
import com.paradisecloud.smc.SmcWebsocketContext;
import com.paradisecloud.smc.TopicMessage;
import com.paradisecloud.smc.processormessage.RealTimeInfoProcessorMessage;
import com.paradisecloud.smc.service.IBusiSmcService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.java_websocket.enums.ReadyState;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.smc.dao.model.BusiSmc;

import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Resource;

/**
 * smc信息Controller
 *
 * @author liuxilong
 * @date 2022-08-25
 */
@RestController
@RequestMapping("/smc")
@Tag(name = "smc信息")
@Slf4j
public class BusiSmcController extends BaseController
{
    @Resource
    private IBusiSmcService busiSmcService;



    /**
     * 查询smc信息列表
     */
    @GetMapping("/list")
    public RestResponse list(BusiSmc busiSmc)
    {

        startPage();
        List<BusiSmc> busiSmcs = busiSmcService.selectBusiSmcListNoP(busiSmc);

        if(!CollectionUtils.isEmpty(busiSmcs)){
            Map<Long, SmcBridge> smcBridgeMap = SmcBridgeCache.getInstance().getSmcBridgeMap();

            for (BusiSmc smc : busiSmcs) {
                SmcBridge smcBridge = smcBridgeMap.get(smc.getId());
                if(smcBridge!=null){
                    smc.setStatus(smcBridge.getBusiSMC().getStatus());
                }


            }

        }
        return getDataTable(busiSmcs);
    }

    /**
     * 获取smc信息详细信息
     */
    @GetMapping(value = "/{id}")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        BusiSmc busiSmc = busiSmcService.selectBusiSmcById(id);
        busiSmc.setPassword(null);
        busiSmc.setMeetingPassword(null);
        return RestResponse.success(busiSmc);
    }

    /**
     * 新增smc信息
     */
    @PostMapping
    public RestResponse add(@RequestBody BusiSmc busiSmc)
    {
        return toAjax(busiSmcService.insertBusiSmc(busiSmc));
    }

    /**
     * 修改smc信息
     */
    @PutMapping
    public RestResponse edit(@RequestBody BusiSmc busiSmc)
    {
        return toAjax(busiSmcService.updateBusiSmc(busiSmc));
    }

    /**
     * 删除smc信息
     */
    @DeleteMapping("/{id}")
    public RestResponse remove(@PathVariable Long id)
    {
        return toAjax(busiSmcService.deleteBusiSmcById(id));
    }


    /**
     * 订阅实时信息
     */
    @PostMapping("/sub/{conferenceId}/{participantId}/{sub}")
    public RestResponse addSubcribe(@PathVariable String conferenceId,@PathVariable String participantId,@PathVariable String sub) throws URISyntaxException, InterruptedException {

        SmcBridge smcBridge =  SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);

        SMCWebsocketClient mwsc = getSmcWebsocketClient(smcBridge);

        String tokenByConferencesId = smcBridge.getSmcConferencesInvoker().getTokenByConferencesId(conferenceId, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

        List<String> participantIdList = new ArrayList<>();
        long groupId = System.currentTimeMillis();
        participantIdList.add(participantId);
        smcBridge.getSmcConferencesInvoker().realTimeInfoGroup(conferenceId, groupId + "", participantIdList, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        String destination = "/topic/conferences/" + conferenceId + "/participants/groups/" + groupId + "/realTimeInfo";
        TopicMessage subscribe = new TopicMessage("SUBSCRIBE", sub, tokenByConferencesId, destination);
        String subscribeMessage = subscribe.getSubscribeMessage();
        mwsc.sendMessage(subscribeMessage);

        JSONObject jsonObject = RealTimeInfoProcessorMessage.getRealTimeMap().get(participantId);
        return RestResponse.success(jsonObject);

    }

    /**
     * getRealTimeInfo
     * @param participantId
     * @return
     */
    @GetMapping("/getRealTimeInfo/participant/{participantId}")
    public RestResponse getRealTimeInfoParticipantOnly(@PathVariable String participantId){

        JSONObject jsonObject = RealTimeInfoProcessorMessage.getRealTimeMap().get(participantId);
        return RestResponse.success(jsonObject);
    }



    /**
     * 订阅实时信息
     */
    @PostMapping("/sub/{conferenceId}/paritipants/{sub}")
    public RestResponse addSubcribe(@PathVariable String conferenceId,@PathVariable String sub) throws URISyntaxException, InterruptedException {

        SmcBridge smcBridge =  SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);


        SMCWebsocketClient websocketClient = getSmcWebsocketClient(smcBridge);
        SmcConferencesInvoker smcConferencesInvoker = smcBridge.getSmcConferencesInvoker();
        String conferencesById = smcConferencesInvoker.getConferencesById(conferenceId, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        if(StringUtils.isBlank(conferencesById)){
            return RestResponse.fail(conferencesById);
        }
        if(conferencesById.contains(ConstAPI.CONFERENCE_NOT_EXIST)){
            return RestResponse.fail("CONFERENCE_NOT_EXIST");
        }
        String tokenByConferencesId = smcConferencesInvoker.getTokenByConferencesId(conferenceId, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        List<String> participantIdList = new ArrayList<>();
        long groupId = System.currentTimeMillis();
        String res = smcBridge.getSmcParticipantsInvoker().getConferencesParticipantsState(conferenceId, 0, 1000, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        SmcParitipantsStateRep smcParitipantsStateRep = JSON.parseObject(res, SmcParitipantsStateRep.class);
        if(smcParitipantsStateRep!=null){
            List<SmcParitipantsStateRep.ContentDTO> content = smcParitipantsStateRep.getContent();
            if(!CollectionUtils.isEmpty(content)){
                for (SmcParitipantsStateRep.ContentDTO contentDTO : content) {

                    if(contentDTO.getState().getOnline()){
                        String participantId = contentDTO.getState().getParticipantId();
                        String name = contentDTO.getGeneralParam().getName();
                        RealTimeInfoProcessorMessage.getSmcParticipantNames().put(participantId,name);
                        //请求订阅
                        participantIdList.add(participantId);
                    }

                }
            }
        }
        log.info("participantsGroups:"+ JSONObject.toJSON(participantIdList));
        smcBridge.getSmcConferencesInvoker().realTimeInfoGroup(conferenceId, groupId + "", participantIdList, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        String destination = "/topic/conferences/" + conferenceId + "/participants/groups/" + groupId + "/realTimeInfo";
        TopicMessage subscribe = new TopicMessage("SUBSCRIBE", sub, tokenByConferencesId, destination);
        String subscribeMessage = subscribe.getSubscribeMessage();
        websocketClient.sendMessage(subscribeMessage);
        RealTimeInfoProcessorMessage.getRealTimeGroupMap().put(conferenceId+sub,groupId);
        return RestResponse.success(RealTimeInfoProcessorMessage.getConferenceParitipantsRealTimeMap().get(conferenceId+sub));
    }

    /**
     * getRealTimeInfo
     * @param conferenceId
     * @return
     */
    @GetMapping("/getRealTimeInfo/{conferenceId}/participants/{sub}")
    public RestResponse getRealTimeInfoParticipants(@PathVariable String conferenceId,@PathVariable String sub){
        return RestResponse.success(RealTimeInfoProcessorMessage.getConferenceParitipantsRealTimeMap().get(conferenceId+sub));
    }



    private SMCWebsocketClient getSmcWebsocketClient(SmcBridge smcBridge) throws URISyntaxException, InterruptedException {
        SMCWebsocketClient mwsc = SmcWebsocketContext.getSmcWebsocketClientMap().get(smcBridge.getIp());
        if(mwsc==null|| (!mwsc.getReadyState().equals(ReadyState.OPEN))){
            String username = smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getUsername();
            String password = smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getPassword();
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            String s = "wss://"+ smcBridge.getIp()+"/conf-portal/websocket";
            mwsc = new SMCWebsocketClient(new URI(s), smcBridge.getIp(), "token", "ticket", username,password, smcBridge);
            mwsc.addHeader("Authorization", authHeader);
            mwsc.addHeader("Origin", "https://"+ smcBridge.getIp());
            mwsc.connectBlocking();
            SmcWebsocketContext.getSmcWebsocketClientMap().put(smcBridge.getIp(),mwsc);
        }
        return mwsc;
    }


    /**
     * 订阅会控状态
     */
    @PostMapping("/sub/state/{conferenceId}/{sub}")
    public RestResponse addSubcribeConferenceControl(@PathVariable String conferenceId,@PathVariable String sub)  {

        SmcBridge smcBridge =  SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
        try {
            SMCWebsocketClient mwsc = getSmcWebsocketClient(smcBridge);
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

//        Long deptId = AuthenticationUtil.getDeptId();
//        SmcBridge smcBridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(deptId);
        SmcBridge smcBridge =  SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
        try {
            SMCWebsocketClient mwsc = getSmcWebsocketClient(smcBridge);
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
        SmcBridge smcBridge =  SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
        long groupId = (long)RealTimeInfoProcessorMessage.getRealTimeGroupMap().get(conferenceId + sub);
        smcBridge.getSmcConferencesInvoker().cancelRealTimeInfo(conferenceId,groupId,smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        try {
            SMCWebsocketClient mwsc = getSmcWebsocketClient(smcBridge);
            mwsc.sendMessage(TopicMessage.getUNSubscribeMessage(sub));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return  RestResponse.success();
    }


    /**
     *  CANCEL
     */
    @PostMapping("/sub/cancel/{sub}")
    public RestResponse addSubcribeChangeList(@PathVariable String sub)  {
        SmcBridge smcBridge =  SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
        try {
            SMCWebsocketClient mwsc = getSmcWebsocketClient(smcBridge);
            mwsc.sendMessage(TopicMessage.getUNSubscribeMessage(sub));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return  RestResponse.success();
    }
}
