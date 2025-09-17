package com.paradisecloud.fcm.web.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.TransCodecStatusCodeEnum;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.PathUtil;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.response.failure.FailureDetailsInfo;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjConferenceContextCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.service.interfaces.IBusiMeetingFileService;
import com.paradisecloud.fcm.service.interfaces.IBusiTransServerService;
import com.paradisecloud.fcm.terminal.fs.cache.LiveBridgeCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveDeptCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveSettingCache;
import com.sinhy.exception.SystemException;
import com.sinhy.http.HttpObjectCreator;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;
import com.sinhy.utils.ThreadUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class MeetingFileScheduler extends Thread implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private IBusiTransServerService busiTransServerService;

    @Resource
    private IBusiMeetingFileService busiMeetingFileService;


    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }

    @Override
    public void run() {
        logger.info("文件入会转码回调启动成功！");
        ThreadUtils.sleep(60 * 1000);
        while (true) {
            try {

                List<BusiTransServer> busiTransServers = busiTransServerService.selectBusiTransServerList(new BusiTransServer());
                if(CollectionUtils.isNotEmpty(busiTransServers)){

                    BusiTransServer busiTransServer = busiTransServers.get(0);
                    String serverIp = busiTransServer.getIp();
                    Integer port = busiTransServer.getPort();
                    BusiMeetingFile busiMeetingFile = new BusiMeetingFile();
                    busiMeetingFile.setCodecStatus(TransCodecStatusCodeEnum.CALLBACK_FAIL.getCode());
                    busiMeetingFile.setFileStatus(1);
                    List<BusiMeetingFile> busiMeetingFiles = busiMeetingFileService.selectBusiMeetingFileList(busiMeetingFile);

                    if(CollectionUtils.isNotEmpty(busiMeetingFiles)){

                        for (BusiMeetingFile meetingFile : busiMeetingFiles) {

                            if(Objects.equals(TransCodecStatusCodeEnum.UN_TRANS.getCode(),meetingFile.getCodecStatus())||
                                    Objects.equals(TransCodecStatusCodeEnum.CALLBACK_FAIL.getCode(),meetingFile.getCodecStatus())||
                                    (Objects.equals(TransCodecStatusCodeEnum.TRANS_ING.getCode(),meetingFile.getCodecStatus())&&System.currentTimeMillis()-meetingFile.getCreateTime().getTime()>1000*60*60*2)){
                                RestTemplate restTemplate = new RestTemplate();
                                JSONObject jsonObject = new JSONObject();
                                String remoteFilePath = meetingFile.getUrl();
                                jsonObject.put("file_path",remoteFilePath);
                                jsonObject.put("id",busiMeetingFile.getId());

                                HttpHeaders headers = new HttpHeaders();
                                headers.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
                                headers.add("Accept", MediaType.APPLICATION_JSON.toString());
                                headers.add("Accept-Charset", "UTF-8");
                                HttpEntity<String> formEntity = new HttpEntity<>(JSON.toJSONString(jsonObject), headers);
                                ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity("http://" + serverIp+":"+port + "/add_file", formEntity, String.class);
                                if(stringResponseEntity!=null){
                                    int statusCodeValue = stringResponseEntity.getStatusCodeValue();
                                    logger.info("Scheduler add_file reslutstatusCodeValue:{}",statusCodeValue);
                                    if(statusCodeValue== 200){
                                        busiMeetingFile.setCodecStatus(TransCodecStatusCodeEnum.TRANS_ING.getCode());
                                        String body = stringResponseEntity.getBody();
                                        JSONObject parseObject = JSONObject.parseObject(body);
                                        Object data = parseObject.get("data");
                                        JSONObject fileJSON = JSONObject.parseObject(JSONObject.toJSONString(data));
                                        Object out_file = fileJSON.get("out_file");
                                        busiMeetingFile.setOutFile((String) out_file);
                                    }
                                }
                                busiMeetingFileService.updateBusiMeetingFile(busiMeetingFile);
                            }
                        }
                    }

                }

            } catch (Throwable e) {
                logger.error("文件入会转码回调出错", e);
            } finally {
                ThreadUtils.sleep(10 * 1000);
            }
        }
    }







}
