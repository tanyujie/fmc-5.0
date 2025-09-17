package com.paradisecloud.fcm.web.service.impls;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.dao.mapper.BusiOpsMapper;
import com.paradisecloud.fcm.dao.mapper.BusiOpsResourceMapper;
import com.paradisecloud.fcm.dao.model.BusiOps;
import com.paradisecloud.fcm.dao.model.BusiOpsResource;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.web.cloud.CloudMeetingResourceRoom;
import com.paradisecloud.fcm.web.cloud.CloudMeetingResourceRoomCache;
import com.paradisecloud.fcm.web.service.interfaces.CloudConferenceService;
import com.paradisecloud.fcm.web.utils.AuthenticationUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class CloudConferenceServiceImpl implements CloudConferenceService {


    @Resource
    private BusiOpsMapper busiOpsMapper;
    @Resource
    private BusiOpsResourceMapper busiOpsResourceMapper;


    @Override
    public synchronized BaseConferenceContext startCloudConference(String conferenceName, String mcuType) {

        Long userId = AuthenticationUtil.getUserId();
        BusiOps busiOps = new BusiOps();
        busiOps.setUserId(userId);
        List<BusiOps> busiOps_query = busiOpsMapper.selectBusiOpsList(busiOps);
        if(CollectionUtils.isEmpty(busiOps_query)){
            throw new CustomException("非法用户");
        }
        BusiOps busiOps1 = busiOps_query.get(0);
        //查询用户最大会议数,最大时长
        BusiOpsResource busiOpsResource_q = new BusiOpsResource();
        busiOpsResource_q.setUserId(busiOps1.getUserId());
        busiOpsResource_q.setSn(busiOps.getSn());
        List<BusiOpsResource> busiOpsResources = busiOpsResourceMapper.selectBusiOpsResourceList(busiOpsResource_q);
        if(CollectionUtils.isEmpty(busiOpsResources)){
            throw new CustomException("用户未分配可使用资源");
        }
        BusiOpsResource busiOpsResource = busiOpsResources.get(0);
        Integer conferenceNumber = busiOpsResource.getConferenceNumber();
        Integer purchaseQuantity = busiOpsResource.getPurchaseQuantity();
        if(purchaseQuantity!=null){
            conferenceNumber=purchaseQuantity;
        }

        if(conferenceNumber-busiOpsResource.getUsingNumber()<=0){
            throw new CustomException("可使用会议个数不足");
        }
        Map<Long, String> userSnMap = CloudMeetingResourceRoomCache.getInstance().getUserSnMap();
        userSnMap.put(userId,busiOpsResource.getSn());
        Integer freeMinutes = busiOpsResource.getFreeMinutes();
        Integer purchaseDuration = busiOpsResource.getPurchaseDuration();

        CloudMeetingResourceRoom cloudMeetingResourceRoom = CloudMeetingResourceRoomCache.getInstance().get(busiOpsResource.getSn());
        if(cloudMeetingResourceRoom==null){
            cloudMeetingResourceRoom = new CloudMeetingResourceRoom(conferenceNumber-busiOpsResource.getUsingNumber(),freeMinutes+purchaseDuration);
            CloudMeetingResourceRoomCache.getInstance().put(busiOpsResource.getSn(),cloudMeetingResourceRoom);
        }


        //查询用户使用策略
        String enableType = busiOpsResource.getEnableType();
        BaseConferenceContext baseConferenceContext;
        if(Objects.equals(enableType,"TIME")){

            baseConferenceContext = cloudMeetingResourceRoom.bookMeeting(conferenceName, mcuType, freeMinutes+purchaseDuration);
        }else {
            baseConferenceContext= cloudMeetingResourceRoom.bookMeetingByCount(conferenceName,mcuType,freeMinutes+purchaseDuration);
        }
          if(baseConferenceContext!=null){
              baseConferenceContext.setSn(busiOpsResource.getSn());
              busiOpsResource.setUsingNumber(busiOpsResource.getUsingNumber()+1);
              busiOpsResourceMapper.updateBusiOpsResource(busiOpsResource);
          }
        return baseConferenceContext;
    }

    @Override
    public BaseConferenceContext startCloudConference(String conferenceName, String mcuType, String sn) {
        return null;
    }


}
