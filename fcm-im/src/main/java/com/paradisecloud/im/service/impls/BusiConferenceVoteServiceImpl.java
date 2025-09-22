package com.paradisecloud.im.service.impls;

import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceVoteOptionVO;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceVoteVO;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IAttendeeForMcuKdcService;
import com.paradisecloud.fcm.service.impls.BusiConferenceNumberSectionServiceImpl;
import com.paradisecloud.im.service.IBusiConferenceVoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Transactional
@Service
public class BusiConferenceVoteServiceImpl implements IBusiConferenceVoteService {
    private final Logger logger = LoggerFactory.getLogger(BusiConferenceVoteServiceImpl.class);

    @Resource
    private BusiConferenceVoteMapper voteMapper;
    @Resource
    private BusiConferenceVoteQuestionMapper questionMapper;
    @Resource
    private BusiConferenceVoteOptionMapper voteOptionMapper;
    @Override
    public boolean save(BusiConferenceVoteVO vo) {
        vo.setVoteCreateTime( (new Date()).getTime());
        vo.setStatus(0);
        vo.setIsAnonymous(1);
        voteMapper.insertBusiConferenceVote(vo);
        if(vo.getQuestionList().size()>0){
            vo.getQuestionList().forEach(e->{
                e.setVoteId(vo.getVoteId());
                questionMapper.insertBusiConferenceVoteQuestion(e);
                if(e.getOptionList().size()>0){
                    e.getOptionList().forEach(o->{
                        o.setQuestionId(e.getQuestionId());
                        voteOptionMapper.insertBusiConferenceVoteOption(o);
                    });
                }


            });


        }

        return true;
    }
}
