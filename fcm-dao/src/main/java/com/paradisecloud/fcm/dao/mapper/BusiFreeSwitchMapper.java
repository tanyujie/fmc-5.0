package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiFreeSwitch;

/**
 * 服务器资源信息Mapper接口
 * 
 * @author zyz
 * @date 2021-09-02
 */
public interface BusiFreeSwitchMapper 
{
    /**
     * 查询服务器资源信息
     * 
     * @param id 服务器资源信息ID
     * @return 服务器资源信息
     */
    public BusiFreeSwitch selectBusiFreeSwitchById(Long id);

    /**
     * 查询服务器资源信息列表
     * 
     * @param busiFreeSwitch 服务器资源信息
     * @return 服务器资源信息集合
     */
    public List<BusiFreeSwitch> selectBusiFreeSwitchList(BusiFreeSwitch busiFreeSwitch);

    /**
     * 新增服务器资源信息
     * 
     * @param busiFreeSwitch 服务器资源信息
     * @return 结果
     */
    public int insertBusiFreeSwitch(BusiFreeSwitch busiFreeSwitch);

    /**
     * 修改服务器资源信息
     * 
     * @param busiFreeSwitch 服务器资源信息
     * @return 结果
     */
    public int updateBusiFreeSwitch(BusiFreeSwitch busiFreeSwitch);

    /**
     * 删除服务器资源信息
     * 
     * @param id 服务器资源信息ID
     * @return 结果
     */
    public int deleteBusiFreeSwitchById(Long id);

    /**
     * 批量删除服务器资源信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiFreeSwitchByIds(Long[] ids);
}
