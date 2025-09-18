package com.paradisecloud.fcm.web.controller.im;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.dao.model.BusiConferenceUserSignIn;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceSignInVO;
import com.paradisecloud.im.service.IBusiConferenceUserService;
import com.paradisecloud.im.service.IBusiConferenceUserSignInService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/im/conference/user/signIn")
@Tag(name = "成员签到关联")
public class BusiConferenceUserSignInController extends BaseController {
    @Resource
    private IBusiConferenceUserSignInService userSignInService;
    /**
     * 新增会议问卷选项
     * @param userSignIn 会议问卷选项信息
     * @return 新增结果
     */
    @Operation(summary = "成员签到", responses = {
            @ApiResponse(responseCode = "200", description = "新增成员签到",
                    content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    @PostMapping("/add")
    public RestResponse add(@RequestBody BusiConferenceUserSignIn userSignIn) {
        boolean result = userSignInService.save(userSignIn);
        return RestResponse.success(result);
    }
}
