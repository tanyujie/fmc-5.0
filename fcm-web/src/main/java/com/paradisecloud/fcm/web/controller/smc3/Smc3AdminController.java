package com.paradisecloud.fcm.web.controller.smc3;

import com.paradisecloud.com.fcm.smc.modle.MeetingRoomResponse;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.terminal.service.interfaces.IBusiTerminalService;
import com.paradisecloud.smc3.service.interfaces.Smc3DeviceroutesService;
import com.paradisecloud.smc3.service.interfaces.Smc3ServiceZoneId;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author nj
 * @date 2022/8/23 10:48
 */

@RestController
@RequestMapping("/smc3/admin/device")
public class Smc3AdminController {

    @Resource
    private Smc3DeviceroutesService smc3deviceroutesService;

    @Resource
    private Smc3ServiceZoneId smc3ServiceZoneId;




    /**
     * 终端号码
     * @return
     */
    @GetMapping("/deviceroutes")
    public RestResponse getSmcServiceZoneId(@RequestParam String zoneId,@RequestParam int number,@RequestParam Long deptId){
        return RestResponse.success(smc3deviceroutesService.getDeviceroutes(zoneId,number,deptId));
    }
    /**
     * 服务区查询
     * @return
     */
    @GetMapping("/smcServiceZoneId")
    public RestResponse getSmcServiceZoneId(@RequestParam Long deptId){
        return RestResponse.success(smc3ServiceZoneId.getSmcServiceZoneId(deptId));
    }

    /**
     * 所有组织查询
     * @return
     */
    @GetMapping("/smcOrganizationsList")
    public RestResponse getOrganizationsList(@RequestParam Long deptId){
        return RestResponse.success(smc3ServiceZoneId.getOrganizationsList(deptId));
    }

    /**
     * 服务区查询
     * @return
     */
    @GetMapping("/mcu")
    public RestResponse getSmcMcu(@RequestParam Long deptId){
        return RestResponse.success(smc3ServiceZoneId.getSmcMcu(deptId));
    }

    /**
     * 区域查询
     * @return
     */
    @GetMapping("/area")
    public RestResponse getSmcAreaId(@RequestParam Long deptId){
        return RestResponse.success(smc3ServiceZoneId.getSmcArea(deptId));
    }


    /**
     * 设备能力查询
     * @return
     */
    @GetMapping("/devicecapabilities")
    public RestResponse getSmcDevicecapabilities(@RequestParam Long deptId){
        return RestResponse.success(smc3ServiceZoneId.getSmcDevicecapabilities(deptId));
    }


    /**
     * 设备能力查询
     * @return
     */
    @GetMapping("/name")
    public RestResponse getroomname(@RequestParam String name,@RequestParam Long deptId){
        return RestResponse.success(smc3ServiceZoneId.searchName(name,deptId));
    }

    @Resource
    private IBusiTerminalService busiTerminalService;

    /**
     *重置激活码
     * @return
     */
    @PutMapping("/resetactivecode/{id}")
    public RestResponse resetactivecode(@PathVariable long id){
        BusiTerminal busiTerminal = busiTerminalService.selectBusiTerminalById(id);

        Map<String, Object> businessProperties = busiTerminal.getBusinessProperties();
        String codeId = (String) businessProperties.get("codeId");
        Object resetactivecode = smc3ServiceZoneId.resetactivecode(codeId,busiTerminal.getDeptId());
        com.paradisecloud.smc3.model.MeetingRoomResponse.TerminalParamDTO.ActiveCodeDTO codeDTO=(com.paradisecloud.smc3.model.MeetingRoomResponse.TerminalParamDTO.ActiveCodeDTO)resetactivecode;
        busiTerminal.setCode(codeDTO.getCode());
        busiTerminal.setRemarks(null);
        busiTerminalService.updateBusiTerminal(busiTerminal);
        return RestResponse.success(codeDTO.getCode());
    }

}
