package com.paradisecloud.fcm.web.controller.im;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.fcm.dao.model.BusiConferenceQuestionnaire;
import com.paradisecloud.im.service.IBusiConferenceQuestionService;
import com.paradisecloud.im.service.IBusiConferenceQuestionnaireService;
import com.paradisecloud.system.model.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/im/conference/questionnaire")
@Tag(name = "会议问卷")
public class BusiConferenceQuestionnaireController extends BaseController {
    @Autowired
    private IBusiConferenceQuestionnaireService busiConferenceQuestionnaireService;

    /**
     * 查询会议问卷主列表
     */
/*    @PreAuthorize("@ss.hasPermi('system:questionnaire:list')")*/
    @GetMapping("/list")
    @Operation(summary = "查询会议问卷主列表")
    public RestResponse list(BusiConferenceQuestionnaire busiConferenceQuestionnaire)
    {
        startPage();
        List<BusiConferenceQuestionnaire> list = busiConferenceQuestionnaireService.selectBusiConferenceQuestionnaireList(busiConferenceQuestionnaire);
        return getDataTable(list);
    }

    /**
     * 导出会议问卷主列表
     */
/*    @PreAuthorize("@ss.hasPermi('system:questionnaire:export')")*/
    @Log(title = "会议问卷主", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "导出会议问卷主列表")
    public RestResponse export(BusiConferenceQuestionnaire busiConferenceQuestionnaire)
    {
        List<BusiConferenceQuestionnaire> list = busiConferenceQuestionnaireService.selectBusiConferenceQuestionnaireList(busiConferenceQuestionnaire);
        ExcelUtil<BusiConferenceQuestionnaire> util = new ExcelUtil<BusiConferenceQuestionnaire>(BusiConferenceQuestionnaire.class);
        return util.exportExcel(list, "questionnaire");
    }

    /**
     * 获取会议问卷主详细信息
     */
/*    @PreAuthorize("@ss.hasPermi('system:questionnaire:query')")*/
    @GetMapping(value = "/{questionnaireId}")
    @Operation(summary = "获取会议问卷主详细信息")
    public RestResponse getInfo(@PathVariable("questionnaireId") Long questionnaireId)
    {
        return RestResponse.success(busiConferenceQuestionnaireService.selectBusiConferenceQuestionnaireById(questionnaireId));
    }

    /**
     * 新增会议问卷主
     */
   // @PreAuthorize("@ss.hasPermi('system:questionnaire:add')")
    @Log(title = "会议问卷主", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增会议问卷主")
    public RestResponse add(@RequestBody BusiConferenceQuestionnaire busiConferenceQuestionnaire)
    {
        return toAjax(busiConferenceQuestionnaireService.insertBusiConferenceQuestionnaire(busiConferenceQuestionnaire));
    }

    /**
     * 修改会议问卷主
     */
   // @PreAuthorize("@ss.hasPermi('system:questionnaire:edit')")
    @Log(title = "会议问卷主", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改会议问卷主")
    public RestResponse edit(@RequestBody BusiConferenceQuestionnaire busiConferenceQuestionnaire)
    {
        return toAjax(busiConferenceQuestionnaireService.updateBusiConferenceQuestionnaire(busiConferenceQuestionnaire));
    }

    /**
     * 删除会议问卷主
     */
   // @PreAuthorize("@ss.hasPermi('system:questionnaire:remove')")
    @Log(title = "会议问卷主", businessType = BusinessType.DELETE)
    @DeleteMapping("/{questionnaireIds}")
    @Operation(summary = "删除会议问卷主")
    public RestResponse remove(@PathVariable Long[] questionnaireIds)
    {
        return toAjax(busiConferenceQuestionnaireService.deleteBusiConferenceQuestionnaireByIds(questionnaireIds));
    }
}
