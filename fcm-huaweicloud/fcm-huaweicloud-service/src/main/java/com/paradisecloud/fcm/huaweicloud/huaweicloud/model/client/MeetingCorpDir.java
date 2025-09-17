package com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client;

import com.huaweicloud.sdk.core.exception.SdkException;
import com.huaweicloud.sdk.meeting.v1.MeetingClient;
import com.huaweicloud.sdk.meeting.v1.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author nj
 * @date 2024/2/29 13:58
 */
public class MeetingCorpDir {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private MeetingClient userClient;

    public MeetingCorpDir( ) {

    }


    public MeetingCorpDir(MeetingClient userClient) {
        this.userClient = userClient;
    }

    public SearchCorpDirResponse searchCorpDir(String searchKey,String searchScope,String deptCode, Integer offset, Integer limit ) {
        logger.info("Start SearchCorpDir...");
        SearchCorpDirRequest request = new SearchCorpDirRequest()
                .withSearchKey(searchKey)
                .withSearchScope(searchScope)
                .withOffset(offset)
                .withLimit(limit)
                .withDeptCode(deptCode);

        try {
            SearchCorpDirResponse response = userClient.searchCorpDir(request);
           return response;
        } catch (SdkException e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    public SearchDevicesResponse searchDevice(String searchKey,String deptCode, Integer offset, Integer limit ) {
        logger.info("Start searchDevices...");
        SearchDevicesRequest request = new SearchDevicesRequest()
                .withSearchKey(searchKey)
                .withOffset(offset)
                .withDeptCode(deptCode)
                .withLimit(limit);

        try {
            SearchDevicesResponse response = userClient.searchDevices(request);
            return response;
        } catch (SdkException e) {
            logger.info(e.getMessage());
        }
        return null;
    }


    public SearchCorpExternalDirResponse searchCorpExternalDir(String searchKey,String searchScope, Integer offset, Integer limit) {
        logger.info("Start searchCorpExternalDir...");

        SearchCorpExternalDirRequest request = new SearchCorpExternalDirRequest()
                .withSearchKey(searchKey)
                .withSearchScope(searchScope)
                .withOffset(offset)
                .withLimit(limit);

        try {
            SearchCorpExternalDirResponse response = userClient.searchCorpExternalDir(request);
            return response;
        } catch (SdkException e) {
            logger.info(e.getMessage());
        }
        return null;
    }


    public ShowCorpResourceResponse showCorpResource() {
        logger.info("Start ShowCorpResource..");
        ShowCorpResourceRequest request = new ShowCorpResourceRequest();
        try {
            ShowCorpResourceResponse response = userClient.showCorpResource(request);
            return response;
        } catch (SdkException e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    public ShowSpResResponse  showSpRes(){
        logger.info("Start showSpRes...");
        ShowSpResRequest request = new ShowSpResRequest();
        try {
            ShowSpResResponse response = userClient.showSpRes(request);
            return response;
        } catch (SdkException e) {
            logger.info(e.getMessage());
        }
        return null;
    }


    public ShowDepartmentResponse  showDepartment(String deptCode){
        logger.info("Start showDepartment...");
        ShowDepartmentRequest request = new ShowDepartmentRequest().withDeptCode(deptCode);
        try {
            ShowDepartmentResponse response = userClient.showDepartment(request);
            return response;
        } catch (SdkException e) {
            logger.info(e.getMessage());
        }
        return null;
    }




}
