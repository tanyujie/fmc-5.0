package com.paradisecloud.fcm.smartroom.model;

import com.paradisecloud.common.utils.bean.BeanUtils;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomBook;
import com.sinhy.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 房间信息
 */
public class MeetingRoomInfo {

    // 房间ID
    private long id;
    // 房间名
    private String roomName;
    // 地点
    private String position;
    /**
     * 状态 0：停用中 1：空闲中 2：即将使用 3：使用中
     */
    private int status;
    // 进行中的预约
    private BusiSmartRoomBook current;
    // 即将开始的预约
    private BusiSmartRoomBook toBegin;
    // 当天已结束的预定列表
    private List<BusiSmartRoomBook> preList;
    // 当天未开始的预定列表
    private List<BusiSmartRoomBook> nextList;
    // 当天所有的预定列表
    private List<BusiSmartRoomBook> allList;
    // 当天时间
    private Date today;
    // 当前时间
    private long currentTime = 0;
    // 最后推送时间
    private long lastPushTime = 0;
    // 状态开始时间
    private long statusStartTime = 0;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public BusiSmartRoomBook getCurrent() {
        return current;
    }

    public void setCurrent(BusiSmartRoomBook current) {
        this.current = current;
    }

    public BusiSmartRoomBook getToBegin() {
        return toBegin;
    }

    public void setToBegin(BusiSmartRoomBook toBegin) {
        this.toBegin = toBegin;
    }

    public List<BusiSmartRoomBook> getPreList() {
        return preList;
    }

    public void setPreList(List<BusiSmartRoomBook> preList) {
        this.preList = preList;
    }

    public List<BusiSmartRoomBook> getNextList() {
        return nextList;
    }

    public void setNextList(List<BusiSmartRoomBook> nextList) {
        this.nextList = nextList;
    }

    public void setAllList(List<BusiSmartRoomBook> allList, Date currentTime) {
        this.allList = allList;
        this.today = DateUtils.getDayStartTime(currentTime);
    }

    public List<BusiSmartRoomBook> getAllList() {
        return allList;
    }

    public Date getToday() {
        return today;
    }

    public void setToday(Date today) {
        this.today = today;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public long getLastPushTime() {
        return lastPushTime;
    }

    public void setLastPushTime(long lastPushTime) {
        this.lastPushTime = lastPushTime;
    }

    public long getStatusStartTime() {
        return statusStartTime;
    }

    public void setStatusStartTime(long statusStartTime) {
        this.statusStartTime = statusStartTime;
    }

    public MeetingRoomInfo getMeetingRoomInfo(Date currentTime) {
        MeetingRoomInfo meetingRoomInfo = new MeetingRoomInfo();
        meetingRoomInfo.setId(id);
        meetingRoomInfo.setRoomName(roomName);
        meetingRoomInfo.setPosition(position);
        int status = 1;
        List<BusiSmartRoomBook> preList = new ArrayList<>();
        List<BusiSmartRoomBook> nextList = new ArrayList<>();
        BusiSmartRoomBook current = null;
        BusiSmartRoomBook toBegin = null;
        if (allList != null && allList.size() > 0) {
            for (BusiSmartRoomBook busiSmartRoomBook : allList) {
                Date startTime = busiSmartRoomBook.getStartTime();
                Date endTime = busiSmartRoomBook.getEndTime();
                if (endTime.before(currentTime)) {
                    preList.add(busiSmartRoomBook);
                }
                if (startTime.after(currentTime)) {
                    nextList.add(busiSmartRoomBook);
                }
                if (startTime.after(currentTime) && startTime.getTime() <= currentTime.getTime() + 600000) {
                    toBegin = busiSmartRoomBook;
                }
                if (startTime.before(currentTime) && endTime.after(currentTime)) {
                    current = busiSmartRoomBook;
                }
            }
        }
        meetingRoomInfo.setPreList(preList);
        meetingRoomInfo.setNextList(nextList);
        meetingRoomInfo.setCurrent(current);
        meetingRoomInfo.setToBegin(toBegin);
        meetingRoomInfo.setToday(today);
        meetingRoomInfo.setCurrentTime(currentTime.getTime());
        meetingRoomInfo.setLastPushTime(lastPushTime);
        if (current != null) {
            status = 3;
            meetingRoomInfo.setStatusStartTime(current.getStartTime().getTime());
        } else if (toBegin != null) {
            status = 2;
            meetingRoomInfo.setStatusStartTime(toBegin.getStartTime().getTime() - 600000);
        } else {
            if (preList.size() > 0) {
                BusiSmartRoomBook last = preList.get(preList.size() - 1);
                meetingRoomInfo.setStatusStartTime(last.getEndTime().getTime());
            } else {
                meetingRoomInfo.setStatusStartTime(DateUtils.getDayStartTime(today).getTime());
            }
        }
        meetingRoomInfo.setStatus(status);
        return meetingRoomInfo;
    }

    public MeetingRoomInfo getMeetingRoomInfoForWeb(Date currentTime) {
        MeetingRoomInfo meetingRoomInfo = new MeetingRoomInfo();
        meetingRoomInfo.setId(id);
        meetingRoomInfo.setRoomName(roomName);
        meetingRoomInfo.setPosition(position);
        int status = 1;
        BusiSmartRoomBook lastPre = null;
        BusiSmartRoomBook current = null;
        BusiSmartRoomBook toBegin = null;
        List<BusiSmartRoomBook> allListWeb = new ArrayList<>();
        if (allList != null && allList.size() > 0) {
            for (BusiSmartRoomBook busiSmartRoomBook : allList) {
                BusiSmartRoomBook busiSmartRoomBookTemp = new BusiSmartRoomBook();
                BeanUtils.copyBeanProp(busiSmartRoomBookTemp, busiSmartRoomBook);
                Date startTime = busiSmartRoomBookTemp.getStartTime();
                Date endTime = busiSmartRoomBookTemp.getEndTime();
                if (endTime.before(currentTime)) {
                    busiSmartRoomBookTemp.setBookStatus(4);// 结束
                    lastPre = busiSmartRoomBookTemp;
                }
                if (startTime.after(currentTime)) {
                    busiSmartRoomBookTemp.setBookStatus(1);// 未开始
                }
                if (startTime.after(currentTime) && startTime.getTime() <= currentTime.getTime() + 600000) {
                    busiSmartRoomBookTemp.setBookStatus(2);// 即将开始
                    toBegin = busiSmartRoomBookTemp;
                }
                if (startTime.before(currentTime) && endTime.after(currentTime)) {
                    busiSmartRoomBookTemp.setBookStatus(3); // 进行中
                    current = busiSmartRoomBookTemp;
                }
                allListWeb.add(busiSmartRoomBookTemp);
            }
        }
        meetingRoomInfo.setAllList(allListWeb, currentTime);
        meetingRoomInfo.setCurrentTime(currentTime.getTime());
        if (current != null) {
            status = 3;
            meetingRoomInfo.setStatusStartTime(current.getStartTime().getTime());
        } else if (toBegin != null) {
            status = 2;
            meetingRoomInfo.setStatusStartTime(toBegin.getStartTime().getTime() - 600000);
        } else {
            if (lastPre != null) {
                meetingRoomInfo.setStatusStartTime(lastPre.getEndTime().getTime());
            } else {
                meetingRoomInfo.setStatusStartTime(DateUtils.getDayStartTime(today).getTime());
            }
        }
        meetingRoomInfo.setStatus(status);
        return meetingRoomInfo;
    }

    public void clear() {
        if (allList != null) {
            allList.clear();
        }
    }
}
