package com.paradisecloud.fcm.smartroom.service.impls;

import java.util.List;
import java.util.Date;

import com.paradisecloud.fcm.dao.mapper.BusiSmartRoomDeptMapper;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDept;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomDeptService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）Service业务层处理
 * 
 * @author lilinhai
 * @date 2024-01-26
 */
@Service
public class BusiSmartRoomDeptServiceImpl implements IBusiSmartRoomDeptService
{
    @Resource
    private BusiSmartRoomDeptMapper busiSmartRoomDeptMapper;

    /**
     * 查询会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）
     * 
     * @param id 会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）ID
     * @return 会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）
     */
    @Override
    public BusiSmartRoomDept selectBusiSmartRoomDeptById(Long id)
    {
        return busiSmartRoomDeptMapper.selectBusiSmartRoomDeptById(id);
    }

    /**
     * 查询会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）列表
     * 
     * @param busiSmartRoomDept 会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）
     * @return 会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）
     */
    @Override
    public List<BusiSmartRoomDept> selectBusiSmartRoomDeptList(BusiSmartRoomDept busiSmartRoomDept)
    {
        return busiSmartRoomDeptMapper.selectBusiSmartRoomDeptList(busiSmartRoomDept);
    }

    /**
     * 新增会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）
     * 
     * @param busiSmartRoomDept 会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int insertBusiSmartRoomDept(BusiSmartRoomDept busiSmartRoomDept)
    {
        busiSmartRoomDept.setCreateTime(new Date());
        return busiSmartRoomDeptMapper.insertBusiSmartRoomDept(busiSmartRoomDept);
    }

    /**
     * 修改会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）
     * 
     * @param busiSmartRoomDept 会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）
     * @return 结果
     */
    @Override
    public int updateBusiSmartRoomDept(BusiSmartRoomDept busiSmartRoomDept)
    {
        return busiSmartRoomDeptMapper.updateBusiSmartRoomDept(busiSmartRoomDept);
    }

    /**
     * 批量删除会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）
     * 
     * @param ids 需要删除的会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmartRoomDeptByIds(Long[] ids)
    {
        return busiSmartRoomDeptMapper.deleteBusiSmartRoomDeptByIds(ids);
    }

    /**
     * 删除会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）信息
     * 
     * @param id 会议室分配租户的中间（一个会议室可以分配给多个租户，一对多）ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmartRoomDeptById(Long id)
    {
        return busiSmartRoomDeptMapper.deleteBusiSmartRoomDeptById(id);
    }
}
