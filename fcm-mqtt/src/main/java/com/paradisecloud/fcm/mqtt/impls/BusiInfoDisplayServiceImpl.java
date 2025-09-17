package com.paradisecloud.fcm.mqtt.impls;

import java.util.*;

import com.paradisecloud.fcm.common.constant.DeptConstant;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.dao.mapper.BusiInfoDisplayMapper;
import com.paradisecloud.fcm.dao.model.BusiInfoDisplay;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.dao.model.vo.BusiInfoDisplayVO;
import com.paradisecloud.fcm.mqtt.cache.InfoDisplayCache;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiInfoDisplayService;
import com.paradisecloud.fcm.mqtt.task.PushInfoDisplayTask;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * 信息展示Service业务层处理
 * 
 * @author lilinhai
 * @date 2024-05-13
 */
@Service
public class BusiInfoDisplayServiceImpl implements IBusiInfoDisplayService
{
    @Resource
    private BusiInfoDisplayMapper busiInfoDisplayMapper;
    @Resource
    private TaskService taskService;

    /**
     * 查询信息展示
     * 
     * @param id 信息展示ID
     * @return 信息展示
     */
    @Override
    public BusiInfoDisplay selectBusiInfoDisplayById(Long id)
    {
        return busiInfoDisplayMapper.selectBusiInfoDisplayById(id);
    }

    /**
     * 查询信息展示列表
     * 
     * @param busiInfoDisplay 信息展示
     * @return 信息展示
     */
    @Override
    public List<BusiInfoDisplay> selectBusiInfoDisplayList(BusiInfoDisplayVO busiInfoDisplay)
    {
        return busiInfoDisplayMapper.selectBusiInfoDisplayList(busiInfoDisplay);
    }

    /**
     * 新增信息展示
     * 
     * @param busiInfoDisplay 信息展示
     * @return 结果
     */
    @Override
    public int insertBusiInfoDisplay(BusiInfoDisplay busiInfoDisplay)
    {
        Assert.isTrue(busiInfoDisplay.getType() != null, "类型不能为空");
        Assert.isTrue(busiInfoDisplay.getName() != null, "名称不能为空");
        Assert.isTrue(busiInfoDisplay.getStatus() != null, "状态不能为空");
        Assert.isTrue(busiInfoDisplay.getPushObject() != null, "推送对象不能为空");

        busiInfoDisplay.setCreateTime(new Date());

        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            busiInfoDisplay.setCreateBy(loginUser.getUser().getUserName());
        }
        Integer type = busiInfoDisplay.getType();
        if (type == 1) {
            Integer pushType = busiInfoDisplay.getPushType();
            if (pushType == 2) {
                busiInfoDisplay.setPushObject(3);
            } else {
                busiInfoDisplay.setPushObject(2);
            }
        } else if (type == 2) {
            busiInfoDisplay.setStatus(1);
        }
        Integer pushObject = busiInfoDisplay.getPushObject();
        if (pushObject == 3) {
            String pushTerminalIds = busiInfoDisplay.getPushTerminalIds();
            if (StringUtils.isEmpty(pushTerminalIds)) {
                throw new SystemException("请选择对象！");
            }
        }

        Integer pushType = busiInfoDisplay.getPushType();
        if (pushType == 2) {
            Long deptId = busiInfoDisplay.getDeptId();
            if (deptId == null) {
                busiInfoDisplay.setDeptId(DeptConstant.SMART_ROOM_DEPT_ID);
            }
        }

        int i = busiInfoDisplayMapper.insertBusiInfoDisplay(busiInfoDisplay);
        if (i > 0) {
            InfoDisplayCache.getInstance().add(busiInfoDisplay);
        }
        return i;
    }

    /**
     * 修改信息展示
     * 
     * @param busiInfoDisplay 信息展示
     * @return 结果
     */
    @Override
    public int updateBusiInfoDisplay(BusiInfoDisplay busiInfoDisplay)
    {
        BusiInfoDisplay busiInfoDisplayTemp = busiInfoDisplayMapper.selectBusiInfoDisplayById(busiInfoDisplay.getId());
        if (busiInfoDisplayTemp == null) {
            return 0;
        }

        Assert.isTrue(busiInfoDisplay.getType() != null, "类型不能为空");
        Assert.isTrue(busiInfoDisplay.getName() != null, "名称不能为空");
        Assert.isTrue(busiInfoDisplay.getStatus() != null, "状态不能为空");
        Assert.isTrue(busiInfoDisplay.getPushObject() != null, "推送对象不能为空");

        busiInfoDisplay.setUpdateTime(new Date());

        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            busiInfoDisplay.setUpdateBy(loginUser.getUser().getUserName());
        }
        Integer type = busiInfoDisplay.getType();
        if (type == 1) {
            Integer pushType = busiInfoDisplay.getPushType();
            if (pushType == 2) {
                busiInfoDisplay.setPushObject(3);
            } else {
                busiInfoDisplay.setPushObject(2);
            }
        } else if (type == 2) {
            busiInfoDisplay.setStatus(1);
        }

        Integer pushType = busiInfoDisplay.getPushType();
        if (pushType == 2) {
            Long deptId = busiInfoDisplay.getDeptId();
            if (deptId == null) {
                busiInfoDisplay.setDeptId(DeptConstant.SMART_ROOM_DEPT_ID);
            }
        }
        Integer pushObject = busiInfoDisplay.getPushObject();
        if (pushObject == 3) {
            String pushTerminalIds = busiInfoDisplay.getPushTerminalIds();
            if (StringUtils.isEmpty(pushTerminalIds)) {
                throw new SystemException("请选择对象！");
            }
        }

        int i = busiInfoDisplayMapper.updateBusiInfoDisplay(busiInfoDisplay);
        if (i > 0) {
            InfoDisplayCache.getInstance().add(busiInfoDisplay);
        }
        return i;
    }

    /**
     * 批量删除信息展示
     * 
     * @param ids 需要删除的信息展示ID
     * @return 结果
     */
    @Override
    public int deleteBusiInfoDisplayByIds(Long[] ids)
    {
        int i = 0;
        for (Long id : ids) {
            i += deleteBusiInfoDisplayById(id);
        }
        return i;
    }

    /**
     * 删除信息展示信息
     * 
     * @param id 信息展示ID
     * @return 结果
     */
    @Override
    public int deleteBusiInfoDisplayById(Long id)
    {
        int i = 0;
        BusiInfoDisplay busiInfoDisplay = busiInfoDisplayMapper.selectBusiInfoDisplayById(id);
        if (busiInfoDisplay != null) {
            i = busiInfoDisplayMapper.deleteBusiInfoDisplayById(id);
            if (i > 0) {
                InfoDisplayCache.getInstance().remove(id);
            }
        }
        return i;
    }

    /**
     * 推送信息展示
     * @param id
     * @return
     */
    @Override
    public int push(Long id) {
        PushInfoDisplayTask pushInfoDisplayTask = new PushInfoDisplayTask(id.toString(), 1000, id);
        taskService.addTask(pushInfoDisplayTask);
        return 1;
    }

    @Override
    public List<DeptRecordCount> getDeptRecordCounts() {
        return busiInfoDisplayMapper.getDeptRecordCounts();
    }

    /**
     * 修改状态
     * @param busiInfoDisplay
     * @return
     */
    @Override
    public int status(BusiInfoDisplay busiInfoDisplay) {
        int i = 0;
        Long id = busiInfoDisplay.getId();
        BusiInfoDisplay busiInfoDisplayTemp = busiInfoDisplayMapper.selectBusiInfoDisplayById(id);
        if (busiInfoDisplayTemp != null) {
            Integer status = busiInfoDisplay.getStatus();
            if (busiInfoDisplayTemp.getType() == 1) {
                BusiInfoDisplay infoDisplay = new BusiInfoDisplay();
                infoDisplay.setType(1);
                infoDisplay.setStatus(1);
                infoDisplay.setDeptId(busiInfoDisplayTemp.getDeptId());
                List<BusiInfoDisplay> busiInfoDisplayList = busiInfoDisplayMapper.selectBusiInfoDisplayList(infoDisplay);
                busiInfoDisplayTemp.setStatus(status);
                i = busiInfoDisplayMapper.updateBusiInfoDisplay(busiInfoDisplayTemp);
                if (i > 0) {
                    InfoDisplayCache.getInstance().add(busiInfoDisplayTemp);
                    if (status == 1) {
                        if (busiInfoDisplayList != null && busiInfoDisplayList.size() > 0) {
                            for (BusiInfoDisplay display : busiInfoDisplayList) {
                                display.setStatus(2);
                                int i1 = busiInfoDisplayMapper.updateBusiInfoDisplay(display);
                                if (i1 > 0) {
                                    InfoDisplayCache.getInstance().add(busiInfoDisplayTemp);
                                }
                            }
                        }
                    }
                }
            } else {
                busiInfoDisplayTemp.setStatus(status);
                i = busiInfoDisplayMapper.updateBusiInfoDisplay(busiInfoDisplayTemp);
                if (i > 0) {
                    InfoDisplayCache.getInstance().add(busiInfoDisplayTemp);
                }
            }
        }
        return i;
    }
}
