package com.paradisecloud.fcm.mqtt.impls;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.AppType;
import com.paradisecloud.fcm.common.enumer.DeviceType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.RoomLevel;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiInfoDisplayMapper;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomDoorplateMapper;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomDoorplateRegisterMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.McuTypeVo;
import com.paradisecloud.fcm.mqtt.cache.InfoDisplayCache;
import com.paradisecloud.fcm.mqtt.constant.TerminalTopic;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiAllMcuService;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiFmqAllConferenceAppointmentService;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomCache;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomDeviceCache;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomDoorplateCache;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomLotCache;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomBookService;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomDeviceMapService;
import com.paradisecloud.fcm.mqtt.common.ResponseTerminal;
import com.paradisecloud.fcm.mqtt.constant.DeviceAction;
import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.interfaces.IDeviceActionService;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.tencent.cache.TencentRoomsCache;
import com.paradisecloud.fcm.tencent.model.MeetingRoom;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.service.ISysUserService;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class DeviceActionServiceImpl implements IDeviceActionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceActionServiceImpl.class);

    @Resource
    private BusiSmartRoomDoorplateRegisterMapper busiSmartRoomDoorplateRegisterMapper;
    @Resource
    private IBusiSmartRoomDeviceMapService busiSmartRoomDeviceMapService;
    @Resource
    private BusiSmartRoomDoorplateMapper busiSmartRoomDoorplateMapper;
    @Resource
    private IMqttService mqttService;
    @Resource
    private IBusiFmqAllConferenceAppointmentService busiFmqAllConferenceAppointmentService;
    @Resource
    private IBusiAllMcuService busiAllMcuService;
    @Resource
    private IBusiSmartRoomBookService busiSmartRoomBookService;
    @Resource
    private ISysUserService sysUserService;
    @Resource
    private BusiSmartRoomDeptMapper busiSmartRoomDeptMapper;
    @Resource
    private BusiInfoDisplayMapper busiInfoDisplayMapper;

    @Override
    public void register(JSONObject jsonS, String clientId) {

        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX_DOORPLATE + clientId;
        String action = DeviceAction.REGISTER;
        JSONObject jsonObject = new JSONObject();
        if (jsonS != null) {
            String ip = jsonS.getString(MqttConfigConstant.IP);
            String appType = jsonS.getString(MqttConfigConstant.TERMINAL_TYPE);
            String versionCode = jsonS.getString(MqttConfigConstant.versionCode);
            String versionName = jsonS.getString(MqttConfigConstant.versionName);
            String connectIp = jsonS.getString(MqttConfigConstant.CONNECT_IP);

            AppType appTypeTemp = AppType.convertByType(appType);
            BusiSmartRoomDoorplate busiSmartRoomDoorplate = SmartRoomDoorplateCache.getInstance().getBySn(clientId);
            if (busiSmartRoomDoorplate != null) {
                busiSmartRoomDoorplate.setAppType(appTypeTemp.getCode());
                busiSmartRoomDoorplate.setAppVersionCode(versionCode);
                busiSmartRoomDoorplate.setAppVersionName(versionName);
                busiSmartRoomDoorplate.setConnectIp(connectIp);
                busiSmartRoomDoorplate.setIp(ip);
                jsonObject.put("registered", true);
                jsonObject.put("bound", false);
                BusiSmartRoomDeviceMap busiSmartRoomDeviceMapCon = new BusiSmartRoomDeviceMap();
                busiSmartRoomDeviceMapCon.setDeviceType(DeviceType.DOORPLATE.getCode());
                busiSmartRoomDeviceMapCon.setDeviceId(busiSmartRoomDoorplate.getId());
                List<BusiSmartRoomDeviceMap> busiSmartRoomDeviceMapList = busiSmartRoomDeviceMapService.selectBusiSmartRoomDeviceMapList(busiSmartRoomDeviceMapCon);
                if (busiSmartRoomDeviceMapList.size() > 0) {
                    BusiSmartRoomDeviceMap busiSmartRoomDeviceMap = busiSmartRoomDeviceMapList.get(0);
                    BusiSmartRoom busiSmartRoom = SmartRoomCache.getInstance().get(busiSmartRoomDeviceMap.getRoomId());
                    if (busiSmartRoom != null) {
                        jsonObject.put("bound", true);
                        jsonObject.put("roomName", busiSmartRoom.getRoomName());
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
                        Long userId = busiSmartRoom.getUserId();
                        SysUser sysUser = sysUserService.selectUserById(userId);
                        boolean canBookOnlineConference = false;
                        if (sysUser != null) {
                            Long deptId = sysUser.getDeptId();
                            McuTypeVo defaultMcuType = busiAllMcuService.getDefaultMcuType(deptId);
                            if (defaultMcuType != null) {
                                canBookOnlineConference = true;
                            }
                            jsonObject.put("deptId", deptId);
                        }
                        jsonObject.put("position", position);
                        jsonObject.put("roomType", busiSmartRoom.getRoomType());
                        jsonObject.put("userId", userId);
                        jsonObject.put("roomLevel", busiSmartRoom.getRoomLevel());
                        jsonObject.put("canBookOnlineConference", canBookOnlineConference);
                    }
                }
                int i = busiSmartRoomDoorplateMapper.updateBusiSmartRoomDoorplate(busiSmartRoomDoorplate);
                if (i > 0) {
                    SmartRoomDoorplateCache.getInstance().add(busiSmartRoomDoorplate);
                }
                BusiSmartRoomDoorplateRegister busiSmartRoomDoorplateRegister = new BusiSmartRoomDoorplateRegister();
                busiSmartRoomDoorplateRegister.setSn(clientId);
                List<BusiSmartRoomDoorplateRegister> busiSmartRoomDoorplateRegisterList = busiSmartRoomDoorplateRegisterMapper.selectBusiSmartRoomDoorplateRegisterList(busiSmartRoomDoorplateRegister);
                if (busiSmartRoomDoorplateRegisterList != null && busiSmartRoomDoorplateRegisterList.size() > 0) {
                    for (BusiSmartRoomDoorplateRegister meetingRoomDoorplateRegister : busiSmartRoomDoorplateRegisterList) {
                        busiSmartRoomDoorplateRegisterMapper.deleteBusiSmartRoomDoorplateRegisterById(meetingRoomDoorplateRegister.getId());
                    }
                }
            } else {
                jsonObject.put("registered", false);
                BusiSmartRoomDoorplateRegister busiSmartRoomDoorplateRegister = new BusiSmartRoomDoorplateRegister();
                busiSmartRoomDoorplateRegister.setSn(clientId);
                List<BusiSmartRoomDoorplateRegister> busiSmartRoomDoorplateRegisterList = busiSmartRoomDoorplateRegisterMapper.selectBusiSmartRoomDoorplateRegisterList(busiSmartRoomDoorplateRegister);
                BusiSmartRoomDoorplateRegister busiSmartRoomDoorplateRegisterTemp = new BusiSmartRoomDoorplateRegister();
                busiSmartRoomDoorplateRegisterTemp.setSn(clientId);
                busiSmartRoomDoorplateRegisterTemp.setAppType(appType);
                busiSmartRoomDoorplateRegisterTemp.setAppVersionCode(versionCode);
                busiSmartRoomDoorplateRegisterTemp.setAppVersionName(versionName);
                busiSmartRoomDoorplateRegisterTemp.setIp(ip);
                busiSmartRoomDoorplateRegisterTemp.setConnectIp(connectIp);
                if (busiSmartRoomDoorplateRegisterList != null && busiSmartRoomDoorplateRegisterList.size() > 0) {
                    busiSmartRoomDoorplateRegisterTemp.setId(busiSmartRoomDoorplateRegisterList.get(0).getId());
                    busiSmartRoomDoorplateRegisterTemp.setUpdateTime(new Date());
                    busiSmartRoomDoorplateRegisterMapper.updateBusiSmartRoomDoorplateRegister(busiSmartRoomDoorplateRegisterTemp);
                } else {
                    busiSmartRoomDoorplateRegisterTemp.setCreateTime(new Date());
                    busiSmartRoomDoorplateRegisterMapper.insertBusiSmartRoomDoorplateRegister(busiSmartRoomDoorplateRegisterTemp);
                }
            }
            ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, clientId, "");
        } else {
            ResponseTerminal.getInstance().responseTerminalFailed(terminalTopic, action, jsonObject, clientId, "");
        }

    }

    @Override
    public void meetingRoomInfo(JSONObject jsonS, String clientId) {
        mqttService.pushMeetingRoomInfo(clientId);
    }

    @Override
    public void createSmartRoomBook(JSONObject jsonS, String clientId) {

        Long id = null;
        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX_DOORPLATE + clientId;
        String action = DeviceAction.CREATE_SMARTROOM_BOOK;
        JSONObject jsonObject = new JSONObject();
        try {
            if (jsonS != null) {
                String bookName = jsonS.getString("bookName");
                Date startTime = jsonS.getDate("startTime");
                Date endTime = jsonS.getDate("endTime");
                Boolean isNeedAppointmentConference = jsonS.getBoolean("isNeedAppointmentConference");

                BusiSmartRoomDoorplate busiSmartRoomDoorplate = SmartRoomDoorplateCache.getInstance().getBySn(clientId);
                BusiSmartRoom roomByDoorplate = SmartRoomCache.getInstance().getRoomByDoorplate(busiSmartRoomDoorplate);
                if (roomByDoorplate != null) {
                    Long roomId = roomByDoorplate.getId();
                    BusiSmartRoom busiSmartRoom = SmartRoomCache.getInstance().get(roomId);
                    if (busiSmartRoom != null) {
                        Integer roomLevel = busiSmartRoom.getRoomLevel();
                        if (roomLevel != RoomLevel.ANYONE.getCode()) {
                            String msg = "创建会议室预约失败！";
                            msg = msg + "此会议室只支持" + RoomLevel.convert(roomLevel).getName() + "用户操作！";
                            jsonObject.put("message", msg);
                            ResponseTerminal.getInstance().responseTerminalFailed(terminalTopic, action, jsonObject, clientId, "");
                            return;
                        }
                    }
                    BusiSmartRoomBook busiSmartRoomBook = new BusiSmartRoomBook();
                    busiSmartRoomBook.setBookName(bookName);
                    busiSmartRoomBook.setStartTime(startTime);
                    busiSmartRoomBook.setEndTime(endTime);
                    busiSmartRoomBook.setRoomId(roomId);
                    int i = busiSmartRoomBookService.insertBusiSmartRoomBook(busiSmartRoomBook);
                    if (i > 0) {
                        id = busiSmartRoomBook.getId();
                        if (isNeedAppointmentConference) {
                            Long userId = busiSmartRoom.getUserId();
                            SysUser sysUser = sysUserService.selectUserById(userId);
                            if (sysUser != null) {
                                Long deptId = sysUser.getDeptId();
                                McuTypeVo defaultMcuType = busiAllMcuService.getDefaultMcuType(deptId);
                                if (defaultMcuType != null) {
                                    busiSmartRoomBook.setMcuType(defaultMcuType.getCode());
                                    Map<String, Object> params = busiSmartRoomBook.getParams();

                                    if (!params.containsKey("conferenceName")) {
                                        params.put("conferenceName", busiSmartRoomBook.getBookName());
                                    }
                                    if (!params.containsKey("endTime")) {
                                        params.put("endTime", busiSmartRoomBook.getEndTime());
                                    }
                                    if (!params.containsKey("muteType")) {
                                        params.put("muteType", busiSmartRoomBook.getMcuType());
                                    }
                                    if (!params.containsKey("startTime")) {
                                        params.put("startTime", busiSmartRoomBook.getStartTime());
                                    }
                                    if (!params.containsKey("conferenceName")) {
                                        params.put("conferenceName", busiSmartRoomBook.getBookName());
                                    }
                                    if (!params.containsKey("createUserId")) {
                                        params.put("createUserId", sysUser.getUserId());
                                    }
                                    if (!params.containsKey("createUserName")) {
                                        params.put("createUserName", sysUser.getUserName());
                                    }
                                    if (!params.containsKey("templateParticipants")) {
                                        BusiSmartRoomDeviceMap busiSmartRoomDeviceMap = new BusiSmartRoomDeviceMap();
                                        busiSmartRoomDeviceMap.setRoomId(busiSmartRoomBook.getRoomId());
                                        List<BusiSmartRoomDeviceMap> busiSmartRoomDeviceMapList = busiSmartRoomDeviceMapService.selectBusiSmartRoomDeviceMapList(busiSmartRoomDeviceMap);
                                        List<Map<String, Object>> mapList = new ArrayList<>();
                                        if (busiSmartRoomDeviceMapList != null && busiSmartRoomDeviceMapList.size() > 0) {
                                            Boolean isTencentMcu = false;
                                            if (busiSmartRoomBook.getMcuType() == McuType.MCU_TENCENT.getCode()) {
                                                isTencentMcu = true;
                                            }
                                            for (BusiSmartRoomDeviceMap smartRoomDeviceMap : busiSmartRoomDeviceMapList) {
                                                BusiSmartRoomDevice busiSmartRoomDevice = SmartRoomDeviceCache.getInstance().get(smartRoomDeviceMap.getDeviceId());
                                                if (busiSmartRoomDevice != null) {
                                                    Integer deviceType = busiSmartRoomDevice.getDeviceType();
                                                    if (DeviceType.isBindId(deviceType)) {
                                                        if (deviceType == DeviceType.TERMINAL.getCode()) {
                                                            BusiTerminal busiTerminal = TerminalCache.getInstance().get(Long.valueOf(busiSmartRoomDevice.getBindId()));
                                                            if (busiTerminal != null) {
                                                                SysDept sysDept = SysDeptCache.getInstance().get(busiTerminal.getDeptId());
                                                                if (sysDept != null) {
                                                                    String ancestors = sysDept.getAncestors();
                                                                    if (ancestors.contains(String.valueOf(deptId)) || deptId == busiTerminal.getDeptId()) {
                                                                        Map<String, Object> map = new HashMap<>();
                                                                        map.put("id", busiTerminal.getId());
                                                                        map.put("terminalId", busiTerminal.getId());
                                                                        map.put("attendType", 1);
                                                                        map.put("weight", 1);
                                                                        map.put("businessProperties", "");
                                                                        mapList.add(map);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        if (isTencentMcu) {
                                                            if (deviceType == DeviceType.TENCENT_ROOMS.getCode()) {
                                                                MeetingRoom meetingRoom = TencentRoomsCache.getInstance().getMeetingRoom(busiSmartRoomDevice.getBindId());
                                                                Map<String, Object> map = new HashMap<>();
                                                                map.put("id", meetingRoom.getMeetingRoomId());
                                                                map.put("terminalId", meetingRoom.getMeetingRoomId());
                                                                map.put("attendType", 1);
                                                                map.put("weight", 1);
                                                                map.put("businessProperties", "");
                                                                mapList.add(map);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        params.put("templateParticipants", mapList);
                                    }

                                    BusiConferenceAppointment busiConferenceAppointment = new BusiConferenceAppointment();
                                    busiConferenceAppointment.setDeptId(Long.valueOf(deptId));
                                    String endTimeStr = DateUtil.convertDateToString(busiSmartRoomBook.getEndTime(), null);
                                    String startTimeStr = DateUtil.convertDateToString(busiSmartRoomBook.getStartTime(), null);
                                    busiConferenceAppointment.setEndTime(endTimeStr);
                                    busiConferenceAppointment.setStartTime(startTimeStr);
                                    busiConferenceAppointment.setIsAutoCreateTemplate(1);
                                    busiConferenceAppointment.setRepeatRate(1);
                                    busiConferenceAppointment.setStatus(1);
                                    busiConferenceAppointment.setType(1);
                                    busiConferenceAppointment.setParams(params);
                                    busiConferenceAppointment.setRoomBookId(busiSmartRoomBook.getId());
                                    Map<String, Object> map = busiFmqAllConferenceAppointmentService.addConferenceAppointment(busiConferenceAppointment, defaultMcuType.getCode());
                                    Long appointmentId = (Long) map.get("appointmentId");
                                    if (appointmentId != null) {
                                        busiSmartRoomBook.setAppointmentConferenceId(appointmentId);
                                        busiSmartRoomBookService.updateBusiSmartRoomBookData(busiSmartRoomBook);
                                        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
                                        String contextKey = EncryptIdUtil.generateContextKey(appointmentId, busiSmartRoomBook.getMcuType());
                                        String generateEncryptId = EncryptIdUtil.generateEncryptId(contextKey);
                                        objectObjectHashMap.put("apConferenceId", generateEncryptId);
                                        busiSmartRoomBook.setParams(objectObjectHashMap);
                                        ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                                        String writeValueAsString = objectMapper.writeValueAsString(busiSmartRoomBook);
                                        JSONObject parseObject = JSONObject.parseObject(writeValueAsString);
                                        ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, parseObject, clientId, "");
                                        return;
                                    }
                                } else {
                                    id = busiSmartRoomBook.getId();
                                    jsonObject.put("message", "当前部门不能开启在线会议！");
                                    ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, clientId, "");
                                    return;
                                }
                            }
                        }
                        ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                        String writeValueAsString = objectMapper.writeValueAsString(busiSmartRoomBook);
                        JSONObject parseObject = JSONObject.parseObject(writeValueAsString);
                        ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, parseObject, clientId, "");
                        return;
                    }
                }
            }
        } catch (Exception e) {
            if (e instanceof CustomException) {
                busiSmartRoomBookService.cancelBusiSmartRoomBookById(id);
                jsonObject.put("message", e.getMessage());
            } else {
                jsonObject.put("message", "创建会议室预约失败！");
            }
            ResponseTerminal.getInstance().responseTerminalFailed(terminalTopic, action, jsonObject, clientId, "");
            return;

        }
        ResponseTerminal.getInstance().responseTerminalFailed(terminalTopic, action, jsonObject, clientId, "");
    }

    @Override
    public void pushInfoDisplay(JSONObject jsonS, String clientId) {
        String terminalTopic = MqttConfigConstant.TOPIC_PREFIX_DOORPLATE + clientId;
        String action = TerminalTopic.INFO_DISPLAY;
        JSONObject jsonObject = new JSONObject();

        boolean isPush = false;
        BusiSmartRoomDoorplate smartRoomDoorplate = SmartRoomDoorplateCache.getInstance().getBySn(clientId);
        BusiInfoDisplay busiInfoDisplayTemp = new BusiInfoDisplay();

        if (smartRoomDoorplate != null) {
            Long id = smartRoomDoorplate.getId();
            BusiSmartRoom roomByDoorplateId = SmartRoomCache.getInstance().getRoomByDoorplateId(id);
            if (roomByDoorplateId != null) {
                Long roomId = roomByDoorplateId.getId();
                BusiInfoDisplay busiInfoDisplay = new BusiInfoDisplay();
                busiInfoDisplay.setPushType(2);
                busiInfoDisplay.setStatus(1);
                busiInfoDisplay.setType(1);
                List<BusiInfoDisplay> busiInfoDisplayList = busiInfoDisplayMapper.selectBusiInfoDisplayList(busiInfoDisplay);
                for (BusiInfoDisplay infoDisplay : busiInfoDisplayList) {
                    String pushTerminalIds = infoDisplay.getPushTerminalIds();
                    String[] split = pushTerminalIds.split(",");
                    for (String idStr : split) {
                        Long idTemp = Long.valueOf(idStr);
                        if (idTemp.longValue() == roomId.longValue()) {
                            busiInfoDisplayTemp = infoDisplay;
                            isPush = true;
                        }
                    }
                }
            }
        }

        if (isPush) {
            String urlTemp = ExternalConfigCache.getInstance().getFmcRootUrl();
            String fmcRootUrlExternal = ExternalConfigCache.getInstance().getFmcRootUrlExternal();
            if (com.paradisecloud.common.utils.StringUtils.isNotEmpty(fmcRootUrlExternal)) {
                try {
                    String ip = smartRoomDoorplate.getConnectIp();
                    String externalIp = fmcRootUrlExternal.replace("http://", "").replace("https://", "");
                    if (externalIp.indexOf(":") > 0) {
                        externalIp.substring(0, externalIp.indexOf(":"));
                    }
                    if (externalIp.indexOf("/") > 0) {
                        externalIp = externalIp.substring(0, externalIp.indexOf("/"));
                    }
                    if (externalIp.equals(ip)) {
                        urlTemp = fmcRootUrlExternal;
                    }
                } catch (Exception e) {
                }
            }
            String introduce = busiInfoDisplayTemp.getUrlData();
            if (com.paradisecloud.common.utils.StringUtils.isNotEmpty(introduce)) {
                introduce = introduce.replace("{url}", urlTemp);
                jsonObject.put("urlData", introduce);
            }

            jsonObject.put("type", busiInfoDisplayTemp.getType());
            jsonObject.put("displayType", busiInfoDisplayTemp.getDisplayType());
            ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, clientId, "");
        } else {

            jsonObject.put("type", 0);
            ResponseTerminal.getInstance().responseTerminalSuccess(terminalTopic, action, jsonObject, clientId, "");
        }

    }
}
