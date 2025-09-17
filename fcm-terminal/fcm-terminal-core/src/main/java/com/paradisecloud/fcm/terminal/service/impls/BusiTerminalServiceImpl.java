package com.paradisecloud.fcm.terminal.service.impls;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.AttendType;
import com.paradisecloud.fcm.common.enumer.FcmType;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.utils.SipAccountUtil;
import com.paradisecloud.fcm.dao.mapper.BusiFcmNumberSectionMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalUpgradeMapper;
import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.TerminalSearchVo;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmAccountCacheAndUtils;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.constant.FcmConfigConstant;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridgeCluster;
import com.paradisecloud.fcm.terminal.fsbc.cache.DeptFsbcMappingCache;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcCredential;
import com.paradisecloud.fcm.terminal.service.interfaces.IBusiTerminalMeetingJoinSettingsService;
import com.paradisecloud.fcm.terminal.service.interfaces.IBusiTerminalService;
import com.paradisecloud.fcm.terminal.service.interfaces.ITerminalInterceptor;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.service.ISysDeptService;
import com.paradisecloud.system.service.ISysUserService;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import com.sinhy.utils.RegExpUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 终端信息Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
@Service
@Transactional
public class BusiTerminalServiceImpl implements IBusiTerminalService 
{
	@Autowired
	private BusiFcmNumberSectionMapper busiFcmNumberSectionMapper;
	
    @Autowired
    private BusiTerminalMapper busiTerminalMapper;

    @Autowired
    private ISysDeptService sysDeptService;
    
    @Autowired
    private IBusiTerminalMeetingJoinSettingsService busiTerminalMeetingJoinSettingsService;
    
    @Autowired
    private List<ITerminalInterceptor> terminalInterceptors;

    @Resource
    private BusiUserTerminalMapper busiUserTerminalMapper;

    @Resource
    private ISysUserService sysUserService;

    @Resource
    private BusiTerminalUpgradeMapper busiTerminalUpgradeMapper;
    
    /**
     * fsbc账号样板
     */
    private Pattern numberPattern = Pattern.compile("^[1-9]\\d{3,9}$");
    
    /**
     * fsbc密码样板
     */
    private Pattern passwordPattern = Pattern.compile("^\\w{1,16}$");
    
    
    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-10-29 12:20 
     * @param businessFieldType
     * @return
     * @see com.paradisecloud.fcm.terminal.service.interfaces.IBusiTerminalService#getDeptRecordCounts(java.lang.Integer)
     */
    @Override
    public List<DeptRecordCount> getDeptRecordCounts(Integer businessFieldType)
    {
        return busiTerminalMapper.getDeptRecordCounts(businessFieldType);
    }

    /**
     * 查询终端信息
     * 
     * @param id 终端信息ID
     * @return 终端信息
     */
    @Override
    public BusiTerminal selectBusiTerminalById(Long id)
    {
        return busiTerminalMapper.selectBusiTerminalById(id);
    }

    /**
     * 查询终端信息列表
     * 
     * @param busiTerminal 终端信息
     * @return 终端信息
     */
    @Override
    public PaginationData<ModelBean> selectBusiTerminalList(TerminalSearchVo busiTerminal)
    {
        Assert.notNull(busiTerminal.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long deptId = busiTerminal.getDeptId() == null ? loginUser.getUser().getDeptId() : busiTerminal.getDeptId();
        
        List<Long> deptIds = null;
        if (deptId != null)
        {
            deptIds = new ArrayList<>();
        }
        
        SysDept dept = new SysDept();
        dept.setDeptId(loginUser.getUser().getDeptId());
        List<SysDept> ds = sysDeptService.selectDeptList(dept);
        for (SysDept sysDept : ds)
        {
            if (deptIds != null)
            {
                deptIds.add(sysDept.getDeptId());
            }
        }

        PaginationData<ModelBean> pd = new PaginationData<>();

        busiTerminal.getParams().put("deptIds", deptIds);
        if (StringUtils.isNotEmpty(busiTerminal.getSearchKey())) {
            busiTerminal.getParams().put("searchKey", busiTerminal.getSearchKey());
        }

        if (busiTerminal.getPageNum() != null && busiTerminal.getPageSize() != null) {
            PageHelper.startPage(busiTerminal.getPageNum(), busiTerminal.getPageSize());
        }

        List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
        PageInfo<?> pageInfo = new PageInfo<>(ts);
        Map<String, BusiTerminalUpgrade> busiTerminalUpgradeMap = new HashMap<>();
        if (ts.size() > 0) {
            List<BusiTerminalUpgrade> busiTerminalUpgradeList = busiTerminalUpgradeMapper.selectBusiTerminalUpgradeList(new BusiTerminalUpgrade());
            for (BusiTerminalUpgrade busiTerminalUpgrade: busiTerminalUpgradeList) {
                busiTerminalUpgradeMap.put(busiTerminalUpgrade.getTerminalType(), busiTerminalUpgrade);
            }
        }
        for (BusiTerminal busiTerminalTemp : ts)
        {
            ModelBean m = new ModelBean(busiTerminalTemp);
            m.put("deptName", SysDeptCache.getInstance().get(busiTerminalTemp.getDeptId()).getDeptName());
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
                    }
                }
            }
//            TerminalAppVersion terminalAppVersion = TerminalAppVersionCache.getInstance().get(busiTerminalTemp.getType());
//            if (terminalAppVersion != null) {
//                if (StringUtils.isEmpty(busiTerminalTemp.getAppVersionCode()) || terminalAppVersion.getVersionCode().compareTo(busiTerminalTemp.getAppVersionCode()) > 0) {
//                    m.put("latestVersionCode", terminalAppVersion.getVersionCode());
//                    m.put("latestVersionName", terminalAppVersion.getVersionName());
//                    m.put("latestVersionTime", terminalAppVersion.getCreateTime());
//                }
//            }
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
    public boolean isFcm(long deptId, String credential){
        BusiTerminal busiTerminal = new BusiTerminal();
        busiTerminal.setDeptId(deptId);
        BusiFcmNumberSection fcmNumberSection = new BusiFcmNumberSection();
        fcmNumberSection.setDeptId(busiTerminal.getDeptId());

        List<BusiFcmNumberSection> nsl = busiFcmNumberSectionMapper.selectBusiFcmNumberSectionList(fcmNumberSection);
        if (null != nsl && nsl.size() > 0){
            for (int i = 0; i < nsl.size(); i++) {
                Long bigDecimal = Long.valueOf(credential);
                Long startValue = nsl.get(i).getStartValue();
                Long endValue = nsl.get(i).getEndValue();
                if (startValue <= bigDecimal && bigDecimal <= endValue){
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

    @Override
    public String getRandomAccount(BusiTerminal busiTerminal) {
        String randomAccount = null;
        if (busiTerminal != null) {
            if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                BusiFcmNumberSection busiFcmNumberSection = selectBusiFcmNumberSection(busiTerminal.getDeptId());
                if (busiFcmNumberSection != null) {
                    Long alreadyExistTerminalCredential = busiTerminalMapper.getAlreadyExistTerminalCredential(busiTerminal.getType(), busiFcmNumberSection.getStartValue(), busiFcmNumberSection.getEndValue());
                    if (ObjectUtils.isEmpty(alreadyExistTerminalCredential)) {
                        BusiTerminal busiTerminalTemp = new BusiTerminal();
                        busiTerminalTemp.setCredential(busiFcmNumberSection.getStartValue().toString());
                        busiTerminalTemp.setType(busiTerminal.getType());
                        List<BusiTerminal> busiTerminalList = busiTerminalMapper.selectBusiTerminalList(busiTerminalTemp);
                        if (busiTerminalList != null && busiTerminalList.size() > 0) {
                            throw new SystemException("该部门的FCM号段已无可用账号！");
                        } else {
                            randomAccount = busiFcmNumberSection.getStartValue().toString();
                        }
                    } else {
                        randomAccount = String.valueOf(alreadyExistTerminalCredential);
                    }
                    return randomAccount;
                }
            }
        }
        return randomAccount;
    }

    public BusiFcmNumberSection selectBusiFcmNumberSection (Long deptId) {
        io.jsonwebtoken.lang.Assert.isTrue(deptId != null, "部门ID不能为空");
        BusiFreeSwitchDept busiFreeSwitchDept = DeptFcmMappingCache.getInstance().get(deptId);
        if (busiFreeSwitchDept == null) {
            return null;
        }
        BusiFcmNumberSection busiFcmNumberSection = new BusiFcmNumberSection();
        busiFcmNumberSection.setDeptId(deptId);
        List<BusiFcmNumberSection> nsl = busiFcmNumberSectionMapper.selectBusiFcmNumberSectionList(busiFcmNumberSection);
        if (null != nsl && nsl.size() > 0){
            return nsl.get(0);
        }else {
            Long a = FcmAccountCacheAndUtils.getInstance().deptId(busiFcmNumberSection.getDeptId());
            if (a == 0){
                io.jsonwebtoken.lang.Assert.isTrue(false,"该部门未分配FCM号段，请联系管理员分配！");
            }else{
                BusiFcmNumberSection busiFcmNumberSection1 = new BusiFcmNumberSection();
                busiFcmNumberSection1.setDeptId(a);
                return this.selectBusiFcmNumberSection(a);
            }
        }
        return null;

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
    public int insertBusiTerminal(BusiTerminal busiTerminal)
    {
        return insertBusiTerminal(busiTerminal, false);
    }
    
    /**
     * 新增终端信息
     * 
     * @param busiTerminal 终端信息
     * @param isAutoAdd 是否是后端自动添加终端
     * @return 结果
     */
    @Override
    public int insertBusiTerminal(BusiTerminal busiTerminal, boolean isAutoAdd)
    {
        if (busiTerminal.getDeptId() == null)
        {
            throw new SystemException(1000102, "请选择部门");
        }

        if (!TerminalType.isSMCNUMBER(busiTerminal.getType())&&!TerminalType.isSMCSIP(busiTerminal.getType())) {
            if (!TerminalType.isGB28181(busiTerminal.getType())&&!TerminalType.isFSBC(busiTerminal.getType()) && !TerminalType.isFCMSIP(busiTerminal.getType())&&!TerminalType.isRtsp(busiTerminal.getType()) && !RegExpUtils.isIP(busiTerminal.getIp())) {
                throw new SystemException(1000103, "IP格式不正确");
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
        if (TerminalType.isFSBC(busiTerminal.getType()))
        {
            if (StringUtils.isEmpty(busiTerminal.getPassword())) {
                busiTerminal.setPassword(String.valueOf(123456));
            }
            if (!isAutoAdd) {
                Assert.isTrue(!SipAccountUtil.isAutoAccount(busiTerminal.getCredential()), "该账号号段已被占用！");
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
        }
        // FCM 类型
        else if (TerminalType.isFCMSIP(busiTerminal.getType()))
        {
            if (StringUtils.isEmpty(busiTerminal.getPassword())) {
                busiTerminal.setPassword(String.valueOf(123456));
            }
            if (!isAutoAdd) {
                Assert.isTrue(!SipAccountUtil.isAutoAccount(busiTerminal.getCredential()), "该账号号段已被占用！");
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
        }
        
        int c = 0;
        try
        {
            c = busiTerminalMapper.insertBusiTerminal(busiTerminal);
            if (c > 0)
            {
                TerminalCache.getInstance().put(busiTerminal.getId(), busiTerminal);
                for (ITerminalInterceptor iTerminalInterceptor : terminalInterceptors)
                {
                    try
                    {
                        iTerminalInterceptor.terminalInserted(busiTerminal);
                    }
                    catch (Exception e)
                    {
                        LoggerFactory.getLogger(getClass()).error("拦截处理出错", e);
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new SystemException("该MAC已存在，请勿重复添加", e);
        }
        return c;
    }

    @Override
    public String delSpace(String sn){
        if (StringUtils.isNotEmpty(sn)){
            return sn.replaceAll(" ","");
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
    public int updateBusiTerminal(BusiTerminal busiTerminal)
    {
        busiTerminal.setUpdateTime(new Date());
        
        if (busiTerminal.getDeptId() == null)
        {
            throw new SystemException(1000102, "请选择部门");
        }

        if (!TerminalType.isSMCNUMBER(busiTerminal.getType())&&!TerminalType.isSMCSIP(busiTerminal.getType())) {
            if (!TerminalType.isGB28181(busiTerminal.getType())&&!TerminalType.isFSBC(busiTerminal.getType()) && !TerminalType.isFCMSIP(busiTerminal.getType())&&!TerminalType.isRtsp(busiTerminal.getType()) && !RegExpUtils.isIP(busiTerminal.getIp())) {
                throw new SystemException(1000103, "IP格式不正确");
            }
        }

        Assert.notNull(busiTerminal.getBusinessFieldType(), "终端业务领域类型businessFieldType不能为空！");
        Assert.notNull(busiTerminal.getAttendType(), "终端入会类型不能为空！");
        AttendType.convert(busiTerminal.getAttendType());
        if (busiTerminal.getNumber() != null && busiTerminal.getNumber().trim().length() == 0) {
            busiTerminal.setNumber(null);
        }
        if (busiTerminal.getSn() != null && busiTerminal.getSn().trim().length() == 0) {
            busiTerminal.setSn(null);
        }

        BusiTerminal ot = busiTerminalMapper.selectBusiTerminalById(busiTerminal.getId());
        if (TerminalType.isFSBC(ot.getType()) && !TerminalType.isFSBC(busiTerminal.getType()))
        {
            throw new SystemException(1009894, "FSBC终端不能修改类型，若是要修改，请删除后再新增！");
        }

        if (TerminalType.isFSBC(busiTerminal.getType()))
        {
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()), "FSBC账号不能为空");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()), "FSBC密码不能为空");
            Assert.isTrue(ot.getDeptId().equals(busiTerminal.getDeptId()), "FSBC终端账号不支持切换部门，请删除重建");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getCredential()) && numberPattern.matcher(String.valueOf(busiTerminal.getCredential())).matches(), "FSBC-SIP账号必须为4-10位数字组成！");
            Assert.isTrue(!ObjectUtils.isEmpty(busiTerminal.getPassword()) && passwordPattern.matcher(String.valueOf(busiTerminal.getPassword())).matches(), "FSBC密码必须为1-16位字母、数字和下划线组成！");

            busiTerminal.setIp(null);
            if (!(ot.getCredential().equals(busiTerminal.getCredential()) && ot.getPassword().equals(busiTerminal.getPassword())))
            {
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
        else if (TerminalType.isFCMSIP(busiTerminal.getType()))
        {
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

        int c = busiTerminalMapper.updateBusiTerminal(busiTerminal);
        if (c > 0)
        {
            TerminalCache.getInstance().remove(ot.getId());
            TerminalCache.getInstance().put(busiTerminal.getId(), busiTerminalMapper.selectBusiTerminalById(busiTerminal.getId()));
            for (ITerminalInterceptor iTerminalInterceptor : terminalInterceptors)
            {
                try
                {
                    iTerminalInterceptor.terminalUpdated(busiTerminal);
                }
                catch (Exception e)
                {
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
    public int deleteBusiTerminalByIds(Long[] ids)
    {

        int c = 0;
        for (Long id : ids)
        {
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
    public int deleteBusiTerminalById(Long id)
    {
        int c = 0;
        try
        {
            BusiTerminal busiTerminal = busiTerminalMapper.selectBusiTerminalById(id);
            if (busiTerminal == null)
            {
                return c;
            }
            BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(id);
            if (busiUserTerminal != null) {
                throw new SystemException(1, "终端删除失败：（" + busiTerminal.getName() + "）该终端已被用户绑定！");
            }
            
            c = busiTerminalMapper.deleteBusiTerminalById(id);
            if (c > 0)
            {
                if (TerminalType.isFSBC(busiTerminal.getType()))
                {
                    FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(busiTerminal.getFsbcServerId());
                    RestResponse restResponse = fsbcBridge.getCredentialInvoker().delete(FsbcCredential.newCredential().name(busiTerminal.getCredential()));
                    if (!restResponse.isSuccess() && !"Failed to delete credential - Name does not exist".equals(restResponse.getMessage()))
                    {
                        throw new SystemException(1005445, "FSBC账号删除失败：" + restResponse.getMessage());
                    }
                }
                else if (TerminalType.isFCMSIP(busiTerminal.getType()))
                {
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
                
                // 关联删除终端绑定的高级参数
                busiTerminalMeetingJoinSettingsService.deleteBusiTerminalMeetingJoinSettingsById(id);
                TerminalCache.getInstance().remove(id);
                for (ITerminalInterceptor iTerminalInterceptor : terminalInterceptors)
                {
                    try
                    {
                        iTerminalInterceptor.terminalRemoved(busiTerminal);
                    }
                    catch (Exception e)
                    {
                        LoggerFactory.getLogger(getClass()).error("拦截处理出错", e);
                    }
                }
            }
        }
        catch (SystemException e)
        {
            throw e;
        }
        catch (Exception e)
        {
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
    public List<BusiTerminal> selectAll()
    {
        return busiTerminalMapper.selectBusiTerminalList(new BusiTerminal());
    }
}
