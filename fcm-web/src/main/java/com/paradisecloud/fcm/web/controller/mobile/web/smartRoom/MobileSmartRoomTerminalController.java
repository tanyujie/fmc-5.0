package com.paradisecloud.fcm.web.controller.mobile.web.smartRoom;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.dao.model.vo.McuTypeVo;
import com.paradisecloud.fcm.dao.model.vo.TerminalSearchVo;
import com.paradisecloud.fcm.service.interfaces.IBusiFcmNumberSectionService;
import com.paradisecloud.fcm.tencent.model.MeetingRoom;
import com.paradisecloud.fcm.tencent.model.reponse.RoomResponse;
import com.paradisecloud.fcm.tencent.service2.interfaces.IAttendeeTencentService;
import com.paradisecloud.fcm.web.service.interfaces.IBusiAllMcuService;
import com.paradisecloud.fcm.web.service.interfaces.IBusiTerminalWebService;
import com.paradisecloud.system.model.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/mobileWeb/smartRoom/terminal")
@Tag(name = "通讯录")
public class MobileSmartRoomTerminalController {

    @Resource
    private IBusiTerminalWebService busiTerminalWebService;
    @Resource
    private IBusiFcmNumberSectionService busiFcmNumberSectionService;
    @Resource
    private IAttendeeTencentService attendeeTencentService;
    @Resource
    private IBusiAllMcuService busiAllMcuService;

    /**
     * 获取终端信息详细信息
     */
    @GetMapping(value = "/terminalInfo")
    @Operation(summary = "根据ID获取终端信息详细信息")
    public RestResponse getInfo(@RequestParam("id") Long id)
    {
        return RestResponse.success(busiTerminalWebService.selectBusiTerminalById(id));
    }

    /**
     * 获取部门可使用MCU类型列表
     */
    @GetMapping(value = "/mcuTypeListDept")
    @Operation(summary = "获取部门可使用MCU列表")
    public RestResponse getMcuTypeList(Long deptId) {
        Assert.isTrue(deptId != null, "部门ID不能为空！");
        List<McuTypeVo> mcuTypeList = busiAllMcuService.getMcuTypeList(deptId);
        return RestResponse.success(mcuTypeList);
    }

    /**
     * 查询终端通讯录列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询终端通讯录列表")
    public RestResponse list(TerminalSearchVo busiTerminal)
    {
        Long deptId = busiTerminal.getDeptId();
        String mcuTypeStr = busiTerminal.getMcuType();
        Assert.isTrue(deptId != null, "部门ID不能为空！");
        Assert.isTrue(mcuTypeStr != null, "MCU类型不能为空！");
        McuType mcuType = McuType.convert(mcuTypeStr);
        Assert.isTrue(mcuType != null, "MCU类型不正确！");
        busiTerminal.setBusinessFieldType(100);
        List<ModelBean> terminalList = new ArrayList<>();
        if (McuType.MCU_TENCENT == mcuType) {
            // 腾讯云
            try {
                Integer pageNum = busiTerminal.getPageNum();
                Integer pageSize = busiTerminal.getPageSize();
                Object rooms = attendeeTencentService.rooms(getDeptId(), pageNum, pageSize, null);
                if (rooms != null) {
                    RoomResponse roomResponse = (RoomResponse) rooms;
                    List<MeetingRoom> meetingRoomList = roomResponse.getMeetingRoomList();
                    for (MeetingRoom meetingRoom : meetingRoomList) {
                        ModelBean modelBeanTemp = new ModelBean();
                        modelBeanTemp.put("id", meetingRoom.getMeetingRoomId());
                        modelBeanTemp.put("name", meetingRoom.getMeetingRoomName());
                        modelBeanTemp.put("status", meetingRoom.getMeetingRoomStatus());
                        terminalList.add(modelBeanTemp);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (McuType.MCU_HWCLOUD == mcuType) {
            // 华为云
        } else {
            PaginationData<ModelBean> list = busiTerminalWebService.selectBusiTerminalList(busiTerminal);
            for (ModelBean record : list.getRecords()) {
                ModelBean modelBeanTemp = new ModelBean();
                modelBeanTemp.put("id", record.get("id"));
                modelBeanTemp.put("name", record.get("name"));
                modelBeanTemp.put("status", record.get("onlineStatus"));
                terminalList.add(modelBeanTemp);
            }
        }
        return RestResponse.success(terminalList);
    }

    private Long getDeptId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Long.valueOf(100);
        }
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        Long deptId = principal.getUser().getDeptId();
        return deptId;
    }

}
