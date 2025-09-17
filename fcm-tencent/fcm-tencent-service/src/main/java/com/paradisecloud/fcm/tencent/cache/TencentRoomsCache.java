package com.paradisecloud.fcm.tencent.cache;

import com.paradisecloud.fcm.tencent.model.MeetingRoom;
import com.paradisecloud.fcm.tencent.model.reponse.RoomResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 腾讯Rooms缓存
 * key：腾讯ID
 */
public class TencentRoomsCache {

    private static final TencentRoomsCache INSTANCE = new TencentRoomsCache();
    private Map<Long, Map<String, MeetingRoom>> mcuMeetingRoomMap = new ConcurrentHashMap<>();
    private Map<Long, List<MeetingRoom>> mcuMeetingRoomListMap = new ConcurrentHashMap<>();

    public static TencentRoomsCache getInstance() {
        return INSTANCE;
    }

    private TencentRoomsCache() {
    }

    public void setMeetingRoomList(long mcuId, List<MeetingRoom> meetingRoomList) {
        if (meetingRoomList == null) {
            meetingRoomList = new ArrayList<>();
        }
        mcuMeetingRoomListMap.put(mcuId, meetingRoomList);
        Map<String, MeetingRoom> meetingRoomMap = new ConcurrentHashMap<>();
        for (MeetingRoom meetingRoom : meetingRoomList) {
            meetingRoomMap.put(meetingRoom.getMeetingRoomId(), meetingRoom);
        }
        mcuMeetingRoomMap.put(mcuId, meetingRoomMap);
    }

    /**
     *
     * @param mcuId
     * @param pageIndex 从1开始
     * @param pageSize
     * @return
     */
    public RoomResponse getMeetingRoomList(long mcuId, int pageIndex, int pageSize) {
        RoomResponse roomResponse = new RoomResponse();
        List<MeetingRoom> allMeetingRoomList = mcuMeetingRoomListMap.get(mcuId);
        if (pageSize == 0 || allMeetingRoomList == null || allMeetingRoomList.size() == 0) {
            roomResponse.setCurrentPage(1);
            roomResponse.setCurrentSize(pageSize);
            roomResponse.setTotalCount(0);
            roomResponse.setTotalPage(0);
            roomResponse.setMeetingRoomList(new ArrayList<>());
            return roomResponse;
        }
        int totalCount= allMeetingRoomList.size();
        int totalPage = allMeetingRoomList.size() / pageSize;
        if (allMeetingRoomList.size() % pageSize > 0) {
            totalPage = totalPage + 1;
        }
        if (pageIndex < 1) {
            pageIndex = 1;
        }
        if (pageIndex > totalPage) {
            pageIndex = totalPage;
        }
        roomResponse.setCurrentPage(pageIndex);
        roomResponse.setCurrentSize(pageSize);
        roomResponse.setTotalCount(totalCount);
        roomResponse.setTotalPage(totalPage);
        List<MeetingRoom> meetingRoomList = new ArrayList<>();
        for (int i = (pageIndex - 1) * pageSize; i < pageIndex * pageSize && i < totalCount; i++) {
            meetingRoomList.add(allMeetingRoomList.get(i));
        }
        roomResponse.setMeetingRoomList(meetingRoomList);
        return roomResponse;
    }

    public MeetingRoom getMeetingRoom(long mcuId, String meetingRoomId) {
        MeetingRoom meetingRoom = null;
        Map<String, MeetingRoom> meetingRoomMap = mcuMeetingRoomMap.get(mcuId);
        if (meetingRoomMap != null) {
            meetingRoom = meetingRoomMap.get(meetingRoomId);
        }
        return meetingRoom;
    }

    public MeetingRoom getMeetingRoom(String meetingRoomId) {
        MeetingRoom meetingRoom = null;
        for (Long mcuId : mcuMeetingRoomMap.keySet()) {
            Map<String, MeetingRoom> meetingRoomMap = mcuMeetingRoomMap.get(mcuId);
            if (meetingRoomMap != null) {
                meetingRoom = meetingRoomMap.get(meetingRoomId);
                if (meetingRoom != null) {
                    break;
                }
            }
        }
        return meetingRoom;
    }

}
