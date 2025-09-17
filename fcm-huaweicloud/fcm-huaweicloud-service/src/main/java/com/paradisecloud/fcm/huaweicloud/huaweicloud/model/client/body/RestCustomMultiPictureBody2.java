package com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client.body;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.huaweicloud.sdk.meeting.v1.model.PicLayoutInfo;
import com.huaweicloud.sdk.meeting.v1.model.RestSubscriberInPic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author nj
 * @date 2024/3/6 16:21
 */
public class RestCustomMultiPictureBody2 {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("manualSet")
    private Integer manualSet;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("picLayoutInfo")
    private PicLayoutInfo picLayoutInfo;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("imageType")
    private String imageType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("subscriberInPics")
    private List<RestSubscriberInPic> subscriberInPics = null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("switchTime")
    private Integer switchTime;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("multiPicSaveOnly")
    private Boolean multiPicSaveOnly;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("isChairViewMultiPic")
    private Boolean isChairViewMultiPic;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("skipEmptyPic")
    private Integer skipEmptyPic;

    public RestCustomMultiPictureBody2() {
    }

    public RestCustomMultiPictureBody2 withIsChairViewMultiPic(Boolean isChairViewMultiPic) {
        this.isChairViewMultiPic = isChairViewMultiPic;
        return this;
    }

    public RestCustomMultiPictureBody2 withManualSet(Integer manualSet) {
        this.manualSet = manualSet;
        return this;
    }

    public RestCustomMultiPictureBody2 withskipEmptyPic(Integer skipEmptyPic) {
        this.skipEmptyPic = skipEmptyPic;
        return this;
    }
    public Integer getManualSet() {
        return this.manualSet;
    }

    public void setManualSet(Integer manualSet) {
        this.manualSet = manualSet;
    }

    public RestCustomMultiPictureBody2 withPicLayoutInfo(PicLayoutInfo picLayoutInfo) {
        this.picLayoutInfo = picLayoutInfo;
        return this;
    }

    public RestCustomMultiPictureBody2 withPicLayoutInfo(Consumer<PicLayoutInfo> picLayoutInfoSetter) {
        if (this.picLayoutInfo == null) {
            this.picLayoutInfo = new PicLayoutInfo();
            picLayoutInfoSetter.accept(this.picLayoutInfo);
        }

        return this;
    }

    public PicLayoutInfo getPicLayoutInfo() {
        return this.picLayoutInfo;
    }

    public void setPicLayoutInfo(PicLayoutInfo picLayoutInfo) {
        this.picLayoutInfo = picLayoutInfo;
    }

    public RestCustomMultiPictureBody2 withImageType(String imageType) {
        this.imageType = imageType;
        return this;
    }

    public String getImageType() {
        return this.imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public RestCustomMultiPictureBody2 withSubscriberInPics(List<RestSubscriberInPic> subscriberInPics) {
        this.subscriberInPics = subscriberInPics;
        return this;
    }

    public RestCustomMultiPictureBody2 addSubscriberInPicsItem(RestSubscriberInPic subscriberInPicsItem) {
        if (this.subscriberInPics == null) {
            this.subscriberInPics = new ArrayList();
        }

        this.subscriberInPics.add(subscriberInPicsItem);
        return this;
    }

    public RestCustomMultiPictureBody2 withSubscriberInPics(Consumer<List<RestSubscriberInPic>> subscriberInPicsSetter) {
        if (this.subscriberInPics == null) {
            this.subscriberInPics = new ArrayList();
        }

        subscriberInPicsSetter.accept(this.subscriberInPics);
        return this;
    }

    public List<RestSubscriberInPic> getSubscriberInPics() {
        return this.subscriberInPics;
    }

    public void setSubscriberInPics(List<RestSubscriberInPic> subscriberInPics) {
        this.subscriberInPics = subscriberInPics;
    }

    public RestCustomMultiPictureBody2 withSwitchTime(Integer switchTime) {
        this.switchTime = switchTime;
        return this;
    }

    public Integer getSwitchTime() {
        return this.switchTime;
    }

    public void setSwitchTime(Integer switchTime) {
        this.switchTime = switchTime;
    }

    public RestCustomMultiPictureBody2 withMultiPicSaveOnly(Boolean multiPicSaveOnly) {
        this.multiPicSaveOnly = multiPicSaveOnly;
        return this;
    }

    public Boolean getMultiPicSaveOnly() {
        return this.multiPicSaveOnly;
    }

    public void setMultiPicSaveOnly(Boolean multiPicSaveOnly) {
        this.multiPicSaveOnly = multiPicSaveOnly;
    }


}
