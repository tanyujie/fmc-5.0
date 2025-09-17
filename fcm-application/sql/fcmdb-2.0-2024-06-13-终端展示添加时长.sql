ALTER TABLE `fcmdb`.`busi_info_display`
ADD COLUMN `duration_time` INT NOT NULL DEFAULT 0 COMMENT '显示时长' AFTER `push_object`;
