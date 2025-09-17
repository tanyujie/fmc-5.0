package com.paradisecloud.fcm.smartroom.service.impls;

import java.util.*;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.constant.DeptConstant;
import com.paradisecloud.fcm.common.enumer.DeviceType;
import com.paradisecloud.fcm.common.enumer.RoomType;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomBookMapper;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomDeviceVo;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomVo;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomCache;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomDeviceCache;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomDoorplateCache;
import com.paradisecloud.fcm.smartroom.model.MeetingRoomInfo;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomDeviceMapService;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomDeviceService;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomService;
import com.paradisecloud.fcm.smartroom.task.UpdateDoorplateForMeetingRoomTask;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.service.ISysUserService;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import com.sinhy.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 会议室Service业务层处理
 * 
 * @author lilinhai
 * @date 2024-01-26
 */
@Service
public class BusiSmartRoomServiceImpl implements IBusiSmartRoomService
{
    @Resource
    private BusiSmartRoomMapper busiSmartRoomMapper;
    @Resource
    private BusiSmartRoomDeptMapper busiSmartRoomDeptMapper;
    @Resource
    private IBusiSmartRoomDeviceMapService busiSmartRoomDeviceMapService;
    @Resource
    private TaskService taskService;
    @Resource
    private BusiSmartRoomBookMapper busiSmartRoomBookMapper;
    @Resource
    private ISysUserService sysUserService;
    @Resource
    private IBusiSmartRoomDeviceService busiSmartRoomDeviceService;

    /**
     * 查询会议室
     * 
     * @param id 会议室ID
     * @return 会议室
     */
    @Override
    public BusiSmartRoom selectBusiSmartRoomById(Long id)
    {
        BusiSmartRoom busiSmartRoom = busiSmartRoomMapper.selectBusiSmartRoomById(id);
        if (busiSmartRoom != null) {
            Long roomId = busiSmartRoom.getId();
            Map<String, Object> params = new HashMap<>();
            Long boundDoorplateId = SmartRoomCache.getInstance().getBoundDoorplateId(roomId);
            if (boundDoorplateId != null) {
                BusiSmartRoomDoorplate busiSmartRoomDoorplate = SmartRoomDoorplateCache.getInstance().get(boundDoorplateId);
                if (busiSmartRoomDoorplate != null) {
                    params.put("doorplateId", busiSmartRoomDoorplate.getId());
                    params.put("doorplateName", busiSmartRoomDoorplate.getName());
                }
            }
            List<Long> deptIds = new ArrayList<>();
            Set<Long> boundDeptIds = SmartRoomCache.getInstance().getBoundDeptIds(busiSmartRoom);
            if (boundDeptIds != null) {
                for (Long deptId : boundDeptIds) {
                    SysDept sysDept = SysDeptCache.getInstance().get(deptId);
                    if (sysDept != null) {
                        deptIds.add(sysDept.getDeptId());
                    }
                }
            }
            params.put("deptIds", deptIds);
            MeetingRoomInfo meetingRoomInfo = SmartRoomCache.getInstance().getMeetingRoomInfo(busiSmartRoom.getId());
            if (meetingRoomInfo != null) {
                params.put("status", meetingRoomInfo.getStatus());
            }
            busiSmartRoom.setParams(params);
        }
        return busiSmartRoom;
    }

    /**
     * 查询会议室列表
     * 
     * @param busiSmartRoomVo 会议室
     * @return 会议室
     */
    @Override
    public List<BusiSmartRoom> selectBusiSmartRoomList(BusiSmartRoomVo busiSmartRoomVo)
    {
        busiSmartRoomVo.setRoomName(busiSmartRoomVo.getSearchKey());
        List<BusiSmartRoom> busiSmartRoomList = busiSmartRoomMapper.selectBusiSmartRoomList(busiSmartRoomVo);
        if (busiSmartRoomList != null && busiSmartRoomList.size() > 0) {
            for (BusiSmartRoom smartRoom : busiSmartRoomList) {
                Long roomId = smartRoom.getId();
                Map<String, Object> params = new HashMap<>();
                Long boundDoorplateId = SmartRoomCache.getInstance().getBoundDoorplateId(roomId);
                if (boundDoorplateId != null) {
                    BusiSmartRoomDoorplate busiSmartRoomDoorplate = SmartRoomDoorplateCache.getInstance().get(boundDoorplateId);
                    if (busiSmartRoomDoorplate != null) {
                        params.put("doorplateId", busiSmartRoomDoorplate.getId());
                        params.put("doorplateName", busiSmartRoomDoorplate.getName());
                    }
                }
                List<Long> deptIds = new ArrayList<>();
                Set<Long> boundDeptIds = SmartRoomCache.getInstance().getBoundDeptIds(smartRoom);
                if (boundDeptIds != null) {
                    for (Long deptId : boundDeptIds) {
                        SysDept sysDept = SysDeptCache.getInstance().get(deptId);
                        if (sysDept != null) {
                            deptIds.add(sysDept.getDeptId());
                        }
                    }
                }
                params.put("deptIds", deptIds);
                MeetingRoomInfo meetingRoomInfo = SmartRoomCache.getInstance().getMeetingRoomInfo(smartRoom.getId());
                if (meetingRoomInfo != null) {
                    params.put("status", meetingRoomInfo.getStatus());
                }
                smartRoom.setParams(params);
            }
        }
        return busiSmartRoomList;
    }

    /**
     * 新增会议室
     * 
     * @param busiSmartRoom 会议室
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertBusiSmartRoom(BusiSmartRoom busiSmartRoom)
    {
        busiSmartRoom.setCreateTime(new Date());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            busiSmartRoom.setCreateBy(loginUser.getUsername());
        }
        int row = 0;
        try {
            SysUser sysUser = new SysUser();
            sysUser.setUserName("smartRoom_" + System.currentTimeMillis());
            sysUser.setNickName(busiSmartRoom.getRoomName());
            sysUser.setPassword(SecurityUtils.encryptPassword("tty@2021"));
            sysUser.setDeptId(DeptConstant.SMART_ROOM_DEPT_ID);
            int i = sysUserService.insertUser(sysUser);
            if (i > 0) {
                busiSmartRoom.setUserId(sysUser.getUserId());
                row = busiSmartRoomMapper.insertBusiSmartRoom(busiSmartRoom);
            }
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                throw new SystemException("同一大楼房间名重复！");
            }
        }
        if (row > 0) {
            BusiSmartRoom busiSmartRoomExist = busiSmartRoomMapper.selectBusiSmartRoomById(busiSmartRoom.getId());
            SmartRoomCache.getInstance().add(busiSmartRoomExist);
            Map<String, Object> params = busiSmartRoom.getParams();
            if (params.containsKey("deptIds")) {
                Object deptIdsObj = params.get("deptIds");
                if (deptIdsObj != null) {
                    List deptIds = (List) deptIdsObj;
                    for (Object deptIdObj : deptIds) {
                        Long deptId = Long.valueOf(deptIdObj.toString());
                        SysDept sysDept = SysDeptCache.getInstance().get(deptId);
                        if (sysDept != null) {
                            BusiSmartRoomDept busiSmartRoomDeptNew = new BusiSmartRoomDept();
                            busiSmartRoomDeptNew.setRoomId(busiSmartRoom.getId());
                            busiSmartRoomDeptNew.setDeptId(deptId);
                            busiSmartRoomDeptNew.setCreateTime(busiSmartRoom.getCreateTime());
                            busiSmartRoomDeptNew.setCreateBy(busiSmartRoom.getCreateBy());
                            int i = busiSmartRoomDeptMapper.insertBusiSmartRoomDept(busiSmartRoomDeptNew);
                            if (i > 0) {
                                SmartRoomCache.getInstance().bindDept(busiSmartRoomDeptNew);
                            }
                        }
                    }
                }
            }

            if (params != null && params.containsKey("doorplateId")) {
                Object doorplateIdObj = params.get("doorplateId");
                Long doorplateId = null;
                if (doorplateIdObj != null) {
                    doorplateId = Long.valueOf(doorplateIdObj.toString());
                }
                BusiSmartRoomDoorplate busiSmartRoomDoorplate = SmartRoomDoorplateCache.getInstance().get(doorplateId);
                if (busiSmartRoomDoorplate != null) {
                    BusiSmartRoomDeviceMap busiSmartRoomDeviceMap = new BusiSmartRoomDeviceMap();
                    busiSmartRoomDeviceMap.setDeviceId(doorplateId);
                    busiSmartRoomDeviceMap.setDeviceType(DeviceType.DOORPLATE.getCode());
                    busiSmartRoomDeviceMap.setRoomId(busiSmartRoom.getId());
                    List<BusiSmartRoomDeviceMap> busiSmartRoomDeviceMapList = busiSmartRoomDeviceMapService.selectBusiSmartRoomDeviceMapList(busiSmartRoomDeviceMap);
                    if (busiSmartRoomDeviceMapList != null && busiSmartRoomDeviceMapList.size() > 0) {
                        throw new SystemException("当前房间已绑定电子门牌，不能重复绑定！");
                    } else {
                        int i = busiSmartRoomDeviceMapService.insertBusiSmartRoomDeviceMap(busiSmartRoomDeviceMap);
                        if (i > 0) {
                            SmartRoomCache.getInstance().bindDoorplate(busiSmartRoomDeviceMap);
                        }
                    }
                }
            }
        }
        return row;
    }

    /**
     * 修改会议室
     * 
     * @param busiSmartRoom 会议室
     * @return 结果
     */
    @Override
    public int updateBusiSmartRoom(BusiSmartRoom busiSmartRoom)
    {
        busiSmartRoom.setUpdateTime(new Date());
        BusiSmartRoom busiSmartRoomTemp = busiSmartRoomMapper.selectBusiSmartRoomById(busiSmartRoom.getId());
        if (busiSmartRoomTemp != null) {
            if (busiSmartRoomTemp.getUserId() == null) {
                SysUser sysUser = new SysUser();
                sysUser.setUserName("smartRoom_" + System.currentTimeMillis());
                sysUser.setNickName(busiSmartRoom.getRoomName());
                sysUser.setPassword(SecurityUtils.encryptPassword("tty@2021"));
                sysUser.setDeptId(DeptConstant.SMART_ROOM_DEPT_ID);
                int i = sysUserService.insertUser(sysUser);
                if (i > 0) {
                    busiSmartRoom.setUserId(sysUser.getUserId());
                }
            }
        }
        int row = 0;
        try {
            row = busiSmartRoomMapper.updateBusiSmartRoom(busiSmartRoom);
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                throw new SystemException("同一大楼房间名重复！");
            }
        }
        if (row > 0) {
            Map<String, Object> params = busiSmartRoom.getParams();
            String roomName = busiSmartRoom.getRoomName();
            busiSmartRoom = busiSmartRoomMapper.selectBusiSmartRoomById(busiSmartRoom.getId());
            SmartRoomCache.getInstance().add(busiSmartRoom);
            SmartRoomCache.getInstance().unBindDept(busiSmartRoom);
            if (!roomName.equals(busiSmartRoomTemp.getRoomName())) {
                updateMeetingRoomCache(busiSmartRoom.getId());
            }

            BusiSmartRoomDept busiSmartRoomDept = new BusiSmartRoomDept();
            busiSmartRoomDept.setRoomId(busiSmartRoom.getId());
            List<BusiSmartRoomDept> busiSmartRoomDeptList = busiSmartRoomDeptMapper.selectBusiSmartRoomDeptList(busiSmartRoomDept);
            for (BusiSmartRoomDept smartRoomDept : busiSmartRoomDeptList) {
                int deleteI = busiSmartRoomDeptMapper.deleteBusiSmartRoomDeptById(smartRoomDept.getId());
                if (deleteI > 0) {
                    SmartRoomCache.getInstance().unBindDept(smartRoomDept);
                }
            }
            if (params.containsKey("deptIds")) {
                Object deptIdsObj = params.get("deptIds");
                if (deptIdsObj != null) {
                    List deptIds = (List) deptIdsObj;
                    if (deptIds != null && deptIds.size() > 0) {
                        for (Object deptIdObj : deptIds) {
                            Long deptId = Long.valueOf(deptIdObj.toString());
                            SysDept sysDept = SysDeptCache.getInstance().get(deptId);
                            if (sysDept != null) {
                                BusiSmartRoomDept busiSmartRoomDeptNew = new BusiSmartRoomDept();
                                busiSmartRoomDeptNew.setRoomId(busiSmartRoom.getId());
                                busiSmartRoomDeptNew.setDeptId(deptId);
                                busiSmartRoomDeptNew.setCreateTime(busiSmartRoom.getCreateTime());
                                busiSmartRoomDeptNew.setCreateBy(busiSmartRoom.getCreateBy());
                                int i = busiSmartRoomDeptMapper.insertBusiSmartRoomDept(busiSmartRoomDeptNew);
                                if (i > 0) {
                                    SmartRoomCache.getInstance().bindDept(busiSmartRoomDeptNew);
                                }
                            }
                        }
                    }
                }
            }
            String sn = null;
            Long boundDoorplateId = SmartRoomCache.getInstance().getBoundDoorplateId(busiSmartRoom.getId());
            Long doorplateId = null;
            if (params != null && params.containsKey("doorplateId")) {
                Object doorplateIdObj = params.get("doorplateId");
                if (doorplateIdObj != null) {
                    doorplateId = Long.valueOf(doorplateIdObj.toString());
                    if (boundDoorplateId != null) {
                        BusiSmartRoomDoorplate busiSmartRoomDoorplate = SmartRoomDoorplateCache.getInstance().get(boundDoorplateId);
                        if (busiSmartRoomDoorplate != null) {
                            sn = busiSmartRoomDoorplate.getSn();
                        }
                    }
                }
            }
            if (boundDoorplateId != null) {
                if (doorplateId == null || doorplateId.longValue() != boundDoorplateId) {
                    BusiSmartRoomDeviceMap busiSmartRoomDeviceMapCon = new BusiSmartRoomDeviceMap();
                    busiSmartRoomDeviceMapCon.setRoomId(busiSmartRoom.getId());
                    busiSmartRoomDeviceMapCon.setDeviceId(boundDoorplateId);
                    busiSmartRoomDeviceMapCon.setDeviceType(DeviceType.DOORPLATE.getCode());
                    List<BusiSmartRoomDeviceMap> busiSmartRoomDeviceMapList = busiSmartRoomDeviceMapService.selectBusiSmartRoomDeviceMapList(busiSmartRoomDeviceMapCon);
                    if (busiSmartRoomDeviceMapList != null && busiSmartRoomDeviceMapList.size() > 0) {
                        BusiSmartRoomDeviceMap busiSmartRoomDeviceMap = busiSmartRoomDeviceMapList.get(0);
                        int deleteI = busiSmartRoomDeviceMapService.deleteBusiSmartRoomDeviceMapById(busiSmartRoomDeviceMap.getId());
                        if (deleteI > 0) {
                            SmartRoomCache.getInstance().unBindDoorplate(busiSmartRoomDeviceMap);
                            BusiSmartRoomDoorplate busiSmartRoomDoorplateBound = SmartRoomDoorplateCache.getInstance().get(boundDoorplateId);
                            sn = busiSmartRoomDoorplateBound.getSn();
                        }
                    }
                }
            }
            if (doorplateId != null) {
                if (boundDoorplateId == null || doorplateId.longValue() != boundDoorplateId) {
                    BusiSmartRoomDoorplate busiSmartRoomDoorplate = SmartRoomDoorplateCache.getInstance().get(doorplateId);
                    if (busiSmartRoomDoorplate != null) {
                        BusiSmartRoomDeviceMap busiSmartRoomDeviceMap = new BusiSmartRoomDeviceMap();
                        busiSmartRoomDeviceMap.setRoomId(busiSmartRoom.getId());
                        busiSmartRoomDeviceMap.setDeviceId(doorplateId);
                        busiSmartRoomDeviceMap.setDeviceType(DeviceType.DOORPLATE.getCode());
                        int i = busiSmartRoomDeviceMapService.insertBusiSmartRoomDeviceMap(busiSmartRoomDeviceMap);
                        if (i > 0) {
                            SmartRoomCache.getInstance().bindDoorplate(busiSmartRoomDeviceMap);
                            sn = busiSmartRoomDoorplate.getSn();
                        }
                    }
                }
            }
            // 推送消息给电子门牌
            UpdateDoorplateForMeetingRoomTask updateDoorplateForMeetingRoomTask = new UpdateDoorplateForMeetingRoomTask(sn, 0, sn);
            taskService.addTask(updateDoorplateForMeetingRoomTask);
        }
        return row;
    }

    /**
     * 批量删除会议室
     * 
     * @param ids 需要删除的会议室ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmartRoomByIds(Long[] ids)
    {
        int rows = 0;
        for (Long id : ids) {
            int i = deleteBusiSmartRoomById(id);
            rows += i;
        }
        return rows;
    }

    /**
     * 删除会议室信息
     * 
     * @param id 会议室ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmartRoomById(Long id)
    {
        int row = 0;
        BusiSmartRoom busiSmartRoom = busiSmartRoomMapper.selectBusiSmartRoomById(id);
        if (busiSmartRoom != null) {
            row = busiSmartRoomMapper.deleteBusiSmartRoomById(id);
            if (row > 0) {
                if (busiSmartRoom.getUserId() != null) {
                    sysUserService.deleteUserById(busiSmartRoom.getUserId());
                }
                BusiSmartRoomDeviceMap busiSmartRoomDeviceMap = new BusiSmartRoomDeviceMap();
                busiSmartRoomDeviceMap.setRoomId(busiSmartRoom.getId());
                List<BusiSmartRoomDeviceMap> busiSmartRoomDeviceMapList = busiSmartRoomDeviceMapService.selectBusiSmartRoomDeviceMapList(busiSmartRoomDeviceMap);
                for (BusiSmartRoomDeviceMap smartRoomDeviceMap : busiSmartRoomDeviceMapList) {
                    int deleteI = busiSmartRoomDeviceMapService.deleteBusiSmartRoomDeviceMapById(smartRoomDeviceMap.getId());
                    if (deleteI > 0) {
                        if (smartRoomDeviceMap.getDeviceType() == DeviceType.DOORPLATE.getCode()) {
                            BusiSmartRoomDoorplate busiSmartRoomDoorplate = SmartRoomDoorplateCache.getInstance().get(smartRoomDeviceMap.getDeviceId());
                            // 推送消息给电子门牌
                            UpdateDoorplateForMeetingRoomTask updateDoorplateForMeetingRoomTask = new UpdateDoorplateForMeetingRoomTask(busiSmartRoom.getId().toString(), 0, busiSmartRoomDoorplate.getSn());
                            taskService.addTask(updateDoorplateForMeetingRoomTask);
                        }
                    }
                }
                BusiSmartRoomDept busiSmartRoomDept = new BusiSmartRoomDept();
                busiSmartRoomDept.setRoomId(busiSmartRoom.getId());
                List<BusiSmartRoomDept> busiSmartRoomDeptList = busiSmartRoomDeptMapper.selectBusiSmartRoomDeptList(busiSmartRoomDept);
                for (BusiSmartRoomDept smartRoomDept : busiSmartRoomDeptList) {
                    busiSmartRoomDeptMapper.deleteBusiSmartRoomDeptById(smartRoomDept.getId());
                }
                SmartRoomCache.getInstance().remove(id);
                SmartRoomCache.getInstance().removeMeetingRoomInfo(id);
            }
        }
        return row;
    }

    /**
     * 获取未绑定门牌智慧办公房间
     * @param doorplateId
     * @return
     */
    @Override
    public List<BusiSmartRoom> selectBusiSmartRoomListForDoorplateNotBound(Long doorplateId) {
        List<BusiSmartRoom> busiSmartRoomList = busiSmartRoomMapper.selectBusiSmartRoomListForDoorplateNotBound(doorplateId);
        return busiSmartRoomList;
    }

    /**
     * 查询智慧办公房间预约列表通过部门
     * @param deptId
     * @return
     */
    @Override
    public List<MeetingRoomInfo> getSmartRoomBookListByDeptId(Long deptId) {
        Date currentTime = new Date();
        List<MeetingRoomInfo> meetingRoomInfoList = new ArrayList<>();
        List<BusiSmartRoomDept> busiSmartRoomDeptList = new ArrayList<>();
        if (deptId != null && DeptConstant.SMART_ROOM_DEPT_ID == deptId) {
            Collection<BusiSmartRoom> values = SmartRoomCache.getInstance().values();
            for (BusiSmartRoom busiSmartRoom : values) {
                MeetingRoomInfo meetingRoomInfo = SmartRoomCache.getInstance().getMeetingRoomInfoForWeb(busiSmartRoom.getId(), currentTime);
                if (meetingRoomInfo != null) {
                    meetingRoomInfoList.add(meetingRoomInfo);
                }
            }
        } else {
            BusiSmartRoomDept busiSmartRoomDeptCon = new BusiSmartRoomDept();
            busiSmartRoomDeptCon.setDeptId(deptId);
            busiSmartRoomDeptList = busiSmartRoomDeptMapper.selectBusiSmartRoomDeptList(busiSmartRoomDeptCon);
        }
        for (BusiSmartRoomDept smartRoomDept : busiSmartRoomDeptList) {
            MeetingRoomInfo meetingRoomInfo = SmartRoomCache.getInstance().getMeetingRoomInfoForWeb(smartRoomDept.getRoomId(), currentTime);
            if (meetingRoomInfo != null) {
                meetingRoomInfoList.add(meetingRoomInfo);
            }
        }
        return meetingRoomInfoList;
    }

    /**
     * 查询房间下的设备列表
     * @param roomId
     * @return
     */
    @Override
    public List<BusiSmartRoomDevice> selectBusiSmartRoomBoundDevice(Long roomId) {
        BusiSmartRoomDeviceMap busiSmartRoomDeviceMap = new BusiSmartRoomDeviceMap();
        busiSmartRoomDeviceMap.setRoomId(roomId);
        List<BusiSmartRoomDeviceMap> busiSmartRoomDeviceMapList = busiSmartRoomDeviceMapService.selectBusiSmartRoomDeviceMapList(busiSmartRoomDeviceMap);
        List<BusiSmartRoomDevice> busiSmartRoomDeviceList = new ArrayList<>();
        for (BusiSmartRoomDeviceMap smartRoomDeviceMap : busiSmartRoomDeviceMapList) {
            if (smartRoomDeviceMap != null) {
                BusiSmartRoomDevice busiSmartRoomDevice = busiSmartRoomDeviceService.selectBusiSmartRoomDeviceById(smartRoomDeviceMap.getDeviceId());
                if (busiSmartRoomDevice != null) {
                    busiSmartRoomDeviceList.add(busiSmartRoomDevice);
                }
            }
        }
        return busiSmartRoomDeviceList;
    }

    /**
     * 绑定设备到房间
     * @param busiSmartRoomDeviceMapList
     * @return
     */
    @Override
    public int bindDevice(List<BusiSmartRoomDeviceMap> busiSmartRoomDeviceMapList) {
        int i = 0;
        for (BusiSmartRoomDeviceMap busiSmartRoomDeviceMap : busiSmartRoomDeviceMapList) {

            Integer deviceType = busiSmartRoomDeviceMap.getDeviceType();
            if (DeviceType.isBindId(deviceType)) {
                BusiSmartRoomDeviceMap busiSmartRoomDeviceMapTemp = new BusiSmartRoomDeviceMap();
                busiSmartRoomDeviceMapTemp.setRoomId(busiSmartRoomDeviceMap.getRoomId());
                busiSmartRoomDeviceMapTemp.setDeviceType(deviceType);
                List<BusiSmartRoomDeviceMap> busiSmartRoomDeviceMapListTemp = busiSmartRoomDeviceMapService.selectBusiSmartRoomDeviceMapList(busiSmartRoomDeviceMapTemp);
                if (busiSmartRoomDeviceMapListTemp != null && busiSmartRoomDeviceMapListTemp.size() > 0) {
                    throw new SystemException("一个房间只能绑定一个注册终端和腾讯Rooms！");
                }
                Map<String, Object> params = busiSmartRoomDeviceMap.getParams();
                if (params != null) {
                    String bindId = String.valueOf(params.get("bindId"));
                    String name = String.valueOf(params.get("name"));

                    BusiSmartRoomDeviceVo busiSmartRoomDeviceVo = new BusiSmartRoomDeviceVo();
                    busiSmartRoomDeviceVo.setBindId(bindId);
                    List<BusiSmartRoomDevice> busiSmartRoomDeviceList = busiSmartRoomDeviceService.selectBusiSmartRoomDeviceList(busiSmartRoomDeviceVo);
                    if (busiSmartRoomDeviceList != null && busiSmartRoomDeviceList.size() > 0) {
                        throw new CustomException("设备（" + name + "）已绑定其他房间，请勿重复绑定！");
                    }

                    BusiSmartRoomDevice busiSmartRoomDevice = new BusiSmartRoomDevice();
                    busiSmartRoomDevice.setDeviceType(deviceType);
                    busiSmartRoomDevice.setBindId(bindId);
                    busiSmartRoomDevice.setDeviceName(name);
                    int i1 = busiSmartRoomDeviceService.insertBusiSmartRoomDevice(busiSmartRoomDevice);
                    if (i1 > 0) {
                        busiSmartRoomDeviceMap.setDeviceId(busiSmartRoomDevice.getId());
                    }
                }
            }
            Long deviceId = busiSmartRoomDeviceMap.getDeviceId();
            Long roomId = busiSmartRoomDeviceMap.getRoomId();
            BusiSmartRoomDevice busiSmartRoomDevice = SmartRoomDeviceCache.getInstance().get(deviceId);
            BusiSmartRoom busiSmartRoom = SmartRoomCache.getInstance().get(roomId);
            if (busiSmartRoomDevice != null && busiSmartRoom != null) {
                busiSmartRoomDeviceMap.setDeviceType(busiSmartRoomDevice.getDeviceType());
                List<BusiSmartRoomDeviceMap> busiSmartRoomDeviceMaps = busiSmartRoomDeviceMapService.selectBusiSmartRoomDeviceMapList(busiSmartRoomDeviceMap);
                if (busiSmartRoomDeviceMaps.size() > 0) {
                    throw new CustomException("设备（" + busiSmartRoomDevice.getDeviceName() + "）已绑定房间，请勿重复绑定！");
                } else {
                    i += busiSmartRoomDeviceMapService.insertBusiSmartRoomDeviceMap(busiSmartRoomDeviceMap);
                }
            }
        }
        return i;
    }

    /**
     * 房间解除设备绑定
     * @param busiSmartRoomDeviceMap
     * @return
     */
    @Override
    public int unBindDevice(BusiSmartRoomDeviceMap busiSmartRoomDeviceMap) {
        int i = 0;
        Long roomId = busiSmartRoomDeviceMap.getRoomId();
        Long deviceId = busiSmartRoomDeviceMap.getDeviceId();
        BusiSmartRoomDevice busiSmartRoomDevice = SmartRoomDeviceCache.getInstance().get(deviceId);
        BusiSmartRoom busiSmartRoom = SmartRoomCache.getInstance().get(roomId);
        if (busiSmartRoomDevice != null && busiSmartRoom != null) {
            List<BusiSmartRoomDeviceMap> busiSmartRoomDeviceMapList = busiSmartRoomDeviceMapService.selectBusiSmartRoomDeviceMapList(busiSmartRoomDeviceMap);
            for (BusiSmartRoomDeviceMap smartRoomDeviceMap : busiSmartRoomDeviceMapList) {
                i = busiSmartRoomDeviceMapService.deleteBusiSmartRoomDeviceMapById(smartRoomDeviceMap.getId());
                if (i > 0) {
                    busiSmartRoomDeviceService.deleteBusiSmartRoomDeviceById(smartRoomDeviceMap.getDeviceId());
                }
            }
        }
        return i;
    }

    /**
     * 更新缓存（今天）
     * @param roomId
     */
    private void updateMeetingRoomCache(Long roomId) {
        BusiSmartRoom busiSmartRoom = SmartRoomCache.getInstance().get(roomId);
        if (busiSmartRoom != null) {
            if (busiSmartRoom.getRoomType() == RoomType.MEETING_ROOM.getCode()) {
                Date date = new Date();
                Date startTime = DateUtils.getDayStartTime(date);
                Date endTime = DateUtils.getDayEndTime(date);
                List<BusiSmartRoomBook> busiSmartRoomBookList = busiSmartRoomBookMapper.selectBusiSmartRoomBookListForRoomTerm(roomId, startTime, endTime);
                Map<String, SysUser> userMap = new HashMap<>();
                for (BusiSmartRoomBook busiSmartRoomBook : busiSmartRoomBookList) {
                    String createByName = "";
                    String createByDeptName = "";
                    if (StringUtils.isNotEmpty(busiSmartRoomBook.getCreateBy())) {
                        SysUser sysUser = userMap.get(busiSmartRoomBook.getCreateBy());
                        if (sysUser == null) {
                            sysUser = sysUserService.selectUserByUserName(busiSmartRoomBook.getCreateBy());
                        }
                        if (sysUser != null) {
                            createByName = sysUser.getNickName();
                            if (sysUser.getDept() != null) {
                                createByDeptName = sysUser.getDept().getDeptName();
                            }
                        }
                    }
                    busiSmartRoomBook.getParams().put("createByName", createByName);
                    busiSmartRoomBook.getParams().put("createByDeptName", createByDeptName);
                }
                MeetingRoomInfo meetingRoomInfo = SmartRoomCache.getInstance().getMeetingRoomInfo(roomId);
                if (meetingRoomInfo == null) {
                    meetingRoomInfo = new MeetingRoomInfo();
                }
                meetingRoomInfo.setId(roomId);
                meetingRoomInfo.setRoomName(busiSmartRoom.getRoomName());
                String position = "";
                if (busiSmartRoom.getCity() != null) {
                    position += busiSmartRoom.getCity();
                }
                if (busiSmartRoom.getBuilding() != null) {
                    position += busiSmartRoom.getBuilding();
                }
                if (busiSmartRoom.getFloor() != null) {
                    position += busiSmartRoom.getFloor();
                }
                meetingRoomInfo.setPosition(position);
                meetingRoomInfo.setAllList(busiSmartRoomBookList, date);
                SmartRoomCache.getInstance().addMeetingRoomInfo(meetingRoomInfo);
            }
        }
    }
}
