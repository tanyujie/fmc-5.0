package com.paradisecloud.fcm.web.controller.oss;

import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.AppType;
import com.paradisecloud.fcm.common.utils.AppFileUtil;
import com.paradisecloud.fcm.common.utils.OssFileUtil;
import com.paradisecloud.fcm.dao.model.BusiTerminalUpgrade;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiTerminalUpgradeService;
import com.paradisecloud.system.model.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 终端升级Controller
 * 
 * @author zyz
 * @date 2021-10-11
 */
@RestController
@RequestMapping("/oss")
@Tag(name = "终端升级")
public class OssController extends BaseController
{

    /**
     * 终端App文件的上传
     *
     * @param uploadFile
     * @throws Exception
     */
    @PostMapping("/upload")
    public Object appFilesUpload(HttpServletRequest request, @RequestParam(value = "uploadFile") MultipartFile uploadFile) {
        Map<String, Object> resultMap = new HashMap<>();
        if (uploadFile != null && !uploadFile.isEmpty()) {
            String urlTemp = ExternalConfigCache.getInstance().getFmcRootUrl();
            String fmcRootUrlExternal = ExternalConfigCache.getInstance().getFmcRootUrlExternal();
            if (StringUtils.isNotEmpty(fmcRootUrlExternal)) {
                try {
                    String referer = request.getHeader("referer");
                    String ip = referer.replace("http://", "").replace("https://", "");
                    if (ip.indexOf(":") > 0) {
                        ip.substring(0, ip.indexOf(":"));
                    }
                    if (ip.indexOf("/") > 0) {
                        ip = ip.substring(0, ip.indexOf("/"));
                    }
                    String externalIp = fmcRootUrlExternal.replace("http://", "").replace("https://", "");
                    if (externalIp.indexOf(":") > 0) {
                        externalIp.substring(0, externalIp.indexOf(":"));
                    }
                    if (externalIp.indexOf("/") > 0) {
                        externalIp = externalIp.substring(0, externalIp.indexOf("/"));
                    }
                    if (externalIp.equals(ip)) {
                        urlTemp = fmcRootUrlExternal;
                    }
                } catch (Exception e) {
                }
            } else {
                try {
                    String host = request.getHeader("Host");
                    if (host.indexOf(":8898") > 0) {
                        urlTemp.replace(":8899", ":8898").replace("https://", "http://");
                    }
                } catch (Exception e) {
                }
            }
            // 保存
            try {
                String path = OssFileUtil.saveFile(uploadFile);
                String url = urlTemp + path;
                resultMap.put("errno", 0);
                Map<String, Object> data = new HashMap<>();
                data.put("url", url);
                data.put("alt", "");
                data.put("href", "");
                resultMap.put("data", data);
            } catch (Exception e) {
                resultMap.put("errno", 1);
                if (e instanceof CustomException) {
                    resultMap.put("message", e.getMessage());
                } else {
                    resultMap.put("message", "文件上传错误");
                }
            }
        }
        return resultMap;
    }
}
