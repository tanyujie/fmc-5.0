package com.paradisecloud.fcm.web.controller.server;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.web.service.interfaces.IServerService;
import io.jsonwebtoken.lang.Assert;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/busi/server")
@Tag(name = "会控服务器信息")
public class FmcServerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FmcServerController.class);

    @Resource
    private IServerService serverService;

    /**
     * 查询会控服务器时间
     *
     * @return
     */
    @GetMapping("/getServerTime")
    @Operation(summary = "获取服务器的时间")
    public RestResponse getServerDate() {
        return RestResponse.success(serverService.getServerTime());
    }

    /**
     * 设置会控服务器时间
     */
    @PostMapping("/setServerTime")
    @Operation(summary = "获取服务器的时间", description = "设置服务器时间")
    public RestResponse setServerDate(@RequestBody JSONObject body) {
        String date = body.getString("date");
        String time = body.getString("time");
        Assert.isTrue(StringUtils.hasText(date), "date不能为空！");
        Assert.isTrue(StringUtils.hasText(time), "time不能为空！");
        LOGGER.info("接口传值为：", date + time);
        serverService.setServerTime(date, time);
        return RestResponse.success();
    }
}
