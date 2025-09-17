package com.paradisecloud.fcm.smartroom.task;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.service.eunm.NotifyType;
import com.paradisecloud.fcm.service.notify.NotifyService;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomParticipantService;
import com.paradisecloud.system.dao.mapper.SysUserMapper;
import com.paradisecloud.system.dao.model.SysUser;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * 通过邮件和短信推送预约会议消息
 */
public class SmartRoomNotifyTask extends Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartRoomNotifyTask.class);

    private static final String TITLE_INVITE = "会议通知";
    private static final String TITLE_CANCEL = "会议取消通知";
    private static final String MSG_INVITE = "您有一个会议通知,请于“{1}”到“{2}”参加“{3}”会议,已生效签到码{4},请在会议屏扫码签到！";
    private static final String MSG_CANCEL = "原定于“{1}”召开的“{2}”会议取消，如有会议需要，将另待审核行通知。";

    private BusiSmartRoomBook busiSmartRoomBook;
    private BusiSmartRoom busiSmartRoom;
    private Integer msgType;

    public SmartRoomNotifyTask(String id, long delayInMilliseconds,
                               BusiSmartRoomBook busiSmartRoomBook,
                               BusiSmartRoom busiSmartRoom,
                               Integer msgType) {
        super(id, delayInMilliseconds);
        this.busiSmartRoomBook = busiSmartRoomBook;
        this.busiSmartRoom = busiSmartRoom;
        this.msgType = msgType;
    }

    @Override
    public void run() {
        LOGGER.info("信息推送开始。ID:" + getId());
        IBusiSmartRoomParticipantService busiSmartRoomParticipantService = BeanFactory.getBean(IBusiSmartRoomParticipantService.class);
        SysUserMapper sysUserMapper = BeanFactory.getBean(SysUserMapper.class);
        NotifyService notifyService = BeanFactory.getBean(NotifyService.class);

        Date startTime = busiSmartRoomBook.getStartTime();
        String startTimeStr = DateUtil.convertDateToString(startTime, null);

        String title = "";
        String msg = "";

        BusiSmartRoomParticipant busiSmartRoomParticipant = new BusiSmartRoomParticipant();
        busiSmartRoomParticipant.setBookId(busiSmartRoomBook.getId());
        List<BusiSmartRoomParticipant> busiSmartRoomParticipantList = busiSmartRoomParticipantService.selectBusiSmartRoomParticipantList(busiSmartRoomParticipant);
        for (BusiSmartRoomParticipant smartRoomParticipant : busiSmartRoomParticipantList) {
            String signInCode = smartRoomParticipant.getSignInCode();

            if (msgType == 1) {
                title = TITLE_INVITE;
                msg = MSG_INVITE;
                msg = msg.replace("{1}", startTimeStr);
                msg = msg.replace("{2}", busiSmartRoom.getRoomName());
                msg = msg.replace("{3}", busiSmartRoomBook.getBookName());
                msg = msg.replace("{4}", signInCode);
            } else if (msgType == 2) {
                title = TITLE_CANCEL;
                msg = MSG_CANCEL;
                msg = msg.replace("{1}", startTimeStr);
                msg = msg.replace("{2}", busiSmartRoom.getRoomName());
            }
            String[] msgArrayInvite = {startTimeStr, busiSmartRoom.getRoomName(), busiSmartRoomBook.getBookName(), signInCode};
            String[] msgArrayCancel = {startTimeStr, busiSmartRoomBook.getBookName()};

            Long userId = smartRoomParticipant.getUserId();
            if (userId != null) {
                SysUser sysUser = sysUserMapper.selectUserById(userId);
                if (sysUser != null) {
                    Boolean isEmail = false;
                    Boolean isPhone = false;

                    String email = sysUser.getEmail();
                    String phonenumber = sysUser.getPhonenumber();

                    if (StringUtils.isNotEmpty(email)) {
                        isEmail = true;
                    }
                    if (StringUtils.isNotEmpty(phonenumber)) {
                        isPhone = true;
                    }

                    if (isEmail) {
                        notifyService.notifyMail(title, msg, email);
                    }
                    if (isPhone) {
                        if (msgType == 1) {
                            notifyService.notifySmsTemplate(phonenumber, NotifyType.MEETING_INVITE, msgArrayInvite);
                        }
                        if (msgType == 2) {
                            notifyService.notifySmsTemplate(phonenumber, NotifyType.MEETING_CANCEL, msgArrayCancel);
                        }
                    }
                }
            }
        }
    }
}
