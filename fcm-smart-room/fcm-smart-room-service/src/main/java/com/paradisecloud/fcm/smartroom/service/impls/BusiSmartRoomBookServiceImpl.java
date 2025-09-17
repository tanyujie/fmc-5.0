package com.paradisecloud.fcm.smartroom.service.impls;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.constant.DeptConstant;
import com.paradisecloud.fcm.common.enumer.RoomType;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomBookMapper;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomMapper;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomParticipantMapper;
import com.paradisecloud.fcm.dao.model.BusiSmartRoom;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomBook;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomParticipant;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomBookVo;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomCache;
import com.paradisecloud.fcm.smartroom.model.MeetingRoomInfo;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomBookService;
import com.paradisecloud.fcm.smartroom.task.SmartRoomNotifyTask;
import com.paradisecloud.fcm.smartroom.task.UpdateDoorplateForMeetingRoomBookTask;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.service.ISysUserService;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 会议室预约Service业务层处理
 *
 * @author lilinhai
 * @date 2024-01-26
 */
@Service
public class BusiSmartRoomBookServiceImpl implements IBusiSmartRoomBookService
{
    @Resource
    private BusiSmartRoomBookMapper busiSmartRoomBookMapper;
    @Resource
    private TaskService taskService;
    @Resource
    private ISysUserService sysUserService;
    @Resource
    private BusiSmartRoomParticipantMapper busiSmartRoomParticipantMapper;
    @Resource
    private BusiSmartRoomMapper busiSmartRoomMapper;

    /**
     * 查询会议室预约
     *
     * @param id 会议室预约ID
     * @return 会议室预约
     */
    @Override
    public BusiSmartRoomBook selectBusiSmartRoomBookById(Long id)
    {
        return busiSmartRoomBookMapper.selectBusiSmartRoomBookById(id);
    }

    /**
     * 查询会议室预约列表
     *
     * @param busiSmartRoomBookVo 会议室预约
     * @return 会议室预约
     */
    @Override
    public List<BusiSmartRoomBook> selectBusiSmartRoomBookList(BusiSmartRoomBookVo busiSmartRoomBookVo)
    {
        busiSmartRoomBookVo.setBookName(busiSmartRoomBookVo.getSearchKey());
        return busiSmartRoomBookMapper.selectBusiSmartRoomBookList(busiSmartRoomBookVo);
    }

    /**
     * 查询会议室预约列表
     *
     * @param roomId 会议室预约
     * @param currentTime
     * @return 会议室预约
     */
    @Override
    public List<BusiSmartRoomBook> selectBusiSmartRoomBookListForNext(Long roomId, Date currentTime)
    {
        return busiSmartRoomBookMapper.selectBusiSmartRoomBookListForNext(roomId, currentTime);
    }

    /**
     * 查询会议室预约列表
     *
     * @param roomId 会议室预约
     * @param currentTime
     * @param endTime
     * @return 会议室预约
     */
    @Override
    public List<BusiSmartRoomBook> selectBusiSmartRoomBookListForNextTerm(Long roomId, Date currentTime, Date endTime)
    {
        return busiSmartRoomBookMapper.selectBusiSmartRoomBookListForNextTerm(roomId, currentTime, endTime);
    }

    /**
     * 查询当前正在召开的会议
     *
     * @param roomId
     * @param currentTime
     * @return
     */
    @Override
    public BusiSmartRoomBook selectBusiSmartRoomBookForCurrent(Long roomId, Date currentTime) {
        return busiSmartRoomBookMapper.selectBusiSmartRoomBookForCurrent(roomId, currentTime);
    }

    /**
     * 新增会议室预约
     *
     * @param busiSmartRoomBook 会议室预约
     * @return 结果
     */
    @Override
    public int insertBusiSmartRoomBook(BusiSmartRoomBook busiSmartRoomBook)
    {
        Date currentTime = new Date();
        Date diffDate = DateUtils.getDiffDate(currentTime, -60, TimeUnit.SECONDS);
        Date startTime = busiSmartRoomBook.getStartTime();
        startTime = DateUtil.getDayOfClearSecond(startTime);
        Date endTime = busiSmartRoomBook.getEndTime();
        endTime = DateUtil.getDayOfClearSecond(endTime);
        if (startTime.after(endTime)) {
            throw new CustomException("开始时间和结束时间不正确！");
        }
        if (startTime.before(diffDate)) {
            throw new CustomException("不能预约过期的时间！");
        }
        Long diff = endTime.getTime() - startTime.getTime();
        if (diff > 3600000 * 12) {
            throw new CustomException("预约时长不能超过12小时！");
        }
        if (endTime.getMonth() - currentTime.getMonth() > 2) {
            throw new CustomException("预约时间只能当月和下月！");
        }
        if (busiSmartRoomBook.getRoomId() == null) {
            throw new CustomException("预约房间不存在！");
        }
        BusiSmartRoom busiSmartRoom = SmartRoomCache.getInstance().get(busiSmartRoomBook.getRoomId());
        if (busiSmartRoom == null) {
            throw new CustomException("预约房间不存在！");
        } else {
            if (busiSmartRoom.getUserId() == null) {
                SysUser sysUser = new SysUser();
                sysUser.setUserName("smartRomm_" + System.currentTimeMillis());
                sysUser.setNickName(busiSmartRoom.getRoomName());
                sysUser.setPassword(SecurityUtils.encryptPassword("tty@2021"));
                sysUser.setDeptId(DeptConstant.SMART_ROOM_DEPT_ID);
                int i = sysUserService.insertUser(sysUser);
                if (i > 0) {
                    busiSmartRoom.setUserId(sysUser.getUserId());
                    int i1 = busiSmartRoomMapper.updateBusiSmartRoom(busiSmartRoom);
                    if (i1 > 0) {
                        SmartRoomCache.getInstance().add(busiSmartRoom);
                    }
                }
            }
        }
        synchronized (busiSmartRoom.getId()) {
            Date startDiffDate = DateUtils.getDiffDate(busiSmartRoomBook.getStartTime(), +1, TimeUnit.SECONDS);
            Date endDiffDate = DateUtils.getDiffDate(busiSmartRoomBook.getEndTime(), -1, TimeUnit.SECONDS);
            BusiSmartRoomBook busiSmartRoomBookExist = busiSmartRoomBookMapper.selectBusiSmartRoomBookListForCheckExist(busiSmartRoomBook.getId(), busiSmartRoomBook.getRoomId(), startDiffDate, endDiffDate);
            if (busiSmartRoomBookExist != null) {
                throw new CustomException("预约期间与其它预约重合，请重新选择时间区间。");
            }

            busiSmartRoomBook.setStartTime(startTime);
            busiSmartRoomBook.setEndTime(endTime);
            busiSmartRoomBook.setCreateTime(new Date());
            LoginUser loginUser = null;
            try {
                loginUser = SecurityUtils.getLoginUser();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (loginUser == null) {
                loginUser = new LoginUser();
                SysUser sysUser = new SysUser();
                sysUser.setUserName("admin");
                loginUser.setUser(sysUser);
            }
            busiSmartRoomBook.setCreateBy(loginUser.getUsername());
            int i = busiSmartRoomBookMapper.insertBusiSmartRoomBook(busiSmartRoomBook);
            if (i > 0) {
                Map<String, Object> params = busiSmartRoomBook.getParams();
                if (params != null && params.size() > 0) {
                    if (params.containsKey("participants")) {
                        Object participants = params.get("participants");
                        if (participants != null) {
                            ArrayList<Map<String, Integer>> participantsJSON = (ArrayList<Map<String, Integer>>) participants;
                            for (Map<String, Integer> map : participantsJSON) {
                                if (map != null) {
                                    Long userId = Long.valueOf(map.get("userId"));
                                    if (userId != null) {
                                        SysUser sysUser = sysUserService.selectUserById(userId);
                                        if (sysUser != null) {
                                            BusiSmartRoomParticipant busiSmartRoomParticipant = new BusiSmartRoomParticipant();
                                            busiSmartRoomParticipant.setUserId(userId);
                                            busiSmartRoomParticipant.setBookId(busiSmartRoomBook.getId());
                                            busiSmartRoomParticipant.setUserName(sysUser.getNickName());
                                            busiSmartRoomParticipant.setCreateBy(loginUser.getUsername());
                                            busiSmartRoomParticipant.setCreateTime(new Date());
                                            String signInCode = SmartRoomCache.getInstance().generateSignInCode();
                                            busiSmartRoomParticipant.setSignInCode(signInCode);
                                            int i1 = busiSmartRoomParticipantMapper.insertBusiSmartRoomParticipant(busiSmartRoomParticipant);
                                            if (i1 > 0) {
                                                SmartRoomCache.getInstance().addSignInCode(busiSmartRoomParticipant.getSignInCode(), busiSmartRoomParticipant.getId());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Date dayStartTime = DateUtils.getDayStartTime(currentTime);
                Date dayEndTime = DateUtils.getDayEndTime(currentTime);
                if (dayStartTime.before(startTime) && dayEndTime.after(startTime) || dayStartTime.before(endTime) && dayEndTime.after(endTime)) {
                    // 更新缓存
                    updateMeetingRoomCache(busiSmartRoomBook.getRoomId());
                    // 推送消息给电子门牌
                    UpdateDoorplateForMeetingRoomBookTask updateDoorplateForMeetingRoomBookTask = new UpdateDoorplateForMeetingRoomBookTask(busiSmartRoomBook.getRoomId().toString(), 1000, busiSmartRoomBook.getRoomId());
                    taskService.addTask(updateDoorplateForMeetingRoomBookTask);
                }

                SmartRoomNotifyTask notifyTask = new SmartRoomNotifyTask(busiSmartRoomBook.getId().toString(), 1000, busiSmartRoomBook, busiSmartRoom, 1);
                BeanFactory.getBean(TaskService.class).addTask(notifyTask);
            }
            return i;
        }
    }

    /**
     * 修改会议室预约
     *
     * @param busiSmartRoomBook 会议室预约
     * @return 结果
     */
    @Override
    public int updateBusiSmartRoomBook(BusiSmartRoomBook busiSmartRoomBook)
    {
        Date currentTime = new Date();
        Date diffDate = DateUtils.getDiffDate(currentTime, -60, TimeUnit.SECONDS);
        Date startTime = busiSmartRoomBook.getStartTime();
        startTime = DateUtil.getDayOfClearSecond(startTime);
        Date endTime = busiSmartRoomBook.getEndTime();
        endTime = DateUtil.getDayOfClearSecond(endTime);
        if (startTime.after(endTime)) {
            throw new CustomException("开始时间和结束时间不正确！");
        }
        if (startTime.before(diffDate)) {
            throw new CustomException("不能预约过期的时间！");
        }
        Long diff = endTime.getTime() - startTime.getTime();
        if (diff > 3600000 * 12) {
            throw new CustomException("预约时长不能超过12小时！");
        }
        if (endTime.getMonth() - currentTime.getMonth() > 2) {
            throw new CustomException("预约时间只能当月和下月！");
        }
        BusiSmartRoomBook busiSmartRoomBookNeed = busiSmartRoomBookMapper.selectBusiSmartRoomBookById(busiSmartRoomBook.getId());
        if (busiSmartRoomBookNeed == null) {
            throw new CustomException("该预约不存在！请刷新后重试！");
        }
        if (busiSmartRoomBookNeed.getRoomId() == null) {
            throw new CustomException("预约房间不存在！");
        }
        BusiSmartRoom busiSmartRoom = SmartRoomCache.getInstance().get(busiSmartRoomBookNeed.getRoomId());
        if (busiSmartRoom == null) {
            throw new CustomException("预约房间不存在！");
        } else {
            if (busiSmartRoom.getUserId() == null) {
                SysUser sysUser = new SysUser();
                sysUser.setUserName("smartRomm_" + System.currentTimeMillis());
                sysUser.setNickName(busiSmartRoom.getRoomName());
                sysUser.setPassword(SecurityUtils.encryptPassword("tty@2021"));
                sysUser.setDeptId(DeptConstant.SMART_ROOM_DEPT_ID);
                int i = sysUserService.insertUser(sysUser);
                if (i > 0) {
                    busiSmartRoom.setUserId(sysUser.getUserId());
                    int i1 = busiSmartRoomMapper.updateBusiSmartRoom(busiSmartRoom);
                    if (i1 > 0) {
                        SmartRoomCache.getInstance().add(busiSmartRoom);
                    }
                }
            }
        }
        synchronized (busiSmartRoom.getId()) {
            Date startDiffDate = DateUtils.getDiffDate(busiSmartRoomBook.getStartTime(), +1, TimeUnit.SECONDS);
            Date endDiffDate = DateUtils.getDiffDate(busiSmartRoomBook.getEndTime(), -1, TimeUnit.SECONDS);
            BusiSmartRoomBook busiSmartRoomBookExist = busiSmartRoomBookMapper.selectBusiSmartRoomBookListForCheckExist(busiSmartRoomBook.getId(), busiSmartRoomBook.getRoomId(), startDiffDate, endDiffDate);
            if (busiSmartRoomBookExist != null) {
                throw new CustomException("预约期间与其它预约重合，请重新选择时间区间。");
            }
            BusiSmartRoomBook busiSmartRoomBookUpdate = new BusiSmartRoomBook();
            busiSmartRoomBookUpdate.setId(busiSmartRoomBook.getId());
            busiSmartRoomBookUpdate.setStartTime(startTime);
            busiSmartRoomBookUpdate.setEndTime(endTime);
            busiSmartRoomBookUpdate.setMcuType(busiSmartRoomBook.getMcuType());
            busiSmartRoomBookUpdate.setAppointmentConferenceId(busiSmartRoomBook.getAppointmentConferenceId());
            busiSmartRoomBookUpdate.setBookName(busiSmartRoomBook.getBookName());
            int i = busiSmartRoomBookMapper.updateBusiSmartRoomBook(busiSmartRoomBookUpdate);
            if (i > 0) {
                Set<Long> oldUserIdSet = new HashSet<>();
                BusiSmartRoomParticipant busiSmartRoomParticipant = new BusiSmartRoomParticipant();
                busiSmartRoomParticipant.setBookId(busiSmartRoomBook.getId());
                List<BusiSmartRoomParticipant> busiSmartRoomParticipantList = busiSmartRoomParticipantMapper.selectBusiSmartRoomParticipantList(busiSmartRoomParticipant);
                for (BusiSmartRoomParticipant smartRoomParticipant : busiSmartRoomParticipantList) {
                    if (smartRoomParticipant != null) {
                        oldUserIdSet.add(smartRoomParticipant.getId());
                    }
                }
                Map<String, Object> params = busiSmartRoomBook.getParams();
                if (params != null && params.size() > 0) {
                    if (params.containsKey("participants")) {
                        Object participants = params.get("participants");
                        if (participants != null) {
                            ArrayList<Map<String, Integer>> participantsJSON = (ArrayList<Map<String, Integer>>) participants;
                            for (Map<String, Integer> map : participantsJSON) {
                                if (map != null) {
                                    Long userId = Long.valueOf(map.get("userId"));
                                    if (userId != null) {
                                        SysUser sysUser = sysUserService.selectUserById(userId);
                                        if (sysUser != null) {
                                            Long userIdTemp = sysUser.getUserId();
                                            if (!oldUserIdSet.contains(userIdTemp)) {
                                                BusiSmartRoomParticipant busiSmartRoomParticipantTemp = new BusiSmartRoomParticipant();
                                                busiSmartRoomParticipantTemp.setUserId(userIdTemp);
                                                busiSmartRoomParticipantTemp.setBookId(busiSmartRoomBook.getId());
                                                busiSmartRoomParticipantTemp.setUserName(sysUser.getNickName());
                                                busiSmartRoomParticipantTemp.setCreateTime(new Date());
                                                String signInCode = SmartRoomCache.getInstance().generateSignInCode();
                                                busiSmartRoomParticipantTemp.setSignInCode(signInCode);
                                                LoginUser loginUser = SecurityUtils.getLoginUser();
                                                busiSmartRoomParticipantTemp.setCreateBy(loginUser.getUsername());
                                                int i1 = busiSmartRoomParticipantMapper.insertBusiSmartRoomParticipant(busiSmartRoomParticipantTemp);
                                                if (i1 > 0) {
                                                    SmartRoomCache.getInstance().addSignInCode(busiSmartRoomParticipantTemp.getSignInCode(), busiSmartRoomParticipantTemp.getId());
                                                }
                                            } else {
                                                oldUserIdSet.remove(userIdTemp);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (!oldUserIdSet.isEmpty()) {
                    for (Long userId : oldUserIdSet) {
                        busiSmartRoomParticipantMapper.deleteBusiSmartRoomParticipantById(userId);
                    }
                }
                Date dayStartTime = DateUtils.getDayStartTime(currentTime);
                Date dayEndTime = DateUtils.getDayEndTime(currentTime);
                if (dayStartTime.before(startTime) && dayEndTime.after(startTime) || dayStartTime.before(endTime) && dayEndTime.after(endTime)) {
                    // 更新缓存
                    updateMeetingRoomCache(busiSmartRoomBook.getRoomId());
                    // 推送消息给电子门牌
                    UpdateDoorplateForMeetingRoomBookTask updateDoorplateForMeetingRoomBookTask = new UpdateDoorplateForMeetingRoomBookTask(busiSmartRoomBook.getRoomId().toString(), 1000, busiSmartRoomBook.getRoomId());
                    taskService.addTask(updateDoorplateForMeetingRoomBookTask);
                }
                SmartRoomNotifyTask notifyTask = new SmartRoomNotifyTask(busiSmartRoomBook.getId().toString(), 1000, busiSmartRoomBook, busiSmartRoom, 1);
                BeanFactory.getBean(TaskService.class).addTask(notifyTask);
            }
            return i;
        }
    }

    /**
     * 批量取消会议室预约
     *
     * @param ids 需要删除的会议室预约ID
     * @return 结果
     */
    @Override
    public int cancelBusiSmartRoomBookByIds(Long[] ids)
    {
        int rows = 0;
        for (Long id : ids) {
            BusiSmartRoomBook busiSmartRoomBook = busiSmartRoomBookMapper.selectBusiSmartRoomBookById(id);
            if (busiSmartRoomBook != null) {
                busiSmartRoomBook.setBookStatus(1);
                int i = busiSmartRoomBookMapper.updateBusiSmartRoomBook(busiSmartRoomBook);
                if (i > 0) {
                    Set<Long> oldUserIdSet = new HashSet<>();
                    BusiSmartRoomParticipant busiSmartRoomParticipant = new BusiSmartRoomParticipant();
                    busiSmartRoomParticipant.setBookId(busiSmartRoomBook.getId());
                    List<BusiSmartRoomParticipant> busiSmartRoomParticipantList = busiSmartRoomParticipantMapper.selectBusiSmartRoomParticipantList(busiSmartRoomParticipant);
                    for (BusiSmartRoomParticipant smartRoomParticipant : busiSmartRoomParticipantList) {
                        if (smartRoomParticipant != null) {
                            oldUserIdSet.add(smartRoomParticipant.getId());
                        }
                    }
                    if (!oldUserIdSet.isEmpty()) {
                        for (Long userId : oldUserIdSet) {
                            BusiSmartRoomParticipant busiSmartRoomParticipantTemp = new BusiSmartRoomParticipant();
                            busiSmartRoomParticipantTemp.setBookId(id);
                            busiSmartRoomParticipantTemp.setUserId(userId);
                            List<BusiSmartRoomParticipant> busiSmartRoomParticipantListTemp = busiSmartRoomParticipantMapper.selectBusiSmartRoomParticipantList(busiSmartRoomParticipantTemp);
                            if (busiSmartRoomParticipantListTemp != null && busiSmartRoomParticipantListTemp.size() > 0) {
                                BusiSmartRoomParticipant smartRoomParticipant = busiSmartRoomParticipantListTemp.get(0);
                                smartRoomParticipant.setSignInCode(null);
                                busiSmartRoomParticipantMapper.updateBusiSmartRoomParticipant(smartRoomParticipant);
                            }
                        }
                    }
                    Date currentTime = new Date();
                    Date startTime = busiSmartRoomBook.getStartTime();
                    Date endTime = busiSmartRoomBook.getEndTime();
                    Date dayStartTime = DateUtils.getDayStartTime(currentTime);
                    Date dayEndTime = DateUtils.getDayEndTime(currentTime);
                    if (dayStartTime.before(startTime) && dayEndTime.after(startTime) || dayStartTime.before(endTime) && dayEndTime.after(endTime)) {
                        // 更新缓存
                        updateMeetingRoomCache(busiSmartRoomBook.getRoomId());
                        // 推送消息给电子门牌
                        UpdateDoorplateForMeetingRoomBookTask updateDoorplateForMeetingRoomBookTask = new UpdateDoorplateForMeetingRoomBookTask(busiSmartRoomBook.getRoomId().toString(), 1000, busiSmartRoomBook.getRoomId());
                        taskService.addTask(updateDoorplateForMeetingRoomBookTask);
                    }
                    rows++;
                    BusiSmartRoom busiSmartRoom = SmartRoomCache.getInstance().get(busiSmartRoomBook.getRoomId());
                    SmartRoomNotifyTask notifyTask = new SmartRoomNotifyTask(busiSmartRoomBook.getId().toString(), 1000, busiSmartRoomBook, busiSmartRoom, 2);
                    BeanFactory.getBean(TaskService.class).addTask(notifyTask);
                }
            }
        }
        return rows;
    }

    /**
     * 取消会议室预约信息
     *
     * @param id 会议室预约ID
     * @return 结果
     */
    @Override
    public int cancelBusiSmartRoomBookById(Long id)
    {
        int i = 0;
        BusiSmartRoomBook busiSmartRoomBook = busiSmartRoomBookMapper.selectBusiSmartRoomBookById(id);
        if (busiSmartRoomBook != null) {
            if (busiSmartRoomBook.getStartTime().getTime() <= System.currentTimeMillis()) {
                throw new CustomException("会议室正在使用中，无法取消！");
            }
            busiSmartRoomBook.setBookStatus(1);
            i = busiSmartRoomBookMapper.updateBusiSmartRoomBook(busiSmartRoomBook);
            if (i > 0) {
                Set<Long> oldUserIdSet = new HashSet<>();
                BusiSmartRoomParticipant busiSmartRoomParticipant = new BusiSmartRoomParticipant();
                busiSmartRoomParticipant.setBookId(busiSmartRoomBook.getId());
                List<BusiSmartRoomParticipant> busiSmartRoomParticipantList = busiSmartRoomParticipantMapper.selectBusiSmartRoomParticipantList(busiSmartRoomParticipant);
                for (BusiSmartRoomParticipant smartRoomParticipant : busiSmartRoomParticipantList) {
                    if (smartRoomParticipant != null) {
                        oldUserIdSet.add(smartRoomParticipant.getId());
                    }
                }
                if (!oldUserIdSet.isEmpty()) {
                    for (Long userId : oldUserIdSet) {
                        BusiSmartRoomParticipant busiSmartRoomParticipantTemp = new BusiSmartRoomParticipant();
                        busiSmartRoomParticipantTemp.setBookId(id);
                        busiSmartRoomParticipantTemp.setUserId(userId);
                        List<BusiSmartRoomParticipant> busiSmartRoomParticipantListTemp = busiSmartRoomParticipantMapper.selectBusiSmartRoomParticipantList(busiSmartRoomParticipantTemp);
                        if (busiSmartRoomParticipantListTemp != null && busiSmartRoomParticipantListTemp.size() > 0) {
                            BusiSmartRoomParticipant smartRoomParticipant = busiSmartRoomParticipantListTemp.get(0);
                            smartRoomParticipant.setSignInCode(null);
                            busiSmartRoomParticipantMapper.updateBusiSmartRoomParticipant(smartRoomParticipant);
                        }
                    }
                }
                Date currentTime = new Date();
                Date startTime = busiSmartRoomBook.getStartTime();
                Date endTime = busiSmartRoomBook.getEndTime();
                Date dayStartTime = DateUtils.getDayStartTime(currentTime);
                Date dayEndTime = DateUtils.getDayEndTime(currentTime);
                if (dayStartTime.before(startTime) && dayEndTime.after(startTime) || dayStartTime.before(endTime) && dayEndTime.after(endTime)) {
                    // 更新缓存
                    updateMeetingRoomCache(busiSmartRoomBook.getRoomId());
                    // 推送消息给电子门牌
                    UpdateDoorplateForMeetingRoomBookTask updateDoorplateForMeetingRoomBookTask = new UpdateDoorplateForMeetingRoomBookTask(busiSmartRoomBook.getRoomId().toString(), 1000, busiSmartRoomBook.getRoomId());
                    taskService.addTask(updateDoorplateForMeetingRoomBookTask);
                }
                BusiSmartRoom busiSmartRoom = SmartRoomCache.getInstance().get(busiSmartRoomBook.getRoomId());
                SmartRoomNotifyTask notifyTask = new SmartRoomNotifyTask(busiSmartRoomBook.getId().toString(), 1000, busiSmartRoomBook, busiSmartRoom, 2);
                BeanFactory.getBean(TaskService.class).addTask(notifyTask);
            }
        }
        return i;
    }

    /**
     * 结束使用会议室预约信息
     *
     * @param id 会议室预约ID
     * @return 结果
     */
    @Override
    public int endBusiSmartRoomBookById(Long id)
    {
        int i = 0;
        BusiSmartRoomBook busiSmartRoomBook = busiSmartRoomBookMapper.selectBusiSmartRoomBookById(id);
        if (busiSmartRoomBook != null) {
            if (busiSmartRoomBook.getStartTime().getTime() > System.currentTimeMillis()) {
                throw new CustomException("会议室还未还是使用，无法结束使用！");
            }
            Date date = new Date();
            int seconds = date.getSeconds();
            if (seconds > 0) {
//                date = DateUtils.getDiffDate(date, +1, TimeUnit.MINUTES);
                date = DateUtil.getDayOfClearSecond(date);
            }
            busiSmartRoomBook.setBookStatus(2);
            if (date.before(busiSmartRoomBook.getEndTime())) {
                busiSmartRoomBook.setEndTime(date);
            }
            i = busiSmartRoomBookMapper.updateBusiSmartRoomBook(busiSmartRoomBook);
            if (i > 0) {

                BusiSmartRoomParticipant busiSmartRoomParticipant = new BusiSmartRoomParticipant();
                busiSmartRoomParticipant.setBookId(id);
                List<BusiSmartRoomParticipant> busiSmartRoomParticipantList = busiSmartRoomParticipantMapper.selectBusiSmartRoomParticipantList(busiSmartRoomParticipant);
                if (busiSmartRoomParticipantList != null && busiSmartRoomParticipantList.size() > 0) {
                    for (BusiSmartRoomParticipant smartRoomParticipant : busiSmartRoomParticipantList) {
                        smartRoomParticipant.setSignInCode(null);
                        smartRoomParticipant.setUpdateTime(new Date());
                        busiSmartRoomParticipantMapper.updateBusiSmartRoomParticipant(smartRoomParticipant);
                    }
                }

                Date currentTime = new Date();
                Date startTime = busiSmartRoomBook.getStartTime();
                Date endTime = busiSmartRoomBook.getEndTime();
                Date dayStartTime = DateUtils.getDayStartTime(currentTime);
                Date dayEndTime = DateUtils.getDayEndTime(currentTime);
                if (dayStartTime.before(startTime) && dayEndTime.after(startTime) || dayStartTime.before(endTime) && dayEndTime.after(endTime)) {
                    // 更新缓存
                    updateMeetingRoomCache(busiSmartRoomBook.getRoomId());
                    // 推送消息给电子门牌
                    UpdateDoorplateForMeetingRoomBookTask updateDoorplateForMeetingRoomBookTask = new UpdateDoorplateForMeetingRoomBookTask(busiSmartRoomBook.getRoomId().toString(), 1000, busiSmartRoomBook.getRoomId());
                    taskService.addTask(updateDoorplateForMeetingRoomBookTask);
                }
            }
        }
        return i;
    }

    /**
     * 获取会议室期间预约日预约列表
     *
     * @param roomId
     * @param yearMonth 月份
     * @return
     */
    @Override
    public List<Map<String, Object>> selectBusiSmartRoomBookListForRoomTerm(Long roomId, Date yearMonth) {
        Date startTime = DateUtil.getFirstDayOfMonth(yearMonth);
        Date endTime = DateUtils.getDayEndTime(DateUtil.getLastDayOfMonth(yearMonth));
        List<Map<String, Object>> dayList = new ArrayList<>();
        TreeMap<Date, List<BusiSmartRoomBook>> dayMap = new TreeMap<>();
        for (Date date = startTime; !date.after(endTime);) {
            List<BusiSmartRoomBook> bookList = new ArrayList<>();
            dayMap.put(date, bookList);
            date = DateUtils.getDiffDate(date, 1, TimeUnit.DAYS);
        }
        Map<String, SysUser> userMap = new HashMap<>();
        List<BusiSmartRoomBook> busiSmartRoomBookList = busiSmartRoomBookMapper.selectBusiSmartRoomBookListForRoomTerm(roomId, startTime, endTime);
        for (BusiSmartRoomBook busiSmartRoomBook : busiSmartRoomBookList) {
            Date startDate = DateUtils.getDayStartTime(busiSmartRoomBook.getStartTime());
            Date endDate = DateUtils.getDayStartTime(busiSmartRoomBook.getEndTime());
            if (!startDate.before(startTime) && !startDate.after(endTime)) {
                List<BusiSmartRoomBook> bookList = dayMap.get(startDate);
                if (bookList != null) {
                    bookList.add(busiSmartRoomBook);
                }
            }
            if (!endDate.before(startTime) && !endDate.after(endTime) && !startDate.equals(endDate)) {
                List<BusiSmartRoomBook> bookList = dayMap.get(endDate);
                if (bookList != null) {
                    bookList.add(busiSmartRoomBook);
                }
            }
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
        Date currentTime = new Date();
        MeetingRoomInfo meetingRoomInfoTemp = new MeetingRoomInfo();
        SortedMap<Date, List<BusiSmartRoomBook>> dateListSortedMap = dayMap.tailMap(new Date(0));
        for (Date date : dateListSortedMap.keySet()) {
            List<BusiSmartRoomBook> allList = dateListSortedMap.get(date);
            meetingRoomInfoTemp.setAllList(allList, date);
            MeetingRoomInfo meetingRoomInfo = meetingRoomInfoTemp.getMeetingRoomInfoForWeb(currentTime);
            Map<String, Object> map = new HashMap<>();
            map.put("dateDay", date);
            map.put("allList", meetingRoomInfo.getAllList());
            map.put("status", meetingRoomInfo.getStatus());
            String weekDay = DateUtil.convertDateToString(date, "EEE", Locale.CHINA).replace("星期", "").replace("周", "");
            map.put("weekDay" ,weekDay);
            dayList.add(map);
        }

        BusiSmartRoomBook terminalBook = SmartRoomCache.getInstance().getTerminalBook(roomId);
        if (terminalBook != null) {
            for (Map<String, Object> map : dayList) {
                Date dateDay = (Date) map.get("dateDay");
                Date dayStartTime = DateUtils.getDayStartTime(new Date());
                if (dayStartTime.compareTo(dateDay) == 0) {
                    List<BusiSmartRoomBook> allList = (List<BusiSmartRoomBook>) map.get("allList");
                    allList.add(terminalBook);
                    return dayList;
                }
            }
        }

        return dayList;
    }

    /**
     * 获取会议室预约日预约列表
     *
     * @param roomId
     * @param dateDay 日
     * @return
     */
    @Override
    public List<BusiSmartRoomBook> selectBusiSmartRoomBookListForRoomDay(Long roomId, Date dateDay) {
        Date currentTime = new Date();
        Date today = DateUtils.getDayStartTime(currentTime);
        Date startTime = DateUtils.getDayStartTime(dateDay);
        Date endTime = DateUtils.getDayEndTime(dateDay);
        if (startTime.equals(today)) {
            MeetingRoomInfo meetingRoomInfo = SmartRoomCache.getInstance().getMeetingRoomInfoForWeb(roomId, currentTime);
            return meetingRoomInfo.getAllList();
        } else {
            List<BusiSmartRoomBook> busiSmartRoomBookList = busiSmartRoomBookMapper.selectBusiSmartRoomBookListForRoomTerm(roomId, startTime, endTime);
            for (BusiSmartRoomBook busiSmartRoomBook : busiSmartRoomBookList) {
                if (startTime.before(today)) {
                    busiSmartRoomBook.setBookStatus(4); // 结束
                }
                if (startTime.after(today)) {
                    busiSmartRoomBook.setBookStatus(1); // 未开始
                }
                if (startTime.after(currentTime) && startTime.getTime() <= currentTime.getTime() + 600000) {
                    busiSmartRoomBook.setBookStatus(2);// 即将开始
                }
            }
            return busiSmartRoomBookList;
        }
    }

    /**
     * 延长预约时间
     * @param id
     * @param minutes
     * @return
     */
    @Override
    public int extendMinutes(Long id, Integer minutes) {
        BusiSmartRoomBook busiSmartRoomBook = busiSmartRoomBookMapper.selectBusiSmartRoomBookById(id);
        int i = 0;
        if (busiSmartRoomBook != null) {
            busiSmartRoomBook.setExtendMinutes(minutes);
            Date endTimeTemp = DateUtils.getDiffDate(busiSmartRoomBook.getEndTime(), minutes, TimeUnit.MINUTES);
            busiSmartRoomBook.setEndTime(endTimeTemp);

            Date currentTime = new Date();
            Date startTime = busiSmartRoomBook.getStartTime();
            startTime = DateUtil.getDayOfClearSecond(startTime);
            Date endTime = busiSmartRoomBook.getEndTime();
            endTime = DateUtil.getDayOfClearSecond(endTime);
            Long diff = endTime.getTime() - startTime.getTime();
            if (diff > 3600000 * 24) {
                throw new CustomException("预约时长不能超过24小时！");
            }
            if (endTime.getMonth() - currentTime.getMonth() > 2) {
                throw new CustomException("预约时间只能当月和下月！");
            }
            Date startDiffDate = DateUtils.getDiffDate(busiSmartRoomBook.getStartTime(), +1, TimeUnit.SECONDS);
            Date endDiffDate = DateUtils.getDiffDate(busiSmartRoomBook.getEndTime(), -1, TimeUnit.SECONDS);
            BusiSmartRoomBook busiSmartRoomBookExist = busiSmartRoomBookMapper.selectBusiSmartRoomBookListForCheckExist(busiSmartRoomBook.getId(), busiSmartRoomBook.getRoomId(), startDiffDate, endDiffDate);
            if (busiSmartRoomBookExist != null) {
                throw new CustomException("延长后预约期间与其它预约重合。");
            }
            busiSmartRoomBook.setStartTime(startTime);
            busiSmartRoomBook.setEndTime(endTime);
            i = busiSmartRoomBookMapper.updateBusiSmartRoomBook(busiSmartRoomBook);
            if (i > 0) {
                Date dayStartTime = DateUtils.getDayStartTime(currentTime);
                Date dayEndTime = DateUtils.getDayEndTime(currentTime);
                if (dayStartTime.before(startTime) && dayEndTime.after(startTime) || dayStartTime.before(endTime) && dayEndTime.after(endTime)) {
                    // 更新缓存
                    updateMeetingRoomCache(busiSmartRoomBook.getRoomId());
                    // 推送消息给电子门牌
                    UpdateDoorplateForMeetingRoomBookTask updateDoorplateForMeetingRoomBookTask = new UpdateDoorplateForMeetingRoomBookTask(busiSmartRoomBook.getRoomId().toString(), 1000, busiSmartRoomBook.getRoomId());
                    taskService.addTask(updateDoorplateForMeetingRoomBookTask);
                }
            }
            BusiSmartRoom busiSmartRoom = SmartRoomCache.getInstance().get(busiSmartRoomBook.getRoomId());
            SmartRoomNotifyTask notifyTask = new SmartRoomNotifyTask(busiSmartRoomBook.getId().toString(), 1000, busiSmartRoomBook, busiSmartRoom, 1);
            BeanFactory.getBean(TaskService.class).addTask(notifyTask);
        }
        return i;
    }

    @Override
    public int updateBusiSmartRoomBookData(BusiSmartRoomBook busiSmartRoomBook) {
        int i = busiSmartRoomBookMapper.updateBusiSmartRoomBook(busiSmartRoomBook);
        if (i > 0) {
            updateMeetingRoomCache(busiSmartRoomBook.getRoomId());
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
    }

}
