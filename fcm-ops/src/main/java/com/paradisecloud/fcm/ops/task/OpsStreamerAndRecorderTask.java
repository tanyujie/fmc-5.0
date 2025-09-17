package com.paradisecloud.fcm.ops.task;


import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.dao.mapper.BusiOpsInfoMapper;
import com.paradisecloud.fcm.dao.model.BusiOpsInfo;
import com.paradisecloud.fcm.ops.utils.SshRemoteServerOperateForFMEOPS;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * @author admin
 */
public class OpsStreamerAndRecorderTask extends Task {


    private static final Logger log = LoggerFactory.getLogger(OpsStreamerAndRecorderTask.class);
    private String quality;

    public OpsStreamerAndRecorderTask(String id, long delayInMilliseconds, String quality) {
        super(id, delayInMilliseconds);
        this.quality=quality;
    }


    @Override
    public void run() {
        log.info("OPS服务器开始设置streamer recorder。ID:" + getId()+" quality:"+quality);
        SshRemoteServerOperateForFMEOPS forFMEOPS = SshRemoteServerOperateForFMEOPS.getInstance();
        try {

            BusiOpsInfoMapper busiOpsInfoMapper = BeanFactory.getBean(BusiOpsInfoMapper.class);
            BusiOpsInfo busiOpsInfo = new BusiOpsInfo();

            List<BusiOpsInfo> busiOpsInfos = busiOpsInfoMapper.selectBusiOpsInfoList(busiOpsInfo);
            if(CollectionUtils.isEmpty(busiOpsInfos)){
                return;
            }
            BusiOpsInfo busiOpsInfo1 = busiOpsInfos.get(0);
            String fmeIp = busiOpsInfo1.getFmeIp();
            if(Strings.isBlank(fmeIp)){
                return;
            }
            forFMEOPS.sshRemoteCallLogin(fmeIp, "ttadmin", "tTcl0uds@cn", 22);
            boolean logined = forFMEOPS.isLogined();
            if (logined) {
                log.info("OPS{}服务器信正在修改recoder streamer quality》》》》》》》》》》》》》》》》》》", fmeIp);

                        String combinedCommand = String.join(" ; ",
                                "recorder disable",
                                "recorder sip listen a 6000 6001",
                                "recorder nfs " + busiOpsInfo.getIpAddress() + ":/mnt/nfs",
                                "recorder resolution "+quality,
                                "recorder enable",
                                "streamer disable",
                                "streamer sip listen a 7000 7000",
                                "streamer sip certs dbs.key dbs.crt root.crt",
                                "streamer sip resolution "+quality,
                                "streamer enable"
                        );
                forFMEOPS.execCommand(combinedCommand);

                forFMEOPS.execCommand("reboot");
                log.info("OPS{}服务器信完成修改recoder streamer quality》》》》》》》》》》》》》》》》》》", fmeIp);
            }
        } catch (Exception e) {
            log.info("OPS服务器信重启失败：" +e.getMessage());
        } finally {
            forFMEOPS.closeSession();
        }
    }

}
