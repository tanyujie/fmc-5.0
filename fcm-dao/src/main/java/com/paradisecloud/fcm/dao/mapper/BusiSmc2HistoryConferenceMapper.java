package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiSmc2HistoryConference;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author lilinhai
 * @date 2023-05-17
 */
public interface BusiSmc2HistoryConferenceMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public BusiSmc2HistoryConference selectBusiSmc2HistoryConferenceById(Integer id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busiSmc2HistoryConference 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<BusiSmc2HistoryConference> selectBusiSmc2HistoryConferenceList(BusiSmc2HistoryConference busiSmc2HistoryConference);

    /**
     * 新增【请填写功能名称】
     * 
     * @param busiSmc2HistoryConference 【请填写功能名称】
     * @return 结果
     */
    public int insertBusiSmc2HistoryConference(BusiSmc2HistoryConference busiSmc2HistoryConference);

    /**
     * 修改【请填写功能名称】
     * 
     * @param busiSmc2HistoryConference 【请填写功能名称】
     * @return 结果
     */
    public int updateBusiSmc2HistoryConference(BusiSmc2HistoryConference busiSmc2HistoryConference);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmc2HistoryConferenceById(Integer id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSmc2HistoryConferenceByIds(Integer[] ids);

    public List<BusiSmc2HistoryConference> selectBusiSmcHistoryConferenceNotTemplate(@Param("searchKey")String searchKey, @Param("ids")Set<Long> ids);

}
