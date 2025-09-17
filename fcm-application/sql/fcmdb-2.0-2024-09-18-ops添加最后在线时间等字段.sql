ALTER TABLE `fcmdb`.`busi_ops`
ADD COLUMN `last_online_time` DATETIME NULL DEFAULT NULL COMMENT '最后在线时间' AFTER `source_id`;

ALTER TABLE `fcmdb`.`busi_ops`
ADD COLUMN `remark` VARCHAR(500) NULL DEFAULT NULL COMMENT '备注' AFTER `last_online_time`;
