package com.paradisecloud.fcm.dao.mapper;
import com.paradisecloud.fcm.dao.model.BusiTencentHistoryConference;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * tencent会议历史Mapper接口
 *
 * @author lilinhai
 * @date 2023-07-07
 */
public interface BusiTencentHistoryConferenceMapper
{
    /**
     * 查询tencent会议历史
     *
     * @param id tencent会议历史ID
     * @return tencent会议历史
     */
    public BusiTencentHistoryConference selectBusiTencentHistoryConferenceById(Integer id);

    /**
     * 查询tencent会议历史列表
     *
     * @param busiTencentHistoryConference tencent会议历史
     * @return tencent会议历史集合
     */
    public List<BusiTencentHistoryConference> selectBusiTencentHistoryConferenceList(BusiTencentHistoryConference busiTencentHistoryConference);

    /**
     * 新增tencent会议历史
     *
     * @param busiTencentHistoryConference tencent会议历史
     * @return 结果
     */
    public int insertBusiTencentHistoryConference(BusiTencentHistoryConference busiTencentHistoryConference);

    /**
     * 修改tencent会议历史
     *
     * @param busiTencentHistoryConference tencent会议历史
     * @return 结果
     */
    public int updateBusiTencentHistoryConference(BusiTencentHistoryConference busiTencentHistoryConference);

    /**
     * 删除tencent会议历史
     *
     * @param id tencent会议历史ID
     * @return 结果
     */
    public int deleteBusiTencentHistoryConferenceById(Integer id);

    /**
     * 批量删除tencent会议历史
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiTencentHistoryConferenceByIds(Integer[] ids);

    public List<BusiTencentHistoryConference> selectBusiSmcHistoryConferenceNotTemplate(@Param("searchKey")String searchKey, @Param("ids") Set<Long> ids);

    List<BusiTencentHistoryConference> selectBusiTencentHistoryConferenceNotTemplate(@Param("searchKey")String  searchKey);
}