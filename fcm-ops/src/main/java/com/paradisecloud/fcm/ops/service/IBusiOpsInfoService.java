package com.paradisecloud.fcm.ops.service;


import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.dao.model.BusiOpsInfo;

import java.util.List;

/**
 * ops配置信息Service接口
 * 
 * @author lilinhai
 * @date 2024-05-27
 */
public interface IBusiOpsInfoService 
{
    /**
     * 查询ops配置信息
     * 
     * @param id ops配置信息ID
     * @return ops配置信息
     */
    public BusiOpsInfo selectBusiOpsInfoById(Integer id);

    /**
     * 查询ops配置信息列表
     * 
     * @param busiOpsInfo ops配置信息
     * @return ops配置信息集合
     */
    public List<BusiOpsInfo> selectBusiOpsInfoList(BusiOpsInfo busiOpsInfo);

    /**
     * 新增ops配置信息
     * 
     * @param busiOpsInfo ops配置信息
     * @return 结果
     */
    public int insertBusiOpsInfo(BusiOpsInfo busiOpsInfo);

    /**
     * 修改ops配置信息
     * 
     * @param busiOpsInfo ops配置信息
     * @return 结果
     */
    public int updateBusiOpsInfo(BusiOpsInfo busiOpsInfo);

    /**
     * 批量删除ops配置信息
     * 
     * @param ids 需要删除的ops配置信息ID
     * @return 结果
     */
    public int deleteBusiOpsInfoByIds(Integer[] ids);

    /**
     * 删除ops配置信息信息
     * 
     * @param id ops配置信息ID
     * @return 结果
     */
    public int deleteBusiOpsInfoById(Integer id);

    int initBusiOpsInfo(BusiOpsInfo busiOpsInfo);

    int restart();

    int shutdown();

    String thermal_zone();

    Object getNetworkInfo();

    Object getNetworkInfoAddress(String ipAddress);

    Object localRecorder();

    void endlocalRecorder(String conferenceId);

    Object setPhone(String phoneNumber);

    String getPhone();

    Object pingIp(String ip);

    int restore() throws Exception;

}
