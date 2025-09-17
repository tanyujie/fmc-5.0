package com.paradisecloud.fcm.web.controller.system;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.web.cache.SystemUpdateCache;
import com.paradisecloud.fcm.web.service.SystemUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/system/update/server")
public class SystemUpdateController {

    private static final Logger logger = LoggerFactory.getLogger(SystemUpdateController.class);

    @Value("${application.version}")
    private String applicationVersion;

    @Resource
    private SystemUpdateService systemUpdateService;

    /**
     * 更新FMC程序
     *
     * @param file
     * @return
     */
    @PostMapping("/fmc/update")
    public Object updateFmcServer(@RequestParam(value = "file") MultipartFile file) {
        logger.info("updateFmcServer  === ");
        try {
            boolean update = systemUpdateService.updateFmcServer(file);
            if (update) {
                return RestResponse.success();
            }
        } catch (Exception e) {
            if (e instanceof CustomException) {
                RestResponse.fail(500, e.getMessage());
            }
        }
        return RestResponse.fail(500,"上传失败！");
    }

    /**
     * 还原FMC程序
     *
     * @return
     */
    @PostMapping("/fmc/restore")
    public Object restoreFmcServer() {
        logger.info("updateFmcServer  === ");
        try {
            boolean update = systemUpdateService.restoreFmcServer();
            if (update) {
                return RestResponse.success();
            }
        } catch (Exception e) {
            if (e instanceof CustomException) {
                RestResponse.fail(500, e.getMessage());
            }
        }
        return RestResponse.fail(500,"还原失败！");
    }

    /**
     * 获取FMC程序更新状态
     *
     * @return
     */
    @GetMapping("/getFmcUpdateStatus")
    public Object getFmcUpdateStatus() {
        logger.info("getFmcUpdateStatus  === ");
        Map<String, Object> data = new HashMap<>();
        int updateStatus = SystemUpdateCache.getFmcUpdateStatus();
        long updateStatusTime = SystemUpdateCache.getFmcUpdateStatusTime();
        if (updateStatus != SystemUpdateCache.UPDATE_STATUS_NORMAL) {
            if (updateStatus >= SystemUpdateCache.UPDATE_STATUS_UPLOAD_ERROR || System.currentTimeMillis() - updateStatusTime > 300000) {
                SystemUpdateCache.updateFmcUpdateStatus(SystemUpdateCache.UPDATE_STATUS_NORMAL);
                updateStatus = SystemUpdateCache.getFmcUpdateStatus();
            }
        }
        data.put("applicationVersion", applicationVersion);
        data.put("updateStatus", updateStatus);
        return RestResponse.success(data);
    }

}
