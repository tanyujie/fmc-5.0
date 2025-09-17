package com.paradisecloud.fcm.service.im.tencent.models.group;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.service.im.tencent.models.QCloudIMResult;
import com.paradisecloud.fcm.service.im.tencent.models.member.GroupId;

import java.util.List;

public class GetGroupListResult extends QCloudIMResult {
    @JSONField(name = "TotalCount")
    private long totalCount;

    @JSONField(name = "GroupIdList")
    private List<GroupId> groupIdList;

    @JSONField(name = "Next")
    private long next;
}
