ALTER TABLE `fcmdb`.`busi_template_conference`
ADD COLUMN `minutes_enabled` INT(11) NULL DEFAULT NULL COMMENT '是否启用会议纪要(1是，2否)' AFTER `conference_mode`;
