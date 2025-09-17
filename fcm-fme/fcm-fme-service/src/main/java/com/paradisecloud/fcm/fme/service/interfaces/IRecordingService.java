package com.paradisecloud.fcm.fme.service.interfaces;

import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.fcm.dao.model.vo.RecordsSearchVo;
import com.paradisecloud.fcm.fme.service.impls.vo.BusiConferenceNumberVo;

import java.util.List;
import java.util.Map;

/**
 * @author johnson liu
 * @date 2021/4/30 11:30
 */
public interface IRecordingService {
    /**
     * 获取录制文件列表
     *
     * @param conferenceNumber
     * @return
     */
    List<Map<String, Object>> getFolder(String conferenceNumber, Long deptId, String coSpaceId);

    /**
     * 删除录制文件或文件夹
     *
     * @param ids
     * @param fileName
     * @param coSpaceId
     */
    void deleteRecordingFile(String ids, String fileName, String coSpaceId, boolean force);

    PaginationData<BusiConferenceNumberVo> getBusiConferenceNumberVoList(RecordsSearchVo recordsSearchVo);

    /**
     * 首页录制文件空间统计
     *
     * @param deptId
     * @return
     */
    Map<String, Object> reportRecordSpace(Long deptId);

    /**
     * 获取存在回收站的录制文件列表
     * @param recordsSearchVo
     * @return
     */
    PaginationData<BusiConferenceNumberVo> getReclaimRecordingConferences(RecordsSearchVo recordsSearchVo);

    /**
     * 恢复回收站的录制文件列表
     * @param ids
     * @param coSpaceId
     * @return
     */
    void recoverRecordingConferences(String ids);

    /**
     * 删除回收站录制文件
     * @param ids
     * @param fileName
     * @param coSpaceId
     */
    void deleteRecoverRecordingFile(String ids);
}
