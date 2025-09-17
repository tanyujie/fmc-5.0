package com.paradisecloud.fcm.fme.service.interfaces;

import java.util.List;
import java.util.Map;

/**
 * @author johnson liu
 * @date 2021/4/30 11:30
 */
public interface IAllRecordingService {

    /**
     * 获取录制文件列表
     *
     * @param conferenceNumber
     * @return
     */
    List<Map<String, Object>> getFolder(String conferenceNumber, String coSpaceId);

    /**
     * 删除录制文件或文件夹
     *
     * @param ids
     * @param fileName
     * @param coSpaceId
     */
    void deleteRecordingFile(String ids, String fileName, String coSpaceId);
}
