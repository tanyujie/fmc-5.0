package com.paradisecloud.fcm.web.controller.im;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceVoteRecordVO;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceVoteVO;
import com.paradisecloud.im.service.IBusiConferenceVoteQuestionService;
import com.paradisecloud.im.service.IBusiConferenceVoteRecordService;
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
import javax.validation.Valid;

@RestController
@RequestMapping("/im/conference/vote/record")
@Tag(name = "会议投票记录")
public class BusiConferenceVoteRecordController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(BusiConferenceVoteController.class);
    @Resource
    private IBusiConferenceVoteRecordService voteRecordService;
    /**
     * 添加会议投票
     * @param voteRecordVO 会议投票信息
     * @return 操作结果
     */
    @Operation(summary = "添加投票记录",
            description = "创建新的投票记录")
    @PostMapping("/add")
    public RestResponse addVote(
            @Valid @RequestBody BusiConferenceVoteRecordVO voteRecordVO) {
        log.info("添加投票记录: {}", voteRecordVO);
        try {
            boolean result = voteRecordService.save(voteRecordVO);
            if (result) {
                return RestResponse.success("投票记录添加成功");
            }
            return RestResponse.fail("投票记录添加失败");
        } catch (Exception e) {
            log.error("添加投票记录异常", e);
            return RestResponse.fail("添加投票记录失败: " + e.getMessage());
        }
    }
}
