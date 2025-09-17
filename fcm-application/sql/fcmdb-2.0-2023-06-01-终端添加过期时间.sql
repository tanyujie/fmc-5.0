ALTER TABLE `fcmdb`.`busi_terminal`
ADD COLUMN `expired_date` DATE NULL DEFAULT NULL COMMENT '过期日期' AFTER `zj_user_id`,
ADD COLUMN `available` INT NULL DEFAULT 1 COMMENT '是否可用：1:可用2:过期' AFTER `expired_date`;
