package com.paradisecloud.smc.service.delay.service;

/**
 * 任务执行器
 * @author nj
 */
public interface DelayService {
    /**
     * 执行
     * @param id
     * @return
     */
    boolean execute(String id);
}
