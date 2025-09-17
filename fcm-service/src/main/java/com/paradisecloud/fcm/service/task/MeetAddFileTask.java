package com.paradisecloud.fcm.service.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.TransCodecStatusCodeEnum;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.dao.model.BusiMeetingFile;
import com.paradisecloud.fcm.dao.model.BusiTransServer;
import com.paradisecloud.fcm.service.interfaces.IBusiMeetingFileService;
import com.paradisecloud.fcm.service.util.SshRemoteServerOperateForMeetingFile;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * @author nj
 * @date 2024/4/2 10:15
 */
public class MeetAddFileTask extends Task {

    public static final String HTTP = "http://";
    public static final int HTTPCODE = 200;
    public static final String ADD_FILE = ":9900/add_file";
    private static final Logger LOGGER = LoggerFactory.getLogger(MeetAddFileTask.class);
    private  BusiMeetingFile busiMeetingFile;


    private  BusiTransServer busiTransServer;

    public MeetAddFileTask(String id, long delayInMilliseconds, BusiMeetingFile busiMeetingFile, BusiTransServer busiTransServer) {
        super("M_A_F_"+id, delayInMilliseconds);
        this.busiMeetingFile = busiMeetingFile;
        this.busiTransServer=busiTransServer;
    }

    @Override
    public void run() {
        LOGGER.info("转码文件添加。ID:" + getId());
        try {

            String os = System.getProperty("os.name");
            if (!os.contains("indows")) {
                //上传到服务器
                LOGGER.info("转码文件上传开始。ID:" + getId());
                uploadFile(busiTransServer,busiMeetingFile.getUrl(), busiMeetingFile.getFileName());
                LOGGER.info("转码文件上传结束。ID:" + getId());
            }

            RestTemplate restTemplate = new RestTemplate();
            JSONObject jsonObject = new JSONObject();
            String remoteFilePath = "/home/upload/transcodec/" + busiMeetingFile.getFileName();
            jsonObject.put("file_path", remoteFilePath);
            jsonObject.put("id", busiMeetingFile.getId());


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());
            headers.add("Accept-Charset", "UTF-8");
            HttpEntity<String> formEntity = new HttpEntity<>(JSON.toJSONString(jsonObject), headers);
            ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(HTTP + busiTransServer.getIp() + ADD_FILE, formEntity, String.class);
            if (stringResponseEntity != null) {
                int statusCodeValue = stringResponseEntity.getStatusCodeValue();
                LOGGER.info("add_file reslutstatusCodeValue:{}", statusCodeValue);
                if (statusCodeValue == HTTPCODE) {
                    busiMeetingFile.setCodecStatus(TransCodecStatusCodeEnum.TRANS_ING.getCode());
                    String body = stringResponseEntity.getBody();
                    JSONObject parseObject = JSONObject.parseObject(body);
                    Object data = parseObject.get("data");
                    JSONObject fileJSON = JSONObject.parseObject(JSONObject.toJSONString(data));
                    Object out_file = fileJSON.get("out_file");
                    busiMeetingFile.setOutFile((String) out_file);
                } else {


                    busiMeetingFile.setCodecStatus(TransCodecStatusCodeEnum.CALLBACK_FAIL.getCode());
                }
            } else {
                busiMeetingFile.setCodecStatus(TransCodecStatusCodeEnum.CALLBACK_FAIL.getCode());
            }

            IBusiMeetingFileService bean = BeanFactory.getBean(IBusiMeetingFileService.class);
            bean.updateBusiMeetingFile(busiMeetingFile);
            LOGGER.info("转码文件添加结束。ID:" + getId());
        } catch (Exception e) {
            LOGGER.info("转码文件添加错误。ID:" + getId()+e.getMessage());
        }

    }

    public void uploadFile(BusiTransServer busiTransServer1,String localFilePath,String filename){
        SshRemoteServerOperateForMeetingFile sshRemoteServerOperate = SshRemoteServerOperateForMeetingFile.getInstance();
        try {
            sshRemoteServerOperate.sshRemoteCallLogin(busiTransServer1.getIp(),busiTransServer1.getUserName(),busiTransServer1.getPassword(),busiTransServer1.getPort());
            boolean logined = sshRemoteServerOperate.isLogined();
            if (logined) {
                LOGGER.info("文件远程上传=================");
                String remoteFilePath = "/home/upload/transcodec/"+filename;
                // 上传文件
                sshRemoteServerOperate.uploadFile(remoteFilePath, localFilePath);
                // 检查文件
                String   result = sshRemoteServerOperate.execCommand("ls " + remoteFilePath);
                LOGGER.info("转码文件上传结果："+ result);
            }
        } catch (Exception e) {
            LOGGER.error("文件上传错误："+ e.getMessage());
        }finally {
            sshRemoteServerOperate.closeSession();
        }
    }

}
