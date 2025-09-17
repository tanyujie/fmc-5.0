ALTER TABLE `fcmdb`.`busi_conference_number`
ADD COLUMN `mcu_type` VARCHAR(20) NULL DEFAULT NULL COMMENT 'MCU类型' AFTER `remarks`;

ALTER TABLE `fcmdb`.`busi_conference_number_section`
ADD COLUMN `mcu_type` VARCHAR(20) NULL DEFAULT NULL COMMENT 'MCU类型' AFTER `end_value`;
