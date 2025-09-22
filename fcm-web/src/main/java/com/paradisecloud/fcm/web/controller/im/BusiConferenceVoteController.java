package com.paradisecloud.fcm.web.controller.im;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.TableDataInfo;
import com.paradisecloud.fcm.dao.model.vo.BusiConferencePendingVoteVO;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceSignInVO;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceVoteVO;
import com.paradisecloud.im.service.IBusiConferenceUserSignInService;
import com.paradisecloud.im.service.IBusiConferenceVoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/im/conference/vote")
@Tag(name = "会议投票")
public class BusiConferenceVoteController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(BusiConferenceVoteController.class);
    @Resource
    private IBusiConferenceVoteService voteService;
    /**
     * 添加会议投票
     * @param voteVO 会议投票信息
     * @return 操作结果
     */
    @Operation(summary = "添加会议投票",
            description = "创建新的会议投票项目")
    @PostMapping("/add")
    public RestResponse addVote(
            @Valid @RequestBody BusiConferenceVoteVO voteVO) {
        log.info("添加会议投票: {}", voteVO);
        try {
            // 校验会议是否存在
            if (StringUtils.isEmpty(voteVO.getConfId())) {
                return RestResponse.fail("会议ID不能为空");
            }

            boolean result = voteService.save(voteVO);
            if (result) {
                return RestResponse.success("会议投票添加成功");
            }
            return RestResponse.fail("会议投票添加失败");
        } catch (Exception e) {
            log.error("添加会议投票异常", e);
            return RestResponse.fail("添加会议投票失败: " + e.getMessage());
        }
    }

    /**
     * 删除会议投票
     * @param params 包含投票ID的参数
     * @return 操作结果
     */
    @Operation(summary = "删除会议投票",
            description = "根据ID删除会议投票项目")
    @PostMapping("/delete")
    public RestResponse deleteVote(
            @Parameter(description = "包含voteId的参数", required = true)
            @RequestBody Map<String, String> params) {
        String voteId = params.get("voteId");
        log.info("删除会议投票, ID: {}", voteId);

        return RestResponse.success();
    }

    /**
     * 修改会议投票
     * @param voteVO 会议投票信息
     * @return 操作结果
     */
    @Operation(summary = "修改会议投票",
            description = "更新会议投票项目信息")
    @PostMapping("/update")
    public RestResponse updateVote(
            @Valid @RequestBody BusiConferenceVoteVO voteVO) {
        log.info("修改会议投票: {}", voteVO);
/*        try {
            if (voteVO.getId() == null) {
                return RestResponse.fail("投票ID不能为空");
            }

            boolean result = voteService.updateById(voteVO);
            if (result) {
                return RestResponse.success(true, "会议投票修改成功");
            }
            return RestResponse.fail("会议投票不存在或修改失败");
        } catch (Exception e) {
            log.error("修改会议投票异常", e);
            return RestResponse.fail("修改会议投票失败: " + e.getMessage());
        }*/
        return RestResponse.success();
    }

    /**
     * 查询会议投票详情
     * @param params 包含投票ID的参数
     * @return 投票详情
     */
    @Operation(summary = "查询会议投票详情",
            description = "根据ID获取会议投票项目详细信息")
    @PostMapping("/getPendingVoteDetail")
    public RestResponse getVotePendingVoteDetail(@Valid @RequestBody BusiConferenceVoteVO voteVO) {
        BusiConferencePendingVoteVO pendingVoteVO=voteService.getPendingVoteDetail(voteVO);
        return RestResponse.success(pendingVoteVO);
    }

    /**
     * 查询会议投票列表
     * @param params 包含会议ID的参数
     * @return 投票列表
     */
    @Operation(summary = "查询会议投票列表",
            description = "根据会议ID获取该会议下的所有投票项目")
    @PostMapping("/list")
    public RestResponse getVoteList(
            @Parameter(description = "包含conferenceId的参数", required = true)
            @RequestBody Map<String, Long> params) {
        Long conferenceId = params.get("conferenceId");
        log.info("查询会议投票列表, 会议ID: {}", conferenceId);
/*
        try {
            if (conferenceId == null) {
                return RestResponse.fail("会议ID不能为空");
            }

            // 分页查询当前会议下的所有投票
            startPage();
            TableDataInfo dataInfo = voteService.selectVoteList(conferenceId);
            return RestResponse.success(dataInfo);
        } catch (Exception e) {
            log.error("查询会议投票列表异常, 会议ID: {}", conferenceId, e);
            return RestResponse.fail("查询会议投票列表失败: " + e.getMessage());
        }*/
        return RestResponse.success();
    }

}
