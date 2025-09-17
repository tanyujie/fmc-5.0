package com.paradisecloud.fcm.service.im.tencent.models.openim;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.service.im.tencent.models.QCloudIMResult;
import com.paradisecloud.fcm.service.im.tencent.models.message.ErrorListItem;

import java.util.List;

public class BatchSendMsgResult extends QCloudIMResult {
    @JSONField(name = "ErrorList")
    private List<ErrorListItem> errorList;

    public BatchSendMsgResult() {
        super();
    }

    public List<ErrorListItem> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<ErrorListItem> errorList) {
        this.errorList = errorList;
    }
}

