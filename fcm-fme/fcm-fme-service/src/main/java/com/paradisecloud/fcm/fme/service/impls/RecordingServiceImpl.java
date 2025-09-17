package com.paradisecloud.fcm.fme.service.impls;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.FileUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.RecordsSearchVo;
import com.paradisecloud.fcm.fme.conference.task.UpdateRecordsTask;
import com.paradisecloud.fcm.fme.service.impls.vo.BusiConferenceNumberVo;
import com.paradisecloud.fcm.fme.service.interfaces.IRecordingService;
import com.paradisecloud.system.utils.SecurityUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author johnson liu
 * @date 2021/4/30 11:30
 */
@Service
public class RecordingServiceImpl implements IRecordingService {

    private Logger logger = LoggerFactory.getLogger(RecordingServiceImpl.class);
    @Resource
    private BusiRecordSettingMapper busiRecordSettingMapper;
    @Resource
    private BusiRecordsMapper busiRecordsMapper;
    @Resource
    private CdrRecordingMapper cdrRecordingMapper;
    @Resource
    private BusiHistoryConferenceMapper busiHistoryConferenceMapper;
    @Resource
    private BusiConfigMapper busiConfigMapper;
    @Resource
    private TaskService taskService;

    @Override
    public List<Map<String, Object>> getFolder(String conferenceNumber, Long deptId, String coSpaceId) {
        deptId = (deptId == null) ? SecurityUtils.getLoginUser().getUser().getDeptId() : deptId;
        BusiRecordSetting recordSetting = new BusiRecordSetting();
        recordSetting.setStatus(YesOrNo.YES.getValue());
        recordSetting.setDeptId(deptId);
        List<BusiRecordSetting> busiRecordSettings = busiRecordSettingMapper.selectBusiRecordSettingList(recordSetting);
        if (busiRecordSettings == null || busiRecordSettings.isEmpty()) {
            return null;
        }

        List<Map<String, Object>> folders = new ArrayList<>();
        String comUrl = (busiRecordSettings != null && busiRecordSettings.size() > 0) ? busiRecordSettings.get(0).getUrl() : "";
        if (comUrl.lastIndexOf("/") < comUrl.length() - 1) {
            comUrl += "/" + coSpaceId;
        } else {
            comUrl += coSpaceId;
        }

        BusiRecords busiRecordsCon = new BusiRecords();
        busiRecordsCon.setDeptId(deptId);
        busiRecordsCon.setCoSpaceId(coSpaceId);
        busiRecordsCon.setConferenceNumber(Long.valueOf(conferenceNumber));
        busiRecordsCon.setRecordsFileStatus(2);
        List<BusiRecords> busiRecordsList = busiRecordsMapper.selectBusiRecordsList(busiRecordsCon);
        logger.debug("\\\\\\\\\\\\" + busiRecordsList);
        boolean process = false;
        for (int i = 0; i < busiRecordsList.size(); i++) {
            BusiRecords busiRecords = busiRecordsList.get(i);
            if (StringUtils.hasText(busiRecords.getRealName())) {
                Map<String, Object> map = new HashMap<>();
                String url = comUrl + "/" + busiRecords.getRealName();
                map.put("fileName", busiRecords.getFileName());
                map.put("realName", busiRecords.getRealName());
                map.put("recordingTime", busiRecords.getCreateTime());
                map.put("fileSize", busiRecords.getFileSize());
                map.put("url", url);
                map.put("id", busiRecords.getId());
                map.put("coSpaceId", coSpaceId);
                map.put("deptId", deptId);
                map.put("recordsFileStatus", busiRecords.getRecordsFileStatus());
                if(Objects.equals("record",busiRecords.getCoSpaceId())){
                    String recordUrl = (busiRecordSettings != null && busiRecordSettings.size() > 0) ? busiRecordSettings.get(0).getUrl() : "";
                    String replace = recordUrl.replace("/spaces", "/record");
                    map.put("url", replace +  "/"+busiRecords.getRealName());
                }
                folders.add(map);
            } else {
                process = true;
            }
        }
        if (process) {
            UpdateRecordsTask updateRecordsTask = new UpdateRecordsTask(coSpaceId, 5000, deptId, coSpaceId, conferenceNumber, null);
            taskService.addTask(updateRecordsTask);
        }

        return folders;
    }

    @Override
    public void deleteRecordingFile(String ids, String fileName, String coSpaceId, boolean force) {
        Long[] idList = null;
        if (StringUtils.hasText(ids)) {
            String[] split = ids.split(",");
            idList = Arrays.stream(split).map(b -> Long.parseLong(b)).toArray(Long[]::new);
        }
        BusiRecordSetting recordSetting = new BusiRecordSetting();
        recordSetting.setStatus(YesOrNo.YES.getValue());
        List<BusiRecordSetting> busiRecordSettings = busiRecordSettingMapper.selectBusiRecordSettingList(recordSetting);
        if (busiRecordSettings == null || busiRecordSettings.isEmpty()) {
            return;
        }
//        StringBuffer stringBuffer = new StringBuffer(busiRecordSettings.get(0).getPath())
//                .append("/")
//                .append(busiRecordSettings.get(0).getFolder())
//                .append(File.separator).append(coSpaceId);
//        String path = (ObjectUtils.isEmpty(fileName)) ? stringBuffer.toString() : stringBuffer.append(File.separator).append(fileName).toString();
//        logger.info("deleteRecordingFile文件路径:{}", path);
        if (idList != null) {
            for (Long id : idList) {
                BusiRecords records = new BusiRecords();
                records.setId(id);
                records.setDeleteTime(new Date());
                records.setRecordsFileStatus(1);
                busiRecordsMapper.updateBusiRecords(records);
                if (force) {
                    deleteRecoverRecordingFile(records.getId().toString());
                }
            }
        } else if (!ObjectUtils.isEmpty(coSpaceId)) {
            //根据coSpaceId删除
            BusiRecords records = new BusiRecords();
            records.setCoSpaceId(coSpaceId);
            List<BusiRecords> busiRecords = busiRecordsMapper.selectBusiRecordsList(records);
            for (BusiRecords busiRecord : busiRecords) {
                busiRecord.setRecordsFileStatus(1);
                busiRecord.setDeleteTime(new Date());
                busiRecordsMapper.updateBusiRecords(busiRecord);
                if (force) {
                    deleteRecoverRecordingFile(busiRecord.getId().toString());
                }
            }
        }
//        String cdrPath = (ObjectUtils.isEmpty(fileName)) ? coSpaceId : coSpaceId + "_" + fileName;
//        cdrRecordingMapper.deleteByPath(cdrPath);
//        deleteFile(file);
    }

    @Override
    public PaginationData<BusiConferenceNumberVo> getBusiConferenceNumberVoList(RecordsSearchVo recordsSearchVo) {
        PaginationData<BusiConferenceNumberVo> busiConferenceNumberVoList = new PaginationData<>();
        
        BusiRecordSetting busiRecordSetting = new BusiRecordSetting();
        busiRecordSetting.setDeptId(recordsSearchVo.getDeptId());
        List<BusiRecordSetting> busiRecordSettings = busiRecordSettingMapper.selectBusiRecordSettingList(busiRecordSetting);
        logger.info("busiRecordSettings:" + busiRecordSettings);
        if (busiRecordSettings == null || busiRecordSettings.isEmpty()) {
            return busiConferenceNumberVoList;
        }
        Long deptId = (recordsSearchVo.getDeptId() == null) ? SecurityUtils.getLoginUser().getUser().getDeptId() : recordsSearchVo.getDeptId();
        if (recordsSearchVo.getPageNum() == null || recordsSearchVo.getPageNum() <= 0) {
            recordsSearchVo.setPageNum(1);
        }
        if (recordsSearchVo.getPageSize() == null || recordsSearchVo.getPageSize() > 100) {
            recordsSearchVo.setPageSize(100);
        }
        PageHelper.startPage(recordsSearchVo.getPageNum(), recordsSearchVo.getPageSize());
        List<BusiRecordsSearchResult> busiRecordsSearchResultList = busiRecordsMapper.selectBusiRecordsListForGroup(deptId, recordsSearchVo.getSearchKey());
        if (busiRecordsSearchResultList != null) {
            PageInfo<?> pageInfo = new PageInfo<>(busiRecordsSearchResultList);
            for (int i = 0; i < busiRecordsSearchResultList.size(); i++) {
                BusiRecordsSearchResult busiRecordsSearchResult = busiRecordsSearchResultList.get(i);
                BusiConferenceNumberVo vo = new BusiConferenceNumberVo();
                vo.setRecordFileNum(busiRecordsSearchResult.getRecordFileNum());
                vo.setRecordingTimeOfLate(busiRecordsSearchResult.getRecordingTimeOfLate());
                vo.setName(busiRecordsSearchResult.getName());
                vo.setConferenceNumber(busiRecordsSearchResult.getConferenceNumber());
                String coSpaceId = busiRecordsSearchResult.getCoSpaceId();
                try {
                    if (coSpaceId.endsWith("-zj") || coSpaceId.endsWith("-plc") || coSpaceId.endsWith("-kdc")) {
                        if (coSpaceId.contains("-")) {
                            vo.setConferenceNumber(Long.valueOf(coSpaceId.substring(0, coSpaceId.indexOf("-"))));
                        } else {
                            vo.setConferenceNumber(Long.valueOf(coSpaceId));
                        }
                    }
                } catch (Exception e) {
                }
                vo.setDeptId(busiRecordsSearchResult.getDeptId());
                vo.setCoSpaceId(busiRecordsSearchResult.getCoSpaceId());
                busiConferenceNumberVoList.addRecord(vo);
            }

            busiConferenceNumberVoList.setTotal(pageInfo.getTotal());
            busiConferenceNumberVoList.setSize(pageInfo.getSize());
            busiConferenceNumberVoList.setPage(pageInfo.getPageNum());
        }

        return busiConferenceNumberVoList;
    }

    /**
     * 首页录制文件空间统计
     *
     * @param deptId
     * @return
     */
    @Override
    public Map<String, Object> reportRecordSpace(Long deptId) {

        deptId = (deptId == null) ? SecurityUtils.getLoginUser().getUser().getDeptId() : deptId;
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

        List<BusiConfig> busiConfigs = busiConfigMapper.selectBusiConfigList(new BusiConfig());
        Double recordingFilesStorageSpaceMax = null;
        if (busiConfigs != null && busiConfigs.size() > 0) {
            for (BusiConfig busiConfig : busiConfigs) {
                if (busiConfig.getConfigKey().equals("Recording_Files_Storage_Space_Max")) {
                    recordingFilesStorageSpaceMax = Double.valueOf(busiConfig.getConfigValue());
                }
            }
        }

        map.put("totalUsableSpace", FileUtil.getFileSizeWithUnit(totalUsableSpace));

        String region = ExternalConfigCache.getInstance().getRegion();
        if(Objects.equals("ops",region)){
            if (recordingFilesStorageSpaceMax != null) {
                String fileSizeWithUnit = FileUtil.getFileSizeWithUnit(totalUsableSpace);
                if(fileSizeWithUnit.contains("M")){
                    double fileSize = FileUtil.getFileSize(totalUsableSpace, FileUtil.SIZE_UNIT_MB);
                    StringBuffer stringBuffer = new StringBuffer();
                    totalFreeSpace = (long) ((1024*recordingFilesStorageSpaceMax)-(fileSize/1024/1024));
                    BigDecimal bigDecimal = new BigDecimal(totalFreeSpace);
                    double v = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    map.put("totalFreeSpace", stringBuffer.append(v).append("MB"));
                }else if(fileSizeWithUnit.contains("G")) {
                    StringBuffer stringBuffer = new StringBuffer();
                    totalFreeSpace = (long) (recordingFilesStorageSpaceMax - FileUtil.getFileSize(totalUsableSpace, FileUtil.SIZE_UNIT_GB));
                    BigDecimal bigDecimal = new BigDecimal(totalFreeSpace);
                    double v = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    map.put("totalFreeSpace", stringBuffer.append(v).append("G"));
                }

            }else {
                map.put("totalFreeSpace", "0G");
            }
        }else {
            if (recordingFilesStorageSpaceMax != null) {
                StringBuffer stringBuffer = new StringBuffer();
                totalFreeSpace = (long) (recordingFilesStorageSpaceMax - FileUtil.getFileSize(totalUsableSpace, FileUtil.SIZE_UNIT_GB));
                BigDecimal bigDecimal = new BigDecimal(totalFreeSpace);
                double v = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                map.put("totalFreeSpace", stringBuffer.append(v).append("G"));
            }else {
                map.put("totalFreeSpace", "0G");
            }
        }

        map.put("fileCount", fileCount);
        return map;
    }

    /**
     * 获取存在回收站的录制文件列表
     * @param recordsSearchVo
     * @return
     */
    @Override
    public PaginationData<BusiConferenceNumberVo> getReclaimRecordingConferences(RecordsSearchVo recordsSearchVo) {

        PaginationData<BusiConferenceNumberVo> busiConferenceNumberVoList = new PaginationData<>();
        PageHelper.startPage(recordsSearchVo.getPageNum(), recordsSearchVo.getPageSize());
        Long deptId = (recordsSearchVo.getDeptId() == null) ? SecurityUtils.getLoginUser().getUser().getDeptId() : recordsSearchVo.getDeptId();
        if (recordsSearchVo.getPageNum() == null || recordsSearchVo.getPageNum() <= 0) {
            recordsSearchVo.setPageNum(1);
        }
        if (recordsSearchVo.getPageSize() == null || recordsSearchVo.getPageSize() > 100) {
            recordsSearchVo.setPageSize(100);
        }
        List<BusiRecords> busiRecordsList = busiRecordsMapper.selectReclaimRecordsList(deptId, recordsSearchVo.getSearchKey());
        PageInfo<?> pageInfo = new PageInfo<>(busiRecordsList);
        Date currentDate = new Date();
        for (BusiRecords busiRecords : busiRecordsList) {
            if (busiRecords.getDeleteTime() == null || busiRecords.getRecordsFileStatus() == 1) {
                BusiConferenceNumberVo vo = new BusiConferenceNumberVo();
                vo.setRecordingTimeOfLate(busiRecords.getCreateTime());
                vo.setName(busiRecords.getTemplateName());
                vo.setConferenceNumber(busiRecords.getConferenceNumber());
                String coSpaceId = busiRecords.getCoSpaceId();
                try {
                    if (coSpaceId.endsWith("-zj") || coSpaceId.endsWith("-plc") || coSpaceId.endsWith("-kdc")) {
                        if (coSpaceId.contains("-")) {
                            vo.setConferenceNumber(Long.valueOf(coSpaceId.substring(0, coSpaceId.indexOf("-"))));
                        } else {
                            vo.setConferenceNumber(Long.valueOf(coSpaceId));
                        }
                    }
                } catch (Exception e) {
                }
                vo.setDeptId(busiRecords.getDeptId());
                vo.setCoSpaceId(busiRecords.getCoSpaceId());
                vo.setDeleteTime(busiRecords.getDeleteTime());
                vo.setRecordsFileStatus(busiRecords.getRecordsFileStatus());
                vo.setId(busiRecords.getId());
                vo.setRecordFileNum(1);
                vo.setRemainDays((int) (30 - ((currentDate.getTime() - busiRecords.getDeleteTime().getTime()) / (1000 * 60 * 60 * 24))));
                busiConferenceNumberVoList.addRecord(vo);
            }
        }
        busiConferenceNumberVoList.setTotal(pageInfo.getTotal());
        busiConferenceNumberVoList.setSize(pageInfo.getSize());
        busiConferenceNumberVoList.setPage(pageInfo.getPageNum());
        return busiConferenceNumberVoList;
    }

    /**
     * 恢复回收站的录制文件列表
     * @param ids
     * @return
     */
    @Override
    public void recoverRecordingConferences(String ids) {
        Long[] idList = null;
        if (StringUtils.hasText(ids)) {
            String[] split = ids.split(",");
            idList = Arrays.stream(split).map(b -> Long.parseLong(b)).toArray(Long[]::new);
        }
        BusiRecordSetting recordSetting = new BusiRecordSetting();
        recordSetting.setStatus(YesOrNo.YES.getValue());
        List<BusiRecordSetting> busiRecordSettings = busiRecordSettingMapper.selectBusiRecordSettingList(recordSetting);
        if (busiRecordSettings == null || busiRecordSettings.isEmpty()) {
            return;
        }
        if (idList != null) {
            for (Long id : idList) {
                BusiRecords records = busiRecordsMapper.selectBusiRecordsById(id);
                busiRecordsMapper.recoverBusiRecords(records);
            }
        }
    }

    /**
     * 删除回收站录制文件
     * @param ids
     */
    @Override
    public void deleteRecoverRecordingFile(String ids) {
        Long[] idList = null;
        if (StringUtils.hasText(ids)) {
            String[] split = ids.split(",");
            idList = Arrays.stream(split).map(b -> Long.parseLong(b)).toArray(Long[]::new);
        }
        BusiRecordSetting recordSetting = new BusiRecordSetting();
        recordSetting.setStatus(YesOrNo.YES.getValue());
        List<BusiRecordSetting> busiRecordSettings = busiRecordSettingMapper.selectBusiRecordSettingList(recordSetting);
        if (busiRecordSettings == null || busiRecordSettings.isEmpty()) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer(busiRecordSettings.get(0).getPath())
                .append("/")
                .append(busiRecordSettings.get(0).getFolder())
                .append(File.separator);
        String path = stringBuffer.toString();
//        File file = new File(path);
        logger.info("deleteRecordingFile文件路径:{}", path);
        if (idList != null) {
            for (long id : idList) {
                BusiRecords busiRecords = busiRecordsMapper.selectBusiRecordsById(id);
                File fileTemp = new File(path + busiRecords.getCoSpaceId() + File.separator + busiRecords.getRealName());
                deleteFile(fileTemp);
                busiRecordsMapper.deleteBusiRecordsById(id);
            }
//            busiRecordsMapper.deleteBusiRecordsByIds(idList);
        }
    }

    /**
     * 读取指定文件夹下的文件数量
     *
     * @param file
     * @return
     */
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
     * 递归删除文件夹
     *
     * @param file
     */
    private void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                deleteFile(f);
            }
        }
        file.delete();
    }

}
