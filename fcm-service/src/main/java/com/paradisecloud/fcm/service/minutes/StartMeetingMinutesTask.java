package com.paradisecloud.fcm.service.minutes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.model.MinutesParam;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.model.BusiOperationLog;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.cache.BaseWebSocketMessagePusher;
import com.paradisecloud.fcm.service.interfaces.IBusiOperationLogService;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

public class StartMeetingMinutesTask extends Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartMeetingMinutesTask.class);

    private String conferenceId;
    private String host;
    private Integer port;

    public StartMeetingMinutesTask(String id, long delayInMilliseconds, String conferenceId, String host, Integer port) {
        super("start_m_minutes_" + id, delayInMilliseconds);
        this.conferenceId = conferenceId;
        this.host = host;
        this.port = port;
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            if (McuType.FME.getCode().equals(conferenceContext.getMcuType())
                || McuType.SMC3.getCode().equals(conferenceContext.getMcuType())) {
                RestTemplate restTemplate = new RestTemplate();
                String url = "http://" + host;
                if (port != null) {
                    url += ":" + port;
                }
                url += "/start_asr";
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("meeting_room", conferenceContext.getConferenceNumber());
                jsonObject.put("fme_ip", conferenceContext.getMcuCallIp());
                jsonObject.put("display_name", "会议纪要");
                jsonObject.put("conf_id", conferenceId);
                jsonObject.put("session_id", "minutes");
                jsonObject.put("voice_engine", "sherpa");//本地："sherpa",讯飞："xf"
                JSONObject ex_params = new JSONObject();
                MinutesParam minutesParam = conferenceContext.getMinutesParam();
                if (minutesParam != null) {
                    if ("normal".equals(minutesParam.getTransType())) {
                        ex_params.put("lang", minutesParam.getLang());
                        ex_params.put("transType", minutesParam.getTransType());
                        ex_params.put("transStrategy", minutesParam.getTransStrategy());
                        ex_params.put("targetLang", minutesParam.getTargetLang());
                        if (minutesParam.getPunc() != null) {
                            ex_params.put("punc", minutesParam.getPunc());
                        }
                        if (minutesParam.getPd() != null) {
                            ex_params.put("pd", minutesParam.getPd());
                        }
                    }
                }
                jsonObject.put("ex_params", ex_params);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
                headers.add("Accept", MediaType.APPLICATION_JSON.toString());
                headers.add("Accept-Charset", "UTF-8");
                HttpEntity<String> formEntity = new HttpEntity<>(JSON.toJSONString(jsonObject), headers);
                ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(url, formEntity, String.class);
                if (stringResponseEntity != null) {
                    String body = stringResponseEntity.getBody();
                    LOGGER.info("asr result:" + body);
                    AsrResult asrResult = JSON.parseObject(body, AsrResult.class);
                    if (asrResult.getCode() == 0) {
                        try {
                            conferenceContext.openMinutesLog();
                        } catch (Exception e) {
                            LOGGER.info("打开写会议纪要文件失败！");
                        }
                        conferenceContext.setMinutesRemoteParty(asrResult.getData().getSession_id() + "@" + host);
                        return;
                    }
                }
                BaseWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_SHOW_TIP, "实时字幕出错！请稍后重试或联系管理员！");

                if (conferenceContext.getHistoryConference() != null) {
                    BusiOperationLog busiOperationLog = new BusiOperationLog();
                    String actionDetails = "开启实时字幕失败！";
                    actionDetails += "（开启转写服务失败）";
                    busiOperationLog.setActionDetails(actionDetails);
                    busiOperationLog.setHistoryConferenceId(conferenceContext.getHistoryConference().getId());
                    busiOperationLog.setUserId(null);
                    busiOperationLog.setOperatorName("系统");
                    busiOperationLog.setTime(new Date());
                    busiOperationLog.setActionResult(2);
                    busiOperationLog.setIp(null);
                    busiOperationLog.setDeviceType("system");
                    IBusiOperationLogService busiOperationLogService = BeanFactory.getBean(IBusiOperationLogService.class);
                    busiOperationLogService.insertBusiOperationLog(busiOperationLog);
                }
            }
        }
    }
}
