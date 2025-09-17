package com.paradisecloud.fcm.web.controller.livebroadcast;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.dao.model.BusiLiveBroadcast;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.web.cache.LiveBroadcastCache;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * 直播记录Controller
 *
 * @author lilinhai
 * @date 2024-05-07
 */
@RestController
@RequestMapping("/busi/srs")
@Tag(name = "直播记录")
public class SRSEventController extends BaseController
{

    /**
     * SRS 推流回调函数  ;如果有SRS推流则回调
     */
    @PostMapping("/v1/streams")
    public String streams(@RequestBody com.alibaba.fastjson.JSONObject jsonObject)
    {
        System.out.println("[[[[[[[[[[[[[[[[[[[streams]]]]]]]]]]]" + jsonObject.toJSONString());
        String tcUrl = jsonObject.getString("tcUrl");
        String stream_url = jsonObject.getString("stream_url");
        String replace = tcUrl.replace(":1935", "");
        String streamUrl = replace.replace("/live", stream_url);
        boolean isTick = true;
        if (StringUtils.isNotEmpty(streamUrl)) {
            Collection<BusiLiveBroadcast> valuesLives = LiveBroadcastCache.getInstance().values();
            for (BusiLiveBroadcast liveBroadcast : valuesLives) {
                String streamingUrl = liveBroadcast.getStreamUrl();
                Integer status = liveBroadcast.getStatus();
                if (streamUrl.equals(streamingUrl) && status != null && status == 1) {
                    isTick = false;
                }
            }

            Collection<BaseConferenceContext> values = AllConferenceContextCache.getInstance().values();
            for (BaseConferenceContext value : values) {
                String streamingUrl = value.getStreamingUrl();
                if (streamUrl.equals(streamingUrl)) {
                    isTick = false;
                }
            }
        }

        JSONObject jsonObject1 = new JSONObject();
        if (isTick) {
            jsonObject1.put("code",1);
        } else {
            jsonObject1.put("code", 0);
        }
        jsonObject1.put("msg","OK");
        System.out.println("===============return=============" + jsonObject1.toString());
        return jsonObject1.toString();
    }
}
