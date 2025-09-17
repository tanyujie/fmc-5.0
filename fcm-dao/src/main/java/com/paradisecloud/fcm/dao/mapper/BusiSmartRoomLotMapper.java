package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiSmartRoomLot;

import java.util.List;

/**
 * 智慧办公物联网关Mapper接口
 * 
 * @author lilinhai
 * @date 2024-03-04
 */
public interface BusiSmartRoomLotMapper 
{
    /**
     * 查询智慧办公物联网关
     * 
     * @param id 智慧办公物联网关ID
     * @return 智慧办公物联网关
     */
    public BusiSmartRoomLot selectBusiSmartRoomLotById(Long id);

    /**
     * 查询智慧办公物联网关列表
     * 
     * @param busiSmartRoomLot 智慧办公物联网关
     * @return 智慧办公物联网关集合
     */
    public List<BusiSmartRoomLot> selectBusiSmartRoomLotList(BusiSmartRoomLot busiSmartRoomLot);

    /**
     * 新增智慧办公物联网关
     * 
     * @param busiSmartRoomLot 智慧办公物联网关
     * @return 结果
     */
    public int insertBusiSmartRoomLot(BusiSmartRoomLot busiSmartRoomLot);

    /**
     * 修改智慧办公物联网关
     * 
     * @param busiSmartRoomLot 智慧办公物联网关
     * @return 结果
     */
    public int updateBusiSmartRoomLot(BusiSmartRoomLot busiSmartRoomLot);

    /**
     * 删除智慧办公物联网关
     * 
     * @param id 智慧办公物联网关ID
     * @return 结果
     */
    public int deleteBusiSmartRoomLotById(Long id);

    /**
     * 批量删除智慧办公物联网关
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSmartRoomLotByIds(Long[] ids);

    /**
     * 查询未绑定的物联网关列表
     *
     * @return
     */
    public List<BusiSmartRoomLot> selectBusiSmartRoomLotListForUnbind();
}
