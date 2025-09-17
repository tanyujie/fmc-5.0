package com.paradisecloud.fcm.service.im.tencent.models.dirtywords;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.service.im.tencent.models.QCloudIMRequest;

import java.util.List;

public class AddDirtyWordRequest extends QCloudIMRequest {
    @JSONField(name = "DirtyWordsList")
    private List<String> dirtyWordsList;

    public List<String> getDirtyWordsList() {
        return dirtyWordsList;
    }

    public void setDirtyWordsList(List<String> dirtyWordsList) {
        this.dirtyWordsList = dirtyWordsList;
    }
}
