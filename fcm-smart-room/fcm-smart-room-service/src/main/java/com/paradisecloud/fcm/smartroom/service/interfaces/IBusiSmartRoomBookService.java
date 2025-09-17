package com.paradisecloud.fcm.smartroom.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiSmartRoomBook;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomBookVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 会议室预约Service接口
 * 
 * @author lilinhai
 * @date 2024-01-26
 */
public interface IBusiSmartRoomBookService
{
    /**
     * 查询会议室预约
     * 
     * @param id 会议室预约ID
     * @return 会议室预约
     */
    public BusiSmartRoomBook selectBusiSmartRoomBookById(Long id);

    /**
     * 查询会议室预约列表
     * 
     * @param busiSmartRoomBookVo 会议室预约
     * @return 会议室预约集合
     */
    public List<BusiSmartRoomBook> selectBusiSmartRoomBookList(BusiSmartRoomBookVo busiSmartRoomBookVo);

    /**
     * 查询会议室预约列表
     *
     * @param roomId 会议室预约
     * @param currentTime
     * @return 会议室预约集合
     */
    public List<BusiSmartRoomBook> selectBusiSmartRoomBookListForNext(Long roomId, Date currentTime);

    /**
     * 查询会议室预约列表
     *
     * @param roomId 会议室预约
     * @param currentTime
     * @param endTime
     * @return 会议室预约集合
     */
    public List<BusiSmartRoomBook> selectBusiSmartRoomBookListForNextTerm(Long roomId, Date currentTime, Date endTime);

    /**
     * 查询当前正在召开的会议
     *
     * @param roomId
     * @param currentTime
     * @return
     */
    public BusiSmartRoomBook selectBusiSmartRoomBookForCurrent(Long roomId, Date currentTime);

    /**
     * 新增会议室预约
     * 
     * @param busiSmartRoomBook 会议室预约
     * @return 结果
     */
    public int insertBusiSmartRoomBook(BusiSmartRoomBook busiSmartRoomBook);

    /**
     * 修改会议室预约
     * 
     * @param busiSmartRoomBook 会议室预约
     * @return 结果
     */
    public int updateBusiSmartRoomBook(BusiSmartRoomBook busiSmartRoomBook);

    /**
     * 批量取消会议室预约
     * 
     * @param ids 需要删除的会议室预约ID
     * @return 结果
     */
    public int cancelBusiSmartRoomBookByIds(Long[] ids);

    /**
     * 取消会议室预约信息
     * 
     * @param id 会议室预约ID
     * @return 结果
     */
    public int cancelBusiSmartRoomBookById(Long id);

    /**
     * 结束使用会议室预约信息
     *
     * @param id 会议室预约ID
     * @return 结果
     */
    public int endBusiSmartRoomBookById(Long id);

    /**
     * 获取会议室期间预约日预约列表
     *
     * @param roomId
     * @param yearMonth 月份
     * @return
     */
    public List<Map<String, Object>> selectBusiSmartRoomBookListForRoomTerm(Long roomId, Date yearMonth);

    /**
     * 获取会议室预约日预约列表
     *
     * @param roomId
     * @param dateDay 日
     * @return
     */
    public List<BusiSmartRoomBook> selectBusiSmartRoomBookListForRoomDay(Long roomId, Date dateDay);

    /**
     * 延长预约时间
     * @param id
     * @param minutes
     * @return
     */
    int extendMinutes(Long id, Integer minutes);

    /**
     * 更新数据库和缓存
     */
    public int updateBusiSmartRoomBookData(BusiSmartRoomBook busiSmartRoomBook);
}
