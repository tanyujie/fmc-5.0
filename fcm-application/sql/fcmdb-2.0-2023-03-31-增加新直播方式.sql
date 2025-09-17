ALTER TABLE `fcmdb`.`busi_live_setting`
ADD COLUMN `remote_party` VARCHAR(60) NULL DEFAULT NULL COMMENT '远程参与方地址' AFTER `update_time`;
