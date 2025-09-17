package com.paradisecloud.fcm.edu.impls;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paradisecloud.fcm.dao.mapper.BusiEduSectionItemMapper;
import com.paradisecloud.fcm.dao.model.BusiEduSectionItem;
import com.paradisecloud.fcm.edu.interfaces.IBusiEduSectionItemService;

/**
 * 课程节次条目Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-10-10
 */
@Service
public class BusiEduSectionItemServiceImpl implements IBusiEduSectionItemService 
{
    @Autowired
    private BusiEduSectionItemMapper busiEduSectionItemMapper;

    /**
     * 查询课程节次条目
     * 
     * @param id 课程节次条目ID
     * @return 课程节次条目
     */
    @Override
    public BusiEduSectionItem selectBusiEduSectionItemById(Long id)
    {
        return busiEduSectionItemMapper.selectBusiEduSectionItemById(id);
    }

    /**
     * 查询课程节次条目列表
     * 
     * @param busiEduSectionItem 课程节次条目
     * @return 课程节次条目
     */
    @Override
    public List<BusiEduSectionItem> selectBusiEduSectionItemList(BusiEduSectionItem busiEduSectionItem)
    {
        return busiEduSectionItemMapper.selectBusiEduSectionItemList(busiEduSectionItem);
    }

    /**
     * 新增课程节次条目
     * 
     * @param busiEduSectionItem 课程节次条目
     * @return 结果
     */
    @Override
    public int insertBusiEduSectionItem(BusiEduSectionItem busiEduSectionItem)
    {
        busiEduSectionItem.setCreateTime(new Date());
        return busiEduSectionItemMapper.insertBusiEduSectionItem(busiEduSectionItem);
    }

    /**
     * 修改课程节次条目
     * 
     * @param busiEduSectionItem 课程节次条目
     * @return 结果
     */
    @Override
    public int updateBusiEduSectionItem(BusiEduSectionItem busiEduSectionItem)
    {
        busiEduSectionItem.setUpdateTime(new Date());
        return busiEduSectionItemMapper.updateBusiEduSectionItem(busiEduSectionItem);
    }

    /**
     * 批量删除课程节次条目
     * 
     * @param ids 需要删除的课程节次条目ID
     * @return 结果
     */
    @Override
    public int deleteBusiEduSectionItemByIds(Long[] ids)
    {
        return busiEduSectionItemMapper.deleteBusiEduSectionItemByIds(ids);
    }

    /**
     * 删除课程节次条目信息
     * 
     * @param id 课程节次条目ID
     * @return 结果
     */
    @Override
    public int deleteBusiEduSectionItemById(Long id)
    {
        return busiEduSectionItemMapper.deleteBusiEduSectionItemById(id);
    }
}
