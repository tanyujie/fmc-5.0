package com.paradisecloud.fcm.web.controller.im;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.fcm.dao.model.BusiConferenceQuestionnaire;
import com.paradisecloud.fcm.dao.model.vo.*;
import com.paradisecloud.im.service.IBusiConferenceQuestionService;
import com.paradisecloud.im.service.IBusiConferenceQuestionnaireRecordService;
import com.paradisecloud.im.service.IBusiConferenceQuestionnaireService;
import com.paradisecloud.system.model.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/im/conference/questionnaire")
@Tag(name = "会议问卷")
public class BusiConferenceQuestionnaireController extends BaseController {
    @Autowired
    private IBusiConferenceQuestionnaireService questionnaireService;
    @Autowired
    private IBusiConferenceQuestionnaireRecordService questionnaireRecordService;

    /**
     * 查询会议问卷主列表
     */
/*    @PreAuthorize("@ss.hasPermi('system:questionnaire:list')")*/
    @PostMapping("/list")
    @Operation(summary = "查询会议问卷主列表")
    public RestResponse list(BusiConferenceQuestionnaire busiConferenceQuestionnaire)
    {
        startPage();
        List<BusiConferenceQuestionnaire> list = questionnaireService.selectBusiConferenceQuestionnaireList(busiConferenceQuestionnaire);
        return getDataTable(list);
    }

    /**
     * 导出会议问卷主列表
     */
/*    @PreAuthorize("@ss.hasPermi('system:questionnaire:export')")*/
    @Log(title = "会议问卷主", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @Operation(summary = "导出会议问卷主列表")
    public RestResponse export(BusiConferenceQuestionnaire busiConferenceQuestionnaire)
    {
        List<BusiConferenceQuestionnaire> list = questionnaireService.selectBusiConferenceQuestionnaireList(busiConferenceQuestionnaire);
        ExcelUtil<BusiConferenceQuestionnaire> util = new ExcelUtil<BusiConferenceQuestionnaire>(BusiConferenceQuestionnaire.class);
        return util.exportExcel(list, "questionnaire");
    }

    /**
     * 获取会议问卷主详细信息
     */
/*    @PreAuthorize("@ss.hasPermi('system:questionnaire:query')")*/
    @PostMapping(value = "/getInfo")
    @Operation(summary = "获取会议问卷主详细信息")
    public RestResponse getInfo(@RequestBody BusiConferenceQuestionnaireVO questionnaireVO)
    {
        return RestResponse.success(questionnaireService.selectBusiConferenceQuestionnaireById(questionnaireVO.getQuestionnaireId()));
    }

    /**
     * 新增会议问卷主
     */
   // @PreAuthorize("@ss.hasPermi('system:questionnaire:add')")
    @Log(title = "会议问卷主", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Operation(summary = "新增会议问卷主")
    public RestResponse add(@RequestBody BusiConferenceQuestionnaireAddVO busiConferenceQuestionnaire)
    {
       return RestResponse.success(questionnaireService.insertBusiConferenceQuestionnaire(busiConferenceQuestionnaire));
    }

    /**
     * 修改会议问卷主
     */
   // @PreAuthorize("@ss.hasPermi('system:questionnaire:edit')")
    @Log(title = "会议问卷主", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    @Operation(summary = "修改会议问卷主")
    public RestResponse edit(@RequestBody BusiConferenceQuestionnaire busiConferenceQuestionnaire)
    {
        return toAjax(questionnaireService.updateBusiConferenceQuestionnaire(busiConferenceQuestionnaire));
    }

    /**
     * 删除会议问卷主
     */
   // @PreAuthorize("@ss.hasPermi('system:questionnaire:remove')")
    @Log(title = "会议问卷主", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    @Operation(summary = "删除会议问卷主")
    public RestResponse remove(@PathVariable Long[] questionnaireIds)
    {
        return toAjax(questionnaireService.deleteBusiConferenceQuestionnaireByIds(questionnaireIds));
    }
    /**
     * 查询会议投票详情
     *
     * @return 投票详情
     */
    @Operation(summary = "查询会议投票详情",
            description = "根据ID获取会议投票项目详细信息")
    @PostMapping("/getPendingQuestionnaireDetail")
    public RestResponse getVotePendingVoteDetail(@Valid @RequestBody BusiConferenceQuestionnaireVO questionnaireVO) {
        BusiConferencePendingQuestionnaireVO pendingQuestionnaireVO = questionnaireService.getPendingQuestionnaireDetail(questionnaireVO);
        return RestResponse.success(pendingQuestionnaireVO);
    }
    @Operation(summary = "添加问卷回答记录")
    @PostMapping("/record/add")
    public RestResponse addVoteRecord(
            @Valid @RequestBody BusiConferenceQuestionnaireRecordAddVO questionnaireRecordAddVO) {
        try {
            // 校验会议是否存在
/*            if (StringUtils.isEmpty(voteRecordAddVO.getConfId())) {
                return RestResponse.fail("会议ID不能为空");
            }*/

            boolean result = questionnaireService.saveQuestionnaireRecords(questionnaireRecordAddVO);
            if (result) {
                return RestResponse.success("添加问卷回答记录成功");
            }
            return RestResponse.fail("添加问卷回答记录失败");
        } catch (Exception e) {
           //log.error("添加投票记录异常", e);
            return RestResponse.fail("添加问卷回答记录失败: " + e.getMessage());
        }
    }
}
