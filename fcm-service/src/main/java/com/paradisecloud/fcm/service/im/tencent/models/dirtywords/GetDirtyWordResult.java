package com.paradisecloud.fcm.service.im.tencent.models.dirtywords;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.service.im.tencent.models.QCloudIMResult;

import java.util.List;

public class GetDirtyWordResult extends QCloudIMResult {
    @JSONField(name = "DirtyWordsList")
    private List<String> dirtyWordsList;

    public List<String> getDirtyWordsList() {
        return dirtyWordsList;
    }

    public void setDirtyWordsList(List<String> dirtyWordsList) {
        this.dirtyWordsList = dirtyWordsList;
    }
}
