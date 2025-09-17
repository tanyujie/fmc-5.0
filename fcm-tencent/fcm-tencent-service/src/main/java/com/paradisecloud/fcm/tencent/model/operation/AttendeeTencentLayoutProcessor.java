package com.paradisecloud.fcm.tencent.model.operation;

import com.paradisecloud.fcm.tencent.busi.attende.AttendeeTencent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nj
 * @date 2024/2/1 15:48
 */
public interface AttendeeTencentLayoutProcessor {

    void processElement(Map<String, AttendeeTencent> batchMap);
    public static void processMapInBatches(Map<String, AttendeeTencent> inputMap,AttendeeTencentLayoutProcessor processor, int batchSize) {
        int totalElements = inputMap.size();
        int processedElements = 0;

        while (processedElements < totalElements) {
            int batchSizeToProcess = Math.min(batchSize, totalElements - processedElements);
            Map<String, AttendeeTencent> batchMap = getBatch(inputMap, processedElements, batchSizeToProcess);

            processor.processElement(batchMap);

            processedElements += batchSizeToProcess;
        }
    }

    private static Map<String, AttendeeTencent> getBatch(Map<String, AttendeeTencent> inputMap, int start, int batchSize) {
        Map<String, AttendeeTencent> batchMap = new HashMap<>();
        int count = 0;

        for (Map.Entry<String, AttendeeTencent> entry : inputMap.entrySet()) {
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
