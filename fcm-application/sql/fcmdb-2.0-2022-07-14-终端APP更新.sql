ALTER TABLE `fcmdb`.`busi_terminal`
ADD COLUMN `app_version_code` VARCHAR(50) NULL DEFAULT NULL COMMENT 'APP版本号' AFTER `remarks`,
ADD COLUMN `app_version_name` VARCHAR(50) NULL DEFAULT NULL COMMENT 'APP版本名' AFTER `app_version_code`,
ADD COLUMN `app_type` VARCHAR(64) NULL DEFAULT NULL COMMENT '软件类型：与busi_terminal_upgrade中terminal_type等同' AFTER `app_version_name`;


ALTER TABLE `fcmdb`.`busi_terminal_upgrade`
ADD COLUMN `version_name` VARCHAR(64) NULL DEFAULT NULL COMMENT '版本名' AFTER `version_num`,
CHANGE COLUMN `terminal_type` `terminal_type` VARCHAR(64) CHARACTER SET 'utf8mb4' NULL DEFAULT NULL COMMENT '终端类型：与busi_terminal中app_type等同' ;


ALTER TABLE `fcmdb`.`busi_register_terminal`
ADD COLUMN `app_version_code` VARCHAR(50) NULL DEFAULT NULL COMMENT 'APP版本号' AFTER `credential`,
ADD COLUMN `app_version_name` VARCHAR(50) NULL DEFAULT NULL COMMENT 'APP版本名' AFTER `app_version_code`;

