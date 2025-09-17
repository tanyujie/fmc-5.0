package com.paradisecloud.fcm.edu.impls;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.paradisecloud.fcm.dao.mapper.BusiEduSubjectMapper;
import com.paradisecloud.fcm.dao.model.BusiEduSubject;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.edu.cache.EduSubjectCache;
import com.paradisecloud.fcm.edu.interfaces.IBusiEduSubjectService;
import com.sinhy.exception.SystemException;

/**
 * 学科信息Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-10-10
 */
@Service
public class BusiEduSubjectServiceImpl implements IBusiEduSubjectService 
{
    @Autowired
    private BusiEduSubjectMapper busiEduSubjectMapper;

    /**
     * 查询学科信息
     * 
     * @param id 学科信息ID
     * @return 学科信息
     */
    @Override
    public BusiEduSubject selectBusiEduSubjectById(Long id)
    {
        return busiEduSubjectMapper.selectBusiEduSubjectById(id);
    }

    /**
     * 查询学科信息列表
     * 
     * @param busiEduSubject 学科信息
     * @return 学科信息
     */
    @Override
    public List<BusiEduSubject> selectBusiEduSubjectList(BusiEduSubject busiEduSubject)
    {
        return busiEduSubjectMapper.selectBusiEduSubjectList(busiEduSubject);
    }

    /**
     * 新增学科信息
     * 
     * @param busiEduSubject 学科信息
     * @return 结果
     */
    @Override
    public int insertBusiEduSubject(BusiEduSubject busiEduSubject)
    {
        Assert.notNull(busiEduSubject.getDeptId(), "部门ID不能为空！");
        int c = 0;
        try
        {
            busiEduSubject.setCreateTime(new Date());
            c = busiEduSubjectMapper.insertBusiEduSubject(busiEduSubject);
            if (c > 0)
            {
                EduSubjectCache.getInstance().add(busiEduSubject);
            }
        }
        catch (Throwable e)
        {
            throw new SystemException(1005436, "同一组织架构中学科名称/学科代码不能重复！");
        }
        return c;
    }

    /**
     * 修改学科信息
     * 
     * @param busiEduSubject 学科信息
     * @return 结果
     */
    @Override
    public int updateBusiEduSubject(BusiEduSubject busiEduSubject)
    {
        busiEduSubject.setUpdateTime(new Date());
        Assert.notNull(busiEduSubject.getDeptId(), "部门ID不能为空！");
        int c = 0;
        try
        {
            c = busiEduSubjectMapper.updateBusiEduSubject(busiEduSubject);
            if (c > 0)
            {
                EduSubjectCache.getInstance().add(busiEduSubject);
            }
        }
        catch (Throwable e)
        {
            throw new SystemException(1005436, "同一组织架构中学科名称/学科代码不能重复！");
        }
        return c;
    }

    /**
     * 批量删除学科信息
     * 
     * @param ids 需要删除的学科信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiEduSubjectByIds(Long[] ids)
    {
        return busiEduSubjectMapper.deleteBusiEduSubjectByIds(ids);
    }

    /**
     * 删除学科信息信息
     * 
     * @param id 学科信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiEduSubjectById(Long id)
    {
        int c = busiEduSubjectMapper.deleteBusiEduSubjectById(id);
        if (c > 0)
        {
            EduSubjectCache.getInstance().remove(id);
        }
        return c;
    }

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-10-29 12:06 
     * @return
     * @see com.paradisecloud.fcm.edu.interfaces.IBusiEduSubjectService#getDeptRecordCounts()
     */
    @Override
    public List<DeptRecordCount> getDeptRecordCounts()
    {
        return busiEduSubjectMapper.getDeptRecordCounts();
    }
    
    
}
