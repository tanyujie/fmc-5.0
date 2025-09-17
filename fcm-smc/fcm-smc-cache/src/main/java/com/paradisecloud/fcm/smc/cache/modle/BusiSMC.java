package com.paradisecloud.fcm.smc.cache.modle;

/**
 * @author nj
 * @date 2022/8/12 11:33
 */
public class BusiSMC {
    private Long id;
    private String ip;
    private String username;
    private String password;

    private String meetingUsername;
    private String meetingPassword;

    private String scUrl;

    public BusiSMC(String ip, String username, String password) {
        this.ip = ip;
        this.username = username;
        this.password = password;
    }

    public BusiSMC(String ip, String username, String password, String meetingUsername, String meetingPassword, String scUrl) {
        this.ip = ip;
        this.username = username;
        this.password = password;
        this.meetingUsername = meetingUsername;
        this.meetingPassword = meetingPassword;
        this.scUrl = scUrl;
    }


    public String getScUrl() {
        return scUrl;
    }

    public void setScUrl(String scUrl) {
        this.scUrl = scUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMeetingUsername() {
        return meetingUsername;
    }

    public void setMeetingUsername(String meetingUsername) {
        this.meetingUsername = meetingUsername;
    }

    public String getMeetingPassword() {
        return meetingPassword;
    }

    public void setMeetingPassword(String meetingPassword) {
        this.meetingPassword = meetingPassword;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
