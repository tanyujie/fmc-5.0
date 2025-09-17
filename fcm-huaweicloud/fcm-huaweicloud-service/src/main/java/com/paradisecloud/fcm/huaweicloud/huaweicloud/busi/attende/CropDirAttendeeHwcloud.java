package com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende;

/**
 * @author nj
 * @date 2024/2/29 14:33
 */
public class CropDirAttendeeHwcloud extends AttendeeHwcloud{
    private String phone;
    private String type="normal";
    private Integer role=0;
    private String userUUID;
    private String sms;
    private String sip;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getSip() {
        return sip;
    }

    public void setSip(String sip) {
        this.sip = sip;
    }
}
