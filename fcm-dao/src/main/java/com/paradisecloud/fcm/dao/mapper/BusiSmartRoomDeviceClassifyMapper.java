package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiSmartRoomDeviceClassify;

import java.util.List;

/**
 * 智慧办公房间设备分类Mapper接口
 *
 * @author lilinhai
 * @date 2024-02-19
 */
public interface BusiSmartRoomDeviceClassifyMapper
{
    /**
     * 查询智慧办公房间设备分类
     *
     * @param id 智慧办公房间设备分类ID
     * @return 智慧办公房间设备分类
     */
    public BusiSmartRoomDeviceClassify selectBusiSmartRoomDeviceClassifyById(Long id);

    /**
     * 查询智慧办公房间设备分类列表
     *
     * @param busiSmartRoomDeviceClassify 智慧办公房间设备分类
     * @return 智慧办公房间设备分类集合
     */
    public List<BusiSmartRoomDeviceClassify> selectBusiSmartRoomDeviceClassifyList(BusiSmartRoomDeviceClassify busiSmartRoomDeviceClassify);

    /**
     * 新增智慧办公房间设备分类
     *
     * @param busiSmartRoomDeviceClassify 智慧办公房间设备分类
     * @return 结果
     */
    public int insertBusiSmartRoomDeviceClassify(BusiSmartRoomDeviceClassify busiSmartRoomDeviceClassify);

    /**
     * 修改智慧办公房间设备分类
     *
     * @param busiSmartRoomDeviceClassify 智慧办公房间设备分类
     * @return 结果
     */
    public int updateBusiSmartRoomDeviceClassify(BusiSmartRoomDeviceClassify busiSmartRoomDeviceClassify);

    /**
     * 删除智慧办公房间设备分类
     *
     * @param id 智慧办公房间设备分类ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDeviceClassifyById(Long id);

    /**
     * 批量删除智慧办公房间设备分类
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDeviceClassifyByIds(Long[] ids);
}