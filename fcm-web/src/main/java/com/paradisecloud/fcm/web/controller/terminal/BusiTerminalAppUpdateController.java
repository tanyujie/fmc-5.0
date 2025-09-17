package com.paradisecloud.fcm.web.controller.terminal;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiTerminalUpgradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

/**
 * 终端App更新Controller
 *
 * @author lilinhai
 * @date 2021-01-20
 */
@RestController
@RequestMapping("/busi/terminal/appupdate")
@Tag(name = "终端信息")
public class BusiTerminalAppUpdateController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BusiTerminalAppUpdateController.class);

    @Autowired
    private IBusiTerminalUpgradeService busiTerminalUpgradeService;

    /**
     * 根据类型和版本号检查是否有更新
     */
    @GetMapping(value = "/check")
    @Operation(summary = "根据类型和客户ID获取版本信息")
    public RestResponse getVersion(@PathParam("terminalType") String terminalType, @PathParam("versionCode") String versionCode) {
        Assert.notNull(terminalType, "类型不能为空");
        Assert.notNull(versionCode, "版本code不能为空");
        return RestResponse.success(busiTerminalUpgradeService.selectBusiAppVersion(terminalType, versionCode));
    }
}
