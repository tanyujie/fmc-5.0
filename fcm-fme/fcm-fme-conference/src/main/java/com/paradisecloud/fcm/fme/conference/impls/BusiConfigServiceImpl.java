package com.paradisecloud.fcm.fme.conference.impls;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.Map;

import com.paradisecloud.fcm.common.cache.CommonConfigCache;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.dao.mapper.BusiConfigMapper;
import com.paradisecloud.fcm.dao.model.BusiConfig;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConfigService;
import com.paradisecloud.system.utils.SecurityUtils;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author lilinhai
 * @date 2022-09-10
 */
@Service
public class BusiConfigServiceImpl implements IBusiConfigService
{
    @Autowired
    private BusiConfigMapper busiConfigMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param configId 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public BusiConfig selectBusiConfigById(Integer configId)
    {
        return busiConfigMapper.selectBusiConfigById(configId);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param busiConfig 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<BusiConfig> selectBusiConfigList(BusiConfig busiConfig)
    {
        return busiConfigMapper.selectBusiConfigList(busiConfig);
    }

    /**
     * 获取录制空间最大容量
     */
    @Override
    public Map<String, Object> getRecordingFilesStorageSpaceMax() {

        Map<String, Object> map = new HashMap<>();
        String str = null;
        BusiConfig busiConfigSelect = new BusiConfig();
        busiConfigSelect.setConfigKey("Recording_Files_Storage_Space_Max");
        List<BusiConfig> busiConfigList = busiConfigMapper.selectBusiConfigList(busiConfigSelect);

        if (busiConfigList != null && busiConfigList.size() > 0) {
            if (busiConfigList.get(0).getConfigValue() != null) {
                Double recordingFilesStorageSpaceMax = Double.valueOf(busiConfigList.get(0).getConfigValue());
                map.put("recordingFilesStorageSpaceMax", recordingFilesStorageSpaceMax);
            }
        } else {
            map.put("recordingFilesStorageSpaceMax", 0);
        }

        return map;
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param busiConfig 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertBusiConfig(BusiConfig busiConfig)
    {
        busiConfig.setCreateTime(new Date());
        return busiConfigMapper.insertBusiConfig(busiConfig);
    }

    /**
     * 设置录制空间最大容量
     * @param recordingFilesStorageSpaceMax
     * @return
     */
    @Override
    public int updateRecordingFilesStorageSpaceMax(Double recordingFilesStorageSpaceMax) {

        BusiConfig busiConfig = new BusiConfig();
        busiConfig.setCreateTime(new Date());
        busiConfig.setConfigName("录音文件存储空间最大");
        busiConfig.setConfigKey("Recording_Files_Storage_Space_Max");

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

        if (recordingFilesStorageSpaceMax.compareTo(usableSpace) <= 0) {
            if (recordingFilesStorageSpaceMax.compareTo(recordingFilesStorageSpaceMaxLimit) <= 0) {
                busiConfig.setConfigValue(recordingFilesStorageSpaceMax.toString());
            } else {
                Assert.isTrue(false, "可设置的最大容量为:" + recordingFilesStorageSpaceMaxLimit + "G");
            }
        } else {
            Assert.isTrue(false, "当前磁盘空间不足！");
        }

        busiConfig.setCreateBy(SecurityUtils.getUsername());

        int i = 0;
        BusiConfig busiConfigSelect = new BusiConfig();
        busiConfigSelect.setConfigKey("Recording_Files_Storage_Space_Max");
        List<BusiConfig> busiConfigList = busiConfigMapper.selectBusiConfigList(busiConfigSelect);
        if (busiConfigList != null && busiConfigList.size() > 0) {
            busiConfig.setConfigId(busiConfigList.get(0).getConfigId());
            i = updateBusiConfig(busiConfig);
        } else {
            i = insertBusiConfig(busiConfig);
        }

        return i;
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
     * 修改【请填写功能名称】
     *
     * @param busiConfig 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateBusiConfig(BusiConfig busiConfig)
    {
        busiConfig.setUpdateTime(new Date());
        return busiConfigMapper.updateBusiConfig(busiConfig);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param configIds 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiConfigByIds(Integer[] configIds)
    {
        return busiConfigMapper.deleteBusiConfigByIds(configIds);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param configId 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiConfigById(Integer configId)
    {
        return busiConfigMapper.deleteBusiConfigById(configId);
    }
}