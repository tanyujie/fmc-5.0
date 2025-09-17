ALTER TABLE `fcmdb`.`busi_history_conference`
ADD COLUMN `end_reasons_type` int(11) not null DEFAULT 1 COMMENT '会议结束原因：1:管理员挂断; 2:到时自动结束; 3:异常结束 ' AFTER `type`;