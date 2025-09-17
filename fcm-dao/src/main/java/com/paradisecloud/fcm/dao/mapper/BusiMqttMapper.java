package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiMqtt;

/**
 * Mapper接口
 * 
 * @author zyz
 * @date 2021-07-21
 */
public interface BusiMqttMapper 
{
    /**
             * 查询mqtt配置信息
     * 
     * @param id 
     * @return BusiMqtt
     */
    public BusiMqtt selectBusiMqttById(Long id);

    /**
             * 查询mqtt配置信息列表
     * 
     * @param busiMqtt 
     * @return List<BusiMqtt>
     */
    public List<BusiMqtt> selectBusiMqttList(BusiMqtt busiMqtt);

    /**
             * 新增mqtt配置信息
     * 
     * @param busiMqtt 
     * @return int
     */
    public int insertBusiMqtt(BusiMqtt busiMqtt);

    /**
     * 修改mqtt配置信息
     * 
     * @param busiMqtt 
     * @return int
     */
    public int updateBusiMqtt(BusiMqtt busiMqtt);

    /**
             * 删除mqtt配置信息
     * 
     * @param id 
     * @return int
     */
    public int deleteBusiMqttById(Long id);

    /**
             * 批量删除mqtt配置信息
     * 
     * @param ids 
     * @return int
     */
    public int deleteBusiMqttByIds(Long[] ids);
}
