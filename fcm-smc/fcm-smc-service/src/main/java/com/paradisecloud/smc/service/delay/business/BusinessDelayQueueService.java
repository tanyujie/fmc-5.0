package com.paradisecloud.smc.service.delay.business;


import com.paradisecloud.smc.service.delay.item.DelayValues;

import java.util.List;

/**
 * @auth nj
 *
 */
public interface BusinessDelayQueueService {
    /**
     * 添加任务
     * @param delayValues
     */
    void add(DelayValues delayValues);

    /**
     * 移除
     * @param delayValues
     */
    void remove(DelayValues delayValues);

    /**
     * 查询
     * @param pageIndex
     * @param pageSize
     * @return
     */
    List<DelayValues> getAll(int pageIndex, int pageSize);

    /**
     * 统计
     * @return
     */
    int count();
}
