package com.paradisecloud.fcm.edu.impls;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paradisecloud.fcm.dao.mapper.BusiEduSchoolBuildingMapper;
import com.paradisecloud.fcm.dao.model.BusiEduSchoolBuilding;
import com.paradisecloud.fcm.edu.interfaces.IBusiEduSchoolBuildingService;

/**
 * 学校建筑，用于存放教室，教室肯定是归属某个建筑Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-10-10
 */
@Service
public class BusiEduSchoolBuildingServiceImpl implements IBusiEduSchoolBuildingService 
{
    @Autowired
    private BusiEduSchoolBuildingMapper busiEduSchoolBuildingMapper;

    /**
     * 查询学校建筑，用于存放教室，教室肯定是归属某个建筑
     * 
     * @param id 学校建筑，用于存放教室，教室肯定是归属某个建筑ID
     * @return 学校建筑，用于存放教室，教室肯定是归属某个建筑
     */
    @Override
    public BusiEduSchoolBuilding selectBusiEduSchoolBuildingById(Long id)
    {
        return busiEduSchoolBuildingMapper.selectBusiEduSchoolBuildingById(id);
    }

    /**
     * 查询学校建筑，用于存放教室，教室肯定是归属某个建筑列表
     * 
     * @param busiEduSchoolBuilding 学校建筑，用于存放教室，教室肯定是归属某个建筑
     * @return 学校建筑，用于存放教室，教室肯定是归属某个建筑
     */
    @Override
    public List<BusiEduSchoolBuilding> selectBusiEduSchoolBuildingList(BusiEduSchoolBuilding busiEduSchoolBuilding)
    {
        return busiEduSchoolBuildingMapper.selectBusiEduSchoolBuildingList(busiEduSchoolBuilding);
    }

    /**
     * 新增学校建筑，用于存放教室，教室肯定是归属某个建筑
     * 
     * @param busiEduSchoolBuilding 学校建筑，用于存放教室，教室肯定是归属某个建筑
     * @return 结果
     */
    @Override
    public int insertBusiEduSchoolBuilding(BusiEduSchoolBuilding busiEduSchoolBuilding)
    {
        busiEduSchoolBuilding.setCreateTime(new Date());
        return busiEduSchoolBuildingMapper.insertBusiEduSchoolBuilding(busiEduSchoolBuilding);
    }

    /**
     * 修改学校建筑，用于存放教室，教室肯定是归属某个建筑
     * 
     * @param busiEduSchoolBuilding 学校建筑，用于存放教室，教室肯定是归属某个建筑
     * @return 结果
     */
    @Override
    public int updateBusiEduSchoolBuilding(BusiEduSchoolBuilding busiEduSchoolBuilding)
    {
        busiEduSchoolBuilding.setUpdateTime(new Date());
        return busiEduSchoolBuildingMapper.updateBusiEduSchoolBuilding(busiEduSchoolBuilding);
    }

    /**
     * 批量删除学校建筑，用于存放教室，教室肯定是归属某个建筑
     * 
     * @param ids 需要删除的学校建筑，用于存放教室，教室肯定是归属某个建筑ID
     * @return 结果
     */
    @Override
    public int deleteBusiEduSchoolBuildingByIds(Long[] ids)
    {
        return busiEduSchoolBuildingMapper.deleteBusiEduSchoolBuildingByIds(ids);
    }

    /**
     * 删除学校建筑，用于存放教室，教室肯定是归属某个建筑信息
     * 
     * @param id 学校建筑，用于存放教室，教室肯定是归属某个建筑ID
     * @return 结果
     */
    @Override
    public int deleteBusiEduSchoolBuildingById(Long id)
    {
        return busiEduSchoolBuildingMapper.deleteBusiEduSchoolBuildingById(id);
    }
}
