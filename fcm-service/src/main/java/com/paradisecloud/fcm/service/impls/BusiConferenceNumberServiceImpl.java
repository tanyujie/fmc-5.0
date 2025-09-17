package com.paradisecloud.fcm.service.impls;

import com.paradisecloud.fcm.common.enumer.ConferenceNumberCreateType;
import com.paradisecloud.fcm.common.enumer.ConferenceNumberSectionType;
import com.paradisecloud.fcm.common.enumer.ConferenceNumberStatus;
import com.paradisecloud.fcm.common.enumer.ConferenceNumberType;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceNumberMapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceNumber;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberSectionService;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberService;
import com.paradisecloud.system.dao.model.SysRole;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 会议号码记录Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
@Service
@Transactional
public class BusiConferenceNumberServiceImpl implements IBusiConferenceNumberService
{
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Resource
    private BusiConferenceNumberMapper busiConferenceNumberMapper;
    
    @Resource
    private IBusiConferenceNumberSectionService busiConferenceNumberSectionService;
    
    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-10-29 12:18 
     * @return
     */
    @Override
    public List<DeptRecordCount> getDeptRecordCounts()
    {
        return busiConferenceNumberMapper.getDeptRecordCounts();
    }

    @Override
    public synchronized BusiConferenceNumber autoCreateConferenceNumber(Long deptId, String mcuType)
    {
        long cn = busiConferenceNumberSectionService.autoGenerate(deptId, mcuType);
        return autoCreateConferenceNumber(deptId, mcuType, cn);
    }

    @Override
    public synchronized BusiConferenceNumber autoCreateConferenceNumber(Long deptId, String mcuType, boolean random, boolean simpleCreate)
    {
        long cn = busiConferenceNumberSectionService.autoGenerate(deptId, mcuType, random);
        if (simpleCreate) {
            BusiConferenceNumber busiConferenceNumber = new BusiConferenceNumber();
            busiConferenceNumber.setId(cn);
            return busiConferenceNumber;
        } else {
            return autoCreateConferenceNumber(deptId, mcuType, cn);
        }
    }

    @Override
    public synchronized BusiConferenceNumber autoCreateConferenceNumberIsMute(Long deptId, String mcuType, Boolean isMute)
    {
        long cn = busiConferenceNumberSectionService.autoGenerate(deptId, mcuType);
        return autoCreateConferenceNumberIsMute(deptId, cn,isMute);
    }
    
    @Override
    public BusiConferenceNumber autoCreateConferenceNumber(Long deptId, String mcuType, long cn)
    {
        BusiConferenceNumber busiConferenceNumber = new BusiConferenceNumber();
        busiConferenceNumber.setCreateType(ConferenceNumberCreateType.AUTO.getValue());
        busiConferenceNumber.setType(ConferenceNumberType.COMMON.getValue());
        busiConferenceNumber.setId(cn);
        busiConferenceNumber.setDeptId(deptId);
        busiConferenceNumber.setMcuType(mcuType);

        insertBusiConferenceNumber(busiConferenceNumber);
        return busiConferenceNumber;
    }


    @Override
    public BusiConferenceNumber autoCreateConferenceNumberIsMute(Long deptId, long cn, Boolean isMute) {
        BusiConferenceNumber busiConferenceNumber = new BusiConferenceNumber();
        busiConferenceNumber.setCreateType(ConferenceNumberCreateType.AUTO.getValue());
        busiConferenceNumber.setType(ConferenceNumberType.COMMON.getValue());
        busiConferenceNumber.setId(cn);
        busiConferenceNumber.setDeptId(deptId);

        insertBusiConferenceNumber(busiConferenceNumber);
        return busiConferenceNumber;
    }


    /**
     * 查询会议号码记录
     *
     * @param id 会议号码记录ID
     * @return 会议号码记录
     */
    @Override
    public BusiConferenceNumber selectBusiConferenceNumberById(Long id) {
        return busiConferenceNumberMapper.selectBusiConferenceNumberById(id);
    }

    /**
     * 查询会议号码记录列表
     * 
     * @param busiConferenceNumber 会议号码记录
     * @return 会议号码记录
     */
    @Override
    public List<BusiConferenceNumber> selectBusiConferenceNumberList(BusiConferenceNumber busiConferenceNumber)
    {
        if (busiConferenceNumber.getDeptId() == null)
        {
            busiConferenceNumber.setDeptId(busiConferenceNumber.getDeptId() == null ? SecurityUtils.getLoginUser().getUser().getDeptId() : busiConferenceNumber.getDeptId());
        }
        
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (!SysRole.isAdmin(loginUser.getUser().getRoles()))
        {
            busiConferenceNumber.setCreateUserId(loginUser.getUser().getUserId());
        }
        
        busiConferenceNumber.setCreateType(ConferenceNumberCreateType.MANUAL.getValue());
        
        List<BusiConferenceNumber> busiConferenceNumbers = busiConferenceNumberMapper.selectBusiConferenceNumberList(busiConferenceNumber);

        return busiConferenceNumbers;
    }

    /**
     * 新增会议号码记录
     * 
     * @param busiConferenceNumber 会议号码记录
     * @return 结果
     */
    @Override
    public int insertBusiConferenceNumber(BusiConferenceNumber busiConferenceNumber)
    {
        Integer sectionType = null;
        try {
            Object sectionTypeObj = busiConferenceNumber.getParams().get("sectionType");
            if (sectionTypeObj != null) {
                sectionType = (Integer) sectionTypeObj;
            }
        } catch (Exception e) {
        }
        busiConferenceNumberSectionService.validNumber(busiConferenceNumber.getId(), busiConferenceNumber.getDeptId(), busiConferenceNumber.getMcuType(), sectionType);
        busiConferenceNumber.setCreateTime(new Date());
        busiConferenceNumber.setType(ConferenceNumberType.COMMON.getValue());
        try
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiConferenceNumber.setCreateUserId(loginUser.getUser().getUserId());
            busiConferenceNumber.setCreateUserName(loginUser.getUser().getUserName());
            
            // 校验会议号类型
            ConferenceNumberType.convert(busiConferenceNumber.getType());
            
            if (busiConferenceNumber.getDeptId() == null)
            {
                busiConferenceNumber.setDeptId(busiConferenceNumber.getDeptId() == null ? loginUser.getUser().getDeptId() : busiConferenceNumber.getDeptId());
            }
        }
        catch (Exception e)
        {
        }
        
        if (busiConferenceNumber.getDeptId() == null)
        {
            throw new SystemException(1003232, "添加会议号码失败，部门ID不能为空！");
        }

        busiConferenceNumber.setStatus(ConferenceNumberStatus.IDLE.getValue());
        try
        {
            return busiConferenceNumberMapper.insertBusiConferenceNumber(busiConferenceNumber);
        }
        catch (Throwable e)
        {
            throw new SystemException(1000576, "会议号【" + busiConferenceNumber.getId() + "】重复！");
        }
    }

    /**
     * 修改会议号码记录
     * 
     * @param busiConferenceNumber 会议号码记录
     * @return 结果
     */
    @Override
    public int updateBusiConferenceNumber(BusiConferenceNumber busiConferenceNumber)
    {
        BusiConferenceNumber oldBusiConferenceNumber = busiConferenceNumberMapper.selectBusiConferenceNumberById(busiConferenceNumber.getId());
        if (!oldBusiConferenceNumber.getId().equals(busiConferenceNumber.getId())
                || !oldBusiConferenceNumber.getDeptId().equals(busiConferenceNumber.getDeptId()))
        {
            throw new SystemException(1001234, "会议号和部门不能修改！");
        }

        Integer sectionType = null;
        try {
            Object sectionTypeObj = busiConferenceNumber.getParams().get("sectionType");
            if (sectionTypeObj != null) {
                sectionType = (Integer) sectionTypeObj;
            }
        } catch (Exception e) {
        }
        busiConferenceNumberSectionService.validNumber(busiConferenceNumber.getId(), busiConferenceNumber.getDeptId(), busiConferenceNumber.getMcuType(), sectionType);
        
        ConferenceNumberType.convert(busiConferenceNumber.getType());
        busiConferenceNumber.setUpdateTime(new Date());
        
        return busiConferenceNumberMapper.updateBusiConferenceNumber(busiConferenceNumber);
    }

    /**
     * 删除会议号码记录信息
     * 
     * @param id 会议号码记录ID
     * @return 结果
     */
    @Override
    public int deleteBusiConferenceNumberById(Long id)
    {
        try
        {
            int c = busiConferenceNumberMapper.deleteBusiConferenceNumberById(id);
            if (c == 0)
            {
                throw new SystemException(1001236654, "删除数据库记录失败");
            }
            return c;
        }
        catch (Exception e)
        {
            throw new SystemException(10012364, "非空闲的会议号不能删除！");
        }
    }

}
