package com.paradisecloud.fcm.service.conference.cascade;

import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author nj
 * @date 2023/8/2 9:45
 */
public abstract class AbstractProcessCascadeEngine implements ProcessCascadeEngine {

    @Override
    public void start(ProcessContext context) {

        List<Processor> processList = getProcessList();
        if (CollectionUtils.isEmpty(processList)) {
            return;
        }

        processList.forEach(processor -> {
            try {
                if (processor.needExecute(context)) {
                    processor.execute(context);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public void end(ProcessContext context) {

        List<Processor> processList = getProcessList();
        if (CollectionUtils.isEmpty(processList)) {
            return;
        }

        processList.forEach(processor -> {
            try {
                processor.end(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    abstract List<Processor> getProcessList();
}
