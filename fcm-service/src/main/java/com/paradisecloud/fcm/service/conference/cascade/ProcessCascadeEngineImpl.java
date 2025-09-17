package com.paradisecloud.fcm.service.conference.cascade;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nj
 * @date 2023/8/2 10:03
 */
public class ProcessCascadeEngineImpl extends AbstractProcessCascadeEngine {

    private List<Processor> processorList=new ArrayList<>();


    @Override
   public List<Processor> getProcessList() {
        return processorList;
    }

    public void addProcessor(Processor processor){
        processorList.add(processor);
    }


}
