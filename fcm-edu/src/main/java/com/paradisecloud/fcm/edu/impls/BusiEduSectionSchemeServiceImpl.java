package com.paradisecloud.fcm.edu.impls;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.dao.mapper.BusiEduSectionItemMapper;
import com.paradisecloud.fcm.dao.mapper.BusiEduSectionSchemeMapper;
import com.paradisecloud.fcm.dao.mapper.BusiEduSectionStrategyMapper;
import com.paradisecloud.fcm.dao.model.BusiEduSectionItem;
import com.paradisecloud.fcm.dao.model.BusiEduSectionScheme;
import com.paradisecloud.fcm.dao.model.BusiEduSectionStrategy;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.edu.interfaces.IBusiEduSectionSchemeService;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;

/**
 * 课程节次方案，每个季节都可能有不同的节次方案Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-10-10
 */
@Service
public class BusiEduSectionSchemeServiceImpl implements IBusiEduSectionSchemeService 
{
    
    private Logger logger = LoggerFactory.getLogger(BusiEduSectionSchemeServiceImpl.class);
    
    @Autowired
    private BusiEduSectionSchemeMapper busiEduSectionSchemeMapper;
    
    @Autowired
    private BusiEduSectionStrategyMapper busiEduSectionStrategyMapper;
    
    @Autowired
    private BusiEduSectionItemMapper busiEduSectionItemMapper;

    /**
     * 查询课程节次方案，每个季节都可能有不同的节次方案
     * 
     * @param id 课程节次方案，每个季节都可能有不同的节次方案ID
     * @return 课程节次方案，每个季节都可能有不同的节次方案
     */
    @Override
    public BusiEduSectionScheme selectBusiEduSectionSchemeById(Long id)
    {
        BusiEduSectionScheme busiEduSectionScheme = busiEduSectionSchemeMapper.selectBusiEduSectionSchemeById(id);
        putSectionItemsAndSectionStrategys(busiEduSectionScheme);
        return busiEduSectionScheme;
    }
    
    public List<BusiEduSectionScheme> getEduSectionSchemeByDept(Long deptId)
    {
        BusiEduSectionScheme con = new BusiEduSectionScheme();
        con.setDeptId(deptId);
        List<BusiEduSectionScheme> eduSectionSchemes = null;
        while (ObjectUtils.isEmpty(eduSectionSchemes = busiEduSectionSchemeMapper.selectBusiEduSectionSchemeList(con)))
        {
            SysDept sysDept = SysDeptCache.getInstance().get(deptId);
            if (sysDept.getParentId() != null && sysDept.getParentId().longValue() > 0)
            {
                logger.info("getEduSectionSchemeByDept, 当前部门【" + deptId + "】没有配置节次方案，准备向上【" + sysDept.getParentId() + "】查找节次方案！");
                deptId = sysDept.getParentId();
                con.setDeptId(sysDept.getParentId());
            }
            else
            {
                break;
            }
        }
        
        if (!ObjectUtils.isEmpty(eduSectionSchemes))
        {
            for (BusiEduSectionScheme busiEduSectionScheme : eduSectionSchemes)
            {
                putSectionItemsAndSectionStrategys(busiEduSectionScheme);
            }
        }
        
        return eduSectionSchemes;
    }

    /**
     * 查询课程节次方案，每个季节都可能有不同的节次方案列表
     * 
     * @param busiEduSectionScheme 课程节次方案，每个季节都可能有不同的节次方案
     * @return 课程节次方案，每个季节都可能有不同的节次方案
     */
    @Override
    public List<BusiEduSectionScheme> selectBusiEduSectionSchemeList(BusiEduSectionScheme busiEduSectionScheme)
    {
        return busiEduSectionSchemeMapper.selectBusiEduSectionSchemeList(busiEduSectionScheme);
    }

    /**
     * 新增课程节次方案，每个季节都可能有不同的节次方案
     * 
     * @param busiEduSectionScheme 课程节次方案，每个季节都可能有不同的节次方案
     * @return 结果
     */
    @Override
    @Transactional
    public int insertBusiEduSectionScheme(BusiEduSectionScheme busiEduSectionScheme)
    {
        Assert.notNull(busiEduSectionScheme.getDeptId(), "部门ID不能为空！");
        Assert.notNull(busiEduSectionScheme.getName(), "节次方案名不能为空！");
        Assert.notNull(busiEduSectionScheme.getClassInterval(), "每节课的进行时间不能为空！");
        Assert.notNull(busiEduSectionScheme.getBreakInterval(), "课间休息时长不能为空！");
        Assert.notNull(busiEduSectionScheme.getEnableStatus(), "启用状态不能为空！");
        busiEduSectionScheme.setCreateTime(new Date());
        Object sectionStrategysObj = busiEduSectionScheme.getParams().get("sectionStrategys");
        sectionStrategysObj = JSON.parseArray(JSON.toJSONString(sectionStrategysObj));
        Assert.isTrue(sectionStrategysObj != null && sectionStrategysObj instanceof JSONArray, "节次策略不能为空，且必须为数组类型！");
        
        // 节次策略
        JSONArray sectionStrategys = (JSONArray) sectionStrategysObj;
        Assert.isTrue(!sectionStrategys.isEmpty(), "节次策略不能为空！");
        
        Object sectionItemsObj = busiEduSectionScheme.getParams().get("sectionItems");
        sectionItemsObj = JSON.parseArray(JSON.toJSONString(sectionItemsObj));
        Assert.isTrue(sectionItemsObj != null && sectionItemsObj instanceof JSONArray, "节次条目不能为空，且必须为数组类型！");
        
        JSONArray sectionItems = (JSONArray) sectionItemsObj;
        Assert.isTrue(!sectionItems.isEmpty(), "节次条目不能为空！");
        
        if (YesOrNo.YES == YesOrNo.convert(busiEduSectionScheme.getEnableStatus()))
        {
            busiEduSectionSchemeMapper.updateBusiEduSectionSchemeEnableStatus(busiEduSectionScheme.getDeptId(), YesOrNo.NO.getValue());
        }
        
        int c = busiEduSectionSchemeMapper.insertBusiEduSectionScheme(busiEduSectionScheme);
        Assert.isTrue(c > 0, "添加节次方案失败！");
        for (int i =  0; i < sectionStrategys.size(); i++)
        {
            JSONObject json = sectionStrategys.getJSONObject(i);
            Assert.isTrue(json.containsKey("name") && !ObjectUtils.isEmpty(json.getString("name")), "节次策略名不能为空！");
            Assert.isTrue(json.containsKey("sectionNumber") && json.getIntValue("sectionNumber") > 0, "节次数必须大于0！");
            Assert.isTrue(json.containsKey("beginTime") && !ObjectUtils.isEmpty(json.getString("beginTime")), "节次策略开始时间不能为空！");
            BusiEduSectionStrategy busiEduSectionStrategy = new BusiEduSectionStrategy();
            busiEduSectionStrategy.setName(json.getString("name"));
            busiEduSectionStrategy.setSectionNumber(json.getIntValue("sectionNumber"));
            busiEduSectionStrategy.setBeginTime(json.getString("beginTime"));
            busiEduSectionStrategy.setSectionSchemeId(busiEduSectionScheme.getId());
            Assert.isTrue(busiEduSectionStrategyMapper.insertBusiEduSectionStrategy(busiEduSectionStrategy) > 0, "添加节次策略失败：" + busiEduSectionStrategy.getName());
        }
        
        for (int i =  0; i < sectionItems.size(); i++)
        {
            JSONObject json = sectionItems.getJSONObject(i);
            Assert.isTrue(json.containsKey("serialNumber") && json.getIntValue("serialNumber") > 0, "节次项序列号不能为空！");
            Assert.isTrue(json.containsKey("beginTime") && !ObjectUtils.isEmpty(json.getString("beginTime")), "节次策略开始时间不能为空！");
            Assert.isTrue(json.containsKey("endTime") && !ObjectUtils.isEmpty(json.getString("endTime")), "节次策略结束时间不能为空！");
            
            BusiEduSectionItem busiEduSectionItem = new BusiEduSectionItem();
            busiEduSectionItem.setSerialNumber(json.getIntValue("serialNumber"));
            busiEduSectionItem.setSectionSchemeId(busiEduSectionScheme.getId());
            busiEduSectionItem.setBeginTime(json.getString("beginTime"));
            busiEduSectionItem.setEndTime(json.getString("endTime"));
            Assert.isTrue(busiEduSectionItemMapper.insertBusiEduSectionItem(busiEduSectionItem) > 0, "添加节次项失败：" + busiEduSectionItem.getSerialNumber());
        }
        
        return c;
    }

    /**
     * 修改课程节次方案，每个季节都可能有不同的节次方案
     * 
     * @param busiEduSectionScheme 课程节次方案，每个季节都可能有不同的节次方案
     * @return 结果
     */
    @Override
    @Transactional
    public int updateBusiEduSectionScheme(BusiEduSectionScheme busiEduSectionScheme)
    {
        Assert.notNull(busiEduSectionScheme.getDeptId(), "部门ID不能为空！");
        Assert.notNull(busiEduSectionScheme.getName(), "节次方案名不能为空！");
        Assert.notNull(busiEduSectionScheme.getClassInterval(), "每节课的进行时间不能为空！");
        Assert.notNull(busiEduSectionScheme.getBreakInterval(), "课间休息时长不能为空！");
        Assert.notNull(busiEduSectionScheme.getEnableStatus(), "启用状态不能为空！");
        busiEduSectionScheme.setUpdateTime(new Date());
        Object sectionStrategysObj = busiEduSectionScheme.getParams().get("sectionStrategys");
        sectionStrategysObj = JSON.parseArray(JSON.toJSONString(sectionStrategysObj));
        Assert.isTrue(sectionStrategysObj != null && sectionStrategysObj instanceof JSONArray, "节次策略不能为空，且必须为数组类型！");
        
        // 节次策略
        JSONArray sectionStrategys = (JSONArray) sectionStrategysObj;
        Assert.isTrue(!sectionStrategys.isEmpty(), "节次策略不能为空！");
        
        Object sectionItemsObj = busiEduSectionScheme.getParams().get("sectionItems");
        sectionItemsObj = JSON.parseArray(JSON.toJSONString(sectionItemsObj));
        Assert.isTrue(sectionItemsObj != null && sectionItemsObj instanceof JSONArray, "节次条目不能为空，且必须为数组类型！");
        
        JSONArray sectionItems = (JSONArray) sectionItemsObj;
        Assert.isTrue(!sectionItems.isEmpty(), "节次条目不能为空！");
        
        if (YesOrNo.YES == YesOrNo.convert(busiEduSectionScheme.getEnableStatus()))
        {
            busiEduSectionSchemeMapper.updateBusiEduSectionSchemeEnableStatus(busiEduSectionScheme.getDeptId(), YesOrNo.NO.getValue());
        }
        
        int c = busiEduSectionSchemeMapper.updateBusiEduSectionScheme(busiEduSectionScheme);
        Assert.isTrue(c > 0, "添加节次方案失败！");
        busiEduSectionStrategyMapper.deleteBusiEduSectionStrategyBySectionSchemeId(busiEduSectionScheme.getId());
        for (int i =  0; i < sectionStrategys.size(); i++)
        {
            JSONObject json = sectionStrategys.getJSONObject(i);
            Assert.isTrue(json.containsKey("name") && !ObjectUtils.isEmpty(json.getString("name")), "节次策略名不能为空！");
            Assert.isTrue(json.containsKey("sectionNumber") && json.getIntValue("sectionNumber") > 0, "节次数必须大于0！");
            Assert.isTrue(json.containsKey("beginTime") && !ObjectUtils.isEmpty(json.getString("beginTime")), "节次策略开始时间不能为空！");
            BusiEduSectionStrategy busiEduSectionStrategy = new BusiEduSectionStrategy();
            busiEduSectionStrategy.setName(json.getString("name"));
            busiEduSectionStrategy.setSectionNumber(json.getIntValue("sectionNumber"));
            busiEduSectionStrategy.setBeginTime(json.getString("beginTime"));
            busiEduSectionStrategy.setSectionSchemeId(busiEduSectionScheme.getId());
            Assert.isTrue(busiEduSectionStrategyMapper.insertBusiEduSectionStrategy(busiEduSectionStrategy) > 0, "添加节次策略失败：" + busiEduSectionStrategy.getName());
        }
        
        busiEduSectionItemMapper.deleteBusiEduSectionItemBuildingBySectionSchemeId(busiEduSectionScheme.getId());
        for (int i =  0; i < sectionItems.size(); i++)
        {
            JSONObject json = sectionItems.getJSONObject(i);
            Assert.isTrue(json.containsKey("serialNumber") && json.getIntValue("serialNumber") > 0, "节次项序列号不能为空！");
            Assert.isTrue(json.containsKey("beginTime") && !ObjectUtils.isEmpty(json.getString("beginTime")), "节次策略开始时间不能为空！");
            Assert.isTrue(json.containsKey("endTime") && !ObjectUtils.isEmpty(json.getString("endTime")), "节次策略结束时间不能为空！");
            
            BusiEduSectionItem busiEduSectionItem = new BusiEduSectionItem();
            busiEduSectionItem.setSerialNumber(json.getIntValue("serialNumber"));
            busiEduSectionItem.setSectionSchemeId(busiEduSectionScheme.getId());
            busiEduSectionItem.setBeginTime(json.getString("beginTime"));
            busiEduSectionItem.setEndTime(json.getString("endTime"));
            Assert.isTrue(busiEduSectionItemMapper.insertBusiEduSectionItem(busiEduSectionItem) > 0, "添加节次项失败：" + busiEduSectionItem.getSerialNumber());
        }
        
        return c;
    }

    /**
     * 删除课程节次方案，每个季节都可能有不同的节次方案信息
     * 
     * @param id 课程节次方案，每个季节都可能有不同的节次方案ID
     * @return 结果
     */
    @Override
    public int deleteBusiEduSectionSchemeById(Long id)
    {
        busiEduSectionStrategyMapper.deleteBusiEduSectionStrategyBySectionSchemeId(id);
        busiEduSectionItemMapper.deleteBusiEduSectionItemBuildingBySectionSchemeId(id);
        return busiEduSectionSchemeMapper.deleteBusiEduSectionSchemeById(id);
    }
    
    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-10-29 12:05 
     * @return
     * @see com.paradisecloud.fcm.edu.interfaces.IBusiEduSectionSchemeService#getDeptRecordCounts()
     */
    @Override
    public List<DeptRecordCount> getDeptRecordCounts()
    {
        return busiEduSectionSchemeMapper.getDeptRecordCounts();
    }

    private void putSectionItemsAndSectionStrategys(BusiEduSectionScheme busiEduSectionScheme)
    {
        BusiEduSectionStrategy busiEduSectionStrategy = new BusiEduSectionStrategy();
        busiEduSectionStrategy.setSectionSchemeId(busiEduSectionScheme.getId());
        List<BusiEduSectionStrategy> ss = busiEduSectionStrategyMapper.selectBusiEduSectionStrategyList(busiEduSectionStrategy);
        busiEduSectionScheme.getParams().put("sectionStrategys", ss);
        
        BusiEduSectionItem busiEduSectionItem = new BusiEduSectionItem();
        busiEduSectionItem.setSectionSchemeId(busiEduSectionScheme.getId());
        List<BusiEduSectionItem> is = busiEduSectionItemMapper.selectBusiEduSectionItemList(busiEduSectionItem);
        busiEduSectionScheme.getParams().put("sectionItems", is);
    }
}
