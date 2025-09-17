package com.paradisecloud.fcm.edu.interfaces;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiEduSchoolBuilding;

/**
 * 学校建筑，用于存放教室，教室肯定是归属某个建筑Service接口
 * 
 * @author lilinhai
 * @date 2021-10-10
 */
public interface IBusiEduSchoolBuildingService 
{
    /**
     * 查询学校建筑，用于存放教室，教室肯定是归属某个建筑
     * 
     * @param id 学校建筑，用于存放教室，教室肯定是归属某个建筑ID
     * @return 学校建筑，用于存放教室，教室肯定是归属某个建筑
     */
    public BusiEduSchoolBuilding selectBusiEduSchoolBuildingById(Long id);

    /**
     * 查询学校建筑，用于存放教室，教室肯定是归属某个建筑列表
     * 
     * @param busiEduSchoolBuilding 学校建筑，用于存放教室，教室肯定是归属某个建筑
     * @return 学校建筑，用于存放教室，教室肯定是归属某个建筑集合
     */
    public List<BusiEduSchoolBuilding> selectBusiEduSchoolBuildingList(BusiEduSchoolBuilding busiEduSchoolBuilding);

    /**
     * 新增学校建筑，用于存放教室，教室肯定是归属某个建筑
     * 
     * @param busiEduSchoolBuilding 学校建筑，用于存放教室，教室肯定是归属某个建筑
     * @return 结果
     */
    public int insertBusiEduSchoolBuilding(BusiEduSchoolBuilding busiEduSchoolBuilding);

    /**
     * 修改学校建筑，用于存放教室，教室肯定是归属某个建筑
     * 
     * @param busiEduSchoolBuilding 学校建筑，用于存放教室，教室肯定是归属某个建筑
     * @return 结果
     */
    public int updateBusiEduSchoolBuilding(BusiEduSchoolBuilding busiEduSchoolBuilding);

    /**
     * 批量删除学校建筑，用于存放教室，教室肯定是归属某个建筑
     * 
     * @param ids 需要删除的学校建筑，用于存放教室，教室肯定是归属某个建筑ID
     * @return 结果
     */
    public int deleteBusiEduSchoolBuildingByIds(Long[] ids);

    /**
     * 删除学校建筑，用于存放教室，教室肯定是归属某个建筑信息
     * 
     * @param id 学校建筑，用于存放教室，教室肯定是归属某个建筑ID
     * @return 结果
     */
    public int deleteBusiEduSchoolBuildingById(Long id);
}
