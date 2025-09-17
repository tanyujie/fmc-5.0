ALTER TABLE `fcmdb`.`busi_template_polling_paticipant`
ADD COLUMN `down_cascade_template_id` BIGINT(20) NULL DEFAULT NULL COMMENT '下级会议模板ID' AFTER `weight`,
ADD COLUMN `down_cascade_mcu_type` VARCHAR(20) NULL DEFAULT NULL COMMENT '下级会议MCU类型' AFTER `down_cascade_template_id`;

ALTER TABLE `fcmdb`.`busi_mcu_zj_template_polling_paticipant`
ADD COLUMN `down_cascade_template_id` BIGINT(20) NULL DEFAULT NULL COMMENT '下级会议模板ID' AFTER `weight`,
ADD COLUMN `down_cascade_mcu_type` VARCHAR(20) NULL DEFAULT NULL COMMENT '下级会议MCU类型' AFTER `down_cascade_template_id`;

ALTER TABLE `fcmdb`.`busi_mcu_plc_template_polling_paticipant`
ADD COLUMN `down_cascade_template_id` BIGINT(20) NULL DEFAULT NULL COMMENT '下级会议模板ID' AFTER `weight`,
ADD COLUMN `down_cascade_mcu_type` VARCHAR(20) NULL DEFAULT NULL COMMENT '下级会议MCU类型' AFTER `down_cascade_template_id`;

ALTER TABLE `fcmdb`.`busi_mcu_kdc_template_polling_paticipant`
ADD COLUMN `down_cascade_template_id` BIGINT(20) NULL DEFAULT NULL COMMENT '下级会议模板ID' AFTER `weight`,
ADD COLUMN `down_cascade_mcu_type` VARCHAR(20) NULL DEFAULT NULL COMMENT '下级会议MCU类型' AFTER `down_cascade_template_id`;

