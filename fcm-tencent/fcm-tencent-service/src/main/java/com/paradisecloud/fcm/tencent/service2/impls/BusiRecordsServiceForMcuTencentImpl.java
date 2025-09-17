package com.paradisecloud.fcm.tencent.service2.impls;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.dao.mapper.BusiConfigMapper;
import com.paradisecloud.fcm.dao.mapper.BusiRecordSettingMapper;
import com.paradisecloud.fcm.dao.mapper.BusiRecordsMapper;
import com.paradisecloud.fcm.dao.model.BusiConfig;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiRecordSetting;
import com.paradisecloud.fcm.dao.model.BusiRecords;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContextCache;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiRecordsForMcuTencentService;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiTencentConferenceService;
import com.paradisecloud.fcm.terminal.fs.util.SpringContextUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 录制文件记录Service业务层处理
 *
 * @author lilinhai
 * @date 2021-05-07
 */
@Service
public class BusiRecordsServiceForMcuTencentImpl implements IBusiRecordsForMcuTencentService {

    @Resource
    private BusiRecordsMapper busiRecordsMapper;
    @Resource
    private BusiRecordSettingMapper busiRecordSettingMapper;
    @Resource
    private IBusiTencentConferenceService iBusiTencentConferenceService;

    @Override
    public int updateBusiRecords(boolean recording, String contextKey) {
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);

        int spaceStatus = getRecordSpaceStatus();
        if (spaceStatus == 0) {
            Assert.isTrue(false, "不能开启录制，未设置录制空间容量！");
        } else if (spaceStatus == 2) {
            Assert.isTrue(false, "不能开启录制，磁盘空间不可用！");
        }

        boolean success = iBusiTencentConferenceService.updateCallRecordStatus(contextKey, recording);

        if (success) {
            String coSpaceId = conferenceContext.getCoSpaceId();
            if (recording) {
            } else {
            }
        }
        return 0;
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

    /**
     *
     * @return 0:未设置, 1:可用, 2:不可用
     */
    public int getRecordSpaceStatus() {
        int result = 2;

        BusiConfigMapper busiConfigMapper = (BusiConfigMapper) SpringContextUtil.getBean("busiConfigMapper");
        BusiConfig busiConfig1 = new BusiConfig();
        busiConfig1.setConfigKey("Recording_Files_Storage_Space_Max");
        List<BusiConfig> busiConfigs = busiConfigMapper.selectBusiConfigList(busiConfig1);

        Double recordingFilesStorageSpaceMax = null;
        if (busiConfigs != null && busiConfigs.size() > 0) {
            recordingFilesStorageSpaceMax = Double.valueOf(busiConfigs.get(0).getConfigValue());
        } else {
            result = 0;
            return result;
        }

        Collection<TencentConferenceContext> values = TencentConferenceContextCache.getInstance().values();
        int i = 1;
        if (values != null && values.size() > 0) {
            for (TencentConferenceContext value : values) {
                if (value.isRecorded()) {
                    i++;
                }
            }
        }

        Map<String, Object> stringObjectMap = reportRecordSpace(null);
        Double totalUsableSpace = (Double) stringObjectMap.get("totalUsableSpace");

        if (recordingFilesStorageSpaceMax != null) {
            totalUsableSpace = totalUsableSpace - (i * 2);
            if (recordingFilesStorageSpaceMax.compareTo(totalUsableSpace) >= 1) {
                result = 1;
            }
        }
        return result;
    }

    public Map<String, Object> reportRecordSpace(Long deptId) {

        deptId = null;
        BusiRecordSetting busiRecordSetting = new BusiRecordSetting();
        busiRecordSetting.setDeptId(deptId);
        BusiRecordSettingMapper busiRecordSettingMapper = (BusiRecordSettingMapper) SpringContextUtil.getBean("busiRecordSettingMapper");
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

        for (String p : basePathSet) {
            File f = new File(p);
            if (f.isDirectory()) {
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


    /**
     * 获取录制文件路径
     *
     * @param coSpaceId
     * @return
     */
    private String getRecordFilePath(Long deptId, String coSpaceId) {
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

    /**
     * 读取录制文件文件名
     *
     * @param conferenceNumber
     * @param conferenceContext
     * @param busiRecords
     * @param path
     */
    private void readRecordFileName(String conferenceNumber, TencentConferenceContext conferenceContext, BusiRecords busiRecords, String path) {
        if (StringUtils.isEmpty(path)) {
            return;
        }
        File file = new File(path);
        if (file.exists()) {
            List<File> files = Arrays.asList(file.listFiles());
            if (null != files && files.size() > 0) {
                File createFile = files.stream().sorted(Comparator.comparing(File::lastModified).reversed()).limit(1).collect(Collectors.toList()).get(0);
                busiRecords.setFileName(new StringBuffer(conferenceNumber).append("_").append(conferenceContext.getName()).append("_录制文件").append(files.size()).toString());
                busiRecords.setRealName(createFile.getName());
            }
        }
    }
}
