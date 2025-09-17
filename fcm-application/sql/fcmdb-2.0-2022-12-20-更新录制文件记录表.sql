ALTER TABLE `fcmdb`.`busi_records`
ADD COLUMN `delete_time` DATETIME NULL DEFAULT NULL COMMENT '删除时间' AFTER `file_size`,
ADD COLUMN `record_file_status` int(11) not null DEFAULT 2 COMMENT '录制文件状态：1:删除，2:正常 ' AFTER `delete_time`;