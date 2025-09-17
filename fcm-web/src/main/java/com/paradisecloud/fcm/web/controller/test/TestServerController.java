package com.paradisecloud.fcm.web.controller.test;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.com.fcm.smc.modle.response.UserInfoRep;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc3TemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplateConference;
import com.paradisecloud.fcm.service.util.TencentCloudUtil;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.model.SmcConferenceTemplateQuery;
import com.sinhy.spring.BeanFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test/server")
@Tag(name = "会控服务器信息")
public class TestServerController {

    @Value("${application.version:}")
    private String appVersion;

    /**
     * 查询会控服务器信息
     * 测试应用是否启动
     *
     * @return
     */
    @GetMapping("/getServerInfo")
    @Operation(summary = "获取服务器信息")
    public RestResponse getServerInfo() {
        Map<String, Object> data = new HashMap<>();
        String time = DateUtil.convertDateToString(new Timestamp(System.currentTimeMillis()), "yyyy-MM-dd HH:mm:ss");
        data.put("serverTime", time);
        data.put("appVersion", appVersion);
        return RestResponse.success(data);
    }

    /**
     * 查询终端信息
     * 测试应用是否正常加载数据
     *
     * @return
     */
    @GetMapping("/getTerminalInfo")
    @Operation(summary = "获取终端信息")
    public RestResponse getTerminalInfo() {
        Map<String, Object> data = new HashMap<>();
        int size = TerminalCache.getInstance().size();
        data.put("terminalSize", size);
        return RestResponse.success(data);
    }



    @GetMapping("/getc")
    public RestResponse edeqw() {
        return RestResponse.success(TencentCloudUtil.getConferenceNumber("213213", null));
    }

    /**
     * 查询SMC3.0模板列表
     * 测试应用是否正常加载数据
     *
     * @return
     */
    @GetMapping("/getSmc3TemplateList")
    @Operation(summary = "查询SMC3.0模板列表")
    public RestResponse getSmc3TemplateList() {
        Map<String, Object> map = new HashMap<>();
        Smc3Bridge smc3Bridge = Smc3BridgeCache.getInstance().getBridgesByDept(1l);
        Map<String, Object> paramsMap = new HashMap<>();
        BusiMcuSmc3TemplateConferenceMapper busiMcuSmc3TemplateConferenceMapper = BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class);
        String s = smc3Bridge.getSmcConferencesTemplateInvoker().queryConferencesTemplate(JSON.toJSONString(paramsMap), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        SmcConferenceTemplateQuery smcConferenceTemplateQuery = JSON.parseObject(s, SmcConferenceTemplateQuery.class);
        map.put("smcConferenceTemplateQuery", smcConferenceTemplateQuery);
        if (smcConferenceTemplateQuery != null && smcConferenceTemplateQuery.getContent() != null) {
            for (SmcConferenceTemplateQuery.Content content : smcConferenceTemplateQuery.getContent()) {
                BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceBySmcTemplateId(content.getId());
                if (busiMcuSmc3TemplateConference == null) {
                    String result = smc3Bridge.getSmcConferencesTemplateInvoker().deleteConferencesTemplate(content.getId(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                    map.put(content.getId(), result);
                }
            }
        }
        return RestResponse.success(smcConferenceTemplateQuery);
    }
}
