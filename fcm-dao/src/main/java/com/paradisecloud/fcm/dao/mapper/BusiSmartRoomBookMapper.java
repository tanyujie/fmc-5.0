package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiSmartRoomBook;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
/**
 * 智慧办公房间预约Mapper接口
 *
 * @author lilinhai
 * @date 2024-02-19
 */
public interface BusiSmartRoomBookMapper
{
    /**
     * 查询智慧办公房间预约
     *
     * @param id 智慧办公房间预约ID
     * @return 智慧办公房间预约
     */
    public BusiSmartRoomBook selectBusiSmartRoomBookById(Long id);

    /**
     * 查询智慧办公房间预约列表
     *
     * @param busiSmartRoomBook 智慧办公房间预约
     * @return 智慧办公房间预约集合
     */
    public List<BusiSmartRoomBook> selectBusiSmartRoomBookList(BusiSmartRoomBook busiSmartRoomBook);

    /**
     * 新增智慧办公房间预约
     *
     * @param busiSmartRoomBook 智慧办公房间预约
     * @return 结果
     */
    public int insertBusiSmartRoomBook(BusiSmartRoomBook busiSmartRoomBook);

    /**
     * 修改智慧办公房间预约
     *
     * @param busiSmartRoomBook 智慧办公房间预约
     * @return 结果
     */
    public int updateBusiSmartRoomBook(BusiSmartRoomBook busiSmartRoomBook);

    /**
     * 删除智慧办公房间预约
     *
     * @param id 智慧办公房间预约ID
     * @return 结果
     */
    public int deleteBusiSmartRoomBookById(Long id);

    /**
     * 批量删除智慧办公房间预约
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSmartRoomBookByIds(Long[] ids);


    /**
     * 查询会议室预约列表（当前时间以后的预约）
     *
     * @param roomId 会议室ID
     * @param currentTime 当前时间
     * @return 会议室预约集合
     */
    public List<BusiSmartRoomBook> selectBusiSmartRoomBookListForNext(@Param("roomId") Long roomId, @Param("currentTime") Date currentTime);

    /**
     * 查询会议室预约列表（当前时间以后某时间前的预约）
     *
     * @param roomId 会议室ID
     * @param currentTime 当前时间
     * @return 会议室预约集合
     */
    public List<BusiSmartRoomBook> selectBusiSmartRoomBookListForNextTerm(@Param("roomId") Long roomId, @Param("currentTime") Date currentTime, @Param("endTime") Date endTime);

    /**
     * 查询会议室预约列表（会议室某个期间的预约）
     *
     * @param roomId 会议室ID
     * @param startTime 期间开始时间
     * @param endTime 期间结束时间
     * @return 会议室预约集合
     */
    public List<BusiSmartRoomBook> selectBusiSmartRoomBookListForRoomTerm(@Param("roomId") Long roomId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 查询会议室预约
     *
     * @param id
     * @param roomId 会议室预约ID
     * @param startTime
     * @param endTime
     * @return 会议室预约
     */
    public BusiSmartRoomBook selectBusiSmartRoomBookListForCheckExist(@Param("id") Long id, @Param("roomId") Long roomId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 查询会议室预约
     *
     * @param roomId 会议室预约ID
     * @return 会议室预约
     */
    public BusiSmartRoomBook selectBusiSmartRoomBookForCurrent(@Param("roomId") Long roomId, @Param("currentTime") Date currentTime);
}