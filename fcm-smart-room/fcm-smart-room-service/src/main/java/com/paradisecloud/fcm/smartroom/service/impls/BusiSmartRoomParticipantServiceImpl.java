package com.paradisecloud.fcm.smartroom.service.impls;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomBookMapper;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomParticipantMapper;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomBook;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomParticipant;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomCache;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomParticipantService;
import com.paradisecloud.fcm.smartroom.task.UpdateDoorplateForMeetingRoomBookTask;
import com.sinhy.utils.DateUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 智慧办公房间参与人Service业务层处理
 *
 * @author lilinhai
 * @date 2024-04-07
 */
@Service
public class BusiSmartRoomParticipantServiceImpl implements IBusiSmartRoomParticipantService
{
    @Resource
    private BusiSmartRoomParticipantMapper busiSmartRoomParticipantMapper;
    @Resource
    private BusiSmartRoomBookMapper busiSmartRoomBookMapper;
    @Resource
    private TaskService taskService;

    /**
     * 查询智慧办公房间参与人
     *
     * @param id 智慧办公房间参与人ID
     * @return 智慧办公房间参与人
     */
    @Override
    public BusiSmartRoomParticipant selectBusiSmartRoomParticipantById(Long id)
    {
        return busiSmartRoomParticipantMapper.selectBusiSmartRoomParticipantById(id);
    }

    /**
     * 查询智慧办公房间参与人列表
     *
     * @param busiSmartRoomParticipant 智慧办公房间参与人
     * @return 智慧办公房间参与人
     */
    @Override
    public List<BusiSmartRoomParticipant> selectBusiSmartRoomParticipantList(BusiSmartRoomParticipant busiSmartRoomParticipant)
    {
        return busiSmartRoomParticipantMapper.selectBusiSmartRoomParticipantList(busiSmartRoomParticipant);
    }

    /**
     * 新增智慧办公房间参与人
     *
     * @param busiSmartRoomParticipant 智慧办公房间参与人
     * @return 结果
     */
    @Override
    public int insertBusiSmartRoomParticipant(BusiSmartRoomParticipant busiSmartRoomParticipant)
    {
        busiSmartRoomParticipant.setCreateTime(new Date());
        return busiSmartRoomParticipantMapper.insertBusiSmartRoomParticipant(busiSmartRoomParticipant);
    }

    /**
     * 修改智慧办公房间参与人
     *
     * @param busiSmartRoomParticipant 智慧办公房间参与人
     * @return 结果
     */
    @Override
    public int updateBusiSmartRoomParticipant(BusiSmartRoomParticipant busiSmartRoomParticipant)
    {
        busiSmartRoomParticipant.setUpdateTime(new Date());
        return busiSmartRoomParticipantMapper.updateBusiSmartRoomParticipant(busiSmartRoomParticipant);
    }

    /**
     * 批量删除智慧办公房间参与人
     *
     * @param ids 需要删除的智慧办公房间参与人ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmartRoomParticipantByIds(Long[] ids)
    {
        return busiSmartRoomParticipantMapper.deleteBusiSmartRoomParticipantByIds(ids);
    }

    /**
     * 删除智慧办公房间参与人信息
     *
     * @param id 智慧办公房间参与人ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmartRoomParticipantById(Long id)
    {
        return busiSmartRoomParticipantMapper.deleteBusiSmartRoomParticipantById(id);
    }

    /**
     * 预约签到
     * @param busiSmartRoomParticipant
     * @return
     */
    @Override
    public int signIn(BusiSmartRoomParticipant busiSmartRoomParticipant) {
        int i = 0;
        Long bookId = busiSmartRoomParticipant.getBookId();
        if (bookId == null) {
            throw new CustomException("预约ID不能为空！");
        }
        busiSmartRoomParticipant.setCreateTime(new Date());
        BusiSmartRoomBook busiSmartRoomBook = busiSmartRoomBookMapper.selectBusiSmartRoomBookById(bookId);
        if (busiSmartRoomBook != null) {
            Date startTime = busiSmartRoomBook.getStartTime();
            Date diffDate = DateUtils.getDiffDate(startTime, -10, TimeUnit.MINUTES);
            if (!(busiSmartRoomParticipant.getCreateTime().after(diffDate) && busiSmartRoomParticipant.getCreateTime().before(busiSmartRoomBook.getEndTime()))) {
                throw new CustomException("签到失败，预约已过期！");
            }
        } else {
            throw new CustomException("签到失败，预约不存在！");
        }
        Long id = SmartRoomCache.getInstance().getParticipantIdBySignInCode(busiSmartRoomParticipant.getSignInCode());
        if (id != null) {
            BusiSmartRoomParticipant busiSmartRoomParticipantById = busiSmartRoomParticipantMapper.selectBusiSmartRoomParticipantById(id);
            if (busiSmartRoomParticipantById != null) {
                busiSmartRoomParticipantById.setSignInTime(new Date());
                i = busiSmartRoomParticipantMapper.updateBusiSmartRoomParticipant(busiSmartRoomParticipantById);
                if (i > 0) {
                    Date currentTime = new Date();
                    Date startTime = busiSmartRoomBook.getStartTime();
                    Date endTime = busiSmartRoomBook.getEndTime();
                    Date dayStartTime = DateUtils.getDayStartTime(currentTime);
                    Date dayEndTime = DateUtils.getDayEndTime(currentTime);
                    if (dayStartTime.before(startTime) && dayEndTime.after(startTime) || dayStartTime.before(endTime) && dayEndTime.after(endTime)) {
                        // 更新缓存
                        // 推送消息给电子门牌
                        UpdateDoorplateForMeetingRoomBookTask updateDoorplateForMeetingRoomBookTask = new UpdateDoorplateForMeetingRoomBookTask(busiSmartRoomBook.getRoomId().toString(), 1000, busiSmartRoomBook.getRoomId());
                        taskService.addTask(updateDoorplateForMeetingRoomBookTask);
                    }
                }

            }
        } else {
            throw new CustomException("签到失败！");
        }
        return i;

    }
}
