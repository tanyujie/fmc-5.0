ALTER TABLE `fcmdb`.`busi_mcu_tencent_conference_appointment`
ADD COLUMN `ops_id` BIGINT(20) NULL DEFAULT NULL COMMENT 'OPS的ID(云端使用)' AFTER `room_book_id`,
ADD COLUMN `is_cloud_conference` INT(11) NULL DEFAULT NULL COMMENT '是否云会议(ops端使用)' AFTER `ops_id`,
ADD COLUMN `cloud_conference_id` VARCHAR(50) NULL COMMENT '云会议ID(ops端用)' AFTER `is_cloud_conference`;

ALTER TABLE `fcmdb`.`busi_mcu_hwcloud_conference_appointment`
ADD COLUMN `ops_id` BIGINT(20) NULL DEFAULT NULL COMMENT 'OPS的ID(云端使用)' AFTER `room_book_id`,
ADD COLUMN `is_cloud_conference` INT(11) NULL DEFAULT NULL COMMENT '是否云会议(ops端使用)' AFTER `ops_id`,
ADD COLUMN `cloud_conference_id` VARCHAR(50) NULL COMMENT '云会议ID(ops端用)' AFTER `is_cloud_conference`;
