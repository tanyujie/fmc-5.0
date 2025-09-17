package com.paradisecloud.fcm.fme.conference.impls;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.Region;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.FileUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeDataCache;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiRecordsService;
import com.paradisecloud.fcm.fme.conference.task.UpdateRecordsTask;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.terminal.fs.util.SpringContextUtil;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 录制文件记录Service业务层处理
 *
 * @author lilinhai
 * @date 2021-05-07
 */
@Service
public class BusiRecordsServiceImpl implements IBusiRecordsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IBusiRecordsService.class);

    @Resource
    private BusiRecordsMapper busiRecordsMapper;

    @Resource
    private IBusiConferenceService iBusiConferenceService;

    @Resource
    private TaskService taskService;

    @Resource
    private CdrRecordingMapper cdrRecordingMapper;
    /**
     * 查询录制文件记录
     *
     * @param id 录制文件记录ID
     * @return 录制文件记录
     */
    @Override
    public BusiRecords selectBusiRecordsById(Long id) {
        return busiRecordsMapper.selectBusiRecordsById(id);
    }

    /**
     * 查询录制文件记录列表
     *
     * @param busiRecords 录制文件记录
     * @return 录制文件记录
     */
    @Override
    public List<BusiRecords> selectBusiRecordsList(BusiRecords busiRecords) {
        return busiRecordsMapper.selectBusiRecordsList(busiRecords);
    }

    /**
     * 新增录制文件记录
     *
     * @param busiRecords 录制文件记录
     * @return 结果
     */
    @Override
    public int insertBusiRecords(BusiRecords busiRecords) {
        busiRecords.setCreateTime(new Date());
        return busiRecordsMapper.insertBusiRecords(busiRecords);
    }

    /**
     * 修改录制文件记录
     *
     * @param busiRecords 录制文件记录
     * @return 结果
     */
    @Override
    public int updateBusiRecords(BusiRecords busiRecords) {
        busiRecords.setUpdateTime(new Date());
        return busiRecordsMapper.updateBusiRecords(busiRecords);
    }

    @Override
    public int updateBusiRecords(boolean recording, String contextKey) {
        int num = 0;
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
        Long deptId = conferenceContext.getDeptId();
        if (recording) {
            int spaceStatus = getRecordSpaceStatus();
            if (spaceStatus == 0) {
                Assert.isTrue(false, "不能开启录制，未设置录制空间容量！");
            } else if (spaceStatus == 2) {
                String region = ExternalConfigCache.getInstance().getRegion();
                if(Objects.equals(region,"ops")){
                    Assert.isTrue(false, "不能开启录制，授权录制容量不足！");
                }else {
                    Assert.isTrue(false, "不能开启录制，磁盘空间不可用！");
                }

            }
        }

        String coSpaceId = FmeDataCache.getCoSpaceByConferenceNumber(deptId, conferenceContext.getConferenceNumber()).getId();
        iBusiConferenceService.updateCallRecordStatus(contextKey, recording);

        if (!recording) {
            UpdateRecordsTask updateRecordsTask = new UpdateRecordsTask(coSpaceId, 20000, deptId, coSpaceId, conferenceContext.getConferenceNumber(), conferenceContext.getName());
            taskService.addTask(updateRecordsTask);
        }
        return num;
    }

    /**
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

        Collection<ConferenceContext> values = ConferenceContextCache.getInstance().values();
        int i = 1;
        if (values != null && values.size() > 0) {
            for (ConferenceContext value : values) {
                if (value.isRecorded()) {
                    i++;
                }
            }
        }

        Map<String, Object> stringObjectMap = reportRecordSpace(null);
        Double totalUsableSpace = (Double) stringObjectMap.get("totalUsableSpace");

        if (recordingFilesStorageSpaceMax != null) {
            String region = ExternalConfigCache.getInstance().getRegion();
            if ("ops".equalsIgnoreCase(region)) {
                
            } else {
                totalUsableSpace = totalUsableSpace + (i * 3);
            }
            if (recordingFilesStorageSpaceMax.compareTo(totalUsableSpace) >= 0) {
                result = 1;
            }
        }
        return result;
    }

    /**
     * totalFreeSpace:可用空间
     * totalUsableSpace:已用空间
     *
     * @param deptId
     * @return
     */
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
        map.put("totalFreeSpace", FileUtil.getFileSize(totalFreeSpace, FileUtil.SIZE_UNIT_GB));
        map.put("totalUsableSpace", FileUtil.getFileSize(totalUsableSpace, FileUtil.SIZE_UNIT_GB));
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
     * 批量删除录制文件记录
     *
     * @param ids 需要删除的录制文件记录ID
     * @return 结果
     */
    @Override
    public int deleteBusiRecordsByIds(Long[] ids) {
        return busiRecordsMapper.deleteBusiRecordsByIds(ids);
    }

    /**
     * 删除录制文件记录信息
     *
     * @param id 录制文件记录ID
     * @return 结果
     */
    @Override
    public int deleteBusiRecordsById(Long id) {
        return busiRecordsMapper.deleteBusiRecordsById(id);
    }

    @Override
    public List<BusiRecords> selectBusiRecordsListGroup(String searchKey, int pageIndex, int pageSize) {
        BusiRecords busiRecords=new BusiRecords();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        busiRecords.setDeptId(loginUser.getUser().getDeptId());
        busiRecords.setSearchValue(searchKey);
        return busiRecordsMapper.selectBusiRecordsListGroup(busiRecords);
    }

    @Override
    public Map<String, Object> getRecordFileInfo(Long deptId, String coSpaceId, String contextKey) {
        if (deptId != null && StringUtils.isNotEmpty(coSpaceId)) {
            try {
                String callId = null;
                ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
                if (conferenceContext != null) {
                    Map<String, Attendee> uuidAttendeeMapByUri = conferenceContext.getUuidAttendeeMapByUri("recording@recorder.com");
                    for (String key : uuidAttendeeMapByUri.keySet()) {
                        Attendee attendee = uuidAttendeeMapByUri.get(key);
                        if (attendee.getRemoteParty().equals("recording@recorder.com") && attendee.getIp().equals("recorder.com")) {
                            callId = attendee.getCallId();
                        }
                    }
                    if (StringUtils.isNotEmpty(callId)) {
                        CdrRecording cdrRecording = new CdrRecording();
                        cdrRecording.setCallId(callId);
                        List<CdrRecording> cdrRecordingList = cdrRecordingMapper.selectCdrRecordingListDesc(cdrRecording);
                        if (cdrRecordingList != null && cdrRecordingList.size() > 0) {
                            CdrRecording cdrRecordingNew = cdrRecordingList.get(0);
                            String path = cdrRecordingNew.getPath();
                            String fileName = StringUtils.substringAfterLast(path, "_") + ".mp4";
                            Map<String, Object> map = new HashMap<>();
                            map.put("coSpaceId", coSpaceId);
                            map.put("fileName", fileName);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                            map.put("fileCreateTime", simpleDateFormat.format(cdrRecordingNew.getTime()));
                            return map;
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("获取录制文件xx错误。", e);
            }
        }

        return null;
    }

}
