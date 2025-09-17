
ALTER TABLE `fcmdb`.`busi_terminal`
    MODIFY COLUMN `protocol` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '协议' AFTER `fs_server_id`;