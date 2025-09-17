package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiLiveSetting;

/**
 * 直播地址配置管理Mapper接口
 *
 * @author lilinhai
 * @date 2021-04-29
 */
public interface BusiLiveSettingMapper {
    /**
     * 查询直播地址配置管理
     *
     * @param id 直播地址配置管理ID
     * @return 直播地址配置管理
     */
    public BusiLiveSetting selectBusiLiveSettingById(Long id);

    /**
     * 查询直播地址配置管理列表
     *
     * @param busiLiveSetting 直播地址配置管理
     * @return 直播地址配置管理集合
     */
    public List<BusiLiveSetting> selectBusiLiveSettingList(BusiLiveSetting busiLiveSetting);

    /**
     * 新增直播地址配置管理
     *
     * @param busiLiveSetting 直播地址配置管理
     * @return 结果
     */
    public int insertBusiLiveSetting(BusiLiveSetting busiLiveSetting);

    /**
     * 修改直播地址配置管理
     *
     * @param busiLiveSetting 直播地址配置管理
     * @return 结果
     */
    public int updateBusiLiveSetting(BusiLiveSetting busiLiveSetting);

    /**
     * 删除直播地址配置管理
     *
     * @param id 直播地址配置管理ID
     * @return 结果
     */
    public int deleteBusiLiveSettingById(Long id);

    /**
     * 批量删除直播地址配置管理
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiLiveSettingByIds(Long[] ids);
}
