package com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces;

/**
 * 录制文件记录Service接口
 *
 * @author lilinhai
 * @date 2021-05-07
 */
public interface IBusiRecordsForMcuHwcloudService {

    /**
     * 修改录制文件记录
     * @param recording 是否开启
     * @param contextKey
     * @return 结果
     */
    int updateBusiRecords(boolean recording,String contextKey);
}
