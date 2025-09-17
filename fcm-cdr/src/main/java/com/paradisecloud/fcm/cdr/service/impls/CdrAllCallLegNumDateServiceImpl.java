package com.paradisecloud.fcm.cdr.service.impls;

import com.paradisecloud.fcm.cdr.service.interfaces.ICdrAllCallLegNumDateService;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.CdrAllCallLegNumDateMapper;
import com.paradisecloud.fcm.dao.model.CdrAllCallLegNumDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @author johnson liu
 * @date 2021/6/16 17:36
 */
@Service
public class CdrAllCallLegNumDateServiceImpl implements ICdrAllCallLegNumDateService
{
    @Resource
    private CdrAllCallLegNumDateMapper cdrAllCallLegNumDateMapper;
    
    /**
     * 查询每天参会的数量
     *
     * @param id 每天参会的数量ID
     * @return 每天参会的数量
     */
    @Override
    public CdrAllCallLegNumDate selectCdrAllCallLegNumDateById(Long id)
    {
        return cdrAllCallLegNumDateMapper.selectCdrAllCallLegNumDateById(id);
    }
    
    /**
     * 查询每天参会的数量列表
     *
     * @param cdrAllCallLegNumDate 每天参会的数量
     * @return 每天参会的数量
     */
    @Override
    public List<CdrAllCallLegNumDate> selectCdrAllCallLegNumDateList(CdrAllCallLegNumDate cdrAllCallLegNumDate)
    {
        return cdrAllCallLegNumDateMapper.selectCdrAllCallLegNumDateList(cdrAllCallLegNumDate);
    }
    
    /**
     * 新增每天参会的数量
     *
     * @param cdrAllCallLegNumDate 每天参会的数量
     * @return 结果
     */
    @Override
    public int insertCdrAllCallLegNumDate(CdrAllCallLegNumDate cdrAllCallLegNumDate)
    {
        cdrAllCallLegNumDate.setCreateTime(new Date());
        return cdrAllCallLegNumDateMapper.insertCdrAllCallLegNumDate(cdrAllCallLegNumDate);
    }
    
    /**
     * 修改每天参会的数量
     *
     * @param cdrAllCallLegNumDate 每天参会的数量
     * @return 结果
     */
    @Override
    public int updateCdrAllCallLegNumDate(CdrAllCallLegNumDate cdrAllCallLegNumDate)
    {
        cdrAllCallLegNumDate.setUpdateTime(new Date());
        return cdrAllCallLegNumDateMapper.updateCdrAllCallLegNumDate(cdrAllCallLegNumDate);
    }
    
    /**
     * 批量删除每天参会的数量
     *
     * @param ids 需要删除的每天参会的数量ID
     * @return 结果
     */
    @Override
    public int deleteCdrAllCallLegNumDateByIds(Long[] ids)
    {
        return cdrAllCallLegNumDateMapper.deleteCdrAllCallLegNumDateByIds(ids);
    }
    
    /**
     * 删除每天参会的数量信息
     *
     * @param id 每天参会的数量ID
     * @return 结果
     */
    @Override
    public int deleteCdrAllCallLegNumDateById(Long id)
    {
        return cdrAllCallLegNumDateMapper.deleteCdrAllCallLegNumDateById(id);
    }
    
    /**
     * 通过监听事件每天新增或更新参会者的数量
     *
     * @param fmeIp
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateByEvent(String fmeIp)
    {
        try
        {
            String date = DateUtil.convertDateToString(LocalDate.now(), null);
            CdrAllCallLegNumDate cdrAllCallLegNumDate = cdrAllCallLegNumDateMapper.selectByFmeIpAndDate(fmeIp, date);
            int number = 0;
            if (cdrAllCallLegNumDate == null)
            {
                cdrAllCallLegNumDate = new CdrAllCallLegNumDate();
                cdrAllCallLegNumDate.setFmeIp(fmeIp);
                number = 1;
                cdrAllCallLegNumDate.setNumber(number);
                cdrAllCallLegNumDate.setRecordDate(new Date());
                cdrAllCallLegNumDate.setCreateTime(new Date());
                cdrAllCallLegNumDateMapper.insertCdrAllCallLegNumDate(cdrAllCallLegNumDate);
            }
            else
            {
                number = cdrAllCallLegNumDate.getNumber() + 1;
                
                cdrAllCallLegNumDate.setNumber(number);
                cdrAllCallLegNumDate.setUpdateTime(new Date());
                cdrAllCallLegNumDateMapper.updateByCurrentDate(cdrAllCallLegNumDate);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 根据fmeIp和记录日期查询
     *
     * @param fmeIp
     * @param date
     * @return
     */
    @Override
    public CdrAllCallLegNumDate selectByFmeIpAndDate(String fmeIp, String date)
    {
        CdrAllCallLegNumDate cdrAllCallLegNumDate = cdrAllCallLegNumDateMapper.selectByFmeIpAndDate(fmeIp, date);
        return cdrAllCallLegNumDate;
    }
    
    /**
     * 添加默认的数据
     *
     * @param fmeIp
     * @return
     */
    @Override
    public int insertInitData(String fmeIp)
    {
        CdrAllCallLegNumDate cdrAllCallLegNumDate = new CdrAllCallLegNumDate();
        cdrAllCallLegNumDate.setNumber(0);
        cdrAllCallLegNumDate.setFmeIp(fmeIp);
        cdrAllCallLegNumDate.setCreateTime(new Date());
        cdrAllCallLegNumDate.setRecordDate(new Date());
        return cdrAllCallLegNumDateMapper.insertCdrAllCallLegNumDate(cdrAllCallLegNumDate);
    }
}
