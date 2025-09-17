ALTER TABLE `fcmdb`.`busi_terminal`
ADD COLUMN `zj_server_id` BIGINT(20) NULL DEFAULT NULL COMMENT 'ZJ服务器ID' AFTER `terminal_num`,
ADD COLUMN `zj_user_id` BIGINT(20) NULL DEFAULT NULL COMMENT 'ZJ用户ID' AFTER `zj_server_id`;

