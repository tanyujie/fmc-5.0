ALTER TABLE `fcmdb`.`busi_register_terminal`
ADD COLUMN `connect_ip` VARCHAR(50) NULL DEFAULT NULL COMMENT '连接IP' AFTER `app_version_name`;

ALTER TABLE `fcmdb`.`busi_terminal`
ADD COLUMN `connect_ip` VARCHAR(50) NULL DEFAULT NULL COMMENT '连接IP' AFTER `available`;

