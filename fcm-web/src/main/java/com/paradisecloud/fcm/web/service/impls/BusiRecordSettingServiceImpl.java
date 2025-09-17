package com.paradisecloud.fcm.web.service.impls;


import java.util.*;

import com.paradisecloud.fcm.dao.model.BusiRecordSettingForm;
import com.paradisecloud.fcm.fme.conference.impls.BusiRecordsServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.dao.mapper.BusiRecordSettingMapper;
import com.paradisecloud.fcm.dao.model.BusiRecordSetting;
import com.paradisecloud.fcm.web.service.interfaces.IBusiRecordSettingService;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * 录制管理Service业务层处理
 *
 * @author lilinhai
 * @date 2021-04-29
 */
@Service
public class BusiRecordSettingServiceImpl implements IBusiRecordSettingService {
    @Resource
    private BusiRecordSettingMapper busiRecordSettingMapper;

    /**
     * 查询录制管理
     *
     * @param id 录制管理ID
     * @return 录制管理
     */
    @Override
    public BusiRecordSetting selectBusiRecordSettingById(Long id) {
        return busiRecordSettingMapper.selectBusiRecordSettingById(id);
    }

    /**
     * 查询录制管理列表
     *
     * @param busiRecordSetting 录制管理
     * @return 录制管理
     */
    @Override
    public List<BusiRecordSetting> selectBusiRecordSettingList(BusiRecordSetting busiRecordSetting) {
        Assert.notNull(busiRecordSetting.getDeptId(), "部门ID不能为空");
        List<BusiRecordSetting> busiRecordSettingList = new ArrayList<>();
        List<BusiRecordSetting> busiRecordSettings = busiRecordSettingMapper.selectBusiRecordSettingList(busiRecordSetting);
        if (busiRecordSettings != null && busiRecordSettings.size() > 0) {

            BusiRecordsServiceImpl busiRecordsService = new BusiRecordsServiceImpl();
            int spaceStatus = busiRecordsService.getRecordSpaceStatus();
            boolean spaceUsable = false;
            if (spaceStatus == 1) {
                spaceUsable = true;
            }

            for (BusiRecordSetting recordSetting : busiRecordSettings) {
                BusiRecordSettingForm busiRecordSettingForm = new BusiRecordSettingForm(recordSetting);
                String host = busiRecordSettingForm.getUrl().replace("https://", "").replace("http://", "").replace(":8899/spaces", "");
                busiRecordSettingForm.setHost(host);

                busiRecordSettingForm.setSpaceUsable(spaceUsable);
                busiRecordSettingList.add(busiRecordSettingForm);
            }
        }
        return busiRecordSettingList;
    }

    /**
     * 新增录制管理
     *
     * @param busiRecordSetting 录制管理
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBusiRecordSetting(BusiRecordSettingForm busiRecordSetting) {
        Assert.notNull(busiRecordSetting.getDeptId(), "部门ID不能为空");
        checkIsStart(busiRecordSetting);
        busiRecordSetting.setCreateTime(new Date());
        if (busiRecordSetting.getPath() == null) {
            busiRecordSetting.setPath("/mnt/nfs");
        }
        if (busiRecordSetting.getFolder() == null) {
            busiRecordSetting.setFolder("spaces");
        }
        if (busiRecordSetting.getMergeName() == null) {
            busiRecordSetting.setMergeName("");
        }
        if (busiRecordSetting.getMergeCoverName() == null) {
            busiRecordSetting.setMergeCoverName("");
        }
        if (busiRecordSetting.getRetentionType() == null) {
            busiRecordSetting.setRetentionType(3);
        }
        String protocol = "https://";
        if (StringUtils.hasText(busiRecordSetting.getUrl())) {
            if (busiRecordSetting.getUrl().startsWith("http")) {
                protocol = "http://";
            }
        }
        if (StringUtils.hasText(busiRecordSetting.getHost())) {
            busiRecordSetting.setUrl(protocol + busiRecordSetting.getHost() + ":8899/spaces");
        }
        return busiRecordSettingMapper.insertBusiRecordSetting(busiRecordSetting);
    }

    /**
     * 修改录制管理
     *
     * @param busiRecordSetting 录制管理
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBusiRecordSetting(BusiRecordSettingForm busiRecordSetting) {
        Assert.notNull(busiRecordSetting.getDeptId(), "部门ID不能为空");
        checkIsStart(busiRecordSetting);
        busiRecordSetting.setUpdateTime(new Date());

        if (busiRecordSetting.getPath() == null) {
            busiRecordSetting.setPath("/mnt/nfs");
        }
        if (busiRecordSetting.getFolder() == null) {
            busiRecordSetting.setFolder("spaces");
        }
        if (busiRecordSetting.getMergeName() == null) {
            busiRecordSetting.setMergeName("");
        }
        if (busiRecordSetting.getMergeCoverName() == null) {
            busiRecordSetting.setMergeCoverName("");
        }
        if (busiRecordSetting.getRetentionType() == null) {
            busiRecordSetting.setRetentionType(3);
        }
        String protocol = "https://";
        if (StringUtils.hasText(busiRecordSetting.getUrl())) {
            if (busiRecordSetting.getUrl().startsWith("http")) {
                protocol = "http://";
            }
        }
        if (StringUtils.hasText(busiRecordSetting.getHost())) {
            busiRecordSetting.setUrl(protocol + busiRecordSetting.getHost() + ":8899/spaces");
        }
        return busiRecordSettingMapper.updateBusiRecordSetting(busiRecordSetting);
    }

    /**
     * 检查是否存在启用状态的配置
     *
     * @param busiRecordSetting
     */
    private void checkIsStart(BusiRecordSetting busiRecordSetting) {
        if (busiRecordSetting.getStatus().equals(YesOrNo.NO.getValue())) {
            return;
        }
        BusiRecordSetting recordSetting = new BusiRecordSetting();
        recordSetting.setStatus(YesOrNo.YES.getValue());
        recordSetting.setDeptId(busiRecordSetting.getDeptId());
        List<BusiRecordSetting> busiRecordSettings = busiRecordSettingMapper.selectBusiRecordSettingList(recordSetting);
        if (Objects.nonNull(busiRecordSettings) && !busiRecordSettings.isEmpty()) {
            if (busiRecordSetting.getId() == null) {
                throw new IllegalArgumentException("只能存在一个启用状态的配置");
            }
            if (busiRecordSetting.getId() != null && !busiRecordSetting.getId().equals(busiRecordSettings.get(0).getId())) {
                throw new IllegalArgumentException("只能存在一个启用状态的配置");
            }

        }
    }

    /**
     * 批量删除录制管理
     *
     * @param ids 需要删除的录制管理ID
     * @return 结果
     */
    @Override
    public int deleteBusiRecordSettingByIds(Long[] ids) {
        return busiRecordSettingMapper.deleteBusiRecordSettingByIds(ids);
    }

    /**
     * 删除录制管理信息
     *
     * @param id 录制管理ID
     * @return 结果
     */
    @Override
    public int deleteBusiRecordSettingById(Long id) {
        return busiRecordSettingMapper.deleteBusiRecordSettingById(id);
    }
}
