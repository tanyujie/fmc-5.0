package com.paradisecloud.fcm.mqtt.interfaces;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiMqttClusterMap;

/**
 * mqtt集群关联接口
 * 
 * @author zyz
 * @date 2021-07-21
 */
public interface IBusiMqttClusterMapService 
{
    /**
             * 查询具体mqtt集群关联
     * 
     * @param id 
     * @return BusiMqttClusterMap
     */
    public BusiMqttClusterMap selectBusiMqttClusterMapById(Long id);

    /**
             * 查询mqtt集群关联
     * 
     * @param busiMqttClusterMap 
     * @return List<BusiMqttClusterMap
     */
    public List<ModelBean> selectBusiMqttClusterMapList(BusiMqttClusterMap busiMqttClusterMap, HttpServletRequest request, HttpServletResponse response);

    /**
             * 新增mqtt集群关联
     * 
     * @param busiMqttClusterMap
     * @return int
     */
    public int insertBusiMqttClusterMap(BusiMqttClusterMap busiMqttClusterMap);

    /**
             * 修改mqtt集群关联
     * 
     * @param busiMqttClusterMap
     * @return int
     */
    public int updateBusiMqttClusterMap(BusiMqttClusterMap busiMqttClusterMap);

    /**
             * 批量删除
     * 
     * @param ids 
     * @return int
     */
    public int deleteBusiMqttClusterMapByIds(Long[] ids);

    /**
             * 删除mqtt集群关联
     * 
     * @param id 
     * @return int
     */
    public int deleteBusiMqttClusterMapById(Long id);
}
