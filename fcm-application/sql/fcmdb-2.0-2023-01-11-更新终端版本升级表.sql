ALTER TABLE `fcmdb`.`busi_terminal_upgrade`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_unicode_ci,
ADD COLUMN `version_description` VARCHAR(500) NULL DEFAULT NULL COMMENT '版本描述' AFTER `version_name`;
