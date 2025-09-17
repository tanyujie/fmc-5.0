ALTER TABLE `fcmdb`.`busi_ops_info`
    ADD COLUMN `public_ip` varchar(45) NULL COMMENT '公网IP' AFTER `sn`;