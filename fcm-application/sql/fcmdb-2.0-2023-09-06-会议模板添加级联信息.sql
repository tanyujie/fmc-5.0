ALTER TABLE `fcmdb`.`busi_template_conference`
ADD COLUMN `up_cascade_id` bigint(20) NULL DEFAULT NULL COMMENT '上级ID' AFTER `presenter`,
ADD COLUMN `up_cascade_mcu_type` VARCHAR(20) NULL DEFAULT NULL COMMENT '上级MCU类型' AFTER `up_cascade_id`,
ADD COLUMN `up_cascade_type` INT NULL DEFAULT 0 COMMENT '级联类型 0:自动生成模板 1:手动选择未开会模板 2:手动选择已开会模板' AFTER `up_cascade_mcu_type`,
ADD COLUMN `up_cascade_index` INT(11) NULL DEFAULT 0 COMMENT '级联索引' AFTER `up_cascade_type`;

ALTER TABLE `fcmdb`.`busi_mcu_zj_template_conference`
ADD COLUMN `up_cascade_id` bigint(20) NULL DEFAULT NULL COMMENT '上级ID' AFTER `presenter`,
ADD COLUMN `up_cascade_mcu_type` VARCHAR(20) NULL DEFAULT NULL COMMENT '上级MCU类型' AFTER `up_cascade_id`,
ADD COLUMN `up_cascade_type` INT NULL DEFAULT 0 COMMENT '级联类型 0:自动生成模板 1:手动选择未开会模板 2:手动选择已开会模板' AFTER `up_cascade_mcu_type`,
ADD COLUMN `up_cascade_index` INT(11) NULL DEFAULT 0 COMMENT '级联索引' AFTER `up_cascade_type`;

ALTER TABLE `fcmdb`.`busi_mcu_plc_template_conference`
ADD COLUMN `up_cascade_id` bigint(20) NULL DEFAULT NULL COMMENT '上级ID' AFTER `conf_id`,
ADD COLUMN `up_cascade_mcu_type` VARCHAR(20) NULL DEFAULT NULL COMMENT '上级MCU类型' AFTER `up_cascade_id`,
ADD COLUMN `up_cascade_type` INT NULL DEFAULT 0 COMMENT '级联类型 0:自动生成模板 1:手动选择未开会模板 2:手动选择已开会模板' AFTER `up_cascade_mcu_type`,
ADD COLUMN `up_cascade_index` INT(11) NULL DEFAULT 0 COMMENT '级联索引' AFTER `up_cascade_type`;

ALTER TABLE `fcmdb`.`busi_mcu_kdc_template_conference`
ADD COLUMN `up_cascade_id` bigint(20) NULL DEFAULT NULL COMMENT '上级ID' AFTER `conf_id`,
ADD COLUMN `up_cascade_mcu_type` VARCHAR(20) NULL DEFAULT NULL COMMENT '上级MCU类型' AFTER `up_cascade_id`,
ADD COLUMN `up_cascade_type` INT NULL DEFAULT 0 COMMENT '级联类型 0:自动生成模板 1:手动选择未开会模板 2:手动选择已开会模板' AFTER `up_cascade_mcu_type`,
ADD COLUMN `up_cascade_index` INT(11) NULL DEFAULT 0 COMMENT '级联索引' AFTER `up_cascade_type`;
