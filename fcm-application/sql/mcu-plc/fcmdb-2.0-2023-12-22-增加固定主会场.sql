ALTER TABLE `fcmdb`.`busi_mcu_plc_template_polling_scheme`
ADD COLUMN `is_fix_self` INT(11) NULL DEFAULT 2 COMMENT '是否固定主会场(1是，2否)' AFTER `is_fill`;
