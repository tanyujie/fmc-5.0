ALTER TABLE `fcmdb`.`busi_template_conference`
ADD COLUMN `conference_mode` VARCHAR(50) NULL DEFAULT NULL COMMENT '会议模式' AFTER `up_cascade_index`;

ALTER TABLE `fcmdb`.`busi_history_conference`
ADD COLUMN `template_id` BIGINT(20) NULL DEFAULT NULL COMMENT '模板ID' AFTER `up_cascade_id`;

