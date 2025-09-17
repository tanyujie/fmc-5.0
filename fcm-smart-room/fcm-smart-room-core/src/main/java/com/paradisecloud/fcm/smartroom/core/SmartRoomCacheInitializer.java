/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : EduCacheInitializer.java
 * Package     : com.paradisecloud.fcm.edu.cache
 * @author sinhy 
 * @since 2021-10-23 16:44
 * @version  V1.0
 */
package com.paradisecloud.fcm.smartroom.core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.paradisecloud.fcm.common.constant.DeptConstant;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomCache;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomDeviceCache;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomDoorplateCache;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomLotCache;
import com.paradisecloud.fcm.smartroom.model.MeetingRoomInfo;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.service.ISysDeptService;
import com.paradisecloud.system.service.ISysUserService;
import com.sinhy.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**  
 * <pre>智慧办公缓存初始化器</pre>
 * @author sinhy
 * @since 2021-10-23 16:44
 * @version V1.0  
 */
@Order(4)
@Component
public class SmartRoomCacheInitializer implements ApplicationRunner
{

    @Resource
    private BusiSmartRoomMapper busiSmartRoomMapper;
    @Resource
    private BusiSmartRoomDeptMapper busiSmartRoomDeptMapper;
    @Resource
    private BusiSmartRoomDoorplateMapper busiSmartRoomDoorplateMapper;
    @Resource
    private BusiSmartRoomDeviceMapMapper busiSmartRoomDeviceMapMapper;
    @Resource
    private BusiSmartRoomBookMapper busiSmartRoomBookMapper;
    @Resource
    private BusiSmartRoomLotMapper busiSmartRoomLotMapper;
    @Resource
    private BusiSmartRoomDeviceMapper busiSmartRoomDeviceMapper;
    @Resource
    private BusiSmartRoomParticipantMapper busiSmartRoomParticipantMapper;
    @Resource
    private ISysUserService sysUserService;
    @Resource
    private ISysDeptService sysDeptService;
    
    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        // 检查系统保留部门
        checkSysDept();
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
                SmartRoomCache.getInstance().addMeetingRoomInfo(meetingRoomInfo);
            }
            meetingRoomInfo.setAllList(busiSmartRoomBookList, date);
        }
        
        List<BusiSmartRoomDept> busiSmartRoomDeptList = busiSmartRoomDeptMapper.selectBusiSmartRoomDeptList(new BusiSmartRoomDept());
        for (BusiSmartRoomDept busiSmartRoomDept : busiSmartRoomDeptList) {
            SmartRoomCache.getInstance().bindDept(busiSmartRoomDept);
        }

        List<BusiSmartRoomParticipant> busiSmartRoomParticipantListForSignInCode = busiSmartRoomParticipantMapper.selectBusiSmartRoomParticipantListForSignInCode();
        for (BusiSmartRoomParticipant busiSmartRoomParticipant : busiSmartRoomParticipantListForSignInCode) {
            SmartRoomCache.getInstance().addSignInCode(busiSmartRoomParticipant.getSignInCode(), busiSmartRoomParticipant.getId());
        }
        SmartRoomCache.getInstance().updateLastCleanSignInCodeTime();

        List<BusiSmartRoomDoorplate> busiSmartRoomDoorplateList = busiSmartRoomDoorplateMapper.selectBusiSmartRoomDoorplateList(new BusiSmartRoomDoorplate());
        for (BusiSmartRoomDoorplate busiSmartRoomDoorplate : busiSmartRoomDoorplateList) {
            SmartRoomDoorplateCache.getInstance().add(busiSmartRoomDoorplate);
        }
        SmartRoomLotCache.getInstance().setLoadFinished();

        List<BusiSmartRoomDeviceMap> busiSmartRoomDeviceMapList = busiSmartRoomDeviceMapMapper.selectBusiSmartRoomDeviceMapList(new BusiSmartRoomDeviceMap());
        for (BusiSmartRoomDeviceMap busiSmartRoomDeviceMap : busiSmartRoomDeviceMapList) {
            SmartRoomCache.getInstance().bindDoorplate(busiSmartRoomDeviceMap);
        }

        List<BusiSmartRoomLot> busiSmartRoomLotList = busiSmartRoomLotMapper.selectBusiSmartRoomLotList(new BusiSmartRoomLot());
        for (BusiSmartRoomLot busiSmartRoomLot : busiSmartRoomLotList) {
            SmartRoomLotCache.getInstance().add(busiSmartRoomLot);
        }
        SmartRoomLotCache.getInstance().setLoadFinished();

        List<BusiSmartRoomDevice> busiSmartRoomDevices = busiSmartRoomDeviceMapper.selectBusiSmartRoomDeviceList(new BusiSmartRoomDevice());
        for (BusiSmartRoomDevice busiSmartRoomDevice : busiSmartRoomDevices) {
            SmartRoomDeviceCache.getInstance().add(busiSmartRoomDevice);
        }
        if (busiSmartRoomDoorplateList.size() > 0) {
            SmartRoomDoorplateCache.getInstance().setNeedUpdateMqStatus(true);
        }
        if (busiSmartRoomLotList.size() > 0) {
            SmartRoomLotCache.getInstance().setNeedUpdateMqStatus(true);
        }
    }

    private void checkSysDept() {
        // smart room
        {
            SysDept sysDept = sysDeptService.selectDeptById(DeptConstant.SMART_ROOM_DEPT_ID);
            if (sysDept == null) {
                sysDept = new SysDept();
                sysDept.setDeptId(DeptConstant.SMART_ROOM_DEPT_ID);
                sysDept.setParentId(100l);
                sysDept.setAncestors("0,1,100");
                sysDept.setDeptName("智慧办公专用部门");
                sysDept.setOrderNum(String.valueOf(DeptConstant.SMART_ROOM_DEPT_ID));
                sysDept.setStatus("0");
                sysDept.setDelFlag("0");
                sysDept.setCreateBy("superAdmin");
                sysDept.setCreateTime(new Date());
                sysDeptService.insertDept(sysDept);
            }
        }
        // oa_qywx
        {
            SysDept sysDept = sysDeptService.selectDeptById(DeptConstant.OA_QYWX);
            if (sysDept == null) {
                sysDept = new SysDept();
                sysDept.setDeptId(DeptConstant.OA_QYWX);
                sysDept.setParentId(100l);
                sysDept.setAncestors("0,1,100");
                sysDept.setDeptName("企业微信专用部门");
                sysDept.setOrderNum(String.valueOf(DeptConstant.OA_QYWX));
                sysDept.setStatus("0");
                sysDept.setDelFlag("0");
                sysDept.setCreateBy("superAdmin");
                sysDept.setCreateTime(new Date());
                sysDeptService.insertDept(sysDept);
            }
        }
        // oa_ding
        {
            SysDept sysDept = sysDeptService.selectDeptById(DeptConstant.OA_DING);
            if (sysDept == null) {
                sysDept = new SysDept();
                sysDept.setDeptId(DeptConstant.OA_DING);
                sysDept.setParentId(100l);
                sysDept.setAncestors("0,1,100");
                sysDept.setDeptName("钉钉专用部门");
                sysDept.setOrderNum(String.valueOf(DeptConstant.OA_DING));
                sysDept.setStatus("0");
                sysDept.setDelFlag("0");
                sysDept.setCreateBy("superAdmin");
                sysDept.setCreateTime(new Date());
                sysDeptService.insertDept(sysDept);
            }
        }
        // ops
        {
            SysDept sysDept = sysDeptService.selectDeptById(DeptConstant.OPS_DEPT_ID);
            if (sysDept == null) {
                sysDept = new SysDept();
                sysDept.setDeptId(DeptConstant.OPS_DEPT_ID);
                sysDept.setParentId(100l);
                sysDept.setAncestors("0,1,100");
                sysDept.setDeptName("OPS专用部门");
                sysDept.setOrderNum(String.valueOf(DeptConstant.OPS_DEPT_ID));
                sysDept.setStatus("0");
                sysDept.setDelFlag("0");
                sysDept.setCreateBy("superAdmin");
                sysDept.setCreateTime(new Date());
                sysDeptService.insertDept(sysDept);
            }
        }
        // 客户端
        {
            SysDept sysDept = sysDeptService.selectDeptById(DeptConstant.CLIENT_DEPT_ID);
            if (sysDept == null) {
                sysDept = new SysDept();
                sysDept.setDeptId(DeptConstant.CLIENT_DEPT_ID);
                sysDept.setParentId(100l);
                sysDept.setAncestors("0,1,100");
                sysDept.setDeptName("客户端专用部门");
                sysDept.setOrderNum(String.valueOf(DeptConstant.CLIENT_DEPT_ID));
                sysDept.setStatus("0");
                sysDept.setDelFlag("0");
                sysDept.setCreateBy("superAdmin");
                sysDept.setCreateTime(new Date());
                sysDeptService.insertDept(sysDept);
            }
        }
    }
    
}
