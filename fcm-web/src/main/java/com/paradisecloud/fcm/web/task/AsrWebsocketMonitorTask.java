package com.paradisecloud.fcm.web.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.model.MinutesParam;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.mapper.BusiTransServerMapper;
import com.paradisecloud.fcm.dao.model.BusiOperationLog;
import com.paradisecloud.fcm.dao.model.BusiTransServer;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.cache.BaseWebSocketMessagePusher;
import com.paradisecloud.fcm.service.interfaces.IBusiOperationLogService;
import com.paradisecloud.fcm.service.minutes.AsrWebSocketCache;
import com.paradisecloud.fcm.service.minutes.AsrWebsocketResult;
import com.paradisecloud.fcm.service.minutes.IWebsocketMessageProcessor;
import com.paradisecloud.fcm.service.minutes.WebsocketClient;
import com.paradisecloud.smc3.service.interfaces.IAttendeeSmc3Service;
import com.sinhy.spring.BeanFactory;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Date;
import java.util.List;

/**
 * CDR定时任务类
 */
@Component
public class AsrWebsocketMonitorTask {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 每30秒启动检查ASR状态
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void checkWebsocketClient() {
        logger.info("检查ASR状态任务启动");
        WebsocketClient websocketClient = AsrWebSocketCache.getInstance().getWebsocketClient();
        if (websocketClient == null || websocketClient.isClosed()) {
            BusiTransServerMapper busiTransServerMapper = BeanFactory.getBean(BusiTransServerMapper.class);
            List<BusiTransServer> busiTransServerList = busiTransServerMapper.selectBusiTransServerList(new BusiTransServer());
            if (busiTransServerList.size() > 0) {
                BusiTransServer busiTransServer = busiTransServerList.get(0);
                String ip = busiTransServer.getIp();
                createWebsocketClient(ip);
            }
        } else if (!websocketClient.isClosed()) {
            BusiTransServerMapper busiTransServerMapper = BeanFactory.getBean(BusiTransServerMapper.class);
            List<BusiTransServer> busiTransServerList = busiTransServerMapper.selectBusiTransServerList(new BusiTransServer());
            if (busiTransServerList.size() > 0) {
                BusiTransServer busiTransServer = busiTransServerList.get(0);
                String ip = busiTransServer.getIp();
                if (!ip.equals(websocketClient.getURI().getHost())) {
                    try {
                        websocketClient.close();
                    } catch (Exception e) {
                    }
                } else {
                    return;
                }
                createWebsocketClient(ip);
            }
        }
    }

    public void createWebsocketClient(String ip) {
        try {
            URI serverUrl = new URI("ws://" + ip + ":9900/ws");
            IWebsocketMessageProcessor websocketMessageProcessor = new IWebsocketMessageProcessor() {
                @Override
                public void processMessage(String message) {
                    logger.info("asr消息：" + message);
                    try {
                        AsrWebsocketResult arsWebsocketResult = JSON.parseObject(message, AsrWebsocketResult.class);
                        String conferenceId = arsWebsocketResult.conf_id;
                        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
                        String contextKey = EncryptIdUtil.generateContextKey(conferenceIdVo.getId(), conferenceIdVo.getMcuType());
                        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                        if (baseConferenceContext == null) {
                            return;
                        }
                        if (StringUtils.isEmpty(arsWebsocketResult.voice_engine) || "xf".equals(arsWebsocketResult.voice_engine)) {
                            // voice_engine:xf
                            boolean transResult = false;
                            MinutesParam minutesParam = baseConferenceContext.getMinutesParam();
                            if (minutesParam != null && "normal".equals(minutesParam.getTransType())) {
                                transResult = true;
                            }
                            if (!"0".equals(arsWebsocketResult.code)) {
                                switch (conferenceIdVo.getMcuType()) {
                                    case FME: {
                                        if (baseConferenceContext.getMinutesAttendee() != null) {
                                            IAttendeeService attendeeService = BeanFactory.getBean(IAttendeeService.class);
                                            attendeeService.hangUp(conferenceId, baseConferenceContext.getMinutesAttendee().getId());
                                            attendeeService.remove(conferenceId, baseConferenceContext.getMinutesAttendee().getId());
                                        }
                                        break;
                                    }
                                    case SMC3: {
                                        if (baseConferenceContext.getMinutesAttendee() != null) {
                                            IAttendeeSmc3Service attendeeService = BeanFactory.getBean(IAttendeeSmc3Service.class);
                                            attendeeService.hangUp(conferenceId, baseConferenceContext.getMinutesAttendee().getId());
                                            attendeeService.remove(conferenceId, baseConferenceContext.getMinutesAttendee().getId());
                                        }
                                        break;
                                    }
                                }
                                String tip = "开启实时字幕出错！";
                                String errMsg = "";
                                if ("10110".equals(arsWebsocketResult.code) || "10800".equals(arsWebsocketResult.code)) {
                                    errMsg += "转写服务超过连接数";
                                } else if ("10202".equals(arsWebsocketResult.code)) {
                                    errMsg += "无法连接转写服务器";
                                } else if ("10105".equals(arsWebsocketResult.code)) {
                                    errMsg += "无权限使用转写服务";
                                }
                                if (StringUtils.isNotEmpty(errMsg)) {
                                    tip += errMsg + "，";
                                }
                                tip += "请稍后重试或联系管理员！";
                                BaseWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(baseConferenceContext, WebsocketMessageType.MESSAGE_SHOW_TIP, tip);

                                if (baseConferenceContext.getHistoryConference() != null) {
                                    BusiOperationLog busiOperationLog = new BusiOperationLog();
                                    String actionDetails = "开启实时字幕出错！" + errMsg;
                                    if (StringUtils.isNotEmpty(arsWebsocketResult.desc)) {
                                        actionDetails += "（" + arsWebsocketResult.desc + "）";
                                    }
                                    busiOperationLog.setActionDetails(actionDetails);
                                    busiOperationLog.setHistoryConferenceId(baseConferenceContext.getHistoryConference().getId());
                                    busiOperationLog.setUserId(null);
                                    busiOperationLog.setOperatorName("系统");
                                    busiOperationLog.setTime(new Date());
                                    busiOperationLog.setActionResult(2);
                                    busiOperationLog.setIp("127.0.0.1");
                                    busiOperationLog.setDeviceType("system");
                                    IBusiOperationLogService busiOperationLogService = BeanFactory.getBean(IBusiOperationLogService.class);
                                    busiOperationLogService.insertBusiOperationLog(busiOperationLog);
                                }
                                return;
                            }
                            if ("0".equals(arsWebsocketResult.code)) {
                                String text = "";
                                String logText = "";
                                String logTextSrc = "";
                                String logTextDst = "";
                                String type = "1";
                                try {
                                    AsrWebsocketResult.TransData transData = JSON.parseObject(arsWebsocketResult.data, AsrWebsocketResult.TransData.class);
                                    if (transData.type != null) {
                                        type = transData.type;
                                        if (StringUtils.isNotEmpty(transData.src)) {
                                            logTextSrc = transData.src;
                                            logTextDst = transData.dst;
                                        }
                                    }
                                } catch (Exception e) {
                                }
                                try {
                                    AsrWebsocketResult.ResultData resultData = JSON.parseObject(arsWebsocketResult.data, AsrWebsocketResult.ResultData.class);
                                    if (resultData.cn.st.type != null) {
                                        type = resultData.cn.st.type;
                                        if (resultData != null && resultData.cn != null) {
                                            for (AsrWebsocketResult.Rt rt : resultData.cn.st.rt) {
                                                for (AsrWebsocketResult.Ws ws : rt.ws) {
                                                    List<AsrWebsocketResult.Cw> cwList = ws.cw;
                                                    for (AsrWebsocketResult.Cw cw : cwList) {
                                                        logText += cw.w;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                }
                                if (transResult) {
                                    if (StringUtils.isNotEmpty(logTextSrc)) {
                                        if (startWithChinesePunctuation(logTextSrc) || startWithPunctuation(logTextSrc)) {
                                            logTextSrc = logTextSrc.substring(1);
                                        }
                                        if (startWithChinesePunctuation(logTextDst) || startWithPunctuation(logTextDst)) {
                                            logTextDst = logTextDst.substring(1);
                                        }
                                        text = logTextSrc + "\n" + logTextDst;
                                        if ("0".equals(type)) {
                                            baseConferenceContext.minutesLog(logTextSrc);
                                            baseConferenceContext.minutesLog(logTextDst);
                                        }
                                    }
                                } else {
                                    if (startWithChinesePunctuation(logText) || startWithPunctuation(logText)) {
                                        logText = logText.substring(1);
                                    }
                                    text = logText;
                                    if ("0".equals(type)) {
                                        baseConferenceContext.minutesLog(logText);
                                    }
                                }
                                if (StringUtils.isNotEmpty(text)) {
                                    logger.info("text:" + text);
                                    switch (conferenceIdVo.getMcuType()) {
                                        case FME: {
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("messagePosition", "bottom");
                                            jsonObject.put("messageText", text);
                                            jsonObject.put("messageDuration", 5);
                                            IAttendeeService attendeeService = BeanFactory.getBean(IAttendeeService.class);
                                            attendeeService.sendMessage(conferenceId, jsonObject);
                                            break;
                                        }
                                        case SMC3: {
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("disPosition", 3);
                                            jsonObject.put("displayType", 4);
                                            jsonObject.put("content", text);
                                            IAttendeeSmc3Service attendeeService = BeanFactory.getBean(IAttendeeSmc3Service.class);
                                            attendeeService.sendMessageForMinutes(conferenceId, jsonObject);
                                            break;
                                        }
                                    }
                                }
                            }
                        } else {
                            // voice_engine:sherpa
                            String text = arsWebsocketResult.text;
                            if (startWithChinesePunctuation(text) || startWithPunctuation(text)) {
                                text = text.substring(1);
                            }
                            if (arsWebsocketResult.is_final == Boolean.TRUE) {
                                baseConferenceContext.minutesLog(text);
                            }
                            if (StringUtils.isNotEmpty(text)) {
                                logger.info("text:" + text);
                                switch (conferenceIdVo.getMcuType()) {
                                    case FME: {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("messagePosition", "bottom");
                                        jsonObject.put("messageText", text);
                                        jsonObject.put("messageDuration", 5);
                                        IAttendeeService attendeeService = BeanFactory.getBean(IAttendeeService.class);
                                        attendeeService.sendMessage(conferenceId, jsonObject);
                                        break;
                                    }
                                    case SMC3: {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("disPosition", 3);
                                        jsonObject.put("displayType", 4);
                                        jsonObject.put("content", text);
                                        IAttendeeSmc3Service attendeeService = BeanFactory.getBean(IAttendeeSmc3Service.class);
                                        attendeeService.sendMessageForMinutes(conferenceId, jsonObject);
                                        break;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.error("会议纪要解析错误！", e);
                    }
                }

                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    logger.info("asr websocket 连接打开成功！");
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    logger.info("asr websocket 连接关闭！");
                    AsrWebSocketCache.getInstance().setWebsocketClient(null);
                }

                @Override
                public void onError(Exception ex) {
                    logger.info("asr websocket 连接错误！", ex);
                    try {
                        AsrWebSocketCache.getInstance().getWebsocketClient().close();
                    } catch (Exception e) {
                    }
                    AsrWebSocketCache.getInstance().setWebsocketClient(null);
                }

            };
            WebsocketClient websocketClient = new WebsocketClient(serverUrl, websocketMessageProcessor);
            websocketClient.connect();
            AsrWebSocketCache.getInstance().setWebsocketClient(websocketClient);
        } catch (Exception e) {
            logger.error("asr websocket 创建出错！", e);;
        }
    }

    public boolean startWithChinesePunctuation(String str) {
        if (StringUtils.isNotEmpty(str)) {
            String s = str.substring(0, 1);
            if (s.equals("，") || s.equals("。") || s.equals("、") || s.equals("！") || s.equals("？") || s.equals("；")) {
                return true;
            }
        }
        return false;
    }

    public boolean startWithPunctuation(String str) {
        if (StringUtils.isNotEmpty(str)) {
            String s = str.substring(0, 1);
            if (s.equals(",") || s.equals(".") || s.equals("!") || s.equals("?") || s.equals(";")) {
                return true;
            }
        }
        return false;
    }

}
