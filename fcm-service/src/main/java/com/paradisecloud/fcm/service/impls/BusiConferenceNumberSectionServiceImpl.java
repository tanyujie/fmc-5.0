package com.paradisecloud.fcm.service.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.ConferenceNumberSectionType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceNumberMapper;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceNumberSectionMapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceNumber;
import com.paradisecloud.fcm.dao.model.BusiConferenceNumberSection;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberSectionService;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.exception.SystemException;
import io.jsonwebtoken.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 会议号段Service业务层处理
 *
 * @author lilinhai
 * @date 2021-05-19
 */
@Service
public class BusiConferenceNumberSectionServiceImpl implements IBusiConferenceNumberSectionService {

    private final Logger logger = LoggerFactory.getLogger(BusiConferenceNumberSectionServiceImpl.class);

    @Resource
    private BusiConferenceNumberSectionMapper busiConferenceNumberSectionMapper;

    @Resource
    private BusiConferenceNumberMapper busiConferenceNumberMapper;

    /**
     * 会议号样板
     */
    private final Pattern numberPattern = Pattern.compile("^[2-9]\\d{3,9}$");
    /**
     * 会议号样板smc3
     */
    private final Pattern numberPattern_smc3 = Pattern.compile("^[1-9]\\d{3,7}$");
    /**
     * 会议号样板mcu-zj
     */
    private final Pattern numberPattern_mcu_zj = Pattern.compile("^[1-9]\\d{3}$");
    /**
     * 会议号样板smc2
     */
    private final Pattern numberPattern_smc2 = Pattern.compile("^[1-9]\\d{1,4}$");

    /**
     * 会议号样板smc2
     */
    private final Pattern numberPattern_zte = Pattern.compile("^[1-9]\\d{3,6}$");

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     *
     * @return
     * @author sinhy
     * @since 2021-10-29 12:16
     */
    @Override
    public List<DeptRecordCount> getDeptRecordCounts() {
        return busiConferenceNumberSectionMapper.getDeptRecordCounts();
    }

    /**
     * 查询会议号段
     *
     * @param id 会议号段ID
     * @return 会议号段
     */
    @Override
    public BusiConferenceNumberSection selectBusiConferenceNumberSectionById(Long id) {
        return busiConferenceNumberSectionMapper.selectBusiConferenceNumberSectionById(id);
    }

    /**
     * 查询会议号段列表
     *
     * @param busiConferenceNumberSection 会议号段
     * @return 会议号段
     */
    @Override
    public List<ModelBean> selectBusiConferenceNumberSectionList(BusiConferenceNumberSection busiConferenceNumberSection) {
        List<ModelBean> mbs = new ArrayList<>();
        List<BusiConferenceNumberSection> nsl = busiConferenceNumberSectionMapper.selectBusiConferenceNumberSectionList(busiConferenceNumberSection);
        for (BusiConferenceNumberSection busiConferenceNumberSection2 : nsl) {
            ModelBean mb = new ModelBean(busiConferenceNumberSection2);
            mb.put("deptName", SysDeptCache.getInstance().get(busiConferenceNumberSection2.getDeptId()).getDeptName());
            McuType mcuType = McuType.convert(busiConferenceNumberSection2.getMcuType());
            mb.put("mcuTypeAlias", mcuType.getAlias());
            ConferenceNumberSectionType conferenceNumberSectionType = ConferenceNumberSectionType.convert(busiConferenceNumberSection2.getSectionType());
            mb.put("sectionTypeAlias", conferenceNumberSectionType.getName());
            String remark = busiConferenceNumberSection2.getRemark();
            if (remark == null) {
                remark = "";
            }
            remark += "（";
            if (conferenceNumberSectionType == ConferenceNumberSectionType.COMMON) {
                remark += "未设置固定号段时，统一使用通用号段；设置了固定号段时，固定号码使用固定号段。";
            }
            if (mcuType == McuType.SMC3) {
                remark += "固定号码区间须设置在SMC虚拟会议室号段区间内。";
            }
            remark += "）";
            mb.put("remark", remark);
            mbs.add(mb);
        }
        return mbs;
    }

    /**
     * 新增会议号段
     *
     * @param busiConferenceNumberSection 会议号段
     * @return 结果
     */
    @Override
    public int insertBusiConferenceNumberSection(BusiConferenceNumberSection busiConferenceNumberSection) {
        busiConferenceNumberSection.setCreateTime(new Date());
        validConferenceNumberSection(busiConferenceNumberSection);
        return busiConferenceNumberSectionMapper.insertBusiConferenceNumberSection(busiConferenceNumberSection);
    }

    /**
     * 修改会议号段
     *
     * @param busiConferenceNumberSection 会议号段
     * @return 结果
     */
    @Override
    public int updateBusiConferenceNumberSection(BusiConferenceNumberSection busiConferenceNumberSection) {
        busiConferenceNumberSection.setUpdateTime(new Date());
        validConferenceNumberSection(busiConferenceNumberSection);
        return busiConferenceNumberSectionMapper.updateBusiConferenceNumberSection(busiConferenceNumberSection);
    }

    /**
     * 批量删除会议号段
     *
     * @param ids 需要删除的会议号段ID
     * @return 结果
     */
    @Override
    public int deleteBusiConferenceNumberSectionByIds(Long[] ids) {
        return busiConferenceNumberSectionMapper.deleteBusiConferenceNumberSectionByIds(ids);
    }

    /**
     * 删除会议号段信息
     *
     * @param id 会议号段ID
     * @return 结果
     */
    @Override
    public int deleteBusiConferenceNumberSectionById(Long id) {
        return busiConferenceNumberSectionMapper.deleteBusiConferenceNumberSectionById(id);
    }

    public long autoGenerate(Long deptId, String mcuType) {
        return autoGenerate(deptId, mcuType, true);
    }

    public long autoGenerate(Long deptId, String mcuType, boolean random) {
        BusiConferenceNumber con = new BusiConferenceNumber();
        List<BusiConferenceNumber> bcns = busiConferenceNumberMapper.selectBusiConferenceNumberList(con);
        Set<Long> cns = new HashSet<>();
        Set<Long> idleCns = new HashSet<>();
        for (BusiConferenceNumber cn : bcns) {
            cns.add(cn.getId());
            if (cn.getStatus() == 1 && cn.getCreateType() == 2) {
                idleCns.add(cn.getId());
            }
        }

        logger.info("autoGenerate, 加载号码到内存成功：" + deptId);
        List<BusiConferenceNumberSection> bnss = getBusiConferenceNumberSections(deptId, mcuType, ConferenceNumberSectionType.COMMON.getValue());
        logger.info("autoGenerate, 向上查找区间成功：" + deptId + ", bnss: " + bnss);
        if (!ObjectUtils.isEmpty(bnss)) {
            if (random) {
                Random rd = new Random();
                while (bnss.size() > 0) {
                    int i = rd.nextInt(bnss.size());
                    BusiConferenceNumberSection cns0 = bnss.remove(i);
                    List<Long> allCns = new ArrayList<>();
                    for (long cn = cns0.getStartValue(); cn <= cns0.getEndValue(); cn++) {
                        if (!cns.contains(cn)) {
                            allCns.add(cn);
                        } else if (cns.contains(cn) && idleCns.contains(cn)) {
                            allCns.add(cn);
                        }
                    }

                    logger.info("autoGenerate, allCns大小：" + allCns.size());
                    if (!ObjectUtils.isEmpty(allCns)) {
                        Long cn = allCns.get(rd.nextInt(allCns.size()));
                        busiConferenceNumberMapper.deleteBusiConferenceNumberById(cn);
                        return cn;
                    }
                }
            } else {
                for (BusiConferenceNumberSection cns0 : bnss) {
                    for (long cn = cns0.getStartValue(); cn <= cns0.getEndValue(); cn++) {
                        if (!cns.contains(cn)) {
                            return cn;
                        }
                        if (cns.contains(cn) && idleCns.contains(cn)) {
                            busiConferenceNumberMapper.deleteBusiConferenceNumberById(cn);
                            return cn;
                        }
                    }
                }
            }
        }
        throw new SystemException(1005435, "【" + SysDeptCache.getInstance().get(deptId).getDeptName() + "】的会议号段资源已使用完毕，请联系系统管理员进行号段分配，或回收其它会议号以使用！");
    }

    public String getConferenceNumberSections(Long deptId, String mcuType, Integer sectionType) {
        List<BusiConferenceNumberSection> bnss = getBusiConferenceNumberSections(deptId, mcuType, sectionType);
        if (!ObjectUtils.isEmpty(bnss)) {
            StringBuilder sb = new StringBuilder();
            for (BusiConferenceNumberSection busiConferenceNumberSection : bnss) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append('[').append(busiConferenceNumberSection.getStartValue()).append(", ").append(busiConferenceNumberSection.getEndValue()).append(']');
            }

            return sb.toString();
        } else {
            return "该租户还未绑定会议号段，请联系系统管理员进行绑定！";
        }
    }

    private List<BusiConferenceNumberSection> getBusiConferenceNumberSections(Long deptId, String mcuType, Integer sectionType) {
        BusiConferenceNumberSection con1 = new BusiConferenceNumberSection();
        con1.setDeptId(deptId);
        con1.setMcuType(mcuType);
        List<BusiConferenceNumberSection> bnss = new ArrayList<>(busiConferenceNumberSectionMapper.selectBusiConferenceNumberSectionList(con1));
        while (ObjectUtils.isEmpty(bnss)) {
            SysDept sysDept = SysDeptCache.getInstance().get(deptId);
            if (sysDept.getParentId() != null && sysDept.getParentId().longValue() > 0) {
                logger.info("autoGenerate, 当前部门【" + deptId + "】没有配置会议号段，准备向上【" + sysDept.getParentId() + "】查找会议号区间！");

                deptId = sysDept.getParentId();
                con1.setDeptId(sysDept.getParentId());
                bnss = new ArrayList<>(busiConferenceNumberSectionMapper.selectBusiConferenceNumberSectionList(con1));
            } else {
                break;
            }
        }
        if (ConferenceNumberSectionType.convert(sectionType) == ConferenceNumberSectionType.FIXED) {
            List<BusiConferenceNumberSection> bnssListNew = new ArrayList<>();
            for (BusiConferenceNumberSection section : bnss) {
                if (ConferenceNumberSectionType.convert(section.getSectionType()) == ConferenceNumberSectionType.FIXED) {
                    bnssListNew.add(section);
                }
            }
            if (bnssListNew.size() > 0) {
                return bnssListNew;
            }
        }
        if (ConferenceNumberSectionType.convert(sectionType) == ConferenceNumberSectionType.COMMON) {
            List<BusiConferenceNumberSection> bnssListNew = new ArrayList<>();
            for (BusiConferenceNumberSection section : bnss) {
                if (ConferenceNumberSectionType.convert(section.getSectionType()) == ConferenceNumberSectionType.COMMON) {
                    bnssListNew.add(section);
                }
            }
            if (bnssListNew.size() > 0) {
                return bnssListNew;
            }
        }

        return bnss;
    }

    /**
     * <pre>会议号规则校验，8位非0开头</pre>
     *
     * @author lilinhai
     * @since 2021-01-27 10:29
     */
    public void validNumber(Long conferenceNumber, Long deptId, String mcuType) {
        validNumber(conferenceNumber, deptId, mcuType, ConferenceNumberSectionType.COMMON.getValue());
    }

    /**
     * <pre>会议号规则校验，8位非0开头</pre>
     *
     * @author lilinhai
     * @since 2021-01-27 10:29
     */
    public void validNumber(Long conferenceNumber, Long deptId, String mcuType, Integer sectionType) {
        List<BusiConferenceNumberSection> bnss = getBusiConferenceNumberSections(deptId, mcuType, sectionType);
        if (!ObjectUtils.isEmpty(bnss)) {
            StringBuilder sb = new StringBuilder();
            for (BusiConferenceNumberSection busiConferenceNumberSection : bnss) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append('[').append(busiConferenceNumberSection.getStartValue()).append(", ").append(busiConferenceNumberSection.getEndValue()).append(']');
                if (conferenceNumber.longValue() <= busiConferenceNumberSection.getEndValue().longValue() && conferenceNumber.longValue() >= busiConferenceNumberSection.getStartValue().longValue()) {
                    return;
                }
            }

            throw new SystemException(1009877, "该租户的会议号只允许在如下区间创建：" + sb);
        } else {
            throw new SystemException(1009877, "该租户还未绑定允许使用会议号段，请先绑定！");
        }
    }

    /**
     * <pre>会议号规则校验，8位非0开头</pre>
     *
     * @author lilinhai
     * @since 2021-01-27 10:29
     */
    private void validNumberFormat(Long conferenceNumber, String mcuType) {
        // 会议号规则校验，10位非0开头
        if (McuType.MCU_ZJ.getCode().equals(mcuType)) {
            if (!numberPattern.matcher(String.valueOf(conferenceNumber)).matches()) {
                throw new SystemException(1001230, "会议号码段，起始值格式有误，需以非0开始，4位");
            }
        }
        else if (McuType.SMC3.getCode().equals(mcuType)) {
            if (!numberPattern_smc3.matcher(String.valueOf(conferenceNumber)).matches()) {
                throw new SystemException(1001230, "会议号码段，起始值格式有误，需以非0开始，6位");
            }
        }
        else if (McuType.SMC2.getCode().equals(mcuType)) {
            if (!numberPattern_smc2.matcher(String.valueOf(conferenceNumber)).matches()) {
                throw new SystemException(1001230, "会议号码段，起始值格式有误需以非0开始，2-5位");
            }
        }else if(McuType.MCU_ZTE.getCode().equals(mcuType)){
            if (!numberPattern_zte.matcher(String.valueOf(conferenceNumber)).matches()) {
                throw new SystemException(1001230, "会议号码段，起始值格式有误，需以非0开始，4-7位");
            }
        }else {
            if (!numberPattern.matcher(String.valueOf(conferenceNumber)).matches()) {
                throw new SystemException(1001230, "会议号码段，起始值格式有误，需以非0开始，4-10位");
            }
        }

    }

    private void validConferenceNumberSection(BusiConferenceNumberSection busiConferenceNumberSection) {
        validNumberFormat(busiConferenceNumberSection.getStartValue(), busiConferenceNumberSection.getMcuType());
        validNumberFormat(busiConferenceNumberSection.getEndValue(), busiConferenceNumberSection.getMcuType());
        long v = busiConferenceNumberSection.getEndValue() - busiConferenceNumberSection.getStartValue();
        Assert.isTrue(v >= 0 && v <= 999, "会议号段起始值须小于等于结束值，且号段容量最大为1000");
        Assert.isTrue(busiConferenceNumberSectionMapper.countSection(busiConferenceNumberSection.getStartValue(), busiConferenceNumberSection.getId()) == 0 && busiConferenceNumberSectionMapper.countSection(busiConferenceNumberSection.getEndValue(), busiConferenceNumberSection.getId()) == 0, "区间起始值非法，同已有的区间有交集！");
    }
}
