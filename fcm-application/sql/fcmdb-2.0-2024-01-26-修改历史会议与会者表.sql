
ALTER TABLE `fcmdb`.`busi_history_participant`
    MODIFY COLUMN `remote_party` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'callLeg的远程参与方地址' AFTER `dept_id`;