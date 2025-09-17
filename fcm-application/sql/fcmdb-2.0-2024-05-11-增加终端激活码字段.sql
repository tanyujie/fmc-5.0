ALTER TABLE `fcmdb`.`busi_terminal`
    ADD COLUMN `code` varchar(255) NULL COMMENT 'smcsip终端激活码' AFTER `sn_check`;