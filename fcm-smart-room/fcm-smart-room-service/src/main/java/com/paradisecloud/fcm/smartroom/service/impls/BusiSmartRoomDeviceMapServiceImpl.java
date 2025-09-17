package com.paradisecloud.fcm.smartroom.service.impls;

import java.util.List;
import java.util.Date;

import com.paradisecloud.fcm.common.enumer.DeviceType;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomDeviceMapMapper;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomDoorplateMapper;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDeviceMap;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDoorplate;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomDeviceMapService;
import com.paradisecloud.fcm.smartroom.task.UpdateDoorplateForMeetingRoomTask;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 会议室设备关联Service业务层处理
 * 
 * @author lilinhai
 * @date 2024-01-26
 */
@Service
public class BusiSmartRoomDeviceMapServiceImpl implements IBusiSmartRoomDeviceMapService
{
    @Resource
    private BusiSmartRoomDeviceMapMapper busiSmartRoomDeviceMapMapper;
    @Resource
    private BusiSmartRoomDoorplateMapper busiSmartRoomDoorplateMapper;
    @Resource
    private TaskService taskService;

    /**
     * 查询会议室设备关联
     * 
     * @param id 会议室设备关联ID
     * @return 会议室设备关联
     */
    @Override
    public BusiSmartRoomDeviceMap selectBusiSmartRoomDeviceMapById(Long id)
    {
        return busiSmartRoomDeviceMapMapper.selectBusiSmartRoomDeviceMapById(id);
    }

    /**
     * 查询会议室设备关联列表
     * 
     * @param busiSmartRoomDeviceMap 会议室设备关联
     * @return 会议室设备关联
     */
    @Override
    public List<BusiSmartRoomDeviceMap> selectBusiSmartRoomDeviceMapList(BusiSmartRoomDeviceMap busiSmartRoomDeviceMap)
    {
        return busiSmartRoomDeviceMapMapper.selectBusiSmartRoomDeviceMapList(busiSmartRoomDeviceMap);
    }

    /**
     * 新增会议室设备关联
     * 
     * @param busiSmartRoomDeviceMap 会议室设备关联
     * @return 结果
     */
    @Override
    public int insertBusiSmartRoomDeviceMap(BusiSmartRoomDeviceMap busiSmartRoomDeviceMap)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        busiSmartRoomDeviceMap.setCreateTime(new Date());
        busiSmartRoomDeviceMap.setCreateBy(loginUser.getUsername());
        int i =  busiSmartRoomDeviceMapMapper.insertBusiSmartRoomDeviceMap(busiSmartRoomDeviceMap);
        if (i > 0) {
            UpdateDoorplateForMeetingRoomTask updateDoorplateForMeetingRoomTask = new UpdateDoorplateForMeetingRoomTask(busiSmartRoomDeviceMap.getRoomId().toString(), 0, busiSmartRoomDeviceMap.getRoomId());
            taskService.addTask(updateDoorplateForMeetingRoomTask);
        }
        return i;
    }

    /**
     * 修改会议室设备关联
     * 
     * @param busiSmartRoomDeviceMap 会议室设备关联
     * @return 结果
     */
    @Override
    public int updateBusiSmartRoomDeviceMap(BusiSmartRoomDeviceMap busiSmartRoomDeviceMap)
    {
//        return busiSmartRoomDeviceMapMapper.updateBusiSmartRoomDeviceMap(busiSmartRoomDeviceMap);
        // 只能添加和删除
        return 0;
    }

    /**
     * 批量删除会议室设备关联
     * 
     * @param ids 需要删除的会议室设备关联ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmartRoomDeviceMapByIds(Long[] ids)
    {
        int rows = 0;
        for (Long id : ids) {
            BusiSmartRoomDeviceMap busiSmartRoomDeviceMap = busiSmartRoomDeviceMapMapper.selectBusiSmartRoomDeviceMapById(id);
            int i = busiSmartRoomDeviceMapMapper.deleteBusiSmartRoomDeviceMapById(id);
            if (i > 0) {
                if (busiSmartRoomDeviceMap != null) {
                    if (busiSmartRoomDeviceMap.getDeviceType() == DeviceType.DOORPLATE.getCode()) {
                        BusiSmartRoomDoorplate busiSmartRoomDoorplate = busiSmartRoomDoorplateMapper.selectBusiSmartRoomDoorplateById(busiSmartRoomDeviceMap.getDeviceId());
                        UpdateDoorplateForMeetingRoomTask updateDoorplateForMeetingRoomTask = new UpdateDoorplateForMeetingRoomTask(busiSmartRoomDoorplate.getSn(), 0, busiSmartRoomDoorplate.getSn());
                        taskService.addTask(updateDoorplateForMeetingRoomTask);
                    }
                }
                rows++;
            }
        }
        return rows;
    }

    /**
     * 删除会议室设备关联信息
     * 
     * @param id 会议室设备关联ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmartRoomDeviceMapById(Long id)
    {
        return busiSmartRoomDeviceMapMapper.deleteBusiSmartRoomDeviceMapById(id);
    }
}
