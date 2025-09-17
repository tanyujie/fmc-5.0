package com.paradisecloud.fcm.mcu.zj.service.impls;

import com.paradisecloud.fcm.dao.mapper.BusiZjNumberSectionMapper;
import com.paradisecloud.fcm.dao.model.BusiZjNumberSection;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiZjNumberSectionService;
import com.paradisecloud.fcm.terminal.cache.ZjAccountCache;
import com.sinhy.exception.SystemException;
import io.jsonwebtoken.lang.Assert;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author lilinhai
 * @date 2023-03-27
 */
@Service
public class BusiZjNumberSectionServiceImpl implements IBusiZjNumberSectionService
{
    @Resource
    private BusiZjNumberSectionMapper busiZjNumberSectionMapper;

    /**
     * 会议号样板
     */
    private Pattern numberPattern = Pattern.compile("^[1-9]\\d{3,9}$");

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public BusiZjNumberSection selectBusiZjNumberSectionById(Long id)
    {
        return busiZjNumberSectionMapper.selectBusiZjNumberSectionById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param busiZjNumberSection 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<BusiZjNumberSection> selectBusiZjNumberSectionList(BusiZjNumberSection busiZjNumberSection)
    {
        Long deptId = busiZjNumberSection.getDeptId();
        List<BusiZjNumberSection> busiZjNumberSectionList = ZjAccountCache.getInstance().getByDeptId(deptId);
        return busiZjNumberSectionList;
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param busiZjNumberSection 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertBusiZjNumberSection(BusiZjNumberSection busiZjNumberSection)
    {
        validConferenceNumberSection(busiZjNumberSection);
        busiZjNumberSection.setCreateTime(new Date());
        int i = busiZjNumberSectionMapper.insertBusiZjNumberSection(busiZjNumberSection);
        if (i > 0) {
            List<BusiZjNumberSection> busiZjNumberSectionList = busiZjNumberSectionMapper.selectBusiZjNumberSectionList(busiZjNumberSection);
            if (busiZjNumberSectionList != null && busiZjNumberSectionList.size() > 0 ) {
                ZjAccountCache.getInstance().add(busiZjNumberSectionList.get(0));
            }
        }
        return i;
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param busiZjNumberSection 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateBusiZjNumberSection(BusiZjNumberSection busiZjNumberSection)
    {
        validConferenceNumberSection(busiZjNumberSection);
        busiZjNumberSection.setUpdateTime(new Date());
        int i = busiZjNumberSectionMapper.updateBusiZjNumberSection(busiZjNumberSection);
        if (i > 0) {
            List<BusiZjNumberSection> busiZjNumberSectionList = busiZjNumberSectionMapper.selectBusiZjNumberSectionList(busiZjNumberSection);
            if (busiZjNumberSectionList != null && busiZjNumberSectionList.size() > 0 ) {
                ZjAccountCache.getInstance().update(busiZjNumberSectionList.get(0));
            }
        }
        return i;
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiZjNumberSectionByIds(Long[] ids)
    {
        int i = 0;
        for (Long id : ids) {
            i = deleteBusiZjNumberSectionById(id);
        }
        return i;
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiZjNumberSectionById(Long id)
    {
        int i = busiZjNumberSectionMapper.deleteBusiZjNumberSectionById(id);
        if (i > 0) {
            ZjAccountCache.getInstance().remove(id);
        }
        return i;
    }

    @Override
    public String selectBusiZjNumberSection(BusiZjNumberSection busiZjNumberSection) {
        String zjAccountInfo = null;
        Long deptId = busiZjNumberSection.getDeptId();
        Assert.isTrue(deptId != null , "部门ID不能为空!");
        List<BusiZjNumberSection> busiZjNumberSectionList = ZjAccountCache.getInstance().getByDeptId(deptId);
        if (busiZjNumberSectionList != null && busiZjNumberSectionList.size() > 0) {
            zjAccountInfo = ZjAccountCache.getInstance().toString(busiZjNumberSectionList);
        } else {
            zjAccountInfo = ZjAccountCache.getInstance().toString(ZjAccountCache.getInstance().getZjAccountByDeptId(deptId));
        }
        return zjAccountInfo;
    }

    @Override
    public List<DeptRecordCount> getDeptRecordCounts() {
        return busiZjNumberSectionMapper.getDeptRecordCounts();
    }

    /**
     * <pre>会议号规则校验，8位非0开头</pre>
     * @author lilinhai
     * @since 2021-01-27 10:29
     * @param conferenceNumber void
     */
    private void validNumberFormat(Long conferenceNumber)
    {
        // 会议号规则校验，10位非0开头
        if (!numberPattern.matcher(String.valueOf(conferenceNumber)).matches())
        {
            throw new SystemException(1001230, "终端账号号码段，起始值格式有误，需以非0开始，4-10位");
        }
    }

    private void validConferenceNumberSection(BusiZjNumberSection busiZjNumberSection)
    {
        validNumberFormat(busiZjNumberSection.getStartValue());
        validNumberFormat(busiZjNumberSection.getEndValue());

        long v = busiZjNumberSection.getEndValue() - busiZjNumberSection.getStartValue();
        Assert.isTrue(v >= 0 && v <= 99999, "终端账号号段起始值须小于等于结束值，且号段容量最大为100000");
        Assert.isTrue(busiZjNumberSectionMapper.countSection(busiZjNumberSection.getStartValue(), busiZjNumberSection.getId()) == 0 && busiZjNumberSectionMapper.countSection(busiZjNumberSection.getEndValue(), busiZjNumberSection.getId()) == 0, "区间起始值非法，同已有的区间有交集！");
    }
}
