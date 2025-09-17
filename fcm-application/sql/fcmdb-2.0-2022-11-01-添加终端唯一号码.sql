ALTER TABLE `fcmdb`.`busi_terminal`
ADD COLUMN `terminal_num` INT(5) NULL DEFAULT NULL COMMENT '终端号（紫荆MCU使用）' AFTER `sort_num`,
ADD UNIQUE INDEX `terminal_num_UNIQUE` (`terminal_num` ASC);

