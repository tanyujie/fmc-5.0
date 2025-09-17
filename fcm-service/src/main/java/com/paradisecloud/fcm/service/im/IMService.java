package com.paradisecloud.fcm.service.im;


import com.paradisecloud.fcm.service.im.tencent.QCloudIMv4;
import com.paradisecloud.fcm.service.im.tencent.models.group.CreateGroupRequest;
import com.paradisecloud.fcm.service.im.tencent.models.group.CreateGroupResult;
import com.paradisecloud.fcm.service.im.tencent.models.group.DestroyGroupRequest;
import com.paradisecloud.fcm.service.im.tencent.models.group.DestroyGroupResult;

public class IMService {

    private String active;
    private IM im;

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public IM getIm() {
        return im;
    }

    public void setIm(IM im) {
        this.im = im;
    }

    private TLSSigAPIv2 tlsSigAPIv2 = null;

    public String getSdkAppId() {
        return im.getSdkAppId();
    }

    public String genUserSig(String userId, long expire) {
        return im.genUserSig(userId, expire);
    }

    /**
     * 创建群组
     * @param groupId 群组ID
     * @param groupType 群组类型：Private/Public/ChatRoom/AVChatRoom
     */
    private IMResult createGroup(String groupId, String groupType) {
        IMResult imResult = new IMResult();
        if (active.equals("tencent")) {
            try {
                QCloudIMv4 cloudIMv4 = new QCloudIMv4(getSdkAppId(), im.genUserSig(im.getAdminUserId(), 864000));
                CreateGroupRequest request = new CreateGroupRequest();
                request.setGroupId(groupId);
                request.setReqIdentifier(im.getAdminUserId());
                request.setName(groupId);
                request.setType(groupType);
                CreateGroupResult createGroupResult = cloudIMv4.groupOpenSvc.createGroup(request);
                imResult.setSuccess(createGroupResult.isSuccess());
                imResult.setCode(String.valueOf(createGroupResult.getErrorCode()));
                imResult.setMessage(createGroupResult.getErrorInfo());
            } catch (Exception ex) {
                imResult.setCode("-1");
            }
        }
        return imResult;
    }

    /**
     * 创建直播群组
     * @param groupId 群组ID
     */
    public IMResult createAVChatRoom(String groupId) {
        return createGroup(groupId, "AVChatRoom");
    }

    /**
     * 解散群组
     * @param groupId 群组ID
     */
    public IMResult destroyGroup(String groupId) {
        IMResult imResult = new IMResult();
        if (active.equals("tencent")) {
            try {
                QCloudIMv4 cloudIMv4 = new QCloudIMv4(getSdkAppId(), im.genUserSig(im.getAdminUserId(), 864000));
                DestroyGroupRequest request = new DestroyGroupRequest();
                request.setGroupId(groupId);
                request.setReqIdentifier(im.getAdminUserId());
                DestroyGroupResult destroyGroupResult = cloudIMv4.groupOpenSvc.destroyGroup(request);
                imResult.setSuccess(destroyGroupResult.isSuccess());
                imResult.setCode(String.valueOf(destroyGroupResult.getErrorCode()));
                imResult.setMessage(destroyGroupResult.getErrorInfo());
            } catch (Exception ex) {
                imResult.setCode("-1");
            }
        }
        return imResult;
    }
}
