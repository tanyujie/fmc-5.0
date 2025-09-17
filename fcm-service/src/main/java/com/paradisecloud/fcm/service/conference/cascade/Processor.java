package com.paradisecloud.fcm.service.conference.cascade;

/**
 * @author nj
 * @date 2023/8/2 9:42
 */
public interface Processor {

    void execute(ProcessContext processContext);

    Boolean needExecute(ProcessContext processContext);

    void end(ProcessContext processContext);
}
