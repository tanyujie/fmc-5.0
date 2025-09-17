package com.paradisecloud.fcm.smartroom.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiSmartRoomDeviceClassify;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomDeviceClassifyVo;

import java.util.List;

/**
 * 会议室设备分类Service接口
 * 
 * @author lilinhai
 * @date 2024-02-02
 */
public interface IBusiSmartRoomDeviceClassifyService
{
    /**
     * 查询会议室设备分类
     * 
     * @param id 会议室设备分类ID
     * @return 会议室设备分类
     */
    public BusiSmartRoomDeviceClassify selectBusiSmartRoomDeviceClassifyById(Long id);

    /**
     * 查询会议室设备分类列表
     * 
     * @param busiSmartRoomDeviceClassify 会议室设备分类
     * @return 会议室设备分类集合
     */
    public List<BusiSmartRoomDeviceClassify> selectBusiSmartRoomDeviceClassifyList(BusiSmartRoomDeviceClassifyVo busiSmartRoomDeviceClassify);

    /**
     * 新增会议室设备分类
     * 
     * @param busiSmartRoomDeviceClassify 会议室设备分类
     * @return 结果
     */
    public int insertBusiSmartRoomDeviceClassify(BusiSmartRoomDeviceClassify busiSmartRoomDeviceClassify);

    /**
     * 修改会议室设备分类
     * 
     * @param busiSmartRoomDeviceClassify 会议室设备分类
     * @return 结果
     */
    public int updateBusiSmartRoomDeviceClassify(BusiSmartRoomDeviceClassify busiSmartRoomDeviceClassify);

    /**
     * 批量删除会议室设备分类
     * 
     * @param ids 需要删除的会议室设备分类ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDeviceClassifyByIds(Long[] ids);

    /**
     * 删除会议室设备分类信息
     * 
     * @param id 会议室设备分类ID
     * @return 结果
     */
    public int deleteBusiSmartRoomDeviceClassifyById(Long id);
}
