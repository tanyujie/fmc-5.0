package com.paradisecloud.fcm.dao.mapper;

import com.github.pagehelper.Page;
import com.paradisecloud.fcm.dao.model.BusiRecords;
import com.paradisecloud.fcm.dao.model.BusiRecordsSearchResult;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 录制文件记录Mapper接口
 *
 * @author lilinhai
 * @date 2021-05-07
 */
public interface BusiRecordsMapper {
    /**
     * 查询录制文件记录
     *
     * @param id 录制文件记录ID
     * @return 录制文件记录
     */
    public BusiRecords selectBusiRecordsById(Long id);

    /**
     * 是否查询最新的录制文件记录
     * @param isLast 是否查询最新的一条记录
     * @param coSpaceId
     * @return
     */
    List<BusiRecords> selectBusiRecordsByCoSpaceId(@Param("deptId") Long deptId, @Param("coSpaceId") String coSpaceId, @Param("isLast") boolean isLast);

    List<BusiRecords> selectIsDelect(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    List<BusiRecords> selectByRetentionTime(@Param("retentionTime") Date retentionTime);

    List<BusiRecords> selectReclaim(@Param("retentionTime") Date retentionTime);

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
     * 删除录制文件记录
     *
     * @param id 录制文件记录ID
     * @return 结果
     */
    public int deleteBusiRecordsById(Long id);

    /**
     * 批量删除录制文件记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiRecordsByIds(Long[] ids);

    /**
     * 根据删除录制文件记录
     * @param coSpaceId
     * @return
     */
    int deleteRecordsByCoSpaceId(String coSpaceId);

     List<BusiRecords> selectBusiRecordsListGroup(BusiRecords busiRecords);

    /**
     * 根据部门，搜索关键字查询录制记录
     * @param deptId 部门ID
     * @param searchKey 搜索关键字
     * @return
     */
    List<BusiRecordsSearchResult> selectBusiRecordsListForGroup(@Param("deptId") Long deptId, @Param("searchKey") String searchKey);

    List<BusiRecords> selectReclaimRecordsList(@Param("deptId") Long deptId, @Param("searchKey") String searchKey);

    int recoverBusiRecords(BusiRecords busiRecords);

    /**
     * 根据部门，搜索关键字查询录制记录
     * @param deptId 部门ID
     * @param searchKey 搜索关键字
     * @param userId 用户id
     * @return
     */
    List<BusiRecordsSearchResult> selectBusiRecordsListForGroupForMyConference(@Param("deptId") Long deptId, @Param("searchKey") String searchKey, @Param("userId") Long userId);

    /**
     * 根据部门，搜索关键字查询录制记录
     * @param deptId 部门ID
     * @param searchKey 搜索关键字
     * @param terminalId 终端id
     * @return
     */
    List<BusiRecordsSearchResult> selectBusiRecordsListForGroupForMyJoinedConference(@Param("deptId") Long deptId, @Param("searchKey") String searchKey, @Param("terminalId") Long terminalId);

    /**
     * 查询我创建的会议的录制文件记录
     * @param isLast 是否查询最新的一条记录
     * @param coSpaceId
     * @param userId 用户id
     * @return
     */
    List<BusiRecords> selectBusiRecordsByCoSpaceIdForMyConference(@Param("deptId") Long deptId, @Param("coSpaceId") String coSpaceId, @Param("isLast") boolean isLast, @Param("userId") Long userId);

    /**
     * 查询我参与的会议的录制文件记录
     * @param isLast 是否查询最新的一条记录
     * @param coSpaceId
     * @param terminalId 终端id
     * @return
     */
    List<BusiRecords> selectBusiRecordsByCoSpaceIdForMyJoinedConference(@Param("deptId") Long deptId, @Param("coSpaceId") String coSpaceId, @Param("isLast") boolean isLast, @Param("terminalId") Long terminalId);
}
