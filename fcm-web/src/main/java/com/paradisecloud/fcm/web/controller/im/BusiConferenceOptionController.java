package com.paradisecloud.fcm.web.controller.im;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.dao.model.BusiConferenceOption;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IHwcloudUserService;
import com.paradisecloud.im.service.IBusiConferenceOptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/im/conference/option")
@Tag(name = "会议问卷选项")
public class BusiConferenceOptionController extends BaseController {
    @Resource
    private IBusiConferenceOptionService optionService;
    /**
     * 根据ID查询会议问卷选项
     * @param optionId 选项ID
     * @return 会议问卷选项信息
     */
    @Operation(summary = "根据ID查询会议问卷选项", responses = {
            @ApiResponse(responseCode = "200", description = "查询成功",
                    content = @Content(schema = @Schema(implementation = BusiConferenceOption.class)))
    })
    @GetMapping("/getById")
    public RestResponse getById(
            @Parameter(name = "optionId", description = "选项ID", in = ParameterIn.QUERY, required = true)
            @RequestParam Long optionId) {
        BusiConferenceOption option = optionService.getById(optionId);
        return RestResponse.success(option);
    }

    /**
     * 新增会议问卷选项
     * @param option 会议问卷选项信息
     * @return 新增结果
     */
    @Operation(summary = "新增会议问卷选项", responses = {
            @ApiResponse(responseCode = "200", description = "新增成功",
                    content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    @PostMapping("/add")
    public RestResponse add(@RequestBody BusiConferenceOption option) {
        boolean result = optionService.save(option);
        return RestResponse.success(result);
    }

    /**
     * 修改会议问卷选项
     * @param option 会议问卷选项信息（包含ID）
     * @return 修改结果
     */
    @Operation(summary = "修改会议问卷选项", responses = {
            @ApiResponse(responseCode = "200", description = "修改成功",
                    content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    @PutMapping("/update")
    public RestResponse update(@RequestBody BusiConferenceOption option) {
        boolean result = optionService.updateById(option);
        return RestResponse.success(result);
    }

    /**
     * 根据ID删除会议问卷选项
     * @param optionId 选项ID
     * @return 删除结果
     */
    @Operation(summary = "根据ID删除会议问卷选项", responses = {
            @ApiResponse(responseCode = "200", description = "删除成功",
                    content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    @PostMapping("/delete")
    public RestResponse deleteById(
            @Parameter(name = "optionId", description = "选项ID", in = ParameterIn.QUERY, required = true)
            @RequestParam Long optionId) {
        boolean result = optionService.removeById(optionId);
        return RestResponse.success(result);
    }
}
