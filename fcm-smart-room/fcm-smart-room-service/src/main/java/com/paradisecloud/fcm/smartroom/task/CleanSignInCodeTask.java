package com.paradisecloud.fcm.smartroom.task;

import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomBookMapper;
import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomParticipantMapper;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomBook;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomParticipant;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomCache;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CleanSignInCodeTask extends Task {

    private boolean cleanAll = false;

    public CleanSignInCodeTask(String id, long delayInMilliseconds, boolean cleanAll) {
        super("clean_sign_code_" + id, delayInMilliseconds);
        this.cleanAll = cleanAll;
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        BusiSmartRoomParticipantMapper busiSmartRoomParticipantMapper = BeanFactory.getBean(BusiSmartRoomParticipantMapper.class);
        BusiSmartRoomBookMapper busiSmartRoomBookMapper = BeanFactory.getBean(BusiSmartRoomBookMapper.class);
        Date now = new Date();
        Date endTime = DateUtils.getDayEndTime(now);
        List<BusiSmartRoomParticipant> busiSmartRoomParticipantList = busiSmartRoomParticipantMapper.selectBusiSmartRoomParticipantListForSignInCodeEnd(endTime);
        BusiSmartRoomBook busiSmartRoomBook = null;
        for (BusiSmartRoomParticipant busiSmartRoomParticipant : busiSmartRoomParticipantList) {
            boolean clean = false;
            Long id = busiSmartRoomParticipant.getId();
            Long bookId = busiSmartRoomParticipant.getBookId();
            String signInCode = busiSmartRoomParticipant.getSignInCode();
            if (bookId != null) {
                if (busiSmartRoomBook == null || busiSmartRoomBook.getId().longValue() != bookId.longValue()) {
                    busiSmartRoomBook = busiSmartRoomBookMapper.selectBusiSmartRoomBookById(bookId);
                }
                if (busiSmartRoomBook != null) {
                    if (busiSmartRoomBook.getEndTime().before(now)) {
                        clean = true;
                    }
                } else {
                    clean = true;
                }
            }
            if (clean) {
                BusiSmartRoomParticipant busiSmartRoomParticipantUpdate = new BusiSmartRoomParticipant();
                busiSmartRoomParticipantUpdate.setId(id);
                int i = busiSmartRoomParticipantMapper.updateBusiSmartRoomParticipant(busiSmartRoomParticipantUpdate);
                if (i > 0) {
                    BusiSmartRoomParticipant busiSmartRoomParticipantUpdated = busiSmartRoomParticipantMapper.selectBusiSmartRoomParticipantById(id);
                    if (StringUtils.isEmpty(busiSmartRoomParticipantUpdated.getSignInCode())) {
                        SmartRoomCache.getInstance().removeSignInCode(signInCode);
                    }
                }
            }
        }
        if (cleanAll) {
            ConcurrentHashMap<String, Long> signInCodeMap = new ConcurrentHashMap<>();
            List<BusiSmartRoomParticipant> busiSmartRoomParticipantListForSignInCode = busiSmartRoomParticipantMapper.selectBusiSmartRoomParticipantListForSignInCode();
            for (BusiSmartRoomParticipant busiSmartRoomParticipant : busiSmartRoomParticipantListForSignInCode) {
                String signInCode = busiSmartRoomParticipant.getSignInCode();
                if (StringUtils.isNotEmpty(signInCode)) {
                    signInCodeMap.put(signInCode, busiSmartRoomParticipant.getId());
                }
            }
            SmartRoomCache.getInstance().setSignInCodeMap(signInCodeMap);
        }
    }
}
