package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiHistoryCall;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;


/**
 * 历史call；busi_history_conference与该是一对多的关系Mapper接口
 *
 * @author lilinhai
 * @date 2021-06-04
 */
public interface BusiHistoryCallMapper {
    /**
     * 查询历史call；busi_history_conference与该是一对多的关系
     *
     * @param id 历史call；busi_history_conference与该是一对多的关系ID
     * @return 历史call；busi_history_conference与该是一对多的关系
     */
    public BusiHistoryCall selectBusiHistoryCallById(Long id);
    
    public BusiHistoryCall selectBusiHistoryCallByCallId(String callId);

    /**
     * 查询历史call；busi_history_conference与该是一对多的关系列表
     *
     * @param busiHistoryCall 历史call；busi_history_conference与该是一对多的关系
     * @return 历史call；busi_history_conference与该是一对多的关系集合
     */
    public List<BusiHistoryCall> selectBusiHistoryCallList(BusiHistoryCall busiHistoryCall);

    /**
     * 新增历史call；busi_history_conference与该是一对多的关系
     *
     * @param busiHistoryCall 历史call；busi_history_conference与该是一对多的关系
     * @return 结果
     */
    public int insertBusiHistoryCall(BusiHistoryCall busiHistoryCall);

    /**
     * 修改历史call；busi_history_conference与该是一对多的关系
     *
     * @param busiHistoryCall 历史call；busi_history_conference与该是一对多的关系
     * @return 结果
     */
    public int updateBusiHistoryCall(BusiHistoryCall busiHistoryCall);

    /**
     * 删除历史call；busi_history_conference与该是一对多的关系
     *
     * @param id 历史call；busi_history_conference与该是一对多的关系ID
     * @return 结果
     */
    public int deleteBusiHistoryCallById(Long id);

    /**
     * 批量删除历史call；busi_history_conference与该是一对多的关系
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiHistoryCallByIds(Long[] ids);

    /**
     * 批量添加记录
     *
     * @param callList
     * @return
     */
    int batchAddHistoryCall(@Param("callList") List<BusiHistoryCall> callList);

    List<BusiHistoryCall> findHistoryCallByCoSpaceAndCall(@Param("callId") String callId, @Param("coSpaceId") String coSpaceId);

    /**
     * 删除日期前的记录
     *
     * @param beforeDate 删除该日期前的数据
     * @return 结果
     */
    int deleteHistory(@Param("beforeDate") Date beforeDate);
}