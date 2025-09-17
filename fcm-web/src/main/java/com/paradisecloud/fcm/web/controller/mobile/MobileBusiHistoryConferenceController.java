package com.paradisecloud.fcm.web.controller.mobile;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiHistoryConferenceService;
import com.paradisecloud.fcm.service.interfaces.IBusiHistoryParticipantService;
import com.paradisecloud.fcm.web.utils.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 历史会议，每次挂断会保存该历史记录Controller
 * 
 */
@RestController
@RequestMapping("/mobile/historyConference")
@Tag(name = "历史会议，每次挂断会保存该历史记录")
@Slf4j
public class MobileBusiHistoryConferenceController extends BaseController
{
    @Resource
    private IBusiHistoryConferenceService iBusiHistoryConferenceService;

    @Resource
    private IBusiHistoryParticipantService iBusiHistoryParticipantService;

    /**
     * 按组织、时间范围、会议号搜索
     */
    @GetMapping("/conference/reportByDept")
    @Operation(summary = "历史会议列表")
    public RestResponse reportByDept(ReportSearchVo reportSearchVo) {
        reportSearchVo.setDeptId(AuthenticationUtil.getDeptId());
        log.info("{}/reportByDept方法入参:{}", this.getClass(), reportSearchVo);
        PaginationData<Map<String,Object>> mapList = iBusiHistoryConferenceService.selectHistoryPage(reportSearchVo);
        return RestResponse.success(0L, "查询成功", mapList);
    }


    /**
     * 参会者详情
     *
     * @param hisConferenceId 历史会议id
     * @param isJoin          是否入会
     */
    @GetMapping("/participant/{hisConferenceId}")
    @Operation(summary = "历史与会者列表信息,包含进出时间")
    public RestResponse reportByHisConferenceId(@PathVariable String hisConferenceId, @RequestParam(required = false) Boolean isJoin, @RequestParam(required = false) Integer pageNum, @RequestParam(required = false) Integer pageSize) {
        log.info("{}/reportByHisConferenceId方法入参:{}", this.getClass(), hisConferenceId);
        PaginationData<Map<String,Object>> list = iBusiHistoryParticipantService.reportByHisConferenceId(hisConferenceId, isJoin, pageNum, pageSize);
        return RestResponse.success(list);
    }


}
