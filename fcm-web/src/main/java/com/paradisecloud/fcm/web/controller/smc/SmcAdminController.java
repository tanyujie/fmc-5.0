package com.paradisecloud.fcm.web.controller.smc;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.smc.service.DeviceroutesService;
import com.paradisecloud.smc.service.impl.SmcServiceZoneId;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author nj
 * @date 2022/8/23 10:48
 */

@RestController
@RequestMapping("/smc/admin/device")
public class SmcAdminController {

    @Resource
   private DeviceroutesService deviceroutesService;

    @Resource
    private SmcServiceZoneId smcServiceZoneId;






    /**
     * 终端号码
     * @return
     */
    @GetMapping("/deviceroutes")
    public RestResponse getSmcServiceZoneId(@RequestParam String zoneId){
        return RestResponse.success(deviceroutesService.getDeviceroutes(zoneId));
    }


    /**
     * 服务区查询
     * @return
     */
    @GetMapping("/smcServiceZoneId")
    public RestResponse getSmcServiceZoneId(){
        return RestResponse.success(smcServiceZoneId.getSmcServiceZoneId());
    }


}
