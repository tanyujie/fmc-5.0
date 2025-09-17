package com.paradisecloud.fcm.tencent.model.client;

import com.google.gson.reflect.TypeToken;
import com.paradisecloud.fcm.tencent.model.reponse.QueryUserMsOpenIdResponse;
import com.paradisecloud.fcm.tencent.model.reponse.TencentQueryUsersResponse;
import com.paradisecloud.fcm.tencent.model.request.QueryUserMsOpenIdRequest;
import com.tencentcloudapi.wemeet.common.RequestSender;
import com.tencentcloudapi.wemeet.common.exception.WemeetSdkException;
import com.tencentcloudapi.wemeet.models.BaseResponse;
import com.tencentcloudapi.wemeet.models.user.*;

/**
 * @author nj
 * @date 2023/7/12 10:28
 */
public class TencentUserClient {

    private final RequestSender sender;

    public TencentUserClient(RequestSender sender) {
        this.sender = sender;
    }

    public BaseResponse createUser(CreateUserRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }

    public BaseResponse modifyUser(ModifyUserRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }

    public QueryUserDetailResponse queryUserDetail(QueryUserDetailRequest request) throws WemeetSdkException {
        return (QueryUserDetailResponse)this.sender.request(request, new TypeToken<QueryUserDetailResponse>() {
        });
    }

    public TencentQueryUsersResponse queryUsers(QueryUsersRequest request) throws WemeetSdkException {
        return (TencentQueryUsersResponse)this.sender.request(request, new TypeToken<TencentQueryUsersResponse>() {
        });
    }

    public BaseResponse deleteUser(DeleteUserRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }


    public QueryUserMsOpenIdResponse queryUserMsOpenId(QueryUserMsOpenIdRequest request) throws WemeetSdkException {
        return (QueryUserMsOpenIdResponse)this.sender.request(request, new TypeToken<QueryUserMsOpenIdResponse>() {
        });
    }

}
