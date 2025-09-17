package com.paradisecloud.fcm.web.controller.config;

import com.paradisecloud.common.annotation.RepeatSubmit;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.system.dao.model.SysConfig;
import com.paradisecloud.system.service.ISysConfigService;
import com.paradisecloud.system.utils.SecurityUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping({"/busi/sys/config"})
public class BusiSysConfigController extends BaseController {

    @Resource
    private ISysConfigService configService;

    @GetMapping({"/list"})
    public RestResponse list(SysConfig config) {
        this.startPage();
        config.setConfigType("N");
        List<SysConfig> list = this.configService.selectConfigList(config);
        return this.getDataTable(list);
    }

    @GetMapping({"/{configId}"})
    public RestResponse getInfo(@PathVariable Long configId) {
        SysConfig config = new SysConfig();
        config.setConfigId(configId);
        config.setConfigType("N");
        List<SysConfig> list = this.configService.selectConfigList(config);
        if (list.size() > 0) {
            return RestResponse.success(list.get(0));
        }
        return RestResponse.fail();
    }

    @GetMapping({"/configKey/{configKey}"})
    public RestResponse getConfigKey(@PathVariable String configKey) {
        SysConfig config = new SysConfig();
        config.setConfigKey(configKey);
        config.setConfigType("N");
        List<SysConfig> list = this.configService.selectConfigList(config);
        if (list.size() > 0) {
            return RestResponse.success(list.get(0));
        }
        return RestResponse.fail();
    }

    @PostMapping
    @RepeatSubmit
    public RestResponse add(@RequestBody SysConfig config) {
        config.setConfigType("N");
        config.setConfigName(config.getConfigKey());
        if ("1".equals(this.configService.checkConfigKeyUnique(config))) {
            return RestResponse.fail("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        } else {
            config.setCreateBy(SecurityUtils.getUsername());
            return this.toAjax(this.configService.insertConfig(config));
        }
    }

    @PutMapping
    public RestResponse edit(@RequestBody SysConfig config) {
        config.setConfigType("N");
        config.setConfigName(config.getConfigKey());
        SysConfig sysConfigCon = new SysConfig();
        sysConfigCon.setConfigKey(config.getConfigKey());
        List<SysConfig> sysConfigs = configService.selectConfigList(sysConfigCon);
        if (sysConfigs.size() == 0) {
            return this.toAjax(this.configService.insertConfig(config));
        }
        if (sysConfigs.size() > 0) {
            SysConfig sysConfig = sysConfigs.get(0);
            if ("N".equals(sysConfig.getConfigType())) {
                sysConfig.setConfigValue(config.getConfigValue());
                return this.toAjax(this.configService.updateConfig(sysConfig));
            }
        }
        return RestResponse.fail();
    }

    @PutMapping("/update")
    public RestResponse edit(@RequestBody List<SysConfig> configs) {
        for (SysConfig config : configs) {
            config.setConfigType("N");
            config.setConfigName(config.getConfigKey());
            SysConfig sysConfigCon = new SysConfig();
            sysConfigCon.setConfigKey(config.getConfigKey());
            List<SysConfig> sysConfigs = configService.selectConfigList(sysConfigCon);
            if (sysConfigs.size() == 0) {
                int i = this.configService.insertConfig(config);
                if (i == 0) {
                    return RestResponse.fail(500, "更新失败", config);
                }
            }
            if (sysConfigs.size() > 0) {
                SysConfig sysConfig = sysConfigs.get(0);
                if ("N".equals(sysConfig.getConfigType())) {
                    sysConfig.setConfigValue(config.getConfigValue());
                    int i = this.configService.updateConfig(sysConfig);
                    if (i == 0) {
                        return RestResponse.fail(500, "更新失败", config);
                    }
                }
            }
        }
        return RestResponse.fail();
    }
}
