package com.paradisecloud.fcm.dao.model.vo;

public class McuTypeVo {

    /**
     * 代码
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 别名
     */
    private String alias;
    /**
     * 绑定部门
     */
    private Long bindDeptId;
    /**
     * 绑定部门级
     */
    private String bindDeptAncestors;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Long getBindDeptId() {
        return bindDeptId;
    }

    public void setBindDeptId(Long bindDeptId) {
        this.bindDeptId = bindDeptId;
    }

    public String getBindDeptAncestors() {
        return bindDeptAncestors;
    }

    public void setBindDeptAncestors(String bindDeptAncestors) {
        this.bindDeptAncestors = bindDeptAncestors;
    }

    @Override
    public String toString() {
        return "McuTypeVo{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", bindDeptId=" + bindDeptId +
                ", bindDeptAncestors='" + bindDeptAncestors + '\'' +
                '}';
    }
}
