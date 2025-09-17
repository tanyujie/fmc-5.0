package com.paradisecloud.fcm.cdr.service.impls;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.paradisecloud.fcm.cdr.service.interfaces.ICdrCallLegNumDateService;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.CdrCallLegNumDateMapper;
import com.paradisecloud.fcm.dao.model.CdrCallLegNumDate;

/**
 * @author johnson liu
 * @date 2021/6/16 17:36
 */
@Service
public class CdrCallLegNumDateServiceImpl implements ICdrCallLegNumDateService
{
    @Autowired
    private CdrCallLegNumDateMapper cdrCallLegNumDateMapper;
    
    /**
     * 查询每天参会的数量
     *
     * @param id 每天参会的数量ID
     * @return 每天参会的数量
     */
    @Override
    public CdrCallLegNumDate selectCdrCallLegNumDateById(Long id)
    {
        return cdrCallLegNumDateMapper.selectCdrCallLegNumDateById(id);
    }
    
    /**
     * 查询每天参会的数量列表
     *
     * @param cdrCallLegNumDate 每天参会的数量
     * @return 每天参会的数量
     */
    @Override
    public List<CdrCallLegNumDate> selectCdrCallLegNumDateList(CdrCallLegNumDate cdrCallLegNumDate)
    {
        return cdrCallLegNumDateMapper.selectCdrCallLegNumDateList(cdrCallLegNumDate);
    }
    
    /**
     * 新增每天参会的数量
     *
     * @param cdrCallLegNumDate 每天参会的数量
     * @return 结果
     */
    @Override
    public int insertCdrCallLegNumDate(CdrCallLegNumDate cdrCallLegNumDate)
    {
        cdrCallLegNumDate.setCreateTime(new Date());
        return cdrCallLegNumDateMapper.insertCdrCallLegNumDate(cdrCallLegNumDate);
    }
    
    /**
     * 修改每天参会的数量
     *
     * @param cdrCallLegNumDate 每天参会的数量
     * @return 结果
     */
    @Override
    public int updateCdrCallLegNumDate(CdrCallLegNumDate cdrCallLegNumDate)
    {
        cdrCallLegNumDate.setUpdateTime(new Date());
        return cdrCallLegNumDateMapper.updateCdrCallLegNumDate(cdrCallLegNumDate);
    }
    
    /**
     * 批量删除每天参会的数量
     *
     * @param ids 需要删除的每天参会的数量ID
     * @return 结果
     */
    @Override
    public int deleteCdrCallLegNumDateByIds(Long[] ids)
    {
        return cdrCallLegNumDateMapper.deleteCdrCallLegNumDateByIds(ids);
    }
    
    /**
     * 删除每天参会的数量信息
     *
     * @param id 每天参会的数量ID
     * @return 结果
     */
    @Override
    public int deleteCdrCallLegNumDateById(Long id)
    {
        return cdrCallLegNumDateMapper.deleteCdrCallLegNumDateById(id);
    }
    
    /**
     * 通过监听事件每天新增或更新参会者的数量
     *
     * @param deptId
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateByEvent(Long deptId, String fmeIp)
    {
        try
        {
            String date = DateUtil.convertDateToString(LocalDate.now(), null);
            CdrCallLegNumDate cdrCallLegNumDate = cdrCallLegNumDateMapper.selectByDeptIdAndFmeIpAndDate(deptId, fmeIp, date);
            int number = 0;
            if (cdrCallLegNumDate == null)
            {
                cdrCallLegNumDate = new CdrCallLegNumDate();
                cdrCallLegNumDate.setFmeIp(fmeIp);
                cdrCallLegNumDate.setDeptId(deptId.intValue());
                number = 1;
                cdrCallLegNumDate.setNumber(number);
                cdrCallLegNumDate.setRecordDate(new Date());
                cdrCallLegNumDate.setCreateTime(new Date());
                cdrCallLegNumDateMapper.insertCdrCallLegNumDate(cdrCallLegNumDate);
            }
            else
            {
                number = cdrCallLegNumDate.getNumber() + 1;
                
                cdrCallLegNumDate.setNumber(number);
                cdrCallLegNumDate.setUpdateTime(new Date());
                cdrCallLegNumDateMapper.updateByCurrentDate(cdrCallLegNumDate);
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
     * @param deptId
     * @param fmeIp
     * @param date
     * @return
     */
    @Override
    public CdrCallLegNumDate selectByFmeIpAndDate(Long deptId, String fmeIp, String date)
    {
        CdrCallLegNumDate cdrCallLegNumDate = cdrCallLegNumDateMapper.selectByDeptIdAndFmeIpAndDate(null, fmeIp, date);
        return cdrCallLegNumDate;
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
        CdrCallLegNumDate cdrCallLegNumDate = new CdrCallLegNumDate();
        cdrCallLegNumDate.setNumber(0);
        cdrCallLegNumDate.setFmeIp(fmeIp);
        cdrCallLegNumDate.setCreateTime(new Date());
        cdrCallLegNumDate.setRecordDate(new Date());
        return cdrCallLegNumDateMapper.insertCdrCallLegNumDate(cdrCallLegNumDate);
    }
}
