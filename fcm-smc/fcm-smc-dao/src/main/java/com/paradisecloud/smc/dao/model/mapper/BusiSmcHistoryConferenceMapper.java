package com.paradisecloud.smc.dao.model.mapper;
import com.paradisecloud.smc.dao.model.BusiSmcHistoryConference;
import com.paradisecloud.system.dao.model.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * @author nj
 * @date 2023/3/14 14:34
 */
public interface BusiSmcHistoryConferenceMapper {

    public BusiSmcHistoryConference selectBusiSmcHistoryConferenceById(Integer id);

    /**
     * 查询【请填写功能名称】列表
     *
     * @param busiSmcHistoryConference 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<BusiSmcHistoryConference> selectBusiSmcHistoryConferenceList(BusiSmcHistoryConference busiSmcHistoryConference);


    List<BusiSmcHistoryConference> selectBusiSmcHistoryConferenceListByDate(BusiSmcHistoryConference busiSmcHistoryConference);

    /**
     * 新增【请填写功能名称】
     *
     * @param busiSmcHistoryConference 【请填写功能名称】
     * @return 结果
     */
    public int insertBusiSmcHistoryConference(BusiSmcHistoryConference busiSmcHistoryConference);

    /**
     * 修改【请填写功能名称】
     *
     * @param busiSmcHistoryConference 【请填写功能名称】
     * @return 结果
     */
    public int updateBusiSmcHistoryConference(BusiSmcHistoryConference busiSmcHistoryConference);

    /**
     * 删除【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiSmcHistoryConferenceById(Integer id);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSmcHistoryConferenceByIds(Integer[] ids);

    public BusiSmcHistoryConference selectBusiSmcHistoryConferenceByConferenceId(@Param("conferenceId") String conferenceId);


    List<BusiSmcHistoryConference> selectBusiSmcHistoryConferenceNotTemplate(@Param("searchKey")String searchKey,@Param("sds")List<SysDept> sds);

    List<BusiSmcHistoryConference> selectBusiSmcHistoryConferenceListBySearchKey(@Param("deptId") Long deptId,@Param("searchKey") String searchKey,@Param("startTime") String startTime,@Param("endTime") String endTime,@Param("endStatus") int endStatus);
}
