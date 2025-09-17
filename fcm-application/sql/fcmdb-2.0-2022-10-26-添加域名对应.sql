ALTER TABLE `fcmdb`.`busi_free_switch`
ADD COLUMN `domain_name` VARCHAR(128) NULL COMMENT '域名' AFTER `out_bound`;


ALTER TABLE `fcmdb`.`busi_fsbc_registration_server`
ADD COLUMN `domain_name` VARCHAR(128) NULL COMMENT '域名' AFTER `password`;


ALTER TABLE `fcmdb`.`busi_mqtt`
ADD COLUMN `domain_name` VARCHAR(128) NULL COMMENT '域名' AFTER `node_name`;

