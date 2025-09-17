package com.paradisecloud.fcm.ding.templateConference;

import com.aliyun.dingtalkconference_1_0.Client;
import com.aliyun.dingtalkconference_1_0.models.CreateVideoConferenceResponse;
import com.aliyun.dingtalkconference_1_0.models.CreateVideoConferenceResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiV2UserGetRequest;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.paradisecloud.fcm.ding.cache.*;
import com.paradisecloud.fcm.ding.model.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.ding.service2.interfaces.IBusiMcuDingHistoryConferenceService;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.uuid.UUID;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryCallMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryCall;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.mapper.BusiMcuDingTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuDingTemplateConference;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author nj
 * @date 2023/4/20 11:53
 */
public class StartTemplateConference extends BuildTemplateConferenceContext {

    Logger logger = LoggerFactory.getLogger(getClass());

    public synchronized DingConferenceContext startTemplateConference(long templateConferenceId) {

        logger.info("模板会议启动入口：" + templateConferenceId);

        BusiMcuDingTemplateConferenceMapper busiMcuDingTemplateConferenceMapper = BeanFactory.getBean(BusiMcuDingTemplateConferenceMapper.class);
        // 获取模板会议实体对象
        BusiMcuDingTemplateConference tc = busiMcuDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceById(templateConferenceId);
        IBusiMcuDingHistoryConferenceService busiMcuDingHistoryConferenceService = BeanFactory.getBean(IBusiMcuDingHistoryConferenceService.class);
        if (tc == null) {
            return null;
        }
        // 获取会议上下文
        DingConferenceContext conferenceContext = buildTemplateConferenceContext(templateConferenceId);
        if (conferenceContext == null) {
            return null;
        }
        // 启动前校验会议是否已开始
        if (conferenceContext.isStart()) {
            throw new SystemException(1009874, "会议已开始，请勿重复开始");
        }

        DingBridge bridge = DingBridgeCache.getInstance().getAvailableBridgesByDept(tc.getDeptId());
        if (bridge == null) {
            return null;
        }
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
        config.protocol = "https";
        config.regionId = "central";
        Client client = null;
        try {
            client = new Client(config);


        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            DingTalkClient client_user = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/get");
            OapiV2UserGetRequest req = new OapiV2UserGetRequest();
            req.setUserid(bridge.getDingUserId());
            req.setLanguage("zh_CN");
            OapiV2UserGetResponse rsp = client_user.execute(req, bridge.getCacheDingAccessToken().getAccessToken());
            String userId = rsp.getResult().getUnionid();

            com.aliyun.dingtalkconference_1_0.models.CreateVideoConferenceRequest createVideoConferenceRequest = new com.aliyun.dingtalkconference_1_0.models.CreateVideoConferenceRequest()
                    .setUserId(userId)
                    .setConfTitle(tc.getName())
                    .setInviteCaller(false);
            CreateVideoConferenceResponse videoConferenceWithOptions = client.createVideoConferenceWithOptions(createVideoConferenceRequest, bridge.getDingtalkAccessHeader(), new RuntimeOptions());
            CreateVideoConferenceResponseBody body = videoConferenceWithOptions.getBody();
            conferenceContext.setConferenceNumber(body.getRoomCode());
            conferenceContext.setMeetingId(body.getConferenceId());
        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                logger.info("开始会议失败"+err.code);
                logger.info("开始会议失败"+err.getMessage());
            }
            throw new CustomException("开始会议失败" + _err.getMessage());
        }

        conferenceContext.setStart(true);
        conferenceContext.setStartTime(new Date());



        saveHistory(busiMcuDingHistoryConferenceService, tc, conferenceContext);
        //缓存
        tc.setConfId(conferenceContext.getMeetingId());
        busiMcuDingTemplateConferenceMapper.updateBusiMcuDingTemplateConference(tc);
        String message = "会议【" + conferenceContext.getName() + "】启动成功！";
        Map<String, Object> obj = new HashMap<>(3);
        obj.put("startTime", conferenceContext.getStartTime());
        obj.put("message", message);
        obj.put("conferenceNumber", conferenceContext.getConferenceNumber());
        obj.put("streamingUrl", conferenceContext.getStreamingUrl());
        obj.put("streamingUrlList", conferenceContext.getStreamUrlList());
        DingWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, message);
        DingWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_STARTED, obj);
        conferenceContext.setId(EncryptIdUtil.generateConferenceId(conferenceContext.getContextKey()));
        DingConferenceContextCache.getInstance().add(conferenceContext);
        DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext);
        conferenceContext.setAttendeeOperation(defaultAttendeeOperation);
        defaultAttendeeOperation.operate();
        return conferenceContext;
    }


    private void saveHistory(IBusiMcuDingHistoryConferenceService busiMcuDingHistoryConferenceService, BusiMcuDingTemplateConference tc, DingConferenceContext conferenceContext) {
        // 保存历史记录
        String callId = UUID.randomUUID().toString();
        // 保存历史记录
        BusiHistoryConference busiHistoryConference = busiMcuDingHistoryConferenceService.saveHistory(conferenceContext);
        conferenceContext.setHistoryConference(busiHistoryConference);
        tc.setLastConferenceId(String.valueOf(busiHistoryConference.getId()));
        //历史call保存
        BusiHistoryCall busiHistoryCall = new BusiHistoryCall();
        busiHistoryCall.setCallId(callId);
        busiHistoryCall.setCoSpace(conferenceContext.getCoSpaceId());
        busiHistoryCall.setDeptId(conferenceContext.getDeptId());
        busiHistoryCall.setCreateTime(new Date());
        busiHistoryCall.setHistoryConferenceId(busiHistoryConference.getId());
        BeanFactory.getBean(BusiHistoryCallMapper.class).insertBusiHistoryCall(busiHistoryCall);
    }

}
