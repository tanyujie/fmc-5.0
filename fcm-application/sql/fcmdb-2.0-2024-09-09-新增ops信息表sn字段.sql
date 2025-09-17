ALTER TABLE `fcmdb`.`busi_ops_info`
    ADD COLUMN `sn` varchar(255) NULL COMMENT '上传的序列号' AFTER `fme_ip`;