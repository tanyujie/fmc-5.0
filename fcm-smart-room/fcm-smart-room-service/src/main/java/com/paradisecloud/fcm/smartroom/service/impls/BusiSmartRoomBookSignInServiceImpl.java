package com.paradisecloud.fcm.smartroom.service.impls;

import java.util.List;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomBookMapper;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomBookSignInMapper;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomBook;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomBookSignIn;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomBookSignInVo;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomBookSignInService;
import com.paradisecloud.fcm.smartroom.task.UpdateDoorplateForMeetingRoomBookTask;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.service.ISysUserService;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 房间预约签到Service业务层处理
 *
 * @author lilinhai
 * @date 2024-03-22
 */
@Service
public class BusiSmartRoomBookSignInServiceImpl implements IBusiSmartRoomBookSignInService
{
    @Resource
    private BusiSmartRoomBookSignInMapper busiSmartRoomBookSignInMapper;
    @Resource
    private BusiSmartRoomBookMapper busiSmartRoomBookMapper;
    @Resource
    private TaskService taskService;

    /**
     * 查询房间预约签到
     *
     * @param id 房间预约签到ID
     * @return 房间预约签到
     */
    @Override
    public BusiSmartRoomBookSignIn selectBusiSmartRoomBookSignInById(Long id)
    {
        BusiSmartRoomBookSignIn busiSmartRoomBookSignIn = busiSmartRoomBookSignInMapper.selectBusiSmartRoomBookSignInById(id);
        Long userId = busiSmartRoomBookSignIn.getUserId();
        String deptName = "";
        if (userId != null) {
            ISysUserService sysUserService = BeanFactory.getBean(ISysUserService.class);
            SysUser sysUser = sysUserService.selectUserById(userId);
            if (sysUser != null && sysUser.getDept() != null) {
                SysDept dept = sysUser.getDept();
                if (dept != null && StringUtils.isNotEmpty(dept.getDeptName())) {
                    deptName = dept.getDeptName();
                }
            }
        }
        busiSmartRoomBookSignIn.getParams().put("deptName", deptName);
        return busiSmartRoomBookSignIn;
    }

    /**
     * 查询房间预约签到列表
     *
     * @param busiSmartRoomBookSignIn 房间预约签到
     * @return 房间预约签到
     */
    @Override
    public List<BusiSmartRoomBookSignIn> selectBusiSmartRoomBookSignInList(BusiSmartRoomBookSignInVo busiSmartRoomBookSignIn)
    {
        List<BusiSmartRoomBookSignIn> busiSmartRoomBookSignIns = busiSmartRoomBookSignInMapper.selectBusiSmartRoomBookSignInList(busiSmartRoomBookSignIn);
        for (BusiSmartRoomBookSignIn smartRoomBookSignIn : busiSmartRoomBookSignIns) {
            Long userId = smartRoomBookSignIn.getUserId();
            String deptName = "";
            if (userId != null) {
                ISysUserService sysUserService = BeanFactory.getBean(ISysUserService.class);
                SysUser sysUser = sysUserService.selectUserById(userId);
                if (sysUser != null && sysUser.getDept() != null) {
                    SysDept dept = sysUser.getDept();
                    if (dept != null && StringUtils.isNotEmpty(dept.getDeptName())) {
                        deptName = dept.getDeptName();
                    }
                }
            }
            smartRoomBookSignIn.getParams().put("deptName", deptName);
        }
        return busiSmartRoomBookSignIns;
    }

    /**
     * 新增房间预约签到
     *
     * @param busiSmartRoomBookSignIn 房间预约签到
     * @return 结果
     */
    @Override
    public int insertBusiSmartRoomBookSignIn(BusiSmartRoomBookSignIn busiSmartRoomBookSignIn)
    {
        Long bookId = busiSmartRoomBookSignIn.getBookId();
        if (bookId == null) {
            throw new CustomException("预约ID不能为空！");
        }
        busiSmartRoomBookSignIn.setCreateTime(new Date());
        BusiSmartRoomBook busiSmartRoomBook = busiSmartRoomBookMapper.selectBusiSmartRoomBookById(bookId);
        if (busiSmartRoomBook != null) {
            Date startTime = busiSmartRoomBook.getStartTime();
            Date diffDate = DateUtils.getDiffDate(startTime, -10, TimeUnit.MINUTES);
            if (!(busiSmartRoomBookSignIn.getCreateTime().after(diffDate) && busiSmartRoomBookSignIn.getCreateTime().before(busiSmartRoomBook.getEndTime()))) {
                throw new CustomException("签到失败，预约已过期！");
            }
        } else {
            throw new CustomException("签到失败，预约不存在！");
        }
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null) {
                SysUser user = loginUser.getUser();
                if (user != null) {
                    busiSmartRoomBookSignIn.setUserId(user.getUserId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int i = busiSmartRoomBookSignInMapper.insertBusiSmartRoomBookSignIn(busiSmartRoomBookSignIn);
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
        return i;
    }

    /**
     * 修改房间预约签到
     *
     * @param busiSmartRoomBookSignIn 房间预约签到
     * @return 结果
     */
    @Override
    public int updateBusiSmartRoomBookSignIn(BusiSmartRoomBookSignIn busiSmartRoomBookSignIn)
    {
        return busiSmartRoomBookSignInMapper.updateBusiSmartRoomBookSignIn(busiSmartRoomBookSignIn);
    }

    /**
     * 批量删除房间预约签到
     *
     * @param ids 需要删除的房间预约签到ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmartRoomBookSignInByIds(Long[] ids)
    {
        return busiSmartRoomBookSignInMapper.deleteBusiSmartRoomBookSignInByIds(ids);
    }

    /**
     * 删除房间预约签到信息
     *
     * @param id 房间预约签到ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmartRoomBookSignInById(Long id)
    {
        return busiSmartRoomBookSignInMapper.deleteBusiSmartRoomBookSignInById(id);
    }
}
