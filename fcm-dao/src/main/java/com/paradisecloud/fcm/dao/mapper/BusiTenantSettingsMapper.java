package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiTenantSettings;

/**
 * 租户设置Mapper接口
 * 
 * @author lilinhai
 * @date 2021-08-04
 */
public interface BusiTenantSettingsMapper 
{
    /**
     * 查询租户设置
     * 
     * @param id 租户设置ID
     * @return 租户设置
     */
    public BusiTenantSettings selectBusiTenantSettingsById(Long id);

    /**
     * 查询租户设置列表
     * 
     * @param busiTenantSettings 租户设置
     * @return 租户设置集合
     */
    public List<BusiTenantSettings> selectBusiTenantSettingsList(BusiTenantSettings busiTenantSettings);

    /**
     * 新增租户设置
     * 
     * @param busiTenantSettings 租户设置
     * @return 结果
     */
    public int insertBusiTenantSettings(BusiTenantSettings busiTenantSettings);

    /**
     * 修改租户设置
     * 
     * @param busiTenantSettings 租户设置
     * @return 结果
     */
    public int updateBusiTenantSettings(BusiTenantSettings busiTenantSettings);

    /**
     * 删除租户设置
     * 
     * @param id 租户设置ID
     * @return 结果
     */
    public int deleteBusiTenantSettingsById(Long id);

    /**
     * 批量删除租户设置
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiTenantSettingsByIds(Long[] ids);
}
