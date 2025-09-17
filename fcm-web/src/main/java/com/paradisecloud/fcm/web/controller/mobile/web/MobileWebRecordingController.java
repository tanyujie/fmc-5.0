package com.paradisecloud.fcm.web.controller.mobile.web;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.dao.model.vo.RecordsSearchVo;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiRecordsService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.service.interfaces.IRecordingService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiRecordsForMcuHwcloudService;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiRecordsForMcuKdcService;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiRecordsForMcuPlcService;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiRecordsForMcuZjService;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiRecordsForMcuSmc2Service;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.service.interfaces.IBusiRecordsForMcuSmc3Service;
import com.sinhy.exception.SystemException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author johnson liu
 * @Date 2021/4/27 10:33
 */
@RestController
@RequestMapping("/mobileWeb/mcu/all/recording")
@Tag(name = "会议录制")
public class MobileWebRecordingController extends BaseController {
    @Autowired
    private IRecordingService iRecordingService;



    @Resource
    private IBusiRecordsForMcuZjService iBusiRecordsForMcuZjService;
    @Resource
    private IBusiRecordsService busiRecordsService;
    @Resource
    private IBusiRecordsForMcuPlcService busiRecordsForMcuPlcService;
    @Resource
    private IBusiRecordsForMcuKdcService busiRecordsForMcuKdcService;
    @Resource
    private IBusiRecordsForMcuSmc3Service busiRecordsForMcuSmc3Service;
    @Resource
    private IBusiRecordsForMcuSmc2Service busiRecordsForMcuSmc2Service;
    @Resource
    private IBusiRecordsForMcuHwcloudService busiRecordsForMcuHwcloudService;

    /**
     * 获取录制文件列表
     *
     * @param conferenceNumber 会议号
     * @return
     */
    @GetMapping("/getFolder")
    @Operation(summary = "获取录制文件列表")
    public RestResponse getFolder(HttpServletRequest request, @RequestParam("conferenceNumber") String conferenceNumber, @RequestParam("deptId") Long deptId, String coSpaceId) {
        List<Map<String, Object>> folder = iRecordingService.getFolder(conferenceNumber, deptId, coSpaceId);
        String host = request.getHeader("Host");
        if (StringUtils.isNotEmpty(host)) {
            if (host.indexOf(":8898") > 0) {
                for (Map<String, Object> map : folder) {
                    try {
                        String url = (String) map.get("url");
                        url = url.replace(":8899", ":8898").replace("https://", "http://");
                        map.put("url", url);
                    } catch (Exception e) {
                    }
                }
            }
        }
        return RestResponse.success(folder);
    }


    /**
     * 开启或关闭会议录制功能
     *
     * @param conferenceId
     * @return
     */
    @PostMapping("/changeRecordingStatus/{conferenceId}")
    @Operation(summary = "开启或关闭会议录制功能", description = "会议录制")
    public RestResponse changeRecordingStatus(@PathVariable String conferenceId, @RequestBody JSONObject jsonObject) {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        boolean flag = jsonObject.getBoolean("recording");
        if (baseConferenceContext != null) {
            if (baseConferenceContext.isApprovedConference()) {
                if (baseConferenceContext.getRecordingEnabled() == YesOrNo.NO.getValue() && flag) {
                    throw new SystemException("当前会议未获取录制许可,不能开启录制！");
                }
            }
        }
        if (baseConferenceContext instanceof ConferenceContext) {
            busiRecordsService.updateBusiRecords(flag, contextKey);
        } else if (baseConferenceContext instanceof McuZjConferenceContext) {
            iBusiRecordsForMcuZjService.updateBusiRecords(flag, contextKey);
        } else if (baseConferenceContext instanceof McuPlcConferenceContext) {
            busiRecordsForMcuPlcService.updateBusiRecords(flag, contextKey);
        } else if (baseConferenceContext instanceof McuKdcConferenceContext) {
            busiRecordsForMcuKdcService.updateBusiRecords(flag, contextKey);
        } else if (baseConferenceContext instanceof Smc3ConferenceContext) {
            busiRecordsForMcuSmc3Service.updateBusiRecords(flag, contextKey);
        }  else if (baseConferenceContext instanceof Smc2ConferenceContext) {
            busiRecordsForMcuSmc2Service.updateBusiRecords(flag, contextKey);
        }  else if (baseConferenceContext instanceof HwcloudConferenceContext) {
            busiRecordsForMcuHwcloudService.updateBusiRecords(flag, contextKey);
        }

        return RestResponse.success();
    }

    /**
     * 根据部门ID和会议名称
     * 获取存在录制文件的会议列表
     * List<File> collect = files.stream().sorted(Comparator.comparing(File::lastModified).reversed()).limit(1).collect(Collectors.toList());
     *
     * @param recordsSearchVo 部门ID
     * @return
     */
    @GetMapping("/getRecordingConferences")
    @Operation(summary = "获取存在录制文件的会议列表")
    public RestResponse getRecordingConferences(RecordsSearchVo recordsSearchVo) {
        return RestResponse.success(iRecordingService.getBusiConferenceNumberVoList(recordsSearchVo));
    }

    /**
     * 删除录制文件
     *
     * @return
     */
    @PostMapping("/deleteRecordingFile/{coSpaceId}")
    @Operation(summary = "删除录制文件", description = "删除录制文件")
    public RestResponse deleteRecordingFile(String id, String fileName, @PathVariable String coSpaceId) {
        logger.info("deleteRecordingFile入参文件名:{},会议号：{}", fileName, coSpaceId);
        iRecordingService.deleteRecordingFile(id, fileName, coSpaceId, false);
        return RestResponse.success();
    }

    /**
     * 获取存在回收站的录制文件列表
     * @return
     */
    @GetMapping("/getReclaimRecordingConferences")
    @Operation(summary = "获取存在回收站的录制文件列表")
    public RestResponse getReclaimRecordingConferences(RecordsSearchVo recordsSearchVo) {
        return RestResponse.success(iRecordingService.getReclaimRecordingConferences(recordsSearchVo));
    }

    /**
     * 恢复回收站的录制文件列表
     * @param id
     * @return
     */
    @PostMapping("/recoverRecordingConferences")
    @Operation(summary = "恢复回收站的录制文件列表", description = "恢复回收站的录制文件")
    public RestResponse recoverRecordingConferences(String id) {
        iRecordingService.recoverRecordingConferences(id);
        return RestResponse.success();
    }

    /**
     * 删除回收站录制文件
     * @param id
     * @return
     */
    @PostMapping("/deleteRecoverRecordingFile")
    @Operation(summary = "删除回收站录制文件", description = "删除回收站录制文件")
    public RestResponse deleteRecoverRecordingFile(String id) {
        logger.info("deleteRecoverRecordingFile入参文件名:{},会议号：{}", id);
        iRecordingService.deleteRecoverRecordingFile(id);
        return RestResponse.success();
    }

}
