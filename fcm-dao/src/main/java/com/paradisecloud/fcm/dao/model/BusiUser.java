package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.utils.bean.BeanUtils;
import com.paradisecloud.system.dao.model.SysUser;

public class BusiUser extends SysUser {

    @Excel(
            name = "终端ID"
    )
    private Long terminalId;

    @Excel(
            name = "终端名"
    )
    private String terminalName;

    private Integer terminalType;

    @Excel(
            name = "终端类型名"
    )
    private String terminalTypeName;

    private boolean loginLocked;

    public BusiUser() {
    }

    public BusiUser(Long userId) {
        setUserId(userId);
    }

    public BusiUser(SysUser sysUser) {
        BeanUtils.copyBeanProp(this, sysUser);
    }

    public Long getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(Long terminalId) {
        this.terminalId = terminalId;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public Integer getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(Integer terminalType) {
        this.terminalType = terminalType;
    }

    public String getTerminalTypeName() {
        return terminalTypeName;
    }

    public void setTerminalTypeName(String terminalTypeName) {
        this.terminalTypeName = terminalTypeName;
    }

    public boolean isLoginLocked() {
        return loginLocked;
    }

    public void setLoginLocked(boolean loginLocked) {
        this.loginLocked = loginLocked;
    }

    @Override
    public String toString() {
        return "BusiUser{" +
                "terminalId=" + terminalId +
                ", terminalName='" + terminalName + '\'' +
                ", terminalType=" + terminalType +
                ", terminalTypeName='" + terminalTypeName + '\'' +
                ", loginLocked=" + loginLocked +
                '}';
    }
}
