package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiSmartRoomThirdOa;

import java.util.List;

/**
 * 智慧办公第三方OAMapper接口
 * 
 * @author lilinhai
 * @date 2024-03-04
 */
public interface BusiSmartRoomThirdOaMapper 
{
    /**
     * 查询智慧办公第三方OA
     * 
     * @param id 智慧办公第三方OAID
     * @return 智慧办公第三方OA
     */
    public BusiSmartRoomThirdOa selectBusiSmartRoomThirdOaById(Long id);

    /**
     * 查询智慧办公第三方OA列表
     * 
     * @param busiSmartRoomThirdOa 智慧办公第三方OA
     * @return 智慧办公第三方OA集合
     */
    public List<BusiSmartRoomThirdOa> selectBusiSmartRoomThirdOaList(BusiSmartRoomThirdOa busiSmartRoomThirdOa);

    /**
     * 新增智慧办公第三方OA
     * 
     * @param busiSmartRoomThirdOa 智慧办公第三方OA
     * @return 结果
     */
    public int insertBusiSmartRoomThirdOa(BusiSmartRoomThirdOa busiSmartRoomThirdOa);

    /**
     * 修改智慧办公第三方OA
     * 
     * @param busiSmartRoomThirdOa 智慧办公第三方OA
     * @return 结果
     */
    public int updateBusiSmartRoomThirdOa(BusiSmartRoomThirdOa busiSmartRoomThirdOa);

    /**
     * 删除智慧办公第三方OA
     * 
     * @param id 智慧办公第三方OAID
     * @return 结果
     */
    public int deleteBusiSmartRoomThirdOaById(Long id);

    /**
     * 批量删除智慧办公第三方OA
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSmartRoomThirdOaByIds(Long[] ids);
}
