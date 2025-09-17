package com.paradisecloud.fcm.fme.conference.interfaces;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.BusiRecords;

/**
 * 录制文件记录Service接口
 *
 * @author lilinhai
 * @date 2021-05-07
 */
public interface IBusiRecordsService {
    /**
     * 查询录制文件记录
     *
     * @param id 录制文件记录ID
     * @return 录制文件记录
     */
    public BusiRecords selectBusiRecordsById(Long id);

    /**
     * 查询录制文件记录列表
     *
     * @param busiRecords 录制文件记录
     * @return 录制文件记录集合
     */
    public List<BusiRecords> selectBusiRecordsList(BusiRecords busiRecords);

    /**
     * 新增录制文件记录
     *
     * @param busiRecords 录制文件记录
     * @return 结果
     */
    public int insertBusiRecords(BusiRecords busiRecords);

    /**
     * 修改录制文件记录
     *
     * @param busiRecords 录制文件记录
     * @return 结果
     */
    public int updateBusiRecords(BusiRecords busiRecords);

    /**
     * 修改录制文件记录
     * @param recording 是否开启
     * @param contextKey
     * @return 结果
     */
    public int updateBusiRecords(boolean recording,String contextKey);

    /**
     * 批量删除录制文件记录
     *
     * @param ids 需要删除的录制文件记录ID
     * @return 结果
     */
    public int deleteBusiRecordsByIds(Long[] ids);

    /**
     * 删除录制文件记录信息
     *
     * @param id 录制文件记录ID
     * @return 结果
     */
    public int deleteBusiRecordsById(Long id);

    /**
     * 查询录制文件记录列表
     *
     * @param busiRecords 录制文件记录
     * @return 录制文件记录集合
     */
     List<BusiRecords> selectBusiRecordsListGroup(String searchKey, int pageIndex, int pageSize);

    Map<String, Object> getRecordFileInfo(Long deptId, String coSpaceId, String contextKey);
}
