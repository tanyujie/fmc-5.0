/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : SmartRoomCache.java
 * Package     : com.paradisecloud.fcm.edu.cache
 * @author sinhy
 * @since 2021-10-19 18:06
 * @version  V1.0
 */
package com.paradisecloud.fcm.smartroom.cache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.common.enumer.DeviceType;
import com.paradisecloud.fcm.common.enumer.RoomType;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.smartroom.model.MeetingRoomInfo;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * <pre>房间缓存</pre>
 *
 * @author sinhy
 * @version V1.0
 * @since 2021-10-19 18:06
 */
public class SmartRoomCache extends JavaCache<Long, BusiSmartRoom> {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     *
     * @since 2021-10-19 18:06
     */
    private static final long serialVersionUID = 1L;
    private static final SmartRoomCache INSTANCE = new SmartRoomCache();
    /** key：部门ID value：《房间ID：房间》 */
    private Map<Long, Map<Long, BusiSmartRoom>> deptSmartRoomMap = new ConcurrentHashMap<>();
    /** key：房间ID value：部门ID列表 */
    private Map<Long, Set<Long>> boundDeptMap = new ConcurrentHashMap();
    /** key：房间ID value：门牌ID */
    private Map<Long, Long> boundDoorplateMap = new ConcurrentHashMap<>();
    /** key：门牌ID value：房间ID */
    private Map<Long, Long> boundDoorplateRoomMap = new ConcurrentHashMap<>();
    /** key：门牌ID value：会议室信息 */
    private Map<Long, MeetingRoomInfo> meetingRoomInfoMap = new ConcurrentHashMap<>();
    /** key：房间ID value：终端预定信息 */
    private Map<Long, BusiSmartRoomBook> terminalBookMap = new ConcurrentHashMap<>();
    /** 已用签到码列表 key：签到码 value：参会人id */
    private volatile Map<String, Long> signInCodeMap = new ConcurrentHashMap<>();
    /** 最后清理签到码列表时间 */
    private volatile long lastCleanSignInCodeTime = 0;

    /**
     * <pre>构造方法</pre>
     *
     * @author sinhy
     * @since 2021-01-22 18:07
     */
    private SmartRoomCache() {
    }

    public BusiSmartRoom add(BusiSmartRoom busiSmartRoom) {
        return super.put(busiSmartRoom.getId(), busiSmartRoom);
    }

    public BusiSmartRoom remove(Long meetingRoomId) {
        BusiSmartRoom busiSmartRoom = super.remove(meetingRoomId);
        if (busiSmartRoom != null) {
            unBindDept(busiSmartRoom);
            unBindDoorplate(busiSmartRoom);
            return busiSmartRoom;
        }
        return null;
    }

    public void bindDept(BusiSmartRoomDept busiSmartRoomDept) {
        BusiSmartRoom busiSmartRoom = get(busiSmartRoomDept.getRoomId());
        if (busiSmartRoom != null) {
            Map<Long, BusiSmartRoom> busiSmartRoomMap = deptSmartRoomMap.get(busiSmartRoomDept.getDeptId());
            if (busiSmartRoomMap == null) {
                busiSmartRoomMap = new ConcurrentHashMap<>();
                deptSmartRoomMap.put(busiSmartRoomDept.getDeptId(), busiSmartRoomMap);
            }
            busiSmartRoomMap.put(busiSmartRoom.getId(), busiSmartRoom);
            Set<Long> deptIdSet = boundDeptMap.get(busiSmartRoomDept.getRoomId());
            if (deptIdSet == null) {
                deptIdSet = new HashSet<>();
            }
            deptIdSet.add(busiSmartRoomDept.getDeptId());
            boundDeptMap.put(busiSmartRoom.getId(), deptIdSet);
        }
    }

    public void unBindDept(BusiSmartRoomDept busiSmartRoomDept) {
        Map<Long, BusiSmartRoom> busiSmartRoomMap = deptSmartRoomMap.get(busiSmartRoomDept.getDeptId());
        if (busiSmartRoomMap != null) {
            deptSmartRoomMap.remove(busiSmartRoomDept.getDeptId());
        }
        Set<Long> deptIdSet = boundDeptMap.get(busiSmartRoomDept.getRoomId());
        if (deptIdSet != null) {
            deptIdSet.remove(busiSmartRoomDept.getDeptId());
        }
    }

    public void unBindDept(BusiSmartRoom busiSmartRoom) {
        for (Long deptId : deptSmartRoomMap.keySet()) {
            Map<Long, BusiSmartRoom> busiSmartRoomMap = deptSmartRoomMap.get(deptId);
            for (BusiSmartRoom busiSmartRoomTemp : busiSmartRoomMap.values()) {
                if (busiSmartRoomTemp.getId().longValue() == busiSmartRoom.getId().longValue()) {
                    busiSmartRoomMap.remove(busiSmartRoom.getId());
                }
            }
            if (busiSmartRoomMap.size() == 0) {
                deptSmartRoomMap.remove(deptId);
            }
        }
        boundDeptMap.remove(busiSmartRoom.getId());
    }

    public Set<Long> getBoundDeptIds(BusiSmartRoom busiSmartRoom) {
        return boundDeptMap.get(busiSmartRoom.getId());
    }

    public void bindDoorplate(BusiSmartRoomDeviceMap busiSmartRoomDeviceMap) {
        BusiSmartRoom busiSmartRoom = get(busiSmartRoomDeviceMap.getRoomId());
        if (busiSmartRoom != null) {
            if (busiSmartRoomDeviceMap.getDeviceType() == DeviceType.DOORPLATE.getCode()) {
                boundDoorplateMap.put(busiSmartRoomDeviceMap.getRoomId(), busiSmartRoomDeviceMap.getDeviceId());
                boundDoorplateRoomMap.put(busiSmartRoomDeviceMap.getDeviceId(), busiSmartRoomDeviceMap.getRoomId());
            }
        }
    }

    public void unBindDoorplate(BusiSmartRoomDeviceMap busiSmartRoomDeviceMap) {
        if (busiSmartRoomDeviceMap.getDeviceType() == DeviceType.DOORPLATE.getCode()) {
            boundDoorplateMap.remove(busiSmartRoomDeviceMap.getRoomId());
            boundDoorplateRoomMap.remove(busiSmartRoomDeviceMap.getDeviceId());
        }
    }

    public void unBindDoorplate(BusiSmartRoom busiSmartRoom) {
        Long doorplateId = boundDoorplateMap.remove(busiSmartRoom.getId());
        if (doorplateId != null) {
            boundDoorplateRoomMap.remove(doorplateId);
        }
    }

    public Long getBoundDoorplateId(BusiSmartRoom busiSmartRoom) {
        return boundDoorplateMap.get(busiSmartRoom.getId());
    }

    public Long getBoundDoorplateId(long roomId) {
        return boundDoorplateMap.get(roomId);
    }

    public Long getRoomIdByDoorplate(BusiSmartRoomDoorplate busiSmartRoomDoorplate) {
        return boundDoorplateRoomMap.get(busiSmartRoomDoorplate.getId());
    }

    public Long getRoomIdByDoorplateId(long doorplateId) {
        return boundDoorplateRoomMap.get(doorplateId);
    }

    public BusiSmartRoom getRoomByDoorplate(BusiSmartRoomDoorplate busiSmartRoomDoorplate) {
        Long roomId = boundDoorplateRoomMap.get(busiSmartRoomDoorplate.getId());
        if (roomId != null) {
            return get(roomId);
        }
        return null;
    }

    public BusiSmartRoom getRoomByDoorplateId(long doorplateId) {
        Long roomId = boundDoorplateRoomMap.get(doorplateId);
        if (roomId != null) {
            return get(roomId);
        }
        return null;
    }

    public void addMeetingRoomInfo(MeetingRoomInfo meetingRoomInfo) {
        meetingRoomInfoMap.put(meetingRoomInfo.getId(), meetingRoomInfo);
    }

    public void removeMeetingRoomInfo(long roomId) {
        meetingRoomInfoMap.remove(roomId);
    }

    public MeetingRoomInfo getMeetingRoomInfo(long roomId) {
        return meetingRoomInfoMap.get(roomId);
    }

    public MeetingRoomInfo getMeetingRoomInfo(long roomId, Date currentTime) {
        MeetingRoomInfo meetingRoomInfo = meetingRoomInfoMap.get(roomId);
        if (meetingRoomInfo != null) {
            return meetingRoomInfo.getMeetingRoomInfo(currentTime);
        }
        return null;
    }

    public MeetingRoomInfo getMeetingRoomInfoForWeb(long roomId, Date currentTime) {
        MeetingRoomInfo meetingRoomInfo = meetingRoomInfoMap.get(roomId);
        if (meetingRoomInfo != null) {
            return meetingRoomInfo.getMeetingRoomInfoForWeb(currentTime);
        }
        return null;
    }

    public void updateLastPushTime(long roomId) {
        MeetingRoomInfo meetingRoomInfo = meetingRoomInfoMap.get(roomId);
        if (meetingRoomInfo != null) {
            meetingRoomInfo.setLastPushTime(System.currentTimeMillis());
        }
    }

    public List<Map<String, Object>> getAllDeptRoomCount() {
        List<Map<String, Object>> deptCountList = new ArrayList<>();
        for (Long deptId : deptSmartRoomMap.keySet()) {
            Map<Long, BusiSmartRoom> busiSmartRoomMap = deptSmartRoomMap.get(deptId);
            long count = busiSmartRoomMap.size();
            if (count > 0) {
                Map<String, Object> countMap = new HashMap<>();
                countMap.put("deptId", deptId);
                countMap.put("count", count);
                deptCountList.add(countMap);
            }
        }
        return deptCountList;
    }

    public List<Map<String, Long>> getAllDeptMeetingRoomCount() {
        List<Map<String, Long>> deptCountList = new ArrayList<>();
        for (Long deptId : deptSmartRoomMap.keySet()) {
            long count = 0;
            Map<Long, BusiSmartRoom> busiSmartRoomMap = deptSmartRoomMap.get(deptId);
            for (Long roomId : busiSmartRoomMap.keySet()) {
                BusiSmartRoom busiSmartRoom = busiSmartRoomMap.get(roomId);
                if (busiSmartRoom.getRoomType() == RoomType.MEETING_ROOM.getCode()) {
                    count++;
                }
            }
            if (count > 0) {
                Map<String, Long> countMap = new HashMap<>();
                countMap.put("deptId", deptId);
                countMap.put("count", count);
                deptCountList.add(countMap);
            }
        }
        return deptCountList;
    }

    public Long addSignInCode(String signInCode, Long participantId) {
        return signInCodeMap.put(signInCode, participantId);
    }

    public Long removeSignInCode(String signInCode) {
        return signInCodeMap.remove(signInCode);
    }

    public void setSignInCodeMap(ConcurrentHashMap<String, Long> signInCodeMap) {
        this.signInCodeMap = signInCodeMap;
    }

    public boolean hasSignInCode(String signInCode) {
        return signInCodeMap.containsKey(signInCode);
    }

    public Long getParticipantIdBySignInCode(String signInCode) {
        return signInCodeMap.get(signInCode);
    }

    public synchronized String generateSignInCode() {
        String signInCode = "";
        for (int i = 0; i < 10000; i++) {
            long codeLong = RandomUtils.nextLong(0, 999999);
            signInCode = StringUtils.leftPad(String.valueOf(codeLong), 6);
            if (!hasSignInCode(signInCode)) {
                break;
            }
        }
        if (StringUtils.isNotEmpty(signInCode)) {
            addSignInCode(signInCode, 0L);
        }
        return signInCode;
    }

    public void updateLastCleanSignInCodeTime() {
        lastCleanSignInCodeTime = System.currentTimeMillis();
    }

    public long getLastCleanSignInCodeTime() {
        return lastCleanSignInCodeTime;
    }

    public static SmartRoomCache getInstance() {
        return INSTANCE;
    }

    public BusiSmartRoomBook getTerminalBook(long roomId) {
        return terminalBookMap.get(roomId);
    }

    public BusiSmartRoomBook addTerminalBook(BusiSmartRoomBook busiSmartRoomBook) {
        return terminalBookMap.put(busiSmartRoomBook.getRoomId(), busiSmartRoomBook);
    }

    public BusiSmartRoomBook removeTerminalBook(long roomId) {
        return terminalBookMap.remove(roomId);
    }
}
