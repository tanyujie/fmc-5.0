package com.paradisecloud.fcm.service.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceNumberSectionMapper;
import com.paradisecloud.fcm.dao.mapper.BusiFcmNumberSectionMapper;
import com.paradisecloud.fcm.dao.model.BusiFcmNumberSection;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.service.interfaces.IBusiFcmNumberSectionService;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmAccountCacheAndUtils;
import com.sinhy.exception.SystemException;
import io.jsonwebtoken.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 会议号段Service业务层处理
 *
 * @author LiuXiLong
 * @date 2022-02-25
 */
@Service
public class BusiFcmNumberSectionServiceImpl implements IBusiFcmNumberSectionService
{
    private static final Logger LOG = LoggerFactory.getLogger(BusiFcmNumberSectionServiceImpl.class);

    @Resource
    private BusiFcmNumberSectionMapper busiFcmNumberSectionMapper;

    @Resource
    private BusiConferenceNumberSectionMapper busiConferenceNumberSectionMapper;

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-10-29 12:16
     * @return
     */
    @Override
    public List<DeptRecordCount> getDeptRecordCounts()
    {
        return busiFcmNumberSectionMapper.getDeptRecordCounts();
    }

    /**
     * 会议号样板
     */
    private Pattern numberPattern = Pattern.compile("^[1-9]\\d{3,9}$");

    /**
     * 查询会议号段
     *
     * @param id 会议号段ID
     * @return 会议号段
     */
    @Override
    public BusiFcmNumberSection selectBusiFcmNumberSectionById(Long id)
    {
        return busiFcmNumberSectionMapper.selectBusiFcmNumberSectionById(id);
    }

    /**
     * 查询会议号段列表
     *restart
     *
     * @param busiFcmNumberSection 会议号段
     * @return 会议号段
     */
    @Override
    public String selectBusiFcmNumberSection(BusiFcmNumberSection busiFcmNumberSection)
    {
    	String tipInfo = null;
    	Assert.isTrue(busiFcmNumberSection.getDeptId() != null, "部门ID不能为空");
        BusiFreeSwitchDept busiFreeSwitchDept = DeptFcmMappingCache.getInstance().get(busiFcmNumberSection.getDeptId());
        if (busiFreeSwitchDept == null) {
            return tipInfo;
        }
    	List<ModelBean> mbs = new ArrayList<>();
    	List<BusiFcmNumberSection> nsl = busiFcmNumberSectionMapper.selectBusiFcmNumberSectionList(busiFcmNumberSection);
    	LOG.info("查询号段详情"+nsl);
    	if (null != nsl && nsl.size() > 0){
    	     tipInfo = FcmAccountCacheAndUtils.getInstance().toString(nsl);
    	     LOG.info("部门id"+tipInfo);
    	}else {
    	    Long a = FcmAccountCacheAndUtils.getInstance().deptId(busiFcmNumberSection.getDeptId());
    	    if (a == 0){
    	        Assert.isTrue(false,"该部门未分配FCM号段，请联系管理员分配！");
    	    }else{

    	        BusiFcmNumberSection busiFcmNumberSection1 = new BusiFcmNumberSection();
    	        busiFcmNumberSection1.setDeptId(a);
    	        LOG.info("busiFcmNumberSection1"+ busiFcmNumberSection1);
    	        return this.selectBusiFcmNumberSection(busiFcmNumberSection1);
    	    }
    	}
    	return tipInfo;

    }


    @Override
    public List<BusiFcmNumberSection> selectBusiFcmNumberSectionList(BusiFcmNumberSection busiFcmNumberSection)
    {

        List<ModelBean> mbs = new ArrayList<>();
        List<BusiFcmNumberSection> nsl = busiFcmNumberSectionMapper.selectBusiFcmNumberSectionList(busiFcmNumberSection);

        return nsl;
    }


    /**
     * 新增会议号段
     *
     * @param busiFcmNumberSection 会议号段
     * @return 结果
     */
    @Override
    public int insertBusiFcmNumberSection(BusiFcmNumberSection busiFcmNumberSection)
    {

        busiFcmNumberSection.setCreateTime(new Date());
        validConferenceNumberSection(busiFcmNumberSection);

        busiFcmNumberSection.setCreateTime(new Date());
        int i = busiFcmNumberSectionMapper.insertBusiFcmNumberSection(busiFcmNumberSection);
        if (i > 0) {
            FcmAccountCacheAndUtils.getInstance().add(busiFcmNumberSection);
        }
        return i;
    }

    /**
     * 修改会议号段
     *
     * @param busiFcmNumberSection 会议号段
     * @return 结果
     */
    @Override
    public int updateBusiFcmNumberSection(BusiFcmNumberSection busiFcmNumberSection)
    {
        busiFcmNumberSection.setUpdateTime(new Date());
        validConferenceNumberSection(busiFcmNumberSection);
        int i = busiFcmNumberSectionMapper.updateBusiFcmNumberSection(busiFcmNumberSection);
        if (i > 0) {
            FcmAccountCacheAndUtils.getInstance().update(busiFcmNumberSection);
        }
        return i;
    }

    /**
     * 批量删除会议号段
     *
     * @param ids 需要删除的会议号段ID
     * @return 结果
     */
    @Override
    public int deleteBusiFcmNumberSectionByIds(Long[] ids)
    {
        return busiFcmNumberSectionMapper.deleteBusiFcmNumberSectionByIds(ids);
    }

    /**
     * 删除会议号段信息
     *
     * @param id 会议号段ID
     * @return 结果
     */
    @Override
    public int deleteBusiFcmNumberSectionById(Long id)
    {
        BusiFcmNumberSection busiFcmNumberSection = busiFcmNumberSectionMapper.selectBusiFcmNumberSectionById(id);
        int i = busiFcmNumberSectionMapper.deleteBusiFcmNumberSectionById(id);
        if (i > 0) {
            FcmAccountCacheAndUtils.getInstance().remove(busiFcmNumberSection);
        }
        return i;
    }


    /**
     * <pre>会议号规则校验，8位非0开头</pre>
     * @author lilinhai
     * @since 2021-01-27 10:29
     */
    private void validNumberFormat(Long conferenceNumber)
    {
        // 会议号规则校验，10位非0开头
        if (!numberPattern.matcher(String.valueOf(conferenceNumber)).matches())
        {
            throw new SystemException(1001230, "终端账号号码段，起始值格式有误，需以非0开始，4-10位");
        }
    }

    private void validConferenceNumberSection(BusiFcmNumberSection busiConferenceNumberSection)
    {
        validNumberFormat(busiConferenceNumberSection.getStartValue());
        validNumberFormat(busiConferenceNumberSection.getEndValue());

        long v = busiConferenceNumberSection.getEndValue() - busiConferenceNumberSection.getStartValue();
        Assert.isTrue(v >= 0 && v <= 99999, "终端账号号段起始值须小于等于结束值，且号段容量最大为100000");
        Assert.isTrue(busiFcmNumberSectionMapper.countSection(busiConferenceNumberSection.getStartValue(), busiConferenceNumberSection.getId()) == 0 && busiFcmNumberSectionMapper.countSection(busiConferenceNumberSection.getEndValue(), busiConferenceNumberSection.getId()) == 0, "区间起始值非法，同已有的区间有交集！");
    }
}
