ALTER TABLE `fcmdb`.`busi_terminal`
    ADD COLUMN `terminal_username` varchar(50) NULL COMMENT '终端账户' AFTER `callmodel`,
    ADD COLUMN `terminal_password` varchar(50) NULL COMMENT '终端密码' AFTER `terminal_username`,
    ADD COLUMN `sn_check` tinyint(1) NULL COMMENT 'sn对比结果' AFTER `terminal_password`;