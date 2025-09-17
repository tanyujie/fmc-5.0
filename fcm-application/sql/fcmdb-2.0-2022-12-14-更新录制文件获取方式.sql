ALTER TABLE `fcmdb`.`busi_records`
ADD COLUMN `file_size` VARCHAR(50) NULL DEFAULT NULL COMMENT '文件大小' AFTER `update_time`;
