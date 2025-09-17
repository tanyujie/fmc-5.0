package com.paradisecloud.fcm.edu.impls;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.paradisecloud.fcm.dao.mapper.BusiEduClassMapper;
import com.paradisecloud.fcm.dao.model.BusiEduClass;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.edu.cache.EduClassCache;
import com.paradisecloud.fcm.edu.interfaces.IBusiEduClassService;

/**
 * 班级信息Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-10-10
 */
@Service
public class BusiEduClassServiceImpl implements IBusiEduClassService 
{
    @Autowired
    private BusiEduClassMapper busiEduClassMapper;

    /**
     * 查询班级信息
     * 
     * @param id 班级信息ID
     * @return 班级信息
     */
    @Override
    public BusiEduClass selectBusiEduClassById(Long id)
    {
        return busiEduClassMapper.selectBusiEduClassById(id);
    }

    /**
     * 查询班级信息列表
     * 
     * @param busiEduClass 班级信息
     * @return 班级信息
     */
    @Override
    public List<BusiEduClass> selectBusiEduClassList(BusiEduClass busiEduClass)
    {
        return busiEduClassMapper.selectBusiEduClassList(busiEduClass);
    }

    /**
     * 新增班级信息
     * 
     * @param busiEduClass 班级信息
     * @return 结果
     */
    @Override
    public int insertBusiEduClass(BusiEduClass busiEduClass)
    {
        Assert.notNull(busiEduClass.getDeptId(), "部门ID不能为空！");
        busiEduClass.setCreateTime(new Date());
        int c = busiEduClassMapper.insertBusiEduClass(busiEduClass);
        if (c > 0)
        {
            EduClassCache.getInstance().add(busiEduClass);
        }
        return c;
    }

    /**
     * 修改班级信息
     * 
     * @param busiEduClass 班级信息
     * @return 结果
     */
    @Override
    public int updateBusiEduClass(BusiEduClass busiEduClass)
    {
        Assert.notNull(busiEduClass.getDeptId(), "部门ID不能为空！");
        busiEduClass.setUpdateTime(new Date());
        int c = busiEduClassMapper.updateBusiEduClass(busiEduClass);
        if (c > 0)
        {
            EduClassCache.getInstance().add(busiEduClass);
        }
        return c;
    }

    /**
     * 删除班级信息信息
     * 
     * @param id 班级信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiEduClassById(Long id)
    {
        int c = busiEduClassMapper.deleteBusiEduClassById(id);
        if (c > 0)
        {
            EduClassCache.getInstance().remove(id);
        }
        return c;
    }

    @Override
    public List<DeptRecordCount> getDeptRecordCounts()
    {
        return busiEduClassMapper.getDeptRecordCounts();
    }
}
