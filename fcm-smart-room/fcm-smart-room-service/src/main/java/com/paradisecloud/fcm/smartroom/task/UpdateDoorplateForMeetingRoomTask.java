package com.paradisecloud.fcm.smartroom.task;

import com.paradisecloud.fcm.common.enumer.RoomType;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomBookMapper;
import com.paradisecloud.fcm.dao.model.BusiSmartRoom;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomBook;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDoorplate;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomCache;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomDoorplateCache;
import com.paradisecloud.fcm.smartroom.model.MeetingRoomInfo;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.service.ISysUserService;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateDoorplateForMeetingRoomTask extends Task {

    private String doorplateSn;
    private Long meetingRoomId;

    public UpdateDoorplateForMeetingRoomTask(String id, long delayInMilliseconds, String doorplateSn) {
        super("update_doorplate_mr_" + id, delayInMilliseconds);
        this.doorplateSn = doorplateSn;
    }

    public UpdateDoorplateForMeetingRoomTask(String id, long delayInMilliseconds, long meetingRoomId) {
        super("update_doorplate_mr_" + id, delayInMilliseconds);
        this.meetingRoomId = meetingRoomId;
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        // 推送注册信息给电子门牌
        if (StringUtils.isNotEmpty(doorplateSn)) {
            IMqttService mqttService = BeanFactory.getBean(IMqttService.class);
            mqttService.pushRegister(doorplateSn);
        } else if (meetingRoomId != null) {
            BusiSmartRoom busiSmartRoom = SmartRoomCache.getInstance().get(meetingRoomId);
            if (busiSmartRoom != null) {
                if (busiSmartRoom.getRoomType() == RoomType.MEETING_ROOM.getCode()) {
                    long roomId = busiSmartRoom.getId();
                    BusiSmartRoomBookMapper busiSmartRoomBookMapper = BeanFactory.getBean(BusiSmartRoomBookMapper.class);
                    ISysUserService sysUserService = BeanFactory.getBean(ISysUserService.class);
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
                        SmartRoomCache.getInstance().addMeetingRoomInfo(meetingRoomInfo);
                    }
                    meetingRoomInfo.setAllList(busiSmartRoomBookList, date);
                    Long boundDoorplateId = SmartRoomCache.getInstance().getBoundDoorplateId(busiSmartRoom);
                    BusiSmartRoomDoorplate busiSmartRoomDoorplate = SmartRoomDoorplateCache.getInstance().get(boundDoorplateId);
                    if (busiSmartRoomDoorplate != null) {
                        // 推送会议室信息给电子门牌
                        IMqttService mqttService = BeanFactory.getBean(IMqttService.class);
                        mqttService.pushMeetingRoomInfo(busiSmartRoomDoorplate.getSn());
                    }
                }
            }
        }
    }
}
