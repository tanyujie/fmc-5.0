package com.paradisecloud.fcm.web.task;

import com.paradisecloud.fcm.dao.model.BusiMeetingFile;
import com.paradisecloud.fcm.dao.model.BusiTransServer;
import com.paradisecloud.fcm.service.interfaces.IBusiMeetingFileService;
import com.paradisecloud.fcm.service.interfaces.IBusiTransServerService;
import com.paradisecloud.fcm.service.util.SshRemoteServerOperateForMeetingFile;
import com.sinhy.utils.ThreadUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;


@Component
public class MeetingFileRemoveScheduler extends Thread implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private IBusiTransServerService busiTransServerService;

    @Resource
    private IBusiMeetingFileService busiMeetingFileService;

    @Override
    public void run() {
        logger.info("入会文件清理启动=======！");
        ThreadUtils.sleep(60 * 1000);
        while (true) {
            try {

                List<BusiTransServer> busiTransServers = busiTransServerService.selectBusiTransServerList(new BusiTransServer());
                if (CollectionUtils.isNotEmpty(busiTransServers)) {
                    logger.info("入会文件清理开始=======！");

                    BusiMeetingFile busiMeetingFileQuery = new BusiMeetingFile();
                    busiMeetingFileQuery.setFileStatus(0);
                    List<BusiMeetingFile> busiMeetingFiles = busiMeetingFileService.selectBusiMeetingFileList(busiMeetingFileQuery);

                    if (CollectionUtils.isNotEmpty(busiMeetingFiles)) {
                        logger.info("本次清理数量:" + busiMeetingFiles.size());
                        for (BusiMeetingFile busiMeetingFile : busiMeetingFiles) {
                            logger.error("本地清理-----");
                            try {
                                File file = new File(busiMeetingFile.getUrl());
                                if (file.exists()) {
                                    file.delete();
                                }
                                if (busiMeetingFile.getOutFile() != null && !busiMeetingFile.getOutFile().equals("")) {
                                    File out_file = new File(busiMeetingFile.getOutFile());
                                    if (out_file.exists()) {
                                        out_file.delete();
                                    }
                                }
                            } catch (Exception e) {

                            }

                            logger.error("远程清理-----");
                            SshRemoteServerOperateForMeetingFile sshRemoteServerOperate = SshRemoteServerOperateForMeetingFile.getInstance();
                            try {
                                BusiTransServer transServer = busiTransServers.get(0);
                                sshRemoteServerOperate.sshRemoteCallLogin(transServer.getIp(), transServer.getUserName(), transServer.getPassword(), transServer.getPort());
                                boolean logined = sshRemoteServerOperate.isLogined();
                                logger.error("文件删除远程登录结果:{}", logined);
                                if (logined) {
                                    sshRemoteServerOperate.deleteFile(busiMeetingFile.getUrl());
                                    sshRemoteServerOperate.deleteFile(busiMeetingFile.getOutFile());
                                }
                            } catch (Exception e) {
                                logger.error("远程清理错误" + e.getMessage());
                            } finally {
                                sshRemoteServerOperate.closeSession();
                            }
                             //文件被清理过
                            busiMeetingFile.setFileStatus(2);
                            busiMeetingFileService.updateBusiMeetingFile(busiMeetingFile);

                        }
                    }

                }

            } catch (Throwable e) {
                logger.error("文件清理任务出错", e.getMessage());
            } finally {
                ThreadUtils.sleep(60*1000*60*2);
            }
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }


}
