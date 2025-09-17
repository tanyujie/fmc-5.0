package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiSmartRoomBookSignIn;
import java.util.List;

/**
 * 房间预约签到Mapper接口
 *
 * @author lilinhai
 * @date 2024-03-22
 */
public interface BusiSmartRoomBookSignInMapper
{
    /**
     * 查询房间预约签到
     *
     * @param id 房间预约签到ID
     * @return 房间预约签到
     */
    public BusiSmartRoomBookSignIn selectBusiSmartRoomBookSignInById(Long id);

    /**
     * 查询房间预约签到列表
     *
     * @param busiSmartRoomBookSignIn 房间预约签到
     * @return 房间预约签到集合
     */
    public List<BusiSmartRoomBookSignIn> selectBusiSmartRoomBookSignInList(BusiSmartRoomBookSignIn busiSmartRoomBookSignIn);

    /**
     * 新增房间预约签到
     *
     * @param busiSmartRoomBookSignIn 房间预约签到
     * @return 结果
     */
    public int insertBusiSmartRoomBookSignIn(BusiSmartRoomBookSignIn busiSmartRoomBookSignIn);

    /**
     * 修改房间预约签到
     *
     * @param busiSmartRoomBookSignIn 房间预约签到
     * @return 结果
     */
    public int updateBusiSmartRoomBookSignIn(BusiSmartRoomBookSignIn busiSmartRoomBookSignIn);

    /**
     * 删除房间预约签到
     *
     * @param id 房间预约签到ID
     * @return 结果
     */
    public int deleteBusiSmartRoomBookSignInById(Long id);

    /**
     * 批量删除房间预约签到
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSmartRoomBookSignInByIds(Long[] ids);
}
