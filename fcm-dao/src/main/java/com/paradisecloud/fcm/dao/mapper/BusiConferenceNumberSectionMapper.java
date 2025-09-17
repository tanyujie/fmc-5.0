package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.paradisecloud.fcm.dao.model.BusiConferenceNumberSection;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;

/**
 * 会议号段Mapper接口
 * 
 * @author lilinhai
 * @date 2021-05-19
 */
public interface BusiConferenceNumberSectionMapper 
{
    /**
     * 查询会议号段
     * 
     * @param id 会议号段ID
     * @return 会议号段
     */
    public BusiConferenceNumberSection selectBusiConferenceNumberSectionById(Long id);

    /**
     * 查询会议号段列表
     * 
     * @param busiConferenceNumberSection 会议号段
     * @return 会议号段集合
     */
    public List<BusiConferenceNumberSection> selectBusiConferenceNumberSectionList(BusiConferenceNumberSection busiConferenceNumberSection);

    /**
     * 新增会议号段
     * 
     * @param busiConferenceNumberSection 会议号段
     * @return 结果
     */
    public int insertBusiConferenceNumberSection(BusiConferenceNumberSection busiConferenceNumberSection);

    /**
     * 修改会议号段
     * 
     * @param busiConferenceNumberSection 会议号段
     * @return 结果
     */
    public int updateBusiConferenceNumberSection(BusiConferenceNumberSection busiConferenceNumberSection);
    
    int countSection(@Param("val") Long val, @Param("id") Long id);

    /**
     * 删除会议号段
     * 
     * @param id 会议号段ID
     * @return 结果
     */
    public int deleteBusiConferenceNumberSectionById(Long id);

    /**
     * 批量删除会议号段
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiConferenceNumberSectionByIds(Long[] ids);
    
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();
}
