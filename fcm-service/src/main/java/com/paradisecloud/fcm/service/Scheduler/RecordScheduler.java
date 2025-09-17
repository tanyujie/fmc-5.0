package com.paradisecloud.fcm.service.Scheduler;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.cache.CommonConfigCache;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.dao.mapper.BusiConfigMapper;
import com.paradisecloud.fcm.dao.mapper.BusiRecordSettingMapper;
import com.paradisecloud.fcm.dao.mapper.BusiRecordsMapper;
import com.paradisecloud.fcm.dao.mapper.CdrRecordingMapper;
import com.paradisecloud.fcm.dao.model.BusiConfig;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiRecordSetting;
import com.paradisecloud.fcm.dao.model.BusiRecords;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Component
public class RecordScheduler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private BusiRecordSettingMapper busiRecordSettingMapper;

    @Resource
    private BusiConfigMapper busiConfigMapper;

    @Resource
    private BusiRecordsMapper  busiRecordsMapper;

    @Resource
    private CdrRecordingMapper cdrRecordingMapper;

    /**
     * 每天03:17启动删除过期录制文件
     */
    @Scheduled(cron = "0 17 3 * * ?")
    public void deleteExpiredRecordingFiles() {
        logger.info("删除过期录制文件定时任务启动");
        List<BusiRecordSetting> busiRecordSettings = busiRecordSettingMapper.selectBusiRecordSettingList(new BusiRecordSetting());
        if (busiRecordSettings != null && busiRecordSettings.size() > 0){
            for (BusiRecordSetting busiRecordSetting : busiRecordSettings) {
                checkReclaimFile();
                check(busiRecordSetting);
            }
        }
    }

    private void checkReclaimFile() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -1);
        Date retentionTime = cal.getTime();
        List<BusiRecords> busiRecords = busiRecordsMapper.selectReclaim(retentionTime);
        for (BusiRecords busiRecord : busiRecords) {
            if (busiRecord.getRecordsFileStatus() == 1) {
                deleteRecordingFile(busiRecord.getRealName(), busiRecord.getCoSpaceId(), busiRecord.getId());
                logger.info("定期删除回收站录制文件====》" + busiRecord.getId());
            }
        }
    }

    private void check(BusiRecordSetting recordSetting){
        try {
            logger.info("录制管理定期清理");
            boolean isExpired = false;
            if (recordSetting.getRetentionType() == null)
            {
                isExpired = false;
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.MONTH, -recordSetting.getRetentionType());
            Date retentionTime = cal.getTime();
//            List<BusiRecords> busiRecords = recordsMapper.selectByRetentionTime(retentionTime);

            Date startTime = retentionTime;
            Date endTime = new Date();

            List<BusiRecords> busiRecordsIsDelect = busiRecordsMapper.selectIsDelect(startTime, endTime);

            if (busiRecordsIsDelect != null && busiRecordsIsDelect.size() > 0){
                isExpired = true;
            }
            if (isExpired){
                Double externalRecordingFilesStorageSpaceMax = ExternalConfigCache.getInstance().getRecordingFilesStorageSpaceMax();
                Double recordingFilesStorageSpaceMaxLimit = CommonConfigCache.getInstance().getRecordingFilesStorageSpaceMax();
                if (externalRecordingFilesStorageSpaceMax != null) {
                    recordingFilesStorageSpaceMaxLimit = externalRecordingFilesStorageSpaceMax;
                }
                Double usableSpace;
                if (externalRecordingFilesStorageSpaceMax != null) {
                    usableSpace = externalRecordingFilesStorageSpaceMax;
                } else {
                    File file = new File("/");
                    usableSpace = getFileSizeToG(file.getUsableSpace());
                    usableSpace = usableSpace - 50;
                }

                BusiConfig busiConfigs = new BusiConfig();
                busiConfigs.setConfigKey("Recording_Files_Storage_Space_Max");
                List<BusiConfig> busiConfigList = busiConfigMapper.selectBusiConfigList(busiConfigs);
                Double totalSpace = null;
                if (busiConfigList != null && busiConfigList.size() > 0) {
                    totalSpace = Double.valueOf(busiConfigList.get(0).getConfigValue());
                } else {
                    if (usableSpace.compareTo(recordingFilesStorageSpaceMaxLimit) < 0) {
                        totalSpace = usableSpace;
                    } else {
                        totalSpace = recordingFilesStorageSpaceMaxLimit;
                    }
                }

                if (totalSpace > 0) {
                    Map<String, Object> stringObjectMap = reportRecordSpace(null);
                    Double totalUsableSpace = (Double) stringObjectMap.get("totalUsableSpace");

                    Double v = totalSpace / 2;
                    if (v.compareTo(totalUsableSpace) <= -1 && v < 30) {
                        logger.info("录制文件---数据库++===》》" + busiRecordsIsDelect);
                        if (busiRecordsIsDelect.size() > 0 && busiRecordsIsDelect != null) {
                            for (BusiRecords busiRecord : busiRecordsIsDelect) {
                                String realName = busiRecord.getRealName().replaceAll(".temp", ".mp4");
                                busiRecord.setRealName(realName);
                                deleteRecordingFile(busiRecord.getRealName(), busiRecord.getCoSpaceId(), busiRecord.getId());
                                logger.info("定期删除录制文件====》" + busiRecord.getId());
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.error("录制管理定期清理错误。", e);
        }
    }

    public void deleteRecordingFile( String fileName, String coSpaceId, Long id) {

        BusiRecordSetting recordSetting = new BusiRecordSetting();
        recordSetting.setStatus(YesOrNo.YES.getValue());
        List<BusiRecordSetting> busiRecordSettings = busiRecordSettingMapper.selectBusiRecordSettingList(recordSetting);
        if (busiRecordSettings == null || busiRecordSettings.isEmpty()) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer(busiRecordSettings.get(0).getPath())
                .append("/")
                .append(busiRecordSettings.get(0).getFolder())
                .append(File.separator).append(coSpaceId);
        String path = (ObjectUtils.isEmpty(fileName)) ? stringBuffer.toString() : stringBuffer.append(File.separator).append(fileName).toString();
        File file = new File(path);
        logger.info("deleteRecordingFile文件路径:{}", path);
        if (!file.exists()) {
            throw new CustomException("文件不存在");
        }
        busiRecordsMapper.deleteBusiRecordsById(id);

        String cdrPath = (ObjectUtils.isEmpty(fileName)) ? coSpaceId : coSpaceId + "_" + fileName;
        cdrRecordingMapper.deleteByPath(cdrPath);
        deleteFile(file);
    }

    private void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        File[] files = file.listFiles();
        for (File f : files) {
            deleteFile(f);
        }
        file.delete();
    }

    /**
     * totalFreeSpace:可用空间
     * totalUsableSpace:已用空间
     * @param deptId
     * @return
     */
    public Map<String, Object> reportRecordSpace(Long deptId) {

        deptId = null;
        BusiRecordSetting busiRecordSetting = new BusiRecordSetting();
        busiRecordSetting.setDeptId(deptId);
        List<BusiRecordSetting> recordSettingList = busiRecordSettingMapper.selectBusiRecordSettingList(busiRecordSetting);
        if (deptId != null) {
            BusiHistoryConference busiHistoryConference = new BusiHistoryConference();
            busiHistoryConference.setDeptId(deptId);
        }
        Map<String, Object> map = new HashMap<>();

        long totalFreeSpace = 0;
        long totalUsableSpace = 0;
        int fileCount = 0;
        Set<String> basePathSet = new HashSet<>();
        for (BusiRecordSetting recordSetting : recordSettingList) {
            basePathSet.add(new StringBuilder(recordSetting.getPath()).append(File.separator).append(recordSetting.getFolder()).toString());
        }

        for (String p : basePathSet)
        {
            File f = new File(p);
            if (f.isDirectory())
            {
                totalUsableSpace += FileUtils.sizeOfDirectory(f);
                totalFreeSpace += f.getUsableSpace();
                fileCount += getFileCount(f);
            }
        }
        map.put("totalFreeSpace", getFileSizeToG(totalFreeSpace));
        map.put("totalUsableSpace", getFileSizeToG(totalUsableSpace));
        map.put("fileCount", fileCount);
        return map;
    }

    private Double getFileSizeToG(long fileSize) {

        //临界值
        long criticalSize = 1024 * 1024;
        long gbCriticalSize = 1024 * 1024 * 1024;
        BigDecimal fileSizeDecimal = new BigDecimal(fileSize);
        BigDecimal criticalSizeDecimal = new BigDecimal(criticalSize);
        double size = 0.0;
        StringBuffer stringBuffer = new StringBuffer();

        if (fileSize >= gbCriticalSize) {
            size = fileSizeDecimal.divide(new BigDecimal(gbCriticalSize)).setScale(2, RoundingMode.HALF_UP).doubleValue();

        } else if (fileSize >= criticalSize) {
            size = fileSizeDecimal.divide(criticalSizeDecimal).setScale(2, RoundingMode.HALF_UP).doubleValue();
            size = size / 1024;
        } else if (fileSize < criticalSize && fileSize > 0) {
            size = 0.001;
        } else if (fileSize == 0) {
            size = 0;
        }
        return size;
    }

    private int getFileCount(File file) {
        int count = 0;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isFile()) {
                    count++;
                }
                count += getFileCount(f);
            }
        }
        return count;
    }

}
