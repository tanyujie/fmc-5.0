package com.paradisecloud.fcm.fme.conference.task;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.FileUtil;
import com.paradisecloud.fcm.dao.mapper.BusiRecordSettingMapper;
import com.paradisecloud.fcm.dao.mapper.BusiRecordsMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiRecordSetting;
import com.paradisecloud.fcm.dao.model.BusiRecords;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 更新录制文件数据任务
 */
public class UpdateRecordsTask extends Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateRecordsTask.class);

    private Long deptId;
    private String coSpaceId;
    private String conferenceNumber;
    private String conferenceName;

    public UpdateRecordsTask(String id, long delayInMilliseconds, Long deptId, String coSpaceId, String conferenceNumber, String conferenceName) {
        super("update_r_" + id, delayInMilliseconds);
        this.deptId = deptId;
        this.coSpaceId = coSpaceId;
        this.conferenceNumber = conferenceNumber;
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
        LOGGER.info("更新录制文件任务开始。");

        if (deptId != null) {
            BusiTemplateConferenceMapper busiTemplateConferenceMapper = BeanFactory.getBean(BusiTemplateConferenceMapper.class);
            BusiRecordsMapper busiRecordsMapper = BeanFactory.getBean(BusiRecordsMapper.class);
            if (StringUtils.isEmpty(conferenceName)) {
                BusiTemplateConference busiTemplateConference = new BusiTemplateConference();
                busiTemplateConference.setConferenceNumber(Long.valueOf(conferenceNumber));
                List<BusiTemplateConference> busiTemplateConferences = busiTemplateConferenceMapper.selectBusiTemplateConferenceList(busiTemplateConference);
                if (busiTemplateConferences != null && busiTemplateConferences.size() > 0) {
                    conferenceName = busiTemplateConferences.get(0).getName();
                }
            }
            if (StringUtils.isEmpty(conferenceName)) {
                conferenceName = "会议";
            }
            String path = getRecordFilePath(deptId, coSpaceId);

            // 检查文件存在，数据库无数据
            try {
                File fileDir = new File(path);
                if (fileDir.exists()) {
                    List<File> files = Arrays.asList(fileDir.listFiles());
                    if (null != files && files.size() > 0) {
                        List<File> sortedFiles = files.stream().sorted(Comparator.comparing(File::lastModified).reversed()).collect(Collectors.toList());
                        for (File file : sortedFiles) {
                            try {
                                if (file.getName().endsWith(".mp4")) {
                                    BusiRecords busiRecordsExistCon = new BusiRecords();
                                    busiRecordsExistCon.setCoSpaceId(coSpaceId);
                                    busiRecordsExistCon.setRealName(file.getName());
                                    List<BusiRecords> busiRecordsExistList = busiRecordsMapper.selectBusiRecordsList(busiRecordsExistCon);
                                    if (busiRecordsExistList == null || busiRecordsExistList.size() == 0) {
                                        BusiRecords busiRecordsNew = new BusiRecords();
                                        busiRecordsNew.setConferenceNumber(Long.valueOf(conferenceNumber));
                                        busiRecordsNew.setCoSpaceId(coSpaceId);
                                        busiRecordsNew.setDeptId(deptId);
                                        busiRecordsNew.setTemplateName(conferenceName);
                                        busiRecordsNew.setRealName(file.getName());
                                        BasicFileAttributeView fileAttributeView = Files.getFileAttributeView(file.toPath(), BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
                                        Date fileCreateTime = new Date(fileAttributeView.readAttributes().creationTime().toMillis());
                                        busiRecordsNew.setCreateTime(fileCreateTime);
                                        String createTimeStr = DateUtil.convertDateToString(fileCreateTime, "yyyyMMddHHmmss");
                                        String fileName = new StringBuffer(conferenceNumber).append("_").append(conferenceName).append("_录制文件_").append(createTimeStr).toString();
                                        busiRecordsNew.setFileName(fileName);
                                        busiRecordsNew.setFileSize(FileUtil.getFileSizeWithUnit(file.length()));
                                        busiRecordsNew.setRecordsFileStatus(2);
                                        LOGGER.info("busiRecords :" + busiRecordsNew);
                                        busiRecordsMapper.insertBusiRecords(busiRecordsNew);
                                    }
                                }
                            } catch (Exception e) {
                                LOGGER.error("匹配录制文件错误，", e);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("匹配录制文件错误。", e);
            }

            // 删除数据库中的空数据
            try {
                List<BusiRecords> busiRecordsList = busiRecordsMapper.selectBusiRecordsByCoSpaceId(deptId, coSpaceId, Boolean.FALSE);
                for (int i = 0; i < busiRecordsList.size(); i++) {
                    BusiRecords busiRecordsTemp = busiRecordsList.get(i);
                    if (StringUtils.isEmpty(busiRecordsTemp.getRealName())) {
                        busiRecordsMapper.deleteBusiRecordsById(busiRecordsTemp.getId());
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    private String getRecordFilePath(Long deptId, String coSpaceId) {
        BusiRecordSettingMapper busiRecordSettingMapper = BeanFactory.getBean(BusiRecordSettingMapper.class);
        BusiRecordSetting recordSetting = new BusiRecordSetting();
        recordSetting.setStatus(YesOrNo.YES.getValue());
        recordSetting.setDeptId(deptId);
        List<BusiRecordSetting> busiRecordSettings = busiRecordSettingMapper.selectBusiRecordSettingList(recordSetting);
        Assert.isTrue(busiRecordSettings != null && !busiRecordSettings.isEmpty(), "请确保有一个录制配置处于开启状态");
        return new StringBuffer(busiRecordSettings.get(0).getPath())
                .append("/")
                .append(busiRecordSettings.get(0).getFolder())
                .append(File.separator)
                .append(coSpaceId)
                .toString();
    }

    private void readRecordFileName(String conferenceNumber, String templateName, BusiRecords busiRecords, String path) {
        if (StringUtils.isEmpty(path)) {
            return;
        }
        File file = new File(path);
        if (file.exists()) {
            List<File> files = Arrays.asList(file.listFiles());
            if (null != files && files.size() > 0) {
                File createFile = files.stream().sorted(Comparator.comparing(File::lastModified).reversed()).limit(1).collect(Collectors.toList()).get(0);
                Date createTime;
                BasicFileAttributeView fileAttributeView = Files.getFileAttributeView(createFile.toPath(), BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
                try {
                    createTime = new Date(fileAttributeView.readAttributes().creationTime().toMillis());
                } catch (IOException e) {
                    LOGGER.error("文件属性读取异常：", e);
                    createTime = new Date();
                }
                String createTimeStr = DateUtil.convertDateToString(createTime, "yyyyMMddHHmmss");
                String fileName = new StringBuffer(conferenceNumber).append("_").append(templateName).append("_录制文件_").append(createTimeStr).toString();
                busiRecords.setFileName(fileName.replaceAll(".temp", ".mp4"));
                busiRecords.setRealName(createFile.getName());
                busiRecords.setCreateTime(createTime);
                busiRecords.setFileSize(FileUtil.getFileSizeWithUnit(createFile.length()));
            }
        }
    }
}
