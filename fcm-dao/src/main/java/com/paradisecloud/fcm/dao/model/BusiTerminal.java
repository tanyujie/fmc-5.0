package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.fcm.dao.model.vo.TerminalParam;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;
import java.util.Map;

/**
 * 终端信息对象 busi_terminal
 *
 * @author lilinhai
 * @date 2021-12-05
 */
@Schema(description = "终端信息")
public class BusiTerminal extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 终端序列号，设备硬件的sn，mqtt必须
     */
    @Schema(description = "终端序列号，设备硬件的sn，mqtt必须")
    @Excel(name = "终端序列号，设备硬件的sn，mqtt必须")
    private String sn;

    /**
     * 创建者ID
     */
    @Schema(description = "创建者ID")
    @Excel(name = "创建者ID")
    private Long createUserId;

    /**
     * 创建者用户名
     */
    @Schema(description = "创建者用户名")
    @Excel(name = "创建者用户名")
    private String createUserName;

    /**
     * 终端所属部门ID
     */
    @Schema(description = "终端所属部门ID")
    @Excel(name = "终端所属部门ID")
    private Long deptId;

    /**
     * 设备的IP地址
     */
    @Schema(description = "设备的IP地址")
    @Excel(name = "设备的IP地址")
    private String ip;

    /**
     * 设备号，设备唯一标识（如果是sfbc终端，则对应凭据）
     */
    @Schema(description = "设备号，设备唯一标识（如果是sfbc终端，则对应凭据）")
    @Excel(name = "设备号，设备唯一标识", readConverterExp = "如=果是sfbc终端，则对应凭据")
    private String number;

    /**
     * 摄像头IP地址
     */
    @Schema(description = "摄像头IP地址")
    @Excel(name = "摄像头IP地址")
    private String cameraIp;

    /**
     * 终端显示名字
     */
    @Schema(description = "终端显示名字")
    @Excel(name = "终端显示名字")
    private String name;

    /**
     * 终端类型，枚举值int类型
     */
    @Schema(description = "终端类型，枚举值int类型")
    @Excel(name = "终端类型，枚举值int类型")
    private Integer type;

    /**
     * 终端状态：1在线，2离线
     */
    @Schema(description = "终端状态：1在线，2离线")
    @Excel(name = "终端状态：1在线，2离线")
    private Integer onlineStatus;

    /**
     * 协议
     */
    @Schema(description = "协议")
    @Excel(name = "协议")
    private String protocol;

    /**
     * 密码
     */
    @Schema(description = "密码")
    @Excel(name = "密码")
    private String password;

    /**
     * fsbc账号
     */
    @Schema(description = "fsbc账号")
    @Excel(name = "fsbc账号")
    private String credential;

    /**
     * FSBC终端最后注册时间
     */
    @Schema(description = "FSBC终端最后注册时间")
    @Excel(name = "FSBC终端最后注册时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date registrationTime;

    /**
     * FSBC终端内网IP
     */
    @Schema(description = "FSBC终端内网IP")
    @Excel(name = "FSBC终端内网IP")
    private String intranetIp;

    /**
     * fsbc服务器ID
     */
    @Schema(description = "fsbc服务器ID")
    @Excel(name = "fsbc服务器ID")
    private Long fsbcServerId;

    /**
     * FSBC终端端口
     */
    @Schema(description = "FSBC终端端口")
    @Excel(name = "FSBC终端端口")
    private Integer port;

    /**
     * FSBC终端的传输协议（TLS,TCP,UDP）
     */
    @Schema(description = "FSBC终端的传输协议（TLS,TCP,UDP）")
    @Excel(name = "FSBC终端的传输协议", readConverterExp = "T=LS,TCP,UDP")
    private String transport;

    /**
     * FSBC终端厂商信息
     */
    @Schema(description = "FSBC终端厂商信息")
    @Excel(name = "FSBC终端厂商信息")
    private String vendor;

    /**
     * fs服务器ID
     */
    @Schema(description = "fs服务器ID")
    @Excel(name = "fs服务器ID")
    private Long fsServerId;

    /**
     * 业务领域类型
     */
    @Schema(description = "业务领域类型")
    @Excel(name = "业务领域类型")
    private Integer businessFieldType;

    /**
     * 业务属性
     */
    @Schema(description = "业务属性")
    @Excel(name = "业务属性")
    private Map<String, Object> businessProperties;

    /**
     * 参会类型：1被叫，2手动主叫，3自动主叫，10直播
     */
    @Schema(description = "参会类型：1被叫，2手动主叫，3自动主叫，10直播")
    @Excel(name = "参会类型：1被叫，2手动主叫，3自动主叫，10直播")
    private Integer attendType;

    /**
     * mqtt连接状态：1在线，2离线
     */
    @Schema(description = "mqtt连接状态：1在线，2离线")
    @Excel(name = "mqtt连接状态：1在线，2离线")
    private Integer mqttOnlineStatus;

    /**
     * mac地址
     */
    @Schema(description = "mac地址")
    @Excel(name = "mac地址")
    private String mac;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @Excel(name = "备注")
    private String remarks;

    /**
     * APP版本号
     */
    @Schema(description = "APP版本号")
    @Excel(name = "APP版本号")
    private String appVersionCode;

    /**
     * APP版本名
     */
    @Schema(description = "APP版本名")
    @Excel(name = "APP版本名")
    private String appVersionName;

    /**
     * APP类型
     */
    @Schema(description = "APP类型")
    @Excel(name = "APP类型")
    private String appType;

    /**
     * 顺序号
     */
    @Schema(description = "顺序号")
    @Excel(name = "顺序号")
    private Long sortNum;

    /**
     * 终端号（紫荆MCU使用）
     */
    @Schema(description = "终端号（紫荆MCU使用）")
    @Excel(name = "终端号（紫荆MCU使用）")
    private Integer terminalNum;

    /**
     * zj服务器ID
     */
    @Schema(description = "zj服务器ID")
    @Excel(name = "zj服务器ID")
    private Long zjServerId;

    /**
     * zj用户ID
     */
    @Schema(description = "zj用户ID")
    @Excel(name = "zj用户ID")
    private Long zjUserId;

    /**
     * 过期日期
     */
    @Schema(description = "过期日期")
    @Excel(name = "日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expiredDate;

    /**
     * 是否可用
     */
    @Schema(description = "是否可用")
    @Excel(name = "是否可用")
    private Integer available;

    /**
     * 连接IP
     */
    @Schema(description = "连接IP")
    @Excel(name = "连接IP")
    private String connectIp;

    @Schema(description = "zte服务器ID")
    @Excel(name = "zte服务器ID")
    private Long zteServerId;

    @Schema(description = "zte终端类型")
    @Excel(name = "zte终端类型")
    private Integer zteTerminalType;


    @Schema(description = "zte终端ID")
    @Excel(name = "zte终端ID")
    private String zteTerminalId;

    @Schema(description = "zte终端呼叫类型")
    @Excel(name = "zte终端呼叫类型")
    private Integer callmodel;

    @Schema(description = "终端账户")
    @Excel(name = "终端账户")
    private String terminalUsername;

    @Schema(description = "终端密码")
    @Excel(name = "终端密码")
    private String terminalPassword;


    @Schema(description = "sn对比结果")
    @Excel(name = "sn对比结果")
    private Integer snCheck;


    private String areaId;

    private String serviceZoneId;

    private String organizationId;

    private TerminalParam terminalParam;

    private String code;


    @Schema(description = "电话号码")
    @Excel(name = "电话号码")
    private String phone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCameraIp() {
        return cameraIp;
    }

    public void setCameraIp(String cameraIp) {
        this.cameraIp = cameraIp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(Integer onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    public Date getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(Date registrationTime) {
        this.registrationTime = registrationTime;
    }

    public String getIntranetIp() {
        return intranetIp;
    }

    public void setIntranetIp(String intranetIp) {
        this.intranetIp = intranetIp;
    }

    public Long getFsbcServerId() {
        return fsbcServerId;
    }

    public void setFsbcServerId(Long fsbcServerId) {
        this.fsbcServerId = fsbcServerId;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public Long getFsServerId() {
        return fsServerId;
    }

    public void setFsServerId(Long fsServerId) {
        this.fsServerId = fsServerId;
    }

    public Integer getBusinessFieldType() {
        return businessFieldType;
    }

    public void setBusinessFieldType(Integer businessFieldType) {
        this.businessFieldType = businessFieldType;
    }

    public Map<String, Object> getBusinessProperties() {
        return businessProperties;
    }

    public void setBusinessProperties(Map<String, Object> businessProperties) {
        this.businessProperties = businessProperties;
    }

    /**
     * <p>Get Method   :   attendType Integer</p>
     *
     * @return attendType
     */
    public Integer getAttendType() {
        return attendType;
    }

    /**
     * <p>Set Method   :   attendType Integer</p>
     *
     * @param attendType
     */
    public void setAttendType(Integer attendType) {
        this.attendType = attendType;
    }

    public Integer getMqttOnlineStatus() {
        return mqttOnlineStatus;
    }

    public void setMqttOnlineStatus(Integer mqttOnlineStatus) {
        this.mqttOnlineStatus = mqttOnlineStatus;
    }

    /**
     * <p>Get Method   :   mac String</p>
     *
     * @return mac
     */
    public String getMac() {
        return mac;
    }

    /**
     * <p>Set Method   :   mac String</p>
     *
     * @param mac
     */
    public void setMac(String mac) {
        this.mac = mac;
    }

    /**
     * <p>Get Method   :   remarks String</p>
     *
     * @return remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * <p>Set Method   :   remarks String</p>
     *
     * @param remarks
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * <p>Get Method   :   appVersionCode String</p>
     *
     * @return appVersionCode
     */
    public String getAppVersionCode() {
        return appVersionCode;
    }

    /**
     * <p>Set Method   :   appVersionCode String</p>
     *
     * @param appVersionCode
     */
    public void setAppVersionCode(String appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    /**
     * <p>Get Method   :   appVersionName String</p>
     *
     * @return appVersionName
     */
    public String getAppVersionName() {
        return appVersionName;
    }

    /**
     * <p>Set Method   :   appVersionName String</p>
     *
     * @param appVersionName
     */
    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    /**
     * <p>Get Method   :   appType String</p>
     *
     * @return appType
     */
    public String getAppType() {
        return appType;
    }

    /**
     * <p>Set Method   :   appType String</p>
     *
     * @param appType
     */
    public void setAppType(String appType) {
        this.appType = appType;
    }

    public Long getSortNum() {
        return sortNum;
    }

    public void setSortNum(Long sortNum) {
        this.sortNum = sortNum;
    }

    public Integer getTerminalNum() {
        return terminalNum;
    }

    public void setTerminalNum(Integer terminalNum) {
        this.terminalNum = terminalNum;
    }

    public Long getZjServerId() {
        return zjServerId;
    }

    public void setZjServerId(Long zjServerId) {
        this.zjServerId = zjServerId;
    }

    public Long getZjUserId() {
        return zjUserId;
    }

    public void setZjUserId(Long zjUserId) {
        this.zjUserId = zjUserId;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }

    public String getConnectIp() {
        return connectIp;
    }

    public void setConnectIp(String connectIp) {
        this.connectIp = connectIp;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getServiceZoneId() {
        return serviceZoneId;
    }

    public void setServiceZoneId(String serviceZoneId) {
        this.serviceZoneId = serviceZoneId;
    }

    public TerminalParam getTerminalParam() {
        return terminalParam;
    }

    public void setTerminalParam(TerminalParam terminalParam) {
        this.terminalParam = terminalParam;
    }

    public Long getZteServerId() {
        return zteServerId;
    }

    public void setZteServerId(Long zteServerId) {
        this.zteServerId = zteServerId;
    }

    public Integer getZteTerminalType() {
        return zteTerminalType;
    }

    public void setZteTerminalType(Integer zteTerminalType) {
        this.zteTerminalType = zteTerminalType;
    }

    public String getZteTerminalId() {
        return zteTerminalId;
    }

    public void setZteTerminalId(String zteTerminalId) {
        this.zteTerminalId = zteTerminalId;
    }

    public Integer getCallmodel() {
        return callmodel;
    }

    public void setCallmodel(Integer callmodel) {
        this.callmodel = callmodel;
    }

    public String getTerminalUsername() {
        return terminalUsername;
    }

    public void setTerminalUsername(String terminalUsername) {
        this.terminalUsername = terminalUsername;
    }

    public String getTerminalPassword() {
        return terminalPassword;
    }

    public void setTerminalPassword(String terminalPassword) {
        this.terminalPassword = terminalPassword;
    }

    public Integer getSnCheck() {
        return snCheck;
    }

    public void setSnCheck(Integer snCheck) {
        this.snCheck = snCheck;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("sn", getSn())
                .append("createUserId", getCreateUserId())
                .append("createUserName", getCreateUserName())
                .append("deptId", getDeptId())
                .append("ip", getIp())
                .append("number", getNumber())
                .append("cameraIp", getCameraIp())
                .append("name", getName())
                .append("type", getType())
                .append("onlineStatus", getOnlineStatus())
                .append("protocol", getProtocol())
                .append("password", getPassword())
                .append("credential", getCredential())
                .append("registrationTime", getRegistrationTime())
                .append("intranetIp", getIntranetIp())
                .append("fsbcServerId", getFsbcServerId())
                .append("port", getPort())
                .append("transport", getTransport())
                .append("vendor", getVendor())
                .append("fsServerId", getFsServerId())
                .append("businessFieldType", getBusinessFieldType())
                .append("businessProperties", getBusinessProperties())
                .append("attendType", attendType)
                .append("mqttOnlineStatus", getMqttOnlineStatus())
                .append("mac", getMac())
                .append("remarks", getRemarks())
                .append("appVersionCode", getAppVersionCode())
                .append("appVersionName", getAppVersionName())
                .append("appType", getAppType())
                .append("sortNum", getSortNum())
                .append("terminalNum", getTerminalNum())
                .append("zjServerId", getZjServerId())
                .append("zjUserId", getZjUserId())
                .append("zteServerId", getZteServerId())
                .append("zteTerminalId", getZteTerminalId())
                .append("zteTerminalType", getZteTerminalType())
                .append("callmode", getCallmodel())
                .append("expiredDate", getExpiredDate())
                .append("available", getAvailable())
                .append("connectIp", getConnectIp())
                .append("areaId", getAreaId())
                .append("serviceZoneId", getServiceZoneId())
                .append("terminalParam", getTerminalParam())
                .append("terminalUsername", getTerminalUsername())
                .append("terminalPassword", getTerminalPassword())
                .append("snCheck", getSnCheck())
                .append("code", getCode())
                .append("phone",getPhone())
                .toString();
    }


}