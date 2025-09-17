package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * 转流服务器对象 busi_trans_server
 *
 * @author lilinhai
 * @date 2024-03-29
 */
@Schema(description = "转流服务器")
public class BusiTransServer extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private Long id;

    /**
     * 服务器用户名
     */
    @Schema(description = "服务器用户名")
    @Excel(name = "服务器用户名")
    private String userName;

    /**
     * 服务器密码
     */
    @Schema(description = "服务器密码")
    @Excel(name = "服务器密码")
    private String password;

    /**
     * 服务器ip
     */
    @Schema(description = "服务器ip")
    @Excel(name = "服务器ip")
    private String ip;

    /**
     * 端口号
     */
    @Schema(description = "端口号")
    @Excel(name = "端口号")
    private Integer port;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getPort() {
        return port;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("userName", getUserName())
                .append("password", getPassword())
                .append("ip", getIp())
                .append("port", getPort())
                .toString();
    }

}