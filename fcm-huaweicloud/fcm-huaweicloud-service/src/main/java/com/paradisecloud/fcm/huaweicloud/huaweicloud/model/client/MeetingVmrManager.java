package com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client;

import com.huaweicloud.sdk.core.exception.SdkException;
import com.huaweicloud.sdk.meeting.v1.MeetingClient;
import com.huaweicloud.sdk.meeting.v1.model.AssociateVmrRequest;
import com.huaweicloud.sdk.meeting.v1.model.AssociateVmrResponse;
import com.huaweicloud.sdk.meeting.v1.model.SearchCorpVmrRequest;
import com.huaweicloud.sdk.meeting.v1.model.SearchCorpVmrResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nj
 * @date 2024/2/27 17:14
 */
public class MeetingVmrManager {

    private Logger logger= LoggerFactory.getLogger(getClass());

    public String listUnallocatedVmr(MeetingClient managerClient) {
       logger.info("Start listUnallocatedVmr...");

        SearchCorpVmrRequest request = new SearchCorpVmrRequest()
                .withVmrMode(1)
                .withStatus(2);

        String vmrUuid = "";
        try {
            SearchCorpVmrResponse response = managerClient.searchCorpVmr(request);

            if (response.getCount() > 0) {
                vmrUuid = response.getData().get(0).getId();
                logger.info("UnAllocated Vmr ID is %s\r\n", vmrUuid);
            }

        } catch (SdkException e) {
            logger.info(e.getMessage());
        }

        return vmrUuid;
    }

    public void allocateVmr(MeetingClient managerClient, String account, String vmrUuid) {
        logger.info("Start allocateVmr...");

        List<String> vmrUuids = new ArrayList<String>();
        vmrUuids.add(vmrUuid);
        AssociateVmrRequest request = new AssociateVmrRequest()
                .withAccount(account)
                .withAccountType(1)
                .withBody(vmrUuids);

        try {
            AssociateVmrResponse response = managerClient.associateVmr(request);

        } catch (SdkException e) {
            logger.info(e.getMessage());
        }

    }
}
