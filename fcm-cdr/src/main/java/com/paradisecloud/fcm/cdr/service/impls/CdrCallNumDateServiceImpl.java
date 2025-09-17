package com.paradisecloud.fcm.cdr.service.impls;

import com.paradisecloud.fcm.cdr.service.interfaces.ICdrCallNumDateService;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.CdrCallNumDateMapper;
import com.paradisecloud.fcm.dao.model.CdrCallNumDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * 每天开始会议的数量Service业务层处理
 *
 * @author johnson liu
 * @date 2021/6/16 17:41
 */
@Service
public class CdrCallNumDateServiceImpl implements ICdrCallNumDateService
{
    @Autowired
    private CdrCallNumDateMapper cdrCallNumDateMapper;
    
    /**
     * 查询每天开始会议的数量
     *
     * @param id 每天开始会议的数量ID
     * @return 每天开始会议的数量
     */
    @Override
    public CdrCallNumDate selectCdrCallNumDateById(Long id)
    {
        return cdrCallNumDateMapper.selectCdrCallNumDateById(id);
    }
    
    /**
     * 查询每天开始会议的数量列表
     *
     * @param cdrCallNumDate 每天开始会议的数量
     * @return 每天开始会议的数量
     */
    @Override
    public List<CdrCallNumDate> selectCdrCallNumDateList(CdrCallNumDate cdrCallNumDate)
    {
        return cdrCallNumDateMapper.selectCdrCallNumDateList(cdrCallNumDate);
    }
    
    /**
     * 新增每天开始会议的数量
     *
     * @param cdrCallNumDate 每天开始会议的数量
     * @return 结果
     */
    @Override
    public int insertCdrCallNumDate(CdrCallNumDate cdrCallNumDate)
    {
        cdrCallNumDate.setCreateTime(new Date());
        return cdrCallNumDateMapper.insertCdrCallNumDate(cdrCallNumDate);
    }
    
    /**
     * 修改每天开始会议的数量
     *
     * @param cdrCallNumDate 每天开始会议的数量
     * @return 结果
     */
    @Override
    public int updateCdrCallNumDate(CdrCallNumDate cdrCallNumDate)
    {
        cdrCallNumDate.setUpdateTime(new Date());
        return cdrCallNumDateMapper.updateCdrCallNumDate(cdrCallNumDate);
    }
    
    /**
     * 批量删除每天开始会议的数量
     *
     * @param ids 需要删除的每天开始会议的数量ID
     * @return 结果
     */
    @Override
    public int deleteCdrCallNumDateByIds(Long[] ids)
    {
        return cdrCallNumDateMapper.deleteCdrCallNumDateByIds(ids);
    }
    
    /**
     * 删除每天开始会议的数量信息
     *
     * @param id 每天开始会议的数量ID
     * @return 结果
     */
    @Override
    public int deleteCdrCallNumDateById(Long id)
    {
        return cdrCallNumDateMapper.deleteCdrCallNumDateById(id);
    }
    
    /**
     * 通过监听事件每天新增或更新创会议的数量
     *
     * @param deptId
     * @param fmeIp
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateByEvent(Long deptId, String fmeIp)
    {
        String date = DateUtil.convertDateToString(LocalDate.now(), null);
        CdrCallNumDate cdrCallNumDate = cdrCallNumDateMapper.selectByDeptIdAndFmeIpAndDate(deptId, fmeIp, date);
        int number = 0;
        if (cdrCallNumDate == null)
        {
            cdrCallNumDate = new CdrCallNumDate();
            cdrCallNumDate.setFmeIp(fmeIp);
            cdrCallNumDate.setDeptId(deptId.intValue());
            number = 1;
            cdrCallNumDate.setNumber(number);
            cdrCallNumDate.setRecordDate(new Date());
            cdrCallNumDate.setCreateTime(new Date());
            cdrCallNumDateMapper.insertCdrCallNumDate(cdrCallNumDate);
        }
        else
        {
            number = cdrCallNumDate.getNumber() + 1;
            cdrCallNumDate.setNumber(number);
            cdrCallNumDate.setUpdateTime(new Date());
            cdrCallNumDateMapper.updateCdrCallNumDate(cdrCallNumDate);
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
    public CdrCallNumDate selectByFmeIpAndDate(Long deptId, String fmeIp, String date)
    {
        CdrCallNumDate cdrCallNumDate = cdrCallNumDateMapper.selectByDeptIdAndFmeIpAndDate(deptId, fmeIp, date);
        return cdrCallNumDate;
    }
    
    /**
     * 添加默认的数据
     *
     * @param fmeIp
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInitData(String fmeIp)
    {
        CdrCallNumDate obj = new CdrCallNumDate();
        obj.setNumber(0);
        obj.setFmeIp(fmeIp);
        obj.setCreateTime(new Date());
        obj.setRecordDate(new Date());
        return cdrCallNumDateMapper.insertCdrCallNumDate(obj);
    }
}
