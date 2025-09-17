package com.paradisecloud.fcm.web.service.interfaces;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiRecordSetting;
import com.paradisecloud.fcm.dao.model.BusiRecordSettingForm;

/**
 * 录制管理Service接口
 *
 * @author lilinhai
 * @date 2021-04-29
 */
public interface IBusiRecordSettingService {
    /**
     * 查询录制管理
     *
     * @param id 录制管理ID
     * @return 录制管理
     */
    public BusiRecordSetting selectBusiRecordSettingById(Long id);

    /**
     * 查询录制管理列表
     *
     * @param busiRecordSetting 录制管理
     * @return 录制管理集合
     */
    public List<BusiRecordSetting> selectBusiRecordSettingList(BusiRecordSetting busiRecordSetting);

    /**
     * 新增录制管理
     *
     * @param busiRecordSetting 录制管理
     * @return 结果
     */
    public int insertBusiRecordSetting(BusiRecordSettingForm busiRecordSetting);

    /**
     * 修改录制管理
     *
     * @param busiRecordSetting 录制管理
     * @return 结果
     */
    public int updateBusiRecordSetting(BusiRecordSettingForm busiRecordSetting);

    /**
     * 批量删除录制管理
     *
     * @param ids 需要删除的录制管理ID
     * @return 结果
     */
    public int deleteBusiRecordSettingByIds(Long[] ids);

    /**
     * 删除录制管理信息
     *
     * @param id 录制管理ID
     * @return 结果
     */
    public int deleteBusiRecordSettingById(Long id);
}
