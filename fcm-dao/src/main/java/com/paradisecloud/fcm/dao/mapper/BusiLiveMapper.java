package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiLive;

import java.util.List;

/**
 * 直播服务器信息Mapper接口
 * 
 * @author lilinhai
 * @date 2022-10-26
 */
public interface BusiLiveMapper 
{
    /**
     * 查询直播服务器信息
     * 
     * @param id 直播服务器信息ID
     * @return 直播服务器信息
     */
    public BusiLive selectBusiLiveById(Long id);

    /**
     * 查询直播服务器信息列表
     * 
     * @param busiLive 直播服务器信息
     * @return 直播服务器信息集合
     */
    public List<BusiLive> selectBusiLiveList(BusiLive busiLive);

    /**
     * 新增直播服务器信息
     * 
     * @param busiLive 直播服务器信息
     * @return 结果
     */
    public int insertBusiLive(BusiLive busiLive);

    /**
     * 修改直播服务器信息
     * 
     * @param busiLive 直播服务器信息
     * @return 结果
     */
    public int updateBusiLive(BusiLive busiLive);

    /**
     * 删除直播服务器信息
     * 
     * @param id 直播服务器信息ID
     * @return 结果
     */
    public int deleteBusiLiveById(Long id);

    /**
     * 批量删除直播服务器信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiLiveByIds(Long[] ids);
}
