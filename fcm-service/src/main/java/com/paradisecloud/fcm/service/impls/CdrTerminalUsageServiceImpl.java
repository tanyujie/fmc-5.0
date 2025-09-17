package com.paradisecloud.fcm.service.impls;

import com.paradisecloud.fcm.dao.mapper.CdrTerminalUsageMapper;
import com.paradisecloud.fcm.dao.model.CdrTerminalUsage;
import com.paradisecloud.fcm.service.interfaces.ICdrTerminalUsageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * cdr使用情况Service业务层处理
 * 
 * @author lilinhai
 * @date 2022-06-08
 */
@Service
public class CdrTerminalUsageServiceImpl implements ICdrTerminalUsageService
{
    @Resource
    private CdrTerminalUsageMapper cdrTerminalUsageMapper;

    /**
     * 查询cdr使用情况
     * 
     * @param id cdr使用情况ID
     * @return cdr使用情况
     */
    @Override
    public CdrTerminalUsage selectCdrTerminalUsageById(Long id)
    {
        return cdrTerminalUsageMapper.selectCdrTerminalUsageById(id);
    }

    /**
     * 查询cdr使用情况列表
     * 
     * @param cdrTerminalUsage cdr使用情况
     * @return cdr使用情况
     */
    @Override
    public List<CdrTerminalUsage> selectCdrTerminalUsageList(CdrTerminalUsage cdrTerminalUsage)
    {
        return cdrTerminalUsageMapper.selectCdrTerminalUsageList(cdrTerminalUsage);
    }

    /**
     * 新增cdr使用情况
     * 
     * @param cdrTerminalUsage cdr使用情况
     * @return 结果
     */
    @Override
    public int insertCdrTerminalUsage(CdrTerminalUsage cdrTerminalUsage)
    {
        cdrTerminalUsage.setCreateTime(new Date());
        return cdrTerminalUsageMapper.insertCdrTerminalUsage(cdrTerminalUsage);
    }

    /**
     * 修改cdr使用情况
     * 
     * @param cdrTerminalUsage cdr使用情况
     * @return 结果
     */
    @Override
    public int updateCdrTerminalUsage(CdrTerminalUsage cdrTerminalUsage)
    {
        cdrTerminalUsage.setUpdateTime(new Date());
        return cdrTerminalUsageMapper.updateCdrTerminalUsage(cdrTerminalUsage);
    }

    /**
     * 批量删除cdr使用情况
     * 
     * @param ids 需要删除的cdr使用情况ID
     * @return 结果
     */
    @Override
    public int deleteCdrTerminalUsageByIds(Long[] ids)
    {
        return cdrTerminalUsageMapper.deleteCdrTerminalUsageByIds(ids);
    }

    /**
     * 删除cdr使用情况信息
     * 
     * @param id cdr使用情况ID
     * @return 结果
     */
    @Override
    public int deleteCdrTerminalUsageById(Long id)
    {
        return cdrTerminalUsageMapper.deleteCdrTerminalUsageById(id);
    }

    /**
     * 更新今天的
     *
     * @return
     */
    @Override
    public int updateCdrTerminalUsageToday() {
        return 0;
    }
}
