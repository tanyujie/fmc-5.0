ALTER TABLE `fcmdb`.`busi_mqtt`
ADD COLUMN `use_ssl` INT(11) NULL DEFAULT 0 COMMENT '使用SSL 0：不使用 1：使用' AFTER `domain_name`;
