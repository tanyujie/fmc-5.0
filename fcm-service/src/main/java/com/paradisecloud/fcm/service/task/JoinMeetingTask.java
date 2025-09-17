package com.paradisecloud.fcm.service.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.TransCodecStatusCodeEnum;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.dao.model.BusiMeetingFile;
import com.paradisecloud.fcm.dao.model.BusiTransServer;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.cache.BaseWebSocketMessagePusher;
import com.paradisecloud.fcm.service.interfaces.IBusiMeetingFileService;
import com.paradisecloud.fcm.service.util.SshRemoteServerOperateForMeetingFile;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * @author nj
 * @date 2024/4/2 10:42
 */
public class JoinMeetingTask extends Task {

    public static final String HTTP = "http://";
    public static final int HTTPCODE = 200;
    public static final String START_FILE = ":9900/start_file_stream";
    private static final Logger LOGGER = LoggerFactory.getLogger(JoinMeetingTask.class);

    private Long[] ids;
    private  String mcu_ip;
    private  String serverip;
    private String conferenceNumber;
    private BaseConferenceContext baseConferenceContext;

    public JoinMeetingTask(String id, long delayInMilliseconds,Long[] ids, String mcu_ip, String conferenceNumber, String serverip, BaseConferenceContext baseConferenceContext) {
        super(id, delayInMilliseconds);
        this.ids=ids;
        this.mcu_ip=mcu_ip;
        this.conferenceNumber=conferenceNumber;
        this.serverip=serverip;
        this.baseConferenceContext=baseConferenceContext;
    }

    @Override
    public void run() {
        LOGGER.info("转码文件添加。ID:" + getId());
        try {
            callFile(ids,mcu_ip,conferenceNumber,serverip,baseConferenceContext);
            LOGGER.info("转码文件添加结束。ID:" + getId());
        } catch (Exception e) {
            LOGGER.info("转码文件添加错误。ID:" + getId()+e.getMessage());
        }

    }

    private void callFile(Long[] ids, String mcu_ip, String conferenceNumber, String serverip, BaseConferenceContext baseConferenceContext) {
        for (Long id : ids) {
            IBusiMeetingFileService busiMeetingFileService = BeanFactory.getBean(IBusiMeetingFileService.class);
            BusiMeetingFile busiMeetingFile = busiMeetingFileService.selectBusiMeetingFileById(id);

            String type="";
            String fileType = busiMeetingFile.getFileType();
            if(fileType.contains("image")){
                type="image";
            }else{
                type="file";
            }
            Integer codecStatus = busiMeetingFile.getCodecStatus();
            if(!Objects.equals(TransCodecStatusCodeEnum.TRANS_SUCCESS.getCode(), codecStatus)){
                throw new CustomException("文件还未转码不能入会");
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", type);
            jsonObject.put("file_path", busiMeetingFile.getOutFile());
            jsonObject.put("meeting_room", conferenceNumber);
            jsonObject.put("fme_ip", mcu_ip);
            jsonObject.put("display_name", busiMeetingFile.getParticipantName());

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());
            headers.add("Accept-Charset", "UTF-8");
            HttpEntity<String> formEntity = new HttpEntity<>(JSON.toJSONString(jsonObject), headers);

            ResponseEntity<String> stringResponseEntity = null;
            try {
                stringResponseEntity = restTemplate.postForEntity(HTTP + serverip + START_FILE, formEntity, String.class);
                int statusCodeValue = stringResponseEntity.getStatusCodeValue();
                if (statusCodeValue == HTTPCODE) {
                    LOGGER.info("JOIN MEETING FILE SUCESS BODYS:{}", stringResponseEntity.getBody());
                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("【").append(busiMeetingFile.getParticipantName()).append("】呼叫已发起！");
                    BaseWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(baseConferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

                }
            } catch (RestClientException e) {
                LOGGER.error("呼叫与会者发生异常-doCall：" + busiMeetingFile.getParticipantName(), e);
                StringBuilder messageTip = new StringBuilder();
                messageTip.append("【").append(busiMeetingFile.getParticipantName()).append("】呼叫失败：").append(e.getMessage());
                BaseWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(baseConferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip);
            }

        }
    }
}
