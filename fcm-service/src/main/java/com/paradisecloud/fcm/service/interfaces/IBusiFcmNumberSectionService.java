package com.paradisecloud.fcm.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiFcmNumberSection;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;

import java.util.List;

/**
 * 会议号段Service接口
 *
 * @author LiuXiLong
 * @date 2022-02-25
 */
public interface IBusiFcmNumberSectionService
{
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();

    /**
     * 查询会议号段
     *
     * @param id 会议号段ID
     * @return 会议号段
     */
    public BusiFcmNumberSection selectBusiFcmNumberSectionById(Long id);

    /**
     * 查询会议号段列表
     *
     * @param busiFcmNumberSection 会议号段
     * @return 会议号段集合
     */
    public String selectBusiFcmNumberSection(BusiFcmNumberSection busiFcmNumberSection);

    public List<BusiFcmNumberSection> selectBusiFcmNumberSectionList(BusiFcmNumberSection busiFcmNumberSection);

    /**
     * 新增会议号段
     * 
     * @param busiFcmNumberSection 会议号段
     * @return 结果
     */
    public int insertBusiFcmNumberSection(BusiFcmNumberSection busiFcmNumberSection);

    /**
     * 修改会议号段
     * 
     * @param busiFcmNumberSection 会议号段
     * @return 结果
     */
    public int updateBusiFcmNumberSection(BusiFcmNumberSection busiFcmNumberSection);

    /**
     * 批量删除会议号段
     * 
     * @param ids 需要删除的会议号段ID
     * @return 结果
     */
    public int deleteBusiFcmNumberSectionByIds(Long[] ids);

    /**
     * 删除会议号段信息
     * 
     * @param id 会议号段ID
     * @return 结果
     */
    public int deleteBusiFcmNumberSectionById(Long id);
}
