package com.paradisecloud.fcm.web.service.impls;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.SipAccountUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.TerminalSearchVo;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.DeptHwcloudMappingCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.*;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.*;
import com.paradisecloud.fcm.mcu.zj.task.DelayTaskService;
import com.paradisecloud.fcm.mcu.zj.task.GetUsrOnlineStatusTask;
import com.paradisecloud.fcm.service.task.HuaweiBarSnTask;
import com.paradisecloud.fcm.smc2.cache.Smc2Bridge;
import com.paradisecloud.fcm.smc2.cache.Smc2BridgeCache;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.cache.ZjAccountCache;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmAccountCacheAndUtils;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.constant.FcmConfigConstant;
import com.paradisecloud.fcm.terminal.fs.db.FreeSwitchTransaction;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridgeCluster;
import com.paradisecloud.fcm.terminal.fsbc.cache.DeptFsbcMappingCache;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcCredential;
import com.paradisecloud.fcm.terminal.service.interfaces.IBusiTerminalMeetingJoinSettingsService;
import com.paradisecloud.fcm.terminal.service.interfaces.IBusiTerminalService;
import com.paradisecloud.fcm.terminal.service.interfaces.ITerminalInterceptor;
import com.paradisecloud.fcm.web.service.interfaces.IBusiTerminalWebService;
import com.paradisecloud.fcm.wvp.gb28181.WvpBridge;
import com.paradisecloud.fcm.wvp.gb28181.WvpBridgeCache;
import com.paradisecloud.fcm.wvp.gb28181.reponse.WvpCommonResponse;
import com.paradisecloud.fcm.wvp.gb28181.request.WvpAddDeviceRequest;
import com.paradisecloud.fcm.wvp.gb28181.service.WvpDeviceService;
import com.paradisecloud.fcm.zte.cache.McuZteBridgeCache;
import com.paradisecloud.fcm.zte.cache.model.McuZteBridge;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.service.ISysDeptService;
import com.paradisecloud.system.service.ISysUserService;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.RegExpUtils;
import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.pojo.local.*;
import com.suntek.smc.esdk.service.client.SiteServiceEx;
import com.zte.m900.bean.TerminalSimpleInfo;
import com.zte.m900.bean.TerminalSimpleInfoV2;
import com.zte.m900.request.AddAddressBookRequest;
import com.zte.m900.request.DelAddressBookRequest;
import com.zte.m900.request.QueryAddressBookV2Request;
import com.zte.m900.response.AddAddressBookResponse;
import com.zte.m900.response.DelAddressBookResponse;
import com.zte.m900.response.QueryAddressBookV2Response;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 终端信息Service业务层处理
 *
 * @author lilinhai
 * @date 2021-01-20
 */
@Service
@Transactional
public class BusiTerminalWebServiceImpl implements IBusiTerminalWebService {
    public static final String SCSCZT = "SCSCZT";
    @Resource
    private BusiFcmNumberSectionMapper busiFcmNumberSectionMapper;

    @Resource
    private BusiTerminalMapper busiTerminalMapper;


    @Resource
    private ISysDeptService sysDeptService;

    @Resource
    private IBusiTerminalMeetingJoinSettingsService busiTerminalMeetingJoinSettingsService;

    @Resource
    private List<ITerminalInterceptor> terminalInterceptors;

    @Resource
    private BusiUserTerminalMapper busiUserTerminalMapper;

    @Resource
    private ISysUserService sysUserService;

    @Resource
    private BusiTerminalUpgradeMapper busiTerminalUpgradeMapper;

    @Resource
    private DelayTaskService delayTaskService;
    @Resource
    private ViewConferenceAppointmentMapper viewConferenceAppointmentMapper;
    @Resource
    private ViewTemplateConferenceMapper viewTemplateConferenceMapper;
    @Resource
    private BusiTemplateParticipantMapper busiTemplateParticipantMapper;
    @Resource
    private WvpDeviceService wvpDeviceService;
    /**
     * fsbc账号样板
     */
    private final Pattern numberPattern = Pattern.compile("^[1-9]\\d{3,9}$");

    /**
     * fsbc密码样板
     */
    private final Pattern passwordPattern = Pattern.compile("^\\w{1,16}$");

    /**
     * zj账号样板
     */
    private final Pattern numberPatternZj = Pattern.compile("^[1-9]\\d{3,9}$");



    /**
     * zj密码样板
     */
    private final Pattern passwordPatternZj = Pattern.compile("^\\d{4,6}$");
    /**
     * SMC2-SIP账号样板
     */
    private final Pattern numberPatternSMC2SIP = Pattern.compile("^[1-9]\\d{5,9}$");

    /**
     * SMC2-SIP密码样板
     */
    private final Pattern passwordPatternSMC2SIP = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])(?!.*\\s).{8,}$");


    /**
     * 提取IP
     */
    private static final Pattern IP_PATTERN = Pattern.compile("([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)");
    /**
     * <pre>TODO 请加上该方法的描述</pre>
     *
     * @param businessFieldType
     * @return
     * @author sinhy
     * @see IBusiTerminalService#getDeptRecordCounts(Integer)
     * @since 2021-10-29 12:20
     */
    @Override
    public List<DeptRecordCount> getDeptRecordCounts(Integer businessFieldType) {
        return busiTerminalMapper.getDeptRecordCounts(businessFieldType);
    }

    /**
     * 查询终端信息
     *
     * @param id 终端信息ID
     * @return 终端信息
     */
    @Override
    public BusiTerminal selectBusiTerminalById(Long id) {
        return busiTerminalMapper.selectBusiTerminalById(id);
    }

    /**
     * 查询终端信息列表
     *
     * @param busiTerminal 终端信息
     * @return 终端信息
     */
    @Override
    public PaginationData<ModelBean> selectBusiTerminalList(TerminalSearchVo busiTerminal) {
        Assert.notNull(busiTerminal.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long deptId = busiTerminal.getDeptId() == null ? loginUser.getUser().getDeptId() : busiTerminal.getDeptId();

        List<Long> deptIds = null;
        if (deptId != null) {
            deptIds = new ArrayList<>();
        }

        SysDept dept = new SysDept();
        dept.setDeptId(loginUser.getUser().getDeptId());
        List<SysDept> ds = sysDeptService.selectDeptList(dept);
        for (SysDept sysDept : ds) {
            if (deptIds != null) {
                deptIds.add(sysDept.getDeptId());
            }
        }

        PaginationData<ModelBean> pd = new PaginationData<>();

        busiTerminal.getParams().put("deptIds", deptIds);
        if (StringUtils.isNotEmpty(busiTerminal.getSearchKey())) {
            String searchKey = busiTerminal.getSearchKey();
            if (searchKey.contains("@")) {
                searchKey = searchKey.substring(0, searchKey.indexOf("@"));
            }
            busiTerminal.getParams().put("searchKey", searchKey);
        }

        if (busiTerminal.getPageNum() != null && busiTerminal.getPageSize() != null) {
            PageHelper.startPage(busiTerminal.getPageNum(), busiTerminal.getPageSize());
        }

        List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
        PageInfo<?> pageInfo = new PageInfo<>(ts);

        try {
            McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().getAvailableMcuZjBridgesByDept(deptId).getMasterMcuZjBridge();
            if (mcuZjBridge != null) {
                GetUsrOnlineStatusTask getUsrOnlineStatusTask = new GetUsrOnlineStatusTask("page_" + pageInfo.getPageNum(), 2000, ts, mcuZjBridge);
                delayTaskService.addTask(getUsrOnlineStatusTask);
            }
        } catch (Exception e) {
        }

        Map<String, BusiTerminalUpgrade> busiTerminalUpgradeMap = new HashMap<>();
        if (ts.size() > 0) {
            List<BusiTerminalUpgrade> busiTerminalUpgradeList = busiTerminalUpgradeMapper.selectBusiTerminalUpgradeList(new BusiTerminalUpgrade());
            for (BusiTerminalUpgrade busiTerminalUpgrade : busiTerminalUpgradeList) {
                busiTerminalUpgradeMap.put(busiTerminalUpgrade.getTerminalType(), busiTerminalUpgrade);
            }
        }
        for (BusiTerminal busiTerminalTemp : ts) {
            ModelBean m = new ModelBean(busiTerminalTemp);
            if (busiTerminalTemp.getDeptId() != null) {
                SysDept sysDept = SysDeptCache.getInstance().get(busiTerminalTemp.getDeptId());
                if (sysDept != null) {
                    m.put("deptName", sysDept.getDeptName());
                }
            }
            BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(busiTerminalTemp.getId());
            if (busiUserTerminal != null) {
                m.put("userId", busiUserTerminal.getUserId());
                SysUser sysUser = sysUserService.selectUserById(busiUserTerminal.getUserId());
                if (sysUser != null) {
                    m.put("userName", sysUser.getUserName());
                }
            }
            if (TerminalType.isFSBC(busiTerminalTemp.getType())) {
                FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(busiTerminalTemp.getFsbcServerId());
                if (fsbcBridge != null) {
                    BusiFsbcRegistrationServer busiFsbcRegistrationServer = fsbcBridge.getBusiFsbcRegistrationServer();
                    if (busiFsbcRegistrationServer != null) {
                        String domainName = busiFsbcRegistrationServer.getDomainName();
                        if (StringUtils.isNotEmpty(domainName)) {
                            m.put("ip", domainName);
                        }
                        m.put("uri", busiTerminalTemp.getCredential() + "@" + busiFsbcRegistrationServer.getCallIp());

                    }

                }
            } else if (TerminalType.isFCMSIP(busiTerminalTemp.getType())) {
                FcmBridge fcmBridge = FcmBridgeCache.getInstance().getById(busiTerminalTemp.getFsServerId());
                if (fcmBridge != null) {
                    BusiFreeSwitch busiFreeSwitch = fcmBridge.getBusiFreeSwitch();
                    if (busiFreeSwitch != null) {
                        String domainName = busiFreeSwitch.getDomainName();
                        if (StringUtils.isNotEmpty(domainName)) {
                            m.put("ip", domainName);
                        }
                        m.put("uri", busiTerminalTemp.getCredential() + "@" + busiFreeSwitch.getIp());
                    }
                }
            } else if (TerminalType.isZJ(busiTerminalTemp.getType())) {
                McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().get(busiTerminalTemp.getZjServerId());
                if (mcuZjBridge != null) {
                    BusiMcuZj busiMcuZj = mcuZjBridge.getBusiMcuZj();
                    if (busiMcuZj != null) {
                        String domainName = busiMcuZj.getCucmIp();
                        if (StringUtils.isNotEmpty(domainName)) {
                            m.put("ip", domainName);
                        }
                        m.put("mcuDomain", busiMcuZj.getMcuDomain());
                    }
                }
            } else if (TerminalType.isCisco(busiTerminalTemp.getType())) {
                if (busiTerminalTemp.getNumber() == null) {
                    m.put("uri", busiTerminalTemp.getIp());
                } else {
                    m.put("uri", busiTerminalTemp.getNumber() + "@" + busiTerminalTemp.getIp());
                }
            } else if (TerminalType.isSMCNUMBER(busiTerminalTemp.getType())) {
                m.put("uri", busiTerminalTemp.getNumber());
            } else if (TerminalType.isWindows(busiTerminalTemp.getType())) {
                m.put("uri", busiTerminalTemp.getIp());
            } else if (TerminalType.isSMCSIP(busiTerminalTemp.getType())) {
                Map<String, Object> businessProperties = busiTerminalTemp.getBusinessProperties();
                if (businessProperties != null) {
                    m.put("areaId", businessProperties.get("areaId"));
                    m.put("serviceZoneId", businessProperties.get("serviceZoneId"));
                    m.put("codeId", businessProperties.get("codeId"));
                    m.put("terminalParam", businessProperties.get("terminalParam"));
                    m.put("organizationId", businessProperties.get("organizationId"));
                }
                m.put("uri", busiTerminalTemp.getNumber());
            } else if (TerminalType.isSMCIP(busiTerminalTemp.getType())) {
                Map<String, Object> businessProperties = busiTerminalTemp.getBusinessProperties();
                if (businessProperties != null) {
                    m.put("areaId", businessProperties.get("areaId"));
                    m.put("organizationId", businessProperties.get("organizationId"));
                }
            }

            boolean hasAppUpdate = false;
            if (StringUtils.isNotEmpty(busiTerminalTemp.getAppType())) {
                BusiTerminalUpgrade busiTerminalUpgrade = busiTerminalUpgradeMap.get(busiTerminalTemp.getAppType());
                if (busiTerminalUpgrade != null) {
                    if (StringUtils.isEmpty(busiTerminalTemp.getAppVersionCode()) || busiTerminalUpgrade.getVersionNum().length() > busiTerminalTemp.getAppVersionCode().length() || busiTerminalUpgrade.getVersionNum().compareTo(busiTerminalTemp.getAppVersionCode()) > 0) {
                        m.put("latestVersionCode", busiTerminalUpgrade.getVersionNum());
                        m.put("latestVersionName", busiTerminalUpgrade.getVersionName());
                        m.put("latestVersionTime", busiTerminalUpgrade.getCreateTime());
                        if (busiTerminalTemp.getMqttOnlineStatus() != null && TerminalOnlineStatus.ONLINE.getValue() == busiTerminalTemp.getMqttOnlineStatus()) {
                            hasAppUpdate = true;
                        }
                    }
                }
            }
            m.put("hasAppUpdate", hasAppUpdate);
            pd.addRecord(m);
        }
        pd.setTotal(pageInfo.getTotal());
        pd.setSize(pageInfo.getSize());
        pd.setPage(pageInfo.getPageNum());

        return pd;
    }

    //判断终端账号是否合法
    @Override
    public boolean isFcm(long deptId, String credential) {
        BusiTerminal busiTerminal = new BusiTerminal();
        busiTerminal.setDeptId(deptId);
        BusiFcmNumberSection fcmNumberSection = new BusiFcmNumberSection();
        fcmNumberSection.setDeptId(busiTerminal.getDeptId());

        List<BusiFcmNumberSection> nsl = busiFcmNumberSectionMapper.selectBusiFcmNumberSectionList(fcmNumberSection);
        if (null != nsl && nsl.size() > 0) {
            for (int i = 0; i < nsl.size(); i++) {
                Long bigDecimal = Long.valueOf(credential);
                Long startValue = nsl.get(i).getStartValue();
                Long endValue = nsl.get(i).getEndValue();
                if (startValue <= bigDecimal && bigDecimal <= endValue) {
                    return true;
                }
            }
        } else {
            Long a = FcmAccountCacheAndUtils.getInstance().deptId(busiTerminal.getDeptId());
            if (a == 0) {
                Assert.isTrue(false, "该部门未分配终端号段,请联系管理员分配！");
            } else {
                return this.isFcm(a, credential);
            }
        }
        return false;
    }

    @Override
    public BusiTerminal selectBusiTerminal(BusiTerminal busiTerminal) {
        List<BusiTerminal> busiTerminals = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
        if (CollectionUtils.isEmpty(busiTerminals)) {
            return null;
        }
        return busiTerminals.get(0);
    }

    /**
     * 新增终端信息
     *
     * @param busiTerminals 终端信息
     * @return 结果
     */
    @Override
    public int insertBusiTerminals(List<BusiTerminal> busiTerminals) {
        if (null != busiTerminals && busiTerminals.size() > 0) {
            for (BusiTerminal busiTerminal : busiTerminals) {
                insertBusiTerminal(busiTerminal);
            }
        }
        return 0;
    }

    /**
     * 新增终端信息
     *
     * @param busiTerminal 终端信息
     * @return 结果
     */
    @Override
    public int insertBusiTerminal(BusiTerminal busiTerminal) {
        return insertBusiTerminal(busiTerminal, false);
    }

    /**
     * 新增终端信息
     *
     * @param busiTerminal 终端信息
     * @param isAutoAdd    是否是后端自动添加终端
     * @return 结果
     */
    @Override
    public int insertBusiTerminal(BusiTerminal busiTerminal, boolean isAutoAdd) {
        if (busiTerminal.getDeptId() == null) {
            throw new SystemException(1000102, "请选择部门");
        }

        if (!TerminalType.isGB28181(busiTerminal.getType()) && !TerminalType.isFSBC(busiTerminal.getType()) && !TerminalType.isFCMSIP(busiTerminal.getType()) && !TerminalType.isZJ(busiTerminal.getType()) && !TerminalType.isSMCSIP(busiTerminal.getType()) && !TerminalType.isSMC2SIP(busiTerminal.getType()) && !TerminalType.isSMCNUMBER(busiTerminal.getType()) && !TerminalType.isRtsp(busiTerminal.getType())
                && !RegExpUtils.isIP(busiTerminal.getIp())) {
            throw new SystemException(1000103, "IP格式不正确");
        }
        if(TerminalType.isRtsp(busiTerminal.getType())){

            String protocol = busiTerminal.getProtocol();
            if(Strings.isBlank(protocol)){
                throw new CustomException("rtsp协议不能为空");
            }
            Matcher matcher = IP_PATTERN.matcher(protocol);
            // 查找匹配结果
            if (matcher.find()) {
                busiTerminal.setIp(matcher.group(1));
            } else {
                throw new CustomException("rtsp协议错误");
            }
        }

        if (TerminalType.isIp(busiTerminal.getType())) {
            if (StringUtils.isNotEmpty(busiTerminal.getIp()) && StringUtils.isEmpty(busiTerminal.getNumber())) {
                TerminalSearchVo terminalSearchVo = new TerminalSearchVo();
                terminalSearchVo.setIp(busiTerminal.getIp());
                List<BusiTerminal> busiTerminalList = busiTerminalMapper.selectBusiTerminalList(terminalSearchVo);
                if (busiTerminalList.size() > 0) {
                    String region = ExternalConfigCache.getInstance().getRegion();
                    if(!Objects.equals(SCSCZT,region)){
                        throw new SystemException(1000103, "号码为空时IP不能重复！");
                    }
                }
            }
        }

        Assert.notNull(busiTerminal.getBusinessFieldType(), "终端业务领域类型businessFieldType不能为空！");
        Assert.notNull(busiTerminal.getAttendType(), "终端入会类型不能为空！");
        AttendType.convert(busiTerminal.getAttendType());
        Assert.notNull(busiTerminal.getName(), "终端名不能为空！");
        try {
            int length = busiTerminal.getName().getBytes("utf-8").length;
            if (length > 50) {
                Assert.isTrue(false, "终端名超过50字节（16个中文）！");
            }
        } catch (UnsupportedEncodingException e) {
            Assert.isTrue(false, "终端名错误！");
        }

        if (StringUtils.isNotEmpty(busiTerminal.getMac())) {
            busiTerminal.setMac(busiTerminal.getMac().toLowerCase());
        }
        if (StringUtils.isNotEmpty(busiTerminal.getSn())) {
            busiTerminal.setSn(this.delSpace(busiTerminal.getSn()).toLowerCase());
        }

        LoginUser loginUser = SecurityUtils.getLoginUser();
        busiTerminal.setCreateUserId(loginUser.getUser().getUserId());
        busiTerminal.setCreateUserName(loginUser.getUser().getUserName());

        busiTerminal.setCreateTime(new Date());
        busiTerminal.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
        busiTerminal.setMqttOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
        if (busiTerminal.getNumber() != null && busiTerminal.getNumber().trim().length() == 0) {
            busiTerminal.setNumber(null);
        }
        if (busiTerminal.getSn() != null && busiTerminal.getSn().trim().length() == 0) {
            busiTerminal.setSn(null);
        }
        if (TerminalType.isFSBC(busiTerminal.getType())) {
            if (StringUtils.isEmpty(busiTerminal.getPassword())) {
                busiTerminal.setPassword(String.valueOf(123456));
            }
            if (!isAutoAdd) {
                Assert.isTrue(!SipAccountUtil.isAutoAccount(busiTerminal.getCredential()), "该账号号段已被占用！");
                Assert.isTrue(!SipAccountUtil.isZjAutoAccount(busiTerminal.getCredential()), "该账号号段已被占用！");
                Assert.isTrue(!SipAccountUtil.isZjReservedAccount(busiTerminal.getCredential()), "该账号号段已被占用！");
            }
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()) && numberPattern.matcher(String.valueOf(busiTerminal.getCredential())).matches(), "FSBC-SIP账号必须为4-10位数字组成！");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()) && passwordPattern.matcher(String.valueOf(busiTerminal.getPassword())).matches(), "FSBC密码必须为1-16位字母、数字和下划线组成！");
            BusiFsbcServerDept fsd = DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId());
            Assert.notNull(fsd, "很抱歉，【" + SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName() + "】当前未绑定FSBC服务器，请联系管理员配置您的FSBC服务器！");

            BusiTerminal con = new BusiTerminal();
            con.setFsbcServerId(fsd.getFsbcServerId());
            con.setCredential(busiTerminal.getCredential());
            List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalList(con);
            Assert.isTrue(ObjectUtils.isEmpty(ts), "该账号已存在，请勿重复添加");

            FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(fsd.getFsbcServerId());

            // 创建
            RestResponse restResponse = fsbcBridge.getCredentialInvoker().create(FsbcCredential.newCredential().name(busiTerminal.getCredential()).password(busiTerminal.getPassword()));
            Assert.isTrue(restResponse.isSuccess() || restResponse.getMessage().equals("Credential name already exists"), "新增终端FSBC账号失败");
            busiTerminal.setFsbcServerId(fsd.getFsbcServerId());
            busiTerminal.setExpiredDate(null);
            busiTerminal.setNumber(null);
        }
        // FCM 类型
        else if (TerminalType.isFCMSIP(busiTerminal.getType())) {
            if (StringUtils.isEmpty(busiTerminal.getPassword())) {
                busiTerminal.setPassword(String.valueOf(123456));
            }
            if (!isAutoAdd) {
                Assert.isTrue(!SipAccountUtil.isAutoAccount(busiTerminal.getCredential()), "该账号号段已被占用！");
                Assert.isTrue(!SipAccountUtil.isZjAutoAccount(busiTerminal.getCredential()), "该账号号段已被占用！");
                Assert.isTrue(!SipAccountUtil.isZjReservedAccount(busiTerminal.getCredential()), "该账号号段已被占用！");
            }
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()) && numberPattern.matcher(String.valueOf(busiTerminal.getCredential())).matches(), "FCM-SIP账号必须为4-10位数字组成！");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()) && passwordPattern.matcher(String.valueOf(busiTerminal.getPassword())).matches(), "FCM-SIP账户密码必须为1-16位字母、数字和下划线组成！");
            BusiFreeSwitchDept fsd = DeptFcmMappingCache.getInstance().get(busiTerminal.getDeptId());
            Assert.notNull(fsd, "很抱歉，【" + SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName() + "】当前未绑定FCM服务器，请联系管理员配置您的FCM服务器！");

            BusiTerminal con = new BusiTerminal();
            con.setType(TerminalType.FCM_SIP.getId());
            con.setCredential(busiTerminal.getCredential());
            List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalList(con);
            Assert.isTrue(ObjectUtils.isEmpty(ts), "该账号已存在，请勿重复添加");
            if (!isAutoAdd) {
                Assert.isTrue(isFcm(busiTerminal.getDeptId(), busiTerminal.getCredential()), "该账号不在号段范围内或该部门未分配号段！");
            }

            if (FcmType.CLUSTER == FcmType.convert(fsd.getFcmType())) {
                FcmBridgeCluster fcmBridgeCluster = FcmBridgeCache.getInstance().getByFcmClusterId(fsd.getServerId());
                if (fcmBridgeCluster != null) {
                    List<FcmBridge> fcmBridges = fcmBridgeCluster.getFcmBridges();
                    // 由于使用固定用户信息数据库，任意一个FCM即可
                    FcmBridge fcmBridge = fcmBridges.get(0);
                    int size = fcmBridges.size();
                    if (size > 0) {
                        Random random = new Random();
                        int i = random.nextInt(size);
                        fcmBridge = fcmBridges.get(i);
                    }
                    Assert.isTrue(fcmBridge.addFreeSwitchUser(busiTerminal.getCredential(), busiTerminal.getPassword()) == FcmConfigConstant.SUCCESS, "添加FCM-SIP账号失败");
                    busiTerminal.setFsServerId(fcmBridge.getBusiFreeSwitch().getId());// 集群插入时随机插入一个FCM的id
                    busiTerminal.setIp(fcmBridge.getBusiFreeSwitch().getIp());
                } else {
                    Assert.isTrue(0 == FcmConfigConstant.SUCCESS, "添加FCM-SIP账号失败");
                }
            } else {
                FcmBridge fcmBridge = FcmBridgeCache.getInstance().getById(fsd.getServerId());
                Assert.isTrue(fcmBridge.addFreeSwitchUser(busiTerminal.getCredential(), busiTerminal.getPassword()) == FcmConfigConstant.SUCCESS, "添加FCM-SIP账号失败");
                busiTerminal.setFsServerId(fsd.getServerId());
                busiTerminal.setIp(fcmBridge.getBusiFreeSwitch().getIp());
            }
            busiTerminal.setExpiredDate(null);
            busiTerminal.setNumber(null);
        }
        // ZJ 类型
        else if (TerminalType.isZJ(busiTerminal.getType())) {
            if (!isAutoAdd) {
                Assert.isTrue(!SipAccountUtil.isAutoAccount(busiTerminal.getCredential()), "该账号号段已被占用！");
                Assert.isTrue(!SipAccountUtil.isZjAutoAccount(busiTerminal.getCredential()), "该账号号段已被占用！");
                Assert.isTrue(!SipAccountUtil.isZjReservedAccount(busiTerminal.getCredential()), "该账号号段已被占用！");
                Assert.isTrue(ZjAccountCache.getInstance().isZjAccount(busiTerminal.getDeptId(), busiTerminal.getCredential()), "该账号不在号段范围内或该部门未分配号段！");
            }

            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()) && numberPatternZj.matcher(String.valueOf(busiTerminal.getCredential())).matches(), "ZJ账号必须为4-10位数字组成！");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()) && passwordPatternZj.matcher(String.valueOf(busiTerminal.getPassword())).matches(), "ZJ账户密码必须为4-6位数字！");
            List<McuZjBridge> mcuZjBridgeList = McuZjBridgeCache.getInstance().getMcuZjBridgesByDept(busiTerminal.getDeptId());
            McuZjBridge mcuZjBridge = null;
            if (mcuZjBridgeList != null && mcuZjBridgeList.size() > 0) {
                mcuZjBridge = mcuZjBridgeList.get(0);
            }
            Assert.notNull(mcuZjBridge, "很抱歉，【" + SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName() + "】当前未绑定ZJ服务器，请联系管理员配置您的ZJ服务器！");

            {
                BusiTerminal con = new BusiTerminal();
                con.setType(TerminalType.ZJ_H323.getId());
                con.setCredential(busiTerminal.getCredential());
                List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalList(con);
                Assert.isTrue(ObjectUtils.isEmpty(ts), "该账号已存在，请勿重复添加");
            }
            {
                BusiTerminal con = new BusiTerminal();
                con.setType(TerminalType.ZJ_SIP.getId());
                con.setCredential(busiTerminal.getCredential());
                List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalList(con);
                Assert.isTrue(ObjectUtils.isEmpty(ts), "该账号已存在，请勿重复添加");
            }
            try {
                int length = 8 - mcuZjBridge.getTenantId().length();
                Integer newTerminalNum = busiTerminalMapper.getAvailableTerminalNumForZj();
                if (newTerminalNum == null) {
                    newTerminalNum = 51;
                }
                int max = (int) Math.pow(10, length);
                if (newTerminalNum >= max) {
                    throw new SystemException("MCU资源耗尽。ZJ终端数超过" + max);//ZJ终端50-9999
                }
                String terminalNumStr = StringUtils.leftPad(newTerminalNum.toString(), length, "0");
                String userMark = terminalNumStr;
                String nickName = busiTerminal.getName();
                CmAddUsrRequest cmAddUsrRequest = CmAddUsrRequest.buildDefaultRequestForAddEps();
                cmAddUsrRequest.setNick_name(nickName);
                cmAddUsrRequest.setLogin_id(busiTerminal.getCredential());
                cmAddUsrRequest.setLogin_pwd(busiTerminal.getPassword());
                cmAddUsrRequest.setUsr_mark(userMark);
                List<Integer> belongToDepartments = new ArrayList<>();
                belongToDepartments.add(mcuZjBridge.getTopDepartmentId());// 总部
                cmAddUsrRequest.setBelong_to_departments(belongToDepartments);
                if (TerminalType.ZJ_H323.getId() == busiTerminal.getType()) {
                    cmAddUsrRequest.setPtotocol_type(1);//  1 H323 协议， 2 SIP 协议， 3 多流协议， 5：RTSP 协议， 默认为多流协议
                } else {
                    cmAddUsrRequest.setPtotocol_type(2);//  1 H323 协议， 2 SIP 协议， 3 多流协议， 5：RTSP 协议， 默认为多流协议
                }
                cmAddUsrRequest.setOption("endpoint");
                cmAddUsrRequest.setIs_endpoint(1);
                CmAddUsrResponse cmAddUsrResponse = mcuZjBridge.getConferenceManageApi().addUsr(cmAddUsrRequest);
                if (cmAddUsrResponse != null && cmAddUsrResponse.getResult().contains("success")) {
                    busiTerminal.setTerminalNum(newTerminalNum);
                    busiTerminal.setZjServerId(mcuZjBridge.getBusiMcuZj().getId());
                    busiTerminal.setZjUserId(Long.valueOf(cmAddUsrResponse.getUsr_id()));//存储zj的用户id
                    busiTerminal.setIp(mcuZjBridge.getBusiMcuZj().getIp());
                    busiTerminal.setNumber(null);
                } else {
                    throw new SystemException("添加ZJ账号失败！");
                }
            } catch (Exception e) {
                if (e instanceof SystemException) {
                    throw e;
                } else {
                    throw new SystemException("添加ZJ账号失败！");
                }
            }
        }
        // SMC2-SIP 类型
        else if (TerminalType.isSMC2SIP(busiTerminal.getType())) {
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()) && numberPatternSMC2SIP.matcher(String.valueOf(busiTerminal.getCredential())).matches(), "SMC2SIP账号必须为5-10位数字组成！");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()) && passwordPatternSMC2SIP.matcher(String.valueOf(busiTerminal.getPassword())).matches(), "SMC2SIP账户密码必须为8位，大小写字母加符号加数字的组合！");
            Smc2Bridge smc2Bridge = Smc2BridgeCache.getInstance().getAvailableBridgesByDept(busiTerminal.getDeptId().intValue());

            Assert.notNull(smc2Bridge, "很抱歉，【" + SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName() + "】当前未绑定SMC2服务器，请联系管理员配置您的SMC2服务器！");

            {
                BusiTerminal con = new BusiTerminal();
                con.setType(TerminalType.SMC2_SIP.getId());
                con.setCredential(busiTerminal.getCredential());
                List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalList(con);
                Assert.isTrue(ObjectUtils.isEmpty(ts), "该账号已存在，请勿重复添加");
            }
            try {
                //设置准备将会场添加到的组织节点ID
                String orgId = "1";
                TerminalInfoEx siteInfo = new TerminalInfoEx();
                siteInfo.setName(busiTerminal.getName());
                siteInfo.setUri(busiTerminal.getCredential());
                siteInfo.setType(7);
                siteInfo.setRate("1920k");
                //新建一个List对象，用于存放终端支持的视频能力参数列表
                List<VideoCapbilityItemEx> videoCapbility = new ArrayList<VideoCapbilityItemEx>();
                {
                    //新建一个VideoCapbilityItemEx对象
                    VideoCapbilityItemEx videoCapbilityItemEx = new VideoCapbilityItemEx();
                    //设置视频协议为H.261协议
                    videoCapbilityItemEx.setVideoProtocol(2);
                    //新建一个List对象，用于存放视频协议对应的视频格式列表
                    List<Integer> videoFormat = new ArrayList<Integer>();
                    //添加视频格式4CIF
                    videoFormat.add(1);
                    videoFormat.add(8);
                    videoCapbilityItemEx.setVideoFormat(videoFormat);
                    videoCapbility.add(videoCapbilityItemEx);
                }
                {
                    //新建一个VideoCapbilityItemEx对象
                    VideoCapbilityItemEx videoCapbilityItemEx = new VideoCapbilityItemEx();
                    //设置视频协议为H.261协议
                    videoCapbilityItemEx.setVideoProtocol(3);
                    //新建一个List对象，用于存放视频协议对应的视频格式列表
                    List<Integer> videoFormat = new ArrayList<Integer>();
                    //添加视频格式4CIF
                    videoFormat.add(1);
                    videoFormat.add(2);
                    videoFormat.add(3);
                    videoFormat.add(14);
                    videoFormat.add(5);
                    videoFormat.add(6);
                    videoFormat.add(8);
                    videoCapbilityItemEx.setVideoFormat(videoFormat);
                    videoCapbility.add(videoCapbilityItemEx);
                }
                {
                    //新建一个VideoCapbilityItemEx对象
                    VideoCapbilityItemEx videoCapbilityItemEx = new VideoCapbilityItemEx();
                    //设置视频协议为H.261协议
                    videoCapbilityItemEx.setVideoProtocol(4);
                    //新建一个List对象，用于存放视频协议对应的视频格式列表
                    List<Integer> videoFormat = new ArrayList<Integer>();
                    //添加视频格式4CIF
                    videoFormat.add(1);
                    videoFormat.add(2);
                    videoFormat.add(3);
                    videoFormat.add(14);
                    videoFormat.add(5);
                    videoFormat.add(6);
                    videoFormat.add(8);
                    videoCapbilityItemEx.setVideoFormat(videoFormat);
                    videoCapbility.add(videoCapbilityItemEx);
                }
                siteInfo.setVideoCapbility(videoCapbility);
                //设置用户名esdk_tp
                siteInfo.setRegUser(busiTerminal.getName());
                //设置密码.
                siteInfo.setRegPassword(busiTerminal.getPassword());

                SiteServiceEx siteServiceEx = ServiceFactoryEx.getService(SiteServiceEx.class);
                Integer integer = siteServiceEx.addSiteInfoEx(orgId, siteInfo);
                if(integer!=0){
                    throw new CustomException("添加smc2sip账号失败！");
                }

            } catch (Exception e) {
                if (e instanceof SystemException) {
                    throw e;
                } else {
                    throw new SystemException("添加smc2sip账号失败！");
                }
            }
        }
        else if (TerminalType.isZTE(busiTerminal.getType())) {
         
            List<McuZteBridge> mcuZteBridgeList = McuZteBridgeCache.getInstance().getMcuZteBridgesByDept(busiTerminal.getDeptId());
            McuZteBridge mcuZteBridge = null;
            if (mcuZteBridgeList != null && mcuZteBridgeList.size() > 0) {
                mcuZteBridge = mcuZteBridgeList.get(0);
            }
            Assert.notNull(mcuZteBridge, "很抱歉，【" + SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName() + "】当前未绑定Zte服务器，请联系管理员配置您的Zte服务器！");

            {
                BusiTerminal con = new BusiTerminal();
                con.setType(TerminalType.ZTE.getId());
                con.setCredential(busiTerminal.getCredential());
                List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalList(con);
                Assert.isTrue(ObjectUtils.isEmpty(ts), "该账号已存在，请勿重复添加");
            }

            QueryAddressBookV2Request queryAddressBookRequest=new QueryAddressBookV2Request();
            queryAddressBookRequest.setAccount(mcuZteBridge.getBusiMcuZte().getUsername());
            queryAddressBookRequest.setOption(2);
            queryAddressBookRequest.setTerName(busiTerminal.getName());
            queryAddressBookRequest.setPage(1);
            queryAddressBookRequest.setNumPerPage(10);

            QueryAddressBookV2Response ccTerminalInfo = mcuZteBridge.getConferenceManageApi().getCcTerminalInfo(queryAddressBookRequest);
            if(ccTerminalInfo!=null){
                TerminalSimpleInfoV2[] terminalInfoV2 = ccTerminalInfo.getTerminalInfoV2();
                if(terminalInfoV2.length>0){
                    Assert.isTrue(false, "该名称已存在，请勿重复添加");
                }
            }

            try {

                String nickName = busiTerminal.getName();
                AddAddressBookRequest cmAddUsrRequest = new AddAddressBookRequest();
                TerminalSimpleInfo terminalInfo=new TerminalSimpleInfo();
                terminalInfo.setTerminalName(nickName);
                terminalInfo.setTerminalNumber(busiTerminal.getCredential());
                terminalInfo.setPassword(busiTerminal.getPassword());
                terminalInfo.setTerType(busiTerminal.getZteTerminalType());
                terminalInfo.setIpAddress(busiTerminal.getIp());
                terminalInfo.setCallMode(busiTerminal.getCallmodel());

                cmAddUsrRequest.setTerminalInfo(terminalInfo);
                cmAddUsrRequest.setAccount(mcuZteBridge.getBusiMcuZte().getUsername());

                AddAddressBookResponse cmAddUsrResponse = mcuZteBridge.getConferenceManageApi().addMrTerminal(cmAddUsrRequest);
                if (cmAddUsrResponse != null && cmAddUsrResponse.getResult().contains("200")) {
                    busiTerminal.setZteServerId(mcuZteBridge.getBusiMcuZte().getId());
                    busiTerminal.setZteTerminalId(cmAddUsrResponse.getTerminalId());
                } else {
                    throw new SystemException("添加Zte账号失败！");
                }
            } catch (Exception e) {
                if (e instanceof SystemException) {
                    throw e;
                } else {
                    throw new SystemException("添加Zte账号失败！");
                }
            }
        } else if (TerminalType.isHwCloud(busiTerminal.getType())) {
            // hwCloud
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()), "账号不能为空！");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()), "密码不能为空！");
            BusiMcuHwcloudDept busiMcuHwcloudDept = DeptHwcloudMappingCache.getInstance().get(busiTerminal.getDeptId());
            Assert.notNull(busiMcuHwcloudDept, "很抱歉，【" + SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName() + "】当前未绑定华为云服务器，请联系管理员配置您的华为云服务器！");

            BusiTerminal con = new BusiTerminal();
            con.setType(TerminalType.HW_CLOUD.getId());
            con.setCredential(busiTerminal.getCredential());
            List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalList(con);
            String terminalName = "";
            if (ts != null && ts.size() > 0) {
                terminalName = ts.get(0).getName();
            }
            Assert.isTrue(ObjectUtils.isEmpty(ts), "该账号已存在，请勿重复添加！（终端名：" + terminalName + "）");

            busiTerminal.setExpiredDate(null);
            busiTerminal.setNumber(null);
        }
        //GB-28181
        else if(TerminalType.isGB28181(busiTerminal.getType())){
            WvpAddDeviceRequest wvpAddDeviceRequest = new WvpAddDeviceRequest();
            wvpAddDeviceRequest.setName(busiTerminal.getName());
            wvpAddDeviceRequest.setPassword(busiTerminal.getPassword());
            if(Strings.isBlank(busiTerminal.getPassword())){
                wvpAddDeviceRequest.setPassword("123456");
            }
            wvpAddDeviceRequest.setDeviceId(busiTerminal.getNumber());
            wvpDeviceService.addDevice(wvpAddDeviceRequest);
            WvpBridge wvpBridge = WvpBridgeCache.getInstance().get();
            busiTerminal.setIp(wvpBridge.getIp());
        }

        int c = 0;
        if (!TerminalType.isZJ(busiTerminal.getType()) && !TerminalType.isFCMSIP(busiTerminal.getType()) && !TerminalType.isFSBC(busiTerminal.getType()) && !TerminalType.isSMCSIP(busiTerminal.getType())) {
            if (StringUtils.isNotEmpty(busiTerminal.getIp()) && StringUtils.isNotEmpty(busiTerminal.getNumber())) {
                BusiTerminal busiTerminal1 = new BusiTerminal();
                busiTerminal1.setIp(busiTerminal.getIp());
                busiTerminal1.setNumber(busiTerminal.getNumber());
                List<BusiTerminal> busiTerminalList1 = busiTerminalMapper.selectBusiTerminalList(busiTerminal1);
                if (busiTerminalList1 != null && busiTerminalList1.size() > 0) {
                    Long deptId = busiTerminalList1.get(0).getDeptId();
                    SysDept sysDept = SysDeptCache.getInstance().get(deptId);
                    throw new SystemException("在【" + sysDept.getDeptName() + "】部门下已存在此IP和号码，不能重复添加！");
                }
            }
        }
        try {
            c = busiTerminalMapper.insertBusiTerminal(busiTerminal);
            if (c > 0) {

                if(TerminalType.isSMCSIP(busiTerminal.getType())){
                    HuaweiBarSnTask huaweiBarSnTask = new HuaweiBarSnTask("会议ID" + busiTerminal.getId(), 10, busiTerminal.getIp(), busiTerminal.getTerminalUsername(), busiTerminal.getTerminalPassword(), busiTerminal.getSn(), busiTerminal);
                    BeanFactory.getBean(TaskService.class).addTask(huaweiBarSnTask);
                }
                TerminalCache.getInstance().put(busiTerminal.getId(), busiTerminal);
                for (ITerminalInterceptor iTerminalInterceptor : terminalInterceptors) {
                    try {
                        iTerminalInterceptor.terminalInserted(busiTerminal);
                    } catch (Exception e) {
                        LoggerFactory.getLogger(getClass()).error("拦截处理出错", e);
                    }
                }
            }
        } catch (Exception e) {
            throw new SystemException("该MAC已存在，请勿重复添加", e);
        }
        return c;
    }

    @Override
    public String delSpace(String sn) {
        if (StringUtils.isNotEmpty(sn)) {
            return sn.replaceAll(" ", "");
        }
        return sn;
    }

    /**
     * 修改终端信息
     *
     * @param busiTerminal 终端信息
     * @return 结果
     */
    @Override
    public int updateBusiTerminal(BusiTerminal busiTerminal) {
        busiTerminal.setUpdateTime(new Date());

        if (busiTerminal.getDeptId() == null) {
            throw new SystemException(1000102, "请选择部门");
        }

        if (!TerminalType.isGB28181(busiTerminal.getType())&&!TerminalType.isFSBC(busiTerminal.getType()) && !TerminalType.isFCMSIP(busiTerminal.getType()) && !TerminalType.isZJ(busiTerminal.getType()) && !TerminalType.isSMCSIP(busiTerminal.getType()) && !TerminalType.isSMC2SIP(busiTerminal.getType()) && !TerminalType.isSMCNUMBER(busiTerminal.getType()) &&!TerminalType.isRtsp(busiTerminal.getType())&& !RegExpUtils.isIP(busiTerminal.getIp())) {
            throw new SystemException(1000103, "IP格式不正确");
        }
        if(TerminalType.isRtsp(busiTerminal.getType())){

            String protocol = busiTerminal.getProtocol();
            if(Strings.isBlank(protocol)){
                throw new CustomException("rtsp协议不能为空");
            }
            Matcher matcher = IP_PATTERN.matcher(protocol);
            // 查找匹配结果
            if (matcher.find()) {
                busiTerminal.setIp(matcher.group(1));
            } else {
                throw new CustomException("rtsp协议错误");
            }
        }

        if (TerminalType.isIp(busiTerminal.getType())) {
            if (StringUtils.isNotEmpty(busiTerminal.getIp()) && StringUtils.isEmpty(busiTerminal.getNumber())) {
                TerminalSearchVo terminalSearchVo = new TerminalSearchVo();
                terminalSearchVo.setIp(busiTerminal.getIp());
                List<BusiTerminal> busiTerminalList = busiTerminalMapper.selectBusiTerminalList(terminalSearchVo);
                if (busiTerminalList  != null && busiTerminalList.size() > 0) {
                    for (BusiTerminal terminal : busiTerminalList) {
                        if (!Objects.equals(terminal.getId(),busiTerminal.getId())) {
                            String region = ExternalConfigCache.getInstance().getRegion();
                            if(!Objects.equals(SCSCZT,region)){
                                throw new SystemException(1000103, "号码为空时IP不能重复！");
                            }
                        }
                    }
                }
            }
        }

        Assert.notNull(busiTerminal.getBusinessFieldType(), "终端业务领域类型businessFieldType不能为空！");
        Assert.notNull(busiTerminal.getAttendType(), "终端入会类型不能为空！");
        AttendType.convert(busiTerminal.getAttendType());
        Assert.notNull(busiTerminal.getName(), "终端名不能为空！");
        try {
            int length = busiTerminal.getName().getBytes("utf-8").length;
            if (length > 50) {
                Assert.isTrue(false, "终端名超过50字节（16个中文）！");
            }
        } catch (UnsupportedEncodingException e) {
            Assert.isTrue(false, "终端名错误！");
        }

        BusiTerminal ot = busiTerminalMapper.selectBusiTerminalById(busiTerminal.getId());
        if ((TerminalType.isFSBC(ot.getType()) && !TerminalType.isFSBC(busiTerminal.getType())) || (TerminalType.isFSIP(ot.getType()) && !TerminalType.isFSIP(busiTerminal.getType())) || (TerminalType.isZJ(ot.getType()) && !TerminalType.isZJ(busiTerminal.getType()))) {
            throw new SystemException(1009894, "该终端不能修改类型，若是要修改，请删除后再新增！");
        }
        if (busiTerminal.getNumber() != null && busiTerminal.getNumber().trim().length() == 0) {
            busiTerminal.setNumber(null);
        }
        if (busiTerminal.getSn() != null && busiTerminal.getSn().trim().length() == 0) {
            busiTerminal.setSn(null);
        }

        if (TerminalType.isFSBC(busiTerminal.getType())) {
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()), "FSBC账号不能为空");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()), "FSBC密码不能为空");
            Assert.isTrue(ot.getDeptId().equals(busiTerminal.getDeptId()), "FSBC终端账号不支持切换部门，请删除重建");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()) && numberPattern.matcher(String.valueOf(busiTerminal.getCredential())).matches(), "FSBC-SIP账号必须为4-10位数字组成！");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()) && passwordPattern.matcher(String.valueOf(busiTerminal.getPassword())).matches(), "FSBC密码必须为1-16位字母、数字和下划线组成！");

            busiTerminal.setIp(null);
            if (!(ot.getCredential().equals(busiTerminal.getCredential()) && ot.getPassword().equals(busiTerminal.getPassword()))) {
                BusiFsbcServerDept fsd = DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId());
                Assert.notNull(fsd, "很抱歉，【" + SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName() + "】当前租户未绑定FSBC服务器，请联系管理员配置您的FSBC服务器！");

                BusiTerminal con = new BusiTerminal();
                con.setFsbcServerId(fsd.getFsbcServerId());
                con.setCredential(busiTerminal.getCredential());
                List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalList(con);
                Assert.isTrue(!ObjectUtils.isEmpty(ts), "该账号已不存在，请删了重新添加");

                FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(fsd.getFsbcServerId());
                RestResponse restResponse = fsbcBridge.getCredentialInvoker().update(FsbcCredential.newCredential().name(ot.getCredential()).newName(busiTerminal.getCredential()).password(busiTerminal.getPassword()));
                Assert.isTrue(restResponse.isSuccess() || restResponse.getMessage().equals("NewName already exist, choose another NewName"), "FSBC账号已存在，请选择其它名字！");
                busiTerminal.setFsbcServerId(fsd.getFsbcServerId());
                busiTerminal.setIp(fsbcBridge.getBusiFsbcRegistrationServer().getCallIp());
            }
        }
        // FCM 类型
        else if (TerminalType.isFCMSIP(busiTerminal.getType())) {
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()), "FCM-SIP账号不能为空");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()), "FCM-SIP密码不能为空");
            Assert.isTrue(ot.getDeptId().equals(busiTerminal.getDeptId()), "FCM-SIP终端账号不支持切换部门，请删除重建");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()) && numberPattern.matcher(String.valueOf(busiTerminal.getCredential())).matches(), "FCM-SIP账号必须为4-10位数字组成！");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()) && passwordPattern.matcher(String.valueOf(busiTerminal.getPassword())).matches(), "FCM-SIP密码必须为1-16位字母、数字和下划线组成！");

            BusiFreeSwitchDept fsd = DeptFcmMappingCache.getInstance().get(busiTerminal.getDeptId());
            Assert.notNull(fsd, "很抱歉，【" + SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName() + "】当前未绑定FCM服务器，请联系管理员配置您的FCM服务器！");

            busiTerminal.setIp(null);
            if (FcmType.CLUSTER == FcmType.convert(fsd.getFcmType())) {
                FcmBridgeCluster fcmBridgeCluster = FcmBridgeCache.getInstance().getByFcmClusterId(fsd.getServerId());
                if (fcmBridgeCluster != null) {
                    List<FcmBridge> fcmBridges = fcmBridgeCluster.getFcmBridges();
                    // 由于使用固定用户信息数据库，任意一个FCM即可
                    FcmBridge fcmBridge = fcmBridges.get(0);
                    int size = fcmBridges.size();
                    if (size > 0) {
                        Random random = new Random();
                        int i = random.nextInt(size);
                        fcmBridge = fcmBridges.get(i);
                    }
                    Assert.isTrue(fcmBridge.updateFreeSwitchUser(busiTerminal.getCredential(), busiTerminal.getPassword()) == FcmConfigConstant.SUCCESS, "更新FCM-SIP账号失败");
//                    busiTerminal.setFsServerId(fsd.getServerId());// 集群时不插入
                } else {
                    Assert.isTrue(0 == FcmConfigConstant.SUCCESS, "更新FCM-SIP账号失败");
                }
            } else {
                FcmBridge fcmBridge = FcmBridgeCache.getInstance().getById(fsd.getServerId());
                Assert.isTrue(fcmBridge.updateFreeSwitchUser(busiTerminal.getCredential(), busiTerminal.getPassword()) == FcmConfigConstant.SUCCESS, "更新FCM-SIP账号失败");
                busiTerminal.setFsServerId(fsd.getServerId());
                busiTerminal.setIp(fcmBridge.getBusiFreeSwitch().getIp());
            }
        }
        // ZJ 类型
        else if (TerminalType.isZJ(busiTerminal.getType())) {
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()), "ZJ账号不能为空");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()), "ZJ密码不能为空");
            Assert.isTrue(ot.getDeptId().equals(busiTerminal.getDeptId()), "ZJ终端账号不支持切换部门，请删除重建");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()) && numberPatternZj.matcher(String.valueOf(busiTerminal.getCredential())).matches(), "ZJ账号必须为4-10位数字组成！");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()) && passwordPatternZj.matcher(String.valueOf(busiTerminal.getPassword())).matches(), "ZJ账户密码必须为4-6位数字！");
            List<McuZjBridge> mcuZjBridgeList = McuZjBridgeCache.getInstance().getMcuZjBridgesByDept(busiTerminal.getDeptId());
            McuZjBridge mcuZjBridge = null;
            if (mcuZjBridgeList != null && mcuZjBridgeList.size() > 0) {
                mcuZjBridge = mcuZjBridgeList.get(0);
            }
            Assert.notNull(mcuZjBridge, "很抱歉，【" + SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName() + "】当前未绑定ZJ服务器，请联系管理员配置您的ZJ服务器！");

            busiTerminal.setFsServerId(mcuZjBridge.getBusiMcuZj().getId());
            busiTerminal.setIp(mcuZjBridge.getBusiMcuZj().getIp());

            BusiTerminal busiTerminalExist = TerminalCache.getInstance().get(busiTerminal.getId());
            int length = 8 - mcuZjBridge.getTenantId().length();
            String terminalNum = StringUtils.leftPad(busiTerminalExist.getTerminalNum().toString(), length, "0");
            CmSearchUsrRequest cmSearchUsrRequest = new CmSearchUsrRequest();
            String[] filterType = new String[1];
            filterType[0] = "usr_mark";
            Object[] filterValue = new Object[1];
            filterValue[0] = terminalNum;
            cmSearchUsrRequest.setFilter_type(filterType);
            cmSearchUsrRequest.setFilter_value(filterValue);
            CmSearchUsrResponse cmSearchUsrResponse = mcuZjBridge.getConferenceManageApi().searchUsr(cmSearchUsrRequest);
            if (cmSearchUsrResponse != null && cmSearchUsrResponse.getUsr_ids().length > 0) {
                // 存在
                Integer[] usrIds = cmSearchUsrResponse.getUsr_ids();
                CmGetUsrInfoRequest cmGetUsrInfoRequest = new CmGetUsrInfoRequest();
                cmGetUsrInfoRequest.setUsr_ids(cmSearchUsrResponse.getUsr_ids());
                Integer[] lastModifyDtms = new Integer[usrIds.length];
                for (int i = 0; i < usrIds.length; i++) {
                    lastModifyDtms[i] = 0;
                }
                cmGetUsrInfoRequest.setLast_modify_dtms(lastModifyDtms);
                CmGetUsrInfoResponse cmGetUsrInfoResponse = mcuZjBridge.getConferenceManageApi().getUsrInfo(cmGetUsrInfoRequest);
                if (cmGetUsrInfoResponse != null && cmGetUsrInfoResponse.getUsr_ids().length > 0) {
                    for (int i = 0; i < cmGetUsrInfoResponse.getUsr_ids().length; i++) {
                        Integer usrId = cmGetUsrInfoResponse.getUsr_ids()[i];
                        String nickName = cmGetUsrInfoResponse.getNick_names()[i];
                        if (!nickName.equals(busiTerminal.getName())) {
                            CmModUsrRequest cmModUsrRequest = new CmModUsrRequest();
                            cmModUsrRequest.setUsr_id(usrId);
                            cmModUsrRequest.setNick_name(busiTerminal.getName());
                            CmModUsrResponse cmModUsrResponse = mcuZjBridge.getConferenceManageApi().modifyUsr(cmModUsrRequest);
                            if (cmModUsrResponse != null) {
                            }
                        }
                    }
                }
            } else {
                if (busiTerminalExist.getAvailable() != null && busiTerminalExist.getAvailable() == 2) {
                    if (busiTerminal.getExpiredDate() == null || busiTerminal.getExpiredDate().getTime() > System.currentTimeMillis()) {
                        try {
                            Integer newTerminalNum = busiTerminalExist.getTerminalNum();
                            if (newTerminalNum == null) {
                                newTerminalNum = busiTerminalMapper.getAvailableTerminalNumForZj();
                                if (newTerminalNum == null) {
                                    newTerminalNum = 51;
                                }
                                if (newTerminalNum >= 10000) {
                                    throw new SystemException("MCU资源耗尽。ZJ终端数超过10000");//ZJ终端50-9999
                                }
                            }
                            String terminalNumStr = StringUtils.leftPad(newTerminalNum.toString(), length, "0");
                            String userMark = terminalNumStr;
                            String nickName = busiTerminal.getName();
                            CmAddUsrRequest cmAddUsrRequest = CmAddUsrRequest.buildDefaultRequestForAddEps();
                            cmAddUsrRequest.setNick_name(nickName);
                            cmAddUsrRequest.setLogin_id(busiTerminal.getCredential());
                            cmAddUsrRequest.setLogin_pwd(busiTerminal.getPassword());
                            cmAddUsrRequest.setUsr_mark(userMark);
                            List<Integer> belongToDepartments = new ArrayList<>();
                            belongToDepartments.add(mcuZjBridge.getTopDepartmentId());// 总部
                            cmAddUsrRequest.setBelong_to_departments(belongToDepartments);
                            if (TerminalType.ZJ_H323.getId() == busiTerminal.getType()) {
                                cmAddUsrRequest.setPtotocol_type(1);//  1 H323 协议， 2 SIP 协议， 3 多流协议， 5：RTSP 协议， 默认为多流协议
                            } else {
                                cmAddUsrRequest.setPtotocol_type(2);//  1 H323 协议， 2 SIP 协议， 3 多流协议， 5：RTSP 协议， 默认为多流协议
                            }
                            cmAddUsrRequest.setOption("endpoint");
                            cmAddUsrRequest.setIs_endpoint(1);
                            CmAddUsrResponse cmAddUsrResponse = mcuZjBridge.getConferenceManageApi().addUsr(cmAddUsrRequest);
                            if (cmAddUsrResponse != null && cmAddUsrResponse.getResult().contains("success")) {
                                busiTerminal.setTerminalNum(newTerminalNum);
                                busiTerminal.setZjServerId(mcuZjBridge.getBusiMcuZj().getId());
                                busiTerminal.setZjUserId(Long.valueOf(cmAddUsrResponse.getUsr_id()));//存储zj的用户id
                                busiTerminal.setIp(mcuZjBridge.getBusiMcuZj().getIp());
                                busiTerminal.setAvailable(1);
                            } else {
                                throw new SystemException("更新ZJ账号失败！");
                            }
                        } catch (Exception e) {
                            if (e instanceof SystemException) {
                                throw e;
                            } else {
                                throw new SystemException("更新ZJ账号失败！");
                            }
                        }
                    }
                }
            }
        }
        // SMC2-SIP
        else if (TerminalType.isSMC2SIP(busiTerminal.getType())) {

            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()) && numberPatternSMC2SIP.matcher(String.valueOf(busiTerminal.getCredential())).matches(), "SMC2SIP账号必须为5-10位数字组成！");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()) && passwordPatternSMC2SIP.matcher(String.valueOf(busiTerminal.getPassword())).matches(), "SMC2SIP账户密码必须为8位，大小写字母加符号加数字的组合！");
            Smc2Bridge smc2Bridge = Smc2BridgeCache.getInstance().getAvailableBridgesByDept(busiTerminal.getDeptId().intValue());

            Assert.notNull(smc2Bridge, "很抱歉，【" + SysDeptCache.getInstance().get(busiTerminal.getDeptId()).getDeptName() + "】当前未绑定SMC2服务器，请联系管理员配置您的SMC2服务器！");
            {
                BusiTerminal con = new BusiTerminal();
                con.setType(TerminalType.SMC2_SIP.getId());
                con.setCredential(busiTerminal.getCredential());
                List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalList(con);
                Assert.isTrue(ObjectUtils.isEmpty(ts), "该账号已存在，请勿重复添加");
            }
            try {
                TerminalInfoEx siteInfo = new TerminalInfoEx();
                siteInfo.setName(busiTerminal.getName());
                siteInfo.setUri(busiTerminal.getCredential());
                siteInfo.setType(7);
                siteInfo.setRate("1920k");
                //新建一个List对象，用于存放终端支持的视频能力参数列表
                List<VideoCapbilityItemEx> videoCapbility = new ArrayList<VideoCapbilityItemEx>();
                //新建一个VideoCapbilityItemEx对象
                VideoCapbilityItemEx videoCapbilityItemEx = new VideoCapbilityItemEx();
                //设置视频协议为H.261协议
                videoCapbilityItemEx.setVideoProtocol(1);
                //新建一个List对象，用于存放视频协议对应的视频格式列表
                List<Integer> videoFormat = new ArrayList<Integer>();
                //添加视频格式4CIF
                videoFormat.add(1);
                videoCapbilityItemEx.setVideoFormat(videoFormat);
                videoCapbility.add(videoCapbilityItemEx);
                siteInfo.setVideoCapbility(videoCapbility);
                siteInfo.setRegUser(busiTerminal.getName());
                //设置密码.
                siteInfo.setRegPassword(busiTerminal.getPassword());

                SiteServiceEx siteServiceEx = ServiceFactoryEx.getService(SiteServiceEx.class);
                Integer integer = siteServiceEx.editSiteInfoEx(siteInfo);
                if(integer!=0){
                    throw new CustomException("添加smc2sip账号失败！");
                }

            } catch (Exception e) {
                if (e instanceof SystemException) {
                    throw e;
                } else {
                    throw new SystemException("添加smc2sip账号失败！");
                }
            }
        }
        else if (TerminalType.isZTE(busiTerminal.getType())) {
            throw new SystemException("不可修改,先删除后再添加！");
        }else if(TerminalType.isSMCSIP(busiTerminal.getType())){

            BusiTerminal terminal = TerminalCache.getInstance().get(busiTerminal.getId());
            if(terminal!=null){
                if(!Objects.equals(terminal.getTerminalUsername(),busiTerminal.getTerminalUsername())||
                        !Objects.equals(terminal.getTerminalPassword(),busiTerminal.getTerminalPassword())||
                        !Objects.equals(terminal.getSn(),busiTerminal.getSn())||
                        !Objects.equals(terminal.getSn(),busiTerminal.getIp())){
                    HuaweiBarSnTask huaweiBarSnTask = new HuaweiBarSnTask("会议ID" + busiTerminal.getId(), 10, busiTerminal.getIp(), busiTerminal.getTerminalUsername(), busiTerminal.getTerminalPassword(), busiTerminal.getSn(), busiTerminal);
                    BeanFactory.getBean(TaskService.class).addTask(huaweiBarSnTask);
                }
            }

        }  //GB-28181
        else if(TerminalType.isGB28181(busiTerminal.getType())){
            WvpAddDeviceRequest wvpAddDeviceRequest = new WvpAddDeviceRequest();
            wvpAddDeviceRequest.setName(busiTerminal.getName());
            wvpAddDeviceRequest.setPassword(busiTerminal.getPassword());
            if(Strings.isBlank(busiTerminal.getPassword())){
                wvpAddDeviceRequest.setPassword("123456");
            }
            wvpAddDeviceRequest.setDeviceId(busiTerminal.getNumber());
            wvpDeviceService.updateDevice(wvpAddDeviceRequest);
            WvpBridge wvpBridge = WvpBridgeCache.getInstance().get();
            busiTerminal.setIp(wvpBridge.getIp());
        }

        int c = busiTerminalMapper.updateBusiTerminal(busiTerminal);
        if (c > 0) {
            TerminalCache.getInstance().remove(ot.getId());
            TerminalCache.getInstance().put(busiTerminal.getId(), busiTerminalMapper.selectBusiTerminalById(busiTerminal.getId()));
            for (ITerminalInterceptor iTerminalInterceptor : terminalInterceptors) {
                try {
                    iTerminalInterceptor.terminalUpdated(busiTerminal);
                } catch (Exception e) {
                    LoggerFactory.getLogger(getClass()).error("拦截处理出错", e);
                }
            }
        }
        return c;
    }

    /**
     * 批量删除终端信息
     *
     * @param ids 需要删除的终端信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiTerminalByIds(Long[] ids) {

        int c = 0;
        for (Long id : ids) {
            c += deleteBusiTerminalById(id);
        }

        return c;
    }

    /**
     * 删除终端信息信息
     *
     * @param id 终端信息ID
     * @return 结果
     */
    @Override
    @FreeSwitchTransaction
    public int deleteBusiTerminalById(Long id) {
        int c = 0;
        try {
            BusiTerminal busiTerminal = busiTerminalMapper.selectBusiTerminalById(id);
            if (busiTerminal == null) {
                return c;
            }
            BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(id);
            if (busiUserTerminal != null) {
                throw new SystemException(1, "终端删除失败：（" + busiTerminal.getName() + "）该终端已被用户绑定！");
            }

            ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
            viewTemplateConferenceCon.setCreateBy(ConferenceTemplateCreateType.AUTO.getName());
            List<ViewTemplateConference> viewTemplateConferenceList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCon);
            for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceList) {
                List<ViewConferenceAppointment> viewConferenceAppointmentList = viewConferenceAppointmentMapper.selectViewConferenceAppointmentByTemplateId(viewTemplateConference.getMcuType(), viewTemplateConference.getId());
                if (viewConferenceAppointmentList == null || viewConferenceAppointmentList.size() == 0) {
                    McuType mcuType = McuType.convert(viewTemplateConference.getMcuType());
                    switch (mcuType) {
                        case FME : {
                            BusiTemplateParticipant busiTemplateParticipantCon = new BusiTemplateParticipant();
                            busiTemplateParticipantCon.setTerminalId(id);
                            busiTemplateParticipantCon.setTemplateConferenceId(viewTemplateConference.getId());
                            List<BusiTemplateParticipant> busiTemplateParticipantList = busiTemplateParticipantMapper.selectBusiTemplateParticipantList(busiTemplateParticipantCon);
                            for (BusiTemplateParticipant busiTemplateParticipant : busiTemplateParticipantList) {
                                busiTemplateParticipantMapper.deleteBusiTemplateParticipantById(busiTemplateParticipant.getId());
                            }
                            break;
                        }
                    }
                }
            }

            c = busiTerminalMapper.deleteBusiTerminalById(id);
            if (c > 0) {
                if (TerminalType.isFSBC(busiTerminal.getType())) {
                    FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(busiTerminal.getFsbcServerId());
                    RestResponse restResponse = fsbcBridge.getCredentialInvoker().delete(FsbcCredential.newCredential().name(busiTerminal.getCredential()));
                    if (!restResponse.isSuccess() && !"Failed to delete credential - Name does not exist".equals(restResponse.getMessage())) {
                        throw new SystemException(1005445, "FSBC账号删除失败：" + restResponse.getMessage());
                    }
                } else if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                    BusiFreeSwitchDept fsd = DeptFcmMappingCache.getInstance().get(busiTerminal.getDeptId());
                    if (FcmType.CLUSTER == FcmType.convert(fsd.getFcmType())) {
                        FcmBridgeCluster fcmBridgeCluster = FcmBridgeCache.getInstance().getByFcmClusterId(fsd.getServerId());
                        if (fcmBridgeCluster != null) {
                            List<FcmBridge> fcmBridges = fcmBridgeCluster.getFcmBridges();
                            // 由于使用固定用户信息数据库，任意一个FCM即可
                            FcmBridge fcmBridge = fcmBridges.get(0);
                            Assert.isTrue(fcmBridge.deleteFreeSwitchUserByIds(busiTerminal.getCredential()) == FcmConfigConstant.SUCCESS, "FCM-SIP账号删除失败：" + busiTerminal.getCredential());
                        } else {
                            Assert.isTrue(0 == FcmConfigConstant.SUCCESS, "FCM-SIP账号删除失败：" + busiTerminal.getCredential());
                        }
                    } else {
                        FcmBridge fcmBridge = FcmBridgeCache.getInstance().getById(fsd.getServerId());
                        Assert.isTrue(fcmBridge.deleteFreeSwitchUserByIds(busiTerminal.getCredential()) == FcmConfigConstant.SUCCESS, "FCM-SIP账号删除失败：" + busiTerminal.getCredential());
                    }
                }
                if (TerminalType.isZJ(busiTerminal.getType())) {
                    McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().get(busiTerminal.getZjServerId());
                    if (mcuZjBridge != null) {
                        CmSearchUsrRequest cmSearchUsrRequest = new CmSearchUsrRequest();
                        String[] filterType = new String[1];
                        filterType[0] = "login_id";
                        Object[] filterValue = new Object[1];
                        filterValue[0] = busiTerminal.getCredential();
                        cmSearchUsrRequest.setFilter_type(filterType);
                        cmSearchUsrRequest.setFilter_value(filterValue);
                        CmSearchUsrResponse cmSearchUsrResponse = mcuZjBridge.getConferenceManageApi().searchUsr(cmSearchUsrRequest);
                        if (cmSearchUsrResponse != null && cmSearchUsrResponse.getUsr_ids().length > 0) {
                            // 删除
                            CmDeleteUsrRequest cmDeleteUsrRequest = new CmDeleteUsrRequest();
                            cmDeleteUsrRequest.setUsr_ids(cmSearchUsrResponse.getUsr_ids());
                            cmDeleteUsrRequest.setOption("endpoint");
                            CmDeleteUsrResponse cmDeleteUsrResponse = mcuZjBridge.getConferenceManageApi().deleteUsr(cmDeleteUsrRequest);
                            if (cmDeleteUsrResponse != null) {
                            }
                            cmSearchUsrResponse = mcuZjBridge.getConferenceManageApi().searchUsr(cmSearchUsrRequest);
                            if (cmSearchUsrResponse != null && cmSearchUsrResponse.getUsr_ids().length > 0) {
                                Assert.isTrue(false, "ZJ账号删除失败：" + busiTerminal.getCredential());
                            }
                        }
                    }
                }
                if (TerminalType.isSMC2SIP(busiTerminal.getType())) {
                    //新建一个List对象，用于准备删除会场的URI
                    List<String> siteUris = new ArrayList<>();
                    siteUris.add(busiTerminal.getCredential());
                    SiteServiceEx siteServiceEx = ServiceFactoryEx.getService(SiteServiceEx.class);
                    Integer resultCode = siteServiceEx.deleteSiteInfoEx(siteUris);
                    if(resultCode!=0){
                        throw new CustomException("smc2sip账户删除失败");
                    }
                }
                if(TerminalType.isZTE(busiTerminal.getType())){
                    McuZteBridge mcuZteBridge = McuZteBridgeCache.getInstance().get(busiTerminal.getZteServerId());
                    if (mcuZteBridge != null) {
                        // 删除
                        DelAddressBookRequest cmDeleteUsrRequest = new DelAddressBookRequest();
                        cmDeleteUsrRequest.setAccount(mcuZteBridge.getBusiMcuZte().getUsername());
                        cmDeleteUsrRequest.setTerminalId(busiTerminal.getZteTerminalId());
                        DelAddressBookResponse cmDeleteUsrResponse = mcuZteBridge.getConferenceManageApi().deleteMrTerminal(cmDeleteUsrRequest);
                        if (cmDeleteUsrResponse != null&&!cmDeleteUsrResponse.getResult().equals("200")) {
                            Assert.isTrue(false, "ZJ账号删除失败：" + busiTerminal.getName());
                        }

                    }
                }
                if(TerminalType.isGB28181(busiTerminal.getType())){
                    WvpBridge wvpBridge = WvpBridgeCache.getInstance().get();
                    if(wvpBridge!=null){
                        wvpBridge.getWvpControllApi().deleteDevice(busiTerminal.getNumber());
                    }
                }

                // 关联删除终端绑定的高级参数
                busiTerminalMeetingJoinSettingsService.deleteBusiTerminalMeetingJoinSettingsById(id);
                TerminalCache.getInstance().remove(id);
                for (ITerminalInterceptor iTerminalInterceptor : terminalInterceptors) {
                    try {
                        iTerminalInterceptor.terminalRemoved(busiTerminal);
                    } catch (Exception e) {
                        LoggerFactory.getLogger(getClass()).error("拦截处理出错", e);
                    }
                }
            }
        } catch (SystemException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("终端删除失败：", e);
            throw new SystemException(1000213, "终端删除失败，请确认所选终端没有被模板等功能使用！");
        }

        return c;
    }

    /**
     * 查询所有的终端列表
     *
     * @return
     */
    @Override
    public List<BusiTerminal> selectAll() {
        return busiTerminalMapper.selectBusiTerminalList(new BusiTerminal());
    }

    @Override
    public String getRandomAccount(BusiTerminal busiTerminal) {
        String randomAccount = null;
        if (busiTerminal != null) {
            if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                BusiFcmNumberSection busiFcmNumberSection = selectBusiFcmNumberSection(busiTerminal.getDeptId());
                if (busiFcmNumberSection != null) {
                    Long startValue = busiFcmNumberSection.getStartValue();
                    Long endValue = busiFcmNumberSection.getEndValue();
                    for (long i = startValue; i <= endValue; i++) {
                        String account = String.valueOf(i);
                        if (!TerminalCache.getInstance().hasFcmAccount(account)) {
                            randomAccount = account;
                            break;
                        }
                    }
                }
            }
        }
        if (StringUtils.isEmpty(randomAccount)) {
            throw new SystemException("该部门的FCM号段已无可用账号！");
        }
        return randomAccount;
    }

    @Override
    public PaginationData<ModelBean> getInfoDisplayTerminal(TerminalSearchVo busiTerminal) {
        Assert.notNull(busiTerminal.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long deptId = busiTerminal.getDeptId() == null ? loginUser.getUser().getDeptId() : busiTerminal.getDeptId();

        List<Long> deptIds = null;
        if (deptId != null) {
            deptIds = new ArrayList<>();
        }

        SysDept dept = new SysDept();
        dept.setDeptId(loginUser.getUser().getDeptId());
        List<SysDept> ds = sysDeptService.selectDeptList(dept);
        for (SysDept sysDept : ds) {
            if (deptIds != null) {
                deptIds.add(sysDept.getDeptId());
            }
        }

        PaginationData<ModelBean> pd = new PaginationData<>();

        busiTerminal.getParams().put("deptIds", deptIds);
        if (StringUtils.isNotEmpty(busiTerminal.getSearchKey())) {
            String searchKey = busiTerminal.getSearchKey();
            if (searchKey.contains("@")) {
                searchKey = searchKey.substring(0, searchKey.indexOf("@"));
            }
            busiTerminal.getParams().put("searchKey", searchKey);
        }

        if (busiTerminal.getPageNum() != null && busiTerminal.getPageSize() != null) {
            PageHelper.startPage(busiTerminal.getPageNum(), busiTerminal.getPageSize());
        }

        List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
        PageInfo<?> pageInfo = new PageInfo<>(ts);

        try {
            McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().getAvailableMcuZjBridgesByDept(deptId).getMasterMcuZjBridge();
            if (mcuZjBridge != null) {
                GetUsrOnlineStatusTask getUsrOnlineStatusTask = new GetUsrOnlineStatusTask("page_" + pageInfo.getPageNum(), 2000, ts, mcuZjBridge);
                delayTaskService.addTask(getUsrOnlineStatusTask);
            }
        } catch (Exception e) {
        }

        Map<String, BusiTerminalUpgrade> busiTerminalUpgradeMap = new HashMap<>();
        if (ts.size() > 0) {
            List<BusiTerminalUpgrade> busiTerminalUpgradeList = busiTerminalUpgradeMapper.selectBusiTerminalUpgradeList(new BusiTerminalUpgrade());
            for (BusiTerminalUpgrade busiTerminalUpgrade : busiTerminalUpgradeList) {
                busiTerminalUpgradeMap.put(busiTerminalUpgrade.getTerminalType(), busiTerminalUpgrade);
            }
        }
        for (BusiTerminal busiTerminalTemp : ts) {
            String appType = busiTerminalTemp.getAppType();
            if (StringUtils.isNotEmpty(appType)) {
                List<AppType> mqttTypeList = AppType.getMqttTypeList();
                AppType convert = AppType.convert(appType);
                if (mqttTypeList.contains(convert)) {
                    ModelBean m = new ModelBean(busiTerminalTemp);
                    if (busiTerminalTemp.getDeptId() != null) {
                        SysDept sysDept = SysDeptCache.getInstance().get(busiTerminalTemp.getDeptId());
                        if (sysDept != null) {
                            m.put("deptName", sysDept.getDeptName());
                        }
                    }
                    BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(busiTerminalTemp.getId());
                    if (busiUserTerminal != null) {
                        m.put("userId", busiUserTerminal.getUserId());
                        SysUser sysUser = sysUserService.selectUserById(busiUserTerminal.getUserId());
                        if (sysUser != null) {
                            m.put("userName", sysUser.getUserName());
                        }
                    }
                    if (TerminalType.isFSBC(busiTerminalTemp.getType())) {
                        FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(busiTerminalTemp.getFsbcServerId());
                        if (fsbcBridge != null) {
                            BusiFsbcRegistrationServer busiFsbcRegistrationServer = fsbcBridge.getBusiFsbcRegistrationServer();
                            if (busiFsbcRegistrationServer != null) {
                                String domainName = busiFsbcRegistrationServer.getDomainName();
                                if (StringUtils.isNotEmpty(domainName)) {
                                    m.put("ip", domainName);
                                }
                                m.put("uri", busiTerminalTemp.getCredential() + "@" + busiFsbcRegistrationServer.getCallIp());

                            }

                        }
                    } else if (TerminalType.isFCMSIP(busiTerminalTemp.getType())) {
                        FcmBridge fcmBridge = FcmBridgeCache.getInstance().getById(busiTerminalTemp.getFsServerId());
                        if (fcmBridge != null) {
                            BusiFreeSwitch busiFreeSwitch = fcmBridge.getBusiFreeSwitch();
                            if (busiFreeSwitch != null) {
                                String domainName = busiFreeSwitch.getDomainName();
                                if (StringUtils.isNotEmpty(domainName)) {
                                    m.put("ip", domainName);
                                }
                                m.put("uri", busiTerminalTemp.getCredential() + "@" + busiFreeSwitch.getIp());
                            }
                        }
                    } else if (TerminalType.isZJ(busiTerminalTemp.getType())) {
                        McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().get(busiTerminalTemp.getZjServerId());
                        if (mcuZjBridge != null) {
                            BusiMcuZj busiMcuZj = mcuZjBridge.getBusiMcuZj();
                            if (busiMcuZj != null) {
                                String domainName = busiMcuZj.getCucmIp();
                                if (StringUtils.isNotEmpty(domainName)) {
                                    m.put("ip", domainName);
                                }
                                m.put("mcuDomain", busiMcuZj.getMcuDomain());
                            }
                        }
                    } else if (TerminalType.isCisco(busiTerminalTemp.getType())) {
                        if (busiTerminalTemp.getNumber() == null) {
                            m.put("uri", busiTerminalTemp.getIp());
                        } else {
                            m.put("uri", busiTerminalTemp.getNumber() + "@" + busiTerminalTemp.getIp());
                        }
                    } else if (TerminalType.isSMCNUMBER(busiTerminalTemp.getType())) {
                        m.put("uri", busiTerminalTemp.getNumber());
                    } else if (TerminalType.isWindows(busiTerminalTemp.getType())) {
                        m.put("uri", busiTerminalTemp.getIp());
                    } else if (TerminalType.isSMCSIP(busiTerminalTemp.getType())) {
                        Map<String, Object> businessProperties = busiTerminalTemp.getBusinessProperties();
                        if (businessProperties != null) {
                            m.put("areaId", businessProperties.get("areaId"));
                            m.put("serviceZoneId", businessProperties.get("serviceZoneId"));
                            m.put("codeId", businessProperties.get("codeId"));
                            m.put("terminalParam", businessProperties.get("terminalParam"));
                            m.put("organizationId", businessProperties.get("organizationId"));
                        }
                        m.put("uri", busiTerminalTemp.getNumber());
                    }

                    boolean hasAppUpdate = false;
                    if (StringUtils.isNotEmpty(busiTerminalTemp.getAppType())) {
                        BusiTerminalUpgrade busiTerminalUpgrade = busiTerminalUpgradeMap.get(busiTerminalTemp.getAppType());
                        if (busiTerminalUpgrade != null) {
                            if (StringUtils.isEmpty(busiTerminalTemp.getAppVersionCode()) || busiTerminalUpgrade.getVersionNum().length() > busiTerminalTemp.getAppVersionCode().length() || busiTerminalUpgrade.getVersionNum().compareTo(busiTerminalTemp.getAppVersionCode()) > 0) {
                                m.put("latestVersionCode", busiTerminalUpgrade.getVersionNum());
                                m.put("latestVersionName", busiTerminalUpgrade.getVersionName());
                                m.put("latestVersionTime", busiTerminalUpgrade.getCreateTime());
                                if (busiTerminalTemp.getMqttOnlineStatus() != null && TerminalOnlineStatus.ONLINE.getValue() == busiTerminalTemp.getMqttOnlineStatus()) {
                                    hasAppUpdate = true;
                                }
                            }
                        }
                    }
                    m.put("hasAppUpdate", hasAppUpdate);
                    pd.addRecord(m);
                }
            } else {
                if (TerminalType.isOnlyIP(busiTerminalTemp.getType())) {
                    ModelBean m = new ModelBean(busiTerminalTemp);
                    if (busiTerminalTemp.getDeptId() != null) {
                        SysDept sysDept = SysDeptCache.getInstance().get(busiTerminalTemp.getDeptId());
                        if (sysDept != null) {
                            m.put("deptName", sysDept.getDeptName());
                        }
                    }
                    m.put("uri", busiTerminalTemp.getIp());
                    pd.addRecord(m);
                }
            }
        }
        pd.setTotal(pd.getRecords().size());
        pd.setSize(pageInfo.getSize());
        pd.setPage(pageInfo.getPageNum());

        return pd;
    }

    public BusiFcmNumberSection selectBusiFcmNumberSection(Long deptId) {
        io.jsonwebtoken.lang.Assert.isTrue(deptId != null, "部门ID不能为空");
        BusiFreeSwitchDept busiFreeSwitchDept = DeptFcmMappingCache.getInstance().get(deptId);
        if (busiFreeSwitchDept == null) {
            return null;
        }
        BusiFcmNumberSection busiFcmNumberSection = new BusiFcmNumberSection();
        busiFcmNumberSection.setDeptId(deptId);
        List<BusiFcmNumberSection> nsl = busiFcmNumberSectionMapper.selectBusiFcmNumberSectionList(busiFcmNumberSection);
        if (null != nsl && nsl.size() > 0) {
            return nsl.get(0);
        } else {
            Long a = FcmAccountCacheAndUtils.getInstance().deptId(busiFcmNumberSection.getDeptId());
            if (a == 0) {
                io.jsonwebtoken.lang.Assert.isTrue(false, "该部门未分配FCM号段，请联系管理员分配！");
            } else {
                BusiFcmNumberSection busiFcmNumberSection1 = new BusiFcmNumberSection();
                busiFcmNumberSection1.setDeptId(a);
                return this.selectBusiFcmNumberSection(a);
            }
        }
        return null;
    }

}
