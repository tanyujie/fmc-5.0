package com.paradisecloud.fcm.service.conference.cascade;

/**
 * @author nj
 * @date 2023/8/2 9:36
 */
public interface ProcessCascadeEngine {

    void start(ProcessContext context);

    void end(ProcessContext context);
}
