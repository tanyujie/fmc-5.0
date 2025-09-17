package com.paradisecloud.fcm.mcu.zj.task;

import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.FileUtil;
import com.paradisecloud.fcm.common.utils.PathUtil;
import com.paradisecloud.fcm.dao.mapper.BusiRecordsMapper;
import com.paradisecloud.fcm.dao.model.BusiRecords;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmDeleteRecordsFilesRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmQueryRecordsInfoRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmQueryRecordsRequest;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.CmDeleteRecordsFilesResponse;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.CmQueryRecordsInfoResponse;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.CmQueryRecordsResponse;
import com.paradisecloud.fcm.mcu.zj.utils.FileUtils;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadRecordFileTask extends DelayTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadRecordFileTask.class);

    private McuZjBridge mcuZjBridge;
    private String coSpace;
    private Long deptId;
    private String conferenceName;

    public DownloadRecordFileTask(String id, long delayInMilliseconds, McuZjBridge mcuZjBridge, String coSpace, Long deptId, String conferenceName) {
        super("download_record_" + id, delayInMilliseconds);
        this.mcuZjBridge = mcuZjBridge;
        this.coSpace = coSpace;
        this.deptId = deptId;
        this.conferenceName = conferenceName;
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
        LOGGER.info("MCU_ZJ下载录制文件开始。ID:" + getId());
        download("public");
        download("private");
    }

    private void download(String option) {
        if (mcuZjBridge != null && StringUtils.hasText(coSpace)) {
            CmQueryRecordsRequest cmQueryRecordsRequest = new CmQueryRecordsRequest();
            cmQueryRecordsRequest.setOption(option);
            if (coSpace.contains("-")) {
                cmQueryRecordsRequest.setFilter_value(coSpace.substring(0, coSpace.indexOf("-")));
            } else {
                cmQueryRecordsRequest.setFilter_value(coSpace);
            }
            CmQueryRecordsResponse cmQueryRecordsResponse = mcuZjBridge.getConferenceManageApi().queryRecords(cmQueryRecordsRequest);
            if (cmQueryRecordsResponse != null && cmQueryRecordsResponse.getUuids() != null && cmQueryRecordsResponse.getUuids().length > 0) {
                CmQueryRecordsInfoRequest cmQueryRecordsInfoRequest = new CmQueryRecordsInfoRequest();
                cmQueryRecordsInfoRequest.setUuids(cmQueryRecordsResponse.getUuids());
                CmQueryRecordsInfoResponse cmQueryRecordsInfoResponse = mcuZjBridge.getConferenceManageApi().queryRecordsInfo(cmQueryRecordsInfoRequest);
                if (cmQueryRecordsInfoResponse != null && cmQueryRecordsInfoResponse.getUuids() != null) {
                    // 获取保存路径
                    String path = getSavePath();
                    File folder = new File(path, coSpace);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    for (int i = 0; i < cmQueryRecordsInfoResponse.getUuids().length; i++) {
                        String uuid = cmQueryRecordsInfoResponse.getUuids()[i];
                        String downloadUrl = cmQueryRecordsInfoResponse.getDownload_urls()[i];
                        String fileName = cmQueryRecordsInfoResponse.getFile_names()[i];
                        File file = new File(folder.getAbsolutePath(), fileName);
                        if (StringUtils.hasText(downloadUrl)) {
                            try {
                                BusiRecordsMapper busiRecordsMapper = BeanFactory.getBean(BusiRecordsMapper.class);
                                BusiRecords busiRecordsCon = new BusiRecords();
                                busiRecordsCon.setCoSpaceId(coSpace);
                                busiRecordsCon.setRealName(fileName);
                                List<BusiRecords> busiRecordsList = busiRecordsMapper.selectBusiRecordsList(busiRecordsCon);
                                if (busiRecordsList == null || busiRecordsList.size() == 0) {
                                    // 下载
                                    URL url = new URL(downloadUrl);
                                    Map<String, String> headers = new HashMap<>();
                                    String referer = "https://" + mcuZjBridge.getBusiMcuZj().getIp() + "/confmgr/";
                                    headers.put("Referer", referer);
                                    FileUtils.copyURLToFile(url, file, 5000, 7200000, headers);
                                    file = new File(folder.getAbsolutePath(), fileName);
                                    // 保存数据
                                    Date createTime = new Date();
                                    String displayFileName = fileName;
                                    try {
                                        if (fileName.contains("_") && fileName.contains(".")) {
                                            if (fileName.indexOf("_") < fileName.indexOf(".")) {
                                                String createTimeStr = fileName.substring(fileName.indexOf("_") + 1, fileName.indexOf("."));
                                                if (createTimeStr.length() == 15) {
                                                    createTime = DateUtil.convertDateByString(createTimeStr, "yyyyMMdd_HHmmss");
                                                }
                                            }
                                        }
                                        displayFileName = coSpace.substring(0, coSpace.indexOf("-")) + "_" + conferenceName + "_录制文件_" + createTime.getTime();
                                    } catch (Exception e) {
                                    }
                                    BusiRecords busiRecords = new BusiRecords();
                                    busiRecords.setCoSpaceId(coSpace);
                                    busiRecords.setConferenceNumber(Long.valueOf(coSpace.substring(3, coSpace.indexOf("-"))));
                                    busiRecords.setTemplateName(conferenceName);
                                    busiRecords.setDeptId(deptId);
                                    busiRecords.setFileName(displayFileName);
                                    busiRecords.setRealName(fileName);
                                    busiRecords.setCreateTime(createTime);
                                    busiRecords.setFileSize(FileUtil.getFileSizeWithUnit(file.length()));
                                    busiRecordsMapper.insertBusiRecords(busiRecords);
                                }
                                // 删除MCU上的文件

                                CmDeleteRecordsFilesRequest cmDeleteRecordsFilesRequest = new CmDeleteRecordsFilesRequest();
                                cmDeleteRecordsFilesRequest.setUuids(new String[]{uuid});
                                cmDeleteRecordsFilesRequest.setAction(1);
                                CmDeleteRecordsFilesResponse cmDeleteRecordsFilesResponse = mcuZjBridge.getConferenceManageApi().deleteRecordsFiles(cmDeleteRecordsFilesRequest);
                                if (cmDeleteRecordsFilesResponse != null) {
                                    cmDeleteRecordsFilesRequest.setUuids(new String[]{uuid});
                                    cmDeleteRecordsFilesRequest.setAction(2);
                                    CmDeleteRecordsFilesResponse thoroughDeleteRecordsFilesResponse = mcuZjBridge.getConferenceManageApi().deleteRecordsFiles(cmDeleteRecordsFilesRequest);
                                    if (thoroughDeleteRecordsFilesResponse != null) {
                                        LOGGER.info("删除MCU上的文件");
                                    }
                                }
                            } catch (Exception e) {
                                LOGGER.error("下载录制文件错误", e);
                            }
                        }
                    }
                }
            }
        }
    }

    private static String getSavePath() {
        String os = System.getProperty("os.name");
        if (os.contains("indows")) {
            return PathUtil.getRootPath();
        } else {
            return "/mnt/nfs/spaces";
        }
    }
}
