package com.paradisecloud.fcm.cdr.service.impls;

import com.paradisecloud.fcm.cdr.service.interfaces.ICdrCallLegStartService;
import com.paradisecloud.fcm.dao.mapper.CdrCallLegStartMapper;
import com.paradisecloud.fcm.dao.model.CdrCallLegStart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * callLegStart 记录Service业务层处理
 *
 * @author lilinhai
 * @date 2021-05-13
 */
@Service
public class CdrCallLegStartServiceImpl implements ICdrCallLegStartService
{
    @Autowired
    private CdrCallLegStartMapper cdrCallLegStartMapper;
    
    /**
     * 查询callLegStart 记录
     *
     * @param cdrId callLegStart 记录ID
     * @return callLegStart 记录
     */
    @Override
    public CdrCallLegStart selectCdrCallLegStartById(Integer cdrId)
    {
        return cdrCallLegStartMapper.selectCdrCallLegStartById(cdrId);
    }
    
    /**
     * 查询callLegStart 记录列表
     *
     * @param cdrCallLegStart callLegStart 记录
     * @return callLegStart 记录
     */
    @Override
    public List<CdrCallLegStart> selectCdrCallLegStartList(CdrCallLegStart cdrCallLegStart)
    {
        return cdrCallLegStartMapper.selectCdrCallLegStartList(cdrCallLegStart);
    }
    
    /**
     * 新增callLegStart 记录
     *
     * @param cdrCallLegStart callLegStart 记录
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertCdrCallLegStart(CdrCallLegStart cdrCallLegStart)
    {
        return cdrCallLegStartMapper.insertCdrCallLegStart(cdrCallLegStart);
    }
    
    /**
     * 修改callLegStart 记录
     *
     * @param cdrCallLegStart callLegStart 记录
     * @return 结果
     */
    @Override
    public int updateCdrCallLegStart(CdrCallLegStart cdrCallLegStart)
    {
        return cdrCallLegStartMapper.updateCdrCallLegStart(cdrCallLegStart);
    }
    
    /**
     * 根据CallLegId更新
     *
     * @param cdrCallLegStart
     * @return
     */
    @Override
    public int updateByCallLegId(CdrCallLegStart cdrCallLegStart)
    {
        return 0;
    }
    
    /**
     * 批量删除callLegStart 记录
     *
     * @param cdrIds 需要删除的callLegStart 记录ID
     * @return 结果
     */
    @Override
    public int deleteCdrCallLegStartByIds(Integer[] cdrIds)
    {
        return cdrCallLegStartMapper.deleteCdrCallLegStartByIds(cdrIds);
    }
    
    /**
     * 删除callLegStart 记录信息
     *
     * @param cdrId callLegStart 记录ID
     * @return 结果
     */
    @Override
    public int deleteCdrCallLegStartById(Integer cdrId)
    {
        return cdrCallLegStartMapper.deleteCdrCallLegStartById(cdrId);
    }
}
