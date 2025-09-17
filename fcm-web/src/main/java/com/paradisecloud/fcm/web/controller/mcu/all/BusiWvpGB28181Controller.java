package com.paradisecloud.fcm.web.controller.mcu.all;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.wvp.gb28181.reponse.WvpConfigInfoResponse;
import com.paradisecloud.fcm.wvp.gb28181.reponse.WvpPlayStartResponse;
import com.paradisecloud.fcm.wvp.gb28181.request.WvpAddDeviceRequest;
import com.paradisecloud.fcm.wvp.gb28181.service.WvpDeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("/busi/wvp/")
@Tag(name = "gb28181平台控制器")
public class BusiWvpGB28181Controller extends BaseController {

    @Resource
    private WvpDeviceService mvpDeviceService;

    /**
     * @return
     */
    @Operation(summary = "添加设备")
    @PostMapping("/add/device")
    public RestResponse addDevice(@RequestBody WvpAddDeviceRequest wvpAddDeviceRequest) {
        String s = mvpDeviceService.addDevice(wvpAddDeviceRequest);
        return RestResponse.success(s);
    }

    /**
     * @return
     */
    @Operation(summary = "修改设备")
    @PostMapping("/update/device")
    public RestResponse updateDevice(@RequestBody WvpAddDeviceRequest wvpAddDeviceRequest) {
        mvpDeviceService.updateDevice(wvpAddDeviceRequest);
        return RestResponse.success();
    }

    /**
     * @return
     */
    @Operation(summary = "查询流")
    @GetMapping("/query/{deviceId}")
    public RestResponse queryPlayStream(@PathVariable String deviceId) {
        WvpPlayStartResponse wvpPlayStartResponse = mvpDeviceService.play(deviceId);
        return RestResponse.success(wvpPlayStartResponse);
    }

    /**
     * @return
     */
    @Operation(summary = "查询系统信息")
    @GetMapping("/configInfo")
    public RestResponse configInfo() {
        WvpConfigInfoResponse wvpConfigInfoResponse = mvpDeviceService.configInfo();

        if (wvpConfigInfoResponse == null) {
            return RestResponse.fail();
        }
        WvpConfigInfoResponse.DataDTO.SipDTO sip = wvpConfigInfoResponse.getData().getSip();

        WvpConfigInfoResponse.DataDTO.SipDTO sipDTO = new WvpConfigInfoResponse.DataDTO.SipDTO();
        sipDTO.setDomain(sip.getDomain());
        sipDTO.setIp(sip.getIp());
        sipDTO.setPort(sip.getPort());
        sipDTO.setId(sip.getId());
        return RestResponse.success(sipDTO);
    }
}
