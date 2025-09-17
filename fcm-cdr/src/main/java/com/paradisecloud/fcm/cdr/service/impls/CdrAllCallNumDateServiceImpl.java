package com.paradisecloud.fcm.cdr.service.impls;

import com.paradisecloud.fcm.cdr.service.interfaces.ICdrAllCallNumDateService;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.CdrAllCallNumDateMapper;
import com.paradisecloud.fcm.dao.model.CdrAllCallNumDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
public class CdrAllCallNumDateServiceImpl implements ICdrAllCallNumDateService
{
    @Resource
    private CdrAllCallNumDateMapper cdrAllCallNumDateMapper;
    
    /**
     * 查询每天开始会议的数量
     *
     * @param id 每天开始会议的数量ID
     * @return 每天开始会议的数量
     */
    @Override
    public CdrAllCallNumDate selectCdrAllCallNumDateById(Long id)
    {
        return cdrAllCallNumDateMapper.selectCdrAllCallNumDateById(id);
    }
    
    /**
     * 查询每天开始会议的数量列表
     *
     * @param cdrAllCallNumDate 每天开始会议的数量
     * @return 每天开始会议的数量
     */
    @Override
    public List<CdrAllCallNumDate> selectCdrAllCallNumDateList(CdrAllCallNumDate cdrAllCallNumDate)
    {
        return cdrAllCallNumDateMapper.selectCdrAllCallNumDateList(cdrAllCallNumDate);
    }
    
    /**
     * 新增每天开始会议的数量
     *
     * @param cdrAllCallNumDate 每天开始会议的数量
     * @return 结果
     */
    @Override
    public int insertCdrAllCallNumDate(CdrAllCallNumDate cdrAllCallNumDate)
    {
        cdrAllCallNumDate.setCreateTime(new Date());
        return cdrAllCallNumDateMapper.insertCdrAllCallNumDate(cdrAllCallNumDate);
    }
    
    /**
     * 修改每天开始会议的数量
     *
     * @param cdrAllCallNumDate 每天开始会议的数量
     * @return 结果
     */
    @Override
    public int updateCdrAllCallNumDate(CdrAllCallNumDate cdrAllCallNumDate)
    {
        cdrAllCallNumDate.setUpdateTime(new Date());
        return cdrAllCallNumDateMapper.updateCdrAllCallNumDate(cdrAllCallNumDate);
    }
    
    /**
     * 批量删除每天开始会议的数量
     *
     * @param ids 需要删除的每天开始会议的数量ID
     * @return 结果
     */
    @Override
    public int deleteCdrAllCallNumDateByIds(Long[] ids)
    {
        return cdrAllCallNumDateMapper.deleteCdrAllCallNumDateByIds(ids);
    }
    
    /**
     * 删除每天开始会议的数量信息
     *
     * @param id 每天开始会议的数量ID
     * @return 结果
     */
    @Override
    public int deleteCdrAllCallNumDateById(Long id)
    {
        return cdrAllCallNumDateMapper.deleteCdrAllCallNumDateById(id);
    }
    
    /**
     * 通过监听事件每天新增或更新创会议的数量
     *
     * @param fmeIp
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateByEvent(String fmeIp)
    {
        String date = DateUtil.convertDateToString(LocalDate.now(), null);
        CdrAllCallNumDate cdrAllCallNumDate = cdrAllCallNumDateMapper.selectByFmeIpAndDate(fmeIp, date);
        int number = 0;
        if (cdrAllCallNumDate == null)
        {
            cdrAllCallNumDate = new CdrAllCallNumDate();
            cdrAllCallNumDate.setFmeIp(fmeIp);
            number = 1;
            cdrAllCallNumDate.setNumber(number);
            cdrAllCallNumDate.setRecordDate(new Date());
            cdrAllCallNumDate.setCreateTime(new Date());
            cdrAllCallNumDateMapper.insertCdrAllCallNumDate(cdrAllCallNumDate);
        }
        else
        {
            number = cdrAllCallNumDate.getNumber() + 1;
            cdrAllCallNumDate.setNumber(number);
            cdrAllCallNumDate.setUpdateTime(new Date());
            cdrAllCallNumDateMapper.updateCdrAllCallNumDate(cdrAllCallNumDate);
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
    public CdrAllCallNumDate selectByFmeIpAndDate(String fmeIp, String date)
    {
        CdrAllCallNumDate cdrAllCallNumDate = cdrAllCallNumDateMapper.selectByFmeIpAndDate(fmeIp, date);
        return cdrAllCallNumDate;
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
        CdrAllCallNumDate obj = new CdrAllCallNumDate();
        obj.setNumber(0);
        obj.setFmeIp(fmeIp);
        obj.setCreateTime(new Date());
        obj.setRecordDate(new Date());
        return cdrAllCallNumDateMapper.insertCdrAllCallNumDate(obj);
    }
}
