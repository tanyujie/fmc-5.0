package com.paradisecloud.fcm.web.monitor;

import com.paradisecloud.fcm.common.enumer.AppointmentConferenceStatus;
import com.paradisecloud.fcm.common.enumer.DeviceType;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomBookMapper;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomDeviceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomMapper;
import com.paradisecloud.fcm.dao.mapper.ViewConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomCache;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomDeviceCache;
import com.paradisecloud.fcm.smartroom.model.MeetingRoomInfo;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomBookService;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomDeviceMapService;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomDeviceService;
import com.paradisecloud.fcm.smartroom.task.CleanSignInCodeTask;
import com.paradisecloud.fcm.smartroom.task.UpdateDoorplateForMeetingRoomBookTask;
import com.paradisecloud.fcm.tencent.cache.TencentRoomsCache;
import com.paradisecloud.fcm.tencent.model.MeetingRoom;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.web.service.interfaces.IBusiAllConferenceAppointmentService;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.service.ISysUserService;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class RoomBookThread extends Thread implements InitializingBean {

    private IBusiSmartRoomBookService busiSmartRoomBookService = null;
    private BusiSmartRoomBookMapper busiSmartRoomBookMapper = null;
    private BusiSmartRoomMapper busiSmartRoomMapper = null;
    private TaskService taskService = null;
    private ISysUserService sysUserService = null;
    private ViewConferenceAppointmentMapper viewConferenceAppointmentMapper;
    private IBusiAllConferenceAppointmentService busiAllConferenceAppointmentService;
    private BusiSmartRoomDeviceMapper busiSmartRoomDeviceMapper;

    @Override
    public void run() {
        try {
            sleep(60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int i = 1;
        while (true) {
            if (isInterrupted()) {
                return;
            }

            try {
                if (busiSmartRoomBookService == null) {
                    busiSmartRoomBookService = BeanFactory.getBean(IBusiSmartRoomBookService.class);
                }
                if (busiSmartRoomBookMapper == null) {
                    busiSmartRoomBookMapper = BeanFactory.getBean(BusiSmartRoomBookMapper.class);
                }
                if (busiSmartRoomMapper == null) {
                    busiSmartRoomMapper = BeanFactory.getBean(BusiSmartRoomMapper.class);
                }
                if (taskService == null) {
                    taskService = BeanFactory.getBean(TaskService.class);
                }
                if (sysUserService == null) {
                    sysUserService = BeanFactory.getBean(ISysUserService.class);
                }
                if (viewConferenceAppointmentMapper == null) {
                    viewConferenceAppointmentMapper = BeanFactory.getBean(ViewConferenceAppointmentMapper.class);
                }
                if (busiAllConferenceAppointmentService == null) {
                    busiAllConferenceAppointmentService = BeanFactory.getBean(IBusiAllConferenceAppointmentService.class);
                }
                if (busiSmartRoomDeviceMapper == null) {
                    busiSmartRoomDeviceMapper = BeanFactory.getBean(BusiSmartRoomDeviceMapper.class);
                }
                Boolean isResetData = false;
                Collection<BusiSmartRoom> values = SmartRoomCache.getInstance().values();
                for (BusiSmartRoom busiSmartRoom : values) {
                    MeetingRoomInfo meetingRoomInfo = SmartRoomCache.getInstance().getMeetingRoomInfo(busiSmartRoom.getId(), new Date());
                    if (meetingRoomInfo != null && meetingRoomInfo.getStatus() != 0) {
                        Date today = meetingRoomInfo.getToday();
                        Date startTime = DateUtils.getDayStartTime(new Date());
                        if (startTime.after(today)) {
                            SmartRoomCache.getInstance().removeMeetingRoomInfo(meetingRoomInfo.getId());
                            isResetData = true;
                        } else {
                            process(meetingRoomInfo);
                        }
                    }
                }
                if (isResetData) {
                    List<BusiSmartRoom> busiSmartRoomList = busiSmartRoomMapper.selectBusiSmartRoomList(new BusiSmartRoom());
                    for (BusiSmartRoom busiSmartRoom : busiSmartRoomList) {
                        SmartRoomCache.getInstance().add(busiSmartRoom);
                        Long roomId = busiSmartRoom.getId();
                        Date date = new Date();
                        Date startTime = DateUtils.getDayStartTime(date);
                        Date endTime = DateUtils.getDayEndTime(date);
                        List<BusiSmartRoomBook> busiSmartRoomBookList = busiSmartRoomBookMapper.selectBusiSmartRoomBookListForNextTerm(roomId, startTime, endTime);
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
                    }
                }

                cleanSignInCode();

                checkDeviceStatus();

                try {
                    if (i >= 12) {
                        checkSmartRoomStatus();
                        i = 1;
                    }
                } catch (Exception e) {
                    i = 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                sleep(5000);
                i++;
            } catch (InterruptedException e) {
                e.printStackTrace();
                i++;
            }
        }
    }

    private void process(MeetingRoomInfo meetingRoomInfo) {
        try {
            Boolean isBoundDoorplate = false;
            BusiSmartRoom busiSmartRoom = SmartRoomCache.getInstance().get(meetingRoomInfo.getId());
            Long boundDoorplateId = SmartRoomCache.getInstance().getBoundDoorplateId(busiSmartRoom);
            if (boundDoorplateId != null) {
                isBoundDoorplate = true;
            }
            if (isBoundDoorplate) {
                long statusStartTime = meetingRoomInfo.getStatusStartTime();
                long lastPushTime = meetingRoomInfo.getLastPushTime();
                if (statusStartTime >= lastPushTime) {
                    // 推送消息给电子门牌
                    UpdateDoorplateForMeetingRoomBookTask updateDoorplateForMeetingRoomBookTask = new UpdateDoorplateForMeetingRoomBookTask(meetingRoomInfo.getId() + "", 1000, meetingRoomInfo.getId());
                    taskService.addTask(updateDoorplateForMeetingRoomBookTask);
                }
            }

            List<BusiSmartRoomBook> meetingRoomInfoPreList = meetingRoomInfo.getPreList();
            for (BusiSmartRoomBook busiSmartRoomBook : meetingRoomInfoPreList) {
                if (busiSmartRoomBook != null) {
                    Long appointmentConferenceId = busiSmartRoomBook.getAppointmentConferenceId();
                    String mcuType = busiSmartRoomBook.getMcuType();
                    if (appointmentConferenceId != null && StringUtils.isNotEmpty(mcuType)) {
                        ViewConferenceAppointment viewConferenceAppointment = viewConferenceAppointmentMapper.selectViewConferenceAppointmentById(mcuType, appointmentConferenceId);
                        if (viewConferenceAppointment != null) {
                            if (viewConferenceAppointment.getStatus() == AppointmentConferenceStatus.DISABLED.getValue()) {
                                String contextKey = EncryptIdUtil.generateContextKey(appointmentConferenceId, mcuType);
                                String generateEncryptId = EncryptIdUtil.generateEncryptId(contextKey);
                                busiAllConferenceAppointmentService.removeConferenceAppointment(generateEncryptId);
                            }
                        }
                    }
                }
            }

            List<BusiSmartRoomBook> preList = meetingRoomInfo.getPreList();
            for (BusiSmartRoomBook busiSmartRoomBook : preList) {
                long endTime = busiSmartRoomBook.getEndTime().getTime();
                long lastPushTime = meetingRoomInfo.getLastPushTime();
                if (endTime >= lastPushTime) {
                    if (busiSmartRoomBook.getBookStatus() == 0) {
                        busiSmartRoomBookService.endBusiSmartRoomBookById(meetingRoomInfo.getId());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cleanSignInCode() {
        long lastCleanSignInCodeTime = SmartRoomCache.getInstance().getLastCleanSignInCodeTime();
        if (System.currentTimeMillis() - lastCleanSignInCodeTime >= 3600000) {
            boolean cleanAll = false;
            try {
                int hour = new Date().getHours();
                if (hour == 4) {
                    cleanAll = true;
                }
            } catch (Exception e) {
            }
            SmartRoomCache.getInstance().updateLastCleanSignInCodeTime();
            CleanSignInCodeTask cleanSignInCodeTask = new CleanSignInCodeTask("1", 1000, cleanAll);
            taskService.addTask(cleanSignInCodeTask);
        }
    }

    private void checkDeviceStatus() {
        Collection<BusiSmartRoomDevice> values = SmartRoomDeviceCache.getInstance().values();
        for (BusiSmartRoomDevice value : values) {
            if (value != null) {
                Integer deviceType = value.getDeviceType();
                if (DeviceType.isBindId(deviceType)) {
                    String bindId = value.getBindId();
                    if (deviceType == DeviceType.TERMINAL.getCode()) {
                        if (bindId != null) {
                            BusiTerminal busiTerminal = TerminalCache.getInstance().get(Long.valueOf(bindId));
                            if (busiTerminal != null) {
                                if (value.getOnlineStatus() != busiTerminal.getOnlineStatus()) {
                                    value.setOnlineStatus(busiTerminal.getOnlineStatus());
                                    int i = busiSmartRoomDeviceMapper.updateBusiSmartRoomDevice(value);
                                    if (i > 0) {
                                        BusiSmartRoomDevice busiSmartRoomDeviceTemp = busiSmartRoomDeviceMapper.selectBusiSmartRoomDeviceById(value.getId());
                                        if (busiSmartRoomDeviceTemp != null && busiSmartRoomDeviceTemp.getId() != null) {
                                            SmartRoomDeviceCache.getInstance().add(busiSmartRoomDeviceTemp);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (deviceType == DeviceType.TENCENT_ROOMS.getCode()) {
                        MeetingRoom meetingRoom = TencentRoomsCache.getInstance().getMeetingRoom(bindId);
                        if (meetingRoom != null) {
                            if (meetingRoom.getMeetingRoomStatus() == 2 || meetingRoom.getMeetingRoomStatus() == 3) {
                                if (value.getOnlineStatus() == null || value.getOnlineStatus() == 2) {
                                    value.setOnlineStatus(1);
                                    int i = busiSmartRoomDeviceMapper.updateBusiSmartRoomDevice(value);
                                    if (i > 0) {
                                        BusiSmartRoomDevice busiSmartRoomDeviceTemp = busiSmartRoomDeviceMapper.selectBusiSmartRoomDeviceById(value.getId());
                                        if (busiSmartRoomDeviceTemp != null && busiSmartRoomDeviceTemp.getId() != null) {
                                            SmartRoomDeviceCache.getInstance().add(busiSmartRoomDeviceTemp);
                                        }
                                    }
                                }
                            } else {
                                if (value.getOnlineStatus() == null || value.getOnlineStatus() == 1) {
                                    value.setOnlineStatus(2);
                                    int i = busiSmartRoomDeviceMapper.updateBusiSmartRoomDevice(value);
                                    if (i > 0) {
                                        BusiSmartRoomDevice busiSmartRoomDeviceTemp = busiSmartRoomDeviceMapper.selectBusiSmartRoomDeviceById(value.getId());
                                        if (busiSmartRoomDeviceTemp != null && busiSmartRoomDeviceTemp.getId() != null) {
                                            SmartRoomDeviceCache.getInstance().add(busiSmartRoomDeviceTemp);
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    private void checkSmartRoomStatus() {
        Collection<BusiSmartRoom> values = SmartRoomCache.getInstance().values();
        for (BusiSmartRoom busiSmartRoom : values) {
            if (busiSmartRoom != null) {
                String bookName = "腾讯会议";
                boolean isUsing = false;
                Long roomId = busiSmartRoom.getId();
                IBusiSmartRoomDeviceMapService busiSmartRoomDeviceMapService = BeanFactory.getBean(IBusiSmartRoomDeviceMapService.class);
                BusiSmartRoomDeviceMap busiSmartRoomDeviceMap = new BusiSmartRoomDeviceMap();
                busiSmartRoomDeviceMap.setRoomId(roomId);
                busiSmartRoomDeviceMap.setDeviceType(DeviceType.TERMINAL.getCode());
                List<BusiSmartRoomDeviceMap> busiSmartRoomDeviceMapList = busiSmartRoomDeviceMapService.selectBusiSmartRoomDeviceMapList(busiSmartRoomDeviceMap);
                BusiSmartRoomDeviceMap busiSmartRoomDeviceMapTencentRoom = new BusiSmartRoomDeviceMap();
                busiSmartRoomDeviceMapTencentRoom.setRoomId(roomId);
                busiSmartRoomDeviceMapTencentRoom.setDeviceType(DeviceType.TENCENT_ROOMS.getCode());
                List<BusiSmartRoomDeviceMap> busiSmartRoomDeviceMapTencentRoomList = busiSmartRoomDeviceMapService.selectBusiSmartRoomDeviceMapList(busiSmartRoomDeviceMapTencentRoom);
                busiSmartRoomDeviceMapList.addAll(busiSmartRoomDeviceMapTencentRoomList);
                for (BusiSmartRoomDeviceMap smartRoomDeviceMap : busiSmartRoomDeviceMapList) {
                    Long deviceId = smartRoomDeviceMap.getDeviceId();
                    IBusiSmartRoomDeviceService busiSmartRoomDeviceService = BeanFactory.getBean(IBusiSmartRoomDeviceService.class);
                    BusiSmartRoomDevice busiSmartRoomDevice = busiSmartRoomDeviceService.selectBusiSmartRoomDeviceById(deviceId);
                    if (busiSmartRoomDevice != null) {
                        String bindId = busiSmartRoomDevice.getBindId();
                        Integer deviceType = busiSmartRoomDevice.getDeviceType();
                        if (com.paradisecloud.common.utils.StringUtils.isNotEmpty(bindId)) {
                            DeviceType convert = DeviceType.convert(deviceType);
                            if (convert.getCode() == DeviceType.TERMINAL.getCode()) {
                                Collection<BaseConferenceContext> valuesTemp = AllConferenceContextCache.getInstance().values();
                                for (BaseConferenceContext value : valuesTemp) {
                                    BaseAttendee attendeeByTerminalId = value.getAttendeeByTerminalId(Long.valueOf(bindId));
                                    if (attendeeByTerminalId != null) {
                                        int meetingStatus = attendeeByTerminalId.getMeetingStatus();
                                        if (meetingStatus == 1) {
                                            bookName = value.getTenantId() + value.getConferenceNumber();
                                            isUsing = true;
                                        }
                                    }
                                }
                            }
                            if (convert.getCode() == DeviceType.TENCENT_ROOMS.getCode()) {
                                MeetingRoom meetingRoom = TencentRoomsCache.getInstance().getMeetingRoom(bindId);
                                if (meetingRoom != null && meetingRoom.getMeetingRoomStatus() == 3) {
                                    isUsing = true;
                                }
                            }
                        }
                    }
                }

                if (isUsing) {
                    Long userId = busiSmartRoom.getUserId();
                    SysUser sysUser = sysUserService.selectUserById(userId);
                    BusiSmartRoomBook busiSmartRoomBook = new BusiSmartRoomBook();
                    busiSmartRoomBook.setBookName(bookName);
                    Date date = new Date();
                    Date diffDate = DateUtils.getDiffDate(date, 10, TimeUnit.MINUTES);
                    busiSmartRoomBook.setStartTime(date);
                    busiSmartRoomBook.setEndTime(diffDate);
                    busiSmartRoomBook.setBookStatus(3);
                    busiSmartRoomBook.setCreateBy(sysUser.getNickName());
                    busiSmartRoomBook.setRoomId(busiSmartRoom.getId());
                    HashMap<String, Object> objectObjectHashMap = new HashMap<>();
                    objectObjectHashMap.put("createByDeptName", sysUser.getDept().getDeptName());
                    objectObjectHashMap.put("createByName", sysUser.getNickName());
                    busiSmartRoomBook.setParams(objectObjectHashMap);
                    busiSmartRoomBook.setRoomId(roomId);
                    SmartRoomCache.getInstance().addTerminalBook(busiSmartRoomBook);
                    // 推送消息给电子门牌
                    UpdateDoorplateForMeetingRoomBookTask updateDoorplateForMeetingRoomBookTask = new UpdateDoorplateForMeetingRoomBookTask(busiSmartRoomBook.getRoomId() + "", 1000, busiSmartRoomBook.getRoomId());
                    taskService.addTask(updateDoorplateForMeetingRoomBookTask);
                } else {
                    BusiSmartRoomBook terminalBook = SmartRoomCache.getInstance().getTerminalBook(roomId);
                    if (terminalBook != null) {
                        // 推送消息给电子门牌
                        UpdateDoorplateForMeetingRoomBookTask updateDoorplateForMeetingRoomBookTask = new UpdateDoorplateForMeetingRoomBookTask(terminalBook.getRoomId() + "", 1000, terminalBook.getRoomId());
                        taskService.addTask(updateDoorplateForMeetingRoomBookTask);
                        SmartRoomCache.getInstance().removeTerminalBook(roomId);
                    }
                }
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}
