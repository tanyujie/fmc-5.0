package com.paradisecloud.fcm.smartroom.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiSmartRoomLot;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomLotVo;

import java.util.List;

/**
 * 智慧办公物联网关Service接口
 * 
 * @author lilinhai
 * @date 2024-03-04
 */
public interface IBusiSmartRoomLotService 
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
    public List<BusiSmartRoomLot> selectBusiSmartRoomLotList(BusiSmartRoomLotVo busiSmartRoomLot);

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
     * 批量删除智慧办公物联网关
     * 
     * @param ids 需要删除的智慧办公物联网关ID
     * @return 结果
     */
    public int deleteBusiSmartRoomLotByIds(Long[] ids);

    /**
     * 删除智慧办公物联网关信息
     * 
     * @param id 智慧办公物联网关ID
     * @return 结果
     */
    public int deleteBusiSmartRoomLotById(Long id);

    /**
     * 查询未绑定的物联网关列表
     *
     * @return
     */
    public List<BusiSmartRoomLot> selectBusiSmartRoomLotListForUnbind();
}
