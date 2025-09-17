ALTER TABLE `fcmdb`.`busi_terminal`
    ADD COLUMN `zte_server_id` BIGINT(20) NULL DEFAULT NULL COMMENT 'zte服务器ID' AFTER `connect_ip`,
    ADD COLUMN `zte_terminal_id` VARCHAR(20) NULL DEFAULT NULL COMMENT 'zte终端ID' AFTER `zte_server_id`,
    ADD COLUMN `zte_terminal_type` BIGINT(20) NULL DEFAULT NULL COMMENT 'zte终端类型' AFTER `zte_server_id`,
    ADD COLUMN `callmodel` BIGINT(20) NULL DEFAULT NULL COMMENT '呼叫类型' AFTER `zte_terminal_type`;


