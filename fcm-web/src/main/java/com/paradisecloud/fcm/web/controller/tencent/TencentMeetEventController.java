package com.paradisecloud.fcm.web.controller.tencent;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.tencent.event.TencentMeetingMessage;
import com.paradisecloud.fcm.tencent.event.TencentMeetingProcessorMessageQueue;
import com.paradisecloud.fcm.tencent.utils.Sha1Util;
import com.sinhy.utils.Base64Utils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author nj
 * @date 2023/7/7 15:14
 */
@RestController
@RequestMapping("/tencent/event")
@Tag(name = "会议事件")
public class TencentMeetEventController extends BaseController {


    @Resource
    private HttpServletRequest request;

    @GetMapping("/meeting")
    public String  checkStr(@RequestParam("check_str") String check_str){
        logger.info("check_str----------------------------------------"+check_str);
        String timestamp = request.getHeader("timestamp");
        String nonce = request.getHeader("nonce");
        String signature = request.getHeader("signature");

        String s = Sha1Util.calSignature(ExternalConfigCache.getInstance().getTencentEventToken(), timestamp, nonce, check_str);

        if(Objects.equals(s,signature)){
            String decode = Base64Utils.decode(check_str);
            return decode;
        }
        return null;

    }


    @PostMapping("/meeting")
    public void  checkStrPost(@RequestBody JSONObject jsonObject){

        try {
            String data = (String)jsonObject.get("data");

            String timestamp = request.getHeader("timestamp");
            String nonce = request.getHeader("nonce");
            String signature = request.getHeader("signature");
            String s = Sha1Util.calSignature(ExternalConfigCache.getInstance().getTencentEventToken(), timestamp, nonce, data);
            if(Objects.equals(s,signature)){
                logger.info("会议事件------------------------------------------------------------------------------------------------------------------------");
                String decode = Base64Utils.decode(data);
                logger.info(decode);
                //{"event":"meeting.canceled","trace_id":"c645bab9-2a2b-4f53-bf6e-bafb08f5b5f1","payload":[{"operate_time":1688717698711,"operator":{"userid":"meeting336181","user_name":"admin","uuid":"WM4fRmWCeVxaWN53Ag","instance_id":"1","ms_open_id":"6SCGhQbjXFgX4hc08N55nEmRZ9ynmclis/UqVwTYPFq8F2hx6evNrn4tiNp0+QHC96bZgbIgptTBXWFNXXIdiw=="},"meeting_info":{"meeting_id":"417543942956446611","meeting_code":"548586385","subject":"腾讯会议","creator":{"userid":"wemeeting1938942","user_name":"LJadmin","uuid":"WMNDWS9c3drIe1amMD","instance_id":"1","ms_open_id":"6SCGhQbjXFgX4hc08N55nEmRZ9ynmclis/UqVwTYPFrYvfM/0K1S5hqFTJejV0FPxwuo2zGwFvQe2xiIkQoEvg=="},"meeting_type":0,"start_time":1688717675,"end_time":1688724875,"meeting_create_mode":0},"cancel_reason":""}]}
                //{"event":"meeting.created","trace_id":"c0cb029c-a664-4688-bbdb-8466278997ba","payload":[{"operate_time":1688717733465,"operator":{"userid":"wemeeting1938942","user_name":"LJadmin","uuid":"WMNDWS9c3drIe1amMD","instance_id":"1","ms_open_id":"ejCL3JKUVJLxXkFUbrGXBzy+DI+DlLad9VaqvYi7vd1POaBHx8lx3o2lKBO6Y1Uf7iEjvJ3pqs4rYkV7VFA3Aw=="},"meeting_info":{"meeting_id":"9995636023117370314","meeting_code":"550538389","subject":"腾讯会议","creator":{"userid":"wemeeting1938942","user_name":"LJadmin","uuid":"WMNDWS9c3drIe1amMD","instance_id":"1","ms_open_id":"ejCL3JKUVJLxXkFUbrGXBzy+DI+DlLad9VaqvYi7vd1POaBHx8lx3o2lKBO6Y1Uf7iEjvJ3pqs4rYkV7VFA3Aw=="},"meeting_type":0,"start_time":1688717732,"end_time":1688724932,"meeting_create_mode":0}}]}
                //{"event":"meeting.started","trace_id":"39589f92-6155-4621-a928-ef225b45f0d3","payload":[{"operate_time":1688717771852,"operator":{"userid":"wemeeting1938942","user_name":"LJadmin","uuid":"WMNDWS9c3drIe1amMD","instance_id":"1","ms_open_id":"ejCL3JKUVJLxXkFUbrGXBzy+DI+DlLad9VaqvYi7vd1POaBHx8lx3o2lKBO6Y1Uf7iEjvJ3pqs4rYkV7VFA3Aw=="},"meeting_info":{"meeting_id":"9995636023117370314","meeting_code":"550538389","subject":"腾讯会议","creator":{"userid":"wemeeting1938942","user_name":"LJadmin","uuid":"WMNDWS9c3drIe1amMD","instance_id":"1","ms_open_id":"ejCL3JKUVJLxXkFUbrGXBzy+DI+DlLad9VaqvYi7vd1POaBHx8lx3o2lKBO6Y1Uf7iEjvJ3pqs4rYkV7VFA3Aw=="},"meeting_type":0,"start_time":1688717732,"end_time":1688724932,"meeting_create_mode":0}}]}
                ///fcm/tencent/event/meeting
                //{"event":"meeting.participant-joined","trace_id":"0b4bc8c3-67b1-49b0-864a-0983582550f3","payload":[{"operate_time":1688717771852,"operator":{"userid":"wemeeting1938942","user_name":"LJadmin","uuid":"WMNDWS9c3drIe1amMD","instance_id":"1","ms_open_id":"ejCL3JKUVJLxXkFUbrGXBzy+DI+DlLad9VaqvYi7vd1POaBHx8lx3o2lKBO6Y1Uf7iEjvJ3pqs4rYkV7VFA3Aw=="},"meeting_info":{"meeting_id":"9995636023117370314","meeting_code":"550538389","subject":"腾讯会议","creator":{"userid":"wemeeting1938942","user_name":"LJadmin","uuid":"WMNDWS9c3drIe1amMD","instance_id":"1","ms_open_id":"ejCL3JKUVJLxXkFUbrGXBzy+DI+DlLad9VaqvYi7vd1POaBHx8lx3o2lKBO6Y1Uf7iEjvJ3pqs4rYkV7VFA3Aw=="},"meeting_type":0,"start_time":1688717732,"end_time":1688724932,"meeting_create_mode":0}}]}
                JSONObject json= JSONObject.parseObject(decode);
                //{"event":"meeting.participant-jbh-waiting","trace_id":"af05f83e-f96e-4d17-9dfb-44913be73ed9","payload":[{"operate_time":1689153130827,"operator":{"userid":"wemeeting1938942","user_name":"LJadmin","uuid":"WMNDWS9c3drIe1amMD","instance_id":"1","ms_open_id":"EO9RLy5wJYdLv34kuuCKr+q1mUOeINQN1ZlxtWB42/uvcVPkEb2MHJSIT+iIM5OYaql03HS9mWkxLVYMY6FJYA=="},"meeting_info":{"meeting_id":"4872433002065862705","meeting_code":"765393428","subject":"swedwqadqw","creator":{"userid":"meeting336181","user_name":"admin","uuid":"WM4fRmWCeVxaWN53Ag","instance_id":"1","ms_open_id":"EO9RLy5wJYdLv34kuuCKr+q1mUOeINQN1ZlxtWB42/tNvgSPr7LdBD9JxuaISYAqGyy0lKq/HKCv9JHmnHCOyQ=="},"meeting_type":0,"start_time":1689153121,"end_time":1689160321,"meeting_create_mode":0}}]}
                 TencentMeetingProcessorMessageQueue.getInstance().put(new TencentMeetingMessage(json,(String) json.get("trace_id")));
                //{"event":"meeting.participant-left","trace_id":"ae8ac920-fbea-49ce-bde9-488298135df1","payload":[{"operate_time":1689153518803,"operator":{"userid":"wemeeting1938942","user_name":"LJadmin","uuid":"WMNDWS9c3drIe1amMD","instance_id":"1","ms_open_id":"EO9RLy5wJYdLv34kuuCKr+q1mUOeINQN1ZlxtWB42/uvcVPkEb2MHJSIT+iIM5OYaql03HS9mWkxLVYMY6FJYA=="},"meeting_info":{"meeting_id":"4872433002065862705","meeting_code":"765393428","subject":"swedwqadqw","creator":{"userid":"meeting336181","user_name":"admin","uuid":"WM4fRmWCeVxaWN53Ag","instance_id":"1","ms_open_id":"EO9RLy5wJYdLv34kuuCKr+q1mUOeINQN1ZlxtWB42/tNvgSPr7LdBD9JxuaISYAqGyy0lKq/HKCv9JHmnHCOyQ=="},"meeting_type":0,"start_time":1689153121,"end_time":1689160321,"meeting_create_mode":0}}]}
            }
        } catch (Exception e) {
           logger.info(e.getMessage());
        }

    }

}
