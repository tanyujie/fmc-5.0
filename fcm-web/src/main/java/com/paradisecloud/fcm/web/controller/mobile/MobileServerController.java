package com.paradisecloud.fcm.web.controller.mobile;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务器Controller
 *
 * @author lilinhai
 * @date 2021-01-20
 */
@RestController
@RequestMapping("/mobile/server")
@Slf4j
@Tag(name = "移动端服务器")
public class MobileServerController extends MobileBaseController {

    /**
     * 查询服务器信息
     */
    @GetMapping("/getServerInfo")
    @Operation(summary = "查询服务器信息")
    public RestResponse getServerInfo() {
        ModelBean modelBean = new ModelBean();
        modelBean.put("time", System.currentTimeMillis());
        return RestResponse.success(modelBean);
    }
}
