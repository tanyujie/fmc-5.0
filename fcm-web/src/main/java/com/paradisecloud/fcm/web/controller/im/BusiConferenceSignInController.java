package com.paradisecloud.fcm.web.controller.im;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.dao.model.BusiConferenceOption;
import com.paradisecloud.fcm.dao.model.BusiConferenceSignIn;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceSignInVO;
import com.paradisecloud.im.service.IBusiConferenceQuestionnaireService;
import com.paradisecloud.im.service.IBusiConferenceSignInService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/im/conference/signIn")
@Tag(name = "会议模板的签到")
public class BusiConferenceSignInController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(BusiConferenceSignInController.class);
    @Resource
    private IBusiConferenceSignInService signInService;

    /**
     * 新增会议问卷选项
     * @param option 会议问卷选项信息
     * @return 新增结果
     */
    @Operation(summary = "立即发起签到", responses = {
            @ApiResponse(responseCode = "200", description = "新增成功",
                    content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    @PostMapping("/add")
    public RestResponse add(@RequestBody BusiConferenceSignInVO option) {
        boolean result = signInService.save(option);
        return RestResponse.success(result);
    }

    @Operation(summary = "重新发起签到", responses = {
            @ApiResponse(responseCode = "200", description = "重新发起签到",
                    content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    @PostMapping("/reinit")
    public RestResponse reinit(@RequestBody BusiConferenceSignInVO option) {
        log.info("重新发起会议签到，会议信息：{}", option);
        try {
            // 1. 校验会议是否存在
            if (option.getConferenceId() == null) {
                return RestResponse.fail("会议ID不能为空");
            }

/*            // 2. 结束当前有效的签到
            boolean closeResult = signInService.closeCurrentSignIn(option.getConferenceId());
            if (!closeResult) {
                log.warn("没有找到需要结束的签到记录，会议ID：{}", option.getConferenceId());
                // 可以选择继续或返回失败，根据业务需求决定
            }*/

            // 3. 创建新的签到记录
            boolean createResult = signInService.save(option);

            if (createResult) {
                log.info("重新发起签到成功，会议ID：{}", option.getConferenceId());
                return RestResponse.success("重新发起签到成功");
            } else {
                log.error("创建新签到记录失败，会议ID：{}", option.getConferenceId());
                return RestResponse.fail("创建新签到记录失败");
            }
        } catch (Exception e) {
            log.error("重新发起签到异常", e);
            return RestResponse.fail("重新发起签到失败：" + e.getMessage());
        }
       // return RestResponse.success();
    }
}
