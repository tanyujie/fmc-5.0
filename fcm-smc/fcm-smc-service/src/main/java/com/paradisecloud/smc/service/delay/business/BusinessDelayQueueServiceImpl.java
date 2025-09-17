package com.paradisecloud.smc.service.delay.business;


import com.paradisecloud.smc.service.delay.item.DelayValues;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author nj
 */
@Service("businessDelayQueueService")
public class BusinessDelayQueueServiceImpl implements BusinessDelayQueueService {



    @Override
    public void add(DelayValues values) {


    }

    @Override
    public void remove(DelayValues values) {

    }

    @Override
    public List<DelayValues> getAll(int pageIndex, int pageSize) {
       return null;

    }

    @Override
    public int count() {
        return 0;
    }
}
