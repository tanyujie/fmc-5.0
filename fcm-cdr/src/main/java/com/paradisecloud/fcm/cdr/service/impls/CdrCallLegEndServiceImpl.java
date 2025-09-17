package com.paradisecloud.fcm.cdr.service.impls;

import com.paradisecloud.fcm.cdr.service.interfaces.ICdrCallLegEndService;
import com.paradisecloud.fcm.dao.mapper.CdrCallLegEndMapper;
import com.paradisecloud.fcm.dao.model.CdrCallLegEnd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * callLegEnd记录Service业务层处理
 *
 * @author lilinhai
 * @date 2021-05-13
 */
@Service
public class CdrCallLegEndServiceImpl implements ICdrCallLegEndService
{
    @Autowired
    private CdrCallLegEndMapper cdrCallLegEndMapper;
    
    /**
     * 查询callLegEnd记录
     *
     * @param cdrId callLegEnd记录ID
     * @return callLegEnd记录
     */
    @Override
    public CdrCallLegEnd selectCdrCallLegEndById(Integer cdrId)
    {
        return cdrCallLegEndMapper.selectCdrCallLegEndById(cdrId);
    }
    
    /**
     * 查询callLegEnd记录列表
     *
     * @param cdrCallLegEnd callLegEnd记录
     * @return callLegEnd记录
     */
    @Override
    public List<CdrCallLegEnd> selectCdrCallLegEndList(CdrCallLegEnd cdrCallLegEnd)
    {
        return cdrCallLegEndMapper.selectCdrCallLegEndList(cdrCallLegEnd);
    }
    
    /**
     * 新增callLegEnd记录
     *
     * @param cdrCallLegEnd callLegEnd记录
     * @return 结果
     */
    @Override
    public int insertCdrCallLegEnd(CdrCallLegEnd cdrCallLegEnd)
    {
        return cdrCallLegEndMapper.insertCdrCallLegEnd(cdrCallLegEnd);
    }
    
    /**
     * 修改callLegEnd记录
     *
     * @param cdrCallLegEnd callLegEnd记录
     * @return 结果
     */
    @Override
    public int updateCdrCallLegEnd(CdrCallLegEnd cdrCallLegEnd)
    {
        return cdrCallLegEndMapper.updateCdrCallLegEnd(cdrCallLegEnd);
    }
    
    /**
     * 批量删除callLegEnd记录
     *
     * @param cdrIds 需要删除的callLegEnd记录ID
     * @return 结果
     */
    @Override
    public int deleteCdrCallLegEndByIds(Integer[] cdrIds)
    {
        return cdrCallLegEndMapper.deleteCdrCallLegEndByIds(cdrIds);
    }
    
    /**
     * 删除callLegEnd记录信息
     *
     * @param cdrId callLegEnd记录ID
     * @return 结果
     */
    @Override
    public int deleteCdrCallLegEndById(Integer cdrId)
    {
        return cdrCallLegEndMapper.deleteCdrCallLegEndById(cdrId);
    }
    
}
