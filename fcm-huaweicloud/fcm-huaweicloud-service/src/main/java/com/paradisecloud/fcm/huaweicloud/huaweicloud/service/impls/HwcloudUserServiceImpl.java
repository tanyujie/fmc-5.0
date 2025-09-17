package com.paradisecloud.fcm.huaweicloud.huaweicloud.service.impls;

import com.huaweicloud.sdk.meeting.v1.MeetingClient;
import com.huaweicloud.sdk.meeting.v1.model.SearchCorpDirResponse;
import com.huaweicloud.sdk.meeting.v1.model.SearchCorpExternalDirResponse;
import com.huaweicloud.sdk.meeting.v1.model.SearchDevicesResponse;
import com.huaweicloud.sdk.meeting.v1.model.ShowDepartmentResponse;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudBridge;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudBridgeCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client.MeetingCorpDir;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IHwcloudUserService;
import org.springframework.stereotype.Service;


/**
 * @author nj
 * @date 2024/3/6 11:33
 */
@Service
public class HwcloudUserServiceImpl implements IHwcloudUserService {

    @Override
    public Object getUsers(Long deptId,String deptCode,String searchKey,String searchScope, Integer pageIndex, Integer pageSize) {
        if(pageIndex==null){
            pageIndex=0;
        }
        if(pageSize==null){
            pageSize=100;
        }

        HwcloudBridge bridgesByDept = HwcloudBridgeCache.getInstance().getBridgesByDept(deptId);

        MeetingClient meetingClient = bridgesByDept.getMeetingClient();
        MeetingCorpDir meetingCorpDir = new MeetingCorpDir(meetingClient);

        SearchCorpDirResponse searchCorpDirResponse = meetingCorpDir.searchCorpDir(searchKey,searchScope,deptCode, pageIndex, pageSize);

        return searchCorpDirResponse;
    }

    @Override
    public Object getExternalContacts(Long deptId,String searchKey,String searchScope, Integer pageIndex, Integer pageSize) {
        if(pageIndex==null||pageIndex<0){
            pageIndex=0;
        }
        if(pageSize==null){
            pageSize=10;
        }
        if(pageSize>500){
            throw new CustomException("最大值为500");
        }

        HwcloudBridge bridgesByDept = HwcloudBridgeCache.getInstance().getBridgesByDept(deptId);

        MeetingClient meetingClient = bridgesByDept.getMeetingClient();
        MeetingCorpDir meetingCorpDir = new MeetingCorpDir(meetingClient);

        SearchCorpExternalDirResponse searchCorpDirResponse = meetingCorpDir.searchCorpExternalDir(searchKey,searchScope, pageIndex, pageSize);

        return searchCorpDirResponse;
    }

    @Override
    public Object getDevices(Long deptId,String deptCode,String searchKey, Integer pageIndex, Integer pageSize) {
        if(pageIndex==null||pageIndex<0){
            pageIndex=0;
        }
        if(pageSize==null){
            pageSize=10;
        }
        if(pageSize>500){
            throw new CustomException("最大值为500");
        }

        HwcloudBridge bridgesByDept = HwcloudBridgeCache.getInstance().getBridgesByDept(deptId);

        MeetingClient meetingClient = bridgesByDept.getMeetingClient();
        MeetingCorpDir meetingCorpDir = new MeetingCorpDir(meetingClient);

        SearchDevicesResponse searchDevicesResponse = meetingCorpDir.searchDevice(searchKey,deptCode, pageIndex, pageSize);

        return searchDevicesResponse;
    }

    @Override
    public Object getCropDept(Long deptId,String deptCode) {

        HwcloudBridge bridgesByDept = HwcloudBridgeCache.getInstance().getBridgesByDept(deptId);

        MeetingClient meetingClient = bridgesByDept.getMeetingClient();
        MeetingCorpDir meetingCorpDir = new MeetingCorpDir(meetingClient);
        ShowDepartmentResponse showDepartmentResponse = meetingCorpDir.showDepartment("1");
        return showDepartmentResponse;
    }
}
