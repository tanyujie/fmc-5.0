ALTER TABLE `fcmdb`.`busi_terminal`
    ADD COLUMN `phone` varchar(255) NULL COMMENT '电话号码' AFTER `zte_terminal_id`;