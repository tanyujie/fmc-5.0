ALTER TABLE `fcmdb`.`busi_mcu_smc3_template_conference`
    ADD COLUMN `up_cascade_id` bigint(20) NULL DEFAULT NULL COMMENT '上级ID' AFTER `presenter`,
    ADD COLUMN `up_cascade_mcu_type` VARCHAR(20) NULL DEFAULT NULL COMMENT '上级MCU类型' AFTER `up_cascade_id`,
    ADD COLUMN `up_cascade_index` INT(11) NULL DEFAULT 0 COMMENT '级联索引' AFTER `up_cascade_type`;
