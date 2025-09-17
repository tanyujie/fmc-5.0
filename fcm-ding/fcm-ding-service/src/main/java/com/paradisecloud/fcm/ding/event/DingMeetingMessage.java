package com.paradisecloud.fcm.ding.event;


import com.alibaba.fastjson2.JSONObject;
import com.aliyun.dingtalkconference_1_0.Client;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.sinhy.core.processormessage.ProcessorMessage;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author nj
 * @date 2023/7/10 9:34
 */
public class DingMeetingMessage extends ProcessorMessage<JSONObject> {

    public static final String MEETING_MEMBER_STATUS_CHANGE = "meeting_member_status_change";

    public DingMeetingMessage(JSONObject jsonObject, String itemId) {
        super(jsonObject, itemId);
    }

    public static String decode2(String base64Str) {
        // 解码
        byte[] base64Data = Base64.getDecoder().decode(base64Str);
        // byte[]-->String（解码后的字符串）
        String str = new String(base64Data, StandardCharsets.UTF_8);
        return str;
    }

    @SneakyThrows
    @Override
    protected void process0() {
        JSONObject updateItem = this.updateItem;
        StreamEventModel streamEventModel = JSONObject.parseObject(updateItem.toJSONString(), StreamEventModel.class);
        String eventType = streamEventModel.getEventType();
        if(Objects.equals(eventType, MEETING_MEMBER_STATUS_CHANGE)){

            Config config = new Config();
            config.protocol = "https";
            config.regionId = "central";
            Client client = new Client(config);


            com.aliyun.dingtalkconference_1_0.models.CreateVideoConferenceHeaders createVideoConferenceHeaders = new com.aliyun.dingtalkconference_1_0.models.CreateVideoConferenceHeaders();
            createVideoConferenceHeaders.xAcsDingtalkAccessToken = "<your access token>";
            com.aliyun.dingtalkconference_1_0.models.CreateVideoConferenceRequest createVideoConferenceRequest = new com.aliyun.dingtalkconference_1_0.models.CreateVideoConferenceRequest()
                    .setUserId("27SaQ3iiHLN0uwqcPisedfreNwiEiE")
                    .setConfTitle("XXX的视频会议")
                    .setInviteUserIds(java.util.Arrays.asList(
                            "iSKzJxxxxx"
                    ))
                    .setInviteCaller(false);
            try {
                client.createVideoConferenceWithOptions(createVideoConferenceRequest, createVideoConferenceHeaders, new com.aliyun.teautil.models.RuntimeOptions());
            } catch (TeaException err) {
                if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                    // err 中含有 code 和 message 属性，可帮助开发定位问题
                }

            } catch (Exception _err) {
                TeaException err = new TeaException(_err.getMessage(), _err);
                if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                    // err 中含有 code 和 message 属性，可帮助开发定位问题
                }

            }
        }

    }





}
