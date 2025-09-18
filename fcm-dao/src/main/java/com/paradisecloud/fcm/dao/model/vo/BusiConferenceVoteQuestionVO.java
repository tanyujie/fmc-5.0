package com.paradisecloud.fcm.dao.model.vo;

import lombok.Data;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@Data
public class BusiConferenceVoteQuestionVO {
    private List<BusiConferenceVoteOptionVO> questionList;
}
