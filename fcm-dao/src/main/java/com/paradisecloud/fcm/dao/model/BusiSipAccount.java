package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * sip账号信息对象 busi_sip_account
 * 
 * @author zyz
 * @date 2021-09-24
 */
@Schema(description = "sip账号信息")
public class BusiSipAccount extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    @Schema(description = "主键id")
    private Long id;

    /** sip服务器ip */
    @Schema(description = "sip服务器ip")
    @Excel(name = "sip服务器ip")
    private String sipServer;

    /** sip服务器端口 */
    @Schema(description = "sip服务器端口")
    @Excel(name = "sip服务器端口")
    private Integer sipPort;

    /** fs用户id */
    @Schema(description = "fs用户id")
    @Excel(name = "fs用户id")
    private Long sipUserName;

    /** fs用户密码 */
    @Schema(description = "fs用户密码")
    @Excel(name = "fs用户密码")
    private String sipPassword;

    /** turnServer的ip */
    @Schema(description = "turnServer的ip")
    @Excel(name = "turnServer的ip")
    private String turnServer;

    /** turnPort端口 */
    @Schema(description = "turnPort端口")
    @Excel(name = "turnPort端口")
    private Integer turnPort;

    /** stunServer的ip */
    @Schema(description = "stunServer的ip")
    @Excel(name = "stunServer的ip")
    private String stunServer;

    /** stunPort的端口 */
    @Schema(description = "stunPort的端口")
    @Excel(name = "stunPort的端口")
    private Integer stunPort;

    /** 终端id */
    @Schema(description = "终端id")
    @Excel(name = "终端id")
    private String sn;

    /** turnUserName用户名 */
    @Schema(description = "turnUserName用户名")
    @Excel(name = "turnUserName用户名")
    private String turnUserName;

    /** turnPassword密码 */
    @Schema(description = "turnPassword密码")
    @Excel(name = "turnPassword密码")
    private String turnPassword;

    /** 代理服务器ip */
    @Schema(description = "代理服务器ip")
    @Excel(name = "代理服务器ip")
    private String proxyServer;

    /** 终端id */
    @Schema(description = "终端id")
    @Excel(name = "终端id")
    private Long terminalId;

    /** 部门id */
    @Schema(description = "部门id")
    @Excel(name = "部门id")
    private Long deptId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setSipServer(String sipServer) 
    {
        this.sipServer = sipServer;
    }

    public String getSipServer() 
    {
        return sipServer;
    }
    public void setSipPort(Integer sipPort) 
    {
        this.sipPort = sipPort;
    }

    public Integer getSipPort() 
    {
        return sipPort;
    }
    public void setSipUserName(Long sipUserName) 
    {
        this.sipUserName = sipUserName;
    }

    public Long getSipUserName() 
    {
        return sipUserName;
    }
    public void setSipPassword(String sipPassword) 
    {
        this.sipPassword = sipPassword;
    }

    public String getSipPassword() 
    {
        return sipPassword;
    }
    public void setTurnServer(String turnServer) 
    {
        this.turnServer = turnServer;
    }

    public String getTurnServer() 
    {
        return turnServer;
    }
    public void setTurnPort(Integer turnPort) 
    {
        this.turnPort = turnPort;
    }

    public Integer getTurnPort() 
    {
        return turnPort;
    }
    public void setStunServer(String stunServer) 
    {
        this.stunServer = stunServer;
    }

    public String getStunServer() 
    {
        return stunServer;
    }
    public void setStunPort(Integer stunPort) 
    {
        this.stunPort = stunPort;
    }

    public Integer getStunPort() 
    {
        return stunPort;
    }
    public void setSn(String sn) 
    {
        this.sn = sn;
    }

    public String getSn() 
    {
        return sn;
    }
    public void setTurnUserName(String turnUserName) 
    {
        this.turnUserName = turnUserName;
    }

    public String getTurnUserName() 
    {
        return turnUserName;
    }
    public void setTurnPassword(String turnPassword) 
    {
        this.turnPassword = turnPassword;
    }

    public String getTurnPassword() 
    {
        return turnPassword;
    }
    public void setProxyServer(String proxyServer) 
    {
        this.proxyServer = proxyServer;
    }

    public String getProxyServer() 
    {
        return proxyServer;
    }
    public void setTerminalId(Long terminalId) 
    {
        this.terminalId = terminalId;
    }

    public Long getTerminalId() 
    {
        return terminalId;
    }
    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("sipServer", getSipServer())
            .append("sipPort", getSipPort())
            .append("sipUserName", getSipUserName())
            .append("sipPassword", getSipPassword())
            .append("turnServer", getTurnServer())
            .append("turnPort", getTurnPort())
            .append("stunServer", getStunServer())
            .append("stunPort", getStunPort())
            .append("sn", getSn())
            .append("turnUserName", getTurnUserName())
            .append("turnPassword", getTurnPassword())
            .append("proxyServer", getProxyServer())
            .append("terminalId", getTerminalId())
            .append("deptId", getDeptId())
            .toString();
    }
}
