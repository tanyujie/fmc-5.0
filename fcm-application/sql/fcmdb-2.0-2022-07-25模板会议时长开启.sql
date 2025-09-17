
ALTER TABLE `fcmdb`.`busi_template_conference`
    ADD COLUMN `duration_enabled` int(11) NULL DEFAULT 0 COMMENT '是否启用会议时长(1是，2否)' AFTER `business_properties`,
ADD COLUMN `duration_time` int(11) NULL DEFAULT 1440 COMMENT '会议时长单位分钟' AFTER `duration_enabled`;