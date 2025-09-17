package com.paradisecloud.fcm.service.im.tencent.models.message;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.service.im.tencent.models.message.contents.MsgContent;

public class MsgBodyItem {
    @JSONField(name = "MsgType")
    private String msgType;

    @JSONField(name = "MsgContent")
    private MsgContent msgContent;

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public MsgContent getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(MsgContent msgContent) {
        this.msgContent = msgContent;
    }
}
