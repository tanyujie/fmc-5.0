package com.paradisecloud.fcm.telep.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.telep.dao.model.BusiTeleParticipant;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author nj
 * @date 2022/10/21 14:18
 */
public class  TeleParticipantCache extends JavaCache<String, BusiTeleParticipant> {

    private static final long serialVersionUID = 1L;
    private static final TeleParticipantCache INSTANCE = new TeleParticipantCache();



    private TeleParticipantCache()
    {

    }

    public static TeleParticipantCache getInstance()
    {
        return INSTANCE;
    }

    public synchronized  BusiTeleParticipant put(String number,String participantName, BusiTeleParticipant value) {
        return super.put(number+participantName, value);
    }

    public synchronized  void removeAll() {
        List<BusiTeleParticipant> copiedAllValues = super.getCopiedAllValues();
        if(!CollectionUtils.isEmpty(copiedAllValues)){
            for (BusiTeleParticipant copiedAllValue : copiedAllValues) {
                TeleParticipantCache.getInstance().remove(copiedAllValue.getConferenceNumber()+copiedAllValue.getParticipantName());

            }
        }

    }

}
