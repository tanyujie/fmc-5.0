package com.paradisecloud.fcm.mqtt.model;

import com.paradisecloud.common.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "终端信息")
public class ExcelTerminalOut {
    /** 终端显示名字 */
    @Schema(description = "终端显示名字")
    @Excel(name = "终端显示名字")
    private String name;

    /** 终端类型，枚举值int类型 */
    @Schema(description = "终端类型，枚举值int类型")
    @Excel(name = "终端类型，枚举值int类型")
    private String type;

    /** fsbc账号 */
    @Schema(description = "fsbc账号")
    @Excel(name = "fsbc账号")
    private String credential;

    /** 密码 */
    @Schema(description = "密码")
    @Excel(name = "密码")
    private String password;

    /** 参会类型：1被叫，2手动主叫，3自动主叫，10直播 */
    @Schema(description = "参会类型：1被叫，2手动主叫，3自动主叫，10直播")
    @Excel(name = "参会类型：1被叫，2手动主叫，3自动主叫，10直播")
    private String attendType;

    /** 终端序列号，设备硬件的sn，mqtt必须 */
    @Schema(description = "终端序列号，设备硬件的sn，mqtt必须")
    @Excel(name = "终端序列号，设备硬件的sn，mqtt必须")
    private String sn;

    /** 设备的IP地址 */
    @Schema(description = "设备的IP地址")
    @Excel(name = "设备的IP地址")
    private String ip;

    /** 设备号，设备唯一标识（如果是sfbc终端，则对应凭据） */
    @Schema(description = "设备号，设备唯一标识（如果是sfbc终端，则对应凭据）")
    @Excel(name = "设备号，设备唯一标识", readConverterExp = "如=果是sfbc终端，则对应凭据")
    private String number;

    /** 摄像头IP地址 */
    @Schema(description = "摄像头IP地址")
    @Excel(name = "摄像头IP地址")
    private String cameraIp;

    /** 备注 */
    @Schema(description = "备注")
    @Excel(name = "备注")
    private String remarks;

    /** APP版本号 */
    @Schema(description = "APP版本号")
    @Excel(name = "APP版本号")
    private String appVersionCode;

    /** APP版本名 */
    @Schema(description = "APP版本名")
    @Excel(name = "APP版本名")
    private String appVersionName;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getAttendType() {
        return attendType;
    }

    public void setAttendType(String attendType) {
        this.attendType = attendType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCameraIp() {
        return cameraIp;
    }

    public void setCameraIp(String cameraIp) {
        this.cameraIp = cameraIp;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(String appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    @Override
    public String toString() {
        return "ExcelTerminalOut{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", credential='" + credential + '\'' +
                ", password='" + password + '\'' +
                ", attendType='" + attendType + '\'' +
                ", sn='" + sn + '\'' +
                ", ip='" + ip + '\'' +
                ", number='" + number + '\'' +
                ", cameraIp='" + cameraIp + '\'' +
                ", remarks='" + remarks + '\'' +
                ", appVersionCode='" + appVersionCode + '\'' +
                ", appVersionName='" + appVersionName + '\'' +
                '}';
    }
}
