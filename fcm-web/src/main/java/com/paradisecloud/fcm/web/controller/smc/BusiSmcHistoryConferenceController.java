package com.paradisecloud.fcm.web.controller.smc;

import com.github.pagehelper.PageHelper;
import com.paradisecloud.com.fcm.smc.modle.HistoryConferenceDetail;
import com.paradisecloud.com.fcm.smc.modle.request.BusiSmcAppointmentConferenceQuery;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.paradisecloud.smc.dao.model.BusiSmcHistoryConference;
import com.paradisecloud.smc.service.IBusiSmcHistoryConferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author nj
 * @date 2023/3/14 14:40
 */
@RestController
@RequestMapping("/smc/history")
@Tag(name = "【历史会议】")
public class BusiSmcHistoryConferenceController extends BaseController {



    @Resource
    private IBusiSmcHistoryConferenceService busiSmcHistoryConferenceService;


    /**
     * 查询【历史会议】列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询【请填写功能名称】列表")
    public RestResponse list(BusiSmcAppointmentConferenceQuery query)
    {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize());
        List<BusiSmcHistoryConference> list = busiSmcHistoryConferenceService.selectBusiSmcHistoryConferenceListBySearchKey(query);
        return getDataTable(list);
    }



    /**
     * 查询【历史会议】详情
     */
    @GetMapping("/detail/history/{id}")
    @Operation(summary = "查询【历史会议】详情")
    public RestResponse queryHistoryConferenceDetail(@PathVariable int id)
    {
        BusiSmcHistoryConference historyConference = busiSmcHistoryConferenceService.selectBusiSmcHistoryConferenceById(id);
        if(historyConference!=null){
            SmcBridge smcBridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(historyConference.getDeptId());
            HistoryConferenceDetail detail = smcBridge.getSmcConferencesInvoker().getConferencesHistoryDetailById(historyConference.getConferenceId(), smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            return  RestResponse.success(detail);
        }
        return  RestResponse.success();
    }


    /**
     * 查询【历史会议】告警
     */
    @GetMapping("/alarm/history/{conferenceId}")
    @Operation(summary = "查询【告警】")
    public RestResponse queryHistoryAlarm(@PathVariable String conferenceId, @RequestParam(required = false,defaultValue = "0") int pageIndex,@RequestParam(required = false,defaultValue = "10") int pageSize,@RequestParam(required = false,defaultValue = "alarmTime") String sort)
    {
        SmcBridge smcBridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
        String s=  smcBridge.getSmcConferencesInvoker().getConferencesAlarm(conferenceId,pageIndex,pageSize,sort, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        return  RestResponse.success(s);
    }

}
