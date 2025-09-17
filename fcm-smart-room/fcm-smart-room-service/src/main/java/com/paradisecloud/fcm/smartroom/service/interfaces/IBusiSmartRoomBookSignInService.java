package com.paradisecloud.fcm.smartroom.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiSmartRoomBookSignIn;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomBookSignInVo;

import java.util.List;

/**
 * 房间预约签到Service接口
 *
 * @author lilinhai
 * @date 2024-03-22
 */
public interface IBusiSmartRoomBookSignInService
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
    public List<BusiSmartRoomBookSignIn> selectBusiSmartRoomBookSignInList(BusiSmartRoomBookSignInVo busiSmartRoomBookSignIn);

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
     * 批量删除房间预约签到
     *
     * @param ids 需要删除的房间预约签到ID
     * @return 结果
     */
    public int deleteBusiSmartRoomBookSignInByIds(Long[] ids);

    /**
     * 删除房间预约签到信息
     *
     * @param id 房间预约签到ID
     * @return 结果
     */
    public int deleteBusiSmartRoomBookSignInById(Long id);
}
