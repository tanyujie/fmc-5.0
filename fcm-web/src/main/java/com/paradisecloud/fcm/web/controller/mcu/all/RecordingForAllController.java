package com.paradisecloud.fcm.web.controller.mcu.all;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiRecordsService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
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
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author johnson liu
 * @Date 2021/4/27 10:33
 */
@RestController
@RequestMapping("/busi/mcu/all/recording")
@Tag(name = "会议录制")
public class RecordingForAllController extends BaseController {

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

}
