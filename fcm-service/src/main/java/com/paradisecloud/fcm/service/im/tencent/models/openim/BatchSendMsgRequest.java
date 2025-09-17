package com.paradisecloud.fcm.service.im.tencent.models.openim;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.service.im.tencent.models.QCloudIMRequest;
import com.paradisecloud.fcm.service.im.tencent.models.message.MsgBodyItem;
import com.paradisecloud.fcm.service.im.tencent.models.message.OfflinePushInfo;

import java.util.List;

public class BatchSendMsgRequest extends QCloudIMRequest {
    @JSONField(name = "SyncOtherMachine")
    private int syncOtherMachine;

    @JSONField(name = "From_Account")
    private String fromAccount;

    @JSONField(name = "To_Account")
    private List<String> toAccount;

    @JSONField(name = "MsgLifeTime")
    private int msgLifeTime;

    @JSONField(name = "MsgRandom")
    private int msgRandom;

    @JSONField(name = "MsgTimeStamp")
    private long msgTimestamp;

    @JSONField(name = "MsgBody")
    private List<MsgBodyItem> msgBody;

    @JSONField(name = "OfflinePushInfo")
    private OfflinePushInfo offlinePushInfo;

    public BatchSendMsgRequest() {
        super();
    }

    public int getSyncOtherMachine() {
        return syncOtherMachine;
    }

    public void setSyncOtherMachine(int syncOtherMachine) {
        this.syncOtherMachine = syncOtherMachine;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public List<String> getToAccount() {
        return toAccount;
    }

    public void setToAccount(List<String> toAccount) {
        this.toAccount = toAccount;
    }

    public int getMsgLifeTime() {
        return msgLifeTime;
    }

    public void setMsgLifeTime(int msgLifeTime) {
        this.msgLifeTime = msgLifeTime;
    }

    public int getMsgRandom() {
        return msgRandom;
    }

    public void setMsgRandom(int msgRandom) {
        this.msgRandom = msgRandom;
    }

    public long getMsgTimestamp() {
        return msgTimestamp;
    }

    public void setMsgTimestamp(long msgTimestamp) {
        this.msgTimestamp = msgTimestamp;
    }

    public List<MsgBodyItem> getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(List<MsgBodyItem> msgBody) {
        this.msgBody = msgBody;
    }

    public OfflinePushInfo getOfflinePushInfo() {
        return offlinePushInfo;
    }

    public void setOfflinePushInfo(OfflinePushInfo offlinePushInfo) {
        this.offlinePushInfo = offlinePushInfo;
    }
}
