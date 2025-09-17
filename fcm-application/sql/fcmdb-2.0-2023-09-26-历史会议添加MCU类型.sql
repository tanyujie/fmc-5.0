ALTER TABLE `fcmdb`.`busi_history_conference`
ADD COLUMN `mcu_type` VARCHAR(20) NULL DEFAULT NULL COMMENT 'MCU类型' AFTER `end_reasons_type`;

ALTER TABLE `fcmdb`.`busi_history_conference`
ADD COLUMN `up_cascade_id` BIGINT(20) NULL DEFAULT NULL COMMENT '上级历史会议ID' AFTER `mcu_type`;
