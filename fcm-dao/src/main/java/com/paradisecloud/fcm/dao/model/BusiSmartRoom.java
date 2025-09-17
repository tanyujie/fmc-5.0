package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * 智慧办公房间对象 busi_smart_room
 *
 * @author lilinhai
 * @date 2024-02-19
 */
@Schema(description = "智慧办公房间")
public class BusiSmartRoom extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 智慧办公房间名称 */
    @Schema(description = "智慧办公房间名称")
    @Excel(name = "智慧办公房间名称")
    private String roomName;

    /** 房间状态 1：启用 2：停用 */
    @Schema(description = "房间状态 1：启用 2：停用")
    @Excel(name = "房间状态 1：启用 2：停用")
    private Integer roomStatus;

    /** 房间类型 0：会议室 */
    @Schema(description = "房间类型 0：会议室")
    @Excel(name = "房间类型 0：会议室")
    private Integer roomType;

    /** 城市 */
    @Schema(description = "城市")
    @Excel(name = "城市")
    private String city;

    /** 楼宇 */
    @Schema(description = "楼宇")
    @Excel(name = "楼宇")
    private String building;

    /** 楼层 */
    @Schema(description = "楼层")
    @Excel(name = "楼层")
    private String floor;

    /** 第三方OA类型 0：非第三方 1：企业微信 2：钉钉 */
    @Schema(description = "第三方OA类型 0：非第三方 1：企业微信 2：钉钉")
    @Excel(name = "第三方OA类型 0：非第三方 1：企业微信 2：钉钉")
    private Integer thirdOaType;

    /** 第三方房间ID */
    @Schema(description = "第三方房间ID")
    @Excel(name = "第三方房间ID")
    private String thirdRoomId;

    /** 会议室等级：0:任何人 1:账号登陆  2:人脸识别 */
    @Schema(description = "会议室等级：0:任何人 1:账号登陆  2:人脸识别")
    @Excel(name = "会议室等级：0:任何人 1:账号登陆  2:人脸识别")
    private Integer roomLevel;

    /** 用户ID */
    @Schema(description = "用户ID")
    private Long userId;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setRoomName(String roomName)
    {
        this.roomName = roomName;
    }

    public String getRoomName()
    {
        return roomName;
    }
    public void setRoomStatus(Integer roomStatus)
    {
        this.roomStatus = roomStatus;
    }

    public Integer getRoomStatus()
    {
        return roomStatus;
    }
    public void setRoomType(Integer roomType)
    {
        this.roomType = roomType;
    }

    public Integer getRoomType()
    {
        return roomType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public Integer getThirdOaType() {
        return thirdOaType;
    }

    public void setThirdOaType(Integer thirdOaType) {
        this.thirdOaType = thirdOaType;
    }

    public String getThirdRoomId() {
        return thirdRoomId;
    }

    public void setThirdRoomId(String thirdRoomId) {
        this.thirdRoomId = thirdRoomId;
    }

    public Integer getRoomLevel() {
        return roomLevel;
    }

    public void setRoomLevel(Integer roomLevel) {
        this.roomLevel = roomLevel;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("roomName", getRoomName())
                .append("roomStatus", getRoomStatus())
                .append("roomType", getRoomType())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .append("remark", getRemark())
                .append("city", getCity())
                .append("building", getBuilding())
                .append("floor", getFloor())
                .append("thirdOaType", getThirdOaType())
                .append("roomLevel", getRoomLevel())
                .append("userId", getUserId())
                .toString();
    }
}