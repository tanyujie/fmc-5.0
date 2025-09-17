package com.paradisecloud.fcm.web.controller.mobile;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.Region;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;

/**
 * 移动端基础类
 */
public class MobileBaseController extends BaseController {

    /**
     * 是否有会议控制权
     *
     * @param conferenceContext
     * @return
     */
    protected boolean canControlConference(BaseConferenceContext conferenceContext) {
        if (conferenceContext != null) {
            String region = ExternalConfigCache.getInstance().getRegion();
            if (Region.SHUMU.getCode().equalsIgnoreCase(region) || Region.OPS.getCode().equalsIgnoreCase(region)) {
                return true;
            }
            LoginUser loginUser = SecurityUtils.getLoginUser();
            String userName = ExternalConfigCache.getInstance().getAutoLoginUser();
            if (loginUser.getUser().getUserName().equalsIgnoreCase(userName)) {
                return true;
            } else {
                if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId()) {
                    return true;
                } else if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 是否有会议控制权
     *
     * @param
     * @return
     */
    protected boolean canControlConference(Long createUserId, Long presenter) {
        String region = ExternalConfigCache.getInstance().getRegion();
        if (Region.SHUMU.getCode().equalsIgnoreCase(region) || Region.OPS.getCode().equalsIgnoreCase(region)) {
            return true;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String userName = ExternalConfigCache.getInstance().getAutoLoginUser();
        if (loginUser.getUser().getUserName().equalsIgnoreCase(userName)) {
            return true;
        } else {
            if (createUserId != null && createUserId.longValue() == loginUser.getUser().getUserId()) {
                return true;
            } else if (presenter != null && presenter.longValue() == loginUser.getUser().getUserId()) {
                return true;
            }
        }

        return false;
    }

}
