package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiRecordSetting;
import org.apache.ibatis.annotations.Mapper;

/**
 * 录制管理Mapper接口
 *
 * @author lilinhai
 * @date 2021-04-29
 */
public interface BusiRecordSettingMapper {
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
    public int insertBusiRecordSetting(BusiRecordSetting busiRecordSetting);

    /**
     * 修改录制管理
     *
     * @param busiRecordSetting 录制管理
     * @return 结果
     */
    public int updateBusiRecordSetting(BusiRecordSetting busiRecordSetting);

    /**
     * 删除录制管理
     *
     * @param id 录制管理ID
     * @return 结果
     */
    public int deleteBusiRecordSettingById(Long id);

    /**
     * 批量删除录制管理
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiRecordSettingByIds(Long[] ids);
}
