package com.paradisecloud.fcm.web.controller.system;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.DateOldUtils;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.dao.mapper.BusiFreeSwitchMapper;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitch;
import com.paradisecloud.fcm.web.model.upload.UploadFile;
import com.paradisecloud.fcm.web.utils.FileUploadUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping({"/busi/system"})
public class BusiSystemController extends BaseController {
    @Value("${application.name:}")
    private String an;
    @Value("${application.version:}")
    private String av;
    @Value("${application.copyrightYear:2021}")
    private int cry;
    @Value("${application.copyrightCompany:}")
    private String crc;

    @Resource
    private BusiFreeSwitchMapper busiFreeSwitchMapper;

    @GetMapping({"/about"})
    public RestResponse about() {
        JSONObject json = new JSONObject();
        String applicationName = this.an;
        if (StringUtils.isNotEmpty(ExternalConfigCache.getInstance().getTitle())) {
            applicationName = ExternalConfigCache.getInstance().getTitle();
        }
        json.put("applicationName", applicationName);
        json.put("applicationVersion", this.av);
        json.put("copyrightYear", this.cry);
        String company = this.crc;
//        if (StringUtils.isNotEmpty(ExternalConfigCache.getInstance().getCompany())) {
            company = ExternalConfigCache.getInstance().getCompany();
            if (StringUtils.isEmpty(company)) {
                company = "";
            }
//        }
        json.put("copyrightCompany", company);
        String runTime = "";
        try {
            runTime = DateOldUtils.getDatePoor(DateOldUtils.getNowDate(), DateOldUtils.getServerStartDate());
        } catch (Exception e) {
            e.printStackTrace();
        }
        json.put("runTime", runTime);
        return RestResponse.success(json);
    }


    /**
     * freewswitch license文件的上传
     *
     * @param uploadFile
     * @throws Exception
     */
    @PostMapping("/licenseFilesUpload")
    @Operation(summary = "", description = "FCM license文件的上传")
    public RestResponse appFilesUpload(UploadFile uploadFile) throws Exception {
        if (uploadFile != null && !uploadFile.getUploadFile().isEmpty()) {
            // 保存
            if (uploadFile.getUploadFile().isEmpty()) {
                throw new CustomException("文件为空");
            }
            String originalFilename = uploadFile.getUploadFile().getOriginalFilename();
            String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            if (!fileSuffix.equals(".pem")) {
                throw new CustomException("文件类型错误，请上传pem文件");
            }
            String path ="/usr/local/freeswitch/certs/";
            BusiFreeSwitch busiFreeSwitch = busiFreeSwitchMapper.selectBusiFreeSwitchById(uploadFile.getId());
            boolean renameFile = FileUploadUtil.renameFile("wss.pem", "wss_back.pem", busiFreeSwitch.getUserName(), busiFreeSwitch.getPassword(), busiFreeSwitch.getIp(), busiFreeSwitch.getPort(), path);
            boolean isSuccess = false;
            if (renameFile) {
                isSuccess = FileUploadUtil.uploadFile("wss.pem", uploadFile.getUploadFile().getInputStream(), busiFreeSwitch.getUserName(), busiFreeSwitch.getPassword(), busiFreeSwitch.getIp(), busiFreeSwitch.getPort(), path);
            }

            return RestResponse.success(isSuccess);

        }
        return RestResponse.fail();
    }


}



