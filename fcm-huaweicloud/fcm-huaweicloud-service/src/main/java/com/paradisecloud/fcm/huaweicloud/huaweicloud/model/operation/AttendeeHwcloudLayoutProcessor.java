package com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation;


import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.AttendeeHwcloud;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nj
 * @date 2024/2/1 15:48
 */
public interface AttendeeHwcloudLayoutProcessor {

    void processElement(Map<String, AttendeeHwcloud> batchMap);
    public static void processMapInBatches(Map<String, AttendeeHwcloud> inputMap, AttendeeHwcloudLayoutProcessor processor, int batchSize) {
        int totalElements = inputMap.size();
        int processedElements = 0;

        while (processedElements < totalElements) {
            int batchSizeToProcess = Math.min(batchSize, totalElements - processedElements);
            Map<String, AttendeeHwcloud> batchMap = getBatch(inputMap, processedElements, batchSizeToProcess);

            processor.processElement(batchMap);

            processedElements += batchSizeToProcess;
        }
    }

    private static Map<String, AttendeeHwcloud> getBatch(Map<String, AttendeeHwcloud> inputMap, int start, int batchSize) {
        Map<String, AttendeeHwcloud> batchMap = new HashMap<>();
        int count = 0;

        for (Map.Entry<String, AttendeeHwcloud> entry : inputMap.entrySet()) {
            if (count >= start && count < start + batchSize) {
                batchMap.put(entry.getKey(), entry.getValue());
            }
            count++;

            if (count >= start + batchSize) {
                break;
            }
        }

        return batchMap;
    }
}
