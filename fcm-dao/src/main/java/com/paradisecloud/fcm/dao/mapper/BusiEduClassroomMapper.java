package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiEduClassroom;

/**
 * 教室Mapper接口
 * 
 * @author lilinhai
 * @date 2021-10-10
 */
public interface BusiEduClassroomMapper 
{
    /**
     * 查询教室
     * 
     * @param id 教室ID
     * @return 教室
     */
    public BusiEduClassroom selectBusiEduClassroomById(Long id);

    /**
     * 查询教室列表
     * 
     * @param busiEduClassroom 教室
     * @return 教室集合
     */
    public List<BusiEduClassroom> selectBusiEduClassroomList(BusiEduClassroom busiEduClassroom);

    /**
     * 新增教室
     * 
     * @param busiEduClassroom 教室
     * @return 结果
     */
    public int insertBusiEduClassroom(BusiEduClassroom busiEduClassroom);

    /**
     * 修改教室
     * 
     * @param busiEduClassroom 教室
     * @return 结果
     */
    public int updateBusiEduClassroom(BusiEduClassroom busiEduClassroom);

    /**
     * 删除教室
     * 
     * @param id 教室ID
     * @return 结果
     */
    public int deleteBusiEduClassroomById(Long id);

    /**
     * 批量删除教室
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiEduClassroomByIds(Long[] ids);
}
