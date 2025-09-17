package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiFcmNumberSection;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会议号段Mapper接口
 * 
 * @author LiuXiLong
 * @date 2022-02-25
 */
public interface BusiFcmNumberSectionMapper
{
/**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54
     * @param businessFieldType
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

    public List<BusiFcmNumberSection> selectBusiFcmNumberSectionVo();
    /**
     * 查询会议号段列表
     * 
     * @param busiFcmNumberSection 会议号段
     * @return 会议号段集合
     */
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

    int countSection(@Param("val") Long val, @Param("id") Long id);

    /**
     * 删除会议号段
     * 
     * @param id 会议号段ID
     * @return 结果
     */
    public int deleteBusiFcmNumberSectionById(Long id);

    /**
     * 批量删除会议号段
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiFcmNumberSectionByIds(Long[] ids);
}
