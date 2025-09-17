package com.paradisecloud.fcm.dao.model.vo;

/**
 * 终端报告终端类
 */
public class TerminalReportTerminalVo {

    /**
     * 终端ID
     */
    private Long Id;
    /**
     * 终端名称
     */
    private String name;
    /**
     * 类型
     */
    private Integer type;
    /**
     * 外网IP
     */
    private String ip;
    /**
     * 内外IP
     */
    private String intranetIp;
    /**
     * 账号
     */
    private String credential;
    /**
     * 部门ID
     */
    private Long deptId;
    /**
     * 参会次数
     */
    private Long joinedTimes;
    /**
     * 参会时长（秒）
     */
    private Long joinedSeconds;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIntranetIp() {
        return intranetIp;
    }

    public void setIntranetIp(String intranetIp) {
        this.intranetIp = intranetIp;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getJoinedTimes() {
        return joinedTimes;
    }

    public void setJoinedTimes(Long joinedTimes) {
        this.joinedTimes = joinedTimes;
    }

    public Long getJoinedSeconds() {
        return joinedSeconds;
    }

    public void setJoinedSeconds(Long joinedSeconds) {
        this.joinedSeconds = joinedSeconds;
    }

    @Override
    public String toString() {
        return "TerminalReportTerminalVo{" +
                "Id=" + Id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", ip='" + ip + '\'' +
                ", intranetIp='" + intranetIp + '\'' +
                ", credential='" + credential + '\'' +
                ", deptId=" + deptId +
                ", joinedTimes=" + joinedTimes +
                ", joinedSeconds=" + joinedSeconds +
                '}';
    }
}
