package com.paradisecloud.fcm.dao.model.vo;

import com.paradisecloud.fcm.dao.model.BusiConferenceVote;
import com.paradisecloud.fcm.dao.model.BusiConferenceVoteQuestion;
import lombok.Data;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@Data
public class BusiConferenceVoteQuestionVO extends BusiConferenceVoteQuestion {
    private List<BusiConferenceVoteOptionVO> optionList;
}
